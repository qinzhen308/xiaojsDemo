package cn.xiaojs.xma.ui.classroom.talk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.main.LiveCtlSessionManager;

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
 * Date:2017/5/3
 * Desc:
 *
 * ======================================================================================== */

public class EmbedTalkFragment extends BaseFragment{
    //talk
    @BindView(R.id.talk_view)
    View mTalkView;
    @BindView(R.id.talk_name)
    TextView mTalkNameTv;
    @BindView(R.id.talk_list_view)
    PullToRefreshListView mTalkMsgLv;

    private CtlSession mCtlSession;
    private Constants.UserMode mUserMode;
    private String mTicket;

    private TalkPresenter mTalkPresenter;
    private ExitPeerTalkListener mExitPeerTalkListener;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_embed_talk, null);
    }

    @Override
    protected void init() {
        initParams();
        initView();

        //load discussion
        mTalkPresenter.switchTalkTab(TalkManager.TYPE_MSG_MUlTI_TAlk, null);
    }

    private void initParams() {
        Bundle data = getArguments();
        if (data != null) {
            mCtlSession = (CtlSession) data.getSerializable(Constants.KEY_CTL_SESSION);
            if (mCtlSession != null) {
                mUserMode = ClassroomBusiness.getUserByCtlSession(mCtlSession);
            }
        }


        mTicket = LiveCtlSessionManager.getInstance().getTicket();
        mTalkPresenter = new TalkPresenter(mContext, mTalkMsgLv, mTalkNameTv);
    }

    public void setExitPeerTalkListener(ExitPeerTalkListener listener) {
        mExitPeerTalkListener = listener;
    }

    private void initView() {
        mTalkMsgLv.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        mTalkMsgLv.getRefreshableView().setScrollBarStyle(AbsListView.SCROLLBARS_INSIDE_INSET);
        mTalkMsgLv.getRefreshableView().setFastScrollEnabled(true);
        mTalkMsgLv.getRefreshableView().setDividerHeight(0);

        mTalkNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTalkPresenter.getTalkCriteria() == TalkManager.TYPE_PEER_TALK) {
                    TalkManager.getInstance().setPeekTalkingAccount(null);
                    mTalkNameTv.setText(R.string.cr_talk_discussion);
                    mTalkNameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mTalkPresenter.switchMsgMultiTalk();
                    if (mExitPeerTalkListener != null) {
                        mExitPeerTalkListener.onExitTalk(TalkManager.TYPE_PEER_TALK);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mTalkPresenter != null) {
            mTalkPresenter.release();
        }

        TalkManager.getInstance().setPeekTalkingAccount(null);
    }

    /**
     * 切换到一对一聊天
     */
    public void switchPeerTalk(Attendee attendee) {
        TalkManager.getInstance().setPeekTalkingAccount(attendee.accountId);
        mTalkNameTv.setText(attendee.name);
        mTalkNameTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back_pressed, 0, 0, 0);
        mTalkPresenter.switchTalkTab(TalkManager.TYPE_PEER_TALK, attendee.accountId);
    }

    public void updateMultiTalk() {
        mTalkPresenter.switchTalkTab(TalkManager.TYPE_MSG_MUlTI_TAlk, null);
    }

    public void updatePeerTalk(String accountId) {
        mTalkPresenter.switchTalkTab(TalkManager.TYPE_PEER_TALK, accountId);
    }
}
