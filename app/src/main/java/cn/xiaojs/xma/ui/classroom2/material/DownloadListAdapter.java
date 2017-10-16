package cn.xiaojs.xma.ui.classroom2.material;

import android.content.Context;
import android.view.ViewGroup;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.widget.RecyclerViewCursorAdapter;

/**
 * Created by maxiaobao on 2017/10/9.
 */

public class DownloadListAdapter extends RecyclerViewCursorAdapter<DownloadViewHolder> {


    public DownloadListAdapter(Context context) {
        super(context);
        setupCursorAdapter(null, 0, R.layout.layout_material_download_item, false);
    }

    @Override
    public DownloadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DownloadViewHolder(mContext,
                mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent));
    }

    @Override
    public void onBindViewHolder(DownloadViewHolder holder, int position) {

        // Move cursor to this position
        mCursorAdapter.getCursor().moveToPosition(position);

        // Set the ViewHolder
        setViewHolder(holder);

        // Bind this view
        mCursorAdapter.bindView(null, mContext, mCursorAdapter.getCursor());
    }
}
