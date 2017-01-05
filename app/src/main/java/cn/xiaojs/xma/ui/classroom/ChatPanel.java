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
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import cn.xiaojs.xma.R;

public class ChatPanel extends Panel implements View.OnClickListener, ContactBookAdapter.OnContactBookListener {
    public final static int MODE_CONTACT = 0;
    public final static int MODE_CHAT = 1;

    private ListView mContactBook;
    private ContactBookAdapter mContactBookAdapter;

    private ListView mChatContactLv;
    private ChatSimpleContactAdapter mChatContactAdapter;

    private ListView mChatMsgLv;
    private ChatMsgAdapter mChatMsgAdapter;

    private View mContactView;
    private View mChatView;

    private View mDefaultContactAction;
    private View mManageContactAction;

    private ImageView mAddContact;
    private ImageView mManageContact;
    private ImageView mOpenMsg;

    private TextView mSetAssistantTv;
    private TextView mKickOutTv;
    private TextView mBackTv;

    private ImageView mOpenContact;
    private ImageView mDelChatTab;

    private EditText mMsgInputEdt;
    private TextView mMsgSendTv;

    private int mCurrentMode = MODE_CONTACT;

    private PanelCallback mCallback;

    public ChatPanel(Context context) {
        super(context);
    }

    public ChatPanel with(int mode) {
        mCurrentMode = mode;
        return this;
    }

    public ChatPanel setPanelCallback(PanelCallback callback) {
        mCallback = callback;
        return this;
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
            case MODE_CHAT:
                switchToChat();
                break;
        }
    }

    @Override
    public void initData() {
        switch (mCurrentMode) {
            case MODE_CONTACT:
                setContactBookData(false);
                break;
            case MODE_CHAT:
                setChatSimpleContactData(false);
                break;
        }
    }

    @Override
    public void initChildView(View root) {
        mContactView = root.findViewById(R.id.contact);
        mChatView = root.findViewById(R.id.chat);

        mContactBook = (ListView) root.findViewById(R.id.contact_book);
        mChatContactLv = (ListView) root.findViewById(R.id.chat_simple_contact);
        mChatMsgLv = (ListView)root.findViewById(R.id.chat_msg);

        mOpenContact = (ImageView) root.findViewById(R.id.open_contact);
        mDelChatTab = (ImageView) root.findViewById(R.id.del_chat_tab);

        mDefaultContactAction = root.findViewById(R.id.default_contact_action);
        mManageContactAction = root.findViewById(R.id.manage_contact_action);

        mOpenMsg = (ImageView) root.findViewById(R.id.open_chat);
        mAddContact = (ImageView) root.findViewById(R.id.add_contact);
        mManageContact = (ImageView) root.findViewById(R.id.manage_contact);

        mSetAssistantTv = (TextView) root.findViewById(R.id.set_assistant);
        mKickOutTv = (TextView) root.findViewById(R.id.kick_out);
        mBackTv = (TextView) root.findViewById(R.id.back_manage_contact);

        mMsgSendTv = (TextView) root.findViewById(R.id.msg_send);
        mMsgInputEdt = (EditText) root.findViewById(R.id.msg_input);

        mOpenMsg.setOnClickListener(this);
        mOpenContact.setOnClickListener(this);
        mAddContact.setOnClickListener(this);
        mSetAssistantTv.setOnClickListener(this);
        mKickOutTv.setOnClickListener(this);
        mBackTv.setOnClickListener(this);
        mManageContact.setOnClickListener(this);
        mDelChatTab.setOnClickListener(this);
        mMsgSendTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_chat:
                switchToChat();
                setChatSimpleContactData(false);
                setChatMsgData(false);
                break;
            case R.id.open_contact:
                switchToContact();
                setContactBookData(false);
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
            case R.id.del_chat_tab:
                break;
            case R.id.msg_send:
                break;
        }
    }

    private void setContactBookData(boolean dataChanged) {
        if (mContactBookAdapter == null) {
            mContactBookAdapter = new ContactBookAdapter(mContext);
            mContactBookAdapter.setOnContactBookListener(this);
            mContactBook.setAdapter(mContactBookAdapter);
            mContactBook.setDividerHeight(0);
        } else {
            //TODO set data
            if (dataChanged) {
                mContactBookAdapter.notifyDataSetChanged();
            }
        }
    }

    private void switchToChat() {
        mContactView.setVisibility(View.GONE);
        mChatView.setVisibility(View.VISIBLE);
    }

    private void switchToContact() {
        mContactView.setVisibility(View.VISIBLE);
        mChatView.setVisibility(View.GONE);
    }

    private void setChatSimpleContactData(boolean dataChanged) {
        if (mChatContactAdapter == null) {
            mChatContactAdapter = new ChatSimpleContactAdapter(mContext);
            mChatContactLv.setAdapter(mChatContactAdapter);
            mChatContactLv.setDividerHeight(0);
        } else {
            //TODO set data
            if (dataChanged) {
                mChatContactAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setChatMsgData(boolean dataChanged) {
        if (mChatMsgAdapter == null) {
            mChatMsgAdapter = new ChatMsgAdapter(mContext);
            mChatMsgLv.setAdapter(mChatMsgAdapter);
            mChatMsgLv.setDividerHeight(0);
        } else {
            //TODO set data
            if (dataChanged) {
                mChatMsgAdapter.notifyDataSetChanged();
            }
        }
    }

    private void enterContactManagement() {
        mDefaultContactAction.setVisibility(View.GONE);
        mManageContactAction.setVisibility(View.VISIBLE);
        if (mContactBookAdapter != null) {
            mContactBookAdapter.enterManagementMode();
        }
    }

    private void exitContactManagement() {
        mDefaultContactAction.setVisibility(View.VISIBLE);
        mManageContactAction.setVisibility(View.GONE);
        if (mContactBookAdapter != null) {
            mContactBookAdapter.exitManagementMode();
        }
    }

    @Override
    public void onPortraitClick() {
        switchToChat();
        setChatSimpleContactData(false);
        setChatMsgData(false);
    }
}

