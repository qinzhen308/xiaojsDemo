package cn.xiaojs.xma.ui.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/*
 *
 * 功能描述：多tab的List适配器 请继承该适配器
 */
public abstract class BaseScrollTabListAdapter<T> extends BaseAdapter {

    protected Context mContext;
    protected LayoutInflater mInflater;
    protected PullToRefreshListView pullToRefreshListView;
    public ArrayList<T> mDataList = new ArrayList<T>();
    public final int PAGE_SIZE = 10; // 默认加载一页数据量
    private int DefaultStartIndex = 0; // 默认起始页 可自定义
    private int PageIndex = 0; // 当前加载数据分页下标
    private boolean isLoadMore = true;// 是否是加载更多
    private boolean mIsLoading = false;
    public boolean isFirstLoad = true;//是否第一次加载
    public boolean mIsNeedPreLoading = true;//是否需要预加载，就是初始化时就将几个页面的数据加载好
    private int mCurrentPosition = 0;//记录多个List的下标
    private OnRequestReturnListener onRequestReturnListener;

    // 无数据提示界面相关数据
    private LinearLayout mEmptyLayout;
    private View mFailedView; // 失败View
    private View mEmptyView;
    private ImageView mEmptyIcon;
    private TextView mEmptyTitle;
    private TextView mEmptyViewTip;
    private RelativeLayout mEmptyDescWrapper;
    private TextView mEmptyDesc;
    private String mEmptyTip;//空提示
    private boolean mIsShowEmptyIcon = true;// 是否显示空图片
    private boolean mIsShowBoldTip = true;// 是否显示大字提示
    private int mEmptyIconRes = 0;//空提示图片

    public int mLoadStatus = 0;// 0 正常 1 空 2 失败
    public static final int VIEW_TYPE_NORMAL = 0;
    public static final int VIEW_TYPE_EMPTY = 1;

    public void setEmptyTip(String emptyTip) {
        this.mEmptyTip = emptyTip;
    }

    public void setShowEmptyIcon(boolean isShowEmptyIcon) {
        this.mIsShowEmptyIcon = isShowEmptyIcon;
    }

    public void setShowEmptyIcon(boolean isShowEmptyIcon, int emptyIconRes) {
        this.mIsShowEmptyIcon = isShowEmptyIcon;
        this.mEmptyIconRes = emptyIconRes;
    }

    public void setIsShowBoldTip(boolean isShowBoldTip) {
        this.mIsShowBoldTip = isShowBoldTip;
    }

    private boolean mShowEmptyDesc;

    public void setShowEmptyDesc(boolean b) {
        mShowEmptyDesc = b;
    }

    public void setOnRequestReturnListener(
            OnRequestReturnListener onRequestReturnListener, int position) {
        this.onRequestReturnListener = onRequestReturnListener;
        this.mCurrentPosition = position;
    }

    public BaseScrollTabListAdapter(Context context,
                                    ArrayList<T> dataList, boolean isNeedPreLoading) {
        if (dataList != null && dataList.size() > 0) {
            mDataList.addAll(dataList);
            isFirstLoad = false;
        }
        mIsNeedPreLoading = isNeedPreLoading;
        initParams(context);
    }

    public BaseScrollTabListAdapter(Context context,
                                    ArrayList<T> dataList) {
        if (dataList != null && dataList.size() > 0) {
            mDataList.addAll(dataList);
            isFirstLoad = false;
        }
        initParams(context);
    }


    public BaseScrollTabListAdapter(Context context) {
        mIsNeedPreLoading = false;//默认不预加载
        initParams(context);

    }

    public void initParams(Context context) {
        mContext = context;
        mInflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void initAdapter(
            PullToRefreshListView pullToRefreshListView) {
        this.pullToRefreshListView = pullToRefreshListView;
        initScrollView();
    }

    /**
     * 重设数据内容。
     */
    public final void reset() {
        mDataList.clear();
        PageIndex = 0;
    }

    @Override
    public final int getCount() {
        if (mDataList.size() == 0) {
            return 1;
        }
        return mDataList.size();
    }

    @Override
    public final int getViewTypeCount() {
        return 2;
    }

    @Override
    public final int getItemViewType(int position) {
        if (position == 0 && mDataList.size() == 0) {
            return VIEW_TYPE_EMPTY;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public T getItem(int position) {
        if (mDataList.size() == 0) {
            return null;
        }
        return mDataList == null ? null : mDataList.get(position);
    }

    private void initScrollView() {
        if (mDataList.size() == 0) {
            DefaultStartIndex = 0;
            if (mIsNeedPreLoading) {
                requestNew();
            }
        } else {
            DefaultStartIndex = 1;
            PageIndex = DefaultStartIndex;
        }
    }

    public void requestNew() {
        isLoadMore = false;
        isFirstLoad = false;
        PageIndex = DefaultStartIndex;
        request();
    }

    public void requestMore() {
        if (!mIsLoading) {
            isLoadMore = true;
            request();
        }
    }

    public void request() {
        mIsLoading = true;
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                if ((mDataList == null || mDataList.size() == 0) && !mIsNeedPreLoading) {
                    pullToRefreshListView.setFirstLoading(true);
                    pullToRefreshListView.setRefreshing();
                }
                requestData();
            }
        }, 500);
    }

    protected abstract void requestData();

