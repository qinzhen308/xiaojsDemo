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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.home.OnScrollYListener;

public abstract class BaseScrollTabFragment extends Fragment implements ScrollTabHolder{

    private View mHeader;

    private int mHeaderHeight;

    protected ScrollTabHolder scrollTabHolder;

    protected PullToRefreshSwipeListView mList;
    private int mPagePosition;

    private View placeHolderView;
    private OnScrollYListener mYListener;

    public void setHeader(View header){
        mHeader = header;
    }

    public void setScrollTabHolder(ScrollTabHolder scrollTabHolder) {
        this.scrollTabHolder = scrollTabHolder;
    }

    public void setPagePosition(int pagePosition){
        this.mPagePosition = pagePosition;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_base_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mHeader != null) {
            mHeader.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    mHeaderHeight = mHeader.getHeight();
                    initHeader(mHeaderHeight);
                    if (Build.VERSION.SDK_INT > 15){
                        mHeader.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }else {
                        mHeader.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
            });
        }
    }

    private void initViews() {
        mList = (PullToRefreshSwipeListView) getView().findViewById(R.id.scroll_tab_base_list);
        mYListener = new OnScrollYListener() {
            @Override
            public void onScrollY(int y) {
                ((BaseScrollTabActivity)getActivity()).onScrollY(y);
            }
        };
        mYListener.setListView(mList.getRefreshableView().getWrappedList());
        mList.setOnHeaderScrollListener(new PullToRefreshSwipeListView.OnHeaderScrollListener() {
            @Override
            public void onHeaderScroll(boolean isRefreashing, boolean isTop, int value) {
                if (scrollTabHolder != null && isTop){
                    scrollTabHolder.onHeaderScroll(isRefreashing,value,mPagePosition);
                }
            }
        });
        mList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (scrollTabHolder != null) {
                    scrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPagePosition);
                }
                if (mYListener != null){
                    mYListener.onScroll(view,firstVisibleItem,visibleItemCount,totalItemCount);
                }
            }
        });

    }

    protected abstract void initData();

    protected void initHeader(int h){
        initViews();
        placeHolderView = new LinearLayout(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, h);
        placeHolderView.setLayoutParams(params);
        placeHolderView.setBackgroundColor(getResources().getColor(R.color.trans));
        mList.getRefreshableView().addHeaderView(placeHolderView);
        initData();
    }

    // PullToRefreshListView 滚动调整
    @Override
    public void adjustScroll(int scrollHeight) {
        if (scrollHeight == 0 && mList.getRefreshableView().getFirstVisiblePosition() >= 1) {
            return;
        }
        Logger.i("adjustScroll " + scrollHeight);
        mList.getRefreshableView().setSelectionFromTop(1, scrollHeight);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        Logger.i("onScroll fragment");
    }

    @Override
    public void onHeaderScroll(boolean isRefreashing, int value, int pagePosition) {

    }
}
