package cn.xiaojs.xma.common.pageload.trigger;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import cn.xiaojs.xma.common.pageload.ILoadingState;
import cn.xiaojs.xma.common.pageload.PageChangeListener;

/**
 * Created by Paul Z on 2017/5/17.
 */

public class PageChangeInRecyclerView extends RecyclerView.OnScrollListener implements ILoadingState{

    RecyclerView mListView;
    PageChangeListener loader;

    private boolean isLoading;

    public PageChangeInRecyclerView(RecyclerView listView, PageChangeListener loader) {
        mListView=listView;
        mListView.addOnScrollListener(this);
        this.loader=loader;
    }


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if(newState==RecyclerView.SCROLL_STATE_IDLE){

            if(!isLoading&&isToBottom()){
                loader.next();
//                isLoading=true;
            }
            if(!isLoading&&isToTop()){
                loader.previous();
            }
        }
    }

    private boolean isToBottom(){
        final RecyclerView.Adapter adapter = mListView.getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {

            return true;

        }else if(mListView.computeVerticalScrollExtent() + mListView.computeVerticalScrollOffset() >= mListView.computeVerticalScrollRange()){
            return true;
        }
        return false;
    }

    private boolean isToTop(){
        final RecyclerView.Adapter adapter = mListView.getAdapter();

        // 如果未设置Adapter或者Adapter没有数据可以下拉刷新
        if (null == adapter || adapter.getItemCount() == 0) {

            return true;

        } else {
            // 第一个条目完全展示,可以刷新
            if (getFirstVisiblePosition() == 0) {
                return mListView.getChildAt(0).getTop() >= 0;
            }
        }

        return false;
    }

    private int getFirstVisiblePosition() {
        View firstVisibleChild = mListView.getChildAt(0);
        return firstVisibleChild != null ? mListView.getChildAdapterPosition(firstVisibleChild) : -1;
    }


    @Override
    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void completeLoading() {
        isLoading=false;
    }
}
