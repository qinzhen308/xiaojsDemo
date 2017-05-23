package cn.xiaojs.xma.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import cn.xiaojs.xma.ui.view.TabScroller;

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
 * Date:2016/11/2
 * Desc:
 *
 * ======================================================================================== */
public class TabIndicatorView extends LinearLayout {
	
	private TabScroller mTabScroller;

	public TabIndicatorView(Context context) {
		super(context);
		mTabScroller = new TabScroller(context, this);
		setOrientation(LinearLayout.HORIZONTAL);
	}
	
	public TabIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mTabScroller = new TabScroller(context, this);
		setOrientation(LinearLayout.HORIZONTAL);
	}
	
	public TabIndicatorView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mTabScroller = new TabScroller(context, this);
		setOrientation(LinearLayout.HORIZONTAL);
	}

    public TabScroller getTabScroller(){
        return mTabScroller;
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mTabScroller.dispatchDraw(canvas);
    }
}
