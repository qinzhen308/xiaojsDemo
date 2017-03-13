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
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.ui.classroom.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;

public abstract class VideoController implements StreamConfirmCallback {
    protected Context mContext;
    protected View mRoot;

    private Handler mHandler;
    protected LiveRecordView mPublishView;
    protected PlayerTextureView mPlayView;

    protected boolean mInitPublishVideo = false;
    protected boolean mLive;

    protected String mPlayStreamUrl;
    protected String mPublishStreamUrl;

    private boolean mStreamPlaying;
    private boolean mStreamPublishing;
    private boolean mNeedStreamRePublishing = false;
    private OnStreamUseListener mOnStreamUseListener;

    public VideoController(Context context, View root, OnStreamUseListener listener) {
        mContext = context;
        mRoot = root;
        mOnStreamUseListener = listener;
        mHandler = new Handler();

        init(root);
        if (mPublishView != null) {
            mPublishView.setOnStreamingStateListener(mStreamingStateChangedListener);
        }
        onResume();

        //监听流开始或暂停
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED), mStreamingStartedListener);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STOPPED), mStreamingStoppedListener);
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
    public void onResume() {
        if (mPlayView != null) {
            mPlayView.resume();
        }
        if (mPublishView != null) {
            mPublishView.resume();
        }

        if (mNeedStreamRePublishing) {
            mNeedStreamRePublishing = false;
            publishStream(mPublishStreamUrl, mLive);
        }
    }

    /**
     * pause视频
     */
    public void onPause() {
        pausePlayStream();
        pausePublishStream();
    }

    /**
     * destroy视频
     */
    public void onDestroy() {
        if (mPlayView != null) {
            mPlayView.destroy();
        }
        if (mPublishView != null) {
            mPublishView.destroy();
        }
    }

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
            if (mStreamPublishing) {
                //send stopped stream
                SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.STREAMING_STOPPED),
                        new SocketManager.AckListener() {
                            @Override
                            public void call(Object... args) {
                                if (args != null && args.length > 0) {
                                    mNeedStreamRePublishing = true;
                                    Toast.makeText(mContext, "推流暂停", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            mStreamPublishing = false;
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

        mPublishStreamUrl = url;
        if (mOnStreamUseListener != null) {
            mLive = live;
            mOnStreamUseListener.onStreamPublish(this);
        }
    }

    /**
     * 播放流
     */
    public void playStream(String url) {
        if (!TextUtils.isEmpty(url)) {
            mPlayStreamUrl = url;
        }
        if (mPlayView != null && !TextUtils.isEmpty(mPlayStreamUrl)) {
            if (mOnStreamUseListener != null) {
                mOnStreamUseListener.onStreamPlay(this);
            }
        }
    }

    /**
     * 暂停播放流
     */
    public void pausePlayStream() {
        if (mPlayView != null) {
            mStreamPlaying = false;
            mPlayView.pause();
            mPlayView.showLoading(false);
        }
    }

    /**
     * 推流的流状态发生变化回调
     *
     * @param streamingState 流状态
     * @param data           额外数据
     */
    public void onSteamStateChanged(StreamingState streamingState, Object data) {
    }

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

    private SocketManager.EventListener mStreamingStartedListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            onStreamingStarted(args);
        }
    };

    private SocketManager.EventListener mStreamingStoppedListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            onStringingStopped(args);
        }
    };

    protected abstract void onStreamingStarted(Object... args);

    protected abstract void onStringingStopped(Object... args);

    /**
     * @return 是否是流在使用，播放流和推送流
     */
    public boolean hasStreamUsing() {
        return mStreamPlaying || mStreamPublishing;
    }

    /**
     * @return 是否有流在推送
     */
    public boolean hasStreamPublishing() {
        return mStreamPublishing;
    }

    /**
     * @return 是否有流在播放
     */
    public boolean hasStreamPlaying() {
        return mStreamPlaying;
    }

    @Override
    public void confirmPlayStream(boolean confirm) {
        mStreamPlaying = true;
        mPlayView.setPath(mPlayStreamUrl);
        mPlayView.resume();
        mPlayView.showLoading(true);
    }

    @Override
    public void confirmPublishStream(boolean confirm) {
        mStreamPublishing = true;
        mPublishView.setPath(mPublishStreamUrl);
        if (!mInitPublishVideo) {
            Log.i("aaa","================confirmPublishStream===================");
            mPublishView.start();
        } else {
            mPublishView.resume();
        }
    }
}
