package cn.xiaojs.xma.common.pulltorefresh.core;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Paul Z on 2017/5/8.
 */

public class PullToRefreshRecyclerView extends PullToRefreshBase<RecyclerView> {


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public PullToRefreshRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public Orientation getPullToRefreshScrollDirection() {
        return Orientation.VERTICAL;
    }

    @Override
    protected RecyclerView createRefreshableView(Context context, AttributeSet attrs) {
        RecyclerView recyclerView=new RecyclerView(context,attrs);
        return recyclerView;
    }

    @Override
    protected boolean isReadyForPullEnd() {
        return isToBottom();
    }

    @Override
    protected boolean isReadyForPullStart() {
        return isToTop();
    }

    private boolean isToBottom(){
        final RecyclerView.Adapter adapter = getRefreshableView().getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {

            return false;

        }else if(mRefreshableView.computeVerticalScrollExtent() + mRefreshableView.computeVerticalScrollOffset() >= mRefreshableView.computeVerticalScrollRange()){
            return true;
        }
        return false;
    }

    private boolean isToTop(){
        final RecyclerView.Adapter adapter = getRefreshableView().getAdapter();

        // 如果未设置Adapter或者Adapter没有数据可以下拉刷新
        if (null == adapter || adapter.getItemCount() == 0) {

            return true;

        } else {
            // 第一个条目完全展示,可以刷新
            if (getFirstVisiblePosition() == 0) {
                return mRefreshableView.getChildAt(0).getTop() >= 0;
            }
        }

        return false;
    }

    private int getFirstVisiblePosition() {
        View firstVisibleChild = mRefreshableView.getChildAt(0);
        return firstVisibleChild != null ? mRefreshableView.getChildAdapterPosition(firstVisibleChild) : -1;
    }


}