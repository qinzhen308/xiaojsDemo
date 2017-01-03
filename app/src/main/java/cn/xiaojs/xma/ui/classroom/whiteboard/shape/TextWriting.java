package cn.xiaojs.xma.ui.classroom.whiteboard.shape;
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
import android.graphics.RectF;
import android.text.TextUtils;

import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.ActionRecord;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.WhiteboardConfigs;

import java.util.ArrayList;

public class TextWriting extends Doodle {
    public final static int TEXT_HORIZONTAL = 1;
    public final static int TEXT_VERTICAL = 2;

    private String mTextString;
    private PointF mTextSize;
    private PointF mTextBasePoint;

    private float mSingleLineTextHeight;
    private float mSingleLineTextWidth;
    private ArrayList<String> mMultiLineText;

    /**
     * 文字排列方式：横排和竖排 默认是横排
     * */
    private int mTextOrientation = TEXT_HORIZONTAL;

    private TextWriting(Whiteboard whiteboard, Paint paint) {
        this(whiteboard, paint, TEXT_HORIZONTAL);
    }

    public TextWriting(Whiteboard whiteboard, Paint paint, int textOrientation) {
        super(whiteboard, Doodle.STYLE_TEXT);
        setPaint(paint);

        init(textOrientation);
    }

    public TextWriting(Whiteboard whiteboard, Paint paint, int textOrientation, String doodleID) {
        super(whiteboard, Doodle.STYLE_TEXT);
        setDoodleId(doodleID);
        setPaint(paint);

        init(textOrientation);
    }

