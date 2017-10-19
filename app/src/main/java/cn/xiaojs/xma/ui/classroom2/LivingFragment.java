package cn.xiaojs.xma.ui.classroom2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.StreamingState;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaResponse;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.MediaFeedbackReceive;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.model.socket.room.StreamStopReceive;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom2.base.AVFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatAdapter;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.ui.classroom2.live.PlayLiveView;
import cn.xiaojs.xma.ui.classroom2.live.VideoStreamView;
import cn.xiaojs.xma.ui.classroom2.member.ChooseMemberFragment;
import cn.xiaojs.xma.ui.classroom2.widget.CameraPreviewFrameView;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.util.ToastUtil;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class LivingFragment extends AVFragment implements ChatAdapter.FetchMoreListener, PlayLiveView.ControlListener {

    @BindView(R.id.videostream_view)
    VideoStreamView videoStreamView;
    @BindView(R.id.video_play)
    PlayLiveView playView;

    private boolean streaming;

    private EventListener.ELLiving livingObserver;

    private Attendee one2oneAttendee;

    private String streamDeprivedBy;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_classroom2_living, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initControlView();
        initTalkData(this);

        playView.setControlListener(this);
        playView.setCloseEnabled(true);

        livingObserver = classroomEngine.observerLiving(receivedConsumer);

    }

    @Override
    protected CameraPreviewFrameView createCameraPreview() {
        return videoStreamView.getCameraPreviewView();
    }

    @Override
    public void onPlayClosed() {
        sendCloseMedia();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (livingObserver != null) {
            livingObserver.dispose();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CTLConstant.REQUEST_CHOOSE_MEMBER:
                    exitSlidePanel();

                    Attendee attendee = (Attendee) data.getSerializableExtra(CTLConstant.EXTRA_MEMBER);
                    if (attendee != null) {
                        requestOne2One(attendee);
                    }

                    break;
            }
        }

    }


    @Override
    public void onTopbackClick(View view, boolean land) {
        back();
    }


    @Override
    public void onSwitchCamera(View view) {
        switchCamera();
    }


    @Override
    public void onStartOrStopLiveClick(View view) {
        back();
    }

    @Override
    public void onOne2OneClick(View view) {

        ChooseMemberFragment chooseMemberFragment = new ChooseMemberFragment();
        chooseMemberFragment.setTargetFragment(this,CTLConstant.REQUEST_CHOOSE_MEMBER);
        showSlidePanel(chooseMemberFragment, "chat_slide");
    }


    @Override
    public void onClosed() {
        exitSlidePanel();
    }

    @Override
    public void onOpened() {

    }




    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
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

        if (one2oneAttendee !=null) {
            stopPlay(true);
        }


        stopStreaming();
        super.back();
        enterIdle();

        sendStopStreaming();
    }

    @Override
    public void onFetchMoreRequested() {
        loadTalk();
    }

    private void initControlView() {

        String avatorUrl = Account.getAvatar(AccountDataManager.getAccountID(getContext()),
                lTopPhotoView.getMeasuredWidth());
        Glide.with(getContext())
                .load(avatorUrl)
                .transform(new CircleTransform(getContext()))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(lTopPhotoView);

        lTopRoominfoView.setText("直播中");
        lRightScreenshortView.setVisibility(View.GONE);

        configStopButton();

    }

    private void configStopButton() {

        if (classroomEngine.getLiveState().equals(Live.LiveSessionState.LIVE)) {
            startOrStopLiveView.setText("下课");
        }else{
            startOrStopLiveView.setText("停止直播");
        }


    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 一对一
    //

    private void requestOne2One(final Attendee attendee) {
        showProgress(true);
        classroomEngine.openMedia(attendee.accountId, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                cancelProgress();
                one2oneAttendee = attendee;
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {

                cancelProgress();

                if (Errors.MEDIA_ALREADY_OPENED.equals(errorCode)) {
                    errorMessage = "你已经正在一对一";
                } else if (Errors.PENDING_FOR_OPEN_ACK.equals(errorCode)) {
                    errorMessage = getContext().getResources().getString(R.string.has_send_one_to_noe_tips);
                } else if (Errors.NOT_ON_LIVE.equals(errorCode)) {
                    errorMessage = getContext().getResources().getString(R.string.send_one_to_one_offline_tips);
                } else {
                    errorMessage = getContext().getResources().getString(R.string.send_one_to_one_failed);
                }

                ToastUtil.showToast(getContext(),errorMessage);
            }
        });
    }


    private void feedback(EventReceived eventReceived) {
        MediaFeedbackReceive feedbackReceive = (MediaFeedbackReceive) eventReceived.t;

        String msg = getContext().getString(R.string.failed_one2one);

        if (Live.MediaStatus.READY == feedbackReceive.status && !TextUtils.isEmpty(feedbackReceive.playUrl)) {
            msg = getContext().getString(R.string.success_one2one);

            showPlay();

           // mPlayStreamUrl = feedbackReceive.playUrl;
           // playStream(CTLConstant.StreamingType.PLAY_PEER_TO_PEER, feedbackReceive.playUrl);
        }else if (Live.MediaStatus.FAILED_DUE_TO_DENIED == feedbackReceive.status) {
            msg = getContext().getString(R.string.user_refuse_one_to_one_tips);
        }else if (Live.MediaStatus.FAILED_DUE_TO_NETWORK_ISSUES == feedbackReceive.status) {
            msg = getContext().getString(R.string.failed_one2one_network_issue);
        }else if (Live.MediaStatus.FAILED_DUE_TO_PRIVACY== feedbackReceive.status) {
            msg = getContext().getString(R.string.failed_one2one_privacy);
        }

        ToastUtil.showToast(getContext(),msg);
    }

    private void showPlay() {
        playView.setVisibility(View.VISIBLE);
        playView.startPlay(classroomEngine.getPlayUrl());
    }


    private void stopPlay(boolean send) {

        if (send) {
            sendCloseMedia();
        }

        playView.stopPlay();
        playView.setVisibility(View.GONE);
        one2oneAttendee = null;
    }


    protected void sendCloseMedia() {

        classroomEngine.closeMedia(one2oneAttendee.accountId, new EventCallback<CloseMediaResponse>() {
            @Override
            public void onSuccess(CloseMediaResponse closeMediaResponse) {
                if (XiaojsConfig.DEBUG) {
                    ToastUtil.showToast(getContext(),"close open peer to peer video success");
                }
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                ToastUtil.showToast(getContext(), errorMessage);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 直播被老师强制挤掉
    //

    private void handleStreamStop(StreamStopReceive streamStopReceive) {

        String myId = AccountDataManager.getAccountID(getContext());


        if (streamStopReceive.streamType == Live.StreamType.INDIVIDUAL
                && myId.equals(streamStopReceive.claimedBy)
                && !TextUtils.isEmpty(streamStopReceive.deprivedBy)) {
            streamDeprivedBy = streamStopReceive.deprivedBy;

            Attendee attendee = classroomEngine.getMember(streamDeprivedBy);
            if (attendee !=null) {
                ToastUtil.showToast(getContext(), "你的直播已被" + attendee.name+"中断");
            }


        }
    }

    private void handleStreamStart(StreamStartReceive streamStartReceive) {
        if (!TextUtils.isEmpty(streamDeprivedBy)
                && streamDeprivedBy.equals(streamStartReceive.claimedBy)) {
            enterPlay();
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
                case Su.EventType.TALK:
                    Talk talk = (Talk) eventReceived.t;
                    //TODO fix同一条消息多次回调?
                    handleReceivedMsg(talk);
                    break;
                case Su.EventType.MEDIA_FEEDBACK:
                    feedback(eventReceived);
                    break;
                case Su.EventType.CLOSE_MEDIA:
                    stopPlay(false);
                    break;
                case Su.EventType.STREAMING_STOPPED:
                    handleStreamStop((StreamStopReceive) eventReceived.t);
                    break;
                case Su.EventType.STREAMING_STARTED:
                    handleStreamStart((StreamStartReceive) eventReceived.t);
                    break;
            }
        }
    };

}
