package cn.xiaojs.xma.ui.personal;
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
 * Date:2016/12/13
 * Desc:个人主页
 *
 * ======================================================================================== */

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataChangeHelper;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.SimpleDataChangeListener;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.account.PublicHome;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabActivity;
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabFragment;
import cn.xiaojs.xma.ui.mine.MySignatureDetailActivity;
import cn.xiaojs.xma.ui.view.RelationshipView;
import cn.xiaojs.xma.ui.widget.IconTextView;
import cn.xiaojs.xma.ui.widget.MaxLineTextView;
import cn.xiaojs.xma.ui.widget.PortraitView;
import cn.xiaojs.xma.ui.widget.flow.ImageFlowLayout;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.ExpandGlide;
import cn.xiaojs.xma.util.FastBlur;
import cn.xiaojs.xma.util.StringUtil;
import cn.xiaojs.xma.util.ToastUtil;
import okhttp3.ResponseBody;

public class PersonHomeActivity extends BaseScrollTabActivity implements BaseBusiness.OnFollowListener{
    private Unbinder mBinder;

    @BindView(R.id.personal_profile)
    View headerLayout;
    @BindView(R.id.person_home)
    View mPersonHomeBtn;
    @BindView(R.id.message_entrance)
    View mMessageEntrace;
    //profile info
    @BindView(R.id.blur_portrait)
    ImageView mBlurImgView;
    @BindView(R.id.portrait)
    PortraitView mPortraitView;
    @BindView(R.id.user_name)
    IconTextView mName;
    @BindView(R.id.profile_cover)
    View mProfileCover;

    //ugc
    @BindView(R.id.fans)
    TextView mFans;
    @BindView(R.id.following)
    TextView mFollows;
    @BindView(R.id.lesson_teaching_duration)
    TextView mTeachingLength;
    @BindView(R.id.first_divider)
    View mFirstDivider;
    @BindView(R.id.second_divider)
    View mSecondDivider;

    //profile
    @BindView(R.id.my_profile_txt)
    MaxLineTextView mProfileTv;

    @BindView(R.id.btn_more)
    TextView btnMore;

    @BindView(R.id.person_home_relationship)
    RelationshipView mRelationship;
    @BindView(R.id.person_home_follow_people)
    ImageFlowLayout mFollowPeople;
    @BindView(R.id.person_home_target)
    TextView mTarget;

    @BindView(R.id.person_home_free_video_cover)
    ImageView mFreeCover;

    @BindView(R.id.person_home_message_more)
    View mFooterMultiple;
//    @BindView(R.id.person_home_message_only)
//    View mFooterSingle;

    @BindView(R.id.person_home_summary_wrapper)
    View mSummaryWrapper;
    @BindView(R.id.label_summary)
    TextView labelSummary;
    @BindView(R.id.person_home_summary)
    TextView mSummary;
    @BindView(R.id.person_home_relationship_wrapper)
    View mRelationshipWrapper;
    @BindView(R.id.person_home_target_wrapper)
    View mTargetWrapper;
    @BindView(R.id.person_home_look_material)
    View mMaterialWrapper;
    @BindView(R.id.person_home_free_wrapper)
    View mFreeWrapper;

    private float mCoverScale = 9.0f / 16;

    private boolean mIsMyself;

    private String mAccount;
    private String mPersonName;
    private PublicHome mBean;

    private boolean isOrganization;

    @Override
    protected void initView() {
        Intent intent = getIntent();
        if (intent != null) {
            mIsMyself = intent.getBooleanExtra(PersonalBusiness.KEY_IS_MYSELF, false);
            if (XiaojsConfig.mLoginUser == null) {
                XiaojsConfig.mLoginUser = AccountDataManager.getUserInfo(this);
            }

            if (!mIsMyself) {
                mAccount = intent.getStringExtra(PersonalBusiness.KEY_PERSONAL_ACCOUNT);
                if (!TextUtils.isEmpty(mAccount)) {
                    if (mAccount.equalsIgnoreCase(XiaojsConfig.mLoginUser.getId())) {
                        mIsMyself = true;
                    }
                }
            } else {
                mAccount = XiaojsConfig.mLoginUser.getId();
            }
        }

        View header = LayoutInflater.from(this).inflate(R.layout.layout_person_home_header, null);
        View footer = LayoutInflater.from(this).inflate(R.layout.layout_person_home_footer, null);

        addHeader(header);
        addFooter(footer);
        mBinder = ButterKnife.bind(this);
        initHeader();
        setDefaultUgc();
        getData();
    }

