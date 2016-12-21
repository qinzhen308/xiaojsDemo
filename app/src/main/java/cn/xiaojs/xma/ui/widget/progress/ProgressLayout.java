package cn.xiaojs.xma.ui.widget.progress;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2016/11/22.
 */

public class ProgressLayout extends LinearLayout {

    private float mCornerRadius = 0f;
    private int mBackgroundColor;

    public ProgressLayout(Context context) {
        super(context);
        init();
    }

    public ProgressLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ProgressLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        int color = getContext().getResources().getColor(R.color.white);
        mCornerRadius = getContext().getResources().getDimensionPixelSize(R.dimen.px20);


        initBackground(color, mCornerRadius);
    }

    private void initBackground(int color, float cornerRadius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setColor(color);
        drawable.setCornerRadius(cornerRadius);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setBackground(drawable);
        } else {
            //noinspection deprecation
            setBackgroundDrawable(drawable);
        }
    }

    public void setCornerRadius(float radius) {
        mCornerRadius = dpToPixel(radius, getContext());
        initBackground(mBackgroundColor, mCornerRadius);
    }

    public void setBaseColor(int color) {
        mBackgroundColor = color;
        initBackground(mBackgroundColor, mCornerRadius);
    }

    private int dpToPixel(float dp, Context context) {

        float scale = context.getResources().getDisplayMetrics().density;

        return (int) (dp * scale);
    }
}
