package cn.xiaojs.xma.data.api;

import android.content.Context;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.Colla.LibCriteria;
import cn.xiaojs.xma.model.Colla.LibOverview;
import cn.xiaojs.xma.model.Colla.TokenPair;
import cn.xiaojs.xma.model.Colla.UploadParam;
import cn.xiaojs.xma.model.Colla.UploadReponse;
import cn.xiaojs.xma.model.Pagination;
import retrofit2.Call;

/**
 * Created by maxiaobao on 2017/2/4.
 */

public class CollaRequest extends ServiceRequest{

    public CollaRequest(Context context, APIServiceCallback callback) {
        super(context, callback);
    }

    public void getUploadTokens(int type, int quantity) {
        Call<TokenPair[]> call = getService().getUploadTokens(type, quantity);
        enqueueRequest(APIType.GET_UPLOAD_TOKENS,call);
    }

    public void addToLibrary(UploadParam param) {
        Call<UploadReponse> call = getService().addToLibrary(param);
        enqueueRequest(APIType.ADD_TO_LIBRARY, call);
    }

    public void getLibraryOverview(LibCriteria criteria, Pagination pagination) {

        String criteriaJsonstr = objectToJsonString(criteria);
        String paginationJsonstr = objectToJsonString(pagination);

        if (XiaojsConfig.DEBUG) {
            Logger.json(criteriaJsonstr);
            Logger.json(paginationJsonstr);
        }

        Call<LibOverview> call = getService().getLibraryOverview(criteriaJsonstr,
                paginationJsonstr);
        enqueueRequest(APIType.GET_LIBRARY_OVERVIEW, call);
    }
}
