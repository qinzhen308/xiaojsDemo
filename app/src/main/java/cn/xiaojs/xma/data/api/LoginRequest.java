package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.content.Intent;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.UpgradeManager;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.data.loader.SyncService;
import cn.xiaojs.xma.data.preference.SecurityPref;
import cn.xiaojs.xma.model.AliasTags;
import cn.xiaojs.xma.model.account.Location;
import cn.xiaojs.xma.model.security.LoginInfo;
import cn.xiaojs.xma.model.security.LoginParams;
import cn.xiaojs.xma.model.Privilege;

import cn.xiaojs.xma.util.JpushUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class LoginRequest extends ServiceRequest {

    private LoginParams loginParams;

    public LoginRequest(Context context, APIServiceCallback callback) {

        super(context, callback);

    }

    public void login(LoginParams params) {

        loginParams = params;

        Location location = AccountDataManager.getLocation(getContext());
        if (location !=null && params !=null) {
            params.geo = location;
        }


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
            UpgradeManager.setUpgrade(getContext(),info.getUpgrade());

            initPermission();

            Intent i = new Intent(getContext(), SyncService.class);
            DataManager.syncData(getContext(),i);

            //jush
            if (loginParams != null){
                String up = String.valueOf(loginParams.getMobile());
                JpushUtil.loginJpush(up,up);
            }


        }
    }
}
