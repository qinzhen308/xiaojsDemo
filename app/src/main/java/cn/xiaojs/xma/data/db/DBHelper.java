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

        createContactTable(db);
        createGroupTable(db);
        createDownloadTable(db);
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
                .append(TContact.GID)
                .append(" INTEGER, ")
                .append(TContact.CID)
                .append(" TEXT NOT NULL, ")
                .append(TContact.FOLLOW_TYPE)
                .append(" INTEGER, ")
                .append(TContact.NAME)
                .append(" TEXT NOT NULL, ")
                .append(TContact.AVATOR)
                .append(" TEXT,")
                .append(TContact.KEY)
                .append(" TEXT,")
                .append(TContact.SUBJECT)
                .append(" TEXT,")
                .append(TContact.SUBTYPE)
                .append(" TEXT,")
                .append(TContact.STATE)
                .append(" TEXT,")
                .append(TContact.STARTED)
                .append(" TEXT,")
                .append(TContact.UNREAD)
                .append(" TEXT,")
                .append(TContact.LAST_MSG)
                .append(" TEXT,")
                .append(TContact.GNAME)
                .append(" TEXT")
                .append("); ")
                .toString();

        db.execSQL(CREATE_TABLE_SQL);
    }

    private void createDownloadTable(SQLiteDatabase db) {
        final String CREATE_TABLE_SQL = new StringBuilder("CREATE TABLE IF NOT EXISTS ")
                .append(DBTables.TDownload.TABLE_NAME)
                .append(" (")
                .append(DBTables.TDownload._ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
                .append(DBTables.TDownload.URL)
                .append(" TEXT, ")
                .append(DBTables.TDownload.LOCAL)
                .append(" TEXT, ")
                .append(DBTables.TDownload.FILE_NAME)
                .append(" TEXT, ")
                .append(DBTables.TDownload.TITLE)
                .append(" TEXT, ")
                .append(DBTables.TDownload.KEY)
                .append(" TEXT, ")
                .append(DBTables.TDownload.ICON)
                .append(" TEXT, ")
                .append(DBTables.TDownload.MIME_TYPE)
                .append(" TEXT, ")
                .append(DBTables.TDownload.CONTROL)
                .append(" INTEGER, ")
                .append(DBTables.TDownload.DELETED)
                .append(" BOOLEAN NOT NULL DEFAULT 0, ")
                .append(DBTables.TDownload.STATUS)
                .append(" INTEGER, ")
                .append(DBTables.TDownload.NUM_FAILED)
                .append(" INTEGER, ")
                .append(DBTables.TDownload.LAST_MOD)
                .append(" BIGINT, ")
                .append(DBTables.TDownload.REFERER)
                .append(" TEXT, ")
                .append(DBTables.TDownload.TOTAL_BYTES)
                .append(" INTEGER, ")
                .append(DBTables.TDownload.CURRENT_BYTES)
                .append(" INTEGER, ")
                .append(DBTables.TDownload.ETAG)
                .append(" TEXT, ")
                .append(DBTables.TDownload.COOKIES)
                .append(" TEXT, ")
                .append(DBTables.TDownload.USER_AGENT)
                .append(" TEXT, ")
                .append(DBTables.TDownload.DESCRIPTION)
                .append(" TEXT, ")
                .append(DBTables.TDownload.ERROR_MSG)
                .append(" TEXT, ")
                .append(DBTables.TDownload.ALLOW_METERED)
                .append("INTEGER NOT NULL DEFAULT 1, ")
                .append(DBTables.TDownload.FLAGS)
                .append("INTEGER NOT NULL DEFAULT 0, ")
                .append(DBTables.TDownload.ALLOWED_NETWORK_TYPES)
                .append("INTEGER NOT NULL DEFAULT 0, ")
                .append(DBTables.TDownload.ALLOWROADMING)
                .append("INTEGER NOT NULL DEFAULT 0")
                .append("); ")
                .toString();
        db.execSQL(CREATE_TABLE_SQL);
    }


}
