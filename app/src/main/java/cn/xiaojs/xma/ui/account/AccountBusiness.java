package cn.xiaojs.xma.ui.account;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/2
 * Desc:
 *
 * ======================================================================================== */

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.LoginDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.security.LoginInfo;
import cn.xiaojs.xma.model.security.LoginParams;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.util.XjsUtils;

public class AccountBusiness {
    public final static String USER_INFO = "user_info";


    public static void login(final Activity activity, final LoginParams loginParams, OnLoginListener listener) {
        login(activity, loginParams, null, listener);
    }

    public static void login(final Activity activity, final LoginParams loginParams) {
        login(activity, loginParams, null, null);
    }

    public static void login(final Activity activity, final LoginParams loginParams, final View submitBtn,
                             final OnLoginListener listener) {
        if (submitBtn != null) {
            submitBtn.setEnabled(false);
        }

        try {
            LoginDataManager.requestLoginByAPI(activity, loginParams, new APIServiceCallback<LoginInfo>() {

                @Override
                public void onSuccess(LoginInfo loginInfo) {
                    if (submitBtn != null) {
                        submitBtn.setEnabled(true);
                    }

                    if (loginInfo != null) {
                        XiaojsConfig.mLoginUser = loginInfo.getUser();
                        XjsUtils.getSharedPreferences().edit().putLong(XiaojsConfig.KEY_LOGIN_USERNAME,
                                loginParams.getMobile()).commit();
                        imLogin();
                        //enter main page
                        Intent intent = new Intent(activity, MainActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);

                        if (listener != null) {
                            listener.onLogin(true);
                        }
                    } else {
                        if (listener != null) {
                            listener.onLogin(false);
                        }
                    }

                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    if (submitBtn != null) {
                        submitBtn.setEnabled(true);
                    }

                    if (listener != null) {
                        listener.onLogin(false);
                    }
                    Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            //do nothing
            if (submitBtn != null) {
                submitBtn.setEnabled(true);
            }

            if (listener != null) {
                listener.onLogin(false);
            }
        }
    }

    public interface OnLoginListener {
        public void onLogin(boolean succ);
    }

    public static void imLogin(){
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
