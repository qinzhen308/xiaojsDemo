package cn.xiaojs.xma.ui.classroom.whiteboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshBase;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.classroom.talk.InviteFriendAdapter;

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
 * Date:2017/2/22
 * Desc:
 *
 * ======================================================================================== */

public class ShareDoodlePopWindow extends PopupWindow implements InviteFriendAdapter.SelectionListener,
        View.OnClickListener {
    private final static int MODE_UN_CHECK_ALL = 0;
    private final static int MODE_CHECK_ALL = 1;

    private Context mContext;
    private TextView mEmptyView;
    private PullToRefreshSwipeListView mListView;
    private InviteFriendAdapter mAdapter;
    private ImageView mCheckAllBtn;
    private int mCheckMode = MODE_UN_CHECK_ALL;

    public ShareDoodlePopWindow(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        setFocusable(true);
        setOutsideTouchable(true);
        setWindowLayoutMode(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        setClippingEnabled(false);

        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_share_contact_book, null);
        setContentView(v);

        mCheckAllBtn = (ImageView) v.findViewById(R.id.check_all);
        mEmptyView = (TextView) v.findViewById(R.id.empty_view);
        mListView = (PullToRefreshSwipeListView) v.findViewById(R.id.contact_list);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        mCheckAllBtn.setOnClickListener(this);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);

        initData();
    }

    public void initData() {
        if (mAdapter == null) {
            mAdapter = new InviteFriendAdapter(mContext, mListView);
            mAdapter.setSelectionListener(this);

            mListView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onSelectChanged(int selectionCount) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.check_all:
                if (mCheckMode == MODE_UN_CHECK_ALL) {
                    mCheckMode = MODE_CHECK_ALL;
                    mCheckAllBtn.setImageResource(R.drawable.ic_multi_checked);
                    mAdapter.checkAll();
                } else {
                    mCheckMode = MODE_UN_CHECK_ALL;
                    mAdapter.unCheckAll();
                    mCheckAllBtn.setImageResource(R.drawable.ic_multi_no_checked);
                    mCheckAllBtn.setSelected(false);
                }
                break;
        }
    }
}
