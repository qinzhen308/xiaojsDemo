package cn.xiaojs.xma.ui.widget;
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
 * Date:2016/12/18
 * Desc:
 *
 * ======================================================================================== */

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class CheckSoftInputLayout extends FrameLayout {

    private OnResizeListener mListener;

    public CheckSoftInputLayout(Context context) {
        super(context);
    }

    public CheckSoftInputLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CheckSoftInputLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public CheckSoftInputLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setOnResizeListener(OnResizeListener l){
        mListener = l;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mListener != null){
            mListener.onResize(w,h,oldw,oldh);
        }
    }

    public interface OnResizeListener{
        void onResize(int w,int h,int oldw,int oldh);
    }
}
