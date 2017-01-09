package cn.xiaojs.xma.data.api;

import android.content.Context;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.Privilege;
import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.model.security.AuthenticateStatus;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2016/12/4.
 */

public class SecurityRequest extends ServiceRequest {


    public SecurityRequest(Context context, APIServiceCallback callback) {
        super(context,callback);
    }

    public void havePrivileges(Privilege[] privileges) {

        String privilegesPath = objectToJsonString(privileges);
        if (XiaojsConfig.DEBUG) {
            Logger.d("privilegesPath: %s",privilegesPath);
        }

        Call<Privilege[]> call = getService().havePrivileges(privilegesPath);
        enqueueRequest(APIType.HAVE_PROVILEGES,call);

    }


    public void checkSession() {
        Call<AuthenticateStatus> call = getService().checkSession();
        enqueueRequest(APIType.CHECK_SESSION,call);
    }
}
