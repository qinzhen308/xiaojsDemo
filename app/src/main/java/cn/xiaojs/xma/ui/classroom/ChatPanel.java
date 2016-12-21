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
import android.widget.ImageView;
import android.widget.ListView;

import cn.xiaojs.xma.R;

public class ChatPanel extends Panel implements View.OnClickListener {
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

    private ImageView mAddContact;
    private ImageView mManageContact;
    private ImageView mOpenMsg;

    private ImageView mOpenContact;

    private int mCurrentMode = MODE_CONTACT;

    public ChatPanel(Context context) {
        super(context);
    }

    public ChatPanel with(int mode) {
        mCurrentMode = mode;
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

        mOpenMsg = (ImageView) root.findViewById(R.id.open_chat);
        mAddContact = (ImageView) root.findViewById(R.id.add_contact);
        mManageContact = (ImageView) root.findViewById(R.id.manage_contact);

        mOpenMsg.setOnClickListener(this);
        mOpenContact.setOnClickListener(this);
        mAddContact.setOnClickListener(this);
        mManageContact.setOnClickListener(this);
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
                break;
            case R.id.manage_contact:
                break;
        }
    }

    private void setContactBookData(boolean dataChanged) {
        if (mContactBookAdapter == null) {
            mContactBookAdapter = new ContactBookAdapter(mContext);
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
}
