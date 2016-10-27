package com.myhandmark.pulltorefresh.library;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;


import java.lang.reflect.Field;


/**
 * Created by cui on 2016/6/2.
 */
public class AutoPullToRefreshGridView extends MPullToRefreshGridView {
    private AutoRefreshListner autoRefreshListner;
    private boolean autoRefresh = true;
    private AbsListView.OnScrollListener listener;

    public AutoPullToRefreshGridView(Context context) {
        super(context);
//        setAutoRefreshListner(null);

    }

    public AutoPullToRefreshGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        setAutoRefreshListner(null);

    }

    public AutoPullToRefreshGridView(Context context, Mode mode) {
        super(context, mode);
//        setAutoRefreshListner(null);

    }

    public AutoPullToRefreshGridView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
//        setAutoRefreshListner(null);

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
                            if (view.getCount() > 2) {
                                if (view.getLastVisiblePosition() == view.getCount() - 1) {
                                    if (AutoPullToRefreshGridView.this.autoRefreshListner == null) {
                                        try {
                                            Field lis = getClass().getDeclaredField("mOnRefreshListener2");
                                            if(lis!= null){
                                                lis.setAccessible(true);
                                                OnRefreshListener2 listener2 = (OnRefreshListener2) lis.get(AutoPullToRefreshGridView.this);
                                                if(listener2!= null){
                                                    listener2.onPullUpToRefresh(AutoPullToRefreshGridView.this);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        AutoPullToRefreshGridView.this.autoRefreshListner.onRefresh(view);
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
