package com.benyuan.xiaojs.data.api.service;

import com.benyuan.xiaojs.model.APIEntity;
import com.benyuan.xiaojs.model.CreateLession;
import com.benyuan.xiaojs.model.Empty;
import com.benyuan.xiaojs.model.HomeData;
import com.benyuan.xiaojs.model.LoginInfo;
import com.benyuan.xiaojs.model.LoginParams;
import com.benyuan.xiaojs.model.RegisterInfo;
import com.benyuan.xiaojs.model.VerifyCode;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;


/**
 * Created by maxiaobao on 2016/10/25.
 */

public interface XiaojsService {

    //Xiaojs rest api 中接口公共URL
    String BASE_URL = "http://192.168.100.4:3000/";


    //注册
    @Headers("Content-Type: application/json")
    @POST("/v1/accounts")
    Call<Empty> accountRegister(@Body RegisterInfo registerInfo);

    //登陆
    @Headers("Content-Type: application/json")
    @POST("/v1/security/login")
    Call<LoginInfo> login(@Body LoginParams params);


    //退出登陆
    @Headers("Content-Type: application/json")
    @DELETE("/v1/security/logout")
    Call<Empty> logout(@Header("SessionID") String sessionID);


    //验证验证码
    @GET("/v1/security/validate/{method}/{mobile}/{code}")
    Call<APIEntity> validateCode(@Path("method") int method,
                                 @Path("mobile") long mobile,
                                 @Path("code") int code);

    //获取验证码
    @GET("/v1/security/verify/{method}/{mobile}")
    Call<VerifyCode> sendVerifyCode(@Path("method") int method, @Path("mobile") long mobile);


    //Get Home Data
    @GET("/v1/accounts/home")
    Call<HomeData> getHomeData(@Header("SessionID") String sessionID);

    //创建直播课
    @Headers("Content-Type: application/json")
    @POST("/v1/ctl/lessons")
    Call<Empty> createLiveLession(@Header("SessionID") String sessionID,
                                  @Body CreateLession lession);

}
