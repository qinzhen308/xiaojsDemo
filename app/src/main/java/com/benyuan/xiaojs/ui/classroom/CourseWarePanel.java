package com.benyuan.xiaojs.ui.classroom;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2016/11/30
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.drawer.DrawerLayout;

public class CourseWarePanel extends Panel {
    private ListView mListView;
    private CourseWareAdapter mAdapter;

    public CourseWarePanel(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        return  null;
    }

    @Override
    public void show(ViewGroup.LayoutParams params, DrawerLayout drawerLayout, ViewGroup container) {
        if (mContentView == null) {
            mContentView = container.findViewById(R.id.course_ware);
        }
        drawerLayout.openDrawer(container);

        initData();
    }

    @Override
    public void initData() {
        if (mListView == null || mAdapter == null) {
            mListView = (ListView)mContentView.findViewById(R.id.course_ware_list);
            mAdapter = new CourseWareAdapter(mContext);
            mListView.setAdapter(mAdapter);
        } else {
            //TODO set data
            mAdapter.notifyDataSetChanged();
        }
    }
}
