package cn.xiaojs.xma.data.api;

import android.content.Context;

import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.UpgradeManager;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;

import cn.xiaojs.xma.model.account.Location;
import cn.xiaojs.xma.model.security.LoginInfo;
import cn.xiaojs.xma.model.security.LoginParams;

import cn.xiaojs.xma.util.JpushUtil;
import cn.xiaojs.xma.util.LeanCloudUtil;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class LoginRequest extends ServiceRequest {

    private LoginParams loginParams;
    private boolean register;

    public LoginRequest(Context context, APIServiceCallback callback) {

        super(context, callback);

    }

    public void setFromRegister(boolean register) {
        this.register = register;
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

//    private void initPermission() {
//
//        SecurityManager.requestHavePrivilege(getApiManager().getAppContext(), new APIServiceCallback<Privilege[]>() {
//            @Override
//            public void onSuccess(Privilege[] privileges) {
//
//                SecurityManager.savePermission(getApiManager().getAppContext(), privileges);
//
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//
//            }
//        }, Su.Permission.COURSE_OPEN_CREATE);
//
//    }

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

            DataManager.lanuchInitDataService(getContext(), info.contactGroups);

            //jpush
            JpushUtil.resumePush(getContext());

            //open lean cloud
            LeanCloudUtil.open(info.getUser().getId());
        }
    }
}
