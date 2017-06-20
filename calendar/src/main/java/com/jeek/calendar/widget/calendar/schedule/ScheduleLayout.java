package com.jeek.calendar.widget.calendar.schedule;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.jeek.calendar.library.R;
import com.jeek.calendar.widget.calendar.CalendarUtils;
import com.jeek.calendar.widget.calendar.OnCalendarClickListener;
import com.jeek.calendar.widget.calendar.OnScheduleChangeListener;
import com.jeek.calendar.widget.calendar.month.MonthCalendarView;
import com.jeek.calendar.widget.calendar.month.MonthView;
import com.jeek.calendar.widget.calendar.week.WeekCalendarView;
import com.jeek.calendar.widget.calendar.week.WeekView;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by Jimmy on 2016/10/7 0007.
 */
public class ScheduleLayout extends FrameLayout {

    private final int DEFAULT_MONTH = 0;
    private final int DEFAULT_WEEK = 1;

    private MonthCalendarView mcvCalendar;
    private WeekCalendarView wcvCalendar;
    private ViewGroup rlMonthCalendar;
    private ViewGroup rlScheduleList;
    private ScrollConflictResolver conflictResolver;

    private int mCurrentSelectYear;
    private int mCurrentSelectMonth;
    private int mCurrentSelectDay;
    private int mRowSize;
    private int mMinDistance;
    private int mAutoScrollDistance;
    private int mDefaultView;
    private float mDownPosition[] = new float[2];
    private boolean mIsScrolling = false;
    private boolean mIsAutoChangeMonthRow;
    private boolean mCurrentRowsIsSix = true;

    private ScheduleState mState;
//    private OnCalendarClickListener mOnCalendarClickListener;
    private OnScheduleChangeListener mOnScheduleChangeListener;
    private GestureDetector mGestureDetector;

    public ScheduleLayout(Context context) {
        this(context, null);
    }

