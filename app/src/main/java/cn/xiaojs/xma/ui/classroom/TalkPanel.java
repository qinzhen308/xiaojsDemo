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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
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
import cn.xiaojs.xma.ui.classroom.bean.TalkBean;
import cn.xiaojs.xma.ui.classroom.bean.TalkResponse;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.ContactBookAdapter;
import cn.xiaojs.xma.ui.classroom.talk.OnImageClickListener;
import cn.xiaojs.xma.ui.classroom.talk.OnPortraitClickListener;
import cn.xiaojs.xma.ui.classroom.talk.OnTalkMsgListener;
import cn.xiaojs.xma.ui.classroom.talk.TalkMsgAdapter;
import cn.xiaojs.xma.ui.classroom.talk.TalkSimpleContactAdapter;

public class TalkPanel extends Panel implements View.OnClickListener, OnPortraitClickListener {
    public final static int MODE_CONTACT = 0;
    public final static int MODE_TALK = 1;

    private final static int MULTI_TALK = 1;
    private final static int TEACHING_TALK = 2;
    private final static int PEER_TALK = 3;

    /**
     * 联系人列表
     */
    private ListView mContactBook;
    private ContactBookAdapter mContactBookAdapter;
    private TextView mEmptyContactView;

    /**
     * Talk界面联系人列表（头像）
     */
    private ListView mTalkContactLv;
    private TalkSimpleContactAdapter mTalkContactAdapter;

    /**
     * Talk消息列表
     */
    private PullToRefreshListView mTalkMsgLv;
    //private TalkMsgAdapter mPeerTalkMsgAdapter;
    private TalkMsgAdapter mMultiTalkAdapter;
    private TalkMsgAdapter mTeachingTalkAdapter;
    private Map<String, TalkMsgAdapter> mPeerTalkMsgAdapterMap;
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
    private ImageView mDelPeerTalkBtn;

    private EditText mSearchEdt;
    private ImageView mSearchBtn;

    private EditText mMsgInputEdt;
    private TextView mMsgSendTv;

    private View mMultiTalkTab;
    private View mTeachingTalkTab;
    private View mPeerTalkTab;
    private ToggleButton mDiscussionSwitcher;
    private TextView mTeachTalkTitle;
    private TextView mMultiTalkTitle;
    private TextView mPeerTalkTitle;

    private int mWhiteColor;
    private int mBlackFont;

    private int mCurrentMode = MODE_CONTACT;

    private LiveCollection<Attendee> mLiveCollection;
    private LiveCollection<Attendee> mSearchLiveCollection;
    private PanelCallback mCallback;
    private OnTalkMsgListener mOnTalkMsgListener;
    private OnImageClickListener mOnImageClickListener;
    private OnPanelItemClick mOnPanelItemClick;
    private final Object MSG_LIST_LOCK = new Object();
    private final Object MSG_COUNT_LOCK = new Object();
    private String mTicket;
    private int mTalkCriteria = MULTI_TALK;
    private Attendee mTalkAttendee;
    private String mPeerTalkAccountId = "";
    private String mMyAccountId = "";
    private Constants.User mUser;
    private boolean mPeerTabShow = false;
    private boolean mSocketListenered = false;
    private Map<String, Integer> mUnreadMsgCountMap;
    private List<TalkItem> mReceivedTalkMsg;

    public TalkPanel(Context context, String ticket, Constants.User user) {
        super(context);
        initParams(context, ticket, user);
    }

    public TalkPanel with(int mode) {
        mCurrentMode = mode;
        return this;
    }

    public TalkPanel setPanelCallback(PanelCallback callback) {
        mCallback = callback;
        return this;
    }

    public TalkPanel setPanelItemClick(OnPanelItemClick itemClick) {
        mOnPanelItemClick = itemClick;
        return this;
    }

    public TalkPanel setTalkMsgListener(OnTalkMsgListener talkMsgListener) {
        mOnTalkMsgListener = talkMsgListener;
        return this;
    }

    public TalkPanel setOnImageClickListener (OnImageClickListener listener) {
        mOnImageClickListener = listener;
        return this;
    }

