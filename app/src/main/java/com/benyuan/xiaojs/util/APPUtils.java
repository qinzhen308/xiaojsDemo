/*
 * =======================================================================================
 * Package Name :  com.benyuan.xiaojs.util.APPUtils
 * Source Name   :  APPUtils.java
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
 * Date          :  Revised on 16-10-26 下午5:32
 * Abstract    :  Initial version by maxiaobao
 *
 * ========================================================================================
 */

package com.benyuan.xiaojs.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by maxiaobao on 2016/10/26.
 */

public class APPUtils {

    /**
     * 获取应用的全部版本号（versionName + versionCode）
     * @param context
     * @return
     */
    public static String getAPPFullVersion(Context context){

        String fullVersion = "";

        try{
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(info.versionName)
                    .append(".")
                    .append(info.versionCode);

            fullVersion = stringBuilder.toString();

        }catch (Exception e){
            e.printStackTrace();
        }

        return fullVersion;

    }

    /**
     * 获取应用的build号
     * @param context
     * @return
     */
    public static int getAPPVersionCode(Context context){

        int versionCode = 0;

        try{
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionCode = info.versionCode;
        }catch (Exception e){
            e.printStackTrace();
        }

        return versionCode;

    }

    /**
     * 获取应用的可见版本号
     * @param context
     * @return
     */
    public static String getAPPVersionName(Context context){

        String versionName = "";

        try{
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            versionName = info.versionName;
        }catch (Exception e){
            e.printStackTrace();
        }

        return versionName;

    }
}
