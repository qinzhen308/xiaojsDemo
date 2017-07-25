package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Checkable;
import android.widget.CheckedTextView;
import android.widget.RelativeLayout;

import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/7/18.
 */

public class CheckableRelativeLayout extends RelativeLayout implements Checkable {

    private CheckedTextView mTextView = null;
    private Paint paint;
    private boolean showLine;

    public CheckableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public CheckableRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, CheckedTextView mTextView) {
        super(context, attrs, defStyleAttr);
        init(attrs);
        this.mTextView = mTextView;
    }

    private void init(AttributeSet attrs) {


        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.bottom_line);
        if (a != null) {
            showLine = a.getBoolean(R.styleable.bottom_line_showLine, false);
            a.recycle();
        }

        if (showLine) {
            setWillNotDraw(false);

            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(getResources().getColor(R.color.font_light_gray));
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(1);
        }

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
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        if (showLine) {
            canvas.drawLine(0, getHeight(), getWidth(), getHeight(), paint);
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
