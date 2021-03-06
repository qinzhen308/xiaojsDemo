package cn.xiaojs.xma.common.crop;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 * Author:huangyong
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

public class CropImageActivity extends BaseActivity {
    public static final String ACTION_DONE_TXT = "action_done_txt"; //action done按钮的文字

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
        String actionDoneTxt = null;
        if (getIntent() != null) {
            mWidth = getIntent().getIntExtra(CropImagePath.CROP_IMAGE_WIDTH, 300);
            mHeight = getIntent().getIntExtra(CropImagePath.CROP_IMAGE_HEIGHT, 300);
            isNotCrop = getIntent().getBooleanExtra(CropImagePath.CROP_NEVER, false);
            isUploadCompress = getIntent().getBooleanExtra(CropImagePath.UPLOAD_COMPRESS, false);
            actionDoneTxt = getIntent().getStringExtra(ACTION_DONE_TXT);
        }
        addView(R.layout.crop_image_layout);
        if (!TextUtils.isEmpty(actionDoneTxt)) {
            ((TextView)findViewById(R.id.save)).setText(actionDoneTxt);
        }

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
                new AsyncTask<Integer, Integer, Intent>() {

                    @Override
                    protected Intent doInBackground(Integer... params) {
                        Bitmap bmp = mCropImage.getCropImage();
                        String path = BitmapUtils.saveImage(bmp, FileUtil.SDCARD_PATH + CropImagePath.CROP_IMAGE_PATH, 90);
                        Intent i = new Intent();
                        i.putExtra(CropImagePath.CROP_IMAGE_PATH_TAG, path);
                        i.putExtra(CropImagePath.CROP_IMAGE_WIDTH, bmp != null ? bmp.getWidth() : 0);
                        i.putExtra(CropImagePath.CROP_IMAGE_HEIGHT, bmp != null ? bmp.getHeight() : 0);
                        return i;
                    }

                    @Override
                    protected void onPostExecute(Intent intent) {
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }.execute(0);
                break;
            default:
                break;
        }
    }
}
