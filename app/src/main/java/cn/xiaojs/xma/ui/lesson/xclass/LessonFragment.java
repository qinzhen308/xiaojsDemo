package cn.xiaojs.xma.ui.lesson.xclass;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.DataPageLoader;
import cn.xiaojs.xma.common.pageload.stateview.AppLoadState2;
import cn.xiaojs.xma.common.pageload.stateview.LoadStateListener;
import cn.xiaojs.xma.common.pageload.stateview.LoadStatusViewDecoratee;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshRecyclerView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.model.CollectionCalendar;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.model.ctl.ScheduleOptions;
import cn.xiaojs.xma.ui.lesson.xclass.model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.LessonFilterHelper;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.lesson.xclass.view.MyClassFilterDialog;
import cn.xiaojs.xma.util.ArrayUtil;

/**
 * Created by maxiaobao on 2017/5/22.
 */

public class LessonFragment extends Fragment implements IUpdateMethod{

//    @BindView(R.id.class_lesson_list)
//    PullToRefreshSwipeListView listView;

    @BindView(R.id.my_course_search)
    TextView searchView;
    @BindView(R.id.filter_line)
    View mFilterLine;
    @BindView(R.id.course_filter)
    TextView mFilter;
    @BindView(R.id.over_layout)
    PullToRefreshRecyclerView mPullRecyclerView;
    RecyclerView mRecyclerView;


    private Context context;

