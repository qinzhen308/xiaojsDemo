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

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.common.pulltorefresh.core.PullToRefreshSwipeListView;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.ui.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class LessonSearchActivity extends BaseActivity {
    @BindView(R.id.my_course_search)
    EditText mInput;
    @BindView(R.id.my_course_search_ok)
    TextView mOk;
    @BindView(R.id.my_course_search_list)
    PullToRefreshSwipeListView mList;

    private AbsSwipeAdapter adapter;
    @Override
    protected void addViewContent() {
        addView(R.layout.activity_my_course_search);
        needHeader(false);
        Intent intent = getIntent();
        boolean isTeacher = false;
        if (intent != null){
            isTeacher = intent.getBooleanExtra(CourseConstant.KEY_IS_TEACHER,false);
        }
        if (isTeacher){
            adapter = new TeachLessonAdapter(this,mList,false);
        }else {
            adapter = new EnrollLessonAdapter(this,mList,false);
        }
        mList.setAdapter(adapter);
    }

    @OnClick({R.id.back,R.id.my_course_search_ok})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.back:
                finish();
                break;
            case R.id.my_course_search_ok://确定搜索
                search();
                break;
            default:
                break;
        }
    }

    private void search(){
        String key = mInput.getText().toString();
        if (!TextUtils.isEmpty(key) && adapter != null){
            Criteria criteria = LessonBusiness.getSearch(key);
            if (adapter instanceof TeachLessonAdapter){
                ((TeachLessonAdapter)adapter).request(criteria);
            }else if (adapter instanceof EnrollLessonAdapter){
                ((EnrollLessonAdapter)adapter).request(criteria);
            }
        }
    }
}
