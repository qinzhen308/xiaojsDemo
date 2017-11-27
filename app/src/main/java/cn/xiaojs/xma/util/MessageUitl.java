package cn.xiaojs.xma.util;

import android.content.Context;
import android.content.Intent;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushLocalNotification;
import cn.xiaojs.xma.XiaojsApplication;
import cn.xiaojs.xma.data.DataManager;

/**
 * Created by maxiaobao on 2017/4/12.
 */

public class MessageUitl {

    public static final String ACTION_PUSH_NOTIFY_OPEN = "cn.xiaojs.xma.action_new_push_opened";
    public static final String EXTRA_NOTIFY_PUSH_ID = "action_new_push_id";

    public static final String ACTION_NEW_PUSH = "action_new_push";
    public static final String ACTION_PUSH_ENTER_MESSAGE_CENTER = "cn.xiaojs.xma.action_push_enter_msg_center";


    public static void newMessageCome(Context context) {
        DataManager.setHasMessage(context,true);
        context.sendBroadcast(new Intent(XiaojsApplication.ACTION_NEW_MESSAGE));
    }

    public static void newPushCome(Context context) {
        DataManager.setHasMessage(context,true);
        context.sendBroadcast(new Intent(ACTION_NEW_PUSH));
    }

    public static Intent getPushNotificationIntent(Context context, String notfyId) {
        Intent intent = new Intent();
        intent.setAction(ACTION_PUSH_NOTIFY_OPEN);
        intent.putExtra(EXTRA_NOTIFY_PUSH_ID, notfyId);
        intent.setPackage(context.getPackageName());
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        return intent;
    }

    public static void setHasMessage(Context context,boolean has) {
        DataManager.setHasMessage(context,has);
    }


    public static void lanuchMessageCenter(Context context) {
        Intent ifarIntent = new Intent();
        ifarIntent.setAction(MessageUitl.ACTION_PUSH_ENTER_MESSAGE_CENTER);
        ifarIntent.setPackage(context.getPackageName());
        ifarIntent.addCategory(Intent.CATEGORY_DEFAULT);
        context.startActivity(ifarIntent);
    }


    public static void createLocalNotify(Context context, String title, String summary) {
        JPushLocalNotification notification = new JPushLocalNotification();
        notification.setTitle(title);
        notification.setContent(summary);
        notification.setNotificationId(System.currentTimeMillis());
        JPushInterface.addLocalNotification(context,notification);
    }

}
