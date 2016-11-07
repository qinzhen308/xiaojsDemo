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
 * Date:2016/10/28
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.benyuan.xiaojs.R;

public class RedTipTextView extends TextView {

    private boolean mTipEnable;
    private Bitmap mMark;
    private Paint mPaint;
    public RedTipTextView(Context context) {
        super(context);
        init();
    }

    public RedTipTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RedTipTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mMark = BitmapFactory.decodeResource(getResources(), R.drawable.ic_red_mark);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTipEnable){
            int width = getWidth();
            Drawable[] ds = getCompoundDrawables();
            int bitmapWidth = 0 ;
            for (Drawable d:ds){
                if (d != null){
                    bitmapWidth = d.getBounds().width();
                    break;
                }
            }
            int left = (width - bitmapWidth) / 2 + bitmapWidth;
            canvas.drawBitmap(mMark,left,getPaddingTop(),mPaint);
        }
    }

    public void setTipEnable(boolean enable){
        mTipEnable = enable;
        invalidate();
    }

}
