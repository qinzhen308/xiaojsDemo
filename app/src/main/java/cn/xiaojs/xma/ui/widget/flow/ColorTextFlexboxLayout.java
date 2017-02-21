package cn.xiaojs.xma.ui.widget.flow;
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
 * Date:2017/2/20
 * Desc:
 *
 * ======================================================================================== */

import com.google.android.flexbox.FlexboxLayout;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TextView;

import cn.xiaojs.xma.R;

public class ColorTextFlexboxLayout extends FlexboxLayout {
    private final static int COLORS[] = {0XFF6BD8EF, 0XFFB8E986, 0XFFFF7E7E, 0XFF50E3C2, 0XFF9ABEF7,
            0XFFFFD24A, 0XFFF24AE0, 0XFF659FF9};
    private int mBorder = 1;
    private int mOldColor = 0;
    private int mMargin = 10;

    public ColorTextFlexboxLayout(Context context) {
        super(context, null);
        init();
    }

    public ColorTextFlexboxLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setFlexWrap(FLEX_WRAP_WRAP);
        mBorder = getResources().getDimensionPixelOffset(R.dimen.px2);
        mMargin = getResources().getDimensionPixelOffset(R.dimen.px10);
    }

    public void addText(int resId) {
        String txt = getContext().getString(resId);
        addText(txt);
    }

    public void addText(String txt) {
        if (!TextUtils.isEmpty(txt)) {

            int color = getRandomColor();
            GradientDrawable gd = new GradientDrawable();
            gd.setCornerRadius(1);
            gd.setStroke(mBorder, color);

            TextView tv = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.color_textview, null);
            tv.setText(txt);
            tv.setTextColor(color);
            tv.setBackgroundDrawable(gd);

            FlexboxLayout.LayoutParams layoutParamsItem = new FlexboxLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT);
            layoutParamsItem.setMargins(mMargin, mMargin, mMargin, mMargin);
            tv.setLayoutParams(layoutParamsItem);
            addView(tv);
        }
    }


    private int getRandomColor() {
        int length = COLORS.length;
        java.util.Random random = new java.util.Random();
        int index = random.nextInt(length);
        if (index >= length) {
            index = length - 1;
        }
        int color = COLORS[index];

        if (color == mOldColor) {
            return getRandomColor();
        }

        mOldColor = color;
        return color;
    }


}
