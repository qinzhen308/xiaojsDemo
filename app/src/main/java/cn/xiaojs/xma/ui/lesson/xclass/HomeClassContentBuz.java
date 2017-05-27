package cn.xiaojs.xma.ui.lesson.xclass;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.schedule.ScheduleLayout;
import com.jeek.calendar.widget.calendar.schedule.ScheduleRecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.model.ctl.ScheduleData;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.ScanQrcodeActivity;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;
import cn.xiaojs.xma.ui.lesson.xclass.Model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleFilter;
import cn.xiaojs.xma.ui.lesson.xclass.view.PageChangeListener;
import cn.xiaojs.xma.ui.search.SearchActivity;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;
import cn.xiaojs.xma.ui.widget.CommonDialog;

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
    @BindView(R.id.title)
    LinearLayout title;
    @BindView(R.id.over_layout)
    ScheduleRecyclerView overLayout;
    @BindView(R.id.slSchedule)
    ScheduleLayout calendarView;

    HomeClassAdapter mAdapter;

    int year, month,day;


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
        day=calendar.get(Calendar.DAY_OF_MONTH);
        month =calendar.get(Calendar.MONTH);
        year=calendar.get(Calendar.YEAR);
        initListView();
        doRequest();
    }

    private void initListView() {
        mAdapter = new HomeClassAdapter();
        overLayout.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        overLayout.setAdapter(mAdapter);

        calendarView.setOnCalendarClickListener(new OnCalendarClickListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                HomeClassContentBuz.this.year=year;
                HomeClassContentBuz.this.month=month;
                HomeClassContentBuz.this.day=day;
                doRequest();
            }

            @Override
            public void onPageChange(int year, int month, int day) {
                getMonthData();
            }
        });
        overLayout.addOnScrollListener(new PageChangeListener());

    }


    private void test() {



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                calendarView.addTaskHint(5);
                calendarView.addTaskHint(10);
                calendarView.addTaskHint(15);
            }
        }, 2000);

    }


    @OnClick({R.id.btn_scan2, R.id.s_root, R.id.btn_add})
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
                        mContext.startActivity(new Intent(mContext,CreateClassActivity.class));
                        break;
                    case 0:
                        if (AccountDataManager.isTeacher(mContext)) {
                            //老师可以开课
                            Intent intent = new Intent(mContext, LessonCreationActivity.class);
                            mContext.startActivityForResult(intent, CourseConstant.CODE_CREATE_LESSON);
                        } else {
                            //提示申明教学能力
                            final CommonDialog dialog = new CommonDialog(mContext);
                            dialog.setTitle(R.string.declare_teaching_ability);
                            dialog.setDesc(R.string.declare_teaching_ability_tip);
                            dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                    Intent intent = new Intent(mContext, TeachingSubjectActivity.class);
                                    mContext.startActivity(intent);
                                }
                            });
                            dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                                @Override
                                public void onClick() {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                        }
                        break;
                }

            }
        });
        int offset = mContext.getResources().getDimensionPixelSize(R.dimen.px68);
        menu.show(targetView, offset);
    }


    private void doRequest(){
        final int y=year;
        final int m=month;
        final int d=day;
        long date=ScheduleFilter.ymdToTimeMill(y,m,d);
        LessonDataManager.getClassesSchedule(mContext, date, date, new APIServiceCallback<ScheduleData>() {
            @Override
            public void onSuccess(ScheduleData object) {
                LessonLabelModel label=new LessonLabelModel(ScheduleFilter.getDateYMD(y,m,d)+" "+ScheduleFilter.getWeek(y,m,d),0,false);
                ArrayList list=new ArrayList();
                list.add(label);
                if(object!=null&&!object.calendar.isEmpty()){
                    ClassSchedule schedule=object.calendar.get(0);
                    label.hasData=true;
                    label.lessonCount=schedule.lessons.size();
                    list.addAll(schedule.lessons);
                }
                mAdapter.setList(list);
                mAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(String errorCode, String errorMessage) {
                LessonLabelModel label=new LessonLabelModel(ScheduleFilter.getDateYMD(y,m,d)+" "+ScheduleFilter.getWeek(y,m,d),0,false);
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
        long date=ScheduleFilter.ymdToTimeMill(y,m,d);
        LessonDataManager.getClassesSchedule(mContext, "monthly", 0, 0, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });

    }
}
