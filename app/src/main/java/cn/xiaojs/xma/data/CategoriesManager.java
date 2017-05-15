package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.CategoriesRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;

import com.orhanobut.logger.Logger;

import java.util.List;

/**
 * Created by maxiaobao on 2016/11/10.
 */

public class CategoriesManager {

    /**
     * Returns a subject by subject id.
     * @param context
     * @param callback
     */
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


    /**
     * Returns subjects for subject optional list.
     * @param context
     * @param parent
     * @param pagination
     * @param callback
     */
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

    /**
     * Add a subject to subject list.
     * @param context
     * @param name
     * @param callback
     */
    public static void addOpenSubject(Context context, String name, APIServiceCallback<CSubject> callback) {
        CategoriesRequest categoriesRequest = new CategoriesRequest(context,callback);
        categoriesRequest.addOpenSubject(name);
    }

    /**
     * Returns subjects for subject list.
     * @param context
     * @param targetName
     * @param pagination
     * @param callback
     */
    public static void searchSubjects(Context context,
                                      String targetName,
                                      Pagination pagination,
                                      APIServiceCallback<CollectionPage<CSubject>> callback) {

        CategoriesRequest categoriesRequest = new CategoriesRequest(context,callback);
        categoriesRequest.searchSubjects(targetName,pagination);
    }

}
