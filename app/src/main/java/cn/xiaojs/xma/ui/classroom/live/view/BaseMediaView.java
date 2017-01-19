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
 * Date:2016/10/19
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.live.core.Config;
import cn.xiaojs.xma.util.DeviceUtil;

public abstract class BaseMediaView extends FrameLayout {

    private FrameLayout mContainer;
    protected FrameLayout mLoadingView;
    private GestureDetector mGesture;
    private String mPath;
    private boolean touchable = true;

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

    private void init() {
        setKeepScreenOn(true);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_media_base_view, this, true);
        mContainer = (FrameLayout) findViewById(R.id.media_container);
        mLoadingView = (FrameLayout) findViewById(R.id.loading_view);

        mContainer.addView(initMediaView());
        mGesture = new GestureDetector(getContext(), new CustomerOnGestureListener());
    }

    protected abstract View initMediaView();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (touchable){
            if (!normal){
                scale();
                return true;
            }
            mGesture.onTouchEvent(event);
            return true;
        }else {
            return false;
        }

    }

    private class CustomerOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            final LiveMenu menu = new LiveMenu(getContext(), BaseMediaView.this instanceof LiveRecordView,isMute());
            menu.show(BaseMediaView.this);
            menu.setOnItemClickListener(new LiveMenu.OnItemClickListener() {
                @Override
                public void onScale() {
                    scale();
                }

                @Override
                public void onMute() {
                    mute();
                }

                @Override
                public void onClose() {
                    close();
                }

                @Override
                public void onSwitchCamera() {
                    switchCamera();
                }
            });
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Toast.makeText(getContext(), "onLongPress", Toast.LENGTH_LONG).show();
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    public void setTouchable(boolean touchable){
        this.touchable = touchable;
    }

    public void setPath(String path) {
        this.mPath = path;
    }

    public String getPath() {
        return this.mPath;
    }

    public abstract void start();

    public abstract void resume();

    public abstract void pause();

    public abstract void destroy();

    protected abstract void mute();

    protected void close() {
        if (isPlayer()) {
            ((ViewGroup) getParent()).removeView(this);
        } else {
            setVisibility(INVISIBLE);
        }

    }

    protected void share() {

    }

    private boolean normal = true;

    protected void scale() {
        ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
        if (normal) {
//            lp.width = getResources().getDimensionPixelSize(Config.SCALED_WIDTH);
//            lp.height = getResources().getDimensionPixelSize(Config.SCALED_HEIGHT);
            lp.width = DeviceUtil.getScreenWidth(getContext());
            lp.height = DeviceUtil.getScreenHeight(getContext());
            lp.leftMargin = 0;
            lp.topMargin = 0;
        } else {
            lp.width = getResources().getDimensionPixelSize(Config.NORMAL_WIDTH);
            lp.height = getResources().getDimensionPixelSize(Config.NORMAL_HEIGHT);
            lp.leftMargin = getResources().getDimensionPixelSize(R.dimen.px18);
            lp.topMargin = getResources().getDimensionPixelSize(R.dimen.px15);
        }
        setLayoutParams(lp);
        normal = !normal;
    }

    protected void switchCamera() {

    }

    protected void showLoading(boolean b) {
        if (b) {
            mLoadingView.setVisibility(VISIBLE);
        } else {
            mLoadingView.setVisibility(GONE);
        }
    }

    private boolean isPlayer() {
        return this instanceof PlayerTextureView;
    }

    protected boolean isMute(){
        return false;
    }
}