    private void initParams(Context context, String ticket, Constants.User user) {
        mTicket = ticket;
        mUser = user;
        mWhiteColor = context.getResources().getColor(R.color.white);
        mBlackFont = context.getResources().getColor(R.color.font_black);
        mUnreadMsgCountMap = new HashMap<String, Integer>();
        mReceivedTalkMsg = new ArrayList<TalkItem>();
        mPeerTalkMsgAdapterMap = new HashMap<String, TalkMsgAdapter>();
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
        if (!mSocketListenered) {
            mSocketListenered = false;
            SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.JOIN), mOnJoin);
            SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.LEAVE), mOnLeave);
            SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.TALK), mOnReceiveTalk);
        }

        mMyAccountId = AccountDataManager.getAccountID(mContext);
        switch (mCurrentMode) {
            case MODE_CONTACT:
                getContactBookData();
                break;
            case MODE_TALK:
                //getTalkMsgData(mTalkCriteria, mTalkAccountId);
                switchTalkTab(mTalkCriteria, mPeerTalkAccountId);
                getTalkSimpleContactData();
                break;
        }
    }

    @Override
    public void initChildView(View root) {
        mContactView = root.findViewById(R.id.contact);
        mTalkView = root.findViewById(R.id.talk);
        mTalkMsgView = root.findViewById(R.id.talk_msg_layout);
        mDiscussionSwitcher = (ToggleButton)root.findViewById(R.id.discussion_switcher);

        mContactBook = (ListView) root.findViewById(R.id.contact_book);
        mTalkContactLv = (ListView) root.findViewById(R.id.talk_simple_contact);
        mTalkMsgLv = (PullToRefreshListView) root.findViewById(R.id.chat_msg);
        mEmptyContactView = (TextView) root.findViewById(R.id.empty_contact_view);

        mCloseMsgLv = (ImageView) root.findViewById(R.id.close_msg_list_view);
        mDelPeerTalkBtn = (ImageView) root.findViewById(R.id.del_peer_talk_btn);

        mDefaultContactAction = root.findViewById(R.id.default_contact_action);
        mManageContactAction = root.findViewById(R.id.manage_contact_action);

        mSearchEdt = (EditText) root.findViewById(R.id.search_txt);
        mSearchBtn = (ImageView) root.findViewById(R.id.search_btn);

        mOpenMsg = (ImageView) root.findViewById(R.id.open_talk);
        mAddContact = (ImageView) root.findViewById(R.id.add_contact);
        mManageContact = (ImageView) root.findViewById(R.id.manage_contact);

        mSetAssistantTv = (TextView) root.findViewById(R.id.set_assistant);
        mKickOutTv = (TextView) root.findViewById(R.id.kick_out);
        mBackTv = (TextView) root.findViewById(R.id.back_manage_contact);

        mMsgSendTv = (TextView) root.findViewById(R.id.msg_send);
        mMsgInputEdt = (EditText) root.findViewById(R.id.msg_input);

        mMultiTalkTab = root.findViewById(R.id.multi_talk_tab);
        mTeachingTalkTab = root.findViewById(R.id.teaching_talk_tab);
        mPeerTalkTab = root.findViewById(R.id.peer_talk_tab);
        mTeachTalkTitle = (TextView) root.findViewById(R.id.teaching_talk_title);
        mMultiTalkTitle = (TextView) root.findViewById(R.id.multi_talk_title);
        mPeerTalkTitle = (TextView) root.findViewById(R.id.peer_talk_title);

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
        mDelPeerTalkBtn.setOnClickListener(this);
        mMultiTalkTab.setOnClickListener(this);
        mTeachingTalkTab.setOnClickListener(this);
        mPeerTalkTab.setOnClickListener(this);
        mMsgSendTv.setOnClickListener(this);
        mSearchBtn.setOnClickListener(this);

        //set search text watcher
        mSearchEdt.addTextChangedListener(mSearchTextWatcher);

        mDiscussionSwitcher.setChecked(true);
        switch (mUser) {
            case TEACHER:
            case ASSISTANT:
            case REMOTE_ASSISTANT:
                mTeachingTalkTab.setVisibility(View.VISIBLE);
                mPeerTalkTab.setVisibility(View.GONE);
                break;
            case STUDENT:
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mMultiTalkTitle.getLayoutParams();
                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                mTeachingTalkTab.setVisibility(View.GONE);
                mDiscussionSwitcher.setVisibility(View.GONE);
                mPeerTalkTab.setVisibility(View.VISIBLE);
                mDelPeerTalkBtn.setVisibility(View.GONE);
                break;
        }

        switchTalkTab(MULTI_TALK, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_talk:
                switchToTalk();
                getTalkSimpleContactData();
                getTalkMsgData(MULTI_TALK, null);
                break;
            case R.id.close_msg_list_view:
                super.close();
                break;
            case R.id.add_contact:
                if (mCallback != null) {
                    mCallback.switchPanel(PanelCallback.INVITE_FRIEND_PANEL);
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
            case R.id.del_peer_talk_btn:
                mPeerTabShow = false;
                mDelPeerTalkBtn.setVisibility(View.GONE);
                mPeerTalkTitle.setText("");
                switchTalkTab(MULTI_TALK, null);
                break;
            case R.id.msg_send:
                sendMsg();
                break;
            case R.id.multi_talk_tab:
                switchTalkTab(MULTI_TALK, null);
                break;
            case R.id.teaching_talk_tab:
                switchTalkTab(TEACHING_TALK, null);
                break;
            case R.id.peer_talk_tab:
                if (mPeerTabShow) {
                    switchTalkTab(PEER_TALK, mPeerTalkAccountId);
                }
                break;
            case R.id.search_btn:
                searchContactFromLocal(mSearchEdt.getText().toString());
                break;
        }
    }

    /**
     * socket断开后，需要重新监听socket
     */
    public void needSocketReListener() {
        mSocketListenered = false;
    }

    private void updateUnreadMsgCount(int criteria, TalkItem talkItem) {
        synchronized (MSG_COUNT_LOCK) {
            if (!mReceivedTalkMsg.contains(talkItem)) {
                mReceivedTalkMsg.add(talkItem);

                if (criteria == PEER_TALK) {
                    //个人聊天的时候
                    String accountId = talkItem.from.accountId;
                    if (mUnreadMsgCountMap.containsKey(accountId)) {
                        int msgCount = mUnreadMsgCountMap.get(accountId);
                        mUnreadMsgCountMap.put(accountId, msgCount + 1);
                    } else {
                        mUnreadMsgCountMap.put(accountId, 1);
                    }
                }

                setUnreadMsgCount2Adapter(false);
            }
        }
    }

    /**
     * 设置未读消息到适配器)
     * @param hasLock 是否需要加锁
     */
    private void setUnreadMsgCount2Adapter(boolean hasLock) {
        if (hasLock) {
            setUnreadMsgCount2Adapter();
        } else {
            synchronized (MSG_COUNT_LOCK) {
               setUnreadMsgCount2Adapter();
            }
        }
    }

    /**
     * 设置未读消息到适配器
     */
    private void setUnreadMsgCount2Adapter() {
        if (mTalkContactAdapter != null) {
            ArrayList<Attendee> attendees = mTalkContactAdapter.getAttendeeList();
            if (attendees != null && !attendees.isEmpty()) {
                for (Attendee attendee : attendees) {
                    if (mUnreadMsgCountMap.containsKey(attendee.accountId)) {
                        int count = mUnreadMsgCountMap.get(attendee.accountId);
                        attendee.unReadMsgCount = attendee.unReadMsgCount + count;
                    }
                }

                mTalkContactAdapter.notifyDataSetChanged();
                mUnreadMsgCountMap.clear();
            }
        }
    }

    /**
     * 切换不同的talk tab
     */
    private void switchTalkTab(int criteria, String accountId) {
        mMultiTalkTab.setBackgroundColor(Color.TRANSPARENT);
        mTeachingTalkTab.setBackgroundColor(Color.TRANSPARENT);
        mPeerTalkTab.setBackgroundColor(Color.TRANSPARENT);
        mMultiTalkTitle.setTextColor(mWhiteColor);
        mTeachTalkTitle.setTextColor(mWhiteColor);
        mPeerTalkTitle.setTextColor(mWhiteColor);

        switch (criteria) {
            case MULTI_TALK:
                mMultiTalkTab.setBackgroundColor(mWhiteColor);
                mMultiTalkTitle.setTextColor(mBlackFont);
                break;
            case TEACHING_TALK:
                mTeachingTalkTab.setBackgroundColor(mWhiteColor);
                mTeachTalkTitle.setTextColor(mBlackFont);
                break;
            case PEER_TALK:
                mPeerTalkTab.setBackgroundColor(mWhiteColor);
                mPeerTalkTab.setVisibility(View.VISIBLE);

                mPeerTalkTitle.setTextColor(mBlackFont);
                mPeerTalkTitle.setText(getNameByAccountId(mPeerTalkAccountId));
                break;
        }

        getTalkMsgData(criteria, accountId);
    }

    /**
     * 切换到talk页面
     */
    private void switchToTalk() {
        if (mContactView.getVisibility() != View.GONE) {
            mContactView.setVisibility(View.GONE);
        }
        if (mTalkView.getVisibility() != View.VISIBLE) {
            mTalkView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 切换到联系人页面
     */
    private void switchToContact() {
        if (mContactView.getVisibility() != View.VISIBLE) {
            mContactView.setVisibility(View.VISIBLE);
        }
        if (mTalkView.getVisibility() != View.GONE) {
            mTalkView.setVisibility(View.GONE);
        }
    }

    /**
     * 获取联系人信息
     */
    private void getContactBookData() {
        if (mContactBookAdapter == null) {
            mContactBookAdapter = new ContactBookAdapter(mContext, mUser);
            mContactBookAdapter.setOnPortraitClickListener(this);
            mContactBookAdapter.setOnPanelItemClick(mOnPanelItemClick);
            mContactBook.setAdapter(mContactBookAdapter);
            mContactBook.setDividerHeight(0);
        }

        if (mLiveCollection != null) {
            mContactBookAdapter.setData(mLiveCollection);
            setEmptyContactView();
        } else {
            LiveManager.getAttendees(mContext, mTicket, new APIServiceCallback<LiveCollection<Attendee>>() {
                @Override
                public void onSuccess(LiveCollection<Attendee> liveCollection) {
                    mLiveCollection = liveCollection;
                    addMyself2Attendees(mLiveCollection);
                    mContactBookAdapter.setData(mLiveCollection);
                    setEmptyContactView();
                    if (XiaojsConfig.DEBUG) {
                        Toast.makeText(mContext, "获取联系成功", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    setEmptyContactView();
                    if (XiaojsConfig.DEBUG) {
                        Toast.makeText(mContext, "获取联系:" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 设置联系人空列表
     */
    private void setEmptyContactView() {
        if (mLiveCollection == null) {
            mEmptyContactView.setVisibility(View.VISIBLE);
            return;
        }

        if (mLiveCollection.attendees == null || mLiveCollection.attendees.isEmpty()) {
            mEmptyContactView.setVisibility(View.VISIBLE);
        } else {
            mEmptyContactView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置talk右边联系人缩略面板
     */
    private void getTalkSimpleContactData() {
        if (mTalkContactAdapter == null) {
            mTalkContactAdapter = new TalkSimpleContactAdapter(mContext);
            mTalkContactAdapter.setOnPortraitClickListener(this);
            mTalkContactLv.setAdapter(mTalkContactAdapter);
            mTalkContactLv.setDividerHeight(0);
        }

        if (mLiveCollection != null) {
            mTalkContactAdapter.setData(mLiveCollection);
            setUnreadMsgCount2Adapter(true);
        } else {
            LiveManager.getAttendees(mContext, mTicket, new APIServiceCallback<LiveCollection<Attendee>>() {
                @Override
                public void onSuccess(LiveCollection<Attendee> liveCollection) {
                    if (XiaojsConfig.DEBUG) {
                        Toast.makeText(mContext, "获取联系成功", Toast.LENGTH_SHORT).show();
                    }
                    mLiveCollection = liveCollection;
                    addMyself2Attendees(mLiveCollection);
                    mTalkContactAdapter.setData(liveCollection);
                    setUnreadMsgCount2Adapter(true);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    if (XiaojsConfig.DEBUG) {
                        Toast.makeText(mContext, "获取联系:" + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * 获取talk消息数据
     */
    private void getTalkMsgData(int criteria, String accountId) {
        mTalkCriteria = criteria;
        switch (criteria) {
            case MULTI_TALK:
                if (mMultiTalkAdapter == null) {
                    mMultiLiveCriteria = new LiveCriteria();
                    mMultiLiveCriteria.to = String.valueOf(Communications.TalkType.OPEN);
                    mMultiTalkAdapter = new TalkMsgAdapter(mContext, mTicket, mMultiLiveCriteria, mTalkMsgLv);
                    mMultiTalkAdapter.setOnImageClickListener(mOnImageClickListener);
                }

                mTalkMsgLv.setAdapter(mMultiTalkAdapter);
                break;
            case TEACHING_TALK:
                if (mTeachingTalkAdapter == null) {
                    mTeachingCriteria = new LiveCriteria();
                    mTeachingCriteria.to = String.valueOf(Communications.TalkType.FACULTY);
                    mTeachingTalkAdapter = new TalkMsgAdapter(mContext, mTicket, mTeachingCriteria, mTalkMsgLv);
                    mTeachingTalkAdapter.setOnImageClickListener(mOnImageClickListener);
                }

                mTalkMsgLv.setAdapter(mTeachingTalkAdapter);
                break;
            case PEER_TALK:
                //重新装载数据
                TalkMsgAdapter adapter = getPeekTalkMsgAdapter(accountId);;
                mTalkMsgLv.setAdapter(adapter);
                break;
        }

        scrollMsgLvToBottom();
    }

    /**
     * 更新talk消息数据
     * @param updateMsgListView 是否更新列表
     */
    private void updateTalkMsgData(int criteria, TalkItem talkItem, boolean updateMsgListView) {
        switch (criteria) {
            case MULTI_TALK:
                if (mMultiTalkAdapter != null) {
                    mMultiTalkAdapter.add(talkItem);
                    if (updateMsgListView) {
                        mTalkMsgLv.setAdapter(mMultiTalkAdapter);
                    }
                }
                break;
            case TEACHING_TALK:
                if (mTeachingTalkAdapter != null) {
                    mTeachingTalkAdapter.add(talkItem);
                    if (updateMsgListView) {
                        mTalkMsgLv.setAdapter(mTeachingTalkAdapter);
                    }
                }
                break;
            case PEER_TALK:
                TalkMsgAdapter adapter = getPeekTalkMsgAdapter(talkItem.from.accountId);
                if (adapter != null) {
                    adapter.add(talkItem);
                    if (updateMsgListView && (mPeerTalkAccountId != null && mPeerTalkAccountId.equals(talkItem.from.accountId))) {
                        mTalkMsgLv.setAdapter(adapter);
                    }
                }
                break;
        }

        scrollMsgLvToBottom();
    }

    private void scrollMsgLvToBottom() {
        ListAdapter adapter = mTalkMsgLv.getRefreshableView().getAdapter();
        int count = adapter != null ? adapter.getCount() : 0;
        if (count > 0) {
            mTalkMsgLv.getRefreshableView().setSelection(count - 1);
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
        if (attendee == null) {
            return;
        }

        mPeerTabShow = true;
        switchToTalk();
        //getTalkSimpleContactData();
        mTalkAttendee = attendee;
        mPeerTalkAccountId = attendee.accountId;
        mDelPeerTalkBtn.setVisibility(View.VISIBLE);
        switchTalkTab(PEER_TALK, mPeerTalkAccountId);
    }

    /**
     * 加入事件
     */
    private SocketManager.EventListener mOnJoin = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            if (XiaojsConfig.DEBUG) {
                Toast.makeText(mContext, "someone enter", Toast.LENGTH_SHORT).show();
            }
            updateContactList(true, args);
        }
    };

    /**
     * 退出事件
     */
    private SocketManager.EventListener mOnLeave = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            if (XiaojsConfig.DEBUG) {
                Toast.makeText(mContext, "someone exit", Toast.LENGTH_SHORT).show();
            }
            updateContactList(false, args);
        }
    };

    /**
     * 更新联系人列表
     */
    private void updateContactList(boolean add, Object... args) {
        if (args == null || args.length == 0) {
            return;
        }

        Attendee attendee = ClassroomBusiness.parseSocketBean(args[0], Attendee.class);
        if (attendee != null && mLiveCollection != null) {
            if (mLiveCollection.attendees == null) {
                mLiveCollection.attendees = new ArrayList<Attendee>();
            }

            //refresh list
            synchronized (MSG_LIST_LOCK) {
                if (add) {
                    //add
                    if (mLiveCollection.attendees.contains(attendee)) {
                        return;
                    }
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

        setEmptyContactView();
    }

    /**
     * 接收到消息
     */
    private SocketManager.EventListener mOnReceiveTalk = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            if (mContext instanceof Activity && args != null && args.length > 0) {
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(mContext, "接收到消息", Toast.LENGTH_SHORT).show();
                }
                //TODO fix同一条消息多次回调?
                handleReceivedMsg(args);
            }
        }
    };

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
            talkItem.time = new Date(receiveBean.time);
            talkItem.body = new cn.xiaojs.xma.model.live.TalkItem.TalkContent();
            talkItem.from = new cn.xiaojs.xma.model.live.TalkItem.TalkPerson();
            talkItem.body.text = receiveBean.body.text;
            talkItem.body.contentType = receiveBean.body.contentType;
            talkItem.from.accountId = receiveBean.from;
            talkItem.from.name = getNameByAccountId(receiveBean.from);

            int criteria = PEER_TALK;
            try {
                criteria = Integer.parseInt(receiveBean.to);
            } catch (NumberFormatException e) {
            }
            updateTalkMsgData(criteria, talkItem, mTalkCriteria == criteria);
            if (mOnTalkMsgListener != null) {
                mOnTalkMsgListener.onTalkMsgReceived(talkItem);
                if (mDrawerOpened) {
                    if ((mPeerTalkAccountId == null && talkItem.from.accountId != null) || (mPeerTalkAccountId != null &&
                            !mPeerTalkAccountId.equals(talkItem.from.accountId))) {
                        updateUnreadMsgCount(criteria, talkItem);
                    }
                } else {
                    updateUnreadMsgCount(criteria, talkItem);
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * 默认是发送文本
     */
    private void sendMsg() {
        sendMsg(Communications.ContentType.TEXT, null);
    }

    public void sendImg(Attendee attendee, String content) {
        String text = content;
        TalkItem talkItem = new TalkItem();
        //file myself info
        fillMyselfInfo(talkItem);

        talkItem.body = new TalkItem.TalkContent();
        talkItem.body.text = text;
        talkItem.body.contentType = Communications.ContentType.STYLUS;

        long sendTime = System.currentTimeMillis();
        talkItem.time = new Date(sendTime);

        //update task message info
        int criteria = attendee == null ? MULTI_TALK : PEER_TALK;
        updateTalkMsgData(criteria, talkItem, true);

        //send socket info
        TalkBean talkBean = new TalkBean();
        talkBean.from = mMyAccountId;
        talkBean.body = new TalkBean.TalkContent();
        talkBean.body.text = text;
        talkBean.body.contentType = Communications.ContentType.STYLUS;
        talkBean.time = sendTime;
        if (attendee == null) {
            talkBean.to = String.valueOf(Communications.TalkType.OPEN);
        } else {
            talkBean.to = attendee.accountId;
        }
        if (talkBean != null) {
            String event = Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.TALK);
            SocketManager.emit(event, talkBean, new SocketManager.AckListener() {
                @Override
                public void call(final Object... args) {
                    handSendResponse(args);
                }
            });
        }
    }

    private void sendMsg(int type, String content) {
        if (mUser == Constants.User.STUDENT && !mDiscussionSwitcher.isChecked()) {
            //R.string.close_cr_discussion
            return;
        }

        String text = content;
        if (TextUtils.isEmpty(content)) {
            text = mMsgInputEdt.getText().toString();
        }
        if (TextUtils.isEmpty(text)) {
            return;
        }

        TalkItem talkItem = new TalkItem();
        //file myself info
        fillMyselfInfo(talkItem);

        talkItem.body = new TalkItem.TalkContent();
        talkItem.body.text = text;
        talkItem.body.contentType = type;

        long sendTime = System.currentTimeMillis();
        talkItem.time = new Date(sendTime);

        //update task message info
        updateTalkMsgData(mTalkCriteria, talkItem, true);

        //send socket info
        TalkBean talkBean = new TalkBean();
        talkBean.from = mMyAccountId;
        talkBean.body = new TalkBean.TalkContent();
        talkBean.body.text = text;
        talkBean.body.contentType = type;
        talkBean.time = sendTime;
        switch (mTalkCriteria) {
            case MULTI_TALK:
                talkBean.to = String.valueOf(Communications.TalkType.OPEN);
                break;
            case TEACHING_TALK:
                talkBean.to = String.valueOf(Communications.TalkType.FACULTY);
                break;
            case PEER_TALK:
                try {
                    talkBean.to = mPeerTalkAccountId;
                } catch (Exception e) {

                }
                break;
        }
        if (talkBean != null) {
            String event = Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.TALK);
            SocketManager.emit(event, talkBean, new SocketManager.AckListener() {
                @Override
                public void call(final Object... args) {
                    //TalkResponse
                    handSendResponse(args);
                }
            });
        }

        //reset text
        mMsgInputEdt.setText("");
    }

    /**
     * 处理消息发送的回调信息
     */
    private void handSendResponse(Object... args) {
        if (args == null || args.length == 0) {
            return;
        }

        try {
            TalkResponse talkResponse = null;
            talkResponse = ClassroomBusiness.parseSocketBean(args[0], TalkResponse.class);

            if (talkResponse == null) {
                return;
            }


            if (XiaojsConfig.DEBUG) {
                if (talkResponse.result) {
                    Toast.makeText(mContext, "消息发送成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "消息发送失败", Toast.LENGTH_SHORT).show();
                    //TODO resend
                }
            }
        } catch (Exception e) {

        }
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
        if (accountId != null && mLiveCollection != null && mLiveCollection.attendees != null) {
            for (Attendee attendee : mLiveCollection.attendees) {
                if (accountId != null && accountId.equals(attendee.accountId)) {
                    return attendee.name;
                }
            }
        }

        return null;
    }

    private TextWatcher mSearchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            searchContactFromLocal(s.toString());
        }
    };

    private void searchContactFromLocal(String searchTxt) {
        if (mContactBookAdapter == null || mLiveCollection == null ||
                mLiveCollection.attendees == null || mLiveCollection.attendees.isEmpty()) {
            return;
        }

        if (TextUtils.isEmpty(searchTxt)) {
            mContactBookAdapter.setData(mLiveCollection);
            return;
        }

        if (mSearchLiveCollection == null) {
            mSearchLiveCollection = new LiveCollection<Attendee>();

            mSearchLiveCollection.current = mLiveCollection.current;
            mSearchLiveCollection.open = mLiveCollection.open;
            mSearchLiveCollection.total = mLiveCollection.total;
            mSearchLiveCollection.attendees = new ArrayList<Attendee>();
        }

        mSearchLiveCollection.attendees.clear();
        for (Attendee attendee : mLiveCollection.attendees) {
            if (attendee.name != null && attendee.name.contains(searchTxt)) {
                mSearchLiveCollection.attendees.add(attendee);
            }
        }

        mContactBookAdapter.setData(mSearchLiveCollection);
    }

    @Override
    protected void onPanelClosed() {
        mSearchEdt.setText("");
        if (mLiveCollection != null && mContactBookAdapter != null) {
            mContactBookAdapter.setData(mLiveCollection);
        }
    }

    private TalkMsgAdapter getPeekTalkMsgAdapter(String accountId) {
        TalkMsgAdapter adapter = null;
        if (mPeerTalkMsgAdapterMap.containsKey(accountId)) {
            adapter = mPeerTalkMsgAdapterMap.get(accountId);
        } else {
            LiveCriteria peerCriteria = new LiveCriteria();
            peerCriteria.to = accountId;
            adapter = new TalkMsgAdapter(mContext, mTicket, peerCriteria, mTalkMsgLv);
            mPeerTalkMsgAdapterMap.put(accountId, adapter);
            adapter.setOnImageClickListener(mOnImageClickListener);
        }

        return adapter;
    }

    private void addMyself2Attendees(LiveCollection<Attendee> liveCollection) {
        if (mLiveCollection == null) {
            return;
        }

        if (liveCollection.attendees == null) {
            liveCollection.attendees = new ArrayList<Attendee>();
        }

        Attendee mySelf = new Attendee();
        mySelf.accountId = AccountDataManager.getAccountID(mContext);
        mySelf.name = XiaojsConfig.mLoginUser != null ? XiaojsConfig.mLoginUser.getName() : null;
        //TODO 怎么判断是自己是否老师
        if (!liveCollection.attendees.contains(mySelf)) {
            liveCollection.attendees.add(0, mySelf);
        }

        mSearchEdt.setText(liveCollection.current + "/" + (liveCollection.total + 1));
    }

}

