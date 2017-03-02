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
import android.view.View;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import cn.xiaojs.xma.ui.classroom.Constants;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;

public abstract class VideoManager {
    protected Context mContext;
    protected View mRoot;
    protected Constants.User mUser;

    private Handler mHandler;
    protected LiveRecordView mPublishView;

    public VideoManager(Context context, View root) {
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
    public abstract void switchCamera();

    /**
     * 截视频帧
     */
    public abstract void takeVideoFrame(FrameCapturedCallback callback);

    /**
     * 暂停推流
     */
    public abstract void pausePublishStream();

    /**
     * 开始推流
     */
    public abstract void publishStream(String url);

    /**
     * 播放流
     */
    public abstract void playStream(String url);

    /**
     * 推流的流状态发生变化回调
     * @param streamingState 流状态
     * @param data 额外数据
     */
    public void onSteamStateChanged(StreamingState streamingState, Object data) {}

    private StreamingStateChangedListener mStreamingStateChangedListener = new StreamingStateChangedListener() {
        @Override
        public void onStateChanged(StreamingState streamingState, Object o) {
            onSteamStateChanged(streamingState, o);
        }
    };


    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
