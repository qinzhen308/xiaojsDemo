package cn.xiaojs.xma.ui.contact2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;
import cn.xiaojs.xma.ui.contact2.model.FriendItem;
import cn.xiaojs.xma.ui.contact2.model.ItemTypes;
import cn.xiaojs.xma.ui.contact2.model.LabelItem;
import cn.xiaojs.xma.ui.contact2.viewholder.FriendsViewHolder;
import cn.xiaojs.xma.ui.contact2.viewholder.LabelViewHolder;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class FriendsAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<AbsContactItem> dataCollect;

    public FriendsAdapter(Context context, ArrayList<AbsContactItem> dataCollect) {
        this.context = context;
        this.dataCollect = dataCollect;
    }

    @Override
    public int getCount() {
        return dataCollect == null ? 0 : dataCollect.size();
    }

    @Override
    public AbsContactItem getItem(int position) {
        return dataCollect.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AbsContactItem contactItem = getItem(position);
        int itemType = contactItem.getItemType();
        if (itemType == ItemTypes.LABEL) {

            LabelViewHolder labelViewHolder = null;

            if (convertView != null && convertView.getTag() instanceof LabelViewHolder) {
                labelViewHolder = (LabelViewHolder) convertView.getTag();
            } else {

                convertView = LabelViewHolder.createView(context, parent);
                labelViewHolder = new LabelViewHolder(context, convertView);
                convertView.setTag(labelViewHolder);
            }

            LabelItem labelItem = (LabelItem) contactItem;

            labelViewHolder.labelView.setText(labelItem.getText());


        } else {

            FriendsViewHolder friendsViewHolder = null;

            if (convertView != null && convertView.getTag() instanceof FriendsViewHolder) {
                friendsViewHolder = (FriendsViewHolder) convertView.getTag();
            } else {
                convertView = FriendsViewHolder.createView(context, parent);
                friendsViewHolder = new FriendsViewHolder(context, convertView);
                convertView.setTag(friendsViewHolder);
            }

            FriendItem friendItem = (FriendItem) contactItem;
            friendsViewHolder.titleView.setText(friendItem.contact.name);
            friendsViewHolder.descView.setText("[哈勒普]");

            String avatorUrl = Account.getAvatar(
                    friendItem.contact.account, friendsViewHolder.avatorView.getMeasuredWidth());
            Glide.with(context)
                    .load(avatorUrl)
                    .transform(new CircleTransform(context))
                    .placeholder(R.drawable.default_avatar_grey)
                    .error(R.drawable.default_avatar_grey)
                    .into(friendsViewHolder.avatorView);
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return dataCollect.get(position).getItemType();
    }

    @Override
    public int getViewTypeCount() {
        return super.getViewTypeCount();
    }

    @Override
    public boolean isEnabled(int position) {
        return (dataCollect == null || dataCollect.get(position).getItemType() == ItemTypes.LABEL) ?
                false : true;
    }
}
