package com.benyuan.xiaojs.ui.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.crop.CropImageMainActivity;
import com.benyuan.xiaojs.common.crop.CropImagePath;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;
import com.benyuan.xiaojs.util.DataPicker;
import com.wheelpicker.DateWheelPicker;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
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
 * Date:2016/11/4
 * Desc:
 *
 * ======================================================================================== */

public class ProfileActivity extends BaseActivity {
    private final static int CROP_PORTRAIT = 100;

    @BindView(R.id.portrait)
    RoundedImageView mPortraitView;
    @BindView(R.id.birthday)
    TextView mBirthdayView;
    @BindView(R.id.sex)
    TextView mSexView;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_profile);

        init();

        //load portrait
        loadPortrait();
    }

    @OnClick({R.id.left_image, R.id.edit_portrait, R.id.sex, R.id.birthday})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;

            case R.id.edit_portrait:
                editPortrait();
                break;

            case R.id.birthday:
                pickBirthday();
                break;

            case R.id.sex:
                pickSex();
                break;

            default:
                break;
        }
    }

    private void init() {
        setMiddleTitle(R.string.my_profile);
        setLeftImage(R.drawable.back_arrow);
        setRightText(R.string.finish);
        mRightText.setTextColor(getResources().getColor(R.color.main_orange));
    }

    private void loadPortrait() {
        Bitmap portrait = BitmapFactory.decodeResource(getResources(), R.drawable.default_portrait);
        mPortraitView.setImageBitmap(portrait);
    }

    private void editPortrait() {
        Intent i = new Intent(this, CropImageMainActivity.class);
        startActivityForResult(i, CROP_PORTRAIT);
    }

    private void pickBirthday() {
        DataPicker.pickBirthday(this, new DataPicker.OnBirthdayPickListener() {
            @Override
            public void onBirthPicked(int year, int month, int day) {
                mBirthdayView.setText("" + year+"-" + month + "-" + day);
            }
        });
    }

    private void pickSex() {
        List<String> sexList = new ArrayList<String>();
        sexList.add(getString(R.string.male));
        sexList.add(getString(R.string.female));
        DataPicker.pickData(this, sexList, new DataPicker.OnDataPickListener() {
            @Override
            public void onDataPicked(Object data) {
                if (data instanceof String) {
                    mSexView.setText((String)data);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (data != null) {
                    String cropImgPath = data.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
                    Bitmap portrait = BitmapFactory.decodeFile(cropImgPath);
                    if (portrait != null) {
                        mPortraitView.setImageBitmap(portrait);
                    }
                }
                break;
        }
    }

}