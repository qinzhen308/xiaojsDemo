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
import android.widget.ImageView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.GeometryShape;

public class GeometryPop extends SettingsPopupWindow implements View.OnClickListener {
    private int mSelectedShape = GeometryShape.BEELINE;
    public GeometryChangeListener mListener;

    private View mWindowContentView;
    private View mAnchorView;
    private final int mOffsetX;
    private final int mOffsetY;
    private final int mWindowOffset;

    //private ColorPickerView mColorPicker;
    private ImageView mShapeView[];

    public interface GeometryChangeListener {
        public void onGeometryChange(int geometryID);
    }

    public GeometryPop(Context context) {
        super(context);
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mWindowContentView = inflate.inflate(R.layout.layout_wb_geometry_pop, null);
        mPopupWindow.setContentView(mWindowContentView);

        mWindowOffset = getDimensionPixelSize(context, R.dimen.px1);
        mWindowContentView.measure(0, 0);
        mOffsetX = -mPadding.left + mWindowOffset;
        mOffsetY = mWindowContentView.getMeasuredHeight() + mPadding.top;

        init();
    }

    private void init() {
        mShapeView = new ImageView[]{
                (ImageView) mWindowContentView.findViewById(R.id.shape_rectangle),
                (ImageView) mWindowContentView.findViewById(R.id.shape_oval),
                (ImageView) mWindowContentView.findViewById(R.id.shape_beeline),
                (ImageView) mWindowContentView.findViewById(R.id.shape_triangle),
        };
        for (int i = 0; i < mShapeView.length; i++) {
            mShapeView[i].setOnClickListener(this);
        }
    }

    public void show(View anchor, int anchorHeight) {
        if (mAnchorView == null) {
            mAnchorView = anchor;
        }
        showAsAnchorLocation(anchor, 0, -(mOffsetY + anchorHeight));
    }

    public int getCurrentShape() {
        return mSelectedShape;
    }

    public void setOnShapeChangeListener(GeometryChangeListener listener) {
        mListener = listener;
    }

    public void setShapeParams(int selectedColor, int selectedShape) {
        mSelectedShape = selectedShape;
        //mShapeView[mSelectedShape].setColor(mSelectedColor, false);
    }

    public void updateShapeColor(int selectedColor) {
        //mShapeView[mSelectedShape].setColor(selectedColor, false);
    }

    public void updateShapeSelectedState(int selectedId) {
        for (int i = 0; i < mShapeView.length; i++) {
            mShapeView[i].setSelected(selectedId == i);
        }
    }

    public void setColor(int color) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.shape_rectangle:
                mSelectedShape = GeometryShape.RECTANGLE;
                break;
            case R.id.shape_oval:
                mSelectedShape = GeometryShape.OVAL;
                break;
            case R.id.shape_beeline:
                mSelectedShape = GeometryShape.BEELINE;
                break;
            case R.id.shape_triangle:
                mSelectedShape = GeometryShape.TRIANGLE;
                break;
        }

        if (mListener != null) {
            mListener.onGeometryChange(mSelectedShape);
        }

        GeometryPop.this.dismiss();
    }

}
