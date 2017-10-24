package cn.xiaojs.xma.ui.classroom2.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import cn.xiaojs.xma.ui.classroom2.live.StreamingEngine;
import cn.xiaojs.xma.ui.classroom2.widget.CameraPreviewFrameView;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public abstract class AVFragment extends MovieFragment implements StreamingEngine.AVStreamingStateListener{



    private StreamingEngine streamingEngine;


    protected abstract CameraPreviewFrameView createCameraPreview();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        streamingEngine = new StreamingEngine(getContext(),createCameraPreview());
        streamingEngine.setStreamingUrl(classroomEngine.getPublishUrl());
        streamingEngine.setStateListener(this);
        streamingEngine.configAV(createCameraPreview());

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

    protected void stopStreaming() {
        streamingEngine.stopStreamingAV();
    }

    protected void switchCamera() {
        streamingEngine.switchCamera();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onRotate(int orientation) {
        onRotateToInitBoard(orientation);
    }

    @Override
    public void closeMovie() {

    }





}
