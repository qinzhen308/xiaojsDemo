package cn.xiaojs.xma.ui.classroom.pendingdev;
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
    protected DrawerLayout mDrawerLayout;
    protected ViewGroup mContainer;

    private DrawerLayout.DrawerListener mDrawerListener;
    private boolean mDataInited = false;
    protected boolean mDrawerOpened = false;

    public Panel(Context context) {
        mContext = context;
        mDrawerListener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mDrawerOpened = true;
                if (!mDataInited) {
                    mDataInited = true;
                    initData();
                    onPanelOpened();
                }
            }


            public void onDrawerClosed(View drawerView) {
                mDrawerOpened = false;
                mDataInited = false;
                onPanelClosed();
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        };
    }

    public abstract View onCreateView();

    public void show(final DrawerLayout drawerLayout, final ViewGroup container) {
        show(null, drawerLayout, container, false);
    }

    public void initWithoutShow(final DrawerLayout drawerLayout, final ViewGroup container) {
        show(null, drawerLayout, container, true);
    }

    public void show(ViewGroup.LayoutParams params, final DrawerLayout drawerLayout, final ViewGroup container, boolean onlyInit) {
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
        if (onlyInit) {
            initData();
        } else {
            drawerLayout.openDrawer(container);
            drawerLayout.addDrawerListener(mDrawerListener);
        }

        mDrawerLayout = drawerLayout;
        mContainer = container;
    }

    public void close(final DrawerLayout drawerLayout, final ViewGroup container) {
        close(drawerLayout, container, true);
    }


    public void close(final DrawerLayout drawerLayout, final ViewGroup container, boolean animate) {
        if (drawerLayout == null || container == null) {
            return;
        }

        drawerLayout.closeDrawer(container, animate);
    }

    public void close(final DrawerLayout drawerLayout, int gravity, boolean animate) {
        if (drawerLayout == null) {
            return;
        }

        drawerLayout.closeDrawer(gravity, animate);
    }

    protected void close () {
        if (mDrawerLayout == null || mContainer == null) {
            return;
        }

        mDrawerLayout.closeDrawer(mContainer, true);
    }

    protected void close(boolean animate) {
        if (mDrawerLayout == null || mContainer == null) {
            return;
        }

        mDrawerLayout.closeDrawer(mContainer, animate);
    }

    public void release() {
        if (mDrawerLayout != null) {
            mDrawerLayout.removeDrawerListener(mDrawerListener);
        }
    }

    public void initChildView(View root) {

    }

    public void setChildViewStyle() {

    }

    public abstract void initData();

    protected void onPanelOpened() {

    }

    protected void onPanelClosed() {

    }
}
