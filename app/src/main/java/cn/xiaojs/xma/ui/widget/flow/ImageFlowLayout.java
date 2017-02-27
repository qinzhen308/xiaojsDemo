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
 * Author:zhanghui
 * Date:2016/11/13
 * Desc:单行image的流式布局，多余的数据自动截取，不会出现显示部分的情况,如果数量超出1行会在最后显示剩余数量
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.BitmapUtils;

public class ImageFlowLayout extends ViewGroup {


    public ImageFlowLayout(Context context) {
        super(context);
    }

    public ImageFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImageFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public ImageFlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = getWidth();
        int lineWidth = 0;
        int num = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            if (lineWidth + childWidth + lp.leftMargin + lp.rightMargin < width) {
                num++;
                lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            } else {
                break;
            }
        }
        int extra = getChildCount() - num;
        int left = 0;
        int top = 0;
        for (int i = 0; i < num; i++) {
            View child = getChildAt(i);
            if (i == num - 1 && extra > 0) {
                //最后一个需要显示数字
                ((ImageView) child).setImageDrawable(getLastItem(extra + 1));
            }

            if (child == null || child.getVisibility() == View.GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

            // 计算childView的left,top,right,bottom
            int lc = 0;
            lc = left + lp.leftMargin;

            int tc = top + lp.topMargin;
            int rc = lc + child.getMeasuredWidth();
            int bc = tc + child.getMeasuredHeight();

            //Log.e(TAG, child + " , l = " + lc + " , t = " + t + " , r =" + rc + " , b = " + bc);

            child.layout(lc, tc, rc, bc);

            left += child.getMeasuredWidth() + lp.rightMargin + lp.leftMargin;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获得它的父容器为它设置的测量模式和大小
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);

        // 如果是warp_content情况下，记录宽和高
        int width = 0;
        int height = 0;
        /**
         * 记录每一行的宽度，width不断取最大宽度
         */
        int lineWidth = 0;
        /**
         * 每一行的高度，累加至height
         */
        int lineHeight = 0;

        int cCount = getChildCount();

        int row = 0;

        // 遍历每个子元素
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            // 测量每一个child的宽和高
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            // 得到child的lp
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            // 当前子空间实际占据的宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            // 当前子空间实际占据的高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            /**
             * 如果加入当前child，则超出最大宽度，则的到目前最大宽度给width，类加height 然后开启新行
             */
            if (lineWidth + childWidth > sizeWidth) {
                width = Math.max(lineWidth, childWidth);// 取最大的
                lineWidth = childWidth; // 重新开启新行，开始记录
                // 叠加当前高度，
                height += lineHeight;
                row++;
                break;
//                // 开启记录下一行的高度
//                lineHeight = childHeight;
            } else
            // 否则累加值lineWidth,lineHeight取最大高度
            {
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight, childHeight);
            }
            // 如果是最后一个，则将当前记录的最大宽度和当前lineWidth做比较
            if (i == cCount - 1) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }
        setMeasuredDimension((modeWidth == MeasureSpec.EXACTLY) ? sizeWidth : width, (modeHeight == MeasureSpec.EXACTLY) ? sizeHeight : height);
    }

    private ImageView getItem(List<Bitmap> bitmaps, int radius, int margin) {
        RoundedImageView image = new RoundedImageView(getContext());
        MarginLayoutParams mlp = new MarginLayoutParams(radius * 2, radius * 2);
        mlp.leftMargin = margin;
        mlp.rightMargin = margin;
        image.setLayoutParams(mlp);
        //image.setImageResource(DeviceUtil.getPor());
        image.setOval(true);
        image.setCornerRadius((float) radius);
        return image;
    }

    private Drawable getLastItem(int num) {
        Bitmap b = BitmapUtils.drawableToBitmap(getResources().getDrawable(R.drawable.grey_circle));
        return BitmapUtils.getDrawableWithText(getContext(), b, String.valueOf(num), R.color.common_text, R.dimen.font_24px);
    }

    public void show(List<Bitmap> bitmaps) {
        int radius = getResources().getDimensionPixelSize(R.dimen.px25);
        int margin = getResources().getDimensionPixelSize(R.dimen.px5);
        show(bitmaps,radius,margin);
    }

    public void show(List<Bitmap> bitmaps,int radius,int margin){
        for (int i = 0; i < bitmaps.size(); i++) {
            ImageView view = getItem(bitmaps, radius, margin);
            addView(view);
        }
    }
}
