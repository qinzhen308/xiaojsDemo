package com.benyuan.xiaojs.ui.widget.flow;
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
 * Date:2016/11/13
 * Desc:单行image的流式布局，多余的数据自动截取，不会出现显示部分的情况
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;

import java.util.List;

public class ImageFlowLayout extends FlowBaseLayout{

    private OnItemClickListener l;
    private int defaultMargin = getResources().getDimensionPixelSize(R.dimen.px10);
    private int mImageCount;

    private int mMargin;
    private int mRadius;
    public ImageFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void show(List<Bitmap> bitmaps){
        show(bitmaps,getResources().getDimensionPixelSize(R.dimen.px30),defaultMargin);
    }

    public void show(List<Bitmap> bitmaps,int margin){
        show(bitmaps,getResources().getDimensionPixelSize(R.dimen.px30),margin);
    }

    public void setOnItemClickListener(OnItemClickListener l){
        this.l = l;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    private ImageView getItem(List<Bitmap> bitmaps,int radius,int margin){
        RoundedImageView image = new RoundedImageView(getContext());
        MarginLayoutParams mlp = new MarginLayoutParams(radius * 2,radius * 2);
        mlp.leftMargin = margin;
        mlp.rightMargin = margin;
        image.setLayoutParams(mlp);
        image.setImageResource(R.drawable.default_portrait);
        image.setOval(true);
        image.setCornerRadius((float) radius);
        return image;
    }

    public void show(List<Bitmap> bitmaps,int radius,int margin){
        if (bitmaps == null || bitmaps.size() == 0)
            return;
        mImageCount = bitmaps.size();
        mMargin = margin;
        mRadius = radius;
        removeAllViews();
        setMaxLines(1);
        for (int i = 0;i<bitmaps.size();i++){
            ImageView image = getItem(bitmaps,radius,margin);
            addView(image);
            final int index = i;
            image.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (l != null){
                        l.onItemClick(index);
                    }
                }
            });
        }
    }

    public void showWithNum(List<Bitmap> bitmaps,int radius,int margin){
        show(bitmaps,radius,margin);
        showLast = true;
    }

    public void showWithNum(List<Bitmap> bitmaps,int margin){
        showWithNum(bitmaps,getResources().getDimensionPixelSize(R.dimen.px30),margin);
    }

    private boolean showLast;

    @Override
    protected boolean showLast() {
        return showLast;
    }

    @Override
    protected View lastView(int num) {
        if (num > 0){
            TextView textView = new TextView(getContext());
            ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(mRadius * 2,mRadius * 2);
            lp.leftMargin = mMargin;
            textView.setLayoutParams(lp);
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(R.color.common_text));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.font_22px));
            textView.setBackgroundResource(R.drawable.grey_circle);
            if (num <= 0){
                textView.setText(String.valueOf(0));
            }else if (num > 0 && num < 99){
                textView.setText(String.valueOf(num));
            }else {
                textView.setText("99+");
            }
            addView(textView);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (l != null){
                        l.onItemClick(getChildCount() - 1);
                    }
                }
            });
            return textView;
        }
        return null;
    }

    @Override
    protected int getTotal() {
        return mImageCount;
    }
}
