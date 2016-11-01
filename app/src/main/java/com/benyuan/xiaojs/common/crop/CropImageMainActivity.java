package com.benyuan.xiaojs.common.crop;

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
import android.provider.MediaStore.MediaColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.widget.BottomSheet;
import com.benyuan.xiaojs.util.FileUtil;

import java.io.File;
import java.net.URI;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */
public class CropImageMainActivity extends BaseActivity {
    private static final String LOG_TAG = "CropImageMainActivity";
    public static final String NEED_DELETE = "need_delete";// 需要删除按钮
    public static final String NEED_LOOK = "need_look";// 需要预览
    public static final String NEED_COVER = "need_cover"; //需要设置封面
    private int mWidth = 300;
    private int mHeight = 300;
    private boolean mCanceled = false;
    private boolean isNotCrop = false;
    private boolean isNeedDelete = false;
    private boolean isNeedLook = false;
    private boolean isNeedCover = false;
    private boolean isUploadCompress = false;
    public static final int RESULT_DELETE = RESULT_FIRST_USER + 1;
    public static final int RESULT_LOOK = RESULT_FIRST_USER + 2;
    public static final int RESULT_COVER = RESULT_FIRST_USER + 3;

    @Override
    public void addViewContent() {
        if (getIntent() != null) {
            mWidth = getIntent().getIntExtra(CropImagePath.CROP_IMAGE_WIDTH,
                    300);
            mHeight = getIntent().getIntExtra(CropImagePath.CROP_IMAGE_HEIGHT,
                    300);
            isNotCrop = getIntent().getBooleanExtra(CropImagePath.CROP_NEVER,
                    false);
            isNeedDelete = getIntent().getBooleanExtra(NEED_DELETE, false);
            isNeedLook = getIntent().getBooleanExtra(NEED_LOOK, false);
            isUploadCompress = getIntent().getBooleanExtra(CropImagePath.UPLOAD_COMPRESS, false);
            isNeedCover = getIntent().getBooleanExtra(NEED_COVER, false);
        }

        needHeader(false);
        //setBaseBg(ResourcesUtil.getColor(R.color.transparent));

        // 默认有相机拍摄、图片选择
        if (isNeedCover && isNeedDelete && isNeedLook) { // 有设置封面、删除、预览
            showCropImageDialogPDC();
        } else if (isNeedDelete && isNeedLook) {// 有预览、删除
            showCropImageDialogPD();
        } else if (isNeedLook) {// 有预览
            showCropImageDialogP();
        } else {// 无预览无删除（默认）
            showCropImageDialog();
        }
    }

