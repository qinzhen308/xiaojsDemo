package cn.xiaojs.xma.data;

import android.content.Context;

import cn.xiaojs.xma.data.api.PlatformRequest;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.Upgrade;

/**
 * Created by maxiaobao on 2017/3/21.
 */

public class PlatformManager {

    public static void checkUpgrade(Context context, APIServiceCallback<Upgrade> callback) {
        PlatformRequest request = new PlatformRequest(context,callback);
        request.checkUpgrade();
    }


    public static Upgrade checkUpgrade(Context context) throws Exception{
        PlatformRequest request = new PlatformRequest(context, null);
        return request.checkUpgradeSync();
    }
}
