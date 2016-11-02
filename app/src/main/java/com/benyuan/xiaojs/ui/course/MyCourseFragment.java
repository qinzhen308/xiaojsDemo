package com.benyuan.xiaojs.ui.course;
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
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import butterknife.BindView;

public class MyCourseFragment extends BaseFragment {
    @BindView(R.id.listview)
    AutoPullToRefreshListView mListView;

    MyCourseAdapter adapter;

    @Override
    protected View getContentView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_my_course,null);
        return v;
    }

    @Override
    protected void init() {
        Bundle b = getArguments();
        boolean isTeacher = false;
        if (b != null){
            isTeacher = b.getBoolean("key");
        }
        adapter = new MyCourseAdapter(mContext,mListView,isTeacher);
        mListView.setAdapter(adapter);
        View header = LayoutInflater.from(mContext).inflate(R.layout.layout_my_course_search,null);
        mListView.getRefreshableView().addHeaderView(header);
//        mListView.setOnPullListener(new AutoPullToRefreshListView.OnPullListener() {
//            @Override
//            public void pullUp(int firstItemPosition) {
//                //Toast.makeText(mContext,"up",Toast.LENGTH_SHORT).show();
//                ((MyCourseActivity)mContext).hideTop();
//            }
//
//            @Override
//            public void pullDown(int firstItemPosition) {
//                //Toast.makeText(mContext,"down",Toast.LENGTH_SHORT).show();
//                if (firstItemPosition > 0){
//                    ((MyCourseActivity)mContext).showTop();
//                }else {
//                    ((MyCourseActivity)mContext).hideTop();
//                }
//            }
//        });
    }


}
