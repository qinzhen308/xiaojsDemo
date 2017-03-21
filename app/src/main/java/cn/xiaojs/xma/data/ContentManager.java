package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.data.api.ContentRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.contents.Article;

/**
 * Created by maxiaobao on 2017/3/21.
 */

public class ContentManager {

    /**
     * Returns the details for the specific article.
     * @param context
     * @param article
     * @param callback
     */
    public static void getArticle(Context context, String article, APIServiceCallback<Article> callback) {
        ContentRequest request = new ContentRequest(context,callback);
        request.getArticle(article);

    }
}
