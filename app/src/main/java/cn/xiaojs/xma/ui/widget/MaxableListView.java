package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by maxiaobao on 2017/4/9.
 */

public class MaxableListView extends ListView {

    private int listViewHeight = -1;

    public int getListViewHeight() {
        return listViewHeight;
    }

    public void setListViewHeight(int listViewHeight) {
        this.listViewHeight = listViewHeight;
    }


    public MaxableListView(Context context) {
        super(context);
    }

    public MaxableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (listViewHeight > -1) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(listViewHeight,
                    MeasureSpec.AT_MOST);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
