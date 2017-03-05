package cn.xiaojs.xma.util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.model.AliasTags;

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


    /**
     * 设置别名和标签
     * @param context
     */
    public static void setAliasAndTags(final Context context, AliasTags aliasTags, TagAliasCallback callback) {

        if (aliasTags == null) return;

        List<String> ptags = aliasTags.getTags();
        Set<String> tags = new HashSet<>();
        if (ptags != null) {
            tags.addAll(ptags);
        }

        JPushInterface.setAliasAndTags(context, aliasTags.getAlias(), tags, callback);
    }
}
