package com.benyuan.xiaojs.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.ErrorPrompts;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.PlatformRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.GNOResponse;
import com.benyuan.xiaojs.model.IgnoreNResponse;
import com.benyuan.xiaojs.model.Notification;
import com.benyuan.xiaojs.model.NotificationCriteria;
import com.benyuan.xiaojs.model.Pagination;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

/**
 * Created by maxiaobao on 2016/11/13.
 */

public class NotificationDataManager extends DataManager {

    public static void requestNotificationsOverview(Context context,
                           @NonNull String sessionID,
                           @NonNull Pagination pagination,
                           @NonNull APIServiceCallback<GNOResponse> callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.getNotificationsOverviewPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }


        PlatformRequest platformRequest = new PlatformRequest();
        platformRequest.getNotificationsOverview(context,sessionID,pagination,callback);
    }

    public static void requestNotifications(Context context,
                                            @NonNull String sessionID,
                                            @NonNull NotificationCriteria criteria,
                                            @NonNull Pagination pagination,
                                            @NonNull APIServiceCallback<ArrayList<Notification>> callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.getNotificationsPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }


        PlatformRequest platformRequest = new PlatformRequest();
        platformRequest.getNotifications(context,sessionID,criteria,pagination,callback);


    }

    public static void requestDelNotification(Context context,
                                          @NonNull String sessionID,
                                          @NonNull String notification,
                                          @NonNull APIServiceCallback callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.deleteNotificationPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        PlatformRequest platformRequest = new PlatformRequest();
        platformRequest.deleteNotification(context,sessionID,notification,callback);

    }

    public static void ignoreNotifications(Context context,
                                           @NonNull String sessionID,
                                           @NonNull NotificationCriteria criteria,
                                           @NonNull APIServiceCallback<IgnoreNResponse> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the sessionID is empty,so the request return failure");
            }

            String errorMessage = ErrorPrompts.ignoreNotificationsPrompt(Errors.BAD_SESSION);
            callback.onFailure(Errors.BAD_SESSION,errorMessage);
            return;
        }

        PlatformRequest platformRequest = new PlatformRequest();
        platformRequest.ignoreNotifications(context,sessionID,criteria,callback);
    }
}
