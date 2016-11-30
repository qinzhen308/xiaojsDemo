package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.AccessLesson;
import com.benyuan.xiaojs.model.CLEResponse;
import com.benyuan.xiaojs.model.CLResponse;
import com.benyuan.xiaojs.model.CancelReason;
import com.benyuan.xiaojs.model.CreateLesson;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.ELResponse;
import com.benyuan.xiaojs.model.Empty;
import com.benyuan.xiaojs.model.GELessonsResponse;
import com.benyuan.xiaojs.model.GetLessonsResponse;
import com.benyuan.xiaojs.model.LessonDetail;
import com.benyuan.xiaojs.model.LiveLesson;
import com.benyuan.xiaojs.model.OfflineRegistrant;
import com.benyuan.xiaojs.model.Pagination;
import com.orhanobut.logger.Logger;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class LessonRequest extends ServiceRequest {


    public void createLiveLesson(Context context, String sessionID, CreateLesson lesson,
                                 @NonNull final APIServiceCallback<CLResponse> callback) {

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.createLiveLesson(sessionID, lesson).enqueue(new Callback<CLResponse>() {
            @Override
            public void onResponse(Call<CLResponse> call, Response<CLResponse> response) {
                int responseCode = response.code();
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

                    CLResponse info = response.body();

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

                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<CLResponse> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }


                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.createLessonPrompt(errorCode);

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
                           @NonNull final APIServiceCallback<GetLessonsResponse> callback) {


        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }


        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getLessons(sessionID, criteriaJsonstr, paginationJsonstr).enqueue(new Callback<GetLessonsResponse>() {
            @Override
            public void onResponse(Call<GetLessonsResponse> call,
                                   Response<GetLessonsResponse> response) {
                int responseCode = response.code();
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

                    GetLessonsResponse lessonsResponse = response.body();

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

                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<GetLessonsResponse> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }


                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getLessonPrompt(errorCode);

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


        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.putLessonOnShelves(sessionID, lesson).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                int responseCode = response.code();
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

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
                String errorMessage = ErrorPrompts.putLessonOnShelvesPrompt(errorCode);

                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }

            }

        });
    }

    public void cancelLessonOnShelves(Context context,
                                      @NonNull String sessionID,
                                      @NonNull String lesson,
                                      @NonNull final APIServiceCallback callback) {

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.cancelLessonOnShelves(sessionID, lesson).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                int responseCode = response.code();
                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

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
                String errorMessage = ErrorPrompts.cancelLessonOnShelvesPrompt(errorCode);

                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }

            }
        });
    }

    public void getEnrolledLessons(Context context,
                                   @NonNull String sessionID,
                                   @NonNull Criteria criteria,
                                   @NonNull Pagination pagination,
                                   @NonNull final APIServiceCallback<GELessonsResponse> callback) {


        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }

        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getEnrolledLessons(sessionID, criteriaJsonstr, paginationJsonstr).enqueue(
                new Callback<GELessonsResponse>() {
                    @Override
                    public void onResponse(Call<GELessonsResponse> call,
                                           Response<GELessonsResponse> response) {

                        int responseCode = response.code();
                        if (XiaojsConfig.DEBUG) {
                            Logger.d("the request has onResponse, the code:%d", responseCode);
                        }

                        if (responseCode == SUCCESS_CODE) {

                            GELessonsResponse lessonsResponse = response.body();

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

                            if (callback != null) {
                                callback.onFailure(errorCode, errorMessage);
                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<GELessonsResponse> call, Throwable t) {

                        if (XiaojsConfig.DEBUG) {
                            String exception = t.getMessage();
                            Logger.d("the request has occur exception:\n %s", exception);
                        }


                        String errorCode = getExceptionErrorCode();
                        String errorMessage = ErrorPrompts.getEnrolledLessonsPrompt(errorCode);

                        if (callback != null) {
                            callback.onFailure(errorCode, errorMessage);
                        }
                    }
                });
    }

    public void getLessonData(Context context,
                              @NonNull String sessionID,
                              @NonNull String lesson,
                              @NonNull final APIServiceCallback<LessonDetail> callback) {


        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getLessonData(sessionID,lesson).enqueue(new Callback<LessonDetail>() {
            @Override
            public void onResponse(Call<LessonDetail> call, Response<LessonDetail> response) {

                int responseCode = response.code();

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

                    LessonDetail lessonDetail = response.body();

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
                    String errorMessage = ErrorPrompts.getLessonDataPrompt(errorCode);

                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }

                }
            }

            @Override
            public void onFailure(Call<LessonDetail> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }


                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getLessonDataPrompt(errorCode);

                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });
    }


    public void getLessonDetails(Context context,
                                  @NonNull String lesson,
                                  @NonNull final APIServiceCallback<LessonDetail> callback) {


        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.getLessonDetails(lesson).enqueue(new Callback<LessonDetail>() {
            @Override
            public void onResponse(Call<LessonDetail> call, Response<LessonDetail> response) {

                int responseCode = response.code();

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

                    LessonDetail lessonDetail = response.body();

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

                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }

            }

            @Override
            public void onFailure(Call<LessonDetail> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }


                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.getLessonDetailsPrompt(errorCode);

                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });

    }

    public void editLesson(Context context,
                           @NonNull String sessionID,
                           @NonNull String lesson,
                           @NonNull LiveLesson liveLesson,
                           @NonNull final APIServiceCallback callback) {


        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.editLesson(sessionID, lesson, liveLesson).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                int responseCode = response.code();

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

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
                    String errorMessage = ErrorPrompts.editLessonPrompt(errorCode);

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
                String errorMessage = ErrorPrompts.editLessonPrompt(errorCode);

                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }

            }
        });

    }


    public void enrollLesson(Context context,
                             @NonNull String sessionID,
                             @NonNull String lesson,
                             @Nullable OfflineRegistrant offlineRegistrant,
                             @NonNull final APIServiceCallback<ELResponse> callback) {


        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.enrollLesson(sessionID, lesson, offlineRegistrant).enqueue(
                new Callback<ELResponse>() {
                    @Override
                    public void onResponse(Call<ELResponse> call, Response<ELResponse> response) {

                        int responseCode = response.code();

                        if (XiaojsConfig.DEBUG) {
                            Logger.d("the request has onResponse, the code:%d", responseCode);
                        }

                        if (responseCode == SUCCESS_CODE) {

                            ELResponse elResponse = response.body();

                            if (callback != null) {
                                callback.onSuccess(elResponse);
                            }


                        } else {

                            String errorBody = null;
                            try {
                                errorBody = response.errorBody().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            String errorCode = parseErrorBody(errorBody);
                            String errorMessage = ErrorPrompts.enrollLessonPrompt(errorCode);

                            if (callback != null) {
                                callback.onFailure(errorCode, errorMessage);
                            }


                        }

                    }

                    @Override
                    public void onFailure(Call<ELResponse> call, Throwable t) {

                        if (XiaojsConfig.DEBUG) {
                            String exception = t.getMessage();
                            Logger.d("the request has occur exception:\n %s", exception);
                        }


                        String errorCode = getExceptionErrorCode();
                        String errorMessage = ErrorPrompts.enrollLessonPrompt(errorCode);

                        if (callback != null) {
                            callback.onFailure(errorCode, errorMessage);
                        }
                    }
                });
    }

    public void confirmLessonEnrollment(Context context,
                                        @NonNull String sessionID,
                                        @NonNull String lesson,
                                        @NonNull String registrant,
                                        @NonNull final APIServiceCallback<CLEResponse> callback) {


        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.confirmLessonEnrollment(sessionID, lesson, registrant).enqueue(
                new Callback<CLEResponse>() {
            @Override
            public void onResponse(Call<CLEResponse> call, Response<CLEResponse> response) {
                int responseCode = response.code();

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

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

                    if (callback != null) {
                        callback.onFailure(errorCode, errorMessage);
                    }


                }
            }

            @Override
            public void onFailure(Call<CLEResponse> call, Throwable t) {

                if (XiaojsConfig.DEBUG) {
                    String exception = t.getMessage();
                    Logger.d("the request has occur exception:\n %s", exception);
                }



                String errorCode = getExceptionErrorCode();
                String errorMessage = ErrorPrompts.confirmLessonEnrollmentPrompt(errorCode);

                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }

            }
        });
    }

    public void cancelLesson(Context context,
                             @NonNull String sessionID,
                             @NonNull String lesson,
                             @NonNull CancelReason reason,
                             @NonNull final APIServiceCallback callback) {


        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.cancelLesson(sessionID, lesson, reason).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                int responseCode = response.code();

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

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
                    String errorMessage = ErrorPrompts.cancelLessonPrompt(errorCode);

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
                String errorMessage = ErrorPrompts.cancelLessonPrompt(errorCode);

                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });
    }


    public void toggleAccessLesson(Context context,
                                   @NonNull String sessionID,
                                   @NonNull String lesson,
                                   final boolean accessible,
                                   @NonNull final APIServiceCallback callback) {

        AccessLesson accessLesson = new AccessLesson();
        accessLesson.setAccessible(accessible);


        XiaojsService xiaojsService = ApiManager.getAPIManager(context).getXiaojsService();
        xiaojsService.toggleAccessLesson(sessionID,lesson,accessLesson).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                int responseCode = response.code();

                if (XiaojsConfig.DEBUG) {
                    Logger.d("the request has onResponse, the code:%d", responseCode);
                }

                if (responseCode == SUCCESS_CODE) {

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
                    String errorMessage = ErrorPrompts.toggleAccessLessonPrompt(accessible,errorCode);

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
                String errorMessage = ErrorPrompts.toggleAccessLessonPrompt(accessible,errorCode);

                if (callback != null) {
                    callback.onFailure(errorCode, errorMessage);
                }
            }
        });
    }


}
