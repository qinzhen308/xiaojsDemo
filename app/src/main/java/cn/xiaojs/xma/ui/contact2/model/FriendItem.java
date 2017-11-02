package cn.xiaojs.xma.ui.contact2.model;

import android.text.TextUtils;

import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.contact2.query.TextComparator;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class FriendItem extends AbsContactItem {


    public Contact contact;
    private final int dataItemType;
    private final String belong;

    public FriendItem(Contact contact) {
        this.dataItemType = ItemTypes.FRIEND;
        this.contact = contact;
        belong = getBelongsGroup();

    }

    @Override
    public int getItemType() {
        return this.dataItemType;
    }

    @Override
    public String belongsGroup() {
        return belong;
    }

    @Override
    public String getCompare() {
        return contact != null ? (belong + contact.name) : null;
        //return contact != null ? belong : null;
    }

    private String getBelongsGroup() {

        if (contact == null) {
            return ContactGroupStrategy.GROUP_NULL;
        }

        String group = TextComparator.getLeadingUp(contact.name);
        return !TextUtils.isEmpty(group) ? group : ContactGroupStrategy.GROUP_SHARP;
    }

}
