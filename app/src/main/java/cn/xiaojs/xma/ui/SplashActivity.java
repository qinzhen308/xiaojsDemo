package cn.xiaojs.xma.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.ui.account.LoginActivity;
import cn.xiaojs.xma.ui.conversation2.ConversationDataProvider;
import cn.xiaojs.xma.util.CacheUtil;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AccountDataManager.isLogin(this)) {

            final ConversationDataProvider dataProvider = ConversationDataProvider.getProvider(this);
            dataProvider.addLoadstatusListener(new ConversationDataProvider.OnLoadstatusListener() {
                @Override
                public void onLoadComplete() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    if (dataProvider != null) {
                        dataProvider.removeLoadstatusListener(this);
                    }

                    finish();
                }
            });
            dataProvider.startLoad();


        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
