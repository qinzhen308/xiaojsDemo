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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
    protected final int PAGE_SIZE = 10;
    private AutoPullToRefreshListView mListView;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<B> mBeanList = new ArrayList<>();
    private boolean isDown;
    protected Pagination mPagination;
    private View mEmptyView;
    private View mFailedView;
    protected boolean mClearItems;
    private boolean mRefreshOnLoad = true;
    private final int PLACE_HOLDER_ID = 0x12;

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
                int headerCount = mListView.getRefreshableView().getHeaderViewsCount();//有header的话要减去header
                int idx = i - headerCount;
                if (idx >= 0 && idx < mBeanList.size()){
                    onDataItemClick(idx,mBeanList.get(idx));
                }
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
                if (refreshOnLoad()){
                    mListView.setRefreshing();
                }
            }
        });
    }

    protected void onDataItemClick(int position,B bean) {

    }

    public void setRefreshOnLoad(boolean b){
        mRefreshOnLoad = b;
    }

    public boolean refreshOnLoad(){
        return mRefreshOnLoad;
    }

    @Override
    public int getCount() {
        return mBeanList.size() == 0 ? 0 : mBeanList.size();
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
        if (mBeanList.size() == 0){
            addEmptyView();
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
//        if (mBeanList.size() > 0){
            H holder = null;
            if (view == null) {
                view = createContentView(i);
                holder = initHolder(view);
                view.setTag(holder);
            } else {
                holder = (H) view.getTag();
            }
            if (holder == null){//view可能会传成下方的占位view
                view = createContentView(i);
                holder = initHolder(view);
                view.setTag(holder);
            }
            setViewContent(holder, getItem(i), i);
            return view;
//        }

//        else {//解决加了header后，header高度超过1屏无法下拉
//            View v = new View(mContext);
//            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1);
//            v.setBackgroundResource(android.R.color.transparent);
//            v.setLayoutParams(lp);
//            return v;
//        }

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

    /**
     * 模拟下拉刷新，但不显示下拉转圈，从第一页数据拉取
     */
    public void refresh(){
        mPagination.setPage(1);
        isDown = true;
        doRequest();
    }

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
            if (mBeanList.isEmpty() && showEmptyView()) {//接口数据为空，本地数据也为空，则显示空视图
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
        addFailedView();
    }

    private void addEmptyView() {
        mListView.setEmptyView(mEmptyView);
        onDataEmpty();
    }

    private void addFailedView(){
        if (mFailedView == null){
            mFailedView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_empty,null);
            TextView desc = (TextView) mFailedView.findViewById(R.id.empty_desc);
            TextView desc1 = (TextView) mFailedView.findViewById(R.id.empty_desc1);
            Button click = (Button) mFailedView.findViewById(R.id.empty_click);
            ImageView image = (ImageView) mFailedView.findViewById(R.id.empty_image);
            desc.setVisibility(View.VISIBLE);
            desc1.setVisibility(View.VISIBLE);
            click.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.ic_data_failed);
            click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListView.setRefreshing();
                }
            });
        }

        mListView.setEmptyView(mFailedView);
        onDataFailed();
    }

    protected void onDataEmpty() {

    }

    protected void onDataFailed(){

    }

    public void setPageNum(int pageNum) {
        mPagination.setPage(pageNum);
    }

    protected int getPageSize() {
        return PAGE_SIZE;
    }

    protected boolean showEmptyView(){

        return true;
    }
}
