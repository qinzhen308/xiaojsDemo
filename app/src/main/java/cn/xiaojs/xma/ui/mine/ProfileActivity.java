package cn.xiaojs.xma.ui.mine;

import android.content.Intent;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.crop.CropImageMainActivity;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.model.account.User;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.DataPicker;
import cn.xiaojs.xma.util.JpushUtil;
import cn.xiaojs.xma.util.TimeUtil;

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
    public final static String KEY_BASE_BEAN = "key_base_bean";
    private final static int CROP_PORTRAIT = 100;
    private final static int MAX_CHAR = 20;

    @BindView(R.id.portrait)
    RoundedImageView mPortraitImg;
    @BindView(R.id.birthday)
    TextView mBirthdayTv;
    @BindView(R.id.sex)
    TextView mSexTv;
    @BindView(R.id.name)
    EditTextDel mNameEdt;
    @BindView(R.id.user_title)
    EditTextDel mUserTitleEdt;
    @BindView(R.id.input_tips)
    TextView mInputTipsTv;

    private Date mBirthDayDate;
//    private String mAvatarFileName;
    private String mAvatarUrl;
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

        mInputTipsTv.setText(Html.fromHtml(String.format(getString(R.string.input_tips), 0, MAX_CHAR)));
        mUserTitleEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                Spanned tips = Html.fromHtml(String.format(getString(R.string.input_tips), s.length(), MAX_CHAR));
                mInputTipsTv.setText(tips);
            }
        });
    }

    private void loadData() {
        showProgress(true);
        AccountDataManager.requestProfile(this, new APIServiceCallback<Account>() {
            @Override
            public void onSuccess(Account account) {
                cancelProgress();
                Account.Basic basic ;
                if (account != null && (basic = account.getBasic()) != null) {
                    //avatar
                    mBasic = basic;
                    Glide.with(ProfileActivity.this)
                            .load(cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(AccountDataManager.getAccountID(ProfileActivity.this),300))
                            .error(R.drawable.default_avatar_grey)
                            .into(mPortraitImg);

                    mNameEdt.setText(basic.getName());
                    //set sex
                    if ("true".equals(basic.getSex())) {
                        mSexTv.setText(R.string.male);
                    } else if ("false".equals(basic.getSex())) {
                        mSexTv.setText(R.string.female);
                    }
                    //set birthday
                    if (basic.getBirthday() != null && (basic.getBirthday().getTime()) > 0) {
                        mOldTime = mBirthDayDate.getTime();
                        mBirthDayDate.setTime(basic.getBirthday().getTime());
                        mBirthdayTv.setText(TimeUtil.format(mBirthDayDate, TimeUtil.TIME_YYYY_MM_DD));
                    }

                    mUserTitleEdt.setText(basic.getTitle());
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

    private boolean checkSubmitInfoChanged() {
        Account.Basic basic = mBasic;
        if (!mNameEdt.getText().toString().equals(basic.getName())) {
            return true;
        }

        String title = mUserTitleEdt.getText().toString();
        if ((TextUtils.isEmpty(title) && !TextUtils.isEmpty(basic.getTitle())) ||
                (!TextUtils.isEmpty(title) && TextUtils.isEmpty(basic.getTitle())) ||
                (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(basic.getTitle()) && !title.equals(basic.getTitle()))) {

            return true;
        }

        String sexTxt = mSexTv.getText().toString();
        String sex = TextUtils.isEmpty(basic.getSex()) ? "" :
                getString("true".equals(basic.getSex()) ? R.string.male : R.string.female);
        if ((TextUtils.isEmpty(sexTxt) && !TextUtils.isEmpty(sex)) ||
                (!TextUtils.isEmpty(sexTxt) && TextUtils.isEmpty(sex)) ||
                (!TextUtils.isEmpty(sexTxt) && !TextUtils.isEmpty(sex) && !sexTxt.equals(sex))) {
            return true;
        }

        if (basic.getBirthday() != null) {
            String t = TimeUtil.format(basic.getBirthday(), TimeUtil.TIME_YYYY_MM_DD);
            if (!mBirthdayTv.getText().toString().equals(t)) {
                return true;
            }
        } else {
            if (!TextUtils.isEmpty(mBirthdayTv.getText().toString())) {
                return true;
            }
        }

        if (mAvatarUrl != null) {
            return true;
        }

        return false;
    }

    private boolean checkSubmitInfoValid() {
        String userName = mNameEdt.getText().toString();
        if (!TextUtils.isEmpty(userName) && (userName.length() < 2 || userName.length() > 16)) {
            if (userName.length() < 2) {
                Toast.makeText(this, R.string.name_length_less_than_2, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.name_length_more_than_16, Toast.LENGTH_SHORT).show();
            }

            return false;
        }

        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, R.string.name_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        String title = mUserTitleEdt.getText().toString();
        if (!TextUtils.isEmpty(title) && title.length() > 20) {
            return false;
        }

        return true;
    }

    private void submitEditProfile() {
        if (mImgUploading) {
            return;
        }


        if (!checkSubmitInfoValid()) {
            return;
        }

        boolean flag = checkSubmitInfoChanged();
        if (!flag) {
            //not editing
            finish();
            return;
        }

        if (mBasic == null) {
            mBasic = new Account.Basic();
        }
        final Account.Basic basic = mBasic;
        basic.setName(mNameEdt.getText().toString());
        basic.setTitle(mUserTitleEdt.getText().toString());

        String sexTv = mSexTv.getText().toString();
        if (getString(R.string.male).equals(sexTv)) {
            basic.setSex("true");
        } else if (getString(R.string.female).equals(sexTv)) {
            basic.setSex("false");
        }
        if (mBirthDayDate.getTime() != mOldTime) {
            basic.setBirthday(new Date(mBirthDayDate.getTime()));
        }

//        if (!TextUtils.isEmpty(mAvatarFileName)) {
//            basic.setAvatar(mAvatarFileName);
//        }

        AccountDataManager.requestEditProfile(this, basic, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(ProfileActivity.this, R.string.edit_profile_success, Toast.LENGTH_SHORT).show();

                Intent i = new Intent();
                //update display url
                if (!TextUtils.isEmpty(mAvatarUrl)){
                    mBasic.setAvatar(mAvatarUrl);
                    XiaojsConfig.AVATOR_TIME = String.valueOf(System.currentTimeMillis());
                    AccountPref.setAvatorTime(ProfileActivity.this, XiaojsConfig.AVATOR_TIME);
                }


                updateUser(basic);

                i.putExtra(KEY_BASE_BEAN, mBasic);
                setResult(RESULT_OK, i);
                finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser(Account.Basic basic) {
        if (basic == null) return;

        User user = XiaojsConfig.mLoginUser;
        if ( user!= null &&  user.getAccount() != null){
            user.getAccount().setBasic(basic);

            AccountDataManager.setUserInfo(this, user);

        }

        //FIXME 更新IM 用户信息


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
                mBirthdayTv.setText(dateStr);
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
                    mSexTv.setText((String) data);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                if (data != null) {
                    final String cropImgPath = data.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
                    if (cropImgPath != null) {
                        mImgUploading = true;
                        showProgress(true);
                        AccountDataManager.requestUploadAvatar(ProfileActivity.this,
                                cropImgPath,
                                new QiniuService() {

                                    @Override
                                    public void uploadSuccess(String key, UploadReponse reponse) {
                                        cancelProgress();
                                        Glide.with(ProfileActivity.this)
                                                .load(cropImgPath)
                                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                                .into(mPortraitImg);
                                        mAvatarUrl = key;
//                                        mAvatarFileName = fileName;

                                        mImgUploading = false;
                                    }

                                    @Override
                                    public void uploadProgress(String key, double percent) {

                                    }

                                    @Override
                                    public void uploadFailure(boolean cancel) {
                                        cancelProgress();
                                        Toast.makeText(ProfileActivity.this, R.string.upload_portrait_fail, Toast.LENGTH_SHORT).show();
                                        mImgUploading = false;
                                    }
                                });
                    }
                }
                break;
        }
    }

}
