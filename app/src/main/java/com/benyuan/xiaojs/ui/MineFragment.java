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
import com.benyuan.xiaojs.data.AccountDataManager;
import com.benyuan.xiaojs.data.api.AccountRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.Account;
import com.benyuan.xiaojs.model.CenterData;
import com.benyuan.xiaojs.model.User;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.course.MyLessonActivity;
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
    private final static int REQUEST_EDIT = 1000;

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
        User u = XiaojsConfig.mLoginUser;
        if (u != null && u.getAccount() != null) {
            setPersonBaseInfo(u.getAccount());
        } else {
            setAvatar(null);
        }
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
                startActivity(new Intent(mContext, MyLessonActivity.class));
                break;
            case R.id.my_course_schedule:
                break;
            case R.id.my_ask_questions:
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

    private void loadData () {
        AccountDataManager.requestCenterData(mContext, new APIServiceCallback<CenterData>() {
            @Override
            public void onSuccess(CenterData centerData) {

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    private void setPersonBaseInfo(Account account) {
        if (account == null) {
            return;
        }

        Account.Basic basic = account.getBasic();
        //set avatar
        setAvatar(basic);

        //set name
        //TODO base对象的name和login返回的name不同时存在
        User u = XiaojsConfig.mLoginUser;
        if (basic != null && !TextUtils.isEmpty(basic.getName())) {
            u.setName(basic.getName());
            mUserName.setText(basic.getName());
        } else {
            if (u != null && !TextUtils.isEmpty(u.getName())) {
                mUserName.setText(u.getName());
            }
        }

        //set title
        if (basic != null) {
            String title = basic.getTitle();
            if (TextUtils.isEmpty(title)) {
                mUserTitle.setText(R.string.my_profile_txt);
            } else {
                mUserTitle.setText(basic.getTitle());
            }
        } else {
            mUserTitle.setText(R.string.my_profile_txt);
        }

        mNameAuthView.setImageResource(R.drawable.ic_name_authed);
    }

    private void setAvatar(Account.Basic basic) {
        mPortraitView.setBorderColor(getResources().getColor(R.color.round_img_border));
        mPortraitView.setBorderWidth(R.dimen.px5);
        //set avatar
        if (basic != null) {
            Glide.with(mContext).load(basic.getAvatar())
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
                            mProfileBgView.setBackgroundDrawable(null);
                            mBlurPortraitView.setBackgroundColor(getResources().getColor(R.color.main_blue));
                        }
                    });
        } else {
            //set default
            mPortraitView.setImageResource(R.drawable.default_avatar);
            mProfileBgView.setBackgroundDrawable(null);
            mBlurPortraitView.setBackgroundColor(getResources().getColor(R.color.main_blue));
        }

        //test
        /*Glide.with(mContext).load("http://cdn.duitang.com/uploads/item/201405/27/20140527165332_JJnWu.thumb.224_0.jpeg")
                .error(R.drawable.default_avatar)
                .into(new GlideDrawableImageViewTarget(mPortraitView) {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                        super.onResourceReady(resource, animation);
                        if (resource instanceof GlideBitmapDrawable) {
                            Bitmap bmp = ((GlideBitmapDrawable) resource).getBitmap();
                            setupBlurPortraitView(bmp);
                        }
                    }

                    @Override
                    public void onLoadFailed(Exception e, Drawable errorDrawable) {
                        super.onLoadFailed(e, errorDrawable);
                        mBlurPortraitView.setBackgroundColor(getResources().getColor(R.color.main_blue));
                    }
                });*/
    }

    private void setupBlurPortraitView(Bitmap portrait) {
        Bitmap blurBitmap = FastBlur.smartBlur(portrait, 2, true);
        mBlurPortraitView.setImageBitmap(blurBitmap);
    }

    private void editProfile() {
        startActivityForResult(new Intent(mContext, ProfileActivity.class), REQUEST_EDIT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_EDIT:
                if (data != null) {
                    Object obj = data.getSerializableExtra(ProfileActivity.KEY_ACCOUNT_BEAN);
                    if (obj instanceof Account) {
                        setPersonBaseInfo((Account) obj);
                    }
                }
                break;
        }
    }
}
