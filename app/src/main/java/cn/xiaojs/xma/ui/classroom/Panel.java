package cn.xiaojs.xma.ui.classroom;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/11/28
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import cn.xiaojs.xma.ui.classroom.drawer.DrawerLayout;

public abstract class Panel {
    protected Context mContext;
    protected View mContentView;

    public Panel(Context context) {
        mContext = context;
    }

    public abstract View onCreateView();

    public void show(final DrawerLayout drawerLayout, final ViewGroup container) {
        show(null, drawerLayout, container);
    }

    public void show(ViewGroup.LayoutParams params, final DrawerLayout drawerLayout, final ViewGroup container) {
        if (mContentView == null) {
            mContentView = onCreateView();
            if (params != null) {
                container.addView(mContentView, params);
            } else {
                container.addView(mContentView);
            }

            initChildView(mContentView);
        }

        if (container != null) {
            int count = (container).getChildCount();
            for (int i = 0; i < count; i++) {
                View v = (container).getChildAt(i);
                if (v == mContentView) {
                    v.setVisibility(View.VISIBLE);
                } else {
                    v.setVisibility(View.GONE);
                }
            }
        }

        setChildViewStyle();
        drawerLayout.openDrawer(container);

        initData();
    }

    public void initChildView(View root) {

    }

    public void setChildViewStyle() {

    }

    public abstract void initData();
}
