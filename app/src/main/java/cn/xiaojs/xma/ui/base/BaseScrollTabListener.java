package cn.xiaojs.xma.ui.base;

import android.widget.AbsListView;

public interface BaseScrollTabListener {


    void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount, int pagePosition);

    void onHeaderScroll(boolean isRefreashing, int value, int pagePosition);
}
