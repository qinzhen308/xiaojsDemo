package cn.xiaojs.xma.ui.lesson.xclass;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.DataPageLoader;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.model.CollectionCalendar;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ClassFilterHelper;
import cn.xiaojs.xma.ui.lesson.xclass.util.LessonFilterHelper;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class ClassSheduleTabModeFragment extends AbsClassScheduleFragment {

    RecyclerView recyclerview;
    HomeClassAdapter mAdapter;
    RadioGroup tabBar;
    RadioButton tab1;
    RadioButton tab2;
    RadioButton tab3;
    RadioButton tab4;
    String classId="";

    DataPageLoader<ClassSchedule,CollectionCalendar<ClassSchedule>> dataPageLoader;
    Pagination mPagination;
    String state;


    @Override
    protected View getContentView() {
        View v=View.inflate(getActivity(), R.layout.fragment_class_schedule_tab_mode, null);
        recyclerview=(RecyclerView)v.findViewById(R.id.recyclerview);
        tabBar=(RadioGroup) v.findViewById(R.id.tab_bar);
        tab1=(RadioButton) v.findViewById(R.id.tab1);
        tab2=(RadioButton) v.findViewById(R.id.tab2);
        tab3=(RadioButton) v.findViewById(R.id.tab3);
        tab4=(RadioButton) v.findViewById(R.id.tab4);
        return v;
    }

    @Override
    protected void init() {
        classId=getActivity().getIntent().getStringExtra(ClassScheduleActivity.EXTRA_ID);
        state=LessonFilterHelper.getState(0);
        mAdapter=new HomeClassAdapter(recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        recyclerview.setAdapter(mAdapter);
        tabBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.tab1:
                        state=LessonFilterHelper.getState(0);
                        break;
                    case R.id.tab2:
                        state=LessonFilterHelper.getState(2);
                        break;
                    case R.id.tab3:
                        state=LessonFilterHelper.getState(1);
                        break;
                    case R.id.tab4:
                        state=LessonFilterHelper.getState(4);
                        break;
                }
                dataPageLoader.refresh();
            }
        });

        initPageLoad();
        dataPageLoader.refresh();
//        getCountPerTab();
    }


    private void request(){
        Map map=LessonDataManager.createScheduleOptions(null,null,null,ClassFilterHelper.getStartTime(0), ClassFilterHelper.getEndTime(0),null,Account.TypeName.CLASS_LESSON, state,null,null);
        LessonDataManager.getClassesSchedule4Lesson(getActivity(),classId,map ,mPagination , dataPageLoader);

    }


    private void getCountPerTab(){
        setTabCount(3333,102,10,1024);
    }

    private void setTabCount(int count1,int count2,int count3,int count4){
        SpannableString ss=new SpannableString("全部 "+formatCount(count1));
        ss.setSpan(new RelativeSizeSpan(0.6f),3,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab1.setText(ss);
        ss=new SpannableString("待上课 "+formatCount(count2));
        ss.setSpan(new RelativeSizeSpan(0.6f),4,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab2.setText(ss);
        ss=new SpannableString("上课中 "+formatCount(count3));
        ss.setSpan(new RelativeSizeSpan(0.6f),4,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab3.setText(ss);
        ss=new SpannableString("已完课 "+formatCount(count4));
        ss.setSpan(new RelativeSizeSpan(0.6f),4,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab4.setText(ss);

    }

    private String formatCount(int count){
        String str="";
        if(count<1000){
            str=""+count;
        }else if(count<10000){
            str=(count/1000)+"k+";
        }else {
            str="上万";
        }
        return str;
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
                request();
            }

            @Override
            public List<ClassSchedule> adaptData(CollectionCalendar<ClassSchedule> object) {
                if(object==null)return new ArrayList<>();
                return object.calendar;
            }

            @Override
            public void onSuccess(List<ClassSchedule> curPage, List<ClassSchedule> all) {
                pageChangeInRecyclerView.completeLoading();
                bindData(all);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                pageChangeInRecyclerView.completeLoading();
                bindData(new ArrayList<ClassSchedule>());
            }

            @Override
            public void prepare() {
                pageChangeInRecyclerView=new PageChangeInRecyclerView(recyclerview,this);

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


    public void refresh(){
        if(mContent==null)return;
        dataPageLoader.refresh();
    }


}
