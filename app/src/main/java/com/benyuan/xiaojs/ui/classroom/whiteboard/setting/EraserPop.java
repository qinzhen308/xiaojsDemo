package com.benyuan.xiaojs.ui.classroom.whiteboard.setting;
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
 * Date:2016/10/24
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.whiteboard.widget.PaintPathPreview;

public class EraserPop extends SettingsPopupWindow implements View.OnClickListener{
    public static final int MIN_ERASER_SIZE = 10;
    public static final int MAX_ERASER_SIZE = 60;
    public static final int DEFAULT_ERASER_SIZE = (MAX_ERASER_SIZE + MIN_ERASER_SIZE) / 2;

    private final int mOffsetX;
    private final int mOffsetY;
    private final int mWindowOffset;

    private EraserChangeListener mParamsChangeListener;

    private View mAnchorView;
    private View mWindowContentView;

    private TextView mClearDoodleButton;
    private PaintPathPreview mEraserSizeView;


    public interface EraserChangeListener {
        void onEraserPaintSize(int size);

        void onClearDoodles();
    }

    public EraserPop(Context context) {
        super(context);
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindowContentView = inflate.inflate(R.layout.layout_wb_eraser_pop, null);
        mPopupWindow.setContentView(mWindowContentView);

        mWindowOffset = getDimensionPixelSize(context, R.dimen.px1);
        mWindowContentView.measure(0, 0);
        mOffsetX = mWindowContentView.getMeasuredWidth() + mPadding.left + mWindowOffset;
        mOffsetY = mWindowContentView.getMeasuredHeight() + mPadding.top;

        initViews(context);
    }

    private void initViews(Context context) {
        mClearDoodleButton = (TextView) mWindowContentView.findViewById(R.id.clear_all);
        mClearDoodleButton.setOnClickListener(this);
    }

    public void setOnEraserParamsListener(EraserChangeListener listener) {
        mParamsChangeListener = listener;
    }

    public void show(View anchor, int anchorHeight) {
        if (mAnchorView == null) {
            mAnchorView = anchor;
        }
        showAsAnchorLocation(anchor, 0, -(mOffsetY + anchorHeight));
    }

    @Override
    public void onClick(View v) {
        if (mParamsChangeListener != null) {
            mParamsChangeListener.onClearDoodles();
            EraserPop.this.dismiss();
        }
    }
}
