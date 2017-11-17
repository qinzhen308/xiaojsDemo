package cn.xiaojs.xma.ui.classroom2.live;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.support.annotation.DrawableRes;
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
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
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
import com.qiniu.pili.droid.streaming.av.common.PLFourCC;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.preference.ClassroomPref;
import cn.xiaojs.xma.ui.classroom2.live.gles.FBO;
import cn.xiaojs.xma.ui.classroom2.widget.CameraPreviewFrameView;
import cn.xiaojs.xma.util.BitmapUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/10/19.
 */

public class StreamingEngine implements CameraPreviewFrameView.Listener,
        StreamingSessionListener,
        StreamingStateChangedListener,
        StreamStatusCallback,
        AudioSourceCallback,
        SurfaceTextureCallback,
        StreamingPreviewCallback {


    public interface AVStreamingStateListener {
        void onAVStateChanged(StreamingState streamingState, Object extra);
    }


    private Context context;
    protected MediaStreamingManager mediaStreamingManager;
    protected StreamingProfile profile;

    protected boolean isReady;
    private int mCurrentZoom = 0;
    private int mMaxZoom = 0;
    private FBO fBO = new FBO();
    private AVStreamingStateListener stateListener;
    private boolean canStreaming = true;

    public StreamingEngine(Context context, CameraPreviewFrameView cameraStreamView) {
        this.context = context;
        initProfile();
        mediaStreamingManager = new MediaStreamingManager(context,
                cameraStreamView, AVCodecType.SW_VIDEO_WITH_SW_AUDIO_CODEC);
    }


    public void preparePublish(CameraPreviewFrameView cameraStreamView) {
        MicrophoneStreamingSetting microphoneStreamingSetting = new MicrophoneStreamingSetting();
        microphoneStreamingSetting.setBluetoothSCOEnabled(false);//麦克风蓝牙支持

        mediaStreamingManager.prepare(cameraStreamingSetting(),
                microphoneStreamingSetting, buildWatermarkSetting(), profile);

//        if (cameraConfig.mIsCustomFaceBeauty) {
        //            mediaStreamingManager.setSurfaceTextureCallback(this);
//        }

        cameraStreamView.setListener(this);
        mediaStreamingManager.setStreamingSessionListener(this);
        mediaStreamingManager.setStreamStatusCallback(this);
        mediaStreamingManager.setAudioSourceCallback(this);
        mediaStreamingManager.setStreamingStateListener(this);
    }

    public void setStreamingUrl(String url) {
        try {
            profile.setPublishUrl(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            if (XiaojsConfig.DEBUG) {
                Logger.e("setPublishUrl exception: %s", e.getMessage());
            }
        }
    }

    public void updateStreamingProfile() {
        mediaStreamingManager.setStreamingProfile(profile);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // streaming profile
    //

    private void initProfile() {
        profile = new StreamingProfile();
        profile.setVideoQuality(ClassroomPref.getLivingLevel(context));
        profile.setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480);
        profile.setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.LAND);
        profile.setEncoderRCMode(StreamingProfile.EncoderRCModes.QUALITY_PRIORITY);
        profile.setBitrateAdjustMode(StreamingProfile.BitrateAdjustMode.Auto);
        profile.setVideoAdaptiveBitrateRange(150 * 1024,
                800 * 1024);

        profile.setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2);

        profile.setDnsManager(StreamingUtil.getMyDnsManager())
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f,
                        0.8f, 3.0f, 20 * 1000));


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 音视频推流
    //


    public void captureFrame(FrameCapturedCallback callback) {
        mediaStreamingManager.captureFrame(0, 0, callback);
    }

    public void togglePictureStreaming() {
        mediaStreamingManager.togglePictureStreaming();
    }

    public void setStateListener(AVStreamingStateListener listener) {
        this.stateListener = listener;
    }

    public void startStreamingAV() {
        canStreaming = true;
        startStreamingAVInternal();

    }


    public void startStreamingAVInternal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mediaStreamingManager.startStreaming();
            }
        }).start();

    }

    public void stopStreamingAV() {
        canStreaming = false;
        mediaStreamingManager.stopStreaming();
    }

    public void switchCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mediaStreamingManager.switchCamera();
            }
        }).start();
    }

    public void resumeAV() {
        if (mediaStreamingManager != null) {
            mediaStreamingManager.resume();
        }
    }

    public void pauseAV() {

        isReady = false;

        if (mediaStreamingManager != null) {
            mediaStreamingManager.pause();
        }
    }

    public void destoryAV() {
        if (mediaStreamingManager != null) {
            mediaStreamingManager.destroy();
            mediaStreamingManager = null;
        }
    }

    private CameraStreamingSetting cameraStreamingSetting() {

        CameraStreamingSetting cameraSetting = new CameraStreamingSetting();
        cameraSetting.setCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .setCameraPrvSizeLevel(CameraStreamingSetting.PREVIEW_SIZE_LEVEL.SMALL)
                .setCameraPrvSizeRatio(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9)
                .setFocusMode(CameraStreamingSetting.FOCUS_MODE_CONTINUOUS_VIDEO)
                .setContinuousFocusModeEnabled(true)
                .setResetTouchFocusDelayInMs(3000)
                .setBuiltInFaceBeautyEnabled(true)
                .setFaceBeautySetting(new CameraStreamingSetting.FaceBeautySetting(1.0f, 1.0f, 0.8f))
                .setVideoFilter(CameraStreamingSetting.VIDEO_FILTER_TYPE.VIDEO_FILTER_BEAUTY);

        return cameraSetting;

    }

    private WatermarkSetting buildWatermarkSetting() {

        WatermarkSetting watermarkSetting = new WatermarkSetting(context);
        //watermarkSetting.setResourcePath("http://static.xiaojs.cn/shuiyin.png");
        watermarkSetting.setResourceId(R.drawable.ic_shuiyin);
        watermarkSetting.setAlpha(200);
        watermarkSetting.setSize(WatermarkSetting.WATERMARK_SIZE.SMALL);
        watermarkSetting.setLocation(WatermarkSetting.WATERMARK_LOCATION.SOUTH_WEST);
        return watermarkSetting;
    }

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
                Logger.d("zoom ongoing, scale: " + mCurrentZoom +
                        ",factor:" + factor + ",maxZoom:" + mMaxZoom);
            }

            mediaStreamingManager.setZoomValue(mCurrentZoom);
        }
        return false;
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
        startStreamingAV();
        return true;
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        Camera.Size size = null;
        if (list != null) {
            StreamingProfile.VideoEncodingSize encodingSize =
                    profile.getVideoEncodingSize(CameraStreamingSetting.PREVIEW_SIZE_RATIO.RATIO_16_9);
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

                if (canStreaming) {
                    startStreamingAVInternal();
                } else {
                    stopStreamingAV();
                }

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

        if (stateListener != null) {
            stateListener.onAVStateChanged(streamingState, extra);
        }
    }

    @Override
    public void notifyStreamStatusChanged(StreamingProfile.StreamStatus streamStatus) {

    }

    @Override
    public void onAudioSourceAvailable(ByteBuffer byteBuffer, int i, long l, boolean b) {

    }

    @Override
    public void onSurfaceCreated() {
        fBO.initialize(context);
    }

    @Override
    public void onSurfaceChanged(int width, int height) {
        fBO.updateSurfaceSize(width, height);
    }

    @Override
    public void onSurfaceDestroyed() {
        fBO.release();
    }

    @Override
    public int onDrawFrame(int texId, int texWidth, int texHeight, float[] transformMatrix) {
        int newTexId = fBO.drawFrame(texId, texWidth, texHeight);
        return newTexId;
    }

    @Override
    public boolean onPreviewFrame(byte[] bytes, int i, int i1, int i2, int i3, long l) {
        return true;
    }


    //////////////////////////////////


    public void setPictureStreamingResourceId(@DrawableRes int res) {
        mediaStreamingManager.setPictureStreamingResourceId(res);
    }

    public void inputWhiteboardData(Bitmap whiteboardBitmap) {

        if (whiteboardBitmap == null)
            return;

        //int inputWidth = whiteboardBitmap.getWidth();
        //int inputHeight = whiteboardBitmap.getHeight();

        //wbDataObservable.onDataReceived(whiteboardBitmap, inputWidth, inputHeight);

        publishStream(whiteboardBitmap);
    }


    private void publishStream(final Bitmap targetBitmap) {

        Observable.create(new ObservableOnSubscribe<String>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {

                //byte[] data = getNV21(inputWidth, inputHeight, targetBitmap);

                String path = BitmapUtils.savePreviewToFile(targetBitmap);

                e.onNext(path);

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String path) throws Exception {

                        if (XiaojsConfig.DEBUG) {
                            Logger.d("inputVideoFrame......................");
                        }

                        //Drawable d;

                        mediaStreamingManager.setPictureStreamingFilePath(path);

//                        mediaStreamingManager.inputVideoFrame(dataBytes, inputWidth, inputHeight,
//                                0, false, PLFourCC.FOURCC_NV21, System.nanoTime());
                    }
                });


    }


    // untested function
    byte[] getNV21(int inputWidth, int inputHeight, Bitmap scaled) {

        int[] argb = new int[inputWidth * inputHeight];

        scaled.getPixels(argb, 0, inputWidth, 0, 0, inputWidth, inputHeight);

        byte[] yuv = new byte[inputWidth * inputHeight * 3 / 2];
        encodeYUV420SP(yuv, argb, inputWidth, inputHeight);

        scaled.recycle();

        return yuv;
    }

    void encodeYUV420SP(byte[] yuv420sp, int[] argb, int width, int height) {
        final int frameSize = width * height;

        int yIndex = 0;
        int uvIndex = frameSize;

        int a, R, G, B, Y, U, V;
        int index = 0;
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {

                a = (argb[index] & 0xff000000) >> 24; // a is not used obviously
                R = (argb[index] & 0xff0000) >> 16;
                G = (argb[index] & 0xff00) >> 8;
                B = (argb[index] & 0xff) >> 0;

                // well known RGB to YUV algorithm
                Y = ((66 * R + 129 * G + 25 * B + 128) >> 8) + 16;
                U = ((-38 * R - 74 * G + 112 * B + 128) >> 8) + 128;
                V = ((112 * R - 94 * G - 18 * B + 128) >> 8) + 128;

                // NV21 has a plane of Y and interleaved planes of VU each sampled by a factor of 2
                //    meaning for every 4 Y pixels there are 1 V and 1 U.  Note the sampling is every other
                //    pixel AND every other scanline.
                yuv420sp[yIndex++] = (byte) ((Y < 0) ? 0 : ((Y > 255) ? 255 : Y));
                if (j % 2 == 0 && index % 2 == 0) {
                    yuv420sp[uvIndex++] = (byte) ((V < 0) ? 0 : ((V > 255) ? 255 : V));
                    yuv420sp[uvIndex++] = (byte) ((U < 0) ? 0 : ((U > 255) ? 255 : U));
                }

                index++;
            }
        }
    }

}
