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
 * Date:2017/2/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsCursorAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.stickylistheaders.StickyListHeadersAdapter;
import cn.xiaojs.xma.data.DownloadManager;
import cn.xiaojs.xma.data.db.DBTables;
import cn.xiaojs.xma.data.download.DownloadInfo;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.ToastUtil;
import cn.xiaojs.xma.util.XjsUtils;

public class DownloadAdapter extends AbsCursorAdapter<DownloadAdapter.Holder> implements StickyListHeadersAdapter {

    public DownloadAdapter(Context context, Cursor c) {
        super(context, c, true);
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
    public void bindView(View view, Context context, Cursor cursor) {
        Holder holder = (Holder) view.getTag();

        String mimeType = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.MIME_TYPE));
        if (FileUtil.getFileType(mimeType) == FileUtil.DOC) {
            holder.thumbnail.setImageResource(R.drawable.ic_word);
        } else if (FileUtil.getFileType(mimeType) == FileUtil.XLS) {
            holder.thumbnail.setImageResource(R.drawable.ic_excel);
        } else if (FileUtil.getFileType(mimeType) == FileUtil.PPT) {
            holder.thumbnail.setImageResource(R.drawable.ic_ppt);
        } else if (FileUtil.getFileType(mimeType) == FileUtil.PDF) {
            holder.thumbnail.setImageResource(R.drawable.ic_pdf);
        } else if (FileUtil.getFileType(mimeType) == FileUtil.PICTURE) {
            holder.thumbnail.setImageResource(R.drawable.ic_picture);
        } else if (FileUtil.getFileType(mimeType) == FileUtil.VIDEO) {
            holder.thumbnail.setImageResource(R.drawable.ic_unknown);
        } else if (FileUtil.getFileType(mimeType) == FileUtil.AUDIO) {
            holder.thumbnail.setImageResource(R.drawable.ic_unknown);
        } else {
            holder.thumbnail.setImageResource(R.drawable.ic_unknown);
        }

        String name = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.FILE_NAME));
        holder.name.setText(name);

        final long id = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload._ID));

        int status = cursor.getInt(cursor.getColumnIndex(DBTables.TDownload.STATUS));
        if (status == DownloadInfo.DownloadStatus.STATUS_SUCCESS) {
            holder.normal();
            long size = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.TOTAL_BYTES));
            holder.size.setText(XjsUtils.getSizeFormatText(size));
            long modify = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.LAST_MOD));
            holder.time.setText(TimeUtil.format(new Date(modify), TimeUtil.TIME_YYYY_MM_DD_HH_MM));
            holder.failure.setVisibility(View.GONE);

        } else if (status == DownloadInfo.DownloadStatus.STATUS_RUNNING) {
            holder.download();
            long size = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.TOTAL_BYTES));
            long current = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.CURRENT_BYTES));
            holder.downloadPercent.setText(XjsUtils.getSizeFormatText(current) + "/" + XjsUtils.getSizeFormatText(size));
            int percent = (int) (((float) current) / size * 100);
            holder.progress.setProgress(percent);
            holder.percent.setText(percent + "%");
            holder.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //取消下载
                    DownloadManager.delDownload(mContext,id);
                }
            });
        } else {
            holder.normal();
            long size = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.TOTAL_BYTES));
            holder.size.setText(XjsUtils.getSizeFormatText(size));
            long modify = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.LAST_MOD));
            holder.time.setText(TimeUtil.format(new Date(modify), TimeUtil.TIME_YYYY_MM_DD_HH_MM));
            holder.failure.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new View(mContext);
        }
        return convertView;
    }

    @Override
    protected void onDeleteClick(Cursor cursor) {
        ToastUtil.showToast(mContext,"DELETE");
        //DownloadManager.delDownload(mContext,id);
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    class Holder extends BaseHolder {
        @BindView(R.id.material_download_thumbnail)
        ImageView thumbnail;
        @BindView(R.id.material_download_name)
        TextView name;
        @BindView(R.id.material_download_size)
        TextView size;
        @BindView(R.id.material_download_time)
        TextView time;
        @BindView(R.id.material_download_size_percent)
        TextView downloadPercent;
        @BindView(R.id.material_up_load_progress)
        ProgressBar progress;
        @BindView(R.id.material_download_percent)
        TextView percent;
        @BindView(R.id.material_download_close)
        ImageView close;
        @BindView(R.id.material_download_failure)
        TextView failure;

        @BindView(R.id.material_download_normal_wrapper)
        LinearLayout normalWrapper;
        @BindView(R.id.material_download_percent_wrapper)
        LinearLayout downloadWrapper;

        public void normal() {
            normalWrapper.setVisibility(View.VISIBLE);
            downloadWrapper.setVisibility(View.GONE);
            close.setVisibility(View.GONE);
        }

        public void download() {
            normalWrapper.setVisibility(View.GONE);
            downloadWrapper.setVisibility(View.VISIBLE);
            close.setVisibility(View.VISIBLE);
        }

        public Holder(View view) {
            super(view);
        }
    }
}
