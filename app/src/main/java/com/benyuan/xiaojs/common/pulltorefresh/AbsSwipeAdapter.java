package com.benyuan.xiaojs.common.pulltorefresh;
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
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.model.Pagination;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;
import com.handmark.pulltorefresh.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

/**
 * @param <B> bean对象
 * @param <H> item的holder
 */
public abstract class AbsSwipeAdapter<B, H extends BaseHolder> extends BaseAdapter {

    protected final int PAGE_FIRST = 1;
    protected final int PAGE_SIZE = 20;
    private AutoPullToRefreshListView mListView;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<B> mBeanList = new ArrayList<>();
    private boolean isDown;
    protected Pagination mPagination;
    private View mEmptyView;
    protected boolean mClearItems;

    public AbsSwipeAdapter(Context context, AutoPullToRefreshListView listView) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        initParam();
        initListView(listView);
    }

    /**
     * 子类可初始化接口参数
     */
    protected void initParam(){

    }

    private void initListView(AutoPullToRefreshListView listView) {
        if (mListView != null) {
            return;
        }
        mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_empty, null);
        mPagination = new Pagination();
        mPagination.setPage(PAGE_FIRST);
        mPagination.setMaxNumOfObjectsPerPage(getPageSize());
        mListView = listView;
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onDataItemClick(adapterView, view, i, l);
            }
        });

        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                isDown = true;
                mPagination.setPage(PAGE_FIRST);
                doRequest();
            }
        });

        mListView.setOnLoadMoreListener(new AutoPullToRefreshListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mPagination.setPage(mPagination.getPage() + 1);
                isDown = false;
                doRequest();
            }
        });
        mListView.setHeaderReadyListener(new PullToRefreshBase.OnHeaderReadyListener() {
            @Override
            public void onReady() {
                if (firstRefresh()){
                    mListView.setRefreshing();
                }
            }
        });
    }

    protected void onDataItemClick(AdapterView<?> adapterView, View view, int position, long l) {

    }

    protected boolean firstRefresh(){
        return true;
    }

    @Override
    public int getCount() {
        return mBeanList.size();
    }

    @Override
    public B getItem(int i) {
        return mBeanList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void removeItem(int position) {
        if (position > mBeanList.size()) {
            return;
        }
        mBeanList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        H holder = null;
        if (view == null) {
            view = createContentView(i);
            holder = initHolder(view);
            view.setTag(holder);
        } else {
            holder = (H) view.getTag();
        }
        setViewContent(holder, getItem(i), i);
        return view;
    }

    /**
     * 设置列表item的显示
     */
    protected abstract void setViewContent(H holder, B bean, int position);

    /**
     * 加载item的布局view
     */
    protected abstract View createContentView(int position);

    /**
     * 初始化holder
     */
    protected abstract H initHolder(View view);

    /**
     * 实现retrofit访问接口
     */
    protected abstract void doRequest();

    protected final void onSuccess(List<B> data) {
        mListView.onRefreshOrLoadComplete();
        if (isDown || mClearItems) {//下拉刷新清空列表
            mBeanList.clear();
            if (mClearItems){//此次请求是否需要刷新
                mClearItems = false;
            }
        }
        if (data == null || data.size() == 0) {
            mPagination.setPage(mPagination.getPage() - 1);
            if (mBeanList.isEmpty()) {//接口数据为空，本地数据也为空，则显示空视图
                addEmptyView();
            }
            return;
        }
        if (!data.isEmpty()) {
            mBeanList.addAll(data);
            notifyDataSetChanged();
        }
    }

    protected final void onFailure(String errorCode, String msg) {
        mListView.onRefreshOrLoadComplete();
    }

    private void addEmptyView() {
        mListView.setEmptyView(mEmptyView);
        onDataEmpty();
    }

    protected void onDataEmpty() {

    }

    public void setPageNum(int pageNum) {
        mPagination.setPage(pageNum);
    }

    protected int getPageSize() {
        return PAGE_SIZE;
    }
}