    private void getData() {
        showProgress(true);
        AccountDataManager.getPublicHome(this, mAccount, new APIServiceCallback<PublicHome>() {
            @Override
            public void onSuccess(PublicHome home) {
                cancelProgress();
                initData(home);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                showFailedView(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getData();
                    }
                });
            }
        });
    }

    private void initData(PublicHome home) {
        if (home == null) {
            return;
        }
        mBean = home;

        //mBean.isFollowed = DataManager.existInContacts(this,mAccount);

        if (!mIsMyself) {
            headerNormal(home.isFollowed);
        }

        isOrganization = home.profile != null ? Account.TypeName.ORGANIZATION.equals(home.profile.typeName) : false;
        if (home.isTeacher ) {//用户是老师
            PersonHomeMomentFragment f2 = new PersonHomeMomentFragment();

            Bundle b2 = new Bundle();
            b2.putSerializable(PersonalBusiness.KEY_PERSONAL_ACTIVITY_LIST, mBean.activities);
            f2.setPagePosition(2);
            f2.setArguments(b2);

            List<BaseScrollTabFragment> fragments = new ArrayList<>();
            fragments.add(PersonHomeRecordedLessonFragment.newInstance(0,mAccount));
            fragments.add(PersonHomeClassFragment.newInstance(1,mAccount));
            fragments.add(f2);
            String[] tabs = new String[]{
                    getString(R.string.record_lesson),
                    "教室",
                    getString(R.string.person_moment)};
//            if (mIsMyself) {
//                tabs[0] = getString(R.string.my_lesson);
//            }
            addContent(fragments, tabs);
        }else if(isOrganization){//机构页面
            //改变头部整体高度
            ViewGroup.LayoutParams lp=headerLayout.getLayoutParams();
            lp.height=getResources().getDimensionPixelSize(R.dimen.px380);
            headerLayout.setLayoutParams(lp);
            organization();
            if(!TextUtils.isEmpty(home.profile.title)){
                labelSummary.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(PersonHomeActivity.this, MySignatureDetailActivity.class).putExtra(MySignatureDetailActivity.KEY_CONTENT,mBean.profile.title));
                    }
                });
                labelSummary.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.ic_entrance,0);
            }else {
                labelSummary.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            }
            mSummary.setText(TextUtils.isEmpty(home.profile.title)?"暂无简介":home.profile.title.trim());
            if (mProfileTv !=null) {
                mProfileTv.setVisibility(View.GONE);
            }


            PersonHomeTeacherFragment f2 = PersonHomeTeacherFragment.createInstance(mAccount);
            f2.setPagePosition(2);
            List<BaseScrollTabFragment> fragments = new ArrayList<>();
            fragments.add(PersonHomeRecordedLessonFragment.newInstance(0,mAccount));
            fragments.add(PersonHomeClassFragment.newInstance(1,mAccount));
            fragments.add(f2);
            String[] tabs = new String[]{
                    getString(R.string.record_lesson),
                    "教室",
                    "老师"};
            addContent(fragments, tabs);
        }else {//用户不是老师
            PersonHomeMomentFragment f1 = new PersonHomeMomentFragment();
            List<BaseScrollTabFragment> fragments = new ArrayList<>();
            fragments.add(f1);
            if (mIndicator != null) {
                mIndicator.setVisibility(View.GONE);
            }

            addContent(fragments, null);
        }

        //set avatar
        mBlurImgView.setImageResource(R.drawable.portrait_default_bg);
        String u = cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(mIsMyself ? AccountDataManager.getAccountID(this) :
                mAccount, 300);
        ExpandGlide expandGlide = new ExpandGlide();
        expandGlide.with(this)
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

