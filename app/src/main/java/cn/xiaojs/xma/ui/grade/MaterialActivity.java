package cn.xiaojs.xma.ui.grade;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2017/1/9
 * Desc:班级资料库/我的资料库
 *
 * ======================================================================================== */

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Keep;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.Doc;
import cn.xiaojs.xma.model.EnrolledLesson;
import cn.xiaojs.xma.model.material.ShareDoc;
import cn.xiaojs.xma.model.material.ShareResource;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseConstant;
import cn.xiaojs.xma.ui.message.ChoiceContactActivity;
import cn.xiaojs.xma.ui.message.ChooseClassActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.ToastUtil;

import static cn.xiaojs.xma.ui.message.PostDynamicActivity.EXTRA_CLASS_POS;
import static cn.xiaojs.xma.ui.message.ShareScopeActivity.REQUEST_CHOOSE_CLASS_CODE;

public class MaterialActivity extends BaseActivity {
    private static final int REQUEST_PERMISSION = 1000;

    public static final String KEY_IS_MINE = "key_is_mine";

    private boolean mIsMine;

    @BindView(R.id.material_list)
    PullToRefreshSwipeListView mList;

    @BindView(R.id.material_uploading_wrapper)
    RelativeLayout mUploadingWrapper;
    @BindView(R.id.material_up_load_name)
    TextView mUploadName;
    @BindView(R.id.material_up_load_progress)
    ProgressBar mUploadProgress;

    @BindView(R.id.material_middle_view)
    TextView mTitle;
    @BindView(R.id.material_right_image)
    ImageView mRightImage;
    @BindView(R.id.material_right_image2)
    ImageView mRightImage2;

    MaterialAdapter mAdapter;
    CollaManager mManager;
    private Uri mUri;

