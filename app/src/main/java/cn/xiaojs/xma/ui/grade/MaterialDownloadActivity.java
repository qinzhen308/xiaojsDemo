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
import android.support.v4.content.Loader;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.db.DownloadLoader;
import cn.xiaojs.xma.ui.base.BaseListActivity;

public class MaterialDownloadActivity extends BaseListActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private DownloadAdapter mAdapter;
    private DownloadLoader mLoader;
    @Override
    protected void initData() {
        setMiddleTitle(R.string.download_of_mine);
        setDividerHeight(R.dimen.px1);
        getSupportLoaderManager().initLoader(0,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mLoader = new DownloadLoader(getApplicationContext());
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null){
            if (mAdapter == null){
                mAdapter = new DownloadAdapter(this,cursor);
                mList.setAdapter(mAdapter);
            }else {
                mAdapter.swapCursor(cursor);
            }

            int count = mAdapter.getCount();
            if (count <= 0){

            }else {

            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (mAdapter != null){
            mAdapter.swapCursor(null);
        }
    }
}
