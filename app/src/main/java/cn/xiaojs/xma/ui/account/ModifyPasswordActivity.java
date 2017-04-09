package cn.xiaojs.xma.ui.account;

import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.preference.AccountPref;
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
 * Date:2017/3/17
 * Desc:
 *
 * ======================================================================================== */

public class ModifyPasswordActivity extends BaseActivity {
    @BindView(R.id.curr_pwd)
    EditTextDel mCurrentPwd;
    @BindView(R.id.new_pwd)
    EditTextDel mNewPwd;
    private boolean mPwdHidden = true;

    @Override
    protected void addViewContent() {
        setMiddleTitle(R.string.account_safe);
        addView(R.layout.activity_modify_pwd);

        mNewPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
    }

    @OnClick({R.id.left_view, R.id.hide_show_pwd, R.id.modify_pwd})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
                finish();
                break;
            case R.id.hide_show_pwd:
                hideOrShowPwd((ImageView) (v));
                break;
            case R.id.modify_pwd:
                modifyPassword();
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
        String currPwd = mCurrentPwd.getEditableText().toString();
        String newPwd = mNewPwd.getEditableText().toString();
        if (TextUtils.isEmpty(currPwd)) {
            Toast.makeText(this,R.string.current_pwd_hint, Toast.LENGTH_SHORT).show();
            return false;
        } else if (TextUtils.isEmpty(newPwd)) {
            Toast.makeText(this,R.string.new_pwd_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else if (newPwd.length() < 6 || newPwd.length() > 16) { //pwd length >6 & < 16
            Toast.makeText(this, R.string.pwd_error, Toast.LENGTH_SHORT).show();
            return false;
        } else if (!AccountPref.validateLoginMd5Pwd(this, currPwd)) {
            Toast.makeText(this,R.string.current_pwd_input_error, Toast.LENGTH_SHORT).show();
            mCurrentPwd.setText("");
            return false;
        }
        return true;
    }

    private void modifyPassword() {
        if(!checkSubmitInfo()) {
            return;
        }

        String newPwd = mNewPwd.getEditableText().toString();
        AccountDataManager.changePassword(this, newPwd, new APIServiceCallback() {

            @Override
            public void onSuccess(Object object) {
                Toast.makeText(ModifyPasswordActivity.this, R.string.modify_pwd_succ, Toast.LENGTH_SHORT).show();
                ModifyPasswordActivity.this.finish();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(ModifyPasswordActivity.this, R.string.modify_pwd_fail, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
