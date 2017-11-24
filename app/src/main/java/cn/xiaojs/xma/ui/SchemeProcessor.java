package cn.xiaojs.xma.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.ui.account.LoginActivity;
import cn.xiaojs.xma.ui.base.IntentFlags;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.recordlesson.RecordedLessonEnrollActivity;
import cn.xiaojs.xma.util.ClassStateUtil;
import cn.xiaojs.xma.util.ToastUtil;

/**
 * Created by Paul Z on 2017/10/18.
 */

public class SchemeProcessor {
    public static final String SCHEME="xiaojs";
    public static final String HOST="www.xiaojs.cn";
    public static final String PATH_CLASSROOM="/classroom";
    public static final String PATH_RECORDED_LESSON="/recorded";


    public static final String PARAMS_TICKET="ticket";
    public static final String PARAMS_ID="id";

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
//        Logger.d("-----qz-----path="+schemeUri.getPath()+"----");
        Logger.d("-----qz-----scheme="+schemeUri.toString());
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
                checkJoinClassStateAndEnterClassroom(ticket);
//                Classroom2Activity.invoke(mContext,ticket);
                break;
            case PATH_RECORDED_LESSON:
                String id=schemeUri.getQueryParameter(PARAMS_ID);
                if (TextUtils.isEmpty(id)) {
                    Toast.makeText(mContext,"参数缺失",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!AccountDataManager.isLogin(mContext)){
                    mContext.startActivity(new Intent(mContext, LoginActivity.class).setData(schemeUri));
                    mContext.finish();
                    return;
                }
                RecordedLessonEnrollActivity.invoke(mContext,id);
                break;
            default:
                break;
        }
    }

    private void checkJoinClassStateAndEnterClassroom(String ticket){
        ClassStateUtil.checkClassroomStateForMe(mContext, ticket, new ClassStateUtil.ClassStateCallback() {
            @Override
            public void onClassroomOpen(String ticket) {
                Classroom2Activity.invoke(mContext,ticket);
            }

            @Override
            public void onClassroomClose(String id) {
                String url= ApiManager.getShareLessonUrl(id, Account.TypeName.CLASS_LESSON);
                if(url.contains("?")){
                    url+="&app=android";
                }else {
                    url+="?app=android";
                }
                CommonWebActivity.invoke(mContext,"",url);
            }

            @Override
            public void onError(String msg) {
                ToastUtil.showToast(mContext,msg);
            }
        });
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
