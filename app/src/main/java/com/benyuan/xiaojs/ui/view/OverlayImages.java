package com.benyuan.xiaojs.ui.view;
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
 * Date:2016/12/11
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;
import com.benyuan.xiaojs.ui.widget.flow.FlowBaseLayout;

import java.util.List;

public class OverlayImages extends FlowBaseLayout {
    public OverlayImages(Context context, AttributeSet attrs) {
        super(context, attrs);
        isOverlay(true);
        setMaxLines(1);
        setOverlayWidth(IMAGE_OVERLAY);
    }

    private List<RoundedImageView> mImages;
    private final int IMAGE_WIDTH = getResources().getDimensionPixelSize(R.dimen.px50);
    private final int IMAGE_OVERLAY = getResources().getDimensionPixelSize(R.dimen.px10);

    private void init() {
        addView(getImageItem());
        addView(getImageItem());
        addView(getImageItem());
        addView(getImageItem());
        addView(getImageItem());
        addView(getImageItem());
        addView(getImageItem());
        addView(getImageItem());
        addView(getImageItem());
        addView(getImageItem());
        addView(getImageItem());
        addView(getImageItem());
        addView(getImageItem());
    }

    public void show(){
        removeAllViews();
        init();
    }

    private RoundedImageView getImageItem() {
        RoundedImageView image = new RoundedImageView(getContext());
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(IMAGE_WIDTH, IMAGE_WIDTH);
        image.setLayoutParams(lp);
        image.setImageResource(R.drawable.default_portrait);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        image.setOval(true);
        return image;
    }
}
