package com.benyuan.xiaojs.data;


import com.benyuan.xiaojs.common.xf_foundation.schemas.Security;
import com.benyuan.xiaojs.util.SecurityUtil;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class DataManager {


    public static String generateLessonKey() {

        String keyStr = "lesson"+System.currentTimeMillis();
        return SecurityUtil.md5(keyStr);

    }

}
