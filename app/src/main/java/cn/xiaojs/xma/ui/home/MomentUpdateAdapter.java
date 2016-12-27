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

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import butterknife.BindView;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.view.MomentUpdateTargetView;

public class MomentUpdateAdapter extends AbsSwipeAdapter<RecommendCourseBean, MomentUpdateAdapter.Holder> {


    public MomentUpdateAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
    }

    public MomentUpdateAdapter(Context context, PullToRefreshSwipeListView listView, boolean autoLoad) {
        super(context, listView, autoLoad);
    }

    @Override
    protected void setViewContent(Holder holder, RecommendCourseBean bean, int position) {
        if (position % 3 == 0){
            holder.target.show(1);
        }else if (position % 3 == 1){
            holder.target.show(2);
        }else {
            holder.target.show(3);
        }
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
        List<RecommendCourseBean> list = new ArrayList<>();
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());
        list.add(new RecommendCourseBean());

        onSuccess(list);
    }

    class Holder extends BaseHolder {

        @BindView(R.id.moment_update_item_target)
        MomentUpdateTargetView target;

        public Holder(View view) {
            super(view);
        }
    }
}
