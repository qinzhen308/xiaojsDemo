package com.benyuan.xiaojs.data.api;

import com.benyuan.xiaojs.data.api.model.RegisterInfo;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public interface XiaojsService {

    //Xiaojs rest api 中接口公共URL
    String BASE_URL = "";


    //注册
    @POST("/v1/accounts")
    Call accountRegister(@Body RegisterInfo registerInfo);




}
