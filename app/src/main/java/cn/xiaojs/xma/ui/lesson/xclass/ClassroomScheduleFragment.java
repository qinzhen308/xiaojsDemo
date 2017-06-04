package cn.xiaojs.xma.ui.lesson.xclass;

import android.support.annotation.IdRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pageload.DataPageLoader;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.pageload.trigger.PageChangeInRecyclerView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.model.CollectionCalendar;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ClassFilterHelper;
import cn.xiaojs.xma.ui.lesson.xclass.util.LessonFilterHelper;

/**
 * Created by Paul Z on 2017/5/23.
 * 教室里面的课表
 * 老师身份：全部，我的
 * 学生身份：全部（不要tab）
 */

public class ClassroomScheduleFragment extends AbsClassScheduleFragment {

    RecyclerView recyclerview;
    HomeClassAdapter mAdapter;
    RadioGroup tabBar;
    RadioButton tab1;
    RadioButton tab2;
    String classId="";

    DataPageLoader<ClassSchedule,CollectionCalendar<ClassSchedule>> dataPageLoader;
    Pagination mPagination;
    String role;


    @Override
    protected View getContentView() {
        View v=View.inflate(getActivity(), R.layout.fragment_class_schedule_tab_mode, null);
        recyclerview=(RecyclerView)v.findViewById(R.id.recyclerview);
        tabBar=(RadioGroup) v.findViewById(R.id.tab_bar);
        tab1=(RadioButton) v.findViewById(R.id.tab1);
        tab2=(RadioButton) v.findViewById(R.id.tab2);
        return v;
    }

    @Override
    protected void init() {
        classId=getActivity().getIntent().getStringExtra(ClassScheduleActivity.EXTRA_ID);
        mAdapter=new HomeClassAdapter(recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        recyclerview.setAdapter(mAdapter);
        tabBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId){
                    case R.id.tab1:
                        break;
                    case R.id.tab2:
                        break;
                }
                dataPageLoader.refresh();
            }
        });

        initPageLoad();
        dataPageLoader.refresh();
//        getCountPerTab();
        if(isStudents()){
            tabBar.setVisibility(View.GONE);
        }

        mAdapter.setCallback(new EventCallback() {
            @Override
            public void onEvent(int what, Object... object) {
                //what代表事件
                if(what==EVENT_1){//点击回放
                    //索引
                    int position=(int )object[0];
                    //数据
                    CLesson data=(CLesson)object[1];
                    // TODO: 2017/6/4  logic
                }
            }
        });
    }

    // TODO: 2017/6/4 初始化传入进来的参数，用于请求参数
    private void initParams(){
        classId="";
        role="";
    }

    // TODO: 2017/6/4 判断对于这个教室，是学生还是老师
    private boolean isStudents(){
        return true;
    }


    private void request(){
        LessonDataManager.getClassesSchedule4Lesson(getActivity(),classId, ClassFilterHelper.getStartTime(0), ClassFilterHelper.getEndTime(0),
                Account.TypeName.CLASS_LESSON, "All",mPagination , dataPageLoader);
    }


    private void getCountPerTab(){
        setTabCount(3333,102);
    }

    private void setTabCount(int count1,int count2){
        SpannableString ss=new SpannableString("全部 "+formatCount(count1));
        ss.setSpan(new RelativeSizeSpan(0.6f),3,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab1.setText(ss);
        ss=new SpannableString("我教的课 "+formatCount(count2));
        ss.setSpan(new RelativeSizeSpan(0.6f),5,ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tab2.setText(ss);
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
            tempLabel=new LessonLabelModel(cs.date,0,false);
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
