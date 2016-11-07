package com.benyuan.xiaojs.ui.view;

import android.content.Context;
import android.widget.Button;
import android.widget.Checkable;

/**
 * Created by maxiaobao on 2016/11/6.
 */

public class TagView extends Button implements Checkable{

    private boolean isChecked;

    public TagView(Context context) {
        super(context);
    }

    @Override
    public void setChecked(boolean checked) {

        if (this.isChecked != checked)
        {
            this.isChecked = checked;
            refreshDrawableState();
        }

    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        setChecked(!isChecked);
    }
}
