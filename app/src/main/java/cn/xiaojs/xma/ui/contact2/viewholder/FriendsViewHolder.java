package cn.xiaojs.xma.ui.contact2.viewholder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.contact2.model.FriendItem;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class FriendsViewHolder extends AbsContactViewHolder<FriendItem> {

    @BindView(R.id.avator)
    public ImageView avatorView;
    @BindView(R.id.title)
    public TextView titleView;
    @BindView(R.id.desc)
    public TextView descView;


    public FriendsViewHolder(Context context, View view) {
        super(context, view);
    }

    public static View createView(Context context,ViewGroup container) {

        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.layout_contact2_friend_item, container, false);

    }
}
