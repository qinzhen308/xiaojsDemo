package cn.xiaojs.xma.ui.classroom.main;
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
import android.text.TextUtils;

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
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.bean.TalkBean;
import cn.xiaojs.xma.ui.classroom.bean.TalkResponse;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.FullScreenTalkMsgAdapter;
import cn.xiaojs.xma.ui.classroom.talk.TalkMsgAdapter;

public class TalkManager {
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

    private TalkManager() {
        mPeerTalkMsgAdapterMap = new HashMap<String, TalkMsgAdapter>();
        mOnTalkMsgReceiveListeners = new ArrayList<OnTalkMsgReceived>();
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.TALK), mOnReceiveTalk);
    }

    public void init(Context context, String ticket) {
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
        String ticket = LiveCtlSessionManager.getInstance().getTicket();
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
                    mFullScreenTalkMsgAdapter = new FullScreenTalkMsgAdapter(context, ticket, listView, null);
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

        mInstance = null;
    }

    /**
     * 接收到消息
     */
    private SocketManager.EventListener mOnReceiveTalk = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                //TODO fix同一条消息多次回调?
                handleReceivedMsg(args);
            }
        }
    };

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

    public void notifyMsgChanged(int criteria, TalkItem talkItem) {
        if (mOnTalkMsgReceiveListeners != null) {
            for (OnTalkMsgReceived listener : mOnTalkMsgReceiveListeners) {
                listener.onMsgChanged(criteria, talkItem);
            }
        }
    }

    /**
     * 更新消息列表
     */
    private void handleReceivedMsg(Object... args) {
        if (args == null || args.length == 0) {
            return;
        }

        try {
            TalkBean receiveBean = ClassroomBusiness.parseSocketBean(args[0], TalkBean.class);
            if (receiveBean == null) {
                return;
            }

            TalkItem talkItem = new TalkItem();
            talkItem.time = receiveBean.time;
            talkItem.body = new cn.xiaojs.xma.model.live.TalkItem.TalkContent();
            talkItem.from = new cn.xiaojs.xma.model.live.TalkItem.TalkPerson();
            talkItem.body.text = receiveBean.body.text;
            talkItem.body.contentType = receiveBean.body.contentType;
            talkItem.from.accountId = receiveBean.from;
            talkItem.from.name = getNameByAccountId(receiveBean.from);

            int type = addTalkItemToAdapter(talkItem, receiveBean.to);
            notifyMsgChanged(type, talkItem);
            //TODO update unread message
            /*if (mOnTalkMsgListener != null) {
                mOnTalkMsgListener.onTalkMsgReceived(talkItem);
                if (mDrawerOpened) {
                    if ((mPeerTalkAccountId == null && talkItem.from.accountId != null) || (mPeerTalkAccountId != null &&
                            !mPeerTalkAccountId.equals(talkItem.from.accountId))) {
                        updateUnreadMsgCount(criteria, talkItem);
                    }
                } else {
                    updateUnreadMsgCount(criteria, talkItem);
                }
            }*/
        } catch (Exception e) {

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

        final TalkItem talkItem = new TalkItem();
        //file myself info
        fillMyselfInfo(talkItem);

        talkItem.body = new TalkItem.TalkContent();
        talkItem.body.text = text;
        talkItem.body.contentType = contentType;
        talkItem.to = accountId;

        long sendTime = System.currentTimeMillis();
        talkItem.time = sendTime;

        //send socket info
        final TalkBean talkBean = new TalkBean();
        talkBean.from = mMyAccountId;
        talkBean.body = new TalkBean.TalkContent();
        talkBean.body.text = text;
        talkBean.body.contentType = contentType;
        talkBean.time = sendTime;
        if (accountId == null) {
            switch (criteria) {
                case Communications.TalkType.OPEN:
                    talkBean.to = String.valueOf(Communications.TalkType.OPEN);
                    break;
                case Communications.TalkType.FACULTY:
                    talkBean.to = String.valueOf(Communications.TalkType.FACULTY);
                    break;
                default:
                    talkBean.to = String.valueOf(Communications.TalkType.OPEN);
                    break;
            }
        } else {
            try {
                talkBean.to = accountId;
            } catch (Exception e) {

            }
        }

        if (talkBean != null) {
            String event = Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.TALK);
            SocketManager.emit(event, talkBean, new SocketManager.AckListener() {
                @Override
                public void call(final Object... args) {
                    // 处理消息发送的回调信息
                    if (args == null || args.length == 0) {
                        return;
                    }

                    try {
                        TalkResponse talkResponse = ClassroomBusiness.parseSocketBean(args[0], TalkResponse.class);
                        if (talkResponse != null && talkResponse.result) {
                            int type = addTalkItemToAdapter(talkItem, talkBean.to);
                            if (type == TYPE_MSG_MUlTI_TAlk || type == TYPE_FULL_SCREEN_MUlTI_TAlk) {
                                notifyMsgChanged(talkType, talkItem);
                            } else {
                                notifyMsgChanged(type, talkItem);
                            }
                        } else {
                            //TODO send failure
                            //Toast.makeText()
                        }
                    } catch (Exception e) {

                    }
                }
            });
        }
    }

    private int addTalkItemToAdapter(TalkItem talkItem, String to) {
        int type = TYPE_PEER_TALK;
        try {
            int criteria = Integer.parseInt(to);
            type = getTalkType(criteria);
        } catch (NumberFormatException e) {
            type = TYPE_PEER_TALK;
        }

        //update all adapters
        if (type == TYPE_MSG_MUlTI_TAlk || type == TYPE_FULL_SCREEN_MUlTI_TAlk) {
            //multi talk
            AbsChatAdapter adapter = getAdapter(TYPE_MSG_MUlTI_TAlk, to);
            if (adapter != null) {
                adapter.add(talkItem);
            }

            //fullscreen multi talk
            AbsChatAdapter fcAdapter = getAdapter(TYPE_FULL_SCREEN_MUlTI_TAlk, to);
            if (fcAdapter != null) {
                fcAdapter.add(talkItem);
            }
        } else {
            AbsChatAdapter adapter = getAdapter(type, to);
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

    private String getNameByAccountId(String accountId) {
        LiveCollection<Attendee> liveCollection = ContactManager.getInstance().getAttendees();
        if (accountId != null && liveCollection != null && liveCollection.attendees != null) {
            for (Attendee attendee : liveCollection.attendees) {
                if (accountId != null && accountId.equals(attendee.accountId)) {
                    return attendee.name;
                }
            }
        }

        return accountId;
    }

    public static interface OnTalkMsgReceived {
        public void onMsgChanged(int criteria, TalkItem talkItem);
    }
}
