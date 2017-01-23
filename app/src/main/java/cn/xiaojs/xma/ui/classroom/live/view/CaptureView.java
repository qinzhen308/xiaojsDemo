package cn.xiaojs.xma.ui.classroom.live.view;
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
 * Date:2017/1/22
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;

public class CaptureView extends View {

    private Bitmap bitmap;
    private Paint mPaint;
    private TextureView mTextureView;
    private Runnable s;

    private static final int FPS = 24;

    public CaptureView(Context context) {
        super(context);
        init();
    }

    public CaptureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CaptureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public CaptureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mTextureView == null || bitmap == null)
            return;

        int width = mTextureView.getWidth() * 2 / 3;//左侧白板宽度
        int height = mTextureView.getHeight();//左侧白板高度
        int destWidth = width / 2;//右侧视频源宽度
        int destHeight = height * 2 / 3;//右侧视频源高度
        Rect dest = new Rect(0, 0, destWidth / 5, destHeight / 5);//显示的视频为源视频的1/5

        //捕捉到源视频的图片窗口
        Rect src = new Rect(width, 0,
                width + destWidth,
                destHeight);

        canvas.drawBitmap(bitmap, src, dest, mPaint);
    }

    private void init() {
        mPaint = new Paint();
        s = new Shot();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setTextureView(TextureView textureView) {
        mTextureView = textureView;
    }

    public void start() {
        mHandler.removeCallbacks(s);
        if (mTextureView == null) {
            return;
        }
        mHandler.postDelayed(s, 1000 / FPS);
    }

    private Handler mHandler = new Handler();

    private class Shot implements Runnable {
        @Override
        public void run() {
            screenShot();
            start();
        }
    }

    private void screenShot() {
        Bitmap bitmap = mTextureView.getBitmap();
        if (bitmap != null) {
            this.bitmap = bitmap;
            invalidate();
        }
    }

    public void stop() {
        if (mHandler != null) {
            mHandler.removeCallbacks(s);
        }
    }
}
