package com.benyuan.xiaojs.ui.classroom.live.view;
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
 * Date:2016/10/20
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.live.core.Config;

public class MediaContainerView extends ScrollView {
    private LinearLayout mContainer;
    private int mWidth;
    private int mHeight;
    private int mPadding;
    public MediaContainerView(Context context) {
        super(context);
        init();
    }

    public MediaContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MediaContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_media_container_view,this,true);
        mContainer = (LinearLayout) findViewById(R.id.media_container);
        mWidth = getResources().getDimensionPixelSize(R.dimen.px208);
        mHeight = getResources().getDimensionPixelSize(R.dimen.px117);
        mPadding = getResources().getDimensionPixelSize(R.dimen.px10);
    }

    public void addPlayer(String playPath){
        if (mContainer.getChildCount() >= Config.MAX_PLAYERS){
            //如果数量已达上限，则删除最后1个，再添加最新的
            mContainer.removeView(mContainer.getChildAt(mContainer.getChildCount() - 1));
        }
        PlayerTextureView view = new PlayerTextureView(getContext());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(mWidth,mHeight);
        lp.topMargin = mPadding;
        lp.bottomMargin = mPadding;
        view.setLayoutParams(lp);
        view.setPath(playPath);
        mContainer.addView(view,0);
    }

    public void destroy(){
        if (mContainer == null)
            return;
        int count = mContainer.getChildCount();
        for (int i = 0;i<count;i++){
            View v = mContainer.getChildAt(i);
            if (v instanceof BaseMediaView){
                ((BaseMediaView) v).destroy();
            }
        }
    }

    public void resume(){
        if (mContainer == null)
            return;
        int count = mContainer.getChildCount();
        for (int i = 0;i<count;i++){
            View v = mContainer.getChildAt(i);
            if (v instanceof BaseMediaView){
                ((BaseMediaView) v).resume();
            }
        }
    }

    public void pause(){
        if (mContainer == null)
            return;
        int count = mContainer.getChildCount();
        for (int i = 0;i<count;i++){
            View v = mContainer.getChildAt(i);
            if (v instanceof BaseMediaView){
                ((BaseMediaView) v).pause();
            }
        }
    }
}
