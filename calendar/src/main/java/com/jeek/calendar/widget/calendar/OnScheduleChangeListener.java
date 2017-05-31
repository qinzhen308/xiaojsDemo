package com.jeek.calendar.widget.calendar;

/**
 * Created by Paul Z on 2017/5/31.
 */

public interface OnScheduleChangeListener {
    void onClickDate(int year, int month, int day);
    void onWeekChange(int year, int month, int day);
    void onMonthChange(int year, int month, int day);
}
