package cn.xiaojs.xma.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.xiaojs.xma.model.social.ContactGroup;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public class ContactDao extends BaseDao {

    @Override
    public Cursor getCursor(Context context) {
        SQLiteDatabase db = DBHelper.getReadDatabase(context);
        return db.rawQuery("select * from " + DBTables.TContact.TABLE_NAME, null);
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Contact group

    /**
     * clear the group tables
     * @param context
     */
    public void clearGroup(Context context) {

        SQLiteDatabase mDb = DBHelper.getInstance(context)
                .getWritableDatabase();
        mDb.delete(DBTables.TGroup.TABLE_NAME, null, null);

    }

    /**
     * Add contact's group
     * @param context
     * @param group
     * @return
     */
    public long addGroup(Context context, ContactGroup group) {
        if (group == null)
            return -1;

        SQLiteDatabase db = DBHelper.getWriteDb(context);
        ContentValues cv = new ContentValues();
        cv.put(DBTables.TGroup.GID, group.group);
        cv.put(DBTables.TGroup.NAME, group.name);
        return db.insert(DBTables.TGroup.TABLE_NAME, null, cv);

    }

    /**
     * Add contact's group by batch
     * @param context
     * @param groups
     */
    public void addGroup(Context context, ContactGroup[] groups) {

        SQLiteDatabase db = DBHelper.getWriteDb(context);
        try {
            db.beginTransaction();
            StringBuilder sb = new StringBuilder("INSERT INTO ");
            sb.append(DBTables.TGroup.TABLE_NAME);
            sb.append(" (");
            sb.append(DBTables.TGroup.GID);
            sb.append(",");
            sb.append(DBTables.TGroup.NAME);
            sb.append(") VALUES(?,?)");

            SQLiteStatement stmt = db.compileStatement(sb.toString());
            for (ContactGroup group : groups) {

                stmt.clearBindings();
                stmt.bindLong(1, group.group);
                stmt.bindString(2, group.name);

                stmt.executeInsert();

            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Get contact group
     * @param context
     * @return
     */
    public Map<Long, ContactGroup> getGroups(Context context) {
        SQLiteDatabase db = DBHelper.getReadDatabase(context);
        Map<Long,ContactGroup> groups = null;
        Cursor cursor = null;
        try{
            cursor = db.rawQuery("select * from " + DBTables.TGroup.TABLE_NAME, null);
            if (cursor != null && cursor.getCount() > 0) {
                groups = new HashMap<>();
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

                    long gid = cursor.getLong(cursor
                            .getColumnIndexOrThrow(DBTables.TGroup.GID));
                    String name = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBTables.TGroup.NAME));

                    ContactGroup contactGroup = new ContactGroup();
                    contactGroup.name = name;
                    contactGroup.group = Long.valueOf(gid);
                    contactGroup.collection = new ArrayList<>(0);

                    groups.put(gid,contactGroup);

                    cursor.moveToNext();
                }
            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor !=null) {
                cursor.close();
            }
        }

        return groups;
    }
}
