package cn.xiaojs.xma.ui.account;

import android.content.Intent;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.widget.EditTextDel;

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
 * Date:2017/3/21
 * Desc:
 *
 * ======================================================================================== */

public class ForgetPasswordStepTwoActivity extends BaseActivity {
    @BindView(R.id.new_pwd)
    EditTextDel mNewPwd;
    private boolean mPwdHidden = true;

    public final static String KEY_MOBILE = "key_mobile";
    public final static String KEY_CODE = "key_code";
    private long mPhoneNum;
    private int mCode;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.set_new_pwd);
        addView(R.layout.activity_forget_pwd_step_two);

        mPhoneNum = getIntent().getLongExtra(KEY_MOBILE, 0);
        mCode = getIntent().getIntExtra(KEY_CODE, 0);

        mNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @OnClick({R.id.left_view, R.id.hide_show_pwd, R.id.set_pwd})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
                finish();
                break;
            case R.id.hide_show_pwd:
                hideOrShowPwd((ImageView) v);
                break;
            case R.id.set_pwd:
                setNewPwd();
                break;
            default:
                break;
        }
    }

    /**
     * 隐藏或显示密码
     */
    private void hideOrShowPwd(ImageView v) {
        String str = mNewPwd.getText().toString().trim();
        if (mPwdHidden) {
            v.setImageDrawable(getResources().getDrawable(R.drawable.show_pwd_gray));
            mNewPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mPwdHidden = false;
        } else {
            v.setImageDrawable(getResources().getDrawable(R.drawable.hide_pwd_gray));
            mNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mPwdHidden = true;
        }

        mNewPwd.setText(str);
        mNewPwd.setSelection(str.length());
    }

    private boolean checkSubmitInfo() {
        String newPwd = mNewPwd.getEditableText().toString();
        if (TextUtils.isEmpty(newPwd)) {
            Toast.makeText(this,R.string.new_pwd_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (newPwd.length() < 6 || newPwd.length() > 16) { //pwd length >6 & < 16
            Toast.makeText(this, R.string.pwd_error, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setNewPwd() {
        if (!checkSubmitInfo()) {
            return;
        }

        String newPwd = mNewPwd.getEditableText().toString();

        SecurityManager.resetPassword(this, mPhoneNum, newPwd, mCode, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(ForgetPasswordStepTwoActivity.this, R.string.set_new_pwd_succ, Toast.LENGTH_SHORT).show();
                finish();
                Intent intent = new Intent(ForgetPasswordStepTwoActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(ForgetPasswordStepTwoActivity.this, R.string.set_new_pwd_error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
