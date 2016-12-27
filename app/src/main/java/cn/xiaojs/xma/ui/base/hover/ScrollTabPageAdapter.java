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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.util.SparseArrayCompat;
import android.view.View;

import java.util.List;

public class ScrollTabPageAdapter extends FragmentPagerAdapter {

    private List<BaseScrollTabFragment> fragments;
    private String[] tabs;
    private SparseArrayCompat<ScrollTabHolder> mScrollTabHolders;
    private View header;
    private ScrollTabHolder mListener;

    public void setTabHolderScrollingListener(ScrollTabHolder listener) {
        mListener = listener;
    }

    public ScrollTabPageAdapter(FragmentManager fm, List<BaseScrollTabFragment> fragments, String[] tabs, View header) {
        super(fm);
        this.fragments = fragments;
        this.tabs = tabs;
        mScrollTabHolders = new SparseArrayCompat<>();
        this.header = header;
    }

    @Override
    public Fragment getItem(int position) {
        BaseScrollTabFragment fragment = fragments.get(position);
        mScrollTabHolders.put(position, fragment);
        fragment.setScrollTabHolder(mListener);
        fragment.setHeader(header);
        return fragment;
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabs == null ? "" : tabs[position];
    }

    public SparseArrayCompat<ScrollTabHolder> getScrollTabHolders(){
        return mScrollTabHolders;
    }
}
