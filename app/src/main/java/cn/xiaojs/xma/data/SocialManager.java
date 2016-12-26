package cn.xiaojs.xma.data;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.app.FragmentActivity;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.db.ContactDao;
import cn.xiaojs.xma.data.db.CursorTaskLoader;
import cn.xiaojs.xma.data.db.LoadDataCallback;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public class SocialManager extends DataManager {

    public static void getContacts(final Context context, final LoadDataCallback<Cursor> callback) {

        if (!(context instanceof FragmentActivity)) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the context is not FragmentActivity,so do nothing and return");
            }
            return;
        }

        CursorTaskLoader taskLoader = new CursorTaskLoader(context, new ContactDao());
        DataLoader<Cursor> dataLoader = new DataLoader<>(taskLoader, callback);
        ((FragmentActivity) context).getSupportLoaderManager().initLoader(0, null, dataLoader);

    }
}
