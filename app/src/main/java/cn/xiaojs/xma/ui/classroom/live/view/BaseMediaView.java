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
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.classroom.live.core.Config;
import cn.xiaojs.xma.ui.widget.LoadingView;
import cn.xiaojs.xma.util.DeviceUtil;

public abstract class BaseMediaView extends FrameLayout {

    protected FrameLayout mLoadingLayout;
    protected LoadingView mLoadingView;
    protected TextView mLadingDesc;
    private GestureDetector mGesture;
    private String mPath;
    private boolean mTouchable = true;
    private boolean mConsume = true;
    protected boolean mResume = false;
    protected Handler mHandler;

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
        initHandler();
        setKeepScreenOn(true);
        LayoutInflater.from(getContext()).inflate(R.layout.layout_media_base_view, this, true);
        mLoadingLayout = (FrameLayout) findViewById(R.id.loading_layout);
        mLoadingView = (LoadingView) findViewById(R.id.loading_progress);
        mLadingDesc = (TextView) findViewById(R.id.loading_desc);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(initMediaView(), 0, params);
        mGesture = new GestureDetector(getContext(), new CustomerOnGestureListener());
    }

    protected void initHandler() {

    }

    protected abstract View initMediaView();

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        /*if (mTouchable) {
            if (!normal) {
                scale();
                return true;
            }
            mGesture.onTouchEvent(event);
            return true;
        } else {

        }*/

        if (mConsume) {
            return super.onTouchEvent(event);
        } else {
            return false;
        }
    }

    private class CustomerOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            final LiveMenu menu = new LiveMenu(getContext(), BaseMediaView.this instanceof PlayerTextureView, isMute());
            menu.show(BaseMediaView.this);
            menu.setOnItemClickListener(new LiveMenu.OnItemClickListener() {
                @Override
                public void onScale() {
                    scale();
                }

                @Override
                public void onAudio() {
                    mute();
                }

                @Override
                public void onVideoClose() {
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
            if (XiaojsConfig.DEBUG) {
                Toast.makeText(getContext(), "onLongPress", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    public void setTouchable(boolean touchable) {
        mTouchable = touchable;
    }

    public void setTouchConsume(boolean consume) {
        mConsume = consume;
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

    public abstract void stop();

    public abstract void destroy();

    public abstract void mute();

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
            lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.px10);
            lp.topMargin = getResources().getDimensionPixelSize(R.dimen.px110);
        }
        setLayoutParams(lp);
        normal = !normal;
    }

    protected void switchCamera() {

    }

    public void showLoading(boolean b) {
        if (b) {
            mLoadingLayout.setVisibility(VISIBLE);
        } else {
            mLoadingLayout.setVisibility(GONE);
        }
    }

    public void showLoading(boolean b, int loadingSize, int txtSize) {
        if (b) {
            mLoadingView.setSize(loadingSize);
            mLadingDesc.getPaint().setTextSize(txtSize);
            mLoadingLayout.setVisibility(VISIBLE);
        } else {
            mLoadingLayout.setVisibility(GONE);
        }
    }

    private boolean isPlayer() {
        return this instanceof PlayerTextureView;
    }

    public boolean isMute() {
        return false;
    }

    public boolean isResume() {
        return false;
    }
}
