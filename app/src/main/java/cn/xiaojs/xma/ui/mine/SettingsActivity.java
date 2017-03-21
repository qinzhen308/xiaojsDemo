package cn.xiaojs.xma.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.LoginDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.ui.account.LoginActivity;
import cn.xiaojs.xma.ui.account.ModifyPasswordActivity;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.CacheUtil;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.util.JpushUtil;

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

    @OnClick({R.id.left_view, R.id.exit_login, R.id.message_notify_set,
            R.id.account_safe, R.id.clear_cache_layout, R.id.feedback_help, R.id.about_us})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_view:
                finish();
                break;
            case R.id.exit_login:
                APPUtils.exitAndLogin(mContext,R.string.exit_login);
                break;
            case R.id.message_notify_set:
                break;
            case R.id.account_safe:
                startActivity(new Intent(this, ModifyPasswordActivity.class));
                break;
            case R.id.clear_cache_layout:
                DataManager.clearbyUser(mContext);
                Toast.makeText(mContext, R.string.clear_cache_completed, Toast.LENGTH_SHORT).show();
                break;
            case R.id.feedback_help:
                break;
            case R.id.about_us:
                break;
            default:
                break;
        }
    }
}