    private void init(int textOrientation) {
        mMultiLineText = new ArrayList<String>();
        mTextSize = new PointF();
        mTextBasePoint = new PointF();
        mTextOrientation = checkTextOrientation(textOrientation) ? textOrientation : TEXT_HORIZONTAL;
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
    public void onDrawSelf(Canvas canvas) {
        String textString = getTextString();
        if (TextUtils.isEmpty(textString)) {
            return;
        }

        canvas.save();

        float baseX = mTextBasePoint.x;
        float baseY = mTextBasePoint.y;

        if (mTextOrientation == TEXT_HORIZONTAL) {
            float singleH = mSingleLineTextHeight;

            canvas.concat(mTransformMatrix);
            mDrawingMatrix.postConcat(mTransformMatrix);
            String singleLineText;
            for (int i = 0; i < mMultiLineText.size(); i++) {
                singleLineText = mMultiLineText.get(i);
                canvas.drawText(singleLineText, baseX, baseY + singleH * i, getPaint());
            }
        } else {
            float singleW = mSingleLineTextWidth;
            canvas.concat(mTransformMatrix);
            mDrawingMatrix.postConcat(mTransformMatrix);
            String singleLineText;
            float offsetX = 0;
            float offsetY = 0;
            for (int i = 0; i < mMultiLineText.size(); i++) {
                singleLineText = mMultiLineText.get(i);
                offsetX = baseX - singleW * i;
                if (!TextUtils.isEmpty(singleLineText)) {
                    for (int j = 0; j < singleLineText.length(); j++) {
                        offsetY = baseY + mSingleLineTextHeight * j;
                        String s = String.valueOf(singleLineText.charAt(j));
                        canvas.drawText(s, offsetX, offsetY, getPaint());
                    }
                }
            }
        }

        canvas.restore();
    }

    @Override
    public void onDrawBorder(Canvas canvas) {
        if(!TextUtils.isEmpty(mTextString)) {
            super.onDrawBorder(canvas);
        }
    }

    @Override
    protected void computeBorderPadding() {
        Whiteboard.WhiteboardParams params = mWhiteboard.getParams();
        float dashW = WhiteboardConfigs.BORDER_DASH_WIDTH / params.scale;

        mBorderPaint.setStrokeWidth(WhiteboardConfigs.BORDER_STROKE_WIDTH / params.scale);
        mBorderPaint.setPathEffect(new DashPathEffect(new float[]{dashW, dashW}, 0));

        float paintStrokeWidth = mPaint != null ? mPaint.getStrokeWidth() : 0;
        float padding = (paintStrokeWidth + mBorderPaint.getStrokeWidth()) / 2 ;
        PointF p = Utils.normalizeScreenPoint(padding, padding, params.drawingBounds);
        float hPadding = p.x / mTotalScale * params.scale;
        float vPadding = p.y / mTotalScale * params.scale;
        //add extra padding
        p = Utils.normalizeScreenPoint(WhiteboardConfigs.TEXT_BORDER_PADDING, WhiteboardConfigs.TEXT_BORDER_PADDING, params.drawingBounds);
        hPadding = hPadding + p.x / mTotalScale;
        vPadding = vPadding + p.y / mTotalScale;

        mBorderPadding.set(hPadding, vPadding);
    }

    @Override
    public Path getScreenPath() {
        mScreenPath.reset();
        mTransRect.set(mDoodleRect);
        mDrawingMatrix.mapRect(mTransRect);
        mDisplayMatrix.mapRect(mTransRect);
        mScreenPath.addOval(mTransRect, Path.Direction.CCW);
        return mScreenPath;
    }

    @Override
    public RectF getDoodleScreenRect() {
        mScreenPath.reset();
        mTransRect.set(mDoodleRect);
        mDrawingMatrix.mapRect(mTransRect);
        mDisplayMatrix.mapRect(mTransRect);
        return mTransRect;
    }

    @Override
    public void changeAreaByEdge(float oldX, float oldY, float x, float y, int edge) {
        //do nothing
    }

    @Override
    public int onCheckPressedRegion(float x, float y) {
        if (getState() == STATE_EDIT) {
            PointF p = Utils.transformPoint(x, y, mRectCenter, mTotalDegree);
            Matrix matrix = Utils.transformMatrix(mDrawingMatrix, mDisplayMatrix, mRectCenter, mTotalDegree);
            mTransRect.set(mDoodleRect);
            int corner = IntersectionHelper.whichCornerPressed(p.x, p.y, mTransRect, matrix);
            if (corner != IntersectionHelper.RECT_NO_SELECTED) {
                return corner;
            } else {
                return IntersectionHelper.checkRectPressed(p.x, p.y, mTransRect, matrix);
            }
        }

        return IntersectionHelper.RECT_NO_SELECTED;
    }

    @Override
    public boolean onCheckSelected(float x, float y) {
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
        Whiteboard.WhiteboardParams params = getWhiteboard().getParams();

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

        if (mTextOrientation == TEXT_HORIZONTAL) {
            PointF p = Utils.normalizeScreenPoint(etW, etH, params.drawingBounds);
            float x = mPoints.get(0).x;
            float y = mPoints.get(0).y;
            x = x + p.x;
            y = y + p.y;
            mPoints.get(1).set(x, y);
            mDoodleRect.set(mPoints.get(0).x, mPoints.get(0).y, mPoints.get(1).x, mPoints.get(1).y);
        } else {
            PointF p = Utils.normalizeScreenPoint(etW, etH, params.drawingBounds);
            float x1 = mPoints.get(0).x;
            float y1 = mPoints.get(0).y;
            float x2 = x1 - p.x;
            float y2 = y1 + p.y;

            p = Utils.normalizeScreenPoint(mSingleLineTextWidth, mSingleLineTextHeight, params.drawingBounds);
            mPoints.get(1).set(x2, y2);
            mDoodleRect.set(x2, y1, x1, y2);
            mDoodleRect.offset(p.x, 0);
        }
    }

    private PointF measureTextSize(TextWriting doodle) {
        Paint paint = doodle.getPaint();
        String text = doodle.getTextString();
        Whiteboard.WhiteboardParams params = doodle.getWhiteboard().getParams();
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float textWidth = 0;
        float textHeight = fontMetrics.descent - fontMetrics.ascent;


        ArrayList<String> multiLineText = getMultiLineText(text);
        int line = Math.max(0, multiLineText.size());

        String singleLineText;
        if (mTextOrientation == TEXT_HORIZONTAL) {
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
        } else {
            float h = 0;
            float singleLineHeight = 0;
            for (int i = 0; i < line; i++) {
                singleLineText = multiLineText.get(i);
                if (!TextUtils.isEmpty(singleLineText)) {
                    h = textHeight * singleLineText.length();
                    if (h > singleLineHeight) {
                        singleLineHeight = h;
                    }
                }
            }

            mSingleLineTextWidth = textHeight;
            mSingleLineTextHeight = textHeight;
            PointF p = getFirstPoint();
            mTextBasePoint.set(p.x * params.originalWidth, p.y * params.originalHeight - fontMetrics.ascent);

            float txtW = mSingleLineTextWidth * line * params.scale;
            float txtH = singleLineHeight * params.scale;
            mTextSize.set(txtW, txtH);
        }

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

    private boolean checkTextOrientation(int textOrientation) {
        int oriArr[] = new int[2];
        oriArr[0] = TEXT_HORIZONTAL;
        oriArr[1] = TEXT_VERTICAL;

        for (int i = 0; i < oriArr.length; i++) {
            if (oriArr[i] == textOrientation) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void addRecords(int action, int groupId) {
        super.addRecords(action, groupId);
        if (mUndoRecords != null && !mUndoRecords.isEmpty()) {
            ActionRecord record = mUndoRecords.get(mUndoRecords.size() - 1);
            record.textStr = getTextString();
        }

    }
}
