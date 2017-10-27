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
 * Date:2016/11/15
 * Desc:
 *
 * ======================================================================================== */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.annotation.ColorRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.util.DeviceUtil;

public class CommonDialog extends Dialog {

    private TextView mTitle;
    private TextView mDesc;
    private RelativeLayout mNormal;
    private FrameLayout mContainer;
    private Button mLeftButton;
    private Button mRightButton;
    private OnClickListener leftListener;
    private OnClickListener rightListener;
    private String titleStr;
    private String descStr;
    private String okStr;
    private Context mContext;
    private LinearLayout bottomLay;
    private LinearLayout rootLay;
    private View bottomLineView;

    public CommonDialog(Context context) {
        super(context, R.style.CommonDialog);
        init(context);
    }

    public CommonDialog(Context context, int themeResId) {
        super(context, R.style.CommonDialog);
        init(context);
    }

    protected CommonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        setContentView(R.layout.layout_common_dialog);
        mTitle = (TextView) findViewById(R.id.common_dialog_title);
        mDesc = (TextView) findViewById(R.id.common_dialog_desc);
        mContainer = (FrameLayout) findViewById(R.id.common_dialog_container);
        bottomLay = (LinearLayout) findViewById(R.id.bottom_lay);
        rootLay = (LinearLayout) findViewById(R.id.root_lay);
        bottomLineView = findViewById(R.id.bottom_line);

//        FrameLayout rootLay = (FrameLayout) findViewById(R.id.common_dialog_root);
//        rootLay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dismiss();
//            }
//        });
        mNormal = (RelativeLayout) findViewById(R.id.common_dialog_normal_wrapper);
        mLeftButton = (Button) findViewById(R.id.left_btn);
        mRightButton = (Button) findViewById(R.id.right_btn);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        int width = DeviceUtil.getScreenWidth(getContext()) - 2 * getContext().getResources().getDimensionPixelSize(R.dimen.px60);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);

        dialogWindow.setLayout(width, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.CENTER);

        mLeftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (leftListener != null) {
                    leftListener.onClick();
                }
            }
        });
        mRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightListener != null) {
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

    public void setDialogLayout(int w, int h) {
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(w, h);
    }

    public void setRootBackgroud(@ColorRes int color) {
        rootLay.setBackgroundColor(getContext().getResources().getColor(color));
    }

    public void setBottomButtonVisibility(int visibility) {
        bottomLineView.setVisibility(visibility);
        bottomLay.setVisibility(visibility);
    }

    public void setCustomView(View customView) {

        if (mContainer != null) {
            mContainer.setVisibility(View.VISIBLE);
            mContainer.addView(customView);
        }
        mDesc.setVisibility(View.GONE);
    }

//    protected View initCustomerView(){
//        return null;
//    }

    @Override
    public void show() {
        if (TextUtils.isEmpty(titleStr) && TextUtils.isEmpty(descStr)) {
            mNormal.setVisibility(View.GONE);
        } else {
            mNormal.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(titleStr)) {
            mTitle.setText(titleStr);
        } else {
            mTitle.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(descStr)) {
            mDesc.setText(descStr);
        } else {
            mDesc.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(okStr)) {
            mRightButton.setText(okStr);
        }

        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (mContext instanceof Activity) {
            if (((Activity) mContext).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                width = DeviceUtil.getScreenWidth(mContext) / 2;
                height = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        }
        setDialogLayout(width, height);

        super.show();
    }

    public void setOkText(String str) {
        okStr = str;
    }

    public void setOnLeftClickListener(OnClickListener l) {
        leftListener = l;
    }

    public void setOnRightClickListener(OnClickListener l) {
        rightListener = l;
    }

    public void setTitle(int resId) {
        setTitle(getContext().getString(resId));
    }

    public void setTitle(String title) {
        titleStr = title;
    }

    public void setDesc(int resId) {
        setDesc(getContext().getString(resId));
    }

    public void setLefBtnText(int resId) {
        mLeftButton.setText(resId);
    }

    public void setRightBtnText(int resId) {
        mRightButton.setText(resId);
    }

    public void setDesc(String desc) {
        descStr = desc;
    }




    public interface OnClickListener {
        void onClick();
    }

}
