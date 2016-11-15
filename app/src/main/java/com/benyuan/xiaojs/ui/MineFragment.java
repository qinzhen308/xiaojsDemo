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
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.model.Account;
import com.benyuan.xiaojs.model.User;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.course.LessonCreationActivity;
import com.benyuan.xiaojs.ui.course.LiveLessonDetailActivity;
import com.benyuan.xiaojs.ui.course.MyCourseActivity;
import com.benyuan.xiaojs.ui.mine.ProfileActivity;
import com.benyuan.xiaojs.ui.mine.SettingsActivity;
import com.benyuan.xiaojs.ui.mine.TeachingAbilityActivity;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;
import com.benyuan.xiaojs.util.FastBlur;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

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
    @BindView(R.id.user_name)
    TextView mUserName;
    @BindView(R.id.user_title)
    TextView mUserTitle;

    @Override
    protected View getContentView() {
        return mContext.getLayoutInflater().inflate(R.layout.fragment_mine, null);
    }

    @Override
    protected void init() {
        setPersonBaseInfo();

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

    private void setPersonBaseInfo() {
        User u = XiaojsConfig.mLoginUser;
        Account ac = u.getAccount();
        //set avatar
        setAvatar();

        mUserName.setText(u.getName());

        //set title
        if (ac != null && ac.getBasic() != null) {
            String title = ac.getBasic().getTitle();
            if (TextUtils.isEmpty(title)) {
                mUserTitle.setText(R.string.my_profile_txt);
            } else {
                mUserTitle.setText(ac.getBasic().getTitle());
            }
        } else {
            mUserTitle.setText(R.string.my_profile_txt);
        }

        mNameAuthView.setImageResource(R.drawable.ic_name_authed);
    }

    private void setAvatar() {
        mPortraitView.setBorderColor(getResources().getColor(R.color.round_img_border));
        mPortraitView.setBorderWidth(R.dimen.px5);
        Account ac = XiaojsConfig.mLoginUser.getAccount();
        /*if (ac != null) {
            //set avatar
            if (ac.getBasic() != null) {
                Glide.with(mContext).load(ac.getBasic().getAvatar())
                        .error(R.drawable.default_avatar)
                        .into(new GlideDrawableImageViewTarget(mPortraitView) {
                            @Override
                            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                                if (resource instanceof GlideBitmapDrawable) {
                                    Bitmap bmp = ((GlideBitmapDrawable)resource).getBitmap();
                                    setupBlurPortraitView(bmp);
                                }
                            }
                        });
            }
        } else {
            //set default
            mBlurPortraitView.setBackgroundColor(getResources().getColor(R.color.main_blue));
        }*/

        //test
        Glide.with(mContext).load("http://cdn.duitang.com/uploads/item/201405/27/20140527165332_JJnWu.thumb.224_0.jpeg")
                .error(R.drawable.default_avatar)
                .into(new GlideDrawableImageViewTarget(mPortraitView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        if (resource instanceof GlideBitmapDrawable) {
                            Bitmap bmp = ((GlideBitmapDrawable)resource).getBitmap();
                            setupBlurPortraitView(bmp);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        mBlurPortraitView.setBackgroundColor(getResources().getColor(R.color.main_blue));
                    }
                });
    }

    private void setupBlurPortraitView(Bitmap portrait) {
        Bitmap blurBitmap = FastBlur.smartBlur(portrait, 2, true);
        mBlurPortraitView.setImageBitmap(blurBitmap);
    }

    private void editProfile() {
        startActivity(new Intent(mContext, ProfileActivity.class));
    }

}
