package cn.xiaojs.xma.ui.contact2.model;

import java.util.Comparator;

import cn.xiaojs.xma.ui.contact2.query.TextComparator;


/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ContactCompare implements Comparator<AbsContactItem> {

    @Override
    public int compare(AbsContactItem o1, AbsContactItem o2) {
        return TextComparator.compareIgnoreCase(o1.getCompare(), o2.getCompare());
    }
}
