package cn.xiaojs.xma.data.api;

import android.content.Context;

import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.Collaboration.TokenPair;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2017/2/4.
 */

public class CollaRequest extends ServiceRequest{

    public CollaRequest(Context context, APIServiceCallback callback) {
        super(context, callback);
    }

    public void getUploadTokens(int type, int quantity) {
        Call<TokenPair[]> call = getService().getUploadTokens(type, quantity);
        enqueueRequest(APIType.GET_UPLOAD_TOKENS,call);
    }
}
