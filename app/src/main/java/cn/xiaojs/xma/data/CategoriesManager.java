package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.CategoriesRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.Pagination;

import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by maxiaobao on 2016/11/10.
 */

public class CategoriesManager {

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


    public static void getSubjects(@NonNull Context context,
                                   String parent,
                                   Pagination pagination,
                                   @NonNull APIServiceCallback<List<CSubject>> callback){

        if (TextUtils.isEmpty(parent)){
            parent = "root";
        }

        int page = 1;
        int limit = 100;
        if (pagination != null){
            page = pagination.getPage();
            limit = pagination.getMaxNumOfObjectsPerPage();
        }

        CategoriesRequest categoriesRequest = new CategoriesRequest(context,callback);
        categoriesRequest.getSubjects(parent, page, limit);
    }

}
