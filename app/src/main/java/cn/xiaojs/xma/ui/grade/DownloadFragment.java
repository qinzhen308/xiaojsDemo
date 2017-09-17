package cn.xiaojs.xma.ui.grade;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.db.DBTables;
import cn.xiaojs.xma.data.download.DownloadProvider;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.MaterialUtil;

/**
 * Created by maxiaobao on 2017/7/18.
 */

public class DownloadFragment extends BaseFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.base_list)
    protected PullToRefreshSwipeListView mList;

    private DownloadAdapter mAdapter;


    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.activity_base_list, null);
    }

    @Override
    protected void init() {

        getActivity().getSupportLoaderManager().initLoader(0, null, this);

        mList.enableLeftSwipe();

        mAdapter = new DownloadAdapter(mContext, mList, null);
        mList.setAdapter(mAdapter);
        mList.getRefreshableView()
                .getWrappedList()
                .setOffsetLeft(DeviceUtil.getScreenWidth(mContext) -
                        getResources().getDimensionPixelSize(R.dimen.px140));

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter != null) {
                    Object object = mAdapter.getItem(position);
                    if (object != null && object instanceof Cursor) {
                        Cursor cursor = (Cursor) object;
                        String mimeType = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.MIME_TYPE));
                        String local = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.LOCAL));
                        String fileName = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.FILE_NAME));
                        MaterialUtil.openFileBySystem(getActivity(), fileName,local, mimeType);
                    }
                }
            }
        });


        DownloadProvider.updateCount(getContext());

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = {
                DBTables.TDownload._ID,
                DBTables.TDownload.URL,
                DBTables.TDownload.TITLE,
                DBTables.TDownload.FILE_NAME,
                DBTables.TDownload.LOCAL,
                DBTables.TDownload.KEY,
                DBTables.TDownload.ICON,
                DBTables.TDownload.MIME_TYPE,
                DBTables.TDownload.STATUS,
                DBTables.TDownload.LAST_MOD,
                DBTables.TDownload.TOTAL_BYTES,
                DBTables.TDownload.CURRENT_BYTES,
                DBTables.TDownload.TYPE_NAME
        };

        String section = DBTables.TDownload.HIDDEN + " = ? and " + DBTables.TDownload.OWNER + " in (?,?)";
        String[] sectionArgs = {"0", "-1", AccountDataManager.getAccountID(mContext)};

        String order = DBTables.TDownload.STATUS + " ASC";
        return new CursorLoader(mContext,
                DownloadProvider.DOWNLOAD_URI,
                projections,
                section,
                sectionArgs,
                order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mAdapter != null) {
            mAdapter.swapCursor(cursor);
        }

        if (cursor == null || cursor.getCount()<=0) {
            showEmptyView(getString(R.string.empty_download));
        }else {
            hideEmptyView();
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.swapCursor(null);
        }
    }
}
