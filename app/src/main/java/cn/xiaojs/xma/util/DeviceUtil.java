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
 * Author:zhanghui
 * Date:2016/10/28
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.TouchDelegate;
import android.view.View;

public class DeviceUtil {
    public static int getScreenWidth(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static int dip2px(Context context, float dip) {
        return (int) (context.getResources().getDisplayMetrics().density * dip + 0.5f);
    }

    /**
     * 扩大控件的点击范围
     *
     * @param view        需要扩大点击范围的控件
     * @param expandWidth 向四周扩大的值
     */
    public static void expandViewTouch(final View view, final int expandWidth) {

        ((View) view.getParent()).post(new Runnable() {
            @Override
            public void run() {
                Rect bounds = new Rect();
                view.setEnabled(true);
                view.getHitRect(bounds);

                bounds.top -= expandWidth;
                bounds.bottom += expandWidth;
                bounds.left -= expandWidth;
                bounds.right += expandWidth;

                TouchDelegate touchDelegate = new TouchDelegate(bounds, view);

                if (View.class.isInstance(view.getParent())) {
                    ((View) view.getParent()).setTouchDelegate(touchDelegate);
                }
            }
        });
    }

    public static int getColor(Context context, int resId) {
        Resources res = context.getResources();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return res.getColor(resId);
        } else {
            return res.getColor(resId, null);
        }
    }

//    public static int getPor(){
//        Random random = new Random();
//        return SourceConfig.pors[random.nextInt(72) + 1];
//    }
//
//    public static int getLesson(){
//        Random random = new Random();
//        return SourceConfig.lessons[random.nextInt(56) + 1];
//    }

    public static String getSignature(){
        return String.valueOf(System.currentTimeMillis());
    }
}
