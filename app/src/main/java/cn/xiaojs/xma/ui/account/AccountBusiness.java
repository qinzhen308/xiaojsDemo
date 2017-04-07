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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.api.BasicCallback;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LoginDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.account.User;
import cn.xiaojs.xma.model.security.LoginInfo;
import cn.xiaojs.xma.model.security.LoginParams;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.util.XjsUtils;

public class AccountBusiness {
    public final static String USER_INFO = "user_info";


    public static void login(final Activity activity, boolean register, final LoginParams loginParams, OnLoginListener listener) {
        login(activity, register,loginParams, null, listener);
    }

    public static void login(final Activity activity,boolean register, final LoginParams loginParams) {
        login(activity, register, loginParams, null, null);
    }

    public static void login(final Activity activity,boolean register, final LoginParams loginParams, final View submitBtn,
                             final OnLoginListener listener) {
        if (submitBtn != null) {
            submitBtn.setEnabled(false);
        }

        try {
            LoginDataManager.requestLoginByAPI(activity, register,loginParams, new APIServiceCallback<LoginInfo>() {

                @Override
                public void onSuccess(LoginInfo loginInfo) {
                    if (submitBtn != null) {
                        submitBtn.setEnabled(true);
                    }

                    if (loginInfo != null) {

                        User user = loginInfo.getUser();
                        if (user != null
                                && user.getName() != null
                                && user.getAccount() != null
                                && user.getAccount().getBasic() != null) {

                            user.getAccount().name = user.getName();
                            user.getAccount().getBasic().setName(user.getName());
                        }

                        XiaojsConfig.mLoginUser = user;
                        XiaojsConfig.AVATOR_TIME = String.valueOf(System.currentTimeMillis());
                        //AccountPref.setAvatorTime(activity, XiaojsConfig.AVATOR_TIME);

                        AccountDataManager.setPhone(activity,String.valueOf(loginParams.getMobile()));
                        //enter main page
                        Intent intent = new Intent(activity, MainActivity.class);
                        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(intent);

                        if (listener != null) {
                            listener.onLogin(true);
                        }

                        AccountPref.setLoginMd5Pwd(activity, loginParams.getPassword());
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
}
