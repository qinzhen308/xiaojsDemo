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
 * Date:2017/1/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.DownloadManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.material.UserDoc;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.MaterialUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;

public class MaterialAdapter extends AbsSwipeAdapter<LibDoc, MaterialAdapter.Holder> {

    private boolean mIsMine;
    private String mOwner;

    public MaterialAdapter(Context context, PullToRefreshSwipeListView listView, String owner) {
        super(context, listView);
        if (TextUtils.isEmpty(owner)) {
            mIsMine = true;
        }
        mOwner = owner;

        setDescNow("空空如也～");
    }

    @Override
    protected PullToRefreshBase.Mode getRefreshMode() {
        return PullToRefreshBase.Mode.DISABLED;
    }

    @Override
    protected void setViewContent(final Holder holder, final LibDoc bean, int position) {
        holder.showOpera(false);
        if (mIsMine) {
            holder.opera2.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.share_selector, 0, 0);
            holder.opera2.setText(R.string.share);
        }


        if (FileUtil.DOC == FileUtil.getFileType(bean.mimeType)) {
            thumbnail(bean.key, R.drawable.ic_word, holder);
        } else if (FileUtil.PPT == FileUtil.getFileType(bean.mimeType)) {
            thumbnail(bean.key, R.drawable.ic_ppt, holder);
        } else if (FileUtil.XLS == FileUtil.getFileType(bean.mimeType)) {
            thumbnail(bean.key, R.drawable.ic_excel, holder);
        } else if (FileUtil.PICTURE == FileUtil.getFileType(bean.mimeType)) {
            thumbnail(bean.key, R.drawable.ic_picture, holder);
        } else if (FileUtil.PDF == FileUtil.getFileType(bean.mimeType)) {
            thumbnail(bean.key, R.drawable.ic_pdf, holder);
        } else if (FileUtil.VIDEO == FileUtil.getFileType(bean.mimeType)){
            thumbnail(bean.key, R.drawable.ic_video_mine, holder);
        } else {
            thumbnail(bean.key, R.drawable.ic_unknown, holder);
        }
        holder.name.setText(bean.name);
        StringBuilder sb = new StringBuilder();
        sb.append(XjsUtils.getSizeFormatText(bean.used));
        sb.append("  ");
        sb.append(TimeUtil.format(bean.uploadedOn, TimeUtil.TIME_YYYY_MM_DD_HH_MM));

        holder.desc.setText(sb);

        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showOpera(holder.opera.getVisibility() != View.VISIBLE);
            }
        });


        holder.opera1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                download(bean);
            }
        });

        holder.opera2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MaterialActivity)mContext).chooseShare(bean.id);

            }
        });

        holder.opera3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MaterialActivity)mContext).confirmDel(bean.id);

            }
        });

        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialUtil.openMaterial((MaterialActivity)mContext, bean);
            }
        });
    }

    private void download(LibDoc bean) {
        if (DownloadManager.allowDownload(mContext)) {
            DownloadManager.enqueueDownload(mContext, bean.name, bean.key, Social.getDrawing(bean.key, false), bean.mimeType, Social.getDrawing(bean.key, true));
        } else {
            Toast.makeText(mContext, "当前有下载任务，不能新建下载", Toast.LENGTH_SHORT).show();
        }

        Intent intent = new Intent(mContext, MaterialDownloadActivity.class);
        mContext.startActivity(intent);
    }

    private void thumbnail(String key, int errorResId, Holder holder) {
        Glide.with(mContext)
                .load(Social.getDrawing(key, true))
                .placeholder(errorResId)
                .error(errorResId)
                .into(holder.image);
    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_material_item, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        CollaManager.getDocuments(mContext, mOwner, Collaboration.SubType.PERSON, mPagination, new APIServiceCallback<UserDoc>() {
            @Override
            public void onSuccess(UserDoc object) {

                if (getList() !=null) {
                    getList().clear();
                }

                if (object != null && object.documents!=null && object.documents.size() > 0) {
                    MaterialAdapter.this.onSuccess(object.documents);
                } else {
                    MaterialAdapter.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                MaterialAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    class Holder extends BaseHolder {
        @BindView(R.id.material_item_image)
        ImageView image;
        @BindView(R.id.material_item_name)
        TextView name;
        @BindView(R.id.material_item_desc)
        TextView desc;
        @BindView(R.id.material_item_expand)
        ImageView expand;

        @BindView(R.id.material_item_opera_wrapper)
        LinearLayout opera;
        @BindView(R.id.material_item_opera1)
        TextView opera1;
        @BindView(R.id.material_item_opera2)
        TextView opera2;
        @BindView(R.id.material_item_opera3)
        TextView opera3;

        @BindView(R.id.material_item)
        View item;

        public void showOpera(boolean b) {
            if (b) {
                opera.setVisibility(View.VISIBLE);
                expand.setImageResource(R.drawable.ic_arrow_up);
            } else {
                opera.setVisibility(View.GONE);
                expand.setImageResource(R.drawable.ic_arrow_down);
            }
        }

        public Holder(View view) {
            super(view);

            opera3.setVisibility(View.VISIBLE);
        }
    }
}
