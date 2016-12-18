package com.benyuan.xiaojs.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.data.AccountDataManager;
import com.benyuan.xiaojs.ui.account.LoginActivity;
import com.benyuan.xiaojs.util.CacheUtil;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AccountDataManager.isLogin(this)) {
            XiaojsConfig.mLoginUser = CacheUtil.getLoginInfo();
            Intent intent = new Intent(this, MainActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else{
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();

    }
}
