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
 * Author:huangyong
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
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.certification.CertificationActivity;
import cn.xiaojs.xma.ui.grade.MaterialActivity;
import cn.xiaojs.xma.ui.lesson.EnrollLessonActivity;
import cn.xiaojs.xma.ui.lesson.SubjectSelectorActivity;
import cn.xiaojs.xma.ui.lesson.TeachLessonActivity;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassesListActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ShareQrcodeActivity;
import cn.xiaojs.xma.ui.message.ContactActivity;
import cn.xiaojs.xma.ui.message.MessageCenterActivity;
import cn.xiaojs.xma.ui.mine.MyOrderActivity;
import cn.xiaojs.xma.ui.mine.ProfileActivity;
import cn.xiaojs.xma.ui.mine.SettingsActivity;
import cn.xiaojs.xma.ui.personal.PersonHomeActivity;
import cn.xiaojs.xma.ui.personal.PersonalBusiness;
import cn.xiaojs.xma.ui.widget.EvaluationStar;
import cn.xiaojs.xma.ui.widget.IconTextView;
import cn.xiaojs.xma.ui.widget.PortraitView;
import cn.xiaojs.xma.ui.widget.RedTipTextView;
import cn.xiaojs.xma.util.ExpandGlide;
import cn.xiaojs.xma.util.FastBlur;

public class MineFragment extends BaseFragment {
    private final static int REQUEST_EDIT = 1000;
    private final static int REQUEST_TEACHING_ABILITY = 100;

    @BindView(R.id.portrait)
    PortraitView mPortraitView;
    @BindView(R.id.blur_portrait)
    ImageView mBlurPortraitView;
    @BindView(R.id.profile_cover)
    View mProfileCover;
    @BindView(R.id.user_name)
    IconTextView mUserName;
    @BindView(R.id.evaluation_star)
    EvaluationStar mEvaluation;
    @BindView(R.id.second_divider)
    View mUcgSecondDivider;
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

    @BindView(R.id.teach_ability)
    TextView abilityTips;
    @BindView(R.id.name_auth)
    TextView cerTips;

    @BindView(R.id.message_entrance)
    RedTipTextView messageEntrace;


    int[] icons=new int[2];

    private Drawable mBlurFloatUpBg;

    @Override
    protected View getContentView() {
        return mContext.getLayoutInflater().inflate(R.layout.fragment_mine, null);
    }

    @Override
    protected void init() {
        //initProfileBg();
        orderLayout.setEnabled(false);;
        mBlurFloatUpBg = new ColorDrawable(getResources().getColor(R.color.blur_float_up_bg));
        mEvaluation.setGrading(EvaluationStar.Grading.THREE_HALF);
        initBaseInfo();
        //set default ugc
        setUgc(null);
        loadData();
        showAbilities();
        showCer();
    }

    @Override
    public void onResume() {
        super.onResume();

        showAbilities();
        showCer();
        showTips();
    }

    private void showTips() {
        showMessageTips(DataManager.hasMessage(getActivity()));
    }

    public void showMessageTips(boolean isShow) {
        if(isShow!=messageEntrace.mTipEnable){
            messageEntrace.setTipEnable(isShow);
        }

        if (isShow){
            //显示Tab红点
            ((MainActivity)getActivity()).showMessageTips();
        }else{
            //消除Tab红点
            ((MainActivity)getActivity()).hiddenMessageTips();
        }



    }

    private void enterMessage(){
        showMessageTips(false);
        DataManager.setHasMessage(getActivity(),false);
        startActivity(new Intent(getActivity(), MessageCenterActivity.class));
    }


    @OnClick({R.id.settings_layout, R.id.my_teaching_layout, R.id.my_document_layout,R.id.my_contact_layout,
             R.id.teach_ability_layout, R.id.name_auth_layout, R.id.person_home,R.id.portrait,
            R.id.user_name,R.id.my_order_layout,R.id.message_entrance, R.id.qrcode_layout})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_teaching_layout:
                startActivity(new Intent(mContext, ClassesListActivity.class));
                //startActivity(new Intent(mContext, TeachLessonActivity.class));
                break;
//            case R.id.my_enrollment_layout:
//                startActivity(new Intent(mContext, EnrollLessonActivity.class));
//                break;
            case R.id.my_document_layout:
                startActivity(new Intent(mContext, MaterialActivity.class).putExtra(MaterialActivity.KEY_IS_MINE,true));
                break;
//            case R.id.my_favorites_layout:
//                break;
            case R.id.my_contact_layout:
                startActivity(new Intent(mContext, ContactActivity.class));
                break;
            case R.id.teach_ability_layout:

