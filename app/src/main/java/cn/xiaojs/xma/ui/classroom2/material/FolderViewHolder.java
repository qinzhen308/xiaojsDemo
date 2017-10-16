package cn.xiaojs.xma.ui.classroom2.material;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/10/10.
 */

public class FolderViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.root_lay)
    ConstraintLayout rootLayout;
    @BindView(R.id.icon)
    ImageView iconView;
    @BindView(R.id.name)
    TextView nameView;
    @BindView(R.id.desc)
    TextView descView;


    public FolderViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static FolderViewHolder createHolder(Context context, ViewGroup parent){

        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new FolderViewHolder(
                layoutInflater.inflate(R.layout.layout_classroom2_folder_item, parent, false));
    }

}
