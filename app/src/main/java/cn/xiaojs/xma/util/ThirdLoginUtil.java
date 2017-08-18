package cn.xiaojs.xma.util;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.common.xf_foundation.schemas.Security;

/**
 * Created by Paul Z on 2017/5/18.
 */

public class ThirdLoginUtil {

    public final static String KEY_UID="uid";
    public final static String KEY_UNIONID="unionid";//qq和微信多应用打通，必须用unionid
    public final static String KEY_NAME="name";
    public final static String KEY_GENDER="gender";
    public final static String KEY_ICONURL="iconurl";


    public final static String VALUE_SEX_MAN="男";
    public final static String VALUE_SEX_MALE="女";

    public static void init(){
        PlatformConfig.setWeixin(XiaojsConfig.WX_APP_ID,XiaojsConfig.WX_APP_KEY);
        PlatformConfig.setQQZone(XiaojsConfig.QQ_APP_ID,XiaojsConfig.QQ_APP_KEY);
    }


    public static String getUId(Map<String,String> data, SHARE_MEDIA type){
//        return data.get(KEY_UID);
        return data.get(KEY_UNIONID);
    }
    public static String getName(Map<String,String> data, SHARE_MEDIA type){
        return data.get(KEY_NAME);
    }

    public static String getSex(Map<String,String> data, SHARE_MEDIA type){
        String sex=data.get(KEY_GENDER);
        return String.valueOf(VALUE_SEX_MAN.equals(sex));
    }
    public static String getAvatar(Map<String,String> data, SHARE_MEDIA type){
        return data.get(KEY_ICONURL);
    }

    public static String getLoginTypeInApi( SHARE_MEDIA type){
        String ea="";
        if(type==SHARE_MEDIA.QQ){
            ea=String.valueOf(Platform.ExternalApp.QQ);
        }else if(type==SHARE_MEDIA.WEIXIN){
            ea=String.valueOf(Platform.ExternalApp.WECHAT);
        }
        return ea;
    }
    public static int getCaptchaTypeInApi( SHARE_MEDIA type){
        int code_type=0;
        if(type==SHARE_MEDIA.WEIXIN){
            code_type=Security.VerifyMethod.SMS4WeChatAssociation;
        }else if(type==SHARE_MEDIA.QQ){
            code_type=Security.VerifyMethod.SMS4QQAssociation;
        }
        return code_type;
    }

    public static int getCaptchaTypeInApi( String ea){
        int code_type=0;
        int type=Integer.valueOf(ea);
        if(type==Platform.ExternalApp.WECHAT){
            code_type=Security.VerifyMethod.SMS4WeChatAssociation;
        }else if(type==Platform.ExternalApp.QQ){
            code_type=Security.VerifyMethod.SMS4QQAssociation;
        }
        return code_type;
    }


    public static boolean checkInstalled(Activity context, SHARE_MEDIA platform){
        boolean isInstalled=UMShareAPI.get(context).isInstall(context,platform);
        if(!isInstalled){
            String platformSrt="";
            if(platform==SHARE_MEDIA.QQ){
                platformSrt="QQ";
            }else if(platform==SHARE_MEDIA.WEIXIN){
                platformSrt="微信";
            }
            Toast.makeText(context,"您还没有安装"+platformSrt,Toast.LENGTH_LONG).show();
        }
        return isInstalled;
    }

}
