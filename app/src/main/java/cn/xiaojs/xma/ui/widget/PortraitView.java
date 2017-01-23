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
 * Date:2017/1/20
 * Desc:右上角待性别的圆形图片，默认上下左右带有20px的padding,在设置时需要加上
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;

public class PortraitView extends RoundedImageView {

    private Bitmap mMale;
    private Bitmap mFemale;
    private Paint mPaint;
    private String mSex;

    public PortraitView(Context context) {
        super(context);
        init();
    }

    public PortraitView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PortraitView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mMale = BitmapFactory.decodeResource(getResources(), R.drawable.ic_male);
        mFemale = BitmapFactory.decodeResource(getResources(), R.drawable.ic_female);
        setOval(true);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);// 防抖动
        int padding = getResources().getDimensionPixelSize(R.dimen.px20);
        setPadding(padding, padding, padding, padding);
    }

    private int getBgLeft() {
        int drawableWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        int r = drawableWidth / 2;
        int half = (int) Math.sqrt(r * r / 2);//2x*x = r*r
        return getWidth() / 2 + half - getBg().getWidth() / 2;

    }

    private int getBgTop() {
        int drawableHeight = getHeight() - getPaddingTop() - getPaddingBottom();
        int r = drawableHeight / 2;
        int half = (int) Math.sqrt(r * r / 2);
        //return getHeight() / 2 - (half + getBg().getHeight() / 2);
        return getHeight() / 2 + (half /*+ getBg().getHeight() / 2*/) - getPaddingBottom();
    }

    public void setSex(String sex) {
        mSex = sex;
    }

    private Bitmap getBg() {
        if (TextUtils.isEmpty(mSex))
            return null;

        if (mSex.equalsIgnoreCase(Account.Sex.MALE)) {
            return mMale;
        } else {
            return mFemale;
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (getBg() == null)
            return;

        Rect src = new Rect(0, 0, getBg().getWidth(), getBg().getHeight());
        Rect dst = new Rect(getBgLeft(), getBgTop(), getBgLeft() + getBg().getWidth(), getBgTop() + getBg().getHeight());
        canvas.drawBitmap(getBg(), src, dst, mPaint);
    }
}
