package cn.xiaojs.xma.data.download;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.DownloadManager;
import cn.xiaojs.xma.data.preference.DataPref;

public class DownloadService extends IntentService {

    private long downlodId;
    private DownloadThread downloadThread;

    public DownloadService() {
        super("Download Doc Service");
    }


    @Override
    public void onCreate() {
        super.onCreate();

        DataPref.setAllowDownload(this,false);

        IntentFilter filter = new IntentFilter();
        filter.addAction(DownloadManager.DEL_ACTION);
        registerReceiver(receiver,filter);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        downlodId = intent.getLongExtra(DownloadProvider.EXTRA_DOWNLOAD_ID,-1);
        if (downlodId == -1){
            if (XiaojsConfig.DEBUG) {
                Logger.d("the download id is invalid,so cancel download...");
            }
            return;
        }

        final DownloadInfo info = DownloadInfo.queryDownloadInfo(this, downlodId);
        if (info == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the download info is null,so cancel download...");
            }

            return;
        }

        if (XiaojsConfig.DEBUG) {
            Logger.d("the download is start ID:"+ downlodId);
        }

        downloadThread = new DownloadThread(this,info);
        downloadThread.downloadSync();


    }

    @Override
    public void onDestroy() {

        if (XiaojsConfig.DEBUG) {
            Logger.d("the DownloadService is onDestroy");
        }
        unregisterReceiver(receiver);

        DataPref.setAllowDownload(this,true);

        super.onDestroy();
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (TextUtils.isEmpty(action) || !action.equals(DownloadManager.DEL_ACTION))
                return;

            long deleteId = intent.getLongExtra(DownloadProvider.EXTRA_DOWNLOAD_ID,-1);
            if (XiaojsConfig.DEBUG) {
                Logger.d("Received delte notify id:" + deleteId);
            }

            if (deleteId == -1 || deleteId != downlodId) return;

            if (downloadThread !=null)
                downloadThread.requestShutdown();
        }
    };
}
