package cn.xiaojs.xma.ui.lesson.xclass;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.schedule.ScheduleRecyclerView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.model.ctl.ScheduleData;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;

/**
 * Created by Paul Z on 2017/5/22.
 */

public class ClassScheduleBuz {
    Activity mContext;
    View mRoot;
    @BindView(R.id.over_layout)
    ScheduleRecyclerView overLayout;
    @BindView(R.id.slSchedule)
    ScheduleLayout calendarView;

    HomeClassAdapter mAdapter;

    int selectDay;
    int selectyear;
    int selectMonth;

    /**
     * @param context
     * @param root
     * @link cn.xiaojs.xma.R.layout.fragment_home_class_normal.xml
     */
    public void init(Activity context, View root) {
        mContext = context;
        mRoot = root;
//        R.layout.fragment_home_class_normal
        ButterKnife.bind(this, root);
        initListView();
        setListener();
        initDate();
        getMonthData(selectyear,selectMonth,selectDay);
    }

    private void initListView(){
        mAdapter=new HomeClassAdapter(overLayout);
        overLayout.setLayoutManager(new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false));
        overLayout.setAdapter(mAdapter);
    }

    private void initDate(){
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        selectDay=calendar.get(Calendar.DAY_OF_MONTH);
        selectMonth =calendar.get(Calendar.MONTH);
        selectyear=calendar.get(Calendar.YEAR);
    }


    private void setListener(){
        calendarView.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                if(year!=selectyear||month!=selectMonth||day!=selectDay){
                    mAdapter.scrollToLabel(ScheduleUtil.getDateYMD(year,month,day));
                }
            }

            @Override
            public void onPageChange(int year, int month, int day) {
                if(year!=selectyear||month!=selectMonth){
                    getMonthData(year,month,day);
                    selectyear=year;
                    selectMonth=month;
                }
            }
        });

    }

    private void getMonthData(final int y,final int m,final int d){
        long start= ScheduleUtil.ymdToTimeMill(y,m,1);
        long end=ScheduleUtil.ymdToTimeMill(y,m+1,1)-1000;
        if(XiaojsConfig.DEBUG){
            Logger.d("----qz----monthData--UTC--("+ScheduleUtil.getUTCDate(start)+")----("+ScheduleUtil.getUTCDate(end)+")");
            Logger.d("----qz----monthData--GMT8:00--("+ScheduleUtil.getDateYMDHMS(start)+")----("+ScheduleUtil.getDateYMDHMS(end)+")");
        }
        LessonDataManager.getClassesSchedule(mContext, ScheduleUtil.getUTCDate(start), ScheduleUtil.getUTCDate(end), new APIServiceCallback<ScheduleData>() {
            @Override
            public void onSuccess(ScheduleData object) {

                if(object!=null&&!object.calendar.isEmpty()){
                    setPoint(object.calendar);
                    bindData(object.calendar,y,m,d);
                }else {
                    bindData(null,y,m,d);
                    setPoint(new ArrayList<ClassSchedule>(0));
                }
            }
            @Override
            public void onFailure(String errorCode, String errorMessage) {
                bindData(null,y,m,d);
            }
        });
    }

    private void bindData(List<ClassSchedule> list,final int y,final int m,final int d){
        ArrayList monthLists=new ArrayList();
        int dayCount= CalendarUtils.getMonthDays(selectyear,selectMonth);
        LessonLabelModel tempLabel=null;
        if(list==null){
            list= new ArrayList<>(0);
        }
        int lessonIndex=0;
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        calendar.set(Calendar.YEAR,y);
        calendar.set(Calendar.MONTH,m);
        for(int i=0;i<dayCount;i++){
            calendar.set(Calendar.DAY_OF_MONTH,i+1);
            tempLabel=new LessonLabelModel(ScheduleUtil.getDateYMDW(new Date(calendar.getTimeInMillis())),0,false);
            monthLists.add(tempLabel);
            for(int j=lessonIndex;j<list.size();j++){
                ClassSchedule cs=list.get(j);
                if(ScheduleUtil.isSameDay(ScheduleUtil.getDateYMD(cs.date),calendar)){//是否同一天
                    monthLists.addAll(cs.lessons);
                    tempLabel.lessonCount=cs.lessons.size();
                    if(cs.lessons.size()>0){
                        tempLabel.hasData=true;
                    }
                    //这里视为后台已经按照升序排列过
                    lessonIndex=j;
                    break;
                }
            }
        }
        monthLists.add(new LastEmptyModel());

        mAdapter.setList(monthLists);
        mAdapter.notifyDataSetChanged();
        mAdapter.scrollToLabel(ScheduleUtil.getDateYMD(y,m,d));
    }

    private void setPoint(List<ClassSchedule> list){
        HashSet hashSet=new HashSet<Integer>();
        for(int i=0;i<list.size();i++){
            String[] strings=list.get(i).date.split("-");
            hashSet.add(Integer.valueOf(strings[2]));
        }
        calendarView.setTaskHintList(hashSet);
    }


}
