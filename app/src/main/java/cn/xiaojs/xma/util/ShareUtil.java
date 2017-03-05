package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

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

        View view = LayoutInflater.from(activity).inflate(R.layout.layout_share_window,null);
        Button cancelBtn = (Button) view.findViewById(R.id.cancel_btn);
        //TextView tvXjs = (TextView) view.findViewById(R.id.tv_xfirends);
        //TextView tvClasses = (TextView) view.findViewById(R.id.tv_xfirends);
        TextView tvQq = (TextView) view.findViewById(R.id.tv_qq);
        TextView tvWechat = (TextView) view.findViewById(R.id.tv_wechat);
        TextView tvFriends = (TextView) view.findViewById(R.id.tv_fcircle);

        tvQq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Tencent tencent = QQUtil.getTencent(activity.getApplicationContext());
                QQUtil.share(activity, tencent, title, message, url, new IUiListener() {
                    @Override
                    public void onComplete(Object o) {

                    }

                    @Override
                    public void onError(UiError uiError) {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        });

        tvWechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IWXAPI iwxapi = WechatUtil.registerToWechat(activity.getApplicationContext());
                WechatUtil.shareWebpage(activity, iwxapi,title,message,url,true);
            }
        });

        tvFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IWXAPI iwxapi = WechatUtil.registerToWechat(activity.getApplicationContext());
                WechatUtil.shareWebpage(activity, iwxapi,title,message,url,false);
            }
        });


        final BottomSheet bottomSheet = new BottomSheet(activity);
        bottomSheet.setTitleVisibility(View.GONE);
        bottomSheet.setContent(view);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheet.dismiss();
            }
        });

        bottomSheet.show();
    }

}
