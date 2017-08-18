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
 *
 * ======================================================================================== */

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.Date;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.AbsCursorAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.pulltorefresh.stickylistheaders.StickyListHeadersAdapter;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.DownloadManager;
import cn.xiaojs.xma.data.db.DBTables;
import cn.xiaojs.xma.data.download.DownloadInfo;
import cn.xiaojs.xma.model.material.DownloadCount;
import cn.xiaojs.xma.ui.widget.CircleProgressView;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.MaterialUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;

public class DownloadAdapter extends AbsCursorAdapter<DownloadAdapter.Holder> implements StickyListHeadersAdapter {

    public DownloadAdapter(Context context, PullToRefreshSwipeListView listView, Cursor c) {
        super(context, listView, c, true);
    }

    @Override
    protected View createContentView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material_download_item, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public Object getItem(int position) {
        return super.getItem(position);
    }

    @Override
    protected void setViewContent(Holder holder, Context context, Cursor cursor) {
        String mimeType = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.MIME_TYPE));
        String key = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.KEY));
        final String path = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.LOCAL));

        thumbnail(mimeType, key, holder);

        String name = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.FILE_NAME));

        final long id = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload._ID));

        final int status = cursor.getInt(cursor.getColumnIndex(DBTables.TDownload.STATUS));
        if (status == DownloadInfo.DownloadStatus.STATUS_SUCCESS) {
            holder.progressView.setProgress(0);
            holder.progressView.setImageResource(R.drawable.ic_download_running);
            holder.progressView.setVisibility(View.GONE);
            holder.size.setVisibility(View.VISIBLE);
            holder.thumbnail.setVisibility(View.VISIBLE);

            holder.errorView.setVisibility(View.GONE);
            holder.fuckView.setVisibility(View.GONE);

            holder.name.setText(name);
            holder.name.setVisibility(View.VISIBLE);


            long size = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.TOTAL_BYTES));
            long modify = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.LAST_MOD));

            String sizeTime = new StringBuilder(XjsUtils.getSizeFormatText(size))
                    .append("  ")
                    .append(TimeUtil.format(new Date(modify), TimeUtil.TIME_YYYY_MM_DD_HH_MM))
                    .toString();

            holder.size.setText(sizeTime);


        } else if (status == DownloadInfo.DownloadStatus.STATUS_PENDING) {
            holder.progressView.setImageResource(R.drawable.ic_download_pending);
            holder.progressView.setProgress(0);
            holder.progressView.setVisibility(View.VISIBLE);
            holder.size.setVisibility(View.VISIBLE);
            holder.thumbnail.setVisibility(View.VISIBLE);

            holder.name.setText(name);
            holder.name.setVisibility(View.VISIBLE);

            holder.errorView.setVisibility(View.GONE);
            holder.fuckView.setVisibility(View.GONE);
            long size = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.TOTAL_BYTES));
            long modify = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.LAST_MOD));

            String sizeTime = new StringBuilder(XjsUtils.getSizeFormatText(size))
                    .append("  ")
                    .append(TimeUtil.format(new Date(modify), TimeUtil.TIME_YYYY_MM_DD_HH_MM))
                    .toString();
            holder.size.setText(sizeTime);

        } else if (status == DownloadInfo.DownloadStatus.STATUS_RUNNING) {
            holder.progressView.setImageResource(R.drawable.ic_download_running);
            holder.progressView.setVisibility(View.VISIBLE);
            holder.size.setVisibility(View.VISIBLE);
            holder.thumbnail.setVisibility(View.VISIBLE);
            holder.errorView.setVisibility(View.GONE);
            holder.fuckView.setVisibility(View.GONE);

            holder.name.setText(name);
            holder.name.setVisibility(View.VISIBLE);

            long size = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.TOTAL_BYTES));
            long current = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.CURRENT_BYTES));
            holder.size.setText(XjsUtils.getSizeFormatText(current) + "/" + XjsUtils.getSizeFormatText(size));
            int percent = (int) (((float) current) / size * 100);
            holder.progressView.setProgress(percent);

        } else if (status == DownloadInfo.DownloadStatus.STATUS_FUCK_ING
                || status == DownloadInfo.DownloadStatus.STATUS_FUCK_OVER) {
            holder.progressView.setImageResource(R.drawable.ic_download_failed);


            DownloadCount count = DownloadManager.getDownloadCount(context);

            StringBuilder fuckStr = new StringBuilder(name);

            if (status == DownloadInfo.DownloadStatus.STATUS_FUCK_ING) {
                fuckStr.append("(").append(count.running).append(")");
            }else {
                fuckStr.append("(").append(count.success).append(")");
            }

            holder.fuckView.setText(fuckStr.toString());
            holder.fuckView.setVisibility(View.VISIBLE);

            holder.errorView.setVisibility(View.GONE);
            holder.progressView.setVisibility(View.GONE);
            holder.size.setVisibility(View.GONE);
            holder.thumbnail.setVisibility(View.GONE);
            holder.name.setVisibility(View.GONE);


        } else {
            holder.progressView.setImageResource(R.drawable.ic_download_failed);
            holder.progressView.setProgress(0);
            holder.progressView.setVisibility(View.VISIBLE);
            holder.errorView.setVisibility(View.VISIBLE);
            holder.size.setVisibility(View.VISIBLE);
            holder.thumbnail.setVisibility(View.VISIBLE);

            holder.fuckView.setVisibility(View.GONE);

            holder.name.setText(name);
            holder.name.setVisibility(View.VISIBLE);

            if (status == DownloadInfo.DownloadStatus.STATUS_CANCELED) {
                holder.errorView.setText(R.string.download_cancel);
            } else {
                holder.errorView.setText(R.string.download_error);
            }

            long size = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.TOTAL_BYTES));
            long modify = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.LAST_MOD));
            String sizeTime = new StringBuilder(XjsUtils.getSizeFormatText(size))
                    .append("  ")
                    .append(TimeUtil.format(new Date(modify), TimeUtil.TIME_YYYY_MM_DD_HH_MM))
                    .toString();

            holder.size.setText(sizeTime);
        }


        holder.progressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleOperaClick(status, id, path);

            }
        });

    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new View(mContext);
        }
        return convertView;
    }

    @Override
    public boolean isEnabled(int position) {
        Cursor cursor = getCursor();
        cursor.moveToPosition(position);
        int status = cursor.getInt(cursor.getColumnIndex(DBTables.TDownload.STATUS));
        if (status == DownloadInfo.DownloadStatus.STATUS_FUCK_ING
                || status == DownloadInfo.DownloadStatus.STATUS_FUCK_OVER) {
            return false;
        }
        return true;
    }

    @Override
    protected void onAttachSwipe(TextView mark, TextView del) {
        mark.setVisibility(View.GONE);
        setLeftOffset(mContext.getResources().getDimension(R.dimen.px140));
    }

    @Override
    protected void onSwipeDelete(int position, Cursor cursor1) {

        Object object = getItem(position);
        if (object != null && object instanceof Cursor) {

            Cursor cursor = (Cursor) object;

            final long id = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload._ID));
            final String path = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.LOCAL));
            DownloadManager.delDownload(mContext, id, path);
        }


    }

    @Override
    protected void onDataItemClick(int position) {

        Object object = getItem(position);

        if (object != null && object instanceof Cursor) {
            Cursor cursor = (Cursor) object;

            int status = cursor.getInt(cursor.getColumnIndex(DBTables.TDownload.STATUS));
            if (status == DownloadInfo.DownloadStatus.STATUS_SUCCESS) {
                String mimeType = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.MIME_TYPE));
                String local = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.LOCAL));
                MaterialUtil.openFileBySystem(mContext, local, mimeType);
            }
        }
    }

    //    @Override
