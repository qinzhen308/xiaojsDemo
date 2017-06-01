package cn.xiaojs.xma.ui.classroom.talk;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.model.live.Attendee;

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

public class EmbedTalkFragment extends Fragment {
    //talk
    @BindView(R.id.talk_view)
    View mTalkView;
    @BindView(R.id.talk_name)
    TextView mTalkNameTv;
    @BindView(R.id.talk_list_view)
    PullToRefreshListView mTalkMsgLv;

    private TalkPresenter mTalkPresenter;
    private ExitPeerTalkListener mExitPeerTalkListener;
    private Context mContext;
    private Unbinder mBinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_embed_talk, null);
        mBinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    protected void init() {
        initParams();
        initView();

        //load discussion
        mTalkPresenter.switchTalkTab(TalkManager.TYPE_MSG_MUlTI_TAlk, null);
    }

    private void initParams() {
        mTalkPresenter = new TalkPresenter(mContext, mTalkMsgLv, mTalkNameTv);
    }

    public void setExitPeerTalkListener(ExitPeerTalkListener listener) {
        mExitPeerTalkListener = listener;
    }

    private void initView() {
        mTalkMsgLv.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        //mTalkMsgLv.getRefreshableView().setScrollBarStyle(AbsListView.SCROLLBARS_OUTSIDE_OVERLAY);
        //mTalkMsgLv.getRefreshableView().setFastScrollEnabled(true); //set true, fast scroll block will be shown
        mTalkMsgLv.getRefreshableView().setDividerHeight(0);

        mTalkNameTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTalkPresenter.getTalkCriteria() == TalkManager.TYPE_PEER_TALK) {
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
    public void onDestroyView() {
        super.onDestroyView();

        if (mBinder != null) {
            mBinder.unbind();
        }
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
        mTalkPresenter.switchPeerTalk(attendee, true);
    }

    public void updateMultiTalk() {
        mTalkPresenter.switchTalkTab(TalkManager.TYPE_MSG_MUlTI_TAlk, null);
    }

    public void updatePeerTalk(String accountId) {
        mTalkPresenter.switchTalkTab(TalkManager.TYPE_PEER_TALK, accountId);
    }
}
