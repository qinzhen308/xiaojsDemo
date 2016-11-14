package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.CLResponse;
import com.benyuan.xiaojs.model.CreateLesson;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.Empty;
import com.benyuan.xiaojs.model.GELessonsResponse;
import com.benyuan.xiaojs.model.GetLessonsResponse;
import com.benyuan.xiaojs.model.LessonDetail;
import com.benyuan.xiaojs.model.Pagination;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class LessonRequest extends ServiceRequest {


    public void createLiveLesson(Context context, String sessionID, CreateLesson lesson,
                                 @NonNull APIServiceCallback<CLResponse> callback) {

        final WeakReference<APIServiceCallback<CLResponse>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.createLiveLesson(sessionID, lesson).enqueue(new Callback<CLResponse>() {
            @Override
            public void onResponse(Call<CLResponse> call, Response<CLResponse> response) {
                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    CLResponse info = response.body();

                    APIServiceCallback<CLResponse> callback = callbackReference.get();
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
                    String errorMessage = ErrorPrompts.createLessonPrompt(errorCode);

                    APIServiceCallback<CLResponse> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<CLResponse> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the createLiveLession request has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.createLessonPrompt(errorCode);

                APIServiceCallback<CLResponse> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }

            }
        });

    }


    public void getLessons(Context context,
                           @NonNull String sessionID,
                           @NonNull Criteria criteria,
                           @NonNull Pagination pagination,
                           @NonNull APIServiceCallback<GetLessonsResponse> callback) {

        final WeakReference<APIServiceCallback<GetLessonsResponse>> callbackReference =
                new WeakReference<>(callback);


        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

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

                if (responseCode == SUCCESS_CODE) {

                    GetLessonsResponse lessonsResponse = response.body();

                    APIServiceCallback<GetLessonsResponse> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(lessonsResponse);
                    }


                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.getLessonPrompt(errorCode);

                    APIServiceCallback<GetLessonsResponse> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<GetLessonsResponse> call, Throwable t) {
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the get lessons request has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getLessonPrompt(errorCode);

                APIServiceCallback<GetLessonsResponse> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }


            }
        });

    }


    public void putLessonOnShelves(Context context,
                                   @NonNull String sessionID,
                                   @NonNull String lesson,
                                   @NonNull final APIServiceCallback callback) {

        final WeakReference<APIServiceCallback> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.putLessonOnShelves(sessionID, lesson).enqueue(new Callback<Empty>() {
            @Override
            public void onResponse(Call<Empty> call, Response<Empty> response) {

                int responseCode = response.code();
                if (responseCode == SUCCESS_CODE) {

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(null);
                    }


                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.putLessonOnShelvesPrompt(errorCode);

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
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
                if (errorMsg.contains(EMPTY_EXCEPTION)) {
                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(null);
                    }

                } else {

                    String errorCode = getExceptionErrorCode();
                    String errorMessage = ErrorPrompts.putLessonOnShelvesPrompt(errorCode);

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }

                }

            }
        });
    }

    public void cancelLessonOnShelves(Context context,
                                      @NonNull String sessionID,
                                      @NonNull String lesson,
                                      @NonNull APIServiceCallback callback) {

        final WeakReference<APIServiceCallback> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.cancelLessonOnShelves(sessionID,lesson).enqueue(new Callback<Empty>() {
            @Override
            public void onResponse(Call<Empty> call, Response<Empty> response) {

                int responseCode = response.code();
                if (responseCode == SUCCESS_CODE) {

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(null);
                    }


                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.cancelLessonOnShelvesPrompt(errorCode);

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<Empty> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the cancelLessonOnShelves request has occur exception");
                }

                String errorMsg = t.getMessage();
                // FIXME: 2016/11/1
                if (errorMsg.contains(EMPTY_EXCEPTION)) {
                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(null);
                    }

                } else {

                    String errorCode = getExceptionErrorCode();
                    String errorMessage = ErrorPrompts.cancelLessonOnShelvesPrompt(errorCode);

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }

                }
            }
        });
    }

    public void getEnrolledLessons(Context context,
                           @NonNull String sessionID,
                           @NonNull Criteria criteria,
                           @NonNull Pagination pagination,
                           @NonNull APIServiceCallback<GELessonsResponse> callback) {


        final WeakReference<APIServiceCallback<GELessonsResponse>> callbackReference =
                new WeakReference<>(callback);

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if(XiaojsConfig.DEBUG){
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getEnrolledLessons(sessionID,criteriaJsonstr,paginationJsonstr).enqueue(
                new Callback<GELessonsResponse>() {
            @Override
            public void onResponse(Call<GELessonsResponse> call,
                                   Response<GELessonsResponse> response) {

                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    GELessonsResponse lessonsResponse = response.body();

                    APIServiceCallback<GELessonsResponse> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(lessonsResponse);
                    }


                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.getEnrolledLessonsPrompt(errorCode);

                    APIServiceCallback<GELessonsResponse> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<GELessonsResponse> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the getEnrolledLessons request has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getEnrolledLessonsPrompt(errorCode);

                APIServiceCallback<GELessonsResponse> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });
    }

    public void getLessonDetails(Context context,
                                 @NonNull String lesson,
                                 @NonNull APIServiceCallback<LessonDetail> callback) {

        final WeakReference<APIServiceCallback<LessonDetail>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getLessonDetails(lesson).enqueue(new Callback<LessonDetail>() {
            @Override
            public void onResponse(Call<LessonDetail> call, Response<LessonDetail> response) {

                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    LessonDetail lessonDetail = response.body();

                    APIServiceCallback<LessonDetail> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(lessonDetail);
                    }


                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.getLessonDetailsPrompt(errorCode);

                    APIServiceCallback<LessonDetail> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }

                }
            }

            @Override
            public void onFailure(Call<LessonDetail> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the getLessonDetails request has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getLessonDetailsPrompt(errorCode);

                APIServiceCallback<LessonDetail> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });
    }


    public void confirmLessonEnrollment(Context context,
                                        @NonNull String sessionID,
                                        @NonNull String lesson,
                                        @NonNull APIServiceCallback callback) {

        final WeakReference<APIServiceCallback> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.confirmLessonEnrollment(sessionID,lesson).enqueue(new Callback<Empty>() {
            @Override
            public void onResponse(Call<Empty> call, Response<Empty> response) {
                int responseCode = response.code();
                if (responseCode == SUCCESS_CODE) {

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(null);
                    }


                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.confirmLessonEnrollmentPrompt(errorCode);

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<Empty> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the confirmLessonEnrollment request has occur exception");
                }

                String errorMsg = t.getMessage();
                // FIXME: 2016/11/1
                if (errorMsg.contains(EMPTY_EXCEPTION)) {
                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(null);
                    }

                } else {

                    String errorCode = getExceptionErrorCode();
                    String errorMessage = ErrorPrompts.confirmLessonEnrollmentPrompt(errorCode);

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }

                }
            }
        });
    }


}
