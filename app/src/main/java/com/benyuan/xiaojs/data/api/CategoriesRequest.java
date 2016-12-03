package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.service.APIType;
import com.benyuan.xiaojs.data.api.service.ErrorPrompts;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.CSubject;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/10.
 */

public class CategoriesRequest extends ServiceRequest {

    public CategoriesRequest(Context context,APIServiceCallback callback) {

        super(context,callback);

    }

    public void getSubject() {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getSubject().enqueue(new Callback<CSubject>() {
            @Override
            public void onResponse(Call<CSubject> call,
                                   Response<CSubject> response) {

                onRespones(APIType.GET_SUBJECT,response);
            }

            @Override
            public void onFailure(Call<CSubject> call, Throwable t) {

                onFailures(APIType.GET_SUBJECT,t);
            }
        });

    }

}
