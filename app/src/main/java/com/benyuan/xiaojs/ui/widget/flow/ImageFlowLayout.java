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
import android.view.View;
import android.widget.ImageView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;

import java.util.List;

public class ImageFlowLayout extends FlowBaseLayout{

    private OnItemClickListener l;

    public ImageFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void show(List<Bitmap> bitmaps){
        if (bitmaps == null || bitmaps.size() == 0)
            return;
        removeAllViews();
        setMaxLines(1);
        for (int i = 0;i<bitmaps.size();i++){
            ImageView image = getItem();
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

    public void setOnItemClickListener(OnItemClickListener l){
        this.l = l;
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    private ImageView getItem(){
        RoundedImageView image = new RoundedImageView(getContext());
        MarginLayoutParams mlp = new MarginLayoutParams(getResources().getDimensionPixelSize(R.dimen.px60),getResources().getDimensionPixelSize(R.dimen.px60));
        mlp.leftMargin = getResources().getDimensionPixelSize(R.dimen.px10);
        mlp.rightMargin = getResources().getDimensionPixelSize(R.dimen.px10);
        image.setLayoutParams(mlp);
        image.setImageResource(R.drawable.default_portrait);
        image.setOval(true);
        image.setCornerRadius((float) getResources().getDimensionPixelSize(R.dimen.px30));
        return image;
    }
}
