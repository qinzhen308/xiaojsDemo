package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.service.APIType;
import com.benyuan.xiaojs.data.api.service.ErrorPrompts;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by maxiaobao on 2016/11/13.
 */

public class PlatformRequest extends ServiceRequest {

    public PlatformRequest(Context context, APIServiceCallback callback) {

        super(context, callback);

    }

    public void getNotificationsOverview(@NonNull String sessionID, @NonNull Pagination pagination) {

        String paginationJson = objectToJsonString(pagination);

        Call<GNOResponse> call = getService().getNotificationsOverview(sessionID, paginationJson);
        enqueueRequest(APIType.GET_NOTIFICATIONS_OVERVIEW, call);

    }


    public void getNotifications(@NonNull String sessionID,
                                 @NonNull NotificationCriteria criteria,
                                 @NonNull Pagination pagination) {

        String criteriaJson = objectToJsonString(criteria);
        String paginationJson = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJson);
            Logger.json(paginationJson);
        }

        Call<GENotificationsResponse> call = getService().getNotifications(sessionID,
                criteriaJson,
                paginationJson);

        enqueueRequest(APIType.GET_NOTIFICATIONS, call);

    }

    public void deleteNotification(@NonNull String sessionID, @NonNull String notification) {

        Call<ResponseBody> call = getService().deleteNotification(sessionID, notification);
        enqueueRequest(APIType.DELETE_NOTIFICATION, call);

    }

    public void ignoreNotifications(@NonNull String sessionID,
                                    @NonNull NotificationCriteria criteria) {

        String criteriaJson = objectToJsonString(criteria);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJson);
        }

        Call<IgnoreNResponse> call = getService().ignoreNotifications(sessionID, criteriaJson);
        enqueueRequest(APIType.IGNORE_NOTIFICATIONS, call);
    }
}