    protected int group1Position;
    protected int group2Position;

//    private LessonAdapter lessonAdapter;
    HomeClassAdapter mAdapter;
    DataPageLoader<ClassSchedule,CollectionCalendar<ClassSchedule>> dataPageLoader;
    Pagination mPagination;
    LoadStatusViewDecoratee stateView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        context = getActivity();
        View v = LayoutInflater.from(context).inflate(R.layout.fragment_public_lesson, null);
        ButterKnife.bind(this,v);
        searchView.setHint(R.string.hint_input_lesson_name);
        mPullRecyclerView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullRecyclerView.setFirstLoading(false);
        mPullRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<RecyclerView>() {
            @Override
            public void onRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                dataPageLoader.refresh();
            }
        });
        mRecyclerView=mPullRecyclerView.getRefreshableView();
        stateView=new LoadStatusViewDecoratee(new AppLoadState2(getActivity(),(ViewGroup) v.findViewById(R.id.load_state_container)));
        mAdapter = new HomeClassAdapter(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
//        stateView=new LoadStatusViewDecoratee(null);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        lessonAdapter = new LessonAdapter(context,listView);
//        lessonAdapter.setDesc(getString(R.string.teach_data_empty));
//        lessonAdapter.setIcon(R.drawable.ic_teach_empty);
//        lessonAdapter.setButtonDesc(getString(R.string.lesson_creation));

//        listView.setAdapter(lessonAdapter);

        initPageLoad();
        getMonthData();
        Logger.d("---lessonfragment--onActivityCreated----adapter="+mAdapter);
    }

    @OnClick({R.id.course_filter,R.id.my_course_search})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.course_filter:
                filter();
                break;
            case R.id.my_course_search:
                startActivity(new Intent(getActivity(),SearchLessonActivity.class));
                break;
        }
    }

    private void filter() {
        MyClassFilterDialog dialog = new MyClassFilterDialog(context, false);
        dialog.setGroup2Title(R.string.course_state);
        dialog.setTimeSelection(group1Position);
        dialog.setStateSelection(group2Position);
        dialog.setGroup1(getResources().getStringArray(R.array.lesson_filter_type)).setGroup2(getResources().getStringArray(R.array.lesson_filter_state));
        dialog.showAsDropDown(mFilterLine);
        mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_up, 0);
        dialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_down, 0);
            }
        });
        dialog.setOnOkListener(new MyClassFilterDialog.OnOkListener() {
            @Override
            public void onOk(int group1Position, int group2Position) {
                LessonFragment.this.group1Position = group1Position;
                LessonFragment.this.group2Position = group2Position;
//                LessonFilterHelper.getType(group1Position);
//                LessonFilterHelper.getState(group2Position);
                dataPageLoader.refresh();

            }
        });
    }


    private void initPageLoad(){
        mPagination=new Pagination();
        mPagination.setPage(1);
        mPagination.setMaxNumOfObjectsPerPage(10);
        dataPageLoader=new DataPageLoader<ClassSchedule, CollectionCalendar<ClassSchedule>>() {
            PageChangeInRecyclerView pageChangeInRecyclerView;
            @Override
            public void onRequst(int page) {
                mPagination.setPage(page);
                getMonthData();
                stateView.change(LoadStateListener.STATE_LOADING,"");
            }

            @Override
            public List<ClassSchedule> adaptData(CollectionCalendar<ClassSchedule> object) {
                if(object==null)return new ArrayList<>();
                return object.calendar;
            }

            @Override
            public void onSuccess(List<ClassSchedule> curPage, List<ClassSchedule> all) {
                pageChangeInRecyclerView.completeLoading();
                mPullRecyclerView.onRefreshComplete();
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
                mPullRecyclerView.onRefreshComplete();
                bindData(new ArrayList<ClassSchedule>());
                stateView.change(LoadStateListener.STATE_LOADING_ERROR,"");
            }

            @Override
            public void prepare() {
                pageChangeInRecyclerView=new PageChangeInRecyclerView(mRecyclerView,this);

            }
        };
    }


    private void bindData(List<ClassSchedule> list){
        ArrayList monthLists=new ArrayList();
        LessonLabelModel tempLabel=null;
        for(int j=0;j<list.size();j++){
            ClassSchedule cs=list.get(j);
            tempLabel=new LessonLabelModel(ScheduleUtil.getDateYMDW(cs.date),0,false);
            monthLists.add(tempLabel);
            monthLists.addAll(cs.lessons);
            tempLabel.lessonCount=cs.lessons.size();
            if(cs.lessons.size()>0){
                tempLabel.hasData=true;
            }
        }
        monthLists.add(new LastEmptyModel());
        mAdapter.setList(monthLists);
        mAdapter.notifyDataSetChanged();
    }

    private void getMonthData(){
        long start=new Date(0).getTime();
        long end=ScheduleUtil.ymdToTimeMill(3000,1,1);
        ScheduleOptions options=new ScheduleOptions.Builder().setStart(ScheduleUtil.getUTCDate(start))
                .setEnd(ScheduleUtil.getUTCDate(end)).setState(LessonFilterHelper.getState(group2Position))
                .setType(Account.TypeName.STAND_ALONE_LESSON).setRole(LessonFilterHelper.getType(group1Position))
                .build();
        LessonDataManager.getClassesSchedule4Lesson(getActivity(),options,mPagination , dataPageLoader);
    }


    @Override
    public void updateData(boolean justNative,Object... others) {
        if(justNative){
            mAdapter.notifyDataSetChanged();
        }else {
            dataPageLoader.refresh();
        }
    }

    @Override
    public void updateItem(int position, Object obj,Object... others) {
        if(position<mAdapter.getItemCount()){
            Object item=mAdapter.getList().get(position);
            //由于有些操作是异步的，为了防止在本方法调用前，列表已经刷新过，作如下判断
            if(item instanceof CLesson&&((CLesson)item).id.equals(((CLesson)obj).id)){
                if(others.length>0&&others[0].equals("remove")){
                    mAdapter.getList().remove(position);
                    //除了删除该项，还需要处理该课对应日期的标签：删除或者改变课的数量
                    for(int i=position-1;i>=0;i--){
                        Object preItem=mAdapter.getList().get(position-1);
                        if(preItem instanceof LessonLabelModel){
                            if(((LessonLabelModel) preItem).lessonCount==1){
                                mAdapter.getList().remove(i);
                            }else {
                                ((LessonLabelModel) preItem).lessonCount--;
                            }
                            break;
                        }
                    }
                }else {
                    mAdapter.getList().set(position,obj);
                }
                mAdapter.notifyDataSetChanged();
            }
        }
    }
}