//        mPortraitView.setSex(home.profile.sex);
        mName.setText(home.profile.name);
        mName.setIcon(Account.Sex.MALE.equalsIgnoreCase(home.profile.sex)?R.drawable.ic_male:R.drawable.ic_female);
        int fancount = home.profile.stats.fans > 0? home.profile.stats.fans: 0;
        int followcount = home.profile.stats.followships > 0? home.profile.stats.followships: 0;

        mFans.setText(getString(R.string.fans_num, fancount));
        mFollows.setText(getString(R.string.follow_num, followcount));
        mPersonName = home.profile.name;
        if (Account.TypeName.ORGANIZATION.equalsIgnoreCase(home.profile.typeName)) {
            //mFooterSingle.setVisibility(View.VISIBLE);
        } else {
            if (!mIsMyself) {
                if (home.isTeacher) {
                    if (mFooterMultiple != null) {
                        mFooterMultiple.setVisibility(View.VISIBLE);
                    }
                } else {
                    //mFooterSingle.setVisibility(View.VISIBLE);
                }
            }
        }

        //set title
        if (home.profile != null && !TextUtils.isEmpty(home.profile.title)) {
            mProfileTv.setText(home.profile.title.trim());
//            mProfileTv.setText("啊高考刚拿11了奶咖了打开了大神快乐可按阿喀琉斯大神了大家爱神的箭阿克苏的煎熬死了肯德基啊快乐的骄傲看爱上加大了解答了速度将拉开觉得去了的骄傲快了速度将拉开");
        }
    }

    private void setDefaultUgc() {
        mFans.setText(getString(R.string.fans_num, 0));
        mFollows.setText(getString(R.string.follow_num, 0));
        String duration;
        if (AccountDataManager.isTeacher(this)) {
            duration = getString(R.string.tea_lesson_duration, 0);
        } else {
            duration = getString(R.string.stu_lesson_duration, 0);
        }
        mTeachingLength.setText(duration);

        mSecondDivider.setVisibility(View.GONE);
        mTeachingLength.setVisibility(View.GONE);
    }

    private void setupBlurPortraitView(Bitmap portrait) {
        mProfileCover.setVisibility(View.VISIBLE);
        Bitmap blurBitmap = FastBlur.smartBlur(this,portrait, 4, true);
        mBlurImgView.setImageBitmap(blurBitmap);
    }

    private void initHeader() {
        needHeaderDivider(false);
        needHeader(false);
        mBackBtn.setImageResource(R.drawable.ic_white_back);
        mPersonHomeBtn.setVisibility(View.GONE);
        mMessageEntrace.setVisibility(View.GONE);
        mScrollTitleBar.setBackgroundResource(R.drawable.ic_home_title_bg);
        if (mIsMyself) {
//            mPortraitView.setSex(XiaojsConfig.mLoginUser.getAccount().getBasic().getSex());
            mName.setText(XiaojsConfig.mLoginUser.getName());
            mName.setIcon(Account.Sex.MALE.equalsIgnoreCase(XiaojsConfig.mLoginUser.getAccount().getBasic().getSex())?R.drawable.ic_male:R.drawable.ic_female);
            mFollows.setVisibility(View.VISIBLE);
            needFooter(false);
            myself();
        } else {
//            mPortraitView.setSex("true");
            mName.setIcon(R.drawable.ic_male);
            mFollows.setVisibility(View.GONE);
            mFirstDivider.setVisibility(View.GONE);
        }

        String duration;
        if (AccountDataManager.isTeacher(this)) {
            duration = getString(R.string.tea_lesson_duration, 0);
        } else {
            duration = getString(R.string.stu_lesson_duration, 0);
        }
        mTeachingLength.setText(duration);

        int paddingv = getResources().getDimensionPixelSize(R.dimen.px10);
        int paddingh = getResources().getDimensionPixelSize(R.dimen.px15);
        mScrollRightText.setPadding(paddingh, paddingv, paddingh, paddingv);
        mScrollRightText.setCompoundDrawablePadding(paddingh);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mScrollRightText.getLayoutParams();
        mlp.rightMargin = getResources().getDimensionPixelSize(R.dimen.px30);

        int width = DeviceUtil.getScreenWidth(this);
        int height = (int) (width * mCoverScale);
        mFreeCover.getLayoutParams().height = height;
        //mFreeCover.setImageResource(DeviceUtil.getLesson());
        List<Bitmap> lists = new ArrayList<>();
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        lists.add(BitmapUtils.getBitmap(this, R.drawable.ic_images_up));
        mFollowPeople.show(lists);

        /*mProfileTv.setOnOverLineChangedListener(new MaxLineTextView.OnOverSizeChangedListener() {
            @Override
            public void onChanged(boolean isOverSize) {
                if(isOverSize){
                    btnMore.setVisibility(View.VISIBLE);
                    mProfileTv.resetTextWithoutListener();
                }else {
                    btnMore.setVisibility(View.GONE);
                }
            }
        });*/
    }

    @Override
    protected int getTitleHeight() {
        return mScrollTitleBar.getHeight();
    }

    @OnClick({R.id.scroll_tab_left_image,
            R.id.person_home_query, R.id.scroll_tab_right_view}) //R.id.person_home_message_only, R.id.person_home_message_btn
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.scroll_tab_left_image:
                finish();
                break;
