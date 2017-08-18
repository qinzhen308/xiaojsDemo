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
 * Desc:我的资料库
 *
 * ======================================================================================== */

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.data.download.DownloadProvider;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.ui.base.BaseConstant;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.NoScrollViewPager;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.ToastUtil;


public class MaterialActivity extends FragmentActivity {

    private static final int REQUEST_PERMISSION = 1000;
    public static final String EXTRA_TAB = "extra_tab";

    public static final String EXTRA_SHOW_DOWNLOAD = "show_download";


    @BindView(R.id.title_view)
    TextView titleView;

    @BindView(R.id.lay_tab_group)
    RadioGroup tabGroupLayout;
    @BindView(R.id.tab_viewpager)
    NoScrollViewPager tabPager;
    @BindView(R.id.left_image)
    ImageView backBtn;
    @BindView(R.id.upload_btn)
    ImageView uploadBtn;
    @BindView(R.id.mode_btn)
    ImageView modeBtn;
    @BindView(R.id.cancel_btn)
    Button cancelBtn;
    @BindView(R.id.choice_btn)
    Button choiceBtn;

    @BindView(R.id.material_uploading_wrapper)
    RelativeLayout mUploadingWrapper;
    @BindView(R.id.material_up_load_name)
    TextView mUploadName;
    @BindView(R.id.uploading)
    TextView mUploadPro;
    @BindView(R.id.material_up_load_progress)
    ProgressBar mUploadProgress;

    private int curCheckedTabId;


    private ArrayList<Fragment> fragmentList;
    private MaterialFragment materialFragment;

    private CollaManager mManager;

    private Uri chooseUri;

