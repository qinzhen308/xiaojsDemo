package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;

import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.api.CollaRequest;
import cn.xiaojs.xma.data.api.QiniuRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;

import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.material.LibCriteria;
import cn.xiaojs.xma.model.material.LibOverview;
import cn.xiaojs.xma.model.material.UploadParam;
import cn.xiaojs.xma.model.material.UserDoc;


/**
 * Created by maxiaobao on 2017/2/6.
 */

public class CollaManager {

    private QiniuRequest qiniuRequest;

    /**
     * Provides access to collaboration interfaces accessible to the Xiaojs client applications.
     * @param context
     */
    public void addToLibrary(Context context,
                             @NonNull final String filePath,
                             @NonNull final String fileName,
                             @NonNull QiniuService qiniuService) {
        UploadParam param = new UploadParam();
        param.fileName = fileName;

        addToLibrary(context,filePath,param,qiniuService);
    }

    public void addToLibrary(Context context,
                             @NonNull final String filePath,
                             @NonNull final String fileName,
                             boolean toOrLib,
                             @NonNull QiniuService qiniuService) {
        UploadParam param = new UploadParam();
        param.fileName = fileName;
        param.toOrgLib = toOrLib;
        addToLibrary(context,filePath,param,qiniuService);
    }

    public void addToLibrary(Context context,
                             @NonNull final String filePath,
                             @NonNull final String fileName,
                             String ticket,
                             @NonNull QiniuService qiniuService) {
        UploadParam param = new UploadParam();
        param.fileName = fileName;
        param.ticket = ticket;
        addToLibrary(context,filePath,param,qiniuService);
    }

    public void addToLibrary(Context context,
                             @NonNull final String filePath,
                             @NonNull final String fileName,
                             boolean toOrLib,
                             String ticket,
                             @NonNull QiniuService qiniuService) {
        UploadParam param = new UploadParam();
        param.fileName = fileName;
        param.toOrgLib = toOrLib;
        param.ticket = ticket;
        addToLibrary(context,filePath,param,qiniuService);
    }

    private void addToLibrary(Context context,
                              @NonNull final String filePath,
                              @NonNull UploadParam param,
                              @NonNull QiniuService qiniuService) {

        qiniuRequest = new QiniuRequest(context,filePath,param,qiniuService);
        qiniuRequest.getToken(Collaboration.UploadTokenType.DOCUMENT_IN_LIBRARY,1);
    }

    public void addToLibrary(Context context,
                             @NonNull byte[] bytes,
                             @NonNull final String fileName,
                             String ticket,
                             @NonNull QiniuService qiniuService) {
        UploadParam param = new UploadParam();
        param.fileName = fileName;
        param.ticket = ticket;
        addToLibrary(context,bytes,param,qiniuService);
    }

    private void addToLibrary(Context context,
                              @NonNull byte[] data,
                              @NonNull UploadParam param,
                              @NonNull QiniuService qiniuService) {

        qiniuRequest = new QiniuRequest(context,data,param,qiniuService);
        qiniuRequest.getToken(Collaboration.UploadTokenType.DOCUMENT_IN_LIBRARY,1);
    }

    /**
     * 取消上传
     */
    public void cancelAdd() {
        if (qiniuRequest != null) {
            qiniuRequest.cancelUpload();
        }
    }


    /**
     * Returns overview for all non-empty document categories in specific library.
     * @param context
     * @param criteria
     * @param pagination
     * @param callback
     */
    public static void getLibraryOverview(Context context,
                                   LibCriteria criteria,
                                   Pagination pagination,
                                   APIServiceCallback<LibOverview> callback) {
        CollaRequest request = new CollaRequest(context,callback);
        request.getLibraryOverview(criteria, pagination);
    }


    /**
     *
     * @param context
     * @param id
     * @param subtype
     * @param pagination
     * @param callback
     */
    public static void getDocuments(Context context,
                                    String id,
                                    String subtype,
                                    Pagination pagination,
                                    APIServiceCallback<UserDoc> callback) {


        int page = 1;
        int limit = 10;
        if (pagination!=null){
            page = pagination.getPage();
            limit = pagination.getMaxNumOfObjectsPerPage();
        }

        CollaRequest request = new CollaRequest(context,callback);
        request.getDocuments(id,subtype,page,limit);
    }

    /**
     *
     * 删除文档
     * @param context
     * @param documentId
     * @param shared shared 表示是否删除此文档分享出去的文档,
     *               (根据产品设计, 此处应该为 true ) 仅在分享的文档为 Shortcut 或 Send 方式时有效.
     * @param callback
     */
    public static void deleteDocument(Context context,
                                      String documentId,
                                      boolean shared ,
                                      APIServiceCallback callback) {

        CollaRequest request = new CollaRequest(context,callback);
        request.deleteDocument(documentId, shared);

    }


}
