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
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.myhandmark.pulltorefresh.library.AutoPullToRefreshListView;
import com.myhandmark.pulltorefresh.library.AutoRefreshListner;
import com.myhandmark.pulltorefresh.library.PullToRefreshBase;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @param <B> bean对象
 * @param <H> item的holder
 */
public abstract class AbsSwipeAdapter<B,H extends BaseHolder> extends BaseAdapter{

    protected final int PAGE_SIZE = 10;
    private AutoPullToRefreshListView mListView;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<B> mBeanList = new ArrayList<>();
    protected int mCurrentPage = 0;
    private boolean isDown;

    public AbsSwipeAdapter(Context context,AutoPullToRefreshListView listView){
        mContext = context;
        mInflater = LayoutInflater.from(context);
        initListView(listView);
    }

    private void initListView(AutoPullToRefreshListView listView){
        if (mListView != null){
            return;
        }
        mListView = listView;
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        mListView.setAutoRefresh(true);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                mCurrentPage = 1;
                mBeanList.clear();
                notifyDataSetChanged();
                doRequest(mCurrentPage);
                mListView.onRefreshComplete();
                isDown = true;
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

        mListView.setAutoRefreshListner(new AutoRefreshListner() {
            @Override
            public void onRefresh(AbsListView view) {
                //上滑自动加载回调
                mCurrentPage++;
                mListView.showFoooter();
                doRequest(mCurrentPage);
                isDown = false;
            }
        });
        doRequest(mCurrentPage);
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

    public void removeItem(int position){
        if (position > mBeanList.size()){
            return;
        }
        mBeanList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        H holder = null;
        if (view == null){
            view = createContentView(i);
            holder = initHolder(view);
            view.setTag(holder);
        }else {
            holder = (H) view.getTag();
        }
        setViewContent(holder,getItem(i),i);
        return view;
    }

    /**
     * 设置列表item的显示
     * @param holder
     * @param bean
     * @param position
     */
    protected abstract void setViewContent(H holder,B bean,int position);

    /**
     * 加载item的布局view
     * @param position
     * @return
     */
    protected abstract View createContentView(int position);

    /**
     * 初始化holder
     * @return
     */
    protected abstract H initHolder(View view);

    /**
     * 实现retrofit访问接口
     */
    protected abstract void doRequest(int pageNo);

    protected final void onSuccess(List<B> data){
        if (isDown){
            mListView.onRefreshComplete();
        }else {
            mListView.hiddenFoooter();
        }
        if (data == null || data.size() == 0){
            mCurrentPage--;
            if (mBeanList.isEmpty()){//接口数据为空，本地数据也为空，则显示空视图
                addEmptyView();
            }
            mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            return;
        }
        if (!data.isEmpty()){
            mBeanList.addAll(data);
            notifyDataSetChanged();
        }
    }

    protected final void onFailure(String errorCode){

    }

    private void addEmptyView(){

        onDataEmpty();
    }

    protected void onDataEmpty(){

    }

}
