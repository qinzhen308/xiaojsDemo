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
 * Date:2016/10/24
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.live.utils.Utils;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

public class PlayerTextureView extends BaseMediaView{
    private PLVideoTextureView mPlayer;
    private static final String TAG = "PlayerTextureView";

    public PlayerTextureView(Context context) {
        super(context);
        init();
    }

    public PlayerTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PlayerTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){

        mPlayer.setBufferingIndicator(mLoadingView);
        showLoading(true);
        AVOptions options = new AVOptions();

        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        options.setInteger(AVOptions.KEY_GET_AV_FRAME_TIMEOUT, 10 * 1000);
        // Some optimization with buffering mechanism when be set to 1
        options.setInteger(AVOptions.KEY_LIVE_STREAMING, 1);
        options.setInteger(AVOptions.KEY_DELAY_OPTIMIZATION, 1);

        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, 1);

        // whether start play automatically after prepared, default value is 1
        options.setInteger(AVOptions.KEY_START_ON_PREPARED, 0);

        mPlayer.setAVOptions(options);

        // You can mirror the display
        // mVideoView.setMirror(true);

        // You can also use a custom `MediaController` widget
//        mMediaController = new MediaController(this, false, isLiveStreaming==1);
//        mVideoView.setMediaController(mMediaController);

        mPlayer.setOnCompletionListener(mOnCompletionListener);
        mPlayer.setOnErrorListener(mOnErrorListener);
        mPlayer.setDisplayAspectRatio(PLVideoTextureView.ASPECT_RATIO_4_3);
        canClose(true);
        setBackgroundResource(R.drawable.common_white_bg_corner);
    }

    @Override
    public void setPath(String path) {
        super.setPath(path);
        mPlayer.setVideoPath(getPath());
        mPlayer.start();
    }

    @Override
    protected View initMediaView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_texture_player_view,null);
        mPlayer = (PLVideoTextureView) v.findViewById(R.id.texture_player);
        return v;
    }

    @Override
    public void start() {

    }

    @Override
    public void resume() {
        if (mPlayer != null && !mPlayer.isPlaying()){
            mPlayer.start();
        }
    }

    @Override
    public void pause() {
        if (mPlayer != null && mPlayer.isPlaying()){
            mPlayer.pause();
        }
    }

    @Override
    public void destroy() {
        //mPlayer.stopPlayback();
        stopInternal();
    }

    @Override
    protected void mute() {
        //远端静音
    }

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener = new PLMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(PLMediaPlayer plMediaPlayer) {
            Log.e(TAG,"Play Completed !");
            //showToastTips("Play Completed !");
            //finish();
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            boolean isNeedReconnect = false;
            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_INVALID_URI:
                    //showToastTips("Invalid URL !");
                    Log.e(TAG,"Invalid URL !");
                    break;
                case PLMediaPlayer.ERROR_CODE_404_NOT_FOUND:
                    //showToastTips("404 resource not found !");
                    Log.e(TAG,"404 resource not found !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_REFUSED:
                    //showToastTips("Connection refused !");
                    Log.e(TAG,"Connection refused !");
                    break;
                case PLMediaPlayer.ERROR_CODE_CONNECTION_TIMEOUT:
                    //showToastTips("Connection timeout !");
                    Log.e(TAG,"Connection timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_EMPTY_PLAYLIST:
                    //showToastTips("Empty playlist !");
                    Log.e(TAG,"Empty playlist !");
                    break;
                case PLMediaPlayer.ERROR_CODE_STREAM_DISCONNECTED:
                    //showToastTips("Stream disconnected !");
                    Log.e(TAG,"Stream disconnected !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    //showToastTips("Network IO Error !");
                    Log.e(TAG,"Network IO Error !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_UNAUTHORIZED:
                    //showToastTips("Unauthorized Error !");
                    Log.e(TAG,"Unauthorized Error !");
                    break;
                case PLMediaPlayer.ERROR_CODE_PREPARE_TIMEOUT:
                    //showToastTips("Prepare timeout !");
                    Log.e(TAG,"Prepare timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.ERROR_CODE_READ_FRAME_TIMEOUT:
                    //showToastTips("Read frame timeout !");
                    Log.e(TAG,"Read frame timeout !");
                    isNeedReconnect = true;
                    break;
                case PLMediaPlayer.MEDIA_ERROR_UNKNOWN:
                    Log.e(TAG,"unknown error !");
                    break;
                default:
                    //showToastTips("unknown error !");
                    Log.e(TAG,"unknown error !");
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
    private static final int MESSAGE_ID_RECONNECTING = 0x01;

    private void sendReconnectMessage() {
        //showToastTips("正在重连...");
        Log.e(TAG,"正在重连...");
        //mLoadingView.setVisibility(View.VISIBLE);
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE_ID_RECONNECTING), 500);
    }

    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what != MESSAGE_ID_RECONNECTING) {
                return;
            }
            if (!Utils.isLiveStreamingAvailable()) {
                //finish();
                return;
            }
            if (!Utils.isNetworkAvailable(getContext())) {
                sendReconnectMessage();
                return;
            }
            mPlayer.setVideoPath(getPath());
            mPlayer.start();
        }
    };

    @Override
    protected void close() {
        pause();
        destroy();
        super.close();
    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        if (visibility == VISIBLE){

        }else {

        }
    }

    private void stopInternal(){
        //关闭播放器有点耗时，放在线程里处理
        if (mPlayer != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mPlayer.stopPlayback();
                }
            }).start();
        }
    }

}
