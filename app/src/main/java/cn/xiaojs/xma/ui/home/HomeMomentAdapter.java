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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.ui.live.LiveScrollView;
import cn.xiaojs.xma.ui.view.MomentContent;
import cn.xiaojs.xma.ui.view.MomentHeader;
import cn.xiaojs.xma.ui.view.MomentUGC;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.ToastUtil;

public class HomeMomentAdapter extends AbsSwipeAdapter<Dynamic, HomeMomentAdapter.Holder> {

    private HomeFragment mFragment;

    public HomeMomentAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    @Override
    protected void setViewContent(Holder holder, final Dynamic bean, int position) {

        holder.showMoment();
        holder.content.show(bean.body, bean.typeName);
        holder.ugc.setStatus(bean);
        holder.ugc.setOnItemClickListener(new MomentUGC.OnItemClickListener() {
            @Override
            public void onPraise(boolean liked, boolean success) {

            }

            @Override
            public void onComment() {

            }

            @Override
            public void onShare() {

            }

            @Override
            public void onMore() {
                more(bean);
            }
        });
        holder.header.setData(bean);

    }

    private void more(final Dynamic bean) {
        ListBottomDialog dialog = new ListBottomDialog(mContext);
        String[] items = mContext.getResources().getStringArray(R.array.ugc_more);
        dialog.setItems(items);
        dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0://忽略此条动态
                        break;
                    case 1://忽略他的动态
                        break;
                    case 2://取消关注
                        cancelFollow(bean);
                        break;
                    case 3://举报
                        break;
                }
            }
        });

        dialog.show();
    }

    //取消关注
    private void cancelFollow(final Dynamic bean){
        //未关注时不能取消关注
        if (!bean.owner.followed)
            return;
        SocialManager.unfollowContact(mContext, bean.owner.account, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                ToastUtil.showToast(mContext,R.string.cancel_followed);
                deleteByOwner(bean.owner);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(mContext,errorMessage);
            }
        });
    }

    private void deleteByOwner(Dynamic.DynOwner owner){
        List<Dynamic> removes = new ArrayList<>();
        for (Dynamic dynamic : getList()){
            if (dynamic.owner.account.equalsIgnoreCase(owner.account)){
                removes.add(dynamic);
            }
        }
        if (removes != null && removes.size() > 0){
            getList().removeAll(removes);
            notifyDataSetChanged();
        }
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
    protected boolean showFailedView() {
        return false;
    }

    @Override
    protected void doRequest() {

        SocialManager.getActivities(mContext, null, mPagination, new APIServiceCallback<CollectionPage<Dynamic>>() {
            @Override
            public void onSuccess(CollectionPage<Dynamic> object) {
                if (object != null) {
                    notifyUpdates(object.totalUpdates);
                    HomeMomentAdapter.this.onSuccess(object.objectsOfPage);
                } else {
                    HomeMomentAdapter.this.onSuccess(null);
                    notifyUpdates(0);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                HomeMomentAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    @Override
    protected void onDataItemClick(int position, Dynamic bean) {
        Intent intent = new Intent(mContext, MomentDetailActivity.class);
        intent.putExtra(HomeConstant.KEY_MOMENT_ID, bean.id);
        mContext.startActivity(intent);
    }

    private void notifyUpdates(int updates){
        if (mFragment != null){
            mFragment.notifyUpdates(updates);
        }
    }

    public void setFragment(HomeFragment fragment){
        mFragment = fragment;
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
