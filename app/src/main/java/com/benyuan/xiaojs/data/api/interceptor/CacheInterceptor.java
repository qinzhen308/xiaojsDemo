package com.benyuan.xiaojs.data.api.interceptor;

import com.benyuan.xiaojs.XiaojsConfig;
import com.orhanobut.logger.Logger;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by maxiaobao on 2016/12/16.
 */

public class CacheInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request oriRequest = chain.request();

        Response oriResponse = chain.proceed(oriRequest);

        String cacheControl = oriRequest.cacheControl().toString();

        if (XiaojsConfig.DEBUG){
            Logger.d("the request cache-control is:%s",cacheControl);
        }

        return oriResponse.newBuilder()
                .header("Cache-Control",cacheControl)
                .removeHeader("Pragma")
                .build();
    }
}
