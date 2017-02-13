package cn.xiaojs.xma.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.File;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.db.DBTables;
import cn.xiaojs.xma.data.download.DownloadProvider;
import cn.xiaojs.xma.data.preference.DataPref;

/**
 * Created by maxiaobao on 2017/2/10.
 */

public class DownloadManager {

    public static final String DEL_ACTION = "xiaojs.action.del.download";

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

    /**
     * 判断当前是否可以进行下载
     * @param context
     * @return true 可以下载，否则不可下载
     */
    public static boolean allowDownload(Context context) {
        return DataPref.allowDownload(context);
    }

    /**
     * 删除下载
     * @param context
     * @param downloadId
     */
    public static void delDownload(Context context, long downloadId, @Nullable String localPath) {
        shutdownDownload(context, downloadId);

        String where = new StringBuilder(DBTables.TDownload._ID)
                .append("='")
                .append(downloadId)
                .append("'")
                .toString();
        context.getContentResolver().delete(DownloadProvider.DOWNLOAD_URI, where, null);

        if (!TextUtils.isEmpty(localPath)){
            new File(localPath).delete();
        }

    }

    private static boolean startDownload(Context context,ContentValues values) {
        Uri uri = context.getContentResolver().insert(DownloadProvider.DOWNLOAD_URI,values);
        return uri == null? false : true;
    }

    private static void shutdownDownload(Context context, long downloadId) {
        Intent i = new Intent();
        i.putExtra(DownloadProvider.EXTRA_DOWNLOAD_ID,downloadId);
        i.setAction(DEL_ACTION);
        context.sendBroadcast(i);
    }
}
