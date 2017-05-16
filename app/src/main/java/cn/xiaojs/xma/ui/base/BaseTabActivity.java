package cn.xiaojs.xma.ui.base;

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
 * Date:2016/10/11
 * Desc:底部带TAB的Activity
 *
 * ======================================================================================== */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.ui.widget.GooeyMenu;
import cn.xiaojs.xma.ui.widget.LazyViewPager;
import cn.xiaojs.xma.ui.widget.RedTipTextView;

public abstract class BaseTabActivity extends BaseActivity {

    @BindView(R.id.tab_fragment_content)
    LazyViewPager mViewPager;
    @BindView(R.id.tab_menu)
    LinearLayout mMenu;
    @BindView(R.id.gooey_menu)
    GooeyMenu mGooeyMenu;
    @BindView(R.id.tab_cover)
    RelativeLayout mCover;
    @BindView(R.id.home_center_image)
    ImageView mCenter;

    FragmentPagerAdapter mAdapter;
    private int[] mTitles;
    private int[] mDrawables;
    private List<RedTipTextView> mTabs;
    private int mCurrentIndex;
    Animation mRotateRight;
    Animation mRotateLeft;

    private int mButtonType;
    protected static final int BUTTON_TYPE_CENTER = 1;
    protected static final int BUTTON_TYPE_NONE = 2;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_base_tab);
        initView();
    }

    protected abstract void initView();

    protected void addViews(int[] tabTxt, int[] drawables, List<? extends Fragment> fragments) {
        mTitles = tabTxt;
        mDrawables = drawables;
        createTabs();
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setScrollState(false);
        mViewPager.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    protected void setButtonType(int type) {
        this.mButtonType = type;
    }

    private void createTabs() {
        LayoutInflater inflater = getLayoutInflater();
        mTabs = new ArrayList<>();
        for (int i = 0; i < mTitles.length; i++) {
            FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.layout_base_tab_item, null);
            RedTipTextView text = (RedTipTextView) fl.findViewById(R.id.base_tab_text);
            text.setText(mTitles[i]);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            if (mDrawables != null && mDrawables.length > i) {
                text.setCompoundDrawablesWithIntrinsicBounds(0,
                        mDrawables[i], 0, 0);
            }
            text.setOnClickListener(new OnTabClick(i));
            mTabs.add(text);
            mMenu.addView(fl, lp);
        }
        if (mButtonType == BUTTON_TYPE_CENTER) {
            showCenterView();
        }
        setTabSelected(0);
    }

    private void showCenterView() {
        FrameLayout fl = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_base_tab_item, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        fl.setLayoutParams(lp);
        mMenu.addView(fl, mTitles.length / 2);
        mGooeyMenu.setVisibility(View.VISIBLE);
        mCenter.setVisibility(View.VISIBLE);
        mRotateRight = AnimationUtils.loadAnimation(BaseTabActivity.this,R.anim.center_rotate_right);
        mRotateLeft = AnimationUtils.loadAnimation(BaseTabActivity.this,R.anim.center_rotate_left);
        mRotateRight.setFillAfter(true);
        mRotateLeft.setFillAfter(true);
        mGooeyMenu.setOnMenuListener(new GooeyMenu.GooeyMenuInterface() {
            @Override
            public void menuOpen() {
                mCover.setVisibility(View.VISIBLE);
                mCenter.clearAnimation();
                mCenter.startAnimation(mRotateRight);
            }

            @Override
            public void menuClose() {
                mCover.setVisibility(View.GONE);
                mCenter.clearAnimation();
                mCenter.startAnimation(mRotateLeft);
            }

            @Override
            public void menuItemClicked(int menuNumber) {
                mGooeyMenu.close();
                onGooeyMenuClick(menuNumber);
            }
        });
        mCover.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                mGooeyMenu.close();
                return true;
            }
        });
    }

    protected void onGooeyMenuClick(int position){

    }

    protected void setTabSelected(int position) {
        for (TextView t : mTabs) {
            t.setSelected(false);
        }
        mTabs.get(position).setSelected(true);
        mViewPager.setCurrentItem(position);
        mCurrentIndex = position;
        onTabClick(position);

//        if (position == 2) {
//            hiddenMessageTips();
//            XiaojsConfig.CURRENT_PAGE_IN_MESSAGE = true;
//            DataManager.setHasMessage(BaseTabActivity.this,false);
//        }else {
//            XiaojsConfig.CURRENT_PAGE_IN_MESSAGE = false;
//        }

    }

    protected void onTabClick(int position) {
        autoClose();
    }

    protected void autoClose(){
        if (mGooeyMenu.opened()){
            mGooeyMenu.close();
        }
    }

    protected final void setTip(int position, boolean enable) {
        if (position < mTabs.size()) {
            mTabs.get(position).setTipEnable(enable);
        }
    }

    public Fragment getCurrentFragment() {
        return mAdapter.getItem(mCurrentIndex);
    }

    public Fragment getFragment(int position) {
        return mAdapter.getItem(position);
    }

    public Fragment getFragmentByPosition(int position) {
        return mAdapter.getItem(position);
    }

    private class OnTabClick implements View.OnClickListener {

        private int index;

        public OnTabClick(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            setTabSelected(index);

        }
    }

    @Override
    protected void onDestroy() {
        if (mRotateRight != null){
            mRotateRight.cancel();
            mRotateRight = null;
        }
        if (mRotateLeft != null){
            mRotateLeft.cancel();
            mRotateLeft = null;
        }
        super.onDestroy();
    }

    public void showMessageTips() {

        if (mTabs == null) return;

        RedTipTextView redTipTextView = mTabs.get(2);

        if (!redTipTextView.mTipEnable) {
            redTipTextView.setTipEnable(true);
        }


    }

    public void hiddenMessageTips() {

        if (mTabs == null) return;

        RedTipTextView redTipTextView = mTabs.get(2);
        if (redTipTextView.mTipEnable) {
            redTipTextView.setTipEnable(false);
        }
    }
}
