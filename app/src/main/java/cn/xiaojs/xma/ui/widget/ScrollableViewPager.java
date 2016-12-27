package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 可设置是否能左右划的ViewPager
 *
 * @author Kevin
 */
public class ScrollableViewPager extends ViewPager {

    public ScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollableViewPager(Context context) {
        super(context);
    }

    private boolean mNoScroll;

    public void setNoScroll(boolean noScroll){
        mNoScroll = noScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        if (mNoScroll){
            return false;
        }
      return super.onInterceptTouchEvent(arg0);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        if (mNoScroll){
            return false;
        }
        return super.onTouchEvent(arg0);
    }
}
