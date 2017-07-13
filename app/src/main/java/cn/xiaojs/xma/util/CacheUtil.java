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
 * Author:huangyong
 * Date:2016/11/22
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

import cn.xiaojs.xma.model.account.User;

public class CacheUtil {
    private static final String APP_CACHE_DIR = "/xjs";
    private static final String DATA_CACHE_DIR = "/cache/";// 存放数据缓存文件，如json, 数据bean等
    private static final String WHITE_BOARD_CACHE_DIR = "/whiteboard/";
    private static final String IMAGE_CACHE_DIR = "/images";// 存放图片文件缓存文件
    private static final String CRASH_CACHE_DIR = "/crash"; // 程序崩溃日志
    private static final String DOWNLOAD_CACHE_DIR = "/download";// 存放下载的apk
    private static final String FLASH_CACHE_DIR = "splashCache"; // 闪屏信息
    private static final String IMAGE_DOWNLOAD_DIR = "/downloads/images/";//图片下载文件夹

    public final static String USER_INFO = "user_info";
    public final static String SUFFIX_JPG = ".jpg";
    public final static String SUFFIX_BMP = ".bmp";
    public final static int DEFAULT_QUALITY = 90;

    public static String getAppCacheRoot() {
        return FileUtil.SDCARD_PATH + APP_CACHE_DIR;
    }

    public static String getAppDataCacheRoot() {
        return getAppCacheRoot() + DATA_CACHE_DIR;
    }

    public static void saveLoginInfo(User user) {
        if (user != null) {
            String path = mkdirs(getAppDataCacheRoot());
            ObjectSerializableUtil.writeObject(user, path + USER_INFO);
        }
    }

    public static User getLoginInfo() {
        return ObjectSerializableUtil.readObject(getAppDataCacheRoot() + USER_INFO);
    }

    public static String saveWhiteboard(Bitmap bmp, String name) {
        if (bmp == null || TextUtils.isEmpty(name)) {
            return null;
        }

        String path = mkdirs(getAppCacheRoot() + WHITE_BOARD_CACHE_DIR);
        String t = String.valueOf(System.currentTimeMillis());
        path = path + name + t +  SUFFIX_JPG;
        BitmapUtils.saveImage(bmp, path, DEFAULT_QUALITY, false);

        return path;
    }

    private static String mkdirs(String path) {
        File f = new File(path);
        if (!f.exists()) {
            boolean succ = f.mkdirs();
            Log.i("aaa", "succ" + succ);
        }

        return path;
    }

    public static String downloadImage(Bitmap bitmap){
        if (bitmap == null)
            return null;
        String path = mkdirs(getAppCacheRoot() + IMAGE_DOWNLOAD_DIR);
        String time = String.valueOf(System.currentTimeMillis());
        StringBuilder sb = new StringBuilder();
        sb.append(path);
        sb.append(time);
        sb.append(SUFFIX_JPG);
        return BitmapUtils.saveImage(bitmap,sb.toString(),DEFAULT_QUALITY,false);
    }

    public static String copyImgFileToGallery(Context context, File file){
        String savedPath="";
        try {
            String fileDir = mkdirs(getAppCacheRoot()+IMAGE_DOWNLOAD_DIR);
            String fileName = new StringBuilder()
                    .append(System.currentTimeMillis())
                    .append("_")
                    .append(".png")
                    .toString();
            savedPath=fileDir+"/"+fileName;
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + savedPath)));
        }catch (Exception e){

        }

        return savedPath;
    }

}
