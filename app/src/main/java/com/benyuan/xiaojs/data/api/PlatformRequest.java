package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.GENotificationsResponse;
import com.benyuan.xiaojs.model.GNOResponse;
import com.benyuan.xiaojs.model.IgnoreNResponse;
import com.benyuan.xiaojs.model.NotificationCriteria;
import com.benyuan.xiaojs.model.Pagination;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/13.
 */

public class PlatformRequest extends ServiceRequest {

    public void getNotificationsOverview(Context context,
                                         @NonNull String sessionID,
                                         @NonNull Pagination pagination,
                                         @NonNull APIServiceCallback<GNOResponse> callback) {

        final WeakReference<APIServiceCallback<GNOResponse>> callbackReference =
                new WeakReference<>(callback);

        String paginationJson = objectToJsonString(pagination);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getNotificationsOverview(sessionID, paginationJson).enqueue(
                new Callback<GNOResponse>() {
                    @Override
                    public void onResponse(Call<GNOResponse> call, Response<GNOResponse> response) {

                        int responseCode = response.code();
                        if (XiaojsConfig.DEBUG) {
                            Logger.d("the request has onResponse, the code:%d", responseCode);
                        }

                        if (responseCode == SUCCESS_CODE) {

                            GNOResponse gnoResponse = response.body();

                            APIServiceCallback<GNOResponse> callback = callbackReference.get();
                            if (callback != null) {
                                callback.onSuccess(gnoResponse);
                            }

                        } else {

                            String errorBody = null;
                            try {
                                errorBody = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            String errorCode = parseErrorBody(errorBody);
                            String errorMessage = ErrorPrompts.getNotificationsOverviewPrompt(errorCode);

                            APIServiceCallback<GNOResponse> callback = callbackReference.get();
                            if (callback != null) {
                                callback.onFailure(errorCode, errorMessage);
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<GNOResponse> call, Throwable t) {

                        if (XiaojsConfig.DEBUG) {
                            String exception = t.getMessage();
                            Logger.d("the request has occur exception:\n %s", exception);
                        }


                        String errorCode = getExceptionErrorCode();
                        String errorMessage = ErrorPrompts.getNotificationsOverviewPrompt(errorCode);

                        APIServiceCallback<GNOResponse> callback = callbackReference.get();
                        if (callback != null) {
                            callback.onFailure(errorCode, errorMessage);
                        }

                    }
                });

    }


    public void getNotifications(Context context,
                                 @NonNull String sessionID,
                                 @NonNull NotificationCriteria criteria,
                                 @NonNull Pagination pagination,
                                 @NonNull APIServiceCallback<GENotificationsResponse> callback) {

        final WeakReference<APIServiceCallback<GENotificationsResponse>> callbackReference =
                new WeakReference<>(callback);

        String criteriaJson = objectToJsonString(criteria);
        String paginationJson = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJson);
            Logger.json(paginationJson);
        }

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getNotifications(sessionID, criteriaJson, paginationJson).enqueue(
                new Callback<GENotificationsResponse>() {

                    @Override
                    public void onResponse(Call<GENotificationsResponse> call,
                                           Response<GENotificationsResponse> response) {

                        int responseCode = response.code();
                        if (XiaojsConfig.DEBUG) {
                            Logger.d("the request has onResponse, the code:%d", responseCode);
                        }

                        if (responseCode == SUCCESS_CODE) {

                            GENotificationsResponse notifications = response.body();

                            APIServiceCallback<GENotificationsResponse> callback = callbackReference.get();
                            if (callback != null) {
                                callback.onSuccess(notifications);
                            }

                        } else {

                            String errorBody = null;
                            try {
                                errorBody = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            String errorCode = parseErrorBody(errorBody);
                            String errorMessage = ErrorPrompts.getNotificationsPrompt(errorCode);

                            APIServiceCallback<GENotificationsResponse> callback = callbackReference.get();
                            if (callback != null) {
                                callback.onFailure(errorCode, errorMessage);
                            }


                        }

                    }

                    @Override
                    public void onFailure(Call<GENotificationsResponse> call, Throwable t) {

                        if (XiaojsConfig.DEBUG) {
                            String exception = t.getMessage();
                            Logger.d("the request has occur exception:\n %s", exception);
                        }


                        String errorCode = getExceptionErrorCode();
                        String errorMessage = ErrorPrompts.getNotificationsPrompt(errorCode);

                        APIServiceCallback<GENotificationsResponse> callback = callbackReference.get();
                        if (callback != null) {
                            callback.onFailure(errorCode, errorMessage);
                        }
                    }
                });


    }

    public void deleteNotification(Context context,
                                   @NonNull String sessionID,
                                   @NonNull String notification,
                                   @NonNull APIServiceCallback callback) {

        final WeakReference<APIServiceCallback> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.deleteNotification(sessionID, notification).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                int responseCode = response.code();
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

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
                    String errorMessage = ErrorPrompts.deleteNotificationPrompt(errorCode);

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }



                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.deleteNotificationPrompt(errorCode);

                APIServiceCallback callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }


            }
        });
    }

    public void ignoreNotifications(Context context,
                                    @NonNull String sessionID,
                                    @NonNull NotificationCriteria criteria,
                                    @NonNull APIServiceCallback<IgnoreNResponse> callback) {

        final WeakReference<APIServiceCallback<IgnoreNResponse>> callbackReference =
                new WeakReference<>(callback);

        String criteriaJson = objectToJsonString(criteria);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJson);
        }

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.ignoreNotifications(sessionID, criteriaJson).enqueue(
                new Callback<IgnoreNResponse>() {

                    @Override
                    public void onResponse(Call<IgnoreNResponse> call, Response<IgnoreNResponse> response) {

                        int responseCode = response.code();
                        if (XiaojsConfig.DEBUG) {
                            Logger.d("the request has onResponse, the code:%d", responseCode);
                        }

                        if (responseCode == SUCCESS_CODE) {

                            IgnoreNResponse ignoreNResponse = response.body();

                            APIServiceCallback<IgnoreNResponse> callback = callbackReference.get();
                            if (callback != null) {
                                callback.onSuccess(ignoreNResponse);
                            }

                        } else {

                            String errorBody = null;
                            try {
                                errorBody = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            String errorCode = parseErrorBody(errorBody);
                            String errorMessage = ErrorPrompts.ignoreNotificationsPrompt(errorCode);

                            APIServiceCallback<IgnoreNResponse> callback = callbackReference.get();
                            if (callback != null) {
                                callback.onFailure(errorCode, errorMessage);
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<IgnoreNResponse> call, Throwable t) {

                        if (XiaojsConfig.DEBUG) {
                            String exception = t.getMessage();
                            Logger.d("the request has occur exception:\n %s", exception);
                        }

                        String errorCode = getExceptionErrorCode();
                        String errorMessage = ErrorPrompts.ignoreNotificationsPrompt(errorCode);

                        APIServiceCallback<IgnoreNResponse> callback = callbackReference.get();
                        if (callback != null) {
                            callback.onFailure(errorCode, errorMessage);
                        }
                    }
                });
    }
}
