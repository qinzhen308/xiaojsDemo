package cn.xiaojs.xma.ui.classroom.talk;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/5/10
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.AbsChatAdapter;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.model.socket.room.TalkResponse;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom2.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.EventListener;

public class TalkManager implements EventListener {
    public final static int TYPE_FULL_SCREEN_MUlTI_TAlk = 1 << 1; //全屏模式下的教室交流
    public final static int TYPE_MSG_MUlTI_TAlk = 1 << 2; //消息模式下的教室交流
    //public final static int TYPE_MULTI_TALK = TYPE_MSG_MUlTI_TAlk | TYPE_FULL_SCREEN_MUlTI_TAlk;
    public final static int TYPE_PEER_TALK = 1 << 3;
    public final static int TYPE_TEACHING_TALK = 1 << 4;

    private static TalkManager mInstance;

    //消息模式下的教室交流Adapter
    private TalkMsgAdapter mMsgMultiTalkAdapter;
    //教学组Adapter
    private TalkMsgAdapter mTeachingTalkAdapter;
    //一对一交流Adapter
    private Map<String, TalkMsgAdapter> mPeerTalkMsgAdapterMap;
    //全屏模式下教室交流Adapter
    private FullScreenTalkMsgAdapter mFullScreenTalkMsgAdapter;

    private List<OnTalkMsgReceived> mOnTalkMsgReceiveListeners;

    private String mTicket;
    private String mMyAccountId;

    private final Object MSG_COUNT_LOCK = new Object();
    private List<TalkItem> mReceivedTalkMsg;
    private Map<String, Integer> mPeerTalkUnreadMsgCountMap; //个人未读消息
    private int mMultiTalkUnreadMsgCount; //教室交流未读消息
    private String mPeekTalkingAccount; //当account不为null时，是一对一聊天, 当为null时，就是教室交流
    private boolean mFullscreenMultiTalkVisible = true; //全屏模式时教室交流是否显示

    private TalkManager() {
        mPeerTalkMsgAdapterMap = new HashMap<String, TalkMsgAdapter>();
        mOnTalkMsgReceiveListeners = new ArrayList<OnTalkMsgReceived>();
        mPeerTalkUnreadMsgCountMap = new HashMap<String, Integer>();
        mReceivedTalkMsg = new ArrayList<TalkItem>();
    }

    public void init(Context context, String ticket) {

        ClassroomEngine.getEngine().addEvenListener(this);

        mTicket = ticket;
        mMyAccountId = AccountDataManager.getAccountID(context);
    }

    public static synchronized TalkManager getInstance() {
        if (mInstance == null) {
            mInstance = new TalkManager();
        }

        return mInstance;
    }

    /**
     * 获取非一对一的聊天
     */
    public AbsChatAdapter getChatAdapter(Context context, int type, PullToRefreshListView listView) {
        if (type == TYPE_PEER_TALK) {
            throw new IllegalArgumentException("illegal type passed!");
        }

        return getChatAdapter(context, type, null, listView);
    }

    public AbsChatAdapter getChatAdapter(Context context, int type, String accountId, PullToRefreshListView listView) {
        AbsChatAdapter adapter = getAdapter(type, accountId);
        LiveCriteria lveCriteria = new LiveCriteria();
        String ticket = ClassroomEngine.getEngine().getTicket();
        if (adapter == null) {
            lveCriteria.to = String.valueOf(Communications.TalkType.OPEN);
            switch (type) {
                case TYPE_MSG_MUlTI_TAlk:
                    lveCriteria.to = String.valueOf(Communications.TalkType.OPEN);
                    mMsgMultiTalkAdapter = new TalkMsgAdapter(context, ticket, lveCriteria, listView);
                    return mMsgMultiTalkAdapter;
                case TYPE_TEACHING_TALK:
                    lveCriteria.to = String.valueOf(Communications.TalkType.FACULTY);
                    mTeachingTalkAdapter = new TalkMsgAdapter(context, ticket, lveCriteria, listView);
                    return mTeachingTalkAdapter;
                case TYPE_FULL_SCREEN_MUlTI_TAlk:
                    mFullScreenTalkMsgAdapter = new FullScreenTalkMsgAdapter(context, ticket, listView);
                    return mFullScreenTalkMsgAdapter;
                case TYPE_PEER_TALK:
                    return getPeekChatAdapter(context, accountId, ticket, listView);
            }
        }

        return adapter;
    }

