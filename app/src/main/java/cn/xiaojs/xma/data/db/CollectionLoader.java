package cn.xiaojs.xma.data.db;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by maxiaobao on 2016/12/23.
 */

public class CollectionLoader<T extends List> extends AsyncTaskLoader<T> {

    public CollectionLoader(Context context) {
        super(context);
    }

    @Override
    public T loadInBackground() {
        return null;
    }
}
