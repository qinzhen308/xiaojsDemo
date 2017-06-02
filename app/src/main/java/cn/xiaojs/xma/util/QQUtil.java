package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.open.SocialConstants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;

import org.w3c.dom.Text;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;


/**
 * Created by maxiaobao on 2017/2/17.
 */

public class QQUtil {

    /**
     * 返回Tencent实例
     * @param context
     * @return
     */
    public static Tencent getTencent(Context context) {
        return Tencent.createInstance(XiaojsConfig.QQ_APP_ID, context.getApplicationContext());
    }

    /**
     * 分享到QQ
     * @param activity
     * @param tencent
     * @param title
     * @param summary
     * @param url
     * @param listener
     */
    public static void share(Activity activity,
                             Tencent tencent,
                             String title,
                             String summary,
                             String url,
                             String imgUrl,
                             IUiListener listener) {
        Bundle bundle = new Bundle();
        //这条分享消息被好友点击后的跳转URL。
        if(!TextUtils.isEmpty(url)) {
            bundle.putString(SocialConstants.PARAM_TARGET_URL, url);
        }

        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
        bundle.putString(SocialConstants.PARAM_TITLE, title);

        //分享的消息摘要，最长50个字
        bundle.putString(SocialConstants.PARAM_SUMMARY, summary);

        if (!TextUtils.isEmpty(imgUrl)) {
            bundle.putString(SocialConstants.PARAM_TARGET_URL, imgUrl);
            bundle.putString(SocialConstants.PARAM_IMG_URL, imgUrl);
            //bundle.putString(SocialConstants.PARAM_AVATAR_URI, imgUrl);
        }

        String appname = activity.getString(R.string.app_name);

        //标识该消息的来源应用，值为应用名称+AppId。
        bundle.putString(SocialConstants.PARAM_APP_SOURCE, appname + XiaojsConfig.QQ_APP_ID);

        tencent.shareToQQ(activity, bundle , listener);
    }
}
