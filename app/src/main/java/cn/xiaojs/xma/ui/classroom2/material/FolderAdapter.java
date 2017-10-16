package cn.xiaojs.xma.ui.classroom2.material;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;

/**
 * Created by maxiaobao on 2017/10/10.
 */

public class FolderAdapter extends RecyclerView.Adapter<FolderViewHolder> {

    private Context context;
    private List<LibDoc> libDocs;
    private boolean loading;

    private MoveFolderFragment fragment;

    public FolderAdapter(MoveFolderFragment fragment, List<LibDoc> libDocs) {
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.libDocs = libDocs;
    }


    @Override
    public FolderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return FolderViewHolder.createHolder(context, parent);
    }

    @Override
    public void onBindViewHolder(final FolderViewHolder holder, final int position) {


        final LibDoc doc = libDocs.get(position);

        holder.nameView.setText(doc.name);

        holder.descView.setTextColor(context.getResources().getColor(R.color.common_text));
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
        holder.descView.setText(sb);


        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.enterNext(doc, position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return libDocs == null ? 0 : libDocs.size();
    }


    public void setLoading(boolean loading) {
        this.loading = loading;
    }
}
