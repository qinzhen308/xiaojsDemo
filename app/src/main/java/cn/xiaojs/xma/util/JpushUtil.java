package cn.xiaojs.xma.util;

import android.content.Context;

import com.orhanobut.logger.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.xiaojs.xma.model.AliasTags;


/**
 * Created by maxiaobao on 2017/2/24.
 */

public class JpushUtil {

    public static final int STATUS_OK = 0;


    public static void resumePush(Context context) {
        if (JPushInterface.isPushStopped(context.getApplicationContext())) {
            JPushInterface.resumePush(context.getApplicationContext());
        }
    }

    public static void stopPush(Context context) {
        JPushInterface.stopPush(context.getApplicationContext());
    }

    /**
     * 设置别名和标签
     * @param context
     */
    public static void setAliasAndTags(final Context context, AliasTags aliasTags) {

        if (aliasTags == null) return;

        List<String> ptags = aliasTags.getTags();
        Set<String> tags = new HashSet<>();
        if (ptags != null) {
            tags.addAll(ptags);
        }

        String alias = "xxiao123";


        JPushInterface.setAlias(context,(int)System.currentTimeMillis(), alias);
        Logger.d("--------xxiao123 over---------------");

        JPushInterface.setTags(context,(int)System.currentTimeMillis(), tags);
    }


    /**
     * 根据状态码，是否决定重试操作
     * @param status
     * @return
     */
    public static boolean isRetry(int status) {

        switch(status){
            case 898030:
            case 898000:
            case 899000:
            case 899030:
            case 800009:
            case 800012:
            case 800013:
            case 871504:
            case 871300:
            case 871102:
            case 871201://响应超时
            case 871103:
            case 871104:
                return true;
        }

        return false;
    }
}
