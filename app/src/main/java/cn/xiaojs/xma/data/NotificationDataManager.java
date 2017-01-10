package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.PlatformRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.GENotificationsResponse;
import cn.xiaojs.xma.model.GNOResponse;
import cn.xiaojs.xma.model.IgnoreNResponse;
import cn.xiaojs.xma.model.NotificationCriteria;
import cn.xiaojs.xma.model.Pagination;
import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/11/13.
 */

public class NotificationDataManager {

    public static void requestNotificationsOverview(Context context,
                           @NonNull Pagination pagination,
                           @NonNull APIServiceCallback<GNOResponse> callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }


        PlatformRequest platformRequest = new PlatformRequest(context,callback);
        platformRequest.getNotificationsOverview(pagination);
    }

    public static void requestNotifications(Context context,
                                            @NonNull NotificationCriteria criteria,
                                            @NonNull Pagination pagination,
                                            @NonNull APIServiceCallback<GENotificationsResponse> callback) {


        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        PlatformRequest platformRequest = new PlatformRequest(context,callback);
        platformRequest.getNotifications(criteria,pagination);


    }

    public static void requestDelNotification(Context context,
                                          @NonNull String notification,
                                          @NonNull APIServiceCallback callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }

        PlatformRequest platformRequest = new PlatformRequest(context,callback);
        platformRequest.deleteNotification(notification);

    }

    public static void ignoreNotifications(Context context,
                                           @NonNull NotificationCriteria criteria,
                                           @NonNull APIServiceCallback<IgnoreNResponse> callback) {

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the request");
            }
            return;
        }


        PlatformRequest platformRequest = new PlatformRequest(context,callback);
        platformRequest.ignoreNotifications(criteria);
    }
}
