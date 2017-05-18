package cn.xiaojs.xma.ui.classroom.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.ui.base.BaseFragment;

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

    private int mTalkCriteria = TalkPresenter.MULTI_TALK;
    private TalkPresenter mTalkPresenter;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_embed_talk, null);
    }

    @Override
    protected void init() {
        initParams();
        initView();

        //load discussion
        mTalkPresenter.switchTalkTab(TalkPresenter.MULTI_TALK, null);
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
        mTalkPresenter = new TalkPresenter(mContext, mTalkMsgLv, mTalkNameTv, mTicket);
    }

    private void initView() {
        mTalkMsgLv.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        mTalkMsgLv.getRefreshableView().setScrollBarStyle(AbsListView.SCROLLBARS_INSIDE_INSET);
        mTalkMsgLv.getRefreshableView().setFastScrollEnabled(true);
        mTalkMsgLv.getRefreshableView().setDividerHeight(0);

        mTalkNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTalkCriteria == TalkPresenter.PEER_TALK) {
                    mTalkNameTv.setText(R.string.cr_talk_discussion);
                    mTalkNameTv.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mTalkPresenter.switchTalkTab(TalkPresenter.MULTI_TALK, null);
                }
            }
        });
    }

    /**
     * 切换到一对一聊天
     */
    public void switchPeerTalk(Attendee attendee) {
        mTalkCriteria = TalkPresenter.PEER_TALK;
        mTalkNameTv.setText(attendee.name);
        mTalkNameTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_back_pressed, 0, 0, 0);
        mTalkPresenter.switchTalkTab(TalkPresenter.PEER_TALK, attendee.accountId);
    }

    /**
     * 默认是发送文本
     */
    public void sendMsg() {
        sendMsg(Communications.ContentType.TEXT, null);
    }

    public void sendImg(Attendee attendee, String content) {
        mTalkPresenter.sendImg(attendee, mTalkCriteria, content);
    }

    public void sendMsg(int type, String content) {
        mTalkPresenter.sendMsg(type, content);
    }
}
