package cn.xiaojs.xma.ui.contact2.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.contact2.model.FriendItem;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class LabelViewHolder extends AbsContactViewHolder<FriendItem> {

    @BindView(R.id.label)
    public TextView labelView;


    public LabelViewHolder(Context context, View view) {
        super(context, view);
    }

    public static View createView(Context context,ViewGroup container) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.layout_contact2_label_item, container, false);
    }
}
