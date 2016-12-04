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

    public PlatformRequest(Context context,APIServiceCallback callback) {

        super(context,callback);

    }

    public void getNotificationsOverview(@NonNull String sessionID, @NonNull Pagination pagination) {


        String paginationJson = objectToJsonString(pagination);

        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getNotificationsOverview(sessionID, paginationJson).enqueue(
                new Callback<GNOResponse>() {
                    @Override
                    public void onResponse(Call<GNOResponse> call, Response<GNOResponse> response) {

                        onRespones(APIType.GET_NOTIFICATIONS_OVERVIEW,response);
                    }

                    @Override
                    public void onFailure(Call<GNOResponse> call, Throwable t) {

                        onFailures(APIType.GET_NOTIFICATIONS_OVERVIEW,t);

                    }
                });

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

        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.getNotifications(sessionID, criteriaJson, paginationJson).enqueue(
                new Callback<GENotificationsResponse>() {

                    @Override
                    public void onResponse(Call<GENotificationsResponse> call,
                                           Response<GENotificationsResponse> response) {

                        onRespones(APIType.GET_NOTIFICATIONS,response);

                    }

                    @Override
                    public void onFailure(Call<GENotificationsResponse> call, Throwable t) {

                        onFailures(APIType.GET_NOTIFICATIONS,t);
                    }
                });


    }

    public void deleteNotification(@NonNull String sessionID, @NonNull String notification) {


        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.deleteNotification(sessionID, notification).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                onRespones(APIType.DELETE_NOTIFICATION,response);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                onFailures(APIType.DELETE_NOTIFICATION,t);


            }
        });
    }

    public void ignoreNotifications(@NonNull String sessionID,
                                    @NonNull NotificationCriteria criteria) {


        String criteriaJson = objectToJsonString(criteria);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJson);
        }

        XiaojsService xiaojsService = getAPIManager().getXiaojsService();
        xiaojsService.ignoreNotifications(sessionID, criteriaJson).enqueue(
                new Callback<IgnoreNResponse>() {

                    @Override
                    public void onResponse(Call<IgnoreNResponse> call, Response<IgnoreNResponse> response) {

                        onRespones(APIType.IGNORE_NOTIFICATIONS,response);
                    }

                    @Override
                    public void onFailure(Call<IgnoreNResponse> call, Throwable t) {

                        onFailures(APIType.IGNORE_NOTIFICATIONS,t);
                    }
                });
    }
}
