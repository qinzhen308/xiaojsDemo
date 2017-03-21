package cn.xiaojs.xma.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.RegisterDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.APIEntity;
import cn.xiaojs.xma.model.security.LoginParams;
import cn.xiaojs.xma.model.account.RegisterInfo;
import cn.xiaojs.xma.model.VerifyCode;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.VerifyUtils;
import cn.xiaojs.xma.util.XjsUtils;
import com.orhanobut.logger.Logger;

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
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

public class RegisterActivity extends BaseActivity {
    private final static String TAG = "RegisterActivity";
    private final static int MSG_RE_SEND = 0;
    private final int SMS_GETTING_TIME_OUT = 60;

    @BindView(R.id.reg_name)
    EditTextDel mRegNameEdit;
    @BindView(R.id.reg_phone)
    EditTextDel mRegPhoneEdit;
    @BindView(R.id.verify_code)
    EditTextDel mRegVerifyEdit;
    @BindView(R.id.get_verify_code)
    TextView mGetRegVerifyTv;
    @BindView(R.id.reg_pwd)
    EditTextDel mRegPwdEdit;

    private Activity mContext;
    private int mCurrVerifyTime = 0;
    private boolean mPwdHidden = true;

    @Override
    protected void addViewContent() {
        mContext = this;
        addView(R.layout.activity_register);
        needHeader(false);

        mRegNameEdit.setDelDrawable(R.drawable.ic_account_edit_text_del);
        mRegPhoneEdit.setDelDrawable(R.drawable.ic_account_edit_text_del);
        mRegVerifyEdit.setDelDrawable(R.drawable.ic_account_edit_text_del);
        mRegPwdEdit.setDelDrawable(R.drawable.ic_account_edit_text_del);

        setMiddleTitle(R.string.register);
    }

