package cn.xiaojs.xma.util;

import com.avos.avoscloud.im.v2.AVIMClient;
import com.avos.avoscloud.im.v2.AVIMException;
import com.avos.avoscloud.im.v2.callback.AVIMClientCallback;
import com.orhanobut.logger.Logger;

import cn.leancloud.chatkit.LCChatKit;
import cn.xiaojs.xma.XiaojsConfig;

/**
 * Created by maxiaobao on 2017/4/1.
 */

public class LeanCloudUtil {

    public static void open(final String accountId) {

        LCChatKit.getInstance().open(accountId, new AVIMClientCallback() {
            @Override
            public void done(AVIMClient avimClient, AVIMException e) {
                if (e == null) {
                   if (XiaojsConfig.DEBUG) {
                       Logger.d("leancloud open success");
                   }
                } else {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("leancloud open failed");
                    }
                }
            }
        });

    }
}
