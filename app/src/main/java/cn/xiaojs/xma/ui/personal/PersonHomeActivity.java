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
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.im.ChatActivity;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.account.PublicHome;
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
import cn.xiaojs.xma.util.FastBlur;
import cn.xiaojs.xma.util.StringUtil;
import cn.xiaojs.xma.util.ToastUtil;

public class PersonHomeActivity extends BaseScrollTabActivity {

    private Unbinder mBinder;

    @BindView(R.id.person_home_header_blur)
    ImageView mBlur;
    @BindView(R.id.person_home_header_image_holder)
    View mImageHolder;
    @BindView(R.id.person_home_head)
    PortraitView mHead;
    @BindView(R.id.person_home_name)
    IconTextView mName;
    @BindView(R.id.person_home_relationship)
    RelationshipView mRelationship;
    @BindView(R.id.person_home_follow_people)
    ImageFlowLayout mFollowPeople;
    @BindView(R.id.person_home_target)
    TextView mTarget;

    @BindView(R.id.person_home_fans)
    TextView mFans;
    @BindView(R.id.person_home_follow)
    TextView mFollows;
    @BindView(R.id.person_home_teaching_length)
    TextView mTeachingLength;
    @BindView(R.id.person_home_follow_divider)
    View mFollowDivider;

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

        addContent(header, footer);
        mBinder = ButterKnife.bind(this);
        initHeader();

        getData();

    }

    private void getData() {
        showProgress(true);
        AccountDataManager.getPublicHome(this, mAccount, new APIServiceCallback<PublicHome>() {
            @Override
            public void onSuccess(PublicHome object) {
                cancelProgress();
                initView(object);
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

    private void initView(PublicHome home) {
        if (home == null)
            return;
        mBean = home;

        if (!mIsMyself) {
            headerNormal(home.isFollowed);
        }
        if (home.isTeacher) {//用户是老师

            PersonHomeLessonFragment f1 = new PersonHomeLessonFragment();
            PersonHomeLessonFragment f2 = new PersonHomeLessonFragment();
            PersonHomeMomentFragment f3 = new PersonHomeMomentFragment();

            Bundle b1 = new Bundle();
            b1.putSerializable(PersonalBusiness.KEY_PERSONAL_LESSON_LIST, mBean.lessons);
            f1.setArguments(b1);
            f1.setPagePosition(0);
            f2.setPagePosition(1);
            f3.setPagePosition(2);

            List<BaseScrollTabFragment> fragments = new ArrayList<>();
            fragments.add(f1);
            fragments.add(f2);
            fragments.add(f3);
            String[] tabs = new String[]{
                    getString(R.string.person_lesson,StringUtil.getTa(home.profile.sex)),
                    getString(R.string.person_comment),
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


        Glide.with(this)
                .load(Account.getAvatar(home.profile.id, 300))
                .error(R.drawable.default_avatar)
                .into(new GlideDrawableImageViewTarget(mHead) {
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
        mHead.setSex(home.profile.sex);
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
    }

    private void setupBlurPortraitView(Bitmap portrait) {
        Bitmap blurBitmap = FastBlur.smartBlur(portrait, 4, true);
        mBlur.setImageBitmap(blurBitmap);
    }

    private void initProfileBg() {
        //mBlur.setBackgroundColor(getResources().getColor(R.color.main_blue));
        mBlur.setImageResource(R.drawable.portrait_default_bg);
    }

    private void setDefaultPortrait() {
        mHead.setImageResource(R.drawable.default_avatar);
        initProfileBg();
    }

    private void initHeader() {
        needHeaderDivider(false);
        mBack.setImageResource(R.drawable.ic_white_back);
        needHeader(false);
        mScrollTitleBar.setBackgroundResource(R.drawable.ic_home_title_bg);
        if (mIsMyself) {
            mHead.setSex(XiaojsConfig.mLoginUser.getAccount().getBasic().getSex());
            mName.setText(XiaojsConfig.mLoginUser.getName());

            mFollows.setVisibility(View.VISIBLE);
            mFollowDivider.setVisibility(View.VISIBLE);
            needFooter(false);
            myself();
        } else {
            mHead.setSex("true");

            mFollows.setVisibility(View.GONE);
            mFollowDivider.setVisibility(View.GONE);
        }

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

    @OnClick({R.id.scroll_tab_left_image, R.id.person_home_message_only, R.id.person_home_message,
            R.id.person_home_query, R.id.scroll_tab_right_view})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.scroll_tab_left_image:
                finish();
                break;
            case R.id.person_home_message_only://发消息
                sendMessage();
                break;
            case R.id.person_home_message:
                sendMessage();
                break;
            case R.id.person_home_query://提问

                break;
            case R.id.scroll_tab_right_view://加关注
                showFollowDialog();
                break;
        }
    }

    private void sendMessage() {
        if (mBean != null) {
            if (mBean.isFollowed) {//已关注跳转到聊天界面
                final Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra(ChatActivity.TARGET_ID, "1234567");
                intent.putExtra(ChatActivity.TARGET_APP_KEY, "e87cffb332432eec3c0807ba");
                intent.putExtra(ChatActivity.ACCOUNT_ID, mAccount);
                startActivity(intent);
            } else {//未关注先提示去关注
                final CommonDialog dialog = new CommonDialog(this);
                if (mBean.basic == null){
                    mBean.basic = new cn.xiaojs.xma.model.account.Account.Basic();
                    mBean.basic.setSex("true");
                }
                dialog.setDesc(getString(R.string.none_follow_tip,StringUtil.getTa(mBean.profile.sex),StringUtil.getTa(mBean.profile.sex)));
                dialog.setOkText(getString(R.string.follow_somebody, StringUtil.getTa(mBean.profile.sex)));
                dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();
                    }
                });
                dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        dialog.dismiss();
                        //follow();
                        showFollowDialog();
                    }
                });

                dialog.show();
            }
        }
    }

    private void showFollowDialog(){
        BaseBusiness.showFollowDialog(this, new BaseBusiness.OnFollowListener() {
            @Override
            public void onFollow(long group) {
                if (group > 0){
                    follow(group);
                }
            }
        });
    }

    private void follow(long group) {
        if (mBean != null && !mBean.isFollowed) {//这里需要弹框选择分组
            SocialManager.followContact(this, mAccount, group, new APIServiceCallback<Relation>() {
                @Override
                public void onSuccess(Relation object) {
                    ToastUtil.showToast(getApplicationContext(), R.string.followed);
                    mBean.isFollowed = true;
                    headerNormal(mBean.isFollowed);
//
//                    Contact contact = new Contact();
//                    contact.alias = alias;
//                    contact.account = id;
//
//                    DataManager.addContact(getApplicationContext(),Social.ContactGroup.FRIENDS,contact);
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
        if (y > mBlur.getHeight()) {
            if (mIsMyself) {
                mScrollTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
                mScrollMiddleText.setText(mPersonName);
            } else {
                headerScrolled(mBean.isFollowed);
            }
            mBack.setImageResource(R.drawable.back_arrow);
        } else {
            if (mIsMyself) {
                mScrollTitleBar.setBackgroundResource(R.drawable.ic_home_title_bg);
                mScrollMiddleText.setText("");
            } else {
                headerNormal(mBean.isFollowed);
            }
            mBack.setImageResource(R.drawable.ic_white_back);
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
        mSummaryWrapper.setVisibility(View.VISIBLE);
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
