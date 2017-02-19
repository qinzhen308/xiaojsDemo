package cn.xiaojs.xma.data.download;


import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;

import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/2/9.
 */

public class DownloadNotifier {

    private final Context context;
    private int notifyId;
    private NotificationManager notificationMgr;
    private Notification.Builder builder;

    public DownloadNotifier(Context context) {
        this.context = context;
        this.notificationMgr = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

    }

    public void initNotify(String title, int notifyID) {

        notifyId = notifyID;
        if (builder == null){
            builder = new Notification.Builder(context);
        }

        builder.setTicker(context.getResources().getString(
                R.string.downloading));
        builder.setContentTitle(title);
        builder.setSmallIcon(android.R.drawable.stat_sys_download);
        builder.setContentText("0%");
        builder.setAutoCancel(false);

        builder.setProgress(0, 0, true);

        notificationMgr.notify(notifyID, builder.build());
    }

    public void updateNotify(int progress) {
        builder.setContentText(progress + "%");
        builder.setProgress(100, progress, false);
        notificationMgr.notify(notifyId, builder.build());
    }

    public void removeNotify() {
        notificationMgr.cancel(notifyId);
    }

    public void showErrorNotify() {

        Resources resources = context.getResources();

        builder.setTicker(resources.getString(R.string.download_error));
        builder.setContentTitle(resources.getString(R.string.app_name));
        builder.setContentText(resources.getString(
                R.string.download_error));
        builder.setSmallIcon(android.R.drawable.stat_sys_download_done);
        builder.setAutoCancel(true);

        notificationMgr.notify(notifyId, builder.build());
    }
}
