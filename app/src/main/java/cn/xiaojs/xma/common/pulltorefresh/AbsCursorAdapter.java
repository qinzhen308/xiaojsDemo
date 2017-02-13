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
import android.widget.CursorAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;

public abstract class AbsCursorAdapter<H extends BaseHolder> extends CursorAdapter {

    protected Context mContext;

    public AbsCursorAdapter(Context context, Cursor c) {
        super(context, c, true);
        mContext = context;
    }

    public AbsCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        mContext = context;
    }

    public AbsCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = createItem(context, cursor, parent);
        H h = initHolder(view);
        view.setTag(h);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        H h = (H) view.getTag();
        TextView delete = (TextView) view.findViewById(R.id.swipe_delete);
        TextView mark = (TextView) view.findViewById(R.id.swipe_mark);
        setDeleteListener(delete, cursor);
        setViewContent(h,context,cursor);
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
        View templateView = LayoutInflater.from(mContext).inflate(
                R.layout.layout_list_swipe_row, parent,false);
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

    protected void setDeleteListener(TextView text, Cursor cursor) {

    }
}
