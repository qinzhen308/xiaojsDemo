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
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.im.ChatActivity;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.account.PublicHome;
import cn.xiaojs.xma.model.account.User;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabActivity;
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabFragment;
import cn.xiaojs.xma.ui.view.RelationshipView;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.IconTextView;
import cn.xiaojs.xma.ui.widget.PortraitView;
import cn.xiaojs.xma.ui.widget.flow.ImageFlowLayout;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.ExpandGlide;
import cn.xiaojs.xma.util.FastBlur;
import cn.xiaojs.xma.util.JpushUtil;
import cn.xiaojs.xma.util.StringUtil;
import cn.xiaojs.xma.util.ToastUtil;

public class PersonHomeActivity extends BaseScrollTabActivity implements BaseBusiness.OnFollowListener{
    private Unbinder mBinder;

    @BindView(R.id.person_home)
    View mPersonHomeBtn;
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
    TextView mProfileTv;

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
    @BindView(R.id.person_home_message_only)
    View mFooterSingle;

    @BindView(R.id.person_home_summary_wrapper)
    View mSummaryWrapper;
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

        //FIXME 接口isFollowed没有返回正确的值，所以先从内存中判断。
        mBean.isFollowed = DataManager.existInContacts(this,mAccount);

        if (!mIsMyself) {
            headerNormal(home.isFollowed);
        }

        boolean isOrganization = home.profile != null ? Account.TypeName.ORGANIZATION.equals(home.profile.typeName) : false;
        if (home.isTeacher || isOrganization) {//用户是老师
            //一期暂时没有评价系统，暂时隐藏
            PersonHomeLessonFragment f1 = new PersonHomeLessonFragment();
            PersonHomeMomentFragment f2 = new PersonHomeMomentFragment();
            //PersonHomeMomentFragment f3 = new PersonHomeMomentFragment();

            Bundle b1 = new Bundle();
            Bundle b2 = new Bundle();
            b1.putSerializable(PersonalBusiness.KEY_PERSONAL_ACCOUNT_ID, mAccount);
            b2.putSerializable(PersonalBusiness.KEY_PERSONAL_ACTIVITY_LIST, mBean.activities);
            f1.setArguments(b1);
            f1.setPagePosition(0);
            f2.setPagePosition(1);
            f2.setArguments(b2);
            //f3.setPagePosition(2);

            List<BaseScrollTabFragment> fragments = new ArrayList<>();
            fragments.add(f1);
            fragments.add(f2);
            //fragments.add(f3);
            String[] tabs = new String[]{
                    getString(R.string.person_lesson, StringUtil.getTa(home.profile.sex)),
                    //getString(R.string.person_comment),
                    getString(R.string.person_moment)};
            if (mIsMyself) {
                tabs[0] = getString(R.string.my_lesson);
            }
            addContent(fragments, tabs);
        } else {//用户不是老师
            PersonHomeMomentFragment f1 = new PersonHomeMomentFragment();
            List<BaseScrollTabFragment> fragments = new ArrayList<>();
            fragments.add(f1);
            mIndicator.setVisibility(View.GONE);
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

        mPortraitView.setSex(home.profile.sex);
        mName.setText(home.profile.name);
        mFans.setText(getString(R.string.fans_num, home.profile.stats.fans));
        mFollows.setText(getString(R.string.follow_num, home.profile.stats.followships));
        mPersonName = home.profile.name;
        if (Account.TypeName.ORGANIZATION.equalsIgnoreCase(home.profile.typeName)) {
            mFooterSingle.setVisibility(View.VISIBLE);
        } else {
            if (!mIsMyself) {
                if (home.isTeacher) {
                    mFooterMultiple.setVisibility(View.VISIBLE);
                } else {
                    mFooterSingle.setVisibility(View.VISIBLE);
                }
            }
        }

        //set title
        if (home.profile != null && !TextUtils.isEmpty(home.profile.title)) {
            mProfileTv.setText(home.profile.title);
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
        Bitmap blurBitmap = FastBlur.smartBlur(portrait, 4, true);
        mBlurImgView.setImageBitmap(blurBitmap);
    }

    private void initHeader() {
        needHeaderDivider(false);
        needHeader(false);
        mBackBtn.setImageResource(R.drawable.ic_white_back);
        mPersonHomeBtn.setVisibility(View.GONE);
        mScrollTitleBar.setBackgroundResource(R.drawable.ic_home_title_bg);
        if (mIsMyself) {
            mPortraitView.setSex(XiaojsConfig.mLoginUser.getAccount().getBasic().getSex());
            mName.setText(XiaojsConfig.mLoginUser.getName());
            mFollows.setVisibility(View.VISIBLE);
            needFooter(false);
            myself();
        } else {
            mPortraitView.setSex("true");
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

    }

    @Override
    protected int getTitleHeight() {
        return mScrollTitleBar.getHeight();
    }

    @OnClick({R.id.scroll_tab_left_image, R.id.person_home_message_only, R.id.person_home_message_btn,
            R.id.person_home_query, R.id.scroll_tab_right_view})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.scroll_tab_left_image:
                finish();
                break;
            case R.id.person_home_message_btn:
            case R.id.person_home_message_only://发消息
                sendMessage();
                break;
//            case R.id.person_home_message:
//                sendMessage();
//                break;
            case R.id.person_home_query://提问

                break;
            case R.id.scroll_tab_right_view://加关注
                if (mBean !=null && !mBean.isFollowed){
                    BaseBusiness.showFollowDialog(this, this);
                }
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
        if (group > 0) {
            follow(group);
        }
    }

    private void follow(long group) {
        if (mBean != null && !mBean.isFollowed) {//这里需要弹框选择分组
            SocialManager.followContact(this, mAccount, group, new APIServiceCallback<Relation>() {
                @Override
                public void onSuccess(Relation object) {
                    ToastUtil.showToast(getApplicationContext(), R.string.followed);
                    mBean.isFollowed = true;
                    headerNormal(mBean.isFollowed);

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
        } else {
            mScrollRightText.setText("已关注");
            mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        mScrollRightText.setBackgroundResource(R.drawable.white_stoke_bg);
        mScrollMiddleText.setText("");
    }

    private void headerScrolled(boolean followed) {
        mScrollTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
        mScrollRightText.setTextColor(getResources().getColor(R.color.font_orange));
        if (!followed) {
            mScrollRightText.setText("关注");
            mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_plus, 0, 0, 0);
        } else {
            mScrollRightText.setText("已关注");
        }
        mScrollRightText.setBackgroundResource(R.drawable.orange_stoke_bg);
        mScrollMiddleText.setText(mPersonName);
    }


    private void myself() {
        //TODO not data for teacher summary
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
