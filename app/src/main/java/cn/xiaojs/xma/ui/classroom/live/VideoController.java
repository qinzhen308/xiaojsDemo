package cn.xiaojs.xma.ui.classroom.live;
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
 * Date:2017/3/1
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;

public abstract class VideoController {
    protected Context mContext;
    protected View mRoot;

    private Handler mHandler;
    protected LiveRecordView mPublishView;
    protected PlayerTextureView mPlayView;

    protected boolean mInitPublishVideo = false;
    protected boolean mLive;

    protected String mPlayStreamUrl;

    public VideoController(Context context, View root) {
        mContext = context;
        mRoot = root;
        mHandler = new Handler();

        init(root);
        if (mPublishView != null) {
            mPublishView.setOnStreamingStateListener(mStreamingStateChangedListener);
        }
        onResume();
    }

    /**
     * 初始化视频view
     */
    protected abstract void init(View root);

    /**
     * 监听socket事件
     */
    protected abstract void listenerSocket();

    /**
     * resume视频
     */
    public abstract void onResume();

    /**
     * pause视频
     */
    public abstract void onPause();

    /**
     * destroy视频
     */
    public abstract void onDestroy();

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (mPublishView != null) {
            mPublishView.switchCamera();
        }
    }

    /**
     * 截视频帧
     */
    public abstract void takeVideoFrame(FrameCapturedCallback callback);

    /**
     * 暂停推流
     */
    public void pausePublishStream() {
        if (mPublishView != null) {
            mPublishView.pause();
        }
    }

    /**
     * 开始推流
     */
    public void publishStream(String url, boolean live) {
        if (TextUtils.isEmpty(url) || mPublishView == null) {
            return;
        }

        mPublishView.setPath(url);
        if (!mInitPublishVideo) {
            mPublishView.start();
        } else {
            mPublishView.resume();
        }
    }

    /**
     * 播放流
     */
    public void playStream(String url) {
        if (!TextUtils.isEmpty(url)) {
            mPlayStreamUrl = url;
        }
        if (mPlayView != null) {
            mPlayView.setPath(mPlayStreamUrl);
            mPlayView.resume();
        }
    }

    /**
     * 推流的流状态发生变化回调
     * @param streamingState 流状态
     * @param data 额外数据
     */
    public void onSteamStateChanged(StreamingState streamingState, Object data) {}

    private StreamingStateChangedListener mStreamingStateChangedListener = new StreamingStateChangedListener() {
        @Override
        public void onStateChanged(StreamingState streamingState, Object o) {
            switch (streamingState) {
                case STREAMING:
                    mInitPublishVideo = true;
                    break;
            }
            onSteamStateChanged(streamingState, o);
        }
    };

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
