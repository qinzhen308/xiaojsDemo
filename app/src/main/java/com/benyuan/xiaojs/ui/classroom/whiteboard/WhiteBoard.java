package com.benyuan.xiaojs.ui.classroom.whiteboard;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.benyuan.xiaojs.ui.classroom.ClassRoomActivity;
import com.benyuan.xiaojs.ui.classroom.ClassRoomGestureDetector;
import com.benyuan.xiaojs.ui.classroom.whiteboard.action.Selector;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Doodle;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.GeometryShape;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.SimpleTouchEventListener;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.TextHelper;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.Utils;
import com.benyuan.xiaojs.ui.classroom.whiteboard.core.ViewGestureListener;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.Beeline;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.HandWriting;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.Oval;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.Rectangle;
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.TextWriting;

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
 * Date:2016/11/29
 * Desc:
 *
 * ======================================================================================== */

public class WhiteBoard extends View implements ViewGestureListener.ViewRectChangedListener{
    /**
     * blackboard mode
     * */
    public final static int MODE_SELECTION = 0;
    public final static int MODE_HAND_WRITING = 1;
    public final static int MODE_GEOMETRY = 2;
    public final static int MODE_ERASER = 3;
    public final static int MODE_TEXT = 4;
    //picker color by taking whiteboard pixel
    public final static int MODE_COLOR_PICKER = 5;

    private static final float DOODLE_CANVAS_RATIO = 4 / 3.0F; // w:h = 4:3
    private static int STATUS_BAR_HEIGHT;

    private static final int TEXT_IDLE_STATUS = 0;
    private static final int TEXT_INPUT_STATUS = 1;
    private static final int TEXT_SELECTED_STATUS = 2;

    private static final int CURSOR_MSG = 0;
    private static final int SET_CURSOR_POS = 1;

    private Context mContext;
    private Doodle mDoodle;
    private int mGeometryShapeId = GeometryShape.BEELINE;
    private RectF mDoodleBounds;
    private int mPaintColor = Color.BLACK;

    private BlackParams mBlackParams;

    private int mCurrentMode = MODE_SELECTION;

    private ViewGestureListener mViewGestureListener;
    private InputMethodManager mInputMethodManager;

    private ArrayList<Doodle> mAllDoodles;
    private ArrayList<Doodle> mReDoStack;

    private int mScreenWidth;
    private int mScreenHeight;

    private int mSrcBmpWidth;
    private int mSrcBmpHeight;

    public int mBlackboardWidth;
    public int mBlackboardHeight;

    /**
     * the original size of the view
     */
    private int mViewWidth;
    private int mViewHeight;

    private float mPaintScale = 1.0f;
    private float mPhotoScale = 1.0f;

    private PointF mDownPoint;
    private PointF mLastPoint;

    /**
     * the bitmap of blackboard background
     */
    private Bitmap mSrcBmp;

    private Bitmap mDoodleBitmap;
    private Canvas mDoodleCanvas;

    private boolean mDoodleStarted;

    private Matrix mDrawingMatrix = new Matrix();
    private Matrix mDisplayMatrix = new Matrix();
    private RectF mBlackboardRect = new RectF();

    private Rect mBlackboardVisibleRect = new Rect();
    private int mBlackboardOffsetY;

    /**
     * edit text
     */
    private float mCursorVisibleBottom;
    private int mTextWritingStatus = TEXT_IDLE_STATUS;
    private EditText mEditText;

    private Path mDrawingPath;

    private ClassRoomGestureDetector mClasssRoomGestureDetector;

    public WhiteBoard(Context context) {
        super(context);
        initData(context);
    }

