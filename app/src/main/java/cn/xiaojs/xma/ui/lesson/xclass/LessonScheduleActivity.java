package cn.xiaojs.xma.ui.lesson.xclass;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.content.Intent;
import android.view.View;

import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.OnScheduleChangeListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.ctl.ClassLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.TimeUtil;

/**
 * Created by Paul Z on 2017/5/18.
 * 创建班时的课表
 */

public class LessonScheduleActivity extends BaseActivity{

    @BindView(R.id.over_layout)
    RecyclerView mListView;
    public final int REQUEST_NEW_LESSON_CODE = 0x1;


    HomeClassAdapter mAdapter;

    Map<String,List<ClassLesson>> datas;
    @BindView(R.id.slSchedule)
    ScheduleLayout calendarView;
    String selectDate;
    int selectDay;
    int selectyear;
    int selectMonth;


    @Override
    protected void addViewContent() {
        needHeader(true);
        setRightText(R.string.add);
        setMiddleTitle(R.string.schedule);
        addView(R.layout.layout_home_schedule);
        mListView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        mAdapter=new HomeClassAdapter(mListView);
        mListView.setAdapter(mAdapter);
        datas= ScheduleUtil.buildScheduleByMonth(CreateClassActivity.classLessons);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        selectDay=calendar.get(Calendar.DAY_OF_MONTH);
        selectMonth =calendar.get(Calendar.MONTH);
        selectyear=calendar.get(Calendar.YEAR);
        selectDate=ScheduleUtil.getDateYM(selectyear,selectMonth,selectDay);
        bindData();
        calendarView.setOnScheduleChangeListener(new OnScheduleChangeListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                if(XiaojsConfig.DEBUG){
                    Logger.d("-----qz--------"+(year+"-"+(month+1)+"-"+day)+"---curDayIndex="+selectDate);
                }

                if(year!=selectyear||month!=selectMonth||day!=selectDay){
                    mAdapter.scrollToLabel(ScheduleUtil.getDateYMD(year,month,day));
                }

                selectDate=ScheduleUtil.getDateYM(year,month,day);
                selectDay=day;
                selectMonth=month;
                selectyear=year;
            }

            @Override
            public void onWeekChange(int year, int month, int day) {

            }

            @Override
            public void onMonthChange(int year, int month, int day) {
                bindData();
            }
        });

    }


//    private void bindData(){
//        ArrayList list2=new ArrayList();
//        if(datas!=null){
//            List list=datas.get(curDayIndex);
//            if(ArrayUtil.isEmpty(list)){
//                list2.add(new LessonLabelModel(TimeUtil.getWeak(curDayIndex*3600*24*1000),0,false));
//            }else {
//                list2.add(new LessonLabelModel(TimeUtil.getWeak(curDayIndex*3600*24*1000),list.size(),true));
//                list2.addAll(list);
//                list2.add(new LastEmptyModel());
//            }
//        }else {
//            list2.add(new LessonLabelModel(TimeUtil.getWeak(curDayIndex*3600*24*1000),0,false));
//        }
//        mAdapter.setList(list2);
//        mAdapter.notifyDataSetChanged();
//    }
    private void bindData(){
        ArrayList monthLists=new ArrayList();
        int dayCount=CalendarUtils.getMonthDays(selectyear,selectMonth);
        LessonLabelModel tempLabel=null;
        List<ClassLesson> list=null;
        if(datas!=null){
            list=datas.get(selectDate);
        }
        if(list==null){
            list= new ArrayList<>(0);
        }
        int lessonIndex=0;
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.set(Calendar.YEAR,selectyear);
        calendar.set(Calendar.MONTH,selectMonth);
        for(int i=0;i<dayCount;i++){
            calendar.set(Calendar.DAY_OF_MONTH,i+1);
            tempLabel=new LessonLabelModel(ScheduleUtil.getDateYMDW(new Date(calendar.getTimeInMillis())),0,false);
            monthLists.add(tempLabel);
            for(int j=lessonIndex;j<list.size();j++){
                if(ScheduleUtil.isSameDay(list.get(j).schedule.getStart(),calendar)){//是否同一天
                    monthLists.add(list.get(j));
                    tempLabel.lessonCount++;
                    tempLabel.hasData=true;
                    lessonIndex++;
                }else {//不是同一天，一次从这天开始
                    tempLabel.lessonCount=j-lessonIndex;
                    if(j-lessonIndex>0){
                        tempLabel.hasData=true;
                    }
                    break;
                }
            }
        }
        monthLists.add(new LastEmptyModel());

        mAdapter.setList(monthLists);
        mAdapter.notifyDataSetChanged();
        mAdapter.scrollToLabel(ScheduleUtil.getDateYMD(selectyear,selectMonth,selectDay));
    }

    @OnClick({R.id.right_view})
    public void onClick(View v){
        switch (v.getId()){
            case R.id.right_view:
                startActivityForResult(new Intent(this, CreateTimetableActivity.class),
                        REQUEST_NEW_LESSON_CODE);
                break;
            case R.id.left_view:
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_NEW_LESSON_CODE:
                    //新添加的课
                    ClassLesson classLesson =
                            (ClassLesson) data.getSerializableExtra(CreateTimetableActivity.EXTRA_CLASS_LESSON);
                    CreateClassActivity.addClassLesson(classLesson);
                    //TODO 更新显示
                    datas= ScheduleUtil.buildScheduleByMonth(CreateClassActivity.classLessons);
                    bindData();
                    break;
            }
        }
    }
}
