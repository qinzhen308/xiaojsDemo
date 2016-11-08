package com.benyuan.xiaojs.ui.course;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

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
 * Date:2016/11/7
 * Desc:
 *
 * ======================================================================================== */

public class CourseCreationOptionalInfoActivity extends BaseActivity implements CourseConstant {
    private final static int COURSE_BRIEF = 0;
    private final static int TEACHER_INTRODUCTION = 1;
    private final static int SIT_IN_ON = 2;
    private final static int SALE_PROMOTION = 3;


    @BindView(R.id.cover_add_layout)
    LinearLayout mCoverAddLayout;
    @BindView(R.id.cover_view)
    ImageView mCoverImgView;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.optional_info);

        addView(R.layout.activity_course_create_optional_info);

        initCoverLayout();
    }

    private void initCoverLayout() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mCoverAddLayout.getLayoutParams();
        int w = getResources().getDisplayMetrics().widthPixels;
        int h = (int) ((COURSE_COVER_HEIGHT / (float) COURSE_COVER_WIDTH) * w);
        params.height = h;
        params.width = w;
    }

    @OnClick({R.id.live_course_brief, R.id.teacher_introduction, R.id.sit_in_on, R.id.sale_promotion})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.live_course_brief:
                startActivityForResult(new Intent(this, LiveCourseBriefActivity.class), COURSE_BRIEF);
                break;
            case R.id.teacher_introduction:
                startActivityForResult(new Intent(this, TeacherIntroductionActivity.class), TEACHER_INTRODUCTION);
                break;
            case R.id.sit_in_on:
                startActivityForResult(new Intent(this, SitInOnActivity.class), SIT_IN_ON);
                break;
            case R.id.sale_promotion:
                //startActivityForResult(new Intent(this, SalePromotionActivity.class), SALE_PROMOTION);
                break;
            default:
                break;
        }
    }


}
