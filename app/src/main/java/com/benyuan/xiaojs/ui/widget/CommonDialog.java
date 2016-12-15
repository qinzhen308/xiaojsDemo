package com.benyuan.xiaojs.ui.widget;
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
 * Date:2016/11/15
 * Desc:
 *
 * ======================================================================================== */

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.util.DeviceUtil;

public class CommonDialog extends Dialog {

    private TextView mTitle;
    private TextView mDesc;
    private FrameLayout mContainer;
    private Button mLeftButton;
    private Button mRightButton;
    private OnClickListener leftListener;
    private OnClickListener rightListener;
    private String titleStr;
    private String descStr;

    public CommonDialog(Context context) {
        super(context,R.style.CommonDialog);
        init();
    }

    public CommonDialog(Context context, int themeResId) {
        super(context, R.style.CommonDialog);
        init();
    }

    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        setContentView(R.layout.layout_common_dialog);
        mTitle = (TextView) findViewById(R.id.common_dialog_title);
        mDesc = (TextView) findViewById(R.id.common_dialog_desc);
        mContainer = (FrameLayout) findViewById(R.id.common_dialog_container);
        mLeftButton = (Button) findViewById(R.id.left_btn);
        mRightButton = (Button) findViewById(R.id.right_btn);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        int width = DeviceUtil.getScreenWidth(getContext()) - 2 * getContext().getResources().getDimensionPixelSize(R.dimen.px60);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(
                width,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.CENTER);
        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (leftListener != null){
                    leftListener.onClick();
                }
            }
        });
        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightListener != null){
                    rightListener.onClick();
                }
            }
        });

//        View view = initCustomerView();
//        if (view != null){
//            mTitle.setVisibility(View.GONE);
//            mDesc.setVisibility(View.GONE);
//            mContainer.addView(view);
//        }else {
//            mContainer.setVisibility(View.GONE);
//        }



    }

    public void setCustomView(View customView) {

        if (mContainer != null) {
            mContainer.addView(customView);
        }

        mDesc.setVisibility(View.GONE);
    }

//    protected View initCustomerView(){
//        return null;
//    }

    @Override
    public void show() {
        mTitle.setText(titleStr);
        mDesc.setText(descStr);
        super.show();
    }

    public void setOnLeftClickListener(OnClickListener l){
        leftListener = l;
    }

    public void setOnRightClickListener(OnClickListener l){
        rightListener = l;
    }

    public void setTitle(int resId){
        setTitle(getContext().getString(resId));
    }

    public void setTitle(String title){
        titleStr = title;
    }

    public void setDesc(int resId){
        setDesc(getContext().getString(resId));
    }

    public void setDesc(String desc){
        descStr = desc;
    }

    public interface OnClickListener{
        void onClick();
    }

}
