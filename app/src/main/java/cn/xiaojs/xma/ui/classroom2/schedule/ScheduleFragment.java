package cn.xiaojs.xma.ui.classroom2.schedule;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.DataPageLoader;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pageload.stateview.AppLoadState2;
import cn.xiaojs.xma.common.pageload.stateview.LoadStateListener;
import cn.xiaojs.xma.common.pageload.stateview.LoadStatusViewDecoratee;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ScheduleCriteria;
import cn.xiaojs.xma.model.ctl.ScheduleLesson;
import cn.xiaojs.xma.model.live.LiveSchedule;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom.main.ClassroomController;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.lesson.xclass.HomeClassAdapter;
import cn.xiaojs.xma.ui.lesson.xclass.model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by Paul Z on 2017/10/25.
 * 教室里面的课表
 */

public class ScheduleFragment extends BottomSheetFragment
        implements DialogInterface.OnKeyListener {

    @BindView(R.id.cl_root)
    RelativeLayout rootLay;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerview;
    HomeClassAdapter mAdapter;

    DataPageLoader<ScheduleLesson, CollectionPage<ScheduleLesson>> dataPageLoader;
    Pagination mPagination;
    LoadStatusViewDecoratee stateView;

    Date historyEndDate;//已经上过的课程和还未上过课程的临界
    Pagination mPrePagination;


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_classroom2_schedule, container, false);
        stateView = new LoadStatusViewDecoratee(new AppLoadState2(getActivity(), (ViewGroup) v.findViewById(R.id.load_state_container)));
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getDialog() == null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rootLay.getLayoutParams();
            params.setMargins(0, 0, 0, 0);
            rootLay.setBackgroundColor(getResources().getColor(R.color.white));
        } else {
            getDialog().setOnKeyListener(this);
        }

        init();

    }

    protected void init() {
        mAdapter = new HomeClassAdapter(recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerview.setAdapter(mAdapter);

        initPageLoad();
        dataPageLoader.refresh();
        mAdapter.setCallback(new EventCallback() {
            @Override
            public void onEvent(int what, Object... object) {
                //what代表事件
                if (what == EVENT_1) {//点击回放
                    //索引
                    int position = (int) object[0];
                    //数据
                    LiveSchedule data = (LiveSchedule) object[1];
                    LibDoc doc = new LibDoc();
                    doc.key = data.playback;

                    doc.mimeType = data.mimeType;

                    if (TextUtils.isEmpty(doc.mimeType)) {
                        doc.mimeType = Collaboration.StreamingTypes.HLS;
                    } else {
                        doc.typeName = Collaboration.TypeName.RECORDING_IN_LIBRARY;
                    }
                    ClassroomController.getInstance(getActivity()).enterVideoPlayPage(doc);
                }
            }
        });
    }

    private boolean isHistoryLoading=false;

    private void requestHistory() {
        if(historyEndDate==null)return;
        isHistoryLoading=true;
        Duration schedule = new Duration();
        schedule.setEnd(historyEndDate);
        ScheduleCriteria criteria = new ScheduleCriteria();
        criteria.duration=schedule;
        criteria.sort=true;
        LessonDataManager.getClassesScheduleNewly(getActivity(), ClassroomEngine.getEngine().getTicket(), criteria, mPrePagination, new APIServiceCallback<CollectionPage<ScheduleLesson>>() {
            @Override
            public void onSuccess(CollectionPage<ScheduleLesson> object) {
                if (object==null||ArrayUtil.isEmpty(object.objectsOfPage)) {
                } else {
                    dataPageLoader.getAllDatas().addAll(0,object.objectsOfPage);
                    bindData(dataPageLoader.getAllDatas());
                    mPrePagination.setPage(mPrePagination.getPage()+1);
                }
                isHistoryLoading=false;
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                isHistoryLoading=false;
            }
        });
    }

    private void requestLast() {

        Duration schedule = new Duration();

        ScheduleCriteria criteria = new ScheduleCriteria();
//        criteria.duration=schedule;
        criteria.sort=false;
        LessonDataManager.getClassesScheduleNewly(getActivity(), ClassroomEngine.getEngine().getTicket(), criteria, mPagination, dataPageLoader);
    }

    private void request() {

        Duration schedule = new Duration();
        schedule.setStart(new Date(0));
        schedule.setEnd(new Date(ScheduleUtil.ymdToTimeMill(2100, 12, 30)));

        ScheduleCriteria criteria = new ScheduleCriteria();
//        criteria.duration=schedule;
        LessonDataManager.getClassesScheduleNewly(getActivity(), ClassroomEngine.getEngine().getTicket(), criteria, mPagination, dataPageLoader);
    }


    private void initPageLoad() {
        mPagination = new Pagination();
        mPagination.setPage(1);
        mPagination.setMaxNumOfObjectsPerPage(10);
        mPrePagination = new Pagination();
        mPrePagination.setPage(1);
        mPrePagination.setMaxNumOfObjectsPerPage(10);
        dataPageLoader = new DataPageLoader<ScheduleLesson, CollectionPage<ScheduleLesson>>() {
            PageChangeInRecyclerView pageChangeInRecyclerView;

            @Override
            public void onRequst(int page) {
                mPagination.setPage(page);
                stateView.change(LoadStateListener.STATE_LOADING, "");
                if(page==1){
                    request();
                }else {
                    requestLast();
                }
            }

            @Override
            public List<ScheduleLesson> adaptData(CollectionPage<ScheduleLesson> object) {
                if (object == null) return new ArrayList<>();
                return object.objectsOfPage;
            }

            @Override
            public void onSuccess(List<ScheduleLesson> curPage, List<ScheduleLesson> all) {
                pageChangeInRecyclerView.completeLoading();
                if (ArrayUtil.isEmpty(all)) {
                    stateView.change(LoadStateListener.STATE_ALL_EMPTY, "");
                    if(historyEndDate==null){
                        historyEndDate=new Date();
                    }
                } else {
                    stateView.change(LoadStateListener.STATE_NORMAL, "");
                    if(historyEndDate==null){
                        historyEndDate=all.get(0).schedule.getStart();
                    }
                }
                bindData(all);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                pageChangeInRecyclerView.completeLoading();
                bindData(new ArrayList<ScheduleLesson>());
                stateView.change(LoadStateListener.STATE_LOADING_ERROR, "");
            }

            @Override
            public void prepare() {
                pageChangeInRecyclerView = new PageChangeInRecyclerView(recyclerview, this);

            }

            @Override
            public void previous() {
                if(!isHistoryLoading){
                    requestHistory();
                }
            }
        };
    }

    //这里认为服务器返回的数据是经过排序后的，不管倒叙还是顺序。
    private void bindData(List<ScheduleLesson> list) {
        ArrayList monthLists = new ArrayList();
        LessonLabelModel tempLabel = null;
        Date tempDate = new Date(0);
        int tempCount = 0;
        for (int j = 0; j < list.size(); j++) {
            ScheduleLesson cs = list.get(j);
            if (!ScheduleUtil.isSameDay(cs.schedule.getStart(), tempDate)) {//不是同一天
                tempCount = 0;
                tempDate = cs.schedule.getStart();
                tempLabel = new LessonLabelModel(ScheduleUtil.getDateYMDW(cs.schedule.getStart()), 0, true);
                monthLists.add(tempLabel);
            }
            tempCount++;
            monthLists.add(cs);
            tempLabel.lessonCount = tempCount;
        }
        monthLists.add(new LastEmptyModel());
        mAdapter.setList(monthLists);
        mAdapter.notifyDataSetChanged();
    }


    public void refresh() {
        dataPageLoader.refresh();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {

            switch (requestCode) {
                case CTLConstant.REQUEST_MATERIAL_ADD_NEW:
                    break;
            }
        }
    }


    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        return false;
    }


    public static ScheduleFragment createInstance(String classId) {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle data = new Bundle();
        fragment.setArguments(data);
        return fragment;
    }


    @OnClick({R.id.add_btn, R.id.v_divider})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_btn:
                AddLessonScheduleFragment fragment=AddLessonScheduleFragment.createInstance();
                fragment.setTargetFragment(this,CTLConstant.REQUEST_MATERIAL_ADD_NEW);
                fragment.show(getChildFragmentManager(),"add_lesson_schedule");
                break;
        }
    }
}
