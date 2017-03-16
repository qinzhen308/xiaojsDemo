package cn.xiaojs.xma.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.ContactGroup;

import static cn.xiaojs.xma.common.xf_foundation.schemas.Social.ContactGroup.CLASSES;

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
    public ArrayList<ContactGroup> loadData(Context context, int ptype,Object... params) {

        if (params != null && params.length >0){
            if (params[0] instanceof Map) {
                Map<Long, ContactGroup> map = (Map<Long, ContactGroup>) params[0];
                if (ptype == -2) {
                    return getContactsOnly(context,map);
                }else{
                    return getContacts(context, map);
                }

            }else if (params[0] instanceof Integer) {
                int type = (int)params[0];
                if (type == -1){
                    return getClasses(context);
                }else {
                    return getContacts(context, type);
                }

            }
        }

        return null;
    }

    @Override
    public void syncData(Context context, ArrayList<ContactGroup> entry) {
//        clearContacts(context);
//        addContact(context,entry);
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

    public ArrayList<ContactGroup> getClasses(Context context) {

        SQLiteDatabase db = DBHelper.getReadDatabase(context);
        ArrayList<ContactGroup> contactGroups = null;
        Cursor cursor = null;
        try{

            String sql = new StringBuilder("select * from ")
                    .append(DBTables.TContact.TABLE_NAME)
                    .append(" where ")
                    .append(DBTables.TContact.GID)
                    .append(" = '")
                    .append(CLASSES)
                    .append("'")
                    .toString();

            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                ArrayList<Contact> contacts = new ArrayList<>(cursor.getCount());
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

//                    long gid = cursor.getLong(cursor
//                            .getColumnIndexOrThrow(DBTables.TContact.GID));
                    String id = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.CID));
//                    int followType = cursor.getInt(cursor
//                            .getColumnIndexOrThrow(DBTables.TContact.FOLLOW_TYPE));
                    String name = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.NAME));
                    String subtype = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.SUBTYPE));

