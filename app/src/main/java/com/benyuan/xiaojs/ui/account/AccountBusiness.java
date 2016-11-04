package com.benyuan.xiaojs.ui.account;
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
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.LoginDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.LoginInfo;
import com.benyuan.xiaojs.model.LoginParams;
import com.benyuan.xiaojs.ui.MainActivity;
import com.benyuan.xiaojs.util.XjsUtils;

public class AccountBusiness {

    public static void login(final Activity activity, final LoginParams loginParams) {
        login(activity, loginParams, null);
    }

    public static void login(final Activity activity, final LoginParams loginParams, final View submitBtn) {
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
                        XjsUtils.getSharedPreferences().edit().putString(XiaojsConfig.KEY_LOGIN_PASSWORD,
                                loginParams.getPassword()).commit();

                        //enter main page
                        activity.startActivity(new Intent(activity, MainActivity.class));
                    }

                }

                @Override
                public void onFailure(String errorCode) {
                    if (submitBtn != null) {
                        submitBtn.setEnabled(true);
                    }

                    Toast.makeText(activity, Errors.getInternalErrorMessage(errorCode), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            //do nothing
            if (submitBtn != null) {
                submitBtn.setEnabled(true);
            }
        }
    }
}
