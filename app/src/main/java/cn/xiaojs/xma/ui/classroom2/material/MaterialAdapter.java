package cn.xiaojs.xma.ui.classroom2.material;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;

/**
 * Created by maxiaobao on 2017/10/10.
 */

public class MaterialAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int FOOTTER_TYPE = 1;
    private final int NORMAL_TYPE = 2;

    private Context context;
    private boolean mine;
    private List<LibDoc> libDocs;
    private boolean loading;

    private DatabaseListFragment fragment;

    public MaterialAdapter(DatabaseListFragment fragment, String owner, List<LibDoc> libDocs) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.libDocs = libDocs;

        if (TextUtils.isEmpty(owner)) {
            mine = true;
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (isFooter(position)) {
            return FOOTTER_TYPE;
        }


        return NORMAL_TYPE;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        if (viewType == FOOTTER_TYPE) {
//            return LoadmoreViewHolder.createHolder(context,parent);
//        }


        LayoutInflater inflater = LayoutInflater.from(context);
        return new MaterialViewHolder(
                inflater.inflate(R.layout.layout_classroom2_material_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof MaterialViewHolder) {
            final LibDoc doc = libDocs.get(position);
            final MaterialViewHolder mholder = (MaterialViewHolder) holder;
            mholder.showOpera(false);
            if (mine) {
                mholder.opera2View.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.share_selector, 0, 0);
                mholder.opera2View.setText(R.string.share);
            }


            thumbnail(doc.typeName, doc.mimeType, doc.key, mholder);


            mholder.nameView.setText(doc.name);

            if (Collaboration.State.INIT.equals(doc.state) || Collaboration.State.CONVERTING.equals(doc.state)) {
                mholder.descView.setTextColor(context.getResources().getColor(R.color.main_orange));
                mholder.descView.setText("转码中...");
            } else if (Collaboration.State.FAULTED.equals(doc.state)) {
                mholder.descView.setTextColor(context.getResources().getColor(R.color.main_orange));
                mholder.descView.setText("转码失败");
            } else {
                mholder.descView.setTextColor(context.getResources().getColor(R.color.common_text));
                Date date = doc.uploadedOn != null ? doc.uploadedOn : doc.createdOn;
                StringBuilder sb = new StringBuilder();
                if (date != null) {
                    sb.append(TimeUtil.format(date, TimeUtil.TIME_YYYY_MM_DD_HH_MM));
                }

                if (doc.used <= 0) {
                    sb.append("");
                } else {
                    sb.append("    ");
                    sb.append(XjsUtils.getSizeFormatText(doc.used));

                }
                mholder.descView.setText(sb);
            }


            mholder.expandView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mholder.showOpera(mholder.operaView.getVisibility() != View.VISIBLE);
                }
            });


            mholder.opera1View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.showDoanloadTips(doc, position);
                }
            });

            mholder.opera2View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //materialFragment.chooseShare(new String[]{bean.id});

                }
            });

            mholder.opera3View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    fragment.confirmDel(doc.id, position);

                }
            });

            mholder.opera4View.setVisibility(View.VISIBLE);
            mholder.opera4View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.showMoreDlg(doc, position);
                }
            });
        } else {
//            LoadmoreViewHolder loadmoreViewHolder = (LoadmoreViewHolder) holder;
//
//            int vi = loading? View.VISIBLE : View.GONE;
//            loadmoreViewHolder.setLoadingVisibility(vi);
        }


    }

    @Override
    public int getItemCount() {
        return libDocs == null ? 0 : libDocs.size();
    }


    private boolean isFooter(int position) {
        return position >= getItemCount() - 1;
    }


    private void thumbnail(String typename, String mimeType, String key, MaterialViewHolder holder) {

        String iconUrl = "";
        int errorResId;

        if (Collaboration.TypeName.DIRECTORY_IN_LIBRARY.equals(typename)) {
            errorResId = R.drawable.ic_folder;
        } else {
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
        }

        Glide.with(context)
                .load(iconUrl)
                .placeholder(errorResId)
                .error(errorResId)
                .into(holder.iconView);
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}
