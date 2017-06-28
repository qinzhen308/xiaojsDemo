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

import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.XjsUtils;

public abstract class VideoController implements StreamConfirmCallback {
    public final static String STREAM_EXPIRED = "expired";
    protected Context mContext;
    protected View mRoot;

    /**
     * 播放,推送view
     */
    protected LiveRecordView mPublishView;
    protected PlayerTextureView mPlayView;

    protected String mPlayStreamUrl;
    protected String mPublishStreamUrl;

    protected boolean mStreamPlaying;
    protected boolean mStreamPublishing;
    /**
     * 是否需要重新推流
     */
    protected boolean mNeedStreamRePublishing = false;
    /**
     * 是否需要重新播放流
     */
    protected boolean mNeedStreamRePlaying = false;
    protected OnStreamChangeListener mStreamChangeListener;

    protected int mPlayType;
    protected int mPublishType;

    private Handler mHandler;
    private CommonDialog mMobileNetworkDialog;
    protected boolean mPausePublishByToggleResolution;

    protected int mPeerStreamViewWidth = 160;
    protected int mPeerStreamViewHeight = 90;
    protected int mPeerStreamViewTopMargin = 10;
    protected int mPeerStreamViewMargin = 10;

    protected Object mExtraData;
    protected boolean mOnDestroy = false;
    protected boolean mCancelPublish = false;

