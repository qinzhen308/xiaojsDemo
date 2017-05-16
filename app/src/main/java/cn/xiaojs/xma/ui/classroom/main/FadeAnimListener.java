package cn.xiaojs.xma.ui.classroom.main;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/5/11
 * Desc: fade in / fade out animator listener
 *
 * ======================================================================================== */

import android.animation.Animator;
import android.view.View;

public class FadeAnimListener implements Animator.AnimatorListener {
    public final static int MODE_ANIM_SHOW = 1 << 1;
    public final static int MODE_ANIM_HIDE = 1 << 2;

    public final static int ANIM_ALPHA = 1 << 1;
    public final static int ANIM_TRANSLATE = 1 << 2;
    public final static int ANIM_SCALE = 1 << 3;

    private View mV;
    private int mAnimMode;
    private boolean mAnimating = false;

    public FadeAnimListener with(View v) {
        mV = v;
        return this;
    }

    public FadeAnimListener play(int mode) {
        mAnimMode = mode;
        return this;
    }

    @Override
    public void onAnimationStart(Animator animation) {
        mAnimating = true;
        if (mV != null) {
            if (mAnimMode == MODE_ANIM_SHOW) {
                mV.setVisibility(View.VISIBLE);
            } else if (mAnimMode == MODE_ANIM_HIDE) {
                //do nothing
            }
        }
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        mAnimating = false;
        if (mV != null) {
            if (mAnimMode == MODE_ANIM_SHOW) {
                mV.setVisibility(View.VISIBLE);
            } else if (mAnimMode == MODE_ANIM_HIDE) {
                mV.setVisibility(View.INVISIBLE);
            }
        }
        mV = null;
    }

    @Override
    public void onAnimationCancel(Animator animation) {
        mAnimating = false;
        mV = null;
    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    public boolean isAnimating() {
        return mAnimating;
    }
}
