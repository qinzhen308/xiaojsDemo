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
import com.benyuan.xiaojs.model.Account;
import com.benyuan.xiaojs.model.ClaimCompetency;
import com.benyuan.xiaojs.model.CompetencyParams;
import com.benyuan.xiaojs.model.Empty;
import com.benyuan.xiaojs.model.HomeData;
import com.orhanobut.logger.Logger;


import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/3.
 */

public class AccountRequest extends ServiceRequest {


    public void getHomeData(Context context,
                            @NonNull String sessionID,
                            @NonNull APIServiceCallback<HomeData> callback) {


        final WeakReference<APIServiceCallback<HomeData>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getHomeData(sessionID).enqueue(new Callback<HomeData>() {
            @Override
            public void onResponse(Call<HomeData> call, Response<HomeData> response) {
                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    HomeData homeData = response.body();

                    APIServiceCallback<HomeData> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(homeData);
                    }


                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.getHomeDataPrompt(errorCode);

                    APIServiceCallback<HomeData> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<HomeData> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the login has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getHomeDataPrompt(errorCode);

                APIServiceCallback<HomeData> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(Errors.NO_ERROR, errorMessage);
                }

            }
        });

    }


    public void claimCompetency(Context context,
                                String sessionID,
                                CompetencyParams competencyParams,
                                @NonNull APIServiceCallback<ClaimCompetency> callback) {

        final WeakReference<APIServiceCallback<ClaimCompetency>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.claimCompetency(sessionID, competencyParams).enqueue(new Callback<ClaimCompetency>() {
            @Override
            public void onResponse(Call<ClaimCompetency> call, Response<ClaimCompetency> response) {
                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    ClaimCompetency claimCompetency = response.body();

                    APIServiceCallback<ClaimCompetency> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(claimCompetency);
                    }

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.claimCompetencyPrompt(errorCode);

                    APIServiceCallback<ClaimCompetency> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<ClaimCompetency> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the login has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.claimCompetencyPrompt(errorCode);

                APIServiceCallback<ClaimCompetency> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }

            }
        });

    }

    public void editProfile(Context context,
                            @NonNull String sessionID,
                            @NonNull Account.Basic basic,
                            @NonNull APIServiceCallback callback) {


        final WeakReference<APIServiceCallback> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.editProfile(sessionID,basic).enqueue(new Callback<Empty>() {
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
                    String errorMessage = ErrorPrompts.editProfilePrompt(errorCode);

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<Empty> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the editProfile request has occur exception");
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
                    String errorMessage = ErrorPrompts.editProfilePrompt(errorCode);

                    APIServiceCallback callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }

                }
            }
        });


    }

    public void getProfile(Context context,
                           @NonNull String sessionID,
                           @NonNull APIServiceCallback<Account> callback) {
        final WeakReference<APIServiceCallback<Account>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getProfile(sessionID).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    Account account = response.body();

                    APIServiceCallback<Account> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(account);
                    }

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.getProfilePrompt(errorCode);

                    APIServiceCallback<Account> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the getProfile has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getProfilePrompt(errorCode);

                APIServiceCallback<Account> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });


    }


    public void getAvatarUpToken(Context context,@NonNull String sessionID,@NonNull APIServiceCallback<String> callback) {

        final WeakReference<APIServiceCallback<String>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getAvatarUpToken(sessionID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    String token = null;
                    try {
                        token = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    APIServiceCallback<String> callback = callbackReference.get();
                    if (callback != null) {
                        if (TextUtils.isEmpty(token)) {

                            String errorCode = getDefaultErrorCode();
                            String errorMessage = ErrorPrompts.getAvatarUpTokenPrompt(errorCode);
                            callback.onFailure(errorCode,errorMessage);
                        }else{
                            callback.onSuccess(token);
                        }

                    }

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.getAvatarUpTokenPrompt(errorCode);

                    APIServiceCallback<String> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the getAvatarUpToken has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getAvatarUpTokenPrompt(errorCode);

                APIServiceCallback<String> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });
    }


    public void getCoverUpToken(Context context,@NonNull String sessionID,@NonNull APIServiceCallback<String> callback) {

        final WeakReference<APIServiceCallback<String>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getCoverUpToken(sessionID).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    String token = null;
                    try {
                        token = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    APIServiceCallback<String> callback = callbackReference.get();
                    if (callback != null) {
                        if (TextUtils.isEmpty(token)) {

                            String errorCode = getDefaultErrorCode();
                            String errorMessage = ErrorPrompts.getCoverUpTokenPrompt(errorCode);
                            callback.onFailure(errorCode,errorMessage);
                        }else{
                            callback.onSuccess(token);
                        }

                    }

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.getCoverUpTokenPrompt(errorCode);

                    APIServiceCallback<String> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the getCoverUpToken has occur exception");
                }

                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getCoverUpTokenPrompt(errorCode);

                APIServiceCallback<String> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });
    }

}
