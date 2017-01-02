package cn.xiaojs.xma.ui.classroom.whiteboard;
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

import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.ActionRecord;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.UndoRedoListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.WhiteboardConfigs;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.ColorPickerPop;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.EraserPop;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.GeometryPop;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.HandwritingPop;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.TextPop;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.TextWriting;
import cn.xiaojs.xma.ui.classroom.whiteboard.widget.CircleView;

public class WhiteboardController implements
        EraserPop.EraserChangeListener,
        HandwritingPop.PaintChangeListener,
        GeometryPop.GeometryChangeListener,
        ColorPickerPop.ColorChangeListener,
        TextPop.TextChangeListener,
        UndoRedoListener {

    private ImageView mSelection;
    private ImageView mHandWriting;
    private ImageView mGeoShape;
    private ImageView mTextWriting;
    private ImageView mEraser;
    private CircleView mColorPicker;
    private ImageView mUndo;
    private ImageView mRedo;

    private HandwritingPop mPaintSetting;
    private EraserPop mEraserSetting;
    private GeometryPop mShapeSetting;
    private ColorPickerPop mColorSetting;
    private TextPop mTextSetting;

    private Whiteboard mWhiteboard;
    private Whiteboard mOldWhiteboard;
    private View mPanel;

    private Context mContext;
    private int mScreenWidth;

    private int mGeometryId;
    private int mPanelWidth;

    public WhiteboardController(Context context, View root) {
        mContext = context;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;

        mPanel = root.findViewById(R.id.white_board_panel);
        mSelection = (ImageView) root.findViewById(R.id.select_btn);
        mHandWriting = (ImageView) root.findViewById(R.id.handwriting_btn);
        mGeoShape = (ImageView) root.findViewById(R.id.shape_btn);
        mTextWriting = (ImageView) root.findViewById(R.id.text_btn);
        mEraser = (ImageView) root.findViewById(R.id.eraser_btn);
        mColorPicker = (CircleView) root.findViewById(R.id.color_picker_btn);
        mUndo = (ImageView) root.findViewById(R.id.undo);
        mRedo = (ImageView) root.findViewById(R.id.redo);

        mGeoShape.setImageResource(R.drawable.wb_oval_selector);

        mGeoShape.setImageResource(R.drawable.wb_rectangle_selector);
        mPanel.measure(0, 0);
        mPanelWidth = mPanel.getMeasuredWidth();
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
                enterMode(Whiteboard.MODE_SELECTION);
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

            case R.id.undo:
                undo();
                break;

            case R.id.redo:
                redo();
                break;
        }
    }

    private void enterText() {
        if (mWhiteboard == null) {
            return;
        }

        if (mWhiteboard.getMode() == Whiteboard.MODE_TEXT) {
            if (mTextSetting == null) {
                mTextSetting = new TextPop(mContext);
                mTextSetting.setTextChangeListener(this);
            }
            mTextSetting.show(mTextWriting, mPanelWidth);
        }
        enterMode(Whiteboard.MODE_TEXT);
    }

    private void enterHandWriting() {
        if (mWhiteboard == null) {
            return;
        }

        if (mWhiteboard.getMode() == Whiteboard.MODE_HAND_WRITING) {
            if (mPaintSetting == null) {
                mPaintSetting = new HandwritingPop(mContext);
                mPaintSetting.setOnDoodlePaintParamsListener(this);
            }
            mPaintSetting.show(mHandWriting, mPanelWidth);
        }
        enterMode(Whiteboard.MODE_HAND_WRITING);
    }

    private void enterEraser() {
        if (mWhiteboard == null) {
            return;
        }

        if (mEraserSetting == null) {
            mEraserSetting = new EraserPop(mContext);
            mEraserSetting.setOnEraserParamsListener(this);
        }
        mEraserSetting.show(mEraser, mPanelWidth);
        enterMode(Whiteboard.MODE_ERASER);
    }

    private void enterGeometry() {
        if (mWhiteboard == null) {
            return;
        }

        if (mWhiteboard.getMode() == Whiteboard.MODE_GEOMETRY) {
            if (mShapeSetting == null) {
                mShapeSetting = new GeometryPop(mContext);
                mShapeSetting.setOnShapeChangeListener(this);
                mShapeSetting.setShapeParams(mWhiteboard.getPaintColor(), mGeometryId);
            }
            mShapeSetting.updateShapeSelectedState(mGeometryId);
            mShapeSetting.show(mGeoShape, mPanelWidth);
        }
        enterMode(Whiteboard.MODE_GEOMETRY);
    }

    private void enterColorPicker() {
        if (mWhiteboard == null) {
            return;
        }

        if (mColorSetting == null) {
            mColorSetting = new ColorPickerPop(mContext);
            mColorSetting.setOnColorChangeListener(this);
        }
        mColorSetting.show(mColorPicker, mPanelWidth);
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
        if (mWhiteboard != null) {
            mColorPicker.setPaintColor(color);
            mWhiteboard.setPaintColor(color);
        }
    }

    @Override
    public void onColorPick() {

    }

    @Override
    public void onEraserPaintSize(int size) {

    }

    @Override
    public void onClearDoodles() {
        if (mWhiteboard != null) {
            mWhiteboard.clearWhiteboard();
        }
    }

    @Override
    public void onGeometryChange(int geometryID) {
        if (mWhiteboard != null) {
            mWhiteboard.setGeometryShapeId(geometryID);
            updateGeometryStyle(geometryID);
        }
    }

    @Override
    public void onPaintSize(float size) {
        if (mWhiteboard != null) {
            mWhiteboard.setPaintStrokeWidth(size);
        }
    }

    @Override
    public void onPaintAlpha(int alpha) {

    }

    @Override
    public void onTextPaintSize(int size) {

    }

    @Override
    public void onTextOrientation(int orientation) {
        if (mWhiteboard != null) {
            switch (orientation) {
                case TextWriting.TEXT_HORIZONTAL:
                    mTextWriting.setImageResource(R.drawable.wb_text_selector);
                    break;

                case TextWriting.TEXT_VERTICAL:
                    mTextWriting.setImageResource(R.drawable.wb_text_vertical_selector);
                    break;
            }
            mWhiteboard.setTextOrientation(orientation);
        }
    }

    private void enterMode(int mode) {
        if (mWhiteboard != null) {
            mWhiteboard.switchMode(mode);
        }
    }

    public void release() {
        if (mWhiteboard != null) {
            mWhiteboard.release();
        }
    }

    public void setWhiteboard(Whiteboard whiteboard) {
        if (mOldWhiteboard != whiteboard) {
            mWhiteboard = whiteboard;
            if (mWhiteboard != null) {
                mWhiteboard.setGeometryShapeId(GeometryShape.RECTANGLE);
                mColorPicker.setPaintColor(mWhiteboard.getPaintColor());
                mWhiteboard.setUndoRedoListener(this);
                reset(whiteboard);
            }
        }

        mOldWhiteboard = whiteboard;
    }

    private void reset(Whiteboard whiteboard) {
        whiteboard.setGeometryShapeId(GeometryShape.RECTANGLE);
        whiteboard.switchMode(Whiteboard.MODE_NONE);

        onGeometryChange(GeometryShape.RECTANGLE);
        onColorChange(WhiteboardConfigs.DEFAULT_PAINT_COLOR);
        onTextOrientation(TextWriting.TEXT_HORIZONTAL);

        mSelection.setSelected(false);
        mHandWriting.setSelected(false);
        mGeoShape.setSelected(false);
        mTextWriting.setSelected(false);
        mEraser.setSelected(false);
    }

    public void exitWhiteboard() {
        if (mWhiteboard != null) {
            mWhiteboard.exit();
        }
    }

    public void setUndoRedoStyle() {
        if (mWhiteboard != null) {
            mUndo.setEnabled(mWhiteboard.isCanUndo());
            mRedo.setEnabled(mWhiteboard.isCanRedo());
        }
    }

    private void undo() {
        if (mWhiteboard != null) {
            mWhiteboard.undo();
        }
    }

    private void redo() {
        if (mWhiteboard != null) {
            mWhiteboard.redo();
        }
    }

    @Override
    public void onUndoRedoStackChanged() {
        setUndoRedoStyle();
    }
}
