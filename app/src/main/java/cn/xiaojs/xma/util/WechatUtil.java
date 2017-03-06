package cn.xiaojs.xma.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;

/**
 * Created by maxiaobao on 2017/2/17.
 */

public class WechatUtil {

    /**
     * 注册微信SDK
     * @param context
     * @return
     */
    public static IWXAPI registerToWechat(Context context) {
        IWXAPI api = WXAPIFactory.createWXAPI(context, XiaojsConfig.WX_APP_ID, true);
        api.registerApp(XiaojsConfig.WX_APP_ID);
        return api;
    }

    /**
     * 分享文字
     * @param api
     * @param text
     * @param session true分享到好友会话，否则分享到朋友圈
     */
    public static boolean shareText(IWXAPI api, String text, boolean session){
        WXTextObject textObject = new WXTextObject();
        textObject.text = text;

        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObject;
        msg.description = text;

        return sendRequest(api,msg,"text",session);

    }

    /**
     * 分享网页
     * @param context
     * @param api
     * @param title
     * @param description
     * @param url
     * @param session true分享到好友会话，否则分享到朋友圈
     */
    public static boolean shareWebpage(Context context,
                                    IWXAPI api,
                                    String title,
                                    String description,
                                    String url,
                                    boolean session) {

        WXWebpageObject webpageObject = new WXWebpageObject();
        webpageObject.webpageUrl = url;

        WXMediaMessage msg = new WXMediaMessage(webpageObject);
        msg.title = title;
        msg.description = description;

        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_share_avator);
        msg.thumbData = BitmapUtils.bmpToByteArray(bmp, true);

        return sendRequest(api,msg,"webpage",session);


    }

    private static boolean sendRequest(IWXAPI api,
                                    WXMediaMessage msg,
                                    String transaction,
                                    boolean session) {

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction =buildTransaction(transaction);
        req.message = msg;
        req.scene = session? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

        boolean send = api.sendReq(req);
        if (XiaojsConfig.DEBUG) {
            Logger.d("wechat send request:"+ (send? "true": "false"));
        }

        return send;

    }

    private static String buildTransaction(final String type) {
        return (type == null) ?
                String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

}
