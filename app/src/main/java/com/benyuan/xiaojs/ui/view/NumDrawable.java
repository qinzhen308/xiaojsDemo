package com.benyuan.xiaojs.ui.view;
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
 * Date:2016/11/7
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.benyuan.xiaojs.R;

public class NumDrawable extends Drawable {

    private Paint mPaint;
    private Bitmap mBitmap;

    public NumDrawable(Context context){
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_enter_cls_have);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    @Override
    public void draw(Canvas canvas) {
        Rect rect = getBounds();
        RectF r = new RectF();
        r.set(0,0,60,60);
        canvas.drawBitmap(mBitmap,null,r,mPaint);

    }

    @Override
    public void setAlpha(int i) {
        mPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

}
