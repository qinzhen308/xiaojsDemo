package cn.xiaojs.xma.util;
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
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;

public class XjsUtils {
    private static Context mAppContext;
    public static int BITMAP_LIMIT_WIDTH;
    public static int BITMAP_LIMIT_HEIGHT;

    public static void init(Context appContext){
        mAppContext = appContext;
        BITMAP_LIMIT_WIDTH = mAppContext.getResources().getDimensionPixelSize(R.dimen.px220) * 3;
        BITMAP_LIMIT_HEIGHT = BITMAP_LIMIT_WIDTH;
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
     * 获取图片显示的最大规格，当前设置最大为690x690
     * @param width
     * @param height
     * @return
     */
    public static int[] getLimitFormat(int width,int height){
        int[] res = new int[2];
        if (width <= BITMAP_LIMIT_WIDTH && height <= BITMAP_LIMIT_HEIGHT){
            res[0] = width;
            res[1] = height;
        }else if (width > BITMAP_LIMIT_WIDTH && height <= BITMAP_LIMIT_HEIGHT){
            int h1 = BITMAP_LIMIT_WIDTH * height / width;
            res[0] = BITMAP_LIMIT_WIDTH;
            res[1] = h1;
        }else if (width <= BITMAP_LIMIT_WIDTH && height > BITMAP_LIMIT_HEIGHT){
            int w1 = width * BITMAP_LIMIT_HEIGHT / height;
            res[0] = w1;
            res[1] = BITMAP_LIMIT_HEIGHT;
        }else {
            int max = Math.max(width,height);
            int min = Math.min(width,height);

            int MAX = BITMAP_LIMIT_WIDTH;
            if (max == height){
                MAX = BITMAP_LIMIT_HEIGHT;
            }

            int tem = MAX * min / max;

            if (max == width){
                res[0] = MAX;
                res[1] = tem;
            }else {
                res[0] = tem;
                res[1] = MAX;
            }

        }

        return res;
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
