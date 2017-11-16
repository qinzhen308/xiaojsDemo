package cn.xiaojs.xma.ui.classroom2.chat;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsApplication;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.CommunicationManager;
import cn.xiaojs.xma.data.XMSManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.data.api.socket.xms.XMSEventObservable;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.ReadTalk;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom2.base.BaseDialogFragment;
import cn.xiaojs.xma.ui.conversation2.ConversationType;
import cn.xiaojs.xma.ui.lesson.xclass.util.RecyclerViewScrollHelper;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.ui.widget.SpecialEditText;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/9/25.
 */

public abstract class ChatSessionFragment extends BaseDialogFragment implements ChatAdapter.FetchMoreListener {

    @BindView(R.id.title)
    TextView titleView;
    @BindView(R.id.back_btn)
    TextView backView;
    @BindView(R.id.more_btn)
    ImageView moreView;

    @BindView(R.id.chat_list)
    RecyclerView recyclerView;
    @BindView(R.id.msg_input)
    SpecialEditText inputView;
    @BindView(R.id.bottom_line)
    View bottomlineView;
    @BindView(R.id.send_btn)
    Button sendBtnView;

    private LiveCriteria liveCriteria;
    private Pagination pagination;
    private int currentPage = 1;

    private ArrayList<TalkItem> messageData;
    private ChatAdapter adapter;
    private MessageComparator messageComparator;
    private boolean loading = false;
    private long lastTimeline = 0;


    public abstract LiveCriteria createLiveCriteria();

    private Disposable eventDisposable;

    protected String titleStr = "聊天";

    private DataProvider dataProvider;

    private XMSManager xmsManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataProvider = DataProvider.getProvider(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);


