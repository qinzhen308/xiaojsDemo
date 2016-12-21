package cn.xiaojs.xma.ui.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.ui.home.OnScrollYListener;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * 功能描述：带共同头视图的单个或者多个Tab可切换 往上拉可选择是否悬浮切换Tab 支持上下拉刷新 采用多PullToRefreshListView实现 通过监听多PullToRefreshListView的滑动坐标来同步顶部Tab的悬停 通过监听页面切换不断remove 和 add headerView到头部
 *
 * 需在子类调用ButterKnife.bind对view进行绑定
 */
public abstract class BaseScrollTabActivity extends BaseActivity implements BaseScrollTabListener {

    protected ArrayList<String> mPagerTitles;//每个Tab切换的标题
    protected ArrayList<? extends BaseScrollTabListAdapter> mBaseAdapters;
    // 缓存着当前存在的几个ListView
    protected ArrayList<PullToRefreshListView> mListViewCache = new ArrayList<PullToRefreshListView>();
    // 缓存着几个ListView上次滑动的最终Y坐标
    protected ArrayList<Integer> mListScrollY = new ArrayList<Integer>();
    protected ArrayList<LinearLayout> mListHeaderCache = new ArrayList<LinearLayout>();
    private ImageView mIvLine;// 下滑动画分割线
    private LinearLayout mTxtContent;// tab标题所在的容器
    private LinearLayout mTopTabContent;

    /**
     * 悬浮框所用控件
     */
    protected View mHoverView;// 悬浮框View
    private ImageView mHovIvLine = null;// 下滑动画分割线
    private LinearLayout mHovTxtContent = null;// tab标题所在的容器
    private LinearLayout mHovTopTabContent = null;// tab标题所在的容器

    private LinearLayout mViewPagerContent;//多个ListView的容器
    private LinearLayout mViewPager;//多个ListView的容器
    private BaseScrollTabLayout mHeaderBaseContent;//滑动头容器 头部视图的根布局
    private LinearLayout mHeaderLayout;//头部添加View所在容器
    private LinearLayout mHeaderHoverLayout;//头部中需要悬浮的容器

    private View mHeaderView; //头视图
    private View mHeaderHoverView; //头视图中需要悬浮的视图
    private View mFootView;
    private boolean mIsShowHover = true;// 是否显示悬浮Tab

    private int mTabWidth; // 整个Tab切换区域的宽度
    private int mEachItemWidth; // 每个tab宽度
    private int mStartDelta;// 用于保存红色横线动画的起始位置
    private int mCurrentPosition = 0;// 当前选中第几个tab
    private int mBaseContentHeight = 0;//除开标题栏内容总高度
    private int mBaseContentTopY;//除开标题栏以外的内容定点Y坐标
    private int mEmptyViewHeight;//空视图和失败视图的高度
    private int headerHeight;//带TAB头的高度
    private int headerTranslationDis;//不带Tab时的高度
    private int mScrollY;//向上滑动的距离
    protected ArrayList<OnScrollYListener> mScrollYListenerCache = new ArrayList<OnScrollYListener>();

    @BindView(R.id.scroll_tab_header)
    protected RelativeLayout mTabHeader;
    @BindView(R.id.scroll_tab_left_image)
    protected ImageView mTabLeftImage;
    @BindView(R.id.scroll_tab_middle_view)
    protected TextView mTabMiddleText;
    @BindView(R.id.scroll_tab_right_view)
    protected TextView mTabRightText;
    @BindView(R.id.scroll_tab_header_divider)
    protected View mDivider;


    public void setShowHover(boolean isShowHover) {
        this.mIsShowHover = isShowHover;
    }

    @Override
    public void addViewContent() {
        addView(R.layout.activity_base_scroll_tab);
        addHoverHeaderView();
        if (hoverMarginTop() > 0) {
            ViewGroup.MarginLayoutParams hoverMlp = (ViewGroup.MarginLayoutParams) mHoverView.getLayoutParams();
            hoverMlp.topMargin = hoverMarginTop();
            mHoverView.setLayoutParams(hoverMlp);
        }
    }

    @Override
    protected boolean delayBindView() {
        return true;
    }

    /**
     * 将多Tab的ListView添加到界面的任何位置 注意标题和空提示要与适配器一一对应
     *
     * @param headerView    添加的头视图
     * @param mPagerTitles  tab的标题字符串
     * @param mBaseAdapters 继承于BaseAdapter的数据适配器
     */
    public void addTabListIntoContent(
            View headerView, ArrayList<String> mPagerTitles,
            ArrayList<? extends BaseScrollTabListAdapter> mBaseAdapters) {
        initStructure(headerView, null, 0, mPagerTitles, mBaseAdapters);
    }

