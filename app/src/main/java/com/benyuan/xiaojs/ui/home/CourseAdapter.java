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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CourseAdapter extends AbsSwipeAdapter<RecommendCourseBean,CourseAdapter.Holder> {

    public CourseAdapter(Context context, AutoPullToRefreshListView listView) {
        super(context, listView);
    }

    @Override
    protected void setViewContent(Holder holder, RecommendCourseBean bean, int position) {
        holder.image.setImageResource(R.mipmap.t_three);
        holder.name.setText("Android高级开发 自定义控件/NDK/架构设计/性能优化");
        holder.desc.setText("免费");
        holder.stuNum.setText("99999人已学");
    }

    @Override
    protected View createContentView(int position) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_home_recommend_course_item,null);
        return v;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest(int pageNo) {
        RecommendCourseBean b = new RecommendCourseBean();
        List<RecommendCourseBean> beans = new ArrayList<>();
        beans.add(b);
        beans.add(b);
        beans.add(b);
        beans.add(b);
        beans.add(b);
        onSuccess(beans);
    }

    class Holder extends BaseHolder {
        @BindView(R.id.recommend_course_image)
        ImageView image;
        @BindView(R.id.recommend_course_name)
        TextView name;
        @BindView(R.id.recommend_course_desc)
        TextView desc;
        @BindView(R.id.recommend_course_stu_num)
        TextView stuNum;

        public Holder(View view) {
            super(view);
        }
    }
}
