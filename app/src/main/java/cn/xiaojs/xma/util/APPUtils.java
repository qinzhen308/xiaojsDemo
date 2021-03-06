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

import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.StringRes;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.Toast;

import com.meituan.android.walle.WalleChannelReader;

import java.io.File;
import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LoginDataManager;
import cn.xiaojs.xma.data.preference.SecurityPref;
import cn.xiaojs.xma.ui.account.LoginActivity;
/**
 * Created by maxiaobao on 2016/10/26.
 */

public class APPUtils {

    /**
     * 是否是生产环境
     * @return
     */
    public static boolean isProEvn() {

        if (XiaojsConfig.CHANNEL.equals(XiaojsConfig.CHANNEL_ENV_DEVTEST)
                || XiaojsConfig.CHANNEL.equals(XiaojsConfig.CHANNEL_ENV_PRE)) {
            return false;
        }

        return true;
    }

    /**
     * 获取渠道号
     * @param context
     * @return
     */
    public static String getChannel(Context context) {
        String channel = WalleChannelReader.getChannel(context.getApplicationContext());
        if (TextUtils.isEmpty(channel)) {
            channel = XiaojsConfig.CHANNEL_ENV_DEVTEST;
        }


        return channel;
        //return "pro";
    }

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
     * 比较版本号
     * @param context
     * @param updateVersion
     * @return
     */
    public static boolean comparisonCode(Context context, String updateVersion) {

        int currentCode = getAPPVersionCode(context);

        if (TextUtils.isEmpty(updateVersion)) return false;

        int updateCode = parseBulidCode(context, updateVersion);


        if (updateCode > 0) {
             return updateCode > currentCode;
        }

        return false;
    }


    public static int parseBulidCode(Context context, String updateVersion) {
        if (TextUtils.isEmpty(updateVersion)) return -1;
        String[] codes = updateVersion.split("\\.");

        //版本号是四位
        if (codes==null || codes.length < XiaojsConfig.VERSION_BITS) return -1;

        String updateCode = codes[XiaojsConfig.VERSION_BITS - 1];
        if (TextUtils.isEmpty(updateCode)) {
            return -1;
        }

        return Integer.valueOf(updateCode);

    }

    public static String getShowVersion(String versionandCode) {
        if (TextUtils.isEmpty(versionandCode)) return "";

        int pos = versionandCode.lastIndexOf(".");
        if (pos < 0) return "";

        return versionandCode.substring(0,pos);
    }


    /**
     * return current APP client type
     * @param context
     * @return
     */
    public static int getAPPType(Context context) {

//        if (UIUtils.isTablet(context)) {
//            return Platform.AppType.TABLET_ANDROID;
//        }

        return Platform.AppType.MOBILE_ANDROID;
    }

    public static void lanuchLogin(Context context) {

        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static void exitAndLogin(Context context,@StringRes int exitTips) {
        LoginDataManager.requestLogoutByAPI(context, null);

        //stop jpush
        JpushUtil.stopPush(context);

        //总是退出成功
        Toast.makeText(context, exitTips, Toast.LENGTH_SHORT).show();
        XiaojsConfig.mLoginUser = null;
        AccountDataManager.clearUserInfo(context);
        SecurityPref.setTicket(context,"");
        SecurityPref.setSFM(context,"");
        //取消友盟授权
//        UMShareAPI.get(context).deleteOauth((Activity) context, SHARE_MEDIA.WEIXIN,null);
//        UMShareAPI.get(context).deleteOauth((Activity) context, SHARE_MEDIA.QQ,null);

        //jump login page
        Intent i = new Intent(context, LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    /**
     * 打开APK包
     * @param context
     * @param filePath
     */
    public static void openPkg(Context context, String filePath) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, XiaojsConfig.FILE_PROVIDER, new File(filePath));
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
    }

    public static String getValueInApplicationInfo(Context context,String name) {
        String value = null;
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);

            value = appInfo.metaData.getString(name);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return value;

    }


    /**
     * 复制内容到剪切板
     * @param context
     * @param content
     */
    public static void clipboard(Context context,String content){
        ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        cm.setText(content);
    }


    public static boolean isAppOnForeground(Context context) {
        ActivityManager mActivityManager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        String mPackageName = context.getPackageName();
        List<ActivityManager.RunningTaskInfo> tasksInfo = mActivityManager.getRunningTasks(1);
        if (tasksInfo.size() > 0) {
            // 应用程序位于堆栈的顶层
            if (mPackageName.equals(tasksInfo.get(0).topActivity
                    .getPackageName())) {
                return true;
            }
        }
        return false;
    }

}
