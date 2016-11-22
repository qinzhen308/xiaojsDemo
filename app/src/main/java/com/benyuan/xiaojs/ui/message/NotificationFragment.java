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

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.data.NotificationDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.Duration;
import com.benyuan.xiaojs.model.IgnoreNResponse;
import com.benyuan.xiaojs.model.NotificationCategory;
import com.benyuan.xiaojs.model.NotificationCriteria;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.view.CommonPopupMenu;
import com.benyuan.xiaojs.ui.widget.CanInScrollviewListView;
import com.benyuan.xiaojs.util.DeviceUtil;
import com.benyuan.xiaojs.util.TimeUtil;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class NotificationFragment extends BaseFragment {

    @BindView(R.id.listview)
    AutoPullToRefreshListView mPullList;

    View mHeader;
    CanInScrollviewListView mListView;

    PlatformNotificationAdapter platformMessageAdapter;

    NotificationAdapter notificationAdapter;

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
                ((BaseActivity)mContext).startActivityForResult(intent,NotificationConstant.REQUEST_NOTIFICATION_CATEGORY_LIST);
            }
        });
        mListView.setOnItemLongClickListener(new CanInScrollviewListView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(View view,final int position) {
                CommonPopupMenu menu = new CommonPopupMenu(mContext);
                String[] items = new String[]{"标记为已读"};
                menu.addTextItems(items);
                menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i){
                            case 0:
                                NotificationCategory category = platformMessageAdapter.getItem(i);
                                mark(category,0);
                                break;
                        }
                    }
                });
                int offset = DeviceUtil.getScreenWidth(mContext) / 2;
                menu.show(view,offset);
                return true;
            }
        });
        mPullList.getRefreshableView().addHeaderView(mHeader);
        View view = new View(mContext);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mContext.getResources().getDimensionPixelSize(R.dimen.px100));
        view.setLayoutParams(lp);
        mPullList.getRefreshableView().addFooterView(view);
        notificationAdapter = new NotificationAdapter(mContext,mPullList,this);
        mPullList.setAdapter(notificationAdapter);
        mPullList.getRefreshableView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                int index = position - mPullList.getRefreshableView().getHeaderViewsCount();
                NotificationCategory category = notificationAdapter.getItem(index);
                mark(category,1);
                return true;
            }
        });
    }


    private void mark(final NotificationCategory notificationCategory,final int type){
        NotificationCriteria criteria = new NotificationCriteria();
        if (notificationCategory != null){
            criteria.category = notificationCategory.id;
        }
        Duration duration = new Duration();
        duration.setStart(TimeUtil.original());
        duration.setEnd(TimeUtil.now());
        criteria.duration = duration;
        NotificationDataManager.ignoreNotifications(mContext, criteria, new APIServiceCallback<IgnoreNResponse>() {
            @Override
            public void onSuccess(IgnoreNResponse object) {
                notificationCategory.count = 0;
                if (type == 0){
                    platformMessageAdapter.notifyDataSetChanged();
                }else {
                    notificationAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    public void notifyHeader(List<NotificationCategory> beans){
        if (platformMessageAdapter != null){
            platformMessageAdapter.setData(beans);
        }
    }

    @OnClick({R.id.right_image,R.id.people_image})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.right_image:
                break;
            case R.id.people_image:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case NotificationConstant.REQUEST_NOTIFICATION_CATEGORY_LIST:
                if (resultCode == Activity.RESULT_OK){
                    notificationAdapter.refresh();
                }
                break;
        }
    }
}
