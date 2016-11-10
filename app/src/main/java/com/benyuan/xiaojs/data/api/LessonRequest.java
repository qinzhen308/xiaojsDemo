package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.CLResponse;
import com.benyuan.xiaojs.model.CreateLesson;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.Empty;
import com.benyuan.xiaojs.model.GetLessonsResponse;
import com.benyuan.xiaojs.model.Pagination;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class LessonRequest extends ServiceRequest {


    public void createLiveLesson(Context context, String sessionID, CreateLesson lesson, @NonNull final APIServiceCallback callback) {

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.createLiveLesson(sessionID, lesson).enqueue(new Callback<CLResponse>() {
            @Override
            public void onResponse(Call<CLResponse> call, Response<CLResponse> response) {
                int responseCode = response.code();

                if (responseCode == 200) {

                    CLResponse info = response.body();
                    callback.onSuccess(info);

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (TextUtils.isEmpty(errorBody)) {

                        String errorMessage = ErrorPrompts.createLessonPrompt(Errors.NO_ERROR);
                        callback.onFailure(Errors.NO_ERROR, errorMessage);

                    } else {

                        String errorCode = ApiManager.parseErrorBody(errorBody);
                        String errorMessage = ErrorPrompts.createLessonPrompt(errorCode);
                        callback.onFailure(errorCode, errorMessage);

                    }

                }
            }

            @Override
            public void onFailure(Call<CLResponse> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the createLiveLession request has occur exception");
                }

                String errorMessage = ErrorPrompts.createLessonPrompt(Errors.NO_ERROR);
                callback.onFailure(Errors.NO_ERROR, errorMessage);
            }
        });

    }


    public void getLessons(Context context,
                           @NonNull String sessionID,
                           @NonNull Criteria criteria,
                           @NonNull Pagination pagination,
                           @NonNull final APIServiceCallback<GetLessonsResponse> callback) {


        String criteriaJsonstr = ApiManager.objectToJsonString(criteria);
        String paginationJsonstr = ApiManager.objectToJsonString(pagination);

        if(XiaojsConfig.DEBUG){
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }


        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getLessons(sessionID, criteriaJsonstr, paginationJsonstr).enqueue(new Callback<GetLessonsResponse>() {
            @Override
            public void onResponse(Call<GetLessonsResponse> call,
                                   Response<GetLessonsResponse> response) {
                int responseCode = response.code();

                if (responseCode == 200) {

                    GetLessonsResponse lessonsResponse = response.body();
                    callback.onSuccess(lessonsResponse);

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (TextUtils.isEmpty(errorBody)) {

                        String errorMessage = ErrorPrompts.getLessonPrompt(Errors.NO_ERROR);
                        callback.onFailure(Errors.NO_ERROR, errorMessage);

                    } else {

                        String errorCode = ApiManager.parseErrorBody(errorBody);
                        String errorMessage = ErrorPrompts.getLessonPrompt(errorCode);
                        callback.onFailure(errorCode, errorMessage);

                    }

                }
            }

            @Override
            public void onFailure(Call<GetLessonsResponse> call, Throwable t) {
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the get lessons request has occur exception");
                }

                String errorMessage = ErrorPrompts.getLessonPrompt(Errors.NO_ERROR);
                callback.onFailure(Errors.NO_ERROR, errorMessage);


            }
        });

    }


    public void putLessonOnShelves(Context context,@NonNull String sessionID,@NonNull String lesson,@NonNull final APIServiceCallback callback) {

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.putLessonOnShelves(sessionID,lesson).enqueue(new Callback<Empty>() {
            @Override
            public void onResponse(Call<Empty> call, Response<Empty> response) {

                int responseCode = response.code();
                if (responseCode == 200) {

                    callback.onSuccess(null);

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    if (TextUtils.isEmpty(errorBody)) {

                        String errorMessage = ErrorPrompts.putLessonOnShelvesPrompt(Errors.NO_ERROR);
                        callback.onFailure(Errors.NO_ERROR,errorMessage);

                    } else {

                        String errorCode = ApiManager.parseErrorBody(errorBody);
                        String errorMessage = ErrorPrompts.putLessonOnShelvesPrompt(errorCode);
                        callback.onFailure(errorCode,errorMessage);

                    }

                }
            }

            @Override
            public void onFailure(Call<Empty> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the put lession on shelves request has occur exception");
                }

                String errorMsg = t.getMessage();
                // FIXME: 2016/11/1
                if(errorMsg.contains("No content to map due to end-of-input")){
                    callback.onSuccess(null);
                }else{
                    String errorMessage = ErrorPrompts.putLessonOnShelvesPrompt(Errors.NO_ERROR);
                    callback.onFailure(Errors.NO_ERROR,errorMessage);
                }

            }
        });
    }


}
