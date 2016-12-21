package cn.xiaojs.xma.ui.base;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class BaseScrollTabLayout extends LinearLayout{

    public boolean isCanResponse = true;
    public OnMoveEventListener onMoveEventListener;
    public void setOnMoveEventListener(OnMoveEventListener onMoveEventListener) {
        this.onMoveEventListener = onMoveEventListener;
    }

    public BaseScrollTabLayout(Context context) {
        super(context);
    }

    public BaseScrollTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if(!isCanResponse){
            return true;
        }
        if(onMoveEventListener != null){
            onMoveEventListener.moveInterceptTouchEvent(event);
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(!isCanResponse){
            return true;
        }
        if(onMoveEventListener != null){
            onMoveEventListener.moveInterceptTouchEvent(event);
        }
        return  super.onInterceptTouchEvent(event);
    }
    float x;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isCanResponse){
            return true;
        }
        if(onMoveEventListener != null){
            onMoveEventListener.moveTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }


    public interface OnMoveEventListener{
        public void moveDispatchTouchEvent(MotionEvent event);
        public void moveInterceptTouchEvent(MotionEvent event);
        public void moveTouchEvent(MotionEvent event);
    }
    private int oldHeight = 0;
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(changed){
            int height = getMeasuredHeight();
            if(height != oldHeight && oldHeight != 0){
                if(onViewHeightChangeListener != null){
                    onViewHeightChangeListener.OnChangedListener(height);
                }
            }
            oldHeight = height;
            Log.e("onLayout","height"+height);
        }
        super.onLayout(changed, l, t, r, b);
    }
    public OnViewHeightChangeListener onViewHeightChangeListener;


    public void setOnViewHeightChangeListener(OnViewHeightChangeListener onViewHeightChangeListener) {
        this.onViewHeightChangeListener = onViewHeightChangeListener;
    }

    public interface OnViewHeightChangeListener{
        public void OnChangedListener(int nowHeight);
    }
}
