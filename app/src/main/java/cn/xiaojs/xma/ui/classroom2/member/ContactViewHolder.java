package cn.xiaojs.xma.ui.classroom2.member;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.MessageImageView;

/**
 * Created by maxiaobao on 2017/10/13.
 */

public class ContactViewHolder extends RecyclerView.ViewHolder{

    @BindView(R.id.root_lay)
    RelativeLayout rootLayout;

    @BindView(R.id.check_btn)
    CheckBox checkBoxView;

    @BindView(R.id.avator)
    MessageImageView avatorView;
    @BindView(R.id.name)
    TextView nameView;
    @BindView(R.id.desc)
    TextView descView;


    public ContactViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public static ContactViewHolder createViewHolder(Context context, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);

        return new ContactViewHolder(
                inflater.inflate(R.layout.layout_classroom2_choose_contact_item,parent,false));

    }
}
