package cn.xiaojs.xma.common.pageload.trigger;

import cn.xiaojs.xma.common.pageload.PageChangeListener;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.pulltorefresh.stickylistheaders.StickyListHeadersListView;

/**
 * Created by Paul Z on 2017/5/16.
 */

public class PageChangeInPullRefresh implements PullToRefreshBase.OnRefreshListener2<StickyListHeadersListView> {

    PullToRefreshSwipeListView mListView;
    PageChangeListener loader;
    private boolean isLoading;

    public PageChangeInPullRefresh(PullToRefreshSwipeListView listView,PageChangeListener loader) {
        mListView=listView;
        mListView.setOnRefreshListener(this);
        this.loader=loader;
    }



    @Override
    public void onPullDownToRefresh(PullToRefreshBase<StickyListHeadersListView> refreshView) {
        loader.refresh();
    }

    @Override
    public void onPullUpToRefresh(PullToRefreshBase<StickyListHeadersListView> refreshView) {
        loader.next();
    }

}
