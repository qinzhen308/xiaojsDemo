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
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.util.DeviceUtil;

public class ImageLine extends LinearLayout {
    private int maxNum;
    private int radius;
    private int dis;
    private ImageMatrixExpandableLayout.OnImageMatrixItemListener listener;
    public ImageLine(Context context) {
        super(context);
        init();
    }

    public ImageLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ImageLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(21)
    public ImageLine(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setOrientation(HORIZONTAL);
    }

    public void setListener(ImageMatrixExpandableLayout.OnImageMatrixItemListener l){
        listener = l;
    }

    public void show(int maxNum,int radius,boolean expand){
        this.maxNum = maxNum;
        this.radius = radius;
        computeDis();
        for (int i = 0;i < maxNum;i++){
            if (expand && i == maxNum - 1){
                addView(getLastItem(false));
                continue;
            }
            ImageView image = getImageItem();
            addView(image);
        }
    }

    private RoundedImageView getImageItem() {
        RoundedImageView image = new RoundedImageView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(radius * 2, radius * 2);
        int child = getChildCount();
        if (child == 0){
            lp.leftMargin = 0;
            lp.rightMargin = dis / 2;
        }else if (child == maxNum - 1){
            lp.leftMargin = dis / 2;
            lp.rightMargin = 0;
        }else {
            lp.leftMargin = dis / 2;
            lp.rightMargin = dis / 2;
        }

        image.setLayoutParams(lp);
        image.setImageResource(R.drawable.default_portrait);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setOval(true);
        return image;
    }

    public void showLast(int maxNum,int num,int radius,boolean shrink){
        this.maxNum = maxNum;
        this.radius = radius;
        computeDis();
        for (int i = 0;i < num;i++){
            ImageView image = getImageItem();
            addView(image);
        }
        if (shrink){
            View view = getLastItem(true);
            addView(view);
        }
    }

    private void computeDis(){
        int screenWidth = DeviceUtil.getScreenWidth(getContext());
        dis = (screenWidth - getResources().getDimensionPixelSize(R.dimen.px60) - maxNum * 2 * radius) / maxNum;
    }

    private ImageView getLastItem(boolean shrink){
        RoundedImageView image = new RoundedImageView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(radius * 2, radius * 2);
        int child = getChildCount();
        int leftMargin = (maxNum - child - 1) * (2 * radius + dis) + dis / 2;
        lp.leftMargin = leftMargin;
        image.setLayoutParams(lp);
        if (shrink){
            image.setImageResource(R.drawable.ic_images_up);
            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onShrinkClick();
                    }
                }
            });
        }else {
            image.setImageResource(R.drawable.ic_images_down);
            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null){
                        listener.onExpandClick();
                    }
                }
            });
        }
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setOval(true);
        return image;
    }
}
