package cn.xiaojs.xma.ui.classroom.whiteboard.widget;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/10/24
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;


public class PaintPathPreview extends View {

    public final static int ERASER_PREVIEW_MODE = 0;
    public final static int PAINT_PREVIEW_MODE = 1;
    public static final int DEFAULT_PAINT_PATH_SIZE = 20;
    public static final int DEFAULT_PAINT_SHADOW_ALPHA = 20;

    private Paint mPaint;
    private Paint mPathPaint;

    private Bitmap mBitmap;
    private Canvas mBitmapCanvas;

    private Path mPath;
    private Context mContext;

    private int mPreviewMode = ERASER_PREVIEW_MODE;
    private int mPreviewLength;
    private float mStartX;
    private float mStartY;
    private float mAmplitude;
    private float mDpStep = 2.0f;

    public PaintPathPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(context.getResources().getColor(R.color.classroom_main_gray));

        mPathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPathPaint.setColor(Color.WHITE);
        mPathPaint.setStrokeWidth(DEFAULT_PAINT_PATH_SIZE);
        mPathPaint.setStrokeJoin(Paint.Join.ROUND);
        mPathPaint.setStrokeCap(Paint.Cap.ROUND);
        mPathPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
        float mDensity = context.getResources().getDisplayMetrics().density;
        mDpStep = mDensity < 3.0f ? 1.0f : 2.0f;
    }

    private int getDimensionPixelSize(Context context, int resId) {
        return context.getResources().getDimensionPixelSize(resId);
    }

    public void setPreviewMode(int mode) {
        mPreviewMode = mode;

        mPreviewLength = getDimensionPixelSize(mContext, R.dimen.px200);
        mStartX = (getDimensionPixelSize(mContext, R.dimen.px300) - mPreviewLength) / 2;
        mStartY = (getDimensionPixelSize(mContext, R.dimen.px80)) / 2;
        mAmplitude = 20f;
    }

    public void setPaintSize(int size) {
        mPathPaint.setStrokeWidth(size);
        invalidate();
    }

    public void setPaintColor(int color, int alpha) {
        color = Color.argb(alpha, Color.red(color), Color.green(color),
                Color.blue(color));
        mPathPaint.setColor(color);
        invalidate();
    }

    private void setPaintPath() {
        float x = mStartX;
        float y = mStartY;
        float endx = x;
        float endy = y;
        mPath.moveTo(x, y);
        while ((x - mStartX) <= mPreviewLength) {
            float sinValue = -(float) Math.sin((x - mStartX) / mPreviewLength
                    * 2 * Math.PI);
            y = mStartY - mAmplitude * sinValue;
            mPath.quadTo(x, y, endx, endy);
            endx = x;
            endy = y;
            x = x + Utils.dpToPixel(getContext(), mDpStep);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPath.isEmpty()) {
            setPaintPath();
        }
        if (mPreviewMode == PAINT_PREVIEW_MODE) {
            if (mBitmap == null) {
                mBitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                        Config.ARGB_8888);
                mBitmapCanvas = new Canvas(mBitmap);

                int shadowColor = Color.argb(DEFAULT_PAINT_SHADOW_ALPHA,
                        Color.red(Color.BLACK), Color.green(Color.BLACK),
                        Color.blue(Color.BLACK));
                mPathPaint.setShadowLayer(5f, 2f, 2f, shadowColor);
            }
            mBitmap.eraseColor(0);

            mPathPaint.setXfermode(null);
            mPathPaint.setStrokeWidth(mPathPaint.getStrokeWidth() - 3);
            mBitmapCanvas.drawPath(mPath, mPathPaint);

            mPathPaint.setXfermode(new PorterDuffXfermode(Mode.SRC));
            mPathPaint.setStrokeWidth(mPathPaint.getStrokeWidth() + 3);
            mBitmapCanvas.drawPath(mPath, mPathPaint);

            canvas.drawBitmap(mBitmap, 0, 0, null);
        } else {
            canvas.drawPath(mPath, mPathPaint);
        }

    }

}
