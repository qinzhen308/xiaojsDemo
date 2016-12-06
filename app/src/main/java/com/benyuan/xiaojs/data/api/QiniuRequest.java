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

public class QiniuRequest implements APIServiceCallback<TokenResponse>{

    private AccountRequest accountRequest;
    private String filePath;
    private QiniuService qiniuService;


    public QiniuRequest(Context context,String filePath,QiniuService service) {
        this.filePath = filePath;
        this.qiniuService = service;
        this.accountRequest = new AccountRequest(context,this);
    }


    public void uploadCover(@NonNull String sessionID, @NonNull String lesson) {

        accountRequest.getCoverUpToken(sessionID,lesson);
    }

    public void uploadAvatar(@NonNull String sessionID) {

        accountRequest.getAvatarUpToken(sessionID);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Connect qiniu server and upload file
    //

    private void uploadFile(final String domain,
                            @NonNull final String filePath,
                            @NonNull String uploadKey,
                            @NonNull String uploadToken,
                            @NonNull final QiniuService callback){

        Configuration configuration = createConfiguration();

        UploadManager uploadManager = new UploadManager(configuration);
        uploadManager.put(filePath, uploadKey, uploadToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("qiniu:%s", key + ",\r\n " + info + ",\r\n " + response);
                }

                if (accountRequest.getServiceCallback() == null) {

                    if (XiaojsConfig.DEBUG) {
                        Logger.d("the API service callback is now null, so don't send callback and return");
                    }

                    return;
                }


                if (info.isOK()){
                    if (callback != null) {

                        StringBuilder fileUrl = new StringBuilder(domain)
                                .append("/")
                                .append(key);


                        callback.uploadSuccess(key,fileUrl.toString());
                    }
                }else{
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



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Xiaojs API service callback
    //

    @Override
    public void onSuccess(TokenResponse tokenResponse) {

        String token = tokenResponse.getToken();
        String key = tokenResponse.getKey();
        String domain = tokenResponse.getDomain();

        if (XiaojsConfig.DEBUG) {
            Logger.d("get upload for token onSuccess token=%s,key=%s,domain=%s",token,key,domain);
        }
        uploadFile(domain,filePath,key,token,qiniuService);

    }

    @Override
    public void onFailure(String errorCode, String errorMessage) {


        if (XiaojsConfig.DEBUG) {
            Logger.d("get upload for token onFailure ");
        }

        if (qiniuService!=null){
            qiniuService.uploadFailure();
        }

    }
}
