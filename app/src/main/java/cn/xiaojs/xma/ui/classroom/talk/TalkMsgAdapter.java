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
 * Date:2016/12/20
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsChatAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.TimeUtil;

public class TalkMsgAdapter extends AbsChatAdapter<TalkItem, TalkMsgAdapter.Holder> {
    public final static int TYPE_MY_SPEAKER = 0;
    public final static int TYPE_OTHER_SPEAKER = 1;

    private Context mContext;
    private String mTicket;
    private LiveCriteria mLiveCriteria;

    public TalkMsgAdapter(Context context, String ticket, LiveCriteria liveCriteria, PullToRefreshListView listView) {
        super(context, listView);
        mContext = context;
        mTicket = ticket;
        mLiveCriteria = liveCriteria;
    }

    public TalkMsgAdapter(Context context, String ticket, LiveCriteria liveCriteria, PullToRefreshListView listView, AbsListView.OnScrollListener listener) {
        super(context, listView);
        mContext = context;
        scrollListener = listener;
        mTicket = ticket;
        mLiveCriteria = liveCriteria;
    }

    public void addData(TalkItem talkItem) {

    }

    @Override
    protected void setViewContent(Holder holder, TalkItem bean, int position) {
        //TODO test
        //holder.portrait.setImageResource(R.drawable.default_portrait);
        Glide.with(mContext).load(bean.from.avatar).into(holder.portrait);
        holder.name.setText(bean.from.name);
        holder.msg.setText(bean.body != null ? bean.body.text : null);
        holder.time.setText(TimeUtil.format(bean.time, TimeUtil.TIME_HH_MM_SS));
    }

    @Override
    public int getItemViewType(int position) {
        List<TalkItem> talkItems = getList();
        TalkItem item = null;
        if (talkItems == null || (item = talkItems.get(position)) == null) {
            Logger.i("task items is empty");
            return TYPE_MY_SPEAKER;
        }

        if (item == null || item.from == null) {
            Logger.i("item is empty");
            return TYPE_MY_SPEAKER;
        }

        return isMyself(item.from.accountId) ? TYPE_MY_SPEAKER : TYPE_OTHER_SPEAKER;
    }

    @Override
    protected View createContentView(int position) {
        int type = getItemViewType(position);
        View v = null;
        switch (type) {
            case TYPE_MY_SPEAKER:
                v = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_my_speaker_item, null);
                break;
            case TYPE_OTHER_SPEAKER:
                v = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_other_speaker_item, null);
                break;
        }
        return v;
    }

    @Override
    protected Holder initHolder(View v) {
        Holder holder = new Holder(v);
        holder.portrait = (RoundedImageView) v.findViewById(R.id.portrait);
        holder.name = (TextView) v.findViewById(R.id.name);
        holder.time = (TextView) v.findViewById(R.id.time);
        holder.msg = (TextView) v.findViewById(R.id.msg);
        return holder;
    }

    @Override
    protected void doRequest() {
        //onSuccess(getTalkList());
        LiveManager.getTalks(mContext, mTicket, mLiveCriteria, mPagination, new APIServiceCallback<CollectionPage<TalkItem>> () {
            @Override
            public void onSuccess(CollectionPage<TalkItem> object) {
                TalkMsgAdapter.this.onSuccess(object.objectsOfPage);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(mContext, "获取消息失败", Toast.LENGTH_SHORT).show();
                TalkMsgAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class Holder extends BaseHolder {
        RoundedImageView portrait;
        TextView name;
        TextView time;
        TextView msg;

        public Holder(View view) {
            super(view);
        }
    }

    private boolean isMyself(String currAccountId) {
        String accountId = AccountDataManager.getAccountID(mContext);
        return accountId != null && accountId.equals(currAccountId);
    }
}
