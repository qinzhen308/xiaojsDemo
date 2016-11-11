package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.GetSubjectResponse;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/10.
 */

public class CategoriesRequest extends ServiceRequest {

    public void getSubject(@NonNull Context context,
                           @NonNull APIServiceCallback<GetSubjectResponse> callback) {

        final WeakReference<APIServiceCallback<GetSubjectResponse>> callbackReference =
                new WeakReference<>(callback);


        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getSubject().enqueue(new Callback<GetSubjectResponse>() {
            @Override
            public void onResponse(Call<GetSubjectResponse> call,
                                   Response<GetSubjectResponse> response) {

                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    GetSubjectResponse info = response.body();

                    APIServiceCallback<GetSubjectResponse> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(info);
                    }


                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.getSubjectPrompt(errorCode);

                    APIServiceCallback<GetSubjectResponse> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<GetSubjectResponse> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the getSubject has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.loginPrompt(errorCode);

                APIServiceCallback<GetSubjectResponse> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });

    }

}