    public void addTabListIntoContent(
            View headerView, ArrayList<String> mPagerTitles,
            ArrayList<? extends BaseScrollTabListAdapter> mBaseAdapters, int defaultIndex) {
        this.mCurrentPosition = defaultIndex;
        initStructure(headerView, null, 0, mPagerTitles, mBaseAdapters);
    }

    public void addTabListIntoContent(
            View headerView, View footView, ArrayList<String> mPagerTitles,
            ArrayList<? extends BaseScrollTabListAdapter> mBaseAdapters, int defaultIndex) {
        this.mCurrentPosition = defaultIndex;
        initStructure(headerView, footView, 0, mPagerTitles, mBaseAdapters);
    }

    public void addTabListIntoContent(
            View headerView, int hoverResId, ArrayList<String> mPagerTitles,
            ArrayList<? extends BaseScrollTabListAdapter> mBaseAdapters, int defaultIndex) {
        this.mCurrentPosition = defaultIndex;
        initStructure(headerView, null, hoverResId, mPagerTitles, mBaseAdapters);
    }

    //使用已有的多Tab切换
    private void initStructure(
            View headerView, View footView, int hoverResId, ArrayList<String> mPagerTitles,
            ArrayList<? extends BaseScrollTabListAdapter> mBaseAdapters) {
        this.mPagerTitles = mPagerTitles;
        this.mBaseAdapters = mBaseAdapters;
        initBaseView(headerView, footView, hoverResId);
    }

    public abstract void addHoverHeaderView();

    public void initViewData() {

    }

    @Override
    protected void needHeaderDivider(boolean need) {
        if (need) {
            mDivider.setVisibility(View.VISIBLE);
        } else {
            mDivider.setVisibility(View.GONE);
        }
    }

    private void initBaseView(View headerView, View footView, int hoverResId) {
        mViewPagerContent = (LinearLayout) findViewById(R.id.pager_content);
        mViewPager = new LinearLayout(this);
        mViewPager.setOrientation(LinearLayout.HORIZONTAL);

        initHeaderView(headerView, hoverResId);

        initListView();
        pageListSelected(mCurrentPosition);
        removeHeaderToNext(mCurrentPosition, mCurrentPosition);

        refreshListOnlyFirst();

        if (footView != null) {
            mFootView = footView;
            LinearLayout.LayoutParams contentLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            mViewPagerContent.addView(mViewPager, contentLayoutParams);
            LinearLayout.LayoutParams footLayoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            mViewPagerContent.addView(footView, footLayoutParams);
        } else {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            mViewPagerContent.addView(mViewPager, layoutParams);
        }
        setCurrentTab(mCurrentPosition);

    }

