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

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsChatAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.TimeUtil;

public class TalkMsgAdapter extends AbsChatAdapter<TalkBean, TalkMsgAdapter.Holder> {
    public final static int TYPE_MY_SPEAKER = 0;
    public final static int TYPE_OTHER_SPEAKER = 1;

    private Context mContext;

    public TalkMsgAdapter(Context context, PullToRefreshListView listView) {
        super(context, listView);
        mContext = context;
    }

    public TalkMsgAdapter(Context context, PullToRefreshListView listView, AbsListView.OnScrollListener listener) {
        super(context, listView);
        mContext = context;
        scrollListener = listener;
    }

    @Override
    protected void setViewContent(Holder holder, TalkBean bean, int position) {
        holder.portrait.setImageResource(R.drawable.default_portrait);
        holder.name.setText(bean.name);
        holder.msg.setText(bean.content);
        holder.time.setText(TimeUtil.format(bean.time, TimeUtil.TIME_HH_MM_SS));
    }

    @Override
    public int getItemViewType(int position) {
        return position % 2 == 0 ? TYPE_MY_SPEAKER : TYPE_OTHER_SPEAKER;
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
        onSuccess(getTalkList());
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

    private List<TalkBean> getTalkList() {
        List<TalkBean> talkBeanList = new ArrayList<TalkBean>();
        for (int i = 0; i < 10; i++) {
            TalkBean talkBean = new TalkBean();
            talkBean.name = "学生" + i;
            talkBean.time = System.currentTimeMillis();
            talkBean.content = "今天要上课e" + i;
            talkBeanList.add(talkBean);
        }

        return talkBeanList;
    }
}
