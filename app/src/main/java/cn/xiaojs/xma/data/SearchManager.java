package cn.xiaojs.xma.data;

import android.content.Context;

import java.util.ArrayList;

import cn.xiaojs.xma.data.api.SearchRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.CollectionPageData;
import cn.xiaojs.xma.model.CollectionResult;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.search.AccountInfo;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.model.search.LessonInfo;
import cn.xiaojs.xma.model.search.SearchResponse;
import cn.xiaojs.xma.model.search.SearchResultV2;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2016/12/28.
 */

public class SearchManager {

    /**
     * Search Accounts
     * Searches accounts with the specific mobile criteria for implementing auto-complete feature.
     */
    public static ServiceRequest searchAccounts(Context context,
                                                String query,
                                                APIServiceCallback<ArrayList<AccountSearch>> callback) {

        SearchRequest searchRequest = new SearchRequest(context, callback);
        searchRequest.searchAccounts(query);
        return searchRequest;

    }

    /**
     * Search accounts by userName or phoneNumber.
     */
    public static ServiceRequest searchAccounts(Context context,
                                                String key,
                                                Pagination pagination,
                                                String type,
                                                APIServiceCallback<CollectionPageData<AccountInfo>> callback) {

        int page = 1;
        int limit = 20;

        if (pagination != null) {
            page = pagination.getPage();
            limit = pagination.getMaxNumOfObjectsPerPage();
        }

        SearchRequest searchRequest = new SearchRequest(context, callback);
        searchRequest.searchAccounts(key, page, limit, type);
        return searchRequest;
    }

    /**
     * Search lessons by title / tags.
     */
    public static ServiceRequest searchLessons(Context context,
                                               String key,
                                               Pagination pagination,
                                               String type,
                                               APIServiceCallback<CollectionPageData<LessonInfo>> callback) {

        int page = 1;
        int limit = 20;

        if (pagination != null) {
            page = pagination.getPage();
            limit = pagination.getMaxNumOfObjectsPerPage();
        }


        SearchRequest searchRequest = new SearchRequest(context, callback);
        searchRequest.searchLessons(key, page, limit, type);
        return searchRequest;
    }

    /**
     * Search lessons & accounts.
     */
    public static ServiceRequest searchAccountsOrLessons(Context context,
                                                         String key,
                                                         String size,
                                                         APIServiceCallback<SearchResponse> callback) {

        SearchRequest searchRequest = new SearchRequest(context, callback);
        searchRequest.searchAccountsOrLessons(key, size);
        return searchRequest;

    }

    /**
     * Search search.
     */
    public static ServiceRequest search(Context context,
                                      String type,
                                      String keyword,
                                      int page,
                                      int limit,
                                      APIServiceCallback<CollectionResult<SearchResultV2>> callback) {

        SearchRequest searchRequest = new SearchRequest(context, callback);
        searchRequest.search(type, keyword, page, limit);
        return searchRequest;

    }


}
