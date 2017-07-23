package cn.xiaojs.xma.ui.widget;
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
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.R;

public class BottomLineCheckButton extends RadioButton {
    private int lineHeight;
    private Paint mPaint;
    public BottomLineCheckButton(Context context) {
        super(context);
        init(null);
    }

    public BottomLineCheckButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public BottomLineCheckButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public BottomLineCheckButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs){

        lineHeight = getResources().getDimensionPixelSize(R.dimen.px3);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeWidth(lineHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isChecked()){
            mPaint.setColor(getCurrentTextColor());
            canvas.drawLine(0,getHeight() - lineHeight,getWidth(),getHeight() - lineHeight,mPaint);
        }
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        invalidate();
    }

}
