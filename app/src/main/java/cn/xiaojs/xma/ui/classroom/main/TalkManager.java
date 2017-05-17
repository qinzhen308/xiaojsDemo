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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xiaojs.xma.common.pulltorefresh.AbsChatAdapter;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.bean.TalkBean;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.FullScreenTalkMsgAdapter;
import cn.xiaojs.xma.ui.classroom.talk.TalkMsgAdapter;

public class TalkManager {
    public final static int TYPE_MULTI_TALK = 1;
    public final static int TYPE_PEER_TALK = 2;
    public final static int TYPE_TEACHING_TALK = 3;
    public final static int TYPE_FULL_SCREEN_MUlTI_TAlk = 4;

    private static TalkManager mInstance;

    //教室交流Adapter
    private TalkMsgAdapter mMultiTalkAdapter;
    //教学组Adapter
    private TalkMsgAdapter mTeachingTalkAdapter;
    //一对一交流Adapter
    private Map<String, TalkMsgAdapter> mPeerTalkMsgAdapterMap;
    //全屏模式下教室交流Adapter
    private FullScreenTalkMsgAdapter mFullScreenTalkMsgAdapter;

    private List<OnTalkMsgReceived> mOnTalkMsgReceiveListeners;

    private String mTicket;

    private TalkManager() {
        mPeerTalkMsgAdapterMap = new HashMap<String, TalkMsgAdapter>();
        mOnTalkMsgReceiveListeners = new ArrayList<OnTalkMsgReceived>();
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.TALK), mOnReceiveTalk);
    }

    public void init(String ticket) {
        mTicket = ticket;
    }

    public synchronized TalkManager getInstance() {
        if (mInstance == null) {
            mInstance = new TalkManager();
        }

        return mInstance;
    }

    /**
     * 获取非一对一的聊天
     */
    public AbsChatAdapter getChatAdapter(Context context, int type, String ticket, PullToRefreshListView listView) {
        if (type == TYPE_PEER_TALK) {
            throw new IllegalArgumentException("illegal type passed!");
        }

        return getChatAdapter(context, type, null, ticket, listView);
    }

    public AbsChatAdapter getChatAdapter(Context context, int type, String accountId, String ticket, PullToRefreshListView listView) {
        AbsChatAdapter adapter = getAdapter(type, accountId);
        LiveCriteria lveCriteria = new LiveCriteria();
        if (adapter == null) {
            lveCriteria.to = String.valueOf(Communications.TalkType.OPEN);
            switch (type) {
                case TYPE_MULTI_TALK:
                    lveCriteria.to = String.valueOf(Communications.TalkType.OPEN);
                    mMultiTalkAdapter = new TalkMsgAdapter(context, ticket, lveCriteria, listView);
                    return mMultiTalkAdapter;
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
    public AbsChatAdapter getPeekChatAdapter(Context context, String accountId, String ticket, PullToRefreshListView listView) {
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
            case TYPE_MULTI_TALK:
                return mMultiTalkAdapter;
            case TYPE_TEACHING_TALK:
                return mTeachingTalkAdapter;
            case TYPE_FULL_SCREEN_MUlTI_TAlk:
                return mFullScreenTalkMsgAdapter;
            case TYPE_PEER_TALK:
                if (mPeerTalkMsgAdapterMap.containsKey(accountId)) {
                    return mPeerTalkMsgAdapterMap.get(accountId);
                }
        }

        return mMultiTalkAdapter;
    }

    public void onDestroy() {
        if (mMultiTalkAdapter != null) {
            mMultiTalkAdapter.release();
        }

        if (mTeachingTalkAdapter != null) {
            mTeachingTalkAdapter.release();
        }

        if (mFullScreenTalkMsgAdapter != null) {
            mFullScreenTalkMsgAdapter.release();
        }

        if (mPeerTalkMsgAdapterMap != null) {
            for (Map.Entry<String, TalkMsgAdapter> entry : mPeerTalkMsgAdapterMap.entrySet()) {
                TalkMsgAdapter adapter = entry.getValue();
                if (adapter != null) {
                    adapter.release();
                }
            }
            mPeerTalkMsgAdapterMap.clear();
        }

        if (mOnTalkMsgReceiveListeners != null) {
            mOnTalkMsgReceiveListeners.clear();
        }
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

    public void notifyMsgReceived(TalkItem talkItem) {
        if (mOnTalkMsgReceiveListeners != null) {
            for (OnTalkMsgReceived listener : mOnTalkMsgReceiveListeners) {
                listener.onMsgReceived(talkItem);
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

            int type = 1;
            try {
                int criteria = Integer.parseInt(receiveBean.to);
                type = getTalkType(criteria);
            } catch (NumberFormatException e) {
                type = TYPE_PEER_TALK;
            }

            AbsChatAdapter adapter = getAdapter(type, receiveBean.to);
            if (adapter != null) {
                adapter.add(talkItem);
            }

            notifyMsgReceived(talkItem);
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


    private int getTalkType(int criteria) {
        int type = TYPE_MULTI_TALK;
        switch (criteria) {
            case Communications.TalkType.OPEN:
                type = TYPE_MULTI_TALK;
                break;
            case Communications.TalkType.FACULTY:
                type = TYPE_TEACHING_TALK;
                break;
        }

        return type;
    }

    //TODO
    private String getNameByAccountId(String accountId) {
        /*if (accountId != null && mLiveCollection != null && mLiveCollection.attendees != null) {
            for (Attendee attendee : mLiveCollection.attendees) {
                if (accountId != null && accountId.equals(attendee.accountId)) {
                    return attendee.name;
                }
            }
        }*/

        return null;
    }

    public static interface OnTalkMsgReceived {
        public void onMsgReceived(TalkItem talkItem);
    }
}
