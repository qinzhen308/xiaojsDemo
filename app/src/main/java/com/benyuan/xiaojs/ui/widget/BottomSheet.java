package com.benyuan.xiaojs.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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
 * Date:2016/10/28
 * Desc:
 *
 * ======================================================================================== */

public class BottomSheet extends Dialog implements DialogInterface.OnCancelListener,
        DialogInterface.OnDismissListener, View.OnClickListener {

    private View mContentView;// dialog content view
    private String mTitleText;
    private Button mLeftBtn;
    private Button mRightBtn;
    private View.OnClickListener mLeftBtnClickListener;
    private View.OnClickListener mRightBtnClickListener;

    private OnDismissListener mOnDismissListener;

    private LinearLayout mTitleLayout;


    public BottomSheet(Context context) {
        super(context, R.style.CommonDialog);
        init();
    }

    public BottomSheet(Context context, int themeResId) {
        super(context, R.style.CommonDialog);
        init();
    }

    protected BottomSheet(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        setContentView(R.layout.bottom_sheet_dialog);
        dialogWindow.setWindowAnimations(R.style.BottomSheetAnim);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);

        mRightBtn = (Button) dialogWindow.findViewById(R.id.left_btn);
        mLeftBtn = (Button) dialogWindow.findViewById(R.id.right_btn);

        mLeftBtn.setOnClickListener(this);
        mRightBtn.setOnClickListener(this);

        setOnCancelListener(this);
        setOnDismissListener(this);
    }

    public void setTitleVisibility(int visibility) {
        if (mTitleLayout != null) {
            mTitleLayout.setVisibility(visibility);
        }
    }

    public void setContent(View content) {
        ((FrameLayout) getWindow().findViewById(R.id.content_dialog)).addView(content);
    }

    public void setContent(int contentId) {
        View content = getLayoutInflater().inflate(contentId, null);
        ((FrameLayout) getWindow().findViewById(R.id.content_dialog)).addView(content);
    }

    public void setMiddleText(String text) {
        ((TextView) getWindow().findViewById(R.id.title_dialog)).setText(text);
    }

    public void setLeftBtnText(String text) {
        ((Button) getWindow().findViewById(R.id.left_btn)).setText(text);
    }

    public void setRightBtnText(String text) {
        ((Button) getWindow().findViewById(R.id.right_btn)).setText(text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_btn:
                dismiss();
                if (mLeftBtnClickListener != null) {
                    mLeftBtnClickListener.onClick(v);
                }
                break;
            case R.id.right_btn:
                dismiss();
                if (mRightBtnClickListener != null) {
                    mRightBtnClickListener.onClick(v);
                }
                break;
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (mOnDismissListener != null) {
            mOnDismissListener.onClose();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if (mOnDismissListener != null) {
            mOnDismissListener.onClose();
        }
    }

    public void setLeftBtnClickListener(View.OnClickListener listener) {
        mLeftBtnClickListener = listener;
    }

    public void setRightBtnClickListener(View.OnClickListener listener) {
        mRightBtnClickListener = listener;
    }

    public void setOnDismissListener(OnDismissListener listener) {
        mOnDismissListener = listener;
    }

    public interface OnDismissListener{
        public void onClose();
    }
}
