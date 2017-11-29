package cn.xiaojs.xma.ui.classroom2;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import cn.xiaojs.xma.ui.classroom2.base.BaseLiveView;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatAdapter;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.ui.classroom2.core.SessionDataObserver;
import cn.xiaojs.xma.ui.classroom2.live.PlayLiveView;
import cn.xiaojs.xma.ui.classroom2.live.StreamingEngine;
import cn.xiaojs.xma.ui.classroom2.live.VideoStreamView;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;
import cn.xiaojs.xma.ui.classroom2.util.VibratorUtil;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.UIUtils;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class PlayFragment extends MovieFragment
        implements ChatAdapter.FetchMoreListener, BaseLiveView.ControlListener {

    @BindView(R.id.play_root_lay)
    RelativeLayout playRootLayout;
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

    private Disposable controlAutotimer;

    private int frontViewWidth;
    private int frontViewheight;
    private int frontMarginLeft;
    private int frontMarginTop;
    private int frontViewIndex = 4; //如果动了XML中的布局，需要更新此index
    private int backViewIndex = 0;

    private SessionDataObserver dataObserver = new SessionDataObserver() {
        @Override
        public void onMemberUpdated() {
            if (TextUtils.isEmpty(pBottomClassnameView.getText())) {
                String targetId = classroomEngine.getCtlSession().claimedBy;
                Attendee attendee = classroomEngine.getMember(targetId);
                if (attendee != null) {
                    String descname = attendee.name;
                    pBottomClassnameView.setText(descname);
                    lTopRoominfoNameView.setText(descname);
                }
            }

        }
    };

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
        frontViewWidth = getResources().getDimensionPixelSize(R.dimen.o2o_width);
        frontViewheight = getResources().getDimensionPixelSize(R.dimen.o2o_height);
        frontMarginLeft = getResources().getDimensionPixelSize(R.dimen.o2o_margin_left);
        frontMarginTop = getResources().getDimensionPixelSize(R.dimen.o2o_margin_top);

        streamView.setControlEnabled(true);
        streamView.setControlListener(this);

        videoView.setControlListener(this);
        videoView.setControlEnabled(false);
        videoView.setCloseViewVisibility(View.GONE);

        initControlPanel();
        classroomEngine.observeSessionData(dataObserver);

        initTalkData(this);

        configPortLandOperaButton();

        videoView.startPlay(classroomEngine.getPlayUrl());

        playLiveObserver = classroomEngine.observerPlaylive(receivedConsumer);
        classroomEngine.addLiveTimerObserver(playLiveObserver);

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

        if (dataObserver != null) {
            classroomEngine.unObserveSessionData(dataObserver);
        }

        if (streamingEngine != null) {
            streamingEngine.stopStreamingAV();
            streamingEngine.destoryAV();
            streamingEngine = null;
        }

        destoryAutotimer();

        classroomEngine.removeLiveTimerObserver(playLiveObserver);

        if (playLiveObserver != null) {
            playLiveObserver.dispose();
        }


    }


    @Override
    protected Bitmap doScreenshot() {
        if (isBoardShown()) {
            return whiteboardFragment.preview();
        }
        return videoView.getBitmap();
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
    protected void controlHandleOnRotate(int orientation) {
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                controlClickView.setVisibility(View.VISIBLE);
                controlPort.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                controlLand.setVisibility(View.VISIBLE);
                startHiddenTimer(controlLand);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                controlLand.setVisibility(View.GONE);
                controlClickView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                controlPort.setVisibility(View.VISIBLE);
                startHiddenTimer(controlPort);
                break;
        }
    }


    @Override
    public void hiddeOrshowControl() {
        int currentOrient = UIUtils.getCurrentOrientation(getContext());
        switch (currentOrient) {
            case Configuration.ORIENTATION_LANDSCAPE:
                if (controlLand == null)
                    return;

                if (controlLand.getVisibility() == View.VISIBLE) {
                    controlLand.setVisibility(View.GONE);
                    destoryAutotimer();
                } else {
                    controlLand.setVisibility(View.VISIBLE);
                    startHiddenTimer(controlLand);
                }

                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if (controlPort == null)
                    return;
                if (controlPort.getVisibility() == View.VISIBLE) {
                    controlPort.setVisibility(View.GONE);
                    destoryAutotimer();
                } else {
                    controlPort.setVisibility(View.VISIBLE);
                    startHiddenTimer(controlPort);
                }
                break;
        }
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
    public void back() {

        if (o2oAttendee != null) {
            onLiveViewClosed(streamView);
        }

        super.back();
    }

    @Override
    public void onStartLiveClick(View view) {

        if (checkVistorPermission())
            return;

        requestPublish();
    }

    @Override
    public void onStartOrStopLiveClick(View view) {

        if (checkVistorPermission())
            return;

        requestPublish();
    }

    @Override
    protected void handleArgeedO2o(Attendee attendee) {
        super.handleArgeedO2o(attendee);
        setupStream();
    }

    @Override
    public void onLiveViewClosed(BaseLiveView liveView) {

        if (liveView == null)
            return;

        if (liveView == streamView) {

            if (playRootLayout.indexOfChild(videoView) == frontViewIndex) {
                bringToBack(videoView);
                bringToFront(streamView);
            }

            closeStreaming();
            sendCloseMedia(o2oAttendee.accountId);
            sendStopStreaming(Live.StreamType.INDIVIDUAL);
        }

    }

    @Override
    public void onLiveViewScaled(BaseLiveView liveView) {

        if (liveView == null)
            return;

        if (liveView == streamView) {
            //streamView全屏，点击View隐藏
            //videoView小屏，并移动到前端
            bringToBack(streamView);
            bringToFront(videoView);

        } else if (liveView == videoView) {
            bringToBack(videoView);
            bringToFront(streamView);
        }
    }


    @Override
    public void onUpdateMembersCount(int count) {
        super.onUpdateMembersCount(count);

        memCount = count;
        updateRoomInfo();

    }

    private void bringToBack(BaseLiveView target) {
        target.setControlEnabled(false);
        RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) target.getLayoutParams();
        lp.height = MATCH_PARENT;
        lp.width = MATCH_PARENT;
        lp.setMargins(0,0,0,0);

        if (target == videoView) {
            videoView.stopPlay();
        }
        playRootLayout.removeView(target);
        playRootLayout.addView(target, backViewIndex);
        if (target == videoView) {
            videoView.startPlay(classroomEngine.getPlayUrl());
        }



    }

    private void bringToFront(BaseLiveView target) {
        target.setControlEnabled(true);
        RelativeLayout.LayoutParams lp= (RelativeLayout.LayoutParams) target.getLayoutParams();
        lp.width = frontViewWidth;
        lp.height = frontViewheight;
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.setMargins(frontMarginLeft,frontMarginTop,0,0);
        //target.bringToFront();

        playRootLayout.removeView(target);
        playRootLayout.addView(target, frontViewIndex);

    }

    private void updateRoomInfo() {

//        String livetimeStr;
//        if (classroomEngine.getCtlSession().streamType == Live.StreamType.LIVE) {
//            long liveDur = classroomEngine.getCtlSession().ctl.duration * 60;
//            String totalStr = TimeUtil.formatSecondTime(liveDur);
//            livetimeStr = TimeUtil.formatSecondTime(reserveliveTime);
//
//        } else {
//            livetimeStr = TimeUtil.formatSecondTime(reserveliveTime);
//        }
        String livetimeStr = TimeUtil.formatSecondTime(reserveliveTime);
        String otherStr = livetimeStr + "  (" + memCount + "人)";

        pBottomClassOtherView.setText(otherStr);
        lTopRoominfoOtherView.setText(otherStr);

    }

    private void initControlPanel() {

        int orientation = UIUtils.getCurrentOrientation(getContext());
        controlHandleOnRotate(orientation);

        lTopRoominfoRootView.setVisibility(View.VISIBLE);

        lTopRoominfoAniView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.class_living_animation, 0, 0, 0);
        lTopRoominfoAniView.start();

        lRightSwitchcameraView.setVisibility(View.GONE);
        lRightSwitchVbView.setVisibility(View.GONE);
        pBottomAnimationView.setVisibility(View.VISIBLE);

        String targetId = classroomEngine.getCtlSession().claimedBy;

        String avatorUrl = Account.getAvatar(targetId,
                lTopRoominfoPhotoView.getMeasuredWidth());
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
                .into(lTopRoominfoPhotoView);


        if (classroomEngine.getCtlSession().streamType == Live.StreamType.LIVE) {
            pBottomClassnameView.setText(classroomEngine.getRoomTitle());
            lTopRoominfoNameView.setText(classroomEngine.getRoomTitle());
        } else {
            Attendee attendee = classroomEngine.getMember(targetId);

            if (attendee != null) {
                String descname = attendee.name;
                pBottomClassnameView.setText(descname);
                lTopRoominfoNameView.setText(descname);
            }
        }


        requestUpdateMemberCount();

        centerOne2oneView.setEnabled(false);

    }

    private void startHiddenTimer(View view) {
        destoryAutotimer();
        controlAutotimer = autoStartHiddeAnim(view);
    }

    private void destoryAutotimer() {
        if (controlAutotimer != null) {
            controlAutotimer.dispose();
            controlAutotimer = null;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 响应1队1
    //

    private void closeStreaming() {
        if (streamingEngine != null) {
            streamingEngine.pauseAV();
            streamingEngine.stopStreamingAV();
        }

        if (playRootLayout.indexOfChild(videoView) == frontViewIndex) {
            bringToBack(videoView);
            bringToFront(streamView);
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

        classroomEngine.stopLiveTimerObserver();

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
                    classroomEngine.stopLiveTimerObserver();
                    enterIdle();
                    break;
                case Su.EventType.STOP_STREAM_BY_EXPIRATION:
                    classroomEngine.stopLiveTimerObserver();
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
