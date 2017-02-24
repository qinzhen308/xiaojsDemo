package cn.xiaojs.xma.common.pulltorefresh;
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
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.pulltorefresh.stickylistheaders.StickyListHeadersAdapter;
import cn.xiaojs.xma.common.pulltorefresh.stickylistheaders.StickyListHeadersListView;
import cn.xiaojs.xma.common.pulltorefresh.swipelistview.BaseSwipeListViewListener;
import cn.xiaojs.xma.common.pulltorefresh.swipelistview.SwipeListView;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.DeviceUtil;

/**
 * @param <B> bean对象
 * @param <H> item的holder
 */
public abstract class AbsSwipeAdapter<B, H extends BaseHolder> extends BaseAdapter implements StickyListHeadersAdapter {
    /**
     * 下拉刷新, 重新请求数据; 底部上拉刷新, 加载更多
     */
    public final static int MODE_DOWN_REFRESH_MORE = 0;
    /**
     * 下拉刷新, 加载更多; 底部上拉刷新, 重新请求数据
     */
    public final static int MODE_UP_REFRESH_MORE = 1;

    /**
     * 初始状态
     */
    private final int STATE_NORMAL = 110;
    /**
     * 下拉刷新
     */
    private final int STATE_UP_REFRESH = 100;
    /**
     * 上拉加载
     */
    private final int STATE_DOWN_REFRESH = 101;
    /**
     * 下拉加载数据异常
     */
    private final int STATE_UP_ERROR = 102;
    /**
     * 上拉加载数据异常
     */
    private final int STATE_DOWN_ERROR = 103;
    /**
     * 所有数据为空
     */
    private final int STATE_ALL_EMPTY = 104;
    /**
     * 单次加载数据为空
     */
    protected final int STATE_SINGLE_EMPTY = 105;
    /**
     * 获取数据参数错误
     */
    private final int STATE_PARAM_ERROR = 106;
    /**
     * 数据为空时加载数据
     */
    private final int STATE_LOADING = 107;
    /**
     * 数据为空时加载数据异常
     */
    private final int STATE_LOADING_ERROR = 108;

    private int mCurrentState = STATE_NORMAL;

    protected final int PAGE_FIRST = 1;
    protected final int PAGE_SIZE = 10;
    protected PullToRefreshSwipeListView mListView;
    protected Context mContext;
    protected LayoutInflater mInflater;
    protected List<B> mBeanList = new ArrayList<>();
    protected Pagination mPagination;
    private View mEmptyView;
    private View mFailedView;
    private boolean mRefreshOnLoad = true;
    private int mRefreshMode = MODE_DOWN_REFRESH_MORE;


    private ImageView mEmptyImage;
    private TextView mEmptyDesc;
    private TextView mEmptyDesc1;
    private Button mEmptyButton;

    private String mDesc;
    private String mDesc1;
    private String mButtonDesc;
    private int mIconResId;

    public AbsSwipeAdapter(Context context, PullToRefreshSwipeListView listView, List<B> data) {
        this(context, listView, false);
        mBeanList = data;
    }

    public AbsSwipeAdapter(Context context, PullToRefreshSwipeListView listView) {
        this(context, listView, true);
    }

    public AbsSwipeAdapter(Context context, PullToRefreshSwipeListView listView, boolean autoLoad) {
        this(context, listView, autoLoad, MODE_DOWN_REFRESH_MORE);
    }

    public AbsSwipeAdapter(Context context, PullToRefreshSwipeListView listView, int refreshMode) {
        this(context, listView, true, refreshMode);
    }

