package cn.xiaojs.xma.ui.classroom2;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom2.base.PlayerFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatAdapter;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.Common3Dialog;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.UIUtils;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.observables.GroupedObservable;


/**
 * Created by maxiaobao on 2017/9/18.
 */

public class PlaybackFragment extends PlayerFragment implements ChatAdapter.FetchMoreListener {

    @BindView(R.id.exo_play_root)
    ConstraintLayout exoPlayLayout;
    @BindView(R.id.control_bg)
    View bottomControlBgView;
    @BindView(R.id.right_btn)
    ImageView orientBtn;
    @BindView(R.id.exo_play)
    TextView exoPlayView;
    @BindView(R.id.exo_pause)
    TextView exoPauseView;


    private int landPaddingSize;
    private EventListener.ELIdle idleObserver;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LibDoc doc = (LibDoc) getArguments().getSerializable(PlayerFragment.EXTRA_OBJECT);
        if (doc !=null) {
            exoPlayView.setText(doc.name);
            exoPauseView.setText(doc.name);
        }

        landPaddingSize = getResources().getDimensionPixelSize(R.dimen.px208);
        initControlPanel();
        initTalkData(this);

        idleObserver = classroomEngine.observerIdle(receivedConsumer);

        config4Preview();
    }

    @OnClick({R.id.left_btn, R.id.right_btn})
    void onPlayControlViewClick(View view) {
        switch (view.getId()) {
            case R.id.left_btn:
                if (!TextUtils.isEmpty(classroomEngine.getPlayUrl())) {
                    enterPlay();
                } else {
                    enterIdle();
                }
                break;
            case R.id.right_btn:
                changeOrientation();
                break;
        }
    }

    @Override
    protected Bitmap doScreenshot() {
        if(isBoardShown()){
            return whiteboardFragment.preview();
        }
        return getBitmap();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(idleObserver != null) {
            idleObserver.dispose();
        }
    }

    @Override
    public void closeMovie() {

    }

    @Override
    public void onTopbackClick(View view, boolean land) {
        back();
    }

    @Override
    public void onStartLiveClick(View view) {
        if (checkVistorPermission())
            return;
        requestLive();
    }


    public void onStartOrStopLiveClick(View view) {

        if (checkVistorPermission())
            return;

        requestLive();
    }

    @Override
    public void onClosed() {
        exitSlidePanel();
    }

    @Override
    public void onOpened() {

    }

    @Override
    public void onRotate(int orientation) {
        super.onRotate(orientation);
        controlHandleOnRotate(orientation);
    }

    @Override
    protected void controlHandleOnRotate(int orientation) {
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                controlPort.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                controlLand.setVisibility(View.VISIBLE);

                exoPlayLayout.setPadding(landPaddingSize, 0, landPaddingSize, 0);
                bottomControlBgView.setBackgroundDrawable(
                        getResources().getDrawable(R.drawable.exo_player_control_land_rect_bg));

                orientBtn.setVisibility(View.GONE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                controlLand.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                controlPort.setVisibility(View.VISIBLE);

                pBottomAvatorView.setVisibility(View.GONE);
                pBottomClassnameView.setVisibility(View.GONE);
                pBottomBgView.setVisibility(View.GONE);
                pBottomOrientView.setVisibility(View.GONE);

                exoPlayLayout.setPadding(0, 0, 0, 0);
                bottomControlBgView.setBackgroundColor(
                        getResources().getColor(R.color.black_opacity_60));

                orientBtn.setVisibility(View.VISIBLE);

                break;
        }
    }

    @Override
    public void onVisibilityChange(int visibility) {
        super.onVisibilityChange(visibility);

        int orientation = UIUtils.getCurrentOrientation(getContext());
        switch (orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                controlPort.setVisibility(visibility);
                break;
            default:
                controlLand.setVisibility(visibility);
                if (centerPanelView.getVisibility() == View.VISIBLE) {
                    showOrHiddenCenterPanel();
                    return;
                }

                break;

        }

    }

    private void initControlPanel() {

        int orientation = UIUtils.getCurrentOrientation(getContext());
        controlHandleOnRotate(orientation);

        lTopRoominfoView.setVisibility(View.GONE);
        lTopPhotoView.setVisibility(View.GONE);
        lRightSwitchcameraView.setVisibility(View.GONE);
        lRightSwitchVbView.setVisibility(View.GONE);

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



    @Override
    public void onFetchMoreRequested() {
        loadTalk();
    }

    private void showClassBeginTips(Attendee attendee) {

        final Common3Dialog tipsDialog = new Common3Dialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.layout_classroom2_dlg_tips_stopped_live, null);

        tipsDialog.setCancelable(false);
        tipsDialog.setCanceledOnTouchOutside(false);

        ImageView avatorView = (ImageView) view.findViewById(R.id.avator);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView tipsView = (TextView) view.findViewById(R.id.tips);
        Button okBtn = (Button) view.findViewById(R.id.ok_btn);

        String avatorUrl = Account.getAvatar(attendee.accountId, avatorView.getMeasuredWidth());
        Glide.with(getContext())
                .load(avatorUrl)
                .transform(new CircleTransform(getContext()))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(avatorView);


        titleView.setText(attendee.name + "老师开始上课了");
        tipsView.setText("同学，别再开小差了噢～");
        okBtn.setText("点击听课");

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();
                enterPlay();
            }
        });

        tipsDialog.needCloseBtn(false);

        tipsDialog.setCustomView(view);
        tipsDialog.show();
    }

    private void showLivingTips(Attendee attendee) {

        final Common3Dialog tipsDialog = new Common3Dialog(getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.layout_classroom2_dlg_tips_stopped_live, null);

        tipsDialog.setCancelable(false);
        tipsDialog.setCanceledOnTouchOutside(false);
        tipsDialog.setRootBackgroud(android.R.color.transparent);

        LinearLayout rootLay = (LinearLayout) view.findViewById(R.id.root_lay);
        ImageView avatorView = (ImageView) view.findViewById(R.id.avator);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        TextView tipsView = (TextView) view.findViewById(R.id.tips);
        Button okBtn = (Button) view.findViewById(R.id.ok_btn);

        String avatorUrl = Account.getAvatar(attendee.accountId, avatorView.getMeasuredWidth());
        Glide.with(getContext())
                .load(avatorUrl)
                .transform(new CircleTransform(getContext()))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(avatorView);


        rootLay.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white_rect_coner));
        titleView.setText(attendee.name + "正在直播");
        tipsView.setText("人都到齐了，就差你了～");
        okBtn.setText("点击观看");

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipsDialog.dismiss();
                enterPlay();
            }
        });

        tipsDialog.needCloseBtn(true);
        tipsDialog.setCustomView(view);
        tipsDialog.show();

    }


    private void handleStreamstartted(StreamStartReceive receive) {

        switch (receive.streamType) {
            case Live.StreamType.LIVE:
                if (receive == null)
                    return;

                Attendee attend = classroomEngine.getMember(receive.claimedBy);
                if (attend == null)
                    return;
                showClassBeginTips(attend);

                break;
            case Live.StreamType.INDIVIDUAL:

                if (receive == null)
                    return;

                Attendee attendee = classroomEngine.getMember(receive.claimedBy);
                if (attendee == null)
                    return;

                showLivingTips(attendee);
                break;
        }
    }

    private void handleSyncState(SyncStateReceive syncStateReceive) {
        configStartOrPausedLiveButton();
    }


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
            }
        }
    };
}
