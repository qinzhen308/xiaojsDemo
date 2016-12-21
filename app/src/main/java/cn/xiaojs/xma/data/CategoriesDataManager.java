package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.CategoriesRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
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

        CategoriesRequest categoriesRequest = new CategoriesRequest(context,callback);
        categoriesRequest.getSubject();
    }

}