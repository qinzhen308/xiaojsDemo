package cn.xiaojs.xma.data.download;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;

public class DownloadService extends IntentService {

    public DownloadService() {
        super("Download Doc Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int id = intent.getIntExtra(DownloadProvider.EXTRA_DOWNLOAD_ID,-1);

        if (id == -1){
            if (XiaojsConfig.DEBUG) {
                Logger.d("the download id is invalid,so cancel download...");
            }
            return;
        }

        final DownloadInfo info = DownloadInfo.queryDownloadInfo(this, id);
        if (info == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the download info is null,so cancel download...");
            }

            return;
        }

        final DownloadThread thread = new DownloadThread(this,info);
        thread.downloadSync();


    }

    @Override
    public void onDestroy() {

        if (XiaojsConfig.DEBUG) {
            Logger.d("the DownloadService is onDestroy");
        }

        super.onDestroy();
    }
}
