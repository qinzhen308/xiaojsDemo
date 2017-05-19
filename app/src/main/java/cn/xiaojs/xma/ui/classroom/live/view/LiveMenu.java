package cn.xiaojs.xma.ui.classroom.live.view;
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

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.ClassroomPopupWindowLayout;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.main.LiveCtlSessionManager;

public class LiveMenu extends PopupWindow {

    private boolean mIsTeacher;
    private Context mContext;
    private PopupWindow mPopupWindow;
    private View mRootView;

    private ImageView mVideo;
    private ImageView mAudio;
    private ImageView mScale;

    private OnItemClickListener mListener;

    private ClassroomPopupWindowLayout mLayout;
    private int mGravity;

    public LiveMenu(Context context, int gravity) {
        super(context);
        init(context, gravity);
    }

    private void init(Context context, int gravity) {
        mContext = context;
        mIsTeacher = LiveCtlSessionManager.getInstance().getUserMode() == Constants.UserMode.TEACHING;
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_live_menu, null);
        mScale = (ImageView) mRootView.findViewById(R.id.live_menu_scale);
        mAudio = (ImageView) mRootView.findViewById(R.id.live_menu_audio);
        mVideo = (ImageView) mRootView.findViewById(R.id.live_menu_video);
        mLayout = new ClassroomPopupWindowLayout(mContext);

        mVideo.setVisibility(View.GONE);
        mAudio.setVisibility(View.GONE);

        mGravity = gravity;
        mLayout.addContent(mRootView, gravity, ClassroomPopupWindowLayout.DARK_GRAY);
        mPopupWindow = new PopupWindow(mLayout, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());

        mScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onScale();
                }
                dismiss();
            }
        });

        mVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onVideoClose();
                }
                dismiss();
            }
        });

        mAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onAudio();
                }
                dismiss();
            }
        });
    }

    public void show(View anchor) {
        mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        if (mGravity == Gravity.BOTTOM) {
            int margin = mContext.getResources().getDimensionPixelSize(R.dimen.px30) - mRootView.getMeasuredWidth() / 2;
            mLayout.setIndicatorOffsetX(margin);
            showAsDropDown(anchor, 0, -mContext.getResources().getDimensionPixelSize(R.dimen.px180));
        } else if (mGravity == Gravity.TOP){
            int leftOffset = mRootView.getMeasuredWidth() + mContext.getResources().getDimensionPixelSize(R.dimen.px20);
            int topOffset = mContext.getResources().getDimensionPixelSize(R.dimen.px10);
            int margin = (mRootView.getMeasuredHeight() - mContext.getResources().getDimensionPixelSize(R.dimen.px22)) / 2;
            mLayout.setIndicatorOffsetX(margin);
            showAsDropDown(anchor, -leftOffset, topOffset);
        }

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
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
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
        super.dismiss();
    }

    public interface OnItemClickListener {
        void onScale();

        void onAudio();

        void onVideoClose();
    }
}