    @OnClick({R.id.left_view, R.id.get_verify_code, R.id.hide_show_pwd,
            R.id.register_btn, R.id.register_protocol, R.id.reg_back})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
            case R.id.reg_back:
                finish();
                break;
            case R.id.get_verify_code:
                getVerifyCode(v);
                break;
            case R.id.register_btn:
                submitReg();
                break;
            case R.id.hide_show_pwd:
                hideOrShowPwd((ImageView) v);
                break;
            case R.id.register_protocol:
                showRegisterProtocol();
                break;
            default:
                break;
        }
    }

    /**
     * 隐藏或显示密码
     */
    private void hideOrShowPwd(ImageView v) {
        String str = mRegPwdEdit.getText().toString().trim();
        if (mPwdHidden) {
            v.setImageDrawable(getResources().getDrawable(R.drawable.show_pwd));
            mRegPwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mPwdHidden = false;
        } else {
            v.setImageDrawable(getResources().getDrawable(R.drawable.hide_pwd));
            mRegPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mPwdHidden = true;
        }

        mRegPwdEdit.setText(str);
        mRegPwdEdit.setSelection(str.length());
    }

    private void getVerifyCode(final View view) {
        String phoneTxt = mRegPhoneEdit.getEditableText().toString().trim();
        if (TextUtils.isEmpty(phoneTxt)) {
            Toast.makeText(this, R.string.phone_empty, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!VerifyUtils.checkPhoneNum(phoneTxt)) {
            Toast.makeText(this, R.string.phone_error, Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mGetRegVerifyTv.setEnabled(false);
            showProgress(true);
            long phone = Long.parseLong(mRegPhoneEdit.getEditableText().toString());
            RegisterDataManager.requestSendVerifyCode(this, phone, new APIServiceCallback<VerifyCode>() {

                @Override
                public void onSuccess(VerifyCode object) {
                    cancelProgress();
                    mCurrVerifyTime = SMS_GETTING_TIME_OUT;
                    mHandler.sendEmptyMessage(0);

                    if (!APPUtils.isProEvn()) {
                        mRegVerifyEdit.setText(String.valueOf(object.getCode()));
                    }

                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    mGetRegVerifyTv.setEnabled(true);
                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (NumberFormatException e) {
            Logger.i(TAG, "phone num error");
            cancelProgress();
            mGetRegVerifyTv.setEnabled(true);
        }

        XjsUtils.hideIMM(this, getCurrentFocus().getWindowToken());
    }

    private void submitReg() {
        if (!checkSubmitInfo()) {
            return;
        }

        try {
            String userName = mRegNameEdit.getEditableText().toString();
            long phone = Long.parseLong(mRegPhoneEdit.getEditableText().toString());
            String password = mRegPwdEdit.getText().toString().trim();
            int verifyCode = Integer.parseInt(mRegVerifyEdit.getText().toString().trim());

            final RegisterInfo regInfo = new RegisterInfo();
            regInfo.mobile = phone;
            regInfo.code = verifyCode;
            regInfo.password = password;
            regInfo.username = userName;

            showProgress(true);
            RegisterDataManager.requestValidateCode(this, phone, verifyCode, new APIServiceCallback<APIEntity>() {

                @Override
                public void onSuccess(APIEntity object) {
                    //verify code right
                    RegisterDataManager.requestRegisterByAPI(mContext, regInfo, new APIServiceCallback() {

                        @Override
                        public void onSuccess(Object object) {
                            //login immediately after successful registration

                            final LoginParams loginParams = new LoginParams();
                            loginParams.setMobile(regInfo.mobile);
                            loginParams.setPassword(regInfo.password);
                            AccountBusiness.login(mContext, true,loginParams, new AccountBusiness.OnLoginListener() {
                                @Override
                                public void onLogin(boolean succ) {
                                    cancelProgress();

                                    if (succ) {
                                        RegisterActivity.this.finish();
                                    } else {
                                        RegisterActivity.this.finish();
                                        Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        i.putExtra(XiaojsConfig.KEY_LOGIN_USERNAME, regInfo.mobile);
                                        startActivity(i);
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(String errorCode, String errorMessage) {
                            //register error
                            cancelProgress();
                            Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    //verify code error
                    cancelProgress();
                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {

        }
    }

    /**
     * 提交注册时，检测输入信息
     *
     */
    private boolean checkSubmitInfo() {
        String userName = mRegNameEdit.getText().toString().trim();
        String phoneNum = mRegPhoneEdit.getText().toString().trim();
        String password = mRegPwdEdit.getText().toString().trim();
        String verifyCode = mRegVerifyEdit.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, R.string.name_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!TextUtils.isEmpty(userName) && (userName.length() < 2 || userName.length() > 16)) {
            if (userName.length() < 2) {
                Toast.makeText(this, R.string.name_length_less_than_2, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.name_length_more_than_16, Toast.LENGTH_SHORT).show();
            }
            return false;
        }else if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(this, R.string.phone_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!VerifyUtils.checkPhoneNum(phoneNum)) {
            Toast.makeText(this, R.string.phone_error, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(verifyCode)) {
            Toast.makeText(this, R.string.verify_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this,R.string.pwd_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6 || password.length() > 16) { //pwd length >6 & < 16
            Toast.makeText(this, R.string.pwd_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showRegisterProtocol() {
        startActivity(new Intent(this, AgreementActivity.class));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_RE_SEND);
        mHandler = null;
    }

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mCurrVerifyTime > 0) {
                mGetRegVerifyTv.setEnabled(false);
                mCurrVerifyTime--;
                mHandler.sendEmptyMessageDelayed(MSG_RE_SEND, 1000);
                //mGetRegVerifyTv.setTextColor(getResources().getColor(R.color.font_gray));
                mGetRegVerifyTv.setText(Html.fromHtml(getString(R.string.verity_code_countdown,
                        mCurrVerifyTime)));
            } else {
                mGetRegVerifyTv.setEnabled(true);
                //mGetRegVerifyTv.setTextColor(getResources().getColor(R.color.font_blue));
                mGetRegVerifyTv.setText(R.string.verity_code_re_send);
            }
        }
    };
}
