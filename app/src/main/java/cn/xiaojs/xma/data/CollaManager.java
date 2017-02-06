package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.data.api.CollaRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Colla.LibCriteria;
import cn.xiaojs.xma.model.Colla.LibOverview;
import cn.xiaojs.xma.model.Colla.UploadParam;
import cn.xiaojs.xma.model.Colla.UploadReponse;
import cn.xiaojs.xma.model.Pagination;

/**
 * Created by maxiaobao on 2017/2/6.
 */

public class CollaManager {

    /**
     * Provides access to collaboration interfaces accessible to the Xiaojs client applications.
     * @param context
     * @param param
     * @param callback
     */
    public static void addToLibrary(Context context,
                                    UploadParam param,
                                    APIServiceCallback<UploadReponse> callback) {
        CollaRequest request = new CollaRequest(context,callback);
        request.addToLibrary(param);
    }

    /**
     * Returns overview for all non-empty document categories in specific library.
     * @param context
     * @param criteria
     * @param pagination
     * @param callback
     */
    public void getLibraryOverview(Context context,
                                   LibCriteria criteria,
                                   Pagination pagination,
                                   APIServiceCallback<LibOverview> callback) {
        CollaRequest request = new CollaRequest(context,callback);
        request.getLibraryOverview(criteria, pagination);
    }
}
