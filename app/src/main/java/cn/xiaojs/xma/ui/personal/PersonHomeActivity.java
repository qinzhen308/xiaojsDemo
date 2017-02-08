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
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabActivity;
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabFragment;
import cn.xiaojs.xma.ui.view.RelationshipView;
import cn.xiaojs.xma.ui.widget.IconTextView;
import cn.xiaojs.xma.ui.widget.PortraitView;
import cn.xiaojs.xma.ui.widget.flow.ImageFlowLayout;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.FastBlur;

public class PersonHomeActivity extends BaseScrollTabActivity{

    private Unbinder mBinder;

    @BindView(R.id.person_home_header_blur)
    ImageView mBlur;
    @BindView(R.id.person_home_look_material)
    View mMaterial;
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

    private float mCoverScale = 9.0f / 16;

    private boolean mIsMyself;
    @Override
    protected void initView() {

        Intent intent = getIntent();
        if (intent != null){
            mIsMyself = intent.getBooleanExtra(PersonalBusiness.KEY_IS_MYSELF,false);
        }

        PersonHomeLessonFragment f1 = new PersonHomeLessonFragment();
        PersonHomeLessonFragment f2 = new PersonHomeLessonFragment();
        PersonHomeLessonFragment f3 = new PersonHomeLessonFragment();

        f1.setPagePosition(0);
        f2.setPagePosition(1);
        f3.setPagePosition(2);

        List<BaseScrollTabFragment> fragments = new ArrayList<>();
        fragments.add(f1);
        fragments.add(f2);
        fragments.add(f3);
        String[] tabs = new String[]{
                getString(R.string.person_lesson),
                getString(R.string.person_comment),
                getString(R.string.person_moment)};

        View header = LayoutInflater.from(this).inflate(R.layout.layout_person_home_header, null);
        View footer = LayoutInflater.from(this).inflate(R.layout.layout_person_home_footer, null);

        if (mIsMyself){
            tabs[0] = getString(R.string.my_lesson);
        }
        addContent(fragments,tabs,header,footer);
        mBinder = ButterKnife.bind(this);
        initHeader();
    }

    private void initHeader() {
        needHeaderDivider(false);
        mBack.setImageResource(R.drawable.ic_white_back);
        Bitmap blur = FastBlur.smartBlur(BitmapUtils.getBitmap(this, R.drawable.por_58), 2, true);
        mBlur.setImageBitmap(blur);
        needHeader(false);
        mScrollTitleBar.setBackgroundResource(R.drawable.ic_home_title_bg);
        if (mIsMyself){
            mScrollRightText.setText(R.string.edit_material);
            mScrollRightText.setTextColor(getResources().getColor(R.color.white));
            mHead.setSex(XiaojsConfig.mLoginUser.getAccount().getBasic().getSex());
            mName.setText(XiaojsConfig.mLoginUser.getName());

            mFollows.setVisibility(View.VISIBLE);
            mFollowDivider.setVisibility(View.VISIBLE);
            needFooter(false);
        }else {
            mScrollRightText.setText("关注");
            mScrollRightText.setTextColor(getResources().getColor(R.color.white));
            mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_white, 0, 0, 0);
            mScrollRightText.setBackgroundResource(R.drawable.white_stoke_bg);
            mHead.setSex("true");

            mFollows.setVisibility(View.GONE);
            mFollowDivider.setVisibility(View.GONE);
        }

        int paddingv = getResources().getDimensionPixelSize(R.dimen.px10);
        int paddingh = getResources().getDimensionPixelSize(R.dimen.px15);
        mScrollRightText.setPadding(paddingh,paddingv,paddingh,paddingv);
        mScrollRightText.setCompoundDrawablePadding(paddingh);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mScrollRightText.getLayoutParams();
        mlp.rightMargin = getResources().getDimensionPixelSize(R.dimen.px30);

        int width = DeviceUtil.getScreenWidth(this);
        int height = (int) (width * mCoverScale);
        mFreeCover.getLayoutParams().height = height;
        mFreeCover.setImageResource(DeviceUtil.getLesson());
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

    @OnClick({R.id.scroll_tab_left_image,R.id.person_home_message_only})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.scroll_tab_left_image:
                finish();
                break;
            case R.id.person_home_message_only://发消息
                break;
        }
    }

    @Override
    public void onScrollY(int y) {
        if (y > mBlur.getHeight()){
            if (mIsMyself){
                mScrollTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
                mScrollRightText.setText(R.string.edit_material);
                mScrollRightText.setTextColor(getResources().getColor(R.color.font_orange));
                mScrollMiddleText.setText(XiaojsConfig.mLoginUser.getName());
            }else {
                mScrollTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
                mScrollRightText.setTextColor(getResources().getColor(R.color.font_orange));
                mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_plus, 0, 0, 0);
                mScrollRightText.setBackgroundResource(R.drawable.orange_stoke_bg);
                mScrollMiddleText.setText("林妙可");
            }
            mBack.setImageResource(R.drawable.back_arrow);
            needHeaderDivider(true);
        }else {
            if (mIsMyself){
                mScrollTitleBar.setBackgroundResource(R.drawable.ic_home_title_bg);
                mScrollRightText.setText(R.string.edit_material);
                mScrollRightText.setTextColor(getResources().getColor(R.color.white));
                mScrollMiddleText.setText("");
            }else {
                mScrollTitleBar.setBackgroundResource(R.drawable.ic_home_title_bg);
                mScrollRightText.setTextColor(getResources().getColor(R.color.white));
                mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_white, 0, 0, 0);
                mScrollRightText.setBackgroundResource(R.drawable.white_stoke_bg);
                mScrollMiddleText.setText("");
            }
            mBack.setImageResource(R.drawable.ic_white_back);
            needHeaderDivider(false);
        }
    }

    @Override
    protected void onDestroy() {
        if (mBinder != null) {
            mBinder.unbind();
        }
        super.onDestroy();
    }

}
