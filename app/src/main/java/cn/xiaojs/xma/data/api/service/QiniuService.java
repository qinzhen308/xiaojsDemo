package cn.xiaojs.xma.data.api.service;


import cn.xiaojs.xma.model.material.UploadReponse;

/**
 * Created by maxiaobao on 2016/11/15.
 */

public interface QiniuService {
    String COVER_BASE_URL = "http://ognyt4ea0.bkt.clouddn.com/";

    /**
     * 文件上传成功
     * @param key
     * @param reponse 上传成功后的响应消息
     */
    void uploadSuccess(String key, UploadReponse reponse);

    /**
     * 文件上传进度
     * @param key
     * @param percent
     */
    void uploadProgress(String key, double percent);

    /**
     * 文件上传失败
     * @param cancel true表示用户取消了上传
     */
    void uploadFailure(boolean cancel);

}
