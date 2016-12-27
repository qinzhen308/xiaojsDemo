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
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabActivity;
import cn.xiaojs.xma.ui.base.hover.BaseScrollTabFragment;
import cn.xiaojs.xma.ui.view.RelationshipView;
import cn.xiaojs.xma.ui.widget.IconTextView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
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
    protected void initView() {

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

        addContent(fragments,tabs,header,footer);

        mBinder = ButterKnife.bind(this);
        initHeader();
    }

    private void initHeader() {
        needHeaderDivider(false);
        Bitmap blur = FastBlur.smartBlur(BitmapUtils.getBitmap(this, R.drawable.default_portrait), 2, true);
        mBlur.setImageBitmap(blur);
        needHeader(false);
        mScrollTitleBar.setBackgroundResource(R.drawable.ic_home_title_bg);
        mScrollRightText.setText("关注");
        mScrollRightText.setTextColor(getResources().getColor(R.color.white));
        mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_white, 0, 0, 0);
        mScrollRightText.setBackgroundResource(R.drawable.white_stoke_bg);
        int paddingv = getResources().getDimensionPixelSize(R.dimen.px10);
        int paddingh = getResources().getDimensionPixelSize(R.dimen.px15);
        mScrollRightText.setPadding(paddingh,paddingv,paddingh,paddingv);
        mScrollRightText.setCompoundDrawablePadding(paddingh);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) mScrollRightText.getLayoutParams();
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
    protected int getTitleHeight() {
        return mScrollTitleBar.getHeight();
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
    public void onScrollY(int y) {
        if (y > mBlur.getHeight()){
            mScrollTitleBar.setBackgroundColor(getResources().getColor(R.color.white));
            mScrollRightText.setTextColor(getResources().getColor(R.color.font_orange));
            mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_plus, 0, 0, 0);
            mScrollRightText.setBackgroundResource(R.drawable.orange_stoke_bg);
            mScrollMiddleText.setText("林妙可");
            needHeaderDivider(true);
        }else {
            mScrollTitleBar.setBackgroundResource(R.drawable.ic_home_title_bg);
            mScrollRightText.setTextColor(getResources().getColor(R.color.white));
            mScrollRightText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_white, 0, 0, 0);
            mScrollRightText.setBackgroundResource(R.drawable.white_stoke_bg);
            mScrollMiddleText.setText("");
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
