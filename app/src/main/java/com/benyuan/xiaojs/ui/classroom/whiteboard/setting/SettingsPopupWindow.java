package com.benyuan.xiaojs.ui.classroom.whiteboard.setting;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

public class SettingsPopupWindow {
    protected final PopupWindow mPopupWindow;
    protected final Rect mPadding = new Rect();

    private View mAnchor;

    public SettingsPopupWindow(Context context) {
        mPopupWindow = new PopupWindow(context);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setWindowLayoutMode(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setClippingEnabled(false);

        if (mPopupWindow.getBackground() != null) {
            mPopupWindow.getBackground().getPadding(mPadding);
        }
    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void showAsAnchorLocation(View anchor, int xOff, int yOff) {
        mAnchor = anchor;
        mPopupWindow.showAsDropDown(mAnchor, xOff, yOff);
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
}
