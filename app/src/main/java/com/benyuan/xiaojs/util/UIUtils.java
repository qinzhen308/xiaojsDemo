/*
 * =======================================================================================
 * Package Name :  com.benyuan.xiaojs.util.UIUtils
 * Source Name   :  UIUtils.java
 * Abstract       :
 *
 * ---------------------------------------------------------------------------------------
 *
 * Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 * This computer program source code file is protected by copyright law and international
 * treaties. Unauthorized distribution of source code files, programs, or portion of the
 * package, may result in severe civil and criminal penalties, and will be prosecuted to
 * the maximum extent under the law.
 *
 * ---------------------------------------------------------------------------------------
 * Revision History:
 * Date          :  Revised on 16-10-26 上午11:53
 * Abstract    :  Initial version by maxiaobao
 *
 * ========================================================================================
 */

package com.benyuan.xiaojs.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

/**
 * Created by maxiaobao on 2016/10/26.
 */




public class UIUtils {

    /**
     * 判断当前设备是否是平板
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
    }



    public static boolean activityDestoryed(final Activity activity) {

        if(activity == null) {
            return true;
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1){

            return activity.isFinishing();

        }else{
            return activity.isDestroyed();
        }



    }
}
