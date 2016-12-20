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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.data.AccountDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.Account;
import com.benyuan.xiaojs.model.CenterData;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.classroom.ClassroomActivity;
import com.benyuan.xiaojs.ui.course.MyLessonActivity;
import com.benyuan.xiaojs.ui.mine.MyEnrollActivity;
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

    @BindView(R.id.personal_info)
    View mPersonalInfo;
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

    //ugc
    @BindView(R.id.fans)
    TextView mFansTv;
    @BindView(R.id.following)
    TextView mFollowingTv;

    private Drawable mBlurFloatUpBg;

    @Override
    protected View getContentView() {
        return mContext.getLayoutInflater().inflate(R.layout.fragment_mine, null);
    }

    @Override
    protected void init() {
        //initProfileBg();

        mBlurFloatUpBg = new ColorDrawable(getResources().getColor(R.color.blur_float_up_bg));
        loadData();
    }

    @OnClick({R.id.settings, R.id.edit_profile, R.id.my_page, R.id.my_course, R.id.my_course_schedule, R.id.my_enrollment,
            R.id.my_ask_questions, R.id.recharge, R.id.withdrawals, R.id.teach_ability_layout, R.id.my_collections,
            R.id.eval_management, R.id.feedback_help})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_profile:
                editProfile();
                break;
            case R.id.my_page:
                startActivity(new Intent(mContext, ClassroomActivity.class));
                break;
            case R.id.my_course:
                startActivity(new Intent(mContext, MyLessonActivity.class));
                break;
            case R.id.my_course_schedule:
                break;
            case R.id.my_enrollment:
                startActivity(new Intent(mContext, MyEnrollActivity.class));
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

    private void loadData() {
        AccountDataManager.requestCenterData(mContext, new APIServiceCallback<CenterData>() {
            @Override
            public void onSuccess(CenterData centerData) {
                setPersonBaseInfo(centerData != null ? centerData.getBasic() : null);
                setUgc(centerData);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                setDefaultPortrait();
            }
        });
    }

    private void setUgc(CenterData centerData) {
        if (centerData != null && centerData.getUgc() != null) {
            CenterData.PersonUgc personUgc = centerData.getUgc();
            mFansTv.setText(String.valueOf(personUgc.getLikedCount()));
            mFollowingTv.setText(String.valueOf(personUgc.getFollowedCount()));
        }
    }

    private void setPersonBaseInfo(Account.Basic basic) {
        //set avatar
        setAvatar(basic);

        //set name
        if (basic != null && !TextUtils.isEmpty(basic.getName())) {
            mUserName.setText(basic.getName());
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
                                Bitmap bmp = ((GlideBitmapDrawable) resource).getBitmap();
                                setupBlurPortraitView(bmp);
                            }
                        }

                        @Override
                        public void onLoadFailed(Exception e, Drawable errorDrawable) {
                            super.onLoadFailed(e, errorDrawable);
                            setDefaultPortrait();
                        }
                    });
        } else {
            //set default
            setDefaultPortrait();
        }
    }

    private void initProfileBg() {
        mProfileBgView.setBackgroundDrawable(null);
        mBlurPortraitView.setBackgroundColor(getResources().getColor(R.color.main_blue));
    }

    private void setDefaultPortrait() {
        mPortraitView.setImageResource(R.drawable.default_avatar);
        initProfileBg();
    }

    private void setupBlurPortraitView(Bitmap portrait) {
        mProfileBgView.setBackgroundDrawable(mBlurFloatUpBg);
        Bitmap blurBitmap = FastBlur.smartBlur(portrait, 2, true);
        int h = mPersonalInfo.getHeight();
        ViewGroup.LayoutParams params = mBlurPortraitView.getLayoutParams();
        if (params != null) {
            params.height = h;
        }
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
                    Object obj = data.getSerializableExtra(ProfileActivity.KEY_BASE_BEAN);
                    if (obj instanceof Account.Basic) {
                        setPersonBaseInfo((Account.Basic) obj);
                    }
                }
                break;
        }
    }
}
