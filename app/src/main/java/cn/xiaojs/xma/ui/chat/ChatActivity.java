package cn.xiaojs.xma.ui.chat;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2017/1/9
 * Desc:聊天界面
 *
 * ======================================================================================== */

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.ui.base.BaseActivity;

public class ChatActivity extends BaseActivity {

    public static final String KEY_CHAT_TITLE = "key_chat_title";

    @BindView(R.id.chat_list)
    PullToRefreshListView mList;
    @BindView(R.id.chat_input)
    EditText mInput;
    @BindView(R.id.chat_send)
    Button mSend;

    private ChatAdapter mAdapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_chat);
        setRightImage(R.drawable.opera_selector);
        setRightImage2(R.drawable.clazz_info_selector);
        Intent intent = getIntent();
        if (intent != null) {
            String title = intent.getStringExtra(KEY_CHAT_TITLE);
            if (!TextUtils.isEmpty(title)) {
                setMiddleTitle(title);
            } else {
                setMiddleTitle("聊天");
            }
        }

        mList.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        mList.getRefreshableView().setScrollBarStyle(AbsListView.SCROLLBARS_INSIDE_INSET);
        mList.getRefreshableView().setFastScrollEnabled(true);

        mSend.setEnabled(false);
        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mSend.setEnabled(true);
                } else {
                    mSend.setEnabled(false);
                }
            }
        });

        mAdapter = new ChatAdapter(this, mList);
        mList.setAdapter(mAdapter);

    }

    @OnClick({R.id.left_image, R.id.right_image, R.id.right_image2, R.id.chat_send})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image://更多
                break;
            case R.id.right_image2://教室信息
                break;
            case R.id.chat_send:
                send();
                break;
        }
    }

    private void send() {
        String message = mInput.getText().toString();
        if (!TextUtils.isEmpty(message) && mAdapter != null) {
            ChatBean bean = new ChatBean();
            bean.messageType = ChatBean.TYPE_MINE;
            bean.content = message;
            mAdapter.add(bean);
            mInput.setText("");
        }
    }

}
