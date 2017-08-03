package cn.xiaojs.xma.ui.grade;

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
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
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
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.material.ConflictRes;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.material.ShareDoc;
import cn.xiaojs.xma.model.material.ShareResource;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.base.BaseConstant;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.message.ChoiceContactActivity;
import cn.xiaojs.xma.ui.message.ChooseClassActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.ToastUtil;

import static android.app.Activity.RESULT_OK;
import static cn.xiaojs.xma.ui.message.ShareScopeActivity.REQUEST_CHOOSE_CLASS_CODE;

/**
 * Created by maxiaobao on 2017/7/18.
 */

public class MaterialFragment extends BaseFragment {

    private static final int REQUEST_PERMISSION = 1000;

    @BindView(R.id.material_list)
    PullToRefreshSwipeListView mList;

    @BindView(R.id.material_uploading_wrapper)
    RelativeLayout mUploadingWrapper;
    @BindView(R.id.material_up_load_name)
    TextView mUploadName;
    @BindView(R.id.material_up_load_progress)
    ProgressBar mUploadProgress;

    @BindView(R.id.share_btn)
    Button shareBtn;


    MaterialAdapter mAdapter;
    CollaManager mManager;
    private Uri mUri;

    private String[] targetDocIds;

    private int choiceMode = ListView.CHOICE_MODE_NONE;
    private boolean cancelChoiceAll = false;


    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_material, null);
    }

    @Override
    protected void init() {
        mAdapter = new MaterialAdapter(this, mList, XiaojsConfig.mLoginUser.getId());
        mList.setAdapter(mAdapter);
    }


    @OnClick({R.id.material_up_load_close, R.id.share_btn, R.id.new_folder_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.material_up_load_close:    //取消上传
                confirmCancel();
                break;
            case R.id.share_btn:                 //分享
                shareToClass();
                break;
            case R.id.new_folder_btn:            //新建文件夹
                newFolder();
                break;
        }
    }

    public int getChoiceMode() {
        return choiceMode;
    }

    public boolean cancelChoiceAll() {
        return cancelChoiceAll;
    }

    protected void changeChoiceStatus() {

        if (cancelChoiceAll) {
            mList.clearChoices();

            cancelChoiceAll = false;

            mAdapter.notifyDataSetChanged();
        } else {
            choiceAll();
            cancelChoiceAll = true;
        }

    }

    protected void changeChoiceMode(int choiceMode) {
        this.choiceMode = choiceMode;
        mList.setChoiceMode(choiceMode);
        cancelChoiceAll = false;

        if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
            shareBtn.setVisibility(View.VISIBLE);
        } else {
            shareBtn.setVisibility(View.GONE);

            mList.clearChoices();
        }

        mAdapter.notifyDataSetChanged();

    }

    private void choiceAll() {
        if (mAdapter != null && mAdapter.getList() != null && mAdapter.getList().size() > 0) {

            int size = mAdapter.getList().size();
            for (int i = 0; i < size; i++) {
                mList.setItemChecked(i, true);
            }
        }
    }

    private void shareToClass() {
        long[] ids = mList.getCheckItemIds();
        if (ids != null && ids.length > 0) {

            int len = ids.length;
            String[] docIds = new String[len];
            for (int i = 0; i < len; i++) {
                LibDoc doc = mAdapter.getItem((int) ids[i]);
                docIds[i] = doc.id;
            }
            //toshare
            chooseShare(docIds);

        } else {
            Toast.makeText(mContext, R.string.choose_material_tips, Toast.LENGTH_SHORT).show();
        }
    }

    public void confirmCancel() {
        final CommonDialog dialog = new CommonDialog(mContext);
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


    protected void upload() {

        String action = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
                Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT;

        Intent intent = new Intent(action);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, BaseConstant.REQUEST_CODE_CHOOSE_FILE);
    }

    private void newFolder() {

        final CommonDialog dialog = new CommonDialog(mContext);
        dialog.setTitle(R.string.new_folder);

        final EditTextDel editText = new EditTextDel(mContext);
        editText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                mContext.getResources().getDimensionPixelSize(R.dimen.px80)));
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setPadding(10, 0, 10, 0);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        editText.setTextColor(mContext.getResources().getColor(R.color.common_text));
        editText.setBackgroundResource(R.drawable.common_edittext_bg);
        editText.setHint(R.string.new_folder_hint);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(50)});

        dialog.setCustomView(editText);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {

                String name = editText.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(mContext, "名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.dismiss();

                //新建文件夹
                createFolder(name, "593d0342332f8a85d73ef3a3");
            }
        });

        dialog.show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseConstant.REQUEST_CODE_CHOOSE_FILE) {
            if (resultCode == RESULT_OK && data != null) {//是否选择，没选择就不会继续
                mUri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                if (mUri != null) {
                    if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(mUri.getScheme())) {
                        addToLibrary(new File(mUri.getPath()));
                    } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(mUri.getScheme())) {
                        if (PermissionUtil.isOverMarshmallow()) {
                            PermissionGen.needPermission(this, REQUEST_PERMISSION,
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

                String subtype = data.getStringExtra(ChooseClassActivity.EXTRA_SUBTYPE);

                if (choiceContacts != null && choiceContacts.size() > 0 && targetDocIds != null) {

                    Contact chooseClass = choiceContacts.get(0);

                    //无论是单个分享或者多个分享，都用批量分享的接口
                    toPatchShare(chooseClass.account, targetDocIds, chooseClass.alias, subtype, false);
//                    if (targetDocIds.length == 1) {
//                        toshare(targetDocIds[0], chooseClass.account, chooseClass.alias);
//                    }else{
//                        toPatchShare(chooseClass.account, targetDocIds,chooseClass.alias,false);
//                    }


                }
            }
        }
    }


    @Keep
    @PermissionSuccess(requestCode = REQUEST_PERMISSION)
    public void accessExternalStorageSuccess() {
        addToLibrary(queryFileFromDataBase());
    }

    @Keep
    @PermissionRationale(requestCode = REQUEST_PERMISSION)
    public void accessExternalStorageRationale() {
        PermissionHelper.showRationaleDialog(this, getResources().getString(R.string.permission_rationale_storage_tip));
    }

    private File queryFileFromDataBase() {
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = mContext.getContentResolver().query(mUri,
                filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            if (picturePath != null) {
                return new File(picturePath);
            } else {
                return new File(getPath(mContext, mUri));
            }
        }

        return null;
    }

    private void addToLibrary(File file) {
        if (file == null) {
            return;
        }
        String name = file.getName();
        int i = name.lastIndexOf(".");
        if (i > 0) {
            int type = FileUtil.getFileTypeBySuffix(name.substring(i + 1, name.length()));
            if (type != FileUtil.PPT
                    && type != FileUtil.PICTURE
                    && type != FileUtil.VIDEO
                    && type != FileUtil.DOC
                    && type != FileUtil.PDF) {
                ToastUtil.showToast(mContext, getString(R.string.upload_support_error_tips));
                return;
            }
        } else {
            ToastUtil.showToast(mContext, getString(R.string.upload_support_error_tips));
            return;
        }

        mUploadName.setText(file.getName());
        mUploadingWrapper.setVisibility(View.VISIBLE);
        mManager = new CollaManager();

        mManager.addToLibrary(mContext, file.getPath(), file.getName(), new QiniuService() {
            @Override
            public void uploadSuccess(String key, UploadReponse reponse) {
                mUploadingWrapper.setVisibility(View.GONE);

                if (mAdapter != null) {
                    mAdapter.doRequest();
                }

                ToastUtil.showToast(mContext, R.string.up_load_success);
            }

            @Override
            public void uploadProgress(String key, double percent) {
                mUploadProgress.setProgress((int) (percent * 100));
            }

            @Override
            public void uploadFailure(boolean cancel) {
                mUploadingWrapper.setVisibility(View.GONE);
                if (cancel) {
                    ToastUtil.showToast(mContext, R.string.up_load_cancel);
                } else {
                    ToastUtil.showToast(mContext, R.string.up_load_failure);
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
            cursor = mContext.getContentResolver().query(uri, projection, selection, selectionArgs,
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


    public void chooseShare(String[] docmentIds) {

        targetDocIds = docmentIds;

        Intent i = new Intent(mContext, ChooseClassActivity.class);
        startActivityForResult(i, REQUEST_CHOOSE_CLASS_CODE);
    }

    private void toPatchShare(final String targetId, String[] documentIds, final String classname, final String subType, boolean repeat) {

        ShareResource resource = new ShareResource();
        resource.documents = documentIds;
        resource.subtype = subType;
        resource.repeated = repeat;

        showProgress(true);
        CollaManager.shareDocuments(mContext, targetId, resource, new APIServiceCallback<ShareDoc>() {
            @Override
            public void onSuccess(ShareDoc object) {
                shareResult();

                if (object != null && object.repeated != null && object.repeated.length > 0) {
                    //说明分享的文件有已存在的，需要询问用户
                    shareConflictWithPatch(targetId, object.repeated, classname, subType);
                } else {
                    changeChoiceMode(ListView.CHOICE_MODE_NONE);
                    shareSuccess(targetId, classname, subType);
                }
                //Toast.makeText(MaterialActivity.this, R.string.shareok_and_to_class, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                shareResult();
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();

            }
        });
    }

//    private void toshare(String documentId, final String targetId, final String classname) {
//
//        ShareResource resource = new ShareResource();
//        resource.targetId = targetId;
//        resource.subtype = Collaboration.SubType.STANDA_LONE_LESSON;
//        //resource.sharedType = Collaboration.ShareType.COPY;
//
//        showProgress(true);
//        CollaManager.shareDocument(this, documentId, resource, new APIServiceCallback<ShareDoc>() {
//            @Override
//            public void onSuccess(ShareDoc object) {
//                shareResult();
//                //Toast.makeText(MaterialActivity.this,"分享成功",Toast.LENGTH_SHORT).show();
//                shareSuccess(targetId, classname);
//            }
//
//            @Override
//            public void onFailure(String errorCode, String errorMessage) {
//                shareResult();
//                Toast.makeText(MaterialActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void shareResult() {
        cancelProgress();
        targetDocIds = null;
    }

    private void shareConflictWithPatch(final String targetId, final ConflictRes[] conflicts, final String classname, final String subType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_dlg_list, null);
        final ListView listView = (ListView) view;


        TextView headerView = new TextView(mContext);
        headerView.setText(R.string.query_go_on_share_when_confilc);
        headerView.setTextSize(14);
        headerView.setTextColor(getResources().getColor(R.color.font_light_gray));
        headerView.setPadding(0, 0, 0, 10);


        ArrayAdapter<ConflictRes> adapter = new ArrayAdapter<ConflictRes>(mContext,
                R.layout.layout_text_item_only, conflicts);


        listView.addHeaderView(headerView);
        listView.setAdapter(adapter);

        final CommonDialog dialog = new CommonDialog(mContext);
        dialog.setTitle(R.string.patch_share_material);
        dialog.setCustomView(listView);
        dialog.setLefBtnText(R.string.cancel);
        dialog.setRightBtnText(R.string.go_on_share_material);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();

                String[] documentIds = new String[conflicts.length];
                for (int i = 0; i < conflicts.length; i++) {
                    documentIds[i] = conflicts[i].id;
                }

                toPatchShare(targetId, documentIds, classname, subType, true);

            }
        });

        dialog.show();

    }

    public void shareSuccess(final String classId, final String classname, final String subType) {
        final CommonDialog dialog = new CommonDialog(mContext);
        dialog.setTitle("提示");
        dialog.setDesc(R.string.shareok_and_to_class);
        dialog.setLefBtnText(R.string.dont_go);
        dialog.setRightBtnText(R.string.go_class);

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                databank(classId, classname, subType);
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
    private void databank(String classid, String name, String subType) {
        Intent intent = new Intent(mContext, ClassMaterialActivity.class);
        //FIXME 此处如果是助教、老师、主讲进入班级资料库，
        intent.putExtra(ClassMaterialActivity.EXTRA_ID, classid);
        intent.putExtra(ClassMaterialActivity.EXTRA_TITLE, name);
        intent.putExtra(ClassMaterialActivity.EXTRA_SUBTYPE, subType);
        startActivity(intent);
    }

    public void confirmDel(final String docId) {
        final CommonDialog dialog = new CommonDialog(mContext);
        dialog.setTitle("提示");
        dialog.setDesc("确定需要删除该资料吗？");
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
        CollaManager.deleteDocument(mContext, docId, true, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();

                if (mAdapter != null) {
                    mAdapter.doRequest();
                }

                Toast.makeText(mContext, R.string.delete_success, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createFolder(String name, String parent) {
        showProgress(true);
        CollaManager.createDirectory(mContext, name, parent, new APIServiceCallback<UploadReponse>() {
            @Override
            public void onSuccess(UploadReponse object) {
                cancelProgress();
               Toast.makeText(mContext,"创建成功",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(mContext,errorMessage,Toast.LENGTH_SHORT).show();
            }
        });
    }

}
