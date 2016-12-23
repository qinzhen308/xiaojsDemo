package cn.xiaojs.xma.ui.classroom.whiteboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.FrameLayout;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;

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
 * Date:2016/12/23
 * Desc:
 *
 * ======================================================================================== */

public class WhiteboardLayout extends FrameLayout {
    private EditText mWhiteboardEdit;
    private Whiteboard mWhiteboard;

    public WhiteboardLayout(Context context) {
        super(context);
        init();
    }

    public WhiteboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WhiteboardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Context context = getContext();
        EditText edt = new EditText(context);
        Whiteboard wb = new Whiteboard(context);
        wb.setEditText(edt);

        FrameLayout.LayoutParams edtPrams = new FrameLayout.LayoutParams(10, 10);
        FrameLayout.LayoutParams wbPrams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(edt, edtPrams);
        addView(wb, wbPrams);

        mWhiteboardEdit = edt;
        mWhiteboard = wb;
    }

    public EditText getWhiteboardEdit(){
        return mWhiteboardEdit;
    }

    public Whiteboard getWhiteboard() {
        return mWhiteboard;
    }

}
