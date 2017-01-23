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
 * Date:2017/1/23
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

public class SurfaceCaptureView extends SurfaceView implements SurfaceHolder.Callback {

    private Bitmap bitmap;
    private Paint mPaint;
    private TextureView mTextureView;

    private static final int FPS = 24;
    private SurfaceHolder mHolder;

    private Loop loop;
    private boolean signal;

    public SurfaceCaptureView(Context context) {
        super(context);
        init();
    }

    public SurfaceCaptureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SurfaceCaptureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public SurfaceCaptureView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mPaint = new Paint();
    }

    private void  doDraw(Canvas canvas) {
        if (mTextureView == null || bitmap == null)
            return;

        int width = mTextureView.getWidth() * 2 / 3 / 5;
        int height = mTextureView.getHeight() / 5;
        int destWidth = width / 2;
        int destHeight = height * 2 / 3;
        Rect dest = new Rect(0, 0, destWidth, destHeight);

        Rect src = new Rect(width, 0,
                width + destWidth,
                destHeight);

        canvas.drawBitmap(bitmap, src, dest, mPaint);
    }

    public void setTextureView(TextureView textureView) {
        mTextureView = textureView;
    }

    public void start() {
        if (mTextureView == null) {
            return;
        }
        if (loop == null) {
            loop = new Loop();
            signal = true;
            new Thread(loop).start();
        }
    }



    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mTextureView == null)
            return;
        int width = mTextureView.getWidth() * 2 / 3;
        int height = mTextureView.getHeight();
        int destWidth = width / 2;
        int destHeight = height * 2 / 3;
        setMeasuredDimension(destWidth / 5, destHeight / 5);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mHolder.removeCallback(this);
        mHolder = null;
    }

    private void screenShot() {
        if (bitmap == null){
            createLocalBitmap();
        }
        mTextureView.getBitmap(bitmap);
    }

    private void createLocalBitmap(){
        if (mTextureView == null)
            return;

        bitmap = Bitmap.createBitmap(mTextureView.getWidth() / 5, mTextureView.getHeight() / 5, Bitmap.Config.ARGB_4444);
    }

    public void stop() {
        signal = false;
        loop = null;
    }

    private class Loop implements Runnable {
        @Override
        public void run() {
            while (signal) {
                try {
                    if (mHolder != null) {
                        Canvas canvas = mHolder.lockCanvas();
                        if (canvas != null) {
                            screenShot();
                            doDraw(canvas);
                            mHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                    Thread.sleep(1000 / FPS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
