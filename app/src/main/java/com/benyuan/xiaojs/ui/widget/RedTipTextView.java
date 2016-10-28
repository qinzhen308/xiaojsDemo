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
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class RedTipTextView extends TextView {

    private boolean mTipEnable;
    public RedTipTextView(Context context) {
        super(context);
    }

    public RedTipTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RedTipTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTipEnable){
            int width = getWidth();
            int height = getHeight();
            int realHeight = height - getPaddingTop() - getPaddingBottom();
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(width * 3 / 4,height / 2 + getPaddingTop()/2,realHeight / 10,paint);
        }
    }

    public void setTipEnable(boolean enable){
        mTipEnable = enable;
        invalidate();
    }
}
