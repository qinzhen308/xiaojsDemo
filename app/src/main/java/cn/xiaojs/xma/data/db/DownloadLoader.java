package cn.xiaojs.xma.data.db;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

import cn.xiaojs.xma.data.download.DownloadProvider;

/**
 * Created by maxiaobao on 2017/2/9.
 */

public class DownloadLoader extends AsyncTaskLoader<Cursor> {
    private Context context;
    private Cursor cursor;
    private ForceLoadContentObserver observer;

    public DownloadLoader(Context context) {
        super(context.getApplicationContext());
        this.context = context.getApplicationContext();
        observer = new ForceLoadContentObserver();
    }

    @Override
    public Cursor loadInBackground() {

//        String[] projections = {DBTables.TDownload._ID,
//                DBTables.TDownload.URL,
//                DBTables.TDownload.FILE_NAME,
//                DBTables.TDownload.KEY,
//                DBTables.TDownload.MIME_TYPE,
//                DBTables.TDownload.STATUS,
//                DBTables.TDownload.LAST_MOD,
//                DBTables.TDownload.TOTAL_BYTES,
//                DBTables.TDownload.CURRENT_BYTES,};
//
//        Cursor cursor = getContext().getContentResolver().query(DownloadProvider.DOWNLOAD_URI,
//                projections,null,null,null);
//        if (cursor!=null) {
//            cursor.registerContentObserver(observer);
//        }

        return null;//由于还没写好，所以先返回null
    }

    @Override
    public void deliverResult(Cursor data) {
        if (isReset()) {
            if (data != null) {
                data.close();
            }
            return;
        }

        Cursor oldCursor = cursor;
        cursor = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldCursor != null && oldCursor != data && !oldCursor.isClosed()) {
            oldCursor.close();
        }

    }

    @Override
    protected void onStartLoading() {
        if (cursor != null) {
            deliverResult(cursor);
        }

        if (takeContentChanged() || cursor == null) {
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor data) {

        if (data != null && !data.isClosed()) {
            data.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        cursor = null;

        getContext().getContentResolver().unregisterContentObserver(observer);

    }
}
