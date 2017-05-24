package cn.xiaojs.xma.util;

import com.umeng.socialize.PlatformConfig;

import cn.xiaojs.xma.XiaojsConfig;

/**
 * Created by Paul Z on 2017/5/18.
 */

public class ThirdLoginUtil {

    public static void init(){
        PlatformConfig.setWeixin(XiaojsConfig.WX_APP_ID,XiaojsConfig.WX_APP_KEY);
        PlatformConfig.setQQZone(XiaojsConfig.QQ_APP_ID,XiaojsConfig.QQ_APP_KEY);
    }


}
