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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.LessonState;
import cn.xiaojs.xma.model.EnrolledLesson;

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
            this.status.setVisibility(VISIBLE);
            this.status.setText(R.string.course_state_cancel);
            this.status.setBackgroundResource(R.drawable.course_state_cancel_bg);
        } else if (bean.getState().equalsIgnoreCase(LessonState.FINISHED)) {
            end.setVisibility(VISIBLE);
        } else if (bean.getState().equalsIgnoreCase(LessonState.STOPPED)) {
            this.status.setVisibility(VISIBLE);
            this.status.setText(R.string.pending_for_course);
            this.status.setBackgroundResource(R.drawable.course_state_wait_bg);
        }else{

        }
    }

    private void reset(){
        status.setVisibility(GONE);
        end.setVisibility(GONE);
        distance.setVisibility(GONE);
        evaluate.setVisibility(GONE);
        review.setVisibility(GONE);
    }
}
