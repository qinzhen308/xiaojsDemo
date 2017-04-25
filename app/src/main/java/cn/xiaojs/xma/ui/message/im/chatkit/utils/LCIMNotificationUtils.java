package cn.xiaojs.xma.ui.message.im.chatkit.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

import com.avos.avospush.notification.NotificationCompat;

import java.util.LinkedList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.NotificationsUtils;
import cn.xiaojs.xma.util.XjsUtils;

/**
 * Created by wli on 15/8/26. Notification 相关的 Util
 */
public class LCIMNotificationUtils {

    private static final int REPLY_NOTIFY_ID = "cn.xiaojs.xma".hashCode();

    /**
     * tag list，用来标记是否应该展示 Notification 比如已经在聊天页面了，实际就不应该再弹出 notification
     */
    private static List<String> notificationTagList = new LinkedList<String>();

    /**
     * 添加 tag 到 tag list，在 MessageHandler 弹出 notification 前会判断是否与此 tag 相等 若相等，则不弹，反之，则弹出
     */
    public static void addTag(String tag) {
        if (!notificationTagList.contains(tag)) {
            notificationTagList.add(tag);
        }
    }

    /**
     * 在 tag list 中 remove 该 tag
     */
    public static void removeTag(String tag) {
        notificationTagList.remove(tag);
    }

    /**
     * 判断是否应该弹出 notification 判断标准是该 tag 是否包含在 tag list 中
     */
    public static boolean isShowNotification(String tag) {
        return !notificationTagList.contains(tag);
    }

    public static void showNotification(Context context, String title, String content, Intent intent) {
        showNotification(context, title, content, null, intent);
    }

    public static void showNotification(Context context, String title, String content, String sound, Intent intent) {
        PendingIntent contentIntent = PendingIntent.getActivity(context, REPLY_NOTIFY_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(context.getApplicationInfo().icon)
                .setContentTitle(title).setAutoCancel(true).setContentIntent(contentIntent)
                .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setContentText(content);
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        if (sound != null && sound.trim().length() > 0) {
            notification.sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + sound);
        }
        manager.notify(REPLY_NOTIFY_ID, notification);

        if (!NotificationsUtils.isNotificationEnabled(context)
                && XjsUtils.isAppOnForeground(context)
                && NotificationsUtils.needOpenNotification()) {
            try {
                //Toast
                Toast.makeText(context, R.string.jmui_open_notification_tips, Toast.LENGTH_LONG).show();
                Intent setIntent = new Intent("android.settings.NOTIFICATION_SETTINGS");
                setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(setIntent);
            } catch (Exception e) {

            }
        }
    }

    public static void cancelNotification(Context context) {
        NotificationManager nMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancel(REPLY_NOTIFY_ID);
    }

    private static void showSettingTipsDialog(final Context context) {
        CommonDialog dialog = new CommonDialog(context);
        dialog.setDesc(R.string.jmui_open_notification_tips);
        dialog.setTitle(R.string.jmui_open_notification_title);
        dialog.setLefBtnText(R.string.temporarily_unable);
        dialog.setRightBtnText(R.string.go_to_setting);
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                Intent intent = new Intent("android.settings.NOTIFICATION_SETTINGS");
                context.startActivity(intent);
            }
        });

        dialog.show();
    }
}
