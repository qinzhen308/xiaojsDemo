package cn.xiaojs.xma.ui.classroom2.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.StreamingState;

import cn.xiaojs.xma.XiaojsConfig;

import cn.xiaojs.xma.ui.classroom2.live.StreamingEngine;
import cn.xiaojs.xma.ui.classroom2.live.WhiteboardStreamEngine;
import cn.xiaojs.xma.ui.classroom2.widget.CameraPreviewFrameView;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public abstract class AVFragment extends MovieFragment implements
        WhiteboardStreamEngine.WBStreamingStateListener,
        StreamingEngine.AVStreamingStateListener{


    private StreamingEngine streamingEngine;
    private WhiteboardStreamEngine whiteboardStreamEngine;

    private boolean streaming;


    protected abstract CameraPreviewFrameView createCameraPreview();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        controlClickView.setVisibility(View.GONE);


        whiteboardStreamEngine = new WhiteboardStreamEngine();
        whiteboardStreamEngine.setWBStreamingStateListener(this);
        whiteboardStreamEngine.preparePublish(getContext(), classroomEngine.getPublishUrl());


        streamingEngine = new StreamingEngine(getContext(),createCameraPreview());
        streamingEngine.setStreamingUrl(classroomEngine.getPublishUrl());
        streamingEngine.setStateListener(this);
        streamingEngine.preparePublish(createCameraPreview());

    }

    @Override
    public void onResume() {
        super.onResume();
        streamingEngine.resumeAV();
        whiteboardStreamEngine.resume();
    }

    @Override
    public void onPause() {
        super.onPause();

        streamingEngine.pauseAV();
        whiteboardStreamEngine.pause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        streamingEngine.destoryAV();
        whiteboardStreamEngine.destory();
    }

    protected void stopStreaming() {
        streamingEngine.stopStreamingAV();
        whiteboardStreamEngine.stopStreaming();
    }

    protected void switchCamera() {
        streamingEngine.switchCamera();
    }

    protected void receivedWhiteboardData(Bitmap bitmap) {
        whiteboardStreamEngine.inputWhiteboardData(bitmap);
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
        int vis =  super.onSwitchStreamingClick(view);

        if (vis == View.VISIBLE) {

            streaming = false;
            streamingEngine.stopStreamingAV();

            whiteboardStreamEngine.stopStreaming();
            whiteboardStreamEngine.resume();
            whiteboardStreamEngine.startStreaming();
        }else {
            streaming = false;
            whiteboardStreamEngine.stopStreaming();

            streamingEngine.stopStreamingAV();
            streamingEngine.resumeAV();
            streamingEngine.startStreamingAV();
        }

        return vis;
    }

    @Override
    public void onWBStateChanged(StreamingState streamingState, Object extra) {
        switch (streamingState) {
            case STREAMING:
                if (XiaojsConfig.DEBUG) {
                    Logger.d("STREAMING xxxxxxxxx");
                }

                if (!streaming) {
                    sendStartStreaming();
                    streaming = true;
                }
                break;
        }
    }

    @Override
    public void onAVStateChanged(StreamingState streamingState, Object extra) {
        switch (streamingState) {
            case STREAMING:
                if (XiaojsConfig.DEBUG) {
                    Logger.d("STREAMING xxxxxxx1");
                }

                if (!streaming) {
                    sendStartStreaming();
                    streaming = true;
                }
                break;
        }
    }


}
