package cn.xiaojs.xma.ui.classroom.pendingdev;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.PopupWindow;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.main.ClassroomPopupWindowLayout;
import cn.xiaojs.xma.ui.widget.ClosableSlidingLayout;
import cn.xiaojs.xma.ui.widget.HorizontalListView;
import cn.xiaojs.xma.util.DeviceUtil;

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
 * Date:2016/11/29
 * Desc:
 *
 * ======================================================================================== */

public class QuestionAnswer extends Dialog {
    private HorizontalListView mListView;
    private QuestionAnswerAdapter mAdapter;
    private PopupWindow mPopupWindow;

    private int mContentW;
    private int mContentH;
    private int mOffsetX;
    private int mOffsetY;

    public QuestionAnswer(Context context) {
        super(context, R.style.CommonDialog);
        onCreateDialog();
    }

    protected QuestionAnswer(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        onCreateDialog();
    }

    public void onCreateDialog() {
        setCanceledOnTouchOutside(false);
        Window dialogWindow = getWindow();
        dialogWindow.setWindowAnimations(R.style.BottomSheetAnim);
        dialogWindow.setContentView(createView());
        //setContentView 必须在在设置属性之前设置
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0;
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);


        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (mAdapter == null) {
                    mAdapter = new QuestionAnswerAdapter(getContext());
                    mListView.setAdapter(mAdapter);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public View createView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ClosableSlidingLayout mDialogView = (ClosableSlidingLayout) inflater.inflate(R.layout.fragment_classroom_qa, null);
        mDialogView.setTarget(mDialogView.findViewById(R.id.slide_target));
        mListView = (HorizontalListView) mDialogView.findViewById(R.id.raise_hands_stu);
        mDialogView.setSlideListener(new ClosableSlidingLayout.SlideListener() {
            @Override
            public void onClosed() {
                QuestionAnswer.this.dismiss();
            }

            @Override
            public void onOpened() {
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                showInfo(view);
            }
        });

        return mDialogView;
    }

    private void showInfo(View anchor) {
        if (mPopupWindow == null) {
            Context context = getContext();
            mPopupWindow = new PopupWindow(context);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
            mPopupWindow.setTouchable(true);
            mPopupWindow.setFocusable(true);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setWindowLayoutMode(WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT);
            mPopupWindow.setClippingEnabled(false);

            LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflate.inflate(R.layout.layout_classroom_raise_hand_pop, null);
            windowContentView = new ClassroomPopupWindowLayout(context);
            windowContentView.addContent(v, Gravity.BOTTOM);
            windowContentView.measure(0, 0);
            mContentW = windowContentView.getMeasuredWidth();
            mContentH = windowContentView.getMeasuredHeight();
            mPopupWindow.setContentView(windowContentView);
        }
        //重置arrow
        windowContentView.setIndicatorOffsetX(0);

        //获取anchor在屏幕中的位置
        int[] location = new int[2];
        anchor.getLocationOnScreen(location);

        //计算正常情况下的偏移
        mOffsetY = -(anchor.getHeight() + mContentH);
        mOffsetX = -mContentW / 2 + anchor.getWidth() / 2;

        //防止弹出框弹出左边界
        if (location[0] - mContentW / 2 + anchor.getWidth() / 2  < 0 ){
            mOffsetX = -location[0];
            int margin = - mContentW / 2 + location[0] + anchor.getWidth() / 2;
            windowContentView.setIndicatorOffsetX(margin);
        }

        //防止弹出框弹出右边界
        if (location[0] + anchor.getWidth() / 2 + mContentW / 2 > DeviceUtil.getScreenWidth(getContext())){
            mOffsetX = DeviceUtil.getScreenWidth(getContext()) - mContentW - location[0];
            int margin = location[0] + anchor.getWidth() / 2 - (DeviceUtil.getScreenWidth(getContext()) - mContentW / 2);
            windowContentView.setIndicatorOffsetX(margin);
        }

        mPopupWindow.showAsDropDown(anchor, mOffsetX, mOffsetY);
    }

    private ClassroomPopupWindowLayout windowContentView;

}
