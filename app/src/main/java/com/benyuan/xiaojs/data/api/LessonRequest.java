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

        Call<CLResponse> call = getService().createLiveLesson(sessionID, lesson);
        enqueueRequest(APIType.CREATE_LESSON, call);

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


        Call<GetLessonsResponse> call = getService().getLessons(sessionID,
                criteriaJsonstr,
                paginationJsonstr);

        enqueueRequest(APIType.GET_LESSONS, call);

    }


    public void putLessonOnShelves(@NonNull String sessionID, @NonNull String lesson) {

        Call<ResponseBody> call = getService().putLessonOnShelves(sessionID, lesson);
        enqueueRequest(APIType.PUT_LESSON_ON_SHELVES, call);

    }

    public void cancelLessonOnShelves(@NonNull String sessionID, @NonNull String lesson) {

        Call<ResponseBody> call = getService().cancelLessonOnShelves(sessionID, lesson);
        enqueueRequest(APIType.CANCEL_LESSON_ON_SHELVES, call);
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

        Call<GELessonsResponse> call = getService().getEnrolledLessons(sessionID,
                criteriaJsonstr,
                paginationJsonstr);
        enqueueRequest(APIType.GET_ENROLLED_LESSONS, call);

    }

    public void getLessonData(@NonNull String sessionID, @NonNull String lesson) {

        Call<LessonDetail> call = getService().getLessonData(sessionID, lesson);
        enqueueRequest(APIType.GET_LESSON_DATA, call);

    }


    public void getLessonDetails(@NonNull String lesson) {

        Call<LessonDetail> call = getService().getLessonDetails(lesson);
        enqueueRequest(APIType.GET_LESSON_DETAILS, call);

    }

    public void editLesson(@NonNull String sessionID,
                           @NonNull String lesson,
                           @NonNull LiveLesson liveLesson) {

        Call<ResponseBody> call = getService().editLesson(sessionID, lesson, liveLesson);
        enqueueRequest(APIType.EDIT_LESSON, call);

    }


    public void enrollLesson(@NonNull String sessionID,
                             @NonNull String lesson,
                             @Nullable OfflineRegistrant offlineRegistrant) {

        Call<ELResponse> call = getService().enrollLesson(sessionID, lesson, offlineRegistrant);
        enqueueRequest(APIType.ENROLL_LESSON, call);

    }

    public void confirmLessonEnrollment(@NonNull String sessionID,
                                        @NonNull String lesson,
                                        @NonNull String registrant) {

        Call<CLEResponse> call = getService().confirmLessonEnrollment(sessionID, lesson, registrant);
        enqueueRequest(APIType.CONFIRM_LESSON_ENROLLMENT, call);
    }

    public void cancelLesson(@NonNull String sessionID,
                             @NonNull String lesson,
                             @NonNull CancelReason reason) {

        Call<ResponseBody> call = getService().cancelLesson(sessionID, lesson, reason);
        enqueueRequest(APIType.CANCEL_LESSON, call);

    }


    public void toggleAccessLesson(@NonNull String sessionID,
                                   @NonNull String lesson,
                                   final boolean accessible) {

        AccessLesson accessLesson = new AccessLesson();
        accessLesson.setAccessible(accessible);

        Call<ResponseBody> call = getService().toggleAccessLesson(sessionID, lesson, accessLesson);
        enqueueRequest(APIType.TOGGLE_ACCESS_TO_LESSON, call);

    }


}
