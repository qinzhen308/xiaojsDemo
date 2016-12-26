package cn.xiaojs.xma.data.db;

import android.content.Context;
import android.database.Cursor;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public abstract class BaseDao {

    public abstract Cursor getCursor(Context context);

}
