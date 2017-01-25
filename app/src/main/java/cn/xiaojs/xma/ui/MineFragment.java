package cn.xiaojs.xma.ui;

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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CenterData;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.certification.CertificationActivity;
import cn.xiaojs.xma.ui.grade.MaterialActivity;
import cn.xiaojs.xma.ui.lesson.EnrollLessonActivity;
import cn.xiaojs.xma.ui.lesson.TeachLessonActivity;
import cn.xiaojs.xma.ui.mine.ProfileActivity;
import cn.xiaojs.xma.ui.mine.SettingsActivity;
import cn.xiaojs.xma.ui.mine.TeachingAbilityActivity;
import cn.xiaojs.xma.ui.personal.PersonHomeActivity;
import cn.xiaojs.xma.ui.widget.EvaluationStar;
import cn.xiaojs.xma.ui.widget.IconTextView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.FastBlur;

public class MineFragment extends BaseFragment {
    private final static int REQUEST_EDIT = 1000;

    @BindView(R.id.portrait)
    RoundedImageView mPortraitView;
    @BindView(R.id.blur_portrait)
    ImageView mBlurPortraitView;
    @BindView(R.id.user_name)
    IconTextView mUserName;
    @BindView(R.id.evaluation_star)
    EvaluationStar mEvaluation;

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
        mEvaluation.setGrading(EvaluationStar.Grading.THREE_HALF);
        loadData();
    }

    @OnClick({R.id.settings_layout, R.id.my_teaching_layout, R.id.my_enrollment_layout, R.id.my_document_layout,
            R.id.my_favorites_layout, R.id.teach_ability_layout, R.id.name_auth_layout, R.id.person_home})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_teaching_layout:
                startActivity(new Intent(mContext, TeachLessonActivity.class));
                break;
            case R.id.my_enrollment_layout:
                startActivity(new Intent(mContext, EnrollLessonActivity.class));
                break;
            case R.id.my_document_layout:
                startActivity(new Intent(mContext, MaterialActivity.class).putExtra(MaterialActivity.KEY_IS_MINE,true));
                break;
            case R.id.my_favorites_layout:
                break;
            case R.id.teach_ability_layout:
                startActivity(new Intent(mContext, TeachingAbilityActivity.class));
                break;
            case R.id.name_auth_layout:
                startActivity(new Intent(mContext, CertificationActivity.class));
                break;
            case R.id.settings_layout:
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;
            case R.id.person_home:
                startActivity(new Intent(mContext, PersonHomeActivity.class));
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
            //mFansTv.setText(String.valueOf(personUgc.getLikedCount()));
            //mFollowingTv.setText(String.valueOf(personUgc.getFollowedCount()));
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
                //mUserTitle.setText(R.string.my_profile_txt);
            } else {
                //mUserTitle.setText(basic.getTitle());
            }
        } else {
            //mUserTitle.setText(R.string.my_profile_txt);
        }
    }

    private void setAvatar(Account.Basic basic) {
        mPortraitView.setBorderColor(getResources().getColor(R.color.round_img_border));
        mPortraitView.setBorderWidth(R.dimen.px2);

        //TODO test
        if (true) {
            mPortraitView.setImageResource(R.drawable.mine_portrait);
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.mine_portrait);
            setupBlurPortraitView(bmp);
            return;
        }

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
        mBlurPortraitView.setBackgroundColor(getResources().getColor(R.color.main_blue));
    }

    private void setDefaultPortrait() {
        mPortraitView.setImageResource(R.drawable.default_avatar);
        initProfileBg();
    }

    private void setupBlurPortraitView(Bitmap portrait) {
        Bitmap blurBitmap = FastBlur.smartBlur(portrait, 4, true);
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
