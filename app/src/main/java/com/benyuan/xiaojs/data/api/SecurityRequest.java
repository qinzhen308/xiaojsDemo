package com.benyuan.xiaojs.data.api;

import android.content.Context;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.APIType;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.Privilege;
import com.orhanobut.logger.Logger;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/12/4.
 */

public class SecurityRequest extends ServiceRequest {


    public SecurityRequest(Context context, APIServiceCallback callback) {
        super(context,callback);
    }

    public void havePrivileges(String session,Privilege[] privileges) {

        String privilegesPath = objectToJsonString(privileges);
        if (XiaojsConfig.DEBUG) {
            Logger.d("privilegesPath: %s",privilegesPath);
        }

        Call<Privilege[]> call = getService().havePrivileges(session,privilegesPath);
        enqueueRequest(APIType.HAVE_PROVILEGES,call);

    }

}
