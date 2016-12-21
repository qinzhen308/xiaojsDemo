package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.APIEntity;
import cn.xiaojs.xma.model.RegisterInfo;
import cn.xiaojs.xma.model.VerifyCode;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2016/10/31.
 */

public class RegisterRequest extends ServiceRequest {

    public RegisterRequest(Context context, APIServiceCallback callback) {

        super(context, callback);

    }

    public void register(@NonNull RegisterInfo registerInfo) {

        Call<ResponseBody> call = getService().accountRegister(registerInfo);
        enqueueRequest(APIType.REGISTER, call);
    }


    public void validateCode(int method, long mobile, int verifycode) {

        Call<APIEntity> call = getService().validateCode(method, mobile, verifycode);
        enqueueRequest(APIType.VALIDATE_CODE, call);

    }


    public void sendVerifyCode(int method, long mobile) {

        Call<VerifyCode> call = getService().sendVerifyCode(method, mobile);
        enqueueRequest(APIType.VERIFY_MOBILE, call);

    }

}
