package com.benyuan.xiaojs;

import android.app.Application;

import com.benyuan.xiaojs.util.XjsUtils;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Settings;

/**
 * Created by maxiaobao on 2016/10/19.
 */

public class XiaojsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        //日志，注意：所有日志都需要使用Logger，不得使用Log。
        Settings logSetting = Logger.init(XiaojsConfig.LOG_TAG)
                .methodCount(3)
                .methodOffset(0);

        //发布Release版本时，需要将loglevel设为NONE。
        if (!XiaojsConfig.DEBUG) {
            logSetting.logLevel(LogLevel.NONE);
        }

        //init xiaojs utils
        XjsUtils.init(this);
    }

}
