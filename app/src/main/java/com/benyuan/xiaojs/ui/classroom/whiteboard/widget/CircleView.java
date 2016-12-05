package com.benyuan.xiaojs.ui.classroom.whiteboard.widget;
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
 * Date:2016/10/24
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.benyuan.xiaojs.R;

public class CircleView extends View {
    private final static int DEFAULT_COLOR = Color.RED;
    private final static int DEFAULT_BORDER_COLOR = 0x1A000000;
    private final static int DEFAULT_BORDER_WIDTH = 4;

    private int mColor = DEFAULT_COLOR;
    private int mBorderColor = DEFAULT_BORDER_COLOR;
    private float mBorderWidth = DEFAULT_BORDER_WIDTH;
    private Paint mPaint;

    public CircleView(Context context) {
        super(context);
        init(null);
    }

    public CircleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        createPaint();

        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WhiteBoardCircle);
            mColor = a.getColor(R.styleable.WhiteBoardCircle_color, DEFAULT_COLOR);
            mBorderColor = a.getColor(R.styleable.WhiteBoardCircle_circle_border_color, DEFAULT_BORDER_COLOR);
            mBorderWidth = a.getDimension(R.styleable.WhiteBoardCircle_circle_border_width, DEFAULT_BORDER_WIDTH);
        }
    }

    private void createPaint() {
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setPaintColor(int color) {
        mColor = color;
        invalidate();
    }

    public void setRingStyle(int color, float stroke) {
        mBorderColor = color;
        mBorderWidth = stroke;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        float r = w / 2.0f;

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mColor);
        canvas.drawCircle(w / 2.0f, h / 2.0f, r - mBorderWidth, mPaint);

        if (mBorderWidth > 0) {
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mBorderWidth);
            mPaint.setColor(mBorderColor);
            canvas.drawCircle(w / 2.0f, h / 2.0f, r - mBorderWidth / 2.0f, mPaint);
        }
    }

}
