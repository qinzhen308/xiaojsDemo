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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CenterData;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.certification.CertificationActivity;
import cn.xiaojs.xma.ui.grade.MaterialActivity;
import cn.xiaojs.xma.ui.lesson.EnrollLessonActivity;
import cn.xiaojs.xma.ui.lesson.TeachLessonActivity;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;
import cn.xiaojs.xma.ui.mine.MyOrderActivity;
import cn.xiaojs.xma.ui.mine.ProfileActivity;
import cn.xiaojs.xma.ui.mine.SettingsActivity;
import cn.xiaojs.xma.ui.personal.PersonHomeActivity;
import cn.xiaojs.xma.ui.personal.PersonalBusiness;
import cn.xiaojs.xma.ui.widget.EvaluationStar;
import cn.xiaojs.xma.ui.widget.IconTextView;
import cn.xiaojs.xma.ui.widget.PortraitView;
import cn.xiaojs.xma.util.ExpandGlide;
import cn.xiaojs.xma.util.FastBlur;

public class MineFragment extends BaseFragment {
    private final static int REQUEST_EDIT = 1000;
    private final static int REQUEST_TEACHING_ABILITY = 100;

    @BindView(R.id.portrait)
    PortraitView mPortraitView;
    @BindView(R.id.blur_portrait)
    ImageView mBlurPortraitView;
    @BindView(R.id.user_name)
    IconTextView mUserName;
    @BindView(R.id.evaluation_star)
    EvaluationStar mEvaluation;
    @BindView(R.id.lesson_teaching_duration)
    TextView mLessonTeachingDurationTv;
    @BindView(R.id.my_profile_txt)
    TextView mMyProfileTxtTv;

    //ugc
    @BindView(R.id.fans)
    TextView mFansTv;
    @BindView(R.id.following)
    TextView mFollowingTv;

    @BindView(R.id.my_order_layout)
    RelativeLayout orderLayout;

    @BindView(R.id.name_auth_layout)
    RelativeLayout authLayout;

    private Drawable mBlurFloatUpBg;

    @Override
    protected View getContentView() {
        return mContext.getLayoutInflater().inflate(R.layout.fragment_mine, null);
    }

    @Override
    protected void init() {
        //initProfileBg();
        orderLayout.setEnabled(false);
        authLayout.setEnabled(false);
        mBlurFloatUpBg = new ColorDrawable(getResources().getColor(R.color.blur_float_up_bg));
        mEvaluation.setGrading(EvaluationStar.Grading.THREE_HALF);
        loadData();
    }

    @OnClick({R.id.settings_layout, R.id.my_teaching_layout, R.id.my_enrollment_layout, R.id.my_document_layout,
             R.id.teach_ability_layout, R.id.name_auth_layout, R.id.person_home,R.id.portrait,
            R.id.user_name,R.id.my_order_layout})
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
//            case R.id.my_favorites_layout:
//                break;
            case R.id.teach_ability_layout:
                //startActivity(new Intent(mContext, TeachingAbilityActivity.class));
                Intent intent = new Intent(mContext, TeachingSubjectActivity.class);
                startActivityForResult(intent, REQUEST_TEACHING_ABILITY);
                break;
            case R.id.name_auth_layout:
                startActivity(new Intent(mContext, CertificationActivity.class));
                break;
            case R.id.settings_layout:
                startActivity(new Intent(mContext, SettingsActivity.class));
                break;
            case R.id.person_home:
                startActivity(new Intent(mContext, PersonHomeActivity.class).putExtra(PersonalBusiness.KEY_IS_MYSELF,true));
                break;
            case R.id.portrait:
            case R.id.user_name:
                startActivityForResult(new Intent(mContext, ProfileActivity.class),REQUEST_EDIT);
                break;
            case R.id.my_order_layout:
                startActivity(new Intent(mContext, MyOrderActivity.class));
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

        //set default
        //TODO
        boolean isTeacher = SecurityManager.checkPermission(mContext, Su.Permission.COURSE_OPEN_CREATE);
        String duration;
        if (isTeacher) {
            duration = getString(R.string.tea_lesson_duration, 0);
        } else {
            duration = getString(R.string.stu_lesson_duration, 0);
        }
        mLessonTeachingDurationTv.setText(duration);
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
            mPortraitView.setSex(basic.getSex());
            String title = basic.getTitle();
            if (TextUtils.isEmpty(title)) {
                mMyProfileTxtTv.setText(R.string.default_profile_txt);
            } else {
                mMyProfileTxtTv.setText(basic.getTitle());
            }
        } else {
            mMyProfileTxtTv.setText(R.string.default_profile_txt);
        }
    }

    private void setAvatar(Account.Basic basic) {
        mPortraitView.setBorderColor(getResources().getColor(R.color.round_img_border));
        mPortraitView.setBorderWidth(R.dimen.px2);

        //set avatar
        if (basic != null) {
            String u = cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(AccountDataManager.getAccountID(mContext),300);
            ExpandGlide expandGlide = new ExpandGlide();
            expandGlide.with(mContext)
                    .load(Uri.parse(u))
                    .placeHolder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(mPortraitView, new ExpandGlide.OnBitmapLoaded() {
                        @Override
                        public void onLoaded(Bitmap bmp) {
                            if (bmp != null) {
                                setupBlurPortraitView(bmp);
                            }
                        }
                    });
        } else {
            //set default
            setDefaultPortrait();
        }
    }

    private void initProfileBg() {
        //mBlurPortraitView.setBackgroundColor(getResources().getColor(R.color.main_blue));
        mBlurPortraitView.setImageResource(R.drawable.portrait_default_bg);
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
