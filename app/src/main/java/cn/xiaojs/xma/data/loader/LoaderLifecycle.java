package cn.xiaojs.xma.data.loader;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;

import cn.xiaojs.xma.data.db.LoadDataCallback;

/**
 * Created by maxiaobao on 2017/1/3.
 */


public class LoaderLifecycle<T> implements LoaderManager.LoaderCallbacks<T> {

    private AsyncTaskLoader<T> loader;
    private LoadDataCallback<T> callback;

    public LoaderLifecycle(AsyncTaskLoader<T> loader, LoadDataCallback<T> callback) {
        this.loader = loader;
        this.callback = callback;
    }

    @Override
    public Loader<T> onCreateLoader(int id, Bundle args) {
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<T> loader, T data) {

        callback.onLoadCompleted(data);

        callback.onRefreshing();
    }

    @Override
    public void onLoaderReset(Loader<T> loader) {

        callback.onLoadReset();
    }
}
