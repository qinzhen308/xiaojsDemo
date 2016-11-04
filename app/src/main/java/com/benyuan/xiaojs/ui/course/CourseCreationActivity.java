package com.benyuan.xiaojs.ui.course;

import android.support.v4.app.Fragment;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseTopTabActivity;

import java.util.ArrayList;
import java.util.List;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/2
 * Desc:
 *
 * ======================================================================================== */

public class CourseCreationActivity extends BaseTopTabActivity {
    @Override
    protected void initView() {
        setTitle(R.string.course_creation);

        List<Fragment> fragments = new ArrayList<>();
        List<String> mTabs = new ArrayList<String>();

        Fragment liveCourse = new LiveCourseCreationFragment();
        Fragment privateCourse = new PrivateCourseCreationFragment();
        fragments.add(liveCourse);
        fragments.add(privateCourse);

        addViews(new int[] {R.string.live_course, R.string.private_course}, fragments);
    }
}
