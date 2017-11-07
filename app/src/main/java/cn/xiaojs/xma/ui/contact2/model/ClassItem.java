package cn.xiaojs.xma.ui.contact2.model;

import android.text.TextUtils;

import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.ui.contact2.query.TextComparator;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ClassItem extends AbsContactItem {


    public Contact contact;
    private final int dataItemType;
    private final String belong;

    public ClassItem(Contact contact) {
        this.dataItemType = ItemTypes.CLASSES;
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
        return contact != null ? (belong + contact.title) : null;
        //return contact != null ? belong : null;
    }

    private String getBelongsGroup() {

        if (contact == null) {
            return ContactGroupStrategy.GROUP_NULL;
        }

        String group = TextComparator.getLeadingUp(contact.title);
        return !TextUtils.isEmpty(group) ? group : ContactGroupStrategy.GROUP_SHARP;
    }

}
