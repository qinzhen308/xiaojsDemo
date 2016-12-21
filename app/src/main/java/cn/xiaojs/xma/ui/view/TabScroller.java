/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 作者：张鑫
 * 说明：tab相关的辅助类，可以让tab下面的指示背景跟随页面滑动而滑动
 * 日期：2013-9-12
 */
package cn.xiaojs.xma.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.util.XjsUtils;

import java.util.ArrayList;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:Administrator
 * Date:2016/11/2
 * Desc:
 *
 * ======================================================================================== */
public class TabScroller {
    //tab下面的指示背景图
    private Drawable mTabIndicator;
    //指示背景图的高度
    private int mTabIndicatorHeight = 10;
    
    //tab所在的直接父view
    private ViewGroup mTabParentView;
    private int mCurrentTabPos = 0;
    private float mCurrentTabOffset;
    private float mOldTabOffset = 0;
    //用来存储所有的tab
    private ArrayList<View> mTabViews = new ArrayList<View>();
    private boolean alignTextBottom = true;
    
    public TabScroller(Context context, ViewGroup view) {
        //mTabIndicator = context.getResources().getDrawable(R.mipmap.tab_indicator_selected);
        //mTabIndicatorHeight = mTabIndicator.getIntrinsicHeight();
        mTabIndicator = new ColorDrawable(context.getResources().getColor(R.color.main_orange));
        mTabIndicatorHeight = XjsUtils.dip2px(context, 2.5f);
        mTabParentView = view;
    }
    
    public void setAlignTextBottom(boolean alignBottom) {
    	alignTextBottom = alignBottom;
    }
    
    /**
     * 设置tab的指示背景
     * @param drawable
     */
    public void setTabIndicatorDrawable(Drawable drawable) {
        if (drawable != null) {
            mTabIndicator = drawable;
            mTabIndicatorHeight = mTabIndicator.getIntrinsicHeight();
        }
    }
    
    /**
     * 设置当前指示背景的位置
     * @param position
     */
    public void setCurrentTab(int position){
        if (mTabParentView == null) {
            return;
        }
        
        mCurrentTabPos = position;
        mCurrentTabOffset = 0;
        mTabParentView.invalidate();
    }
    
    /**
     * 添加一个新的tab view
     * @param view
     */
    public void addTabView(View view){
        if (view != null && !mTabViews.contains(view) ) {
            mTabViews.add(view);
        }
    }
    
    /**
     * 添加一个新的tab view，允许指定tab的插入位置
     * @param index 指定tab的位置
     * @param view
     */
    public void addTabView(int index, View view) {
        if (view != null && !mTabViews.contains(view)) {
            mTabViews.add(index, view);  
        }
    }
    /**
     * 删除指定的tab
     * @param view
     * @return true 表示删除成功，false反之
     */
    public boolean removeTabView(View view) {
        boolean flag = mTabViews.remove(view);
        if (mTabParentView != null) {
            mTabParentView.invalidate();
        }
        return flag;
    }
    
    /**
     * 根据索引删除指定的tab
     * @param index tab所在的位置
     * @return true 表示删除成功，false反之
     */
    public boolean removeTabView(int index) {
        return removeTabView(mTabViews.get(index));
    }
    /**
     * 指定当前tab的位置及偏移量，以便能够正确的绘制指示背景
     * @param position 当前tab的位置
     * @param positionOffset 当前tab的偏移量
     */
    public void onTabScrolled(int position, float positionOffset) {
        mCurrentTabPos = position;
        mCurrentTabOffset = positionOffset;
        if (mTabParentView != null) {
            mTabParentView.invalidate();
        }
    } 
    
    public void clearTabView() {
    	mTabViews.clear();
    	mOldTabOffset = 0;
    	mCurrentTabOffset = 0;
        if (mTabParentView != null) {
            mTabParentView.invalidate();
        }
    }
    
    /**
     * 根据设置好的位置和偏移量绘制指示背景
     * @param canvas tab 所在的父viｅｗ的canvas
     */
    public void dispatchDraw(Canvas canvas) {
        int tabSize = mTabViews.size();
        if (tabSize == 0) {
            return;
        }

        if (mCurrentTabPos >= tabSize) {
            mCurrentTabPos = tabSize - 1;
        } else if (mCurrentTabPos < 0) {
            mCurrentTabPos = 0;
        }
        
        View mCurTab = mTabViews.get(mCurrentTabPos);
        int width = mCurTab.getWidth();
        int height = mCurTab.getHeight();
        float x = mCurTab.getLeft() + width * mCurrentTabOffset;
        
        View nextTab;
        float deltaWidth = 0;
        //有些时候相邻的两个tab长度不一样，所以需要在平移的过程中动态的增加或者缩短指示背景的长度
        if (mCurrentTabOffset > mOldTabOffset && mCurrentTabPos < tabSize - 1) {
            //表示当前是向右滑动
            nextTab = mTabViews.get(mCurrentTabPos + 1);
            deltaWidth = (nextTab.getWidth() - width) * mCurrentTabOffset;
        } else if (mCurrentTabOffset < mOldTabOffset&& mCurrentTabPos > 0) {
            //表示当前是向左滑动
            nextTab = mTabViews.get(mCurrentTabPos - 1);
            deltaWidth = (nextTab.getWidth() - width) * mCurrentTabOffset;
        }
        
        if (alignTextBottom) {
        	//align bottom
        	mTabIndicator.setBounds((int)x, height - mTabIndicatorHeight, (int)(x + deltaWidth) + width, height);
        } else {
        	//align top
        	mTabIndicator.setBounds((int)x, 0, (int)(x + deltaWidth) + width, mTabIndicatorHeight);
        }
        
        canvas.save();
        mTabIndicator.draw(canvas);
        
        canvas.restore();
    }
}
