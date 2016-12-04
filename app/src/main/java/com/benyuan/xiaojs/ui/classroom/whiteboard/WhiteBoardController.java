package com.benyuan.xiaojs.ui.classroom.whiteboard;
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
 * Date:2016/12/2
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.GeometryShape;
import com.benyuan.xiaojs.ui.classroom.whiteboard.setting.ColorPickerPop;
import com.benyuan.xiaojs.ui.classroom.whiteboard.setting.EraserPop;
import com.benyuan.xiaojs.ui.classroom.whiteboard.setting.GeometryPop;
import com.benyuan.xiaojs.ui.classroom.whiteboard.setting.HandwritingPop;
import com.benyuan.xiaojs.ui.classroom.whiteboard.setting.TextPop;
import com.benyuan.xiaojs.ui.classroom.whiteboard.widget.CircleView;

public class WhiteBoardController implements
        EraserPop.EraserChangeListener,
        HandwritingPop.PaintChangeListener,
        GeometryPop.GeometryChangeListener,
        ColorPickerPop.ColorChangeListener,
        TextPop.TextChangeListener {

    private ImageView mSelection;
    private ImageView mHandWriting;
    private ImageView mGeoShape;
    private ImageView mTextWriting;
    private ImageView mEraser;
    private CircleView mColorPicker;

    private HandwritingPop mPaintSetting;
    private EraserPop mEraserSetting;
    private GeometryPop mShapeSetting;
    private ColorPickerPop mColorSetting;
    private TextPop mTextSetting;

    private WhiteBoard mWhiteboard;
    private EditText mWhiteboardEdit;

    private Context mContext;
    private View mAnchorView;
    private int mScreenWidth;

    private int mGeometryId;
    private int mAnchorViewHeight;

    public WhiteBoardController(Context context, View root) {
        mContext = context;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;

        mAnchorView = root.findViewById(R.id.white_board_panel);
        mSelection = (ImageView) root.findViewById(R.id.select_btn);
        mHandWriting = (ImageView) root.findViewById(R.id.handwriting_btn);
        mGeoShape = (ImageView) root.findViewById(R.id.shape_btn);
        mTextWriting = (ImageView) root.findViewById(R.id.text_btn);
        mEraser = (ImageView) root.findViewById(R.id.eraser_btn);
        mColorPicker = (CircleView) root.findViewById(R.id.color_picker_btn);

        mWhiteboard = (WhiteBoard) root.findViewById(R.id.white_board);
        mWhiteboardEdit = (EditText) root.findViewById(R.id.blackboard_edit);
        mGeoShape.setImageResource(R.drawable.wb_oval_selector);

        mWhiteboard.setEditText(mWhiteboardEdit);
        mWhiteboard.setGeometryShapeId(GeometryShape.OVAL);
        mColorPicker.setPaintColor(mWhiteboard.getColor());
        mAnchorView.measure(0, 0);
        mAnchorViewHeight = mAnchorView.getMeasuredHeight();
    }

    public void handlePanelItemClick(View v) {
        if (v.getId() == R.id.color_picker_btn) {
            enterColorPicker();
            return;
        }

        mSelection.setSelected(false);
        mHandWriting.setSelected(false);
        mGeoShape.setSelected(false);
        mTextWriting.setSelected(false);
        mEraser.setSelected(false);
        //mColorPicker.setSelected(false);
        v.setSelected(true);

        switch (v.getId()) {
            case R.id.select_btn:
                enterMode(WhiteBoard.MODE_SELECTION);
                break;

            case R.id.handwriting_btn:
                enterHandWriting();
                break;

            case R.id.shape_btn:
                enterGeometry();
                break;

            case R.id.text_btn:
                enterText();
                break;

            case R.id.eraser_btn:
                enterEraser();
                break;
        }
    }

    private void enterText() {
        if (mWhiteboard.getMode() == WhiteBoard.MODE_TEXT) {
            if (mTextSetting == null) {
                mTextSetting = new TextPop(mContext);
                mTextSetting.setTextChangeListener(this);
            }
            mTextSetting.show(mAnchorView, mAnchorViewHeight);
        }
        enterMode(WhiteBoard.MODE_TEXT);
    }

    private void enterHandWriting() {
        if (mWhiteboard.getMode() == WhiteBoard.MODE_HAND_WRITING) {
            if (mPaintSetting == null) {
                mPaintSetting = new HandwritingPop(mContext);
                mPaintSetting.setOnDoodlePaintParamsListener(this);
            }
            mPaintSetting.show(mAnchorView, mAnchorViewHeight);
        }
        enterMode(WhiteBoard.MODE_HAND_WRITING);
    }

    private void enterEraser() {
        if (mEraserSetting == null) {
            mEraserSetting = new EraserPop(mContext);
            mEraserSetting.setOnEraserParamsListener(this);
        }
        mEraserSetting.show(mAnchorView, mAnchorViewHeight);
        enterMode(WhiteBoard.MODE_ERASER);
    }

    private void enterGeometry() {
        if (mWhiteboard.getMode() == WhiteBoard.MODE_GEOMETRY) {
            if (mShapeSetting == null) {
                mShapeSetting = new GeometryPop(mContext);
                mShapeSetting.setOnShapeChangeListener(this);
                mShapeSetting.setShapeParams(mWhiteboard.getColor(), mGeometryId);
            }
            mShapeSetting.updateShapeSelectedState(mGeometryId);
            mShapeSetting.show(mAnchorView, mAnchorViewHeight);
        }
        enterMode(WhiteBoard.MODE_GEOMETRY);
    }

    private void enterColorPicker() {
        if (mColorSetting == null) {
            mColorSetting = new ColorPickerPop(mContext);
            mColorSetting.setOnColorChangeListener(this);
        }
        mColorSetting.show(mAnchorView, mAnchorViewHeight);
    }

    private void updateGeometryStyle(int geometryId) {
        switch (geometryId) {
            case GeometryShape.ARROW:
                break;

            case GeometryShape.DOUBLE_ARROW:
                break;

            case GeometryShape.RECTANGLE:
                mGeoShape.setImageResource(R.drawable.wb_rectangle_selector);
                break;

            case GeometryShape.BEELINE:
                mGeoShape.setImageResource(R.drawable.wb_beelinel_selector);
                break;

            case GeometryShape.OVAL:
                mGeoShape.setImageResource(R.drawable.wb_oval_selector);
                break;

            case GeometryShape.TRIANGLE:
                mGeoShape.setImageResource(R.drawable.wb_triangle_selector);
                break;

        }
    }

    @Override
    public void onColorChange(int color) {
        mColorPicker.setPaintColor(color);
        mWhiteboard.setColor(color);
    }

    @Override
    public void onColorPick() {
        //mColorPicker.setPaintColor(color);
    }

    @Override
    public void onEraserPaintSize(int size) {

    }

    @Override
    public void onClearDoodles() {
        mWhiteboard.clearWhiteboard();
    }

    @Override
    public void onGeometryChange(int geometryID) {
        mWhiteboard.setGeometryShapeId(geometryID);
        updateGeometryStyle(geometryID);
    }

    @Override
    public void onPaintSize(int size) {

    }

    @Override
    public void onPaintAlpha(int alpha) {

    }

    @Override
    public void onTextPaintSize(int size) {

    }

    @Override
    public void onTextOrientation(int orientation) {

    }

    @Override
    public void onInsertLine() {

    }

    private void enterMode(int mode) {
        mWhiteboard.switchMode(mode);
    }

    public void release() {
        mWhiteboard.release();
    }
}
