package cn.xiaojs.xma.ui.classroom2;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.model.socket.room.StreamStopReceive;
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class PlayFragment extends MovieFragment {

    @BindView(R.id.video_view)
    PLVideoTextureView videoView;
    @BindView(R.id.loading_View)
    LinearLayout loadingView;

    private EventListener.ELPlaylive playLiveObserver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classroom2_play, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        int changeRequest = getActivity().getRequestedOrientation();
        controlHandleOnRotate(changeRequest);

        configPortLandOperaButton();
        configVideoView(videoView);
        videoView.setVideoPath(classroomEngine.getPlayUrl());
        videoView.start();

        playLiveObserver = classroomEngine.observerPlaylive(receivedConsumer);

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();

        if (playLiveObserver != null) {
            playLiveObserver.dispose();
        }
    }

    @Override
    public void onRotate(int orientation) {
        controlHandleOnRotate(orientation);
    }

    @Override
    public void closeMovie() {

    }

    @Override
    public void onClosed() {
        exitSlidePanel();
    }

    @Override
    public void onOpened() {

    }

    @Override
    public void onTopbackClick(View view, boolean land) {
        back();
    }

    @Override
    public void onStartLiveClick(View view) {
        requestPublish();
    }

    @Override
    public void onStartOrStopLiveClick(View view) {
        requestPublish();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 开始直播
    //

    private void requestPublish() {
        if (classroomEngine.canForceIndividual()) {
            requestLive();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 操作面板
    //
    public void configPortLandOperaButton() {

        int streamType = classroomEngine.getCtlSession().streamType;

        if (classroomEngine.getLiveState().equals(Live.LiveSessionState.LIVE)
                || streamType == Live.StreamType.LIVE) {

            pTopLiveView.setVisibility(View.GONE);
            startOrStopLiveView.setVisibility(View.GONE);

        } else if (streamType == Live.StreamType.INDIVIDUAL) {

            if (classroomEngine.canForceIndividual()) {//如果是老师或者管理员或者班主任可以强行抢占直播
                pTopLiveView.setText("开始直播");
                pTopLiveView.setVisibility(View.VISIBLE);

                startOrStopLiveView.setText("开始直播");
                startOrStopLiveView.setVisibility(View.VISIBLE);

            } else {
                pTopLiveView.setVisibility(View.GONE);
                startOrStopLiveView.setVisibility(View.GONE);
            }

        }

    }


    private void updateStartOrPausedLiveButtonWhenSyncStateChanged(SyncStateReceive syncStateReceive) {

        int streamType = classroomEngine.getCtlSession().streamType;

        switch (streamType) {
            case Live.StreamType.INDIVIDUAL:
                if (classroomEngine.canForceIndividual()) {

                    String liveState = classroomEngine.getLiveState();

                    if (liveState.equals(Live.LiveSessionState.PENDING_FOR_JOIN)
                            || liveState.equals(Live.LiveSessionState.PENDING_FOR_LIVE)) {
                        pTopLiveView.setText("开始上课");
                        pTopLiveView.setVisibility(View.VISIBLE);

                        startOrStopLiveView.setText("开始上课");
                        startOrStopLiveView.setVisibility(View.VISIBLE);
                    } else {
                        pTopLiveView.setText("开始直播");
                        pTopLiveView.setVisibility(View.VISIBLE);

                        startOrStopLiveView.setText("开始直播");
                        startOrStopLiveView.setVisibility(View.VISIBLE);
                    }

                }
                break;
            case Live.StreamType.LIVE:
                break;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 事件响应
    //

    private void handleStreamStop(StreamStopReceive streamStopReceive) {

        String myId = AccountDataManager.getAccountID(getContext());

        if (!TextUtils.isEmpty(streamStopReceive.deprivedBy)
                && myId.equals(streamStopReceive.deprivedBy)) {
            return;//老师自己抢占别人的直播，会收到流停止事件，此时不需处理；
        }

        enterIdle();
    }

    private void handleSyncState(SyncStateReceive syncStateReceive) {

        updateStartOrPausedLiveButtonWhenSyncStateChanged(syncStateReceive);

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 事件监听
    //

    private Consumer<EventReceived> receivedConsumer = new Consumer<EventReceived>() {
        @Override
        public void accept(EventReceived eventReceived) throws Exception {

            if (XiaojsConfig.DEBUG) {
                Logger.d("receivedConsumer .....");
            }

            switch (eventReceived.eventType) {
                case Su.EventType.SYNC_CLASS_STATE:
                    break;
                case Su.EventType.SYNC_STATE:
                    SyncStateReceive syncStateReceive = (SyncStateReceive) eventReceived.t;
                    handleSyncState(syncStateReceive);
                    break;
                case Su.EventType.STREAM_RECLAIMED:
                    enterIdle();
                    break;
                case Su.EventType.STOP_STREAM_BY_EXPIRATION:
                    enterIdle();
                    break;
                case Su.EventType.STREAMING_STOPPED:
                    handleStreamStop((StreamStopReceive) eventReceived.t);
                    break;
            }
        }
    };


}
