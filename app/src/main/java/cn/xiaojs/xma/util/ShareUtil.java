package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.common.LogRedirector;
import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.widget.BottomSheet;


import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/3/3.
 */

public class ShareUtil {


    /**
     * 分享图片
     * @param activity
     * @param bitmap
     * @param imgUrl
     */
    public static void shareImage(final Activity activity, final Bitmap bitmap, final String imgUrl) {
        if (XiaojsConfig.DEBUG) {
            Logger.d("the share url" + imgUrl);
        }


        shareDld(activity, new ShareBtnListener() {
            @Override
            public void onClickListener(View v) {
                switch (v.getId()) {
                    case R.id.tv_wechat:
                        IWXAPI iwxapi = WechatUtil.registerToWechat(activity.getApplicationContext());
                        WechatUtil.sharePicture(iwxapi,bitmap,true);
                        break;
                    case R.id.tv_fcircle:
                        IWXAPI iwxapiq = WechatUtil.registerToWechat(activity.getApplicationContext());
                        boolean send = WechatUtil.sharePicture(iwxapiq,bitmap,true);
                        break;
                    case R.id.tv_qq:
                        //QQ
                        Tencent tencent = QQUtil.getTencent(activity.getApplicationContext());
                        QQUtil.share(activity, tencent, "", "", "", imgUrl, new IUiListener() {
                            @Override
                            public void onComplete(Object o) {

                                //Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(UiError uiError) {
                                //Toast.makeText(activity, "分享失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancel() {
                                //Toast.makeText(activity, "分享已取消", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }

            }
        });


    }

    /**
     * 显示分享框
     * @param
     */
    public static void show(final Activity activity, final String title, final String message, final String url) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("the share url" + url);
        }

        shareDld(activity, new ShareBtnListener() {
            @Override
            public void onClickListener(View v) {
                switch (v.getId()) {
                    case R.id.tv_wechat:
                        IWXAPI iwxapi = WechatUtil.registerToWechat(activity.getApplicationContext());
                        WechatUtil.shareWebpage(activity, iwxapi, title, message, url, true);
                        break;
                    case R.id.tv_fcircle:
                        IWXAPI iwxapiq = WechatUtil.registerToWechat(activity.getApplicationContext());
                        boolean send = WechatUtil.shareWebpage(activity, iwxapiq, title, message, url, false);
                        break;
                    case R.id.tv_qq:
                        //QQ
                        Tencent tencent = QQUtil.getTencent(activity.getApplicationContext());
                        QQUtil.share(activity, tencent, title, message, url, "", new IUiListener() {
                            @Override
                            public void onComplete(Object o) {

                                //Toast.makeText(activity, "分享成功", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(UiError uiError) {
                                //Toast.makeText(activity, "分享失败", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancel() {
                                //Toast.makeText(activity, "分享已取消", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                }

            }
        });
    }


    public static void shareDld(final Context context, final ShareBtnListener btnListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_share_window,null);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        //TextView tvXjs = (TextView) view.findViewById(R.id.tv_xfirends);
        //TextView tvClasses = (TextView) view.findViewById(R.id.tv_xfirends);
        TextView tvQq = (TextView) view.findViewById(R.id.tv_qq);
        TextView tvWechat = (TextView) view.findViewById(R.id.tv_wechat);
        TextView tvFriends = (TextView) view.findViewById(R.id.tv_fcircle);

        final BottomSheet bottomSheet = new BottomSheet(context);
        bottomSheet.setTitleVisibility(View.GONE);
        bottomSheet.setContent(view);

        tvQq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnListener.onClickListener(v);
                bottomSheet.dismiss();
            }
        });
        tvWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnListener.onClickListener(v);
                bottomSheet.dismiss();
            }
        });
        tvFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnListener.onClickListener(v);
                bottomSheet.dismiss();

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheet.dismiss();
            }
        });

        bottomSheet.show();
    }


    public interface ShareBtnListener{
        void onClickListener(View v);
    }

}
