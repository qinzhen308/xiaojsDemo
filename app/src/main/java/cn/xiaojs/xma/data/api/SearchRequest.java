package cn.xiaojs.xma.data.api;

import android.content.Context;

import java.util.ArrayList;

import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.search.AccountSearch;
import retrofit2.Call;

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
}
