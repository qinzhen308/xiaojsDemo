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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.benyuan.xiaojs.R;

public class MessageImageView extends ImageView {
    private Bitmap mBackground;
    private Paint mPaint;
    private int mValue = 22;

    public MessageImageView(Context context) {
        super(context);
        init();
    }

    public MessageImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MessageImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MessageImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        mBackground = BitmapFactory.decodeResource(getResources(), R.drawable.ic_msg_bg);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);// 防抖动
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(getResources().getDimension(R.dimen.font_20px));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mValue <= 0){
            return;
        }
        if (mValue > 0 && mValue < 99){
            int bgLeft = getWidth()/2 + getDrawable().getIntrinsicWidth()/2 - mBackground.getWidth()/2;
            int bgTop = getHeight() - (getHeight() / 2 + getDrawable().getIntrinsicHeight() / 2 + mBackground.getHeight() / 2);
            Rect src = new Rect(0,0,mBackground.getWidth(),mBackground.getHeight());
            Rect dst = new Rect(bgLeft,bgTop,bgLeft + mBackground.getWidth(),bgTop + mBackground.getHeight());
            canvas.drawBitmap(mBackground,src,dst,mPaint);
            int len = (int) mPaint.measureText(String.valueOf(mValue));
            int txtLeft = (getWidth() - getDrawable().getIntrinsicWidth())/2 + getDrawable().getIntrinsicWidth() - len/2;
            Paint.FontMetricsInt fontMetrics = mPaint.getFontMetricsInt();
            int tt = 1/2 * (fontMetrics.descent- fontMetrics.ascent) - fontMetrics.bottom;
            int baseline = getHeight() - ((getHeight() - getDrawable().getIntrinsicHeight())/2 + getDrawable().getIntrinsicHeight()) - tt;
            canvas.drawText(String.valueOf(mValue),txtLeft,baseline,mPaint);
        }else {

        }
    }

    public void setCount(int value){
        mValue = value;
        invalidate();
    }
}
