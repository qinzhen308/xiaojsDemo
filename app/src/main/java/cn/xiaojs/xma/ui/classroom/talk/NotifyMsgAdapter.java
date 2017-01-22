package cn.xiaojs.xma.ui.classroom.talk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.widget.RoundedImageView;

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
 * Date:2017/1/22
 * Desc:
 *
 * ======================================================================================== */

public class NotifyMsgAdapter extends AbsSwipeAdapter<TalkItem, NotifyMsgAdapter.Holder> {
    private String mTicket;
    private LiveCriteria mLiveCriteria;

    public NotifyMsgAdapter(Context context, String ticket, PullToRefreshSwipeListView listView) {
        super(context, listView);

        mTicket = ticket;
        mLiveCriteria = new LiveCriteria();
        mLiveCriteria.to = String.valueOf(Communications.TalkType.SYSTEM);
    }

    @Override
    protected void setViewContent(Holder holder, TalkItem bean, int position) {
        Glide.with(mContext).load(bean.from.avatar).error(R.drawable.default_avatar).into(holder.portrait);
        holder.msgContent.setText(bean.body != null ? bean.body.text : null);
    }

    @Override
    protected View createContentView(int position) {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_cr_notify_msg_item, null);
    }

    @Override
    protected Holder initHolder(View v) {
        Holder holder = new Holder(v);
        holder.portrait = (RoundedImageView) v.findViewById(R.id.portrait);
        holder.msgContent = (TextView) v.findViewById(R.id.msg_content);
        return holder;
    }

    @Override
    protected void doRequest() {
        LiveManager.getTalks(mContext, mTicket, mLiveCriteria, mPagination, new APIServiceCallback<CollectionPage<TalkItem>>() {
            @Override
            public void onSuccess(CollectionPage<TalkItem> object) {
                Toast.makeText(mContext, "获通知取消息成功", Toast.LENGTH_SHORT).show();
                NotifyMsgAdapter.this.onSuccess(object.objectsOfPage);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(mContext, "获通知取消息失败", Toast.LENGTH_SHORT).show();
                NotifyMsgAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    class Holder extends BaseHolder {
        RoundedImageView portrait;
        //TextView name;
        //TextView time;
        TextView msgContent;

        public Holder(View view) {
            super(view);
        }
    }
}
