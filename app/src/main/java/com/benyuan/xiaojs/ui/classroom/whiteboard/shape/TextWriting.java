package com.benyuan.xiaojs.ui.classroom.whiteboard.shape;
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
 * Date:2016/10/18
 * Desc:
 *
 * ======================================================================================== */

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.TextUtils;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Doodle;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.IntersectionHelper;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Utils;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.WhiteboardConfigs;

import java.util.ArrayList;

public class TextWriting extends Doodle {
    private String mTextString;
    private PointF mTextSize;
    private PointF mTextBasePoint;

    private float mSingleLineTextHeight;

    ArrayList<String> mMultiLineText;

    public TextWriting(WhiteBoard whiteBoard, Paint paint) {
        super(whiteBoard, Doodle.STYLE_TEXT);
        setPaint(paint);

        init();
    }

    private void init() {
        mMultiLineText = new ArrayList<String>();
        mTextSize = new PointF();
        mTextBasePoint = new PointF();
    }

    @Override
    protected int initialCapacity() {
        return 2;
    }

    public void setTextString(String str) {
        mTextString = str;
    }

    public String getTextString() {
        return mTextString;
    }

    @Override
    public void drawSelf(Canvas canvas) {
        String textString = getTextString();
        if (TextUtils.isEmpty(textString)) {
            return;
        }

        canvas.save();

        float baseX = mTextBasePoint.x;
        float baseY = mTextBasePoint.y;
        float singleH = mSingleLineTextHeight;

        canvas.concat(mTransformMatrix);
        mDrawingMatrix.postConcat(mTransformMatrix);
        String SingleLineText;
        for (int i = 0; i < mMultiLineText.size(); i++) {
            SingleLineText = mMultiLineText.get(i);
            canvas.drawText(SingleLineText, baseX, baseY + singleH * i, getPaint());
        }
        canvas.restore();
    }

    @Override
    public void drawBorder(Canvas canvas) {
        if(!TextUtils.isEmpty(mTextString)) {
            WhiteBoard.WhiteboardParams params = mWhiteboard.getParams();
            float dashW = WhiteboardConfigs.BORDER_DASH_WIDTH / params.scale;

            mBorderPaint.setStrokeWidth(WhiteboardConfigs.BORDER_STROKE_WIDTH / params.scale);
            mBorderPaint.setPathEffect(new DashPathEffect(new float[]{dashW, dashW}, 0));

            float paintStrokeWidth = mPaint != null ? mPaint.getStrokeWidth() : 0;
            float padding = (paintStrokeWidth + mBorderPaint.getStrokeWidth()) / 2 + WhiteboardConfigs.TEXT_BORDER_PADDING;
            PointF p = Utils.normalizeScreenPoint(padding, padding, params.drawingBounds);
            float hPadding = p.x / mTotalScale;
            float vPadding = p.y / mTotalScale;
            mBorderRect.set(mDoodleRect.left - hPadding, mDoodleRect.top - vPadding, mDoodleRect.right + hPadding, mDoodleRect.bottom + vPadding);

            mBorderDrawingPath.reset();
            mBorderDrawingPath.addRect(mBorderRect, Path.Direction.CCW);
            mBorderDrawingPath.transform(mDrawingMatrix);
            canvas.drawPath(mBorderDrawingPath, mBorderPaint);

            //draw controller
            float radius = mControllerPaint.getStrokeWidth() / mTotalScale;
            p = Utils.normalizeScreenPoint(radius, radius, params.drawingBounds);
            mBorderRect.set(mDoodleRect.right - p.x, mDoodleRect.top - p.y, mDoodleRect.right + p.x, mDoodleRect.top + p.y);
            mBorderDrawingPath.reset();
            mBorderDrawingPath.addOval(mBorderRect, Path.Direction.CCW);
            mBorderDrawingPath.transform(mDrawingMatrix);
            canvas.drawPath(mBorderDrawingPath, mControllerPaint);
        }
    }

    @Override
    public Path getOriginalPath() {
        return null;
    }

    @Override
    public void changeAreaByEdge(float oldX, float oldY, float x, float y, int edge) {
        //do nothing
    }

    @Override
    public int checkRegionPressedArea(float x, float y) {
        if (getState() == STATE_EDIT) {
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            mTransRect.set(mDoodleRect);
            int corner = IntersectionHelper.isPressedCorner(p.x, p.y, mTransRect, matrix);
            if (corner != IntersectionHelper.RECT_NO_SELECTED) {
                return corner;
            } else {
                return IntersectionHelper.checkRectPressed(p.x, p.y, mTransRect, matrix);
            }
        }

        return IntersectionHelper.RECT_NO_SELECTED;
    }

    @Override
    public boolean isSelected(float x, float y) {
        if (mPoints.size() > 1) {
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            mTransRect.set(mDoodleRect);
            return IntersectionHelper.checkRectPressed(p.x, p.y, mTransRect, matrix) != IntersectionHelper.RECT_NO_SELECTED;
        }

        return false;
    }

    /**
     * Update TextView size when text changes
     */
    public void onTextChanged(String changedText) {
        if (mPoints.isEmpty()) {
            return;
        }

        setTextString(changedText);
        WhiteBoard.WhiteboardParams params = getWhiteboard().getParams();

        float etW = 0;
        float etH = 0;

        if (TextUtils.isEmpty(changedText)) {
            etW = WhiteboardConfigs.MIN_EDIT_TEXT_WIDTH;
            etH = Utils.getDefaultTextHeight(this);
        } else {
            PointF p = measureTextSize(this);
            etW = p.x;
            etH = p.y;
        }

        PointF p = Utils.normalizeScreenPoint(etW, etH, params.drawingBounds);
        float x = mPoints.get(0).x + p.x;
        float y = mPoints.get(0).y + p.y;


        mPoints.get(1).set(x, y);
        mDoodleRect.set(mPoints.get(0).x, mPoints.get(0).y, mPoints.get(1).x, mPoints.get(1).y);
    }

    private PointF measureTextSize(TextWriting doodle) {
        Paint paint = doodle.getPaint();
        String text = doodle.getTextString();
        WhiteBoard.WhiteboardParams params = doodle.getWhiteboard().getParams();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textWidth = 0;
        float textHeight = fontMetrics.descent - fontMetrics.ascent;


        ArrayList<String> multiLineText = getMultiLineText(text);
        int line = Math.max(1, multiLineText.size());

        String singleLineText;
        float w = 0;
        for (int i = 0; i < line; i++) {
            singleLineText = multiLineText.get(i);
            if (!TextUtils.isEmpty(singleLineText)) {
                w = paint.measureText(singleLineText);
                if (w > textWidth) {
                    textWidth = w;
                }
            }
        }

        mSingleLineTextHeight = textHeight;
        PointF p = getFirstPoint();
        mTextBasePoint.set(p.x * params.originalWidth, p.y * params.originalHeight - fontMetrics.ascent);

        float txtW = textWidth * params.scale;
        float txtH = textHeight * line * params.scale;
        mTextSize.set(txtW, txtH);

        return mTextSize;
    }

    private ArrayList<String> getMultiLineText(String text) {
        mMultiLineText.clear();
        StringBuffer sb = new StringBuffer();
        int size = text.length();
        for (int i = 0; i < size; i++) {
            char c = text.charAt(i);
            if (c != 13 && c != 10) {
                sb.append(c);
            } else {
                mMultiLineText.add(sb.toString());
                sb.delete(0, sb.toString().length());
            }

            if (i == size - 1) {
                mMultiLineText.add(sb.toString());
                sb.delete(0, sb.toString().length());
            }
        }

        return mMultiLineText;
    }

}
