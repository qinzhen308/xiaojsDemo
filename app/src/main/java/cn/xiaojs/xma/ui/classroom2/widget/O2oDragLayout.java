package cn.xiaojs.xma.ui.classroom2.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by maxiaobao on 2017/11/20.
 */

public class O2oDragLayout extends RelativeLayout {
//    private ViewDragHelper dragHelper;
//    private boolean dragable;
//    private Point lastPos = new Point();
//    private boolean isFirst;
//
//    private View liveView;

    public O2oDragLayout(Context context) {
        super(context);
        //init();
    }

    public O2oDragLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        //init();
    }

    public O2oDragLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //init();
    }

//    private void init() {
//        dragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
//            @Override
//            public boolean tryCaptureView(View child, int pointerId) {
//                if (child instanceof BaseLiveView) {
//                    return true;
//                }
//                return true;
//            }
//
//            @Override
//            public int clampViewPositionHorizontal(View child, int left, int dx) {
//                final int leftBound = getPaddingLeft();
//                final int rightBound = getWidth() - child.getWidth();
//                final int newLeft = Math.min(Math.max(left, leftBound), rightBound);
//                return newLeft;
//            }
//
//            @Override
//            public int clampViewPositionVertical(View child, int top, int dy) {
//                final int topBound = getPaddingTop();
//                final int bottomBound = getHeight() - child.getHeight();
//                final int newTop = Math.min(Math.max(top, topBound), bottomBound);
//                return newTop;
//            }
//
//            @Override
//            public int getViewHorizontalDragRange(View child) {
//                return liveView.getWidth();
//            }
//
//            @Override
//            public int getViewVerticalDragRange(View child) {
//                return liveView.getHeight();
//            }
//
//            @Override
//            public void onViewReleased(View releasedChild, float xvel, float yvel) {
//                //super.onViewReleased(releasedChild, xvel, yvel);
//
////                int mY=releasedChild.getTop();
////                if(releasedChild.getTop()<0){
////                    mY=0;
////                }
////                if(releasedChild.getBottom()>getHeight()){
////                    mY=getHeight()-releasedChild.getMeasuredHeight();
////                }
////                if(releasedChild.getRight()-releasedChild.getMeasuredWidth()/2>getWidth()/2){
////                    lastPos.x= (int) (getWidth()-liveView.getWidth()*1);
////                    lastPos.y=mY;
////                    dragHelper.settleCapturedViewAt(lastPos.x,lastPos.y);
////
////                }else{
////                    lastPos.x= (int) (0-liveView.getWidth()*(1-1));
////                    lastPos.y=mY;
////                    dragHelper.settleCapturedViewAt(lastPos.x, lastPos.y);
////                }
////
//                lastPos.x = liveView.getLeft();
//                lastPos.y = liveView.getTop();
//                dragHelper.settleCapturedViewAt(lastPos.x, lastPos.y);
//
//            }
//        });
//    }
//
//    public void setDragEnable(boolean dragable) {
//        this.dragable = dragable;
//    }
//
//    public void setLiveView(View liveView) {
//        this.liveView = liveView;
//    }
//
//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//
//        if (!dragable)
//            return super.onInterceptTouchEvent(ev);
//
//
//        return dragHelper.shouldInterceptTouchEvent(ev);
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        if (dragable) {
//            dragHelper.processTouchEvent(event);
//            return true;
//        }
//
//        return super.onTouchEvent(event);
//
//    }
//
//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);
//
//        if (liveView != null) {
//            liveView.layout(lastPos.x, lastPos.y,
//                    lastPos.x + liveView.getMeasuredWidth(),
//                    lastPos.y + liveView.getMeasuredHeight());
//        }
//
//    }
}
