package cn.xiaojs.xma.data;

import android.content.Context;

import java.util.ArrayList;

import cn.xiaojs.xma.data.api.SearchRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.search.AccountSearch;

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
}
