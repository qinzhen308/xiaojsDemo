package cn.xiaojs.xma.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.ui.account.LoginActivity;
import cn.xiaojs.xma.ui.base.IntentFlags;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;

/**
 * Created by Paul Z on 2017/10/18.
 */

public class SchemeProcessor {
    public static final String SCHEME="xiaojs";
    public static final String HOST="www.xiaojs.cn";
    public static final String PATH_CLASSROOM="/classroom";


    public static final String PARAMS_TICKET="ticket";

    private Activity mContext;

    public SchemeProcessor(Activity activity){
        mContext=activity;
    }

    public void handle(Intent intent){
        if(intent==null)return;
        Uri schemeUri=intent.getData();
        if(schemeUri==null||!SCHEME.equals(schemeUri.getScheme())||!HOST.equals(schemeUri.getHost())){
            return;
        }
        String path=schemeUri.getPath();
        switch (path){
            case PATH_CLASSROOM:
                String ticket=schemeUri.getQueryParameter(PARAMS_TICKET);
                if (TextUtils.isEmpty(ticket)) {
                    Toast.makeText(mContext,"进入教室失败,无效的ticket",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!AccountDataManager.isLogin(mContext)){
                    mContext.startActivity(new Intent(mContext, LoginActivity.class).setData(schemeUri));
                    mContext.finish();
                    return;
                }
                Classroom2Activity.invoke(mContext,ticket);
                break;
            default:
                break;
        }

    }


    public static boolean isSchemeUri(Uri uri){
        if(uri==null||!SCHEME.equals(uri.getScheme())||!HOST.equals(uri.getHost())){
            return false;
        }
        return true;
    }
    public static boolean isSchemeUri(Intent intent){
        return isSchemeUri(intent.getData());
    }

}
