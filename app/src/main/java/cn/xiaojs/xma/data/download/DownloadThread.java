package cn.xiaojs.xma.data.download;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

import cn.xiaojs.xma.data.db.DBTables;
import cn.xiaojs.xma.util.IOUtils;

import static android.text.format.DateUtils.SECOND_IN_MILLIS;
import static cn.xiaojs.xma.data.download.DConstants.LOG_TAG;
import static cn.xiaojs.xma.data.download.DownloadInfo.ControlStatus.CONTROL_PAUSED;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_CANCELED;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_CANNOT_RESUME;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_FILE_ERROR;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_HTTP_DATA_ERROR;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_PAUSED_BY_APP;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_QUEUED_FOR_WIFI;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_TOO_MANY_REDIRECTS;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_UNHANDLED_HTTP_CODE;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_UNKNOWN_ERROR;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_WAITING_FOR_NETWORK;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_WAITING_TO_RETRY;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_MOVED_PERM;
import static java.net.HttpURLConnection.HTTP_MOVED_TEMP;
import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_PARTIAL;
import static java.net.HttpURLConnection.HTTP_PRECON_FAILED;
import static java.net.HttpURLConnection.HTTP_SEE_OTHER;
import static java.net.HttpURLConnection.HTTP_UNAVAILABLE;
import static okhttp3.internal.http.StatusLine.HTTP_TEMP_REDIRECT;

/**
 * Created by maxiaobao on 2017/2/8.
 */

public class DownloadThread extends Thread {

    private static final String NOT_CANCELED = DBTables.TDownload.STATUS + " != '" + STATUS_CANCELED + "'";
    private static final String NOT_DELETED = DBTables.TDownload.DELETED + " == '0'";
    private static final String NOT_PAUSED = "(" + DBTables.TDownload.CONTROL + " IS NULL OR "
            + DBTables.TDownload.CONTROL + " != '" + CONTROL_PAUSED + "')";

    private static final String SELECTION_VALID = NOT_CANCELED + " AND " + NOT_DELETED + " AND "
            + NOT_PAUSED;

    private static final int DEFAULT_TIMEOUT = (int) (20 * SECOND_IN_MILLIS);

    private final Context context;
    private final DownloadService downloadService;
    private final SystemFacade systemFacade;
    //private final DownloadNotifier notifier;

    private final DownloadInfo downloadInfo;
    private final DownloadInfoDelta infoDelta;

    private volatile boolean shutdownRequested;
    private boolean mMadeProgress = false;

    private long mSpeed;
    private long mSpeedSampleStart;
    private long mSpeedSampleBytes;

    private long mLastUpdateBytes = 0;
    private long mLastUpdateTime = 0;

    private final long mId;




    public DownloadThread(DownloadService service,DownloadInfo info) {
        this.context = service;
        this.downloadService = service;
        this.downloadInfo = info;

        this.mId = info.id;

        this.infoDelta = new DownloadInfoDelta(info);
        this.systemFacade = new RealSystemFacade(context);

    }

    public void requestShutdown() {
        shutdownRequested = true;
    }

    public void downloadSync() {
        exeDownload();
    }

    @Override
    public void run() {
        exeDownload();
    }

