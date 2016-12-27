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
 * Date:2016/12/21
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.xiaojs.xma.R;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MomentUpdateTargetView extends FrameLayout {

    @BindView(R.id.moment_update_target_word_wrapper)
    LinearLayout mWordWrapper;
    @BindView(R.id.moment_update_target_image)
    ImageView mImage;
    @BindView(R.id.moment_update_target_lesson_wrapper)
    LinearLayout mLessonWrapper;

    public static final int TYPE_WORD = 1;
    public static final int TYPE_IMAGE = 2;
    public static final int TYPE_LESSON = 3;

    public MomentUpdateTargetView(Context context) {
        super(context);
        init();
    }

    public MomentUpdateTargetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MomentUpdateTargetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public MomentUpdateTargetView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), cn.xiaojs.xma.R.layout.layout_moment_update_target, this);
        ButterKnife.bind(this);
    }

    public void show(int type) {
        if (type == TYPE_WORD) {
            mWordWrapper.setVisibility(VISIBLE);
            mImage.setVisibility(GONE);
            mLessonWrapper.setVisibility(GONE);
        } else if (type == TYPE_IMAGE) {
            mWordWrapper.setVisibility(GONE);
            mImage.setVisibility(VISIBLE);
            mLessonWrapper.setVisibility(GONE);
        } else {
            mWordWrapper.setVisibility(GONE);
            mImage.setVisibility(GONE);
            mLessonWrapper.setVisibility(VISIBLE);
        }
    }
}
