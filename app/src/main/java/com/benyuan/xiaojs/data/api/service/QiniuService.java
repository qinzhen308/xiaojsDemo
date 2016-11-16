package com.benyuan.xiaojs.data.api.service;

/**
 * Created by maxiaobao on 2016/11/15.
 */

public interface QiniuService {

    String AVATAR_BASE_URL = "http://ogkty8t9b.bkt.clouddn.com/";
    String COVER_BASE_URL = "http://ognyt4ea0.bkt.clouddn.com/";

    void uploadSucess(String fileName,String fileUrl);

    void uploadFailure();

}
