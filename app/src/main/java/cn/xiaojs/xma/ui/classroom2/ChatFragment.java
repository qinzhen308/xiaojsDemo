package cn.xiaojs.xma.ui.classroom2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.CommunicationManager;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import cn.xiaojs.xma.ui.classroom.main.ClassroomController;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.page.MsgInputFragment;
import cn.xiaojs.xma.ui.classroom2.chat.ChatAdapter;
import cn.xiaojs.xma.ui.classroom2.chat.MessageComparator;
import cn.xiaojs.xma.ui.classroom2.chat.ProfileFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.ui.classroom2.material.DatabaseFragment;
import cn.xiaojs.xma.ui.classroom2.member.MemberListFragment;
import cn.xiaojs.xma.ui.classroom2.schedule.ScheduleFragment;
import cn.xiaojs.xma.ui.lesson.xclass.util.RecyclerViewScrollHelper;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by maxiaobao on 2017/9/25.
 */

public class ChatFragment extends Fragment implements ChatAdapter.FetchMoreListener{

    @BindView(R.id.chat_list)
    RecyclerView recyclerView;

    private ClassroomEngine classroomEngine;
    private LiveCriteria liveCriteria;
    private Pagination pagination;
    private int currentPage = 1;

    private ArrayList<TalkItem> messageData;
    private ChatAdapter adapter;
    private MessageComparator messageComparator;
    private boolean loading = false;

    private EventListener.ELTalk talkObserver;
    private long lastTimeline = 0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classroom2_chat, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.bottom_input, R.id.bottom_members, R.id.bottom_database, R.id.bottom_schedule})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.bottom_input:
                popInput();
                break;
            case R.id.bottom_members:               //教室成员
                popMembers();
                break;
            case R.id.bottom_database:              //资料库
                popDatabase();
                break;
            case R.id.bottom_schedule:              //课表
                popClassSchedule();
                break;
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        classroomEngine = ClassroomEngine.getEngine();
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

        liveCriteria = new LiveCriteria();
        liveCriteria.to = classroomEngine.getCtlSession().cls.id;
        liveCriteria.type = Communications.TalkType.OPEN;

        loadData();

        talkObserver = classroomEngine.observerTalk(receivedConsumer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (talkObserver != null) {
            talkObserver.dispose();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
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

                            Toast.makeText(getContext(),errorMessage,Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;
            }
        }

    }

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
                    //TODO fix同一条消息多次回调?
                    handleReceivedMsg(talk);
                    break;
            }
        }
    };


    private void loadData() {
        loading = true;
        CommunicationManager.getTalks(getContext(),
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

                        if (getActivity() == null) {
                            return;
                        }

                        if (currentPage == 1) {
                            adapter.notifyDataSetChanged();
                            RecyclerViewScrollHelper rvHelper = new RecyclerViewScrollHelper();
                            rvHelper.smoothMoveToPosition(recyclerView, messageData.size()-1);
                        }else {
                            adapter.notifyItemRangeInserted(0,talkItems.size());
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
        talkItem.from.name = attendee == null? "nil" : attendee.name;

        timeline(talkItem);

        messageData.add(talkItem);
        adapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(messageData.size()-1);

    }

    private void timeline(TalkItem item) {
        long time = item.time;
        if (lastTimeline == 0
                || time - lastTimeline >= (long) (5 * 60 * 1000)) {
            item.showTime = true;
            lastTimeline = time;
        }

    }

    private void popInput() {

        MsgInputFragment inputFragment = new MsgInputFragment();
        Bundle data = new Bundle();
        data.putInt(CTLConstant.EXTRA_INPUT_FROM, 1);
        inputFragment.setArguments(data);
        inputFragment.setTargetFragment(this, CTLConstant.REQUEST_INPUT_MESSAGE);
        inputFragment.show(getFragmentManager(), "input");
    }


    private void popMembers() {
        MemberListFragment memberListfragment = new MemberListFragment();
        memberListfragment.show(getFragmentManager(), "member");
    }

    private void popDatabase() {
        DatabaseFragment databaseFragment = new DatabaseFragment();
        databaseFragment.show(getFragmentManager(), "database");

    }

    private void popClassSchedule(){
        /*ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.show(getFragmentManager(), "profile");*/
        ScheduleFragment.createInstance("").show(getFragmentManager(),"schedule");
    }


}
