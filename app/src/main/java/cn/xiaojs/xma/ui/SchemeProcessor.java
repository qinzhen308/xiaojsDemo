package cn.xiaojs.xma.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import cn.xiaojs.xma.ui.base.IntentFlags;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;

/**
 * Created by Paul Z on 2017/10/18.
 */

public class SchemeProcessor {
    public static final String SCHEME="xjs";
    public static final String HOST="www.xjs.cn";
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
                Intent i = new Intent();
                i.putExtra(Constants.KEY_TICKET, ticket);
                i.setClass(mContext, ClassroomActivity.class);
                mContext.startActivity(i);
                break;
            default:
                break;
        }

    }
}
