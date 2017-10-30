package cn.xiaojs.xma.ui.contact2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.classroom2.material.ClassViewHolder;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;
import cn.xiaojs.xma.ui.contact2.model.FriendItem;
import cn.xiaojs.xma.ui.contact2.model.ItemTypes;
import cn.xiaojs.xma.ui.contact2.model.LabelItem;
import cn.xiaojs.xma.ui.contact2.viewholder.ClassroomViewHolder;
import cn.xiaojs.xma.ui.contact2.viewholder.FriendsViewHolder;
import cn.xiaojs.xma.ui.contact2.viewholder.LabelViewHolder;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ClassroomsAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<Contact> dataCollect;

    public ClassroomsAdapter(Context context, ArrayList<Contact> dataCollect) {
        this.context = context;
        this.dataCollect = dataCollect;
    }

    @Override
    public int getCount() {
        return dataCollect == null ? 0 : dataCollect.size();
    }

    @Override
    public Contact getItem(int position) {
        return dataCollect.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ClassroomViewHolder viewHolder = null;

        if (convertView == null) {

            convertView = ClassroomViewHolder.createView(context, parent);
            viewHolder = new ClassroomViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ClassroomViewHolder) convertView.getTag();
        }

        Contact contactItem = getItem(position);
        viewHolder.avatorTextView.setText(String.valueOf(contactItem.title.charAt(0)));
        viewHolder.titleView.setText(contactItem.title);
        viewHolder.descView.setText("[Oh YES]");

        return convertView;
    }

}
