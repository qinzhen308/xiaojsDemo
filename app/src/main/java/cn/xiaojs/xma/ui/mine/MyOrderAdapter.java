package cn.xiaojs.xma.ui.mine;
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
 * Date:2017/2/20
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.OrderManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.order.EnrollOrder;
import cn.xiaojs.xma.ui.widget.RoundedImageView;

public class MyOrderAdapter extends AbsSwipeAdapter<EnrollOrder, MyOrderAdapter.Holder> {

    public MyOrderAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    @Override
    protected void setViewContent(Holder holder, EnrollOrder bean, int position) {

    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_my_order_item, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        OrderManager.getOrders(mContext, mPagination, new APIServiceCallback<List<EnrollOrder>>() {
            @Override
            public void onSuccess(List<EnrollOrder> object) {
                MyOrderAdapter.this.onSuccess(object);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                MyOrderAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    class Holder extends BaseHolder {

        @BindView(R.id.my_order_por)
        RoundedImageView head;
        @BindView(R.id.my_order_name)
        TextView name;
        @BindView(R.id.my_order_state)
        TextView state;
        @BindView(R.id.my_order_title)
        TextView title;
        @BindView(R.id.my_order_type)
        TextView type;
        @BindView(R.id.my_order_time)
        TextView time;
        @BindView(R.id.my_order_price)
        TextView price;
        @BindView(R.id.my_order_desc)
        TextView desc;
        @BindView(R.id.my_order_time_down)
        TextView timeDown;
        @BindView(R.id.my_order_pay)
        TextView pay;
        @BindView(R.id.my_order_cancel)
        TextView cancel;

        @BindView(R.id.my_order_opera_wrapper)
        View opera;

        public Holder(View view) {
            super(view);
        }
    }
}
