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
import com.benyuan.xiaojs.model.Notification;
import com.benyuan.xiaojs.util.TimeUtil;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import butterknife.BindView;

public class MessageCategoryAdapter extends AbsSwipeAdapter<Notification,MessageCategoryAdapter.Holder>{

    public MessageCategoryAdapter(Context context, AutoPullToRefreshListView listView) {
        super(context, listView);
    }

    @Override
    protected void setViewContent(Holder holder, Notification bean, int position) {
        holder.reset();
        holder.time.setText(TimeUtil.getTimeByNow(bean.createdOn));
        holder.content.setText(bean.body);

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

        public void reset(){
            operaWrapper.setVisibility(View.VISIBLE);
            bottomDivider.setVisibility(View.VISIBLE);
            opera1.setTextColor(mContext.getResources().getColor(R.color.common_text));
            opera2.setTextColor(mContext.getResources().getColor(R.color.font_blue));
            opera2.setVisibility(View.VISIBLE);
        }
        public Holder(View view) {
            super(view);
        }
    }
}
