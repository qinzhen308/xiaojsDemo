package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/7/19.
 */

public class CircleProgressView extends ImageView {

    private int strokeWidth;
    private Paint mPaint;
    private int mProgress;
    private int mCenterX;
    private int mCenterY;
    private int mRadius;
    private RectF mOval;

    private int rootColor;
    private int proColor;

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        strokeWidth = getResources().getDimensionPixelSize(R.dimen.px2);
        rootColor = getResources().getColor(R.color.circle_root);
        proColor = getResources().getColor(R.color.circle_pro);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(strokeWidth);

        mOval = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mCenterX = getWidth() / 2;
        mCenterY = getHeight() / 2;
        mRadius = getWidth() / 2 - strokeWidth;

        // 画最外层的大圆环
        mPaint.setColor(rootColor);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mPaint);

        if (mProgress > 0) {

            mOval.set(mCenterX - mRadius, mCenterY - mRadius, mCenterX
                    + mRadius, mCenterY + mRadius);
            // 画圆环的进度
            mPaint.setColor(proColor);
            canvas.drawArc(mOval, 0, 360 * mProgress / 100, false, mPaint);
        }
    }


    public void setProgress(int progress) {
        mProgress = progress;
        invalidate();
    }

}
