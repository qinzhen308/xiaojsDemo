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
 * Date:2017/1/3
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;

public class AnimationView2 extends android.support.v7.widget.AppCompatImageView {
    public AnimationView2(Context context) {
        super(context);
    }

    public AnimationView2(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AnimationView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void start() {
        AnimationDrawable drawable = (AnimationDrawable) getDrawable();
        if (drawable != null) {
            drawable.setOneShot(false);
            drawable.start();
        }
    }

    public void stop() {
        AnimationDrawable drawable = (AnimationDrawable) getDrawable();
        if (drawable != null) {
            drawable.stop();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stop();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }
}
