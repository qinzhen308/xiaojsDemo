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
 * Date:2017/1/17
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.pili.pldroid.player.widget.PLVideoTextureView;

public class CutView extends FrameLayout {
    private static final int BOARD_WIDTH = 1280;
    private static final int BOARD_HEIGHT = 720;

    private static final int VIDEO_WIDTH = 640;
    private static final int VIDEO_HEIGHT = 480;


    PlayerTextureView mPlayer;
    private boolean mBoard = true;
    private static final int SCALE = 4;

    public CutView(Context context) {
        super(context);
        init();
    }

    public CutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public CutView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        mPlayer = new PlayerTextureView(getContext(), PLVideoTextureView.ASPECT_RATIO_PAVED_PARENT);
        mPlayer.setTouchable(false);
        addView(mPlayer);
    }

    public void setPath(String path) {
        ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) mPlayer.getLayoutParams();
//        if (mBoard){
        lp.height = getContext().getResources().getDisplayMetrics().heightPixels;
        lp.width = (BOARD_WIDTH + VIDEO_WIDTH) * lp.height / BOARD_HEIGHT;
//        }else {
//            lp.height = getContext().getResources().getDisplayMetrics().heightPixels / SCALE;
//            lp.width = (BOARD_WIDTH + VIDEO_WIDTH) * lp.height / BOARD_HEIGHT;
//            lp.leftMargin = (int) (-lp.width * ((float)BOARD_WIDTH/(VIDEO_WIDTH + BOARD_WIDTH)));
//        }
        mPlayer.setPath(path);
    }

    public void resume() {
        mPlayer.resume();
    }

    public void pause() {
        mPlayer.pause();
    }

    public void destroy() {
        mPlayer.destroy();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        if (mBoard){
        int height = getContext().getResources().getDisplayMetrics().heightPixels;
        int width = BOARD_WIDTH * height / BOARD_HEIGHT;
        setMeasuredDimension(width, height);
//        }else {
//            int height = (int) (getContext().getResources().getDisplayMetrics().heightPixels / SCALE * ((float)VIDEO_HEIGHT / BOARD_HEIGHT));
//            int width = VIDEO_WIDTH * height / VIDEO_HEIGHT;
//            setMeasuredDimension(width,height);
//        }

    }

    public void setCaptureView(SurfaceCaptureView captureView) {
        if (mPlayer != null) {
            mPlayer.setCaptureView(captureView);
        }
    }

    public PlayerTextureView getPlayer() {
        return mPlayer;
    }
}
