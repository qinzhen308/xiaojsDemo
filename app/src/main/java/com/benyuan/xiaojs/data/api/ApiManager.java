package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Platform;
import com.benyuan.xiaojs.data.api.interceptor.CommonHeaderInterceptor;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.util.APPUtils;
import com.benyuan.xiaojs.util.UIUtils;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class ApiManager {


    private volatile static ApiManager apiManager;
    //private XiaojsService xiaojsService;
    private OkHttpClient okHttpClient;
    private Context appContext;


    public static ApiManager getAPIManager(Context appContext) {

        if (apiManager == null) {

            synchronized (ApiManager.class) {

                if (apiManager == null) {
                    apiManager = new ApiManager(appContext);
                }
            }
        }

        return apiManager;
    }

    private ApiManager(Context appContext) {
        this.appContext = appContext.getApplicationContext();
    }

    public Context getAppContext() {
        return appContext;
    }


    public XiaojsService getXiaojsService() {

//        if (xiaojsService == null) {
//            xiaojsService = createXiaojsService();
//        }
//
//        return xiaojsService;

        return createXiaojsService();

    }

    private OkHttpClient createOkhttp() {

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        if (XiaojsConfig.DEBUG) {
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {

            logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }


        int appType = getAPPType();
        String appVerion = APPUtils.getAPPFullVersion(appContext);

        CommonHeaderInterceptor headerInterceptor = new CommonHeaderInterceptor(appType, appVerion);


        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(headerInterceptor)
                .connectTimeout(20,TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20,TimeUnit.SECONDS);


        if (XiaojsConfig.DEBUG) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        return builder.build();

    }

    private XiaojsService createXiaojsService() {

//        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
//        if (XiaojsConfig.DEBUG) {
//            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        } else {
//
//            logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
//        }
//
//
//        int appType = getAPPType();
//        String appVerion = APPUtils.getAPPFullVersion(appContext);
//
//        CommonHeaderInterceptor headerInterceptor = new CommonHeaderInterceptor(appType, appVerion);
//
//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(logInterceptor)
//                .addNetworkInterceptor(logInterceptor)
//                .addInterceptor(headerInterceptor)
//                .build();

        if (okHttpClient == null) {
            okHttpClient = createOkhttp();
        }


        JacksonConverterFactory jacksonFactory = JacksonConverterFactory.create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(XiaojsService.BASE_URL)
                .addConverterFactory(jacksonFactory)
                .client(okHttpClient)
                .build();

        return retrofit.create(XiaojsService.class);
    }


    private int getAPPType() {

        if (UIUtils.isTablet(appContext)) {
            return Platform.AppType.TABLET_ANDROID;
        }

        return Platform.AppType.MOBILE_ANDROID;
    }

}
