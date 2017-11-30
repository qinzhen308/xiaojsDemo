package cn.xiaojs.xma.ui.mine;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.preference.AccountPref;
import cn.xiaojs.xma.data.preference.ClassroomPref;
import cn.xiaojs.xma.ui.CommonWebActivity;
import cn.xiaojs.xma.ui.account.ForgetPasswordStepOneActivity;
import cn.xiaojs.xma.ui.account.LoginActivity;
import cn.xiaojs.xma.ui.account.ModifyPasswordActivity;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.APPUtils;

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
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

public class SettingsActivity extends BaseActivity {
    @BindView(R.id.exit_login)
    TextView mExitLoginBtn;
    //Button mExitLoginBtn;
    @BindView(R.id.data_cache)
    TextView mDataCacheTv;

    @BindView(R.id.xjs_info)
    TextView infoView;
    @BindView(R.id.account_safe)
    TextView accountSafe;


    @BindView(R.id.check_4g)
    CheckedTextView checked4GView;

    private Context mContext;

    @Override
    protected void addViewContent() {
        mContext = this;
        addView(R.layout.activity_settings);
        setMiddleTitle(R.string.settings);

        String versionName = APPUtils.getAPPVersionName(this);
        String infoStr = new StringBuilder(getResources()
                .getString(R.string.app_name))
                .append(" ")
                .append(versionName)
                .toString();

        infoView.setText(infoStr);

        mDataCacheTv.setText(DataManager.getLocalDataCache(this));
//        if(AccountPref.getThirdPartFlag(this)){//三方登录进来，隐藏掉
//            accountSafe.setVisibility(View.GONE);
//        }

        checked4GView.setChecked(ClassroomPref.allowLive4G(this));

    }

    @OnClick({R.id.left_view, R.id.exit_login, R.id.message_notify_set,
            R.id.account_safe, R.id.clear_cache_layout, R.id.feedback_help, R.id.about_us,R.id.btn_help,R.id.check_4g})
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
//                startActivity(new Intent(this, ModifyPasswordActivity.class));
                startActivity(new Intent(this, ForgetPasswordStepOneActivity.class).putExtra(ForgetPasswordStepOneActivity.EXTRA_IS_MODIFY,true));
                break;
            case R.id.clear_cache_layout:
                DataManager.clearbyUser(mContext);
                Toast.makeText(mContext, R.string.clear_cache_completed, Toast.LENGTH_SHORT).show();
                mDataCacheTv.setText("0KB");
                break;
            case R.id.feedback_help:
                break;
            case R.id.about_us:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.check_4g:
                boolean allow = ClassroomPref.allowLive4G(this);
                checked4GView.setChecked(!allow);
                ClassroomPref.setAllowLive4g(this, !allow);
                break;
            case R.id.btn_help:
                CommonWebActivity.invoke(this,"帮助", ApiManager.getHelpUrl(),true,true);
                break;
            default:
                break;
        }
    }

}
