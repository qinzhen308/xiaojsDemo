package cn.xiaojs.xma.data.api;

import android.content.Context;
import android.support.annotation.NonNull;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.colla.TokenPair;

import com.orhanobut.logger.Logger;
import com.qiniu.android.common.ServiceAddress;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

/**
 * Created by maxiaobao on 2016/11/15.
 */

public class QiniuRequest implements APIServiceCallback<TokenPair[]>{

    private CollaRequest collaRequest;
    private String filePath;
    private QiniuService qiniuService;
    private int type;


    public QiniuRequest(Context context,String filePath,QiniuService service) {
        this.filePath = filePath;
        this.qiniuService = service;
        this.collaRequest = new CollaRequest(context,this);
    }


//    public void uploadCover(@NonNull String sessionID, @NonNull String lesson) {
//
//        accountRequest.getCoverUpToken(sessionID,lesson);
//    }
//
    public void getToken(int type, int quantity) {

        this.type = type;
        collaRequest.getUploadTokens(type, quantity);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Connect qiniu server and upload file
    //

    private void uploadFile(@NonNull final String filePath,
                            @NonNull String uploadKey,
                            @NonNull String uploadToken,
                            @NonNull final QiniuService callback){

       // Configuration configuration = createConfiguration();
       // UploadManager uploadManager = new UploadManager(configuration);
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(filePath, uploadKey, uploadToken, new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("qiniu:%s", key + ",\r\n " + info + ",\r\n " + response);
                }

                if (collaRequest.getServiceCallback() == null) {

                    if (XiaojsConfig.DEBUG) {
                        Logger.d("the API service callback is now null, so don't send callback and return");
                    }

                    return;
                }


                if (info.isOK()){
                    if (callback != null) {

                        callback.uploadSuccess(key,null);
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
    public void onSuccess(TokenPair[] tokenPairs) {



        if(tokenPairs == null || tokenPairs.length <= 0) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("get upload for token is null or lenght = 0");
            }

            if (qiniuService!=null){
                qiniuService.uploadFailure();
            }

            return;
        }

        //因为目前所有的上传只支持一次上传一个文件，所以只要取第一个token即可。
        TokenPair tokenPair = tokenPairs[0];
        if (tokenPair == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("get upload for tokenpair is null");
            }

            if (qiniuService!=null){
                qiniuService.uploadFailure();
            }

            return;
        }


        String token = tokenPair.token;
        String key = tokenPair.key;
        //String domain = upToken.domain;

        if (XiaojsConfig.DEBUG) {
            Logger.d("get upload for token onSuccess token=%s,key=%s",token,key);
        }
        uploadFile(filePath,key,token,qiniuService);

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