    private void showCropImageDialogP() {
        final BottomSheet bottomSheet = new BottomSheet(this);
        bottomSheet.setTitleVisibility(View.GONE);
        ListView lv = new ListView(this);
        String[] actions = getResources().getStringArray(R.array.selects_pic_p);
        ActionAdapter adapter = new ActionAdapter(actions);
        lv.setAdapter(adapter);
        bottomSheet.setContent(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setResult(RESULT_LOOK);
                        finish();
                        break;
                    case 1:
                        mCanceled = true;
                        pickerPhoto();
                        break;
                    case 2:
                        mCanceled = true;
                        takePhoto();
                        break;
                    case 3:
                        mCanceled = true;
                        setFailure();
                        break;
                }
                bottomSheet.dismiss();
            }
        });

        bottomSheet.show();
    }

    private void showCropImageDialogPD() {
        final BottomSheet bottomSheet = new BottomSheet(this);
        bottomSheet.setTitleVisibility(View.GONE);
        ListView lv = new ListView(this);
        String[] actions = getResources().getStringArray(R.array.selects_pic_pd);
        ActionAdapter adapter = new ActionAdapter(actions);
        lv.setAdapter(adapter);
        bottomSheet.setContent(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setResult(RESULT_LOOK);
                        finish();
                        break;
                    case 1:
                        mCanceled = true;
                        pickerPhoto();
                        break;
                    case 2:
                        mCanceled = true;
                        takePhoto();
                        break;
                    case 3:
                        setResult(RESULT_DELETE);
                        finish();
                        break;
                    case 4:
                        mCanceled = true;
                        setFailure();
                        break;
                }
                bottomSheet.dismiss();
            }
        });

        bottomSheet.show();
    }

    private void showCropImageDialogPDC() {
        final BottomSheet bottomSheet = new BottomSheet(this);
        bottomSheet.setTitleVisibility(View.GONE);
        ListView lv = new ListView(this);
        String[] actions = getResources().getStringArray(R.array.selects_pic_pdc);
        ActionAdapter adapter = new ActionAdapter(actions);
        lv.setAdapter(adapter);
        bottomSheet.setContent(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        setResult(RESULT_COVER);
                        finish();
                        break;
                    case 1:
                        setResult(RESULT_LOOK);
                        finish();
                        break;
                    case 2:
                        mCanceled = true;
                        pickerPhoto();
                        break;
                    case 3:
                        mCanceled = true;
                        takePhoto();
                        break;
                    case 4:
                        setResult(RESULT_DELETE);
                        finish();
                        break;
                    case 5:
                        mCanceled = true;
                        setFailure();
                        break;
                }
                bottomSheet.dismiss();
            }
        });

        bottomSheet.show();
    }

    private void showCropImageDialog() {
        final BottomSheet bottomSheet = new BottomSheet(this);
        bottomSheet.setTitleVisibility(View.GONE);
        ListView lv = new ListView(this);
        String[] actions = getResources().getStringArray(R.array.selects_pic);
        ActionAdapter adapter = new ActionAdapter(actions);
        lv.setAdapter(adapter);
        bottomSheet.setContent(lv);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mCanceled = true;
                        pickerPhoto();
                        break;
                    case 1:
                        mCanceled = true;
                        takePhoto();
                        break;
                    case 2:
                        mCanceled = true;
                        setFailure();
                        break;
                }
                bottomSheet.dismiss();
            }
        });

        bottomSheet.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CropImagePath.CHOOSE_IMAGE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri imageUri = data.getData();
                    String[] filePathColumn = {MediaColumns.DATA};
                    Cursor cursor = getContentResolver().query(imageUri,
                            filePathColumn, null, null, null);
                    if (cursor != null) {
                        cursor.moveToFirst();
                        int columnIndex = cursor.getColumnIndex(MediaColumns.DATA);
                        String picturePath = cursor.getString(columnIndex);
                        if (picturePath == null) {
                            picturePath = getPath(this, imageUri);
                        }

                        File file = new File(picturePath);
                        if (file.exists()) {
                            FileUtil.copyFiles(picturePath,
                                    CropImagePath.UPLOAD_IMAGE_PATH, true);
                            Intent intent = new Intent();
                            intent.setClass(this, CropImageActivity.class);
                            intent.putExtra(CropImagePath.CROP_IMAGE_WIDTH, mWidth);
                            intent.putExtra(CropImagePath.CROP_IMAGE_HEIGHT,
                                    mHeight);
                            intent.putExtra(CropImagePath.CROP_NEVER, isNotCrop);
                            intent.putExtra(CropImagePath.UPLOAD_COMPRESS, isUploadCompress);
                            startActivityForResult(intent,
                                    CropImagePath.CROP_IMAGE_REQUEST_CODE);
                            cursor.close();
                        } else {
                            cursor.close();
                            //ToastUtil.showCustomViewToast(this, R.string.not_find_image);
                            finish();
                        }
                    } else {
                        URI uri = URI.create(data.getData().toString());
                        File fileP = new File(uri);
                        if (fileP != null && fileP.exists()) {
                            FileUtil.copyFiles(fileP.getAbsolutePath(),
                                    CropImagePath.UPLOAD_IMAGE_PATH, true);
                            Intent intent = new Intent();
                            intent.setClass(this, CropImageActivity.class);
                            intent.putExtra(CropImagePath.CROP_IMAGE_WIDTH, mWidth);
                            intent.putExtra(CropImagePath.CROP_IMAGE_HEIGHT,
                                    mHeight);
                            intent.putExtra(CropImagePath.CROP_NEVER, isNotCrop);
                            intent.putExtra(CropImagePath.UPLOAD_COMPRESS, isUploadCompress);
                            startActivityForResult(intent,
                                    CropImagePath.CROP_IMAGE_REQUEST_CODE);
                        } else {
                            //ToastUtil.showCustomViewToast(this, R.string.not_find_image);
                            finish();
                        }
                    }
                } else {
                    // 未选择图片
                    setFailure();
                }
                break;
            case CropImagePath.TAKE_PHOTO: // 拍照
                if (resultCode == RESULT_OK) {
                    File file = new File(CropImagePath.UPLOAD_IMAGE_PATH);
                    if (file.exists()) {
                        Intent intent = new Intent();
                        intent.setClass(this, CropImageActivity.class);
                        intent.putExtra(CropImagePath.CROP_IMAGE_WIDTH, mWidth);
                        intent.putExtra(CropImagePath.CROP_IMAGE_HEIGHT, mHeight);
                        intent.putExtra(CropImagePath.CROP_NEVER, isNotCrop);
                        intent.putExtra(CropImagePath.UPLOAD_COMPRESS, isUploadCompress);
                        startActivityForResult(intent,
                                CropImagePath.CROP_IMAGE_REQUEST_CODE);
                    }
                } else {
                    // 未拍照
                    setFailure();
                }
                break;
            case CropImagePath.CROP_IMAGE_REQUEST_CODE: // 裁剪结束
                if (resultCode == RESULT_OK && data != null) {
                    // 已裁剪成功
                    setResult(RESULT_OK, data);
                    this.finish();
                } else {
                    // 未裁剪
                    setFailure();
                }
                break;
        }
    }

    public void setFailure() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void pickerPhoto() {
        Intent intent = new Intent(
                Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CropImagePath.CHOOSE_IMAGE);
        } else {
            intent = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, CropImagePath.CHOOSE_IMAGE);
            } else {
                //如手机没有找到默认打开图片的应用(比如用户没有安装图库)，让用户自己选择打开图片的应用程序
                intent = new Intent(
                        Intent.ACTION_GET_CONTENT,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("*/*");
                startActivityForResult(intent, CropImagePath.CHOOSE_IMAGE);
            }
        }
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
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
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

    /**
     * 拍照
     */
    private void takePhoto() {
        try {
            Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent1.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(new File(
                            CropImagePath.UPLOAD_IMAGE_PATH)));
            startActivityForResult(intent1, CropImagePath.TAKE_PHOTO);
        } catch (Exception e) {

        }
    }

    private class ActionAdapter extends BaseAdapter {
        private String[] mActions;

        public ActionAdapter(String[] actions)  {
            mActions = actions;
        }

        @Override
        public int getCount() {
            return mActions == null ? 0 : mActions.length;
        }

        @Override
        public Object getItem(int position) {
            return mActions == null ? null : mActions[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(CropImageMainActivity.this).
                        inflate(R.layout.crop_img_action_item, null);
            }
            ((TextView)convertView).setText(mActions[position]);
            return convertView;
        }
    }
}
