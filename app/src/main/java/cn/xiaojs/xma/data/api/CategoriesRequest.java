package cn.xiaojs.xma.data.api;

import android.content.Context;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.CSubject;

import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2016/11/10.
 */

public class CategoriesRequest extends ServiceRequest {

    public CategoriesRequest(Context context, APIServiceCallback callback) {

        super(context, callback);

    }

    public void getSubject() {

        Call<CSubject> call = getService().getSubject();
        enqueueRequest(APIType.GET_SUBJECT,call);

    }

    public void getSubjects(String parent, int page, int limit) {

        Call<List<CSubject>> call = getService().getSubjects(parent,page, limit);
        enqueueRequest(APIType.GET_SUBJECTS,call);

    }

    public void addOpenSubject(String name) {
        Call<ResponseBody> call = getService().addOpenSubject(name);
        enqueueRequest(APIType.ADD_OPEN_SUBJECT,call);
    }

    public void searchSubjects(String name, Pagination pagination) {

        String paginationJsonstr = objectToJsonString(pagination);
        if (XiaojsConfig.DEBUG) {
            Logger.json(paginationJsonstr);
        }
        Call<CollectionPage<CSubject>> call = getService().searchSubjects(name,paginationJsonstr);
        enqueueRequest(APIType.SEARCH_SUBJECTS,call);
    }

}
