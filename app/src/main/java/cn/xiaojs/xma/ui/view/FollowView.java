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
 * Date:2016/12/18
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import cn.xiaojs.xma.R;

public class FollowView extends TextView {

    private int padding;


    public FollowView(Context context) {
        super(context);
        init();
    }

    public FollowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FollowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public FollowView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        padding = getResources().getDimensionPixelSize(R.dimen.px5);
        setPadding(2 * padding,padding,2 * padding,padding);
        setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_plus,0,0,0);
        setCompoundDrawablePadding(2 * padding);
        setTextColor(getResources().getColor(R.color.font_orange));
        setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_28px));
        setGravity(Gravity.CENTER);
        setBackgroundResource(R.drawable.orange_stoke_bg);
    }
}
