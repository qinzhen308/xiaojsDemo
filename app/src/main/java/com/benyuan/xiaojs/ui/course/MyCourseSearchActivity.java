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
 * Date:2016/11/3
 * Desc:
 *
 * ======================================================================================== */

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import butterknife.BindView;
import butterknife.OnClick;

public class MyCourseSearchActivity extends BaseActivity {
    @BindView(R.id.my_course_search)
    EditText mInput;
    @BindView(R.id.my_course_search_ok)
    TextView mOk;
    @BindView(R.id.my_course_search_list)
    AutoPullToRefreshListView mList;


    @Override
    protected void addViewContent() {
        addView(R.layout.activity_my_course_search);
        needHeader(false);
        MyCourseAdapter adapter = new MyCourseAdapter(this,mList,false);
        mList.setAdapter(adapter);
    }

    @OnClick({R.id.back,R.id.my_course_search_ok})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.my_course_search_ok://确定搜索
                break;
            default:
                break;
        }
    }
}
