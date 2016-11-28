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
 * Date:2016/11/16
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.benyuan.xiaojs.common.pulltorefresh.core.PullToRefreshSwipeListView;
import com.benyuan.xiaojs.data.NotificationDataManager;
import com.benyuan.xiaojs.data.api.service.APIServiceCallback;
import com.benyuan.xiaojs.model.GNOResponse;
import com.benyuan.xiaojs.model.Notification;
import com.benyuan.xiaojs.model.NotificationCategory;
import com.benyuan.xiaojs.ui.widget.MessageImageView;
import com.benyuan.xiaojs.util.TimeUtil;

import java.util.List;

import butterknife.BindView;

public class NotificationAdapter extends AbsSwipeAdapter<NotificationCategory,NotificationAdapter.Holder> {

    private NotificationFragment mFragment;

    public NotificationAdapter(Context context, PullToRefreshSwipeListView listView, NotificationFragment fragment) {
        super(context, listView);
        mFragment = fragment;
    }

    @Override
    protected void initParam() {
    }

    @Override
    protected void setViewContent(Holder holder, NotificationCategory bean, int position) {
        holder.title.setText(bean.remarks);
        if (bean.notifications != null && bean.notifications.size() > 0){
            Notification notification = bean.notifications.get(0);
            holder.content.setText(notification.body);
            holder.time.setText(TimeUtil.format(notification.createdOn,TimeUtil.TIME_YYYY_MM_DD_HH_MM));
        }
    }

    @Override
    protected View createContentView(int position) {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_message_list_item,null);
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {

        NotificationDataManager.requestNotificationsOverview(mContext, mPagination, new APIServiceCallback<GNOResponse>() {
            @Override
            public void onSuccess(GNOResponse object) {
                if (object.categories != null){
                    List<NotificationCategory> platform = NotificationBusiness.getPlatformMessageCategory(object.categories);
                    if (platform != null){//从返回的数据中提出平台类型消息，放到头部显示区域
                        object.categories.removeAll(platform);
                        if (mFragment != null){
                            mFragment.notifyHeader(platform);
                        }
                    }

                }

                NotificationAdapter.this.onSuccess(object.categories);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                NotificationAdapter.this.onFailure(errorCode,errorMessage);
            }
        });
    }

    @Override
    protected boolean showEmptyView() {
        return false;
    }

    @Override
    protected boolean patchedHeader() {

        return true;
    }

    class Holder extends BaseHolder{
        @BindView(R.id.message_image)
        MessageImageView image;
        @BindView(R.id.message_title)
        TextView title;
        @BindView(R.id.message_time)
        TextView time;
        @BindView(R.id.message_content)
        TextView content;
        @BindView(R.id.message_head)
        View head;
        @BindView(R.id.message_top_divider)
        View topDivider;
        @BindView(R.id.message_bottom_divider)
        View bottomDivider;

        public void normal(){
            head.setVisibility(View.GONE);
            topDivider.setVisibility(View.GONE);
            bottomDivider.setVisibility(View.GONE);
        }

        public void special(){
            head.setVisibility(View.VISIBLE);
            topDivider.setVisibility(View.VISIBLE);
            bottomDivider.setVisibility(View.VISIBLE);
        }
        public Holder(View view) {
            super(view);
        }
    }
}
