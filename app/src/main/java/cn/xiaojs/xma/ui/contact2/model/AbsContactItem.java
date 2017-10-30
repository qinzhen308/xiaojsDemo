package cn.xiaojs.xma.ui.contact2.model;


import android.support.annotation.NonNull;

import cn.xiaojs.xma.ui.contact2.query.TextComparator;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public abstract class AbsContactItem implements Comparable<AbsContactItem>{

    /**
     * 所属的类型
     */
    public abstract int getItemType();

    /**
     * 所属的分组
     */
    public abstract String belongsGroup();
    public abstract String getCompare();

    protected final int compareType(AbsContactItem item) {
        return compareType(getItemType(), item.getItemType());
    }

    public static int compareType(int lhs, int rhs) {
        return lhs - rhs;
    }

    @Override
    public int compareTo(@NonNull AbsContactItem o) {
        return TextComparator.compareIgnoreCase(getCompare(), o.getCompare());
    }
}
