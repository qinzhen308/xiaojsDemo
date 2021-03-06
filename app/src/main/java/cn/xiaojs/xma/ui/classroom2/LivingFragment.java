package cn.xiaojs.xma.ui.classroom2;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;

import java.util.concurrent.TimeUnit;

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
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom.page.BoardScreenshotFragment;
import cn.xiaojs.xma.ui.classroom.page.OnPhotoDoodleShareListener;
import cn.xiaojs.xma.ui.classroom2.base.AVFragment;
import cn.xiaojs.xma.ui.classroom2.base.BaseLiveView;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatAdapter;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.ui.classroom2.live.PlayLiveView;
import cn.xiaojs.xma.ui.classroom2.live.VideoStreamView;
import cn.xiaojs.xma.ui.classroom2.member.ChooseMemberFragment;
import cn.xiaojs.xma.ui.classroom2.member.ShareToFragment;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;
import cn.xiaojs.xma.ui.classroom2.widget.CameraPreviewFrameView;
import cn.xiaojs.xma.ui.classroom2.widget.O2oDragLayout;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.Common3Dialog;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.ToastUtil;
import cn.xiaojs.xma.util.UIUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class LivingFragment extends AVFragment implements ChatAdapter.FetchMoreListener,
        BaseLiveView.ControlListener {

    @BindView(R.id.stream_root_lay)
    O2oDragLayout streamRootLayout;
    @BindView(R.id.videostream_view)
    VideoStreamView videoStreamView;
    @BindView(R.id.video_play)
    PlayLiveView playView;
    @BindView(R.id.loading_layoutx)
    RelativeLayout loadingLayout;

    private EventListener.ELLiving livingObserver;

    private Attendee one2oneAttendee;

    private String streamDeprivedBy;

    private int memCount;
    private long liveTime;

    private Disposable controlAutotimer;
    private Disposable o2oOuttimer;

    private int frontViewWidth;
    private int frontViewheight;
    private int frontMarginLeft;
    private int frontMarginTop;
    private int frontViewIndex = 5; //如果动了XML中的布局，需要更新此index
    private int backViewIndex = 0;

    private boolean finishByTeacher;


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

        frontViewWidth = getResources().getDimensionPixelSize(R.dimen.o2o_width);
        frontViewheight = getResources().getDimensionPixelSize(R.dimen.o2o_height);
        frontMarginLeft = getResources().getDimensionPixelSize(R.dimen.o2o_margin_left);
        frontMarginTop = getResources().getDimensionPixelSize(R.dimen.o2o_margin_top);

        //streamRootLayout.setDragEnable(false);

        showLoading();

        initControlView();
        initTalkData(this);

        playView.setControlListener(this);
        playView.setControlEnabled(true);

        videoStreamView.setControlEnabled(false);
        videoStreamView.setControlListener(this);
        videoStreamView.setCloseViewVisibility(View.GONE);

        livingObserver = classroomEngine.observerLiving(receivedConsumer);
        classroomEngine.addLiveTimerObserver(livingObserver);

        config4Preview();
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
    public void onLiveViewClosed(BaseLiveView liveView) {

        if (liveView == null)
            return;

        if (liveView == playView) {

            if (streamRootLayout.indexOfChild(videoStreamView) == frontViewIndex) {
                bringToBack(videoStreamView);
                bringToFront(playView);
            }
            stopPlay(true);
        }


    }

    @Override
    public void onLiveViewScaled(BaseLiveView liveView) {

        if (liveView == null)
            return;

        makeCameraStreamOnly();
        //streamRootLayout.setDragEnable(true);

        if (liveView == playView) {
            bringToBack(playView);
            bringToFront(videoStreamView);
            //streamRootLayout.setLiveView(videoStreamView);

        } else if (liveView == videoStreamView) {
            bringToBack(videoStreamView);
            bringToFront(playView);
            //streamRootLayout.setLiveView(playView);
        }

    }

    @Override
    protected boolean makeCameraStreamOnly() {
        boolean changed = super.makeCameraStreamOnly();
        if (changed) {
            lRightSwitchVbView.setVisibility(View.GONE);
            controlClickView.setVisibility(View.VISIBLE);
            startHiddenTimer(controlLand);
        }

        return changed;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handleDestory();
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
    public void onAVStateChanged(StreamingState streamingState, Object extra) {
        super.onAVStateChanged(streamingState, extra);

        switch (streamingState) {
            case PREPARING:
            case CONNECTED:
            case CONNECTING:
            case READY:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showLoading();
                    }
                });

                break;
            case STREAMING:
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hiddenLoading();
                    }
                });

                break;
            case SHUTDOWN:
                break;
            case IOERROR:
            case DISCONNECTED:
                break;

        }

    }

    @Override
    public void onScreenshotClick(View view) {
        if (isBoardShown()) {
            super.onScreenshotClick(view);
        } else {
            captureFrame(new FrameCapturedCallback() {
                @Override
                public void onFrameCaptured(Bitmap bitmap) {
                    if (bitmap != null) {
                        Fragment fragment = BoardScreenshotFragment.createInstance(
                                getActivity(), bitmap, new OnPhotoDoodleShareListener() {
                                    @Override
                                    public void onPhotoShared(Attendee attendee, Bitmap bmp) {
                                        ShareToFragment shareToFragment = new ShareToFragment();
                                        shareToFragment.setTargetBitmap(bmp);
                                        shareToFragment.setRootFragment(LivingFragment.this);
                                        showSlidePanel(shareToFragment, "share_to");
                                    }
                                });
                        getFragmentManager()
                                .beginTransaction()
                                .add(R.id.screenshot_container, fragment)
                                .addToBackStack("screenshot")
                                .commitAllowingStateLoss();
                    } else {
                        ToastUtil.showToast(getActivity(), "截图失败");
                    }
                }
            });
        }
    }

    @Override
    protected Bitmap doScreenshot() {
        return whiteboardFragment.preview();
    }

    @Override
    public void onTopbackClick(View view, boolean land) {
        dealBack(false);
    }


    @Override
    public void onSwitchCamera(View view) {
        switchCamera();
    }


    @Override
    public void onStartOrStopLiveClick(View view) {

        dealBack(true);
    }

    @Override
    public void onOne2OneClick(View view) {

        ChooseMemberFragment chooseMemberFragment = new ChooseMemberFragment();
        chooseMemberFragment.setTargetFragment(this, CTLConstant.REQUEST_CHOOSE_MEMBER);
        if (one2oneAttendee != null) {
            Bundle b = new Bundle();
            b.putString(CTLConstant.EXTRA_ID, one2oneAttendee.accountId);
            chooseMemberFragment.setArguments(b);
        }
        showSlidePanel(chooseMemberFragment, "chat_slide");
    }


    @Override
    public int onSwitchStreamingClick(View view) {
        int whiteboardVis = super.onSwitchStreamingClick(view);

        if (whiteboardVis == View.VISIBLE) {
            controlClickView.setVisibility(View.GONE);
            destoryAutotimer();
            controlLand.setVisibility(View.VISIBLE);
        } else {
            controlClickView.setVisibility(View.VISIBLE);
            startHiddenTimer(controlLand);
        }

        return whiteboardVis;
    }

    @Override
    public void hiddeOrshowControl() {

        if (controlLand.getVisibility() == View.VISIBLE) {
            controlLand.setVisibility(View.GONE);
            destoryAutotimer();
        } else {
            controlLand.setVisibility(View.VISIBLE);
            startHiddenTimer(controlLand);
        }
    }

    @Override
    public int showOrHiddenCenterPanel() {
        int vis = super.showOrHiddenCenterPanel();

        if (vis == View.VISIBLE) {
            if (controlClickView.getVisibility() == View.GONE) {
                controlClickView.setVisibility(View.VISIBLE);
            }
        } else {
            if (getWhiteboardContainerLayoutVisibility() == View.VISIBLE) {
                controlClickView.setVisibility(View.GONE);
            }
        }
        return vis;
    }

    @Override
    public void onRotate(int orientation) {
        super.onRotate(orientation);
        controlHandleOnRotate(orientation);
    }

    @Override
    protected void controlHandleOnRotate(int orientation) {
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                dealBack(false);
                break;
        }
    }

    @Override
    public void onUpdateMembersCount(int count) {
        super.onUpdateMembersCount(count);
        memCount = count;
        updateRoomInfo();

    }

    private void updateRoomInfo() {


//        long liveDur = classroomEngine.getCtlSession().ctl.duration * 60;
//        String totalStr = TimeUtil.formatSecondTime(liveDur);
//        String livetimeStr = TimeUtil.formatSecondTime(liveTime);
//        lTopRoominfoView.setText(livetimeStr + "/" + totalStr + "  " + memCount + "人观看");

        if (XiaojsConfig.DEBUG) {
            Logger.d("the received live time:%d", liveTime);
        }


        String livetimeStr = TimeUtil.formatSecondTime(liveTime);
        String otherStr = livetimeStr + "  (" + memCount + "人)";

        lTopRoominfoOtherView.setText(otherStr);

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
        dealBack(false);
    }

    @Override
    public void onFetchMoreRequested() {
        loadTalk();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // back
    //

    private void handleDestory(){
        destoryAutotimer();
        destoryO2otimer();

        classroomEngine.removeLiveTimerObserver(livingObserver);

        if (livingObserver != null) {
            livingObserver.dispose();
        }

    }

    private void dealBack(boolean finishOrStop) {
        if (finishOrStop) {
            if (Live.LiveSessionState.LIVE.equals(classroomEngine.getLiveState())
                    && classroomEngine.getIdentityInLesson() == CTLConstant.UserIdentity.LEAD) {
                showFinishClassDlg();
                return;
            }
            classroomEngine.stopLiveTimerObserver();
            handleBack(Live.StreamType.INDIVIDUAL);
        } else {
            if (Live.LiveSessionState.LIVE.equals(classroomEngine.getLiveState())) {
                showExitLessoningClassDlg();
                return;
            }
            showExitLivingDlg();
        }
    }


    private void handleBack(int streamType) {

        Toast.makeText(getContext(), "您的直播已结束", Toast.LENGTH_SHORT).show();

        if (one2oneAttendee != null) {
            stopPlay(true);
        }

        handleDestory();

        stopStreaming();

        changeOrientation();
        enterIdle();
        sendStopStreaming(streamType);
    }

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

    protected void showExitLessoningClassDlg() {

        final CommonDialog dialog = new CommonDialog(getContext());
        if (one2oneAttendee != null) {
            dialog.setDesc("若退出，本节课将暂停且课堂互动自动结束？");
        } else {
            dialog.setDesc("若退出，本节课将暂停且课堂互动自动结束？");
        }

        dialog.setRightBtnText("取消");
        dialog.setLefBtnText("确定");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                handleBack(Live.StreamType.LIVE);
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    protected void showExitLivingDlg() {

        final CommonDialog dialog = new CommonDialog(getContext());
        if (one2oneAttendee != null) {
            dialog.setDesc("若退出，本次直播及互动将自动结束？");
        } else {
            dialog.setDesc("若退出，本次直播将自动结束？");
        }

        dialog.setRightBtnText("取消");
        dialog.setLefBtnText("确定");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();

                classroomEngine.stopLiveTimerObserver();
                handleBack(Live.StreamType.INDIVIDUAL);
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void bringToBack(BaseLiveView target) {
        target.setControlEnabled(false);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) target.getLayoutParams();
        lp.height = MATCH_PARENT;
        lp.width = MATCH_PARENT;
        lp.setMargins(0, 0, 0, 0);

        if (target == playView) {
            playView.stopPlay();
        }
        streamRootLayout.removeView(target);
        streamRootLayout.addView(target, backViewIndex);
        if (target == playView) {
            playView.startPlay(classroomEngine.getPlayUrl());
        }


    }

    private void bringToFront(BaseLiveView target) {
        target.setControlEnabled(true);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) target.getLayoutParams();
        lp.width = frontViewWidth;
        lp.height = frontViewheight;
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.setMargins(frontMarginLeft, frontMarginTop, 0, 0);
        //target.bringToFront();

        streamRootLayout.removeView(target);
        streamRootLayout.addView(target, frontViewIndex);

    }

    private void initControlView() {

        lRightSwitchcameraView.setVisibility(View.GONE);

        lTopRoominfoRootView.setVisibility(View.VISIBLE);

        lTopRoominfoAniView.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.class_living_animation, 0, 0, 0);
        lTopRoominfoAniView.start();


        String avatorUrl = Account.getAvatar(AccountDataManager.getAccountID(getContext()),
                lTopRoominfoPhotoView.getMeasuredWidth());
        Glide.with(getContext())
                .load(avatorUrl)
                .transform(new CircleTransform(getContext()))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(lTopRoominfoPhotoView);


        lTopRoominfoNameView.setText(classroomEngine.getRoomTitle());

        lRightScreenshortView.setVisibility(View.VISIBLE);

        configStopButton();

        requestUpdateMemberCount();

        controlClickView.setVisibility(View.GONE);
        destoryAutotimer();

    }

    private void configStopButton() {

        startOrStopLiveView.setBackgroundResource(R.drawable.class_dismiss_over);

        if (classroomEngine.getLiveState().equals(Live.LiveSessionState.LIVE)) {
            startOrStopLiveView.setText("下课");
        } else {
            startOrStopLiveView.setText("结束直播");
        }


    }

    private void showLoading() {
        loadingLayout.setVisibility(View.VISIBLE);
    }

    private void hiddenLoading() {
        loadingLayout.setVisibility(View.GONE);
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
    //
    // 下课
    //

    protected void requestFinishClass() {
        finishByTeacher = true;
        showProgress(true);
        classroomEngine.finishClass(classroomEngine.getTicket(),
                new APIServiceCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody object) {



                        cancelProgress();
                        handleBack(Live.StreamType.LIVE);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress();
                        ToastUtil.showToast(getContext(), errorMessage);
                        finishByTeacher = false;
                    }
                });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 一对一
    //


    private void startO2oTimer() {
        destoryO2otimer();
        Observable observable = Observable.timer(CTLConstant.O2O_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        o2oOuttimer = observable.subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                one2oneAttendee = null;
            }
        });
    }

    private void destoryO2otimer() {
        if (o2oOuttimer != null) {
            o2oOuttimer.dispose();
            o2oOuttimer = null;
        }
    }


    private void requestOne2One(final Attendee attendee) {
        showProgress(true);
        classroomEngine.openMedia(attendee.accountId, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                cancelProgress();
                one2oneAttendee = attendee;

                startO2oTimer();

                Toast.makeText(getContext(), "正在等待对方接受邀请…", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {

                cancelProgress();

                if (Errors.MEDIA_ALREADY_OPENED.equals(errorCode)) {
                    errorMessage = "你已经在视频对话了";
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

        destoryO2otimer();

        MediaFeedbackReceive feedbackReceive = (MediaFeedbackReceive) eventReceived.t;

        String msg = getContext().getString(R.string.failed_one2one);

        if (Live.MediaStatus.READY == feedbackReceive.status && !TextUtils.isEmpty(feedbackReceive.playUrl)) {

            msg = getContext().getString(R.string.success_one2one);

            showPlay();

            // mPlayStreamUrl = feedbackReceive.playUrl;
            // playStream(CTLConstant.StreamingType.PLAY_PEER_TO_PEER, feedbackReceive.playUrl);
        } else if (Live.MediaStatus.FAILED_DUE_TO_DENIED == feedbackReceive.status) {
            one2oneAttendee = null;
            msg = getContext().getString(R.string.user_refuse_one_to_one_tips);
        } else if (Live.MediaStatus.FAILED_DUE_TO_NETWORK_ISSUES == feedbackReceive.status) {
            one2oneAttendee = null;
            msg = getContext().getString(R.string.failed_one2one_network_issue);
        } else if (Live.MediaStatus.FAILED_DUE_TO_PRIVACY == feedbackReceive.status) {
            one2oneAttendee = null;
            msg = getContext().getString(R.string.failed_one2one_privacy);
        } else {
            one2oneAttendee = null;
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
        } else {
            if (streamRootLayout.indexOfChild(videoStreamView) == frontViewIndex) {
                bringToBack(videoStreamView);
                bringToFront(playView);

            }
        }
        lRightSwitchVbView.setVisibility(View.VISIBLE);
        //streamRootLayout.setDragEnable(false);
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

            classroomEngine.stopLiveTimerObserver();

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

            classroomEngine.stopLiveTimerObserver();
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

    private void handleSyncState(SyncStateReceive syncStateReceive) {

        if (syncStateReceive == null || finishByTeacher)
            return;

        if (Live.LiveSessionState.LIVE.equals(syncStateReceive.from)
                && Live.LiveSessionState.FINISHED.equals(syncStateReceive.to)) {
            Toast.makeText(getContext(), "此课已到下课的时间了，系统已为您自动下课～", Toast.LENGTH_LONG).show();

            handleDestory();
            enterIdle();
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
                case Su.EventType.SYNC_STATE:
                    SyncStateReceive syncStateReceive = (SyncStateReceive) eventReceived.t;
                    handleSyncState(syncStateReceive);
                    break;
                case Su.EventType.JOIN:
                case Su.EventType.LEAVE:
                    requestUpdateMemberCount();
                    break;
                case Su.EventType.TIME_UPDATE:
                    liveTime = eventReceived.value2;
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
