package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Platform;
import com.benyuan.xiaojs.data.api.interceptor.CommonHeaderInterceptor;
import com.benyuan.xiaojs.data.api.service.XiaojsService;
import com.benyuan.xiaojs.util.APPUtils;
import com.benyuan.xiaojs.util.UIUtils;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class ApiManager {


    private volatile static ApiManager apiManager;
    private XiaojsService xiaojsService;
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


        int appType = getAPPType();
        String appVerion = APPUtils.getAPPFullVersion(appContext);

        CommonHeaderInterceptor headerInterceptor = new CommonHeaderInterceptor(appType,appVerion);


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



    private int getAPPType(){

        if (UIUtils.isTablet(appContext)){
            return Platform.AppType.TABLET_ANDROID;
        }

        return Platform.AppType.MOBILE_ANDROID;
    }

    /**
     * 根据错误码，返回内部错误信息。
     * @param errorCode
     */
    public static String getInternalErrorMessage(String errorCode) {

        String errorMessage = "未知错误";

        if (TextUtils.isEmpty(errorCode)){
            return errorMessage;
        }

        switch (errorCode){
            case Errors.NO_ERROR:
                errorMessage = "未指定错误";
                break;
            case Errors.ILLEGAL_CALL:
                errorMessage = "非法请求或调用";
                break;
            case Errors.NOT_IMPLEMENTED:
                errorMessage = "功能没有实现";
                break;
            case Errors.SERVER_ERROR:
                errorMessage = "服务器内部错误";
                break;
            case Errors.INVALID_CSRF:
                errorMessage = "无效的CSRF令牌";
                break;
            case Errors.INVALID_OPERATION:
                errorMessage = "无效的操作";
                break;
            case Errors.TYPE_NOT_FOUND:
                errorMessage = "类型没被找到";
                break;
            case Errors.DOC_NOT_FOUND:
                errorMessage = "文档没被找到";
                break;
            case Errors.DOC_ALREADY_EXISTS:
                errorMessage = "文档已存在";
                break;
            case Errors.OPERATION_TIMEOUT:
                errorMessage = "操作超时";
                break;
            case Errors.NOT_SUPPORTED:
                errorMessage = "不支持该客户端";
                break;
            default:
                break;
        }
        return errorCode;
    }
}
