package com.benyuan.xiaojs.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.LessonRequest;
import com.benyuan.xiaojs.data.api.QiniuRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.QiniuService;
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

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class LessonDataManager extends DataManager{


    /**
     * 创建直播课
     */
    public static void requestCreateLiveLesson(Context context,
                                               CreateLesson lesson,
                                               APIServiceCallback<CLResponse> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the create live lession request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);
        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the create live lession request return failure");
            }

            String errorMessage = ErrorPrompts.createLessonPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.createLiveLesson(context, session, lesson, callback);

    }

    /**
     * 获取开的课程
     * @param context
     * @param criteria
     * @param pagination
     * @param callback
     */
    public static void requestGetLessons(Context context,
                                         @NonNull Criteria criteria,
                                         @NonNull Pagination pagination,
                                         @NonNull APIServiceCallback<GetLessonsResponse> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the GetLessons request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);
        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the GetLessons request return failure");
            }

            String errorMessage = ErrorPrompts.putLessonOnShelvesPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.getLessons(context,session,criteria,pagination,callback);

    }

    /**
     * 上架直播课
     * @param context
     * @param sessionID
     * @param lesson
     * @param callback
     */
    public static void requestPutLessonOnShelves(Context context,
                                                 @NonNull String lesson,
                                                 @NonNull APIServiceCallback<GetLessonsResponse> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the putLessonOnShelves request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);

        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the putLessonOnShelves request return failure");
            }

            String errorMessage = ErrorPrompts.putLessonOnShelvesPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        if (TextUtils.isEmpty(lesson)) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the lesson param is empty,so the putLessonOnShelves request return failure");
            }

            String errorMessage = ErrorPrompts.putLessonOnShelvesPrompt(Errors.BAD_PARAMETER);
            callback.onFailure(Errors.BAD_PARAMETER,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.putLessonOnShelves(context,session,lesson,callback);

    }

    /**
     * 取消上架课程
     * @param context
     * @param sessionID
     * @param lesson
     * @param callback
     */
    public static void requestCancelLessonOnShelves(Context context,
                                                    @NonNull String lesson,
                                                    @NonNull APIServiceCallback callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the requestCancelLessonOnShelves request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);

        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the requestCancelLessonOnShelves request return failure");
            }

            String errorMessage = ErrorPrompts.cancelLessonOnShelvesPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        if (TextUtils.isEmpty(lesson)) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the lesson param is empty,so the requestCancelLessonOnShelves request return failure");
            }

            String errorMessage = ErrorPrompts.cancelLessonOnShelvesPrompt(Errors.BAD_PARAMETER);
            callback.onFailure(Errors.BAD_PARAMETER,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.cancelLessonOnShelves(context,session,lesson,callback);
    }

    /**
     * 获取已报名的课程
     * @param context
     * @param sessionID
     * @param criteria
     * @param pagination
     * @param callback
     */
    public static void requestGetEnrolledLessons(Context context,
                                         @NonNull Criteria criteria,
                                         @NonNull Pagination pagination,
                                         @NonNull APIServiceCallback<GELessonsResponse> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the GetEnrolledLessons request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);
        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the GetEnrolledLessons request return failure");
            }

            String errorMessage = ErrorPrompts.getEnrolledLessonsPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.getEnrolledLessons(context,session,criteria,pagination,callback);

    }

    /**
     * 获取直播课详情
     * @param context
     * @param lesson
     * @param callback
     */
    public static void requestGetLessonDetails(Context context,
                                               @NonNull String lesson,
                                               @NonNull APIServiceCallback<LessonDetail> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the requestGetLessonDetails request");
            }
            return;
        }

        if (TextUtils.isEmpty(lesson)) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the lesson param is empty,so the requestGetLessonDetails request return failure");
            }

            String errorMessage = ErrorPrompts.getLessonDetailsPrompt(Errors.BAD_PARAMETER);
            callback.onFailure(Errors.BAD_PARAMETER,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.getLessonDetails(context,lesson,callback);
    }


    public static void requestUploadCover(Context context,
                                           @NonNull final String filePath,
                                           @NonNull QiniuService qiniuService) {


        if (qiniuService == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            qiniuService.uploadFailure();
            return;
        }

        String session = AccountDataManager.getSessionID(context);

        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            qiniuService.uploadFailure();
            return;
        }


        QiniuRequest qiniuRequest = new QiniuRequest();
        qiniuRequest.uploadCover(context,session,filePath,qiniuService);


    }


    public static void requestLessonHomepage(Context context,
                                             @NonNull String lesson,
                                             @NonNull APIServiceCallback<LessonDetail> callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.getLessonHomepage(context,lesson,callback);
    }


    public static void requestEditLesson(Context context,
                                  @NonNull String lesson,
                                  @NonNull LiveLesson liveLesson,
                                  @NonNull APIServiceCallback callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);
        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.editLessonPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.editLesson(context,session,lesson,liveLesson,callback);
    }


    public static void requestEnrollLesson(Context context,
                                           @NonNull String lesson,
                                           @Nullable OfflineRegistrant offlineRegistrant,
                                           @NonNull APIServiceCallback<ELResponse> callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);
        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.enrollLessonPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.enrollLesson(context,session,lesson,offlineRegistrant,callback);

    }

    public static void requestLessonEnrollment(Context context,
                                        @NonNull String lesson,
                                        @NonNull String registrant,
                                        @NonNull APIServiceCallback<CLEResponse> callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);
        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.confirmLessonEnrollmentPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }


        if (TextUtils.isEmpty(lesson)) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the lesson param is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.confirmLessonEnrollmentPrompt(Errors.BAD_PARAMETER);
            callback.onFailure(Errors.BAD_PARAMETER,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.confirmLessonEnrollment(context,session,lesson,registrant,callback);
    }


    public static void requestCancelLesson(Context context,
                                           @NonNull String lesson,
                                           @NonNull CancelReason reason,
                                           @NonNull APIServiceCallback callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);
        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.cancelLessonPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        if (TextUtils.isEmpty(lesson)) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the lesson param is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.cancelLessonPrompt(Errors.BAD_PARAMETER);
            callback.onFailure(Errors.BAD_PARAMETER,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.cancelLesson(context,session,lesson,reason,callback);

    }



}
