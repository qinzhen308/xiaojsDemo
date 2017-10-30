package cn.xiaojs.xma.ui.contact2.model;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class LabelItem extends AbsContactItem {

    private final String text;

    public LabelItem(String text) {
        this.text = text;
    }

    @Override
    public int getItemType() {
        return ItemTypes.LABEL;
    }

    @Override
    public String belongsGroup() {
        return text;
    }

    public String getText() {
        return text;
    }

    @Override
    public String getCompare() {
        return text;
    }
}
