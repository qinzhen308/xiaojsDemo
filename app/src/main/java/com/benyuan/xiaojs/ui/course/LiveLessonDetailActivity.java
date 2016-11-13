package com.benyuan.xiaojs.ui.course;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.model.LiveLesson;
import com.benyuan.xiaojs.ui.base.BaseActivity;

import butterknife.BindView;

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
 * Date:2016/11/13
 * Desc:
 *
 * ======================================================================================== */

public class LiveLessonDetailActivity extends BaseActivity {
    //===============base info================
    @BindView(R.id.lesson_name)
    TextView mLessonNameTv;
    @BindView(R.id.lesson_subject)
    TextView mLessonSubjectTv;
    @BindView(R.id.lesson_stu_count)
    TextView mLessonStuCountTv;
    @BindView(R.id.enroll_empty_layout)
    View mEnrollEmptyView;
    @BindView(R.id.enrolled_layout)
    View mEnrolledView;
    @BindView(R.id.enrolled_portrait)
    LinearLayout mEnrolledPortraitLayout;
    @BindView(R.id.teach_form)
    TextView mTeachFormTv;
    @BindView(R.id.lesson_fee)
    TextView mLessonFeeTv;
    @BindView(R.id.lesson_start_time)
    TextView mLessonStartTimeTv;
    @BindView(R.id.lesson_duration)
    TextView mLessonDurationTv;

    //==============optional info==============
    //lesson brief
    @BindView(R.id.lesson_brief_layout)
    LinearLayout mLessonBriefLayout;
    @BindView(R.id.live_lesson_brief)
    TextView mLessonBriefTv;
    @BindView(R.id.unfold_lesson_brief)
    TextView mUnfoldLessonBriefTv;

    //teacher introduction
    @BindView(R.id.teacher_intro_layout)
    LinearLayout mTeacherIntroLayout;
    @BindView(R.id.teacher_introduction)
    TextView mTeacherIntroTv;
    @BindView(R.id.unfold_teacher_intro)
    TextView mUnfoldTeacherIntroTv;

    //sale promotion
    @BindView(R.id.sale_promotion_layout)
    LinearLayout mSalePromotionLayout;
    @BindView(R.id.enroll_before_promotion)
    LinearLayout mEnrollBeforePromotionLayout;
    @BindView(R.id.enroll_before)
    TextView mEnrollBeforeTv;
    @BindView(R.id.enroll_before_discount)
    TextView mEnrollBeforeDisCountTv;
    @BindView(R.id.lesson_before_promotion)
    LinearLayout mLessonBeforePromotionLayout;
    @BindView(R.id.lesson_before)
    TextView mLessonBeforeTv;
    @BindView(R.id.lesson_before_discount)
    TextView mLessonBeforeDisCountTv;

    //audit
    @BindView(R.id.audit_layout)
    LinearLayout mAuditLayout;
    @BindView(R.id.audit_portrait)
    LinearLayout mAuditPortraitLayout;
    @BindView(R.id.visible_to_stu)
    ToggleButton mAuditVisibleBtn;


    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.live_lesson_detail);
        addView(R.layout.activity_live_lesson_detail);
    }

    

    private void setData(LiveLesson liveLesson) {

    }
}
