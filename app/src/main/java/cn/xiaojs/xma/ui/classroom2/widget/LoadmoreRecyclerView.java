package cn.xiaojs.xma.ui.classroom2.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.classroom2.base.LoadmoreViewHolder;
import cn.xiaojs.xma.ui.widget.LoadingView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/10/9.
 */

public class LoadmoreRecyclerView extends RecyclerView {

    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;

    private static final int LOADMORE_TYPE = -1;

    private LinearLayoutManager layoutManager;
    private boolean loadMoreEnabled;
    private LoadmoreListener loadmoreListener;


    public interface LoadmoreListener {
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
                            && ((LMAdapter)getAdapter()).getStatus() != STATE_LOADING
                            && layoutManager.findLastVisibleItemPosition() + 1 == layoutManager.getItemCount()) {

                        if (XiaojsConfig.DEBUG) {
                            Logger.w("onScrollStateChanged Loadmore");
                        }

                        if (loadmoreListener != null) {
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


    public void loadCompleted() {

        ((LMAdapter) getAdapter()).setState(STATE_COMPLETE);
    }

    public void startLoading() {
        ((LMAdapter) getAdapter()).setState(STATE_LOADING);
    }

    public static abstract class LMAdapter extends RecyclerView.Adapter<LMViewHolder> {

        private Context context;
        private LoadmoreViewHolder loadmoreViewHolder;
        private int status;

        public LMAdapter(Context context) {
            this.context = context;
            status = STATE_NOMORE;

        }

        public abstract int getDataCount();

        public abstract int getItemType(int position);

        public abstract LMViewHolder createViewholder(ViewGroup parent, int viewType);

        public abstract void bindViewholder(LMViewHolder holder, int position);


        public int getStatus() {
            return status;
        }

        @Override
        public LMViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            if (viewType == LOADMORE_TYPE) {
                View view = LoadmoreViewHolder.createView(context, parent);
                loadmoreViewHolder = new LoadmoreViewHolder(view);
                return loadmoreViewHolder;
            }

            return createViewholder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(LMViewHolder holder, int position) {

            if (holder instanceof LoadmoreViewHolder) {
                return;
            }

            bindViewholder(holder, position);
        }

        @Override
        public int getItemCount() {

//            if (status == STATE_COMPLETE) {
//                return getDataCount();
//            }

            return getDataCount() + 1;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == getDataCount()) {
                return LOADMORE_TYPE;
            }
            return getItemType(position);
        }

        public void setState(int state) {
            status = state;
            loadmoreViewHolder.setState(state);
            //notifyDataSetChanged();
        }
    }

    public static abstract class LMViewHolder extends RecyclerView.ViewHolder {
        public LMViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public static class LoadmoreViewHolder extends LMViewHolder {


        @BindView(R.id.loading_layout)
        RelativeLayout rootLayout;

        @BindView(R.id.loading_progress)
        LoadingView loadingView;

        public LoadmoreViewHolder(View itemView) {
            super(itemView);
            setState(STATE_NOMORE);
        }

        public void setState(int state) {
            switch (state) {
                case STATE_LOADING:
                    loadingView.resume();
                    loadingView.setVisibility(View.VISIBLE);
                    break;
                case STATE_COMPLETE:
                case STATE_NOMORE:
                    loadingView.pause();
                    loadingView.setVisibility(View.GONE);
                    break;
            }
        }


        public static View createView(Context context, ViewGroup parent) {

            LayoutInflater inflater = LayoutInflater.from(context);
            return inflater.inflate(R.layout.layout_base2_recyclerview_loadmore, parent, false);
        }

    }

}
