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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;

public class MaterialDownloadAdapter extends AbsSwipeAdapter<DownloadItem, MaterialDownloadAdapter.Holder> {

    public MaterialDownloadAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    @Override
    protected void setViewContent(Holder holder, DownloadItem bean, int position) {
        if (bean.downloading){
            holder.download();
        }else {
            holder.normal();

        }
    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material_download_item, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    public void download(String url){
        DownloadItem item = new DownloadItem();
        item.downloading = true;
        getList().add(0,item);
        notifyDataSetChanged();
    }

    @Override
    protected void onSwipeDelete(int position) {
        download("");
    }

    @Override
    protected void doRequest() {
        List<DownloadItem> list = new ArrayList<>();
        list.add(new DownloadItem());
        list.add(new DownloadItem());
        list.add(new DownloadItem());
        list.add(new DownloadItem());
        list.add(new DownloadItem());

        onSuccess(list);
    }

    @Override
    protected void onAttachSwipe(TextView mark, TextView del) {
        setLeftOffset(mContext.getResources().getDimension(R.dimen.px140));
    }

    @Override
    protected boolean leftSwipe() {
        return true;
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
