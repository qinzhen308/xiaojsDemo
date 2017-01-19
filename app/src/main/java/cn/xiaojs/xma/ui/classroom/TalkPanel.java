package cn.xiaojs.xma.ui.classroom;
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
 * Date:2016/11/29
 * Desc: 教室班级交流界面 (聊天, 联系人)
 *
 * ======================================================================================== */

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Date;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkBean;
import cn.xiaojs.xma.ui.classroom.talk.TalkMsgAdapter;
import cn.xiaojs.xma.ui.classroom.talk.TalkSimpleContactAdapter;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class TalkPanel extends Panel implements View.OnClickListener, ContactBookAdapter.OnContactBookListener {
    public final static int MODE_CONTACT = 0;
    public final static int MODE_TALK = 1;

    private final static int MULTI_TALK = 1;
    private final static int TEACHING_TALK = 2;
    private final static int PEER_TALK = 3;

    private ListView mContactBook;
    private ContactBookAdapter mContactBookAdapter;

    private ListView mTalkContactLv;
    private TalkSimpleContactAdapter mTalkContactAdapter;

    private PullToRefreshListView mTalkMsgLv;
    private TalkMsgAdapter mPeerTalkMsgAdapter;
    private TalkMsgAdapter mMultiTalkAdapter;
    private TalkMsgAdapter mTeachingTalkAdapter;
    private LiveCriteria mMultiLiveCriteria;
    private LiveCriteria mTeachingCriteria;
    private LiveCriteria mPeerCriteria;

    private View mContactView;
    private View mTalkView;
    private View mTalkMsgView;

    private View mDefaultContactAction;
    private View mManageContactAction;

    private ImageView mAddContact;
    private ImageView mManageContact;
    private ImageView mOpenMsg;

    private TextView mSetAssistantTv;
    private TextView mKickOutTv;
    private TextView mBackTv;

    private ImageView mCloseMsgLv;
    private ImageView mDelSingleTalkBtn;

    private EditText mMsgInputEdt;
    private TextView mMsgSendTv;

    private View mMultiTalkTab;
    private View mSingleTalkTab;
    private TextView mTeachTalkTab;
    private TextView mMultiTalkTitle;
    private TextView mSingleTalkTitle;

    private int mWhiteColor;
    private int mBlackFont;

    private int mCurrentMode = MODE_CONTACT;

    private LiveCollection<Attendee> mLiveCollection;
    private PanelCallback mCallback;
    private final Object LOCK = new Object();
    private String mTicket;
    private Socket mSocket;
    private int mTalkCriteria = MULTI_TALK;
    private Attendee mTalkAttendee;
    private String mPeerTalkAccountId = "";
    private String mMyAccountId = "";

    public TalkPanel(Context context, String ticket) {
        super(context);
        initParams(context, ticket);
    }

    public TalkPanel with(int mode) {
        mCurrentMode = mode;
        return this;
    }

    public TalkPanel setPanelCallback(PanelCallback callback) {
        mCallback = callback;
        return this;
    }

    private void initParams(Context context, String ticket) {
        mTicket = ticket;
        mWhiteColor = context.getResources().getColor(R.color.white);
        mBlackFont = context.getResources().getColor(R.color.font_black);
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_contact_msg, null);
    }

    @Override
    public void setChildViewStyle() {
        switch (mCurrentMode) {
            case MODE_CONTACT:
                switchToContact();
                break;
            case MODE_TALK:
                switchToTalk();
                break;
        }
    }

    @Override
    public void initData() {
        mSocket = SocketManager.getSocket();
        if (mSocket != null) {
            mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.JOIN), mOnJoin);
            mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.LEAVE), mOnLeave);
            mSocket.on(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.TALK), mOnSendTalk);
            mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.TALK), mOnReceiveTalk);
        }

        mMyAccountId = AccountDataManager.getAccountID(mContext);

        switch (mCurrentMode) {
            case MODE_CONTACT:
                getContactBookData();
                break;
            case MODE_TALK:
                getTalkMsgData(MULTI_TALK);
                getTalkSimpleContactData();
                break;
        }
    }

    @Override
    public void initChildView(View root) {
        mContactView = root.findViewById(R.id.contact);
        mTalkView = root.findViewById(R.id.talk);
        mTalkMsgView = root.findViewById(R.id.talk_msg_layout);

        mContactBook = (ListView) root.findViewById(R.id.contact_book);
        mTalkContactLv = (ListView) root.findViewById(R.id.talk_simple_contact);
        mTalkMsgLv = (PullToRefreshListView) root.findViewById(R.id.chat_msg);

        mCloseMsgLv = (ImageView) root.findViewById(R.id.close_msg_list_view);
        mDelSingleTalkBtn = (ImageView) root.findViewById(R.id.del_single_talk_btn);

        mDefaultContactAction = root.findViewById(R.id.default_contact_action);
        mManageContactAction = root.findViewById(R.id.manage_contact_action);

        mOpenMsg = (ImageView) root.findViewById(R.id.open_talk);
        mAddContact = (ImageView) root.findViewById(R.id.add_contact);
        mManageContact = (ImageView) root.findViewById(R.id.manage_contact);

        mSetAssistantTv = (TextView) root.findViewById(R.id.set_assistant);
        mKickOutTv = (TextView) root.findViewById(R.id.kick_out);
        mBackTv = (TextView) root.findViewById(R.id.back_manage_contact);

        mMsgSendTv = (TextView) root.findViewById(R.id.msg_send);
        mMsgInputEdt = (EditText) root.findViewById(R.id.msg_input);

        mMultiTalkTab = root.findViewById(R.id.multi_talk_tab);
        mSingleTalkTab = root.findViewById(R.id.single_talk_tab);
        mTeachTalkTab = (TextView) root.findViewById(R.id.teaching_talk_tab);
        mMultiTalkTitle = (TextView) root.findViewById(R.id.multi_talk_title);
        mSingleTalkTitle = (TextView) root.findViewById(R.id.single_talk_title);

        mTalkMsgLv.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        mTalkMsgLv.getRefreshableView().setScrollBarStyle(AbsListView.SCROLLBARS_INSIDE_INSET);
        mTalkMsgLv.getRefreshableView().setFastScrollEnabled(true);
        mTalkMsgLv.getRefreshableView().setDividerHeight(0);

        mOpenMsg.setOnClickListener(this);
        mCloseMsgLv.setOnClickListener(this);
        mAddContact.setOnClickListener(this);
        mSetAssistantTv.setOnClickListener(this);
        mKickOutTv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
        mManageContact.setOnClickListener(this);
        mDelSingleTalkBtn.setOnClickListener(this);
        mMultiTalkTab.setOnClickListener(this);
        mTeachTalkTab.setOnClickListener(this);
        mSingleTalkTab.setOnClickListener(this);
        mMsgSendTv.setOnClickListener(this);

        switchTalkTab(MULTI_TALK);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_talk:
                switchToTalk();
                getTalkSimpleContactData();
                getTalkMsgData(MULTI_TALK);
                break;
            case R.id.close_msg_list_view:
                if (mCallback != null) {
                    mCallback.onClosePanel(PanelCallback.TALK_PANEL_MSG);
                }
                break;
            case R.id.add_contact:
                if (mCallback != null) {
                    mCallback.onOpenPanel(PanelCallback.INVITE_FRIEND_PANEL);
                }
                break;
            case R.id.manage_contact:
                enterContactManagement();
                break;
            case R.id.set_assistant:
                break;
            case R.id.kick_out:
                break;
            case R.id.back_manage_contact:
                exitContactManagement();
                break;
            case R.id.del_single_talk_btn:
                break;
            case R.id.msg_send:
                sendMsg();
                break;
            case R.id.multi_talk_tab:
                switchTalkTab(MULTI_TALK);
                break;
            case R.id.teaching_talk_tab:
                switchTalkTab(TEACHING_TALK);
                break;
            case R.id.single_talk_tab:
                switchTalkTab(PEER_TALK);
                break;
        }
    }

    /**
     * 切换不同的talk tab
     */
    private void switchTalkTab(int criteria) {
        mMultiTalkTab.setBackgroundColor(Color.TRANSPARENT);
        mTeachTalkTab.setBackgroundColor(Color.TRANSPARENT);
        mSingleTalkTab.setBackgroundColor(Color.TRANSPARENT);
        mMultiTalkTitle.setTextColor(mWhiteColor);
        mTeachTalkTab.setTextColor(mWhiteColor);
        mSingleTalkTitle.setTextColor(mWhiteColor);

        switch (criteria) {
            case MULTI_TALK:
                mMultiTalkTab.setBackgroundColor(mWhiteColor);
                mMultiTalkTitle.setTextColor(mBlackFont);
                break;
            case TEACHING_TALK:
                mTeachTalkTab.setBackgroundColor(mWhiteColor);
                mTeachTalkTab.setTextColor(mBlackFont);
                break;
            case PEER_TALK:
                mSingleTalkTab.setBackgroundColor(mWhiteColor);
                mSingleTalkTitle.setTextColor(mBlackFont);
                break;
        }

        getTalkMsgData(criteria);
    }

    /**
     * 切换到talk页面
     */
    private void switchToTalk() {
        mContactView.setVisibility(View.GONE);
        mTalkView.setVisibility(View.VISIBLE);
    }

    /**
     * 切换到联系人页面
     */
    private void switchToContact() {
        mContactView.setVisibility(View.VISIBLE);
        mTalkView.setVisibility(View.GONE);
    }

    /**
     * 获取联系人信息
     */
    private void getContactBookData() {
        if (mContactBookAdapter == null) {
            mContactBookAdapter = new ContactBookAdapter(mContext);
            mContactBookAdapter.setOnContactBookListener(this);
            mContactBook.setAdapter(mContactBookAdapter);
            mContactBook.setDividerHeight(0);
        }

        if (mLiveCollection != null) {
            mContactBookAdapter.setData(mLiveCollection);
        } else {
            LiveManager.getAttendees(mContext, mTicket, new APIServiceCallback<LiveCollection<Attendee>>() {
                @Override
                public void onSuccess(LiveCollection<Attendee> liveCollection) {
                    mLiveCollection = liveCollection;
                    mContactBookAdapter.setData(mLiveCollection);
                    Toast.makeText(mContext, "获取联系成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    Toast.makeText(mContext, "获取联系:" + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 设置talk右边联系人缩略面板
     */
    private void getTalkSimpleContactData() {
        if (mTalkContactAdapter == null) {
            mTalkContactAdapter = new TalkSimpleContactAdapter(mContext);
            mTalkContactLv.setAdapter(mTalkContactAdapter);
            mTalkContactLv.setDividerHeight(0);
        }

        if (mLiveCollection != null) {
            mTalkContactAdapter.setData(mLiveCollection);
        } else {
            LiveManager.getAttendees(mContext, mTicket, new APIServiceCallback<LiveCollection<Attendee>>() {
                @Override
                public void onSuccess(LiveCollection<Attendee> liveCollection) {
                    Toast.makeText(mContext, "获取联系成功", Toast.LENGTH_SHORT).show();
                    mLiveCollection = liveCollection;
                    mTalkContactAdapter.setData(liveCollection);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    Toast.makeText(mContext, "获取联系:" + errorMessage, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * 获取talk消息数据
     */
    private void getTalkMsgData(int criteria) {
        mTalkCriteria = criteria;
        switch (criteria) {
            case MULTI_TALK:
                if (mMultiTalkAdapter == null) {
                    mMultiLiveCriteria = new LiveCriteria();
                    mMultiLiveCriteria.to = Communications.TalkType.OPEN;
                    mMultiTalkAdapter = new TalkMsgAdapter(mContext, mTicket, mMultiLiveCriteria, mTalkMsgLv);
                    mTalkMsgLv.setAdapter(mMultiTalkAdapter);
                } else {
                    mTalkMsgLv.setAdapter(mMultiTalkAdapter);
                }
                break;
            case TEACHING_TALK:
                if (mTeachingTalkAdapter == null) {
                    mTeachingCriteria = new LiveCriteria();
                    mTeachingCriteria.to = Communications.TalkType.FACULTY;
                    mTeachingTalkAdapter = new TalkMsgAdapter(mContext, mTicket, mTeachingCriteria, mTalkMsgLv);
                    mTalkMsgLv.setAdapter(mTeachingTalkAdapter);
                } else {
                    mTalkMsgLv.setAdapter(mTeachingTalkAdapter);
                }
                break;
            case PEER_TALK:
                if (mPeerCriteria == null) {
                    mPeerCriteria = new LiveCriteria();
                }

                try {
                    mPeerCriteria.to = Integer.parseInt(mPeerTalkAccountId);
                } catch (Exception e) {

                }

                //重新装载数据
                mPeerTalkMsgAdapter = new TalkMsgAdapter(mContext, mTicket, mPeerCriteria, mTalkMsgLv);
                mTalkMsgLv.setAdapter(mPeerTalkMsgAdapter);
                break;
        }
    }

    /**
     * 获取talk消息数据
     */
    private void updateTalkMsgData(int criteria, TalkItem talkItem) {
        switch (criteria) {
            case MULTI_TALK:
                if (mMultiTalkAdapter != null) {
                    mMultiTalkAdapter.add(talkItem);
                    mTalkMsgLv.setAdapter(mMultiTalkAdapter);
                }
                break;
            case TEACHING_TALK:
                if (mTeachingTalkAdapter != null) {
                    mTeachingTalkAdapter.add(talkItem);
                    mTalkMsgLv.setAdapter(mTeachingTalkAdapter);
                }
                break;
            case PEER_TALK:
                if (mPeerTalkMsgAdapter != null) {
                    mPeerTalkMsgAdapter.add(talkItem);
                    mTalkMsgLv.setAdapter(mPeerTalkMsgAdapter);
                }
                break;
        }
    }

    /**
     * 进入联系人管理
     */
    private void enterContactManagement() {
        mDefaultContactAction.setVisibility(View.GONE);
        mManageContactAction.setVisibility(View.VISIBLE);
        if (mContactBookAdapter != null) {
            mContactBookAdapter.enterManagementMode();
        }
    }

    /**
     * 退出联系人管理
     */
    private void exitContactManagement() {
        mDefaultContactAction.setVisibility(View.VISIBLE);
        mManageContactAction.setVisibility(View.GONE);
        if (mContactBookAdapter != null) {
            mContactBookAdapter.exitManagementMode();
        }
    }

    @Override
    public void onPortraitClick(Attendee attendee) {
        switchToTalk();
        getTalkSimpleContactData();
        mTalkAttendee = attendee;
        mPeerTalkAccountId = attendee != null ? attendee.accountId : "";
        getTalkMsgData(PEER_TALK);
    }

    /**
     * 加入事件
     */
    private Emitter.Listener mOnJoin = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (mContext instanceof Activity && args != null && args.length > 0) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "有人加入", Toast.LENGTH_SHORT).show();
                        updateContactList(true, args);
                    }
                });
            }
        }
    };

    /**
     * 退出事件
     */
    private Emitter.Listener mOnLeave = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (mContext instanceof Activity && args != null && args.length > 0) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "有人退出", Toast.LENGTH_SHORT).show();
                        updateContactList(false, args);
                    }
                });
            }
        }
    };

    /**
     * 更新联系人列表
     */
    private void updateContactList(boolean add, Object... args) {
        if (args == null || args.length == 0) {
            return;
        }

        if (!(args[0] instanceof String)) {
            return;
        }

        String result = (String) args[0];
        Attendee attendee = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            attendee = mapper.readValue(result, Attendee.class);
        } catch (Exception e) {

        }

        if (attendee != null && mLiveCollection != null) {
            if (mLiveCollection.attendees == null) {
                mLiveCollection.attendees = new ArrayList<Attendee>();
            }

            //refresh list
            synchronized (LOCK) {
                if (add) {
                    //add
                    mLiveCollection.attendees.add(attendee);
                } else {
                    //remove
                    mLiveCollection.attendees.remove(attendee);
                }

                if (mTalkContactAdapter != null) {
                    mTalkContactAdapter.setData(mLiveCollection);
                }

                if (mContactBookAdapter != null) {
                    mContactBookAdapter.setData(mLiveCollection);
                }
            }
        }
    }

    /**
     * 接收到消息
     */
    private Emitter.Listener mOnReceiveTalk = new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    if (mContext instanceof Activity && args != null && args.length > 0) {
                        ((Activity) mContext).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(mContext, "接收到消息", Toast.LENGTH_SHORT).show();
                                updateMsgList(args);
                            }
                        });
                    }
                }
            };

    /**
     * 消息发送
     */
    private Emitter.Listener mOnSendTalk = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            if (mContext instanceof Activity && args != null && args.length > 0) {
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "消息发送", Toast.LENGTH_SHORT).show();
                        updateMsgList(args);
                    }
                });
            }
        }
    };

    private void updateMsgList(Object... args) {
        if (args == null || args.length == 0) {
            return;
        }

        if (!(args[0] instanceof String)) {
            return;
        }

        String result = (String) args[0];
        TalkItem talkItem = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            talkItem = mapper.readValue(result, TalkItem.class);
        } catch (Exception e) {

        }

        if (talkItem != null) {
            updateTalkMsgData(mTalkCriteria, talkItem);
        }
    }

    private void sendMsg() {
        String text = mMsgInputEdt.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        TalkItem talkItem = new TalkItem();

        TalkItem.TalkPerson person = new TalkItem.TalkPerson();
        //myself info
        person.accountId = mMyAccountId;
        //person.name = mTalkAttendee.name;
        //person.avatar = mTalkAttendee.avatar;
        talkItem.from = person;

        TalkItem.TalkContent talkContent = new TalkItem.TalkContent();
        talkContent.text = text;
        talkItem.body = talkContent;

        long sendTime = System.currentTimeMillis();
        talkItem.time = new Date(sendTime);

        updateTalkMsgData(mTalkCriteria, talkItem);

        TalkBean talkBean = new TalkBean();
        talkBean.payload = new TalkBean.Payload();
        talkBean.payload.body = new TalkBean.TalkContent();
        talkBean.payload.body.text = text;

        talkBean.payload.time = sendTime;
        switch (mTalkCriteria) {
            case MULTI_TALK:
                talkBean.payload.to = Communications.TalkType.OPEN;
                break;
            case PEER_TALK:
                talkBean.payload.to = Communications.TalkType.FACULTY;
                break;
            case TEACHING_TALK:
                try {
                    talkBean.payload.to = Integer.parseInt(mPeerTalkAccountId);
                } catch (Exception e) {

                }
                break;
        }
        String sendJson = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            sendJson = mapper.writeValueAsString(talkBean);
        } catch (Exception e) {

        }
        if (sendJson != null && mSocket != null) {
            mSocket.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.TALK), sendJson, new Ack() {
                @Override
                public void call(Object... args) {
                    ((Activity) mContext).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "消息发送成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        //reset text
        mMsgInputEdt.setText("");
    }
}

