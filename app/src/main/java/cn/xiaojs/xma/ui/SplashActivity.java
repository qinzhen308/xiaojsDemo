package cn.xiaojs.xma.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cn.xiaojs.xma.XiaojsApplication;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.preference.DataPref;
import cn.xiaojs.xma.ui.account.LoginActivity;
import cn.xiaojs.xma.util.APPUtils;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(DataPref.getVersionCode(this)<APPUtils.getAPPVersionCode(this)){
//        if(true){
            startActivity(new Intent(this, GuideActivity.class));
            finish();
        }else {
            if (AccountDataManager.isLogin(this)) {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
