package com.benyuan.xiaojs.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.data.LoginDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.ui.account.LoginActivity;
import com.benyuan.xiaojs.ui.base.BaseActivity;

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

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.exit_login)
    Button mExitLoginBtn;

    private Context mContext;

    @Override
    protected void addViewContent() {
        mContext = this;
        addView(R.layout.activity_settings);
        setMiddleTitle(R.string.settings);
    }

    @OnClick({R.id.left_view, R.id.exit_login})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
                finish();
                break;
            case R.id.exit_login:
                exitLogin();
                break;
            default:
                break;
        }
    }

    private void exitLogin() {
        String sessionId = XiaojsConfig.mLoginUser.getSessionID();
        LoginDataManager.requestLogoutByAPI(this, sessionId, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                Toast.makeText(mContext, "账户已经退出", Toast.LENGTH_SHORT).show();
                XiaojsConfig.mLoginUser = null;

                //jump login page
                Intent i = new Intent(mContext, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
