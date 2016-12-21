package cn.xiaojs.xma.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/10/28
 * Desc: quickly location by touch on the right side of the alphabet
 *
 *
 * ======================================================================================== */

public class QuickLocationRightAlphabet extends View {
    private ArrayList<String> mChars = null;
    private OnTouchLetterChangedListener mOnTouchLetterChangedListener;

    private int DEFAULT_TEXT_SIZE = 40;

    /**
     * All the letters displayed on center.
     */
    public final static int TYPE_CENTER = 0;
    /**
     * All the letters displayed align top.
     */
    public final static int TYPE_NORMAL = 1;
    public final static float FACTOR = 0.5f;

    private int mType = TYPE_NORMAL;

    private int mChoose = -1;
    private Paint mPaint = new Paint();

    private int mCharGap;
    /**
     * the offset of first char
     */
    private float mOffsetY;
    private float mCharHeight;
    /**
     * single line height
     */
    private float mSingleHeight;
    /**
     * the offset between char top and char base line
     */
    private float mBaseLineOffsetY;

    public QuickLocationRightAlphabet(Context context) {
        super(context);
        init(0);
    }

    public QuickLocationRightAlphabet(Context context, int textSize) {
        super(context);
        init(textSize);
    }

    public QuickLocationRightAlphabet(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(0);
    }

    public QuickLocationRightAlphabet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(0);
    }

    @TargetApi(21)
    public QuickLocationRightAlphabet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(0);
    }

    private void init(int textSize) {
        mPaint.setColor(0xff057afb);
        mPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(textSize <= 0 ? DEFAULT_TEXT_SIZE : textSize);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        mCharHeight = fontMetrics.descent - fontMetrics.ascent;
        mBaseLineOffsetY = fontMetrics.ascent;

        mCharGap = mCharGap > 0 ? mCharGap : (int) (mCharHeight * FACTOR);
        mSingleHeight = mCharGap + mCharHeight;
        mOffsetY = (getMeasuredHeight() - mChars.size() * (mSingleHeight)) / 2.0f;
        if (mOffsetY < mCharHeight) {
            mCharGap = (int) (getMeasuredHeight() / mChars.size() - mCharHeight);
            mOffsetY = mCharHeight;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mChars.isEmpty()) {
            return;
        }

        if (mType == TYPE_NORMAL) {
            final int height = getHeight();
            final int width = getWidth();
            final int singleHeight = height / mChars.size();
            for (int i = 0; i < mChars.size(); i++) {
                float xPos = (width - mPaint.measureText(mChars.get(i))) / 2;
                float yPos = singleHeight * (i + 1);
                canvas.drawText(mChars.get(i), xPos, yPos, mPaint);
            }
        } else {
            final int width = getWidth();
            for (int i = 0; i < mChars.size(); i++) {
                float xPos = (width - mPaint.measureText(mChars.get(i))) / 2;
                float yPos = -mBaseLineOffsetY + mOffsetY + mSingleHeight * i;
                canvas.drawText(mChars.get(i), xPos, yPos, mPaint);
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mChars.isEmpty()) {
            return true;
        }

        final int action = event.getAction();
        final float y = event.getY();
        int c = 0;
        if (mType == TYPE_NORMAL) {
            c = (int) (y / getHeight() * mChars.size());
        } else {
            c = (int) ((y - mOffsetY + mCharGap / 2) / mSingleHeight);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                handleCharTouch(c);
                break;
            case MotionEvent.ACTION_MOVE:
                handleCharTouch(c);
                break;
            case MotionEvent.ACTION_UP:
                mChoose = -1;
                invalidate();
                break;
        }
        return true;
    }

    private void handleCharTouch(int c) {
        final int oldChoose = mChoose;
        if (oldChoose != c && mOnTouchLetterChangedListener != null) {
            if (c >= 0 && c < mChars.size()) {
                mOnTouchLetterChangedListener.onTouchingLetterChanged(mChars
                        .get(c));
                //Duplicate removal
                mChoose = c;
                invalidate();
            }
        }
    }

    public void setOnTouchingLetterChangedListener(
            OnTouchLetterChangedListener onTouchLetterChangedListener) {
        mOnTouchLetterChangedListener = onTouchLetterChangedListener;
    }

    public interface OnTouchLetterChangedListener {
        public void onTouchingLetterChanged(String s);
    }

    public void setChars(ArrayList<String> chars) {
        this.mChars = chars;
    }

    public void setCharGap(int gap) {
        mCharGap = gap;
    }

    public void setType(int type) {
        mType = type;
    }

}
