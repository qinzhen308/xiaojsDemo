package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.orhanobut.logger.Logger;

/**
 * Created by Paul Z on 2017/5/25.
 */

public class PageChangeListener extends RecyclerView.OnScrollListener {


    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        if(newState==RecyclerView.SCROLL_STATE_IDLE){

            if(isToBottom(recyclerView)){
                Logger.d("-----qz-----recyclerview--scroll to bottom");
            }
        }
    }

    private boolean isToBottom(RecyclerView recyclerView){
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if (null == adapter || adapter.getItemCount() == 0) {

            return true;

        }else if(recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange()){
            return true;
        }
        return false;
    }

    private boolean isToTop(RecyclerView recyclerView){
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();

        // 如果未设置Adapter或者Adapter没有数据可以下拉刷新
        if (null == adapter || adapter.getItemCount() == 0) {

            return true;

        } else {
            // 第一个条目完全展示,可以刷新
            if (getFirstVisiblePosition(recyclerView) == 0) {
                return recyclerView.getChildAt(0).getTop() >= 0;
            }
        }

        return false;
    }

    private int getFirstVisiblePosition(RecyclerView recyclerView) {
        View firstVisibleChild = recyclerView.getChildAt(0);
        return firstVisibleChild != null ? recyclerView.getChildAdapterPosition(firstVisibleChild) : -1;
    }

//    public abstract void onNext();


}