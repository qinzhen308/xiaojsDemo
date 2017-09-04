package cn.xiaojs.xma.ui.classroom.whiteboard.setting;
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

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;

public class GeometryPop extends SettingsPopupWindow implements View.OnClickListener {
    private int mSelectedShape = GeometryShape.BEELINE;

    public GeometryChangeListener mListener;

    //private ColorPickerView mColorPicker;
    private ImageView mShapeView[];

    public interface GeometryChangeListener {
        public void onGeometryChange(int geometryID);
    }

    public GeometryPop(Context context) {
        super(context);

        initViews();
    }

    @Override
    public View createView(LayoutInflater inflate) {
        return inflate.inflate(R.layout.layout_wb_geometry_pop, null);
    }

    private void initViews() {
        mShapeView = new ImageView[]{
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_rectangle),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_oval),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_beeline),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_triangle),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_square),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_arc_line),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_trapezoid),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_pentagon),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_hexagon),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_sine_curve),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_arrow),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_dash_line),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_coordinate),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_rectangular_coordinate),
                (ImageView) mPopWindowLayout.findViewById(R.id.shape_xyz_coordinate),
        };
        for (int i = 0; i < mShapeView.length; i++) {
            mShapeView[i].setOnClickListener(this);
        }
    }

    public void show(View anchor, int panelWidth) {
        super.showAsAnchorTop(anchor);
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
            case R.id.shape_square:
                mSelectedShape = GeometryShape.SQUARE;
                break;
            case R.id.shape_arc_line:
                mSelectedShape = GeometryShape.ARC_LINE;
                break;
            case R.id.shape_trapezoid:
                mSelectedShape = GeometryShape.TRAPEZOID;
                break;
            case R.id.shape_pentagon:
                mSelectedShape = GeometryShape.PENTAGON;
                break;
            case R.id.shape_hexagon:
                mSelectedShape = GeometryShape.HEXAGON;
                break;
            case R.id.shape_sine_curve:
                mSelectedShape = GeometryShape.SINE_CURVE;
                break;
            case R.id.shape_arrow:
                mSelectedShape = GeometryShape.ARROW;
                break;
            case R.id.shape_dash_line:
                mSelectedShape = GeometryShape.DASH_LINE;
                break;
            case R.id.shape_coordinate:
                mSelectedShape = GeometryShape.COORDINATE;
                break;
            case R.id.shape_rectangular_coordinate:
                mSelectedShape = GeometryShape.RECTANGULAR_COORDINATE;
                break;
            case R.id.shape_xyz_coordinate:
                mSelectedShape = GeometryShape.XYZ_COORDINATE;
                break;

        }

        if (mListener != null) {
            mListener.onGeometryChange(mSelectedShape);
        }

        GeometryPop.this.dismiss();
    }

}
