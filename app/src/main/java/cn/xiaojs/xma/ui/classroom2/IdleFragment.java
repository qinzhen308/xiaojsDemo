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
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.ui.classroom.page.BoardCollaborateFragment;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2017/9/25.
 */

public class IdleFragment extends MovieFragment {


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
    public void closeMovie() {
        //do nothing
    }

    @Override
    public void onRotate(int orientation) {
        controlHandleOnRotate(orientation);
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            ivWhiteboardPreview.setImageBitmap(null);
            ivWhiteboardPreview.setVisibility(View.GONE);
            getChildFragmentManager()
                    .beginTransaction()
                    .add(R.id.layout_idle_container, whiteboardFragment)
                    .addToBackStack("board_default")
                    .commit();
        } else {
            if (whiteboardFragment.isAdded() && whiteboardFragment.isInLayout()) {
                getChildFragmentManager().popBackStack();
            }
            ivWhiteboardPreview.setImageBitmap(whiteboardFragment.preview());
            ivWhiteboardPreview.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void back() {
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
        if (classroomEngine.canIndividualByState()) {
            requestLive();
        } else {

            if (classroomEngine.getLiveMode() == Live.ClassroomMode.TEACHING) {

                //开始上课


            }
        }

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

        controlLand.setVisibility(View.GONE);
        pBottomClassnameView.setText(classroomEngine.getRoomTitle());

        if (classroomEngine.canIndividualByState()) {
            startOrStopLiveView.setText("开始直播");
            startOrStopLiveView.setVisibility(View.VISIBLE);
        } else {

            if (classroomEngine.getLiveMode() == Live.ClassroomMode.TEACHING) {
                startOrStopLiveView.setText("开始上课");
                startOrStopLiveView.setVisibility(View.VISIBLE);
            } else {
                startOrStopLiveView.setVisibility(View.GONE);
            }


        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 开始上课
    //

    private void requestBeginClass() {
        showProgress(true);
        classroomEngine.beginClass(classroomEngine.getTicket(), new APIServiceCallback<ClassResponse>() {
            @Override
            public void onSuccess(ClassResponse object) {
                cancelProgress();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
            }
        });
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
                    break;
                case Su.EventType.SYNC_CLASS_STATE:
                    break;
                case Su.EventType.SYNC_STATE:

                    break;
            }
        }
    };
}
