package com.benyuan.xiaojs.ui.classroom.drawer;
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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;

public class DrawerLayoutCompatApi21 {
    private static final int[] THEME_ATTRS = {
            android.R.attr.colorPrimaryDark
    };

    @TargetApi(20)
    public static void configureApplyInsets(View drawerLayout) {
        if (drawerLayout instanceof DrawerLayoutImpl) {
            drawerLayout.setOnApplyWindowInsetsListener(new InsetsListener());
            drawerLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
    }

    @TargetApi(20)
    public static void dispatchChildInsets(View child, Object insets, int gravity) {
        WindowInsets wi = (WindowInsets) insets;
        if (gravity == Gravity.LEFT) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(),
                    wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (gravity == Gravity.RIGHT) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(),
                    wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        child.dispatchApplyWindowInsets(wi);
    }

    @TargetApi(20)
    public static void applyMarginInsets(ViewGroup.MarginLayoutParams lp, Object insets,
                                         int gravity) {
        WindowInsets wi = (WindowInsets) insets;
        if (gravity == Gravity.LEFT) {
            wi = wi.replaceSystemWindowInsets(wi.getSystemWindowInsetLeft(),
                    wi.getSystemWindowInsetTop(), 0, wi.getSystemWindowInsetBottom());
        } else if (gravity == Gravity.RIGHT) {
            wi = wi.replaceSystemWindowInsets(0, wi.getSystemWindowInsetTop(),
                    wi.getSystemWindowInsetRight(), wi.getSystemWindowInsetBottom());
        }
        lp.leftMargin = wi.getSystemWindowInsetLeft();
        lp.topMargin = wi.getSystemWindowInsetTop();
        lp.rightMargin = wi.getSystemWindowInsetRight();
        lp.bottomMargin = wi.getSystemWindowInsetBottom();
    }

    @TargetApi(20)
    public static int getTopInset(Object insets) {
        return insets != null ? ((WindowInsets) insets).getSystemWindowInsetTop() : 0;
    }

    public static Drawable getDefaultStatusBarBackground(Context context) {
        final TypedArray a = context.obtainStyledAttributes(THEME_ATTRS);
        try {
            return a.getDrawable(0);
        } finally {
            a.recycle();
        }
    }
    @TargetApi(20)
    static class InsetsListener implements View.OnApplyWindowInsetsListener {
        @Override
        public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
            final DrawerLayoutImpl drawerLayout = (DrawerLayoutImpl) v;
            drawerLayout.setChildInsets(insets, insets.getSystemWindowInsetTop() > 0);
            return insets.consumeSystemWindowInsets();
        }
    }
}
