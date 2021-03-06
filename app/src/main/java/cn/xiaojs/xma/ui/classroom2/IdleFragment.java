package cn.xiaojs.xma.ui.classroom2;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom.page.BoardCollaborateFragment;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatAdapter;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.ui.classroom2.live.filter.IFilter;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;
import cn.xiaojs.xma.ui.lesson.xclass.util.ScheduleUtil;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.StringUtil;
import cn.xiaojs.xma.util.ToastUtil;
import cn.xiaojs.xma.util.UIUtils;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/9/25.
 */

public class IdleFragment extends MovieFragment implements ChatAdapter.FetchMoreListener {


    @BindView(R.id.p_bottom_class_name)
    TextView classNameView;
    @BindView(R.id.iv_whiteboard_preview)
    ImageView ivWhiteboardPreview;

    @BindView(R.id.lessontip_lay)
    LinearLayout lessonTipsLayout;
    @BindView(R.id.lessontip_title)
    TextView lessonTipsTitleView;
    @BindView(R.id.lessontip_desc)
    TextView lessonTipsDescView;
    @BindView(R.id.lessontip_desc_time)
    TextView lessonTipsDesctimeView;
    @BindView(R.id.lessontip_start)
    TextView lessonTipsStartView;

    private EventListener.ELIdle idleObserver;

    private Observable<Long> durationObservable;
    private Disposable timerDisposable;

    private int memCount;
    private long reserveliveTime;

    private boolean fromLivingFragment;


    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classroom2_idle, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initControlPanel();

        try {
            updateLessonTips();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initTalkData(this);

        initDefaultBoard();

        idleObserver = classroomEngine.observerIdle(receivedConsumer);
        checkLiveLesson();

        config4Preview();
    }

