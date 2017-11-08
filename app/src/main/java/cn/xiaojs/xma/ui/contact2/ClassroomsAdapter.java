package cn.xiaojs.xma.ui.contact2;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.classroom2.chat.GroupSessionFragment;
import cn.xiaojs.xma.ui.classroom2.material.ClassViewHolder;
import cn.xiaojs.xma.ui.contact2.model.AbsContactItem;
import cn.xiaojs.xma.ui.contact2.model.ClassItem;
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
    private ArrayList<AbsContactItem> dataCollect;
    private int choiceMode;

    public interface OnItemClickListener{
        void OnItemClick(Contact contact);
    }

    public ClassroomsAdapter(Context context, int choiceMode) {
        this.context = context;
        this.dataCollect = new ArrayList<>();
        this.choiceMode = choiceMode;
    }

    public void addDatas(ArrayList<AbsContactItem> data) {
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
    public AbsContactItem getItem(int position) {
        return dataCollect.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final AbsContactItem contactItem = getItem(position);
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


        }else {

            ClassroomViewHolder classViewHolder = null;

            if (convertView != null && convertView.getTag() instanceof ClassItem) {
                classViewHolder = (ClassroomViewHolder) convertView.getTag();

            } else {
                convertView = ClassroomViewHolder.createView(context, parent);
                classViewHolder = new ClassroomViewHolder(convertView);
                convertView.setTag(classViewHolder);
            }


            ClassItem classItem = (ClassItem) contactItem;

            if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {
                classViewHolder.checkedTextView.setVisibility(View.VISIBLE);
            } else {
                classViewHolder.checkedTextView.setVisibility(View.GONE);
            }


            String titleStr = classItem.contact.title;

            String title = TextUtils.isEmpty(titleStr) ?
                    "#" : String.valueOf(titleStr.trim().charAt(0));

            classViewHolder.avatorTextView.setIconWithText(title);
            classViewHolder.titleView.setText(titleStr);
            classViewHolder.descView.setText(classItem.contact.owner);
        }

        return convertView;
    }

}
