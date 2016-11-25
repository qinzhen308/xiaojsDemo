package com.benyuan.xiaojs.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.benyuan.xiaojs.R;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/11/24
 * Desc:
 *
 * ======================================================================================== */

public class LoadingView extends View {
    private final static int DEFAULT_SIZE = 70;
    private final static int MSG_ANIM = 100;
    private final static int DURATION = 800; //800ms
    private final static int REFRESH_INTERVAL = 20; //20ms

    private final static int FRAME_COUNT = DURATION / REFRESH_INTERVAL;
    private final static int HALF_FRAME_COUNT = FRAME_COUNT / 2;

    private int mSize = DEFAULT_SIZE;
    private int mCircleWidth = DEFAULT_SIZE / 2;
    private int mCircleHeight = DEFAULT_SIZE / 2;
    private float mRadius = DEFAULT_SIZE / 4.0f;
    private float mMinX = DEFAULT_SIZE / 4.0f;;
    private float mMaxX = DEFAULT_SIZE * 3 / 4.0f;
    private float mDistance = DEFAULT_SIZE / 2;
    private Handler mHandler;
    private int mOffsetX;
    private int mOffsetY;

    private int mOrangeColor;
    private int mBlueColor;
    private int mCurrFrame;

    private Paint mPaint;
    private long mStart;

    public LoadingView(Context context, int size) {
        super(context);
        init(size);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(0);
    }

    @TargetApi(21)
    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(0);
    }

    private void init (int size) {
        if (size <= 0) {
            mSize = getResources().getDimensionPixelSize(R.dimen.px70);
        } else {
            mSize = size;
        }

        mCircleWidth = mSize / 2;
        mCircleHeight = mSize /2;
        mRadius = mSize / 4.0f;
        mDistance = mSize / 2.0f;
        mMinX = mSize / 4.0f;;
        mMaxX = (mSize * 3) / 4.0f;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);

        mOrangeColor = getResources().getColor(R.color.main_orange);
        mBlueColor = getResources().getColor(R.color.main_blue);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MSG_ANIM) {
                    mCurrFrame++;
                    if (mCurrFrame > FRAME_COUNT) {
                        mCurrFrame = 1;
                    }
                    postInvalidate();
                }
            }
        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int horPadding = getPaddingLeft() + getPaddingRight();
        int verPadding = getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(mSize + horPadding, mSize + verPadding);
        mOffsetX = horPadding / 2;
        mOffsetY = verPadding / 2;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mOffsetX, mOffsetY);
        if (mCurrFrame <= HALF_FRAME_COUNT ) {
            //first draw blue circle, then draw orange circle.
            //Blue circle moves from left to right. Orange moves from the right to the left.
            drawCircle(canvas, mMinX + mDistance * ((float) mCurrFrame / HALF_FRAME_COUNT), mBlueColor);
            drawCircle(canvas, mMaxX - mDistance * ((float) mCurrFrame / HALF_FRAME_COUNT), mOrangeColor);
        } else if (mCurrFrame > HALF_FRAME_COUNT){
            //first draw orange circle, then draw blue circle.
            //Orange circle moves from left to right. Blue moves from the right to the left.
            drawCircle(canvas, mMinX + mDistance * ((float)(mCurrFrame - HALF_FRAME_COUNT) / HALF_FRAME_COUNT), mOrangeColor);
            drawCircle(canvas, mMaxX - mDistance * ((float)(mCurrFrame - HALF_FRAME_COUNT) / HALF_FRAME_COUNT), mBlueColor);
        }
        canvas.restore();

        mStart = System.currentTimeMillis();
        mHandler.sendEmptyMessageDelayed(MSG_ANIM, REFRESH_INTERVAL);
    }


    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeMessages(MSG_ANIM);
        mHandler = null;
    }

    private void drawCircle(Canvas canvas, float offsetX, int color) {
        float y = mSize / 2.0f;
        mPaint.setColor(color);
        canvas.drawCircle(offsetX, y, mRadius, mPaint);
    }
}
