package com.benyuan.xiaojs.ui.home;
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
 * Date:2016/11/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.benyuan.xiaojs.common.pulltorefresh.core.PullToRefreshSwipeListView;
import com.benyuan.xiaojs.ui.live.LiveScrollView;
import com.benyuan.xiaojs.ui.view.MomentContent;
import com.benyuan.xiaojs.ui.view.MomentHeader;
import com.benyuan.xiaojs.ui.view.MomentUGC;
import com.benyuan.xiaojs.ui.widget.HorizontalAdaptScrollerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HomeMomentAdapter extends AbsSwipeAdapter<RecommendCourseBean, HomeMomentAdapter.Holder> {

    public HomeMomentAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    @Override
    protected void setViewContent(Holder holder, RecommendCourseBean bean, int position) {

        if (position % 5 == 0) {
            holder.showRecommend();
            RecyclerView.Adapter adapter = new PersonAdapter(mContext);
            holder.mList.setItemVisibleCountType(HorizontalAdaptScrollerView.ItemVisibleTypeCount.TYPE_FREE);
            holder.mList.setItemVisibleCount(1.3f);
            holder.mList.setAdapter(adapter);
            ViewGroup.LayoutParams lp = holder.mList.getLayoutParams();
            lp.height = mContext.getResources().getDimensionPixelSize(R.dimen.px370);
            holder.mList.setLayoutParams(lp);
        } else {
            holder.showMoment();
            holder.content.show();
        }

        holder.header.setData();

    }

    @Override
    protected View createContentView(int position) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_home_item, null);
        return v;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        RecommendCourseBean b = new RecommendCourseBean();
        List<RecommendCourseBean> beans = new ArrayList<>();
        beans.add(b);
        beans.add(b);
        beans.add(b);
        beans.add(b);
        beans.add(b);
        onSuccess(beans);
    }

    @Override
    protected void onDataItemClick(int position, RecommendCourseBean bean) {
        mContext.startActivity(new Intent(mContext,MomentDetailActivity.class));
    }

    class Holder extends BaseHolder {

        @BindView(R.id.home_moment_wrapper)
        LinearLayout mMomentWrapper;
        @BindView(R.id.home_recommend_wrapper)
        LinearLayout mRecommendWrapper;

        @BindView(R.id.home_moment_header)
        MomentHeader header;
        @BindView(R.id.home_moment_content)
        MomentContent content;
        @BindView(R.id.home_moment_ugc)
        MomentUGC ugc;

        @BindView(R.id.home_recommend_list)
        LiveScrollView mList;

        public void showMoment() {
            mMomentWrapper.setVisibility(View.VISIBLE);
            mRecommendWrapper.setVisibility(View.GONE);
        }

        public void showRecommend() {
            mMomentWrapper.setVisibility(View.GONE);
            mRecommendWrapper.setVisibility(View.VISIBLE);
        }

        public Holder(View view) {
            super(view);
        }
    }

    @Override
    protected boolean showEmptyView() {
        return false;
    }
}
