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
 * Date:2016/11/4
 * Desc:
 *
 * ======================================================================================== */

import android.view.LayoutInflater;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.HorizontalListView;

import butterknife.BindView;

public class HomeCourseFragment extends BlockFragment {

    @BindView(R.id.home_course_list)
    HorizontalListView mList;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_home_course,null);
    }

    @Override
    protected void init() {
        MyAdapter ma = new MyAdapter(mContext,mList);
        mList.setAdapter(ma);
    }

    @Override
    public int getFragmentHeight() {
        return 200;
    }
}
