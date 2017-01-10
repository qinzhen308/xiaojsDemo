package cn.xiaojs.xma.ui.chat;
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
 * Date:2017/1/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsChatAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.StringUtil;
import cn.xiaojs.xma.util.ToastUtil;

public class ChatAdapter extends AbsChatAdapter<ChatBean, ChatAdapter.Holder> {

    public ChatAdapter(Context context, PullToRefreshListView listView) {
        super(context, listView);
    }

    public ChatAdapter(Context context, PullToRefreshListView listView, AbsListView.OnScrollListener listener) {
        super(context, listView);
        scrollListener = listener;
    }

    @Override
    protected void setViewContent(Holder holder, ChatBean bean, int position) {
        if (bean.messageType == ChatBean.TYPE_MINE) {
            holder.myself();
            holder.contentMyself.setText(bean.content);
            String errorTips = mContext.getResources().getString(R.string.message_error_tip);
            String again = mContext.getString(R.string.send_again);
            Spannable span = StringUtil.getSpecialString(errorTips, again, mContext.getResources().getColor(R.color.font_blue));
            int start = errorTips.indexOf(again);
            span.setSpan(clickableSpan, start, again.length() + start, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.tips.setText(span);
            holder.tips.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (bean.messageType == ChatBean.TYPE_OTHER) {
            holder.other();
            holder.contentOther.setText(bean.content);
        } else {
            holder.desc();
            holder.desc.setText(bean.content);
        }
    }

    private ClickableSpan clickableSpan = new ClickableSpan() {
        @Override
        public void onClick(View widget) {
            ToastUtil.showToast(mContext, "重新发送");
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    };

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_chat_item, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        return new Holder(view);
    }

    @Override
    protected void doRequest() {
        List<ChatBean> dates = new ArrayList<>();

        ChatBean cb = new ChatBean();
        cb.messageType = 1;
        cb.content = "故人西辞黄鹤楼，烟花三月下扬州" + UUID.randomUUID().toString().substring(0, 4);

        ChatBean cb1 = new ChatBean();
        cb1.messageType = 2;
        cb1.content = "儿童相见不相识，笑问客从何处来" + UUID.randomUUID().toString().substring(0, 4);

        ChatBean cb2 = new ChatBean();
        cb2.messageType = 3;
        cb2.content = "2017-1-1 18:33 " + UUID.randomUUID().toString().substring(0, 4);


        ChatBean cb3 = new ChatBean();
        cb3.messageType = 1;
        cb3.content = "年年岁岁花相似，岁岁年年人不同 " + UUID.randomUUID().toString().substring(0, 4);


        ChatBean cb4 = new ChatBean();
        cb4.messageType = 3;
        cb4.content = "李白进入教室" + UUID.randomUUID().toString().substring(0, 4);

        ChatBean cb5 = new ChatBean();
        cb5.messageType = 2;
        cb5.content = "君不见黄河之水天上来，奔流到海不复回；君不见高堂明镜悲白发，朝如青丝暮成雪" + UUID.randomUUID().toString().substring(0, 4);

        dates.add(cb);
        dates.add(cb1);
        dates.add(cb2);
        dates.add(cb3);
        dates.add(cb4);
        dates.add(cb5);

        onSuccess(dates);
    }

    class Holder extends BaseHolder {

        @BindView(R.id.chat_item_message_top)
        TextView desc;

        @BindView(R.id.chat_item_image_other)
        RoundedImageView imageOther;
        @BindView(R.id.chat_mark_other)
        TextView mark;
        @BindView(R.id.chat_name_other)
        TextView name;
        @BindView(R.id.chat_content_other)
        TextView contentOther;

        @BindView(R.id.chat_item_image_me)
        RoundedImageView imageMyself;
        @BindView(R.id.chat_content_me)
        TextView contentMyself;
        @BindView(R.id.chat_item_me_send_tips)
        TextView tips;

        @BindView(R.id.chat_item_other_wrapper)
        LinearLayout otherWrapper;
        @BindView(R.id.chat_item_me_wrapper)
        LinearLayout myselfWrapper;

        public void desc() {
            otherWrapper.setVisibility(View.GONE);
            myselfWrapper.setVisibility(View.GONE);
            desc.setVisibility(View.VISIBLE);
        }

        public void other() {
            otherWrapper.setVisibility(View.VISIBLE);
            myselfWrapper.setVisibility(View.GONE);
            desc.setVisibility(View.GONE);
        }

        public void myself() {
            otherWrapper.setVisibility(View.GONE);
            myselfWrapper.setVisibility(View.VISIBLE);
            desc.setVisibility(View.GONE);
        }

        public void hide() {

        }

        public Holder(View view) {
            super(view);
        }
    }
}