    private void removeHeaderToNext(int oldPosition, int position) {
        if (position >= 0 && position < mListHeaderCache.size()) {
            mListHeaderCache.get(oldPosition).removeAllViews();
            mListHeaderCache.get(position).addView(mHeaderView);

            ViewTreeObserver vto = mHeaderLayout.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @SuppressWarnings("deprecation")
                @Override
                public void onGlobalLayout() {
                    mHeaderLayout.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                    getHeaderHeight();
                }
            });
        }
    }

    private void initHeaderView(View headerView, int hoverResId) {
        mTabWidth = getResources().getDisplayMetrics().widthPixels;
        mHeaderView = LayoutInflater.from(this).inflate(R.layout.activity_base_scroll_tab_header, null);
        mHeaderBaseContent = (BaseScrollTabLayout) mHeaderView.findViewById(R.id.header_content);
        mHeaderLayout = (LinearLayout) mHeaderView.findViewById(R.id.header);
        mHeaderHoverLayout = (LinearLayout) mHeaderView.findViewById(R.id.header_hover);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mTabWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        if (hoverResId <= 0) {
            mHeaderHoverView = LayoutInflater.from(this).inflate(R.layout.activity_base_tab_top_switch, null);
            mHeaderHoverLayout.addView(mHeaderHoverView, lp);
            initDefaultTabView();
        } else {
            try {
                mHeaderHoverView = LayoutInflater.from(this).inflate(hoverResId, null);
                mHeaderHoverLayout.addView(mHeaderHoverView, lp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mHeaderLayout.addView(headerView, lp);

        if (mIsShowHover) {
            initHoverView(hoverResId);
        }
    }

    private void initDefaultTabView() {
        // 创建切换区域
        mTopTabContent = (LinearLayout) mHeaderView.findViewById(R.id.top_tab_content);
        mIvLine = (ImageView) mHeaderView.findViewById(R.id.tab_scroll_img);
        mTxtContent = (LinearLayout) mHeaderView.findViewById(R.id.txt_content);
        createNormalTab(mTxtContent, mPagerTitles);
        setImageViewDefault(mIvLine);
    }

    private void getHeaderHeight() {
        mBaseContentHeight = mContent.getHeight();
        //手机屏幕高 减去内容高度
        mBaseContentTopY = getResources().getDisplayMetrics().heightPixels - mBaseContentHeight;
        refreshHeaderHeight();

        //注解 解析注解的控件和事件
        // ViewUtils.injectChildAndSelf(this);
        initViewData();
    }

    private void refreshHeaderHeight() {
        headerHeight = mHeaderBaseContent.getHeight();
        headerTranslationDis = mHeaderLayout.getHeight() - hoverMarginTop();
        mEmptyViewHeight = mBaseContentHeight - headerTranslationDis - mHeaderHoverLayout.getHeight();
        if (mFootView != null) {
            mEmptyViewHeight = mEmptyViewHeight - mFootView.getHeight();
        }
        if (mEmptyViewHeight < mBaseContentHeight / 3) {
            mEmptyViewHeight = ViewGroup.LayoutParams.MATCH_PARENT;
        }
    }

    protected int hoverMarginTop() {
        return 0;
    }

    // 创建普通样式的Tab
    protected void createNormalTab(LinearLayout layout, ArrayList<String> pagerTitles) {
        layout.removeAllViews();
        LayoutInflater inflater = getLayoutInflater();
        int tabSize = pagerTitles.size();
        for (int i = 0; i < tabSize; i++) {
            View view = inflater.inflate(R.layout.layout_tab_title_item, null);
            TextView txtView = (TextView) view
                    .findViewById(R.id.title_item_text);
            txtView.setText(pagerTitles.get(i));
            txtView.setSingleLine();
            txtView.setEllipsize(TextUtils.TruncateAt.END);//超过用省略号代替
            if (i == mCurrentPosition) {
                txtView.setTextColor(getResources().getColor(R.color.font_orange));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1);
            if (tabSize == 1) {
                params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params.leftMargin = getResources()
                        .getDimensionPixelOffset(R.dimen.px30);
            }
            view.setLayoutParams(params);
            final int position = i;
            view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    setCurrentTab(position);
                }
            });
            layout.addView(view);
        }
    }

    public void setCurrentTab(int position) {
        if (mCurrentPosition != position) {
            if (mCurrentPosition >= 0 && mCurrentPosition < mListViewCache.size()) {
                mListViewCache.get(mCurrentPosition).onRefreshComplete();
            }
            removeHeaderToNext(mCurrentPosition, position);
            // 修正滚出去的偏移量
            adjustScroll(mCurrentPosition, position);
            mCurrentPosition = position;
            refreshListOnlyFirst();
            pageSelected(mTxtContent, mIvLine, position);
            if (mHovTxtContent != null && mHovIvLine != null) {
                pageSelected(mHovTxtContent, mHovIvLine, position);
            }
            if (position >= 0 && position < mListScrollY.size()) {
                synchHoverView(mListScrollY.get(position));
            }
            if (mOnPagerClickListener != null) {
                mOnPagerClickListener.onSelected(position);
            }
        }
    }

    /**
     * @param position 更新第几个tab的标题
     * @param newTitle 标题内容
     */
    public void updateTextNew(int position, String newTitle) {
        if (position >= 0 && position < mPagerTitles.size()) {
            ((TextView) mTxtContent.getChildAt(position).findViewById(
                    R.id.title_item_text)).setText(newTitle);
            if (mHoverView != null) {
                ((TextView) mHovTxtContent.getChildAt(position).findViewById(
                        R.id.title_item_text)).setText(newTitle);
            }
        }
    }

    /**
     * @param layout   改变的布局
     * @param position 第几个TextView的文字大小和颜色
     */
    private void changeText(LinearLayout layout, int position) {
        for (int i = 0; i < mPagerTitles.size(); i++) {
            TextView textView = (TextView) layout.getChildAt(i).findViewById(
                    R.id.title_item_text);
            if (position == i) {
                textView.setTextColor(getResources()
                        .getColor(R.color.font_orange));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.font_32px));
            } else {
                textView.setTextColor(getResources()
                        .getColor(R.color.font_black));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                        getResources().getDimension(R.dimen.font_28px));
            }
        }
    }

    // 仅仅初始化的时候调用
    private void setImageViewDefault(ImageView ivLine) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ivLine
                .getLayoutParams();

        if (mTabWidth > 0) {
            mEachItemWidth = mTabWidth / mPagerTitles.size();
        } else {
            mEachItemWidth = dm.widthPixels / mPagerTitles.size();
        }

        if (mPagerTitles.size() == 1) {
            params.width = 0;
        } else if (mPagerTitles.size() == 2) {
            params.width = mEachItemWidth
                    - getResources().getDimensionPixelOffset(R.dimen.px60);
        } else {
            params.width = mEachItemWidth;
        }

        ivLine.setLayoutParams(params);

        int itemSpacing = 0;
        itemSpacing = (mEachItemWidth - params.width) / 2;

        int endDelta = itemSpacing + mCurrentPosition * mEachItemWidth;
        Animation animation = new TranslateAnimation(endDelta, endDelta, 0, 0);
        animation.setFillAfter(true);
        ivLine.startAnimation(animation);
        mStartDelta = endDelta;
    }

    /**
     * tab下划线切换
     */
    private void setImagePosition(ImageView ivLine, int position) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ivLine
                .getLayoutParams();
        int endDelta = mEachItemWidth * position
                + (mEachItemWidth - params.width) / 2;
        Animation animation = new TranslateAnimation(mStartDelta, endDelta, 0,
                0);
        animation.setFillAfter(true);
        animation.setDuration(300);
        ivLine.startAnimation(animation);
        mStartDelta = endDelta;
    }

    private void initListView() {
        if (mBaseAdapters != null && mBaseAdapters.size() > 0) {
            ColorDrawable colorDrawable = new ColorDrawable(
                    getResources().getColor(R.color.white));
            int height = getResources().getDimensionPixelSize(R.dimen.px1);
            for (int i = 0; i < mBaseAdapters.size(); i++) {
                mListScrollY.add(i, 0);
                final int position = i;
                PullToRefreshListView listView = new PullToRefreshListView(this);
                listViewAddHeader(position, listView);
                mBaseAdapters.get(i).setOnRequestReturnListener(new BaseScrollTabListAdapter.OnRequestReturnListener() {
                    @Override
                    public void oSuccessData(int position) {
                        delayedSynchHeaderHeight();

                    }

                    @Override
                    public void onEmptyData(final int position) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                refreshHeaderHeight();
                                Log.e("onEmptyData", "mBaseContentHeight" + mBaseContentHeight + "headerHeight" + headerHeight + "headerTranslationDis" + headerTranslationDis + "mEmptyViewHeight" + mEmptyViewHeight);
                                mBaseAdapters.get(position).showEmptyFailed(mEmptyViewHeight);
                            }
                        }, 100);
                    }

                    @Override
                    public void onFailedData(int position) {
                        mBaseAdapters.get(position).showEmptyFailed(mEmptyViewHeight);
                    }
                }, i);
                mBaseAdapters.get(i).initAdapter(listView);
                listView.setAdapter(mBaseAdapters.get(i));
                listView.setMode(PullToRefreshBase.Mode.BOTH);
                listView.getRefreshableView().setDivider(colorDrawable);
                listView.getRefreshableView().setDividerHeight(height);

                listView.setOnScrollListener(new AbsListView.OnScrollListener() {

                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                        switch (scrollState) {
                            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE://空闲状态 0
                                Log.e("onScrollStateChanged", "position" + position + "mScrollY" + mScrollY);
                                mListScrollY.set(position, mScrollY);
                                break;
                            case AbsListView.OnScrollListener.SCROLL_STATE_FLING://滚动状态 1
                                break;
                            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL://触摸后滚动 2
                                break;
                        }
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        BaseScrollTabActivity.this.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, position);
                        OnScrollYListener yl = getYListener(position);
                        if (yl != null) {
                            yl.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                        }
                    }
                });
                listView.setOnHeaderScrollListener(new PullToRefreshListView.OnHeaderScrollListener() {

                    @Override
                    public void onHeaderScroll(boolean isRefreashing, boolean istop, int value) {
                        if (istop) {
                            BaseScrollTabActivity.this.onHeaderScroll(isRefreashing, value, position);
                        }
                    }
                });
                listView.setOnRefreshListener(onRefreshListener2);
                mListViewCache.add(listView);
                LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                mViewPager.addView(listView, nlp);
            }
        }
    }

    private OnScrollYListener mYListener;

    private OnScrollYListener getYListener(int position) {
        if (position != mCurrentPosition) {
            return null;
        }

        if (mYListener == null) {
            mYListener = new OnScrollYListener() {
                @Override
                public void onScrollY(int y) {
                    BaseScrollTabActivity.this.onScrollY(y);
                }
            };
        }
        mYListener.setListView(mListViewCache.get(position).getRefreshableView());
        return mYListener;
    }

    protected void onScrollY(int y) {
        //Logger.i(getClass().getName() + "ScrollY = " + y);
    }

    private void delayedSynchHeaderHeight() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshHeaderHeight();
            }
        }, 100);
    }

    private void pageListSelected(int position) {
        if (mBaseAdapters != null && mBaseAdapters.size() > 0) {
            if (position < mBaseAdapters.size()
                    && mViewPager.getChildCount() > 0
                    && position < mViewPager.getChildCount()) {
                for (int i = 0; i < mViewPager.getChildCount(); i++) {
                    mViewPager.getChildAt(i).setVisibility(View.GONE);
                }
                mViewPager.getChildAt(position).setVisibility(View.VISIBLE);
            }
        }

    }

    private PullToRefreshBase.OnRefreshListener2<ListView> onRefreshListener2 = new PullToRefreshBase.OnRefreshListener2<ListView>() {

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            updateCurrentList();
        }

        //只相应上拉加载更多
        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            if (currentAdapterIsNotNull()) {
                mBaseAdapters.get(mCurrentPosition).requestMore();
            }
        }
    };

    private void listViewAddHeader(int position, PullToRefreshListView listView) {
        LinearLayout placeHolderView = new LinearLayout(this);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, AbsListView.LayoutParams.WRAP_CONTENT);
        placeHolderView.setLayoutParams(params);
        mListHeaderCache.add(placeHolderView);
        listView.getRefreshableView().addHeaderView(placeHolderView);
        listView.getRefreshableView().setVerticalScrollBarEnabled(false);

    }

    /**
     * 刷新当前列表 初始化使用 只在 第一次时 数据为空时 刷新 如果预加载过的 就不再加载
     */
    public void refreshListOnlyFirst() {
        if (currentAdapterIsNotNull()) {
            if (mBaseAdapters.get(mCurrentPosition).mDataList.isEmpty() &&
                    !mBaseAdapters.get(mCurrentPosition).mIsNeedPreLoading) {
                mBaseAdapters.get(mCurrentPosition).requestNew();
            }
        }
    }

    /**
     * @return 当前所在第几个tab
     */
    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    /**
     * 返回当前正在使用的ListView
     */
    public PullToRefreshListView getCurrentListView() {
        return mListViewCache.get(mCurrentPosition);
    }

    /**
     * @return 当前Adapter是否不为空
     */
    public boolean currentAdapterIsNotNull() {
        if (mBaseAdapters != null && mCurrentPosition < mBaseAdapters.size()) {
            return true;
        }
        return false;
    }

    /**
     * 刷新当前列表
     */
    public void updateCurrentList() {
        if (currentAdapterIsNotNull()) {
            mBaseAdapters.get(mCurrentPosition).requestNew();
        }
    }

    private int headerScrollSize = 0;

    public void adjustScroll(int oldPosition, int position) {
        PullToRefreshListView listView = mListViewCache.get(position);

        int oldScrollY = mListScrollY.get(oldPosition);
        int newScrollY = mListScrollY.get(position);
        Log.e("ViewHelper", "oldScrollY" + oldScrollY + "newScrollY" + newScrollY);
        if (oldScrollY != newScrollY || (oldScrollY == 0 && newScrollY == 0)) {
            //比较 选大的一个 也就是绝对值小的
            int factScrollY = oldScrollY < newScrollY ? newScrollY : oldScrollY;
            //同步坐标值
            mListScrollY.set(oldPosition, factScrollY);
            mListScrollY.set(position, factScrollY);

            listView.getRefreshableView().setSelectionFromTop(0, factScrollY);
        }
    }

    /**
     * 显示Tab切换的悬浮框
     */
    private void showSuspend() {
        if (mHoverView != null) {
            mHoverView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 移除Tab切换的悬浮框
     */
    private void removeSuspend() {
        if (mHoverView != null) {
            mHoverView.setVisibility(View.GONE);
        }
    }

    private void initHoverView(int resource) {
        if (mHoverView == null) {
            if (resource <= 0) {
                mHoverView = LayoutInflater.from(this).inflate(
                        R.layout.activity_base_tab_top_switch, null);
                mHovIvLine = (ImageView) mHoverView
                        .findViewById(R.id.tab_scroll_img);
                mHovTxtContent = (LinearLayout) mHoverView
                        .findViewById(R.id.txt_content);
                mHovTopTabContent = (LinearLayout) mHoverView
                        .findViewById(R.id.top_tab_content);
                FrameLayout.LayoutParams rLp = new FrameLayout.LayoutParams(
                        mTabWidth, WindowManager.LayoutParams.WRAP_CONTENT);
                rLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                rLp.topMargin = 0;
                mContent.addView(mHoverView, rLp);
                // 创建切换区域
                createNormalTab(mHovTxtContent, mPagerTitles);
                // 根据控件本身测量的宽度进行下滑线宽度设定
                ViewTreeObserver vto = mHovTopTabContent
                        .getViewTreeObserver();
                vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    @SuppressWarnings("deprecation")
                    @Override
                    public void onGlobalLayout() {
                        mHovTopTabContent.getViewTreeObserver()
                                .removeGlobalOnLayoutListener(this);
                        setImageViewDefault(mHovIvLine);
                        pageSelected(mHovTxtContent, mHovIvLine, mCurrentPosition);
                    }
                });

            } else {
                mHoverView = LayoutInflater.from(this).inflate(resource, null);
                FrameLayout.LayoutParams rLp = new FrameLayout.LayoutParams(
                        mTabWidth, WindowManager.LayoutParams.WRAP_CONTENT);
                rLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                rLp.topMargin = 0;
                mContent.addView(mHoverView, rLp);
                pageListSelected(mCurrentPosition);
            }
            // 初始化选中
            mHoverView.setVisibility(View.GONE);
        }
    }

    public void setHoverViewClickListener(View.OnClickListener onClickListener) {
    }

    private void pageSelected(LinearLayout layout, ImageView ivLine, int position) {
        changeText(layout, position);
        setImagePosition(ivLine, position);
        pageListSelected(position);

    }

    // 刷新头部显示时，没有onScroll回调，只有刷新时有
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition) {
        if (mCurrentPosition != pagePosition) {
            return;
        }
        mScrollY = Math.max(-getScrollY(view), -headerTranslationDis);//最多只允许滑动到tab的位置 高度;
        synchHoverView(mScrollY);
    }

    private void synchHoverView(int scrollY) {
        Log.e("synchHoverView", "synchHoverView=" + scrollY + "headerTranslationDis" + headerTranslationDis);
        if (mIsShowHover) {
            if (-scrollY >= headerTranslationDis && scrollY != 0) {
                showSuspend();
            } else {
                removeSuspend();
            }
        }
    }

    /**
     * 计算向上滑动的距离
     */
    public int getScrollY(AbsListView view) {
        View c = view.getChildAt(0);
        if (c == null) {
            return 0;
        }
        int top = c.getTop();
        int firstVisiblePosition = view.getFirstVisiblePosition();
        if (firstVisiblePosition == 0) {
            return -top + headerScrollSize;
        } else {
            return headerTranslationDis;
        }

    }

    /**
     * 与onScroll互斥，不能同时执行
     */
    @Override
    public void onHeaderScroll(boolean isRefreashing, int value, int pagePosition) {
        Logger.i("onHeaderScroll");
        if (mCurrentPosition != pagePosition && isRefreashing) {
            return;
        }
        headerScrollSize = value;
    }

    public void setNeedTabView(boolean need) {
        if (need) {
            if (mTopTabContent != null) {
                mTopTabContent.setVisibility(View.VISIBLE);
            }
        } else {
            setShowHover(false);
            if (mTopTabContent != null) {
                mTopTabContent.setVisibility(View.GONE);
            }
        }
    }

    public interface OnPagerClickListener {
        public void onSelected(int position);
    }

    private OnPagerClickListener mOnPagerClickListener;

    public void setOnPagerClickListener(OnPagerClickListener onPagerClickListener) {
        this.mOnPagerClickListener = onPagerClickListener;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBaseAdapters = null;
        mListViewCache = null;
        mListHeaderCache = null;
        mPagerTitles = null;
        mListScrollY = null;
        System.gc();
    }
}
