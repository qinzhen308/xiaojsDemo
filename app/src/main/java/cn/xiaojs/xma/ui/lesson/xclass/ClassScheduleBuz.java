package cn.xiaojs.xma.ui.lesson.xclass;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.TextView;

import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.HintBoxPool;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.OnScheduleChangeListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.schedule.ScheduleRecyclerView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import butterknife.BindColor;
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
    @BindView(R.id.tv_top_date)
    TextView tvTopDate;

    HomeClassAdapter mAdapter;

    int selectDay;
    int selectyear;
    int selectMonth;
    int todayYear, todayMonth,todayDay;

    String classId;

    @BindColor(R.color.orange_point)
    int c_red;
    @BindColor(R.color.grey_point)
    int c_gray;

    /**
     * @param context
     * @param root
     * @link cn.xiaojs.xma.R.layout.fragment_home_class_normal.xml
     */
    public void init(Activity context, View root) {
        mContext = context;
        mRoot = root;
//        R.layout.fragment_home_class_normal
        classId=context.getIntent().getStringExtra(ClassScheduleActivity.EXTRA_ID);
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
        todayDay=selectDay=calendar.get(Calendar.DAY_OF_MONTH);
        todayMonth=selectMonth =calendar.get(Calendar.MONTH);
        todayYear=selectyear=calendar.get(Calendar.YEAR);
        tvTopDate.setText(ScheduleUtil.getDateYM_Ch(new Date()));
    }


    private void setListener(){
        calendarView.setHintBoxTag(HintBoxPool.TAG_SENCOND_BOX);
        calendarView.setOnScheduleChangeListener(new OnScheduleChangeListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                if(year!=selectyear||month!=selectMonth||day!=selectDay){
                    mAdapter.scrollToLabel(ScheduleUtil.getDateYMD(year,month,day));
                    tvTopDate.setText(ScheduleUtil.getDateYM_Ch(year,month,day));
                }
                selectyear=year;
                selectMonth=month;
                selectDay=day;
            }

            @Override
            public void onWeekChange(int year, int month, int day) {
                
            }

            @Override
            public void onMonthChange(int year, int month, int day) {
                getMonthData(year,month,day);

            }
        });

    }

    private void getMonthData(final int y,final int m,final int d){
        long start= ScheduleUtil.ymdToTimeMill(y,m,1);
        long end=0;
        if(m==11){
            end=ScheduleUtil.ymdToTimeMill(y+1,1,1)-1;
        }else {
            end=ScheduleUtil.ymdToTimeMill(y,m+1,1)-1;
        }
        if(XiaojsConfig.DEBUG){
            Logger.d("----qz----monthData--UTC--("+ScheduleUtil.getUTCDate(start)+")----("+ScheduleUtil.getUTCDate(end)+")");
            Logger.d("----qz----monthData--GMT8:00--("+ScheduleUtil.getDateYMDHMS(start)+")----("+ScheduleUtil.getDateYMDHMS(end)+")");
        }
        Map map=LessonDataManager.createScheduleOptions(null,null,null,ScheduleUtil.getUTCDate(start), ScheduleUtil.getUTCDate(end),null,null,null,null,null);

        LessonDataManager.getClassesSchedule(mContext,classId, map, new APIServiceCallback<ScheduleData>() {
            @Override
            public void onSuccess(ScheduleData object) {

                if(object!=null&&!object.calendar.isEmpty()){
                    setPoint(object.calendar,y,m,d);
                    bindData(object.calendar,y,m,d);
                }else {
                    bindData(null,y,m,d);
                    setPoint(new ArrayList<ClassSchedule>(0),y,m,d);
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

    private void setPoint(List<ClassSchedule> list,final int y,final int m,final int d){
        HashSet hashSet=new HashSet<Integer>();
        HashMap<String , Integer> colors=new HashMap<String, Integer>();
            for(int i=0;i<list.size();i++){
                String[] strings=list.get(i).date.split("-");
                int da=Integer.valueOf(strings[2]);
                int mo=Integer.valueOf(strings[1])-1;
                int ye=Integer.valueOf(strings[0]);

                if(mo==m){
                    hashSet.add(da);
                    if(todayYear>ye||todayMonth>mo||todayDay>da){//今天之前
                        colors.put(list.get(i).date,c_gray);//灰色
                    }else {
                        colors.put(list.get(i).date,c_red);//红色
                    }
                }
            }
        HintBoxPool.box(HintBoxPool.TAG_SENCOND_BOX).setMonthDates(colors);
        calendarView.hintBoxChanged();
    }


    /**
     * 刷新指定日期对应月份数据
     * @param date 格式：yyyy-MM-dd
     */
    public void updateAndMoveToDate(String date){
        String[] ymd=date.split("-");
        selectDay=Integer.valueOf(ymd[2]);
        selectMonth=Integer.valueOf(ymd[1])-1;
        selectyear=Integer.valueOf(ymd[0]);
        getMonthData(selectyear,selectMonth,selectDay);
        calendarView.post(new Runnable() {
            @Override
            public void run() {
                calendarView.initData(selectyear,selectMonth,selectDay);
            }
        });
    }


    public long getSelectedDate(){
        return ScheduleUtil.ymdToTimeMill(selectyear,selectMonth,selectDay);
    }

}
