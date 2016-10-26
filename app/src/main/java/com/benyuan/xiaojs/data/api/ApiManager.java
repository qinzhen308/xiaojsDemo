package com.benyuan.xiaojs.data.api;

import android.content.Context;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.interceptor.CommonHeaderInterceptor;
import com.benyuan.xiaojs.util.APPUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class APIManager {

    private volatile static APIManager apiManager;
    private XiaojsService xiaojsService;
    private Context appContext;


    public static APIManager getAPIManager(Context appContext) {

        if (apiManager == null) {

            synchronized (APIManager.class) {

                if (apiManager == null) {
                    apiManager = new APIManager(appContext);
                }
            }
        }

        return apiManager;
    }

    private APIManager(Context appContext) {
        this.appContext = appContext.getApplicationContext();
    }


    public XiaojsService getXiaojsService() {

        if (xiaojsService == null) {
            xiaojsService = createXiaojsService();
        }

        return xiaojsService;

    }

    private XiaojsService createXiaojsService() {

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        if (XiaojsConfig.RELEASE) {
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        } else {
            logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        String appVerion = APPUtils.getAPPFullVersion(appContext);
        CommonHeaderInterceptor headerInterceptor = new CommonHeaderInterceptor(appVerion);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(headerInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(XiaojsService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(XiaojsService.class);
    }


}
