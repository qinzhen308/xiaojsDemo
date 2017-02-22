package cn.xiaojs.xma.data.db;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public abstract class BaseDao<T> {

    //public abstract Cursor getCursor(Context context);

    public abstract T loadData(Context context,int type, Object... params);
    public abstract void syncData(Context context,T entry);
}
