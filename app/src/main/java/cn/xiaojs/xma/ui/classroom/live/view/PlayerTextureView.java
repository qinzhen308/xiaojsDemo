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
 * Date:2016/10/24
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.pili.pldroid.player.widget.PLVideoView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.live.utils.Utils;

public class PlayerTextureView extends BaseMediaView {
    private static final int MESSAGE_ID_RECONNECTING = 0x01;
    private static final int MESSAGE_MEDIA_INFO_RENDERING_START = 0x02;

    private PLVideoTextureView mPlayer;
    private static final String TAG = "PlayerTextureView";
    private boolean mIsPause;
    private boolean mIsMute;
    private int TIME_OUT = 60 * 1000; //60s
    private boolean mRetry = true;
    private long mRetryTime = 0;
    private PLMediaPlayer.OnInfoListener mOnInfoWrapperListener;
    private int mIsLiveStreaming = 1; // 1:live  0:video

    public PlayerTextureView(Context context) {
        super(context);
        init(PLVideoView.ASPECT_RATIO_FIT_PARENT, null);
    }

    public PlayerTextureView(Context context, int ratio) {
        super(context);
        init(ratio, null);
    }

    public PlayerTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(PLVideoView.ASPECT_RATIO_FIT_PARENT, attrs);
    }

    public PlayerTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(PLVideoView.ASPECT_RATIO_FIT_PARENT, attrs);
    }

    public void setPLMediaPlayerInfoListener(PLMediaPlayer.OnInfoListener infoListener) {
        mOnInfoWrapperListener = infoListener;
    }

    @Override
    protected void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                onHandleMessage(msg);
            }
        };
    }

    private void onHandleMessage(Message msg) {
        switch (msg.what) {
            case MESSAGE_ID_RECONNECTING:
                if (!Utils.isLiveStreamingAvailable()) {
                    //finish();
                    return;
                }
                if (mIsPause || !Utils.isNetworkAvailable(getContext())) {
                    sendReconnectMessage();
                    return;
                }
                if (mPlayer != null) {
                    mPlayer.setVideoPath(getPath());
                    mPlayer.start();
                }
                break;
            case MESSAGE_MEDIA_INFO_RENDERING_START:
                showLoading(false);
                break;
        }
    }

    private void init(int ratio, AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PlayerTextureView);
        if (a != null) {
            mIsLiveStreaming = a.getInteger(R.styleable.PlayerTextureView_play_mode, 1);
            a.recycle();
        }

        mPlayer.setBufferingIndicator(mLoadingLayout);
        //showLoading(true);
        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, mIsLiveStreaming);
        if (mIsLiveStreaming == 1) {
            options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);
        }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, 1);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        mPlayer.setAVOptions(options);

        // You can mirror the display
        // mVideoView.setMirror(true);

        mPlayer.setOnCompletionListener(mOnCompletionListener);
        mPlayer.setOnErrorListener(mOnErrorListener);
        mPlayer.setOnPreparedListener(mPrepared);
        mPlayer.setDisplayAspectRatio(ratio);
        mPlayer.setOnInfoListener(mOnInfoListener);
        mPlayer.setDisplayOrientation(0);
        //setBackgroundResource(R.drawable.common_white_bg_corner);
    }

    @Override
    public void setPath(String path) {
        super.setPath(path);
        mPlayer.setVideoPath(getPath());
        mPlayer.start();
    }

    @Override
    protected View initMediaView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_texture_player_view, null);
        mPlayer = (PLVideoTextureView) v.findViewById(R.id.texture_player);
        mPlayer.setDisplayOrientation(90);
        return v;
    }

    @Override
    public void start() {

    }

    @Override
    public void resume() {
        if (mPlayer != null && !mPlayer.isPlaying()) {
            mPlayer.start();
        }
        mRetry = true;
        mResume = true;
        mIsPause = false;
    }

    @Override
    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
        }
        mResume = false;
        mIsPause = true;
    }

    @Override
    public void stop() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stopPlayback();
        }
        mResume = false;
        mIsPause = true;
    }

    @Override
    public void destroy() {
        stopInternal();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
    }

    @Override
    public void mute() {
        if (mIsMute) {
            mPlayer.setVolume(1f, 1f);
        } else {
            mPlayer.setVolume(0f, 0f);
        }
        mIsMute = !mIsMute;
    }

    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            switch (what) {
                case PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    mHandler.removeMessages(MESSAGE_MEDIA_INFO_RENDERING_START);
                    mHandler.sendEmptyMessage(MESSAGE_MEDIA_INFO_RENDERING_START);
                    break;
                case PLMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                    Log.i("aaa", "=========video rotation changed=================");
                    break;
            }

            if (mOnInfoWrapperListener != null) {
                mOnInfoWrapperListener.onInfo(plMediaPlayer, what, extra);
            }
            return false;
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            Log.e(TAG, "Play Completed !");
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    //showToastTips("Invalid URL !");
                    Log.e(TAG, "Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    //showToastTips("404 resource not found !");
                    Log.e(TAG, "404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    //showToastTips("Connection refused !");
                    Log.e(TAG, "Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    //showToastTips("Connection timeout !");
                    Log.e(TAG, "Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    //showToastTips("Empty playlist !");
                    Log.e(TAG, "Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    //showToastTips("Stream disconnected !");
                    Log.e(TAG, "Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    //showToastTips("Network IO Error !");
                    Log.e(TAG, "Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    //showToastTips("Unauthorized Error !");
                    Log.e(TAG, "Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    //showToastTips("Prepare timeout !");
                    Log.e(TAG, "Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    //showToastTips("Read frame timeout !");
                    Log.e(TAG, "Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.e(TAG, "unknown error !");
                    break;
                default:
                    //showToastTips("unknown error !");
                    Log.e(TAG, "unknown error !");
                    break;
            }
            // Todo pls handle the error status here, reconnect or call finish()
            if (isNeedReconnect) {
                sendReconnectMessage();
            } else {
                //finish();
            }
            // Return true means the error has been handled
            // If return false, then `onCompletion` will be called
            return true;
        }
    };
    private PLMediaPlayer.OnPreparedListener mPrepared = new PLMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(PLMediaPlayer plMediaPlayer) {
            Log.e(TAG, "Play prepared !");
        }
    };

    private void sendReconnectMessage() {
        if (mHandler == null) {
            return;
        }

        if (mRetry) {
            mRetry = false;
            mRetryTime = System.currentTimeMillis();
        }

        if (System.currentTimeMillis() - mRetryTime > TIME_OUT) {
            //重试超时
        }

        Logger.i(TAG, "正在重连...");
        showLoading(true);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }

    @Override
    protected void close() {
        pause();
        destroy();
        super.close();
    }

    private void stopInternal() {
        //关闭播放器有点耗时，放在线程里处理
        if (mPlayer != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPlayer.stopPlayback();
                    mPlayer = null;
                }
            }).start();
        }
    }

    @Override
    public boolean isMute() {
        return mIsMute;
    }

    public PLVideoTextureView getPlayer() {
        return mPlayer;
    }

    public boolean isResume() {
        return mResume;
    }

    //TODO to be optimized
    public void delayHideLoading() {
        mHandler.removeMessages(MESSAGE_MEDIA_INFO_RENDERING_START);
        mHandler.sendEmptyMessageDelayed(MESSAGE_MEDIA_INFO_RENDERING_START, 2000);
    }

    public void setDisplayOrientation(int orientation) {
        mPlayer.setDisplayOrientation(orientation);
    }

    public void setDisplayOrientationBySize(int videoW, int viewH) {

    }

}
