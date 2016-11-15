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
import com.benyuan.xiaojs.model.TeachLesson;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.util.DataPicker;
import com.benyuan.xiaojs.util.TimeUtil;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 修改上课时间
 */
public class ModifyLessonActivity extends BaseActivity {

    @BindView(R.id.modify_lesson_origin_time)
    TextView mOriginTime;
    @BindView(R.id.modify_lesson_origin_name)
    TextView mName;
    @BindView(R.id.modify_lesson_origin_duration)
    TextView mDuration;
    @BindView(R.id.modify_lesson_new_time)
    TextView mNewTime;

    private TeachLesson bean;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_modify_lesson);
        setMiddleTitle(R.string.modify_lesson);
        Intent intent = getIntent();
        if (intent != null) {
            bean = (TeachLesson) intent.getSerializableExtra(CourseConstant.KEY_LESSON_BEAN);
            if (bean != null){
                mOriginTime.setText(TimeUtil.format(bean.getSchedule().getStart(), TimeUtil.TIME_YYYY_MM_DD_HH_MM));
                mName.setText(bean.getTitle());
                mDuration.setText(getString(R.string.lesson_duration_tip, bean.getSchedule().getDuration()));
            }
        }
    }

    @OnClick({R.id.left_image, R.id.limit_select_time,R.id.modify_lesson_ok})
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
            case R.id.modify_lesson_ok:
                modify();
                break;
            default:
                break;
        }
    }

    private void modify(){
        if (bean != null){

        }
    }
}