    private String targetDocId;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_material);
        //setRightImage(R.drawable.upload_selector);
        needHeader(false);
        Intent intent = getIntent();
        if (intent != null) {
            mIsMine = intent.getBooleanExtra(KEY_IS_MINE, false);
        }
        if (mIsMine) {
            mTitle.setText(R.string.data_bank_of_mine);
        } else {
            mTitle.setText(R.string.data_bank);
        }
        mAdapter = new MaterialAdapter(this, mList, XiaojsConfig.mLoginUser.getId());
        mList.setAdapter(mAdapter);
        mRightImage2.setImageResource(R.drawable.upload_selector);
        mRightImage.setImageResource(R.drawable.ic_my_download);
    }

    @OnClick({R.id.material_left_image, R.id.material_right_image, R.id.material_right_image2, R.id.material_up_load_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.material_left_image:
                finish();
                break;
            case R.id.material_right_image://我的下载
//                if (DownloadManager.allowDownload(this)) {
//                    DownloadManager.enqueueDownload(this, "vipkid" + System.currentTimeMillis() + ".apk", "key", "http://file.vipkid.com.cn/apps/vipkid_v1.2.1.apk", "", "");
//                } else {
//                    Toast.makeText(this, "当前有下载任务，不能新建下载", Toast.LENGTH_SHORT).show();
//                }

                Intent intent = new Intent(this, MaterialDownloadActivity.class);
                startActivity(intent);
                break;
            case R.id.material_right_image2://上传文件
                upload();
                break;
            case R.id.material_up_load_close://取消上传
                confirmCancel();
                break;
        }
    }

    public void confirmCancel() {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.setTitle("提示");
        dialog.setDesc("确定需要取消上传资料么？");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                if (mManager != null) {
                    mManager.cancelAdd();
                }
            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void upload() {

        String action = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
                Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT;

        Intent intent = new Intent(action);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, BaseConstant.REQUEST_CODE_CHOOSE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseConstant.REQUEST_CODE_CHOOSE_FILE) {
            if (resultCode == RESULT_OK && data != null) {//是否选择，没选择就不会继续
                mUri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                if (mUri != null) {
                    if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(mUri.getScheme())) {
                        addToLibrary(new File(mUri.getPath()));
                    } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(mUri.getScheme())) {
                        if (PermissionUtil.isOverMarshmallow()) {
                            PermissionGen.needPermission(MaterialActivity.this, REQUEST_PERMISSION,
                                    Manifest.permission.READ_EXTERNAL_STORAGE);
                        } else {
                            addToLibrary(queryFileFromDataBase());
                        }
                    }
                }
            }
        } else if (requestCode == REQUEST_CHOOSE_CLASS_CODE) {
            if (resultCode == RESULT_OK) {
                ArrayList<Contact> choiceContacts = (ArrayList<Contact>) data.getSerializableExtra(
                        ChoiceContactActivity.CHOOSE_CONTACT_EXTRA);

                if (choiceContacts != null && choiceContacts.size() > 0) {

                    Contact chooseClass = choiceContacts.get(0);
                    toshare(targetDocId, chooseClass.account, chooseClass.alias);
                }
            }
        }
    }

    @Keep
    @PermissionSuccess(requestCode = REQUEST_PERMISSION)
    public void accessExternalStorageSuccess() {
        addToLibrary(queryFileFromDataBase());
    }

    private File queryFileFromDataBase() {
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(mUri,
                filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            if (picturePath != null) {
                return new File(picturePath);
            } else {
                return new File(getPath(MaterialActivity.this, mUri));
            }
        }

        return null;
    }

    private void addToLibrary(File file) {
        if (file == null) {
            return;
        }

        mUploadName.setText(file.getName());
        mUploadingWrapper.setVisibility(View.VISIBLE);
        mManager = new CollaManager();

        mManager.addToLibrary(this, file.getPath(), file.getName(), new QiniuService() {
            @Override
            public void uploadSuccess(String key, UploadReponse reponse) {
                mUploadingWrapper.setVisibility(View.GONE);

                if (mAdapter != null) {
                    mAdapter.doRequest();
                }

                ToastUtil.showToast(getApplicationContext(), R.string.up_load_success);
            }

            @Override
            public void uploadProgress(String key, double percent) {
                mUploadProgress.setProgress((int) (percent * 100));
            }

            @Override
            public void uploadFailure(boolean cancel) {
                mUploadingWrapper.setVisibility(View.GONE);
                if (cancel) {
                    ToastUtil.showToast(getApplicationContext(), R.string.up_load_cancel);
                } else {
                    ToastUtil.showToast(getApplicationContext(), R.string.up_load_failure);
                }
            }

        });
    }

    @TargetApi(19)
    public String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= 19; //Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }

            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }

            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            // MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(context, uri, null, null);
        } else if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(uri.getScheme())) {
            // File
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other
     * file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection,
                                String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }


    public void chooseShare(String docmentId) {

        targetDocId = docmentId;

        Intent i = new Intent(this, ChooseClassActivity.class);
        startActivityForResult(i, REQUEST_CHOOSE_CLASS_CODE);
    }

    private void toshare(String documentId,final String targetId,final String classname) {

        ShareResource resource = new ShareResource();
        resource.targetId = targetId;
        resource.subtype = Collaboration.SubType.STANDA_LONE_LESSON;
        //resource.sharedType = Collaboration.ShareType.COPY;

        showProgress(true);
        CollaManager.shareDocument(this, documentId, resource, new APIServiceCallback<ShareDoc>() {
            @Override
            public void onSuccess(ShareDoc object) {
                cancelProgress();
                //Toast.makeText(MaterialActivity.this,"分享成功",Toast.LENGTH_SHORT).show();
                shareSuccess(targetId, classname);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(MaterialActivity.this, errorMessage,Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void shareSuccess(final String classId, final String classname) {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.setTitle("提示");
        dialog.setDesc(R.string.shareok_and_to_class);
        dialog.setLefBtnText(R.string.dont_go);
        dialog.setRightBtnText(R.string.go_class);

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                databank(classId, classname);
            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    //资料库
    private void databank(String classid, String name) {
        Intent intent = new Intent(this, ClassMaterialActivity.class);
        intent.putExtra(ClassMaterialActivity.EXTRA_LESSON_ID, classid);
        intent.putExtra(ClassMaterialActivity.EXTRA_LESSON_NAME, name);
        startActivity(intent);
    }

    public void confirmDel(final String docId) {
        final CommonDialog dialog = new CommonDialog(this);
        dialog.setTitle("提示");
        dialog.setDesc("确定需要删除该资料么？");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                deleteDoc(docId);
            }
        });
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void deleteDoc(String docId) {

        showProgress(true);
        CollaManager.deleteDocument(this, docId, true, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();

                if (mAdapter != null) {
                    mAdapter.doRequest();
                }

                Toast.makeText(MaterialActivity.this, R.string.delete_success,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(MaterialActivity.this,errorMessage,Toast.LENGTH_SHORT).show();
            }
        });
    }


//    private class BytesTask extends AsyncTask<Uri,Integer, String ad>{
//
//        @Override
//        protected byte[] doInBackground(Uri... params) {
//
//            if(){
//
//            }
//
//            return new byte[0];
//        }
//
//        @Override
//        protected void onPostExecute(byte[] bytes) {
//            super.onPostExecute(bytes);
//        }
//    }
}
