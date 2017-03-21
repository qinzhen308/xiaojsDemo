package cn.xiaojs.xma.data.api;

import android.content.Context;

import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.contents.Article;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2017/3/21.
 */

public class ContentRequest extends ServiceRequest {

    public ContentRequest(Context context, APIServiceCallback callback) {
        super(context, callback);
    }

    public void getArticle(String article) {
        Call<Article> call = getService().getArticle(article);
        enqueueRequest(APIType.GET_ARTICLE, call);
    }
}
