package cn.xiaojs.xma.data;


import android.content.Context;
import android.text.TextUtils;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.service.ErrorPrompts;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.SecurityUtil;
import com.orhanobut.logger.Logger;

import java.io.File;

/**
 * Created by maxiaobao on 2016/10/25.
 */

public class DataManager{


    //    public final void initLoader(Context context, LoaderManager.LoaderCallbacks<Cursor> callbacks) {
//        if (context instanceof FragmentActivity) {
//
//            FragmentActivity fragmentActivity = (FragmentActivity) context;
//
//            ((FragmentActivity)context).getLoaderManager().initLoader(0,null,callbacks);
//        }
//
//    }


    public static String generateLessonKey() {

        String keyStr = "lesson"+System.currentTimeMillis();
        return SecurityUtil.md5(keyStr);

    }

    public static boolean checkSession(String session, APIServiceCallback callback) {
        if (TextUtils.isEmpty(session)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the session is empty,so the request return failure");
            }

            if (callback != null){
                String errorMessage = ErrorPrompts.getErrorMessage(-1,Errors.BAD_SESSION);
                callback.onFailure(Errors.BAD_SESSION,errorMessage);
            }

            return true;
        }

        return false;
    }

    /**
     * clear cache data which create by api service
     * @param context
     */
    public static void clearAPICache(Context context) {

        File cacheDir = new File(context.getCacheDir(),XiaojsConfig.HTTP_CACHE_DIR);
        FileUtil.clearDirFiles(cacheDir);

    }

}
