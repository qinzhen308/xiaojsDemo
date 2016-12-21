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
 * Date:2016/12/20
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import cn.xiaojs.xma.R;

import butterknife.ButterKnife;

public class RelationshipView extends FrameLayout {
    public RelationshipView(Context context) {
        super(context);
        init();
    }

    public RelationshipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RelationshipView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public RelationshipView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_relationship, this);
        ButterKnife.bind(this);
    }
}
