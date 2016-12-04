package com.benyuan.xiaojs.data;


import android.text.TextUtils;

import com.benyuan.xiaojs.XiaojsConfig;
import com.benyuan.xiaojs.common.xf_foundation.Errors;
import com.benyuan.xiaojs.common.xf_foundation.schemas.Security;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.data.api.service.APIType;
import com.benyuan.xiaojs.data.api.service.ErrorPrompts;
import com.benyuan.xiaojs.util.SecurityUtil;
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