    private void exeDownload() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        try {

            //TODO 判断是否已被下载完成

            infoDelta.status = DownloadInfo.DownloadStatus.STATUS_RUNNING;
            infoDelta.writeToDatabase();

            //TODO 检查网络状态

            executeDownload();

            infoDelta.status = DownloadInfo.DownloadStatus.STATUS_SUCCESS;

            // If we just finished a chunked file, record total size
            if (infoDelta.totalBytes == -1) {
                infoDelta.totalBytes = infoDelta.currentBytes;
            }



        } catch (StopRequestException e) {
            infoDelta.status = e.getStatus();
            infoDelta.errorMsg = e.getMessage();

            // Nobody below our level should request retries, since we handle
            // failure counts at this level.
            if (infoDelta.status == STATUS_WAITING_TO_RETRY) {
                throw new IllegalStateException("Execution should always throw final error codes");
            }

            // Some errors should be retryable, unless we fail too many times.
            if (isStatusRetryable(infoDelta.status)) {
                if (mMadeProgress) {
                    infoDelta.numFailed = 1;
                } else {
                    infoDelta.numFailed += 1;
                }

//                if (infoDelta.numFailed < DConstants.MAX_RETRIES) {
//                    final NetworkInfo info = mSystemFacade.getNetworkInfo(mNetwork, mInfo.mUid,
//                            mIgnoreBlocked);
//                    if (info != null && info.getType() == mNetworkType && info.isConnected()) {
//                        // Underlying network is still intact, use normal backoff
//                        infoDelta.status = STATUS_WAITING_TO_RETRY;
//                    } else {
//                        // Network changed, retry on any next available
//                        infoDelta.status = STATUS_WAITING_FOR_NETWORK;
//                    }
//
//                    if (infoDelta.eTag == null && mMadeProgress) {
//                        // However, if we wrote data and have no ETag to verify
//                        // contents against later, we can't actually resume.
//                        infoDelta.status = STATUS_CANNOT_RESUME;
//                    }
//                }
            }

            // If we're waiting for a network that must be unmetered, our status
            // is actually queued so we show relevant notifications
            if (infoDelta.status == STATUS_WAITING_FOR_NETWORK
                    && !downloadInfo.isMeteredAllowed(infoDelta.totalBytes)) {
                infoDelta.status = STATUS_QUEUED_FOR_WIFI;
            }
        } catch (Throwable t) {
            infoDelta.status = STATUS_UNKNOWN_ERROR;
            infoDelta.errorMsg = t.toString();

            logError("Failed: " + infoDelta.errorMsg, t);

        } finally {
            finalizeDestination();
            infoDelta.writeToDatabase();
        }

        if (isStatusCompleted(infoDelta.status)) {
            // If download was canceled, we already sent requested intent when
            // deleted in the provider
//            if (infoDelta.status != STATUS_CANCELED) {
//                downloadInfo.sendIntentIfRequested();
//            }
//            if (downloadInfo.shouldScanFile(infoDelta.status)) {
//                DownloadScanner.requestScanBlocking(context, downloadInfo.id, infoDelta.fileName,
//                        infoDelta.mimeType);
//            }
            //nothing to do

        } else if (infoDelta.status == STATUS_WAITING_TO_RETRY
                || infoDelta.status == STATUS_WAITING_FOR_NETWORK
                || infoDelta.status == STATUS_QUEUED_FOR_WIFI) {
            //scheduleJob
        }

