package com.benyuan.xiaojs.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.LessonRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.CLResponse;
import com.benyuan.xiaojs.model.CreateLesson;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.model.GetLessonsResponse;
import com.benyuan.xiaojs.model.Pagination;
import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class LessonDataManager {


    /**
     * 创建直播课
     */
    public static void requestCreateLiveLesson(Context context,
                                               String sessionID,
                                               CreateLesson lesson,
                                               APIServiceCallback<CLResponse> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the create live lession request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the create live lession request return failure");
            }

            String errorMessage = ErrorPrompts.createLessonPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.createLiveLesson(context, sessionID, lesson, callback);

    }

    /**
     * 获取开的课程
     * @param context
     * @param criteria
     * @param pagination
     * @param callback
     */
    public static void requestGetLessons(Context context,
                                         @NonNull String sessionID,
                                         @NonNull Criteria criteria,
                                         @NonNull Pagination pagination,
                                         @NonNull APIServiceCallback<GetLessonsResponse> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the GetLessons request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the GetLessons request return failure");
            }

            String errorMessage = ErrorPrompts.putLessonOnShelvesPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        LessonRequest lessonRequest = new LessonRequest();
        lessonRequest.getLessons(context,sessionID,criteria,pagination,callback);

    }

    /**
     * 上架直播课
     * @param context
     * @param sessionID
     * @param lesson
     * @param callback
     */
    public static void requestPutLessonOnShelves(Context context,
                                         @NonNull String sessionID,
                                         @NonNull String lesson,
                                         @NonNull APIServiceCallback<GetLessonsResponse> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the putLessonOnShelves request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)) {

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
        lessonRequest.putLessonOnShelves(context,sessionID,lesson,callback);

    }
}
