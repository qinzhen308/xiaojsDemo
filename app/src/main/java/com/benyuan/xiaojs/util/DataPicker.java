package com.benyuan.xiaojs.util;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/4
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.BottomSheet;
import com.benyuan.xiaojs.ui.widget.FutureTimePicker;
import com.wheelpicker.DateWheelPicker;
import com.wheelpicker.core.AbstractWheelPicker;
import com.wheelpicker.core.OnWheelPickedListener;
import com.wheelpicker.widget.TextWheelPicker;
import com.wheelpicker.widget.TextWheelPickerAdapter;

import java.util.Date;
import java.util.List;

public class DataPicker {
    private static int mYear;
    private static int mMonth;
    private static int mDay;

    private static int mHour;
    private static int mMinute;
    private static int mSecond;

    private static Object mPickedData;

    public static void pickBirthday(Context context, final OnBirthdayPickListener pickListener) {
        BottomSheet bottomSheet = new BottomSheet(context);
        final DateWheelPicker picker = new DateWheelPicker(context);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(new Date());

        int currYear = calendar.get(java.util.Calendar.YEAR);
        int currMonth = calendar.get(java.util.Calendar.MONTH) + 1;
        int currDay = calendar.get(java.util.Calendar.DATE);

        picker.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        picker.setTextColor(context.getResources().getColor(R.color.font_black));
        picker.setVisibleItemCount(7);
        picker.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.font_32px));
        picker.setItemSpace(context.getResources().getDimensionPixelOffset(R.dimen.px25));

        picker.setDateRange(currYear - 100, currYear);
        picker.setCurrentDate(currYear, currMonth, currDay);
        picker.setOnDatePickListener(new DateWheelPicker.OnDatePickListener() {
            @Override
            public void onDatePicked(int year, int month, int day) {
                mYear = year;
                mMonth = month;
                mDay = day;
            }
        });

        bottomSheet.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickListener != null) {
                    pickListener.onBirthPicked(mYear, mMonth, mDay);
                }
            }
        });

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.px20);
        picker.setPadding(0, padding, 0, padding);

        bottomSheet.setContent(picker);
        bottomSheet.show();
    }

    public static void pickData(Context context, List<String> data, final OnDataPickListener pickedListener) {
        BottomSheet bottomSheet = new BottomSheet(context);

        TextWheelPicker picker = new TextWheelPicker(context);
        TextWheelPickerAdapter adapter = new TextWheelPickerAdapter(data);
        picker.setAdapter(adapter);

        picker.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        picker.setTextColor(context.getResources().getColor(R.color.font_black));
        picker.setVisibleItemCount(7);
        picker.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.font_32px));
        picker.setItemSpace(context.getResources().getDimensionPixelOffset(R.dimen.px25));

        picker.setCurrentItem(0);

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.px20);
        picker.setPadding(0, padding, 0, padding);

        bottomSheet.setContent(picker);
        bottomSheet.show();
        picker.setOnWheelPickedListener(new OnWheelPickedListener() {
            @Override
            public void onWheelSelected(AbstractWheelPicker wheelPicker, int index, Object data) {
                mPickedData = data;
            }
        });

        bottomSheet.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickedListener != null) {
                    pickedListener.onDataPicked(mPickedData);
                }
            }
        });

    }

    public static void pickFutureDate(Context context, final OnDatePickListener pickListener) {
        BottomSheet bottomSheet = new BottomSheet(context);
        FutureTimePicker picker = new FutureTimePicker(context);

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(new Date());

        picker.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        picker.setTextColor(context.getResources().getColor(R.color.font_black));
        picker.setVisibleItemCount(7);
        picker.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.font_32px));
        picker.setItemSpace(context.getResources().getDimensionPixelOffset(R.dimen.px25));
        picker.setFutureDuration(365);

        picker.setOnFutureDatePickListener(new FutureTimePicker.OnFutureDatePickListener() {
            @Override
            public void onDatePicked(int year, int month, int day, int hour, int minute, int second) {
                mYear = year;
                mMonth = month;
                mDay = day;
                mHour = hour;
                mMinute = minute;
                mSecond = second;
            }
        });

        bottomSheet.setRightBtnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pickListener != null) {
                    pickListener.onDatePicked(mYear, mMonth, mDay, mHour, mMinute, mSecond);
                }
            }
        });

        int padding = context.getResources().getDimensionPixelOffset(R.dimen.px20);
        picker.setPadding(0, padding, 0, padding);

        bottomSheet.setContent(picker);
        bottomSheet.show();
    }

    public interface OnBirthdayPickListener {
        public void onBirthPicked(int year, int month, int day);
    }

    public interface OnDatePickListener {
        public void onDatePicked(int year, int month, int day, int hour, int minute, int second);
    }

    public interface OnDataPickListener {
        public void onDataPicked(Object data);
    }

}

