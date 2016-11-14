package com.benyuan.xiaojs.ui.course;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/11/14
 * Desc:
 *
 * ======================================================================================== */

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.util.DataPicker;
import com.benyuan.xiaojs.util.TimeUtil;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.OnClick;

public class ModifyLessonActivity extends BaseActivity {

    @BindView(R.id.modify_lesson_origin_time)
    TextView mOriginTime;
    @BindView(R.id.modify_lesson_origin_name)
    TextView mName;
    @BindView(R.id.modify_lesson_origin_duration)
    TextView mDuration;
    @BindView(R.id.modify_lesson_new_time)
    TextView mNewTime;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_modify_lesson);
        setMiddleTitle(R.string.modify_lesson);
        Intent intent = getIntent();
        if (intent != null) {
            String name = intent.getStringExtra(CourseConstant.KEY_LESSON_NAME);
            long time = intent.getLongExtra(CourseConstant.KEY_LESSON_TIME, 0);
            int duration = intent.getIntExtra(CourseConstant.KEY_LESSON_DURATION, 0);
            mOriginTime.setText(TimeUtil.format(new Date(time), TimeUtil.TIME_YYYY_MM_DD_HH_MM));
            mName.setText(name);
            mDuration.setText(getString(R.string.lesson_duration_tip, duration));
        }
    }

    @OnClick({R.id.left_image, R.id.limit_select_time})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.limit_select_time:
                DataPicker.pickFutureDate(this, new DataPicker.OnDatePickListener() {
                    @Override
                    public void onDatePicked(int year, int month, int day, int hour, int minute, int second) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, hour, minute, second);
                        long time = calendar.getTimeInMillis();
                        String dateStr = TimeUtil.formatDate(time, TimeUtil.TIME_YYYY_MM_DD_HH_MM);
                        mNewTime.setText(dateStr);
                    }
                });
                break;
            default:
                break;
        }
    }
}
