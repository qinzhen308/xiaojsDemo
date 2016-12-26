package cn.xiaojs.xma.ui.base.hover;
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
 * Date:2016/12/23
 * Desc:
 *
 * ======================================================================================== */

import android.os.Build;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nineoldandroids.view.ViewHelper;
import com.orhanobut.logger.Logger;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.home.OnScrollYListener;
import cn.xiaojs.xma.ui.widget.ScrollableViewPager;

public abstract class BaseScrollTabActivity extends BaseActivity implements ScrollTabHolder, ViewPager.OnPageChangeListener {

    ScrollableViewPager mPager;
    LinearLayout mHeader;
    FrameLayout mHeaderContainer;
    FrameLayout mFooterContainer;
    ScrollTabIndicator mIndicator;

    @BindView(R.id.scroll_tab_middle_view)
    protected TextView mScrollMiddleText;
    @BindView(R.id.scroll_tab_right_view)
    protected TextView mScrollRightText;
    @BindView(R.id.scroll_tab_title_bar)
    protected RelativeLayout mScrollTitleBar;
    private View mTitleDivider;

    private boolean reLocation = false;
    private int headerTop = 0;
    private int headerHeight;
    private int headerTranslationDis;

    private ScrollTabPageAdapter mAdapter;
    private int headerScrollSize = 0;

    private OnScrollYListener mYListener;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_scroll_tab_base);
        mPager = (ScrollableViewPager) findViewById(R.id.scroll_tab_pager);
        mHeader = (LinearLayout) findViewById(R.id.scroll_tab_header);
        mHeaderContainer = (FrameLayout) findViewById(R.id.scroll_tab_header_container);
        mFooterContainer = (FrameLayout) findViewById(R.id.scroll_tab_footer_container);
        mIndicator = (ScrollTabIndicator) findViewById(R.id.scroll_tab_indicator);
        mTitleDivider = findViewById(R.id.scroll_tab_title_divider);

        initView();
        mHeader.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                headerHeight = mHeader.getHeight();
                headerTranslationDis = mIndicator.getHeight() - headerHeight;
                if (Build.VERSION.SDK_INT > 15){
                    mHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }else {
                    mHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    protected void needHeaderDivider(boolean need) {
        super.needHeaderDivider(false);
        if (need){
            mTitleDivider.setVisibility(View.VISIBLE);
        }else {
            mTitleDivider.setVisibility(View.GONE);
        }
    }

    protected void setPagerScrollable(boolean scrollable) {
        mPager.setNoScroll(!scrollable);
    }

    protected abstract void initView();


    protected void addContent(List<BaseScrollTabFragment> fragments, String[] tabs, View header) {
        addContent(fragments, tabs, header, null);
    }

    protected void addContent(List<BaseScrollTabFragment> fragments, String[] tabs, View header, View footer) {
        mAdapter = new ScrollTabPageAdapter(getSupportFragmentManager(), fragments, tabs, mHeader);
        mAdapter.setTabHolderScrollingListener(this);
        mPager.setOffscreenPageLimit(fragments.size());
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(this);

        mIndicator.setShouldExpand(true);
        mIndicator.setViewPager(mPager);

        if (header != null) {
            mHeaderContainer.addView(header);
        }
        if (footer != null) {
            mFooterContainer.addView(footer);
        }
    }

    @Override
    protected boolean delayBindView() {
        return true;
    }

    // 刷新头部显示时，没有onScroll回调，只有刷新时有
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        if (mPager.getCurrentItem() != pagePosition) {
            return;
        }
        if (headerScrollSize == 0 && reLocation) {
            reLocation = false;
            return;
        }

        reLocation = false;
        final int scrollY = Math.max(-getScrollY(view), headerTranslationDis);
        Logger.i("onScroll " + scrollY + " -getScrollY(view)=" + -getScrollY(view) + " headerTranslationDis=" + headerTranslationDis);
        ViewHelper.setTranslationY(mHeader, scrollY);

    }

    /**
     * 主要算这玩意，PullToRefreshListView插入了一个刷新头部，因此要根据不同的情况计算当前的偏移量</br>
     *
     * 当刷新时： 刷新头部显示，因此偏移量要加上刷新头的数值 未刷新时： 偏移量不计算头部。
     *
     * firstVisiblePosition >1时，listview中的项开始显示，姑且认为每一项等高来计算偏移量（其实只要显示一个项，向上偏移 量已经大于头部的最大偏移量，因此不准确也没有关系）
     */
    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int top = c.getTop();
        int h = c.getMeasuredHeight();
        Logger.i("height" + h);
        int firstVisiblePosition = view.getFirstVisiblePosition();

        if (firstVisiblePosition == 0) {
            Logger.i("firstVisiblePosition = 0,top = " + top + " headerScrollSize=" + headerScrollSize);
            return -top + headerScrollSize;
        } else if (firstVisiblePosition == 1) {
            Logger.i("firstVisiblePosition = 1,top = " + top + ",headerTranslationDis = " + headerTranslationDis);
            return Math.abs(top) + headerHeight;
        } else {
            return -top + (firstVisiblePosition - 2) * c.getHeight() + headerHeight;
        }
    }

    /**
     * 与onHeadScroll互斥，不能同时执行
     */
    @Override
    public void onHeaderScroll(boolean isRefreashing, int value, int pagePosition) {
        if (mPager.getCurrentItem() != pagePosition) {
            return;
        }

        headerScrollSize = value;
        Logger.i("onHeaderScroll " + value);
        ViewHelper.setTranslationY(mHeader, -value);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        mIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
    }

    @Override
    public void onPageSelected(int position) {
        mIndicator.onPageSelected(position);
        reLocation = true;
        SparseArrayCompat<ScrollTabHolder> scrollTabHolders = mAdapter.getScrollTabHolders();
        ScrollTabHolder currentHolder = scrollTabHolders.valueAt(position);
        Logger.i("onPageSelected " + headerTop);
        Logger.i("onPageSelected mHeader.getMeasuredHeight() " + mHeader.getMeasuredHeight());
        Logger.i("onPageSelected ViewHelper.getTranslationY " + ViewHelper.getTranslationY(mHeader));
        Logger.i("onPageSelected Indicator H =  " + mIndicator.getMeasuredHeight());

        currentHolder.adjustScroll((int) (mHeader.getMeasuredHeight() + ViewHelper.getTranslationY(mHeader)));// 修正滚出去的偏移量
//        mOnChangedScroll = 0;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mIndicator.onPageScrollStateChanged(state);
    }

    @Override
    public void adjustScroll(int scrollHeight) {

    }

    public void onScrollY(int y){
        Logger.i("onScrollY = " + y);
    }
}
