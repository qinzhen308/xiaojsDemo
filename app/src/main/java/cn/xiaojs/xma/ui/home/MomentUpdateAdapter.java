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
 * Date:2016/12/21
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.social.DynUpdate;
import cn.xiaojs.xma.ui.view.MomentUpdateTargetView;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.util.TimeUtil;

public class MomentUpdateAdapter extends AbsSwipeAdapter<DynUpdate, MomentUpdateAdapter.Holder> {

    private MomentUpdateActivity mActivity;
    private CircleTransform circleTransform;

    public MomentUpdateAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
        circleTransform = new CircleTransform(context);
    }

    public MomentUpdateAdapter(Context context, PullToRefreshSwipeListView listView, boolean autoLoad) {
        super(context, listView, autoLoad);
        circleTransform = new CircleTransform(context);
    }

    @Override
    protected void setViewContent(Holder holder, DynUpdate bean, int position) {

        Glide.with(mContext)
                .load(Account.getAvatar(bean.behavedBy.id,holder.portrait.getMeasuredWidth()))
                .transform(circleTransform)
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(holder.portrait);

        holder.name.setText(bean.behavedBy.name);
        holder.behavior.setText(bean.tips);
        holder.time.setText(TimeUtil.getTimeFromNow(bean.createdOn));
        if (bean.body == null){
            holder.content.setVisibility(View.GONE);
        }else {
            holder.content.setVisibility(View.VISIBLE);
            holder.content.setText(bean.body.summary);
        }
        holder.target.show(bean);
    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_moment_update_item, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void onDataItemClick(int position, DynUpdate bean) {
        if (bean == null || bean.source == null)
            return;

        Intent intent = new Intent(mContext,MomentDetailActivity.class);
        intent.putExtra(HomeConstant.KEY_MOMENT_ID,bean.source.id);
        mContext.startActivity(intent);
    }

    @Override
    protected void doRequest() {
        SocialManager.getUpdates(mContext, mPagination, new APIServiceCallback<CollectionPage<DynUpdate>>() {
            @Override
            public void onSuccess(CollectionPage<DynUpdate> object) {
                notifySuccess(true);
                if (object != null){
                    MomentUpdateAdapter.this.onSuccess(object.objectsOfPage);
                }else {
                    MomentUpdateAdapter.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                MomentUpdateAdapter.this.onFailure(errorCode,errorMessage);
                notifySuccess(false);
            }
        });
    }

    private void notifySuccess(boolean success){
        if (mActivity != null){
            mActivity.notifySuccess(success);
        }
    }

    public void setActivity(MomentUpdateActivity activity){
        mActivity = activity;
    }

    class Holder extends BaseHolder {

        @BindView(R.id.moment_update_item_target)
        MomentUpdateTargetView target;
        @BindView(R.id.moment_update_item_portrait)
        ImageView portrait;
        @BindView(R.id.moment_update_item_name)
        TextView name;
        @BindView(R.id.moment_update_item_behavior)
        TextView behavior;
        @BindView(R.id.moment_update_item_time)
        TextView time;
        @BindView(R.id.moment_update_item_content)
        TextView content;

        public Holder(View view) {
            super(view);
        }
    }
}
