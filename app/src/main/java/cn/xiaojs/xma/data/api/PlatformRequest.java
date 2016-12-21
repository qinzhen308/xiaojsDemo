package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.GENotificationsResponse;
import cn.xiaojs.xma.model.GNOResponse;
import cn.xiaojs.xma.model.IgnoreNResponse;
import cn.xiaojs.xma.model.NotificationCriteria;
import cn.xiaojs.xma.model.Pagination;
import com.orhanobut.logger.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;

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