//                    String avatar = cursor.getString(cursor
//                            .getColumnIndexOrThrow(DBTables.TContact.AVATOR));

                    Contact contact = new Contact();
                    contact.account = id;
                    contact.alias = name;
                    contact.subtype = subtype;

                    contacts.add(contact);

                    cursor.moveToNext();
                }

                ContactGroup contactGroup = new ContactGroup();
                contactGroup.collection = contacts;

                contactGroups = new ArrayList<>(1);
                contactGroups.add(contactGroup);
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
     * 获取联系人的ID集合
     * @param context
     * @return
     */
    public Set<String> getContactIds(Context context) {
        SQLiteDatabase db = DBHelper.getReadDatabase(context);
        Set<String> ids = null;
        Cursor cursor = null;

        try {
            String sql = new StringBuilder("select ")
                    .append(DBTables.TContact.CID)
                    .append(" from ")
                    .append(DBTables.TContact.TABLE_NAME)
                    .append(" where ")
                    .append(DBTables.TContact.FOLLOW_TYPE)
                    .append(" = '")
                    .append(CLASSES)
                    .append("'")
                    .toString();

            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                ids = new HashSet<>(cursor.getCount());
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    String id = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.CID));
                    ids.add(id);
                }
            }

        }catch (Exception e) {
             e.printStackTrace();
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return ids;
    }

    /**
     * @param context
     * @return
     */
    public ArrayList<ContactGroup> getContacts(Context context, int followType) {

        SQLiteDatabase db = DBHelper.getReadDatabase(context);
        ArrayList<ContactGroup> contactGroups = null;
        Cursor cursor = null;
        try{

            String sql = new StringBuilder("select * from ")
                    .append(DBTables.TContact.TABLE_NAME)
                    .append(" where ")
                    .append(DBTables.TContact.FOLLOW_TYPE)
                    .append(" = '")
                    .append(followType)
                    .append("' and ")
                    .append(DBTables.TContact.GID)
                    .append(" <> '")
                    .append(CLASSES)
                    .append("'")
                    .toString();

            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                ArrayList<Contact> contacts = new ArrayList<>(cursor.getCount());
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {

//                    long gid = cursor.getLong(cursor
//                            .getColumnIndexOrThrow(DBTables.TContact.GID));
                    String id = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.CID));
//                    int followType = cursor.getInt(cursor
//                            .getColumnIndexOrThrow(DBTables.TContact.FOLLOW_TYPE));
                    String name = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.NAME));

                    String avatar = cursor.getString(cursor
                            .getColumnIndexOrThrow(DBTables.TContact.AVATOR));

                    Contact contact = new Contact();
                    contact.account = id;
                    contact.alias = name;
                    contact.followType = followType;
                    contact.avatar = avatar;

                    contacts.add(contact);

                    cursor.moveToNext();
                }

                ContactGroup contactGroup = new ContactGroup();
                contactGroup.collection = contacts;

                contactGroups = new ArrayList<>(1);
                contactGroups.add(contactGroup);
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
     * @param context
     * @return
     */
    public ArrayList<ContactGroup> getContactsOnly(Context context,Map<Long, ContactGroup> map) {

        SQLiteDatabase db = DBHelper.getReadDatabase(context);
        ArrayList<ContactGroup> contactGroups = null;
        Cursor cursor = null;
        try{

            String sql = new StringBuilder("select * from ")
                    .append(DBTables.TContact.TABLE_NAME)
                    .append(" where ")
                    .append(DBTables.TContact.GID)
                    .append(" <> '")
                    .append(CLASSES)
                    .append("'")
                    .toString();

            cursor = db.rawQuery(sql, null);
            if (cursor != null && cursor.getCount() > 0) {
                Map<Long, ContactGroup> temp = new HashMap<>();
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

                    boolean isclass = gid == CLASSES;

                    ContactGroup group = map.get(gid);
                    if (group != null || isclass) {

                        ContactGroup  tempGroup = temp.get(gid);
                        if (tempGroup == null) {

                            ContactGroup nGroup = new ContactGroup();
                            nGroup.group = isclass? gid : group.group;
                            //nGroup.id = group.id;
                            nGroup.collection = new ArrayList<>();
                            nGroup.collection.add(contact);
                            temp.put(gid,nGroup);

                        }else {
                            tempGroup.collection.add(contact);
                        }

                    }

                    cursor.moveToNext();
                }

                contactGroups = new ArrayList<>();
                for(ContactGroup group : temp.values()) {
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
                Map<Long, ContactGroup> temp = new HashMap<>();
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

                    boolean isclass = gid == CLASSES;

                    ContactGroup group = map.get(gid);
                    if (group != null || isclass) {

                        ContactGroup  tempGroup = temp.get(gid);
                        if (tempGroup == null) {

                            ContactGroup nGroup = new ContactGroup();
                            nGroup.group = isclass? gid : group.group;
                            //nGroup.id = group.id;
                            nGroup.collection = new ArrayList<>();
                            nGroup.collection.add(contact);
                            temp.put(gid,nGroup);

                        }else {
                            tempGroup.collection.add(contact);
                        }

                    }

                    cursor.moveToNext();
                }

                contactGroups = new ArrayList<>();
                for(ContactGroup group : temp.values()) {
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
    public void addContact(Context context, ArrayList<ContactGroup> contactGroups, Set<String> ids) {

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
            sb.append(",");
            sb.append(DBTables.TContact.SUBJECT);
            sb.append(",");
            sb.append(DBTables.TContact.SUBTYPE);
            sb.append(",");
            sb.append(DBTables.TContact.STATE);
            sb.append(",");
            sb.append(DBTables.TContact.STARTED);
            sb.append(") VALUES(?,?,?,?,?,?,?,?,?)");

            SQLiteStatement stmt = db.compileStatement(sb.toString());
            for (ContactGroup group : contactGroups) {

                if (TextUtils.isEmpty(group.set)) continue;

                boolean isclass = group.set.equalsIgnoreCase(Social.GroupSetType.CLASSES);

                long gid  = isclass? CLASSES : group.group;
                for (Contact contact : group.collection) {
                    stmt.clearBindings();

                    stmt.bindLong(1, gid);

                    String cid = isclass? contact.id: contact.account;
                    stmt.bindString(2, cid);

                    if (ids != null && !isclass) {
                        ids.add(cid);
                    }

                    stmt.bindLong(3, contact.followType);

                    String name = isclass? contact.title : contact.alias;
                    stmt.bindString(4, name);

                    String url = contact.avatar;
                    String s = url != null? url :"";
                    stmt.bindString(5, s);

                    String subject = TextUtils.isEmpty(contact.subject)? "" : contact.subject;
                    stmt.bindString(6,subject);

                    String subtype = TextUtils.isEmpty(contact.subtype)? "" : contact.subtype;
                    stmt.bindString(7,subtype);

                    String state = TextUtils.isEmpty(contact.state)? "" : contact.state;
                    stmt.bindString(8,state);

                    String start = TextUtils.isEmpty(contact.startedOn)? "" : contact.startedOn;
                    stmt.bindString(9,start);

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
