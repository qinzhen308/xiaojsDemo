/*
 * =======================================================================================
 * Package Name :  cn.xiaojs.xma.util.UIUtils
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

package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.text.TextUtils;

import java.util.ArrayList;

import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.ui.common.ImageViewActivity;

/**
 * Created by maxiaobao on 2016/10/26.
 */




public class UIUtils {


    public static boolean isLandspace(Context context) {
        return getCurrentOrientation(context) == Configuration.ORIENTATION_LANDSCAPE ? true : false;
    }


    public static int getCurrentOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

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

    public static void toImageViewActivity(Activity context, Dynamic.DynPhoto[] photos){
        if (photos != null && photos.length > 0){
            ArrayList<String> us = new ArrayList<>();
            for ( Dynamic.DynPhoto photo:photos) {
                us.add(photo.name);
            }
            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putExtra(ImageViewActivity.IMAGE_PATH_KEY,us);
            context.startActivity(intent);
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    public static void toImageViewActivity(Activity context, ArrayList<String> photos,String title){
        if (photos != null && photos.size() > 0){
            Intent intent = new Intent(context, ImageViewActivity.class);
            intent.putExtra(ImageViewActivity.IMAGE_PATH_KEY,photos);
            if(title!=null){
                intent.putExtra(ImageViewActivity.IMAGE_TITLE_KEY,title);
            }
            context.startActivity(intent);
            context.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }


}