    public VideoController(Context context, View root, OnStreamChangeListener listener) {
        mContext = context;
        mRoot = root;
        mStreamChangeListener = listener;
        mHandler = new Handler();

        mPeerStreamViewWidth = context.getResources().getDimensionPixelOffset(R.dimen.px160);
        mPeerStreamViewHeight = context.getResources().getDimensionPixelOffset(R.dimen.px90);
        mPeerStreamViewMargin = context.getResources().getDimensionPixelOffset(R.dimen.px10);
        mPeerStreamViewTopMargin = context.getResources().getDimensionPixelOffset(R.dimen.px110);

        init(root);
        //一个activity不能多个LiveRecordView同时存在
        if (mPublishView != null) {
            mPublishView.setOnStreamingStateListener(mStreamingStateListener);
        }

        if (mPlayView != null) {
            mPlayView.setOnStreamStateChangeListener(mStreamChangeListener);
        }

        onResume();

        //监听流开始或暂停
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED), mStreamingStartedListener);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STOPPED), mStreamingStoppedListener);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLAIM_STREAMING), mStreamReclaimedListener);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STOP_STREAM_BY_EXPIRATION), mStreamStopByExpirationListener);
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
        if (mPlayView != null && !mPlayView.isResume()) {
            mPlayView.resume();
        }
        if (mPublishView != null && !mPublishView.isResume()) {
            mPublishView.resume();
        }

        if (mNeedStreamRePublishing) {
            //mNeedStreamRePublishing = false;
            publishStream(mPublishType, mPublishStreamUrl);
        }

        if (mNeedStreamRePlaying) {
            playStream(mPlayType, mPlayStreamUrl);
        }
    }

    /**
     * pause视频
     */
    public void onPause() {
        pausePlayStream(mPlayType);
        pausePublishStream(mPublishType);
    }

    /**
     * destroy视频
     */
    public void onDestroy() {
        mOnDestroy = true;

        if (mPlayView != null) {
            mPlayView.destroy();
        }
        if (mPublishView != null) {
            mPublishView.destroy();
        }

        offSocketListener();
    }

    protected void offSocketListener() {
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED), mStreamingStartedListener);
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STOPPED), mStreamingStoppedListener);
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLAIM_STREAMING), mStreamReclaimedListener);
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STOP_STREAM_BY_EXPIRATION), mStreamStopByExpirationListener);
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (mPublishView != null && mPublishView.getVisibility() == View.VISIBLE) {
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
    public abstract void pausePublishStream(int type);

    public void pausePublishStream() {
        pausePublishStream(mPublishType);
    }

    /**
     * 开始推流
     *
     * @see #publishStream(int, String)
     */
    public void publishStream() {
        publishStream(mPublishType, mPublishStreamUrl);
    }

    /**
     * 开始推流
     *
     * @see #publishStream(int, String)
     */
    public void publishStream(int type) {
        publishStream(type, mPublishStreamUrl);
    }

    /**
     * 开始推流
     *
     * @see #confirmPublishStream(boolean)
     */
    public void publishStream(int type, String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        mPublishType = type;
        mPublishStreamUrl = url;
        handleNetworkLiveDialog(true);
    }

    /**
     * 播放流
     *
     * @see #playStream(int, String)
     */
    public void playStream() {
        playStream(mPlayType, mPlayStreamUrl);
    }

    /**
     * 播放流
     *
     * @see #playStream(int, String)
     */
    public void playStream(int type) {
        playStream(type, mPlayStreamUrl);
    }

    /**
     * 播放流
     *
     * @see #confirmPlayStream(boolean)
     */
    public void playStream(int type, String url) {
        playStream(type, url, null);
    }

    /**
     * 播放流
     *
     * @see #confirmPlayStream(boolean)
     */
    public void playStream(int type, String url, Object extra) {
        if (!TextUtils.isEmpty(url)) {
            mPlayStreamUrl = url;
        }

        mPlayType = type;
        mExtraData = extra;
        if (mPlayView != null && !TextUtils.isEmpty(mPlayStreamUrl)) {
            handleNetworkLiveDialog(false);
        }
    }

    /**
     * 暂停播放流
     */
    public abstract void pausePlayStream(int type);

    public void pausePlayStream() {
        pausePlayStream(mPlayType);
    }

    /**
     * 推流的流状态发生变化回调
     *
     * @param streamingState 流状态
     * @param data           额外数据
     */
    public void onSteamStateChanged(StreamingState streamingState, Object data) {

    }

    /**
     * 推流的监听器（个人推流或直播推流）
     */
    protected StreamingStateChangedListener mStreamingStateListener = new StreamingStateChangedListener() {
        @Override
        public void onStateChanged(StreamingState streamingState, Object o) {
            switch (streamingState) {
                case STREAMING:
                    //do some thing
                    break;
            }
            onSteamStateChanged(streamingState, o);
        }
    };

    /**
     * 开启静音/取消静音
     */
    public abstract void muteOrUnmute();

    /**
     * 打开/关闭摄像头
     */
    public abstract void openOrCloseCamera();

    public abstract void togglePublishResolution();

    /**
     * 切换摄像头的方向
     *
     * @param targetOrientation 待切换的摄像头方向
     */
    public void togglePublishOrientation(int targetOrientation, LiveRecordView.OnStreamOrientationListener listener) {
        if (mPublishView != null) {
            mPublishView.encodingOrientationSwitch(targetOrientation, listener);
        }
    }

    public void release() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    private SocketManager.EventListener mStreamingStartedListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.STREAMING_STARTED**");
            }

            onStreamingStarted(args);
        }
    };

    private SocketManager.EventListener mStreamingStoppedListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.STREAMING_STOPPED**");
            }
            onStreamingStopped(args);
        }
    };

    private SocketManager.EventListener mStreamReclaimedListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.CLAIM_STREAMING**");
            }
            onStreamingReclaimed(args);
        }
    };

    private SocketManager.EventListener mStreamStopByExpirationListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.STOP_STREAM_BY_EXPIRATION**");
            }
            onStreamingStoppedByExpired(args);
        }
    };

    protected abstract void onStreamingStarted(Object... args);

    protected abstract void onStreamingStopped(Object... args);

    protected abstract void onStreamingStoppedByExpired(Object... args);

    protected abstract void onStreamingReclaimed(Object... args);

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

    public boolean needStreamRePublishing() {
        return mNeedStreamRePublishing;
    }

    public boolean needStreamRePlaying() {
        return mNeedStreamRePlaying;
    }

    /**
     * 处理移动网络的弹出对话框，提示是否允许使用移动网络进行直播
     */
    private void handleNetworkLiveDialog(final boolean publishStream) {
        if (ClassroomBusiness.getCurrentNetwork(mContext) == ClassroomBusiness.NETWORK_OTHER) {
            boolean open = XjsUtils.getSharedPreferences().getBoolean(Constants.KEY_MOBILE_NETWORK_LIVE, false);
            boolean allowMobileLive = XjsUtils.getSharedPreferences().getBoolean(Constants.KEY_MOBILE_NETWORK_LIVE_4_LESSON, false);
            if (!open && !allowMobileLive) {
                if (mMobileNetworkDialog == null) {
                    mMobileNetworkDialog = new CommonDialog(mContext);
                    mMobileNetworkDialog.setTitle(R.string.mobile_network_title);
                    mMobileNetworkDialog.setDesc(R.string.mobile_network_desc);
                    mMobileNetworkDialog.setLefBtnText(R.string.mobile_network_forbid);
                    mMobileNetworkDialog.setRightBtnText(R.string.mobile_network_allow);
                    mMobileNetworkDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                        @Override
                        public void onClick() {
                            mMobileNetworkDialog.dismiss();
                            XjsUtils.getSharedPreferences().edit().putBoolean(Constants.KEY_MOBILE_NETWORK_LIVE_4_LESSON, true).apply();
                            if (publishStream) {
                                confirmPublishStream(true);
                            } else {
                                confirmPlayStream(true);
                            }
                        }
                    });
                }
                mMobileNetworkDialog.show();
            } else {
                if (publishStream) {
                    confirmPublishStream(true);
                } else {
                    confirmPlayStream(true);
                }
            }
        } else {
            if (publishStream) {
                confirmPublishStream(true);
            } else {
                confirmPlayStream(true);
            }
        }
    }

    public void setPublishStreamByToggleResolution(boolean isToggleResolution) {
        mPausePublishByToggleResolution = isToggleResolution;
    }

    public void setCancelPublish(boolean cancel) {
        mCancelPublish = cancel;
    }

    public int getPlayType() {
        return mPlayType;
    }

    public int getPublishType() {
        return mPublishType;
    }
}
