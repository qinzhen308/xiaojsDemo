package cn.xiaojs.xma.data.api.service;

/**
 * Created by maxiaobao on 2016/11/15.
 */

public interface QiniuService {
    String COVER_BASE_URL = "http://ognyt4ea0.bkt.clouddn.com/";

    /**
     *
     * @param key
     * @param fileUrl 永远返回NULL
     */
    void uploadSuccess(String key,String fileUrl);

    void uploadFailure();

}
