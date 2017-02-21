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
 * Date:2016/10/14
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

import com.orhanobut.logger.Logger;
import com.qiniu.android.dns.DnsManager;
import com.qiniu.android.dns.IResolver;
import com.qiniu.android.dns.NetworkInfo;
import com.qiniu.android.dns.http.DnspodFree;
import com.qiniu.android.dns.local.AndroidDnsServer;
import com.qiniu.android.dns.local.Resolver;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.AudioSourceCallback;
import com.qiniu.pili.droid.streaming.CameraStreamingSetting;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.SurfaceTextureCallback;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.classroom.live.core.CameraPreviewFrameView;
import cn.xiaojs.xma.ui.classroom.live.core.Config;
import cn.xiaojs.xma.ui.classroom.live.gles.FBO;
import cn.xiaojs.xma.util.ToastUtil;

public class LiveRecordView extends BaseMediaView implements
        SurfaceTextureCallback,
        AudioSourceCallback{

    private static final String TAG = "LiveRecordView";
    private static final int MSG_START_STREAMING = 0;
    private static final int MSG_STOP_STREAMING = 1;
    private static final int MSG_MUTE = 2;
    private static final int MSG_SHOW_LOADING = 6;
    private static final int MSG_HIDE_LOADING = 7;

    private AspectFrameLayout mAspect;
    private CameraPreviewFrameView mPreviewFrameView;
    private MediaStreamingManager mMediaStreamingManager;
    private CameraStreamingSetting mCameraStreamingSetting;
    private MicrophoneStreamingSetting mMicrophoneStreamingSetting;
    private StreamingProfile mProfile;
    private FBO mFBO = new FBO();

    private StreamingStateChangedListener mOuterStreamingStateChangedListener;

    private boolean mMute;

    private int mCurrentCamFacingIndex;
    private boolean mIsReady;
    private Switcher mCameraSwitcher = new Switcher();
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (mMediaStreamingManager == null)
                return;
            switch (msg.what) {
                case MSG_START_STREAMING:
                    startStreamingInThread();
                    break;
                case MSG_STOP_STREAMING:
                    boolean res = mMediaStreamingManager.stopStreaming();
                    Log.i(TAG, "res:" + res);
                    break;
                case MSG_MUTE:
                    mMediaStreamingManager.mute(mMute = !mMute);
                    break;
                case MSG_HIDE_LOADING:
                    showLoading(false);
                    break;
                case MSG_SHOW_LOADING:
                    showLoading(true);
                    break;
                default:
                    Log.e(TAG, "Invalid message");
                    break;
            }

            Object obj = msg.obj;
            if (obj != null){
                ToastUtil.showToast(getContext(),obj.toString());
            }
        }
    };

    public LiveRecordView(Context context) {
        super(context);
    }

    public LiveRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View initMediaView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.layout_live_record_view,null);
        mAspect = (AspectFrameLayout) v.findViewById(R.id.camera_afl);
        mPreviewFrameView = (CameraPreviewFrameView) v.findViewById(R.id.camera_preview_surfaceView);
        init();
        return v;
    }

    @Override
    public void setPath(String path) {
        super.setPath(path);
        //init();

        try {
            mProfile.setPublishUrl(getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void init() {
        //设置音频的采样率为 44100 HZ，码率为 48 kbps。44100 是 Android 平台唯一保证所有设备支持的采样率
        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(Config.AUDIO_SAMPLING_RATE, Config.AUDIO_BITRATE);
        // fps is 24, video bitrate is 512 * 1024 bps, maxKeyFrameInterval is 48
        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(Config.VIDEO_FPS,
                Config.VIDEO_BITRATE,
                Config.VIDEO_MAX_KEY_FRAME_INTERVAL);
        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
        mAspect.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);
        mProfile = new StreamingProfile();
        /*try {
            mProfile.setPublishUrl(getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/

        mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH3)
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                .setEncodingSizeLevel(Config.ENCODING_LEVEL)
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.BITRATE_PRIORITY)//码率优先
                .setAVProfile(avProfile)
                .setDnsManager(getMyDnsManager())
                //若注册了 mCameraStreamingManager.setStreamStatusCallback ，每隔 3 秒回调 StreamStatus 信息
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                .setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.LAND)
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));


        CameraStreamingSetting.CAMERA_FACING_ID cameraFacingId = chooseCameraFacingId();
        mCurrentCamFacingIndex = cameraFacingId.ordinal();
        mCameraStreamingSetting = new CameraStreamingSetting();
        mCameraStreamingSetting.setCameraId(mCurrentCamFacingIndex)
                .setContinuousFocusModeEnabled(true)
                .setRecordingHint(false)
                //.setFrontCameraMirror(false)//避免前置摄像头字体镜像反转
                .setCameraFacingId(cameraFacingId)
                .setBuiltInFaceBeautyEnabled(true)
                .setResetTouchFocusDelayInMs(3000)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f))
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);
        mMicrophoneStreamingSetting = new MicrophoneStreamingSetting();
        mMicrophoneStreamingSetting.setBluetoothSCOEnabled(false);//麦克风蓝牙支持

        mMediaStreamingManager = new MediaStreamingManager(getContext(), mAspect, mPreviewFrameView,
                AVCodecType.HW_VIDEO_WITH_HW_AUDIO_CODEC); // hw codec

        mMediaStreamingManager.prepare(mCameraStreamingSetting, mMicrophoneStreamingSetting, mProfile);

        mMediaStreamingManager.setStreamingStateListener(new OnStreamingState());
        mMediaStreamingManager.setSurfaceTextureCallback(this);
        //mMediaStreamingManager.setStreamingSessionListener(this);
        //mMediaStreamingManager.setStreamingPreviewCallback(this);
        mMediaStreamingManager.setNativeLoggingEnabled(XiaojsConfig.DEBUG);
        resume();
    }

    public void setPublishUrl(String url){
        if (mProfile != null){
            try {
                mProfile.setPublishUrl(url);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void setOnStreamingStateListener(StreamingStateChangedListener listener) {
        mOuterStreamingStateChangedListener = listener;
    }

    private class OnStreamingState implements StreamingStateChangedListener{

        @Override
        public void onStateChanged(StreamingState streamingState, Object extra) {
            Log.i(TAG, "StreamingState streamingState:" + streamingState + ",extra:" + extra);
            int id = MSG_HIDE_LOADING;
            switch (streamingState) {
                /**
                 * Preparing the environment for network connection.
                 * <p>
                 * The first state after calling {@link #startStreaming()}
                 *
                 * */
                case PREPARING:
                    //id = MSG_SHOW_LOADING;
                    info("PREPARING");
                    break;
                /**
                 * <ol>
                 *     <li>{@link #resume()} done in pure audio streaming</li>
                 *     <li>{@link #resume()} done and camera be activated in AV streaming.</li>
                 * </ol>
                 * */
                case READY:
                    // start streaming when READY
                    mIsReady = true;
                    //id = MSG_SHOW_LOADING;
                    start();
                    info("READY");
                    break;
                /**
                 * Being connecting.
                 *
                 * */
                case CONNECTING:
                    id = MSG_SHOW_LOADING;
                    info("CONNECTING");
                    break;
                /**
                 * The av datas start sending successfully.
                 *
                 * */
                case STREAMING:
                    info("STREAMING");
                    break;
                /**
                 * Streaming has been finished, and you can {@link #startStreaming()} again.
                 *
                 * */
                case SHUTDOWN:
                    Logger.i("Recorder Shutdown!");
                    info("SHUTDOWN");
                    break;
                /**
                 * Connect error.
                 *
                 * The following is the possible case:
                 *
                 * <ol>
                 *     <li>Stream is invalid</li>
                 *     <li>Network is unreachable</li>
                 * </ol>
                 * 连接失败，重连也失败，无法通过网络和服务端建立链接
                 * */
                case IOERROR:
                    info("IOERROR");
                    break;
                /**
                 * The initial state.
                 *
                 * */
                case UNKNOWN:
                    info("UNKNOWN");
                    break;
                /**
                 * Sending buffer is empty.
                 *
                 * */
                case SENDING_BUFFER_EMPTY:
                    info("SENDING_BUFFER_EMPTY");
                    break;
                /**
                 * Sending buffer have been full.
                 *
                 * */
                case SENDING_BUFFER_FULL:
                    info("SENDING_BUFFER_FULL");
                    break;
                /**
                 * {@link AudioRecord#startRecording()} failed.
                 *
                 * */
                case AUDIO_RECORDING_FAIL:
                    info("AUDIO_RECORDING_FAIL");
                    break;
                /**
                 * camera open failed.
                 * 摄像机打开失败，需要提示
                 * */
                case OPEN_CAMERA_FAIL:
                    Log.e(TAG, "Open Camera Fail. id:" + extra);
                    info("OPEN_CAMERA_FAIL");
                    break;
                /**
                 * The network connection has been broken.
                 *  网络连接断开，需要提示
                 * */
                case DISCONNECTED:
                    info("DISCONNECTED");
                    break;
                /**
                 * Invalid streaming url.
                 *
                 * Gets the message after call {@link #setStreamingProfile(StreamingProfile)} if streaming
                 * url is invalid. Also gets the url as the extra info.
                 * */
                case INVALID_STREAMING_URL:
                    Log.e(TAG, "Invalid streaming url:" + extra);
                    info("INVALID_STREAMING_URL");
                    break;
                /**
                 * Invalid streaming url.
                 *
                 * Gets the message after call {@link #setStreamingProfile(StreamingProfile)} if streaming
                 * url is invalid. Also gets the url as the extra info.
                 * */
                case UNAUTHORIZED_STREAMING_URL:
                    Log.e(TAG, "Unauthorized streaming url:" + extra);
                    info("UNAUTHORIZED_STREAMING_URL");
                    break;
                /**
                 * Notify the camera switched.
                 * <p>
                 * extra will including the info the new camera id.
                 *
                 * <ol>
                 *     <li>Camera.CameraInfo.CAMERA_FACING_FRONT</li>
                 *     <li>Camera.CameraInfo.CAMERA_FACING_BACK</li>
                 * </ol>
                 *
                 * <pre>
                 *     <code>
                 *         Log.i(TAG, "current camera id:" + (Integer)extra);
                 *     </code>
                 * </pre>
                 *  摄像机切换镜头
                 * */
                case CAMERA_SWITCHED:
                    info("CAMERA_SWITCHED");
                    break;
                /**
                 * Notify the torch info after camera be active.
                 * <p>
                 * extra will including the info if the device support the Torch.
                 *
                 * <ol>
                 *     <li>true, supported</li>
                 *     <li>false, unsupported</li>
                 * </ol>
                 *
                 * <pre>
                 *     <code>
                 *         final boolean isSupportedTorch = (Boolean) extra;
                 *     </code>
                 * </pre>
                 *
                 * */
                case TORCH_INFO:
                    info("TORCH_INFO");
                    break;
            }
            mHandler.sendMessage(mHandler.obtainMessage(id));

            if (mOuterStreamingStateChangedListener != null) {
                mOuterStreamingStateChangedListener.onStateChanged(streamingState, extra);
            }
        }

    }

    private void info(String info){
        Message msg = Message.obtain();
        msg.obj = info;
        mHandler.sendMessage(msg);
    }

    @Override
    public void resume() {
        if (mMediaStreamingManager != null){
            mMediaStreamingManager.resume();
        }
    }

    @Override
    public void pause() {
        mIsReady = false;
        if (mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mMediaStreamingManager != null){
            mMediaStreamingManager.pause();
        }
    }

    @Override
    public void destroy() {
        stopStreamingInternal();
    }

    @Override
    protected void mute() {
        //本地静音
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_MUTE), 50);
    }

    @Override
    protected void switchCamera() {
        //切换摄像头
        mHandler.removeCallbacks(mCameraSwitcher);
        mHandler.postDelayed(mCameraSwitcher,100);
    }

    @Override
    public void start() {
        startStreamingInThread ();
    }

    private void startStreamingInThread(){
       Thread t = new T();
        t.start();
    }

    private class T extends Thread{
        @Override
        public void run() {
            if (mMediaStreamingManager != null){
                mMediaStreamingManager.startStreaming();
            }
        }
    }

    private void stopStreamingInternal(){
        if (mMediaStreamingManager != null){
            mMediaStreamingManager.destroy();
            mMediaStreamingManager = null;
        }
        mAspect = null;
        mPreviewFrameView = null;
    }

    private static DnsManager getMyDnsManager() {
        IResolver r0 = new DnspodFree();
        IResolver r1 = AndroidDnsServer.defaultResolver();
        IResolver r2 = null;
        try {
            r2 = new Resolver(InetAddress.getByName("119.29.29.29"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
    }

    private CameraStreamingSetting.CAMERA_FACING_ID chooseCameraFacingId() {
        if (CameraStreamingSetting.hasCameraFacing(CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD)) {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_3RD;
        } else if (CameraStreamingSetting.hasCameraFacing(CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT)) {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_FRONT;
        } else {
            return CameraStreamingSetting.CAMERA_FACING_ID.CAMERA_FACING_BACK;
        }
    }

    @Override
    public void onAudioSourceAvailable(ByteBuffer byteBuffer, int i, long l, boolean b) {

    }

    @Override
    public void onSurfaceCreated() {
        Log.i(TAG, "onSurfaceCreated");
        mFBO.initialize(getContext());
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        Log.i(TAG, "onSurfaceChanged width:" + width + ",height:" + height);
        mFBO.updateSurfaceSize(width, height);
    }

    @Override
    public void onSurfaceDestroyed() {
        Log.i(TAG, "onSurfaceDestroyed");
        mFBO.release();
    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight, float[] transformMatrix) {
        int newTexId = mFBO.drawFrame(texId, texWidth, texHeight);
        return newTexId;
    }

    private class Switcher implements Runnable {
        @Override
        public void run() {
            mMediaStreamingManager.switchCamera();
        }
    }

    @Override
    protected boolean isMute() {
        return mMute;
    }
}
