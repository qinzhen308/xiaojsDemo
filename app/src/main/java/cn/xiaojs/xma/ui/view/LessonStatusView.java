package cn.xiaojs.xma.ui.view;
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
 * Date:2017/1/17
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.model.EnrolledLesson;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.StringUtil;
import cn.xiaojs.xma.util.TimeUtil;

public class LessonStatusView extends RelativeLayout {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.evaluate)
    TextView evaluate;
    @BindView(R.id.review)
    TextView review;

    @BindView(R.id.lesson_end)
    ImageView end;

    @BindView(R.id.mark)
    View mark;

    public LessonStatusView(Context context) {
        super(context);
        init();
    }

    public LessonStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LessonStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public LessonStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_lesson_status, this);
        ButterKnife.bind(this);
        reset();
    }

    public void show(EnrolledLesson bean) {
        if (bean == null)
            return;
        reset();
        if (bean.getState().equalsIgnoreCase(LessonState.CANCELLED)) {
            time.setText(TimeUtil.format(bean.getSchedule().getStart(), TimeUtil.TIME_YYYY_MM_DD_HH_MM) + " " + bean.getSchedule().getDuration() + "分钟");
            title.setText("因老师个人原因，该班已取消");
            this.status.setVisibility(VISIBLE);
            this.status.setText(R.string.course_state_cancel);
            this.status.setBackgroundResource(R.drawable.course_state_cancel_bg);
            mark.setBackgroundResource(R.color.hor_divide_line);
        } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
            time.setText(TimeUtil.format(bean.getSchedule().getStart(), TimeUtil.TIME_YYYY_MM_DD_HH_MM) + " " + bean.getSchedule().getDuration() + "分钟");
            title.setText("累计学时2小时，获得了50积分");
            end.setVisibility(VISIBLE);
            evaluate.setVisibility(VISIBLE);
            review.setVisibility(VISIBLE);
            mark.setBackgroundResource(R.color.hor_divide_line);
        } else if (bean.getState().equalsIgnoreCase(LessonState.STOPPED)) {
            this.status.setVisibility(VISIBLE);
            this.status.setText(R.string.force_stop);
            this.status.setBackgroundResource(R.drawable.course_state_stop_bg);
        } else if (bean.getState().equalsIgnoreCase(LessonState.PENDING_FOR_LIVE)) {

            Date date =bean.getSchedule().getStart();
            time.setText(TimeUtil.format(date, TimeUtil.TIME_YYYY_MM_DD_HH_MM) + " " + bean.getSchedule().getDuration() + "分钟");
            title.setText(TimeUtil.distanceDay(date, false));
        }
    }

    private void reset() {
        status.setVisibility(GONE);
        end.setVisibility(GONE);
        distance.setVisibility(GONE);
        evaluate.setVisibility(GONE);
        review.setVisibility(GONE);
        mark.setBackgroundResource(R.color.font_orange);
    }
}
