package com.benyuan.xiaojs.ui.classroom.whiteboard.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.text.TextUtils;

import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Doodle;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.DrawingHelper;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.TextHelper;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Utils;

import java.util.ArrayList;

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

public class TextWriting extends Doodle {
    public static float TEXT_BORDER_PADDING = 20;
    public static int MIN_EDIT_TEXT_WIDTH = 80;
    public static int MIN_EDIT_TEXT_HEIGHT = 60;

    public final static float MIN_SCALE = 1 / 2.0f;
    public final static float MAX_SCALE = 4.0f;

    private static int CURSOR_COLOR = Color.argb(255, 49, 164, 229);
    private static int TEXT_CURSOR_WIDTH = 3;

    private String mTextString;

    public static long mStartTime;
    private static int mCursorCount;
    private static Paint mCursorPaint = new Paint(Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);

    private PointF mTextRotateCenter;
    ArrayList<String> mMultiLineText;

    private static PointF mCursorStartPosition = new PointF();
    private static PointF mCursorEndPosition = new PointF();

    public TextWriting(WhiteBoard whiteBoard, Paint paint) {
        super(whiteBoard, Doodle.STYLE_TEXT);
        setPaint(paint);
        mTextRotateCenter = new PointF();
        mMultiLineText = new ArrayList<String>();
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

        PointF centerP = mTextRotateCenter;
        canvas.rotate(mTotalDegree, centerP.x, centerP.y);
        PointF p = getFirstPoint();
        float x = p.x;
        float y = p.y;

        Paint paint = getPaint();
        Paint.FontMetrics fm = paint.getFontMetrics();
        //the val of fm.ascent is less than 0
        float singleH = fm.descent - fm.ascent;
        float baseX = x * mWhiteboard.getBlackParams().originalWidth;
        float baseY = y * mWhiteboard.getBlackParams().originalHeight - fm.ascent;

        mMultiLineText = TextHelper.getMultiLineText(textString);
        String SingleLineText;
        for (int i = 0; i < mMultiLineText.size(); i++) {
            SingleLineText = mMultiLineText.get(i);
            canvas.drawText(SingleLineText, baseX, baseY + singleH * i, paint);
        }

        int line = mMultiLineText.size();
        float LastLineStartX = x * mWhiteboard.getBlackParams().originalWidth;
        float LastLineStartY = y * mWhiteboard.getBlackParams().originalHeight;

        float textWidth = 0;
        if (line > 0) {
            LastLineStartY = LastLineStartY + singleH * (line - 1);
            textWidth = paint.measureText(mMultiLineText.get(line - 1));
        }

        mCursorStartPosition.set(LastLineStartX + textWidth, LastLineStartY);
        mCursorEndPosition.set(LastLineStartX + textWidth, LastLineStartY + singleH);

        canvas.restore();
    }

    @Override
    public Path getOriginalPath() {
        return null;
    }

    /**
     * Update TextView size when text changes
     */
    public void onTextChanged(String changedText) {
        setTextString(changedText);

        float etW = 0;
        float etH = 0;

        if (TextUtils.isEmpty(changedText)) {
            etW = MIN_EDIT_TEXT_WIDTH;
            etH = TextHelper.getDefaultTextHeight(this);
        } else {
            PointF p = TextHelper.measureTextSize(this);
            etW = p.x;
            etH = p.y;
        }

        PointF p = Utils.normalizeScreenPoint(etW, etH, getWhiteboard().getBlackParams().drawingBounds);
        float x = mPoints.get(0).x + p.x;
        float y = mPoints.get(0).y + p.y;

        mPoints.get(1).set(x, y);
    }

    /**
     * Draw EditText cursor
     */
    public void drawCursor(Canvas canvas, float invertScale) {
        canvas.save();

        PointF centerP = mTextRotateCenter;
        canvas.rotate(mTotalDegree, centerP.x, centerP.y);
        mCursorPaint.setStrokeWidth(TEXT_CURSOR_WIDTH * invertScale);
        if (mCursorCount % 2 == 0) {
            mCursorPaint.setColor(CURSOR_COLOR);
        } else {
            mCursorPaint.setColor(Color.TRANSPARENT);
        }
        canvas.drawLine(mCursorStartPosition.x, mCursorStartPosition.y, mCursorEndPosition.x,
                mCursorEndPosition.y, mCursorPaint);

        canvas.restore();
    }

    public static void clearCursorCount() {
        mCursorCount = 0;
        mStartTime = System.currentTimeMillis();
    }

    public static void increaseCursorCount() {
        mCursorCount++;
    }

    @Override
    public void changeAreaByEdge(float oldX, float oldY, float x, float y, int edge) {
        //do nothing
    }

    @Override
    public int checkRegionPressedArea(float x, float y) {
        return super.checkRegionPressedArea(x, y);
    }

    @Override
    public boolean isSelected(float x, float y) {
        if (mPoints.size() > 1) {
            PointF dp = mPoints.get(0);
            PointF up = mPoints.get(1);
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            int flag = Utils.checkRectPressed(p.x, p.y, dp, up, matrix);
            return flag != Utils.RECT_NO_SELECTED ? true : false;
        }

        return false;
    }

}
