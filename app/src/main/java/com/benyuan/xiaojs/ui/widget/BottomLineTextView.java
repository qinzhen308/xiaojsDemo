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
 * Date:2016/11/11
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.benyuan.xiaojs.R;

public class BottomLineTextView extends TextView {
    private int lineHeight;
    private Paint mPaint;
    public BottomLineTextView(Context context) {
        super(context);
        init();
    }

    public BottomLineTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BottomLineTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public BottomLineTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setPadding(getResources().getDimensionPixelSize(R.dimen.px5),
                getResources().getDimensionPixelSize(R.dimen.px20),
                getResources().getDimensionPixelSize(R.dimen.px5),
                getResources().getDimensionPixelSize(R.dimen.px20));
        lineHeight = getResources().getDimensionPixelSize(R.dimen.px4);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(getResources().getColor(R.color.main_orange));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0,getHeight() - lineHeight,getWidth(),getHeight(),mPaint);
    }
}
