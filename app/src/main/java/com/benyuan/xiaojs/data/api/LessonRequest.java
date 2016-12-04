package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.service.APIType;
import com.benyuan.xiaojs.data.api.service.ErrorPrompts;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.ServiceRequest;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.AccessLesson;
import com.benyuan.xiaojs.model.CLEResponse;
import com.benyuan.xiaojs.model.CLResponse;
import com.benyuan.xiaojs.model.CancelReason;
import com.benyuan.xiaojs.model.CreateLesson;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.ELResponse;
import com.benyuan.xiaojs.model.GELessonsResponse;
import com.benyuan.xiaojs.model.GetLessonsResponse;
import com.benyuan.xiaojs.model.LessonDetail;
import com.benyuan.xiaojs.model.LiveLesson;
import com.benyuan.xiaojs.model.OfflineRegistrant;
import com.benyuan.xiaojs.model.Pagination;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class LessonRequest extends ServiceRequest {

    public LessonRequest(Context context, APIServiceCallback callback) {

        super(context, callback);

    }

    public void createLiveLesson(String sessionID, CreateLesson lesson) {

        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.createLiveLesson(sessionID, lesson).enqueue(new Callback<CLResponse>() {
            @Override
            public void onResponse(Call<CLResponse> call, Response<CLResponse> response) {
                onRespones(APIType.CREATE_LESSON, response);
            }

            @Override
            public void onFailure(Call<CLResponse> call, Throwable t) {

                onFailures(APIType.CREATE_LESSON, t);

            }
        });

    }


    public void getLessons(@NonNull String sessionID,
                           @NonNull Criteria criteria,
                           @NonNull Pagination pagination) {


        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getLessons(sessionID, criteriaJsonstr, paginationJsonstr).enqueue(
                new Callback<GetLessonsResponse>() {
            @Override
            public void onResponse(Call<GetLessonsResponse> call,
                                   Response<GetLessonsResponse> response) {
                onRespones(APIType.GET_LESSONS, response);
            }

            @Override
            public void onFailure(Call<GetLessonsResponse> call, Throwable t) {

                onFailures(APIType.GET_LESSONS, t);

            }
        });

    }


    public void putLessonOnShelves(@NonNull String sessionID, @NonNull String lesson) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.putLessonOnShelves(sessionID, lesson).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                onRespones(APIType.PUT_LESSON_ON_SHELVES, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                onFailures(APIType.PUT_LESSON_ON_SHELVES, t);

            }

        });
    }

    public void cancelLessonOnShelves(@NonNull String sessionID, @NonNull String lesson) {

        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.cancelLessonOnShelves(sessionID, lesson).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                onRespones(APIType.CANCEL_LESSON_ON_SHELVES, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                onFailures(APIType.CANCEL_LESSON_ON_SHELVES, t);

            }
        });
    }

    public void getEnrolledLessons(@NonNull String sessionID,
                                   @NonNull Criteria criteria,
                                   @NonNull Pagination pagination) {


        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }

        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getEnrolledLessons(sessionID, criteriaJsonstr, paginationJsonstr).enqueue(
                new Callback<GELessonsResponse>() {
                    @Override
                    public void onResponse(Call<GELessonsResponse> call,
                                           Response<GELessonsResponse> response) {

                        onRespones(APIType.GET_ENROLLED_LESSONS, response);
                    }

                    @Override
                    public void onFailure(Call<GELessonsResponse> call, Throwable t) {

                        onFailures(APIType.GET_ENROLLED_LESSONS, t);
                    }
                });
    }

    public void getLessonData(@NonNull String sessionID, @NonNull String lesson) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getLessonData(sessionID, lesson).enqueue(new Callback<LessonDetail>() {
            @Override
            public void onResponse(Call<LessonDetail> call, Response<LessonDetail> response) {

                onRespones(APIType.GET_LESSON_DATA, response);
            }

            @Override
            public void onFailure(Call<LessonDetail> call, Throwable t) {

                onFailures(APIType.GET_LESSON_DATA, t);
            }
        });
    }


    public void getLessonDetails(@NonNull String lesson) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getLessonDetails(lesson).enqueue(new Callback<LessonDetail>() {
            @Override
            public void onResponse(Call<LessonDetail> call, Response<LessonDetail> response) {

                onRespones(APIType.GET_LESSON_DETAILS, response);

            }

            @Override
            public void onFailure(Call<LessonDetail> call, Throwable t) {

                onFailures(APIType.GET_LESSON_DETAILS, t);
            }
        });

    }

    public void editLesson(@NonNull String sessionID,
                           @NonNull String lesson,
                           @NonNull LiveLesson liveLesson) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.editLesson(sessionID, lesson, liveLesson).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                onRespones(APIType.EDIT_LESSON, response);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                onFailures(APIType.EDIT_LESSON, t);

            }
        });

    }


    public void enrollLesson(@NonNull String sessionID,
                             @NonNull String lesson,
                             @Nullable OfflineRegistrant offlineRegistrant) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.enrollLesson(sessionID, lesson, offlineRegistrant).enqueue(
                new Callback<ELResponse>() {
                    @Override
                    public void onResponse(Call<ELResponse> call, Response<ELResponse> response) {

                        onRespones(APIType.ENROLL_LESSON, response);

                    }

                    @Override
                    public void onFailure(Call<ELResponse> call, Throwable t) {

                        onFailures(APIType.ENROLL_LESSON, t);
                    }
                });
    }

    public void confirmLessonEnrollment(@NonNull String sessionID,
                                        @NonNull String lesson,
                                        @NonNull String registrant) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.confirmLessonEnrollment(sessionID, lesson, registrant).enqueue(
                new Callback<CLEResponse>() {
                    @Override
                    public void onResponse(Call<CLEResponse> call, Response<CLEResponse> response) {
                        onRespones(APIType.CONFIRM_LESSON_ENROLLMENT, response);
                    }

                    @Override
                    public void onFailure(Call<CLEResponse> call, Throwable t) {

                        onFailures(APIType.CONFIRM_LESSON_ENROLLMENT, t);

                    }
                });
    }

    public void cancelLesson(@NonNull String sessionID,
                             @NonNull String lesson,
                             @NonNull CancelReason reason) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.cancelLesson(sessionID, lesson, reason).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                onRespones(APIType.CANCEL_LESSON, response);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                onFailures(APIType.CANCEL_LESSON, t);
            }
        });
    }


    public void toggleAccessLesson(@NonNull String sessionID,
                                   @NonNull String lesson,
                                   final boolean accessible) {

        AccessLesson accessLesson = new AccessLesson();
        accessLesson.setAccessible(accessible);


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.toggleAccessLesson(sessionID, lesson, accessLesson).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                onRespones(APIType.TOGGLE_ACCESS_TO_LESSON, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                onFailures(APIType.TOGGLE_ACCESS_TO_LESSON, t);
            }
        });
    }


}
