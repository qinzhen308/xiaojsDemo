package cn.xiaojs.xma.ui.classroom2.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by maxiaobao on 2017/10/31.
 */

public class SwapRecylcerView extends RecyclerView {

    private TouchEventListener touchEventListener;

    public interface TouchEventListener{
        boolean patchTouchEvent(MotionEvent ev);
    }

    public SwapRecylcerView(Context context) {
        super(context);
    }

    public SwapRecylcerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwapRecylcerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTouchEventListener(TouchEventListener touchEventListener) {
        this.touchEventListener = touchEventListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (touchEventListener != null && touchEventListener.patchTouchEvent(ev))
            return true;
        return super.dispatchTouchEvent(ev);
    }
}
