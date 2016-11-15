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
 * Date:2016/11/14
 * Desc:
 *
 * ======================================================================================== */

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.model.TeachLesson;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.widget.LimitInputBox;

import butterknife.BindView;
import butterknife.OnClick;

public class CancelLessonActivity extends BaseActivity {

    @BindView(R.id.cancel_lesson_origin_name)
    TextView mName;
    @BindView(R.id.cancel_lesson_reason)
    LimitInputBox mInput;

    private TeachLesson lesson;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_cancel_lesson);
        Intent intent = getIntent();
        if (intent != null) {
            lesson = (TeachLesson) intent.getSerializableExtra(CourseConstant.KEY_LESSON_BEAN);
            if (lesson != null) {
                mName.setText(lesson.getTitle());
            }
        }
        setMiddleTitle(R.string.cancel_lesson);
    }

    @OnClick({R.id.left_image, R.id.cancel_lesson_ok})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.cancel_lesson_ok://确定取消
                cancel();
                break;
            default:
                break;
        }
    }

    private void cancel(){
        if (lesson != null){

        }
    }
}
