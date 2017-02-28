package cn.xiaojs.xma;

import android.app.Application;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Settings;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.tencent.bugly.crashreport.CrashReport;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.im.android.api.JMessageClient;
import cn.xiaojs.xma.data.DataManager;
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
        String appid = "900060174";
        String channel = APPUtils.getChannel(getApplicationContext());
        if (XiaojsConfig.DEBUG) {
            Logger.d("the channel:%s",channel);
        }
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppChannel(channel);
        CrashReport.initCrashReport(getApplicationContext(), appid, true, strategy);

        //init data cache
        DataManager.init(this);

        //init xiaojs utils
        XjsUtils.init(this);

        //初始化直播
        StreamingEnv.init(getApplicationContext());

        //JMessage的debug模式
        JMessageClient.setDebugMode(XiaojsConfig.DEBUG);
        //初始化JMessage-sdk
        JMessageClient.init(getApplicationContext());
        //SharePreferenceManager.init(getApplicationContext(), JCHAT_CONFIGS);
        //设置Notification的模式
        JMessageClient.setNotificationMode(JMessageClient.NOTI_MODE_DEFAULT);
//        //注册Notification点击的接收器
//        new NotificationClickEventReceiver(getApplicationContext());

        JPushInterface.setDebugMode(XiaojsConfig.DEBUG);
        JPushInterface.init(getApplicationContext());
    }

}
