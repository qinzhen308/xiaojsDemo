package cn.xiaojs.xma.ui.classroom.talk;
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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.main.ClassroomController;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.widget.ClosableSlidingLayout;

public class SlideTalkFragment extends BaseFragment {
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

    private ClosableSlidingLayout mClosableSlidingLayout;

    private CtlSession mCtlSession;

    private TalkPresenter mTalkPresenter;
    private Attendee mAttendee;
    private int mFragmentHeight;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    protected View getContentView() {
        mClosableSlidingLayout = (ClosableSlidingLayout) LayoutInflater.from(mContext)
                .inflate(R.layout.fragment_classroom_sliding_talk, null);
        return mClosableSlidingLayout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (mClosableSlidingLayout != null) {
            mClosableSlidingLayout.setTarget(mContent.findViewById(R.id.talk_name));
            mClosableSlidingLayout.setSlideOrientation(ClosableSlidingLayout.SLIDE_FROM_TOP_TO_BOTTOM);

            mClosableSlidingLayout.setSlideListener(new ClosableSlidingLayout.SlideListener() {
                @Override
                public void onClosed() {
                    SlideTalkFragment.this.getFragmentManager().popBackStack();
                }

                @Override
                public void onOpened() {

                }
            });
        }

        mContent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SlideTalkFragment.this.getFragmentManager().popBackStack();
                return false;
            }
        });
    }

    @Override
    protected void init() {
        initParams();
        initView();

        if (mAttendee != null) {
            mTalkPresenter.switchPeerTalk(mAttendee, false);
            TalkManager.getInstance().setPeekTalkingAccount(mAttendee.accountId);
        } else {
            mTalkPresenter.switchMsgMultiTalk();
            TalkManager.getInstance().setPeekTalkingAccount(null);
        }
    }

    @OnClick({R.id.open_input, R.id.msg_send})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.open_input:
                ClassroomController.getInstance().openInputText(this, 0);
                break;
            case R.id.msg_send:

                break;
            default:
                break;
        }
    }

    private void initParams() {
        Bundle data = getArguments();
        mFragmentHeight = mContext.getResources().getDimensionPixelSize(R.dimen.px400);
        if (data != null) {
            mCtlSession = (CtlSession) data.getSerializable(Constants.KEY_CTL_SESSION);
            mAttendee = (Attendee) data.getSerializable(Constants.KEY_TALK_ATTEND);
            mFragmentHeight = data.getInt(Constants.KEY_PAGE_HEIGHT, mFragmentHeight);
        }

        mTalkPresenter = new TalkPresenter(mContext, mTalkMsgLv, mTalkNameTv);
    }

    private void initView() {
        if (mFragmentHeight > 0) {
            ViewGroup.LayoutParams params = mClosableSlidingLayout.getLayoutParams();
            if (params != null) {
                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                params.height = mFragmentHeight;
            } else {
                params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mFragmentHeight);
            }
            mClosableSlidingLayout.setLayoutParams(params);
        }

        mTalkMsgLv.getRefreshableView().setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        mTalkMsgLv.getRefreshableView().setScrollBarStyle(AbsListView.SCROLLBARS_INSIDE_INSET);
        mTalkMsgLv.getRefreshableView().setFastScrollEnabled(true);
        mTalkMsgLv.getRefreshableView().setDividerHeight(0);

        mMsgInputEdt.setVisibility(View.GONE);
        mOpenInput.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case ClassroomController.REQUEST_INPUT:
                    String content = data.getStringExtra(Constants.KEY_MSG_INPUT_TXT);
                    //send msg
                    sendMsg(content);
                    break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        TalkManager.getInstance().setPeekTalkingAccount(null);
    }

    private void sendMsg (String content) {
        if (TextUtils.isEmpty(content)) {
            return;
        }

        if (mAttendee == null || TextUtils.isEmpty(mAttendee.accountId)) {
            TalkManager.getInstance().sendText(content);
        } else {
            TalkManager.getInstance().sendText(mAttendee.accountId, content);
        }
    }
}
