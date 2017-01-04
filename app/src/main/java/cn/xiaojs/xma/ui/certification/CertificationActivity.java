package cn.xiaojs.xma.ui.certification;
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
 * Date:2017/1/4
 * Desc:
 *
 * ======================================================================================== */

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.crop.CropImageMainActivity;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.DeviceUtil;

public class CertificationActivity extends BaseActivity {

    @BindView(R.id.certification_status_wrapper)
    LinearLayout mStatusWrapper;
    @BindView(R.id.certification_input_wrapper)
    LinearLayout mApplyWrapper;

    @BindView(R.id.certification_status)
    TextView mStatus;
    @BindView(R.id.certification_status_desc)
    TextView mStatusDesc;

    @BindView(R.id.certification_verify_time)
    TextView mVerifyTime;
    @BindView(R.id.certification_submit_time)
    TextView mSubmitTime;
    @BindView(R.id.certification_id_card)
    TextView mIdCard;
    @BindView(R.id.certification_name)
    TextView mName;
    @BindView(R.id.certification_modify)
    Button mModify;

    @BindView(R.id.verify_timer_wrapper)
    LinearLayout mVerifyTimeWrapper;

    @BindView(R.id.certification_input_name)
    EditText mNameInput;
    @BindView(R.id.certification_input_number)
    EditText mNumberInput;
    @BindView(R.id.certification_image_liner)
    LinearLayout mCerImageWrapper;
    @BindView(R.id.certification_image)
    ImageView mCerImage;

    private final float PERCENT = 3.0F / 4;

    private boolean mHasImage;

    private int mApplyImageWidth;
    private int mApplyImageHeight;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_certification);
        setMiddleTitle(R.string.certification);
        apply();
    }

    private void apply() {
        mApplyWrapper.setVisibility(View.VISIBLE);
        mStatusWrapper.setVisibility(View.GONE);
        mApplyImageWidth = (DeviceUtil.getScreenWidth(this) - getResources().getDimensionPixelSize(R.dimen.px90)) / 2;
        mApplyImageHeight = (int) (mApplyImageWidth * PERCENT);
        mCerImageWrapper.getLayoutParams().height = mApplyImageHeight;

    }

    private void examine() {
        mApplyWrapper.setVisibility(View.GONE);
        mStatusWrapper.setVisibility(View.VISIBLE);
        mModify.setVisibility(View.GONE);
        mVerifyTimeWrapper.setVisibility(View.GONE);
    }

    private void failed() {
        mApplyWrapper.setVisibility(View.GONE);
        mStatusWrapper.setVisibility(View.VISIBLE);
        mModify.setVisibility(View.VISIBLE);
        mVerifyTimeWrapper.setVisibility(View.VISIBLE);
    }

    private void success() {
        mApplyWrapper.setVisibility(View.GONE);
        mStatusWrapper.setVisibility(View.VISIBLE);
        mModify.setVisibility(View.GONE);
        mVerifyTimeWrapper.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.left_image, R.id.certification_modify, R.id.certification_image_wrapper, R.id.submit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.certification_modify://修改认证资料
                apply();
                break;
            case R.id.certification_image_wrapper://点击弹出加载图片对话框
                selectImage();
                break;
            case R.id.submit://提交认证资料
                break;
        }
    }

    private void selectImage() {
        Intent intent = new Intent(this, CropImageMainActivity.class);
        intent.putExtra(CropImageMainActivity.NEED_DELETE, mHasImage);
        intent.putExtra(CropImagePath.CROP_IMAGE_WIDTH,mApplyImageWidth);
        intent.putExtra(CropImagePath.CROP_IMAGE_HEIGHT,mApplyImageHeight);
        startActivityForResult(intent, CertificationConstant.REQUEST_SELECT_IMAGE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CertificationConstant.REQUEST_SELECT_IMAGE:
                if (resultCode == CropImageMainActivity.RESULT_DELETE) {
                    mCerImage.setImageBitmap(null);
                    mHasImage = false;
                } else {
                    if (data != null) {
                        String cropImgPath = data.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
                        Glide.with(this)
                                .load(cropImgPath)
                                .error(R.drawable.default_lesson_cover)
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(mCerImage);
                        mHasImage = true;
                    }
                }
                break;
        }
    }
}
