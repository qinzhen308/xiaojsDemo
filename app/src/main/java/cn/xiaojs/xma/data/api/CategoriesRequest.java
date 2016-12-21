package cn.xiaojs.xma.data.api;

import android.content.Context;

import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.CSubject;

import retrofit2.Call;

/**
 * Created by maxiaobao on 2016/11/10.
 */

public class CategoriesRequest extends ServiceRequest {

    public CategoriesRequest(Context context, APIServiceCallback callback) {

        super(context, callback);

    }

    public void getSubject() {

        Call<CSubject> call = getService().getSubject();
        enqueueRequest(APIType.GET_SUBJECT,call);

    }

}
