package cn.xiaojs.xma.ui.account;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Keep;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.permissiongen.internal.PermissionUtil;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LoginDataManager;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.preference.DataPref;
import cn.xiaojs.xma.model.security.AuthenticateStatus;
import cn.xiaojs.xma.model.security.LoginParams;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.EditTextDel;
import cn.xiaojs.xma.util.QQUtil;
import cn.xiaojs.xma.util.VerifyUtils;

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

    private final int PERMISSION_CODE = 0x1;

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

    public final static int REQUEST_CODE_THIRD_LOGIN=123;

    @Override
    protected void addViewContent() {
        mContext = this;
        addView(R.layout.activity_login);
        needHeader(false);

        setMiddleTitle(R.string.login);
        setLeftImage(-1);

        mLoginNamedEdit.setDelDrawable(R.drawable.ic_account_edit_text_del);
        mLoginPwdEdit.setDelDrawable(R.drawable.ic_account_edit_text_del);

        mLoginPwdEdit.setTransformationMethod(PasswordTransformationMethod.getInstance());

        initRegGuideStyle();
        initLoginInfo();

        checkSession();

        if (XiaojsConfig.CHANNEL.equals(XiaojsConfig.CHANNEL_ENV_DEVTEST) && XiaojsConfig.SHOW_DEMO){
            changeBaseUrl();
        }

        String[] needPermissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE
        };

        if (PermissionUtil.isOverMarshmallow()){
            PermissionGen.needPermission(this,PERMISSION_CODE,needPermissions);
        }else{
            LoginDataManager.requestLocation(getApplicationContext());
        }

    }

    @Keep
    @PermissionSuccess(requestCode = PERMISSION_CODE)
    public void accessSuccess() {
        LoginDataManager.requestLocation(getApplicationContext());
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
                LinearLayout layout = new LinearLayout(LoginActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                final EditText urlEdt = new EditText(LoginActivity.this);
                final EditText xasPort = new EditText(LoginActivity.this);
                final EditText xlsPort = new EditText(LoginActivity.this);
                layout.addView(urlEdt);
                layout.addView(xasPort);
                layout.addView(xlsPort);

                commonDialog.setCustomView(layout);
                urlEdt.setHint("请输入服务器地址");
                xasPort.setHint("请输入XAS端口");
                xlsPort.setHint("请输入XLS端口");
                urlEdt.setText("http://");
                commonDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        commonDialog.dismiss();
                        String baseurl = urlEdt.getText().toString();
                        String aPort = xasPort.getText().toString();
                        String lPort = xlsPort.getText().toString();

                        DataPref.setServerIP(LoginActivity.this, baseurl);
                        DataPref.setXASPort(LoginActivity.this, aPort);
                        DataPref.setXLSPort(LoginActivity.this, lPort);

                        //切换地址后，清空token 和 session
                        SecurityManager.saveCSRFToken(LoginActivity.this, "");
                        AccountDataManager.saveSessionID(LoginActivity.this,"");

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

    @OnClick({R.id.left_view, R.id.login_btn, R.id.hide_show_pwd, R.id.forget_pwd,R.id.login_wx,R.id.login_qq})
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
            case R.id.forget_pwd:
                startActivity(new Intent(this, ForgetPasswordStepOneActivity.class));
                break;
            case R.id.login_wx:
                thirdLogin(SHARE_MEDIA.WEIXIN);
                break;
            case R.id.login_qq:
                thirdLogin(SHARE_MEDIA.QQ);
//                QQUtil.getTencent(this).login(this, "get_simple_userinfo", new IUiListener() {
//                    @Override
//                    public void onComplete(Object o) {
//                        Toast.makeText( getApplicationContext(), "qq onComplete", Toast.LENGTH_SHORT).show();
//                    }
//
//                    @Override
//                    public void onError(UiError uiError) {
//                        Toast.makeText( getApplicationContext(), "qq "+uiError.toString(), Toast.LENGTH_SHORT).show();
//
//                    }
//
//                    @Override
//                    public void onCancel() {
//                        Toast.makeText( getApplicationContext(), "qq cancel", Toast.LENGTH_SHORT).show();
//                    }
//                });
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
        String phoneNum = AccountDataManager.getPhone(this);

        if (!TextUtils.isEmpty(phoneNum)) {
            mLoginNamedEdit.setText(phoneNum);
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
            AccountBusiness.login(this, false, loginParams, new AccountBusiness.OnLoginListener() {
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
            Toast.makeText(this, R.string.phone_empty, Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    //----------------三方登录------------
    private void thirdLogin(final SHARE_MEDIA platform){
        UMShareAPI.get(this).getPlatformInfo(this, platform ,umAuthListener);
    }

    private UMAuthListener umAuthListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            //授权开始的回调
        }
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            Toast.makeText(getApplicationContext(), "Authorize succeed", Toast.LENGTH_SHORT).show();
            startActivityForResult(new Intent(LoginActivity.this,BindThirdAccountActivity.class),REQUEST_CODE_THIRD_LOGIN);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText( getApplicationContext(), "Authorize fail", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText( getApplicationContext(), "Authorize cancel", Toast.LENGTH_SHORT).show();
        }
    };
}
