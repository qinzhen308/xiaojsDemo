package cn.xiaojs.xma.data.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public class ContactDao extends BaseDao{

    @Override
    public Cursor getCursor(Context context) {
        SQLiteDatabase db = DBHelper.getReadDatabase(context);
        return db.rawQuery("select * from " + DBTables.TContact.TABLE_NAME, null);
    }

}
