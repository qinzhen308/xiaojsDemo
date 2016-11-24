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
import com.benyuan.xiaojs.model.CenterData;
import com.benyuan.xiaojs.model.ClaimCompetency;
import com.benyuan.xiaojs.model.CompetencyParams;
import com.benyuan.xiaojs.model.Empty;
import com.benyuan.xiaojs.model.HomeData;
import com.benyuan.xiaojs.model.TokenResponse;
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
        xiaojsService.editProfile(sessionID, basic).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

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
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                String errorMsg = t.getMessage();

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the editProfile request has occur exception:\n%s", errorMsg);
                }


                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.editProfilePrompt(errorCode);

                APIServiceCallback callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }

            }
        });


    }

    public void getProfile(Context context,
                           @NonNull String sessionID,
                           @NonNull APIServiceCallback<Account.Basic> callback) {
        final WeakReference<APIServiceCallback<Account.Basic>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getProfile(sessionID).enqueue(new Callback<Account.Basic>() {
            @Override
            public void onResponse(Call<Account.Basic> call, Response<Account.Basic> response) {
                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    Account.Basic basic = response.body();

                    APIServiceCallback<Account.Basic> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onSuccess(basic);
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

                    APIServiceCallback<Account.Basic> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<Account.Basic> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }


                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getProfilePrompt(errorCode);

                APIServiceCallback<Account.Basic> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });


    }


    protected void getAvatarUpToken(Context context,
                                    @NonNull String sessionID,
                                    @NonNull APIServiceCallback<TokenResponse> callback) {

        final WeakReference<APIServiceCallback<TokenResponse>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getAvatarUpToken(sessionID).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {

                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    TokenResponse tokenResponse = response.body();

                    APIServiceCallback<TokenResponse> callback = callbackReference.get();
                    if (callback != null) {

                        callback.onSuccess(tokenResponse);
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

                    APIServiceCallback<TokenResponse> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }

                }

            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }


                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getAvatarUpTokenPrompt(errorCode);

                APIServiceCallback<TokenResponse> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });
    }


    protected void getCoverUpToken(Context context,
                                   @NonNull String sessionID,
                                   @NonNull APIServiceCallback<TokenResponse> callback) {

        final WeakReference<APIServiceCallback<TokenResponse>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getCoverUpToken(sessionID).enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {

                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    TokenResponse tokenResponse = response.body();

                    APIServiceCallback<TokenResponse> callback = callbackReference.get();
                    if (callback != null) {

                        callback.onSuccess(tokenResponse);

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

                    APIServiceCallback<TokenResponse> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }

            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }


                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getCoverUpTokenPrompt(errorCode);

                APIServiceCallback<TokenResponse> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });
    }

    public void getCenterData(Context context,
                              @NonNull String sessionID,
                              @NonNull APIServiceCallback<CenterData> callback) {

        final WeakReference<APIServiceCallback<CenterData>> callbackReference =
                new WeakReference<>(callback);

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getCenterData(sessionID).enqueue(new Callback<CenterData>() {
            @Override
            public void onResponse(Call<CenterData> call, Response<CenterData> response) {

                int responseCode = response.code();

                if (responseCode == SUCCESS_CODE) {

                    CenterData centerData = response.body();

                    APIServiceCallback<CenterData> callback = callbackReference.get();
                    if (callback != null) {

                        callback.onSuccess(centerData);

                    }

                } else {

                    String errorBody = null;
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    String errorCode = parseErrorBody(errorBody);
                    String errorMessage = ErrorPrompts.getCenterDataPrompt(errorCode);

                    APIServiceCallback<CenterData> callback = callbackReference.get();
                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }

            }

            @Override
            public void onFailure(Call<CenterData> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }


                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getCenterDataPrompt(errorCode);

                APIServiceCallback<CenterData> callback = callbackReference.get();
                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });


    }

}
