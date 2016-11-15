package com.benyuan.xiaojs.ui.course;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.model.TeachLesson;
import com.benyuan.xiaojs.ui.base.BaseTopTabActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

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

public class LessonCreationActivity extends BaseTopTabActivity {

    @Override
    protected void initView() {
        setMiddleTitle(R.string.lesson_creation);

        List<Fragment> fragments = new ArrayList<>();
        Fragment liveLesson = new LiveLessonCreationFragment();
        Fragment privateLesson = new PrivateLessonCreationFragment();
        fragments.add(liveLesson);
        fragments.add(privateLesson);

        Bundle data = new Bundle();
        data.putString(CourseConstant.KEY_LESSON_ID, getLessonId());
        liveLesson.setArguments(data);

        addViews(new int[] {R.string.live_lesson, R.string.private_lesson}, fragments);
    }

    @OnClick({R.id.left_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            default:
                break;
        }
    }

    private String getLessonId () {
        Object obj = getIntent().getSerializableExtra(CourseConstant.KEY_LESSON_BEAN);
        if (obj instanceof TeachLesson) {
            return ((TeachLesson)obj).getId();
        }

        return null;
    }
}
