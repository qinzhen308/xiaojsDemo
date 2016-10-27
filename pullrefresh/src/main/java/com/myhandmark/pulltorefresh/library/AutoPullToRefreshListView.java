package com.myhandmark.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;


import java.lang.reflect.Field;


/**
 * Created by cui on 2016/5/31.
 */
public class AutoPullToRefreshListView extends MPullToRefreshListView {
    private AbsListView.OnScrollListener listener;
    private AutoRefreshListner autoRefreshListner;
    private boolean autoRefresh = true;
    public AutoPullToRefreshListView(Context context) {
        super(context);
        setAutoRefreshListner(null);
    }

    public AutoPullToRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAutoRefreshListner(null);
    }

    public AutoPullToRefreshListView(Context context, Mode mode) {
        super(context, mode);
        setAutoRefreshListner(null);

    }

    public AutoPullToRefreshListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
        setAutoRefreshListner(null);
    }

    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
    }

    public void setAutoRefreshListner(AutoRefreshListner autoRefreshListner) {
        this.autoRefreshListner = autoRefreshListner;
        if (listener == null) {
            listener = new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    if(!autoRefresh)
                        return;
                    switch (scrollState) {
                        // 当不滚动时
                        case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                            // 判断滚动到底部
                            if (view.getCount() > 6) {
                                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                                    if (AutoPullToRefreshListView.this.autoRefreshListner == null) {
                                        try {
                                            Field lis = getClass().getDeclaredField("mOnRefreshListener2");
                                            if(lis!= null){
                                                lis.setAccessible(true);
                                                OnRefreshListener2 listener2 = (OnRefreshListener2) lis.get(AutoPullToRefreshListView.this);
                                                if(listener2!= null){
                                                    listener2.onPullUpToRefresh(AutoPullToRefreshListView.this);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {


                                        AutoPullToRefreshListView.this.autoRefreshListner.onRefresh(view);
                                    }
                                }
                            }

                            break;
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {

                }
            }

            ;
        }
        setOnScrollListener(listener);
    }


}
