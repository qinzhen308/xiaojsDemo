package cn.xiaojs.xma.ui.grade;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2017/2/8
 * Desc:
 *
 * ======================================================================================== */

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.db.DBTables;
import cn.xiaojs.xma.data.download.DownloadProvider;
import cn.xiaojs.xma.ui.base.BaseListActivity;
import cn.xiaojs.xma.util.DeviceUtil;

public class MaterialDownloadActivity extends BaseListActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private DownloadAdapter mAdapter;

    @Override
    protected void initData() {
        setMiddleTitle(R.string.download_of_mine);
        setDividerHeight(R.dimen.px1);
        getSupportLoaderManager().initLoader(0, null, this);

        mAdapter = new DownloadAdapter(this, null);
        mList.setAdapter(mAdapter);
        mList.enableLeftSwipe();
        mList.getRefreshableView().getWrappedList().setOffsetLeft(DeviceUtil.getScreenWidth(this) - getResources().getDimensionPixelSize(R.dimen.px140));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projections = {DBTables.TDownload._ID,
                DBTables.TDownload.URL,
                DBTables.TDownload.TITLE,
                DBTables.TDownload.FILE_NAME,
                DBTables.TDownload.KEY,
                DBTables.TDownload.ICON,
                DBTables.TDownload.MIME_TYPE,
                DBTables.TDownload.STATUS,
                DBTables.TDownload.LAST_MOD,
                DBTables.TDownload.TOTAL_BYTES,
                DBTables.TDownload.CURRENT_BYTES};

        String order = DBTables.TDownload._ID + " DESC";

        return new CursorLoader(this, DownloadProvider.DOWNLOAD_URI, projections, null, null, order);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (mAdapter != null) {
            mAdapter.swapCursor(cursor);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null) {
            mAdapter.swapCursor(null);
        }
    }
}
