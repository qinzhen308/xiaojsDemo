package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;


/**
 * Created by Administrator on 2017/5/22.
 */

public class StrikeDownLinearLayout extends LinearLayout {
    public StrikeDownLinearLayout(Context context) {
        super(context);
    }

    public StrikeDownLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public StrikeDownLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    float downX=0;
    float downY=0;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX=event.getX();
                downY=event.getY();
                return true;

            case MotionEvent.ACTION_MOVE:
                if(Math.pow(downX-event.getX(),2)+Math.pow(downY-event.getY(),2)>Math.pow((double) ViewConfiguration.getTouchSlop(),2)){//失效
                    getParent().requestDisallowInterceptTouchEvent(false);
                }else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(Math.pow(downX-event.getX(),2)+Math.pow(downY-event.getY(),2)<Math.pow((double) ViewConfiguration.getTouchSlop(),2)){//有效点击
                    if(onClickListener!=null){
                        onClickListener.onClick();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX=event.getX();
                downY=event.getY();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;

            case MotionEvent.ACTION_MOVE:
                if(Math.pow(downX-event.getX(),2)+Math.pow(downY-event.getY(),2)>Math.pow((double) ViewConfiguration.getTouchSlop(),2)){//失效
                    getParent().requestDisallowInterceptTouchEvent(false);
                }else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
                if(Math.pow(downX-event.getX(),2)+Math.pow(downY-event.getY(),2)<Math.pow((double) ViewConfiguration.getTouchSlop(),2)){//有效点击
                    if(onClickListener!=null){
                        onClickListener.onClick();
                    }
                }
                break;
        }
        return super.onInterceptTouchEvent(event);
    }

    SDOnClickListener onClickListener;
    public void setOnclickListener(SDOnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    public interface SDOnClickListener{
        void onClick();
    }


}