                Intent intent = new Intent();
                intent.setClass(mContext, SubjectSelectorActivity.class);
                intent.putExtra(SubjectSelectorActivity.EXTRA_NORMAL, 1);

                //Intent intent = new Intent(mContext, TeachingSubjectActivity.class);
                startActivityForResult(intent, REQUEST_TEACHING_ABILITY);
                break;
            case R.id.name_auth_layout:
                if (AccountDataManager.isTeacher(mContext)) {
                    startActivity(new Intent(mContext, CertificationActivity.class));
                }else{
                    Toast.makeText(mContext,"你不是老师，不能进行实名认证",Toast.LENGTH_SHORT).show();
                    //提示申明教学能力
//                    final CommonDialog dialog = new CommonDialog(mContext);
//                    dialog.setTitle(R.string.declare_teaching_ability);
//                    dialog.setDesc(R.string.declare_teaching_ability_tip);
//                    dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
//                        @Override
//                        public void onClick() {
//                            dialog.dismiss();
//                            Intent intent = new Intent(mContext, TeachingSubjectActivity.class);
//                            startActivity(intent);
//                        }
//                    });
//                    dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
//                        @Override
//                        public void onClick() {
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show();
                }

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
            case R.id.message_entrance:
                enterMessage();
                break;
            case R.id.qrcode_layout:
                Intent qrIntent = new Intent(mContext, ShareQrcodeActivity.class);
                qrIntent.putExtra(ShareQrcodeActivity.EXTRA_QRCODE_TYPE,
                        ShareQrcodeActivity.CLIENT_DOWNLOAD_QRCODE);
                startActivity(qrIntent);
                break;
            default:
                break;
        }
    }

    private void loadData() {
        AccountDataManager.requestProfile(mContext, new APIServiceCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                setPersonBaseInfo(account != null ? account.getBasic() : null);
                setUgc(account);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                setDefaultPortrait();
            }
        });
    }

    private void initBaseInfo() {
        Account account = AccountDataManager.getAccont(mContext);
        if (account != null && account.getBasic() != null) {
            setPersonBaseInfo(account.getBasic());
        }

    }

    private void setUgc(Account account) {
        if (account != null && account.stats != null) {

            int fancount = account.stats.fans > 0? account.stats.fans: 0;
            int followcount = account.stats.followships > 0? account.stats.followships: 0;

            mFansTv.setText(getString(R.string.fans_num, fancount));
            mFollowingTv.setText(getString(R.string.follow_num, followcount));
        } else {
            mFansTv.setText(getString(R.string.fans_num, 0));
            mFollowingTv.setText(getString(R.string.follow_num, 0));
        }

        //set default
        //TODO
        String duration;
        mUcgSecondDivider.setVisibility(View.GONE);
        mLessonTeachingDurationTv.setVisibility(View.GONE);
        if (AccountDataManager.isTeacher(mContext)) {
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
//            mPortraitView.setSex(basic.getSex());
            icons[0]= cn.xiaojs.xma.common.xf_foundation.schemas.Account.Sex.MALE.equalsIgnoreCase(basic.getSex())?R.drawable.ic_male:R.drawable.ic_female;
            mUserName.setIcons(icons);
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

    private void showCer() {

        if (AccountDataManager.isVerified(getContext())) {
            cerTips.setText("已认证");
            icons[1]=R.drawable.ic_vip;
            mUserName.setIcons(icons);
        }else {
            cerTips.setText("未认证");
            icons[1]=0;
            mUserName.setIcons(icons);
        }
    }

    private void showAbilities(){

        String abilities = AccountDataManager.getAbilities(getContext());
        if (!TextUtils.isEmpty(abilities)) {
            abilityTips.setText(abilities);
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
        if (mProfileCover != null) {
            mProfileCover.setVisibility(View.VISIBLE);
        }

        if (mBlurPortraitView != null) {
            Bitmap blurBitmap = FastBlur.smartBlur(getContext(),portrait, 8, true);
            mBlurPortraitView.setImageBitmap(blurBitmap);
        }

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
