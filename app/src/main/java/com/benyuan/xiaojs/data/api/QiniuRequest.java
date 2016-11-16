package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.QiniuService;
import com.orhanobut.logger.Logger;
import com.qiniu.android.common.ServiceAddress;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;
import java.lang.ref.WeakReference;

/**
 * Created by maxiaobao on 2016/11/15.
 */

public class QiniuRequest {


    public void uploadCover(Context context,
                            @NonNull String sessionID,
                            @NonNull final String key,
                            @NonNull final String filePath,
                            @NonNull QiniuService qiniuService) {

        final WeakReference<QiniuService> callbackReference =
                new WeakReference<>(qiniuService);

        AccountRequest accountRequest = new AccountRequest();

        accountRequest.getCoverUpToken(context, sessionID, new APIServiceCallback<String>() {
            @Override
            public void onSuccess(String object) {
                if (XiaojsConfig.DEBUG) {
                    Logger.d("get upload for token onSuccess:%s ",object);
                }

                uploadFile(filePath,key,object,callbackReference);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("get upload for token onFailure ");
                }

                QiniuService callback = callbackReference.get();
                if (callback!=null){
                    callback.uploadFailure();
                }
            }
        });
    }

    public void uploadAvatar(Context context,
                             @NonNull String sessionID,
                             @NonNull final String key,
                             @NonNull final String filePath,
                             @NonNull QiniuService qiniuService) {


        final WeakReference<QiniuService> callbackReference =
                new WeakReference<>(qiniuService);


        AccountRequest accountRequest = new AccountRequest();

        accountRequest.getAvatarUpToken(context, sessionID, new APIServiceCallback<String>() {
            @Override
            public void onSuccess(String object) {
                if (XiaojsConfig.DEBUG) {
                    Logger.d("get upload for token onSuccess:%s ",object);
                }

                uploadFile(filePath,key,object,callbackReference);

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("get upload for token onFailure ");
                }

                QiniuService callback = callbackReference.get();
                if (callback!=null){
                    callback.uploadFailure();
                }
            }
        });





    }

    private void uploadFile(@NonNull String filePath,
                              @NonNull String key,
                              @NonNull String token,
                              @NonNull final WeakReference<QiniuService> callbackReference){

        Configuration configuration = createConfiguration();

        UploadManager uploadManager = new UploadManager(configuration);
        uploadManager.put(filePath, key, token, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("qiniu:%s", key + ",\r\n " + info + ",\r\n " + response);
                }

                if (info.isOK()){
                    QiniuService callback = callbackReference.get();
                    if (callback != null) {
                        StringBuilder fileUrl = new StringBuilder(QiniuService.AVATAR_BASE_URL);
                        fileUrl.append(key);
                        callback.uploadSucess(key,fileUrl.toString());
                    }
                }else{
                    QiniuService callback = callbackReference.get();
                    if (callback != null) {
                        callback.uploadFailure();
                    }
                }


            }
        },null);
    }



    private Configuration createConfiguration() {

        String upIp = "183.136.139.10";
        String upIp2 = "115.231.182.136";

        String upHost = "upload-z2.qiniu.com";
        String upHostBackup = "up-z2.qiniu.com";

        String[] upIps = {upIp, upIp2};
        ServiceAddress up = new ServiceAddress("http://" + upHost, upIps);
        ServiceAddress upBackup = new ServiceAddress("http://" + upHostBackup, upIps);

        Zone zone = new Zone(up, upBackup);

        Configuration con = new Configuration.Builder().zone(zone).build();

        return con;
    }
}
