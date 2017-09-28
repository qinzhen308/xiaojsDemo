package cn.xiaojs.xma.ui.classroom2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.StreamingState;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.classroom2.base.AVFragment;
import cn.xiaojs.xma.ui.classroom2.widget.CameraPreviewFrameView;


/**
 * Created by maxiaobao on 2017/9/18.
 */

public class LivingFragment extends AVFragment{

    @BindView(R.id.camera_preview)
    CameraPreviewFrameView cameraPreviewFrameView;

    @BindView(R.id.top_roominfo)
    TextView topRoominfoView;
    @BindView(R.id.top_photo)
    ImageView topPhotoView;
    @BindView(R.id.top_switchcamera)
    ImageView topSwitchcameraView;
    @BindView(R.id.top_start_or_stop_living)
    TextView topStartstopView;

    private boolean streaming;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classroom2_living, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.top_back, R.id.top_switchcamera, R.id.bottom_more, R.id.bottom_chat, R.id.top_start_or_stop_living})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.top_back:
            case R.id.top_start_or_stop_living:
                back();
                break;
            case R.id.top_switchcamera:
                switchCamera();
                break;
            case R.id.bottom_more:
                break;
            case R.id.bottom_chat:
                break;
        }
    }


    @Override
    protected CameraPreviewFrameView createCameraPreview() {
        return cameraPreviewFrameView;
    }


    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        super.onStateChanged(streamingState, extra);
        switch (streamingState) {
            case STREAMING:
                if (XiaojsConfig.DEBUG) {
                    Logger.d("STREAMING");
                }

                if (!streaming) {
                    sendStartStreaming();
                    streaming = true;
                }
                break;
        }
    }


    @Override
    public void back() {
        stopStreaming();
        super.back();
        enterIdle();

        sendStopStreaming();
    }
}
