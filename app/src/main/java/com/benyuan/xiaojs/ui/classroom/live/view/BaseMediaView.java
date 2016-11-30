package com.benyuan.xiaojs.ui.classroom.live.view;
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
 * Date:2016/10/19
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.benyuan.xiaojs.R;

public abstract class BaseMediaView extends FrameLayout {

    private LinearLayout mFunctionBar;
    private TextView mMute;
    private TextView mShare;
    private TextView mScale;
    private TextView mClose;
    private FrameLayout mContainer;
    protected FrameLayout mLoadingView;
    private GestureDetector mGesture;
    private String mPath;
    private boolean mCanClose;

    private int screenWidth;
    private int screenHeight;
    private int lastX;
    private int lastY;

    public BaseMediaView(Context context) {
        super(context);
        init();
    }

    public BaseMediaView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseMediaView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        setKeepScreenOn(true);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_media_base_view,this,true);
        mFunctionBar = (LinearLayout) findViewById(R.id.function_bar);
        mContainer = (FrameLayout) findViewById(R.id.media_container);
        mClose = (TextView) findViewById(R.id.close);
        mMute = (TextView) mFunctionBar.findViewById(R.id.mute);
        mShare = (TextView) mFunctionBar.findViewById(R.id.share);
        mScale = (TextView) mFunctionBar.findViewById(R.id.scale);
        mLoadingView = (FrameLayout) findViewById(R.id.loading_view);

        mContainer.addView(initMediaView());
        mMute.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onMute();
            }
        });
        mClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClose();
            }
        });
        mScale.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onScale();
            }
        });
        mShare.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onShare();
            }
        });
        mGesture = new GestureDetector(getContext(),new CustomerOnGestureListener());
    }

    protected abstract View initMediaView();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //drag(event);
        mGesture.onTouchEvent(event);
        return true;
    }
    private void drag(MotionEvent event){
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int dx = (int) event.getRawX() - lastX;
                int dy = (int) event.getRawY() - lastY;
                int left = getLeft() + dx;
                int top = getTop() + dy;
                int right = getRight() + dx;
                int bottom = getBottom() + dy;
                if (left < 0) {
                    left = 0;
                    right = left + getWidth();
                }

                if (right > screenWidth) {
                    right = screenWidth;
                    left = right - getWidth();
                }

                if (top < 0) {
                    top = 0;
                    bottom = top + getHeight();
                }

                if (bottom > screenHeight) {
                    bottom = screenHeight;
                    top = bottom - getHeight();
                }
                layout(left, top, right, bottom);
                setX(left);
                setY(top);
                lastX = (int) event.getRawX();
                lastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
    }

    private class CustomerOnGestureListener extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            showFunction(mFunctionBar.getVisibility() != VISIBLE);
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Toast.makeText(getContext(),"onLongPress", Toast.LENGTH_LONG).show();
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    private void showFunction(boolean show){
        removeCallbacks(mHideFunction);
        if (show){
            mFunctionBar.setVisibility(VISIBLE);
            if (mCanClose){
                mClose.setVisibility(VISIBLE);
            }
            postDelayed(mHideFunction,3000);
        }else {
            mFunctionBar.setVisibility(INVISIBLE);
            if (mCanClose){
                mClose.setVisibility(INVISIBLE);
            }
        }
    }

    protected void canClose(boolean b){
        mCanClose = b;
    }

    private FunctionBarHandler mHideFunction = new FunctionBarHandler();

    private class FunctionBarHandler implements Runnable {
        @Override
        public void run() {
            showFunction(false);
        }
    }

    public void setPath(String path){
        this.mPath = path;
    }

    public String getPath(){
        return this.mPath;
    }

    public abstract void start();
    public abstract void resume();
    public abstract void pause();
    public abstract void destroy();

    protected void onMute(){

    }

    protected void onClose(){
        ((ViewGroup)getParent()).removeView(this);
    }

    protected void onShare(){

    }

    protected void onScale(){

    }

    protected void showLoading(boolean b){
        if (b){
            mLoadingView.setVisibility(VISIBLE);
        }else {
            mLoadingView.setVisibility(GONE);
        }
    }
}
