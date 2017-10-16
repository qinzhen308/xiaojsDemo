package cn.xiaojs.xma.ui.classroom2.material;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.DownloadManager;
import cn.xiaojs.xma.data.db.DBTables;
import cn.xiaojs.xma.data.download.DownloadInfo;
import cn.xiaojs.xma.model.material.DownloadCount;
import cn.xiaojs.xma.ui.classroom2.widget.RecyclerViewCursorViewHolder;
import cn.xiaojs.xma.ui.widget.CircleProgressView;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;

/**
 * Created by maxiaobao on 2017/10/9.
 */

public class DownloadViewHolder extends RecyclerViewCursorViewHolder {


    @BindView(R.id.material_download_thumbnail)
    ImageView thumbnailView;
    @BindView(R.id.material_download_name)
    TextView nameView;
    @BindView(R.id.material_download_size)
    TextView sizeView;
    @BindView(R.id.progress)
    CircleProgressView progressView;
    @BindView(R.id.error_tip)
    TextView errorView;
    @BindView(R.id.fuck_title)
    TextView fuckView;

    private Context context;


    public DownloadViewHolder(Context context, View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);

        this.context = context;
    }

    @Override
    public void bindCursor(Cursor cursor) {
        String mimeType = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.MIME_TYPE));
        String key = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.KEY));
        final String path = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.LOCAL));

        thumbnail(mimeType, key);

        String name = cursor.getString(cursor.getColumnIndex(DBTables.TDownload.FILE_NAME));

        final long id = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload._ID));

        final int status = cursor.getInt(cursor.getColumnIndex(DBTables.TDownload.STATUS));
        if (status == DownloadInfo.DownloadStatus.STATUS_SUCCESS) {
            progressView.setProgress(0);
            progressView.setImageResource(R.drawable.ic_download_running);
            progressView.setVisibility(View.GONE);
            sizeView.setVisibility(View.VISIBLE);
            thumbnailView.setVisibility(View.VISIBLE);

            errorView.setVisibility(View.GONE);
            fuckView.setVisibility(View.GONE);

            nameView.setText(name);
            nameView.setVisibility(View.VISIBLE);


            long size = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.TOTAL_BYTES));
            long modify = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.LAST_MOD));

            String sizeTime = new StringBuilder(XjsUtils.getSizeFormatText(size))
                    .append("  ")
                    .append(TimeUtil.format(new Date(modify), TimeUtil.TIME_YYYY_MM_DD_HH_MM))
                    .toString();

            sizeView.setText(sizeTime);


        } else if (status == DownloadInfo.DownloadStatus.STATUS_PENDING) {
            progressView.setImageResource(R.drawable.ic_download_pending);
            progressView.setProgress(0);
            progressView.setVisibility(View.VISIBLE);
            sizeView.setVisibility(View.VISIBLE);
            thumbnailView.setVisibility(View.VISIBLE);

            nameView.setText(name);
            nameView.setVisibility(View.VISIBLE);

            errorView.setVisibility(View.GONE);
            fuckView.setVisibility(View.GONE);
            long size = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.TOTAL_BYTES));
            long modify = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.LAST_MOD));

            String sizeTime = new StringBuilder(XjsUtils.getSizeFormatText(size))
                    .append("  ")
                    .append(TimeUtil.format(new Date(modify), TimeUtil.TIME_YYYY_MM_DD_HH_MM))
                    .toString();
            sizeView.setText(sizeTime);

        } else if (status == DownloadInfo.DownloadStatus.STATUS_RUNNING) {
            progressView.setImageResource(R.drawable.ic_download_running);
            progressView.setVisibility(View.VISIBLE);
            sizeView.setVisibility(View.VISIBLE);
            thumbnailView.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.GONE);
            fuckView.setVisibility(View.GONE);

            nameView.setText(name);
            nameView.setVisibility(View.VISIBLE);

            long size = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.TOTAL_BYTES));
            long current = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.CURRENT_BYTES));
            sizeView.setText(XjsUtils.getSizeFormatText(current) + "/" + XjsUtils.getSizeFormatText(size));
            int percent = (int) (((float) current) / size * 100);
            progressView.setProgress(percent);

        } else if (status == DownloadInfo.DownloadStatus.STATUS_FUCK_ING
                || status == DownloadInfo.DownloadStatus.STATUS_FUCK_OVER) {
            progressView.setImageResource(R.drawable.ic_download_failed);


            DownloadCount count = DownloadManager.getDownloadCount(context);

            StringBuilder fuckStr = new StringBuilder(name);

            if (status == DownloadInfo.DownloadStatus.STATUS_FUCK_ING) {
                fuckStr.append("(").append(count.running).append(")");
            }else {
                fuckStr.append("(").append(count.success).append(")");
            }

            fuckView.setText(fuckStr.toString());
            fuckView.setVisibility(View.VISIBLE);

            errorView.setVisibility(View.GONE);
            progressView.setVisibility(View.GONE);
            sizeView.setVisibility(View.GONE);
            thumbnailView.setVisibility(View.GONE);
           nameView.setVisibility(View.GONE);


        } else {
            progressView.setImageResource(R.drawable.ic_download_failed);
            progressView.setProgress(0);
            progressView.setVisibility(View.VISIBLE);
            errorView.setVisibility(View.VISIBLE);
            sizeView.setVisibility(View.VISIBLE);
            thumbnailView.setVisibility(View.VISIBLE);

            fuckView.setVisibility(View.GONE);

           nameView.setText(name);
            nameView.setVisibility(View.VISIBLE);

            if (status == DownloadInfo.DownloadStatus.STATUS_CANCELED) {
                errorView.setText(R.string.download_cancel);
            } else {
                errorView.setText(R.string.download_error);
            }

            long size = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.TOTAL_BYTES));
            long modify = cursor.getLong(cursor.getColumnIndex(DBTables.TDownload.LAST_MOD));
            String sizeTime = new StringBuilder(XjsUtils.getSizeFormatText(size))
                    .append("  ")
                    .append(TimeUtil.format(new Date(modify), TimeUtil.TIME_YYYY_MM_DD_HH_MM))
                    .toString();

            sizeView.setText(sizeTime);
        }


        progressView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //handleOperaClick(status, id, path);

            }
        });
    }

    private void thumbnail(String mimeType, String key) {

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

        Glide.with(context)
                .load(iconUrl)
                .placeholder(errorResId)
                .error(errorResId)
                .into(thumbnailView);
    }
}