    public ScheduleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScheduleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context.obtainStyledAttributes(attrs, R.styleable.ScheduleLayout));
        initDate();
        initGestureDetector();
    }

    private void initAttrs(TypedArray array) {
        mDefaultView = array.getInt(R.styleable.ScheduleLayout_default_view, DEFAULT_MONTH);
        mIsAutoChangeMonthRow = array.getBoolean(R.styleable.ScheduleLayout_auto_change_month_row, false);
        array.recycle();
        mState = ScheduleState.OPEN;
        mRowSize = getResources().getDimensionPixelSize(R.dimen.week_calendar_height);
        mMinDistance = getResources().getDimensionPixelSize(R.dimen.calendar_min_distance);
        mAutoScrollDistance = getResources().getDimensionPixelSize(R.dimen.auto_scroll_distance);
    }

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(getContext(), new OnScheduleScrollListener(this));
    }

    private void initDate() {
        Calendar calendar = Calendar.getInstance();
        resetCurrentSelectDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mcvCalendar = (MonthCalendarView) findViewById(R.id.mcvCalendar);
        wcvCalendar = (WeekCalendarView) findViewById(R.id.wcvCalendar);
        rlMonthCalendar = (ViewGroup) findViewById(R.id.rlMonthCalendar);
        rlScheduleList = (ViewGroup) findViewById(R.id.over_layout_wraper);
        View conflictView = findViewById(R.id.over_layout);
        if(conflictView instanceof ScrollConflictResolver){
            conflictResolver=(ScrollConflictResolver)conflictView;
        }else {
            conflictResolver=new ScrollConflictResolver(){
                @Override
                public boolean resolve() {
                    return false;
                }
            };
        }
        bindingMonthAndWeekCalendar();
    }

    private void bindingMonthAndWeekCalendar() {
        mcvCalendar.setOnCalendarClickListener(mMonthCalendarClickListener);
        wcvCalendar.setOnCalendarClickListener(mWeekCalendarClickListener);
        // 初始化视图
        Calendar calendar = Calendar.getInstance();
        if (mIsAutoChangeMonthRow) {
            mCurrentRowsIsSix = CalendarUtils.getMonthRows(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH)) == 6;
        }
        if (mDefaultView == DEFAULT_MONTH) {
            wcvCalendar.setVisibility(INVISIBLE);
            mState = ScheduleState.OPEN;
            if (!mCurrentRowsIsSix) {
                rlScheduleList.setY(rlScheduleList.getY() - mRowSize);
            }
        } else if (mDefaultView == DEFAULT_WEEK) {
            wcvCalendar.setVisibility(VISIBLE);
            mState = ScheduleState.CLOSE;
            int row = CalendarUtils.getWeekRow(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            rlMonthCalendar.setY(-row * mRowSize);
            rlScheduleList.setY(rlScheduleList.getY() - 5 * mRowSize);
        }
    }

    private void resetCurrentSelectDate(int year, int month, int day) {
        mCurrentSelectYear = year;
        mCurrentSelectMonth = month;
        mCurrentSelectDay = day;
    }

    private OnCalendarClickListener mMonthCalendarClickListener = new OnCalendarClickListener() {
        int y,m,d=0;
        @Override
        public void onClickDate(int year, int month, int day) {
            wcvCalendar.setOnCalendarClickListener(null);
            int weeks = CalendarUtils.getWeeksAgo(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay, year, month, day);
            resetCurrentSelectDate(year, month, day);
            int position = wcvCalendar.getCurrentItem() + weeks;
            if (weeks != 0) {
                wcvCalendar.setCurrentItem(position, false);
            }
            resetWeekView(position);
            wcvCalendar.setOnCalendarClickListener(mWeekCalendarClickListener);
        }

        @Override
        public void onPageChange(int year, int month, int day) {
            computeCurrentRowsIsSix(year, month);
            if(mOnScheduleChangeListener!=null){
                mOnScheduleChangeListener.onMonthChange(year, month, day);

            }
        }
    };

    private void computeCurrentRowsIsSix(int year, int month) {
        if (mIsAutoChangeMonthRow) {
            boolean isSixRow = CalendarUtils.getMonthRows(year, month) == 6;
            if (mCurrentRowsIsSix != isSixRow) {
                mCurrentRowsIsSix = isSixRow;
                if (mState == ScheduleState.OPEN) {
                    if (mCurrentRowsIsSix) {
                        AutoMoveAnimation animation = new AutoMoveAnimation(rlScheduleList, mRowSize);
                        rlScheduleList.startAnimation(animation);
                    } else {
                        AutoMoveAnimation animation = new AutoMoveAnimation(rlScheduleList, -mRowSize);
                        rlScheduleList.startAnimation(animation);
                    }
                }
            }
        }
    }

    private void resetWeekView(int position) {
        WeekView weekView = wcvCalendar.getCurrentWeekView();
        if (weekView != null) {
            weekView.setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
            weekView.setTaskHintList(mHintList);
            weekView.setTaskHintColors(mHintColors,false);
            weekView.invalidate();
        } else {
            WeekView newWeekView = wcvCalendar.getWeekAdapter().instanceWeekView(position);
            newWeekView.setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
            newWeekView.setTaskHintList(mHintList);
            newWeekView.setTaskHintColors(mHintColors,false);
            newWeekView.invalidate();
            wcvCalendar.setCurrentItem(position);
        }
        if (mOnScheduleChangeListener != null) {
            mOnScheduleChangeListener.onClickDate(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
        }
    }

    private OnCalendarClickListener mWeekCalendarClickListener = new OnCalendarClickListener() {
        @Override
        public void onClickDate(int year, int month, int day) {
            mcvCalendar.setOnCalendarClickListener(null);
            int months = CalendarUtils.getMonthsAgo(mCurrentSelectYear, mCurrentSelectMonth, year, month);
            resetCurrentSelectDate(year, month, day);
            if (months != 0) {
                int position = mcvCalendar.getCurrentItem() + months;
                mcvCalendar.setCurrentItem(position, false);
            }
            resetMonthView();
            mcvCalendar.setOnCalendarClickListener(mMonthCalendarClickListener);
        }

        @Override
        public void onPageChange(int year, int month, int day) {
            if (mIsAutoChangeMonthRow) {
                if (mCurrentSelectMonth != month) {
                    mCurrentRowsIsSix = CalendarUtils.getMonthRows(year, month) == 6;
                }
            }
            if(mOnScheduleChangeListener!=null){
                mOnScheduleChangeListener.onWeekChange(year, month, day);

                if (mCurrentSelectMonth != month) {
                    mOnScheduleChangeListener.onMonthChange(year,month,day);
                }
            }
        }
    };

    private void resetMonthView() {
        MonthView monthView = mcvCalendar.getCurrentMonthView();
        if (monthView != null) {
            monthView.setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
            monthView.setTaskHintList(mHintList);
            monthView.setTaskHintColors(mHintColors,false);
            monthView.invalidate();
        }
        if (mOnScheduleChangeListener != null) {
            mOnScheduleChangeListener.onClickDate(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
        }
        resetCalendarPosition();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        resetViewHeight(rlScheduleList, height - mRowSize);
//        resetViewHeight(this, height + 100);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private void resetViewHeight(View view, int height) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams.height != height) {
            layoutParams.height = height;
            view.setLayoutParams(layoutParams);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownPosition[0] = ev.getRawX();
                mDownPosition[1] = ev.getRawY();
                mGestureDetector.onTouchEvent(ev);
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mIsScrolling) {
            return true;
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                float x = ev.getRawX();
                float y = ev.getRawY();
                float distanceX = Math.abs(x - mDownPosition[0]);
                float distanceY = Math.abs(y - mDownPosition[1]);
                if (distanceY > mMinDistance && distanceY > distanceX * 2.0f) {
                    return (y > mDownPosition[1] && isRecyclerViewTouch()) || (y < mDownPosition[1] && mState == ScheduleState.OPEN);
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    private boolean isRecyclerViewTouch() {
        return mState == ScheduleState.CLOSE && (conflictResolver.resolve());
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownPosition[0] = event.getRawX();
                mDownPosition[1] = event.getRawY();
                resetCalendarPosition();
                return true;
            case MotionEvent.ACTION_MOVE:
                transferEvent(event);
                mIsScrolling = true;
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                transferEvent(event);
                changeCalendarState();
                resetScrollingState();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void transferEvent(MotionEvent event) {
        if (mState == ScheduleState.CLOSE) {
            mcvCalendar.setVisibility(VISIBLE);
            wcvCalendar.setVisibility(INVISIBLE);
            mGestureDetector.onTouchEvent(event);
        } else {
            mGestureDetector.onTouchEvent(event);
        }
    }

    private void changeCalendarState() {
        if (rlScheduleList.getY() > mRowSize * 2 &&
                rlScheduleList.getY() < mcvCalendar.getHeight() - mRowSize) { // 位于中间
            ScheduleAnimation animation = new ScheduleAnimation(this, mState, mAutoScrollDistance);
            animation.setDuration(300);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    changeState();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlScheduleList.startAnimation(animation);
        } else if (rlScheduleList.getY() <= mRowSize * 2) { // 位于顶部
            ScheduleAnimation animation = new ScheduleAnimation(this, ScheduleState.OPEN, mAutoScrollDistance);
            animation.setDuration(50);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mState == ScheduleState.OPEN) {
                        changeState();
                    } else {
                        resetCalendar();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlScheduleList.startAnimation(animation);
        } else {
            ScheduleAnimation animation = new ScheduleAnimation(this, ScheduleState.CLOSE, mAutoScrollDistance);
            animation.setDuration(50);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mState == ScheduleState.CLOSE) {
                        mState = ScheduleState.OPEN;
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            rlScheduleList.startAnimation(animation);
        }
    }

    private void resetCalendarPosition() {
        if (mState == ScheduleState.OPEN) {
            rlMonthCalendar.setY(0);
            if (mCurrentRowsIsSix) {
                rlScheduleList.setY(mcvCalendar.getHeight());
            } else {
                rlScheduleList.setY(mcvCalendar.getHeight() - mRowSize);
            }
        } else {
            rlMonthCalendar.setY(-CalendarUtils.getWeekRow(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay) * mRowSize);
            rlScheduleList.setY(mRowSize);
        }
    }

    private void resetCalendar() {
        if (mState == ScheduleState.OPEN) {
            mcvCalendar.setVisibility(VISIBLE);
            wcvCalendar.setVisibility(INVISIBLE);
        } else {
            mcvCalendar.setVisibility(INVISIBLE);
            wcvCalendar.setVisibility(VISIBLE);
        }
    }

    private void changeState() {
        if (mState == ScheduleState.OPEN) {
            mState = ScheduleState.CLOSE;
            mcvCalendar.setVisibility(INVISIBLE);
            wcvCalendar.setVisibility(VISIBLE);
            rlMonthCalendar.setY((1 - mcvCalendar.getCurrentMonthView().getWeekRow()) * mRowSize);
            checkWeekCalendar();
        } else {
            mState = ScheduleState.OPEN;
            mcvCalendar.setVisibility(VISIBLE);
            wcvCalendar.setVisibility(INVISIBLE);
            rlMonthCalendar.setY(0);
        }
    }

    private void checkWeekCalendar() {
        WeekView weekView = wcvCalendar.getCurrentWeekView();
        DateTime start = weekView.getStartDate();
        DateTime end = weekView.getEndDate();
        DateTime current = new DateTime(mCurrentSelectYear, mCurrentSelectMonth + 1, mCurrentSelectDay, 23, 59, 59);
        int week = 0;
        while (current.getMillis() < start.getMillis()) {
            week--;
            start = start.plusDays(-7);
        }
        current = new DateTime(mCurrentSelectYear, mCurrentSelectMonth + 1, mCurrentSelectDay, 0, 0, 0);
        if (week == 0) {
            while (current.getMillis() > end.getMillis()) {
                week++;
                end = end.plusDays(7);
            }
        }
        if (week != 0) {
            int position = wcvCalendar.getCurrentItem() + week;
            if (wcvCalendar.getWeekViews().get(position) != null) {
                wcvCalendar.getWeekViews().get(position).setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
                wcvCalendar.getWeekViews().get(position).invalidate();
            } else {
                WeekView newWeekView = wcvCalendar.getWeekAdapter().instanceWeekView(position);
                newWeekView.setSelectYearMonth(mCurrentSelectYear, mCurrentSelectMonth, mCurrentSelectDay);
                newWeekView.invalidate();
            }
            wcvCalendar.setCurrentItem(position, false);
        }
    }

    private void resetScrollingState() {
        mDownPosition[0] = 0;
        mDownPosition[1] = 0;
        mIsScrolling = false;
    }

    protected void onCalendarScroll(float distanceY) {
        MonthView monthView = mcvCalendar.getCurrentMonthView();
        distanceY = Math.min(distanceY, mAutoScrollDistance);
        float calendarDistanceY = distanceY / (mCurrentRowsIsSix ? 5.0f : 4.0f);
        int row = monthView.getWeekRow() - 1;
        int calendarTop = -row * mRowSize;
        int scheduleTop = mRowSize;
        float calendarY = rlMonthCalendar.getY() - calendarDistanceY * row;
        calendarY = Math.min(calendarY, 0);
        calendarY = Math.max(calendarY, calendarTop);
        rlMonthCalendar.setY(calendarY);
        float scheduleY = rlScheduleList.getY() - distanceY;
        if (mCurrentRowsIsSix) {
            scheduleY = Math.min(scheduleY, mcvCalendar.getHeight());
        } else {
            scheduleY = Math.min(scheduleY, mcvCalendar.getHeight() - mRowSize);
        }
        scheduleY = Math.max(scheduleY, scheduleTop);
        rlScheduleList.setY(scheduleY);
    }

    public void setOnScheduleChangeListener(OnScheduleChangeListener onScheduleChangeListener) {
        mOnScheduleChangeListener = onScheduleChangeListener;
    }

    private void resetMonthViewDate(final int year, final int month, final int day, final int position) {
        if (mcvCalendar.getMonthViews().get(position) == null) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    resetMonthViewDate(year, month, day, position);
                }
            }, 50);
        } else {
            mcvCalendar.getMonthViews().get(position).clickThisMonth(year, month, day);
        }
    }

    /**
     * 初始化年月日
     *
     * @param year
     * @param month (0-11)
     * @param day   (1-31)
     */
    public void initData(int year, int month, int day) {
        int monthDis = CalendarUtils.getMonthsAgo(mCurrentSelectYear, mCurrentSelectMonth, year, month);
        int position = mcvCalendar.getCurrentItem() + monthDis;
        mcvCalendar.setCurrentItem(position);
        resetMonthViewDate(year, month, day, position);
    }

    /**
     * 添加一个圆点提示
     *
     * @param day
     */
    public void addTaskHint(Integer day) {
        if (mcvCalendar.getCurrentMonthView() != null)
            mcvCalendar.getCurrentMonthView().addTaskHint(day);
        if (wcvCalendar.getCurrentWeekView() != null)
            wcvCalendar.getCurrentWeekView().addTaskHint(day);

    }

    /**
     * 删除一个圆点提示
     *
     * @param day
     */
    public void removeTaskHint(Integer day) {
        if (mcvCalendar.getCurrentMonthView() != null)
            mcvCalendar.getCurrentMonthView().removeTaskHint(day);
        if (wcvCalendar.getCurrentWeekView() != null)
            wcvCalendar.getCurrentWeekView().removeTaskHint(day);
    }

    public void setHintBoxTag(String tag){
        mcvCalendar.setHintBoxTag(tag);
        wcvCalendar.setHintBoxTag(tag);
    }

    public void hintBoxChanged() {
        if (mcvCalendar.getCurrentMonthView() != null)
            mcvCalendar.getCurrentMonthView().invalidate();
        if (wcvCalendar.getCurrentWeekView() != null)
            wcvCalendar.getCurrentWeekView().invalidate();
    }

    private HashSet<Integer> mHintList=new HashSet<>();

    public void setTaskHintList(HashSet<Integer> hintList) {
        mHintList=hintList;
        if (mcvCalendar.getCurrentMonthView() != null)
            mcvCalendar.getCurrentMonthView().setTaskHintList(hintList);
        if (wcvCalendar.getCurrentWeekView() != null)
            wcvCalendar.getCurrentWeekView().setTaskHintList(hintList);
    }

    Map<Integer,Integer> mHintColors;

    /**
     * 设置圆点的颜色集合
     * @param hintColors
     * @param immediately true:刷新
     */
    public void setTaskHintColors(Map<Integer,Integer> hintColors,boolean immediately) {
        mHintColors=hintColors;
        if (mcvCalendar.getCurrentMonthView() != null)
            mcvCalendar.getCurrentMonthView().setTaskHintColors(hintColors,immediately);
        if (wcvCalendar.getCurrentWeekView() != null)
            wcvCalendar.getCurrentWeekView().setTaskHintColors(hintColors,immediately);
    }

    public void removeAllTaskHint() {
        mHintList.clear();
        if (mcvCalendar.getCurrentMonthView() != null)
            mcvCalendar.getCurrentMonthView().setTaskHintList(new HashSet<Integer>());
        if (wcvCalendar.getCurrentWeekView() != null)
            wcvCalendar.getCurrentWeekView().setTaskHintList(new HashSet<Integer>());
    }

    public ScrollConflictResolver getConflictView() {
        return conflictResolver;
    }

    public MonthCalendarView getMonthCalendar() {
        return mcvCalendar;
    }

    public WeekCalendarView getWeekCalendar() {
        return wcvCalendar;
    }

    public int getCurrentSelectYear() {
        return mCurrentSelectYear;
    }

    public int getCurrentSelectMonth() {
        return mCurrentSelectMonth;
    }

    public int getCurrentSelectDay() {
        return mCurrentSelectDay;
    }

}
