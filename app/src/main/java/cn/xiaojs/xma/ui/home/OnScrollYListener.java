package cn.xiaojs.xma.ui.home;
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
 * Date:2016/11/11
 * Desc:
 *
 * ======================================================================================== */

import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.util.HashMap;
import java.util.Map;

public abstract class OnScrollYListener implements AbsListView.OnScrollListener {

    private Map<Integer, Integer> mItemHeights = new HashMap<Integer, Integer>();
    private ListView mListView;
    private int lastY;

    public OnScrollYListener(ListView list){
        mListView = list;
    }

    public OnScrollYListener(){
    }

    public void setListView(ListView listView){
        mListView = listView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mListView == null || mListView.getAdapter() == null) {
            return;
        }        //listview当前在屏幕可见的第一个item，也就是firstVisibleItem索引对应的item
        View firstVisibleChild = mListView.getChildAt(0);
        if (firstVisibleChild == null) {
            return;
        }
        if (!mItemHeights.containsKey(firstVisibleItem)) {
            //将每个item以 key:index, value:height 存入map
            mItemHeights.put(firstVisibleItem, firstVisibleChild.getHeight());
        }
        int scrollY = computeScrollY(firstVisibleItem, firstVisibleChild);
        if (lastY != scrollY){
            lastY = scrollY;
            onScrollY(scrollY);
        }
    }

    private int computeScrollY(int firstVisibleItem, View firstVisibleChild) {
        int scrollY = 0;
        int sum = 0, count = 0;
        for (int i = 0; i <= firstVisibleItem; i++) {
            Integer h = mItemHeights.get(i);
            //当快速滑动listview时，firstVisibleItem不是连续增长，ex 0,1,3,7....
            if (h == null) {
                continue;
            }
            sum += h;
            count++;
        }
        if (count == 0) {
            return 0;
        }
        //已经记录的item的高度的平均值
        int avarage = sum / count;
        //已记录的item高度总和+没有记录的item的高度总和
        scrollY = sum + avarage * (firstVisibleItem + 1 - count);
        //第一个item可能这时候还有一半在屏幕内，这时候要减去这个item在屏幕内部分的高度
        scrollY -= firstVisibleChild.getBottom();
        return scrollY;
    }

    public abstract void onScrollY(int y);
}