    protected final void onSuccess(List<T> data) {
        pullToRefreshListView.onRefreshComplete();
        mIsLoading = false;
        addList(data);
    }

    protected final void onFailure(String errorCode, String msg) {
        pullToRefreshListView.onRefreshComplete();
        if (onRequestReturnListener != null
                && mDataList.size() < 1) {
            mLoadStatus = 2;
            onRequestReturnListener.onFailedData(mCurrentPosition);
        }
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        ToastUtil.showToast(mContext, msg);
        mIsLoading = false;
    }


    @SuppressLint("HandlerLeak")
    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (!isLoadMore) {
                mDataList.clear();
            }
            if (msg.obj != null) {
                //onSuccess(msg.obj.toString());
            } else {
                addList(null);
            }
            mIsLoading = false;
        }
    };

    public void addList(List<T> newList) {
        if (newList != null && newList.size() > 0) {
            PageIndex = PageIndex + 1;
            mDataList.addAll(newList);
            notifyDataSetChanged();
            mLoadStatus = 0;
            if (onRequestReturnListener != null) {
                onRequestReturnListener.oSuccessData(mCurrentPosition);
            }
            if (newList.size() < PAGE_SIZE) {
                pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            } else {
                pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
            }
        } else {
            pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            if (onRequestReturnListener != null && mDataList.size() < 1) {
                mLoadStatus = 1;
                onRequestReturnListener.onEmptyData(mCurrentPosition);

            } else {
                //ToastUtil.showCustomViewToast(mContext, R.string.no_data);
            }
        }
    }

    public void deleteItem(int position) {
        mDataList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public final View getView(final int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        if (type == VIEW_TYPE_EMPTY) {
            if (mEmptyLayout == null) {
                mEmptyLayout = new LinearLayout(mContext);
                mEmptyLayout.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.MATCH_PARENT));
                mEmptyLayout.setOrientation(LinearLayout.VERTICAL);
            }

            return mEmptyLayout;
        } else {
            View view = getView(position, convertView);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick(position);
                }
            });
            return view;
        }
    }

    public abstract View getView(int position, View convertView);

    protected void onItemClick(int position) {

    }

    public void showEmptyFailed(int emptyHeight) {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, emptyHeight);
        if (mLoadStatus == 2) {
            addFailedView();
            mEmptyLayout.removeAllViews();
            mEmptyLayout.addView(mFailedView, lp);
        } else {
            addEmptyView();
            mEmptyLayout.removeAllViews();
            mEmptyLayout.addView(mEmptyView, lp);
        }
    }

    /**
     * 添加失败提示视图
     */
    protected void addFailedView() {
        if (mFailedView == null) {
//            mFailedView = View.inflate(mContext,R.layout.failed_note,null);
//            ImageView mFailedImage = (ImageView)mFailedView.findViewById(R.id.base_load_failed_image);
//            //调整顶部边距
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mFailedImage.getLayoutParams();
//            lp.topMargin = 30;
//            mFailedImage.setLayoutParams(lp);
//
//            mFailedView.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    requestNew();
//                }
//            });
//            mFailedView.setVisibility(View.VISIBLE);
        }

    }


    /**
     * 添加为空视图
     */
    protected void addEmptyView() {
//        if (mEmptyView == null) {
//            mEmptyView =  View.inflate(mContext,
//                    R.layout.pull_to_refresh_empty, null);
//            mEmptyIcon = (ImageView) mEmptyView
//                    .findViewById(R.id.empty_icon);
//            mEmptyTitle = (TextView) mEmptyView
//                    .findViewById(R.id.empty_title);
//            mEmptyViewTip = (TextView) mEmptyView.findViewById(R.id.empty_tip);
//
//            mEmptyDescWrapper = (RelativeLayout) mEmptyView.findViewById(R.id.empty_desc_wrapper);
//            mEmptyDesc = (TextView) mEmptyView.findViewById(R.id.empty_desc);
//            //调整顶部边距
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mEmptyIcon.getLayoutParams();
//            lp.topMargin = 30;
//            mEmptyIcon.setLayoutParams(lp);
//            mEmptyView.setVisibility(View.VISIBLE);
//            initEmptyViewData();
//
//        }

    }

    private void initEmptyViewData() {
        mEmptyViewTip.setText("" + mEmptyTip);
        if (mIsShowBoldTip) {
            mEmptyTitle.setVisibility(View.VISIBLE);
        } else {
            mEmptyTitle.setVisibility(View.GONE);
        }
        if (mIsShowEmptyIcon) {
            mEmptyIcon.setVisibility(View.VISIBLE);
            if (mEmptyIconRes > 0) {
                try {
                    mEmptyIcon.setImageResource(mEmptyIconRes);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            mEmptyIcon.setVisibility(View.GONE);
        }

        if (mShowEmptyDesc) {
            mEmptyDesc.setText("" + mEmptyTip);
            mEmptyDescWrapper.setVisibility(View.VISIBLE);
            mEmptyIcon.setVisibility(View.GONE);
            mEmptyTitle.setVisibility(View.GONE);
            mEmptyViewTip.setVisibility(View.GONE);
        }
    }


    public interface OnRequestReturnListener {
        public void oSuccessData(int position);

        public void onEmptyData(int position);

        public void onFailedData(int position);
    }
}
