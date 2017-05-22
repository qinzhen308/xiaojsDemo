package cn.xiaojs.xma.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;

import cn.xiaojs.xma.ui.classroom.whiteboard.OnImeBackListener;

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
 * Date:2017/1/10
 * Desc:
 *
 * ======================================================================================== */

public class SpecialEditText extends EditText {
    private OnImeBackListener onImeBackListener;

    public SpecialEditText(Context context) {
        super(context);
    }

    public SpecialEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpecialEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public SpecialEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (onImeBackListener != null) {
                onImeBackListener.onImeBackPressed();
            }
        }
        return super.dispatchKeyEventPreIme(event);
    }


    public void setOnImeBackListener(OnImeBackListener listener) {
        onImeBackListener = listener;
    }

}
