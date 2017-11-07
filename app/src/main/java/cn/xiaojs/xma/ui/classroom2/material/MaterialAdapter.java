package cn.xiaojs.xma.ui.classroom2.material;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.classroom2.widget.LoadmoreRecyclerView;
import cn.xiaojs.xma.util.FileUtil;
import cn.xiaojs.xma.util.MaterialUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;

/**
 * Created by maxiaobao on 2017/10/10.
 */

public class MaterialAdapter extends LoadmoreRecyclerView.LMAdapter {

    private final int NORMAL_TYPE = 2;

    private Context context;
    private List<LibDoc> libDocs;
    private String subType;

    private String mId;
    private DatabaseListFragment fragment;

    public MaterialAdapter(DatabaseListFragment fragment, String subType, List<LibDoc> libDocs) {
        super(fragment.getContext());
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.libDocs = libDocs;
        this.subType = subType;
        this.mId = AccountDataManager.getAccountID(context);


    }


    @Override
    public int getDataCount() {
        return libDocs == null ? 0 : libDocs.size();
    }

    @Override
    public int getItemType(int position) {
        return NORMAL_TYPE;
    }

    @Override
    public LoadmoreRecyclerView.LMViewHolder createViewholder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new MaterialViewHolder(
                inflater.inflate(R.layout.layout_classroom2_material_item, parent, false));
    }

    @Override
    public void bindViewholder(LoadmoreRecyclerView.LMViewHolder holder, final int position) {

        final LibDoc doc = libDocs.get(position);
        final MaterialViewHolder mholder = (MaterialViewHolder) holder;
        mholder.showOpera(false);


        thumbnail(doc.typeName, doc.mimeType, doc.key, mholder);


        mholder.nameView.setText(doc.name);

        if (Collaboration.State.INIT.equals(doc.state)
                || Collaboration.State.CONVERTING.equals(doc.state)) {
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


            if (subType == Collaboration.SubType.PRIVATE_CLASS
                    && !TextUtils.isEmpty(doc.owner.name)) {
                String author = doc.owner.name;
                mholder.descView.setText(
                        Html.fromHtml(context.getString(R.string.material_info_summary, sb, author)));
            } else {
                mholder.descView.setText(sb);
            }

        }

        if (subType == Collaboration.SubType.PRIVATE_CLASS
                || Collaboration.TypeName.DIRECTORY_IN_LIBRARY.equals(doc.typeName)) {
            mholder.opera1View.setVisibility(View.GONE);
        } else {
            mholder.opera1View.setVisibility(View.VISIBLE);
        }

        if (subType == Collaboration.SubType.PRIVATE_CLASS) {
            mholder.opera4View.setVisibility(View.GONE);
        } else {
            mholder.opera4View.setVisibility(View.VISIBLE);
        }

        if (doc.owner.id.equals(mId)) {
            mholder.opera2View.setVisibility(View.VISIBLE);
            mholder.opera3View.setVisibility(View.VISIBLE);
            mholder.expandView.setVisibility(View.VISIBLE);
        } else {
            mholder.opera3View.setVisibility(View.GONE);
            mholder.opera2View.setVisibility(View.GONE);
            mholder.expandView.setVisibility(View.GONE);
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

                fragment.chooseClasses(doc, position);

            }
        });

        mholder.opera3View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragment.confirmDel(doc.id, position);

            }
        });
        mholder.opera4View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.showMoreDlg(doc, position);
            }
        });

        mholder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Collaboration.TypeName.DIRECTORY_IN_LIBRARY.equals(doc.typeName)) {
                    fragment.enterNext(doc, position);
                } else if (context instanceof Classroom2Activity) {
                    if (Collaboration.isStreaming(doc.mimeType) || Collaboration.isVideo(doc.mimeType)) {
                        ((Classroom2Activity) context).enterPlayback(doc);
                    } else {
                        ((Classroom2Activity) context).getCollaBorateFragment().openDocInBoard(doc);

                    }

                    ((Classroom2Activity) context).exitDatabaseFragment();

                } else {
                    MaterialUtil.openMaterial(fragment.getActivity(), doc);
                    ((Classroom2Activity) context).exitDatabaseFragment();
                }


            }
        });
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
}
