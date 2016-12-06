package com.benyuan.xiaojs.ui.classroom.live.view;
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
 * Date:2016/12/1
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.ClassroomPopupWindowLayout;

public class LiveMenu extends PopupWindow {

    private boolean mIsTeacher;
    private Context mContext;
    private PopupWindow mPopupWindow;
    private View mRootView;

    private ImageView mSwitchCamera;
    private ImageView mClose;
    private ImageView mMute;
    private ImageView mScale;

    private OnItemClickListener mListener;

    private ClassroomPopupWindowLayout mLayout;

    public LiveMenu(Context context,boolean isTeacher){
        super(context);
        mIsTeacher = isTeacher;
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_live_menu,null);
        mSwitchCamera = (ImageView) mRootView.findViewById(R.id.live_menu_switch);
        mScale = (ImageView) mRootView.findViewById(R.id.live_menu_scale);
        mMute = (ImageView) mRootView.findViewById(R.id.live_menu_audio);
        mClose = (ImageView) mRootView.findViewById(R.id.live_menu_video);
        mLayout = new ClassroomPopupWindowLayout(mContext);
        int gravity = Gravity.TOP;
        if (!mIsTeacher)
            gravity = Gravity.LEFT;

        mLayout.addContent(mRootView, gravity);
        mPopupWindow = new PopupWindow(mLayout, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        mSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onSwitchCamera();
                }
                dismiss();
            }
        });

        mScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onScale();
                }
                dismiss();
            }
        });

        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onClose();
                }
                dismiss();
            }
        });

        mMute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null){
                    mListener.onMute();
                }
                dismiss();
            }
        });
    }

    public void show(View anchor){
        mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        if (mIsTeacher){
            int margin = mContext.getResources().getDimensionPixelSize(R.dimen.px30) - mRootView.getMeasuredWidth() / 2;
            mLayout.setIndicatorOffsetX(margin);
            showAsDropDown(anchor);
        }else {
            int leftOffset = mRootView.getMeasuredWidth() + mContext.getResources().getDimensionPixelSize(R.dimen.px13);
            int topOffset = anchor.getMeasuredHeight() - (anchor.getMeasuredHeight() - mRootView.getMeasuredHeight()) / 2;
            int margin= (mRootView.getMeasuredHeight() - mContext.getResources().getDimensionPixelSize(R.dimen.px22)) / 2;
            mLayout.setIndicatorOffsetX(margin);
            showAsDropDown(anchor,-leftOffset,-topOffset);
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @Override
    public void showAsDropDown(View anchor) {
        mPopupWindow.showAsDropDown(anchor, 0, 0);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.update();
    }

    @Override
    public void showAsDropDown(View parent, int x, int y) {
        mPopupWindow.showAsDropDown(parent, x, y);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setFocusable(true);
        mPopupWindow.update();
    }

    @Override
    public void dismiss() {
        if (mPopupWindow !=  null){
            mPopupWindow.dismiss();
        }
        super.dismiss();
    }

    public interface OnItemClickListener{
        void onScale();
        void onMute();
        void onClose();
        void onSwitchCamera();
    }
}
