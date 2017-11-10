package cn.xiaojs.xma.ui.classroom2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.socket.EventResponse;
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
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;
import cn.xiaojs.xma.ui.classroom2.widget.CameraPreviewFrameView;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.Common3Dialog;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.ToastUtil;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class LivingFragment extends AVFragment implements ChatAdapter.FetchMoreListener,
        PlayLiveView.ControlListener {

    @BindView(R.id.videostream_view)
    VideoStreamView videoStreamView;
    @BindView(R.id.video_play)
    PlayLiveView playView;

    private EventListener.ELLiving livingObserver;

    private Attendee one2oneAttendee;

    private String streamDeprivedBy;

    private int memCount;
    private long liveTime;


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
        classroomEngine.setLiveTimerObserver(livingObserver);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected CameraPreviewFrameView createCameraPreview() {
        return videoStreamView.getCameraPreviewView();
    }

    @Override
    public void onPlayClosed() {
        closeMedia();
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
        chooseMemberFragment.setTargetFragment(this, CTLConstant.REQUEST_CHOOSE_MEMBER);
        showSlidePanel(chooseMemberFragment, "chat_slide");
    }


    @Override
    public int onSwitchStreamingClick(View view) {
        int whiteboardVis = super.onSwitchStreamingClick(view);

        if (whiteboardVis == View.VISIBLE) {
            controlClickView.setVisibility(View.GONE);
            controlLand.setVisibility(View.VISIBLE);
            clearControlAnim();
        } else {
            controlClickView.setVisibility(View.VISIBLE);
            hiddeControlAnim(controlLand);
        }

        return whiteboardVis;
    }

    @Override
    public void onUpdateMembersCount(int count) {
        super.onUpdateMembersCount(count);
        memCount = count;
        updateRoomInfo();

    }

    private void updateRoomInfo() {

        if (classroomEngine.getLiveState().equals(Live.LiveSessionState.LIVE)) {

            long liveDur = classroomEngine.getCtlSession().ctl.duration * 60;
            String totalStr = TimeUtil.formatSecondTime(liveDur);
            String livetimeStr = TimeUtil.formatSecondTime(liveTime);
            lTopRoominfoView.setText(livetimeStr + "/" + totalStr + "  " + memCount + "人观看");
        } else {
            lTopRoominfoView.setText("直播中（" + memCount + "人观看）");
        }

    }


    @Override
    public void onClosed() {
        exitSlidePanel();
    }

    @Override
    public void onOpened() {

    }


    @Override
    public void back() {

        if (Live.LiveSessionState.LIVE.equals(classroomEngine.getLiveState())
                && classroomEngine.getIdentityInLesson() == CTLConstant.UserIdentity.LEAD) {
            showFinishClassDlg();
            return;
        }

        handleBack();


    }

    private void handleBack() {

        classroomEngine.cannelLiveTimerObserver();

        if (one2oneAttendee != null) {
            stopPlay(true);
        }

        stopStreaming();

        changeOrientation();
        enterIdle();
        sendStopStreaming();
    }

    @Override
    public void onFetchMoreRequested() {
        loadTalk();
    }

    private void initControlView() {

        lTopRoominfoView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.class_living_animation,0,0,0);
        lTopRoominfoView.start();


        String avatorUrl = Account.getAvatar(AccountDataManager.getAccountID(getContext()),
                lTopPhotoView.getMeasuredWidth());
        Glide.with(getContext())
                .load(avatorUrl)
                .transform(new CircleTransform(getContext()))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(lTopPhotoView);
        lRightScreenshortView.setVisibility(View.GONE);

        configStopButton();

        requestUpdateMemberCount();

    }

    private void configStopButton() {

        if (classroomEngine.getLiveState().equals(Live.LiveSessionState.LIVE)) {
            startOrStopLiveView.setText("下课");
        } else {
            startOrStopLiveView.setText("停止直播");
        }


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 下课
    //

    protected void showFinishClassDlg() {

        final CommonDialog finishDialog = new CommonDialog(getContext());
        finishDialog.setTitle(R.string.finish_classroom);
        finishDialog.setDesc(R.string.finish_classroom_tips);
        finishDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                finishDialog.dismiss();
            }
        });

        finishDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                finishDialog.dismiss();
                requestFinishClass();
            }
        });

        finishDialog.show();
    }

    protected void requestFinishClass() {

        showProgress(true);
        classroomEngine.finishClass(classroomEngine.getTicket(),
                new APIServiceCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody object) {
                        cancelProgress();
                        handleBack();
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress();
                        ToastUtil.showToast(getContext(), errorMessage);
                    }
                });
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

                ToastUtil.showToast(getContext(), errorMessage);
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
        } else if (Live.MediaStatus.FAILED_DUE_TO_DENIED == feedbackReceive.status) {
            msg = getContext().getString(R.string.user_refuse_one_to_one_tips);
        } else if (Live.MediaStatus.FAILED_DUE_TO_NETWORK_ISSUES == feedbackReceive.status) {
            msg = getContext().getString(R.string.failed_one2one_network_issue);
        } else if (Live.MediaStatus.FAILED_DUE_TO_PRIVACY == feedbackReceive.status) {
            msg = getContext().getString(R.string.failed_one2one_privacy);
        }

        ToastUtil.showToast(getContext(), msg);
    }

    private void showPlay() {
        playView.setVisibility(View.VISIBLE);
        playView.startPlay(classroomEngine.getPlayUrl());
    }


    private void stopPlay(boolean send) {

        if (send) {
            closeMedia();
        }

        playView.stopPlay();
        playView.setVisibility(View.GONE);
        one2oneAttendee = null;
    }


    protected void closeMedia() {
        sendCloseMedia(one2oneAttendee.accountId);
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

            classroomEngine.cannelLiveTimerObserver();

            Attendee attendee = classroomEngine.getMember(streamDeprivedBy);
            if (attendee == null) {
                ToastUtil.showToast(getContext(), "你的直播已被老师中断");
            } else {
                showStopTips(attendee);
            }


        }
    }

    private void handleStreamStart(StreamStartReceive streamStartReceive) {
        if (!TextUtils.isEmpty(streamDeprivedBy)
                && streamDeprivedBy.equals(streamStartReceive.claimedBy)) {

            classroomEngine.cannelLiveTimerObserver();
            enterPlay();
        }
    }


    private void showStopTips(Attendee attendee) {
        final Common3Dialog tipsDialog = new Common3Dialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_classroom2_dlg_tips_stopped_live, null);

        ImageView avatorView = (ImageView) view.findViewById(R.id.avator);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        Button okBtn = (Button) view.findViewById(R.id.ok_btn);

        String avatorUrl = Account.getAvatar(attendee.accountId, avatorView.getMeasuredWidth());
        Glide.with(getContext())
                .load(avatorUrl)
                .transform(new CircleTransform(getContext()))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(avatorView);


        titleView.setText(attendee.name + "老师正在直播分享");

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();
            }
        });

        tipsDialog.setCustomView(view);
        tipsDialog.show();
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
                case Su.EventType.JOIN:
                case Su.EventType.LEAVE:
                    requestUpdateMemberCount();
                    break;
                case Su.EventType.TIME_UPDATE:
                    liveTime = eventReceived.value1;
                    updateRoomInfo();
                    break;
            }
        }
    };

    @Override
    protected boolean isDefaultShowBoard() {
        return true;
    }
}
