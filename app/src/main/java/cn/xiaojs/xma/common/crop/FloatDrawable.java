package cn.xiaojs.xma.common.crop;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

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
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */
public class FloatDrawable extends Drawable {

    private Context mContext;
    private Drawable mCropPointDrawable;
    private Paint mLinePaint = new Paint();

    {
        mLinePaint.setARGB(200, 50, 50, 50);
        mLinePaint.setStrokeWidth(2F);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setColor(Color.WHITE);
    }

    public FloatDrawable(Context context) {
        super();
        this.mContext = context;
        init();
    }

    private void init() {
        //mCropPointDrawable = mContext.getResources().getDrawable(R.drawable.clip_point);
        //TODO
        mCropPointDrawable = new ColorDrawable(Color.GRAY);
        mCropPointDrawable.setBounds(0, 0, 100, 100);
    }

    public int getCircleWidth() {
        return mCropPointDrawable.getIntrinsicWidth();
    }

    public int getCircleHeight() {
        return mCropPointDrawable.getIntrinsicHeight();
    }

    @Override
    public void draw(Canvas canvas) {

        int left = getBounds().left;
        int top = getBounds().top;
        int right = getBounds().right;
        int bottom = getBounds().bottom;

        Rect mRect = new Rect(
                left + mCropPointDrawable.getIntrinsicWidth() / 2, top
                + mCropPointDrawable.getIntrinsicHeight() / 2, right
                - mCropPointDrawable.getIntrinsicWidth() / 2, bottom
                - mCropPointDrawable.getIntrinsicHeight() / 2);
        // 方框
        canvas.drawRect(mRect, mLinePaint);

    }

    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(new Rect(bounds.left
                - mCropPointDrawable.getIntrinsicWidth() / 2, bounds.top
                - mCropPointDrawable.getIntrinsicHeight() / 2, bounds.right
                + mCropPointDrawable.getIntrinsicWidth() / 2, bounds.bottom
                + mCropPointDrawable.getIntrinsicHeight() / 2));
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

}
