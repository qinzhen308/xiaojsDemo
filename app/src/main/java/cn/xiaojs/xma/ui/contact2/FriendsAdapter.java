package cn.xiaojs.xma.ui.contact2;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.AccountDataManager;
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
    private int choiceMode;

    public FriendsAdapter(Context context, ArrayList<AbsContactItem> dataCollect, int choiceMode) {
        this.context = context;
        this.dataCollect = dataCollect;
        this.choiceMode = choiceMode;
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

            if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
                friendsViewHolder.checkedTextView.setVisibility(View.VISIBLE);
            } else {
                friendsViewHolder.checkedTextView.setVisibility(View.GONE);
            }

            friendsViewHolder.titleView.setText(friendItem.contact.name);

            if (TextUtils.isEmpty(friendItem.contact.title)) {
                friendsViewHolder.descView.setText(R.string.contact_empty_tips);
            } else {
                friendsViewHolder.descView.setText(friendItem.contact.title);
            }


            if (AccountDataManager.isXiaojsAccount(friendItem.contact.account)) {
                friendsViewHolder.avatorView.setImageResource(R.drawable.ic_im_xiaojs);
            }else {
                String avatorUrl = Account.getAvatar(
                        friendItem.contact.account, friendsViewHolder.avatorView.getMeasuredWidth());
                Glide.with(context)
                        .load(avatorUrl)
                        .transform(new CircleTransform(context))
                        .placeholder(R.drawable.ic_defaultavatar)
                        .error(R.drawable.ic_defaultavatar)
                        .into(friendsViewHolder.avatorView);
            }
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
