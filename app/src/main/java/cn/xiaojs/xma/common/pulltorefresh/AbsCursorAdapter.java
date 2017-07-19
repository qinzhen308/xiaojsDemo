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
 * Date:2017/2/13
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorListenerAdapter;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.pulltorefresh.swipelistview.BaseSwipeListViewListener;
import cn.xiaojs.xma.common.pulltorefresh.swipelistview.SwipeListView;
import cn.xiaojs.xma.util.DeviceUtil;

public abstract class AbsCursorAdapter<H extends BaseHolder> extends CursorAdapter {

    protected Context mContext;
    protected PullToRefreshSwipeListView listView;

    public AbsCursorAdapter(Context context, PullToRefreshSwipeListView listView, Cursor c) {
        super(context, c, true);
        mContext = context;
        init(listView);
    }

    public AbsCursorAdapter(Context context, PullToRefreshSwipeListView listView, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
        init(listView);
    }

    public AbsCursorAdapter(Context context, PullToRefreshSwipeListView listView, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
        init(listView);
    }


    private void init(PullToRefreshSwipeListView listView) {
        this.listView = listView;
        final SwipeListView list = listView.getRefreshableView().getWrappedList();
        if (list.isSwipeEnabled()) {
            list.setSwipeListViewListener(new BaseSwipeListViewListener() {
                @Override
                public void onClickFrontView(int position) {

                    onDataItemClick(position);

                }
            });
        } else {
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    onDataItemClick(position);
                }
            });
        }

    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = createItem(context, cursor, parent);
        H h = initHolder(view);
        view.setTag(h);
        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        H h = (H) view.getTag();
        TextView delete = (TextView) view.findViewById(R.id.swipe_delete);
        TextView mark = (TextView) view.findViewById(R.id.swipe_mark);

        final int pos = cursor.getPosition();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDeleteListener(pos, cursor);
            }
        });

        onAttachSwipe(mark, delete);

        setViewContent(h, context, cursor);
    }

    protected abstract void setViewContent(H holder, Context context, Cursor cursor);

    /**
     * 加载item的布局view
     */
    protected abstract View createContentView(Context context, Cursor cursor, ViewGroup parent);

    /**
     * 初始化holder
     */
    protected abstract H initHolder(View view);

    /**
     * 创建ListItem
     */
    protected View createItem(Context context, Cursor cursor, ViewGroup parent) {

        if (listView != null && listView.getRefreshableView().isSwipeEnabled()) {
            View templateView = LayoutInflater.from(mContext).inflate(
                    R.layout.layout_list_swipe_row, parent, false);
            LinearLayout frontView = (LinearLayout) templateView
                    .findViewById(R.id.swipe_front);
            LinearLayout backView = (LinearLayout) templateView
                    .findViewById(R.id.swipe_back);
            FrameLayout contentFrame = (FrameLayout) frontView
                    .findViewById(R.id.content);
            // 滑动删除list item的自定义区域
            View contentView = createContentView(context, cursor, parent);
            contentFrame.addView(contentView);
            return templateView;
        }

        return createContentView(context, cursor, parent);
    }

    protected final void setLeftOffset(float width) {
        SwipeListView swipe = listView.getRefreshableView().getWrappedList();
        swipe.setOffsetLeft(DeviceUtil.getScreenWidth(mContext) - width);
    }


    public final void deleteItem(final int position, final Cursor cursor) {
        final SwipeListView list = listView.getRefreshableView()
                .getWrappedList();
        list.closeOpenedItems();
        list.dismiss(position, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                onSwipeDelete(position,cursor);
            }
        });
    }


    protected void setDeleteListener(int position, final Cursor cursor) {
        deleteItem(position, cursor);
    }

    protected void onSwipeDelete(int position, final Cursor cursor) {

    }

    protected void onAttachSwipe(TextView mark, TextView del) {

    }

    protected void onDataItemClick(int position) {

    }
}
