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
 * Date:2016/11/15
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.benyuan.xiaojs.R;

public class RedTipImageView extends ImageView {

    private boolean mTipEnable;
    private Bitmap mMark;
    private Paint mPaint;

    public RedTipImageView(Context context) {
        super(context);
        init();
    }

    public RedTipImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RedTipImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RedTipImageView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
            Drawable image = getDrawable();
            if (image != null){
                int left = (getWidth() - image.getIntrinsicWidth())/2 + image.getIntrinsicWidth();
                int top = getHeight() - ((getHeight() - image.getIntrinsicHeight())/2 + image.getIntrinsicHeight()) - mMark.getHeight();
                canvas.drawBitmap(mMark,left,top,mPaint);
            }
        }
    }

    public void setTipEnable(boolean enable){
        mTipEnable = enable;
    }
}
