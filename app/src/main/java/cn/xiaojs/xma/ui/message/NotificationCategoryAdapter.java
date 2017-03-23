package cn.xiaojs.xma.ui.message;
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
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Ctl;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.NotificationDataManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.GENotificationsResponse;
import cn.xiaojs.xma.model.Notification;
import cn.xiaojs.xma.model.NotificationCriteria;
import cn.xiaojs.xma.model.account.DealAck;
import cn.xiaojs.xma.util.TimeUtil;

import butterknife.BindView;

public class NotificationCategoryAdapter extends AbsSwipeAdapter<Notification,NotificationCategoryAdapter.Holder> {

    private NotificationCriteria criteria;

    public NotificationCategoryAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    public void setCriteria(NotificationCriteria criteria){
        this.criteria = criteria;
    }

    @Override
    protected void setViewContent(Holder holder, final Notification bean, int position) {
        holder.reset();
        //已读
        if (!TextUtils.isEmpty(bean.state) && bean.state.equalsIgnoreCase(Platform.NotificationState.DISMISSED)){
            holder.content.setTextColor(mContext.getResources().getColor(R.color.common_text));
        }else {
            holder.content.setTextColor(mContext.getResources().getColor(R.color.font_black));
        }
        holder.time.setText(TimeUtil.getTimeByNow(bean.createdOn));
        holder.content.setText(bean.body);
        if (bean.actions != null && bean.actions.length == 2){
            holder.opera2.setText(bean.actions[0].getTitle());
            holder.opera1.setText(bean.actions[1].getTitle());
            holder.showBottom();

            holder.opera2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //同意

                    dealNotify(bean.initiator.id, Ctl.ACKDecision.ACKNOWLEDGE);
                }
            });

            holder.opera1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //拒绝
                    dealNotify(bean.initiator.id, Ctl.ACKDecision.REFUSED);
                }
            });

        }else {
            holder.hideBottom();
        }

    }

    @Override
    protected View createContentView(int position) {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_message_category_list_item,null);
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        NotificationDataManager.requestNotifications(mContext, criteria, mPagination, new APIServiceCallback<GENotificationsResponse>() {
            @Override
            public void onSuccess(GENotificationsResponse response) {
                if (response != null){
                    NotificationCategoryAdapter.this.onSuccess(response.objectsOfPage);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                NotificationCategoryAdapter.this.onFailure(errorCode,errorMessage);
            }
        });
    }

    @Override
    protected void onDataItemClick(int position,Notification bean) {
        bean.read = true;
        notifyDataSetChanged();
    }

    @Override
    protected boolean leftSwipe() {
        return true;
    }

    @Override
    protected void onAttachSwipe(TextView mark, TextView del) {
        mark.setVisibility(View.GONE);
        setLeftOffset(mContext.getResources().getDimension(R.dimen.px140));
    }

    @Override
    protected void onSwipeDelete(final int position) {
        Notification notification = getItem(position);
        if (notification != null){
            NotificationDataManager.requestDelNotification(mContext, notification.id, new APIServiceCallback() {
                @Override
                public void onSuccess(Object object) {
                    removeItem(position);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {

                }
            });
        }
    }

    class Holder extends BaseHolder{

        @BindView(R.id.message_list_item_time)
        TextView time;
        @BindView(R.id.message_list_item_content)
        TextView content;
        @BindView(R.id.message_list_item_opera1)
        TextView opera1;
        @BindView(R.id.message_list_item_opera2)
        TextView opera2;
        @BindView(R.id.message_list_item_bottom_divider)
        View bottomDivider;
        @BindView(R.id.message_list_item_opera)
        View operaWrapper;

//        @BindView(R.id.message_item_btn_opera1)
//        Button btnOper1;
//        @BindView(R.id.message_item_btn_opera2)
//        Button btnOper2;

        public void reset(){
            operaWrapper.setVisibility(View.VISIBLE);
            bottomDivider.setVisibility(View.VISIBLE);
            opera1.setTextColor(mContext.getResources().getColor(R.color.common_text));
            opera2.setTextColor(mContext.getResources().getColor(R.color.font_blue));
            opera2.setVisibility(View.VISIBLE);
            content.setTextColor(mContext.getResources().getColor(R.color.font_black));
        }

        public void hideBottom(){
            operaWrapper.setVisibility(View.GONE);
            bottomDivider.setVisibility(View.GONE);
        }

        public void showBottom(){
            operaWrapper.setVisibility(View.VISIBLE);
            bottomDivider.setVisibility(View.VISIBLE);
        }

//        @OnClick({R.id.message_list_item_opera1, R.id.message_list_item_opera2})
//        public void onClick(View view) {
//            switch (view.getId()) {
//                case R.id.message_list_item_opera1:
//                    Toast.makeText(mContext,"nnnn1",Toast.LENGTH_SHORT).show();
//                    break;
//                case R.id.message_list_item_opera2:
//                    Toast.makeText(mContext,"nnnn",Toast.LENGTH_SHORT).show();
//                    break;
//            }
//        }

        public Holder(View view) {
            super(view);
        }
    }


    private void dealNotify(String orgId, int decision) {

        DealAck dealAck = new DealAck();
        dealAck.decision = decision;

        showProgress(false);
        AccountDataManager.acknowledgeInvitation(mContext, orgId, dealAck, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                Toast.makeText(mContext,"操作成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(mContext,errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
