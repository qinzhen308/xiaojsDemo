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
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;

public class MomentDetailAdapter extends AbsSwipeAdapter<RecommendCourseBean,MomentDetailAdapter.Holder> {

    public MomentDetailAdapter(Context context, PullToRefreshSwipeListView list, boolean isNeedPreLoading) {
        super(context,list,isNeedPreLoading);
    }

    public MomentDetailAdapter(Context context, PullToRefreshSwipeListView list) {
        super(context, list);
    }

    @Override
    protected void setViewContent(Holder holder, RecommendCourseBean bean, int position) {

    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_moment_comment_item,null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        List<RecommendCourseBean> list = new ArrayList<>();
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());

//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        onSuccess(list);
    }

    @Override
    protected void onDataItemClick(int position, RecommendCourseBean bean) {
        Intent intent = new Intent(mContext,MomentCommentActivity.class);
        intent.putExtra(HomeConstant.KEY_COMMENT_TYPE,HomeConstant.COMMENT_TYPE_REPLY);
        intent.putExtra(HomeConstant.KEY_COMMENT_REPLY_NAME,"菜菜菜");
        mContext.startActivity(intent);
    }

    class Holder extends BaseHolder {
        public Holder(View view) {
            super(view);
        }
    }

}
