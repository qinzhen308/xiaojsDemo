package cn.xiaojs.xma.ui.lesson;
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
 * Date:2017/1/15
 * Desc:我授的课
 *
 * ======================================================================================== */

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.base.BaseActivity;

public class TeachLessonActivity extends BaseActivity {

    @BindView(R.id.teach_lesson_list)
    PullToRefreshSwipeListView mList;

    private TeachLessonAdapter mAdapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_teach_lesson);
        setMiddleTitle(R.string.course_of_teach);
        mAdapter = new TeachLessonAdapter(this, mList);
        mList.setAdapter(mAdapter);
    }
}
