package cn.xiaojs.xma.data;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.db.DBTables;
import cn.xiaojs.xma.data.download.DownloadProvider;

/**
 * Created by maxiaobao on 2017/2/10.
 */

public class DownloadManager {

    /**
     * 下载文件
     * @param context
     * @param fileName 文件全名
     * @param key 文件的KEY
     * @param url 下载地址
     * @param mimeType 文件类型
     * @param iconUrl 文件ICON
     * @return
     */
    public static boolean enqueueDownload(Context context,
                                        @NonNull String fileName,
                                        @NonNull String key,
                                        @NonNull String url,
                                        @Nullable String mimeType,
                                        @Nullable String iconUrl) {

        if (TextUtils.isEmpty(fileName) || TextUtils.isEmpty(key) || TextUtils.isEmpty(url)) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the filename or key or url is null,so cancel download...");
            }
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DBTables.TDownload.FILE_NAME,fileName);
        values.put(DBTables.TDownload.KEY,key);
        values.put(DBTables.TDownload.URL,url);
        values.put(DBTables.TDownload.MIME_TYPE,mimeType);
        values.put(DBTables.TDownload.ICON,iconUrl);

        return startDownload(context, values);
    }


    private static boolean startDownload(Context context,ContentValues values) {
        Uri uri = context.getContentResolver().insert(DownloadProvider.DOWNLOAD_URI,values);
        return uri == null? false : true;
    }
}
