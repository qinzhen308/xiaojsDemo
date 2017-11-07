package cn.xiaojs.xma.ui.classroom2.material;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.ui.base.BaseConstant;
import cn.xiaojs.xma.ui.classroom.main.ClassroomController;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.grade.MaterialActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.ToastUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class AddNewFragment extends BottomSheetFragment implements DialogInterface.OnKeyListener{

    private static final int REQUEST_PERMISSION = 1000;

    @BindView(R.id.cl_root)
    ConstraintLayout rootLay;

    @BindView(R.id.back_btn)
    ImageView backView;
    @BindView(R.id.upload_btn)
    TextView uploadBtnView;
    @BindView(R.id.folder_btn)
    TextView folderBtnView;

    @BindView(R.id.upload_lay)
    LinearLayout uploadLayout;
    @BindView(R.id.upload_name)
    TextView uploadNameView;
    @BindView(R.id.uploading)
    TextView uploadingView;
    @BindView(R.id.upload_progress)
    ProgressBar progressBarView;


    private CollaManager collaManager;
    private Uri chooseUri;
    private String directoryId;

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classroom2_material_addnew, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getDialog()==null) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rootLay.getLayoutParams();
            params.setMargins(0,0,0,0);
            rootLay.setBackgroundColor(getResources().getColor(R.color.white));
        }else {
            getDialog().setOnKeyListener(this);
        }


        directoryId = getArguments().getString(CTLConstant.EXTRA_DIRECTORY_ID);

    }

    @OnClick({R.id.back_btn, R.id.folder_btn, R.id.upload_btn, R.id.upload_close})
    void onViewClick(View view) {
        switch(view.getId()) {
            case R.id.back_btn:
                dismiss();
                break;
            case R.id.folder_btn:
                newFolder();
                break;
            case R.id.upload_btn:
                chooseFile();
                break;
            case R.id.upload_close:
                confirmCancel();
                break;
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
            dismiss();
            return true;
        }

        return false;
    }

    @Keep
    @PermissionSuccess(requestCode = REQUEST_PERMISSION)
    public void accessExternalStorageSuccess() {
        addToLibrary(queryFileFromDataBase(chooseUri));
    }

    @Keep
    @PermissionRationale(requestCode = REQUEST_PERMISSION)
    public void accessExternalStorageRationale() {
        PermissionHelper.showRationaleDialog(this,
                getResources().getString(R.string.permission_rationale_storage_tip));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseConstant.REQUEST_CODE_CHOOSE_FILE) {
            if (resultCode == RESULT_OK && data != null) {//是否选择，没选择就不会继续
                chooseUri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                if (chooseUri != null) {
                    if (ContentResolver.SCHEME_FILE.equalsIgnoreCase(chooseUri.getScheme())) {
                        addToLibrary(new File(chooseUri.getPath()));
                    } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(chooseUri.getScheme())) {
                        if (PermissionUtil.isOverMarshmallow()
                                && ContextCompat.checkSelfPermission(getContext(),
                                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            PermissionGen.needPermission(this, REQUEST_PERMISSION,
                                    Manifest.permission.READ_EXTERNAL_STORAGE);
                        } else {
                            addToLibrary(queryFileFromDataBase(chooseUri));
                        }
                    }
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 新建文件夹
    //

    private void newFolder() {

        final CommonDialog dialog = new CommonDialog(getContext());
        dialog.setTitle(R.string.new_folder);

        final EditTextDel editText = new EditTextDel(getContext());
        editText.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.px80)));
        editText.setGravity(Gravity.CENTER_VERTICAL);
        editText.setPadding(10, 0, 10, 0);
        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        editText.setTextColor(getResources().getColor(R.color.common_text));
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
                    Toast.makeText(getContext(), "名称不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.dismiss();

                //新建文件夹
                createFolder(name);
            }
        });

        dialog.show();

    }

    private void createFolder(String name) {
        CollaManager.createDirectory(getContext(),
                name, directoryId, new APIServiceCallback<UploadReponse>() {
            @Override
            public void onSuccess(UploadReponse object) {
                ToastUtil.showToast(getContext(), "创建成功");

                Fragment target = getTargetFragment();
                if (target != null) {
                    Intent intent = new Intent();
                    target.onActivityResult(CTLConstant.REQUEST_MATERIAL_ADD_NEW,
                            Activity.RESULT_OK, intent);
                }
                dismiss();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(getContext(), errorMessage);
            }
        });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 上传文件
    //

    private void chooseFile() {
        String action = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
                Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT;

        Intent intent = new Intent(action);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, BaseConstant.REQUEST_CODE_CHOOSE_FILE);
    }

    private void showUploadSatus(boolean show) {
        if (show) {
            uploadLayout.setVisibility(View.VISIBLE);

            backView.setVisibility(View.GONE);
            uploadBtnView.setVisibility(View.GONE);
            folderBtnView.setVisibility(View.GONE);
        }else {
            uploadLayout.setVisibility(View.GONE);

            backView.setVisibility(View.VISIBLE);
            uploadBtnView.setVisibility(View.VISIBLE);
            folderBtnView.setVisibility(View.VISIBLE);
        }
    }



    private File queryFileFromDataBase(Uri uri) {
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContext().getContentResolver().query(uri,
                filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(MediaStore.MediaColumns.DATA);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            if (picturePath != null) {
                return new File(picturePath);
            } else {
                return new File(getPath(uri));
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
                ToastUtil.showToast(getContext(), getString(R.string.upload_support_error_tips));
                return;
            }
        } else {
            ToastUtil.showToast(getContext(), getString(R.string.upload_support_error_tips));
            return;
        }

        uploadNameView.setText(file.getName());
        showUploadSatus(true);
        collaManager = new CollaManager();

        collaManager.addToLibraryWithParent(getContext(),
                directoryId, file.getPath(), file.getName(), new QiniuService() {
            @Override
            public void uploadSuccess(String key, UploadReponse reponse) {
                showUploadSatus(false);
                chooseUri = null;

                ToastUtil.showToast(getContext(), R.string.up_load_success);

                Fragment target = getTargetFragment();
                if (target != null) {
                    Intent intent = new Intent();
                    target.onActivityResult(CTLConstant.REQUEST_MATERIAL_ADD_NEW,
                            Activity.RESULT_OK, intent);
                }
                dismiss();

            }

            @Override
            public void uploadProgress(String key, double percent) {

                int progress = (int) (percent * 100);

                uploadingView.setText(getString(R.string.up_loading_with_per, progress));
                progressBarView.setProgress(progress);
            }

            @Override
            public void uploadFailure(boolean cancel) {
                showUploadSatus(false);
                if (cancel) {
                    ToastUtil.showToast(getContext(), R.string.up_load_cancel);
                } else {
                    ToastUtil.showToast(getContext(), R.string.up_load_failure);
                }

                chooseUri = null;
            }

        });
    }


    public void confirmCancel() {
        final CommonDialog dialog = new CommonDialog(getContext());
        dialog.setTitle("提示");
        dialog.setDesc("确定需要取消上传资料么？");
        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                if (collaManager != null) {
                    collaManager.cancelAdd();
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


    @TargetApi(19)
    public String getPath(final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= 19; //Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(getContext(), uri)) {
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
                return getDataColumn(getContext(), contentUri, null, null);
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

                return getDataColumn(getContext(), contentUri, selection, selectionArgs);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            // MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(getContext(), uri, null, null);
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
            cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs,
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


}