    @OnClick({R.id.lessontip_start, R.id.lessontip_lay})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.lessontip_start:
                goonLesson();
                break;
            case R.id.lessontip_lay:
                hiddeOrshowControl();
                break;
        }
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK) {
//            switch (requestCode) {
//                case CTLConstant.REQUEST_RECEIVE_WHITEBOARD_DATA:
//                    Bitmap bitmap = data.getParcelableExtra(CTLConstant.EXTRA_BITMAP);
//
//                    if (XiaojsConfig.DEBUG) {
//                        Logger.d("received whiteboard data(%d, %d): %s, ",
//                                bitmap.getWidth(),bitmap.getHeight(), bitmap);
//                    }
//
//                    break;
//            }
//        }
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        classroomEngine.removeLiveTimerObserver(idleObserver);

        if (idleObserver != null) {
            idleObserver.dispose();
        }

        cancelDurationObserver();
    }

    @Override
    public void onFetchMoreRequested() {
        loadTalk();
    }


    @Override
    public void closeMovie() {
        //do nothing
    }

    @Override
    public void changeOrientation() {

        if (needLiveLesson()) {
            showLivingClassDlg();
            return;
        }

        super.changeOrientation();
    }

    @Override
    public void onRotate(int orientation) {
        controlHandleOnRotate(orientation);
        onRotateToInitBoard(orientation);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ivWhiteboardPreview.setImageBitmap(null);
            ivWhiteboardPreview.setVisibility(View.GONE);
        } else {
            ivWhiteboardPreview.setImageBitmap(whiteboardFragment.preview());
            ivWhiteboardPreview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void controlHandleOnRotate(int orientation) {
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                controlClickView.setVisibility(View.GONE);
                controlLand.setVisibility(View.VISIBLE);
                controlPort.setVisibility(View.GONE);
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                    lBottomSessionView.setImageResource(R.drawable.ic_class_chatlist_hide);
                }
                lessonTipsLayout.setVisibility(View.GONE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                controlClickView.setVisibility(View.VISIBLE);
                controlLand.setVisibility(View.GONE);
                controlPort.setVisibility(View.VISIBLE);
                if (recyclerView != null) {
                    recyclerView.setVisibility(View.GONE);
                    lBottomSessionView.setImageResource(R.drawable.ic_class_chatlist_hide);
                }
                if (canShowLessonTips()) {
                    lessonTipsLayout.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public void hiddeOrshowControl() {
        int currentOrient = UIUtils.getCurrentOrientation(getContext());
        switch (currentOrient) {
            case Configuration.ORIENTATION_PORTRAIT:
                if (controlPort.getVisibility() == View.VISIBLE) {
                    controlPort.setVisibility(View.GONE);

                } else {
                    controlPort.setVisibility(View.VISIBLE);
                }
                break;
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
            controlClickView.setVisibility(View.GONE);
        }

        return vis;
    }

    @Override
    public void onClosed() {
        exitSlidePanel();
    }

    @Override
    public void onOpened() {

    }

    @Override
    protected Bitmap doScreenshot() {
        if (isBoardShown()) {
            return whiteboardFragment.preview();
        }
        return null;
    }

    @Override
    public void onTopbackClick(View view, boolean land) {
        back();
    }

    @Override
    public void onStartLiveClick(View view) {
        if (checkVistorPermission())
            return;

        if (needLiveLesson()) {

            toFinishClass();

        } else {
            requestLive();
        }

    }

    @Override
    public void onUpdateMembersCount(int count) {
        super.onUpdateMembersCount(count);
        memCount = count;
        updateRoomInfo();

    }


    public void onStartOrStopLiveClick(View view) {

        if (checkVistorPermission())
            return;
        requestLive();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 白板
    //


    private void initDefaultBoard() {
        /*getChildFragmentManager()
                .beginTransaction()
                .add(R.id.layout_idle_container, BoardCollaborateFragment.createInstance(""))
                .addToBackStack("board_default")
                .commit();*/
        Bitmap preview = whiteboardFragment.preview();
        if (preview == null) {
            ivWhiteboardPreview.setImageDrawable(new ColorDrawable());
        } else {
            ivWhiteboardPreview.setImageBitmap(preview);
        }
        whiteboardFragment.setLastBoardLoadListener(new BoardCollaborateFragment.OnLastBoardLoadListener() {
            @Override
            public void onSuccess(Bitmap preview) {
                ivWhiteboardPreview.setImageBitmap(preview);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 课提示
    //

    private void goonLesson() {
        if (checkVistorPermission())
            return;
        goonLive();
    }

    private void toFinishClass() {
        showProgress(true);
        classroomEngine.finishClass(classroomEngine.getTicket(),
                new APIServiceCallback<ResponseBody>() {
                    @Override
                    public void onSuccess(ResponseBody object) {
                        cancelProgress();
                        configStartOrPausedLiveButton();
                        lessonTipsLayout.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        cancelProgress();
                        ToastUtil.showToast(getContext(), errorMessage);
                    }
                });
    }


    private void checkLiveLesson() {
        if (needLiveLesson()) {

            pTopLiveView.setText("下课");
            pTopLiveView.setVisibility(View.VISIBLE);

            lessonTipsTitleView.setText("该课还未结束");
            lessonTipsDescView.setVisibility(View.GONE);
            lessonTipsDesctimeView.setVisibility(View.GONE);
            lessonTipsStartView.setVisibility(View.VISIBLE);
            lessonTipsLayout.setVisibility(View.VISIBLE);


            pBottomAnimationView.setVisibility(View.VISIBLE);
            Glide.with(getContext())
                    .load(AccountDataManager.getAccountID(getContext()))
                    .transform(new CircleTransform(getContext()))
                    .placeholder(R.drawable.ic_defaultavatar)
                    .error(R.drawable.ic_defaultavatar)
                    .into(pBottomAvatorView);
            pBottomAvatorView.setVisibility(View.VISIBLE);

            pBottomClassnameView.setText(classroomEngine.getRoomTitle());

            pBottomClassOtherView.setVisibility(View.VISIBLE);

            requestUpdateMemberCount();


            showLivingClassDlg();

            classroomEngine.addLiveTimerObserver(idleObserver);
        }
    }

    private void updateRoomInfo() {

        if (XiaojsConfig.DEBUG) {
            Logger.d("the received reserveliveTime:%d", reserveliveTime);
        }

        String livetimeStr = TimeUtil.formatSecondTime(reserveliveTime);
        String otherStr = livetimeStr + "  (" + memCount + "人)";
        pBottomClassOtherView.setText(otherStr);

    }

    public void setFromLivingFragment(boolean fromLivingFragment) {
        this.fromLivingFragment = fromLivingFragment;
    }

    protected void showLivingClassDlg() {

        if (fromLivingFragment)
            return;

        final CommonDialog dialog = new CommonDialog(getContext());
        dialog.setDesc("您有一节直播课还未结束，是否继续上课？");
        dialog.setRightBtnText(R.string.continue_live);
        dialog.setLefBtnText(R.string.cancel);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                goonLesson();
            }
        });

        dialog.show();
    }

    private void cancelDurationObserver() {
        if (timerDisposable != null) {
            timerDisposable.dispose();
            timerDisposable = null;
        }
    }

    private boolean canShowLessonTips() {
        CtlSession.Ctl ctl = classroomEngine.getCtlSession().ctl;
        CtlSession.Cls cls = classroomEngine.getCtlSession().cls;

        if (ctl != null
                && cls != null
                && cls.state.equals(Live.LiveSessionState.PENDING_FOR_LIVE)) {
            return true;
        }

        return false;
    }

    private void updateLessonTips() throws Exception {


        if (canShowLessonTips()) {

            CtlSession.Ctl ctl = classroomEngine.getCtlSession().ctl;

            String d = ctl.startedOn.replace("Z", "UTC");
            Date startDate = ScheduleUtil.simpleUTCFormat.parse(d);
            startTimer(startDate);

            lessonTipsTitleView.setText(ctl.title);

            String leadName = ctl.lead == null ? "" : "   主讲：" + ctl.lead.name;

            lessonTipsDescView.setText(TimeUtil.getFormatDate(startDate, "yyyy.MM.dd HH:mm") + leadName);
            lessonTipsDescView.setVisibility(View.VISIBLE);
            lessonTipsDesctimeView.setVisibility(View.VISIBLE);
            lessonTipsStartView.setVisibility(View.GONE);
            lessonTipsLayout.setVisibility(View.VISIBLE);

            pBottomAnimationView.setVisibility(View.GONE);
            pBottomAvatorView.setVisibility(View.GONE);
            pBottomClassOtherView.setVisibility(View.GONE);


        } else {
            lessonTipsLayout.setVisibility(View.GONE);

            pBottomAnimationView.setVisibility(View.GONE);
            pBottomAvatorView.setVisibility(View.GONE);
            pBottomClassOtherView.setVisibility(View.GONE);
        }

    }

    private void startTimer(final Date startDate) {
        if (durationObservable == null) {
            durationObservable = Observable.interval(
                    1 * 1000, TimeUnit.MILLISECONDS, Schedulers.io());
        }

        cancelDurationObserver();

        long takeTime = startDate.getTime() - System.currentTimeMillis();

        if (takeTime <= 0) {
            lessonTipsDesctimeView.setText("已到上课时间");
            return;
        }


        takeTime = (takeTime / 1000);

        final long finalTakeTime = takeTime;
        timerDisposable = durationObservable.take(takeTime + 2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {

                        if (getActivity() == null) {
                            return;
                        }

                        if (XiaojsConfig.DEBUG) {
                            Logger.d("the total time: %d, now taketime:%d", finalTakeTime, aLong);
                        }

                        if (aLong >= finalTakeTime) {
                            lessonTipsDesctimeView.setText("已到上课时间");
                            return;
                        }

                        long sep = startDate.getTime() - System.currentTimeMillis();

                        String str = TimeUtil.getElapseTimeForShow(sep);

                        Spannable spannable = StringUtil.getSpecialString(
                                "距离上课 " + str, str, getResources().getColor(R.color.main_orange));

                        lessonTipsDesctimeView.setText(spannable);

                    }
                });
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 操作面板
    //

    private void initControlPanel() {

        int orientation = UIUtils.getCurrentOrientation(getContext());
        controlHandleOnRotate(orientation);

        lRightSwitchcameraView.setVisibility(View.GONE);
        lRightSwitchVbView.setVisibility(View.GONE);

        pBottomClassnameView.setText(classroomEngine.getClassTitle());

        configStartOrPausedLiveButton();

        centerOne2oneView.setEnabled(false);

    }

    private void configStartOrPausedLiveButton() {
        if (classroomEngine.canIndividualByState()) {
            startOrStopLiveView.setText("开始直播");
            startOrStopLiveView.setVisibility(View.VISIBLE);
            pTopLiveView.setText("开始直播");
            pTopLiveView.setVisibility(View.VISIBLE);
        } else {

            if (classroomEngine.getLiveMode() == Live.ClassroomMode.TEACHING) {
                startOrStopLiveView.setText("开始上课");
                startOrStopLiveView.setVisibility(View.VISIBLE);

                pTopLiveView.setText("开始上课");
                pTopLiveView.setVisibility(View.VISIBLE);
            } else {
                startOrStopLiveView.setVisibility(View.GONE);
                pTopLiveView.setVisibility(View.GONE);
            }


        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //  响应事件
    //

    private void handleStreamstartted(StreamStartReceive receive) {

        enterPlay();
    }

    private void handleSyncState(SyncStateReceive syncStateReceive) {
        configStartOrPausedLiveButton();
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
                case Su.EventType.STREAMING_STARTED:
                    StreamStartReceive receive = (StreamStartReceive) eventReceived.t;
                    handleStreamstartted(receive);
                    break;
                case Su.EventType.SYNC_CLASS_STATE:
                    try {
                        updateLessonTips();
                        configStartOrPausedLiveButton();

                        if (!needLiveLesson()) {
                            classroomEngine.removeLiveTimerObserver(idleObserver);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case Su.EventType.SYNC_STATE:
                    SyncStateReceive syncStateReceive = (SyncStateReceive) eventReceived.t;
                    handleSyncState(syncStateReceive);
                    break;
                case Su.EventType.TALK:
                    Talk talk = (Talk) eventReceived.t;
                    //TODO fix同一条消息多次回调?
                    handleReceivedMsg(talk);
                    break;
                case Su.EventType.JOIN:
                case Su.EventType.LEAVE:
                    requestUpdateMemberCount();
                    break;
                case Su.EventType.TIME_UPDATE:
                    reserveliveTime = eventReceived.value2;
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
