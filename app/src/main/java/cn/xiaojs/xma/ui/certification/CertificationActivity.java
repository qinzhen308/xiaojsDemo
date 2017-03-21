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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.crop.CropImageMainActivity;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.TimeUtil;

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

    private String photoKey;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_certification);
        setMiddleTitle(R.string.certification);

        if (AccountDataManager.isVerified(this)){
            success();
        }else{
            apply();
        }
    }

    private void apply() {
        mApplyWrapper.setVisibility(View.VISIBLE);
        mStatusWrapper.setVisibility(View.GONE);
        mApplyImageWidth = (DeviceUtil.getScreenWidth(this) - getResources().getDimensionPixelSize(R.dimen.px90)) / 2;
        mApplyImageHeight = (int) (mApplyImageWidth * PERCENT);
        mCerImageWrapper.getLayoutParams().height = mApplyImageHeight;

    }

    private void examine(String name,String no) {

        mStatus.setText("您已提交审核");
        mStatusDesc.setText("预计24小时内完成审核，请耐心等待");
        mSubmitTime.setText(TimeUtil.formatDate(System.currentTimeMillis(), TimeUtil.TIME_YYYY_MM_DD_HH_MM));

        mName.setText(name);
        mIdCard.setText(no);

        mApplyWrapper.setVisibility(View.GONE);
        mStatusWrapper.setVisibility(View.VISIBLE);
        mModify.setVisibility(View.GONE);
        mVerifyTimeWrapper.setVisibility(View.GONE);
    }

    //审核失败
    private void failed() {

        mStatus.setText("审核失败");
        mStatusDesc.setText("您上传的身份信息不清晰，请重新上传");

        mApplyWrapper.setVisibility(View.GONE);
        mStatusWrapper.setVisibility(View.VISIBLE);
        mModify.setVisibility(View.VISIBLE);
        mVerifyTimeWrapper.setVisibility(View.VISIBLE);
    }

    //认证成功
    private void success() {
        mApplyWrapper.setVisibility(View.GONE);
        mModify.setVisibility(View.GONE);

        mStatus.setText("您已认证成功");
        mStatusDesc.setText("很高兴认识您，您可以享受更多小教室服务与权益");

        mStatusWrapper.setVisibility(View.VISIBLE);
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
                toSubmit();
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
                        uploadHand(cropImgPath);
                    }
                }
                break;
        }
    }

    private void uploadHand(final String filePath) {

        showProgress(true);
        AccountDataManager.uploadHandhold(this, filePath, new QiniuService() {
            @Override
            public void uploadSuccess(String key, UploadReponse reponse) {
                cancelProgress();

                Glide.with(CertificationActivity.this)
                        .load(filePath)
                        .error(R.drawable.default_lesson_cover)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .into(mCerImage);

                photoKey = key;
                mHasImage = true;
                Toast.makeText(CertificationActivity.this,"上传成功",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void uploadProgress(String key, double percent) {

            }

            @Override
            public void uploadFailure(boolean cancel) {
                cancelProgress();
                mHasImage = false;
            }
        });
    }

    private void toSubmit() {

        final String name = mNameInput.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this,"请输入姓名",Toast.LENGTH_SHORT).show();
            return;
        }

        final String cardNo = mNumberInput.getText().toString().trim();
        if (TextUtils.isEmpty(cardNo)) {
            Toast.makeText(this,"请输入身份证号码",Toast.LENGTH_SHORT).show();
            return;
        }

        if(!mHasImage) {
            Toast.makeText(this,"请上传手持身份证照片",Toast.LENGTH_SHORT).show();
            return;
        }

        showProgress(true);
        AccountDataManager.requestVerification(this, name, cardNo, photoKey, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                examine(name, cardNo);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                cancelProgress();
                Toast.makeText(CertificationActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
