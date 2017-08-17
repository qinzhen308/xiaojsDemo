package cn.xiaojs.xma.data.api;

import android.content.Context;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.APIType;
import cn.xiaojs.xma.data.api.service.ServiceRequest;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.material.CDirectory;
import cn.xiaojs.xma.model.material.LibCriteria;
import cn.xiaojs.xma.model.material.LibOverview;
import cn.xiaojs.xma.model.material.ShareDoc;
import cn.xiaojs.xma.model.material.ShareResource;
import cn.xiaojs.xma.model.material.TokenPair;
import cn.xiaojs.xma.model.material.UploadParam;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.model.material.UserDoc;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;


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

    public void getDocuments(String id, String subtype,int page,int perPage) {
        Call<UserDoc> call = getService().getDocuments(id,subtype,"createdOn","1",page,perPage);
        enqueueRequest(APIType.GET_DOCUMENTS, call);
    }


    public void convertDocument(String id) {
        Call<ResponseBody> call = getService().convertDocument(id);
        enqueueRequest(APIType.CONVERT_DOCUMENT, call);
    }

    public void deleteDocument(String documentId, boolean shared) {
        Call<ResponseBody> call = getService().deleteDocument(documentId, shared);
        enqueueRequest(APIType.DELETE_DOCUMENT, call);
    }

    public void shareDocument(String document, ShareResource resource) {
        Call<ShareDoc> call = getService().shareDocument(document, resource);
        enqueueRequest(APIType.SHARE_DOCUMENT, call);
    }

    public void shareDocuments(String targetId, ShareResource resource) {
        Call<ShareDoc> call = getService().shareDocuments(targetId, resource);
        enqueueRequest(APIType.SHARE_DOCUMENTS, call);
    }

    public void createDirectory(CDirectory directory) {
        Call<UploadReponse> call = getService().createDirectory(directory);
        enqueueRequest(APIType.CREATE_DIRECTORY, call);
    }
}
