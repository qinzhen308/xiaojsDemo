package cn.xiaojs.xma.util;

import android.content.Context;
import android.content.Intent;

import cn.xiaojs.xma.XiaojsApplication;
import cn.xiaojs.xma.data.DataManager;

/**
 * Created by maxiaobao on 2017/4/12.
 */

public class MessageUitl {

    public static final String ACTION_PUSH_NOTIFY_OPEN = "cn.xiaojs.xma.action_new_push_opened";
    public static final String EXTRA_NOTIFY_PUSH_ID = "action_new_push_id";

    public static final String ACTION_NEW_PUSH = "action_new_push";


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

}
