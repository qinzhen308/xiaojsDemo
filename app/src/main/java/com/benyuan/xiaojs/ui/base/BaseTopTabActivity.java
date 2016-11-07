package com.benyuan.xiaojs.ui.base;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/2
 * Desc:
 *
 * ======================================================================================== */

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.LazyViewPager;
import com.benyuan.xiaojs.ui.widget.TabIndicatorView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/2
 * Desc: 顶部带有tab的页面切换
 *
 * ======================================================================================== */

public abstract class BaseTopTabActivity extends BaseActivity implements View.OnClickListener{
    private final static String TAG = "BaseTabFragment";

    protected final static int TAB_INDICATOR_DIVIDE_EQUAL = 1;
    protected final static int TAB_INDICATOR_CENTER = 2;
    protected final static int TAB_INDICATOR_LEFT = 3;

    @BindView(R.id.base_fragment_content)
    RelativeLayout mBaseFragContent;
    @BindView(R.id.tab_fixed_view)
    LinearLayout mTabFixedView;
    @BindView(R.id.tab_scroller)
    TabIndicatorView mTabIndicator;
    @BindView(R.id.view_pager)
    protected LazyViewPager mViewPager;
    @BindView(R.id.hover_container)
    RelativeLayout mHoverContainer;

    private int mIndicatorStyle = TAB_INDICATOR_DIVIDE_EQUAL;
    private List<String> mTabTitles;
    private List<TextView> mTabTitleView;
    private List<? extends Fragment> mFragments;
    private TabFragmentPagerAdapter mAdapter;
    private int mPosition;
    private boolean mScrollable = true;

    @Override
    protected void addViewContent() {
        addView(R.layout.fragment_base_tab);
        initView();
    }

    protected abstract void initView();

    protected void setIndicatorStyle(int indicatorStyle) {
        mIndicatorStyle = indicatorStyle;
    }

    protected void setScrollable(boolean scrollable) {
        mViewPager.setScrollState(scrollable);
        mScrollable = scrollable;
    }

    protected void addViews(int[] resIds, List<? extends Fragment> fragments) {
        if (resIds == null || fragments == null) {
            return;
        }
        List<String> tabTitles = new ArrayList<String>();
        for (int i = 0; i < resIds.length; i++) {
            tabTitles.add(getString(resIds[i]));
        }
        addViews(tabTitles, null, fragments);
    }

    protected void addViews(List<String> tabTitles, List<? extends Fragment> fragments) {
        addViews(tabTitles, null, fragments);
    }

    protected void addViews(List<String> tabTitles, View fixedView, List<? extends Fragment> fragments) {
        if (tabTitles == null || tabTitles == null) {
            Logger.w(TAG, "Not arguments passed!");
            return;
        }

        if (tabTitles != null && fragments != null && tabTitles.size() != fragments.size()) {
            throw new IllegalArgumentException("Base tab fragment, illegal arguments passed!");
        }

        mTabTitles = tabTitles;
        mFragments = fragments;

        createTabs(tabTitles);

        if (fixedView != null) {
            mTabFixedView.addView(fixedView, mTabFixedView.getChildCount());
        }

        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setScrollState(mScrollable);
        mViewPager.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mTabIndicator != null && mTabIndicator.getTabScroller() != null) {
                    mTabIndicator.getTabScroller().onTabScrolled(position, positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                updateTabTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    protected void addHover(List<String> tabTitles, View hover, List<? extends Fragment> fragments){
        if (tabTitles == null || tabTitles == null) {
            Logger.w(TAG, "Not arguments passed!");
            return;
        }

        if (tabTitles != null && fragments != null && tabTitles.size() != fragments.size()) {
            throw new IllegalArgumentException("Base tab fragment, illegal arguments passed!");
        }

        mTabTitles = tabTitles;
        mFragments = fragments;

        createTabs(tabTitles);

        if (hover != null) {
            //mHoverContainer.addView(hover);
            mHoverContainer.addView(hover,new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        }

        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setScrollState(mScrollable);
        mViewPager.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (mTabIndicator != null && mTabIndicator.getTabScroller() != null) {
                    mTabIndicator.getTabScroller().onTabScrolled(position, positionOffset);
                }
            }

            @Override
            public void onPageSelected(int position) {
                mPosition = position;
                updateTabTextColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void createTabs(List<String> tabTitles) {
        int i = 0;
        mTabTitleView = new ArrayList<TextView>();
        for (String title : tabTitles) {
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.tab_indicatior_item, null);
            tv.setText(title);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            switch (mIndicatorStyle) {
                case TAB_INDICATOR_DIVIDE_EQUAL:
                    params.weight = 1;
                case TAB_INDICATOR_CENTER:
                    params.gravity = Gravity.CENTER;
                    break;
            }

            mTabIndicator.addView(tv, params);
            mTabIndicator.getTabScroller().addTabView(tv);
            mTabIndicator.getTabScroller().setAlignTextBottom(false);
            tv.setOnClickListener(this);
            tv.setId(i++);
            mTabTitleView.add(tv);
        }
        mTabIndicator.getTabScroller().setAlignTextBottom(true);
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            mPosition = v.getId();
            mViewPager.setCurrentItem(mPosition);
            updateTabTextColor(mPosition);
        }
    }

    private void updateTabTextColor(int position) {
        if (mTabIndicator == null) {
            return;
        }

        Resources resources = getResources();
        for (TextView v : mTabTitleView) {
            int vPos = v.getId();
            v.setTextColor(resources.getColor(position == vPos ? R.color.main_orange : android.R.color.black));
        }
    }
}
