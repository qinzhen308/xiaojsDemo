package cn.xiaojs.xma.ui.classroom2.material;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.widget.ColorIconTextView;

/**
 * Created by maxiaobao on 2017/10/11.
 */

public class ClassViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.root_lay)
    RelativeLayout rootLayout;
    @BindView(R.id.check_btn)
    CheckedTextView checkedView;
    @BindView(R.id.avator)
    ColorIconTextView avatorView;
    @BindView(R.id.name)
    TextView nameView;
    @BindView(R.id.desc)
    TextView descView;

    public ClassViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public static ClassViewHolder createView(Context context, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        return new ClassViewHolder(
                layoutInflater.inflate(R.layout.layout_classroom2_choose_class_item,parent,false));
    }
}
