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

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.model.NotificationCategory;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.widget.CanInScrollviewListView;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import java.util.List;

import butterknife.BindView;

public class NotificationFragment extends BaseFragment {

    @BindView(R.id.listview)
    AutoPullToRefreshListView mPullList;

    View mHeader;
    CanInScrollviewListView mListView;

    PlatformNotificationAdapter platformMessageAdapter;

    private String[] titles;
    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_message, null);
        return v;
    }

    @Override
    protected void init() {
        titles = mContext.getResources().getStringArray(R.array.platform_message_title);
        mHeader = LayoutInflater.from(mContext).inflate(R.layout.layout_message_header,null);
        mListView = (CanInScrollviewListView) mHeader.findViewById(R.id.list);
        mListView.setNeedDivider(true);
        platformMessageAdapter = new PlatformNotificationAdapter(mContext,titles);
        mListView.setAdapter(platformMessageAdapter);
        mListView.setOnItemClickListener(new CanInScrollviewListView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String title = titles[position];
                NotificationCategory category = platformMessageAdapter.getItem(position);
                String categoryId = "";
                if (category != null){
                    categoryId = category.id;
                }

                Intent intent = new Intent(mContext,NotificationCategoryListActivity.class);
                intent.putExtra(NotificationConstant.KEY_NOTIFICATION_CATEGORY_ID,categoryId);
                intent.putExtra(NotificationConstant.KEY_NOTIFICATION_TITLE,title);
                startActivity(intent);
            }
        });
        mPullList.getRefreshableView().addHeaderView(mHeader);
        mPullList.setAdapter(new NotificationAdapter(mContext,mPullList,this));
    }

    public void notifyHeader(List<NotificationCategory> beans){
        if (platformMessageAdapter != null){
            platformMessageAdapter.setData(beans);
        }
    }

}
