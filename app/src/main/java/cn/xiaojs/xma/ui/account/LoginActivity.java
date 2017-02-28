package cn.xiaojs.xma.ui.account;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.security.AuthenticateStatus;
import cn.xiaojs.xma.model.security.LoginParams;
import cn.xiaojs.xma.ui.SplashActivity;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.VerifyUtils;
import cn.xiaojs.xma.util.XjsUtils;

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

public class LoginActivity extends BaseActivity {
    @BindView(R.id.reg_guide)
    TextView mRegGuide;
    @BindView(R.id.login_name)
    EditTextDel mLoginNamedEdit;
    @BindView(R.id.login_pwd)
    EditTextDel mLoginPwdEdit;
    @BindView(R.id.login_btn)
    Button mLoginBtn;

    private Context mContext;
    private boolean mPwdHidden = true;

    @Override
    protected void addViewContent() {
        mContext = this;
        addView(R.layout.activity_login);
        needHeader(false);

        setMiddleTitle(R.string.login);
        setLeftImage(-1);

        mLoginNamedEdit.setDelDrawable(R.drawable.ic_account_edit_text_del);
        mLoginPwdEdit.setDelDrawable(R.drawable.ic_account_edit_text_del);

        initRegGuideStyle();
        initLoginInfo();

        checkSession();
        changeBaseUrl();
    }

    /**
     * 修改baseUrl，测试专用
     */
    private void changeBaseUrl() {
        mContent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final CommonDialog commonDialog = new CommonDialog(LoginActivity.this);
                commonDialog.setTitle("修改服务器地址");
                commonDialog.show();
                final EditText edt = new EditText(LoginActivity.this);
                commonDialog.setCustomView(edt);
                edt.setText("http://");
                commonDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        commonDialog.dismiss();
                        XiaojsConfig.BASE_URL = edt.getText().toString();
                    }
                });
                commonDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        commonDialog.dismiss();
                    }
                });
                return false;
            }
        });
    }

    @OnClick({R.id.left_view, R.id.login_btn, R.id.hide_show_pwd})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
                finish();
                break;
            case R.id.hide_show_pwd:
                hideOrShowPwd((ImageView) v);
                break;
            case R.id.login_btn:
                submitLogin();
                break;
            default:
                break;
        }
    }

    private void initRegGuideStyle() {
        SpannableString spanString = new SpannableString(getString(R.string.register_guide));
        ForegroundColorSpan span = new ForegroundColorSpan(Color.argb(255, 222, 238, 255));
        spanString.setSpan(span, 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        spanString.setSpan(new ClickableSpan() {

            @Override
            public void onClick(View widget) {
                //enter register page
                startActivity(new Intent(mContext, RegisterActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getResources().getColor(R.color.white));
                ds.setFakeBoldText(true);
                ds.setUnderlineText(false);
            }
        }, 6, 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        mRegGuide.setText(spanString);
        mRegGuide.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void initLoginInfo() {
        long phoneNum = XjsUtils.getSharedPreferences().getLong(XiaojsConfig.KEY_LOGIN_USERNAME, 0);

        if (phoneNum != 0) {
            mLoginNamedEdit.setText(String.valueOf(phoneNum));
        }
    }

    /**
     * 隐藏或显示密码
     */
    private void hideOrShowPwd(ImageView v) {
        String str = mLoginPwdEdit.getText().toString().trim();
        if (mPwdHidden) {
            v.setImageDrawable(getResources().getDrawable(R.drawable.show_pwd));
            mLoginPwdEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mPwdHidden = false;
        } else {
            v.setImageDrawable(getResources().getDrawable(R.drawable.hide_pwd));
            mLoginPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mPwdHidden = true;
        }

        mLoginPwdEdit.setText(str);
        mLoginPwdEdit.setSelection(str.length());
    }

    private void submitLogin() {
        String userName = mLoginNamedEdit.getText().toString().trim();
        String password = mLoginPwdEdit.getText().toString().trim();

        if (!checkSubmitInfo(userName, password)) {
            return;
        }

        try {
            final LoginParams loginParams = new LoginParams();
            loginParams.setMobile(Long.parseLong(userName));
            loginParams.setPassword(password);
            showProgress(true);
            AccountBusiness.login(this, loginParams, new AccountBusiness.OnLoginListener() {
                @Override
                public void onLogin(boolean succ) {
                    if (succ) {
                        LoginActivity.this.finish();
                    }
                    cancelProgress();
                }
            });
        } catch (Exception e) {
            //do nothing
        }
    }

    /**
     * 提交登录时，检测输入信息
     *
     */
    private boolean checkSubmitInfo(String userName, String password) {
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, R.string.name_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!VerifyUtils.checkPhoneNum(userName)) {
            Toast.makeText(this, R.string.phone_error, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this,R.string.pwd_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void checkSession() {

        SecurityManager.checkSession(this, new APIServiceCallback<AuthenticateStatus>() {
            @Override
            public void onSuccess(AuthenticateStatus status) {

                String csrf;

                if (status == null || TextUtils.isEmpty(csrf = status.csrf)) {
                    return;
                }

                SecurityManager.saveCSRFToken(LoginActivity.this, csrf);
                AccountDataManager.saveSessionID(LoginActivity.this,status.sessionID);

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(LoginActivity.this,
                            errorCode + errorMessage,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }
}
