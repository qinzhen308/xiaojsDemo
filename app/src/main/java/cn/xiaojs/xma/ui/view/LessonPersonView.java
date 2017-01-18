package cn.xiaojs.xma.ui.view;
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
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.EnrolledLesson;
import cn.xiaojs.xma.ui.widget.RoundedImageView;

public class LessonPersonView extends LinearLayout {

    @BindView(R.id.image1)
    RoundedImageView image1;
    @BindView(R.id.image2)
    RoundedImageView image2;
    @BindView(R.id.image3)
    RoundedImageView image3;

    @BindView(R.id.name1)
    TextView name1;
    @BindView(R.id.name2)
    TextView name2;
    @BindView(R.id.name3)
    TextView name3;

    @BindView(R.id.desc1)
    TextView desc1;
    @BindView(R.id.desc2)
    TextView desc2;
    @BindView(R.id.desc3)
    TextView desc3;

    public LessonPersonView(Context context) {
        super(context);
        init();
    }

    public LessonPersonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LessonPersonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public LessonPersonView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        inflate(getContext(), R.layout.layout_lesson_person_view, this);
        ButterKnife.bind(this);
    }

    public void show(EnrolledLesson bean){
        if (bean == null)
            return;
        name1.setText(bean.getTeacher().getBasic().getName());
        desc1.setText("主讲");

        image2.setVisibility(GONE);
        name2.setVisibility(GONE);
        desc2.setVisibility(GONE);
        image3.setVisibility(GONE);
        name3.setVisibility(GONE);
        desc3.setVisibility(GONE);
    }

}
