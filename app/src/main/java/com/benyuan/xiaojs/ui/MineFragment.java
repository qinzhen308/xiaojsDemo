package com.benyuan.xiaojs.ui;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:hy
 * Date:2016/11/13
 * Desc:
 *
 * ======================================================================================== */

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.course.LessonCreationActivity;
import com.benyuan.xiaojs.ui.course.LiveLessonDetailActivity;
import com.benyuan.xiaojs.ui.course.MyCourseActivity;
import com.benyuan.xiaojs.ui.mine.ProfileActivity;
import com.benyuan.xiaojs.ui.mine.SettingsActivity;
import com.benyuan.xiaojs.ui.mine.TeachingAbilityActivity;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;
import com.benyuan.xiaojs.util.FastBlur;

import butterknife.BindView;
import butterknife.OnClick;

public class MineFragment extends BaseFragment {
    @BindView(R.id.portrait)
    RoundedImageView mPortraitView;
    @BindView(R.id.blur_portrait)
    ImageView mBlurPortraitView;
    @BindView(R.id.profile_cover)
    ImageView mProfileBgView;
    @BindView(R.id.authenticate)
    ImageView mNameAuthView;
    @BindView(R.id.base_info)
    LinearLayout mBaseInfo;
    @BindView(R.id.authentication_info)
    LinearLayout mAuthenticationInfo;

    @Override
    protected View getContentView() {
        return mContext.getLayoutInflater().inflate(R.layout.fragment_mine, null);
    }

    @Override
    protected void init() {
        setPortrait();
        setNameAuth();
    }

    @OnClick({R.id.settings, R.id.edit_profile, R.id.my_course, R.id.my_course_schedule, R.id.my_ask_questions,
            R.id.recharge, R.id.withdrawals, R.id.teach_ability_layout, R.id.my_collections, R.id.eval_management,
            R.id.feedback_help})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile:
                editProfile();
                break;
            case R.id.my_page:
                break;
            case R.id.my_course:
                startActivity(new Intent(mContext, MyCourseActivity.class));
                break;
            case R.id.my_course_schedule:
                startActivity(new Intent(mContext, LessonCreationActivity.class));
                break;
            case R.id.my_ask_questions:
                startActivity(new Intent(mContext, LiveLessonDetailActivity.class));
                break;
            case R.id.recharge:
                break;
            case R.id.withdrawals:
                break;
            case R.id.teach_ability_layout:
                startActivity(new Intent(mContext, TeachingAbilityActivity.class));
                break;
            case R.id.my_collections:
                break;
            case R.id.eval_management:
                break;
            case R.id.feedback_help:
                break;
            case R.id.settings:
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;
            default:
                break;
        }
    }

    private void setPortrait() {
        Bitmap portrait = BitmapFactory.decodeResource(getResources(), R.drawable.default_portrait);
        mPortraitView.setImageBitmap(portrait);
        setupBlurPortraitView(portrait);
    }

    private void setupBlurPortraitView(Bitmap portrait) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(portrait,
                portrait.getWidth() / 3,
                portrait.getHeight() / 3,
                false);
        Bitmap blurBitmap = FastBlur.doBlur(scaledBitmap, 10, true);
        mBlurPortraitView.setImageBitmap(blurBitmap);
    }

    private void setNameAuth() {
        mNameAuthView.setImageResource(R.drawable.ic_name_authed);
    }

    private void editProfile() {
        startActivity(new Intent(mContext, ProfileActivity.class));
    }

}
