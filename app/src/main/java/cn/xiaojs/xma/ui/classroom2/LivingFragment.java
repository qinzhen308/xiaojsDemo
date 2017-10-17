package cn.xiaojs.xma.ui.classroom2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.qiniu.pili.droid.streaming.StreamingState;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaResponse;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.MediaFeedbackReceive;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import cn.xiaojs.xma.ui.classroom.page.MsgInputFragment;
import cn.xiaojs.xma.ui.classroom2.base.AVFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatAdapter;
import cn.xiaojs.xma.ui.classroom2.chat.ChatLandAdapter;
import cn.xiaojs.xma.ui.classroom2.chat.MessageComparator;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.ui.classroom2.member.ChooseMemberFragment;
import cn.xiaojs.xma.ui.classroom2.widget.CameraPreviewFrameView;
import cn.xiaojs.xma.ui.lesson.xclass.util.RecyclerViewScrollHelper;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.util.ToastUtil;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class LivingFragment extends AVFragment implements ChatAdapter.FetchMoreListener {

    @BindView(R.id.camera_preview)
    CameraPreviewFrameView cameraPreviewFrameView;
    @BindView(R.id.chat_list)
    RecyclerView recyclerView;
    @BindView(R.id.video_play)
    PLVideoTextureView playView;
    @BindView(R.id.video_root)
    FrameLayout videoRootView;
    @BindView(R.id.close_video)
    ImageView closeVideoView;

    private boolean streaming;


    private LiveCriteria liveCriteria;
    private Pagination pagination;
    private int currentPage = 1;

    private ArrayList<TalkItem> messageData;
    private ChatLandAdapter adapter;
    private MessageComparator messageComparator;
    private boolean loading = false;

    private EventListener.ELLiving livingObserver;
    private long lastTimeline = 0;

    private Attendee one2oneAttendee;


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
        initTalkData();

        livingObserver = classroomEngine.observerLiving(receivedConsumer);

        configVideoView(playView);

    }

    @OnClick({R.id.close_video, R.id.overlay_mask})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.close_video:
                stopPlay(true);
                break;
            case R.id.overlay_mask:
                closeVideoView.setVisibility(View.VISIBLE);
                break;

        }
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
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CTLConstant.REQUEST_INPUT_MESSAGE:

                    final Talk talkBean = new Talk();
                    talkBean.from = AccountDataManager.getAccountID(getContext());
                    talkBean.body = new Talk.TalkContent();
                    talkBean.body.text = data.getStringExtra(CTLConstant.EXTRA_INPUT_MESSAGE);
                    talkBean.body.contentType = Communications.ContentType.TEXT;
                    talkBean.time = System.currentTimeMillis();
                    talkBean.to = String.valueOf(Communications.TalkType.OPEN);


                    classroomEngine.sendTalk(talkBean, new EventCallback<TalkResponse>() {
                        @Override
                        public void onSuccess(TalkResponse talkResponse) {
                            if (talkResponse != null) {
                                talkBean.time = talkResponse.time;
                            }
                            handleReceivedMsg(talkBean);
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMessage) {

                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
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
    protected CameraPreviewFrameView createCameraPreview() {
        return cameraPreviewFrameView;
    }


    @Override
    public void onStateChanged(StreamingState streamingState, Object extra) {
        super.onStateChanged(streamingState, extra);
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
    public void onTalkVisibilityClick(View view) {
        int vis = recyclerView.getVisibility() == View.GONE ? View.VISIBLE : View.GONE;
        recyclerView.setVisibility(vis);
    }

    @Override
    public void onInputMessageClick(View view) {
        MsgInputFragment inputFragment = new MsgInputFragment();
        Bundle data = new Bundle();
        data.putInt(CTLConstant.EXTRA_INPUT_FROM, 1);
        inputFragment.setArguments(data);
        inputFragment.setTargetFragment(this, CTLConstant.REQUEST_INPUT_MESSAGE);
        inputFragment.show(getFragmentManager(), "input");
    }

    @Override
    public void onFetchMoreRequested() {
        if (loading) return;

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
    }

    private void initTalkData() {

        messageData = new ArrayList<>();
        messageComparator = new MessageComparator();
        int maxNumOfObjectPerPage = 50;

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatLandAdapter(getContext(), messageData);
        adapter.setAutoFetchMoreSize(8);
        adapter.setPerpageMaxCount(maxNumOfObjectPerPage);
        adapter.setFetchMoreListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
                outRect.bottom = getResources().getDimensionPixelSize(R.dimen.px20);
            }
        });


        pagination = new Pagination();
        pagination.setMaxNumOfObjectsPerPage(maxNumOfObjectPerPage);
        pagination.setPage(currentPage);

        liveCriteria = new LiveCriteria();
        liveCriteria.to = String.valueOf(Communications.TalkType.OPEN);

        loadTalk();
    }


    private void loadTalk() {
        loading = true;
        LiveManager.getTalks(getContext(), classroomEngine.getTicket(),
                liveCriteria, pagination, new APIServiceCallback<CollectionPage<TalkItem>>() {
                    @Override
                    public void onSuccess(CollectionPage<TalkItem> object) {
                        if (object != null && object.objectsOfPage != null
                                && object.objectsOfPage.size() > 0) {
                            handleNewData(object);
                        }

                        loading = false;
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {

                        Toast.makeText(getContext(), "获取消息列表失败", Toast.LENGTH_SHORT).show();

                        loading = false;
                    }
                });
    }

    private void handleNewData(CollectionPage<TalkItem> object) {
        Observable.just(object.objectsOfPage)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doAfterNext(new Consumer<ArrayList<TalkItem>>() {
                    @Override
                    public void accept(ArrayList<TalkItem> talkItems) throws Exception {
                        Collections.sort(talkItems, messageComparator);

                        for (TalkItem item : talkItems) {
                            timeline(item);
                        }
                        messageData.addAll(0, talkItems);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<TalkItem>>() {
                    @Override
                    public void accept(ArrayList<TalkItem> talkItems) throws Exception {

                        if (currentPage == 1) {
                            adapter.notifyDataSetChanged();
                            RecyclerViewScrollHelper rvHelper = new RecyclerViewScrollHelper();
                            rvHelper.smoothMoveToPosition(recyclerView, messageData.size() - 1);
                        } else {
                            adapter.notifyItemRangeInserted(0, talkItems.size());
                            recyclerView.scrollToPosition(talkItems.size());
                        }
                        pagination.setPage(++currentPage);

                        adapter.setFirstLoad(false);

                    }
                });
    }


    private void handleReceivedMsg(Talk talk) {

        TalkItem talkItem = new TalkItem();
        talkItem.time = talk.time;
        talkItem.body = new cn.xiaojs.xma.model.live.TalkItem.TalkContent();
        talkItem.from = new cn.xiaojs.xma.model.live.TalkItem.TalkPerson();
        talkItem.body.text = talk.body.text;
        talkItem.body.contentType = talk.body.contentType;
        talkItem.from.accountId = talk.from;
        //获取名字
        Attendee attendee = classroomEngine.getMember(talk.from);
        talkItem.from.name = attendee == null ? "nil" : attendee.name;

        timeline(talkItem);

        messageData.add(talkItem);
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messageData.size() - 1);

    }

    private void timeline(TalkItem item) {
        long time = item.time;
        if (lastTimeline == 0
                || time - lastTimeline >= (long) (5 * 60 * 1000)) {
            item.showTime = true;
            lastTimeline = time;
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
        videoRootView.setVisibility(View.VISIBLE);

        playView.setVideoPath(classroomEngine.getPlayUrl());
        playView.start();
    }


    private void stopPlay(boolean send) {

        if (send) {
            sendCloseMedia();
        }

        playView.pause();
        videoRootView.setVisibility(View.GONE);
        closeVideoView.setVisibility(View.GONE);
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
            }
        }
    };

}
