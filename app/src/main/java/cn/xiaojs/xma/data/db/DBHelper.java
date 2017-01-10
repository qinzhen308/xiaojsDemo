package cn.xiaojs.xma.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cn.xiaojs.xma.XiaojsConfig;

import static cn.xiaojs.xma.data.db.DBTables.TContact;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public final class DBHelper extends SQLiteOpenHelper {

    private static volatile DBHelper dbHelper;


    @Override
    public void onCreate(SQLiteDatabase db) {

        createTables(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Public db

    public DBHelper(Context context) {
        super(context, XiaojsConfig.DB_NAME, null, XiaojsConfig.DB_VERSION);
    }

    public static DBHelper getInstance(Context context) {

        if (dbHelper == null) {
            synchronized (DBHelper.class) {
                if (dbHelper == null) {
                    dbHelper = new DBHelper(context.getApplicationContext());
                }
            }
        }

        return dbHelper;
    }

    public static SQLiteDatabase getWriteDb(Context context) {

        SQLiteDatabase db = getInstance(context).getWritableDatabase();
        if (!db.isOpen()) {
            SQLiteDatabase.openDatabase(XiaojsConfig.DB_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return db;

    }

    public static SQLiteDatabase getReadDatabase(Context context) {

        SQLiteDatabase db = getInstance(context).getReadableDatabase();
        if (!db.isOpen()) {
            SQLiteDatabase.openDatabase(XiaojsConfig.DB_NAME, null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return db;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //Create table methods

    private void createTables(SQLiteDatabase db) {

        //createContactTable(db);
        createGroupTable(db);
    }

    private void createGroupTable(SQLiteDatabase db) {
        final String CREATE_TABLE_SQL = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(DBTables.TGroup.TABLE_NAME)
                .append(" (")
                .append(TContact._ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(DBTables.TGroup.GID)
                .append(" INTEGER, ")
                .append(DBTables.TGroup.NAME)
                .append(" TEXT NOT NULL")
                .append("); ")
                .toString();

        db.execSQL(CREATE_TABLE_SQL);
    }

    private void createContactTable(SQLiteDatabase db) {

        //Create contact table sql
        final String CREATE_TABLE_SQL = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(TContact.TABLE_NAME)
                .append(" (")
                .append(TContact._ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(TContact.NAME)
                .append(" TEXT NOT NULL, ")
                .append(TContact.AVATOR)
                .append(" TEXT")
                .append("); ")
                .toString();

        db.execSQL(CREATE_TABLE_SQL);
    }


}
