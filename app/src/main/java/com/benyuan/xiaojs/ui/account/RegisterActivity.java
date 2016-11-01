package com.benyuan.xiaojs.ui.account;

import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.util.VerifyUtils;
import com.benyuan.xiaojs.util.XjsUtils;

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
    private final static int MSG_RE_SEND = 0;
    @BindView(R.id.reg_name)
    EditText mRegNameEdit;
    @BindView(R.id.reg_phone)
    EditText mRegPhoneEdit;
    @BindView(R.id.verify_code)
    EditText mRegVerifyEdit;
    @BindView(R.id.get_verify_code)
    EditText mGetRegVerifyEdit;
    @BindView(R.id.reg_pwd)
    EditText mRegPwdEdit;

    private int mCurrVerifyTime = 0;
    private boolean mPwdHidden = true;

    @Override
    protected void addViewContent() {

        addView(R.layout.activity_register);
        setMiddleTitle(R.string.register);
    }

    @OnClick({R.id.left_view, R.id.get_verify_code, R.id.hide_show_pwd,
            R.id.register_btn, R.id.register_protocol})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
                finish();
                break;
            case R.id.get_verify_code:
                getVerifyCode();
                break;
            case R.id.register_btn:
                submitReg();
                break;
            case R.id.hide_show_pwd:
                hideOrShowPwd((ImageView) v);
                break;
            case R.id.register_protocol:
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
            //v.setImageDrawable(getResources().getDrawable(R.drawable.show_pw_selector));
            mRegPwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mPwdHidden = false;
        } else {
            //v.setImageDrawable(getResources().getDrawable(R.drawable.hide_pw_selector));
            mRegPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mPwdHidden = true;
        }

        mRegPwdEdit.setText(str);
        mRegPwdEdit.setSelection(str.length());
    }

    private void getVerifyCode() {

        XjsUtils.hideIMM(this, getCurrentFocus().getWindowToken());
    }

    private void submitReg() {
        final String phoneNum = mRegPhoneEdit.getText().toString().trim();
        final String password = mRegPwdEdit.getText().toString().trim();
        final String verifyCode = mRegVerifyEdit.getText().toString().trim();

        if (!checkSubmitInfo()) {
            return;
        }
    }

    /**
     * 提交注册时，检测输入信息
     *
     * @return
     */
    private boolean checkSubmitInfo() {
        final String phoneNum = mRegPhoneEdit.getText().toString().trim();
        final String password = mRegPwdEdit.getText().toString().trim();
        final String verifyCode = mRegVerifyEdit.getText().toString().trim();

        if (TextUtils.isEmpty(phoneNum)) { // 电话号码是否为空
            Toast.makeText(this, R.string.phone_input_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!VerifyUtils.checkPhoneNum(phoneNum)) { // 电话号码非法
            Toast.makeText(this, R.string.phone_input_error, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(verifyCode)) { // 验证码为空
            Toast.makeText(this, R.string.phone_verify_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(password)) { // 密码是否为空
            Toast.makeText(this,R.string.phone_pwd_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 6 || password.length() > 16) { // 密码是否符合6-16位字符的要求
            Toast.makeText(this, R.string.phone_pwd_error, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void showRegisterProtocol() {

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
                mGetRegVerifyEdit.setEnabled(false);
                mCurrVerifyTime--;
                mHandler.sendEmptyMessageDelayed(MSG_RE_SEND, 1000);

                mGetRegVerifyEdit.setText(Html.fromHtml(getString(R.string.verity_code_countdown,
                        mCurrVerifyTime)));
            } else {
                mGetRegVerifyEdit.setEnabled(true);
                mGetRegVerifyEdit.setTextColor(getResources().getColor(R.color.disable_btn));
                mGetRegVerifyEdit.setText(R.string.verity_code_re_send);
            }
        }
    };
}
