package com.benyuan.xiaojs.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.api.CategoriesRequest;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.CSubject;
import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/11/10.
 */

public class CategoriesDataManager extends DataManager {

    public static void requestGetSubject(@NonNull Context context,
                                         @NonNull APIServiceCallback<CSubject> callback){

        if (callback == null) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("the api service callback is null,so cancel the get home data request");
            }
            return;
        }

        CategoriesRequest categoriesRequest = new CategoriesRequest();
        categoriesRequest.getSubject(context,callback);
    }

}
