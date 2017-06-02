package cn.xiaojs.xma.ui.lesson.xclass;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Debug;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.OnScheduleChangeListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.schedule.ScheduleRecyclerView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.model.ctl.PrivateClass;
import cn.xiaojs.xma.model.ctl.ScheduleData;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.ScanQrcodeActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;
import cn.xiaojs.xma.ui.lesson.xclass.Model.ClassLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.lesson.xclass.view.PageChangeListener;
import cn.xiaojs.xma.ui.search.SearchActivity;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.JudgementUtil;

/**
 * Created by Paul Z on 2017/5/22.
 */

public class HomeClassContentBuz {
    Activity mContext;
    View mRoot;
    @BindView(R.id.btn_scan2)
    ImageButton btnScan2;
    @BindView(R.id.my_course_search)
    TextView myCourseSearch;
    @BindView(R.id.btn_add)
    ImageButton btnAdd;
    @BindView(R.id.btn_today)
    ImageButton btnToday;
    @BindView(R.id.title)
    LinearLayout title;
    @BindView(R.id.over_layout)
    ScheduleRecyclerView overLayout;
    @BindView(R.id.slSchedule)
    ScheduleLayout calendarView;

    HomeClassAdapter mAdapter;

    int year, month,day;
    int todayYear, todayMonth,todayDay;

