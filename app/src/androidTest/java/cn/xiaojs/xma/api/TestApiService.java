/*
 * =======================================================================================
 * Package Name :  cn.xiaojs.xma.api.TestApiService
 * Source Name   :  TestApiService.java
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
 * Date          :  Revised on 16-10-26 下午4:07
 * Abstract    :  Initial version by maxiaobao
 *
 * ========================================================================================
 */

package cn.xiaojs.xma.api;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by maxiaobao on 2016/10/26.
 */

public interface TestApiService {


    String BASE_URL = "https://api.github.com/";

    @GET("users/list")
    Call<HubMsg> getUserList();

}
