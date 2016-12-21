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

import cn.xiaojs.xma.model.User;

import java.io.File;

public class CacheUtil {
    private static final String APP_CACHE_DIR = "/xjs";
    private static final String DATA_CACHE_DIR = "/cache/";// 存放数据缓存文件，如json, 数据bean等
    private static final String IMAGE_CACHE_DIR = "/images";// 存放图片文件缓存文件
    private static final String CRASH_CACHE_DIR = "/crash"; // 程序崩溃日志
    private static final String DOWNLOAD_CACHE_DIR = "/download";// 存放下载的apk
    private static final String FLASH_CACHE_DIR = "splashCache"; // 闪屏信息

    public final static String USER_INFO = "user_info";

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

    private static String mkdirs(String path) {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }

        return path;
    }

}
