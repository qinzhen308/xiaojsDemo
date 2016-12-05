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


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();

        xiaojsService.login(params).enqueue(new Callback<LoginInfo>() {
            @Override
            public void onResponse(Call<LoginInfo> call, Response<LoginInfo> response) {

                onRespones(APIType.LOGIN, response);

                if (response.code() == SUCCESS_CODE) {
                    LoginInfo info = response.body();
                    AccountDataManager.saveUserInfo(getAPIManager().getAppContext(), info.getUser());

                    initPermission();
                }

            }

            @Override
            public void onFailure(Call<LoginInfo> call, Throwable t) {

                onFailures(APIType.LOGIN, t);
            }
        });

    }

    public void logout(String sessionID) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.logout(sessionID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                onRespones(APIType.LOGOUT, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                onFailures(APIType.LOGOUT, t);
            }
        });

    }

    private void initPermission() {

        SecurityManager.requestHavePrivilege(getAPIManager().getAppContext(), new APIServiceCallback<Privilege[]>() {
            @Override
            public void onSuccess(Privilege[] privileges) {

                SecurityManager.savePermission(getAPIManager().getAppContext(),privileges);

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        }, Su.Permission.COURSE_OPEN_CREATE);

    }


}
