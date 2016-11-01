package com.benyuan.xiaojs.util;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.benyuan.xiaojs.XiaojsConfig;

public class XjsUtils {
    private static Context mAppContext;

    public static void init(Context appContext){
        mAppContext = appContext;
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = getDisplayMetrics(context).density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = getDisplayMetrics(context).density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context context, float spValue) {
        final float fontScale = getDisplayMetrics(context).scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 根据view显示输入法
     *
     * @param ctx
     * @param view
     */
    public static void showIMM(Context ctx, View view) {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏输入法
     *
     * @param ctx
     * @param token
     */
    public static void hideIMM(Context ctx, IBinder token) {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(token,
                InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    public static SharedPreferences getSharedPreferences() {
        return mAppContext.getSharedPreferences(XiaojsConfig.XIAOJS_PREFERENCE_NAME, Context.MODE_PRIVATE);
    }
}
