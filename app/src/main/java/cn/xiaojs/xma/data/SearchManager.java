package cn.xiaojs.xma.data;

import android.content.Context;

import java.util.ArrayList;

import cn.xiaojs.xma.data.api.SearchRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPageData;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.search.AccountInfo;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.model.search.LessonInfo;
import cn.xiaojs.xma.model.search.SearchResponse;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2016/12/28.
 */

public class SearchManager {

    /**
     * Search Accounts
     * Searches accounts with the specific mobile criteria for implementing auto-complete feature.
     * @param context
     * @param query
     * @param callback
     */
    public static void searchAccounts(Context context,
                                      String query,
                                      APIServiceCallback<ArrayList<AccountSearch>> callback) {

        SearchRequest searchRequest = new SearchRequest(context,callback);
        searchRequest.searchAccounts(query);

    }

    /**
     * Search accounts by userName or phoneNumber.
     * @param context
     * @param key
     * @param page
     * @param limit
     * @param type
     * @param callback
     */
    public static void searchAccounts(Context context,
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

        SearchRequest searchRequest = new SearchRequest(context,callback);
        searchRequest.searchAccounts(key, page, limit, type);
    }

    /**
     * Search lessons by title / tags.
     * @param context
     * @param key
     * @param page
     * @param limit
     * @param type
     * @param callback
     */
    public static void searchLessons(Context context,
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



        SearchRequest searchRequest = new SearchRequest(context,callback);
        searchRequest.searchLessons(key, page, limit, type);
    }

    /**
     * Search lessons & accounts.
     * @param context
     * @param key
     * @param size
     * @param callback
     */
    public static void searchAccountsOrLessons(Context context,
                                               String key,
                                               String size,
                                               APIServiceCallback<SearchResponse> callback) {

        SearchRequest searchRequest = new SearchRequest(context,callback);
        searchRequest.searchAccountsOrLessons(key, size);

    }


}
