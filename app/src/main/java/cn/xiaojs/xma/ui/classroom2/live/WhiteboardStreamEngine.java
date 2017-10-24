package cn.xiaojs.xma.ui.classroom2.live;

import android.content.Context;
import android.graphics.Bitmap;
import android.hardware.Camera;

import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.AVCodecType;
import com.qiniu.pili.droid.streaming.StreamStatusCallback;
import com.qiniu.pili.droid.streaming.StreamingManager;
import com.qiniu.pili.droid.streaming.StreamingProfile;
import com.qiniu.pili.droid.streaming.StreamingSessionListener;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;
import com.qiniu.pili.droid.streaming.av.common.PLFourCC;

import java.net.URISyntaxException;
import java.util.List;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.classroom2.live.capture.ExtAudioCapture;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by maxiaobao on 2017/10/23.
 */

public class WhiteboardStreamEngine implements StreamingSessionListener,
        StreamStatusCallback,
        StreamingStateChangedListener {

    private StreamingManager streamingManager;
    protected StreamingProfile streamProfile;

    private ExtAudioCapture extAudioCapture;

    private WBStreamingStateListener streamingStateListener;

    private boolean canStreaming = true;

    // private WBDataObservable wbDataObservable;


    public interface WBStreamingStateListener {
        void onWBStateChanged(StreamingState streamingState, Object extra);
    }


    public WhiteboardStreamEngine() {
        initProfile();

//        wbDataObservable = WBDataObservable.createObservable();
//        wbDataObservable.observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<WBDataObservable.WBData>() {
//                    @Override
//                    public void accept(WBDataObservable.WBData wbData) throws Exception {
//
//                        if (wbData == null)
//                            return;
//
//                        if (XiaojsConfig.DEBUG) {
//                            Logger.d("inputVideoFrame......................");
//                        }
//
//                        streamingManager.inputVideoFrame(wbData.data,
//                                wbData.inputWidth,
//                                wbData.inputHeight,
//                                0, false, PLFourCC.FOURCC_NV21, System.nanoTime());
//                    }
//                });

    }

    public void preparePublish(Context context, String url) {

        setStreamingUrl(url);

        extAudioCapture = new ExtAudioCapture();

        this.streamingManager = new StreamingManager(context,
                AVCodecType.HW_VIDEO_YUV_AS_INPUT_WITH_HW_AUDIO_CODEC);

        streamingManager.prepare(streamProfile);
        streamingManager.setStreamingSessionListener(this);
        streamingManager.setStreamStatusCallback(this);
        streamingManager.setStreamingStateListener(this);
    }

    private void initProfile() {
        streamProfile = new StreamingProfile();
        streamProfile.setVideoQuality(StreamingProfile.VIDEO_QUALITY_LOW1);
        streamProfile.setPreferredVideoEncodingSize(848, 480);
        //streamProfile.setEncodingSizeLevel(StreamingProfile.VIDEO_ENCODING_HEIGHT_480);
        streamProfile.setEncodingOrientation(StreamingProfile.ENCODING_ORIENTATION.LAND);
        streamProfile.setEncoderRCMode(StreamingProfile.EncoderRCModes.BITRATE_PRIORITY);
        streamProfile.setBitrateAdjustMode(StreamingProfile.BitrateAdjustMode.Auto);
        streamProfile.setVideoAdaptiveBitrateRange(150 * 1024,
                800 * 1024);

        streamProfile.setAudioQuality(StreamingProfile.AUDIO_QUALITY_MEDIUM2);
        streamProfile.setDnsManager(StreamingUtil.getMyDnsManager())
                .setStreamStatusConfig(new StreamingProfile.StreamStatusConfig(3))
                .setSendingBufferProfile(new StreamingProfile.SendingBufferProfile(0.2f,
                        0.8f, 3.0f, 20 * 1000));

    }

    private void setStreamingUrl(String url) {
        try {
            streamProfile.setPublishUrl(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void setWBStreamingStateListener(WBStreamingStateListener listener) {
        this.streamingStateListener = listener;
    }

    private void startStreamingInternal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                streamingManager.startStreaming();
            }
        }).start();


    }

    public void startStreaming() {
        canStreaming = true;
        startStreamingInternal();
    }

    public void stopStreaming() {
        canStreaming = false;
        streamingManager.stopStreaming();
    }

    public void resume() {
        extAudioCapture.startCapture();
        extAudioCapture.setOnAudioFrameCapturedListener(mOnAudioFrameCapturedListener);
        streamingManager.resume();
    }

    public void pause() {
        extAudioCapture.stopCapture();
        streamingManager.pause();
    }

    public void destory() {
        streamingManager.destroy();

//        WBDataObservable.WbDataWachter wbDataWachter = wbDataObservable.getObserver();
//        if (wbDataWachter !=null) {
//            wbDataWachter.dispose();
//        }

    }

    private ExtAudioCapture.OnAudioFrameCapturedListener mOnAudioFrameCapturedListener
            = new ExtAudioCapture.OnAudioFrameCapturedListener() {
        @Override
        public void onAudioFrameCaptured(byte[] audioData) {
            long timestamp = System.nanoTime();
            streamingManager.inputAudioFrame(audioData, timestamp, false);
        }
    };


    public void inputWhiteboardData(Bitmap whiteboardBitmap) {

        if (whiteboardBitmap == null)
            return;

        int inputWidth = whiteboardBitmap.getWidth();
        int inputHeight = whiteboardBitmap.getHeight();

        //wbDataObservable.onDataReceived(whiteboardBitmap, inputWidth, inputHeight);

        publishStream(whiteboardBitmap, inputWidth, inputHeight);
    }


    private void publishStream(final Bitmap targetBitmap, final int inputWidth, final int inputHeight) {

        Observable.create(new ObservableOnSubscribe<byte[]>() {

            @Override
            public void subscribe(@NonNull ObservableEmitter<byte[]> e) throws Exception {

                byte[] data = getNV21(inputWidth, inputHeight, targetBitmap);
                e.onNext(data);

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<byte[]>() {
                    @Override
                    public void accept(byte[] dataBytes) throws Exception {

                        if (XiaojsConfig.DEBUG) {
                            Logger.d("inputVideoFrame......................");
                        }

                        streamingManager.inputVideoFrame(dataBytes, inputWidth, inputHeight,
                                0, false, PLFourCC.FOURCC_NV21, System.nanoTime());
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

    ///////////////

    @Override
    public boolean onRecordAudioFailedHandled(int i) {
        return false;
    }

    @Override
    public boolean onRestartStreamingHandled(int i) {
        return false;
    }

    @Override
    public Camera.Size onPreviewSizeSelected(List<Camera.Size> list) {
        return null;
    }


    ///// StreamStatusCallback


    @Override
    public void notifyStreamStatusChanged(StreamingProfile.StreamStatus streamStatus) {

    }

    ///////StreamingStateChangedListener

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

                if (canStreaming) {
                    startStreamingInternal();
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


        if (streamingStateListener != null) {
            streamingStateListener.onWBStateChanged(streamingState, extra);
        }
    }


}