    /**
     * 得到一对一的聊天Adapter
     */
    private AbsChatAdapter getPeekChatAdapter(Context context, String accountId, String ticket, PullToRefreshListView listView) {
        if (mPeerTalkMsgAdapterMap.containsKey(accountId)) {
            return mPeerTalkMsgAdapterMap.get(accountId);
        }

        LiveCriteria peerCriteria = new LiveCriteria();
        peerCriteria.to = accountId;
        TalkMsgAdapter adapter = new TalkMsgAdapter(context, ticket, peerCriteria, listView);
        mPeerTalkMsgAdapterMap.put(accountId, adapter);

        return adapter;
    }

    private AbsChatAdapter getAdapter(int type, String accountId) {
        switch (type) {
            case TYPE_MSG_MUlTI_TAlk:
                return mMsgMultiTalkAdapter;
            case TYPE_TEACHING_TALK:
                return mTeachingTalkAdapter;
            case TYPE_FULL_SCREEN_MUlTI_TAlk:
                return mFullScreenTalkMsgAdapter;
            case TYPE_PEER_TALK:
                if (mPeerTalkMsgAdapterMap.containsKey(accountId)) {
                    return mPeerTalkMsgAdapterMap.get(accountId);
                } else {
                    return null;
                }
            default:
                return mMsgMultiTalkAdapter;
        }
    }

    public void release() {

        ClassroomEngine.getEngine().removeEvenListener(this);

        if (mMsgMultiTalkAdapter != null) {
            mMsgMultiTalkAdapter.release();
            mMsgMultiTalkAdapter = null;
        }

        if (mTeachingTalkAdapter != null) {
            mTeachingTalkAdapter.release();
            mTeachingTalkAdapter = null;
        }

        if (mFullScreenTalkMsgAdapter != null) {
            mFullScreenTalkMsgAdapter.release();
            mFullScreenTalkMsgAdapter = null;
        }

        if (mPeerTalkMsgAdapterMap != null) {
            for (Map.Entry<String, TalkMsgAdapter> entry : mPeerTalkMsgAdapterMap.entrySet()) {
                TalkMsgAdapter adapter = entry.getValue();
                if (adapter != null) {
                    adapter.release();
                    adapter = null;
                }
            }
            mPeerTalkMsgAdapterMap.clear();
            mPeerTalkMsgAdapterMap = null;
        }

        if (mOnTalkMsgReceiveListeners != null) {
            mOnTalkMsgReceiveListeners.clear();
            mOnTalkMsgReceiveListeners = null;
        }

        if (mPeerTalkUnreadMsgCountMap != null) {
            mPeerTalkUnreadMsgCountMap.clear();
            mPeerTalkUnreadMsgCountMap = null;
        }

        if (mReceivedTalkMsg != null) {
            mReceivedTalkMsg.clear();
            mReceivedTalkMsg = null;
        }

        resetMultiTalkUnreadMsgCount();

        mInstance = null;
    }


