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
 * Author:huangyong
 * Date:2017/5/9
 * Desc:
 *
 * ======================================================================================== */

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import butterknife.ButterKnife;
import cn.xiaojs.xma.R;

public abstract class SheetFragment extends DialogFragment implements ClosableSlidingLayout.SlideListener {
    public final static int SHEET_GRAVITY_LEFT = 0;
    public final static int SHEET_GRAVITY_TOP = 1;
    public final static int SHEET_GRAVITY_RIGHT = 2;
    public final static int SHEET_GRAVITY_BOTTOM = 3;

    public final static String KEY_FRAGMENT_W = "key_contact_fragment_w";
    public final static String KEY_FRAGMENT_H = "key_contact_fragment_h";
    public final static String KEY_SHEET_GRAVITY = "key_contact_sheet_gravity";

    private int mViewHeight;
    private int mViewWidth;

    protected Context mContext;
    protected int mSheetGravity = SHEET_GRAVITY_BOTTOM;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initParams(context);
    }

    private void initParams(Context context) {
        mContext = context;
        Bundle data = getArguments();
        if (data != null) {
            mViewWidth = data.getInt(KEY_FRAGMENT_W);
            mViewHeight = data.getInt(KEY_FRAGMENT_H);
            mSheetGravity = data.getInt(KEY_SHEET_GRAVITY, SHEET_GRAVITY_BOTTOM);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(mContext, R.style.CommonDialog);
        dialog.setCanceledOnTouchOutside(true);

        View view = getContentView();
        //forbid closeable by sliding
        //view.setEnabled(false);
        Window dialogWindow = dialog.getWindow();

        //set attributes
        dialogWindow.setContentView(view);
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        switch (mSheetGravity) {
            case SHEET_GRAVITY_BOTTOM:
                dialogWindow.setWindowAnimations(R.style.BottomSheetAnim);
                dialogWindow.setGravity(Gravity.BOTTOM);
                dialogWindow.setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
                break;
            case SHEET_GRAVITY_RIGHT:
                dialogWindow.setWindowAnimations(R.style.RightSheetAnim);
                dialogWindow.setGravity(Gravity.RIGHT);
                dialogWindow.setLayout(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
                break;
        }
        //set params
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0;
        dialogWindow.setAttributes(params);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                onFragmentShow(dialogInterface);
            }
        });

        ButterKnife.bind(this, dialog);
        onViewCreated(view);

        return dialog;
    }

    @Override
    public void onClosed() {
        SheetFragment.this.dismiss();
        onFragmentClosed();
    }

    @Override
    public void onOpened() {
        onFragmentOpened();
    }

    private View getContentView() {
        View view = onCreateView();
        if (view instanceof ClosableSlidingLayout) {
            View contactView = ((ClosableSlidingLayout) view).getChildAt(0);

            ViewGroup.LayoutParams params = contactView.getLayoutParams();
            switch (mSheetGravity) {
                case SHEET_GRAVITY_BOTTOM:
                    int paramsH = mViewHeight > 0 ? mViewHeight : ViewGroup.LayoutParams.WRAP_CONTENT;
                    if (params == null) {
                        params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, paramsH);
                        contactView.setLayoutParams(params);
                    } else {
                        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
                        params.height = paramsH;
                    }
                    ((ClosableSlidingLayout) view).setSlideOrientation(ClosableSlidingLayout.SLIDE_FROM_TOP_TO_BOTTOM);
                    break;
                case SHEET_GRAVITY_RIGHT:
                    int paramsW = mViewWidth > 0 ? mViewWidth : ViewGroup.LayoutParams.WRAP_CONTENT;
                    if (params == null) {
                        params = new ViewGroup.LayoutParams(paramsW, ViewGroup.LayoutParams.MATCH_PARENT);
                        contactView.setLayoutParams(params);
                    } else {
                        params.width = paramsW;
                        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
                    }
                    ((ClosableSlidingLayout) view).setSlideOrientation(ClosableSlidingLayout.SLIDE_FROM_LEFT_TO_RIGHT);
                    if (view instanceof ClosableAdapterSlidingLayout) {
                        setSlideConflictView((ClosableAdapterSlidingLayout) view);
                    }
                    //view.setSlideConflictView(view.findViewById(R.id.contact_list_view));
                    break;
            }

            ((ClosableSlidingLayout) view).setSlideListener(this);
            //view.setTarget(view.findViewById(R.id.contact_title));
            View targetView = getTargetView(view);
            if (targetView != null) {
                ((ClosableSlidingLayout) view).setTarget(targetView);
            }
        }
        return view;
    }

    protected abstract View onCreateView();

    protected abstract void onFragmentShow(DialogInterface dialogInterface);

    protected void setSlideConflictView(ClosableAdapterSlidingLayout horizontalSlidingLayout) {

    }

    protected abstract View getTargetView(View root);

    protected void onViewCreated(View view) {

    }

    protected void onFragmentClosed() {

    }

    protected void onFragmentOpened() {

    }
}
