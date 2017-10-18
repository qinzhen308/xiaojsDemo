package cn.xiaojs.xma.ui.classroom2.base;

import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;

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
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingPreviewCallback;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.SurfaceTextureCallback;
import com.qiniu.pili.droid.streaming.WatermarkSetting;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.classroom2.live.gles.FBO;
import cn.xiaojs.xma.ui.classroom2.widget.CameraPreviewFrameView;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public abstract class AVFragment extends MovieFragment implements StreamingSessionListener,
        StreamStatusCallback,
        StreamingStateChangedListener,
        AudioSourceCallback,
        StreamingPreviewCallback,
        CameraPreviewFrameView.Listener,
        SurfaceTextureCallback {


    protected CameraStreamingSetting cameraStreamingSetting;
    protected StreamingProfile mProfile = new StreamingProfile();
    protected CameraPreviewFrameView cameraPreviewFrameView;

    protected MediaStreamingManager mediaStreamingManager;

    protected boolean isReady;

    private int mCurrentZoom = 0;
    private int mMaxZoom = 0;

    private FBO mFBO = new FBO();


    protected abstract CameraPreviewFrameView createCameraPreview();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        cameraPreviewFrameView = createCameraPreview();

        initEncodingProfile();
        initCameraStreamingSetting();

        try {
            mProfile.setPublishUrl(classroomEngine.getPublishUrl());
        } catch (URISyntaxException e) {
            e.printStackTrace();
            if (XiaojsConfig.DEBUG) {
                Logger.e("setPublishUrl exception: %s", e.getMessage());
            }
        }

        initStreamingManager(cameraPreviewFrameView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mediaStreamingManager.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        isReady = false;
        mediaStreamingManager.pause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaStreamingManager.destroy();
    }
    protected void startStreaming() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                mediaStreamingManager.startStreaming();
            }
        }).start();

    }

    protected void stopStreaming() {
        mediaStreamingManager.stopStreaming();
    }

    protected void switchCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mediaStreamingManager.switchCamera();
            }
        }).start();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRotate(int orientation) {
        onRotateToInitBoard(orientation);
    }

    @Override
    public void closeMovie() {

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {

        if (XiaojsConfig.DEBUG) {
            Logger.i("StreamingState streamingState:" + streamingState + ",extra:" + extra);
        }

        switch (streamingState) {
            case PREPARING:
                if (XiaojsConfig.DEBUG) {
                    Logger.d("PREPARING");
                }
                break;
            case READY:
                if (XiaojsConfig.DEBUG) {
                    Logger.d("READY");
                }
                isReady = true;
                mMaxZoom = mediaStreamingManager.getMaxZoom();

                startStreaming();
                break;
            case CONNECTING:
                if (XiaojsConfig.DEBUG) {
                    Logger.d("CONNECTING");
                }
                break;
            case STREAMING:
                if (XiaojsConfig.DEBUG) {
                    Logger.d("STREAMING");
                }
                break;
            case SHUTDOWN:
                if (XiaojsConfig.DEBUG) {
                    Logger.e("SHUTDOWN");
                }
                break;
            case IOERROR:
                /**
                 * Network-connection is unavailable when `startStreaming`.
                 * You can `startStreaming` later or just finish the streaming
                 */
                if (XiaojsConfig.DEBUG) {
                    Logger.e("IOERROR");
                }
                break;
            case DISCONNECTED:
                /**
                 * Network-connection is broken when streaming
                 * You can do reconnecting in `onRestartStreamingHandled`
                 */
                if (XiaojsConfig.DEBUG) {
                    Logger.e("DISCONNECTED");
                }
                break;
            case UNKNOWN:
                if (XiaojsConfig.DEBUG) {
                    Logger.e("UNKOWN");
                }
                break;
            case SENDING_BUFFER_EMPTY:
                break;
            case SENDING_BUFFER_FULL:
                break;
            case AUDIO_RECORDING_FAIL:
                break;
            case INVALID_STREAMING_URL:
                if (XiaojsConfig.DEBUG) {
                    Logger.e("Invalid streaming url:" + extra);
                }
                break;
            case UNAUTHORIZED_STREAMING_URL:
                if (XiaojsConfig.DEBUG) {
                    Logger.e("Unauthorized streaming url:" + extra);
                }
                break;
            case OPEN_CAMERA_FAIL:
                if (XiaojsConfig.DEBUG) {
                    Logger.e("open camera failed:" + extra);
                }
                break;
        }

    }


    @Override
    public void onAudioSourceAvailable(ByteBuffer byteBuffer, int i, long l, boolean b) {

    }

    @Override
    public void notifyStreamStatusChanged(StreamingProfile.StreamStatus streamStatus) {

    }

    @Override
    public boolean onRecordAudioFailedHandled(int i) {
        if (XiaojsConfig.DEBUG) {
            Logger.d("onRecordAudioFailedHandled");
        }

        return false;
    }

    @Override
    public boolean onRestartStreamingHandled(int i) {

        if (XiaojsConfig.DEBUG) {
            Logger.i("onRestartStreamingHandled");
        }
        startStreaming();
        return true;
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {

        Camera.Size size = null;
        if (list != null) {
            StreamingProfile.VideoEncodingSize encodingSize =
                    mProfile.getVideoEncodingSize(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);
            for (Camera.Size s : list) {
                if (s.width >= encodingSize.width && s.height >= encodingSize.height) {
                        size = s;
                        if (XiaojsConfig.DEBUG) {
                            Logger.d("selected size :" + size.width + "x" + size.height);
                        }
                    break;
                }
            }
        }
        return size;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (XiaojsConfig.DEBUG) {
            Logger.i("onSingleTapUp X:" + e.getX() + ",Y:" + e.getY());
        }

        if (isReady) {
            //setFocusAreaIndicator();
            mediaStreamingManager.doSingleTapUp((int) e.getX(), (int) e.getY());
            return true;
        }
        return false;
    }

    @Override
    public boolean onZoomValueChanged(float factor) {
        if (isReady && mediaStreamingManager.isZoomSupported()) {
            mCurrentZoom = (int) (mMaxZoom * factor);
            mCurrentZoom = Math.min(mCurrentZoom, mMaxZoom);
            mCurrentZoom = Math.max(0, mCurrentZoom);
            if (XiaojsConfig.DEBUG) {
                Logger.d("zoom ongoing, scale: " + mCurrentZoom + ",factor:" + factor + ",maxZoom:" + mMaxZoom);
            }

            mediaStreamingManager.setZoomValue(mCurrentZoom);
        }
        return false;
    }


    @Override
    public boolean onPreviewFrame(byte[] bytes,
                                  int width, int height, int rotation, int fmt, long tsInNanoTime) {
        return true;
    }

    @Override
    public void onSurfaceCreated() {

        mFBO.initialize(getActivity());
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        mFBO.updateSurfaceSize(width, height);
    }

    @Override
    public void onSurfaceDestroyed() {
        mFBO.release();
    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight, float[] transformMatrix) {
        int newTexId = mFBO.drawFrame(texId, texWidth, texHeight);
        return newTexId;
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void initEncodingProfile() {

        mProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_HIGH3);
        mProfile.setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480);
        mProfile.setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.LAND);
        mProfile.setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY);
        mProfile.setBitrateAdjustMode(StreamingProfile.BitrateAdjustMode.Auto);
        mProfile.setVideoAdaptiveBitrateRange(150 * 1024,
                800 * 1024);

        mProfile.setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2);

        mProfile.setDnsManager(getMyDnsManager())
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f,
                        0.8f, 3.0f, 20 * 1000));
    }

    protected void initStreamingManager(CameraPreviewFrameView cameraPreview) {

        mediaStreamingManager = new MediaStreamingManager(getContext(),
                cameraPreview, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);

        MicrophoneStreamingSetting microphoneStreamingSetting = new MicrophoneStreamingSetting();
        microphoneStreamingSetting.setBluetoothSCOEnabled(false);//麦克风蓝牙支持

        mediaStreamingManager.prepare(cameraStreamingSetting,
                microphoneStreamingSetting, buildWatermarkSetting(), mProfile);
//        if (cameraConfig.mIsCustomFaceBeauty) {
//            mediaStreamingManager.setSurfaceTextureCallback(this);
//        }
        cameraPreview.setListener(this);
        mediaStreamingManager.setStreamingSessionListener(this);
        mediaStreamingManager.setStreamStatusCallback(this);
        mediaStreamingManager.setAudioSourceCallback(this);
        mediaStreamingManager.setStreamingStateListener(this);
    }

    private void initCameraStreamingSetting() {

        cameraStreamingSetting = new CameraStreamingSetting();
        cameraStreamingSetting.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)
                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_VIDEO)
                .setContinuousFocusModeEnabled(true)
                .setResetTouchFocusDelayInMs(3000)
                .setBuiltInFaceBeautyEnabled(true)
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f))
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);

    }

    private WatermarkSetting buildWatermarkSetting() {

        WatermarkSetting watermarkSetting = new WatermarkSetting(getActivity());
        watermarkSetting.setResourceId(R.drawable.logo_splash);
        watermarkSetting.setAlpha(100);
        watermarkSetting.setSize(WatermarkSetting.WATERMARK_SIZE.SMALL);
        watermarkSetting.setLocation(WatermarkSetting.WATERMARK_LOCATION.SOUTH_WEST);
        return watermarkSetting;
    }

    private static DnsManager getMyDnsManager() {
        IResolver r0 = null;
        IResolver r1 = new DnspodFree();
        IResolver r2 = AndroidDnsServer.defaultResolver();
        try {
            r0 = new Resolver(InetAddress.getByName("119.29.29.29"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DnsManager(NetworkInfo.normal, new IResolver[]{r0, r1, r2});
    }



}
