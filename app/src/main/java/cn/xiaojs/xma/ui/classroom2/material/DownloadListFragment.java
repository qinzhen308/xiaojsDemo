package cn.xiaojs.xma.ui.classroom2.material;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.db.DBTables;
import cn.xiaojs.xma.data.download.DownloadProvider;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.live.MemberAdapter;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class DownloadListFragment extends BottomSheetFragment
        implements LoaderManager.LoaderCallbacks<Cursor>{


    @BindView(R.id.rlist)
    RecyclerView recyclerView;

    private DownloadListAdapter listAdapter;


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_classroom2_download_list, container, false);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);

        listAdapter = new DownloadListAdapter(getContext());
        recyclerView.setAdapter(listAdapter);

        getActivity().getSupportLoaderManager().initLoader(0, null, this);

        DownloadProvider.updateCount(getContext());

    }

    @OnClick({R.id.back_btn})
    void onViewClick(View view) {
        switch(view.getId()) {
            case R.id.back_btn:
                dismiss();
                break;
        }
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
                DBTables.TDownload.CURRENT_BYTES
        };

        String section = DBTables.TDownload.HIDDEN + " = ? and " + DBTables.TDownload.OWNER + " in (?,?)";
        String[] sectionArgs = {"0", "-1", AccountDataManager.getAccountID(getContext())};

        String order = DBTables.TDownload.STATUS + " ASC";
        return new CursorLoader(getContext(),
                DownloadProvider.DOWNLOAD_URI,
                projections,
                section,
                sectionArgs,
                order);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (listAdapter != null) {
            listAdapter.swapCursor(cursor);
        }

//        if (cursor == null || cursor.getCount()<=0) {
//            showEmptyView(getString(R.string.empty_download));
//        }else {
//            hideEmptyView();
//        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        if (listAdapter != null) {
            listAdapter.swapCursor(null);
        }

    }
}
