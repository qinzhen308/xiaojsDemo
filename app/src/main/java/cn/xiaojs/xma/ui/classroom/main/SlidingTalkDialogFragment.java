package cn.xiaojs.xma.ui.classroom.main;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.ui.widget.ClosableAdapterSlidingLayout;
import cn.xiaojs.xma.ui.widget.SheetFragment;

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
 * Date:2017/5/9
 * Desc:
 *
 * ======================================================================================== */

public class SlidingTalkDialogFragment extends SheetFragment implements View.OnClickListener {
    //talk
    @BindView(R.id.talk_view)
    View mTalkView;
    @BindView(R.id.talk_name)
    TextView mTalkNameTv;
    @BindView(R.id.talk_list_view)
    PullToRefreshListView mTalkMsgLv;

    //send bar
    @BindView(R.id.msg_input)
    EditText mMsgInputEdt;
    @BindView(R.id.open_input)
    TextView mOpenInput;
    @BindView(R.id.msg_send)
    TextView mSendBtn;

    private Constants.UserMode mUserMode;
    private String mTicket;

    private int mTalkCriteria = TalkPresenter.MULTI_TALK;
    private TalkPresenter mTalkPresenter;
    private Attendee mAttendee;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        init();
    }

    private void init() {
        Bundle data = getArguments();
        if (data != null) {
            CtlSession session = (CtlSession) data.getSerializable(Constants.KEY_CTL_SESSION);
            if (session != null) {
                mUserMode = ClassroomBusiness.getUserByCtlSession(session);
            }
            mAttendee = (Attendee) data.getSerializable(Constants.KEY_OPEN_TALK_ATTEND);
        }

        mTicket = LiveCtlSessionManager.getInstance().getTicket();
    }

    @Override
    protected View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_sliding_talk, null);
    }

    @Override
    protected void onFragmentShow(DialogInterface dialogInterface) {
        if (mAttendee != null) {
            mTalkPresenter.switchPeerTalk(mAttendee, false);
        } else {
            mTalkPresenter.switchMultiTalk();
        }
    }

    @Override
    protected View getTargetView(View root) {
        return root.findViewById(R.id.talk_name);
    }

    @Override
    protected void setSlideConflictView(ClosableAdapterSlidingLayout horizontalSlidingLayout) {
        horizontalSlidingLayout.setSlideConflictView(horizontalSlidingLayout.findViewById(R.id.talk_list_view));
    }

    @Override
    protected void onViewCreated(View view) {
        mTalkPresenter = new TalkPresenter(mContext, mTalkMsgLv, mTalkNameTv, mTicket);

        mTalkMsgLv.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        mTalkMsgLv.getRefreshableView().setScrollBarStyle(AbsListView.SCROLLBARS_INSIDE_INSET);
        mTalkMsgLv.getRefreshableView().setFastScrollEnabled(true);
        mTalkMsgLv.getRefreshableView().setDividerHeight(0);
        mTalkNameTv.setCompoundDrawables(null, null, null, null);

        mSendBtn.setOnClickListener(this);
        mOpenInput.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_input:
                //SlidingTalkDialogFragment.this.dismiss();
                //ClassroomController.getInstance().openInputText(this);
                break;
            case R.id.msg_send:
                String content = mMsgInputEdt.getText().toString();
                if (!TextUtils.isEmpty(content)) {
                    mTalkPresenter.sendMsg(content);
                    mMsgInputEdt.setText("");
                }

                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case ClassroomController.REQUEST_INPUT:
                    String content = data.getStringExtra(Constants.KEY_MSG_INPUT_TXT);
                    //send msg
                    if (!TextUtils.isEmpty(content)) {
                        mTalkPresenter.sendMsg(content);
                    }
                    break;
            }
        }
    }
}
