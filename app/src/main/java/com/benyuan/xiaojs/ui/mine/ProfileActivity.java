package com.benyuan.xiaojs.ui.mine;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.crop.CropImageMainActivity;
import com.benyuan.xiaojs.common.crop.CropImagePath;
import com.benyuan.xiaojs.data.AccountDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.QiniuService;
import com.benyuan.xiaojs.model.Account;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.base.BaseBusiness;
import com.benyuan.xiaojs.ui.widget.EditTextDel;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;
import com.benyuan.xiaojs.util.DataPicker;
import com.benyuan.xiaojs.util.TimeUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    public final static String KEY_ACCOUNT_BEAN = "key_account_bean";
    private final static int CROP_PORTRAIT = 100;

    @BindView(R.id.portrait)
    RoundedImageView mPortraitView;
    @BindView(R.id.birthday)
    TextView mBirthdayView;
    @BindView(R.id.sex)
    TextView mSexView;
    @BindView(R.id.name)
    EditTextDel mName;
    @BindView(R.id.user_title)
    EditTextDel mUserTitle;

    private Date mBirthDayDate;
    private String mAvatarFileName;
    private Account mAccount;
    private long mOldTime;
    private Account.Basic mBasic;
    private boolean mImgUploading = false;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_profile);

        init();

        //load portrait
        loadData();
    }

    @OnClick({R.id.left_image, R.id.edit_portrait, R.id.sex, R.id.birthday, R.id.right_view})
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

            case R.id.right_view:
                submitEditProfile();
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
        mBirthDayDate = new Date(System.currentTimeMillis());
        mOldTime = mBirthDayDate.getTime();
    }

    private void loadData() {
        showProgress(true);
        AccountDataManager.requestProfile(this, new APIServiceCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                cancelProgress();
                if (account != null) {
                    mAccount = account;
                    Account.Basic basic = account.getBasic();
                    if (basic != null) {
                        //avatar
                        Glide.with(ProfileActivity.this)
                                .load(basic.getAvatar())
                                .error(R.drawable.default_avatar)
                                .into(mPortraitView);

                        mName.setText(basic.getName());
                        mSexView.setText(basic.isSex() ? R.string.male : R.string.female);
                        //TODO to be optimized
                        if (basic.getBirthday() != null && (basic.getBirthday().getTime()) > 0) {
                            mOldTime = mBirthDayDate.getTime();
                            mBirthDayDate.setTime(basic.getBirthday().getTime());
                            mBirthdayView.setText(TimeUtil.format(mBirthDayDate, TimeUtil.TIME_YYYY_MM_DD));
                        }

                        mUserTitle.setText(basic.getTitle());
                    }
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                showFailedView(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loadData();
                    }
                });
            }
        });
    }

    private boolean checkSubmitEditInfo() {
        if (mAccount == null || mAccount.getBasic() == null) {
            return true;
        }

        Account.Basic basic = mAccount.getBasic();
        if (!mName.getText().toString().equals(basic.getName())) {
            return true;
        }

        if (!mUserTitle.getText().toString().equals(basic.getTitle())) {
            return true;
        }

        if (!mSexView.getText().toString().equals(getString(basic.isSex()
                ? R.string.male : R.string.female))) {
            return true;
        }

        if (basic.getBirthday() != null) {
            String t = TimeUtil.format(basic.getBirthday(), TimeUtil.TIME_YYYY_MM_DD);
            if (!mBirthdayView.getText().toString().equals(t)) {
                return true;
            }
        } else {
            if (!TextUtils.isEmpty(mBirthdayView.getText().toString())) {
                return true;
            }
        }

        if (mAvatarFileName != null) {
            return true;
        }

        return false;
    }

    private void submitEditProfile() {
        if (mImgUploading) {
            return;
        }

        if (!checkSubmitEditInfo()) {
            //not editing
            finish();
            return;
        }

        if (mBasic == null) {
            mBasic = new Account.Basic();
        }
        Account.Basic basic = mBasic;
        basic.setName(mName.getText().toString());
        basic.setTitle(mUserTitle.getText().toString());

        //TODO sex
        if (getString(R.string.male).equals(mSexView.getText().toString())) {
            basic.setSex(true);
        } else {
            basic.setSex(false);
        }
        if (mBirthDayDate.getTime() !=  mOldTime) {
            basic.setBirthday(new Date(mBirthDayDate.getTime()));
        }

        if (!TextUtils.isEmpty(mAvatarFileName)) {
            basic.setAvatar(mAvatarFileName);
        }

        AccountDataManager.requestEditProfile(this, basic, new APIServiceCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                Toast.makeText(ProfileActivity.this, R.string.edit_profile_success, Toast.LENGTH_SHORT).show();
                if (mAccount == null) {
                    mAccount = new Account();
                }
                mAccount.setBasic(mBasic);

                Intent i = new Intent();
                i.putExtra(KEY_ACCOUNT_BEAN, mAccount);
                setResult(RESULT_OK, i);
                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void editPortrait() {
        Intent i = new Intent(this, CropImageMainActivity.class);
        startActivityForResult(i, CROP_PORTRAIT);
    }

    private void pickBirthday() {
        DataPicker.pickBirthday(this, mBirthDayDate, new DataPicker.OnBirthdayPickListener() {
            @Override
            public void onBirthPicked(int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day, 0, 0, 0);
                mBirthDayDate.setTime(calendar.getTimeInMillis());
                String dateStr = TimeUtil.formatDate(calendar.getTimeInMillis(), TimeUtil.TIME_YYYY_MM_DD);
                mBirthdayView.setText(dateStr);
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
                    mSexView.setText((String) data);
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
                    //Bitmap portrait = BitmapFactory.decodeFile(cropImgPath);
                    if (cropImgPath != null) {
                        //mPortraitView.setImageBitmap(portrait);
                        //TODO upload avatar and get filename
                        mImgUploading = true;
                        AccountDataManager.requestUploadAvatar(ProfileActivity.this,
                                cropImgPath,
                                new QiniuService() {

                                    @Override
                                    public void uploadSuccess(String fileName, String fileUrl) {
                                        Glide.with(ProfileActivity.this)
                                                .load(fileUrl)
                                                .into(mPortraitView);
                                        mAvatarFileName = fileName;

                                        mImgUploading = false;
                                    }

                                    @Override
                                    public void uploadFailure() {
                                        mImgUploading = false;
                                    }
                                } );
                    }
                }
                break;
        }
    }

}
