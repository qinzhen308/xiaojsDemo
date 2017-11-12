package cn.xiaojs.xma.ui.classroom2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.StreamingState;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.OpenMediaReceive;
import cn.xiaojs.xma.model.socket.room.StreamStopReceive;
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatAdapter;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.ui.classroom2.live.PlayLiveView;
import cn.xiaojs.xma.ui.classroom2.live.StreamingEngine;
import cn.xiaojs.xma.ui.classroom2.live.VideoStreamView;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;
import cn.xiaojs.xma.ui.classroom2.util.VibratorUtil;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.UIUtils;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class PlayFragment extends MovieFragment
        implements ChatAdapter.FetchMoreListener, VideoStreamView.ControlListener {

    @BindView(R.id.video_view)
    PlayLiveView videoView;
    @BindView(R.id.video_stream)
    VideoStreamView streamView;

    private EventListener.ELPlaylive playLiveObserver;

    private StreamingEngine streamingEngine;

    private boolean streaming;

    private int memCount;
    private long liveTime;
    private long reserveliveTime;

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

        streamView.setCloseEnabled(true);
        streamView.setControlListener(this);

        initControlPanel();

        initTalkData(this);

        configPortLandOperaButton();

        videoView.startPlay(classroomEngine.getPlayUrl());

        playLiveObserver = classroomEngine.observerPlaylive(receivedConsumer);
        classroomEngine.setLiveTimerObserver(playLiveObserver);

        config4Preview();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (streamingEngine != null) {
            streamingEngine.pauseAV();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.resume();
        if (streamingEngine != null) {
            streamingEngine.resumeAV();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.destroy();

        if (streamingEngine != null) {
            streamingEngine.stopStreamingAV();
            streamingEngine.destoryAV();
            streamingEngine = null;
        }

        if (playLiveObserver != null) {
            playLiveObserver.dispose();
        }
    }


    public void onFetchMoreRequested() {
        loadTalk();
    }

    @Override
    public void onRotate(int orientation) {
        controlHandleOnRotate(orientation);
        onRotateToInitBoard(orientation);
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

    @Override
    protected void handleArgeedO2o(Attendee attendee) {
        super.handleArgeedO2o(attendee);
        setupStream();
    }

    @Override
    public void onPlayClosed() {
        closeStreaming();
        sendCloseMedia(o2oAttendee.accountId);
        sendStopStreaming();
    }


    @Override
    public void onUpdateMembersCount(int count) {
        super.onUpdateMembersCount(count);

        memCount = count;
        updateRoomInfo();

    }

    private void updateRoomInfo() {

        if (classroomEngine.getCtlSession().streamType == Live.StreamType.LIVE) {
            long liveDur = classroomEngine.getCtlSession().ctl.duration * 60;
            String totalStr = TimeUtil.formatSecondTime(liveDur);
            String livetimeStr = TimeUtil.formatSecondTime(reserveliveTime);
            lTopRoominfoView.setText(livetimeStr + "/" + totalStr + "  " + memCount + "人观看");
        } else {
            String livetimeStr = TimeUtil.formatSecondTime(reserveliveTime);
            lTopRoominfoView.setText(livetimeStr + "  " + memCount + "人观看");
        }

    }

    private void initControlPanel() {

        int orientation = UIUtils.getCurrentOrientation(getContext());
        controlHandleOnRotate(orientation);

        lTopRoominfoView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.class_living_animation,0,0,0);
        lTopRoominfoView.start();

        lRightSwitchcameraView.setVisibility(View.GONE);
        lRightSwitchVbView.setVisibility(View.GONE);
        pBottomClassnameView.setText(classroomEngine.getRoomTitle());
        pBottomAnimationView.setVisibility(View.VISIBLE);

        String avatorUrl = Account.getAvatar(classroomEngine.getCtlSession().claimedBy,
                lTopPhotoView.getMeasuredWidth());
        Glide.with(getContext())
                .load(avatorUrl)
                .transform(new CircleTransform(getContext()))
                .placeholder(R.drawable.ic_defaultavatar)
                .error(R.drawable.ic_defaultavatar)
                .into(pBottomAvatorView);
        pBottomAvatorView.setVisibility(View.VISIBLE);

        Glide.with(getContext())
                .load(avatorUrl)
                .transform(new CircleTransform(getContext()))
                .placeholder(R.drawable.ic_defaultavatar)
                .error(R.drawable.ic_defaultavatar)
                .into(lTopPhotoView);

        requestUpdateMemberCount();

        centerOne2oneView.setEnabled(false);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 响应1队1
    //

    private void closeStreaming() {
        if (streamingEngine != null) {
            streamingEngine.pauseAV();
            streamingEngine.stopStreamingAV();
        }

        streamView.setVisibility(View.GONE);
        streaming = false;
    }

    private void setupStream() {
        streamView.setVisibility(View.VISIBLE);
        streaming = false;
        if (streamingEngine == null) {
            streamingEngine = new StreamingEngine(getContext(), streamView.getCameraPreviewView());
            streamingEngine.setStreamingUrl(classroomEngine.getPublishUrl());
            streamingEngine.setStateListener(new StreamingEngine.AVStreamingStateListener() {
                @Override
                public void onAVStateChanged(StreamingState streamingState, Object extra) {
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
                        case READY:
                            if (XiaojsConfig.DEBUG) {
                                Logger.d("READY");
                            }
                            streamingEngine.startStreamingAV();
                            break;
                    }
                }
            });
            streamingEngine.preparePublish(streamView.getCameraPreviewView());
            streamingEngine.resumeAV();
        } else {
            streamingEngine.setStreamingUrl(classroomEngine.getPublishUrl());
            streamingEngine.updateStreamingProfile();
            streamingEngine.resumeAV();
            streamingEngine.startStreamingAV();
        }


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 开始直播
    //

    private void requestPublish() {
        if (classroomEngine.canForceIndividual()) {

            if (classroomEngine.canIndividualByState()) {
                showFroceDlg();
                return;
            }
            requestLive();
        }
    }


    private void showFroceDlg() {
        final CommonDialog tipsDialog = new CommonDialog(getContext());

        String name = "当前用户";
        Attendee attendee = classroomEngine.getMember(classroomEngine.getCtlSession().claimedBy);
        if (attendee != null) {
            name = attendee.name;
        }


        tipsDialog.setDesc(name + "的直播将被终止，你确定要直播么？");
        tipsDialog.setLefBtnText(R.string.cancel);
        tipsDialog.setRightBtnText(R.string.start_live);
        tipsDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                tipsDialog.dismiss();
                requestLive();
            }
        });
        tipsDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                tipsDialog.dismiss();
            }
        });
        tipsDialog.show();
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

        classroomEngine.cannelLiveTimerObserver();

        enterIdle();
    }

    private void handleSyncState(SyncStateReceive syncStateReceive) {

        updateStartOrPausedLiveButtonWhenSyncStateChanged(syncStateReceive);

    }

    private void handleO2o(OpenMediaReceive receive) {
        Attendee attendee = classroomEngine.getMember(receive.from);
        if (attendee != null) {
            startVirbating();
            receivedO2o(attendee);
        }
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
                    classroomEngine.cannelLiveTimerObserver();
                    enterIdle();
                    break;
                case Su.EventType.STOP_STREAM_BY_EXPIRATION:
                    classroomEngine.cannelLiveTimerObserver();
                    enterIdle();
                    break;
                case Su.EventType.STREAMING_STOPPED:
                    handleStreamStop((StreamStopReceive) eventReceived.t);
                    break;
                case Su.EventType.TALK:
                    Talk talk = (Talk) eventReceived.t;
                    //TODO fix同一条消息多次回调?
                    handleReceivedMsg(talk);
                    break;
                case Su.EventType.OPEN_MEDIA:
                    if (eventReceived.t == null)
                        return;
                    OpenMediaReceive receive = (OpenMediaReceive) eventReceived.t;
                    handleO2o(receive);
                    break;
                case Su.EventType.CLOSE_MEDIA:
                    if (eventReceived.t == null)
                        return;
                    closeStreaming();
                    break;
                case Su.EventType.JOIN:
                case Su.EventType.LEAVE:
                    requestUpdateMemberCount();
                    break;
                case Su.EventType.TIME_UPDATE:
                    liveTime = eventReceived.value1;
                    reserveliveTime = eventReceived.value2;
                    updateRoomInfo();
                    break;
            }
        }
    };


}
