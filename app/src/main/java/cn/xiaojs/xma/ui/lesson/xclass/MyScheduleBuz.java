package cn.xiaojs.xma.ui.lesson.xclass;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jeek.calendar.widget.calendar.HintBoxPool;
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
import java.util.TimeZone;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.pageload.EventCallback;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.ctl.ClassSchedule;
import cn.xiaojs.xma.model.ctl.PrivateClass;
import cn.xiaojs.xma.model.ctl.ScheduleData;
import cn.xiaojs.xma.model.ctl.ScheduleOptions;
import cn.xiaojs.xma.model.recordedlesson.RLesson;
import cn.xiaojs.xma.model.recordedlesson.RecordedLessonCriteria;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.ScanQrcodeActivity;
import cn.xiaojs.xma.ui.classroom2.chat.SingleSessionFragment;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.xclass.model.ClassFooterModel;
import cn.xiaojs.xma.ui.lesson.xclass.model.ClassLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.model.LastEmptyModel;
import cn.xiaojs.xma.ui.lesson.xclass.model.LessonLabelModel;
import cn.xiaojs.xma.ui.lesson.xclass.model.LoadStateMode;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.lesson.xclass.view.HomeClassLabelView;
import cn.xiaojs.xma.ui.lesson.xclass.view.PageChangeListener;
import cn.xiaojs.xma.ui.recordlesson.CreateRecordlessonActivity;
import cn.xiaojs.xma.ui.recordlesson.model.HomeRLessonFooterModel;
import cn.xiaojs.xma.ui.recordlesson.util.RLessonFilterHelper;
import cn.xiaojs.xma.ui.search.SearchActivity;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.JudgementUtil;

/**
 * Created by Paul Z on 2017/5/22.
 */

public class MyScheduleBuz {
    Activity mContext;
    View mRoot;
    @BindView(R.id.over_layout)
    ScheduleRecyclerView overLayout;
    @BindView(R.id.slSchedule)
    ScheduleLayout calendarView;

    HomeClassAdapter mAdapter;

    int year, month, day;
    int todayYear, todayMonth, todayDay;

    private List<RLesson> recordedLessonList;
    private List<PrivateClass> hotClass;
    private ScheduleData curLessons;
    LessonLabelModel label;

    @BindColor(R.color.orange_point)
    int c_red;
    @BindColor(R.color.grey_point)
    int c_gray;

    ServiceRequest mRequest;

    LastEmptyModel lastEmptyModel = new LastEmptyModel();
    ClassLabelModel classLabel = new ClassLabelModel(false);

