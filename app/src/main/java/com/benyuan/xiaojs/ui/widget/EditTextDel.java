package com.benyuan.xiaojs.ui.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

import com.benyuan.xiaojs.R;

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

public class EditTextDel extends EditText {
    private Drawable mDelDrawable;
    private boolean isNeedDelete = true;

    public EditTextDel(Context context) {
        super(context);
        init();
    }

    public EditTextDel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextDel(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mDelDrawable = getResources().getDrawable(R.drawable.ic_edit_text_del);
        try {
            setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.px20));
        } catch (Exception e) {

        }
        addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setDrawable();
            }
        });
        setDrawable();
    }

    private void setDrawable() {
        Drawable[] drawable = getCompoundDrawables();
        if (isNeedDelete && length() > 0 && isFocused() && isEnabled()) {
            setCompoundDrawablesWithIntrinsicBounds(drawable[0], drawable[1], mDelDrawable, drawable[3]);
        } else {
            setCompoundDrawablesWithIntrinsicBounds(drawable[0], drawable[1], null, drawable[3]);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setDrawable();
    }

    public void setEdtText(String text) {
        setText(text);
    }

    /**
     * 是否需要右侧的删除按钮
     *
     * @param isNeedDel
     */
    public void isNeedDelete(boolean isNeedDel) {
        isNeedDelete = isNeedDel;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP && isFocused()
                && isEnabled()) {
            if (mDelDrawable != null && isNeedDelete) {
                if ((event.getX() > (getWidth() - getTotalPaddingRight()))
                        && (event.getX() < (getWidth() - getPaddingRight()))) {
                    setText("");
                }
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setDrawable();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

}
