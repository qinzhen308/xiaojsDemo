package cn.xiaojs.xma.data.db;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.AsyncTaskLoader;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public class CursorTaskLoader extends AsyncTaskLoader<Cursor> {

    private Context context;
    private Cursor cursor;
    private BaseDao baseDao;

    public CursorTaskLoader(Context context, BaseDao dao) {

        super(context.getApplicationContext());
        this.context = context.getApplicationContext();
        this.baseDao = dao;

    }

    @Override
    public Cursor loadInBackground() {
        return null; //baseDao.getCursor(context);
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

    }
}
