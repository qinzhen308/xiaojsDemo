/*
 * =======================================================================================
 * Package Name :  cn.xiaojs.xma.data.api.interceptor.CommonHeaderInterceptor
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

package cn.xiaojs.xma.data.api.interceptor;

import android.content.Context;

import java.io.IOException;
import java.lang.reflect.Method;

import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.service.XiaojsService;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.data.preference.SecurityPref;
import cn.xiaojs.xma.util.APPUtils;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by maxiaobao on 2016/10/26.
 *
 */

public class CommonHeaderInterceptor implements Interceptor{


    private Context context;

    public CommonHeaderInterceptor(Context context){
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {

        Request original = chain.request();

        // Request customization: add request headers
        Request.Builder requestBuilder = original.newBuilder()
                .addHeader("XA", Integer.toString(APPUtils.getAPPType(context)))
                .addHeader("XAV", APPUtils.getAPPFullVersion(context))
                .addHeader("SessionID", AccountPref.getAuthToken(context));

        if (original.method() != XiaojsService.METHOD_GET) {
            requestBuilder.addHeader("X-CSRF-Token", SecurityPref.getCSRFToken(context));
            //requestBuilder.addHeader("Cookie", SecurityPref.getCSRFCookie(context));
        }

        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
