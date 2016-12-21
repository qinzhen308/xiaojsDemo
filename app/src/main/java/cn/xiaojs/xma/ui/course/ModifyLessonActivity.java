package cn.xiaojs.xma.ui.course;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.LessonDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.Schedule;
import cn.xiaojs.xma.model.TeachLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.LimitInputBox;
import cn.xiaojs.xma.util.DataPicker;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.ToastUtil;

import java.util.Calendar;
import java.util.Date;

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
    @BindView(R.id.modify_lesson_reason)
    LimitInputBox mInput;

    private Date mLessonsDate;

    private TeachLesson bean;
    private long newDate;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_modify_lesson);
        setMiddleTitle(R.string.modify_lesson);
        Intent intent = getIntent();
        if (intent != null) {
            bean = (TeachLesson) intent.getSerializableExtra(CourseConstant.KEY_LESSON_BEAN);
            if (bean != null){
                mOriginTime.setText(TimeUtil.format(bean.getSchedule().getStart(), TimeUtil.TIME_YYYY_MM_DD_HH_MM));
                mLessonsDate = new Date(bean.getSchedule().getStart().getTime());
                mName.setText(bean.getTitle());
                mDuration.setText(getString(R.string.lesson_duration_tip, bean.getSchedule().getDuration()));
            }
        }
        mInput.setHint(getString(R.string.delay_reason_hint));
    }

    @OnClick({R.id.left_image, R.id.limit_select_time,R.id.modify_lesson_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.limit_select_time:
                DataPicker.pickFutureDate(this, mLessonsDate, new DataPicker.OnDatePickListener() {
                    @Override
                    public void onDatePicked(int year, int month, int day, int hour, int minute, int second) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, day, hour, minute, second);
                        newDate = calendar.getTimeInMillis();
                        mLessonsDate.setTime(newDate);
                        String dateStr = TimeUtil.formatDate(newDate, TimeUtil.TIME_YYYY_MM_DD_HH_MM);
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
        if (newDate <= 0){
            ToastUtil.showToast(this,"请输入正确的时间！");
            return;
        }
        if (bean != null){
            String input = mInput.getInput().getText().toString();
            if (TextUtils.isEmpty(input)){
                ToastUtil.showToast(this,R.string.delay_reason_hint);
                return;
            }
            LiveLesson liveLesson = new LiveLesson();
            Schedule schedule = new Schedule();
            schedule.setStart(new Date(newDate));
            liveLesson.setSchedule(schedule);
            LessonDataManager.requestEditLesson(this, bean.getId(), liveLesson, new APIServiceCallback() {
                @Override
                public void onSuccess(Object object) {
                    cancelProgress();
                    ToastUtil.showToast(ModifyLessonActivity.this,"修改上课时间成功！");
                    setResult(RESULT_OK);
                    finish();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    ToastUtil.showToast(ModifyLessonActivity.this,errorMessage);
                }
            });

            showProgress(false);
        }
    }
}
