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
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.benyuan.xiaojs.R;
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
import com.benyuan.xiaojs.ui.classroom.whiteboard.shape.Triangle;

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
    public static int PRESSED_SCOPE = 20;
    public static int CORNER_EDGE_SIZE = 60;
    public static int TOUCH_SLOPE = 20;
    public static int TEXT_BORDER_PADDING  =20;

    private static final int TEXT_IDLE_STATE = 0;
    private static final int TEXT_INPUT_STATE = 1;
    private static final int TEXT_SELECTED_STATE = 2;

    private static final int CURSOR_MSG = 0;
    private static final int SET_CURSOR_POS = 1;

    private Context mContext;
    private Doodle mDoodle;
    private Doodle mSelector;
    private int mGeometryShapeId = GeometryShape.BEELINE;
    private RectF mDoodleBounds;
    private PointF mPreviousPoint;

    private int mPaintColor = Color.BLACK;
    private int mPaintStrokeWidth = 15;

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
    private int mTextWritingStatus = TEXT_IDLE_STATE;
    private EditText mEditText;

    private Path mDrawingPath;
    private int mSelectionRectRegion;
    private int mSelectedDoodleIndex;
    private boolean mSelectedOnPressed;
    private boolean mTransform;
    private boolean mCanMovable;
    private boolean mIsRecordedParams;
    private boolean mDoodleEditing;


    private ClassRoomGestureDetector mClassRoomGestureDetector;

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
        mClassRoomGestureDetector = gestureDetector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mClassRoomGestureDetector != null) {
            if (ClassRoomActivity.STATE_WHITE_BOARD == mClassRoomGestureDetector.getState()) {
                mClassRoomGestureDetector.onTouchEvent(event);
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

    public void setEditText (final EditText editText) {
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
                    if (mDoodle.getState() == Doodle.STATE_DRAWING) {
                        mDoodle.setState(Doodle.STATE_EDIT);
                    }
                    String doodleTxt = ((TextWriting)mDoodle).getTextString();
                    if (!TextUtils.isEmpty(doodleTxt) && TextUtils.isEmpty(text)) {
                        mAllDoodles.remove(mDoodle);
                        mDoodle = null;
                    } else {
                        ((TextWriting)mDoodle).onTextChanged(text);
                    }

                    WhiteBoard.this.invalidate();
                }
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

        TOUCH_SLOPE = getResources().getDimensionPixelOffset(R.dimen.px20);
        PRESSED_SCOPE = getResources().getDimensionPixelOffset(R.dimen.px20);
        CORNER_EDGE_SIZE = getResources().getDimensionPixelOffset(R.dimen.px60);
        TEXT_BORDER_PADDING = getResources().getDimensionPixelOffset(R.dimen.px12);
        mPreviousPoint = new PointF();
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

        //map matrix
        mDisplayMatrix.setRectToRect(mBlackboardRect, mDoodleBounds, Matrix.ScaleToFit.FILL);

        canvas.save();
        //clip
        canvas.clipRect(mDoodleBounds);
        //draw background
        canvas.drawColor(Color.argb(255, 230, 230, 230));

        canvas.concat(mDisplayMatrix);
        //1. draw doodle
        if (mDoodleBitmap != null) {
            canvas.drawBitmap(mDoodleBitmap, 0, 0, null);
        }

        //2. draw selector
        drawDoodle(canvas, mSelector);
        canvas.restore();

        //3. draw border(selected doodle or selector)
        if (mCurrentMode == MODE_SELECTION) {
            drawDoodleBorder(canvas, mSelector);
        } else {
            canvas.save();
            canvas.concat(mDisplayMatrix);
            drawDoodleBorder(canvas, mDoodle);
            canvas.restore();
        }
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
            mIsRecordedParams = false;
            mSelectionRectRegion = Utils.RECT_NO_SELECTED;
            mTransform = false;
            mCanMovable = false;

            switch (mCurrentMode) {
                case MODE_SELECTION:
                    if (mSelector != null) {
                        mSelectionRectRegion = mSelector.checkRegionPressedArea(mDownPoint.x ,mDownPoint.y);
                        if (mSelectionRectRegion == Utils.RECT_NO_SELECTED) {
                            mSelector.reset();
                        }
                    }
                    break;
                case MODE_TEXT:
                    if (mDoodle instanceof TextWriting && TextUtils.isEmpty(((TextWriting)mDoodle).getTextString())) {
                        mAllDoodles.remove(mDoodle);
                        mDoodle = null;
                    }
                    break;
                case MODE_COLOR_PICKER:
                    break;
                case MODE_HAND_WRITING:
                case MODE_GEOMETRY:
                    if (mDoodle != null) {
                        if (mDoodle.getState() == Doodle.STATE_EDIT) {
                            mSelectionRectRegion = mDoodle.checkRegionPressedArea(mDownPoint.x ,mDownPoint.y);
                            //do nothing
                            //在手指弹起的时候才更新才状态，即在onActionUP函数调用时候
                        }
                    }
                    break;
                case MODE_ERASER:
                    break;
            }
            return false;
        }

        @Override
        public void onActionMove(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            Utils.mapScreenToDoodlePoint(x, y, mDoodleBounds, mLastPoint);
            if (!mCanMovable) {
                mCanMovable = Utils.isMovable(mDownPoint.x, mDownPoint.y, x, y, TOUCH_SLOPE);
            }

            switch (mCurrentMode) {
                case MODE_HAND_WRITING:
                case MODE_GEOMETRY:
                case MODE_TEXT:
                    if (mCanMovable) {
                        if (!mIsRecordedParams) {
                            mIsRecordedParams = true;
                            mPreviousPoint.x = x;
                            mPreviousPoint.y = y;
                            if (mSelectionRectRegion != Utils.RECT_NO_SELECTED) {
                                mTransform = true;
                            }
                        }

                        if (mSelectionRectRegion != Utils.RECT_NO_SELECTED) {
                            switch (mSelectionRectRegion) {
                                case Utils.RIGHT_TOP_CORNER:
                                    //only scale
                                    /*mDoodle.scale(mPreviousPoint.x, mPreviousPoint.y, x, y);
                                    drawAllDoodlesCanvas();
                                    postInvalidate();*/
                                    //only rotate
                                    /*mDoodle.rotate(mPreviousPoint.x, mPreviousPoint.y, x, y);
                                    drawAllDoodlesCanvas();
                                    postInvalidate();*/

                                    //scale and rotate
                                    mDoodle.scaleAndRotate(mPreviousPoint.x, mPreviousPoint.y, x, y);
                                    drawAllDoodlesCanvas();
                                    postInvalidate();
                                    break;
                                case Utils.RIGHT_BOTTOM_CORNER:
                                case Utils.RECT_BODY:
                                case Utils.LEFT_TOP_CORNER:
                                case Utils.LEFT_BOTTOM_CORNER:
                                    //move
                                    mDoodle.move((x - mPreviousPoint.x), (y - mPreviousPoint.y));
                                    drawAllDoodlesCanvas();
                                    postInvalidate();
                                    break;
                                case Utils.TOP_EDGE:
                                case Utils.RIGHT_EDGE:
                                case Utils.BOTTOM_EDGE:
                                case Utils.LEFT_EDGE:
                                    mDoodle.changeAreaByEdge(mPreviousPoint.x, mPreviousPoint.y, x, y, mSelectionRectRegion);
                                    drawAllDoodlesCanvas();
                                    postInvalidate();
                                    break;

                            }

                            mPreviousPoint.x = x;
                            mPreviousPoint.y = y;
                        } else {
                            if (!mDoodleStarted) {
                                mDoodleStarted = true;
                                buildDoodle();
                            }

                            addLastPointIntoDoodle();
                            drawAllDoodlesCanvas();
                        }
                    }
                    break;
                case MODE_SELECTION:
                    if (mCanMovable && mSelector != null) {
                        if (!mIsRecordedParams) {
                            mIsRecordedParams = true;
                            mPreviousPoint.x = x;
                            mPreviousPoint.y = y;
                            if (mSelectionRectRegion != Utils.RECT_NO_SELECTED) {
                                mTransform = true;
                            }
                        }
                        if (mSelectionRectRegion != Utils.RECT_NO_SELECTED) {
                            //transform
                            mSelector.move((x - mPreviousPoint.x), (y - mPreviousPoint.y));
                            drawAllDoodlesCanvas();
                            postInvalidate();

                            mPreviousPoint.x = x;
                            mPreviousPoint.y = y;
                        } else {
                            mSelector.setState(Doodle.STATE_DRAWING);
                            mSelector.addControlPoint(mLastPoint.x, mLastPoint.y);
                        }
                        postInvalidate();
                    }
                    break;
                case MODE_ERASER:

                    break;
                case MODE_COLOR_PICKER:

                    break;
            }

        }

        @Override
        public void onActionUP(MotionEvent event) {
            //update current doodle state
            switch (mCurrentMode) {
                case MODE_SELECTION:
                    if (mSelector != null) {
                        int intersectCount = 0;
                        if (mSelectionRectRegion != Utils.RECT_NO_SELECTED) {
                            if (mTransform) {
                                //mSelector.setState(Doodle.STATE_EDIT);
                            }
                        } else {
                            intersectCount = mCanMovable ? ((Selector)mSelector).checkIntersect() :
                                    ((Selector)mSelector).checkIntersect(event.getX(), event.getY());
                            if (intersectCount <= 0) {
                                mSelector.reset();
                            }
                            postInvalidate();
                        }
                    }
                    break;
                case MODE_GEOMETRY:
                case MODE_HAND_WRITING:
                    if (mDoodle != null) {
                        if (mDoodle.getStyle() != Doodle.STYLE_TEXT) {
                            if (mDoodle.getState() == Doodle.STATE_DRAWING) {
                                if (mDoodle instanceof HandWriting) {
                                    //如果是手写模式，默认绘制图形不被选择
                                    mDoodle.setState(Doodle.STATE_IDLE);
                                    mDoodle = null;
                                    postInvalidate();
                                } else {
                                    mDoodle.setState(Doodle.STATE_EDIT);
                                    postInvalidate();
                                }
                            } else if (mDoodle.getState() == Doodle.STATE_EDIT) {
                                if (mSelectionRectRegion == Utils.RECT_NO_SELECTED) {
                                    mDoodle.setState(Doodle.STATE_IDLE);
                                    mDoodle = null;
                                    postInvalidate();
                                }
                            }

                            //如果当前在选择模式下，但是是没有变换（如拖动，缩放，平移操作），重新检测该图形是否被选中
                            if (!mTransform && mSelectionRectRegion != Utils.RECT_NO_SELECTED) {
                                mSelectedOnPressed = mDoodle.isSelected(mDownPoint.x ,mDownPoint.y);
                                if (!mSelectedOnPressed) {
                                    mDoodle.setState(Doodle.STATE_IDLE);
                                    mDoodle = null;
                                    postInvalidate();
                                }
                            }
                        }
                    } else {
                        if (mAllDoodles != null && !mAllDoodles.isEmpty()) {
                            for (int i = mAllDoodles.size() - 1; i >= 0; i--) {
                                mSelectedDoodleIndex = i;
                                Doodle d = mAllDoodles.get(i);
                                d.setState(Doodle.STATE_IDLE);
                                mSelectedOnPressed = d.isSelected(mDownPoint.x ,mDownPoint.y);
                                if (mSelectedOnPressed) {
                                    mSelectionRectRegion = Utils.RECT_BODY;
                                    mDoodle = d;
                                    mDoodle.setState(Doodle.STATE_EDIT);
                                    postInvalidate();
                                }
                            }
                        }
                    }
                    break;
                case MODE_TEXT:
                    break;
                case MODE_COLOR_PICKER:
                    break;
            }

        }

        @Override
        public void onSingleTapConfirmed(MotionEvent event) {

        }

        @Override
        public void onDoubleTap(MotionEvent event) {
            if (mDoodle instanceof TextWriting && mDoodle.getState() == Doodle.STATE_EDIT) {
                eraserLastDoodle();
                invalidate();

                mEditText.requestFocus();
                showInputMethod(mEditText);
                mEditText.setText(((TextWriting)mDoodle).getTextString());
            }
        }

        @Override
        public void onSingleTapUp(MotionEvent event) {
            //对于文本绘制手指弹起逻辑
            if (mCurrentMode == MODE_TEXT) {
                drawToDoodleCanvas();
                if (mDoodle != null) {
                    if (mDoodle.getState() == Doodle.STATE_EDIT || mDoodle.getState() == Doodle.STATE_DRAWING) {
                        if (mSelectionRectRegion == Utils.RECT_NO_SELECTED) {
                            mDoodle.setState(Doodle.STATE_IDLE);
                            mDoodle = null;
                        }
                    }

                    //如果当前在选择模式下，但是是没有变换（如拖动，缩放，平移操作），重新检测该图形是否被选中
                    if (!mTransform && mSelectionRectRegion != Utils.RECT_NO_SELECTED) {
                        mSelectedOnPressed = mDoodle.isSelected(mDownPoint.x ,mDownPoint.y);
                        if (!mSelectedOnPressed) {
                            mDoodle.setState(Doodle.STATE_IDLE);
                            mDoodle = null;
                            postInvalidate();
                        }
                    }
                } else {
                    buildDoodle();
                    addEditText(event.getX(), event.getY(), false);
                }
            }
        }
    }

    private void drawDoodle(Canvas canvas, Doodle doodle) {
        if (doodle == null) {
            return;
        }

        doodle.setDrawingMatrix(mDrawingMatrix);
        doodle.setDisplayMatrix(mDisplayMatrix);
        switch (mCurrentMode) {
            case MODE_HAND_WRITING:
                doodle.drawSelf(canvas);
                break;
            case MODE_GEOMETRY:
                doodle.drawSelf(canvas);
                break;
            case MODE_SELECTION:
                if(doodle.getState() == Doodle.STATE_DRAWING) {
                    doodle.drawSelf(canvas);
                }
                break;
            case MODE_TEXT:
                doodle.drawSelf(canvas);
                break;
            case MODE_ERASER:

                break;
            case MODE_COLOR_PICKER:

                break;
        }
    }

    private void drawDoodleBorder(Canvas canvas, Doodle doodle) {
        if (doodle == null) {
            return;
        }

        int state = doodle.getState();
        switch (mCurrentMode) {
            case MODE_HAND_WRITING:
                if (state == Doodle.STATE_EDIT) {
                    doodle.drawBorder(canvas);
                }
                break;
            case MODE_GEOMETRY:
            case MODE_TEXT:
            case MODE_ERASER:
                if (state == Doodle.STATE_EDIT || state == Doodle.STATE_DRAWING) {
                    doodle.drawBorder(canvas);
                }
                break;
            case MODE_SELECTION:
                if (state == Doodle.STATE_EDIT) {
                    doodle.drawBorder(canvas);
                }
                break;
        }
    }

    private void buildDoodle() {
        Paint paint = null;
        switch (mCurrentMode) {
            case MODE_SELECTION:
                //mSelector = new Selector(this);
                break;
            case MODE_HAND_WRITING:
                paint = Utils.createPaint(mPaintColor, mPaintStrokeWidth, Paint.Style.STROKE);
                mDoodle = new HandWriting(this, paint, mLastPoint.x, mLastPoint.y);
                break;
            case MODE_GEOMETRY:
                paint = Utils.createPaint(mPaintColor, mPaintStrokeWidth, Paint.Style.STROKE);
                switch (mGeometryShapeId) {
                    case GeometryShape.BEELINE:
                        mDoodle = new Beeline(this, paint);
                        break;
                    case GeometryShape.OVAL:
                        mDoodle = new Oval(this, paint);
                        break;
                    case GeometryShape.RECTANGLE:
                        mDoodle = new Rectangle(this, paint);
                        break;
                    case GeometryShape.TRIANGLE:
                        mDoodle = new Triangle(this, paint);
                        break;
                }

                break;
            case MODE_TEXT:
                paint = Utils.createTextWritingPaint();
                paint.setColor(mPaintColor);
                mDoodle = new TextWriting(this, paint);
                break;
            case MODE_ERASER:

                break;
            case MODE_COLOR_PICKER:

                break;

        }

        if (mDoodle != null && mDoodle.getState() == Doodle.STATE_IDLE) {
            mDoodle.setState(Doodle.STATE_DRAWING);
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
        if (mDoodle == null || mDoodle instanceof Selector) {
            return;
        }

        if (mDoodleCanvas == null) {
            mDoodleBitmap = Bitmap.createBitmap(mBlackboardWidth, mBlackboardHeight, Bitmap.Config.ARGB_8888);
            mDoodleCanvas = new Canvas(mDoodleBitmap);
        }

        drawDoodle(mDoodleCanvas, mDoodle);
        invalidate();
    }

    private void eraserAllDoodle() {
        if (mDoodleBitmap != null) {
            mDoodleBitmap.eraseColor(0);
        }
    }

    private void eraserLastDoodle() {
        if (mDoodleBitmap != null) {
            mDoodleBitmap.eraseColor(0);
        }

        for (int i = 0; i < mAllDoodles.size() - 1; i++) {
            drawDoodle(mDoodleCanvas, mAllDoodles.get(i));
        }
    }

    private void drawAllDoodlesCanvas() {
        if (mDoodleCanvas == null) {
            mDoodleBitmap = Bitmap.createBitmap(mBlackboardWidth, mBlackboardHeight, Bitmap.Config.ARGB_8888);
            mDoodleCanvas = new Canvas(mDoodleBitmap);
        }

        eraserAllDoodle();
        for (Doodle d : mAllDoodles) {
            d.setDrawingMatrix(mDrawingMatrix);
            d.setDisplayMatrix(mDisplayMatrix);
            d.drawSelf(mDoodleCanvas);
        }
    }

    private void addEditText(float x, float y, boolean center) {
        if (!(mDoodle instanceof TextWriting)) {
            Log.i("aaa", "please init the text doodle");
            return;
        }

        mTextWritingStatus = TEXT_INPUT_STATE;
        //TextWriting.clearCursorCount();
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
        //startDrawCursorTask();
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

        if (mode == MODE_SELECTION) {
            if (mSelector == null) {
                mSelector = new Selector(this);
            }
        } else {
            if (mSelector != null) {
                mSelector.reset();
                postInvalidate();
            }
        }

        if (mDoodle != null) {
            drawToDoodleCanvas();
            mDoodle.setState(Doodle.STATE_IDLE);
            mDoodle = null;
            postInvalidate();
        }
    }

    public int getMode() {
        return mCurrentMode;
    }


    public void setGeometryShapeId(int shapeId) {
        mGeometryShapeId = shapeId;
    }

    public void setColor(int color) {
        mPaintColor = color;
        switch (mCurrentMode) {
            case MODE_SELECTION:
                if (mSelector != null) {
                    ((Selector)mSelector).updateDoodleColor(color);
                    drawAllDoodlesCanvas();
                    postInvalidate();
                }
                break;
            default:
                if (mDoodle != null) {
                    mDoodle.getPaint().setColor(color);
                    drawAllDoodlesCanvas();
                    postInvalidate();
                }
                break;
        }
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
        public float paintStrokeWidth;
    }

    public BlackParams getBlackParams() {
        mBlackParams.originalWidth = mBlackboardWidth;
        mBlackParams.originalHeight = mBlackboardHeight;
        mBlackParams.paintScale = mPaintScale;
        mBlackParams.scale = mViewGestureListener.getScale();
        mBlackParams.drawingBounds = mDoodleBounds;
        mBlackParams.paintStrokeWidth = mPaintStrokeWidth;
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

            mDoodle = null;
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