    int curSubTab;


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
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        todayDay = day = calendar.get(Calendar.DAY_OF_MONTH);
        todayMonth = month = calendar.get(Calendar.MONTH);
        todayYear = year = calendar.get(Calendar.YEAR);
        initListView();
        doRequest(todayYear, todayMonth, todayDay);
        getMonthData();
    }

    private EventCallback adapterEventCallback = new EventCallback() {
        @Override
        public void onEvent(int what, Object... object) {
            switch (what) {
                case EVENT_1://切换班和录播课
                    curSubTab = (int) object[0];
                    bindHomeDatas(false);
                    break;
                case EVENT_3://点击老师头像，进聊天页面
                    SingleSessionFragment.invoke(((FragmentActivity)mContext).getSupportFragmentManager(),(String)object[0],(String)object[1],-1);
                    break;
            }
        }
    };

    private void initListView() {
        if(mOnMonthChangeListener!=null){
            mOnMonthChangeListener.onMonthChange(ScheduleUtil.getDateYM_Ch(new Date()));
        }
        mAdapter = new HomeClassAdapter(overLayout);
        mAdapter.setCallback(adapterEventCallback);
        overLayout.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        overLayout.setAdapter(mAdapter);
        calendarView.setHintBoxTag(HintBoxPool.TAG_FIRST_BOX);
        calendarView.setOnScheduleChangeListener(new OnScheduleChangeListener() {
            @Override
            public void onClickDate(int year, int month, int day) {
                if (XiaojsConfig.DEBUG) {
                    Logger.d("----qz----calendar---onClickDate---" + year + "年" + (month + 1) + "月" + day);
                }
                if (MyScheduleBuz.this.month != month || MyScheduleBuz.this.year != year || MyScheduleBuz.this.day != day) {
                    prepareRequestDay(month == MyScheduleBuz.this.month ? 0 : 450);
                    MyScheduleBuz.this.day = day;
                    MyScheduleBuz.this.year = year;
                    MyScheduleBuz.this.month = month;
//                    doRequest(year,month,day);
                    if(mOnMonthChangeListener!=null){
                        mOnMonthChangeListener.onMonthChange(ScheduleUtil.getDateYM_Ch(year, month, day));
                    }
                }
            }

            @Override
            public void onWeekChange(int year, int month, int day) {
                if (XiaojsConfig.DEBUG) {
                    Logger.d("----qz----calendar---onWeekChange---" + year + "年" + (month + 1) + "月" + day);
                }

            }

            @Override
            public void onMonthChange(int year, int month, int day) {
                if (XiaojsConfig.DEBUG) {
                    Logger.d("----qz----calendar---onMonthChange---" + year + "年" + (month + 1) + "月" + day);
                }
                prepareRequestMonth();
//                if(todayYear==year&&todayMonth==month){
//                    btnToday.setVisibility(View.GONE);
//                }else {
//                    btnToday.setVisibility(View.VISIBLE);
//                }
            }
        });

        overLayout.addOnScrollListener(new PageChangeListener());
    }

    private void showMenu(View targetView) {
        CommonPopupMenu menu = new CommonPopupMenu(mContext);
        String[] items = mContext.getResources().getStringArray(R.array.add_menu2);
        menu.setWidth(mContext.getResources().getDimensionPixelSize(R.dimen.px280));
        menu.addTextItems(items);
        menu.addImgItems(new Integer[]{R.drawable.ic_menu_create_lesson,
                R.drawable.ic_create_tapedlesson,
                R.drawable.ic_add_class1});
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 2:
                        AnalyticEvents.onEvent(mContext, 34);
                        if (JudgementUtil.checkTeachingAbility(mContext)) {
                            mContext.startActivity(new Intent(mContext, CreateClassActivity.class));
                        }
                        break;
                    case 1:             //开录播课
                        if (JudgementUtil.checkTeachingAbility(mContext)) {
                            mContext.startActivity(new Intent(mContext, CreateRecordlessonActivity.class));
                        }
                        break;
                    case 0:
                        AnalyticEvents.onEvent(mContext, 33);
                        if (JudgementUtil.checkTeachingAbility(mContext)) {
                            //老师可以开课
                            Intent intent = new Intent(mContext, LessonCreationActivity.class);
                            intent.putExtra(LessonCreationActivity.EXTRA_NEED_TIP, true);
                            mContext.startActivityForResult(intent, CourseConstant.CODE_CREATE_LESSON);
                        }
                        break;
                }

            }
        });
        int offset = mContext.getResources().getDimensionPixelSize(R.dimen.px68);
        menu.show(targetView, offset);
    }


    private void cancelRequest() {
        if (mRequest != null) {
            mRequest.cancelRequest();
        }
    }

    /**
     * 获取某天数据
     *
     * @param y
     * @param m
     * @param d
     */
    private void doRequest(final int y, final int m, final int d) {
//        clearLessonsAndLoading(true);
        cancelRequest();
        long time = System.currentTimeMillis();
        long start = ScheduleUtil.ymdToTimeMill(y, m, d);
        long end = start + ScheduleUtil.DAY - 1000;
        if (XiaojsConfig.DEBUG) {
            Logger.d("----qz----start time mil=" + start + "---end time mil=" + end);
            Logger.d("----qz----start GMT+8:00 Time=" + ScheduleUtil.getDateYMDHMS(start) + "---end GMT+8:00 Time=" + ScheduleUtil.getDateYMDHMS(end));
            Logger.d("----qz----start UTC Time=" + ScheduleUtil.getUTCDate(start) + "---end UTC Time=" + ScheduleUtil.getUTCDate(end));
        }
        ScheduleOptions options = new ScheduleOptions.Builder().setStart(ScheduleUtil.getUTCDate(start))
                .setEnd(ScheduleUtil.getUTCDate(end))
                .build();
        mRequest = LessonDataManager.getClassesSchedule(mContext, options, new APIServiceCallback<ScheduleData>() {
            @Override
            public void onSuccess(ScheduleData object) {
//                clearLessonsAndLoading(false);
                label = new LessonLabelModel(ScheduleUtil.getDateYMD(y, m, d) + " " + ScheduleUtil.getWeek(y, m, d), 0, false);
                curLessons = object;
                bindHomeDatas(true);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                label = new LessonLabelModel(ScheduleUtil.getDateYMD(y, m, d) + " " + ScheduleUtil.getWeek(y, m, d), 0, false);
                curLessons = null;
                bindHomeDatas(true);
            }
        });
        Logger.d("-----qz-----time analyze---doRequest=" + (System.currentTimeMillis() - time));
    }


    /**
     * 获取月课表数据
     */
    private void getMonthData() {
        long time = System.currentTimeMillis();
        final int y = year;
        final int m = month;
        final int d = day;
        int next = (y - todayYear) * 12 + (m - todayMonth);
        ScheduleOptions options = new ScheduleOptions.Builder().setCycle("monthly").setNext("" + next)
                .build();
        if (XiaojsConfig.DEBUG)
            Logger.d("-----qz-----time analyze---getMonthData=" + (System.currentTimeMillis() - time));
        LessonDataManager.getClassesSchedule(mContext, options, new APIServiceCallback<ScheduleData>() {
            @Override
            public void onSuccess(ScheduleData object) {
                long time = System.currentTimeMillis();
                HashSet hashSet = new HashSet<Integer>();
                HashMap<String, Integer> colors = new HashMap<String, Integer>();
                if (object != null && object.calendar != null) {
                    for (int i = 0; i < object.calendar.size(); i++) {
                        String[] strings = object.calendar.get(i).date.split("-");
                        int da = Integer.valueOf(strings[2]);
                        int mo = Integer.valueOf(strings[1]) - 1;
                        int ye = Integer.valueOf(strings[0]);

                        if (mo == m) {
                            hashSet.add(da);
                            if (ScheduleUtil.compare(todayYear, todayMonth, todayDay, ye, mo, da) > 0) {//今天之前
                                colors.put(object.calendar.get(i).date, c_gray);//灰色
                            } else {
                                colors.put(object.calendar.get(i).date, c_red);//红色
                            }
                        }
                    }
                }
                HintBoxPool.box(HintBoxPool.TAG_FIRST_BOX).setMonthDates(colors);
                calendarView.hintBoxChanged();
//                calendarView.setTaskHintList(hashSet);
                if (XiaojsConfig.DEBUG)
                    Logger.d("-----qz-----time analyze---setPoint=" + (System.currentTimeMillis() - time));
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }


    private void bindHomeDatas(boolean backTop) {
        ArrayList list = new ArrayList();
        if (label != null) {
            list.add(label);
        }
        if (curLessons != null && !curLessons.calendar.isEmpty()) {
            ClassSchedule schedule = curLessons.calendar.get(0);
            label.hasData = true;
            label.lessonCount = schedule.lessons.size();
            list.addAll(schedule.lessons);
        }
        list.add(lastEmptyModel);
        mAdapter.setList(list);
        mAdapter.notifyDataSetChanged();
        if (backTop) {
            overLayout.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.scrollToPosition(0);
                }
            });
        }
    }


    public void update() {
        hotClass = null;
        getMonthData();
        doRequest(year, month, day);
    }


    private final static int BEGIN_REQUEST_MONTH = 0xff;
    private final static int BEGIN_REQUEST_DAY = 0xfe;

    private void prepareRequestMonth() {
        handler.removeMessages(BEGIN_REQUEST_MONTH);
        Message msg = new Message();
        msg.what = BEGIN_REQUEST_MONTH;
        handler.sendMessageDelayed(msg, 450);
    }

    private void prepareRequestDay(long delayed) {
        handler.removeMessages(BEGIN_REQUEST_DAY);
        Message msg = new Message();
        msg.what = BEGIN_REQUEST_DAY;
        handler.sendMessageDelayed(msg, delayed);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == BEGIN_REQUEST_MONTH) {
                getMonthData();
            } else if (msg.what == BEGIN_REQUEST_DAY) {
                doRequest(year, month, day);
            }
        }
    };

    public long getSelectedDate() {
        return ScheduleUtil.ymdToTimeMill(year, month, day);
    }


    public interface OnMonthChangeListener{
        public void onMonthChange(String Date);
    }

    OnMonthChangeListener mOnMonthChangeListener;

    public void setOnMonthChangeListener(OnMonthChangeListener onMonthChangeListener) {
        mOnMonthChangeListener = onMonthChangeListener;
    }
}
