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
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

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
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.MediaStreamingManager;
import com.qiniu.pili.droid.streaming.MicrophoneStreamingSetting;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.SurfaceTextureCallback;
import com.qiniu.pili.droid.streaming.widget.AspectFrameLayout;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.classroom.live.core.CameraPreviewFrameView;
import cn.xiaojs.xma.ui.classroom.live.core.Config;
import cn.xiaojs.xma.ui.classroom.live.gles.FBO;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.util.ToastUtil;
import cn.xiaojs.xma.util.XjsUtils;

public class LiveRecordView extends BaseMediaView implements
        SurfaceTextureCallback,
        AudioSourceCallback,
        CameraPreviewFrameView.Listener,
        StreamingSessionListener {


    public interface Listener {
        void onViewClickedListener();
    }



    private static final String TAG = "LiveRecordView";
    private static final int MSG_START_STREAMING = 0;
    private static final int MSG_STOP_STREAMING = 1;
    private static final int MSG_MUTE = 2;
    private static final int MSG_SHOW_LOADING = 6;
    private static final int MSG_HIDE_LOADING = 7;
    private static final int MSG_SWITCH_ORIENTATION = 8;
    private static final int MSG_SWITCH_RESOLUTION = 9;
    private static final int MSG_SWITCH_ORIENTATION_DELAY = 10;

    private AspectFrameLayout mAspect;
    private CameraPreviewFrameView mPreviewFrameView;
    private MediaStreamingManager mMediaStreamingManager;
    private CameraStreamingSetting mCameraStreamingSetting;
    private MicrophoneStreamingSetting mMicrophoneStreamingSetting;
    private StreamingProfile mProfile;
    private FBO mFBO = new FBO();

    private StreamingStateChangedListener mOuterStreamingStateChangedListener;
    private Listener listener;

    private boolean mMute;
    private int mQuality = Constants.QUALITY_STANDARD;

    private int mCurrentCamFacingIndex;
    private boolean mIsReady;
    private Switcher mCameraSwitcher = new Switcher();

    private int mCurrentZoom = 0;
    private int mMaxZoom = 0;

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
        if (mMediaStreamingManager == null) {
            return;
        }

        switch (msg.what) {
            case MSG_START_STREAMING:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        boolean success = mMediaStreamingManager.startStreaming();
                        if (XiaojsConfig.DEBUG) {
                            Logger.i(success + "");
                        }
                    }
                }).start();
                break;
            case MSG_STOP_STREAMING:
                boolean success = mMediaStreamingManager.stopStreaming();
                if (XiaojsConfig.DEBUG) {
                    Logger.i(success + "");
                }
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
            case MSG_SWITCH_ORIENTATION:
                mMediaStreamingManager.stopStreaming();
                mProfile.setEncodingOrientation(msg.arg1 == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        ? StreamingProfile.ENCODING_ORIENTATION.PORT : StreamingProfile.ENCODING_ORIENTATION.LAND);
                mMediaStreamingManager.setStreamingProfile(mProfile);
                mMediaStreamingManager.notifyActivityOrientationChanged();
                /*Object obj = msg.obj;
                if (obj instanceof OnStreamOrientationListener) {
                    ((OnStreamOrientationListener)obj).onStreamOrientationChanged(msg.arg1);
                }*/
                if (msg.obj != null) {
                    Message m = mHandler.obtainMessage(MSG_SWITCH_ORIENTATION);
                    m.obj = msg.obj;
                    m.arg1 = msg.arg1;
                    m.what = MSG_SWITCH_ORIENTATION_DELAY;
                    mHandler.sendMessageDelayed(m, 500);
                }
                break;
            case MSG_SWITCH_ORIENTATION_DELAY:
                Object o = msg.obj;
                if (o instanceof OnStreamOrientationListener) {
                    ((OnStreamOrientationListener) o).onStreamOrientationChanged(msg.arg1);
                }
                break;
            case MSG_SWITCH_RESOLUTION:
                mMediaStreamingManager.stopStreaming();
                setStreamingProfile();
                mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_STREAMING), 50);
                break;
            default:
                Log.e(TAG, "Invalid message");
                break;
        }

        Object obj = msg.obj;
        if (obj != null && XiaojsConfig.DEBUG) {
            ToastUtil.showToast(getContext(), obj.toString());
        }
    }

    public LiveRecordView(Context context) {
        super(context);
    }

    public LiveRecordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected View initMediaView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View v = inflater.inflate(R.layout.layout_live_record_view, null);
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
        mMediaStreamingManager.setStreamingProfile(mProfile);
    }

    public void init() {
        setStreamingProfile();

        CameraStreamingSetting.CAMERA_FACING_ID cameraFacingId = chooseCameraFacingId();
        mCurrentCamFacingIndex = cameraFacingId.ordinal();
        mCameraStreamingSetting = new CameraStreamingSetting();
        mCameraStreamingSetting.setCameraId(mCurrentCamFacingIndex)
                .setContinuousFocusModeEnabled(true)
                .setRecordingHint(false)
                .setFrontCameraMirror(false)//避免前置摄像头字体镜像反转
                .setCameraFacingId(cameraFacingId)
                .setBuiltInFaceBeautyEnabled(true)
                .setResetTouchFocusDelayInMs(3000)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(0.5f, 0.5f, 0.5f))
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);
        mMicrophoneStreamingSetting = new MicrophoneStreamingSetting();
        mMicrophoneStreamingSetting.setBluetoothSCOEnabled(false);//麦克风蓝牙支持

        mMediaStreamingManager = new MediaStreamingManager(getContext(), mAspect, mPreviewFrameView,
                AVCodecType.HW_VIDEO_WITH_HW_AUDIO_CODEC); // hw codec

        mMediaStreamingManager.prepare(mCameraStreamingSetting, mMicrophoneStreamingSetting, mProfile);

        mPreviewFrameView.setListener(this);
        mMediaStreamingManager.setStreamingStateListener(new OnStreamingState());
        mMediaStreamingManager.setSurfaceTextureCallback(this);
        mMediaStreamingManager.setStreamingSessionListener(this);
        mMediaStreamingManager.setNativeLoggingEnabled(XiaojsConfig.DEBUG);
    }

    public void setViewClickListener(Listener listener) {
        this.listener = listener;
    }

    public void setPublishUrl(String url) {
        if (mProfile != null) {
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

    @Override
    public boolean onRecordAudioFailedHandled(int i) {
        return false;
    }

    @Override
    public boolean onRestartStreamingHandled(int i) {
        if (mMediaStreamingManager != null) {
            return mMediaStreamingManager.startStreaming();
        }

        return false;
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        Camera.Size size = null;
        if (list != null) {
            for (Camera.Size s : list) {
                if (s.height >= 480) {
                    size = s;
                    break;
                }
            }
        }
//        Log.e(TAG, "selected size :" + size.width + "x" + size.height);
        return size;
    }

    private class OnStreamingState implements StreamingStateChangedListener {

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

                    mMaxZoom = mMediaStreamingManager.getMaxZoom();

                    if (mHandler != null) {
                        mHandler.removeCallbacksAndMessages(null);
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_START_STREAMING), 50);
                    }
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
            if (mHandler != null) {
                mHandler.sendMessage(mHandler.obtainMessage(id));
            }

            if (mOuterStreamingStateChangedListener != null) {
                mOuterStreamingStateChangedListener.onStateChanged(streamingState, extra);
            }
        }

    }

    private void info(String info) {
        if (XiaojsConfig.DEBUG) {
            Message msg = Message.obtain();
            msg.obj = info;
            if (mHandler != null) {
                mHandler.sendMessage(msg);
            }
        }
    }

    @Override
    public void resume() {
        if (mMediaStreamingManager != null) {
            mMediaStreamingManager.resume();
            mResume = true;
        }
    }

    @Override
    public void pause() {
        mIsReady = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mMediaStreamingManager != null) {
            mMediaStreamingManager.pause();
        }

        mResume = false;
    }

    @Override
    public void stop() {

    }

    @Override
    public void destroy() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                stopStreamingInternal();
            }
        }).start();
        mAspect = null;
        mPreviewFrameView = null;
    }

    @Override
    public void mute() {
        //本地静音
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_MUTE), 50);
    }

    @Override
    public void switchCamera() {
        //切换摄像头
        mHandler.removeCallbacks(mCameraSwitcher);
        mHandler.postDelayed(mCameraSwitcher, 100);
    }

    @Override
    public void start() {
        //do nothing
    }

    private void stopStreamingInternal() {
        try {
            if (mMediaStreamingManager != null) {
                mMediaStreamingManager.destroy();
                mMediaStreamingManager = null;
            }
        } catch (Exception e) {

        }
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

    protected void setFocusAreaIndicator() {
//        if (mRotateLayout == null) {
//            mRotateLayout = (RotateLayout) findViewById(R.id.focus_indicator_rotate_layout);
//            mMediaStreamingManager.setFocusAreaIndicator(mRotateLayout,
//                    mRotateLayout.findViewById(R.id.focus_indicator));
//        }
    }


    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (XiaojsConfig.DEBUG) {
            Log.i(TAG, "onSingleTapUp X:" + e.getX() + ",Y:" + e.getY());
        }

//        if (mIsReady) {
//            setFocusAreaIndicator();
//            mMediaStreamingManager.doSingleTapUp((int) e.getX(), (int) e.getY());
//            return true;
//        }
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {

        if (listener != null) {
            listener.onViewClickedListener();
        }

        return false;
    }

    @Override
    public boolean onZoomValueChanged(float factor) {

        if (mIsReady && mMediaStreamingManager.isZoomSupported()) {
            mCurrentZoom = (int) (mMaxZoom * factor);
            mCurrentZoom = Math.min(mCurrentZoom, mMaxZoom);
            mCurrentZoom = Math.max(0, mCurrentZoom);

            if (XiaojsConfig.DEBUG) {
                Log.d(TAG, "zoom ongoing, scale: " + mCurrentZoom + ",factor:" + factor + ",maxZoom:" + mMaxZoom);
            }

            mMediaStreamingManager.setZoomValue(mCurrentZoom);
        }

        return false;
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
    public boolean isMute() {
        return mMute;
    }

    public void captureOriginalFrame(FrameCapturedCallback callback) {
        mMediaStreamingManager.captureFrame(0, 0, callback);
    }

    @Override
    public boolean isResume() {
        return mResume;
    }

    public void togglePublishResolution() {
        if (mMediaStreamingManager == null) {
            return;
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SWITCH_RESOLUTION), 50);
        }
    }

    private void setStreamingProfile() {
        if (mProfile == null) {
            mProfile = new StreamingProfile();
        }
        mQuality = XjsUtils.getSharedPreferences().getInt(Constants.KEY_QUALITY, Constants.QUALITY_STANDARD);
        int fps = Config.VIDEO_STANDARD_FPS;
        int bps = Config.VIDEO_STANDARD_BITRATE;
        int resolution = StreamingProfile.VIDEO_ENCODING_HEIGHT_480;
        switch (mQuality) {
            case Constants.QUALITY_FLUENT:
                fps = Config.VIDEO_FLUENT_FPS;
                bps = Config.VIDEO_FLUENT_BITRATE;
                resolution = StreamingProfile.VIDEO_ENCODING_HEIGHT_240;
                break;
            case Constants.QUALITY_STANDARD:
                fps = Config.VIDEO_STANDARD_FPS;
                bps = Config.VIDEO_STANDARD_BITRATE;
                resolution = StreamingProfile.VIDEO_ENCODING_HEIGHT_480;
                break;
            case Constants.QUALITY_HIGH:
                fps = Config.VIDEO_STANDARD_FPS;
                bps = Config.VIDEO_HIGH_BITRATE;
                resolution = StreamingProfile.VIDEO_ENCODING_HEIGHT_720;
                break;
        }

        //设置音频的采样率为 44100 HZ，码率为 48 kbps。44100 是 Android 平台唯一保证所有设备支持的采样率
        StreamingProfile.AudioProfile aProfile = new StreamingProfile.AudioProfile(Config.AUDIO_SAMPLING_RATE, Config.AUDIO_BITRATE);
        // fps is 24, video bitrate is 512 * 1024 bps, maxKeyFrameInterval is 48
        StreamingProfile.VideoProfile vProfile = new StreamingProfile.VideoProfile(fps, bps, Config.VIDEO_MAX_KEY_FRAME_INTERVAL);
        StreamingProfile.AVProfile avProfile = new StreamingProfile.AVProfile(vProfile, aProfile);
        mAspect.setShowMode(AspectFrameLayout.SHOW_MODE.REAL);

        mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH3)
                .setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2)
                .setEncodingSizeLevel(resolution)
                .setEncoderRCMode(StreamingProfile.EncoderRCModes.BITRATE_PRIORITY)//码率优先
                .setAVProfile(avProfile)
                .setDnsManager(getMyDnsManager())
                //若注册了 mCameraStreamingManager.setStreamStatusCallback ，每隔 3 秒回调 StreamStatus 信息
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                .setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.PORT)
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f, 0.8f, 3.0f, 20 * 1000));
    }

    private void stopStreaming() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_STOP_STREAMING), 50);
    }

    public void encodingOrientationSwitch(int targetOrientation, OnStreamOrientationListener listener) {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            Message msg = mHandler.obtainMessage(MSG_SWITCH_ORIENTATION);
            msg.arg1 = targetOrientation;
            msg.obj = listener;
            mHandler.sendMessageDelayed(msg, 50);
        }
    }

    public interface OnStreamOrientationListener {
        public void onStreamOrientationChanged(int orientation);
    }
}
