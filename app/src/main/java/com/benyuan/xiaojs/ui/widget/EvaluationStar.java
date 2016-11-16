package com.benyuan.xiaojs.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.benyuan.xiaojs.R;

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
 * Date:2016/11/14
 * Desc:
 *
 * ======================================================================================== */

public class EvaluationStar extends LinearLayout {
    private final static int NORMAL = 0;
    private final static int LARGE = 1;

    private final static int STAR_NONE = 0;
    private final static int STAR_HALF = 1;
    private final static int STAR_FULL = 2;

    public enum Grading {
        NONE,
        HALF,
        ONE,
        ONE_HALF,
        TWO,
        TWO_HALF,
        THREE,
        THREE_HALF,
        FOUR,
        FOUR_HALF,
        FULL
    }

    public EvaluationStar(Context context) {
        super(context);
        init();

    }

    public EvaluationStar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EvaluationStar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public EvaluationStar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        int marginRight = getResources().getDimensionPixelOffset(R.dimen.px3);
        for (int i = 0; i < 5; i++) {
            ImageView iv = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.rightMargin = marginRight;
            iv.setLayoutParams(params);
            iv.setImageResource(R.drawable.ic_eval_star_none);
            addView(iv);
        }
    }

    public void setGap(int gap) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
           LayoutParams params = (LinearLayout.LayoutParams)getChildAt(i).getLayoutParams();
            params.rightMargin = gap;
        }
    }

    /**
     * set normal style
     * @param grading
     */
    public void setGrading(Grading grading) {
        setGrading(grading, NORMAL);
    }

    public void setGrading(Grading grading, int type) {
        reset();
        switch (type) {
            case NORMAL:
                default:
                switch (grading) {
                    case HALF:
                        setStarStyle(0, STAR_HALF);
                        break;
                    case ONE:
                        setStarStyle(0, STAR_FULL);
                        break;
                    case ONE_HALF:
                        setStarStyle(0, STAR_FULL);
                        setStarStyle(1, STAR_HALF);
                        break;
                    case TWO:
                        setStarStyle(0, STAR_FULL);
                        setStarStyle(1, STAR_FULL);
                        break;
                    case TWO_HALF:
                        setStarStyle(0, STAR_FULL);
                        setStarStyle(1, STAR_FULL);
                        setStarStyle(2, STAR_HALF);
                        break;
                    case THREE:
                        setStarStyle(0, STAR_FULL);
                        setStarStyle(1, STAR_FULL);
                        setStarStyle(2, STAR_FULL);
                        break;
                    case THREE_HALF:
                        setStarStyle(0, STAR_FULL);
                        setStarStyle(1, STAR_FULL);
                        setStarStyle(2, STAR_FULL);
                        setStarStyle(3, STAR_HALF);
                        break;
                    case FOUR:
                        setStarStyle(0, STAR_FULL);
                        setStarStyle(1, STAR_FULL);
                        setStarStyle(2, STAR_FULL);
                        setStarStyle(3, STAR_FULL);
                        break;
                    case FOUR_HALF:
                        setStarStyle(0, STAR_FULL);
                        setStarStyle(1, STAR_FULL);
                        setStarStyle(2, STAR_FULL);
                        setStarStyle(3, STAR_FULL);
                        setStarStyle(4, STAR_HALF);
                        break;
                    case FULL:
                        setStarStyle(0, STAR_FULL);
                        setStarStyle(1, STAR_FULL);
                        setStarStyle(2, STAR_FULL);
                        setStarStyle(3, STAR_FULL);
                        setStarStyle(4, STAR_FULL);
                        break;
                }
                break;
        }
    }

    private void setStarStyle(int index, int starType) {
        int count = getChildCount();
        if (index < 0 || index >= count) {
            return;
        }

        ImageView iv = (ImageView)getChildAt(index);
        switch (starType) {
            case STAR_NONE:
                iv.setImageResource(R.drawable.ic_eval_star_none);
                break;
            case STAR_HALF:
                iv.setImageResource(R.drawable.ic_eval_star_half);
                break;
            case STAR_FULL:
                iv.setImageResource(R.drawable.ic_eval_star_full);
                break;
            default:
                iv.setImageResource(R.drawable.ic_eval_star_none);
                break;
        }
    }

    private void reset() {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            ImageView iv = (ImageView)getChildAt(i);
            iv.setImageResource(R.drawable.ic_eval_star_none);
        }
    }
}
