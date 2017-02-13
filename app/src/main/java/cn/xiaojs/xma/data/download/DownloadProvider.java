package cn.xiaojs.xma.data.download;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.db.DBHelper;
import cn.xiaojs.xma.data.db.DBTables;

public class DownloadProvider extends ContentProvider {

    public static final String EXTRA_DOWNLOAD_ID = "did";

    public static final String AUTHORITY = "cn.xiaojs.xma";
    //FIXME to impl define
    public static final Uri DOWNLOAD_URI = Uri.parse("content://cn.xiaojs.xma/downloads");

    private static final int ALL_DOWNLOAD = 1;
    private static final int DOWNLOAD_ID = 2;

    /** URI matcher used to recognize URIs sent by applications */
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, "downloads", ALL_DOWNLOAD);
        sURIMatcher.addURI(AUTHORITY, "downloads/#", DOWNLOAD_ID);
    }

    public SystemFacade systemFacade;


    public DownloadProvider() {
    }

    @Override
    public boolean onCreate() {
        if (systemFacade == null) {
            systemFacade = new RealSystemFacade(getContext());
        }
        return true;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = DBHelper.getWriteDb(getContext());
        int count = db.delete(DBTables.TDownload.TABLE_NAME, selection, selectionArgs);

        notifyContentChanged();
        return count;
    }

    @Override
    public String getType(Uri uri) {
        SQLiteDatabase db = DBHelper.getReadDatabase(getContext());

        final String id = getDownloadIdFromUri(uri);

        final String mimeType = DatabaseUtils.stringForQuery(db,
                "SELECT " + DBTables.TDownload.MIME_TYPE + " FROM " + DBTables.TDownload.TABLE_NAME +
                        " WHERE " + DBTables.TDownload._ID + " = ?",
                new String[]{id});
        if (TextUtils.isEmpty(mimeType)) {
            return "";
        } else {
            return mimeType;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

//        int match = sURIMatcher.match(uri);
//        if (match != DOC_DOWNLOAD) {
//            throw new IllegalArgumentException("Unknown/Invalid URI " + uri);
//        }

        SQLiteDatabase db = DBHelper.getWriteDb(getContext());

        ContentValues filteredValues = new ContentValues();
        copyString(DBTables.TDownload.URL, values, filteredValues);
        copyString(DBTables.TDownload.FILE_NAME, values, filteredValues);
        copyString(DBTables.TDownload.MIME_TYPE, values, filteredValues);

        // set lastupdate to current time
        long lastMod = systemFacade.currentTimeMillis();
        filteredValues.put(DBTables.TDownload.LAST_MOD, lastMod);

        long rowID = db.insert(DBTables.TDownload.TABLE_NAME, null, filteredValues);
        if (rowID == -1) {
            if (XiaojsConfig.DEBUG){
                Logger.d("couldn't insert into downloads database");
            }
            return null;
        }

        notifyContentChanged();

        lanuchDownload(rowID);
        return ContentUris.withAppendedId(DOWNLOAD_URI, rowID);
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match = sURIMatcher.match(uri);
        if (match == -1) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        switch (match) {
            case ALL_DOWNLOAD:
                break;
            case DOWNLOAD_ID:
                selection = DBTables.TDownload._ID + " = "+ uri.getLastPathSegment();
                break;
        }

        SQLiteDatabase db = DBHelper.getReadDatabase(getContext());
        Cursor ret = db.query(DBTables.TDownload.TABLE_NAME, projection, selection,
                selectionArgs, null, null, sortOrder);

        if (ret != null) {
            ret.setNotificationUri(getContext().getContentResolver(), uri);
        }

        return ret;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int match = sURIMatcher.match(uri);
        if (match != DOWNLOAD_ID) {
            return -1;
        }

        selection = DBTables.TDownload._ID + " = "+ uri.getLastPathSegment();

        SQLiteDatabase db = DBHelper.getWriteDb(getContext());
        int count = db.update(DBTables.TDownload.TABLE_NAME, values, selection, selectionArgs);

        notifyContentChanged();

        return count;
    }

    private String getDownloadIdFromUri(final Uri uri) {
        return uri.getPathSegments().get(1);
    }

    private static final void copyInteger(String key, ContentValues from, ContentValues to) {
        Integer i = from.getAsInteger(key);
        if (i != null) {
            to.put(key, i);
        }
    }

    private static final void copyBoolean(String key, ContentValues from, ContentValues to) {
        Boolean b = from.getAsBoolean(key);
        if (b != null) {
            to.put(key, b);
        }
    }

    private static final void copyString(String key, ContentValues from, ContentValues to) {
        String s = from.getAsString(key);
        if (s != null) {
            to.put(key, s);
        }
    }

    private static final void copyStringWithDefault(String key, ContentValues from,
                                                    ContentValues to, String defaultValue) {
        copyString(key, from, to);
        if (!to.containsKey(key)) {
            to.put(key, defaultValue);
        }
    }

    public void lanuchDownload(long id) {
        Intent i = new Intent(getContext(),DownloadService.class);
        i.putExtra(EXTRA_DOWNLOAD_ID,id);
        getContext().startService(i);
    }

    private void notifyContentChanged() {
        getContext().getContentResolver().notifyChange(DOWNLOAD_URI, null);
    }

}
