package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.Su;
import com.benyuan.xiaojs.data.SecurityManager;
import com.benyuan.xiaojs.data.api.service.APIType;
import com.benyuan.xiaojs.data.api.service.ErrorPrompts;
import com.benyuan.xiaojs.data.AccountDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.LoginInfo;
import com.benyuan.xiaojs.model.LoginParams;
import com.benyuan.xiaojs.model.Privilege;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class LoginRequest extends ServiceRequest {

    public LoginRequest(Context context, APIServiceCallback callback) {

        super(context, callback);

    }

    public void login(LoginParams params) {

        Call<LoginInfo> call = getService().login(params);
        enqueueRequest(APIType.LOGIN, call);

    }

    public void logout(String sessionID) {

        Call<ResponseBody> call = getService().logout(sessionID);
        enqueueRequest(APIType.LOGOUT, call);

    }

    private void initPermission() {

        SecurityManager.requestHavePrivilege(getApiManager().getAppContext(), new APIServiceCallback<Privilege[]>() {
            @Override
            public void onSuccess(Privilege[] privileges) {

                SecurityManager.savePermission(getApiManager().getAppContext(), privileges);

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        }, Su.Permission.COURSE_OPEN_CREATE);

    }


    @Override
    public void doTask(int apiType, Object responseBody) {
        if (apiType == APIType.LOGIN) {
            LoginInfo info = (LoginInfo) responseBody;
            AccountDataManager.saveUserInfo(getApiManager().getAppContext(), info.getUser());

            initPermission();
        }
    }
}
