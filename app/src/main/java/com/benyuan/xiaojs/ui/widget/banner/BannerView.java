package com.benyuan.xiaojs.ui.widget.banner;
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
 * Date:2016/10/29
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;

import com.benyuan.xiaojs.R;

public class BannerView extends FrameLayout {

    private ScrollerViewPager mScroller;
    private PointIndicateView mPoint;
    public BannerView(Context context) {
        super(context);
        init();
    }

    public BannerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BannerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        mScroller = new ScrollerViewPager(getContext());
        LayoutParams scrollerLp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mScroller.setLayoutParams(scrollerLp);
        addView(mScroller);
        mPoint = new PointIndicateView(getContext());
        LayoutParams pointLp = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        pointLp.gravity = Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL;
        pointLp.bottomMargin = getResources().getDimensionPixelSize(R.dimen.px20);
        mPoint.setLayoutParams(pointLp);
        addView(mPoint);
    }

    public void setAdapter(PagerAdapter adapter){
        mScroller.setAdapter(adapter);
        mPoint.setViewPager(mScroller,adapter.getCount(),0,true,null);
    }

    public void start(){
        if (mScroller != null){
            mScroller.startAutoScroll();
        }
    }
    public void start(int delayTimeInMills){
        if (mScroller != null){
            mScroller.startAutoScroll(delayTimeInMills);
        }
    }

    public void stop(){
        if (mScroller != null){
            mScroller.stopAutoScroll();
        }
    }
}
