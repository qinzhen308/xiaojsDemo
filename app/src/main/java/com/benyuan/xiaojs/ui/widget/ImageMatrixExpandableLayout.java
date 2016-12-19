package com.benyuan.xiaojs.ui.widget;
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
 * Date:2016/12/19
 * Desc:可伸缩的图片矩阵列表
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.util.DeviceUtil;

public class ImageMatrixExpandableLayout extends LinearLayout{

    private int topMargin;
    private int maxNum;
    private int IMAGE_RADIUS;
    private int minDis;
    private int sum;
    private final int SHRINK_LINE = 2;

    public ImageMatrixExpandableLayout(Context context) {
        super(context);
        init();
    }

    public ImageMatrixExpandableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageMatrixExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public ImageMatrixExpandableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        topMargin = getResources().getDimensionPixelSize(R.dimen.px10);
        IMAGE_RADIUS = getResources().getDimensionPixelSize(R.dimen.px25);
        int screenWidth = DeviceUtil.getScreenWidth(getContext());
        minDis = getResources().getDimensionPixelSize(R.dimen.px8);
        maxNum = (screenWidth - getResources().getDimensionPixelSize(R.dimen.px60) + minDis) / (2 * IMAGE_RADIUS + minDis);
    }

    public void show(int sum) {
        this.sum = sum;
        shrink();
    }

    private void shrink() {
        removeAllViews();
        //最多只有两行数据就直接显示，不会伸缩
        if (sum <= maxNum * SHRINK_LINE){
            expand();
            return;
        }

        //大于两行则第二行最后显示伸缩按钮
        for (int i = 0;i < SHRINK_LINE; i++) {
            ImageLine l = new ImageLine(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i != 0) {
                lp.topMargin = topMargin;
            }
            l.setLayoutParams(lp);
            if (i == SHRINK_LINE - 1){
                l.show(maxNum, IMAGE_RADIUS,true);
                l.setListener(new OnImageMatrixItemListener() {
                    @Override
                    public void onShrinkClick() {

                    }

                    @Override
                    public void onExpandClick() {
                        expand();
                    }
                });
            }else {
                l.show(maxNum, IMAGE_RADIUS,false);
            }
            addView(l);
        }
    }

    private void expand() {
        removeAllViews();
        int i = 0;
        for (; i < sum / maxNum; i++) {
            ImageLine l = new ImageLine(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (i != 0) {
                lp.topMargin = topMargin;
            }
            l.setLayoutParams(lp);
            l.show(maxNum, IMAGE_RADIUS,false);
            addView(l);
        }

        if (sum % maxNum > 0) {
            ImageLine l = new ImageLine(getContext());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = topMargin;
            l.setLayoutParams(lp);
            l.showLast(maxNum, sum % maxNum, IMAGE_RADIUS,i > 1);
            l.setListener(new OnImageMatrixItemListener() {
                @Override
                public void onShrinkClick() {
                    shrink();
                }

                @Override
                public void onExpandClick() {

                }
            });
            addView(l);
        }
    }

    public interface OnImageMatrixItemListener{
        void onShrinkClick();
        void onExpandClick();
    }
}
