package cn.xiaojs.xma.ui.widget;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.UIUtils;

/**
 * created by Paul Z on 2017/6/7
 */
public class Common2Dialog extends Dialog {

    private TextView mTitle;
    private TextView mDesc;
    private RelativeLayout mNormal;
    private FrameLayout mContainer;
    private Button mOkBtn;
    private OnClickListener leftListener;
    private OnClickListener rightListener;
    private String titleStr;
    private String descStr;
    private String okStr;
    private Context mContext;

    public Common2Dialog(Context context) {
        super(context, R.style.CommonDialog);
        init(context);
    }

    public Common2Dialog(Context context, int themeResId) {
        super(context, R.style.CommonDialog);
        init(context);
    }

    protected Common2Dialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        setContentView(R.layout.layout_common2_dialog);
        mTitle = (TextView) findViewById(R.id.common_dialog_title);
        mDesc = (TextView) findViewById(R.id.common_dialog_desc);
        mContainer = (FrameLayout) findViewById(R.id.common_dialog_container);

        FrameLayout rootLay = (FrameLayout) findViewById(R.id.common_dialog_root);
        rootLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mNormal = (RelativeLayout) findViewById(R.id.common_dialog_normal_wrapper);
        mOkBtn = (Button) findViewById(R.id.close_btn);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        int width = DeviceUtil.getScreenWidth(getContext()) - 2 * getContext().getResources().getDimensionPixelSize(R.dimen.px60);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);

        dialogWindow.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.CENTER);


        mOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rightListener != null) {
                    rightListener.onClick();
                }
                dismiss();
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
            mOkBtn.setText(okStr);
        }

        int width = ViewGroup.LayoutParams.WRAP_CONTENT;
        int height = ViewGroup.LayoutParams.WRAP_CONTENT;
        if (mContext instanceof Activity) {
            if (UIUtils.isLandspace(mContext)) {
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


    public void setOkBtnText(int resId) {
        mOkBtn.setText(resId);
    }

    public void setDesc(String desc) {
        descStr = desc;
    }

    public interface OnClickListener {
        void onClick();
    }

}
