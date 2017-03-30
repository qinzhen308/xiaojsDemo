package cn.xiaojs.xma.data.loader;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.PlatformManager;
import cn.xiaojs.xma.data.UpgradeManager;
import cn.xiaojs.xma.model.Upgrade;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.NetUtil;

public class UpgradeService extends IntentService {

    public UpgradeService() {
        super("UpgradeService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("UpgradeService check begin ...");
        }

        //检测升级
        try {
            Context context = getApplicationContext();

            if (NetUtil.getCurrentNetwork(context) == NetUtil.NETWORK_NONE) {
                return;
            }

            Upgrade upgrade = PlatformManager.checkUpgrade(context);
            if (upgrade != null && !TextUtils.isEmpty(upgrade.verStr)) {

                int newBuildCode = APPUtils.parseBulidCode(context,upgrade.verStr);

                Upgrade oldInfo = UpgradeManager.getUpgrade(context);

                if (newBuildCode <= 0){
                    return;
                }

                if (oldInfo != null && !TextUtils.isEmpty(oldInfo.verStr)){
                    int oldBuildCode = APPUtils.parseBulidCode(context,oldInfo.verStr);

                    if (XiaojsConfig.DEBUG) {
                        Logger.d("UpgradeService check old save code:"
                                + oldBuildCode
                                + ", the new code:"
                                + newBuildCode);
                    }

                    if (oldBuildCode >= newBuildCode) {
                        return;
                    }
                }

                UpgradeManager.setUpgrade(context, upgrade);
                //通知弹出更新提示
                sendBroadcast(new Intent(UpgradeManager.ACTION_SHOW_UPGRADE));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (XiaojsConfig.DEBUG) {
                Logger.d("UpgradeService check end ...");
            }
        }

    }
}
