package cn.xiaojs.xma.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.api.CollaRequest;
import cn.xiaojs.xma.data.api.QiniuRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;

import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.material.CDirectory;
import cn.xiaojs.xma.model.material.EditDoc;
import cn.xiaojs.xma.model.material.LibCriteria;
import cn.xiaojs.xma.model.material.LibOverview;
import cn.xiaojs.xma.model.material.MoveParam;
import cn.xiaojs.xma.model.material.ShareDoc;
import cn.xiaojs.xma.model.material.ShareResource;
import cn.xiaojs.xma.model.material.UploadParam;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.model.material.UserDoc;
import okhttp3.ResponseBody;


/**
 * Created by maxiaobao on 2017/2/6.
 */

public class CollaManager {

    private QiniuRequest qiniuRequest;

    /**
     * Provides access to collaboration interfaces accessible to the Xiaojs client applications.
     */

    public void addToLibraryWithParent(Context context,
                             String parent,
                             @NonNull final String filePath,
                             @NonNull final String fileName,
                             @NonNull QiniuService qiniuService) {
        UploadParam param = new UploadParam();
        param.fileName = fileName;
        param.parent = parent;

        addToLibrary(context, filePath, param, qiniuService);
    }

    public void addToLibrary(Context context,
                             @NonNull final String filePath,
                             @NonNull final String fileName,
                             @NonNull QiniuService qiniuService) {
        UploadParam param = new UploadParam();
        param.fileName = fileName;

        addToLibrary(context, filePath, param, qiniuService);
    }

    public void addToLibrary(Context context,
                             @NonNull final String filePath,
                             @NonNull final String fileName,
                             boolean toOrLib,
                             @NonNull QiniuService qiniuService) {
        UploadParam param = new UploadParam();
        param.fileName = fileName;
        param.toOrgLib = toOrLib;
        addToLibrary(context, filePath, param, qiniuService);
    }

    public void addToLibrary(Context context,
                             @NonNull final String filePath,
                             @NonNull final String fileName,
                             String ticket,
                             @NonNull QiniuService qiniuService) {
        UploadParam param = new UploadParam();
        param.fileName = fileName;
        param.ticket = ticket;
        addToLibrary(context, filePath, param, qiniuService);
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
        addToLibrary(context, filePath, param, qiniuService);
    }

    private void addToLibrary(Context context,
                              @NonNull final String filePath,
                              @NonNull UploadParam param,
                              @NonNull QiniuService qiniuService) {

        qiniuRequest = new QiniuRequest(context, filePath, param, qiniuService);
        qiniuRequest.getToken(Collaboration.UploadTokenType.DOCUMENT_IN_LIBRARY, 1);
    }

    public void addToLibrary(Context context,
                             @NonNull byte[] bytes,
                             @NonNull final String fileName,
                             String ticket,
                             @NonNull QiniuService qiniuService) {
        UploadParam param = new UploadParam();
        param.fileName = fileName;
        param.ticket = ticket;
        addToLibrary(context, bytes, param, qiniuService);
    }

