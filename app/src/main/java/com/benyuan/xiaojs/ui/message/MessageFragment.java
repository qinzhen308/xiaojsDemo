package com.benyuan.xiaojs.ui.message;

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
 * Date:2016/10/11
 * Desc:
 *
 * ======================================================================================== */

import android.view.LayoutInflater;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.widget.CanInScrollviewListView;
import com.benyuan.xiaojs.util.ToastUtil;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MessageFragment extends BaseFragment {

    @BindView(R.id.listview)
    AutoPullToRefreshListView mPullList;

    View mHeader;
    CanInScrollviewListView mListView;
    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_message, null);
        return v;
    }

    @Override
    protected void init() {
        mHeader = LayoutInflater.from(mContext).inflate(R.layout.layout_message_header,null);
        mListView = (CanInScrollviewListView) mHeader.findViewById(R.id.list);
        List<MessageBean> beans = new ArrayList<>();
        MessageBean b = new MessageBean();
        beans.add(b);
        beans.add(b);
        beans.add(b);
        beans.add(b);
        beans.add(b);
        beans.add(b);

        mListView.setNeedDivider(true);
        mListView.setAdapter(new PlatformMessageAdapter(mContext,beans));
        mListView.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ToastUtil.showToast(mContext,"position = " + position);
            }
        });
        mPullList.getRefreshableView().addHeaderView(mHeader);
        mPullList.setAdapter(new MessageAdapter(mContext,mPullList));
    }


}