    public WhiteBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    public WhiteBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context);
    }

    @TargetApi(21)
    public WhiteBoard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initData(context);
    }

    public void setGestureDetector(ClassRoomGestureDetector gestureDetector) {
        mClasssRoomGestureDetector = gestureDetector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mClasssRoomGestureDetector != null) {
            if (ClassRoomActivity.STATE_WHITE_BOARD == mClasssRoomGestureDetector.getState()) {
                mClasssRoomGestureDetector.onTouchEvent(event);
                mViewGestureListener.onTouchEvent(event);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);

        int temp = (int)(DOODLE_CANVAS_RATIO * mViewHeight);
        if (temp > mViewWidth) {
            // depend width
            mBlackboardWidth = mViewWidth;
            mBlackboardHeight = (int)(mViewWidth / DOODLE_CANVAS_RATIO);
        } else {
            // depend height
            mBlackboardHeight = mViewHeight;
            mBlackboardWidth = temp;
        }
        Log.i("aaa", "after:w="+mViewWidth+"   h="+mViewHeight);

        mBlackboardRect.set(0, 0, mBlackboardWidth, mBlackboardHeight);
        mDrawingMatrix.setRectToRect(new RectF(0, 0, 1, 1), mBlackboardRect, Matrix.ScaleToFit.FILL);
        mDisplayMatrix.setRectToRect(mBlackboardRect, mDoodleBounds, Matrix.ScaleToFit.FILL);
        mViewGestureListener.onViewChanged(mViewWidth, mViewHeight, mBlackboardWidth, mBlackboardHeight);
    }

    public void setEditText (EditText editText) {
        mEditText = editText;
        mEditText.setVisibility(View.VISIBLE);
        mEditText.setAlpha(0);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (mDoodle instanceof TextWriting) {
                    ((TextWriting)mDoodle).onTextChanged(text);
                }
                WhiteBoard.this.invalidate();
                setCursorPosition(text.length());
                startDrawCursorTask();
            }
        });

    }

    public void setSourceBmp(Bitmap srcBmp) {
        mSrcBmp = srcBmp;
        mSrcBmpWidth = srcBmp.getWidth();
        mSrcBmpHeight = srcBmp.getHeight();

        if (mDoodleCanvas == null) {
            mDoodleBitmap = Bitmap.createBitmap(mSrcBmpWidth, mSrcBmpHeight, Bitmap.Config.ARGB_8888);
            mDoodleCanvas = new Canvas(mDoodleBitmap);
        }
        mPhotoScale = getPhotoScale(mViewWidth, mViewHeight, mSrcBmpWidth, mSrcBmpHeight);
        mPaintScale = 1.0f / mPhotoScale;
        mBlackboardWidth = Math.round(mSrcBmpWidth * mPhotoScale);
        mBlackboardHeight = Math.round(mSrcBmpHeight * mPhotoScale);

        mBlackboardRect.set(0, 0, mSrcBmpWidth, mSrcBmpHeight);
        mDrawingMatrix.setRectToRect(new RectF(0, 0, 1, 1), mBlackboardRect, Matrix.ScaleToFit.FILL);
        mDisplayMatrix.setRectToRect(mBlackboardRect, mDoodleBounds, Matrix.ScaleToFit.FILL);
        mViewGestureListener.onViewChanged(mViewWidth, mViewHeight, mSrcBmpWidth, mSrcBmpHeight);

        postInvalidate();
    }

    public float getPhotoScale(int viewW, int viewH, int photoW, int photoH) {
        float scale = Math.min((float) viewW / (float) photoW, (float) viewH / (float) photoH);
        return Math.min(20, scale);
    }

    private void initData(Context context) {
        mContext = context;
        mDoodleBounds = new RectF();

        mAllDoodles = new ArrayList<Doodle>();
        mReDoStack = new ArrayList<Doodle>();
        mDoodleBounds = new RectF();

        mDownPoint = new PointF();
        mLastPoint = new PointF();
        mDrawingPath = new Path();
        mBlackParams = new BlackParams();

        //init screen size
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        mViewGestureListener = new ViewGestureListener(context, this, new TouchEventListener());
        mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        RectF destF = mViewGestureListener.getDestRect();
        mDoodleBounds.set(destF.left, destF.top, destF.right, destF.bottom);

        canvas.save();
        //clip
        canvas.clipRect(mDoodleBounds);
        //draw background
        canvas.drawColor(Color.argb(255, 230, 230, 230));
        //canvas.drawColor(Color.argb(255, 255, 80, 80));

        //map matrix
        mDisplayMatrix.setRectToRect(mBlackboardRect, mDoodleBounds, Matrix.ScaleToFit.FILL);
        canvas.concat(mDisplayMatrix);
        if (mDoodleBitmap != null) {
            canvas.drawBitmap(mDoodleBitmap, 0, 0, null);
        }

        //draw doodle
        drawDoodle(canvas, mDoodle);

        canvas.restore();
    }

    @Override
    public void onViewRectChanged() {
        postInvalidate();
    }

    private class TouchEventListener extends SimpleTouchEventListener {
        @Override
        public boolean onActionDown(MotionEvent event) {
            mDownPoint.x = event.getX();
            mDownPoint.y = event.getY();

            //reset status
            mDoodleStarted = false;

            switch (mCurrentMode) {
                case MODE_HAND_WRITING:
                    break;
                case MODE_GEOMETRY:

                    break;
                case MODE_TEXT:

                    break;
                case MODE_ERASER:

                    break;
                case MODE_COLOR_PICKER:

                    break;
            }
            return false;
        }

        @Override
        public void onActionMove(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            Utils.mapScreenToDoodlePoint(x, y, mDoodleBounds, mLastPoint);
            boolean movable = Utils.isMovable(mDownPoint.x, mDownPoint.y, x, y);

            switch (mCurrentMode) {
                case MODE_HAND_WRITING:
                    if (movable) {
                        if (!mDoodleStarted) {
                            mDoodleStarted = true;
                            buildDoodle();
                        }
                        addLastPointIntoDoodle();
                    }
                    break;
                case MODE_SELECTION:
                case MODE_GEOMETRY:
                    if (movable) {
                        if (!mDoodleStarted) {
                            mDoodleStarted = true;
                            buildDoodle();
                        }
                        addLastPointIntoDoodle();
                    }
                    break;
                case MODE_TEXT:

                    break;
                case MODE_ERASER:

                    break;
                case MODE_COLOR_PICKER:

                    break;
            }

        }

        @Override
        public void onActionUP(MotionEvent event) {
            if (mDoodleStarted) {
                drawToDoodleCanvas();
            }
        }

        @Override
        public void onSingleTapConfirmed(MotionEvent event) {

        }

        @Override
        public void onSingleTapUp(MotionEvent event) {
            if (mTextWritingStatus == TEXT_IDLE_STATUS) {
                buildDoodle();
                addEditText(event.getX(), event.getY(), false);
            } else if (mTextWritingStatus == TEXT_INPUT_STATUS) {
                hideInputMethod();
                mTextWritingStatus = TEXT_IDLE_STATUS;
                drawToDoodleCanvas();
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        getWindowVisibleDisplayFrame(mBlackboardVisibleRect);
        Log.i("aaa", "====w="+mBlackboardVisibleRect.width()+"   h="+mBlackboardVisibleRect.height());
    }

    private void drawDoodle(Canvas canvas, Doodle doodle) {
        if (doodle == null) {
            return;
        }

        Paint p = doodle.getPaint();
        if (p != null) {
            p.setColor(mPaintColor);
        }
        doodle.setDrawingMatrix(mDrawingMatrix);
        switch (mCurrentMode) {
            case MODE_HAND_WRITING:
                doodle.drawSelf(canvas);
                break;
            case MODE_GEOMETRY:
                doodle.drawSelf(canvas);
                break;
            case MODE_SELECTION:
                doodle.drawSelf(canvas);
                break;
            case MODE_TEXT:
                doodle.drawSelf(canvas);
                if (mTextWritingStatus == TEXT_INPUT_STATUS) {
                    ((TextWriting)doodle).drawCursor(canvas, mViewGestureListener.getInverseScale() * mPaintScale);
                }
                break;
            case MODE_ERASER:

                break;
            case MODE_COLOR_PICKER:

                break;
        }
    }

    private void buildDoodle() {
        switch (mCurrentMode) {
            case MODE_SELECTION:
                mDoodle = new Selector(this);
                break;
            case MODE_HAND_WRITING:
                mDoodle = new HandWriting(this, Utils.createHandWritingPaint(), mLastPoint.x, mLastPoint.y);
                break;
            case MODE_GEOMETRY:
                switch (mGeometryShapeId) {
                    case GeometryShape.BEELINE:
                        mDoodle = new Beeline(this, Utils.createHandWritingPaint());
                        break;
                    case GeometryShape.OVAL:
                        mDoodle = new Oval(this, Utils.createHandWritingPaint());
                        break;
                    case GeometryShape.RECTANGLE:
                        mDoodle = new Rectangle(this, Utils.createHandWritingPaint());
                        break;
                    case GeometryShape.TRIANGLE:
                        break;
                }

                break;
            case MODE_TEXT:
                if (mTextWritingStatus == TEXT_IDLE_STATUS) {
                    mDoodle = new TextWriting(this, Utils.createTextWritingPaint());
                }
                break;
            case MODE_ERASER:

                break;
            case MODE_COLOR_PICKER:

                break;

        }

        if (mDoodle != null) {
            mAllDoodles.add(mDoodle);
        }
    }

    private void addLastPointIntoDoodle() {
        if (mDoodle != null) {
            mDoodle.addControlPoint(mLastPoint.x, mLastPoint.y);
            postInvalidate();
        }
    }

    private void drawToDoodleCanvas() {
        if (mDoodle == null || mDoodle.getStyle() == Doodle.SELECTION) {
            return;
        }

        if (mDoodleCanvas == null) {
            mDoodleBitmap = Bitmap.createBitmap(mBlackboardWidth, mBlackboardHeight, Bitmap.Config.ARGB_8888);
            mDoodleCanvas = new Canvas(mDoodleBitmap);
        }

        drawDoodle(mDoodleCanvas, mDoodle);
        postInvalidate();
    }

    private void addEditText(float x, float y, boolean center) {
        if (!(mDoodle instanceof TextWriting)) {
            Log.i("aaa", "please init the text doodle");
            return;
        }

        mTextWritingStatus = TEXT_INPUT_STATUS;
        TextWriting.clearCursorCount();
        //isAddDoodle = true;
        mEditText.requestFocus();
        showInputMethod(mEditText);

        float padding = TextWriting.TEXT_BORDER_PADDING;

        float defaultW = TextWriting.MIN_EDIT_TEXT_WIDTH;
        float defaultH = TextHelper.getDefaultTextHeight(mDoodle);

        x = center ? x - defaultW / 2.0f : x;
        y = y - defaultH / 2.0f;

        if (x + defaultW + padding > mDoodleBounds.right) {
            x = mDoodleBounds.right - defaultW - padding;
        }

        if (x < mDoodleBounds.left + padding) {
            x = mDoodleBounds.left + padding;
        }

        if (y + defaultH + padding > mDoodleBounds.bottom) {
            y = mDoodleBounds.bottom - defaultH - padding;
        }

        if (y - padding < mDoodleBounds.top) {
            y = mDoodleBounds.top + padding;
        }

        //the first point
        Utils.mapScreenToDoodlePoint(x, y, mDoodleBounds, mLastPoint);
        addLastPointIntoDoodle();

        //the second point
        x += defaultW;
        y += defaultH;
        Utils.mapScreenToDoodlePoint(x, y, mDoodleBounds, mLastPoint);
        addLastPointIntoDoodle();

        mEditText.setText("");

        invalidate();

        mCursorVisibleBottom = y + padding;
        startDrawCursorTask();
    }

    private void startDrawCursorTask() {
        TextWriting.clearCursorCount();
        mHandler.removeMessages(CURSOR_MSG);
        mHandler.sendEmptyMessageDelayed(CURSOR_MSG, 450);
    }

    private void setCursorPosition(int index) {
        //set selection
        Message msg = Message.obtain();
        msg.what = SET_CURSOR_POS;
        msg.arg1 = index;
        mHandler.sendMessage(msg);
    }

    private void drawCursorTask () {
        mHandler.sendEmptyMessageDelayed(CURSOR_MSG, 450);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case CURSOR_MSG:
                    TextWriting.increaseCursorCount();
                    invalidate();
                    drawCursorTask();
                    break;

                case SET_CURSOR_POS:
                    if (msg.arg1 > -1 && msg.arg1 <= mEditText.getEditableText().length()) {
                        mEditText.setSelection(msg.arg1);
                    }
                    break;

                default:
                    break;
            }

        }
    };

    private void stopDrawCursorTask () {
        mHandler.removeCallbacksAndMessages(null);
    }

    public void switchMode(int mode) {
        if (mCurrentMode == MODE_TEXT && mode != MODE_TEXT) {
            stopDrawCursorTask();
        }
        mCurrentMode = mode;
    }

    public int getMode() {
        return mCurrentMode;
    }


    public void setGeometryShapeId(int shapeId) {
        mGeometryShapeId = shapeId;
    }

    public void setColor(int color) {
        mPaintColor = color;
    }

    public int getColor() {
        return mPaintColor;
    }

    public void showInputMethod(EditText editText) {
        mInputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    public void hideInputMethod() {
        //mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //force hide
        mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    public static class BlackParams {
        public RectF drawingBounds;
        public int originalWidth;
        public int originalHeight;
        public float paintScale = 1.0f;
        public float scale = 1.0f;
    }

    public BlackParams getBlackParams() {
        mBlackParams.originalWidth = mBlackboardWidth;
        mBlackParams.originalHeight = mBlackboardHeight;
        mBlackParams.paintScale = mPaintScale;
        mBlackParams.scale = mPhotoScale;
        mBlackParams.drawingBounds = mDoodleBounds;
        return mBlackParams;
    }

    public ArrayList<Doodle> getAllDoodles() {
        return mAllDoodles;
    }

    public void clearWhiteboard() {
        if (mAllDoodles != null) {
            mAllDoodles.clear();

            if (mDoodleBitmap != null) {
                mDoodleBitmap.eraseColor(0);
            }

            postInvalidate();
        }
    }

    public void release() {
        if (mHandler != null) {
            mHandler.removeMessages(CURSOR_MSG);
            mHandler.removeMessages(SET_CURSOR_POS);
            mHandler = null;
        }
    }

    public void transformation(MotionEvent event) {
        if (mViewGestureListener != null) {
            mViewGestureListener.onTouchEvent(event);
        }
    }

}