    private List<PrivateClass> hotClass;

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
        ButterKnife.bind(this, root);
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        todayDay=day=calendar.get(Calendar.DAY_OF_MONTH);
        todayMonth=month =calendar.get(Calendar.MONTH);
        todayYear=year=calendar.get(Calendar.YEAR);
        initListView();
        doRequest(todayYear,todayMonth,todayDay);
        getMonthData();
        loadHotClasses();
    }

    private void initListView() {
        mAdapter = new HomeClassAdapter(overLayout);
        overLayout.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        overLayout.setAdapter(mAdapter);

        calendarView.setOnScheduleChangeListener(new OnScheduleChangeListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                if(HomeClassContentBuz.this.month!=month||HomeClassContentBuz.this.year!=year||HomeClassContentBuz.this.day!=day){
                    HomeClassContentBuz.this.day=day;
                    HomeClassContentBuz.this.year=year;
                    HomeClassContentBuz.this.month=month;
                    doRequest(year,month,day);
                }
            }

            @Override
            public void onWeekChange(int year, int month, int day) {

            }

            @Override
            public void onMonthChange(int year, int month, int day) {
                getMonthData();
                if(todayYear==year&&todayMonth==month){
                    btnToday.setVisibility(View.GONE);
                }else {
                    btnToday.setVisibility(View.VISIBLE);
                }
            }
        });

        overLayout.addOnScrollListener(new PageChangeListener());

    }




    @OnClick({R.id.btn_scan2, R.id.s_root, R.id.btn_add,R.id.btn_today})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_scan2:
                if (PermissionUtil.isOverMarshmallow() && ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    mContext.requestPermissions(new String[]{Manifest.permission.CAMERA}, MainActivity.PERMISSION_CODE);
                } else {
                    mContext.startActivity(new Intent(mContext, ScanQrcodeActivity.class));
                }
                break;
            case R.id.s_root:
                mContext.startActivityForResult(new Intent(mContext,SearchActivity.class),100);
                break;
            case R.id.btn_add:
                showMenu(btnAdd);
                break;
            case R.id.btn_today:
                calendarView.initData(todayYear,todayMonth,todayDay);
                break;
        }
    }


    private void showMenu(View targetView) {
        CommonPopupMenu menu = new CommonPopupMenu(mContext);
        String[] items = mContext.getResources().getStringArray(R.array.add_menu2);
        menu.setWidth(mContext.getResources().getDimensionPixelSize(R.dimen.px280));
        menu.addTextItems(items);
        menu.addImgItems(new Integer[]{R.drawable.ic_menu_create_lesson,R.drawable.ic_add_class1});
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 1:
                        if (JudgementUtil.checkTeachingAbility(mContext)) {
                            mContext.startActivity(new Intent(mContext, CreateClassActivity.class));
                        }
                        break;
                    case 0:
                        if (JudgementUtil.checkTeachingAbility(mContext)) {
                            //老师可以开课
                            Intent intent = new Intent(mContext, LessonCreationActivity.class);
                            mContext.startActivityForResult(intent, CourseConstant.CODE_CREATE_LESSON);
                        }
                        break;
                }

            }
        });
        int offset = mContext.getResources().getDimensionPixelSize(R.dimen.px68);
        menu.show(targetView, offset);
    }


    private void doRequest(final int y,final int m,final int d){
        long start= ScheduleUtil.ymdToTimeMill(y,m,d);
        long end=start+ ScheduleUtil.DAY-1000;
        if(XiaojsConfig.DEBUG){
            Logger.d("----qz----start time mil="+start+"---end time mil="+end);
            Logger.d("----qz----start GMT+8:00 Time="+ScheduleUtil.getDateYMDHMS(start)+"---end GMT+8:00 Time="+ScheduleUtil.getDateYMDHMS(end));
            Logger.d("----qz----start UTC Time="+ScheduleUtil.getUTCDate(start)+"---end UTC Time="+ScheduleUtil.getUTCDate(end));
        }
        LessonDataManager.getClassesSchedule(mContext,"", ScheduleUtil.getUTCDate(start), ScheduleUtil.getUTCDate(end), new APIServiceCallback<ScheduleData>() {
            @Override
            public void onSuccess(ScheduleData object) {
                LessonLabelModel label=new LessonLabelModel(ScheduleUtil.getDateYMD(y,m,d)+" "+ ScheduleUtil.getWeek(y,m,d),0,false);
                ArrayList list=new ArrayList();
                list.add(label);
                if(object!=null&&!object.calendar.isEmpty()){
                    ClassSchedule schedule=object.calendar.get(0);
                    label.hasData=true;
                    label.lessonCount=schedule.lessons.size();
                    list.addAll(schedule.lessons);
                }
                mAdapter.setList(list);
                if(hotClass!=null){
                    bindHotClasses(hotClass);
                }
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(String errorCode, String errorMessage) {
                LessonLabelModel label=new LessonLabelModel(ScheduleUtil.getDateYMD(y,m,d)+" "+ ScheduleUtil.getWeek(y,m,d),0,false);
                ArrayList list=new ArrayList();
                list.add(label);
                mAdapter.setList(list);
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    private void getMonthData(){
        final int y=year;
        final int m=month;
        final int d=day;
        int next=(y-todayYear)*12+(m-todayMonth);
        LessonDataManager.getClassesSchedule(mContext,"", "monthly", next, 0, new APIServiceCallback<ScheduleData>() {
            @Override
            public void onSuccess(ScheduleData object) {
                HashSet hashSet=new HashSet<Integer>();
                HashMap<Integer , Integer> colors=new HashMap<Integer, Integer>();
                if(object!=null&&object.calendar!=null){
                    for(int i=0;i<object.calendar.size();i++){
                        String[] strings=object.calendar.get(i).date.split("-");
                        int da=Integer.valueOf(strings[2]);
                        int mo=Integer.valueOf(strings[1])-1;
                        int ye=Integer.valueOf(strings[0]);

                        if(mo==m){
                            hashSet.add(da);
                            if(todayYear>ye||todayMonth>mo||todayDay>da){//今天之前
                                colors.put(da,c_gray);//灰色
                            }else {
                                colors.put(da,c_red);//红色
                            }
                        }
                    }
                }
                calendarView.setTaskHintColors(colors,false);
                calendarView.setTaskHintList(hashSet);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    private void loadHotClasses(){
        LessonDataManager.getHotClasses(mContext, 4, new APIServiceCallback<CollectionResult<PrivateClass>>() {
            @Override
            public void onSuccess(CollectionResult<PrivateClass> object) {
                if(object!=null){
                    hotClass=object.results;
                    if(ArrayUtil.isEmpty(mAdapter.getList())){//

                    }else {//课先回来，绑定班
                        bindHotClasses(object.results);
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onFailure(String errorCode, String errorMessage) {
                if(!ArrayUtil.isEmpty(mAdapter.getList())){//课先回来，绑定班
                    bindHotClasses(null);
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }
    private void bindHotClasses(List<PrivateClass> list){
        ClassLabelModel classLabel=new ClassLabelModel(false);
        mAdapter.getList().add(classLabel);
        if(!ArrayUtil.isEmpty(list)){
            classLabel.hasData=true;
            mAdapter.getList().addAll(list);
        }
    }

    public void update(){
        getMonthData();
        doRequest(year,month,day);
        loadHotClasses();
    }
}
