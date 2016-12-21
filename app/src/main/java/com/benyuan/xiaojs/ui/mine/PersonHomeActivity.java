package com.benyuan.xiaojs.ui.mine;
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

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseScrollTabActivity;
import com.benyuan.xiaojs.ui.base.BaseScrollTabListAdapter;
import com.benyuan.xiaojs.ui.view.RelationshipView;
import com.benyuan.xiaojs.ui.widget.IconTextView;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;
import com.benyuan.xiaojs.ui.widget.flow.ImageFlowLayout;
import com.benyuan.xiaojs.util.BitmapUtils;
import com.benyuan.xiaojs.util.DeviceUtil;
import com.benyuan.xiaojs.util.FastBlur;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PersonHomeActivity extends BaseScrollTabActivity implements BaseScrollTabActivity.OnPagerClickListener {

    private Unbinder mBinder;

    @BindView(R.id.person_home_header_blur)
    ImageView mBlur;
    @BindView(R.id.person_home_look_material)
    View mMaterial;
    @BindView(R.id.person_home_header_image_holder)
    View mImageHolder;
    @BindView(R.id.person_home_head)
    RoundedImageView mHead;
    @BindView(R.id.person_home_name)
    IconTextView mName;
    @BindView(R.id.person_home_relationship)
    RelationshipView mRelationship;
    @BindView(R.id.person_home_follow_people)
    ImageFlowLayout mFollowPeople;
    @BindView(R.id.person_home_target)
    TextView mTarget;

    @BindView(R.id.person_home_free_video_cover)
    ImageView mFreeCover;

    private float mCoverScale = 9.0f / 16;

    @Override
    public void addHoverHeaderView() {
        ArrayList<String> mPagerTitles = new ArrayList<String>();
        mPagerTitles.add(getString(R.string.person_lesson));
        mPagerTitles.add(getString(R.string.person_comment));
        mPagerTitles.add(getString(R.string.person_moment));
        View header = LayoutInflater.from(this).inflate(R.layout.layout_person_home_header, null);
        View footer = LayoutInflater.from(this).inflate(R.layout.layout_person_home_footer, null);
        ArrayList<BaseScrollTabListAdapter> adapters = new ArrayList<>();
        PersonHomeLessonAdapter adapter = new PersonHomeLessonAdapter(this);
        PersonHomeLessonAdapter adapter1 = new PersonHomeLessonAdapter(this);
        PersonHomeLessonAdapter adapter2 = new PersonHomeLessonAdapter(this);
        adapters.add(adapter);
        adapters.add(adapter1);
        adapters.add(adapter2);
        addTabListIntoContent(header, footer, mPagerTitles, adapters, 0);
        setNeedTabView(true);

        mBinder = ButterKnife.bind(this);
        initHeader();
        setOnPagerClickListener(this);
    }

    private void initHeader() {
        needHeaderDivider(false);
        Bitmap blur = FastBlur.smartBlur(BitmapUtils.getBitmap(this, R.drawable.default_portrait), 2, true);
        mBlur.setImageBitmap(blur);
        needHeader(false);
        mTabHeader.setBackgroundResource(R.drawable.ic_home_title_bg);
        mTabRightText.setText("关注");
        mTabRightText.setTextColor(getResources().getColor(R.color.white));
        mTabRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_white, 0, 0, 0);
        mTabRightText.setBackgroundResource(R.drawable.white_stoke_bg);
        int paddingv = getResources().getDimensionPixelSize(R.dimen.px10);
        int paddingh = getResources().getDimensionPixelSize(R.dimen.px15);
        mTabRightText.setPadding(paddingh,paddingv,paddingh,paddingv);
        mTabRightText.setCompoundDrawablePadding(paddingh);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mTabRightText.getLayoutParams();
        mlp.rightMargin = getResources().getDimensionPixelSize(R.dimen.px30);

        int width = DeviceUtil.getScreenWidth(this);
        int height = (int) (width * mCoverScale);
        mFreeCover.getLayoutParams().height = height;

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
    protected void onScrollY(int y) {
        int bannerHeight = mImageHolder.getMeasuredHeight();
        Logger.i("onScrollY y = " + y);
        if (y <= bannerHeight) {
            changeHeader(true);
        } else {
            changeHeader(false);
        }
    }

    private void changeHeader(boolean transparent) {
        if (mTabHeader != null && mTabRightText != null) {
            if (transparent) {
                mTabHeader.setBackgroundResource(R.drawable.ic_home_title_bg);
                mTabRightText.setTextColor(getResources().getColor(R.color.white));
                mTabRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_white, 0, 0, 0);
                mTabRightText.setBackgroundResource(R.drawable.white_stoke_bg);
                mTabMiddleText.setText("");
                needHeaderDivider(false);
            } else {
                mTabHeader.setBackgroundColor(getResources().getColor(R.color.white));
                mTabRightText.setTextColor(getResources().getColor(R.color.font_orange));
                mTabRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_plus, 0, 0, 0);
                mTabRightText.setBackgroundResource(R.drawable.orange_stoke_bg);
                mTabMiddleText.setText("林妙可");
                needHeaderDivider(true);
            }
        }
    }

    @Override
    protected int hoverMarginTop() {
        return getResources().getDimensionPixelSize(R.dimen.px90);
    }

    @OnClick({R.id.left_image})
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.left_image:
                if (mMaterial.getVisibility() == View.VISIBLE) {
                    mMaterial.setVisibility(View.GONE);
                } else {
                    mMaterial.setVisibility(View.VISIBLE);
                }

                break;
        }
    }


    @Override
    protected void onDestroy() {
        if (mBinder != null) {
            mBinder.unbind();
        }
        super.onDestroy();
    }

    @Override
    public void onSelected(int position) {
        changeHeader(true);

        Logger.i("onSelected");
    }
}
