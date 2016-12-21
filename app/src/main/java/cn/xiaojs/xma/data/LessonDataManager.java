package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.LessonRequest;
import cn.xiaojs.xma.data.api.QiniuRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.CLEResponse;
import cn.xiaojs.xma.model.CLResponse;
import cn.xiaojs.xma.model.CancelReason;
import cn.xiaojs.xma.model.CreateLesson;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.ELResponse;
import cn.xiaojs.xma.model.GELessonsResponse;
import cn.xiaojs.xma.model.GetLessonsResponse;
import cn.xiaojs.xma.model.LessonDetail;
import cn.xiaojs.xma.model.LiveLesson;
import cn.xiaojs.xma.model.OfflineRegistrant;
import cn.xiaojs.xma.model.Pagination;
import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class LessonDataManager extends DataManager {


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
        if (checkSession(session, callback)) {
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.createLiveLesson(session, lesson);

    }

    /**
     * 获取开的课程
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
        if (checkSession(session, callback)) {
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getLessons(session, criteria, pagination);

    }

    /**
     * 上架直播课
     */
    public static void requestPutLessonOnShelves(Context context,
                                                 @NonNull String lesson,
                                                 @NonNull APIServiceCallback callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the putLessonOnShelves request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);

        if (checkSession(session, callback)) {
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.putLessonOnShelves(session, lesson);

    }

    /**
     * 取消上架课程
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

        if (checkSession(session, callback)) {
            return;
        }


        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.cancelLessonOnShelves(session, lesson);
    }

    /**
     * 获取已报名的课程
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
        if (checkSession(session, callback)) {
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getEnrolledLessons(session, criteria, pagination);

    }

    /**
     * 获取直播课详情
     */
    public static void requestLessonData(Context context,
                                         @NonNull String lesson,
                                         @NonNull APIServiceCallback<LessonDetail> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the requestGetLessonDetails request");
            }
            return;
        }

        String session = AccountDataManager.getSessionID(context);

        if (checkSession(session, callback)) {
            return;
        }


        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getLessonData(session, lesson);
    }


    /**
     * @param lesson 可为空
     */
    public static void requestUploadCover(Context context,
                                          @Nullable String lesson,
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

        if (TextUtils.isEmpty(lesson)) {

            lesson = DataManager.generateLessonKey();

            if (XiaojsConfig.DEBUG) {
                Logger.d("the lesson is empty,so auto generate lesson key=%s", lesson);
            }


        }

        QiniuRequest qiniuRequest = new QiniuRequest(context, filePath, qiniuService);
        qiniuRequest.uploadCover(session, lesson);

    }


    public static void requestLessonDetails(Context context,
                                            @NonNull String lesson,
                                            @NonNull APIServiceCallback<LessonDetail> callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.getLessonDetails(lesson);
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
        if (checkSession(session, callback)) {
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.editLesson(session, lesson, liveLesson);
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
        if (checkSession(session, callback)) {
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.enrollLesson(session, lesson, offlineRegistrant);

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
        if (checkSession(session, callback)) {
            return;
        }


        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.confirmLessonEnrollment(session, lesson, registrant);
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
        if (checkSession(session, callback)) {
            return;
        }


        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.cancelLesson(session, lesson, reason);

    }

    public static void requestToggleAccessLesson(Context context,
                                                 @NonNull String lesson,
                                                 boolean accessible,
                                                 @NonNull APIServiceCallback callback) {

        String session = AccountDataManager.getSessionID(context);
        if (checkSession(session, callback)) {
            return;
        }

        LessonRequest lessonRequest = new LessonRequest(context, callback);
        lessonRequest.toggleAccessLesson(session, lesson, accessible);
    }


}
