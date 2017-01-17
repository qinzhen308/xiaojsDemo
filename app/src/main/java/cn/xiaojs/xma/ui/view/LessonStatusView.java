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
 * Date:2017/1/17
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;

public class LessonStatusView extends RelativeLayout {

    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.time)
    TextView time;

    @BindView(R.id.status)
    TextView status;

    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.evaluate)
    TextView evaluate;
    @BindView(R.id.review)
    TextView review;

    @BindView(R.id.lesson_end)
    ImageView end;

    public LessonStatusView(Context context) {
        super(context);
        init();
    }

    public LessonStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LessonStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public LessonStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_lesson_status, this);
        ButterKnife.bind(this);
    }
}
