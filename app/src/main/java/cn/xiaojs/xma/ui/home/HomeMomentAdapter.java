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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.DynamicStatus;
import cn.xiaojs.xma.model.social.Dynamic;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.live.LiveScrollView;
import cn.xiaojs.xma.ui.personal.PersonHomeActivity;
import cn.xiaojs.xma.ui.personal.PersonalBusiness;
import cn.xiaojs.xma.ui.view.MomentContent;
import cn.xiaojs.xma.ui.view.MomentHeader;
import cn.xiaojs.xma.ui.view.MomentUGC;
import cn.xiaojs.xma.ui.widget.ListBottomDialog;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.ShareUtil;
import cn.xiaojs.xma.util.ToastUtil;
import cn.xiaojs.xma.util.VerifyUtils;

public class HomeMomentAdapter extends AbsSwipeAdapter<Dynamic, HomeMomentAdapter.Holder> {

    private HomeFragment mFragment;

    public HomeMomentAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    @Override
    protected void setViewContent(Holder holder, final Dynamic bean, final int position) {

        holder.showMoment();
        holder.content.show(bean);
        holder.showOrHiddenMore(bean);
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

                //ShareUtil.show();
            }

            @Override
            public void onMore() {
                more(bean, position);
            }
        });
        holder.header.setData(bean);
        DeviceUtil.expandViewTouch(holder.ugc.getMore(), 150);
        holder.header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personalHome(bean);
            }
        });
    }

    private void personalHome(Dynamic bean) {
        Intent intent = new Intent(mContext, PersonHomeActivity.class);
        intent.putExtra(PersonalBusiness.KEY_PERSONAL_ACCOUNT, bean.owner.account);
        mContext.startActivity(intent);
    }

    private void more(final Dynamic bean, final int itemPosition) {
        ListBottomDialog dialog = new ListBottomDialog(mContext);
        if (!VerifyUtils.isMyself(bean.owner.account)) {
            String[] items = mContext.getResources().getStringArray(R.array.ugc_more);
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    switch (position) {
//                        case 0://忽略此条动态
//                            break;
//                        case 1://忽略他的动态
//                            break;
                        case 0://取消关注
                            cancelFollow(bean);
                            break;
                        case 1://举报
                            break;
                    }
                }
            });
        } else {
            String[] items = new String[]{mContext.getString(R.string.delete)};
            dialog.setItems(items);
            dialog.setOnItemClick(new ListBottomDialog.OnItemClick() {
                @Override
                public void onItemClick(int position) {
                    delete(bean, itemPosition);
                }
            });
        }
        dialog.show();
    }

    private void delete(Dynamic bean, final int itemPosition) {

        showProgress(true);
        SocialManager.deleteActivity(mContext, bean.id, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                cancelProgress();
                getList().remove(itemPosition);
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                ToastUtil.showToast(mContext, errorMessage);
            }
        });
    }

    //取消关注
    private void cancelFollow(final Dynamic bean) {
        //未关注时不能取消关注
        if (!bean.owner.followed)
            return;

        if (AccountDataManager.unFollowable(mContext,bean.owner.account)) {
            Toast.makeText(mContext, R.string.unfollow_forbidden,Toast.LENGTH_SHORT).show();
            return;
        }

        SocialManager.unfollowContact(mContext, bean.owner.account, new APIServiceCallback() {
            @Override
            public void onSuccess(Object object) {
                ToastUtil.showToast(mContext, R.string.cancel_followed);
                deleteByOwner(bean.owner);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(mContext, errorMessage);
            }
        });
    }

    private void deleteByOwner(Dynamic.DynOwner owner) {
        List<Dynamic> removes = new ArrayList<>();
        for (Dynamic dynamic : getList()) {
            if (dynamic.owner.account != null
                    && owner.account!=null
                    && dynamic.owner.account.equalsIgnoreCase(owner.account)) {
                removes.add(dynamic);
            }
        }
        if (removes != null && removes.size() > 0) {
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
        Criteria criteria = new Criteria();
        SocialManager.getActivities(mContext, criteria, mPagination, new APIServiceCallback<CollectionPage<Dynamic>>() {
            @Override
            public void onSuccess(CollectionPage<Dynamic> dynamic) {

                if (mPagination.getPage() == 1) {
                    if (dynamic == null || dynamic.objectsOfPage == null || dynamic.objectsOfPage.size() == 0) {
                        if (mFragment != null) {
                            mFragment.showOrHiddenEmptyViiew(false, mContext.getResources().getString(R.string.empty_activity_tip));
                        }
                    } else {
                        if (mFragment != null) {
                            mFragment.showOrHiddenEmptyViiew(true, "");
                        }
                    }
                }

                if (dynamic != null) {
                    notifyUpdates(dynamic);
                    HomeMomentAdapter.this.onSuccess(dynamic.objectsOfPage);
                } else {
                    HomeMomentAdapter.this.onSuccess(null);
                    notifyUpdates(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

                if (getCount() <= 0 && mPagination.getPage() == 1 && mFragment != null) {
                    mFragment.showOrHiddenEmptyViiew(false, mContext.getResources().getString(R.string.load_failed_tip));
                }

                HomeMomentAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    @Override
    protected void onDataItemClick(int position, Dynamic bean) {
        Intent intent = new Intent(mContext, MomentDetailActivity.class);
        intent.putExtra(HomeConstant.KEY_MOMENT_ID, bean.id);
        intent.putExtra(HomeConstant.KEY_ITEM_POSITION, position);
        ((BaseActivity) mContext).startActivityForResult(intent, HomeConstant.REQUEST_CODE_MOMENT_DETAIL);
    }

    private void notifyUpdates(CollectionPage<Dynamic> dynamic) {
        if (mFragment != null) {
            mFragment.notifyUpdates(dynamic);
        }
    }

    public void setFragment(HomeFragment fragment) {
        mFragment = fragment;
    }

    public void update(DynamicStatus status) {
        if (status != null) {
            for (Dynamic dynamic : getList()) {
                if (dynamic.id.equalsIgnoreCase(status.id)) {
                    dynamic.liked = status.liked;
                    dynamic.stats = status.status;
                    notifyDataSetChanged();
                    break;
                }
            }
        }
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

        public void showOrHiddenMore(Dynamic bean) {
            if (AccountDataManager.unFollowable(mContext,bean.owner.account)) {
                ugc.setMoreVisibility(View.GONE);
            }else {
                ugc.setMoreVisibility(View.VISIBLE);
            }
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
