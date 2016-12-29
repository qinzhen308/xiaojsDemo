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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.social.DynUpdate;
import cn.xiaojs.xma.ui.view.MomentUpdateTargetView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.TimeUtil;

public class MomentUpdateAdapter extends AbsSwipeAdapter<DynUpdate, MomentUpdateAdapter.Holder> {


    public MomentUpdateAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    public MomentUpdateAdapter(Context context, PullToRefreshSwipeListView listView, boolean autoLoad) {
        super(context, listView, autoLoad);
    }

    @Override
    protected void setViewContent(Holder holder, DynUpdate bean, int position) {
        holder.name.setText(bean.behavedBy.name);
        holder.behavior.setText(bean.tips);
        holder.time.setText(TimeUtil.getTimeByNow(bean.createdOn));
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
    protected void doRequest() {
        SocialManager.getUpdates(mContext, mPagination, new APIServiceCallback<CollectionPage<DynUpdate>>() {
            @Override
            public void onSuccess(CollectionPage<DynUpdate> object) {
                if (object != null){
                    MomentUpdateAdapter.this.onSuccess(object.objectsOfPage);
                }else {
                    MomentUpdateAdapter.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                MomentUpdateAdapter.this.onFailure(errorCode,errorMessage);
            }
        });
    }

    class Holder extends BaseHolder {

        @BindView(R.id.moment_update_item_target)
        MomentUpdateTargetView target;
        @BindView(R.id.moment_update_item_portrait)
        RoundedImageView portrait;
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