        //mJobService.jobFinishedInternal(mParams, false);


    }

    private void executeDownload() throws StopRequestException{

        final boolean resuming = infoDelta.currentBytes != 0;

        URL url;
        try {
            url = new URL(infoDelta.url);
        } catch (MalformedURLException e) {
            throw new StopRequestException(DownloadInfo.DownloadStatus.STATUS_BAD_REQUEST, e);
        }

        int redirectionCount = 0;
        while (redirectionCount++ < DConstants.MAX_REDIRECTS) {

            HttpURLConnection conn = null;
            checkConnectivity();
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(false);
                conn.setConnectTimeout(DEFAULT_TIMEOUT);
                conn.setReadTimeout(DEFAULT_TIMEOUT);

                addRequestHeaders(conn, resuming);

                final int responseCode = conn.getResponseCode();
                switch(responseCode) {
                    case HTTP_OK:
                        if (resuming) {
                            throw new StopRequestException(
                                    STATUS_CANNOT_RESUME,
                                    "Expected partial, but received OK");
                        }

                        parseOkHeaders(conn);
                        transferData(conn);
                        return;
                    case HTTP_PARTIAL:
                        if (!resuming) {
                            throw new StopRequestException(
                                    STATUS_CANNOT_RESUME, "Expected OK, but received partial");
                        }
                        transferData(conn);
                        return;
                    case HTTP_MOVED_PERM:
                    case HTTP_MOVED_TEMP:
                    case HTTP_SEE_OTHER:
                    case HTTP_TEMP_REDIRECT:
                        final String location = conn.getHeaderField("Location");
                        url = new URL(url, location);
                        if (responseCode == HTTP_MOVED_PERM) {
                            // Push updated URL back to database
                            infoDelta.url = url.toString();
                        }
                        continue;

                    case HTTP_PRECON_FAILED:
                        throw new StopRequestException(
                                STATUS_CANNOT_RESUME, "Precondition failed");

//                    case HTTP_REQUESTED_RANGE_NOT_SATISFIABLE:
//                        throw new StopRequestException(
//                                STATUS_CANNOT_RESUME, "Requested range not satisfiable");

                    case HTTP_UNAVAILABLE:
                        parseUnavailableHeaders(conn);
                        throw new StopRequestException(
                                HTTP_UNAVAILABLE, conn.getResponseMessage());

                    case HTTP_INTERNAL_ERROR:
                        throw new StopRequestException(
                                HTTP_INTERNAL_ERROR, conn.getResponseMessage());

                    default:
                        StopRequestException.throwUnhandledHttpError(
                                responseCode, conn.getResponseMessage());

                }

            } catch (IOException e) {
                if (e instanceof ProtocolException
                        && e.getMessage().startsWith("Unexpected status line")) {
                    throw new StopRequestException(STATUS_UNHANDLED_HTTP_CODE, e);
                } else {
                    // Trouble with low-level sockets
                    throw new StopRequestException(STATUS_HTTP_DATA_ERROR, e);
                }
            } finally {
                if (conn != null) conn.disconnect();
            }

        }

        throw new StopRequestException(STATUS_TOO_MANY_REDIRECTS, "Too many redirects");

    }

    private void transferData(HttpURLConnection conn) throws StopRequestException {
        // To detect when we're really finished, we either need a length, closed
        // connection, or chunked encoding.
        final boolean hasLength = infoDelta.totalBytes != -1;
        final boolean isConnectionClose = "close".equalsIgnoreCase(
                conn.getHeaderField("Connection"));
        final boolean isEncodingChunked = "chunked".equalsIgnoreCase(
                conn.getHeaderField("Transfer-Encoding"));

        final boolean finishKnown = hasLength || isConnectionClose || isEncodingChunked;
        if (!finishKnown) {
            throw new StopRequestException(
                    STATUS_CANNOT_RESUME, "can't know size of download, giving up");
        }


//        DrmManagerClient drmClient = null;
//        ParcelFileDescriptor outPfd = null;
//        FileDescriptor outFd = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            try {
                in = conn.getInputStream();
            } catch (IOException e) {
                throw new StopRequestException(STATUS_HTTP_DATA_ERROR, e);
            }

           //TODO 检查可用空间

           // Start streaming data, periodically watch for pause/cancel
           // commands and checking disk space as needed.
           transferData(in, out);


        } finally {

            IOUtils.closeQuietly(in);
            try {
                if (out != null) out.flush();
            } catch (IOException e) {
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
    }


    private void transferData(InputStream in, OutputStream out)
            throws StopRequestException {
        final byte buffer[] = new byte[DConstants.BUFFER_SIZE];
        while (true) {

            if (shutdownRequested) {
                throw new StopRequestException(STATUS_HTTP_DATA_ERROR,
                        "Local halt requested; job probably timed out");
            }

            int len = -1;
            try {
                len = in.read(buffer);
            } catch (IOException e) {
                throw new StopRequestException(
                        STATUS_HTTP_DATA_ERROR, "Failed reading response: " + e, e);
            }

            if (len == -1) {
                break;
            }

            //TODO When streaming, ensure space before each write

            try {
                out.write(buffer, 0, len);

                mMadeProgress = true;
                infoDelta.currentBytes += len;

                updateProgress();

            } catch (IOException e) {
                throw new StopRequestException(STATUS_FILE_ERROR, e);
            }
        }

        // Finished without error; verify length if known
        if (infoDelta.totalBytes != -1 && infoDelta.currentBytes != infoDelta.totalBytes) {
            throw new StopRequestException(STATUS_HTTP_DATA_ERROR, "Content length mismatch");
        }
    }

    /**
     * Report download progress through the database if necessary.
     */
    private void updateProgress() throws IOException, StopRequestException {
        final long now = SystemClock.elapsedRealtime();
        final long currentBytes = infoDelta.currentBytes;

        final long sampleDelta = now - mSpeedSampleStart;
        if (sampleDelta > 500) {
            final long sampleSpeed = ((currentBytes - mSpeedSampleBytes) * 1000)
                    / sampleDelta;

            if (mSpeed == 0) {
                mSpeed = sampleSpeed;
            } else {
                mSpeed = ((mSpeed * 3) + sampleSpeed) / 4;
            }

            // Only notify once we have a full sample window
//            if (mSpeedSampleStart != 0) {
//                notifier.notifyDownloadSpeed(mId, mSpeed);
//            }

            mSpeedSampleStart = now;
            mSpeedSampleBytes = currentBytes;
        }

        final long bytesDelta = currentBytes - mLastUpdateBytes;
        final long timeDelta = now - mLastUpdateTime;
        if (bytesDelta > DConstants.MIN_PROGRESS_STEP && timeDelta > DConstants.MIN_PROGRESS_TIME) {
            // fsync() to ensure that current progress has been flushed to disk,
            // so we can always resume based on latest database information.
            //outFd.sync();

            infoDelta.writeToDatabaseOrThrow();

            mLastUpdateBytes = currentBytes;
            mLastUpdateTime = now;
        }
    }

    private void parseUnavailableHeaders(HttpURLConnection conn) {
//        long retryAfter = conn.getHeaderFieldInt("Retry-After", -1);
//        retryAfter = MathUtils.constrain(retryAfter, Constants.MIN_RETRY_AFTER,
//                Constants.MAX_RETRY_AFTER);
//        mInfoDelta.mRetryAfter = (int) (retryAfter * SECOND_IN_MILLIS);
    }

    /**
     * Called just before the thread finishes, regardless of status, to take any
     * necessary action on the downloaded file.
     */
    private void finalizeDestination() {
        if (isStatusError(infoDelta.status)) {

            // Delete if local file
            if (infoDelta.fileName != null) {
                new File(infoDelta.fileName).delete();
                infoDelta.fileName = null;
            }

        } else if (isStatusSuccess(infoDelta.status)) {
            // When success, open access if local file

            if (infoDelta.fileName != null) {
                //TODO 文件转换
            }
        }
    }

    public boolean isStatusSuccess(int status) {
        return (status >= 200 && status < 300);
    }

    public boolean isStatusError(int status) {
        return (status >= 400 && status < 600);
    }

    public boolean isStatusCompleted(int status) {
        return (status >= 200 && status < 300) || (status >= 400 && status < 600);
    }



    /**
     * Check if current connectivity is valid for this request.
     */
    private void checkConnectivity() throws StopRequestException {
        // checking connectivity will apply current policy
       //TODO
    }

    /**
     * Add custom headers for this download to the HTTP request.
     */
    private void addRequestHeaders(HttpURLConnection conn, boolean resuming) {

        // Defeat connection reuse, since otherwise servers may continue
        // streaming large downloads after cancelled.
        conn.setRequestProperty("Connection", "close");

        //TODO
    }

    /**
     * Process response headers from first server response. This derives its
     * filename, size, and ETag.
     */
    private void parseOkHeaders(HttpURLConnection conn) throws StopRequestException {
//        if (infoDelta.fileName == null) {
//            final String contentDisposition = conn.getHeaderField("Content-Disposition");
//            final String contentLocation = conn.getHeaderField("Content-Location");
//
//            try {
//                infoDelta.fileName = SyncStateContract.Helpers.generateSaveFile(context, infoDelta.url,
//                        mInfo.mHint, contentDisposition, contentLocation, mInfoDelta.mMimeType,
//                        mInfo.mDestination);
//            } catch (IOException e) {
//                throw new StopRequestException(
//                        Downloads.Impl.STATUS_FILE_ERROR, "Failed to generate filename: " + e);
//            }
//        }

        if (infoDelta.mimeType == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                infoDelta.mimeType = Intent.normalizeMimeType(conn.getContentType());
            }else{
                infoDelta.mimeType = normalizeMimeType(conn.getContentType());
            }
        }

        final String transferEncoding = conn.getHeaderField("Transfer-Encoding");
        if (transferEncoding == null) {
            infoDelta.totalBytes = getHeaderFieldLong(conn, "Content-Length", -1);
        } else {
            infoDelta.totalBytes = -1;
        }

        infoDelta.eTag = conn.getHeaderField("ETag");

        infoDelta.writeToDatabaseOrThrow();

        // Check connectivity again now that we know the total size
        checkConnectivity();
    }

    private static long getHeaderFieldLong(URLConnection conn, String field, long defaultValue) {
        try {
            return Long.parseLong(conn.getHeaderField(field));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }


    public static boolean isStatusRetryable(int status) {
        switch (status) {
            case STATUS_HTTP_DATA_ERROR:
            case HTTP_UNAVAILABLE:
            case HTTP_INTERNAL_ERROR:
            case STATUS_FILE_ERROR:
                return true;
            default:
                return false;
        }
    }

    public static String normalizeMimeType(String type) {
        if (type == null) {
            return null;
        }

        type = type.trim().toLowerCase(Locale.ROOT);

        final int semicolonIndex = type.indexOf(';');
        if (semicolonIndex != -1) {
            type = type.substring(0, semicolonIndex);
        }
        return type;
    }

    private void logDebug(String msg) {
        Log.d(LOG_TAG, "[" + mId + "] " + msg);
    }

    private void logWarning(String msg) {
        Log.w(LOG_TAG, "[" + mId + "] " + msg);
    }

    private void logError(String msg, Throwable t) {
        Log.e(LOG_TAG, "[" + mId + "] " + msg, t);
    }



    private class DownloadInfoDelta {
        public String url;
        public String fileName;
        public String mimeType;
        public int status;
        public int numFailed;
        //public int mRetryAfter;
        public long totalBytes;
        public long currentBytes;
        public String eTag;

        public String errorMsg;

        public DownloadInfoDelta(DownloadInfo info) {
            url = info.url;
            fileName = info.fileName;
            mimeType = info.mimeType;
            status = info.status;
            numFailed = info.numFailed;
            //mRetryAfter = info.mRetryAfter;
            totalBytes = info.totalBytes;
            currentBytes = info.currentBytes;
            eTag = info.eTag;
        }

        public void writeToDatabase() {
            context.getContentResolver().update(downloadInfo.getAllDownloadsUri(),
                    buildContentValues(),
                    null,
                    null);
        }

        public void writeToDatabaseOrThrow() throws StopRequestException {
            if (context.getContentResolver().update(downloadInfo.getAllDownloadsUri(),
                    buildContentValues(), SELECTION_VALID, null) == 0) {
                if (downloadInfo.queryDownloadControl() == CONTROL_PAUSED) {
                    throw new StopRequestException(STATUS_PAUSED_BY_APP, "Download paused!");
                } else {
                    throw new StopRequestException(STATUS_CANCELED, "Download deleted or missing!");
                }
            }
        }

        private ContentValues buildContentValues() {
            final ContentValues values = new ContentValues();

            values.put(DBTables.TDownload.URL, url);
            values.put(DBTables.TDownload.FILE_NAME, fileName);
            values.put(DBTables.TDownload.MIME_TYPE, mimeType);
            values.put(DBTables.TDownload.STATUS, status);
            values.put(DBTables.TDownload.NUM_FAILED, numFailed);
            //values.put(DownloadInfo.DownloadColumn.REDIRECT_COUNT, mRetryAfter);
            values.put(DBTables.TDownload.TOTAL_BYTES, totalBytes);
            values.put(DBTables.TDownload.CURRENT_BYTES, currentBytes);
            values.put(DBTables.TDownload.ETAG, eTag);

            values.put(DBTables.TDownload.LAST_MOD, systemFacade.currentTimeMillis());
            //values.put(DownloadInfo.DownloadColumn.ERROR_MSG, mErrorMsg);

            return values;
        }


    }
}
