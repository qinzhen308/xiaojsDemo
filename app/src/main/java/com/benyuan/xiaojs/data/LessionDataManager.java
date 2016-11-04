package com.benyuan.xiaojs.data;

import android.content.Context;
import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.api.LessionRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.CreateLession;
import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/11/4.
 */

public class LessionDataManager {


    /**
     * 创建直播课
     * @param context
     * @param sessionID
     * @param lession
     * @param callback
     */
    public static void requestCreateLiveLession(Context context,
                                                String sessionID,
                                                CreateLession lession,
                                                APIServiceCallback callback) {

        if (callback == null){
            if(XiaojsConfig.DEBUG){
                Logger.d("the api service callback is null,so cancel the create live lession request");
            }
            return;
        }

        if (TextUtils.isEmpty(sessionID)){

            if(XiaojsConfig.DEBUG){
                Logger.d("the sessionID is empty,so the create live lession request return failure");
            }

            callback.onFailure(Errors.UNAUTHORIZED);
            return;
        }

        LessionRequest lessionRequest = new LessionRequest();
        lessionRequest.createLiveLession(context,sessionID,lession,callback);

    }
}
