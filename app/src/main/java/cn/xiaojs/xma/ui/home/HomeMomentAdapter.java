package cn.xiaojs.xma.ui.home;
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

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.live.LiveScrollView;
import cn.xiaojs.xma.ui.view.MomentContent;
import cn.xiaojs.xma.ui.view.MomentHeader;
import cn.xiaojs.xma.ui.view.MomentUGC;
import cn.xiaojs.xma.ui.widget.HorizontalAdaptScrollerView;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;

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
            holder.ugc.setOnItemClickListener(new MomentUGC.OnItemClickListener() {
                @Override
                public void onPraise() {

                }

                @Override
                public void onComment() {

                }

                @Override
                public void onShare() {

                }

                @Override
                public void onMore() {
                    more();
                }
            });
        }

        holder.header.setData();

    }

    private void more(){
        ListBottomDialog dialog = new ListBottomDialog(mContext);
        String[] items = mContext.getResources().getStringArray(R.array.ugc_more);
        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position){
                    case 0://忽略此条动态
                        break;
                    case 1://忽略他的动态
                        break;
                    case 2://取消关注
                        break;
                    case 3://举报
                        break;
                }
            }
        });

        dialog.show();
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