    /**
     * 接收到消息
     */
    @Override
    public void receivedEvent(String event, Object object) {

        if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.TALK).equals(event)) {
            if (object == null)
                return;

            Talk talk = (Talk) object;
            //TODO fix同一条消息多次回调?
            handleReceivedMsg(talk);
        }
    }

    private Handler receivedHandler  = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            notifyMsgChanged(true, msg.arg1, (TalkItem) msg.obj);

        }
    };

    /**
     * 更新消息列表
     */
    private void handleReceivedMsg(Talk talk) {

        try {
            if (talk == null) {
                return;
            }

            TalkItem talkItem = new TalkItem();
            talkItem.time = talk.time;
            talkItem.body = new cn.xiaojs.xma.model.live.TalkItem.TalkContent();
            talkItem.from = new cn.xiaojs.xma.model.live.TalkItem.TalkPerson();
            talkItem.body.text = talk.body.text;
            talkItem.body.contentType = talk.body.contentType;
            talkItem.from.accountId = talk.from;
            talkItem.from.name = ClassroomBusiness.getNameByAccountId(talk.from);

            String account = talk.to != null ? talk.to : talk.from;
            int type = addTalkItemToAdapter(talkItem, account);
            //update unread msg count
            updateUnreadCount(type, talkItem);
            //notify msg change


            Message message = new Message();
            message.arg1 = type;
            message.obj = talkItem;
            receivedHandler.sendMessageDelayed(message,24);

        } catch (Exception e) {

            e.printStackTrace();
        }
    }


    public void registerMsgReceiveListener(OnTalkMsgReceived listener) {
        if (listener != null && mOnTalkMsgReceiveListeners != null) {
            mOnTalkMsgReceiveListeners.add(listener);
        }
    }

    public void unregisterMsgReceiveListener(OnTalkMsgReceived listener) {
        if (listener != null && mOnTalkMsgReceiveListeners != null) {
            mOnTalkMsgReceiveListeners.remove(listener);
        }
    }

    public void notifyMsgChanged(boolean receive, int criteria, TalkItem talkItem) {
        if (mOnTalkMsgReceiveListeners != null) {
            for (OnTalkMsgReceived listener : mOnTalkMsgReceiveListeners) {
                listener.onMsgChanged(receive, criteria, talkItem);
            }
        }
    }

    //==============================send img====================================

    /**
     * 发送图片(教室交流:消息模式和全屏模式)
     */
    public void sendImg(String content) {
        sendMsg(Communications.ContentType.STYLUS, null, Communications.TalkType.OPEN, content);
    }

    /**
     * 发送图片(一对一)
     */
    public void sendImg(String accountId, String content) {
        sendMsg(Communications.ContentType.STYLUS, accountId, Communications.TalkType.PEER, content);
    }

    /**
     * 发送图片
     */
    public void sendImg(String accountId, int criteria, String content) {
        sendMsg(Communications.ContentType.STYLUS, accountId, criteria, content);
    }
    //==============================send img====================================


    //==============================send text====================================

    /**
     * 发送文本(教室交流:消息模式和全屏模式)
     */
    public void sendText(String content) {
        sendMsg(Communications.ContentType.TEXT, null, Communications.TalkType.OPEN, content);
    }

    /**
     * 发送文本(一对一)
     */
    public void sendText(String accountId, String content) {
        sendMsg(Communications.ContentType.TEXT, accountId, Communications.TalkType.PEER, 0, content);
    }

    /**
     * 发送文本
     */
    public void sendText(String accountId, int criteria, String content) {
        sendMsg(Communications.ContentType.TEXT, accountId, criteria, content);
    }
    //==============================send text====================================


    public void sendMsg(int contentType, String accountId, final int criteria, String content) {
        sendMsg(contentType, accountId, criteria, 0, content);
    }

    /**
     * 发送消息
     */
    public void sendMsg(int contentType, String accountId, final int criteria, final int talkType, String content) {
        String text = content;
        if (TextUtils.isEmpty(text)) {
            return;
        }

        String to = accountId;
        if (accountId == null) {
            switch (criteria) {
                case Communications.TalkType.OPEN:
                    to = String.valueOf(Communications.TalkType.OPEN);
                    break;
                case Communications.TalkType.FACULTY:
                    to = String.valueOf(Communications.TalkType.FACULTY);
                    break;
                default:
                    to = String.valueOf(Communications.TalkType.OPEN);
                    break;
            }
        } else {
            try {
                to = accountId;
            } catch (Exception e) {

            }
        }

        final TalkItem talkItem = new TalkItem();
        //file myself info
        fillMyselfInfo(talkItem);

        talkItem.body = new TalkItem.TalkContent();
        talkItem.body.text = text;
        talkItem.body.contentType = contentType;
        talkItem.to = to;

        long sendTime = System.currentTimeMillis();
        talkItem.time = sendTime;

        //send socket info
        final Talk talkBean = new Talk();
        talkBean.from = mMyAccountId;
        talkBean.body = new Talk.TalkContent();
        talkBean.body.text = text;
        talkBean.body.contentType = contentType;
        talkBean.time = sendTime;
        talkBean.to = to;

        if (talkBean != null) {
            ClassroomEngine.getEngine().sendTalk(talkBean, new EventCallback<TalkResponse>() {
                @Override
                public void onSuccess(TalkResponse talkResponse) {
                    try {

                        //TODO 现在服务器返回消息的时间是是使用的本地时间戳，如果服务器返回的消息时间戳和本地不一样，需要更新本地的时间
                        //talkItem.time = talkResponse.time;
                        int type = addTalkItemToAdapter(talkItem, talkBean.to);
                        if (type == TYPE_MSG_MUlTI_TAlk || type == TYPE_FULL_SCREEN_MUlTI_TAlk) {
                            notifyMsgChanged(false, talkType, talkItem);
                        } else {
                            notifyMsgChanged(false, type, talkItem);
                        }
                    } catch (Exception e) {

                    }
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {

                    Toast.makeText(ClassroomEngine.getEngine().getContext(),
                            "发送消息失败",
                            Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private int addTalkItemToAdapter(TalkItem talkItem, String accountId) {
        int type = TYPE_PEER_TALK;
        try {
            int criteria = Integer.parseInt(accountId);
            type = getTalkType(criteria);
        } catch (NumberFormatException e) {
            type = TYPE_PEER_TALK;
        }

        //update all adapters
        if (type == TYPE_MSG_MUlTI_TAlk || type == TYPE_FULL_SCREEN_MUlTI_TAlk) {
            //multi talk
            AbsChatAdapter adapter = getAdapter(TYPE_MSG_MUlTI_TAlk, accountId);
            if (adapter != null) {
                adapter.add(talkItem);
            }

            //fullscreen multi talk
            AbsChatAdapter fcAdapter = getAdapter(TYPE_FULL_SCREEN_MUlTI_TAlk, accountId);
            if (fcAdapter != null) {
                fcAdapter.add(talkItem);
            }
        } else {
            AbsChatAdapter adapter = getAdapter(type, accountId);
            if (adapter != null) {
                adapter.add(talkItem);
            }
        }

        return type;
    }

    private int getTalkType(int criteria) {
        int type = TYPE_PEER_TALK;
        switch (criteria) {
            case Communications.TalkType.OPEN:
                type = TYPE_MSG_MUlTI_TAlk;
                break;
            case Communications.TalkType.FACULTY:
                type = TYPE_TEACHING_TALK;
                break;
        }

        return type;
    }

    private void fillMyselfInfo(TalkItem talkItem) {
        talkItem.from = new TalkItem.TalkPerson();
        talkItem.from.accountId = mMyAccountId;
        cn.xiaojs.xma.model.account.User mLoginUser = XiaojsConfig.mLoginUser;
        if (mLoginUser != null) {
            talkItem.from.name = mLoginUser.getName();
            if (mLoginUser.getAccount() != null && mLoginUser.getAccount().getBasic() != null) {
                talkItem.from.avatar = mLoginUser.getAccount().getBasic().getAvatar();
            }
        }
    }

    /**
     * 更新未读消息统计数量
     */
    private void updateUnreadCount(int type, TalkItem talkItem) {
        synchronized (MSG_COUNT_LOCK) {
            if (!mReceivedTalkMsg.contains(talkItem)) {
                mReceivedTalkMsg.add(talkItem);

                if (type == TYPE_PEER_TALK) {
                    setUnreadMsgCount2Adapter(talkItem);
                } else if (type == TYPE_FULL_SCREEN_MUlTI_TAlk || type == TYPE_MSG_MUlTI_TAlk) {
                    if (!TextUtils.isEmpty(mPeekTalkingAccount) || !mFullscreenMultiTalkVisible) {
                        mMultiTalkUnreadMsgCount = mMultiTalkUnreadMsgCount + 1;
                    }
                }
            }
        }
    }

    /**
     * 设置未读消息到适配器
     */
    private void setUnreadMsgCount2Adapter(TalkItem talkItem) {
        //个人聊天的时候
        String accountId = talkItem.from.accountId;
        if (mPeekTalkingAccount != null && mPeekTalkingAccount.equals(accountId)) {
            return;
        }
        if (mPeerTalkUnreadMsgCountMap.containsKey(accountId)) {
            int msgCount = mPeerTalkUnreadMsgCountMap.get(accountId);
            mPeerTalkUnreadMsgCountMap.put(accountId, msgCount + 1);
        } else {
            mPeerTalkUnreadMsgCountMap.put(accountId, 1);
        }

        ArrayList<Attendee> attendees = ContactManager.getInstance().getAttendees().attendees;
        if (attendees != null && !attendees.isEmpty()) {
            for (Attendee attendee : attendees) {
                if (mPeerTalkUnreadMsgCountMap.containsKey(attendee.accountId)) {
                    int count = mPeerTalkUnreadMsgCountMap.get(attendee.accountId);
                    attendee.unReadMsgCount = attendee.unReadMsgCount + count;
                }
            }

            mPeerTalkUnreadMsgCountMap.clear();
        }
    }

    public void resetMultiTalkUnreadMsgCount() {
        mMultiTalkUnreadMsgCount = 0;
    }

    public int getMultiTalkUnreadMsgCount() {
        return mMultiTalkUnreadMsgCount;
    }

    /**
     * 如果消息过多，不建议考虑遍历集合中所有元素，建议维护一个变量值，考虑使用增量变化
     */
    public int getPeerTalkUnreadMsgCount() {
        LiveCollection<Attendee> collection = ContactManager.getInstance().getAttendees();
        if (collection == null) {
            return 0;
        }

        ArrayList<Attendee> attendees = collection.attendees;
        int peerCount = 0;
        if (attendees != null && !attendees.isEmpty()) {
            for (Attendee attendee : attendees) {
                peerCount += attendee.unReadMsgCount;
            }
        }

        return peerCount;
    }

    public int clearPeerTalkUnreadMsgCount(String accountId) {
        if (TextUtils.isEmpty(accountId)) {
            return getPeerTalkUnreadMsgCount();
        }

        LiveCollection<Attendee> collection = ContactManager.getInstance().getAttendees();
        if (collection == null) {
            return 0;
        }

        ArrayList<Attendee> attendees = collection.attendees;
        int peerCount = 0;
        if (attendees != null && !attendees.isEmpty()) {
            for (Attendee attendee : attendees) {
                if (accountId.equals(attendee.accountId)) {
                    attendee.unReadMsgCount = 0;
                }
                peerCount += attendee.unReadMsgCount;
            }
        }

        return peerCount;
    }

    public int getAllUnreadMsgCount() {
        return mMultiTalkUnreadMsgCount + getPeerTalkUnreadMsgCount();
    }

    public void setPeekTalkingAccount(String account) {
        mPeekTalkingAccount = account;
    }

    public String getPeekTalkingAccount() {
        return mPeekTalkingAccount;
    }

    public void setFullscreenMultiTalkVisible(boolean visible) {
        mFullscreenMultiTalkVisible = visible;
    }

    public static interface OnTalkMsgReceived {
        /**
         * @param receive 是接收的消息，还是发送的消息
         */
        public void onMsgChanged(boolean receive, int criteria, TalkItem talkItem);
    }
}
