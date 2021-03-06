package cn.xiaojs.xma.ui.classroom2.base;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;

import cn.xiaojs.xma.ui.classroom.page.BoardCollaborateFragment;
import cn.xiaojs.xma.ui.classroom2.live.StreamingEngine;
import cn.xiaojs.xma.ui.classroom2.widget.CameraPreviewFrameView;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public abstract class AVFragment extends MovieFragment
        implements StreamingEngine.AVStreamingStateListener {


    private StreamingEngine streamingEngine;

    private boolean streaming;

    protected abstract CameraPreviewFrameView createCameraPreview();


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        controlClickView.setVisibility(View.GONE);

        streamingEngine = new StreamingEngine(getContext(), createCameraPreview());
        streamingEngine.setStreamingUrl(classroomEngine.getPublishUrl());
        streamingEngine.setStateListener(this);
        streamingEngine.preparePublish(createCameraPreview());

        Bitmap bitmap = whiteboardFragment.preview();
        if (bitmap != null) {
            streamingEngine.inputWhiteboardData(bitmap);
        } else {

            if (XiaojsConfig.DEBUG) {
                Logger.d("---create stream bitmap ----------");
            }

            Bitmap bmp=Bitmap.createBitmap(16, 9, Bitmap.Config.RGB_565);
            Canvas canvas=new Canvas(bmp);
            canvas.drawColor(Color.WHITE);
            streamingEngine.inputWhiteboardData(bmp);
        }

        whiteboardFragment.setLastBoardLoadListener(new BoardCollaborateFragment.OnLastBoardLoadListener() {
            @Override
            public void onSuccess(Bitmap preview) {
                if (XiaojsConfig.DEBUG) {
                    Logger.d("---Bitmap preview---------onSuccess----------");
                }
                streamingEngine.inputWhiteboardData(preview);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        streamingEngine.resumeAV();

    }

    @Override
    public void onPause() {
        super.onPause();
        streamingEngine.pauseAV();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        streamingEngine.destoryAV();
    }

    public void captureFrame(FrameCapturedCallback callback) {
        streamingEngine.captureFrame(callback);
    }


    protected void stopStreaming() {
        streamingEngine.stopStreamingAV();
    }

    protected void switchCamera() {
        streamingEngine.switchCamera();
    }

    protected void receivedWhiteboardData(Bitmap bitmap) {
        streamingEngine.inputWhiteboardData(bitmap);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRotate(int orientation) {
        onRotateToInitBoard(orientation);
    }

    @Override
    public void closeMovie() {

    }

    @Override
    public int onSwitchStreamingClick(View view) {
        int vis = super.onSwitchStreamingClick(view);
        streamingEngine.togglePictureStreaming();

        return vis;
    }

    protected boolean makeCameraStreamOnly() {
        boolean changed = hiddeWhiteboardContainer();
        if (changed) {
            streamingEngine.togglePictureStreaming();
        }

        return changed;
    }

    @Override
    public void onAVStateChanged(StreamingState streamingState, Object extra) {
        switch (streamingState) {
            case STREAMING:
                if (XiaojsConfig.DEBUG) {
                    Logger.d("STREAMING xxxxxxx1");
                }

                if (!streaming) {
                    streamingEngine.togglePictureStreaming();
                    sendStartStreaming();
                    streaming = true;
                }
                break;
        }
    }

    @Override
    public void onPushPreview(Bitmap bitmap) {
        super.onPushPreview(bitmap);

        if (XiaojsConfig.DEBUG) {
            Logger.d("received whiteboard data(%d, %d): %s",
                    bitmap.getWidth(), bitmap.getHeight(), bitmap);

            Logger.d("publish_url: %s", classroomEngine.getPublishUrl());
        }

        receivedWhiteboardData(bitmap);
    }

    //需要推白板就返回true
    @Override
    public boolean pushPreviewEnable() {
        return true;
    }

}
