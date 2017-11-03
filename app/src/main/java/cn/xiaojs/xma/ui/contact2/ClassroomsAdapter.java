package cn.xiaojs.xma.ui.contact2;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.classroom2.chat.GroupSessionFragment;
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

    public interface OnItemClickListener{
        void OnItemClick(Contact contact);
    }

    public ClassroomsAdapter(Context context) {
        this.context = context;
        this.dataCollect = new ArrayList<>();
    }

    public void addDatas(ArrayList<Contact> data) {
        if (data !=null) {
            dataCollect = data;
            notifyDataSetChanged();
        }

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

        final Contact contactItem = getItem(position);

        String title = TextUtils.isEmpty(contactItem.title) ? "#" : String.valueOf(contactItem.title.trim().charAt(0));

        viewHolder.avatorTextView.setIconWithText(title);
        viewHolder.titleView.setText(contactItem.title);
        viewHolder.descView.setText("[Oh YES]");

        return convertView;
    }

}
