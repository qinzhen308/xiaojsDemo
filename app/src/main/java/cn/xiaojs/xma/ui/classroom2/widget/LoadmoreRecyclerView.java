package cn.xiaojs.xma.ui.classroom2.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;

/**
 * Created by maxiaobao on 2017/10/9.
 */

public class LoadmoreRecyclerView extends RecyclerView{

    private final int VISIBLE_THRESHOLD = 1;

    private LinearLayoutManager layoutManager;
    private boolean loadMoreEnabled;
    private LoadmoreListener loadmoreListener;


    public interface LoadmoreListener{
        void onLoadMore();
    }


    public LoadmoreRecyclerView(Context context) {
        super(context);
        init();
    }

    public LoadmoreRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoadmoreRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
        loadMoreEnabled = true;
    }

    public void setLoadmoreListener(LoadmoreListener listener) {
        loadmoreListener = listener;

        layoutManager = (LinearLayoutManager) getLayoutManager();

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

//                if (canLoadMore() && dy >= 0) {
//                    int totalItemCount = layoutManager.getItemCount();
//                    int lastVisibleItem = layoutManager.findLastVisibleItemPosition();
//                    if (lastVisibleItem + VISIBLE_THRESHOLD >= totalItemCount) {
//
//                        if (XiaojsConfig.DEBUG) {
//                            Logger.w("onScrolled Loadmore");
//                        }
//
//                        if (loadmoreListener !=null) {
//                            loadmoreListener.onLoadMore();
//                        }
//
//                    }
//                }


            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (canLoadMore()
                            && layoutManager.findLastVisibleItemPosition() + 1 == layoutManager.getItemCount()) {

                        if (XiaojsConfig.DEBUG) {
                            Logger.w("onScrollStateChanged Loadmore");
                        }

                        if (loadmoreListener !=null) {
                            loadmoreListener.onLoadMore();
                        }
                    }
                }
            }
        });

    }

    public void setLoadMoreEnabled(boolean enabled) {
        this.loadMoreEnabled = enabled;
    }

    private boolean canLoadMore() {
        return loadMoreEnabled;
    }




}
