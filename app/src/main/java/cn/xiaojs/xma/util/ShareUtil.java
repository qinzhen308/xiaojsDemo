package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.social.tool.UMImageMark;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.widget.BottomSheet;


import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/3/3.
 */

public class ShareUtil {

    static {
        PlatformConfig.setWeixin(XiaojsConfig.WX_APP_ID, XiaojsConfig.WX_APP_KEY);
        PlatformConfig.setQQZone(XiaojsConfig.QQ_APP_ID, XiaojsConfig.QQ_APP_KEY);
    }


    /**
     * 分享图片
     *
     * @param activity
     * @param bitmap
     * @param imgUrl
     */
    public static void shareImage(final Activity activity, final Bitmap bitmap, final String imgUrl, final String title) {
        if (XiaojsConfig.DEBUG) {
            Logger.d("the share url" + imgUrl);
        }


        shareDld(activity, new ShareBtnListener() {
            @Override
            public void onClickListener(View v) {
                switch (v.getId()) {
                    case R.id.tv_wechat:
                        IWXAPI iwxapi = WechatUtil.registerToWechat(activity.getApplicationContext());
                        WechatUtil.sharePicture(iwxapi, bitmap, true);

                        break;
                    case R.id.tv_fcircle:
                        IWXAPI iwxapiq = WechatUtil.registerToWechat(activity.getApplicationContext());
                        boolean send = WechatUtil.sharePicture(iwxapiq, bitmap, false);
                        break;
                    case R.id.tv_qq:
                        //QQ
                        Tencent tencent = QQUtil.getTencent(activity.getApplicationContext());
                        QQUtil.share(activity, tencent, title, "", "", imgUrl, new IUiListener() {
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


    public static void shareByUmeng(final Activity activity, final String imgUrl, final String title) {
        if (XiaojsConfig.DEBUG) {
            Logger.d("the share url" + imgUrl);
        }
        final UMImage umImage = new UMImage(activity, imgUrl);
        umImage.setThumb(new UMImage(activity, R.drawable.ic_launcher));
        umImage.setTitle(title);
        shareDld(activity, new ShareBtnListener() {
            @Override
            public void onClickListener(View v) {
                switch (v.getId()) {
                    case R.id.tv_wechat:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN).withMedia(umImage).share();
                        break;
                    case R.id.tv_fcircle:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).withMedia(umImage).share();
                        break;
                    case R.id.tv_qq:
                        //QQ
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ).withMedia(umImage).share();
                        break;
                    case R.id.tv_url:
                        //QQ
                        APPUtils.clipboard(activity, imgUrl);
                        ToastUtil.showToast(activity, "链接已复制到剪切板");
                        break;
                }

            }
        });
    }

    public static void shareByUmeng(final Activity activity, final Bitmap imgBm, final String title) {

        final UMImage umImage = new UMImage(activity, imgBm);
        umImage.compressFormat = Bitmap.CompressFormat.PNG;
        umImage.setThumb(new UMImage(activity, imgBm));
        umImage.setTitle(title);
        sharePicDld(activity, new ShareBtnListener() {
            @Override
            public void onClickListener(View v) {
                switch (v.getId()) {
                    case R.id.tv_wechat:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN).withMedia(umImage).share();
                        break;
                    case R.id.tv_fcircle:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).withMedia(umImage).share();
                        break;
                    case R.id.tv_qq:
                        //QQ
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ).withMedia(umImage).share();
                        break;
                    case R.id.tv_url:
                        //保存图片
                        BitmapUtils.saveImgToFile(activity,imgBm);
                        break;
                }

            }
        });
    }

    public static void shareUrlByUmeng(final Activity activity, final String title, final String message, final String url) {

        final UMWeb web = new UMWeb(url);
        UMImage umImage = new UMImage(activity, R.drawable.ic_share_avator);
        web.setThumb(umImage);
        web.setTitle(title);
        web.setDescription(message);
        shareDld(activity, new ShareBtnListener() {
            @Override
            public void onClickListener(View v) {
                switch (v.getId()) {
                    case R.id.tv_wechat:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN).withMedia(web).share();
                        break;
                    case R.id.tv_fcircle:
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE).withMedia(web).share();
                        break;
                    case R.id.tv_qq:
                        //QQ
                        new ShareAction(activity).setPlatform(SHARE_MEDIA.QQ).withMedia(web).share();
                        break;
                    case R.id.tv_url:
                        //QQ
                        APPUtils.clipboard(activity, url);
                        ToastUtil.showToast(activity, "链接已复制到剪切板");
                        break;
                }

            }
        });
    }


    /**
     * 显示分享框
     *
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
        shareDld(context, btnListener, true);
    }


    public static void shareDld(final Context context, final ShareBtnListener btnListener, boolean isNeedCopyUrl) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_share_window, null);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        //TextView tvXjs = (TextView) view.findViewById(R.id.tv_xfirends);
        //TextView tvClasses = (TextView) view.findViewById(R.id.tv_xfirends);
        TextView tvQq = (TextView) view.findViewById(R.id.tv_qq);
        TextView tvWechat = (TextView) view.findViewById(R.id.tv_wechat);
        TextView tvFriends = (TextView) view.findViewById(R.id.tv_fcircle);
        TextView tvUrl = (TextView) view.findViewById(R.id.tv_url);
        if (!isNeedCopyUrl) {
            tvUrl.setVisibility(View.GONE);
        }

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
        tvUrl.setOnClickListener(new View.OnClickListener() {
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

    public static void sharePicDld(final Context context, final ShareBtnListener btnListener) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_share_window, null);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        //TextView tvXjs = (TextView) view.findViewById(R.id.tv_xfirends);
        //TextView tvClasses = (TextView) view.findViewById(R.id.tv_xfirends);
        TextView tvQq = (TextView) view.findViewById(R.id.tv_qq);
        TextView tvWechat = (TextView) view.findViewById(R.id.tv_wechat);
        TextView tvFriends = (TextView) view.findViewById(R.id.tv_fcircle);
        TextView tvUrl = (TextView) view.findViewById(R.id.tv_url);
        tvUrl.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_share_picture,0,0);
        tvUrl.setText("保存图片");
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
        tvUrl.setOnClickListener(new View.OnClickListener() {
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


    public interface ShareBtnListener {
        void onClickListener(View v);
    }

}
