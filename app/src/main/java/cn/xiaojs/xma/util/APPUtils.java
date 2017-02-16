/*
 * =======================================================================================
 * Package Name :  cn.xiaojs.xma.util.APPUtils
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

package cn.xiaojs.xma.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Looper;

import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.ui.account.LoginActivity;

/**
 * Created by maxiaobao on 2016/10/26.
 */

public class APPUtils {

    public static boolean isBackgroundThread(){

        return !isMainThread();
    }

    public static boolean isMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }

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

    /**
     * return current APP client type
     * @param context
     * @return
     */
    public static int getAPPType(Context context) {

        if (UIUtils.isTablet(context)) {
            return Platform.AppType.TABLET_ANDROID;
        }

        return Platform.AppType.MOBILE_ANDROID;
    }

    public static void lanuchLogin(Context context) {

        AccountDataManager.clearUserInfo(context);
        //jump login page
        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
