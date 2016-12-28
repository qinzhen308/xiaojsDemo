package cn.xiaojs.xma.common.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.FileUtil;

import butterknife.OnClick;

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

public class CropImageActivity extends BaseActivity {
    private int mWidth = 300;
    private int mHeight = 300;
    private Bitmap mBitmap;
    private CropImageView mCropImage;
    private boolean isNotCrop = false;
    private boolean isUploadCompress = false;
    private RelativeLayout mBottomContent;
    private int mScreenWidth;
    private int mScreenHeight;

    @Override
    public void addViewContent() {
        if (getIntent() != null) {
            mWidth = getIntent().getIntExtra(CropImagePath.CROP_IMAGE_WIDTH,
                    300);
            mHeight = getIntent().getIntExtra(CropImagePath.CROP_IMAGE_HEIGHT,
                    300);
            isNotCrop = getIntent().getBooleanExtra(CropImagePath.CROP_NEVER,
                    false);
            isUploadCompress = getIntent().getBooleanExtra(
                    CropImagePath.UPLOAD_COMPRESS, false);
        }
        addView(R.layout.crop_image_layout);

        needHeader(false);
        mBottomContent = (RelativeLayout)findViewById(R.id.crop_bottom_content);
        mBottomContent.getBackground().setAlpha(242);


        DisplayMetrics dm = getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;
        cropImage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

    private void cropImage() {
        mCropImage = (CropImageView) findViewById(R.id.crop_img);
        mBitmap = BitmapUtils.createNewBitmapAndCompressByFile(
                CropImagePath.UPLOAD_IMAGE_PATH,
                new int[] { mScreenWidth,
                        mScreenHeight - 100 }, 0);
        Drawable drawable = new BitmapDrawable(getResources(), mBitmap);
        mCropImage.setDrawable(drawable, mWidth, mHeight, isNotCrop);
    }

    @OnClick({ R.id.rotate, R.id.save, R.id.cancel })
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rotate:
                mCropImage.setRotate(90);
                break;
            case R.id.cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.save:
                new AsyncTask<Integer, Integer, String>() {

                    @Override
                    protected String doInBackground(Integer... params) {
                        return BitmapUtils.saveImage(mCropImage.getCropImage(),
                                FileUtil.SDCARD_PATH + CropImagePath.CROP_IMAGE_PATH, 90);
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        Intent mIntent = new Intent();
                        mIntent.putExtra(CropImagePath.CROP_IMAGE_PATH_TAG, result);
                        setResult(RESULT_OK, mIntent);
                        finish();
                    }
                }.execute(0);
                break;
            default:
                break;
        }
    }
}