    private boolean showDownload;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_tab);
        ButterKnife.bind(this);

        showDownload = getIntent().getBooleanExtra(EXTRA_SHOW_DOWNLOAD,true);

        initView();

        if (showDownload) {
            int tab = getIntent().getIntExtra(EXTRA_TAB, -1);
            if (tab > 0) {
                initToTab(tab);
            }
        }
    }

    @OnClick({R.id.left_image, R.id.cancel_btn,
            R.id.upload_btn, R.id.mode_btn, R.id.choice_btn, R.id.tab_material,
            R.id.tab_download,R.id.material_up_load_close, R.id.material_uploading_wrapper})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:             //返回
                finish();
                break;
            case R.id.upload_btn:             //上传文件
                chooseFile();
                break;
            case R.id.mode_btn:               //进入选择模式
                changeChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                break;
            case R.id.cancel_btn:             //取消
                changeChoiceMode(ListView.CHOICE_MODE_NONE);
                break;
            case R.id.choice_btn:             //全选OR取消全选
                changeChoiceStatus();
                break;
            case R.id.tab_material:
                if (curCheckedTabId != R.id.tab_material) {
                    tabPager.setCurrentItem(0);
                    curCheckedTabId = R.id.tab_material;
                }
                break;
            case R.id.tab_download:
                if (curCheckedTabId != R.id.tab_download) {
                    tabPager.setCurrentItem(1);
                    curCheckedTabId = R.id.tab_download;
                }
                break;
            case R.id.material_up_load_close:    //取消上传
                confirmCancel();
                break;
            case R.id.material_uploading_wrapper:
                break;
        }
    }

    @Keep
    @PermissionSuccess(requestCode = REQUEST_PERMISSION)
    public void accessExternalStorageSuccess() {
        addToLibrary(queryFileFromDataBase(chooseUri));
    }

    @Keep
    @PermissionRationale(requestCode = REQUEST_PERMISSION)
    public void accessExternalStorageRationale() {
        PermissionHelper.showRationaleDialog(this, getResources().getString(R.string.permission_rationale_storage_tip));
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
                                && ContextCompat.checkSelfPermission(MaterialActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            PermissionGen.needPermission(MaterialActivity.this, REQUEST_PERMISSION,
                                    Manifest.permission.READ_EXTERNAL_STORAGE);
                        } else {
                            addToLibrary(queryFileFromDataBase(chooseUri));
                        }
                    }
                }
            }
            return;
        }

        //此处必须交给父类处理，不然fragment收不到其他的result
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void finish() {

        if (!showDownload) {
            resultRefresh();
        }

        super.finish();
    }


    private void initView() {


        DownloadProvider.updateCount(this);

        uploadBtn.setImageResource(R.drawable.upload_selector);
        modeBtn.setImageResource(R.drawable.ic_datasection_selector);


        if (showDownload) {

            tabGroupLayout.check(R.id.tab_material);
            tabGroupLayout.setVisibility(View.VISIBLE);
            titleView.setVisibility(View.GONE);

            fragmentList = new ArrayList<>(2);
            materialFragment = new MaterialFragment();

            fragmentList.add(materialFragment);
            fragmentList.add(new DownloadFragment());
        }else {

            tabGroupLayout.setVisibility(View.GONE);
            titleView.setVisibility(View.VISIBLE);

            fragmentList = new ArrayList<>(1);
            materialFragment = new MaterialFragment();
            fragmentList.add(materialFragment);
        }


        FrgStatePageAdapter adapter = new FrgStatePageAdapter(getSupportFragmentManager());
        adapter.setList(fragmentList);
        tabPager.setAdapter(adapter);

        tabPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    curCheckedTabId = R.id.tab_material;
                    tabGroupLayout.check(R.id.tab_material);
                    switchOperaBtn(R.id.tab_material);
                } else {
                    curCheckedTabId = R.id.tab_download;
                    tabGroupLayout.check(R.id.tab_download);
                    switchOperaBtn(R.id.tab_download);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabPager.setScrollEnable(false);
    }

    private void initToTab(int tab) {
        if (tab == 0) {
            tabPager.setCurrentItem(0);
            tabGroupLayout.check(R.id.tab_material);
            curCheckedTabId = R.id.tab_material;
        } else if (tab == 1) {
            tabPager.setCurrentItem(1);
            tabGroupLayout.check(R.id.tab_download);
            curCheckedTabId = R.id.tab_download;
        }
    }

    class FrgStatePageAdapter extends FragmentStatePagerAdapter {

        public FrgStatePageAdapter(FragmentManager fm) {
            super(fm);
        }

        private ArrayList<Fragment> listFrg = new ArrayList<Fragment>();

        public void setList(ArrayList<Fragment> listFrg) {
            this.listFrg = listFrg;
        }

        @Override
        public Fragment getItem(int arg0) {
            return listFrg.get(arg0);
        }

        @Override
        public int getCount() {
            return listFrg.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void switchOperaBtn(@IdRes int tabId) {
        if (tabId == R.id.tab_material) {
            modeBtn.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);

        } else {

            changeChoiceMode(ListView.CHOICE_MODE_NONE);

            modeBtn.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.GONE);
        }

        cancelBtn.setVisibility(View.GONE);
        choiceBtn.setVisibility(View.GONE);
    }

    private void changeChoiceStatus() {

        if (materialFragment.cancelChoiceAll()) {
            choiceBtn.setText(R.string.choice_all);
        } else {
            choiceBtn.setText(R.string.cancel_choice_all);
        }

        materialFragment.changeChoiceStatus();

    }

    protected void changeChoiceMode(int choiceMode) {
        choiceBtn.setText(R.string.choice_all);
        if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {

            backBtn.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.GONE);
            modeBtn.setVisibility(View.GONE);

            choiceBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        } else {

            backBtn.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);
            modeBtn.setVisibility(View.VISIBLE);

            choiceBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        }

        if (materialFragment.isAdded()) {
            materialFragment.changeChoiceMode(choiceMode);
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void resultRefresh() {
        MaterialAdapter materialAdapter = materialFragment.mAdapter;
        if(materialAdapter !=null
                && materialAdapter.getList() != null
                && materialAdapter.getList().size() > 0) {
            setResult(RESULT_OK);
        }
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //upload file

    private void chooseFile() {
        String action = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ?
                Intent.ACTION_OPEN_DOCUMENT : Intent.ACTION_GET_CONTENT;

        Intent intent = new Intent(action);
        intent.setType("*/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, BaseConstant.REQUEST_CODE_CHOOSE_FILE);
    }


    private File queryFileFromDataBase(Uri uri) {
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = getContentResolver().query(uri,
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
                ToastUtil.showToast(this, getString(R.string.upload_support_error_tips));
                return;
            }
        } else {
            ToastUtil.showToast(this, getString(R.string.upload_support_error_tips));
            return;
        }

        mUploadName.setText(file.getName());
        mUploadingWrapper.setVisibility(View.VISIBLE);
        mManager = new CollaManager();

        mManager.addToLibrary(this, file.getPath(), file.getName(), new QiniuService() {
            @Override
            public void uploadSuccess(String key, UploadReponse reponse) {
                mUploadingWrapper.setVisibility(View.GONE);
                chooseUri = null;

                if (materialFragment != null && materialFragment.mAdapter !=null) {
                    materialFragment.mAdapter.refresh();
                }

                ToastUtil.showToast(MaterialActivity.this, R.string.up_load_success);
            }

            @Override
            public void uploadProgress(String key, double percent) {

                int progress = (int) (percent * 100);

                mUploadPro.setText(getString(R.string.up_loading_with_per, progress));
                mUploadProgress.setProgress(progress);
            }

            @Override
            public void uploadFailure(boolean cancel) {
                mUploadingWrapper.setVisibility(View.GONE);
                if (cancel) {
                    ToastUtil.showToast(MaterialActivity.this, R.string.up_load_cancel);
                } else {
                    ToastUtil.showToast(MaterialActivity.this, R.string.up_load_failure);
                }

                chooseUri = null;
            }

        });
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


    @TargetApi(19)
    public String getPath(final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= 19; //Build.VERSION_CODES.KITKAT
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
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
                return getDataColumn(this, contentUri, null, null);
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

                return getDataColumn(this, contentUri, selection, selectionArgs);
            }
        } else if (ContentResolver.SCHEME_CONTENT.equalsIgnoreCase(uri.getScheme())) {
            // MediaStore (and general)
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.getLastPathSegment();
            }

            return getDataColumn(this, uri, null, null);
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



}