        View view = inflater.inflate(R.layout.fragment_classroom2_chat_session, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.back_btn, R.id.more_btn, R.id.send_btn})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                dismiss();
                break;
            case R.id.more_btn:
                showMoreMenu();
                break;
            case R.id.send_btn:
                String bodyStr = inputView.getText().toString();
                sendTalk(bodyStr);
                break;
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        liveCriteria = createLiveCriteria();

        xmsManager = XMSManager.getXmsManager(getContext());
        if (xmsManager != null) {
            xmsManager.addOpenedSession(liveCriteria.to);
        }

        titleView.setText(titleStr);

        messageData = new ArrayList<>();
        messageComparator = new MessageComparator();


        int maxNumOfObjectPerPage = 50;

        GridLayoutManager layoutManager =
                new GridLayoutManager(getContext(), 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(getContext(), messageData);
        adapter.setAutoFetchMoreSize(8);
        adapter.setPerpageMaxCount(maxNumOfObjectPerPage);
        adapter.setFetchMoreListener(this);
        recyclerView.setAdapter(adapter);


        pagination = new Pagination();
        pagination.setMaxNumOfObjectsPerPage(maxNumOfObjectPerPage);
        pagination.setPage(currentPage);

        loadData();

        eventDisposable = XMSEventObservable.observeChatSession(getContext(), receivedConsumer);

        updateUnread();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (eventDisposable != null) {
            eventDisposable.dispose();
            eventDisposable = null;
        }

        if (xmsManager != null) {
            xmsManager.closeSession(liveCriteria.to);
        }

    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK && data != null) {
//            switch (requestCode) {
//                case ClassroomController.REQUEST_INPUT:
//                    break;
//            }
//        }
//
//    }

    @Override
    public void onFetchMoreRequested() {
        if (loading) return;

        loadData();
    }

    private Consumer<EventReceived> receivedConsumer = new Consumer<EventReceived>() {
        @Override
        public void accept(EventReceived eventReceived) throws Exception {

            if (XiaojsConfig.DEBUG) {
                Logger.d("receivedConsumer talk .....");
            }

            switch (eventReceived.eventType) {
                case Su.EventType.TALK:
                    Talk talk = (Talk) eventReceived.t;

                    if (talk.type != liveCriteria.type) {
                        return;
                    }

                    if (liveCriteria.type == Communications.TalkType.PEER
                            && talk.from.equals(liveCriteria.to)) {
                        handleReceivedMsg(false, talk);
                    }

                    if (liveCriteria.type == Communications.TalkType.OPEN
                            && !TextUtils.isEmpty(talk.to)
                            && talk.to.equals(liveCriteria.to)) {
                        handleReceivedMsg(false, talk);
                    }
                    break;
            }
        }
    };

    protected void sendTalk(String bodyStr) {

        if (TextUtils.isEmpty(bodyStr))
            return;

        final Talk talkBean = new Talk();
        talkBean.type = liveCriteria.type;
        talkBean.from = AccountDataManager.getAccountID(getContext());
        talkBean.body = new Talk.TalkContent();
        talkBean.body.text = bodyStr;
        talkBean.body.contentType = Communications.ContentType.TEXT;
        talkBean.time = System.currentTimeMillis();
        talkBean.to = liveCriteria.to;
        talkBean.name = titleView.getText().toString();


        XMSManager.sendTalk(getContext(), true, talkBean, new EventCallback<TalkResponse>() {
            @Override
            public void onSuccess(TalkResponse talkResponse) {
                if (talkResponse != null) {
                    talkBean.time = talkResponse.time;
                }
                inputView.setText("");

                handleReceivedMsg(true, talkBean);

            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {

                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadData() {
        loading = true;
        CommunicationManager.getTalks(getContext(),
                liveCriteria, pagination, new APIServiceCallback<CollectionPage<TalkItem>>() {
                    @Override
                    public void onSuccess(CollectionPage<TalkItem> object) {

                        if (object != null && object.objectsOfPage != null
                                && object.objectsOfPage.size() > 0) {

                            handleNewData(object);
                        }else {
                            if (currentPage == 1) {
                                handleChatSessionOpened(null);
                            }
                        }

                        loading = false;
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {

                        Toast.makeText(getContext(), "获取消息列表失败", Toast.LENGTH_SHORT).show();

                        if (currentPage == 1) {
                            handleChatSessionOpened(null);
                        }

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

                        if (getActivity() == null) {
                            return;
                        }

                        if (currentPage == 1) {
                            adapter.notifyDataSetChanged();
                            //RecyclerViewScrollHelper rvHelper = new RecyclerViewScrollHelper();
                            //rvHelper.smoothMoveToPosition(recyclerView, messageData.size() - 1);

                            int lastIndex = messageData.size() - 1;

                            if (lastIndex >= 0) {
                                recyclerView.scrollToPosition(lastIndex);
                                handleChatSessionOpened(messageData.get(lastIndex));
                            }


                        } else {
                            adapter.notifyItemRangeInserted(0, talkItems.size());
                            recyclerView.scrollToPosition(talkItems.size());
                        }
                        pagination.setPage(++currentPage);

                        adapter.setFirstLoad(false);

                    }
                });
    }

    public void addTipsItem(TalkItem talkItem) {
        if (talkItem == null)
            return;

        timeline(talkItem);
        messageData.add(talkItem);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messageData.size() - 1);
    }


    protected void handleReceivedMsg(boolean send, Talk talk) {

        TalkItem talkItem = new TalkItem();
        talkItem.time = talk.time;
        talkItem.body = new TalkItem.TalkContent();
        talkItem.from = new TalkItem.TalkPerson();
        talkItem.body.text = talk.body.text;
        talkItem.body.contentType = talk.body.contentType;
        talkItem.from.accountId = talk.from;
        talkItem.from.name = talk.name;

        timeline(talkItem);

        messageData.add(talkItem);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(messageData.size() - 1);

        if (send) {
            updateConveration(talkItem);
        } else {
            sendReadTalk(talk);
        }


    }

    private void updateUnread() {
        dataProvider.updateConversationUnread(liveCriteria.to, 0);
    }

    private void sendReadTalk(Talk talk) {
        ReadTalk readTalk = new ReadTalk();
        readTalk.type = liveCriteria.type;
        readTalk.from = talk.from;

        if (liveCriteria.type == Communications.TalkType.OPEN) {
            readTalk.to = AccountDataManager.getAccountID(getContext());
        }

        readTalk.stime = talk.stime;

        XMSManager.sendReadTalk(getContext(), readTalk, null);
        updateUnread();
    }

    private void updateConveration(TalkItem talkItem) {
        Contact contact = new Contact();

        if (TextUtils.isEmpty(talkItem.from.name)) {
            talkItem.from.name = "nil";
        }

        contact.id = liveCriteria.to;
        contact.name = talkItem.from.name;
        contact.title = talkItem.from.name;
        contact.lastMessage = talkItem.body.text;
        contact.lastTalked = talkItem.time;
        contact.unread = 0;

        dataProvider.moveOrInsertConversation(contact);
    }


    private void timeline(TalkItem item) {
        long time = item.time;
        if (lastTimeline == 0
                || time - lastTimeline >= (long) (5 * 60 * 1000)) {
            item.showTime = true;
            lastTimeline = time;
        }

    }

    private void handleChatSessionOpened(TalkItem talkItem) {

        String lastMessage = "";
        long lastTalked = System.currentTimeMillis();

        if (talkItem !=null) {
            lastTalked = talkItem.time;
            if (talkItem.body.contentType == Communications.ContentType.TEXT) {
                lastMessage = talkItem.body.text;
            }else if (talkItem.body.contentType == Communications.ContentType.STYLUS) {
                lastMessage = "【图片】";
            }
        }

        Contact contact = new Contact();
        contact.title = titleStr;
        contact.name = titleStr;
        contact.id = liveCriteria.to;
        contact.subtype = ConversationType.getConversationType(liveCriteria.type);
        contact.lastMessage = lastMessage;
        contact.lastTalked = lastTalked;
        dataProvider.conversationOpened(contact);

    }


    public void showMoreMenu() {

    }

    public void hiddenSendBar() {
        bottomlineView.setVisibility(View.GONE);
        inputView.setVisibility(View.GONE);
        sendBtnView.setVisibility(View.GONE);
    }

    public void hiddenTitleBar() {
        titleView.setVisibility(View.GONE);
        backView.setVisibility(View.GONE);
        moreView.setVisibility(View.GONE);
    }


}
