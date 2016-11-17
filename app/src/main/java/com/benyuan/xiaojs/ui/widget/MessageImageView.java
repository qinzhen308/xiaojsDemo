package com.benyuan.xiaojs.ui.widget;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/11/13
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.benyuan.xiaojs.R;

public class MessageImageView extends RoundedImageView {
    private Bitmap mBackground;
    private Paint mPaint;
    private int mValue = 0;
    private boolean mIsCircle;

    public MessageImageView(Context context) {
        super(context);
        init(null);
    }

    public MessageImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MessageImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(
                    attrs,
                    R.styleable.MessageImageView,
                    0, 0);
            try {
                mIsCircle = typedArray.getBoolean(R.styleable.MessageImageView_isCircle,false);
            }finally {
                typedArray.recycle();
                typedArray = null;
            }
        }
        mBackground = BitmapFactory.decodeResource(getResources(), R.drawable.ic_msg_bg);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);// 防抖动
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(getResources().getDimension(R.dimen.font_20px));
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mValue <= 0) {
            return;
        }
        if (mValue > 0 && mValue < 99) {


            //bg: left, top
            int bgLeft = getBgLeft();
            int bgTop = getBgTop();

            //draw bg
            Rect src = new Rect(0, 0, mBackground.getWidth(), mBackground.getHeight());
            Rect dst = new Rect(bgLeft, bgTop, bgLeft + mBackground.getWidth(), bgTop + mBackground.getHeight());
            canvas.drawBitmap(mBackground, src, dst, mPaint);

            //text: w, h
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            int txtW = (int) mPaint.measureText(String.valueOf(mValue));
            int txtH = fontMetrics.descent - fontMetrics.ascent;

            //text: x, y, baseline
            int x = bgLeft + (mBackground.getWidth() - txtW) / 2;
            int y = bgTop + (mBackground.getHeight() - txtH) / 2;
            int baseline = y - fontMetrics.ascent;

            //draw text
            canvas.drawText(String.valueOf(mValue), x, baseline, mPaint);
        } else {

        }
    }

    public void setCount(int value) {
        mValue = value;
        invalidate();
    }

    private int getBgLeft(){
        int drawableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        if (mIsCircle){//是圆形图片的话要按圆形内部最大正方形的宽度计算，
            int r = drawableWidth / 2;
            int half = (int) Math.sqrt(r * r / 2) ;//2x*x = r*r
            return getWidth() / 2 + half - mBackground.getWidth() / 2;
        }else {
            return getWidth() / 2 + drawableWidth / 2 - mBackground.getWidth() / 2;
        }

    }

    private int getBgTop(){
        int drawableHeight = getHeight() - getPaddingTop() - getPaddingBottom();

        if (mIsCircle){
            int r = drawableHeight / 2;
            int half = (int) Math.sqrt(r * r / 2) ;
            return getHeight() / 2 - (half + mBackground.getHeight() / 2);
        }else {
            return getHeight() / 2 - (drawableHeight / 2 + mBackground.getHeight() / 2);
        }
    }
}
