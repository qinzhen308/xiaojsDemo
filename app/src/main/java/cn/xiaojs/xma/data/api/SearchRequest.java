package cn.xiaojs.xma.data.api;

import android.content.Context;

import java.util.ArrayList;

import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.CollectionPageData;
import cn.xiaojs.xma.model.search.AccountInfo;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.model.search.LessonInfo;
import cn.xiaojs.xma.model.search.SearchResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Query;

/**
 * Created by maxiaobao on 2016/12/28.
 */

public class SearchRequest extends ServiceRequest{

    public SearchRequest(Context context, APIServiceCallback callback) {
        super(context, callback);
    }

    public void searchAccounts(String query) {
        Call<ArrayList<AccountSearch>> call = getService().searchAccounts(query);
        enqueueRequest(APIType.SEARCH_ACCOUNT,call);
    }

    public void searchAccounts(String key, int page, int limit, String type) {
        Call<CollectionPageData<AccountInfo>> call = getService().searchAccounts(key, page, limit, type);
        enqueueRequest(APIType.SEARCH_ACCOUNT_V2, call);
    }

    public void searchLessons(String key, int page, int limit, String type) {
        Call<CollectionPageData<LessonInfo>> call = getService().searchLessons(key, page, limit, type);
        enqueueRequest(APIType.SEARCH_LESSONS, call);
    }

    public void searchAccountsOrLessons(String key, String size) {
        Call<SearchResponse> call = getService().searchAccountsOrLessons(key, size);
        enqueueRequest(APIType.SEARCH_ACCOUNT_OR_LESSON, call);
    }
}