    private void addToLibrary(Context context,
                              @NonNull byte[] data,
                              @NonNull UploadParam param,
                              @NonNull QiniuService qiniuService) {

        qiniuRequest = new QiniuRequest(context, data, param, qiniuService);
        qiniuRequest.getToken(Collaboration.UploadTokenType.DOCUMENT_IN_LIBRARY, 1);
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
     */
    public static void getLibraryOverview(Context context,
                                          LibCriteria criteria,
                                          Pagination pagination,
                                          APIServiceCallback<LibOverview> callback) {
        CollaRequest request = new CollaRequest(context, callback);
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
                                    String category,
                                    Pagination pagination,
                                    APIServiceCallback<UserDoc> callback) {


        int page = 1;
        int limit = 10;
        if (pagination != null) {
            page = pagination.getPage();
            limit = pagination.getMaxNumOfObjectsPerPage();
        }

        CollaRequest request = new CollaRequest(context, callback);
        request.getDocuments(id, subtype,category,pagination.getPage(),
                pagination.getMaxNumOfObjectsPerPage());
    }

    public static void getDocuments(Context context,
                                    String id,
                                    String parent,
                                    String subtype,
                                    String name,
                                    String category,
                                    String sort,
                                    int reverse,
                                    Pagination pagination,
                                    APIServiceCallback<UserDoc> callback) {


        int page = 1;
        int limit = 10;
        if (pagination != null) {
            page = pagination.getPage();
            limit = pagination.getMaxNumOfObjectsPerPage();
        }

        CollaRequest request = new CollaRequest(context, callback);

        if (TextUtils.isEmpty(parent)) {
            request.getDocuments(id, subtype,category,sort, String.valueOf(reverse), pagination.getPage(),
                    pagination.getMaxNumOfObjectsPerPage());
        }else {
            if (TextUtils.isEmpty(name)) {
                request.getDocuments(id, parent, subtype,category,sort, String.valueOf(reverse),pagination.getPage(),
                        pagination.getMaxNumOfObjectsPerPage());
            }else {
                request.getDocuments(id, parent, subtype,name, category,sort, String.valueOf(reverse),pagination.getPage(),
                        pagination.getMaxNumOfObjectsPerPage());
            }

        }


    }

    /**
     *
     * @param context
     * @param id
     * @param subtype
     * @param pagination
     * @param limit
     * @param callback
     */
    @Deprecated
    public static void getDocuments(Context context,
                                    String id,
                                    String subtype,
                                    Pagination pagination,
                                    int limit,
                                    APIServiceCallback<UserDoc> callback) {


        if (limit <= 0) {
            limit = 10;
        }

        int page = 1;
        if (pagination != null) {
            page = pagination.getPage();
        }

        CollaRequest request = new CollaRequest(context, callback);
        request.getDocuments(id, subtype,Collaboration.TypeName.ALL,pagination.getPage(),pagination.getMaxNumOfObjectsPerPage());
    }

    /**
     * Convert document (recorded m3u8 for now) to mp4.
     * @param context
     * @param id
     * @param callback
     */
    public static void convertDocument(Context context,
                                    String id,
                                    APIServiceCallback<ResponseBody> callback) {
        CollaRequest request = new CollaRequest(context, callback);
        request.convertDocument(id);
    }

    /**
     * 删除文档
     *
     * @param shared shared 表示是否删除此文档分享出去的文档, (根据产品设计, 此处应该为 true ) 仅在分享的文档为 Shortcut 或 Send 方式时有效.
     */
    public static void deleteDocument(Context context,
                                      String documentId,
                                      boolean shared,
                                      APIServiceCallback callback) {

        CollaRequest request = new CollaRequest(context, callback);
        request.deleteDocument(documentId, shared);

    }

    /**
     * 分享文档
     * @param context
     * @param document
     * @param resource
     * @param callback
     */
    public static void shareDocument(Context context,
                                     String document,
                                     ShareResource resource,
                                     APIServiceCallback<ShareDoc> callback) {

        CollaRequest request = new CollaRequest(context,callback);
        request.shareDocument(document, resource);
    }


    /**
     * 批量分享文档
     * @param context
     * @param targetId
     * @param resource
     * @param callback
     */
    public static void shareDocuments(Context context,
                                     String targetId,
                                     ShareResource resource,
                                     APIServiceCallback<ShareDoc> callback) {

        CollaRequest request = new CollaRequest(context,callback);
        request.shareDocuments(targetId, resource);
    }


    /**
     * Create directory for a library or directory.
     * @param context
     * @param name
     * @param parent
     * @param callback
     */
    public static void createDirectory(Context context,
                                       String name,
                                       String parent,
                                       APIServiceCallback<UploadReponse> callback) {

        CDirectory directory = new CDirectory();
        directory.name = name;

        CollaRequest request = new CollaRequest(context,callback);
        request.createDirectory(parent, directory);
    }

    public static void editDocument(Context context, String docId,
                                    String newName, APIServiceCallback<ResponseBody> callback) {

        EditDoc editDoc = new EditDoc();
        editDoc.name = newName;

        CollaRequest request = new CollaRequest(context,callback);
        request.editDocument(docId, editDoc);
    }


    public static void moveDocuments(Context context, String targetId,
                                     String[] docs, APIServiceCallback<ResponseBody> callback) {

        MoveParam param = new MoveParam();
        param.documents = docs;

        CollaRequest request = new CollaRequest(context,callback);
        request.moveDocuments(targetId, param);
    }

}
