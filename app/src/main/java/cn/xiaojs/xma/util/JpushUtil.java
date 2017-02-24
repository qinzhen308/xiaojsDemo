package cn.xiaojs.xma.util;

import android.util.Log;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;

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
                    Log.i("imLogin", "im登录成功" + status);
                } else {
                    Log.i("imLogin", "im登录失败" + status);
                }
            }
        });
    }
}
