package cn.xiaojs.xma.util;

import android.util.Log;

import com.orhanobut.logger.Logger;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import cn.xiaojs.xma.XiaojsConfig;

/**
 * Created by maxiaobao on 2017/2/24.
 */

public class JpushUtil {

    /**
     * 登陆JPUSH
     */
    public static void loginJpush() {
        //FIXME 临时测试用
        JMessageClient.login("123456", "123456", new BasicCallback() {
            @Override
            public void gotResult(int status, String desc) {
                if (status == 0) {
                    //登录成功
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("im登录成功" + status);
                    }

                } else {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("im登录失败" + status);
                    }
                }
            }
        });
    }
}
