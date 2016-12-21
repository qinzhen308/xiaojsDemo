package cn.xiaojs.xma.data;


import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ErrorPrompts;
import cn.xiaojs.xma.util.SecurityUtil;
import com.orhanobut.logger.Logger;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class DataManager {


    public static String generateLessonKey() {

        String keyStr = "lesson"+System.currentTimeMillis();
        return SecurityUtil.md5(keyStr);

    }

    public static boolean checkSession(String session, APIServiceCallback callback) {
        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the session is empty,so the get home data request return failure");
            }

            if (callback != null){
                String errorMessage = ErrorPrompts.getErrorMessage(-1,Errors.BAD_SESSION);
                callback.onFailure(Errors.BAD_SESSION,errorMessage);
            }

            return true;
        }

        return false;
    }

}
