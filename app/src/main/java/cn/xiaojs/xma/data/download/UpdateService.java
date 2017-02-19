package cn.xiaojs.xma.data.download;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.util.IOUtils;

import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_FILE_ERROR;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_HTTP_DATA_ERROR;
import static cn.xiaojs.xma.data.download.DownloadInfo.DownloadStatus.STATUS_UNHANDLED_HTTP_CODE;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_OK;

public class UpdateService extends IntentService {

    private DownloadNotifier notifier;

    public UpdateService() {
        super("UpdateService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        String url = intent.getStringExtra(DConstants.EXTRA_URL);
        if (!TextUtils.isEmpty(url)) {

            boolean success = false;
            try {

                if (notifier == null) {
                    notifier = new DownloadNotifier(getApplicationContext());
                } else {
                    notifier.removeNotify();
                }
                notifier.initNotify(getResources().getString(R.string.downloading),
                        DConstants.UPDATE_NOTIFY_ID);

                success = startDownloadClient(url);


            } catch (StopRequestException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (success) {
                    notifier.removeNotify();
                } else {
                    notifier.showErrorNotify();
                }
            }
        }

    }

    @Override
    public void onDestroy() {

        if (XiaojsConfig.DEBUG) {
            Logger.d("the update service is ondestroy");
        }

        super.onDestroy();


    }

    private boolean startDownloadClient(String durl) throws StopRequestException {
        boolean isDownloadOk = false;

        String filePath = "";

        URL url;
        try {
            url = new URL(durl);
        } catch (MalformedURLException e) {
            throw new StopRequestException(DownloadInfo.DownloadStatus.STATUS_BAD_REQUEST, e);
        }

        HttpURLConnection urlConn = null;
        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            urlConn = (HttpURLConnection) url.openConnection();

            final int responseCode = urlConn.getResponseCode();
            if (responseCode != HTTP_OK) {
                throw StopRequestException.throwUnhandledHttpError(
                        responseCode, urlConn.getResponseMessage());
            }
            int fileSize = -1;
            if (url != null) {
                fileSize = urlConn.getContentLength();
            }

            in = new BufferedInputStream(urlConn.getInputStream());

            File sdCardDir = getApplicationContext().getExternalFilesDir(null);
            File saveFile = new File(sdCardDir, DConstants.DOWNLOAD_CLIENT_FILE_NAME);
            filePath = saveFile.getAbsolutePath();

            FileOutputStream fos = new FileOutputStream(saveFile);

            out = new BufferedOutputStream(fos);

            final byte buffer[] = new byte[DConstants.BUFFER_SIZE];
            long currentBytes = 0;
            long smartPro = -1;
            long between = 0;
            while (true) {

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

                try {
                    out.write(buffer, 0, len);

                    currentBytes += len;

                    long progress = (currentBytes * 100) / fileSize;
                    progress = Math.abs(progress);

                    if (smartPro < progress) {

                        long sep = progress - smartPro;
                        between = between + sep;
                        if (between > 5 || progress > 95 || progress < 3) {
                            notifier.updateNotify((int)progress);
                            between = 0;
                        }

                    }

                    smartPro = progress;

                } catch (IOException e) {
                    throw new StopRequestException(STATUS_FILE_ERROR, e);
                }

            }
            isDownloadOk = true;

        } catch (IOException e) {
            if (e instanceof ProtocolException
                    && e.getMessage().startsWith("Unexpected status line")) {
                throw new StopRequestException(STATUS_UNHANDLED_HTTP_CODE, e);
            } else {
                // Trouble with low-level sockets
                throw new StopRequestException(STATUS_HTTP_DATA_ERROR, e);
            }
        } finally {
            if (urlConn != null) urlConn.disconnect();

            IOUtils.closeQuietly(in);
            try {
                if (out != null) out.flush();
            } catch (IOException e) {
            } finally {
                IOUtils.closeQuietly(out);
            }

            if (isDownloadOk) {
                openPkg(filePath);
                return true;
            }
            return false;
        }
    }


    private void openPkg(String filepath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // intent.setDataAndType(Uri.fromFile(new File(filepath)),
        // "application/vnd.android.package-archive");
        intent.setDataAndType(Uri.parse("file://" + filepath),
                "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }

}
