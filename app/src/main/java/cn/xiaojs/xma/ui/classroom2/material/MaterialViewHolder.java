package cn.xiaojs.xma.ui.classroom2.material;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/10/10.
 */

public class MaterialViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.root_lay)
    ConstraintLayout rootLayout;
    @BindView(R.id.icon)
    ImageView iconView;
    @BindView(R.id.name)
    TextView nameView;
    @BindView(R.id.desc)
    TextView descView;
    @BindView(R.id.expand_btn)
    ImageView expandView;

    @BindView(R.id.opera_wrapper)
    LinearLayout operaView;
    @BindView(R.id.opera1)
    TextView opera1View;
    @BindView(R.id.opera2)
    TextView opera2View;
    @BindView(R.id.opera3)
    TextView opera3View;
    @BindView(R.id.opera4)
    TextView opera4View;


    public MaterialViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void showOpera(boolean b) {
        if (b) {
            operaView.setVisibility(View.VISIBLE);
            expandView.setImageResource(R.drawable.ic_arrow_up);
        } else {
            operaView.setVisibility(View.GONE);
            expandView.setImageResource(R.drawable.ic_arrow_down);
        }
    }

}
