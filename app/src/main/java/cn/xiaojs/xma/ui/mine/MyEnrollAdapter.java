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
 * Date:2016/11/23
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.NumberUtil;
import cn.xiaojs.xma.util.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MyEnrollAdapter extends AbsSwipeAdapter<MyEnrollAdapter.MyEnrollBean, MyEnrollAdapter.Holder> {


    public MyEnrollAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    @Override
    protected void setViewContent(Holder holder, MyEnrollBean bean, int position) {
        holder.reset();
        holder.time.setText("创建时间：" + TimeUtil.format(TimeUtil.beforeDawn(),TimeUtil.TIME_YYYY_MM_DD_HH_MM));
        holder.state.setText("待支付");
        holder.category.setText("直播课");
        holder.title.setText("人力资源实战大咖系列课");
        holder.name.setText("有一个培训机构");
        holder.realPrice.setText(NumberUtil.getPrice(188));
        holder.originPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.originPrice.setText(NumberUtil.getPrice(888));
        holder.tip.setText("报名前5人享受8折优惠");
        holder.opera1.setText("取消");
        holder.opera2.setText("去付款");
    }

    @Override
    protected View createContentView(int position) {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_my_enroll_item, null);
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        List<MyEnrollBean> beans = new ArrayList<>();
        MyEnrollBean bean = new MyEnrollBean();
        beans.add(bean);
        beans.add(bean);
        beans.add(bean);
        beans.add(bean);
        beans.add(bean);
        beans.add(bean);

        onSuccess(beans);
    }

    class MyEnrollBean {

    }

    class Holder extends BaseHolder {

        @BindView(R.id.my_enroll_time)
        TextView time;
        @BindView(R.id.my_enroll_state)
        TextView state;
        @BindView(R.id.my_enroll_type)
        TextView category;
        @BindView(R.id.my_enroll_title)
        TextView title;
        @BindView(R.id.my_enroll_teacher_image)
        RoundedImageView image;
        @BindView(R.id.my_enroll_teacher_name)
        TextView name;
        @BindView(R.id.my_enroll_real_price)
        TextView realPrice;
        @BindView(R.id.my_enroll_origin_price)
        TextView originPrice;
        @BindView(R.id.my_enroll_tip)
        TextView tip;
        @BindView(R.id.my_enroll_common_opera)
        TextView opera1;
        @BindView(R.id.my_enroll_blue_opera)
        TextView opera2;

        @BindView(R.id.my_enroll_opera_wrapper)
        View operaArea;

        public void reset() {
            tip.setVisibility(View.VISIBLE);
            opera1.setVisibility(View.VISIBLE);
            opera2.setVisibility(View.VISIBLE);
            operaArea.setVisibility(View.VISIBLE);
            originPrice.setVisibility(View.VISIBLE);

        }

        public Holder(View view) {
            super(view);
        }
    }
}
