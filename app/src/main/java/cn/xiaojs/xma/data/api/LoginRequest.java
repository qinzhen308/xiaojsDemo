package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.content.Intent;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.data.preference.SecurityPref;
import cn.xiaojs.xma.model.security.LoginInfo;
import cn.xiaojs.xma.model.security.LoginParams;
import cn.xiaojs.xma.model.Privilege;

import okhttp3.ResponseBody;
import retrofit2.Call;

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

    public void logout() {

        Call<ResponseBody> call = getService().logout();
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

//    private void initDataCache(Context context) {
//
//        DataManager.init(context);
//    }



    @Override
    public void doTask(int apiType, Object responseBody) {
        if (apiType == APIType.LOGIN) {
            LoginInfo info = (LoginInfo) responseBody;
            AccountDataManager.saveUserInfo(getApiManager().getAppContext(), info.getUser());

            initPermission();

            Intent i = new Intent();
            DataManager.syncData(getContext(),i);

        }
    }
}
