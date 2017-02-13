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

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.DownloadManager;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.colla.UploadReponse;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.base.BaseConstant;
import cn.xiaojs.xma.util.ToastUtil;

public class MaterialActivity extends BaseActivity {

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
        mAdapter = new MaterialAdapter(this, mList, mIsMine);
        mList.setAdapter(mAdapter);
        mRightImage2.setImageResource(R.drawable.upload_selector);
        mRightImage.setImageResource(R.drawable.ic_my_download);
    }

    @OnClick({R.id.material_left_image, R.id.material_right_image,R.id.material_right_image2, R.id.material_up_load_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.material_left_image:
                finish();
                break;
            case R.id.material_right_image://我的下载
                DownloadManager.enqueueDownload(this,"vipkid" + System.currentTimeMillis() + ".apk","key","http://file.vipkid.com.cn/apps/vipkid_v1.2.1.apk","","");
                Intent intent = new Intent(this,MaterialDownloadActivity.class);
                startActivity(intent);
                break;
            case R.id.material_right_image2://上传文件
                upload();
                break;
            case R.id.material_up_load_close://取消上传
                if (mManager != null){
                    mManager.cancelAdd();
                }
                break;
        }
    }

    private void upload() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, BaseConstant.REQUEST_CODE_CHOOSE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseConstant.REQUEST_CODE_CHOOSE_FILE) {
            if (resultCode == RESULT_OK && data != null) {//是否选择，没选择就不会继续
                Uri uri = data.getData();//得到uri，后面就是将uri转化成file的过程。
                if (uri != null) {
                    File file = new File(uri.getPath());
                    if (file == null)
                        return;
                    mUploadName.setText(file.getName());
                    mUploadingWrapper.setVisibility(View.VISIBLE);
                    mManager = new CollaManager();
                    String t = "869f6f9be63c3ee2157b4188e709718638f7e8faf2e1223f389631a3f2dfc5f8f9025c1208dacc1b32ab324f5d9da842";
                    mManager.addToLibrary(this, file.getPath(), file.getName(), t, new QiniuService() {
                        @Override
                        public void uploadSuccess(String key, UploadReponse reponse) {
                            mUploadingWrapper.setVisibility(View.GONE);
                            ToastUtil.showToast(getApplicationContext(), R.string.up_load_success);
                        }

                        @Override
                        public void uploadProgress(String key, double percent) {
                            mUploadProgress.setProgress((int) (percent * 100));
                        }

                        @Override
                        public void uploadFailure(boolean cancel) {
                            mUploadingWrapper.setVisibility(View.GONE);
                            if (cancel){
                                ToastUtil.showToast(getApplicationContext(), R.string.up_load_cancel);
                            }else {
                                ToastUtil.showToast(getApplicationContext(), R.string.up_load_failure);
                            }
                        }
                    });
                }
            }
        }
    }
}
