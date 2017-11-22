package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.MessageImageView;

/**
 * Created by maxiaobao on 2017/11/13.
 */

public class MemberViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.root_lay)
    RelativeLayout rootLayout;
    @BindView(R.id.avator)
    MessageImageView avatorView;
    @BindView(R.id.name)
    TextView nameView;
    @BindView(R.id.flag_mark)
    TextView flagMarkView;
    @BindView(R.id.desc)
    TextView descView;

    public MemberViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public static View createView(Context context, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.layout_classroom2_member_item, parent, false);

    }
}
