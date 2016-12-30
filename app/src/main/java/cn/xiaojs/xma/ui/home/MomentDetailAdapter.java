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
 * Date:2016/12/13
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.Intent;
import android.text.Spannable;
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
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.model.Doc;
import cn.xiaojs.xma.model.social.Comment;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.util.StringUtil;
import cn.xiaojs.xma.util.TimeUtil;

public class MomentDetailAdapter extends AbsSwipeAdapter<Comment, MomentDetailAdapter.Holder> {

    private String mMomentId;
    private String mSubType;

    public MomentDetailAdapter(Context context, PullToRefreshSwipeListView list, boolean isNeedPreLoading, String momentId, String sutType) {
        super(context, list, isNeedPreLoading);
        mMomentId = momentId;
        mSubType = sutType;
    }

    @Override
    protected void setViewContent(Holder holder, Comment bean, int position) {
        if (bean.target == null) {//普通评论
            holder.reply(false);
            holder.name.setText(bean.createdBy.getBasic().getName());
            holder.time.setText(TimeUtil.getTimeByNow(bean.createdOn));
            holder.content.setText(bean.body.text);
        } else {//评论的回复
            holder.reply(true);
            holder.name.setText(bean.createdBy.getBasic().getName());
            String replyTitle = mContext.getString(R.string.comment_reply_title,
                    bean.target.createdBy.getBasic().getName(),
                    bean.target.body.text);
            Spannable replySpan = StringUtil.getSpecialString(replyTitle,
                    bean.target.createdBy.getBasic().getName(),
                    mContext.getResources().getColor(R.color.font_black));
            holder.reply.setText(replySpan);
            holder.time.setText(TimeUtil.getTimeByNow(bean.createdOn));
            holder.content.setText(bean.body.text);
        }

    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_moment_comment_item, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        Criteria criteria = new Criteria();
        Doc doc = new Doc();
        doc.id = mMomentId;
        doc.subtype = mSubType;
        criteria.setDoc(doc);
        SocialManager.getComments(mContext, criteria, mPagination, new APIServiceCallback<CollectionPage<Comment>>() {
            @Override
            public void onSuccess(CollectionPage<Comment> object) {
                if (object != null) {
                    //MomentDetailAdapter.this.onSuccess(object.objectsOfPage);
                    MomentDetailAdapter.this.onSuccess(HomeBusiness.resolveComments(object.objectsOfPage));
                } else {
                    MomentDetailAdapter.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                MomentDetailAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    @Override
    protected void onDataItemClick(int position, Comment bean) {
        Intent intent = new Intent(mContext, MomentCommentActivity.class);
        if (bean.target == null){
            intent.putExtra(HomeConstant.KEY_COMMENT_TYPE, HomeConstant.COMMENT_TYPE_REPLY);
        }else {
            intent.putExtra(HomeConstant.KEY_COMMENT_TYPE, HomeConstant.COMMENT_TYPE_REPLY_REPLY);
        }
        intent.putExtra(HomeConstant.KEY_COMMENT_REPLY_NAME, bean.createdBy.getBasic().getName());
        intent.putExtra(HomeConstant.KEY_MOMENT_REPLY_ID, bean.id);
        ((BaseActivity)mContext).startActivityForResult(intent,HomeConstant.REQUEST_CODE_COMMENT_REPLY);
    }

    @Override
    protected boolean showEmptyView() {
        return false;
    }

    @Override
    protected boolean showFailedView() {
        return false;
    }

    class Holder extends BaseHolder {

        @BindView(R.id.moment_comment_name)
        TextView name;
        @BindView(R.id.moment_comment_time)
        TextView time;
        @BindView(R.id.moment_comment_content)
        TextView content;
        @BindView(R.id.moment_comment_reply)
        TextView reply;

        public void reply(boolean reply) {
            if (reply){
                this.reply.setVisibility(View.VISIBLE);
            }else {
                this.reply.setVisibility(View.GONE);
            }
        }

        public Holder(View view) {
            super(view);
        }
    }

}
