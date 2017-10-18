package cn.xiaojs.xma.ui.classroom2;

import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom.page.BoardCollaborateFragment;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatAdapter;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2017/9/25.
 */

public class IdleFragment extends MovieFragment implements ChatAdapter.FetchMoreListener {


    @BindView(R.id.p_bottom_class_name)
    TextView classNameView;
    @BindView(R.id.iv_whiteboard_preview)
    ImageView ivWhiteboardPreview;

    private EventListener.ELIdle idleObserver;

    BoardCollaborateFragment whiteboardFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        whiteboardFragment = BoardCollaborateFragment.createInstance("");
    }

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

        initTalkData(this);

        initDefaultBoard();
        idleObserver = classroomEngine.observerIdle(receivedConsumer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (idleObserver != null) {
            idleObserver.dispose();
        }
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
    public void onRotate(int orientation) {
        controlHandleOnRotate(orientation);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (whiteboardFragment.isAdded()) {
                getChildFragmentManager().beginTransaction()
                        .attach(whiteboardFragment)
                        .commitAllowingStateLoss();
            } else {
                getChildFragmentManager()
                        .beginTransaction()
                        .add(R.id.layout_idle_container, whiteboardFragment)
                        .commitAllowingStateLoss();
            }
            ivWhiteboardPreview.setImageBitmap(null);
            ivWhiteboardPreview.setVisibility(View.GONE);
        } else {
            if (whiteboardFragment.isAdded() && whiteboardFragment.isInLayout()) {
                getChildFragmentManager().beginTransaction()
                        .detach(whiteboardFragment)
                        .commitAllowingStateLoss();
            }
            ivWhiteboardPreview.setImageBitmap(whiteboardFragment.preview());
            ivWhiteboardPreview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void back() {
        if (!whiteboardFragment.isDetached()) {
            getChildFragmentManager().beginTransaction().
                    detach(whiteboardFragment)
                    .commitAllowingStateLoss();
            return;
        }
        super.back();
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
        requestLive();
    }

    @Override
    public void onNewboardClick(View view) {

    }

    public void onStartOrStopLiveClick(View view) {
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
        ivWhiteboardPreview.setImageDrawable(new ColorDrawable());
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 操作面板
    //

    private void initControlPanel() {

        int changeRequest = getActivity().getRequestedOrientation();
        controlHandleOnRotate(changeRequest);

        lRightSwitchcameraView.setVisibility(View.GONE);
        lTopPhotoView.setVisibility(View.GONE);
        lTopRoominfoView.setVisibility(View.GONE);

        pBottomClassnameView.setText(classroomEngine.getRoomTitle());

        configStartOrPausedLiveButton();

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
