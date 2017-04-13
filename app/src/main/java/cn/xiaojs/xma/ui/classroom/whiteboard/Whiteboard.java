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
 * Date:2016/11/29
 * Desc:
 *
 * ======================================================================================== */

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
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.xiaojs.xma.ui.classroom.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.InteractiveLevel;
import cn.xiaojs.xma.ui.classroom.bean.Commend;
import cn.xiaojs.xma.ui.classroom.bean.CommendLine;
import cn.xiaojs.xma.ui.classroom.socketio.Packer;
import cn.xiaojs.xma.ui.classroom.socketio.Parser;
import cn.xiaojs.xma.ui.classroom.socketio.ProtocolConfigs;
import cn.xiaojs.xma.ui.classroom.socketio.Receiver;
import cn.xiaojs.xma.ui.classroom.socketio.Sender;
import cn.xiaojs.xma.ui.classroom.whiteboard.action.Selector;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Action;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.ActionRecord;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.BitmapPool;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.IntersectionHelper;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.OnColorChangeListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.SimpleTouchEventListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.UndoRedoListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Utils;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.ViewGestureListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.WhiteboardConfigs;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Beeline;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.HandWriting;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Oval;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Rectangle;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.TextWriting;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Triangle;

public class Whiteboard extends View implements ViewGestureListener.ViewRectChangedListener,
        Receiver<List<CommendLine>>, Sender<String> {
    /**
     * blackboard mode
     */
    public final static int MODE_NONE = -1;
    public final static int MODE_SELECTION = 0;
    public final static int MODE_HAND_WRITING = 1;
    public final static int MODE_GEOMETRY = 2;
    public final static int MODE_ERASER = 3;
    public final static int MODE_TEXT = 4;
    //picker color by taking whiteboard pixel
    public final static int MODE_COLOR_PICKER = 5;

    private final float DOODLE_CANVAS_RATIO = WhiteboardLayer.DOODLE_CANVAS_RATIO; // w:h = 4:3

    private final int BG_COLOR = Color.argb(255, 255, 255, 255);

    private Context mContext;
    private Doodle mDoodle;
    private Selector mSelector;
    private int mGeometryShapeId = GeometryShape.BEELINE;
    private RectF mDoodleBounds;
    private PointF mPreviousPoint;

    private int mPaintColor = WhiteboardConfigs.DEFAULT_PAINT_COLOR;
    private float mPaintStrokeWidth = WhiteboardConfigs.DEFAULT_PAINT_STROKE_WIDTH;

    private WhiteboardParams mWhiteboardParams;

    private int mCurrentMode = MODE_NONE;

    private ViewGestureListener mViewGestureListener;
    private InputMethodManager mInputMethodManager;

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
     * the bitmap of whiteboard background
     */
    private Bitmap mCourseBmp;
    private Uri mCourseUri;
    private Rect mSrcCourseRect;
    private Rect mDesCourseRect;
    private WhiteboardLayer mLayer;

    private ArrayList<Doodle> mAllDoodles;
    private ArrayList<Doodle> mReDoStack;

    private Bitmap mDoodleBitmap;
    private Canvas mDoodleCanvas;

    private boolean mDoodleAdded;

    private Matrix mDrawingMatrix = new Matrix();
    private Matrix mDisplayMatrix = new Matrix();
    private RectF mBlackboardRect = new RectF();

    /**
     * edit text
     */
    private SpecialEditText mEditText;
    private int mTextOrientation = TextWriting.TEXT_HORIZONTAL;

    private Path mDrawingPath;
    private int mSelectionRectRegion;
    private boolean onViewChanged;
    private boolean mSelectedOnPressed;
    private boolean mTransform;
    private boolean mCanMovable;
    private boolean mIsRecordedParams;
    private boolean mMeasureFinished = false;
    private boolean mInitLayer = false;
    private boolean mNeedBitmapPool = true;

    private GestureDetector mClassroomGestureDetector;

    private UndoRedoListener mUndoRedoListener;
    private OnColorChangeListener mColorChangeListener;
    private int mDoodleAction;
    private List<Integer> mUndoRecordIds;
    private List<Integer> mRedoRecordIds;

    private BitmapPool mDoodleBitmapPool;

    public Whiteboard(Context context) {
        super(context);
        initParams(context);
    }

    public Whiteboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initParams(context);
    }

    public Whiteboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParams(context);
    }

    @TargetApi(21)
    public Whiteboard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initParams(context);
    }

    public void setGestureDetector(GestureDetector gestureDetector) {
        mClassroomGestureDetector = gestureDetector;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mClassroomGestureDetector != null) {
            if (InteractiveLevel.WHITE_BOARD == getInteractiveLevel()) {
                mClassroomGestureDetector.onTouchEvent(event);
                mViewGestureListener.onTouchEvent(event);
                return true;
            } else {
                return false;
            }
        } else {
            mViewGestureListener.onTouchEvent(event);
            return true;
        }
    }

    private int getInteractiveLevel() {
        Context cxt = getContext();
        if (cxt instanceof ClassroomActivity) {
            return ((ClassroomActivity) cxt).getInteractiveLevel();
        }

        return InteractiveLevel.MAIN_PANEL;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
        mViewHeight = MeasureSpec.getSize(heightMeasureSpec);

        int temp = (int) (DOODLE_CANVAS_RATIO * mViewHeight);
        if (temp > mViewWidth) {
            // depend width
            mBlackboardWidth = mViewWidth;
            mBlackboardHeight = (int) (mViewWidth / DOODLE_CANVAS_RATIO);
        } else {
            // depend height
            mBlackboardHeight = mViewHeight;
            mBlackboardWidth = temp;
        }

        mBlackboardRect.set(0, 0, mBlackboardWidth, mBlackboardHeight);
        mDrawingMatrix.setRectToRect(new RectF(0, 0, 1, 1), mBlackboardRect, Matrix.ScaleToFit.FILL);
        mDisplayMatrix.setRectToRect(mBlackboardRect, mDoodleBounds, Matrix.ScaleToFit.FILL);
        mViewGestureListener.onViewChanged(mViewWidth, mViewHeight, mBlackboardWidth, mBlackboardHeight);
        mMeasureFinished = mBlackboardHeight > 0 && mBlackboardWidth > 0;
        onMeasureFinished(mMeasureFinished);
    }

    private void onMeasureFinished(boolean finished) {
        if (finished) {
            createDoodleCanvas();

            if (mCourseBmp != null) {
                mSrcCourseRect = new Rect(0, 0, mCourseBmp.getWidth(), mCourseBmp.getHeight());
                mDesCourseRect = new Rect(0, 0, mBlackboardWidth, mBlackboardHeight);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {

    }

    public void setEditText(final SpecialEditText editText) {
        mEditText = editText;
        mEditText.setVisibility(View.VISIBLE);
        mEditText.setAlpha(0);

        mEditText.addTextChangedListener(mTextWatcher);
        mEditText.setOnImeBackListener(onImeBackListener);
        mEditText.setOnKeyListener(mEditTextKeyListener);
    }

    private TextWatcher mTextWatcher = new TextWatcher() {
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
                ((TextWriting) mDoodle).onTextChanged(text);

                Whiteboard.this.invalidate();
            }
        }
    };

    private OnImeBackListener onImeBackListener = new OnImeBackListener() {
        @Override
        public void onImeBackPressed() {
            handleEditOnImeHide();
        }
    };

    private OnKeyListener mEditTextKeyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_UP &&
                    keyCode == KeyEvent.KEYCODE_ENTER && event.getFlags() == 0x16) {
                handleEditOnImeHide();
            }
            return false;
        }
    };

    private void handleEditOnImeHide() {
        if (mDoodle instanceof TextWriting) {
            String text = ((TextWriting) mDoodle).getTextString();
            if (!TextUtils.isEmpty(text)) {
                if (mDoodle.getUndoRecords().isEmpty() && mDoodle.getRedoRecords().isEmpty()) {
                    //add action
                    addRecords(mDoodle, Action.ADD_ACTION);
                }
            } else {
                if (mDoodle.isCanRedo() || mDoodle.isCanUndo()) {
                    boolean remove = false;
                    if (mAllDoodles != null) {
                        remove = mAllDoodles.remove(mDoodle);
                    }
                    if (mUndoRecordIds.size() > 0 && remove) {
                        mUndoRecordIds.remove(mUndoRecordIds.size() - 1);
                        if (mUndoRedoListener != null) {
                            mUndoRedoListener.onUndoRedoStackChanged();
                        }
                    }
                    mDoodle = null;
                }
            }
        }
    }

    public void setNeedBitmapPool(boolean needBitmapPool) {
        mNeedBitmapPool = needBitmapPool;
    }

    public void setLayer(WhiteboardLayer layer) {
        layer.setWidth(mBlackboardWidth);
        layer.setHeight(mBlackboardHeight);

        mLayer = layer;
        mAllDoodles = layer.getAllDoodles();
        mReDoStack = layer.getReDoStack();
        mUndoRecordIds = layer.getUndoRecordIds();
        mRedoRecordIds = layer.getRedoRecordIds();

        forceCenter();

        if (mUndoRedoListener != null) {
            mUndoRedoListener.onUndoRedoStackChanged();
        }
        mSelector.reset();
        mDoodle = null;

        if (mAllDoodles != null) {
            for (Doodle d : mAllDoodles) {
                d.setWhiteboard(this);
            }
        }

        if (mDoodleCanvas != null) {
            drawAllDoodlesCanvas();
        }

        if (mNeedBitmapPool) {
            mDoodleBitmapPool = BitmapPool.getPool(BitmapPool.TYPE_DOODLE);
        }
        postInvalidate();

        if (mLayer != null && !TextUtils.isEmpty(mLayer.getCoursePath())) {
            loadCourse(Uri.parse(mLayer.getCoursePath()));
        }
    }

    public void setSrcBitmap(Bitmap bmp) {
        if (bmp == null) {
            return;
        }

        mSrcCourseRect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        mDesCourseRect = new Rect(0, 0, mBlackboardWidth, mBlackboardHeight);
        mCourseBmp = bmp;
        postInvalidate();
    }

    /**
     * 强制是白板居中
     */
    private void forceCenter() {
        if (mBlackboardWidth > 0 && mBlackboardHeight > 0 && mViewWidth > 0 && mViewHeight > 0) {
            mViewGestureListener.reset();
        }
    }

    public void onWhiteboardSelected() {
        setGeometryShapeId(GeometryShape.RECTANGLE);
        switchMode(Whiteboard.MODE_NONE);
    }

    /**
     * 加载课件
     *
     * @param uri 课件对于图片的uri
     */
    private void loadCourse(final Uri uri) {
        mCourseUri = uri;
        //load course img
        new AsyncTask<Void, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    int w = mBlackboardWidth;
                    int h = mBlackboardHeight;
                    if (w == 0 || h == 0) {
                        w = ProtocolConfigs.VIRTUAL_WIDTH;
                        h = ProtocolConfigs.VIRTUAL_HEIGHT;
                    }

                    return Glide.with(mContext)
                            .load(uri)
                            .asBitmap()
                            .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                            .into(w, h)
                            .get();
                } catch (Exception e) {
                    Logger.i(e != null ? e.getLocalizedMessage() : "null");
                }

                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    mSrcCourseRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    mDesCourseRect = new Rect(0, 0, mBlackboardWidth, mBlackboardHeight);
                    mCourseBmp = bitmap;
                    postInvalidate();
                }
            }
        }.execute();
    }

    private void initParams(Context context) {
        mMeasureFinished = false;
        mContext = context;
        mDoodleBounds = new RectF();
        mDoodleBounds = new RectF();

        mDownPoint = new PointF();
        mLastPoint = new PointF();
        mDrawingPath = new Path();
        mWhiteboardParams = new WhiteboardParams();

        //init screen size
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        mViewGestureListener = new ViewGestureListener(context, this, new TouchEventListener(), true);
        mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        mPreviousPoint = new PointF();
        mSelector = new Selector(this);
        WhiteboardConfigs.init(getContext());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mInitLayer && mMeasureFinished) {
            mInitLayer = true;
            drawAllDoodlesCanvas();
        }

        RectF destF = mViewGestureListener.getDestRect();
        mDoodleBounds.set(destF.left, destF.top, destF.right, destF.bottom);

        canvas.save();
        //clip
        canvas.clipRect(mDoodleBounds);

        //1. draw background
        canvas.drawColor(BG_COLOR);

        //map matrix
        mDisplayMatrix.setRectToRect(mBlackboardRect, mDoodleBounds, Matrix.ScaleToFit.FILL);
        canvas.concat(mDisplayMatrix);

        //2. draw course image
        if (mCourseBmp != null) {
            canvas.drawBitmap(mCourseBmp, mSrcCourseRect, mDesCourseRect, null);
        }

        //3. draw doodle bitmap
        if (mDoodleBitmap != null) {
            updateAllDoodleDisplayMatrix();
            canvas.drawBitmap(mDoodleBitmap, 0, 0, null);
        }

        //4. draw current doodle
        if (mDoodle != null) {
            drawDoodle(canvas, mDoodle);
        }

        //5. draw selector
        drawSelector(canvas, mSelector);
        canvas.restore();

        //6. draw border(selected doodle or selector)
        if (!onViewChanged) {
            if (mCurrentMode == MODE_SELECTION) {
                drawDoodleBorder(canvas, mSelector);
            } else {
                canvas.save();
                canvas.concat(mDisplayMatrix);
                drawDoodleBorder(canvas, mDoodle);
                canvas.restore();
            }
        }
    }

    @Override
    public void onViewRectChanged() {
        onViewChanged = true;
        postInvalidate();
    }

    private class TouchEventListener extends SimpleTouchEventListener {
        @Override
        public boolean onActionDown(MotionEvent event) {
            mDownPoint.x = event.getX();
            mDownPoint.y = event.getY();

            //reset status
            onViewChanged = false;
            mDoodleAdded = false;
            mIsRecordedParams = false;
            mSelectionRectRegion = IntersectionHelper.RECT_NO_SELECTED;
            mTransform = false;
            mCanMovable = false;
            mDoodleAction = Action.NO_ACTION;

            switch (mCurrentMode) {
                case MODE_SELECTION:
                    mSelector.setBorderVisible(true);
                    if (mSelector.getState() == Doodle.STATE_EDIT) {
                        mSelectionRectRegion = mSelector.checkPressedRegion(mDownPoint.x, mDownPoint.y);
                    }
                    break;
                case MODE_TEXT:
                    if (mDoodle instanceof TextWriting) {
                        if (!TextUtils.isEmpty(((TextWriting) mDoodle).getTextString())) {
                            if (mDoodle.getState() == Doodle.STATE_EDIT) {
                                mSelectionRectRegion = mDoodle.checkPressedRegion(mDownPoint.x, mDownPoint.y);
                            }
                        } else {
                            if (mAllDoodles != null) {
                                mAllDoodles.remove(mDoodle);
                            }
                            mDoodle = null;
                        }
                    }
                    break;
                case MODE_COLOR_PICKER:
                    break;
                case MODE_HAND_WRITING:
                case MODE_GEOMETRY:
                    if (mDoodle != null) {
                        if (mDoodle.getState() == Doodle.STATE_EDIT) {
                            mSelectionRectRegion = mDoodle.checkPressedRegion(mDownPoint.x, mDownPoint.y);
                            //do nothing
                            //在手指弹起的时候才更新才状态，即在onActionUP函数调用时候
                        }
                    }
                    break;
                case MODE_ERASER:
                    break;
            }

            if (mSelectionRectRegion == IntersectionHelper.LEFT_BOTTOM_CORNER) {
                switch (mCurrentMode) {
                    case MODE_SELECTION:
                        mSelector.setVisibility(View.GONE);
                        mSelector.setBorderVisible(false);
                        mDoodleAction = Action.DELETE_ACTION;
                        drawAllDoodlesCanvas();
                        postInvalidate();
                        break;
                    case MODE_TEXT:
                    case MODE_HAND_WRITING:
                    case MODE_GEOMETRY:
                        mDoodle.setVisibility(View.GONE);
                        mDoodleAction = Action.DELETE_ACTION;
                        drawAllDoodlesCanvas();
                        postInvalidate();
                        break;
                }
            }
            return false;
        }

        @Override
        public void onActionMove(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            Utils.mapScreenToDoodlePoint(x, y, mDoodleBounds, mLastPoint);
            if (!mCanMovable) {
                mCanMovable = Utils.isMovable(mDownPoint.x, mDownPoint.y, x, y, WhiteboardConfigs.TOUCH_SLOPE);
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
                            if (mSelectionRectRegion != IntersectionHelper.RECT_NO_SELECTED) {
                                mTransform = true;
                            }
                        }

                        if (mSelectionRectRegion != IntersectionHelper.RECT_NO_SELECTED) {
                            switch (mSelectionRectRegion) {
                                case IntersectionHelper.RIGHT_TOP_CORNER:
                                    //scale and rotate
                                    mDoodle.scaleAndRotate(mPreviousPoint.x, mPreviousPoint.y, x, y);
                                    //scale_rotate record
                                    mDoodleAction = Action.SCALE_ROTATE_ACTION;
                                    postInvalidate();
                                    break;
                                case IntersectionHelper.RIGHT_BOTTOM_CORNER:
                                case IntersectionHelper.RECT_BODY:
                                case IntersectionHelper.LEFT_TOP_CORNER:
                                    //move
                                    mDoodle.move((x - mPreviousPoint.x), (y - mPreviousPoint.y));
                                    //move record
                                    mDoodleAction = Action.MOVE_ACTION;

                                    postInvalidate();
                                    break;
                                case IntersectionHelper.TOP_EDGE:
                                case IntersectionHelper.RIGHT_EDGE:
                                case IntersectionHelper.BOTTOM_EDGE:
                                case IntersectionHelper.LEFT_EDGE:
                                    mDoodle.changeByEdge(mPreviousPoint.x, mPreviousPoint.y, x, y, mSelectionRectRegion);
                                    //change area record
                                    mDoodleAction = Action.CHANGE_AREA_ACTION;

                                    postInvalidate();
                                    break;

                            }

                            mPreviousPoint.x = x;
                            mPreviousPoint.y = y;
                        } else if (mCurrentMode != MODE_TEXT) {
                            if (!mDoodleAdded) {
                                mDoodleAdded = true;
                                mDoodleAction = Action.ADD_ACTION;
                                //保存之前的绘制的
                                drawToDoodleCanvas();
                                buildDoodle();
                            }

                            addLastPointIntoDoodle();
                            postInvalidate();
                        }
                    }
                    break;
                case MODE_SELECTION:
                    if (mCanMovable) {
                        if (!mIsRecordedParams) {
                            mIsRecordedParams = true;
                            mPreviousPoint.x = x;
                            mPreviousPoint.y = y;
                            if (mSelectionRectRegion != IntersectionHelper.RECT_NO_SELECTED) {
                                mTransform = true;
                            } else {
                                mSelector.reset();
                            }
                        }
                        if (mSelectionRectRegion != IntersectionHelper.RECT_NO_SELECTED) {
                            switch (mSelectionRectRegion) {
                                case IntersectionHelper.RIGHT_TOP_CORNER:
                                    //scale and rotate
                                    mSelector.scaleAndRotate(mPreviousPoint.x, mPreviousPoint.y, x, y);
                                    //scale_rotate record
                                    mDoodleAction = Action.SCALE_ROTATE_ACTION;
                                    drawAllDoodlesCanvas();
                                    postInvalidate();
                                    break;
                                case IntersectionHelper.TOP_EDGE:
                                case IntersectionHelper.RIGHT_EDGE:
                                case IntersectionHelper.BOTTOM_EDGE:
                                case IntersectionHelper.LEFT_EDGE:
                                    mSelector.changeByEdge(mPreviousPoint.x, mPreviousPoint.y, x, y, mSelectionRectRegion);
                                    //change area record
                                    mDoodleAction = Action.CHANGE_AREA_ACTION;
                                    drawAllDoodlesCanvas();
                                    postInvalidate();
                                    break;
                                default:
                                    //move
                                    mSelector.move((x - mPreviousPoint.x), (y - mPreviousPoint.y));
                                    //add move record
                                    mDoodleAction = Action.MOVE_ACTION;
                                    drawAllDoodlesCanvas();
                                    postInvalidate();
                                    break;
                            }

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
                    if (mSelectionRectRegion == IntersectionHelper.RECT_NO_SELECTED) {
                        int intersectCount = mCanMovable ? mSelector.checkIntersect() :
                                mSelector.checkSingleIntersect(event.getX(), event.getY());
                        if (intersectCount <= 0) {
                            mSelector.reset();
                        } else {
                            mSelector.setState(Doodle.STATE_EDIT);
                            mSelectionRectRegion = IntersectionHelper.RECT_BODY;
                        }
                        postInvalidate();
                    }

                    if (mDoodleAction != Action.NO_ACTION) {
                        //add action
                        addRecords(mDoodleAction);

                        if (mDoodleAction == Action.DELETE_ACTION) {
                            mSelector.reset();
                        }
                    }
                    break;
                case MODE_GEOMETRY:
                case MODE_HAND_WRITING:
                case MODE_TEXT:
                    if (mDoodle != null) {
                        if (mDoodleAction != Action.NO_ACTION) {
                            //add action
                            addRecords(mDoodle, mDoodleAction);
                        }

                        if (mCurrentMode != MODE_TEXT) {
                            if (mDoodle.getState() == Doodle.STATE_DRAWING) {
                                if (mDoodle instanceof HandWriting) {
                                    //如果是手写模式，默认绘制图形不被选择
                                    drawToDoodleCanvas();
                                    mDoodle.setState(Doodle.STATE_IDLE);
                                    mDoodle = null;
                                    postInvalidate();
                                } else {
                                    mDoodle.setState(Doodle.STATE_EDIT);
                                    postInvalidate();
                                }
                            } else if (mDoodle.getState() == Doodle.STATE_EDIT) {
                                if (mSelectionRectRegion == IntersectionHelper.RECT_NO_SELECTED) {
                                    drawToDoodleCanvas();
                                    mDoodle.setState(Doodle.STATE_IDLE);
                                    mDoodle = null;
                                    postInvalidate();
                                }
                            }

                            //如果当前在选择模式下，但是是没有变换（如拖动，缩放，平移操作），重新检测该图形是否被选中
                            if (!mTransform && mSelectionRectRegion != IntersectionHelper.RECT_NO_SELECTED) {
                                mSelectedOnPressed = mDoodle.isSelected(mDownPoint.x, mDownPoint.y);
                                if (!mSelectedOnPressed) {
                                    drawToDoodleCanvas();
                                    mDoodle.setState(Doodle.STATE_IDLE);
                                    mDoodle = null;
                                    postInvalidate();
                                }
                            }
                        }
                    }
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
            //edit again
            //if (mCurrentMode == MODE_SELECTION && mSelector != null) {
            //    mDoodle = mSelector.getSelectedDoodle();
            //}
            //
            //if (mDoodle instanceof TextWriting && mDoodle.getState() == Doodle.STATE_EDIT) {
            //    mEditText.requestFocus();
            //    showInputMethod(mEditText);
            //   mEditText.setText(((TextWriting)mDoodle).getTextString());
            //}
        }

        @Override
        public void onSingleTapUp(MotionEvent event) {
            //对于文本绘制手指弹起逻辑
            if (mCurrentMode == MODE_TEXT) {
                if (mDoodle != null) {
                    if (mDoodle.getState() == Doodle.STATE_EDIT || mDoodle.getState() == Doodle.STATE_DRAWING) {
                        if (mSelectionRectRegion == IntersectionHelper.RECT_NO_SELECTED) {
                            hideInputMethod();
                            handleEditOnImeHide();
                            drawToDoodleCanvas();
                            mDoodle.setState(Doodle.STATE_IDLE);
                            mDoodle = null;
                            postInvalidate();
                        } else if (mDoodleAction == Action.DELETE_ACTION) {
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
        doodle.drawSelf(canvas);
    }

    private void drawSelector(Canvas canvas, Selector selector) {
        if (selector == null) {
            return;
        }

        selector.setDrawingMatrix(mDrawingMatrix);
        selector.setDisplayMatrix(mDisplayMatrix);
        switch (mCurrentMode) {
            case MODE_SELECTION:
                if (selector.getState() == Doodle.STATE_DRAWING) {
                    selector.drawSelf(canvas);
                }
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

    private Doodle buildDoodle() {
        return buildDoodle(null, mCurrentMode);
    }

    private Doodle buildDoodle(String doodleId, int mode) {
        Paint paint = null;
        mDoodle = null;
        switch (mode) {
            case MODE_SELECTION:
                //mSelector = new Selector(this);
                break;
            case MODE_HAND_WRITING:
                paint = Utils.createPaint(mPaintColor, mPaintStrokeWidth, Paint.Style.STROKE);
                mDoodle = new HandWriting(this, paint);
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
                mDoodle = new TextWriting(this, paint, mTextOrientation);
                break;
            case MODE_ERASER:

                break;
            case MODE_COLOR_PICKER:

                break;

        }

        if (mDoodle != null && mDoodle.getState() == Doodle.STATE_IDLE && mAllDoodles != null) {
            mDoodle.setState(Doodle.STATE_DRAWING);
            if (!TextUtils.isEmpty(doodleId)) {
                mDoodle.setDoodleId(doodleId);
            }
            mAllDoodles.add(mDoodle);
        }

        return mDoodle;
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
        drawDoodle(mDoodleCanvas, mDoodle);
        postInvalidate();
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

        if (mAllDoodles == null) {
            return;
        }

        for (int i = 0; i < mAllDoodles.size() - 1; i++) {
            drawDoodle(mDoodleCanvas, mAllDoodles.get(i));
        }
    }

    private void updateAllDoodleDisplayMatrix() {
        if (mAllDoodles != null) {
            for (Doodle d : mAllDoodles) {
                d.setDisplayMatrix(mDisplayMatrix);
            }
        }
    }

    private void drawAllDoodlesCanvas() {
        eraserAllDoodle();

        if (mAllDoodles != null) {
            for (Doodle d : mAllDoodles) {
                d.setDrawingMatrix(mDrawingMatrix);
                d.setDisplayMatrix(mDisplayMatrix);
                d.drawSelf(mDoodleCanvas);
            }
        }
    }

    private void createDoodleCanvas() {
        if (mDoodleCanvas == null && mLayer != null) {
            int w = mBlackboardWidth;
            int h = mBlackboardHeight;
            mLayer.setWidth(mBlackboardWidth);
            mLayer.setHeight(mBlackboardHeight);

            mDoodleBitmap = mDoodleBitmapPool != null ? mDoodleBitmapPool.getBitmap(w, h) : null;
            if (mDoodleBitmap == null) {
                mDoodleBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
            }
            mDoodleCanvas = new Canvas(mDoodleBitmap);

            mBlackboardRect.set(0, 0, w, h);
            mDrawingMatrix.setRectToRect(new RectF(0, 0, 1, 1), mBlackboardRect, Matrix.ScaleToFit.FILL);
            mDisplayMatrix.setRectToRect(mBlackboardRect, mDoodleBounds, Matrix.ScaleToFit.FILL);
        }
    }

    private void addEditText(float x, float y, boolean center) {
        if (!(mDoodle instanceof TextWriting) || mEditText == null) {
            return;
        }

        mEditText.requestFocus();
        showInputMethod(mEditText);

        float padding = WhiteboardConfigs.TEXT_BORDER_PADDING;

        float defaultW = WhiteboardConfigs.MIN_EDIT_TEXT_WIDTH;
        float defaultH = Utils.getDefaultTextHeight(mDoodle);

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
    }

    public void switchMode(int mode) {
        mCurrentMode = mode;

        mSelector.reset();
        if (mDoodle != null) {
            drawToDoodleCanvas();
            mDoodle.setState(Doodle.STATE_IDLE);
            mDoodle = null;
        }
        postInvalidate();

        //test
        if (mBlackboardWidth > 0 && mBlackboardHeight > 0) {
            /*String[] params = new String[5];
            long s = System.currentTimeMillis();

            for (int i = 0; i < 100; i ++) {
                mGeometryShapeId = GeometryShape.RECTANGLE;
                int m = MODE_GEOMETRY;
                //int r = (int)(Math.random() * 255);
                //int g = (int)(Math.random() * 255);
                //int b = (int)(Math.random() * 255);
                ///mPaintColor = Color.argb(255, r, g, b);
                mPaintColor = Color.RED;
                Doodle d = buildDoodle(null, m);
                d.setState(Doodle.STATE_EDIT);
                //addRecords(d, Action.ADD_ACTION);
                params[0] = "rect";
                params[1] = String.valueOf((int)(Math.random() * 800));
                params[2] = String.valueOf((int)(Math.random() * 600));
                params[3] = String.valueOf((int)(Math.random() * 800));
                params[4] = String.valueOf((int)(Math.random() * 600));

                fillDoodlePoints(params, d);
            }
            Log.i("aaa", "================build data============"+(System.currentTimeMillis()-s));

            drawAllDoodlesCanvas();
            Log.i("aaa", "================take============"+(System.currentTimeMillis()-s) +"  "+mAllDoodles.size());

            postInvalidate();*/
        }
    }

    public int getMode() {
        return mCurrentMode;
    }


    public void setGeometryShapeId(int shapeId) {
        mGeometryShapeId = shapeId;
    }

    public void setPaintStrokeWidth(final float strokeWidth) {
        mPaintStrokeWidth = strokeWidth;
        //onSend(Packer.getChangePaintCmd(strokeWidth, mPaintColor, true));
    }

    public void setPaintColor(int color) {
        mPaintColor = color;
        StringBuilder sb = new StringBuilder();
        switch (mCurrentMode) {
            case MODE_SELECTION:
                mSelector.updateDoodleColor(color);
                drawAllDoodlesCanvas();
                postInvalidate();

                if (mAllDoodles != null) {
                    for (Doodle d : mAllDoodles) {
                        if (d.getState() == Doodle.STATE_EDIT) {
                            String cmd = Packer.getChangeColorCmd(d, false);
                            sb.append(cmd);
                            sb.append(" ");
                        }
                    }
                }
                break;
            default:
                if (mDoodle != null) {
                    mDoodle.getPaint().setColor(color);
                    drawAllDoodlesCanvas();
                    postInvalidate();

                    String cmd = Packer.getChangeColorCmd(mDoodle, false);
                    sb.append(cmd);
                    sb.append(" ");
                }
                break;
        }

        String cmd = null;
        if (sb.length() > 0) {
            sb.append("#");
            sb.append(System.currentTimeMillis());
            cmd = sb.toString();
        } else {
            cmd = Packer.getChangePaintCmd(mPaintStrokeWidth, color, true);
        }

        onSend(cmd);
    }

    public int getPaintColor() {
        return mPaintColor;
    }

    public void setTextOrientation(int orientation) {
        mTextOrientation = orientation;
    }

    public void showInputMethod(EditText editText) {
        mInputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
    }

    public void hideInputMethod() {
        mInputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    public static class WhiteboardParams {
        public RectF drawingBounds;
        public int originalWidth;
        public int originalHeight;
        public float paintScale = 1.0f;
        public float scale = 1.0f;
        public float paintStrokeWidth;
    }

    public WhiteboardParams getParams() {
        mWhiteboardParams.originalWidth = mBlackboardWidth;
        mWhiteboardParams.originalHeight = mBlackboardHeight;
        mWhiteboardParams.paintScale = mPaintScale;
        mWhiteboardParams.scale = mViewGestureListener.getScale();
        mWhiteboardParams.drawingBounds = mDoodleBounds;
        mWhiteboardParams.paintStrokeWidth = mPaintStrokeWidth;
        return mWhiteboardParams;
    }

    public ArrayList<Doodle> getAllDoodles() {
        return mAllDoodles;
    }

    public ArrayList<Doodle> getReDoStack() {
        return mReDoStack;
    }

    public void onClearWhiteboard() {
        clearWhiteboard();
        onSend(Packer.getClearCmd());
    }

    private void clearWhiteboard() {
        if (mAllDoodles != null) {
            mAllDoodles.clear();
            mReDoStack.clear();

            mUndoRecordIds.clear();
            mRedoRecordIds.clear();

            if (mDoodleBitmap != null) {
                mDoodleBitmap.eraseColor(0);
            }

            if (mSelector != null) {
                mSelector.reset();
            }
            mDoodle = null;
            if (mUndoRedoListener != null) {
                mUndoRedoListener.onUndoRedoStackChanged();
            }

            postInvalidate();
        }
    }

    public void recycle() {
        if (mDoodleBitmapPool != null) {
            mDoodleBitmapPool.recycle(mDoodleBitmap);
        } else {
            if (mDoodleBitmap != null && !mDoodleBitmap.isRecycled()) {
                mDoodleBitmap.recycle();
            }
        }

        if (mAllDoodles != null) {
            for (Doodle d : mAllDoodles) {
                d.setWhiteboard(null);
            }
        }

        if (mSelector != null) {
            mSelector.setWhiteboard(null);
        }

        if (mViewGestureListener != null) {
            mViewGestureListener.removeViewRectChangedListener();
        }

        if (mEditText != null) {
            mEditText.removeTextChangedListener(mTextWatcher);
            mEditText.setOnImeBackListener(null);
            mEditText.setOnKeyListener(null);
        }

        mContext = null;
        mLayer = null;
        mEditText = null;
        mInputMethodManager = null;
        mViewGestureListener = null;
        mClassroomGestureDetector = null;
    }

    public void recycleCourseBmp() {
        if (mCourseBmp != null && !mCourseBmp.isRecycled()) {
            mCourseBmp.recycle();
            mCourseBmp = null;
        }
    }

    public void release() {
        recycle();
        if (mDoodleBitmapPool != null) {
            mDoodleBitmapPool.release();
        }
    }

    public void transformation(MotionEvent event) {
        if (mViewGestureListener != null) {
            mViewGestureListener.onTouchEvent(event);
        }
    }

    public void exit() {
        if (mDoodle != null) {
            mDoodle.setWhiteboard(null);
        }
        mDoodle = null;
        postInvalidate();
    }

    public void undo() {
        List<Doodle> allDoodles = getAllDoodles();
        if (allDoodles != null && !allDoodles.isEmpty()) {
            Iterator<Doodle> it = allDoodles.iterator();
            boolean hasUndo = false;
            int groupId = mUndoRecordIds.get(mUndoRecordIds.size() - 1);
            StringBuilder sb = new StringBuilder();
            while (it.hasNext()) {
                Doodle doodle = it.next();
                ActionRecord[] records = doodle.undo(groupId);
                ActionRecord lastRecord = records[0];
                ActionRecord prevRecord = records[1];
                if (lastRecord == null) {
                    continue;
                }

                if (lastRecord.action == Action.ADD_ACTION) {
                    it.remove();
                    mSelector.reset();
                } else if (prevRecord != null) {
                    if (lastRecord.action == Action.DELETE_ACTION) {
                        doodle.setVisibility(View.VISIBLE);
                    }
                    doodle.setPoints(prevRecord.mPoints);
                    doodle.setDoodleRect(prevRecord.rect);
                    doodle.setTransformMatrix(prevRecord.mTransMatrix);
                    doodle.setTotalDegree(prevRecord.degree);
                    doodle.setTotalScale(prevRecord.scale);
                    doodle.setTranslateX(prevRecord.translateX);
                    doodle.setTranslateY(prevRecord.translateY);

                    if (doodle instanceof Triangle) {
                        ((Triangle) doodle).updateTriangleCoordinates();
                    }
                }

                hasUndo = true;
                if (!mReDoStack.contains(doodle)) {
                    mReDoStack.add(doodle);
                }

                String cmd = getUndoRedoSendCommend(doodle, lastRecord, true, false);
                sb.append(cmd);
                sb.append(" ");
            }

            if (hasUndo) {
                mDoodle = null;
                mSelector.reset();
                mUndoRecordIds.remove(mUndoRecordIds.size() - 1);
                mRedoRecordIds.add(groupId);
                drawAllDoodlesCanvas();
                postInvalidate();

                if (mUndoRedoListener != null) {
                    mUndoRedoListener.onUndoRedoStackChanged();
                }
            }

            if (hasUndo) {
                sb.append("#");
                sb.append(System.currentTimeMillis());
                onSend(sb.toString());
            }
        }
    }

    public void redo() {
        List<Doodle> reDoStack = getReDoStack();
        if (reDoStack != null && !reDoStack.isEmpty()) {
            Iterator<Doodle> it = reDoStack.iterator();
            boolean hasRedo = false;
            int groupId = mRedoRecordIds.get(mRedoRecordIds.size() - 1);
            StringBuilder sb = new StringBuilder();
            while (it.hasNext()) {
                Doodle doodle = it.next();
                ActionRecord record = doodle.redo(groupId);
                if (record == null) {
                    continue;
                }

                hasRedo = true;
                if (record.action == Action.DELETE_ACTION) {
                    doodle.setVisibility(View.GONE);
                }
                doodle.setPoints(record.mPoints);
                doodle.setDoodleRect(record.rect);
                doodle.setTransformMatrix(record.mTransMatrix);
                doodle.setTotalDegree(record.degree);
                doodle.setTotalScale(record.scale);
                doodle.setTranslateX(record.translateX);
                doodle.setTranslateY(record.translateY);

                if (doodle instanceof Triangle) {
                    ((Triangle) doodle).updateTriangleCoordinates();
                }

                if (doodle instanceof TextWriting) {
                    TextWriting tw = (TextWriting) doodle;
                    tw.onTextChanged(tw.getTextString());
                }

                if (!doodle.isCanRedo()) {
                    it.remove();
                }

                if (!mAllDoodles.contains(doodle)) {
                    mAllDoodles.add(doodle);
                }

                String cmd = getUndoRedoSendCommend(doodle, record, false, false);
                sb.append(cmd);
                sb.append(" ");
            }

            if (hasRedo) {
                mDoodle = null;
                mSelector.reset();

                mRedoRecordIds.remove(mRedoRecordIds.size() - 1);
                mUndoRecordIds.add(groupId);
                drawAllDoodlesCanvas();
                postInvalidate();

                if (mUndoRedoListener != null) {
                    mUndoRedoListener.onUndoRedoStackChanged();
                }
            }

            if (hasRedo) {
                sb.append("#");
                sb.append(System.currentTimeMillis());
                onSend(sb.toString());
            }
        }
    }

    private void addRecords(Doodle doodle, int action) {
        if (doodle == null || action == Action.NO_ACTION) {
            return;
        }

        doodle.addRecords(action, mLayer.getRecordGroupId());
        mReDoStack.clear();
        mRedoRecordIds.clear();
        mUndoRecordIds.add(mLayer.getRecordGroupId());
        mLayer.incrementGroupId();
        if (mUndoRedoListener != null) {
            mUndoRedoListener.onUndoRedoStackChanged();
        }

        //onSend(getSendCommend(doodle, action, true));
    }

    private void addRecords(int action) {
        if (action == Action.NO_ACTION) {
            return;
        }

        List<Doodle> allDoodles = getAllDoodles();
        boolean hasRecords = false;
        StringBuilder sb = new StringBuilder();
        if (allDoodles != null && !allDoodles.isEmpty()) {
            for (Doodle doodle : allDoodles) {
                if (doodle.getState() == Doodle.STATE_EDIT) {
                    hasRecords = true;
                    doodle.addRecords(action, mLayer.getRecordGroupId());

                    //String cmd = getSendCommend(doodle, action, false);
                    //sb.append(cmd);
                    sb.append(" ");
                }
            }

            if (hasRecords) {
                mReDoStack.clear();
                mRedoRecordIds.clear();
            }

            if (mUndoRedoListener != null && hasRecords) {
                mUndoRecordIds.add(mLayer.getRecordGroupId());
                mLayer.incrementGroupId();
                mUndoRedoListener.onUndoRedoStackChanged();
            }

            if (hasRecords) {
                sb.append("#");
                sb.append(System.currentTimeMillis());
                onSend(sb.toString());
            }
        }
    }

    private String getSendCommend(final Doodle d, int action, boolean whitTime) {
        if (d == null) {
            return null;
        }

        String cmd = null;
        switch (action) {
            case Action.ADD_ACTION:
                cmd = Packer.getBuildDoodleCmd(d, whitTime);
                break;
            case Action.DELETE_ACTION:
                cmd = Packer.getDeleteCmd(d, whitTime);
                break;
            case Action.MOVE_ACTION:
                float deltaX = d.getDeltaTransX();
                float deltaY = d.getDeltaTransY();
                cmd = Packer.getMoveCmd(d, deltaX, deltaY, mBlackboardWidth, mBlackboardHeight, whitTime);
                break;
            case Action.SCALE_ROTATE_ACTION:
                cmd = Packer.getScaleAndRotateCmd(d, d.getDeltaScale(), d.getDeltaDegree(), whitTime);
                break;
            case Action.SCALE_ACTION:
                cmd = Packer.getScaleCmd(d, d.getDeltaScale(), whitTime);
                break;
            case Action.ROTATE_ACTION:
                cmd = Packer.getRotateCmd(d, d.getDeltaDegree(), whitTime);
                break;
        }

        return cmd;
    }

    private String getUndoRedoSendCommend(final Doodle d, ActionRecord actRd, boolean undo, boolean whitTime) {
        if (d == null || actRd == null) {
            return null;
        }

        String cmd = null;
        switch (actRd.action) {
            case Action.ADD_ACTION:
                if (undo) {
                    cmd = Packer.getDeleteCmd(d, whitTime);
                } else {
                    cmd = Packer.getBuildDoodleCmd(d, whitTime);
                }
                break;
            case Action.DELETE_ACTION:
                if (!undo) {
                    cmd = Packer.getDeleteCmd(d, whitTime);
                } else {
                    cmd = Packer.getBuildDoodleCmd(d, whitTime);
                }
                break;
            case Action.MOVE_ACTION:
                float moveX = undo ? -actRd.translateX : actRd.translateX;
                float moveY = undo ? -actRd.translateY : actRd.translateY;
                cmd = Packer.getMoveCmd(d, moveX, moveY, mBlackboardWidth, mBlackboardHeight, whitTime);
                break;
            case Action.SCALE_ROTATE_ACTION:
                cmd = Packer.getScaleAndRotateCmd(d, undo ? 1.f / actRd.scale : actRd.scale,
                        undo ? -actRd.degree : actRd.degree, whitTime);
                break;
            case Action.SCALE_ACTION:
                cmd = Packer.getScaleCmd(d, undo ? 1.f / actRd.scale : actRd.scale, whitTime);
                break;
            case Action.ROTATE_ACTION:
                cmd = Packer.getRotateCmd(d, undo ? -actRd.degree : actRd.degree, whitTime);
                break;
        }

        return cmd;
    }

    public boolean isCanUndo() {
        return mUndoRecordIds != null && !mUndoRecordIds.isEmpty();
    }

    public boolean isCanRedo() {
        return mRedoRecordIds != null && !mRedoRecordIds.isEmpty();
    }

    public void setUndoRedoListener(UndoRedoListener listener) {
        mUndoRedoListener = listener;
    }

    public void setOnColorChangeListener(OnColorChangeListener listener) {
        mColorChangeListener = listener;
    }

    public Bitmap getWhiteboardBitmap() {
        //return mDoodleBitmap;
        if (mCourseBmp != null) {
            Bitmap bmp = Bitmap.createBitmap(mDoodleBitmap.getWidth(), mDoodleBitmap.getHeight(), Bitmap.Config.ARGB_4444);
            Canvas c = new Canvas(bmp);
            c.drawBitmap(mCourseBmp, mSrcCourseRect, mDesCourseRect, null);
            c.drawBitmap(mDoodleBitmap, 0, 0, null);
            return bmp;
        } else {
            Bitmap bmp = Bitmap.createBitmap(mDoodleBitmap.getWidth(), mDoodleBitmap.getHeight(), Bitmap.Config.ARGB_4444);
            Canvas c = new Canvas(bmp);
            c.drawColor(Color.WHITE);
            c.drawBitmap(mDoodleBitmap, 0, 0, null);
            return bmp;
        }
    }

    public WhiteboardLayer getLayer() {
        return mLayer;
    }

    @Override
    public void onReceive(List<CommendLine> data) {
        if (data != null && !data.isEmpty() && (mLayer != null && mLayer.isCanReceive())) {
            for (CommendLine cmdl : data) {
                List<Commend> cmdList = cmdl.whiteboardCommends;
                if (cmdList != null && !cmdList.isEmpty()) {
                    for (Commend cmd : cmdList) {
                        Log.i("aaa", ">>>>>>>>>>>>>> receive=" + cmd);
                        handleReceiveCmd(cmd);
                    }
                }
            }

            //draw
            postInvalidate();
        }
    }

    @Override
    public void onSend(String cmd) {
        if (!TextUtils.isEmpty(cmd) && (mLayer != null && mLayer.isCanSend())) {
            Log.i("aaa", "************ send=" + cmd);
            //to do something
        }
    }

    private synchronized void handleReceiveCmd(Commend cmd) {
        if (cmd == null) {
            return;
        }

        String[] params = cmd.params != null ? cmd.params.split(",") : null;
        if (ProtocolConfigs.NEW.equals(cmd.cm)) {
            //new doodle
            int shapeId = Parser.getShapeId(params);
            if (shapeId > 0) {
                mGeometryShapeId = shapeId;
            }
            int mode = Parser.getDoodleMode(params);
            Doodle d = buildDoodle(cmd.id, mode);
            d.setState(Doodle.STATE_EDIT);
            fillDoodlePoints(params, d);
            drawToDoodleCanvas();
            if (d != null) {
                d.setState(Doodle.STATE_IDLE);
            }
        } else if (ProtocolConfigs.DEL.equals(cmd.cm)) {
            //del doodle
            Doodle d = findDoodleById(cmd.id);
            if (d != null) {
                d.setVisibility(View.GONE);
                mAllDoodles.remove(d);
            }
            drawToDoodleCanvas();
        } else if (ProtocolConfigs.CLEAR.equals(cmd.cm)) {
            //clear all doodle
            clearWhiteboard();
        } else if (ProtocolConfigs.MOVE_A.equalsIgnoreCase(cmd.cm)) {
            //move doodle: absolute or relative
            Doodle d = findDoodleById(cmd.id);
            moveDoodle(params, d, ProtocolConfigs.MOVE_A.equals(cmd.cm));
            drawToDoodleCanvas();
        } else if (ProtocolConfigs.ROTATE_A.equalsIgnoreCase(cmd.cm)) {
            //rotate doodle: by doodle center
            Doodle d = findDoodleById(cmd.id);
            boolean absolute = ProtocolConfigs.ROTATE_A.equals(cmd.cm);
            rotateDoodle(params, d, absolute);
            drawToDoodleCanvas();
        } else if (ProtocolConfigs.ZOOM_A.equalsIgnoreCase(cmd.cm)) {
            //scale doodle: by doodle center
            Doodle d = findDoodleById(cmd.id);
            boolean absolute = ProtocolConfigs.ZOOM_A.equals(cmd.cm);
            scaleDoodle(params, d, absolute);
            drawToDoodleCanvas();
        } else if (ProtocolConfigs.CHANGE_COLOR.equals(cmd.cm)) {
            //change color
            Doodle doodle = findDoodleById(cmd.id);
            if (doodle != null && params.length > 0) {
                int c = Utils.getColor(params[0], mPaintColor);
                doodle.getPaint().setColor(c);
            }
            drawToDoodleCanvas();
        } else if (ProtocolConfigs.PAINT.equals(cmd.cm)) {
            //change stroke and color
            changePaintStyle(params);
        } else if (ProtocolConfigs.ADJUST.equals(cmd.cm)) {
            //do nothing, not implemented
        }
    }

    private void fillDoodlePoints(String[] params, Doodle doodle) {
        if (params == null || doodle == null || params.length < 1) {
            return;
        }
        int j = 0;
        float x = 0;
        float y = 0;
        try {
            for (int i = 1; i < params.length; i++) {
                j++;
                switch (j % 2) {
                    case 1:
                        x = Integer.parseInt(params[i]) / (float) ProtocolConfigs.VIRTUAL_WIDTH;
                        break;
                    case 0:
                        y = Integer.parseInt(params[i]) / (float) ProtocolConfigs.VIRTUAL_HEIGHT;
                        doodle.addControlPoint(x, y);
                        break;
                }
            }
        } catch (Exception e) {

        }
    }

    private Doodle findDoodleById(String id) {
        if (TextUtils.isEmpty(id) || mAllDoodles == null) {
            return null;
        }

        for (Doodle doodle : mAllDoodles) {
            if (id.equals(doodle.getDoodleId())) {
                return doodle;
            }
        }

        return null;
    }

    private void changePaintStyle(String[] params) {
        if (params == null || params.length == 0) {
            return;
        }

        try {
            if (params.length == 1) {
                mPaintStrokeWidth = Integer.parseInt(params[0]);
            } else if (params.length > 1) {
                mPaintStrokeWidth = Integer.parseInt(params[0]);
                mPaintColor = Utils.getColor(params[1], mPaintColor);
            }
        } catch (Exception e) {

        }
    }

    private boolean moveDoodle(String[] params, Doodle doodle, boolean absolute) {
        if (params == null || params.length == 0 || doodle == null) {
            return false;
        }

        try {
            int x = 0;
            int y = 0;
            if (!absolute) {
                if (params.length == 1) {
                    x = (Integer.parseInt(params[0]) * mBlackboardWidth) / ProtocolConfigs.VIRTUAL_WIDTH;
                } else if (params.length > 1) {
                    x = (Integer.parseInt(params[0]) * mBlackboardWidth) / ProtocolConfigs.VIRTUAL_WIDTH;
                    y = (Integer.parseInt(params[1]) * mBlackboardHeight) / ProtocolConfigs.VIRTUAL_HEIGHT;
                }
            }

            doodle.move(x, y);
            return true;
        } catch (Exception e) {

        }

        return false;
    }

    private boolean scaleDoodle(String[] params, Doodle doodle, boolean absolute) {
        if (params == null || params.length == 0 || doodle == null) {
            return false;
        }

        try {
            RectF rectF = doodle.getDoodleRect();
            int oldW = (int) (rectF.width() * ProtocolConfigs.VIRTUAL_WIDTH);
            int oldH = (int) (rectF.height() * ProtocolConfigs.VIRTUAL_HEIGHT);
            int w = oldW;
            int h = oldH;
            if (absolute) {
                if (params.length == 1) {
                    w = Integer.parseInt(params[0]);
                } else if (params.length > 1) {
                    w = Integer.parseInt(params[0]);
                    h = Integer.parseInt(params[1]);
                }
                float scale = w / (float) oldW;
                doodle.scale(scale);
            } else {
                float scale = Float.parseFloat(params[0]);
                doodle.scale(scale);
            }
        } catch (Exception e) {

        }

        return false;
    }

    private boolean rotateDoodle(String[] params, Doodle doodle, boolean absolute) {
        if (params == null || params.length == 0 || doodle == null) {
            return false;
        }

        try {
            float degree = Float.parseFloat(params[0]);
            if (absolute) {
                float oldDegree = doodle.getTotalDegree();
                doodle.rotate(degree - oldDegree);
            } else {
                doodle.scale(degree);
            }
        } catch (Exception e) {

        }

        return false;
    }
}

