package cn.xiaojs.xma;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Settings;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.tencent.bugly.crashreport.CrashReport;

import cn.jpush.android.api.JPushInterface;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.ui.message.im.UserProvider;
import cn.xiaojs.xma.ui.message.im.chatkit.LCChatKit;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.XjsUtils;

/**
 * Created by maxiaobao on 2016/10/19.
 */

public class XiaojsApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        //日志，注意：所有日志都需要使用Logger，不得使用Log。
        Settings logSetting = Logger.init(XiaojsConfig.LOG_TAG)
                .methodCount(1)
                .hideThreadInfo()
                .methodOffset(0);

        if (!XiaojsConfig.DEBUG) {
            //发布Release版本时，需要将loglevel设为NONE。
            logSetting.logLevel(LogLevel.NONE);

        }

        //发布Release版本时，需要引入crash report
        XiaojsConfig.CHANNEL = APPUtils.getChannel(getApplicationContext());
        if (XiaojsConfig.DEBUG) {
            Logger.d("the channel:%s",XiaojsConfig.CHANNEL);
        }
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppChannel(XiaojsConfig.CHANNEL);
        CrashReport.initCrashReport(getApplicationContext(), XiaojsConfig.BUGLY_APP_ID, false, strategy);

        //init xiaojs utils
        XjsUtils.init(this);

        //初始化直播
        StreamingEnv.init(getApplicationContext());


        JPushInterface.setDebugMode(XiaojsConfig.DEBUG);
        JPushInterface.init(getApplicationContext());

        //lean cloud
        LCChatKit.getInstance().setProfileProvider(UserProvider.getUserProvider());
        AVOSCloud.setDebugLogEnabled(XiaojsConfig.DEBUG);
        LCChatKit.getInstance().init(getApplicationContext(),
                XiaojsConfig.LEANCLOUD_APPID,
                XiaojsConfig.LEANCLOUD_APPKEY);

        //init data cache
        DataManager.init(this);
    }

}