    public AbsSwipeAdapter(Context context, PullToRefreshSwipeListView listView, boolean autoLoad, int refreshMode) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mRefreshOnLoad = autoLoad;
        mRefreshMode = refreshMode;
        initParam();
        initListView(listView);
    }

    /**
     * 子类可初始化接口参数
     */
    protected void initParam() {

    }

    private void initListView(PullToRefreshSwipeListView listView) {
        if (mListView != null) {
            return;
        }
        mEmptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_empty, null);
        mEmptyDesc = (TextView) mEmptyView.findViewById(R.id.empty_desc);
        mEmptyDesc1 = (TextView) mEmptyView.findViewById(R.id.empty_desc1);
        mEmptyButton = (Button) mEmptyView.findViewById(R.id.empty_click);
        mEmptyImage = (ImageView) mEmptyView.findViewById(R.id.empty_image);

        mPagination = new Pagination();
        mPagination.setPage(PAGE_FIRST);
        mPagination.setMaxNumOfObjectsPerPage(getPageSize());
        mListView = listView;
        mListView.setMode(PullToRefreshBase.Mode.BOTH);
        if (leftSwipe()) {
            mListView.enableLeftSwipe();
        }
        final SwipeListView list = mListView.getRefreshableView().getWrappedList();
        if (list.isSwipeEnabled()) {
            list.setSwipeListViewListener(new BaseSwipeListViewListener() {
                @Override
                public void onClickFrontView(int position) {
                    int idx = position;
                    if (list.getHeaderViewsCount() > 0 || list.getFooterViewsCount() > 0) {
                        idx = position - list.getHeaderViewsCount();
                        if (idx >= 0 && idx < mBeanList.size()) {
                            onDataItemClick(idx, mBeanList.get(idx));
                        }
                    }
                }
            });
        } else {
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int idx = i;
                    if (list.getHeaderViewsCount() > 0 || list.getFooterViewsCount() > 0) {
                        idx = i - list.getHeaderViewsCount();
                    }
                    if (idx >= 0 && idx < mBeanList.size()) {
                        onDataItemClick(idx, mBeanList.get(idx));
                    }
                }
            });
        }

        if (mRefreshMode == MODE_UP_REFRESH_MORE) {
            mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<StickyListHeadersListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<StickyListHeadersListView> refreshView) {
                    mPagination.setPage(mPagination.getPage() + 1);
                    if (mCurrentState != STATE_DOWN_REFRESH) {
                        mCurrentState = STATE_DOWN_REFRESH;
                    }
                    request();
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<StickyListHeadersListView> refreshView) {
                    mPagination.setPage(PAGE_FIRST);
                    mCurrentState = STATE_UP_REFRESH;
                    request();
                }
            });
        } else {
            mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<StickyListHeadersListView>() {
                @Override
                public void onPullDownToRefresh(PullToRefreshBase<StickyListHeadersListView> refreshView) {
                    mPagination.setPage(PAGE_FIRST);
                    mCurrentState = STATE_UP_REFRESH;
                    request();
                }

                @Override
                public void onPullUpToRefresh(PullToRefreshBase<StickyListHeadersListView> refreshView) {
                    mPagination.setPage(mPagination.getPage() + 1);
                    if (mCurrentState != STATE_DOWN_REFRESH) {
                        mCurrentState = STATE_DOWN_REFRESH;
                    }
                    //Log.i("mPagination page = ",mPagination.getPage() + "");
                    request();
                }
            });
        }

        if (refreshOnLoad()) {
            request();
        }
    }

    Handler mHandler = new Handler() {

    };

    protected final void request() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mBeanList == null || mBeanList.size() == 0) {
                    changeRequestStatus(STATE_LOADING);
                }
                doRequest();
            }
        }, 500);
    }

    /**
     * 创建ListItem
     */
    protected View createItem(final int position) {
        if (mListView != null
                && mListView.getRefreshableView().isSwipeEnabled()) {
            View templateView = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_list_swipe_row, null);
            LinearLayout frontView = (LinearLayout) templateView
                    .findViewById(R.id.swipe_front);
            LinearLayout backView = (LinearLayout) templateView
                    .findViewById(R.id.swipe_back);
            FrameLayout contentFrame = (FrameLayout) frontView
                    .findViewById(R.id.content);
            // 滑动删除list item的自定义区域
            View contentView = createContentView(position);
            contentFrame.addView(contentView);
            TextView delete = (TextView) templateView.findViewById(R.id.swipe_delete);
            TextView mark = (TextView) templateView.findViewById(R.id.swipe_mark);
            setMarkListener(mark, position);
            setDeleteListener(delete, position);
            onAttachSwipe(mark, delete);
            return templateView;
        } else {
            return createContentView(position);
        }
    }

    protected void onAttachSwipe(TextView mark, TextView del) {

    }

    protected final void setLeftOffset(float width) {
        SwipeListView swipe = mListView.getRefreshableView().getWrappedList();
        swipe.setOffsetLeft(DeviceUtil.getScreenWidth(mContext) - width);
    }

    private void setDeleteListener(TextView delete, final int position) {
        if (delete != null && position >= 0) {
            // 有滑动删除时的公有逻辑
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteItem(position);
                }
            });
        }
    }

    private void setMarkListener(TextView mark, final int position) {
        if (mark != null && position >= 0) {
            // 滑动标记为已读
            mark.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onSwipeMark(position);
                }
            });
        }
    }

    public final void deleteItem(final int position) {
        final SwipeListView list = mListView.getRefreshableView()
                .getWrappedList();
        list.closeOpenedItems();
        list.dismiss(position, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                onSwipeDelete(position);
            }
        });
    }

    public void onSwipeMark(int position) {

    }

    protected void onSwipeDelete(int position) {

    }

    /**
     * 改变状态
     */
    protected void changeRequestStatus(int statusLoading) {
        mCurrentState = statusLoading;
        switch (mCurrentState) {
            case STATE_NORMAL:
                mListView.onRefreshComplete();
                mListView.removeEmptyView(mEmptyView);
                mListView.removeEmptyView(mFailedView);
                if (mListView.getMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    mListView.setMode(PullToRefreshBase.Mode.BOTH);
                }
                break;
            case STATE_LOADING:
                mListView.setFirstLoading(true);
                mListView.setRefreshing();
                break;
            case STATE_LOADING_ERROR:
                mListView.onRefreshComplete();
                mCurrentState = STATE_NORMAL;
                addFailedView();
                mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                break;
            case STATE_PARAM_ERROR:
                mListView.onRefreshComplete();
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_UP_REFRESH:
                mListView.setRefreshing();
                break;
            case STATE_DOWN_REFRESH:

                break;
            case STATE_UP_ERROR:
                mListView.onRefreshComplete();
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_DOWN_ERROR:
                if (mPagination.getPage() < 1) {
                    mPagination.setPage(PAGE_FIRST);
                }
                mListView.onRefreshComplete();
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_ALL_EMPTY:
                mListView.onRefreshComplete();
                mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                addEmptyView();
                mCurrentState = STATE_NORMAL;
                break;
            case STATE_SINGLE_EMPTY:
                mListView.onRefreshComplete();
                mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                if (mBeanList == null || mBeanList.size() == 0) {
                    addEmptyView();
                }
                break;
            default:
                break;
        }
    }

    public void reset() {
        mBeanList.clear();
        mPagination.setPage(PAGE_FIRST);
        mCurrentState = STATE_NORMAL;
    }

    /**
     * 此方法模拟下拉刷新，回调onPullDownToRefresh的逻辑
     */
    public void doRefresh() {
        changeRequestStatus(STATE_UP_REFRESH);
    }

    protected void onDataItemClick(int position, B bean) {

    }

    public boolean refreshOnLoad() {
        return mRefreshOnLoad;
    }

    @Override
    public int getCount() {
        //当数据为空且不需要显示空view的时候，增加一个自定义view到列表确保header能够正常显示与操作
        if (mBeanList.size() == 0 && patchedHeader() && !showEmptyView()) {
            return 1;
        }
        return mBeanList.size() == 0 ? 0 : mBeanList.size();
    }

    @Override
    public B getItem(int i) {
        if (i >= 0 && mBeanList.size() > i) {
            return mBeanList.get(i);
        }
        return null;
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
        if (mBeanList.size() == 0) {
            addEmptyView();
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (mBeanList.size() > 0) {
            H holder = null;
            if (view == null) {
                view = createItem(i);
                holder = initHolder(view);
                view.setTag(holder);
            } else {
                holder = (H) view.getTag();
                TextView delete = (TextView) view.findViewById(R.id.swipe_delete);
                TextView mark = (TextView) view.findViewById(R.id.swipe_mark);
                setMarkListener(mark, i);
                setDeleteListener(delete, i);
            }
            if (holder == null) {//view可能会传成下方的占位view
                view = createItem(i);
                holder = initHolder(view);
                view.setTag(holder);
            }
            setViewContent(holder, getItem(i), i);
            return view;
        } else {//解决加了header后，header高度超过1屏无法下拉
            View v = new View(mContext);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            v.setBackgroundResource(android.R.color.transparent);
            v.setLayoutParams(lp);
            return v;
        }

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
    public void refresh() {
        mPagination.setPage(1);
        doRequest();
    }

    protected final void onSuccess(List<B> data) {
        if (STATE_UP_REFRESH == mCurrentState || mPagination.getPage() == PAGE_FIRST) {
            mBeanList.clear();
            notifyDataSetChanged();
        }
        if (data == null || data.size() == 0) {
            mPagination.setPage(mPagination.getPage() - 1);
            if (mBeanList.isEmpty()) {//接口数据为空，本地数据也为空，则显示空视图
                changeRequestStatus(STATE_ALL_EMPTY);
            } else {
                changeRequestStatus(STATE_SINGLE_EMPTY);
            }
            return;
        }
        if (!data.isEmpty()) {
            mBeanList.addAll(data);
            notifyDataSetChanged();
        }

        if (mBeanList == null || mBeanList.size() == 0) {
            changeRequestStatus(STATE_ALL_EMPTY);
        } else {
            changeRequestStatus(STATE_NORMAL);
        }
    }

    protected final void onFailure(String errorCode, String msg) {
        mPagination.setPage(mPagination.getPage() - 1);
        if (mCurrentState == STATE_DOWN_REFRESH) {
            changeRequestStatus(STATE_DOWN_ERROR);
        } else if (mCurrentState == STATE_UP_REFRESH) {
            changeRequestStatus(STATE_UP_ERROR);
        } else {
            changeRequestStatus(STATE_LOADING_ERROR);
        }
    }

    public void showProgress(boolean cancelable) {
        ((BaseActivity) mContext).showProgress(cancelable);
    }

    public void cancelProgress() {
        ((BaseActivity) mContext).cancelProgress();
    }

    public void setDesc(String desc) {
        mDesc = desc;
    }

    public void setDesc1(String desc) {
        mDesc1 = desc;
    }

    public void setIcon(int resId) {
        mIconResId = resId;
    }

    public void setButtonDesc(String desc) {
        mButtonDesc = desc;
    }

    private void addEmptyView() {
        if (showEmptyView()) {
            if (!TextUtils.isEmpty(mDesc)) {
                mEmptyDesc.setVisibility(View.VISIBLE);
                mEmptyDesc.setText(mDesc);
            } else {
                mEmptyDesc.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mDesc1)) {
                mEmptyDesc1.setVisibility(View.VISIBLE);
                mEmptyDesc1.setText(mDesc1);
            } else {
                mEmptyDesc1.setVisibility(View.GONE);
            }

            if (!TextUtils.isEmpty(mButtonDesc)) {
                mEmptyButton.setVisibility(View.VISIBLE);
                mEmptyButton.setText(mButtonDesc);
                mEmptyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onEmptyButtonClick();
                    }
                });
            } else {
                mEmptyButton.setVisibility(View.GONE);
            }

            if (mIconResId > 0) {
                mEmptyImage.setImageResource(mIconResId);
            }
            mListView.removeEmptyView(mFailedView);
            mListView.setEmptyView(mEmptyView);
            onDataEmpty();
        }
    }

    protected void onEmptyButtonClick() {

    }

    private void addFailedView() {
        if (!showFailedView())
            return;
        if (mFailedView == null) {
            mFailedView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_empty, null);
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
        mListView.removeEmptyView(mEmptyView);
        mListView.setEmptyView(mFailedView);
        onDataFailed();
    }

    protected void onDataEmpty() {

    }

    protected void onDataFailed() {

    }

    public List<B> getList() {
        return mBeanList;
    }

    public void setPageNum(int pageNum) {
        mPagination.setPage(pageNum);
    }

    protected int getPageSize() {
        return PAGE_SIZE;
    }

    protected boolean showEmptyView() {
        return true;
    }

    protected boolean showFailedView() {
        return true;
    }

    /**
     * 如果顶部有header,建议返回true,以避免header超过1屏且无list数据时，不能正常向上滑动
     */
    protected boolean patchedHeader() {

        return false;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new View(mContext);
        }
        return convertView;
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    public boolean isChoiceMode() {
        return false;
    }

    /**
     * 是否支持左滑
     */
    protected boolean leftSwipe() {
        return false;
    }
}
