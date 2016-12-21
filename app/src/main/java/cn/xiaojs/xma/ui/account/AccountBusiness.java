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
import android.view.View;
import android.widget.Toast;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.LoginDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.LoginInfo;
import cn.xiaojs.xma.model.LoginParams;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.util.CacheUtil;
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
                        CacheUtil.saveLoginInfo(loginInfo.getUser());
                        XjsUtils.getSharedPreferences().edit().putLong(XiaojsConfig.KEY_LOGIN_USERNAME,
                                loginParams.getMobile()).commit();


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

}