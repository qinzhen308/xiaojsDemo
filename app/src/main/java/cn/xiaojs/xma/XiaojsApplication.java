package cn.xiaojs.xma;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.support.text.emoji.EmojiCompat;
import android.support.text.emoji.bundled.BundledEmojiCompatConfig;

import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.Settings;
import com.qiniu.pili.droid.streaming.StreamingEnv;
import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;

import cn.xiaojs.xma.data.XMSManager;
import cn.xiaojs.xma.data.api.socket.xms.SocketStatus;
import cn.xiaojs.xma.data.api.socket.xms.XMSObservable;
import cn.xiaojs.xma.data.preference.SecurityPref;
import cn.xiaojs.xma.ui.contact2.query.PinYin;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.ThirdLoginUtil;
import cn.xiaojs.xma.util.XjsUtils;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2016/10/19.
 */

public class XiaojsApplication extends Application {

    public static final String ACTION_NEW_MESSAGE = "xjs_lc_new_msg";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            MultiDex.install(this);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();

        EmojiCompat.Config emojiconfig = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(emojiconfig);

        ThirdLoginUtil.init();
        //日志，注意：所有日志都需要使用Logger，不得使用Log。
        Settings logSetting = Logger.init(XiaojsConfig.LOG_TAG)
                .methodCount(1)
                //.hideThreadInfo()
                .methodOffset(0);

        if (!XiaojsConfig.DEBUG) {
            //发布Release版本时，需要将loglevel设为NONE。
            logSetting.logLevel(LogLevel.NONE);

        }

        //发布Release版本时，需要引入crash report
        XiaojsConfig.CHANNEL = APPUtils.getChannel(getApplicationContext());
        if (XiaojsConfig.DEBUG) {
            Logger.d("the channel:%s", XiaojsConfig.CHANNEL);
        }
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(getApplicationContext());
        strategy.setAppChannel(XiaojsConfig.CHANNEL);

        String buglyAppId = APPUtils.getValueInApplicationInfo(this, "BUGLY_APPID");
        if (XiaojsConfig.DEBUG) {
            Logger.d("bugly appid is: %s", buglyAppId);
        }
        CrashReport.initCrashReport(getApplicationContext(), buglyAppId, false, strategy);

        //umeng release版本时，才加入统计
        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(getApplicationContext(),
                XiaojsConfig.UMENG_APPKEY,
                XiaojsConfig.CHANNEL,
                MobclickAgent.EScenarioType.E_UM_NORMAL,
                false);
        if (APPUtils.isProEvn()) {
            MobclickAgent.setDebugMode(false);
        } else {
            MobclickAgent.setDebugMode(true);
        }
        MobclickAgent.startWithConfigure(config);

        //init xiaojs utils
        XjsUtils.init(this);

        //初始化直播
        StreamingEnv.init(this);


        //jpush
        JPushInterface.setDebugMode(XiaojsConfig.DEBUG);
        JPushInterface.init(this);

        //init data cache and XMS
        DataManager.init(this);

        PinYin.init(this);
    }

    public Consumer<Integer> getXmsConsumer() {
        return xmsConsumer;
    }

    private Consumer<Integer> xmsConsumer = new Consumer<Integer>() {
        @Override
        public void accept(Integer status) throws Exception {

            if (XiaojsConfig.DEBUG) {
                Logger.d("XiaojsApplication received XMS status: %d", status);
            }

            switch (status) {
                case SocketStatus.CONNECT_BEGIN:                               //开始连接
                    break;
                case SocketStatus.CONNECTING:                                  //连接中
                    break;
                case SocketStatus.CONNECT_SUCCESS:                             //连接成功
                    DataManager.syncData(getApplicationContext());
                    break;
                case SocketStatus.CONNECT_FAILED:                              //连接失败
                    break;
                case SocketStatus.CONNECT_TIMEOUT:                             //连接超时
                    break;
                case SocketStatus.DISCONNECTED:                                //断开连接
                    break;
                case SocketStatus.RECONNECT:                                   //重新连接
                    break;
                case SocketStatus.RECONNECT_ARRIVED_MAX_COUNT:                 //重连次数达到上限
                    break;
            }
        }
    };


//    public boolean isMainAppPro() {
//        String info = getCurProcessName();
//        return (!TextUtils.isEmpty(info) && info.equals(getPackageName()));
//    }
//
//    boolean isLoadRunningAppProcessInfo;
//
//    public String getCurProcessName() {
//        try {
//            int pid = android.os.Process.myPid();
//            Object oo = getSystemService(Context.ACTIVITY_SERVICE);
//            if (oo == null) return null;
//            ActivityManager mActivityManager = (ActivityManager) oo;
//            if (oo == null) return null;
//            if (isLoadRunningAppProcessInfo) return null;
//            isLoadRunningAppProcessInfo = true;
//            java.util.List<android.app.ActivityManager.RunningAppProcessInfo> list = mActivityManager.getRunningAppProcesses();
//            isLoadRunningAppProcessInfo = false;
//            if (list == null) return null;
//            for (ActivityManager.RunningAppProcessInfo appProcess : list) {
//                if (appProcess.pid == pid) {
//                    return appProcess.processName;
//                }
//            }
//        } catch (Exception e) {
////            LogUtil.w(e);
//        }
//        return null;
//    }


}
