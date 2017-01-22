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
 * Date:2016/12/27
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.model.social.Contact;

public class InviteFriendPanel extends Panel implements InviteFriendAdapter.SelectionListener {
    private TextView mSelectionCountView;
    private PullToRefreshSwipeListView mFriendListView;
    private InviteFriendAdapter mAdapter;

    public InviteFriendPanel(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_invite_friend, null);
    }

    @Override
    public void initChildView(View root) {
        mSelectionCountView = (TextView) root.findViewById(R.id.selected_friends);
        mFriendListView = (PullToRefreshSwipeListView) root.findViewById(R.id.friends);
        mFriendListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
    }

    @Override
    public void initData() {
        if (mAdapter == null) {
            mAdapter = new InviteFriendAdapter(mContext, mFriendListView);
            mAdapter.setSelectionListener(this);

            mFriendListView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onSelectChanged(int selectionCount) {
        if (selectionCount > 0) {
            mSelectionCountView.setVisibility(View.VISIBLE);
            mSelectionCountView.setText(mContext.getString(R.string.invite_selected_friends, selectionCount));
        } else {
            mSelectionCountView.setVisibility(View.INVISIBLE);
        }
    }
}
