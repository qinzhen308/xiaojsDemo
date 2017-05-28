package cn.xiaojs.xma.ui.lesson.xclass;


import android.content.Context;
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

import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.model.ctl.ScheduleData;
import cn.xiaojs.xma.ui.lesson.CourseFilterDialog;
import cn.xiaojs.xma.ui.lesson.LessonBusiness;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;

/**
 * Created by maxiaobao on 2017/5/22.
 */

public class LessonFragment extends Fragment {

//    @BindView(R.id.class_lesson_list)
//    PullToRefreshSwipeListView listView;

    @BindView(R.id.my_course_search)
    TextView searchView;
    @BindView(R.id.filter_line)
    View mFilterLine;
    @BindView(R.id.course_filter)
    TextView mFilter;
    @BindView(R.id.over_layout)
    RecyclerView mRecyclerView;


    private Context context;

    protected int timePosition;
    protected int statePosition;
    protected int sourcePosition;

//    private LessonAdapter lessonAdapter;
    HomeClassAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        context = getActivity();
        View v = LayoutInflater.from(context).inflate(R.layout.fragment_public_lesson, null);
        ButterKnife.bind(this,v);
        searchView.setHint(R.string.hint_input_lesson_name);
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
        mAdapter = new HomeClassAdapter(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setAdapter(mAdapter);
        getMonthData();
    }

    @OnClick({R.id.course_filter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.course_filter:
                filter();
                break;
        }
    }

    private void filter() {
        CourseFilterDialog dialog = new CourseFilterDialog(context, false);
        dialog.setTimeSelection(timePosition);
        dialog.setStateSelection(statePosition);
        dialog.showAsDropDown(mFilterLine);
        mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_up, 0);
        dialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_down, 0);
            }
        });
        dialog.setOnOkListener(new CourseFilterDialog.OnOkListener() {
            @Override
            public void onOk(int timePosition, int statePosition, int sourcePosition) {
                LessonFragment.this.timePosition = timePosition;
                LessonFragment.this.statePosition = statePosition;
                LessonFragment.this.sourcePosition = sourcePosition;
                Criteria criteria = LessonBusiness.getFilter(timePosition, statePosition, sourcePosition, false);
                //TODO 重新查询
//                if (lessonAdapter != null) {
//                    lessonAdapter.request(criteria);
//                }
            }
        });
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

    private void getMonthData(){
        long start=ScheduleUtil.ymdToTimeMill(2017,6,3);
        long end=ScheduleUtil.ymdToTimeMill(2017,6,16);

        LessonDataManager.getClassesSchedule(getActivity(), ScheduleUtil.getUTCDate(start), ScheduleUtil.getUTCDate(end), new APIServiceCallback<ScheduleData>() {
            @Override
            public void onSuccess(ScheduleData object) {

                if(object!=null&&!object.calendar.isEmpty()){
                    bindData(object.calendar);
                }else {
                    bindData(new ArrayList<ClassSchedule>());
                }
            }
            @Override
            public void onFailure(String errorCode, String errorMessage) {
                bindData(new ArrayList<ClassSchedule>());
            }
        });
    }


}
