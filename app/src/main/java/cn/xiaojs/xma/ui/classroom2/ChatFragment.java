package cn.xiaojs.xma.ui.classroom2;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom.page.MsgInputFragment;
import cn.xiaojs.xma.ui.classroom2.chat.GroupSessionFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import io.reactivex.functions.Consumer;


/**
 * Created by maxiaobao on 2017/9/25.
 */

public class ChatFragment extends GroupSessionFragment {

    private EventListener.ELMember eventListener;
    private ClassroomEngine classroomEngine;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        hiddenTitleBar();
        hiddenSendBar();

        classroomEngine = ClassroomEngine.getEngine();

        eventListener = ClassroomEngine.getEngine().observerMember(receivedConsumer);

        IntentFilter intentFilter = new IntentFilter(CTLConstant.ACTION_SEND_TALK);
        getContext().registerReceiver(messageReceiver,intentFilter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case CTLConstant.REQUEST_INPUT_MESSAGE:
                    String bodyStr = data.getStringExtra(CTLConstant.EXTRA_INPUT_MESSAGE);
                    sendTalk(bodyStr);
                    break;
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (messageReceiver !=null) {
            getContext().unregisterReceiver(messageReceiver);
        }

        if (eventListener != null) {
            eventListener.dispose();
            eventListener = null;
        }


    }

    public void popInput() {

        MsgInputFragment inputFragment = new MsgInputFragment();
        Bundle data = new Bundle();
        data.putInt(CTLConstant.EXTRA_INPUT_FROM, 1);
        inputFragment.setArguments(data);
        inputFragment.setTargetFragment(this, CTLConstant.REQUEST_INPUT_MESSAGE);
        inputFragment.show(getFragmentManager(), "input");
    }


    private TalkItem createJoinTipsItem(Talk talk) {

        TalkItem talkItem = new TalkItem();
        talkItem.time = System.currentTimeMillis();
        talkItem.tips = talk.name + "加入教室";

        return talkItem;

    }

    private TalkItem createLeaveTipsItem(Talk talk) {

        TalkItem talkItem = new TalkItem();
        talkItem.time = System.currentTimeMillis();
        talkItem.tips = talk.name + "离开教室";

        return talkItem;

    }

    private Consumer<EventReceived> receivedConsumer = new Consumer<EventReceived>() {
        @Override
        public void accept(EventReceived eventReceived) throws Exception {

            switch (eventReceived.eventType) {
                case Su.EventType.JOIN:
                    //避免与系统消息发生重复，所以注释掉
//                    Talk talk = (Talk) eventReceived.t;
//                    if (talk != null && !TextUtils.isEmpty(talk.name)) {
//                        addTipsItem(createJoinTipsItem(talk));
//                    }

                    break;
                case Su.EventType.LEAVE:
                    //避免与系统消息发生重复，所以注释掉
//                    Talk talkl = (Talk) eventReceived.t;
//                    if (talkl != null && !TextUtils.isEmpty(talkl.accountId)) {
//
//                        String name = talkl.name;
//                        if (TextUtils.isEmpty(name)) {
//                            Attendee attendee = classroomEngine.getMember(talkl.accountId);
//                            if (attendee !=null) {
//                                name = attendee.name;
//                            }
//                        }
//
//                        talkl.name = name;
//                        addTipsItem(createLeaveTipsItem(talkl));
//                    }
                    break;
            }
        }
    };

    private BroadcastReceiver messageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (CTLConstant.ACTION_SEND_TALK.equals(action)) {

                Talk talk = (Talk) intent.getSerializableExtra(CTLConstant.EXTRA_TALK);
                if (talk != null) {
                    handleReceivedMsg(true, talk);
                }

            }
        }
    };


}
