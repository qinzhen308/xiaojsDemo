package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;

/**
 * Created by maxiaobao on 2017/7/18.
 */

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    private CheckedTextView mTextView = null;

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, CheckedTextView mTextView) {
        super(context, attrs, defStyleAttr);
        this.mTextView = mTextView;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int count = this.getChildCount();
        for (int i = 0; i < count; i++) {
            View v = getChildAt(i);

            if (v instanceof CheckedTextView) {
                mTextView = (CheckedTextView) v;
            }
        }

    }

    @Override
    public void setChecked(boolean checked) {
        mTextView.setChecked(checked);
    }

    @Override
    public boolean isChecked() {
        return mTextView.isChecked();
    }

    @Override
    public void toggle() {
        mTextView.toggle();
    }
}
