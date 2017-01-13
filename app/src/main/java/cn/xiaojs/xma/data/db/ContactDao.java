package cn.xiaojs.xma.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public class ContactDao extends BaseDao<ArrayList<ContactGroup>> {

//    @Override
//    public Cursor getCursor(Context context) {
//        SQLiteDatabase db = DBHelper.getReadDatabase(context);
//        return db.rawQuery("select * from " + DBTables.TContact.TABLE_NAME, null);
//    }


    @Override
    public ArrayList<ContactGroup> loadData(Context context, Object... params) {

        if (params != null && params.length >0){
            Map<Long, ContactGroup> map = (Map<Long, ContactGroup>) params[0];
            return getContacts(context,map);
        }

        return null;
    }

    @Override
    public void syncData(Context context, ArrayList<ContactGroup> entry) {
        clearContacts(context);
        addContact(context,entry);
    }

    /**
     * clear contacts and groups local data
     * @param context
     */
    public static void clear(Context context) {
        ContactDao dao = new ContactDao();
        dao.clearContacts(context);
        dao.clearGroups(context);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Contacs

    /**
     * clear the group tables
     * @param context
     */
    public void clearContacts(Context context) {

        SQLiteDatabase mDb = DBHelper.getWriteDb(context);
        mDb.delete(DBTables.TContact.TABLE_NAME, null, null);

    }

    /**
     * Add a contact
     * @param context
     * @param contact
     * @return
     */
    public long addGroup(Context context, long gid,Contact contact) {
        if (contact == null)
            return -1;

        SQLiteDatabase db = DBHelper.getWriteDb(context);
        ContentValues cv = new ContentValues();
        cv.put(DBTables.TContact.GID, gid);
        cv.put(DBTables.TContact.CID, contact.account);
        cv.put(DBTables.TContact.FOLLOW_TYPE, contact.followType);
        cv.put(DBTables.TGroup.NAME, contact.alias);
        cv.put(DBTables.TContact.AVATOR, contact.avatar);
        return db.insert(DBTables.TContact.TABLE_NAME, null, cv);

    }

    /**
     * delete a contact
     * @param context
     * @param id
     */
    public void deleteContact(Context context, String id) {

        SQLiteDatabase mDb = DBHelper.getWriteDb(context);

        String sql = "delete from " + DBTables.TContact.TABLE_NAME + " where "
                + DBTables.TContact.CID + "='" + id + "'";
        mDb.execSQL(sql);
    }

    /**
     * @param context
     * @return
     */
    public ArrayList<ContactGroup> getContacts(Context context,Map<Long, ContactGroup> map) {

        SQLiteDatabase db = DBHelper.getReadDatabase(context);
        ArrayList<ContactGroup> contactGroups = null;
        Cursor cursor = null;
        try{
            cursor = db.rawQuery("select * from " + DBTables.TContact.TABLE_NAME, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

                    long gid = cursor.getLong(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.GID));
                    String id = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.CID));
                    int followType = cursor.getInt(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.FOLLOW_TYPE));
                    String name = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.NAME));

                    String avatar = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.AVATOR));

                    Contact contact = new Contact();
                    contact.account = id;
                    contact.alias = name;
                    contact.followType = followType;
                    contact.avatar = avatar;

                    ContactGroup group = map.get(gid);
                    if (group != null) {
                        group.collection.add(contact);
                    }

                    cursor.moveToNext();
                }

                contactGroups = new ArrayList<>();
                for(ContactGroup group : map.values()) {
                    if (group.collection.size()>0) {
                        contactGroups.add(group);
                    }
                }

            }

        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (cursor !=null) {
                cursor.close();
            }
        }

        return contactGroups;
    }

    /**
     * Add contacts by batch
     * @param context
     * @param contactGroups
     */
    public void addContact(Context context, ArrayList<ContactGroup> contactGroups) {

        SQLiteDatabase db = DBHelper.getWriteDb(context);
        try {
            db.beginTransaction();
            StringBuilder sb = new StringBuilder("INSERT INTO ");
            sb.append(DBTables.TContact.TABLE_NAME);
            sb.append(" (");
            sb.append(DBTables.TContact.GID);
            sb.append(",");
            sb.append(DBTables.TContact.CID);
            sb.append(",");
            sb.append(DBTables.TContact.FOLLOW_TYPE);
            sb.append(",");
            sb.append(DBTables.TContact.NAME);
            sb.append(",");
            sb.append(DBTables.TContact.AVATOR);
            sb.append(") VALUES(?,?,?,?,?)");

            SQLiteStatement stmt = db.compileStatement(sb.toString());
            for (ContactGroup group : contactGroups) {

                for (Contact contact : group.collection) {
                    stmt.clearBindings();
                    stmt.bindLong(1, group.group);
                    stmt.bindString(2, contact.account);
                    stmt.bindLong(3, contact.followType);
                    stmt.bindString(4, contact.alias);

                    String url = contact.avatar;
                    String s = url != null? url :"";

                    stmt.bindString(5, s);
                    stmt.executeInsert();
                }

            }

            db.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // Contact group

    /**
     * clear the group tables
     * @param context
     */
    public void clearGroups(Context context) {

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
