package cn.xiaojs.xma.ui.classroom2.schedule;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.DataPageLoader;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pageload.stateview.AppLoadState2;
import cn.xiaojs.xma.common.pageload.stateview.LoadStateListener;
import cn.xiaojs.xma.common.pageload.stateview.LoadStatusViewDecoratee;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Duration;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ScheduleCriteria;
import cn.xiaojs.xma.model.ctl.ScheduleLesson;
import cn.xiaojs.xma.model.live.LiveSchedule;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom.main.ClassroomController;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.lesson.xclass.AbsClassScheduleFragment;
import cn.xiaojs.xma.ui.lesson.xclass.HomeClassAdapter;
import cn.xiaojs.xma.ui.lesson.xclass.model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.util.ArrayUtil;

import static cn.xiaojs.xma.ui.classroom.main.Constants.KEY_CLASS_ID;

/**
 * Created by Paul Z on 2017/5/23.
 * 教室里面的课表
 */

public class ClassroomScheduleFragment extends AbsClassScheduleFragment {

    RecyclerView recyclerview;
    HomeClassAdapter mAdapter;
    String classId = "";

    DataPageLoader<ScheduleLesson, CollectionPage<ScheduleLesson>> dataPageLoader;
    Pagination mPagination;
    String role;
    LoadStatusViewDecoratee stateView;


    @Override
    protected View getContentView() {
        View v = View.inflate(getActivity(), R.layout.fragment_classroom_schedule_v2, null);
        recyclerview = (RecyclerView) v.findViewById(R.id.recyclerview);
        stateView=new LoadStatusViewDecoratee(new AppLoadState2(getActivity(),(ViewGroup) v.findViewById(R.id.load_state_container)));
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    protected void init() {
        if (getArguments() != null) {
            classId = getArguments().getString(KEY_CLASS_ID);
        }

        mAdapter = new HomeClassAdapter(recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
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
                    LibDoc doc=new LibDoc();
                    doc.key=data.playback;

                    doc.mimeType= data.mimeType;

                    if (TextUtils.isEmpty(doc.mimeType)) {
                        doc.mimeType= Collaboration.StreamingTypes.HLS;
                    }else{
                        doc.typeName = Collaboration.TypeName.RECORDING_IN_LIBRARY;
                    }
                    ClassroomController.getInstance(mContext).enterVideoPlayPage(doc);
                }
            }
        });
    }

    // TODO: 2017/6/4 初始化传入进来的参数，用于请求参数
    private void initParams() {
        classId = "";
        role = "";
    }

    // TODO: 2017/6/4 判断对于这个教室，是学生还是老师
    private boolean isStudents() {
        return true;
    }

    public void exit(){
        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }


    private void request() {

        Duration schedule = new Duration();
        schedule.setStart(new Date(0));
        schedule.setEnd(new Date(ScheduleUtil.ymdToTimeMill(2100,12,30)));

        ScheduleCriteria criteria = new ScheduleCriteria();
        criteria.duration=schedule;

        Pagination pagination = new Pagination();
        pagination.setMaxNumOfObjectsPerPage(10);

        LessonDataManager.getClassesScheduleNewly(getActivity(), ClassroomEngine.getEngine().getTicket(), criteria, pagination, dataPageLoader);
    }


    private void initPageLoad() {
        mPagination = new Pagination();
        mPagination.setPage(1);
        mPagination.setMaxNumOfObjectsPerPage(10);
        dataPageLoader = new DataPageLoader<ScheduleLesson, CollectionPage<ScheduleLesson>>() {
            PageChangeInRecyclerView pageChangeInRecyclerView;

            @Override
            public void next() {
                //不想分页就重写它，什么都不做
            }

            @Override
            public void onRequst(int page) {
                mPagination.setPage(page);
                stateView.change(LoadStateListener.STATE_LOADING,"");
                request();
            }

            @Override
            public List<ScheduleLesson> adaptData(CollectionPage<ScheduleLesson> object) {
                if (object == null) return new ArrayList<>();
                return object.objectsOfPage;
            }

            @Override
            public void onSuccess(List<ScheduleLesson> curPage, List<ScheduleLesson> all) {
                pageChangeInRecyclerView.completeLoading();
                if(ArrayUtil.isEmpty(all)){
                    stateView.change(LoadStateListener.STATE_ALL_EMPTY,"");
                }else {
                    stateView.change(LoadStateListener.STATE_NORMAL,"");
                }
                bindData(all);

            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                pageChangeInRecyclerView.completeLoading();
                bindData(new ArrayList<ScheduleLesson>());
                stateView.change(LoadStateListener.STATE_LOADING_ERROR,"");
            }

            @Override
            public void prepare() {
                pageChangeInRecyclerView = new PageChangeInRecyclerView(recyclerview, this);

            }
        };
    }

    //这里认为服务器返回的数据是经过排序后的，不管倒叙还是顺序。
    private void bindData(List<ScheduleLesson> list) {
        ArrayList monthLists = new ArrayList();
        LessonLabelModel tempLabel = null;
        Date tempDate=new Date(0);
        int tempCount=0;
        for (int j = 0; j < list.size(); j++) {
            ScheduleLesson cs = list.get(j);
            if(!ScheduleUtil.isSameDay(cs.schedule.getStart(),tempDate)){//不是同一天
                tempCount=0;
                tempDate=cs.schedule.getStart();
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
        if (mContent == null) return;
        dataPageLoader.refresh();
    }


}
