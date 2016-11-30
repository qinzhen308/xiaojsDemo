package com.benyuan.xiaojs;

import android.app.Application;

import com.benyuan.xiaojs.util.XjsUtils;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Settings;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.tencent.bugly.crashreport.CrashReport;

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


        if (!XiaojsConfig.DEBUG) {
            //发布Release版本时，需要将loglevel设为NONE。
            logSetting.logLevel(LogLevel.NONE);

        }

        //发布Release版本时，需要引入crash report
        String appid = "900060174";
        CrashReport.initCrashReport(getApplicationContext(), appid, false);

        //init xiaojs utils
        XjsUtils.init(this);

        //初始化直播
        StreamingEnv.init(getApplicationContext());
    }

}
