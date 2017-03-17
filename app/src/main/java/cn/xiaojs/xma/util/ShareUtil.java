package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Context;
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
     * 显示分享框
     * @param
     */
    public static void show(final Activity activity, final String title, final String message, final String url){

        if (XiaojsConfig.DEBUG) {
            Logger.d("the share url" + url);
        }

        View view = LayoutInflater.from(activity).inflate(R.layout.layout_share_window,null);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        //TextView tvXjs = (TextView) view.findViewById(R.id.tv_xfirends);
        //TextView tvClasses = (TextView) view.findViewById(R.id.tv_xfirends);
        TextView tvQq = (TextView) view.findViewById(R.id.tv_qq);
        TextView tvWechat = (TextView) view.findViewById(R.id.tv_wechat);
        TextView tvFriends = (TextView) view.findViewById(R.id.tv_fcircle);

        final BottomSheet bottomSheet = new BottomSheet(activity);
        bottomSheet.setTitleVisibility(View.GONE);
        bottomSheet.setContent(view);


        tvQq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tencent tencent = QQUtil.getTencent(activity.getApplicationContext());
                QQUtil.share(activity, tencent, title, message, url, new IUiListener() {
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

                bottomSheet.dismiss();
            }
        });

        tvWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IWXAPI iwxapi = WechatUtil.registerToWechat(activity.getApplicationContext());
                WechatUtil.shareWebpage(activity, iwxapi,title,message,url,true);

                bottomSheet.dismiss();
            }
        });

        tvFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IWXAPI iwxapi = WechatUtil.registerToWechat(activity.getApplicationContext());
                boolean send = WechatUtil.shareWebpage(activity, iwxapi,title,message,url,false);
//                String tip = send? "分享成功": "分享失败";
//                Toast.makeText(activity, tip, Toast.LENGTH_SHORT).show();
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

}