//    protected void setDeleteListener(TextView text, Cursor cursor) {
//        if (text == null)
//            return;
//        final long id = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload._ID));
//        final String path = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.LOCAL));
//        text.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                DownloadManager.delDownload(mContext, id, path);
//            }
//        });
//    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }


    private void handleOperaClick(int status, long id, String localPath) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("the status: %d, id: %d", status, id);
        }

       if (status == DownloadInfo.DownloadStatus.STATUS_RUNNING) {
//            DownloadManager.cancelDownload(mContext, id, localPath);
        } else if (canResumeStatus(status)) {
            DownloadManager.resumeDownload(mContext, id);
        }

    }

    private boolean canResumeStatus(int status) {
        return status >= DownloadInfo.DownloadStatus.STATUS_QUEUED_FOR_WIFI
                && status <= DownloadInfo.DownloadStatus.STATUS_CANCELED;
    }

    private void thumbnail(String mimeType, String key, Holder holder) {

        String iconUrl = "";
        int errorResId;

        if (FileUtil.DOC == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_word;
        } else if (FileUtil.PPT == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_ppt;
        } else if (FileUtil.XLS == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_excel;
        } else if (FileUtil.PICTURE == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_picture;
            iconUrl = Social.getDrawing(key, true);
        } else if (FileUtil.PDF == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_pdf;
        } else if (FileUtil.VIDEO == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_video_mine;
        } else if (FileUtil.STEAMIMG == FileUtil.getFileType(mimeType)) {
            errorResId = R.drawable.ic_record_video;
        } else {
            errorResId = R.drawable.ic_unknown;
        }

        Glide.with(mContext)
                .load(iconUrl)
                .placeholder(errorResId)
                .error(errorResId)
                .into(holder.thumbnail);
    }

    class Holder extends BaseHolder {
        @BindView(R.id.material_download_thumbnail)
        ImageView thumbnail;
        @BindView(R.id.material_download_name)
        TextView name;
        @BindView(R.id.material_download_size)
        TextView size;
        @BindView(R.id.progress)
        CircleProgressView progressView;
        @BindView(R.id.error_tip)
        TextView errorView;
        @BindView(R.id.fuck_title)
        TextView fuckView;

        public Holder(View view) {
            super(view);
        }
    }
}
