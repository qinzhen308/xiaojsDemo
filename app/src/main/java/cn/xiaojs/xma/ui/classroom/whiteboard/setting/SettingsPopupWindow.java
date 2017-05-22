package cn.xiaojs.xma.ui.classroom.whiteboard.setting;
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
 * Date:2016/10/24
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import cn.xiaojs.xma.ui.classroom.main.ClassroomPopupWindowLayout;

public abstract class SettingsPopupWindow {
    protected final PopupWindow mPopupWindow;
    protected final Rect mPadding = new Rect();
    protected int mAnchorPaddingTop;
    protected ClassroomPopupWindowLayout mPopWindowLayout;
    protected int mAnchorWidth;
    protected int mAnchorHeight;
    protected Context mContext;

    protected int mOffsetX;
    protected int mOffsetY;

    protected int mContentW;
    protected int mContentH;

    protected View mAnchorView;

    public SettingsPopupWindow(Context context) {
        mContext = context;
        mPopupWindow = new PopupWindow(context);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable());
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setWindowLayoutMode(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setClippingEnabled(false);

        if (mPopupWindow.getBackground() != null) {
            mPopupWindow.getBackground().getPadding(mPadding);
        }

        addContent(context);
    }


    private void addContent(Context context) {
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = createView(inflate);
        mPopWindowLayout = new ClassroomPopupWindowLayout(context);
        mPopWindowLayout.addContent(v, Gravity.BOTTOM);
        mPopupWindow.setContentView(mPopWindowLayout);

        mPopWindowLayout.measure(0, 0);
        mContentW = mPopWindowLayout.getMeasuredWidth();
        mContentH = mPopWindowLayout.getMeasuredHeight() + mPadding.bottom;
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void showAsAnchorTop(View anchor) {
        if (mAnchorView == null) {
            mAnchorView = anchor;

            mAnchorHeight = mAnchorView.getMeasuredHeight();
            mAnchorWidth = mAnchorView.getMeasuredWidth();
            mOffsetY = -(mAnchorHeight + mAnchorPaddingTop + mContentH);
            mOffsetX = -mContentW / 2 + mAnchorWidth / 2;
        }

        mPopupWindow.showAsDropDown(anchor, mOffsetX, mOffsetY);
    }

    public void dismiss() {
        mPopupWindow.dismiss();
    }

    public void setDismissListener(PopupWindow.OnDismissListener l) {
        mPopupWindow.setOnDismissListener(l);
    }

    public int getDimensionPixelSize(Context context, int resId) {
        return context.getResources().getDimensionPixelSize(resId);
    }

    public abstract View createView(LayoutInflater inflate);
}
