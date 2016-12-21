/*
 * =======================================================================================
 * Package Name :  cn.xiaojs.xma.api.InterceptorTest
 * Source Name   :  InterceptorTest.java
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
 * Date          :  Revised on 16-10-26 下午4:12
 * Abstract    :  Initial version by maxiaobao
 *
 * ========================================================================================
 */

package cn.xiaojs.xma.api;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by maxiaobao on 2016/10/26.
 */

public class InterceptorTest {

    public static void testCommonHeader(){

        HttpLoggingInterceptor logInterceptor = new HttpLoggingInterceptor();
        logInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //CommonHeaderInterceptor headerInterceptor =  new CommonHeaderInterceptor();

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                //.addNetworkInterceptor(logInterceptor)
               // .addInterceptor(headerInterceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TestApiService.BASE_URL)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(okHttpClient)
                .build();


        TestApiService api = retrofit.create(TestApiService.class);
        try {
            api.getUserList().execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        api.getUserList().enqueue(new Callback<HubMsg>() {
//            @Override
//            public void onResponse(Call<HubMsg> call, Response<HubMsg> response) {
//                System.out.println("onResponse--------------------");
//            }
//
//            @Override
//            public void onFailure(Call<HubMsg> call, Throwable t) {
//                System.out.println("onFailure--------------------");
//            }
//        });



    }
}
