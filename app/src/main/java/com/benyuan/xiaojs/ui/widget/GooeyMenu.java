package com.benyuan.xiaojs.ui.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;

import com.benyuan.xiaojs.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anshul on 24/06/15.
 */
public class GooeyMenu extends View {

    private static final long ANIMATION_DURATION = 1000;
    private static final int DEFUALT_MENU_NO = 5;
    private final float START_ANGLE = 0f;
    private final float END_ANGLE = 45f;
    private int mNumberOfMenu;//Todo
    private final float BEZIER_CONSTANT = 0.551915024494f;// pre-calculated value

    private int mFabButtonRadius;
    private int mMenuButtonRadius;
    private int mGab;
    private int mCenterX;
    private int mCenterY;
    private Paint mCirclePaint;
    private Paint mShadowPaint;
    private ArrayList<CirclePoint> mMenuPoints = new ArrayList<>();
    private ArrayList<ObjectAnimator> mShowAnimation = new ArrayList<>();
    private ArrayList<ObjectAnimator> mHideAnimation = new ArrayList<>();
    private ValueAnimator mBezierAnimation, mBezierEndAnimation, mRotationAnimation;
    private boolean isMenuVisible = false;
    private Float bezierConstant = BEZIER_CONSTANT;
    private Bitmap mPlusBitmap;
    private float mRotationAngle;
    private ValueAnimator mRotationReverseAnimation;
    private GooeyMenuInterface mGooeyMenuInterface;
    private Paint mCircleBorder;
    private List<Drawable> mDrawableArray;

    public static final int[] STATE_ACTIVE =
            {android.R.attr.state_enabled, android.R.attr.state_active};
    public static final int[] STATE_PRESSED =
            {android.R.attr.state_enabled, -android.R.attr.state_active,
                    android.R.attr.state_pressed};

    private ArcCake mArcCake;
    private ObjectAnimator mShowArcCakeAnim;
    private ObjectAnimator mHideArcCakeAnim;
    public GooeyMenu(Context context) {
        super(context);
        init(null);
    }

