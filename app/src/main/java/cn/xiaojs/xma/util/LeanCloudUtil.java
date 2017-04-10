package cn.xiaojs.xma.util;

import android.content.Context;
import android.content.Intent;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatKit;
import cn.xiaojs.xma.ui.message.im.chatkit.activity.LCIMConversationActivity;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMConstants;

/**
 * Created by maxiaobao on 2017/4/1.
 */

public class LeanCloudUtil {

    public static void lanchChatPage(Context context, String accountId) {
        Intent intent = new Intent(context, LCIMConversationActivity.class);
        intent.putExtra(LCIMConstants.PEER_ID, accountId);
        context.startActivity(intent);
    }

    public static void open(final String accountId) {

        LCChatKit.getInstance().open(accountId, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                   if (XiaojsConfig.DEBUG) {
                       Logger.d("leancloud open success: " + accountId);
                   }
                } else {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("leancloud open failed: "  + accountId);
                    }
                }
            }
        });

    }


    public static void close() {

        LCChatKit.getInstance().close(new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("close success");
                    }
                }else {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("close failed");
                    }
                }
            }
        });

    }
}