//            case R.id.person_home_message_btn://发消息
//            case R.id.person_home_message_only://发消息
//                sendMessage();
//                break;
            case R.id.person_home_query://提问

                break;
            case R.id.scroll_tab_right_view://加关注
                if (mBean !=null && !mBean.isFollowed){
                    boolean isOrganization = mBean.profile != null ? Account.TypeName.ORGANIZATION.equals(mBean.profile.typeName) : false;
                    if (isOrganization){
                        BaseBusiness.showFollowDialog(this, this, Social.ContactGroup.ORGANIZATIONS);
                    }else {
                        BaseBusiness.showFollowDialog(this, this);
                    }
                }else {
                    unFollow();
                }
                break;
            case R.id.person_home_summary_wrapper://查看签名
                startActivity(new Intent(this, MySignatureDetailActivity.class).putExtra(MySignatureDetailActivity.KEY_CONTENT,mBean.profile.title));
                break;
        }
    }

    private void sendMessage() {
        if (mBean != null) {
            String name = (mBean.profile== null || TextUtils.isEmpty(mBean.profile.name))? "未知" : mBean.profile.name;
            String sex = (mBean.profile ==null || TextUtils.isEmpty(mBean.profile.sex)) ? "true" : mBean.profile.sex;
            BaseBusiness.advisory(this,mBean.isFollowed,mAccount,name,sex,this);
        }
    }

    @Override
    public void onFollow(long group) {

        follow(Social.ContactGroup.FRIENDS);
//        if (group > 0) {
//            follow(group);
//        }
    }

    private void follow(long group) {
        if (mBean != null && !mBean.isFollowed) {//这里需要弹框选择分组

            Contact.MetIn metIn = new Contact.MetIn();
            metIn.id = mAccount;
            metIn.subtype = isOrganization ? Collaboration.SubType.ORGANIZATION : Collaboration.SubType.PERSON;

            SocialManager.followContact(this,
                    mAccount,mPersonName, group, metIn, new APIServiceCallback<Relation>() {
                @Override
                public void onSuccess(Relation object) {
                    ToastUtil.showToast(getApplicationContext(), R.string.followed);
                    mBean.isFollowed = true;
                    headerNormal(mBean.isFollowed);
                    DataChangeHelper.getInstance().notifyDataChanged(SimpleDataChangeListener.FOLLOW_USER);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    ToastUtil.showToast(getApplicationContext(), errorMessage);
                }
            });
        }
    }

    private void unFollow() {
        if (mBean != null && mBean.isFollowed) {

            Contact.MetIn metIn = new Contact.MetIn();
            metIn.subtype = isOrganization ? Collaboration.SubType.ORGANIZATION : Collaboration.SubType.PERSON;

            SocialManager.unfollowContact(this,
                    mAccount, new APIServiceCallback<ResponseBody>() {
                        @Override
                        public void onSuccess(ResponseBody object) {
                            ToastUtil.showToast(getApplicationContext(), "已取消关注");
                            mBean.isFollowed = false;
                            headerNormal(mBean.isFollowed);
                            DataChangeHelper.getInstance().notifyDataChanged(SimpleDataChangeListener.FOLLOW_USER);
                        }

                        @Override
                        public void onFailure(String errorCode, String errorMessage) {
                            ToastUtil.showToast(getApplicationContext(), errorMessage);
                        }
                    });
        }
    }

    @Override
    public void onScrollY(int y) {
        if (y > mBlurImgView.getHeight()) {
            if (mIsMyself) {
                mScrollTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
                mScrollMiddleText.setText(mPersonName);
            } else {
                headerScrolled(mBean.isFollowed);
            }
            mBackBtn.setImageResource(R.drawable.back_arrow);
        } else {
            if (mIsMyself) {
                mScrollTitleBar.setBackgroundResource(R.drawable.ic_home_title_bg);
                mScrollMiddleText.setText("");
            } else {
                headerNormal(mBean.isFollowed);
            }
            mBackBtn.setImageResource(R.drawable.ic_white_back);
        }
    }

    private void headerNormal(boolean followed) {
        mScrollTitleBar.setBackgroundResource(R.drawable.ic_home_title_bg);
        mScrollRightText.setTextColor(getResources().getColor(R.color.white));
        if (!followed) {
            mScrollRightText.setText("关注");
            mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_white, 0, 0, 0);
            mScrollRightText.setBackgroundResource(R.drawable.blue_solid_bg_corner);
        } else {
            mScrollRightText.setText("取消关注");
            mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mScrollRightText.setBackgroundResource(R.drawable.white_stoke_bg);
        }
        mScrollMiddleText.setText("");
    }

    private void headerScrolled(boolean followed) {
        mScrollTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        if (!followed) {
            mScrollRightText.setText("关注");
            mScrollRightText.setTextColor(getResources().getColor(R.color.white));
            mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_white, 0, 0, 0);
            mScrollRightText.setBackgroundResource(R.drawable.blue_solid_bg_corner);
        } else {
            mScrollRightText.setText("取消关注");
            mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            mScrollRightText.setBackgroundResource(R.drawable.white_stoke_bg);
            mScrollRightText.setTextColor(getResources().getColor(R.color.font_orange));
            mScrollRightText.setBackgroundResource(R.drawable.orange_stoke_bg_corner);
        }
        mScrollMiddleText.setText(mPersonName);
    }


    private void myself() {
        //TODO not data for teachers summary
        //mSummaryWrapper.setVisibility(View.VISIBLE);
        mRelationshipWrapper.setVisibility(View.GONE);
        mTargetWrapper.setVisibility(View.GONE);
        mMaterialWrapper.setVisibility(View.GONE);
        mFreeWrapper.setVisibility(View.GONE);
    }

    private void organization() {
        mSummaryWrapper.setVisibility(View.VISIBLE);
        mRelationshipWrapper.setVisibility(View.GONE);
        mTargetWrapper.setVisibility(View.GONE);
        mMaterialWrapper.setVisibility(View.GONE);
        mFreeWrapper.setVisibility(View.GONE);
    }

    private void teacher() {
        mSummaryWrapper.setVisibility(View.VISIBLE);
        mRelationshipWrapper.setVisibility(View.GONE);
        mTargetWrapper.setVisibility(View.GONE);
        mMaterialWrapper.setVisibility(View.GONE);
        mFreeWrapper.setVisibility(View.GONE);
    }

    private void student() {
        mSummaryWrapper.setVisibility(View.GONE);
        mRelationshipWrapper.setVisibility(View.VISIBLE);
        mTargetWrapper.setVisibility(View.VISIBLE);
        mMaterialWrapper.setVisibility(View.VISIBLE);
        mFreeWrapper.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        if (mBinder != null) {
            mBinder.unbind();
        }

        super.onDestroy();
    }

}
