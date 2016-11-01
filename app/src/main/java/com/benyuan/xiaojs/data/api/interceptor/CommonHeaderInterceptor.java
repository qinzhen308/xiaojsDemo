/*
 * =======================================================================================
 * Package Name :  com.benyuan.xiaojs.data.api.interceptor.CommonHeaderInterceptor
 * Source Name   :  CommonHeaderInterceptor.java
 * Abstract       :
 *
 * ---------------------------------------------------------------------------------------
 *
 * Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 * This computer program source code file is protected by copyright law and international
 * treaties. Unauthorized distribution of source code files, programs, or portion of the
 * package, may result in severe civil and criminal penalties, and will be prosecuted to
 * the maximum extent under the law.
 *
 * ---------------------------------------------------------------------------------------
 * Revision History:
 * Date          :  Revised on 16-10-26 下午3:37
 * Abstract    :  Initial version by maxiaobao
 *
 * ========================================================================================
 */

package com.benyuan.xiaojs.data.api.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by maxiaobao on 2016/10/26.
 *
 */

public class CommonHeaderInterceptor implements Interceptor{

    private int appType;
    private String appVersion;

    public CommonHeaderInterceptor(int appType, String appVersion){
        this.appVersion = appVersion;
        this.appType = appType;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .addHeader("XA", Integer.toString(appType))
                .addHeader("XAV", appVersion);

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
