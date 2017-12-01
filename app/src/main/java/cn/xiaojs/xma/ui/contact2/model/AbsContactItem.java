package cn.xiaojs.xma.ui.contact2.model;


import android.support.annotation.NonNull;

import cn.xiaojs.xma.ui.contact2.query.TextComparator;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public abstract class AbsContactItem implements Comparable<AbsContactItem> {


    private boolean top;

    /**
     * 所属的类型
     */
    public abstract int getItemType();

    /**
     * 所属的分组
     */
    public abstract String belongsGroup();

    public abstract String getCompare();

    public boolean isTop() {
        return top;
    }

    public void setTop(boolean top) {
        this.top = top;
    }

    protected final int compareType(AbsContactItem item) {
        return compareType(getItemType(), item.getItemType());
    }

    public static int compareType(int lhs, int rhs) {
        return lhs - rhs;
    }

    @Override
    public int compareTo(@NonNull AbsContactItem o) {

        if (top && o.top)
            return 0;

        if (top)
            return -1;

        if (o.top)
            return 1;

        return TextComparator.compareIgnoreCase(getCompare(), o.getCompare());
    }
}
