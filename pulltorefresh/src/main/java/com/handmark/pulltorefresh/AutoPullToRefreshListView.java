package com.handmark.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by maxiaobao on 2016/11/2.
 */

public class AutoPullToRefreshListView extends PullToRefreshListView implements PullToRefreshBase.OnLastItemVisibleListener{

    private boolean loading = false;
    private OnLoadMoreListener loadMoreListener;
    private boolean isDisableLoadMore = false;

    public AutoPullToRefreshListView(Context context) {
        super(context);
        init();
    }

    public AutoPullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AutoPullToRefreshListView(Context context, Mode mode) {
        super(context, mode);
        init();
    }

    public AutoPullToRefreshListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
        init();
    }

    private void init() {
        setOnLastItemVisibleListener(this);
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {

        this.loadMoreListener = listener;

    }

    public void onRefreshOrLoadComplete() {

        onRefreshComplete();

        hiddenFooterLoading();

        loading = false;

    }

    public void setDisableLoadMore(boolean disable){
        isDisableLoadMore = disable;
    }


    @Override
    public void onLastItemVisible() {


        if(isDisableLoadMore)
            return;


        if(!loading && loadMoreListener!=null){

            loading = true;

            showFooterLoading();

            loadMoreListener.onLoadMore();


        }

    }




    public static interface OnLoadMoreListener{
        public void onLoadMore();
    }
}
