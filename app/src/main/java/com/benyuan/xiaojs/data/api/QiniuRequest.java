package com.benyuan.xiaojs.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.QiniuService;
import com.benyuan.xiaojs.model.TokenResponse;
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
                            @NonNull final String filePath,
                            @NonNull QiniuService qiniuService) {

        final WeakReference<QiniuService> callbackReference =
                new WeakReference<>(qiniuService);

        AccountRequest accountRequest = new AccountRequest();

        accountRequest.getCoverUpToken(context, sessionID, new APIServiceCallback<TokenResponse>() {
            @Override
            public void onSuccess(TokenResponse tokenResponse) {

                String token = tokenResponse.getToken();
                String key = tokenResponse.getKey();
                String domain = tokenResponse.getDomain();

                if (XiaojsConfig.DEBUG) {
                    Logger.d("get upload for token onSuccess token=%s,key=%s,domain=%s",token,key,domain);
                }
                uploadFile(domain,filePath,key,token,callbackReference);

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
                             @NonNull final String filePath,
                             @NonNull QiniuService qiniuService) {


        final WeakReference<QiniuService> callbackReference =
                new WeakReference<>(qiniuService);


        AccountRequest accountRequest = new AccountRequest();

        accountRequest.getAvatarUpToken(context, sessionID, new APIServiceCallback<TokenResponse>() {
            @Override
            public void onSuccess(TokenResponse tokenResponse) {

                String token = tokenResponse.getToken();
                String key = tokenResponse.getKey();
                String domain = tokenResponse.getDomain();

                if (XiaojsConfig.DEBUG) {
                    Logger.d("get upload for token onSuccess token=%s,key=%s,domain=%s",token,key,domain);
                }

                uploadFile(domain,filePath,key,token,callbackReference);

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

    private void uploadFile(final String domain,
                            @NonNull final String filePath,
                            @NonNull String uploadKey,
                            @NonNull String uploadToken,
                            @NonNull final WeakReference<QiniuService> callbackReference){

        Configuration configuration = createConfiguration();

        UploadManager uploadManager = new UploadManager(configuration);
        uploadManager.put(filePath, uploadKey, uploadToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("qiniu:%s", key + ",\r\n " + info + ",\r\n " + response);
                }

                if (info.isOK()){
                    QiniuService callback = callbackReference.get();
                    if (callback != null) {

                        StringBuilder fileUrl = new StringBuilder(domain)
                                .append("/")
                                .append(key);


                        callback.uploadSuccess(key,fileUrl.toString());
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
