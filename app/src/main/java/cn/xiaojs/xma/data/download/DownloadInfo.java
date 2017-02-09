package cn.xiaojs.xma.data.download;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import cn.xiaojs.xma.data.db.DBTables;

/**
 * Created by maxiaobao on 2017/2/8.
 */

public class DownloadInfo {

    private final Context context;

    public String key;
    public long id;
    public String url;
    public String title;
    public int status;
    public String mimeType;
    public String fileName;
    public int numFailed;
    public long lastMod;
    public long totalBytes;
    public long currentBytes;
    public int control;
    public boolean deleted;
    public String description;
    public String eTag;
    public String referer;
    public String cookies;
    public String userAgent;
    public int allowedNetworkTypes;
    public boolean allowRoaming;
    public boolean allowMetered;
    public int flags;
    public String errorMsg;


    public static class Reader {
        private ContentResolver resolver;
        private Cursor cursor;

        public Reader(ContentResolver resolver, Cursor cursor) {
            this.resolver = resolver;
            this.cursor = cursor;
        }

        public void updateFromDatabase(DownloadInfo info) {
            info.id = getLong(DBTables.TDownload._ID);
            info.url = getString(DBTables.TDownload.URL);
            info.title = getString(DBTables.TDownload.TITLE);
            info.status = getInt(DBTables.TDownload.STATUS);
            info.mimeType = getString(DBTables.TDownload.MIME_TYPE);
            info.fileName = getString(DBTables.TDownload.FILE_NAME);
            info.key = getString(DBTables.TDownload.KEY);
            info.lastMod = getLong(DBTables.TDownload.LAST_MOD);
            info.totalBytes = getLong(DBTables.TDownload.TOTAL_BYTES);
            info.currentBytes = getLong(DBTables.TDownload.CURRENT_BYTES);
            info.deleted = getInt(DBTables.TDownload.DELETED) == 1;

            synchronized (this) {
                info.control = getInt(DBTables.TDownload.CONTROL);
            }

        }

        private String getString(String column) {
            int index = cursor.getColumnIndexOrThrow(column);
            String s = cursor.getString(index);
            return (TextUtils.isEmpty(s)) ? null : s;
        }

        private Integer getInt(String column) {
            return cursor.getInt(cursor.getColumnIndexOrThrow(column));
        }

        private Long getLong(String column) {
            return cursor.getLong(cursor.getColumnIndexOrThrow(column));
        }
    }

    public DownloadInfo(Context context) {
        this.context = context;
    }

    public static DownloadInfo queryDownloadInfo(Context context, long downloadId) {
        final ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                ContentUris.withAppendedId(DownloadProvider.DOWNLOAD_URI, downloadId),
                null, null, null, null);
        if (cursor != null){
            final DownloadInfo.Reader reader = new DownloadInfo.Reader(resolver, cursor);
            final DownloadInfo info = new DownloadInfo(context);
            if (cursor.moveToFirst()) {
                reader.updateFromDatabase(info);
            }

            if (!cursor.isClosed()) cursor.close();

            return info;
        }
        return null;
    }

    public Uri getAllDownloadsUri() {
        return ContentUris.withAppendedId(DownloadProvider.DOWNLOAD_URI, id);
    }

    public int queryDownloadStatus() {
        return queryDownloadInt(DBTables.TDownload.STATUS, DownloadStatus.STATUS_PENDING);
    }

    public int queryDownloadControl() {
        return queryDownloadInt(DBTables.TDownload.CONTROL, ControlStatus.CONTROL_RUN);
    }

    public int queryDownloadInt(String columnName, int defaultValue) {

        Cursor cursor = context.getContentResolver().query(getAllDownloadsUri(),
                new String[] { columnName }, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
        }

        return defaultValue;
    }

    public boolean isMeteredAllowed(long totalBytes) {
        return getRequiredNetworkType(totalBytes) != MeterType.TYPE_UNMETERED;
    }

    /**
     * Return the network type constraint required by this download.
     *
     * @see android.app.job.JobInfo.Builder#setRequiredNetworkType(int)
     */
    public int getRequiredNetworkType(long totalBytes) {
        if (!allowMetered) {
            return MeterType.TYPE_UNMETERED;
        }
//        if (mAllowedNetworkTypes == DownloadManager.Request.NETWORK_WIFI) {
//            return JobInfo.NETWORK_TYPE_UNMETERED;
//        }
//        if (totalBytes > mSystemFacade.getMaxBytesOverMobile()) {
//            return JobInfo.NETWORK_TYPE_UNMETERED;
//        }
//        if (totalBytes > mSystemFacade.getRecommendedMaxBytesOverMobile()
//                && mBypassRecommendedSizeLimit == 0) {
//            return JobInfo.NETWORK_TYPE_UNMETERED;
//        }
//        if (!mAllowRoaming) {
//            return JobInfo.NETWORK_TYPE_NOT_ROAMING;
//        }
        return MeterType.TYPE_METERED;
    }

    public static class DownloadStatus{
        public static final int STATUS_PENDING = 0;
        public static final int STATUS_RUNNING = 1;
        public static final int STATUS_PAUSED = 2;
        public static final int STATUS_FAILED = 3;
        public static final int STATUS_SUCCESS = 6;
        public static final int STATUS_CANCELED = 41;
        public static final int STATUS_UNKNOWN_ERROR = 7;

        public static final int STATUS_CANNOT_RESUME = 4;
        public static final int STATUS_FILE_ERROR = 5;
        public static final int STATUS_UNHANDLED_HTTP_CODE = 8;
        public static final int STATUS_TOO_MANY_REDIRECTS = 9;
        public static final int STATUS_WAITING_FOR_NETWORK = 10;
        public static final int STATUS_WAITING_TO_RETRY = 11;
        public static final int STATUS_QUEUED_FOR_WIFI = 12;
        public static final int STATUS_BAD_REQUEST = 13;
        public static final int STATUS_UNHANDLED_REDIRECT = 14;
        public static final int STATUS_HTTP_DATA_ERROR = 15;
        public static final int STATUS_PAUSED_BY_APP = 16;

    }

    public static class ControlStatus {
        public static final int CONTROL_RUN = 0;
        public static final int CONTROL_PAUSED = 1;
    }

    public static class MeterType{
        public static final int TYPE_UNMETERED = 0;
        public static final int TYPE_METERED = 1;
    }
}