    public GooeyMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }


    public GooeyMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public GooeyMenu(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.GooeyMenu,
                    0, 0);
            try {


                mNumberOfMenu = typedArray.getInt(R.styleable.GooeyMenu_no_of_menu, DEFUALT_MENU_NO);
                mFabButtonRadius = (int) typedArray.getDimension(R.styleable.GooeyMenu_fab_radius, getResources().getDimension(R.dimen.px80));
                mMenuButtonRadius = (int) typedArray.getDimension(R.styleable.GooeyMenu_menu_radius, getResources().getDimension(R.dimen.px40));
                mGab = (int) typedArray.getDimension(R.styleable.GooeyMenu_gap_between_menu_fab, getResources().getDimensionPixelSize(R.dimen.px160));

                TypedValue outValue = new TypedValue();
                // Read array of target drawables
                if (typedArray.getValue(R.styleable.GooeyMenu_menu_drawable, outValue)) {
                    Resources res = getContext().getResources();
                    TypedArray array = res.obtainTypedArray(outValue.resourceId);
                    mDrawableArray = new ArrayList<>(array.length());
                    for (int i = 0; i < array.length(); i++) {
                        TypedValue value = array.peekValue(i);
                        mDrawableArray.add(getResources().getDrawable(value != null ? value.resourceId : 0));
                    }
                    array.recycle();
                }else {
                    mDrawableArray = new ArrayList<>();
                }

            } finally {
                typedArray.recycle();
                typedArray = null;
            }

        }

        mCirclePaint = new Paint();
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setColor(getResources().getColor(R.color.main_orange));
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mShadowPaint = new Paint();
        mShadowPaint.setAntiAlias(true);
        mShadowPaint.setColor(getResources().getColor(R.color.translucent_50));
        mShadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mCircleBorder = new Paint(mCirclePaint);
        mCircleBorder.setStyle(Paint.Style.STROKE);
        mCircleBorder.setStrokeWidth(1f);
        mCircleBorder.setColor(getResources().getColor(R.color.main_orange));

        mBezierEndAnimation = ValueAnimator.ofFloat(BEZIER_CONSTANT + .2f, BEZIER_CONSTANT);
        mBezierEndAnimation.setInterpolator(new LinearInterpolator());
        mBezierEndAnimation.setDuration(300);
        mBezierEndAnimation.addUpdateListener(mBezierUpdateListener);

        mBezierAnimation = ValueAnimator.ofFloat(BEZIER_CONSTANT - .02f, BEZIER_CONSTANT + .2f);
        mBezierAnimation.setDuration(ANIMATION_DURATION / 4);
        mBezierAnimation.setRepeatCount(4);
        mBezierAnimation.setInterpolator(new LinearInterpolator());
        mBezierAnimation.addUpdateListener(mBezierUpdateListener);
        mBezierAnimation.addListener(mBezierAnimationListener);

        mRotationAnimation = ValueAnimator.ofFloat(START_ANGLE, END_ANGLE);
        mRotationAnimation.setDuration(ANIMATION_DURATION / 4);
        mRotationAnimation.setInterpolator(new AccelerateInterpolator());
        mRotationAnimation.addUpdateListener(mRotationUpdateListener);
        mRotationReverseAnimation = ValueAnimator.ofFloat(END_ANGLE, START_ANGLE);
        mRotationReverseAnimation.setDuration(ANIMATION_DURATION / 4);
        mRotationReverseAnimation.setInterpolator(new AccelerateInterpolator());
        mRotationReverseAnimation.addUpdateListener(mRotationUpdateListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int desiredWidth;
        int desiredHeight;
        desiredWidth = getMeasuredWidth();
        desiredHeight = getContext().getResources().getDimensionPixelSize(R.dimen.px400);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;
        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }

        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h - mFabButtonRadius;
        for (int i = 0; i < mNumberOfMenu; i++) {
            CirclePoint circlePoint = new CirclePoint();
            circlePoint.setRadius(0);
            circlePoint.setAngle((Math.PI / (mNumberOfMenu - 1)) * (i));
            mMenuPoints.add(circlePoint);
            ObjectAnimator animShow = ObjectAnimator.ofFloat(mMenuPoints.get(i), "Radius", 0f, mGab);
            animShow.setDuration(ANIMATION_DURATION);
            animShow.setInterpolator(new AnticipateOvershootInterpolator());
            animShow.setStartDelay((ANIMATION_DURATION * (mNumberOfMenu - i)) / 10);
            animShow.addUpdateListener(mUpdateListener);
            mShowAnimation.add(animShow);
            ObjectAnimator animHide = animShow.clone();
            animHide.setFloatValues(mGab, 0f);
            animHide.setStartDelay((ANIMATION_DURATION * i) / 10);
            mHideAnimation.add(animHide);

            if (mDrawableArray != null) {
                for (Drawable drawable : mDrawableArray)
                    drawable.setBounds(0, 0, /*2 * */mMenuButtonRadius,/* 2 * */mMenuButtonRadius);
            }
        }

        mArcCake = new ArcCake();
        mArcCake.setRadius(0);
        mArcCake.setStartAngle(180);
        mArcCake.setSweepAngle(180);
        mShowArcCakeAnim = ObjectAnimator.ofFloat(mArcCake,"Radius",0f,mGab);
        mShowArcCakeAnim.setDuration(ANIMATION_DURATION);
        mShowArcCakeAnim.setInterpolator(new AnticipateOvershootInterpolator());
        mShowArcCakeAnim.addUpdateListener(mUpdateListener);
        mHideArcCakeAnim = mShowArcCakeAnim.clone();
        mHideArcCakeAnim.setFloatValues(mGab,0f);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mPlusBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.plus);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPlusBitmap = null;
        mBezierAnimation = null;
        mHideAnimation.clear();
        mHideAnimation = null;
        mShowAnimation.clear();
        mHideAnimation = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isMenuVisible){
            canvas.save();
            canvas.translate(mCenterX, mCenterY);
            RectF t = new RectF(-mArcCake.radius,-mArcCake.radius,mArcCake.radius,mArcCake.radius);
            canvas.drawArc(t,mArcCake.startAngle,mArcCake.sweepAngle,true,mShadowPaint);
            canvas.restore();
        }


        for (int i = 0; i < mNumberOfMenu; i++) {
            CirclePoint circlePoint = mMenuPoints.get(i);
            float x = (float) (circlePoint.radius * Math.cos(circlePoint.angle));
            float y = (float) (circlePoint.radius * Math.sin(circlePoint.angle));
            canvas.drawCircle(x + mCenterX, mCenterY - y, mMenuButtonRadius, mCirclePaint);
            if (i < mDrawableArray.size()) {
                canvas.save();
                canvas.translate(x + mCenterX - mMenuButtonRadius / 2, mCenterY - y - mMenuButtonRadius / 2);
                mDrawableArray.get(i).draw(canvas);
                canvas.restore();
            }
        }

        canvas.save();
        canvas.translate(mCenterX, mCenterY);
        Path path = createPath();
        canvas.drawPath(path, mCirclePaint);
        //canvas.drawPath(path, mCircleBorder);
        canvas.rotate(mRotationAngle);
        canvas.drawBitmap(mPlusBitmap, -mPlusBitmap.getWidth() / 2, -mPlusBitmap.getHeight() / 2, mCirclePaint);
        canvas.restore();
    }

    // Use Bezier path to create circle,
    /*    P_0 = (0,1), P_1 = (c,1), P_2 = (1,c), P_3 = (1,0)
        P_0 = (1,0), P_1 = (1,-c), P_2 = (c,-1), P_3 = (0,-1)
        P_0 = (0,-1), P_1 = (-c,-1), P_3 = (-1,-c), P_4 = (-1,0)
        P_0 = (-1,0), P_1 = (-1,c), P_2 = (-c,1), P_3 = (0,1)
        with c = 0.551915024494*/

    private Path createPath() {
        Path path = new Path();
        float c = bezierConstant * mFabButtonRadius;

        path.moveTo(0, mFabButtonRadius);
        path.cubicTo(bezierConstant * mFabButtonRadius, mFabButtonRadius, mFabButtonRadius, BEZIER_CONSTANT * mFabButtonRadius, mFabButtonRadius, 0);
        path.cubicTo(mFabButtonRadius, BEZIER_CONSTANT * mFabButtonRadius * (-1), c, (-1) * mFabButtonRadius, 0, (-1) * mFabButtonRadius);
        path.cubicTo((-1) * c, (-1) * mFabButtonRadius, (-1) * mFabButtonRadius, (-1) * BEZIER_CONSTANT * mFabButtonRadius, (-1) * mFabButtonRadius, 0);
        path.cubicTo((-1) * mFabButtonRadius, BEZIER_CONSTANT * mFabButtonRadius, (-1) * bezierConstant * mFabButtonRadius, mFabButtonRadius, 0, mFabButtonRadius);

        return path;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isGooeyMenuTouch(event)) {
                    return true;
                }
                int menuItem = isMenuItemTouched(event);
                if (isMenuVisible && menuItem > 0) {
                    if (menuItem <= mDrawableArray.size()) {
                        mDrawableArray.get(mMenuPoints.size() - menuItem).setState(STATE_PRESSED);
                        invalidate();
                    }

                    return true;
                }
                return false;
            case MotionEvent.ACTION_UP:
                if (isGooeyMenuTouch(event)) {
                    mBezierAnimation.start();
                    cancelAllAnimation();
                    if (isMenuVisible) {
                        startHideAnimate();
                        if (mGooeyMenuInterface != null) {
                            mGooeyMenuInterface.menuClose();
                        }
                    } else {
                        startShowAnimate();
                        if (mGooeyMenuInterface != null) {
                            mGooeyMenuInterface.menuOpen();
                        }
                    }
                    isMenuVisible = !isMenuVisible;
                    return true;
                }

                if (isMenuVisible) {
                    menuItem = isMenuItemTouched(event);
                    invalidate();
                    if (menuItem > 0) {
                        if (menuItem <= mDrawableArray.size()) {
                            mDrawableArray.get(mMenuPoints.size() - menuItem).setState(STATE_ACTIVE);
                            postInvalidateDelayed(1000);
                        }
                        if (mGooeyMenuInterface != null) {
                            mGooeyMenuInterface.menuItemClicked(menuItem);
                        }
                        return true;
                    }
                }
                return false;

        }
        return true;
    }

    private int isMenuItemTouched(MotionEvent event) {

        if (!isMenuVisible) {
            return -1;
        }

        for (int i = 0; i < mMenuPoints.size(); i++) {
            CirclePoint circlePoint = mMenuPoints.get(i);
            float x = (float) (mGab * Math.cos(circlePoint.angle)) + mCenterX;
            float y = mCenterY - (float) (mGab * Math.sin(circlePoint.angle));
            if (event.getX() >= x - mMenuButtonRadius && event.getX() <= x + mMenuButtonRadius) {
                if (event.getY() >= y - mMenuButtonRadius && event.getY() <= y + mMenuButtonRadius) {
                    return mMenuPoints.size() - i;
                }
            }
        }

        return -1;
    }

    public void setOnMenuListener(GooeyMenuInterface onMenuListener) {
        mGooeyMenuInterface = onMenuListener;
    }

    public boolean isGooeyMenuTouch(MotionEvent event) {
        if (event.getX() >= mCenterX - mFabButtonRadius && event.getX() <= mCenterX + mFabButtonRadius) {
            if (event.getY() >= mCenterY - mFabButtonRadius && event.getY() <= mCenterY + mFabButtonRadius) {
                return true;
            }
        }
        return false;
    }

    // Helper class for animation and Menu Item cicle center Points
    public class CirclePoint {
        private float x;
        private float y;
        private float radius = 0.0f;
        private double angle = 0.0f;

        public void setX(float x1) {
            x = x1;
        }

        public float getX() {
            return x;
        }

        public void setY(float y1) {
            y = y1;
        }

        public float getY() {
            return y;
        }

        public void setRadius(float r) {
            radius = r;
        }

        public float getRadius() {
            return radius;
        }

        public void setAngle(double angle) {
            this.angle = angle;
        }

        public double getAngle() {
            return angle;
        }
    }

    public class ArcCake{
        private float radius = 0.0f;
        private float startAngle;
        private float sweepAngle;

        public float getRadius() {
            return radius;
        }

        public void setRadius(float radius) {
            this.radius = radius;
        }

        public float getStartAngle() {
            return startAngle;
        }

        public void setStartAngle(float startAngle) {
            this.startAngle = startAngle;
        }

        public float getSweepAngle() {
            return sweepAngle;
        }

        public void setSweepAngle(float sweepAngle) {
            this.sweepAngle = sweepAngle;
        }
    }

    private void startShowAnimate() {
        mRotationAnimation.start();
        mShowArcCakeAnim.start();
        for (ObjectAnimator objectAnimator : mShowAnimation) {
            objectAnimator.start();
        }
    }

    private void startHideAnimate() {
        mRotationReverseAnimation.start();
        mHideArcCakeAnim.start();
        for (ObjectAnimator objectAnimator : mHideAnimation) {
            objectAnimator.start();
        }
    }

    private void cancelAllAnimation() {
        for (ObjectAnimator objectAnimator : mHideAnimation) {
            objectAnimator.cancel();
        }
        for (ObjectAnimator objectAnimator : mShowAnimation) {
            objectAnimator.cancel();
        }
        mShowArcCakeAnim.cancel();
        mHideArcCakeAnim.cancel();
    }

    public void hideMenu(){
        startHideAnimate();
    }

    public void showMenu(){
        startShowAnimate();
    }

    ValueAnimator.AnimatorUpdateListener mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            invalidate();
        }
    };

    ValueAnimator.AnimatorUpdateListener mBezierUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            bezierConstant = (float) valueAnimator.getAnimatedValue();
            invalidate();
        }
    };
    ValueAnimator.AnimatorUpdateListener mRotationUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            mRotationAngle = (float) valueAnimator.getAnimatedValue();
            invalidate();
        }
    };

    ValueAnimator.AnimatorListener mBezierAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            mBezierEndAnimation.start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };


    public interface GooeyMenuInterface {
        /**
         * Called when menu opened
         */
        void menuOpen();

        /**
         * Called when menu Closed
         */
        void menuClose();

        /**
         * Called when Menu item Clicked
         *
         * @param menuNumber give menu number which clicked.
         */
        void menuItemClicked(int menuNumber);
    }
}
