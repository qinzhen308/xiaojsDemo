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

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.bumptech.glide.util.ExceptionCatchingInputStream;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;
import cn.xiaojs.xma.model.socket.room.whiteboard.Command;
import cn.xiaojs.xma.model.socket.room.whiteboard.Ctx;
import cn.xiaojs.xma.model.socket.room.whiteboard.Drawing;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncData;
import cn.xiaojs.xma.model.socket.room.whiteboard.SyncLayer;
import cn.xiaojs.xma.model.socket.room.whiteboard.Viewport;
import cn.xiaojs.xma.ui.classroom.bean.Commend;
import cn.xiaojs.xma.ui.classroom.bean.CommendLine;
import cn.xiaojs.xma.ui.classroom.live.core.Config;
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
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.ArcLine;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Arrow;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Beeline;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Coordinate;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Dashline;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.HandWriting;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Hexagon;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.HistoryLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Oval;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Pentagon;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Rectangle;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.RectangularCoordinate;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Rhombus;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.SineCurve;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Square;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.SyncRemoteImgLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.SyncRemoteLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.TextWriting;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Trapezoid;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.Triangle;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.ColorUtil;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.PushPreviewBoardListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncCollector;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncDrawingListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncGenerator;
import cn.xiaojs.xma.ui.classroom2.core.SyncboardHelper;
import cn.xiaojs.xma.ui.widget.SpecialEditText;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.ArrayUtil;

public class Whiteboard extends View implements ViewGestureListener.ViewRectChangedListener,
        Receiver<SyncBoardReceive>, Sender<String> {
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
    public final static int MODE_SYNC_REMOTE = 6;
    public final static int MODE_SYNC_REMOTE_IMG = 7;
    public final static int MODE_HISTORY_LAYER = 8;
    public final static int MODE_HISTORY_IMG_LAYER = 9;

    public final static int BG_SCALE_TYPE_FIT_XY = 1;
    public final static int BG_SCALE_TYPE_FIT_CENTER = 2;

    private float DOODLE_CANVAS_RATIO = WhiteboardLayer.DOODLE_CANVAS_RATIO;

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
    private Bitmap mBackgroundBmp;
    private Uri mBackgroundUri;
    private Rect mSrcBackgroundRect;
    private Rect mDesBackgroundRect;
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
    private boolean sizeChanged = false;
    private boolean mNeedBitmapPool = true;

    private UndoRedoListener mUndoRedoListener;
    private OnColorChangeListener mColorChangeListener;
    private int mDoodleAction;
    private List<Integer> mUndoRecordIds;
    private List<Integer> mRedoRecordIds;

    private BitmapPool mDoodleBitmapPool;
    private int mBackgroundScaleType = BG_SCALE_TYPE_FIT_CENTER;

    SyncGenerator syncGenerator=new SyncGenerator();

    Viewport viewport=null;

    private String whiteBoardId;

    Bitmap bmControllDelete;
    Bitmap bmControllRotate;


    boolean readOnly;
    PushPreviewBoardListener pushPreviewBoardListener;


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

    public void setCanvasRatio(float ratio) {
        DOODLE_CANVAS_RATIO = ratio;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(readOnly)return false;
        mViewGestureListener.onTouchEvent(event);
        return true;
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
        //设置同步的viewport
        if(viewport==null){
            viewport=new Viewport();
            viewport.height=mBlackboardHeight;
            viewport.width=mBlackboardWidth;
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

            if (mBackgroundBmp != null) {
                mSrcBackgroundRect = new Rect(0, 0, mBackgroundBmp.getWidth(), mBackgroundBmp.getHeight());
                //mDesBackgroundRect = new Rect(0, 0, mBlackboardWidth, mBlackboardHeight);
                setBackgroundBmpDrawDestRect(mBackgroundBmp);
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
            loadBackground(Uri.parse(mLayer.getCoursePath()));
        }
    }

    public void setDoodleBackground(Bitmap bmp) {
        if (bmp == null) {
            return;
        }

        mSrcBackgroundRect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        //mDesBackgroundRect = new Rect(0, 0, mBlackboardWidth, mBlackboardHeight);
        mBackgroundBmp = bmp;
        setBackgroundBmpDrawDestRect(mBackgroundBmp);

        postInvalidate();
    }

    private void setBackgroundBmpDrawDestRect(Bitmap bitmap) {
        if (bitmap == null) {
            mDesBackgroundRect = new Rect(0, 0, mBlackboardWidth, mBlackboardHeight);
        } else {
            switch (mBackgroundScaleType) {
                case BG_SCALE_TYPE_FIT_CENTER:
                    float ratio = bitmap.getWidth() / (float)bitmap.getHeight();
                    int temp = (int) (ratio * mBlackboardHeight);
                    int w = 0;
                    int h = 0;
                    if (temp > mBlackboardWidth) {
                        // depend width
                        w = mBlackboardWidth;
                        h = (int) (mBlackboardWidth / ratio);
                    } else {
                        // depend height
                        h = mBlackboardHeight;
                        w = temp;
                    }

                    //int offsetX = (mBlackboardWidth - w) / 2;
                    //int offsetY = (mBlackboardHeight - h) / 2;
                    mDesBackgroundRect = new Rect(0, 0, w, h);
                    break;
                case BG_SCALE_TYPE_FIT_XY:
                    mDesBackgroundRect = new Rect(0, 0, mBlackboardWidth, mBlackboardHeight);
                    break;
            }
        }
    }

    /**
     * 图片背景scale Type
     * @param type
     */
    public void setScaleType(int type) {
        mBackgroundScaleType = type;
    }

    /**
     * 强制是白板居中
     */
    private void forceCenter() {
        if (mBlackboardWidth > 0 && mBlackboardHeight > 0 && mViewWidth > 0 && mViewHeight > 0) {
            mViewGestureListener.reset();
        }
    }

    /**
     * 白板选择回调
     */
    public void onWhiteboardSelected() {
        setGeometryShapeId(GeometryShape.RECTANGLE);
        switchMode(Whiteboard.MODE_NONE);
    }

    /**
     * 加载背景
     *
     * @param uri 图片的uri
     */
    private void loadBackground(final Uri uri) {
        mBackgroundUri = uri;
        //load bg img
        new AsyncTask<Void, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    int w = mBlackboardWidth;
                    int h = mBlackboardHeight;
                    if (w == 0 || h == 0) {
                        w = Target.SIZE_ORIGINAL;
                        h = Target.SIZE_ORIGINAL;
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
                    mSrcBackgroundRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
                    mBackgroundBmp = bitmap;
                    setBackgroundBmpDrawDestRect(mBackgroundBmp);
                    postInvalidate();
                }
            }
        }.execute();
    }

    private void initParams(Context context) {
        whiteBoardId= UUID.randomUUID().toString();
        mMeasureFinished = false;
        mContext = context;
        mDoodleBounds = new RectF();

        mDownPoint = new PointF();
        mLastPoint = new PointF();
        mDrawingPath = new Path();
        mWhiteboardParams = new WhiteboardParams();

        //init screen size
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        mScreenHeight = dm.heightPixels;

        mViewGestureListener = new ViewGestureListener(context, this, new TouchEventListener(), true){
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                onTouchPreview();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        };
        mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        mPreviousPoint = new PointF();
        WhiteboardConfigs.init(getContext());
        mSelector = new Selector(this);

        bmControllDelete= BitmapFactory.decodeResource(getResources(), R.drawable.ic_board_layer_controll_delete);
        bmControllRotate= BitmapFactory.decodeResource(getResources(), R.drawable.ic_board_layer_control_rotate);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //为了解决切换横竖屏，但图层doodle对象中的matrix都是根据横屏算的，所以变换后位置有问题，暂时弃置此方案
//        sizeChanged=true;
//        updateDoodleCanvas();

        if(XiaojsConfig.DEBUG){
            Logger.d("------qz-----onSizeChanged-----new("+w+","+h+")---old("+oldw+","+oldh+")");
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mInitLayer && mMeasureFinished) {
            mInitLayer = true;
            drawAllDoodlesCanvas();
        }/*else if(sizeChanged&&mMeasureFinished){
            sizeChanged = false;
            drawAllDoodlesCanvas();
        }*/

        RectF destF = mViewGestureListener.getDestRect();
        mDoodleBounds.set(destF.left, destF.top, destF.right, destF.bottom);
//        Logger.d("-------qz------whiteboard----ondraw----doodleBounds="+mDoodleBounds.toString());
//        Logger.d("-------qz------whiteboard----ondraw----mBlackboardRect="+mBlackboardRect.toString());

        canvas.save();
        //clip

        canvas.clipRect(mDoodleBounds);
//        canvas.clipRect(new RectF(mDoodleBounds.left,mDoodleBounds.top,mDoodleBounds.right,mDoodleBounds.bottom*boardCount));

        //1. draw background
        canvas.drawColor(BG_COLOR);

        //map matrix
        mDisplayMatrix.setRectToRect(mBlackboardRect, mDoodleBounds, Matrix.ScaleToFit.FILL);
        canvas.concat(mDisplayMatrix);

        //2. draw course image
        if (mBackgroundBmp != null) {
            int offsetX = 0;
            int offsetY = 0;
            switch (mBackgroundScaleType) {
                case BG_SCALE_TYPE_FIT_CENTER:
                    offsetX = (mBlackboardWidth - mDesBackgroundRect.width()) / 2;
                    offsetY = (mBlackboardHeight - mDesBackgroundRect.height()) / 2;
                    break;
                case BG_SCALE_TYPE_FIT_XY:

                    break;
            }
            canvas.translate(offsetX, offsetY);
            canvas.drawBitmap(mBackgroundBmp, mSrcBackgroundRect, mDesBackgroundRect, null);
            canvas.translate(-offsetX, offsetY);
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

        //每一屏的分割线
        /*Paint dividerPaint=new Paint();
        dividerPaint.setPathEffect(new DashPathEffect(new float[]{10,10,10},0));
        dividerPaint.setColor(Color.BLACK);
        for(int i=1;i<boardCount;i++){
            canvas.drawLine(mDoodleBounds.left,mDoodleBounds.bottom*i,mDoodleBounds.right,mDoodleBounds.bottom*i,dividerPaint);
        }*/

        //6. draw border(selected doodle or selector)
       /* if (!onViewChanged) {
            if (mCurrentMode == MODE_SELECTION) {
                drawDoodleBorder(canvas, mSelector);
            } else {
                canvas.save();
                canvas.concat(mDisplayMatrix);
                drawDoodleBorder(canvas, mDoodle);
                canvas.restore();
            }
        }*/
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
        onViewChanged = true;
        postInvalidate();
    }

    private int boardCount=1;

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

            boolean isDeleteSelector=false;
            if (mSelectionRectRegion == IntersectionHelper.LEFT_BOTTOM_CORNER) {
                switch (mCurrentMode) {
                    case MODE_SELECTION:
                        isDeleteSelector=true;
                        mSelector.setVisibility(View.GONE);
                        mSelector.setBorderVisible(false);
                        mDoodleAction = Action.DELETE_ACTION;
                        drawAllDoodlesCanvas();
                        postInvalidate();
                        break;
                    case MODE_TEXT:
                    case MODE_HAND_WRITING:
                    case MODE_GEOMETRY:
                        isDeleteSelector=false;
                        mDoodle.setVisibility(View.GONE);
                        mDoodleAction = Action.DELETE_ACTION;
                        drawAllDoodlesCanvas();
                        postInvalidate();
                        break;
                }
            }
            syncGenerator.onActionDown(mDoodleAction);
            if(mDoodleAction== Action.DELETE_ACTION){
                Doodle doodle=null;
                if( isDeleteSelector){
                    doodle=mSelector.getSelectedDoodle();
                }else {
                    doodle=mDoodle;
                }
                if(doodle instanceof SyncCollector){
                    syncGenerator.onActionMove((SyncCollector)doodle);
                }
            }

            //扩展白板
            if(mCurrentMode==MODE_GEOMETRY||mCurrentMode==MODE_HAND_WRITING||mCurrentMode==MODE_HISTORY_IMG_LAYER||mCurrentMode==MODE_HISTORY_LAYER){
                float[] downIn2=new float[]{mDownPoint.x,mDownPoint.y};
                Matrix matrix=new Matrix();
                mDisplayMatrix.invert(matrix);
                matrix.mapPoints(downIn2);
                Logger.d("-----qz-----add board count-----downIn2="+ Arrays.toString(downIn2));
                if(downIn2[1]/mBlackboardHeight>boardCount-0.3){
                    boardCount++;
                    Logger.d("-----qz-----add board count-----boardCount="+boardCount);
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
                                    if(mDoodle instanceof SyncCollector){
                                        syncGenerator.updateAction(mDoodleAction);
                                        syncGenerator.onActionMove((SyncCollector)mDoodle);
                                    }
                                    break;
                                case IntersectionHelper.RIGHT_BOTTOM_CORNER:
                                case IntersectionHelper.RECT_BODY:
                                case IntersectionHelper.LEFT_TOP_CORNER:
                                    //move
                                    mDoodle.move((x - mPreviousPoint.x), (y - mPreviousPoint.y));
                                    //move record
                                    mDoodleAction = Action.MOVE_ACTION;

                                    postInvalidate();
                                    if(mDoodle instanceof SyncCollector){
                                        syncGenerator.updateAction(mDoodleAction);
                                        syncGenerator.onActionMove((SyncCollector)mDoodle);
                                    }
                                    break;
                                case IntersectionHelper.TOP_EDGE:
                                case IntersectionHelper.RIGHT_EDGE:
                                case IntersectionHelper.BOTTOM_EDGE:
                                case IntersectionHelper.LEFT_EDGE:
                                    mDoodle.changeByEdge(mPreviousPoint.x, mPreviousPoint.y, x, y, mSelectionRectRegion);
                                    //change area record
                                    mDoodleAction = Action.CHANGE_AREA_ACTION;

                                    postInvalidate();
                                    if(mDoodle instanceof SyncCollector){
                                        syncGenerator.updateAction(mDoodleAction);
                                        syncGenerator.onActionMove((SyncCollector)mDoodle);
                                    }
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
                            if(mDoodle instanceof SyncCollector){
                                syncGenerator.updateAction(mDoodleAction);
                                syncGenerator.onActionMove((SyncCollector)mDoodle);
                            }
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
                                    syncGenerator.updateAction(mDoodleAction);
                                    syncGenerator.onActionMove(mSelector);
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
                                    syncGenerator.updateAction(mDoodleAction);
                                    syncGenerator.onActionMove(mSelector);
                                    drawAllDoodlesCanvas();
                                    postInvalidate();
                                    break;
                                default:
                                    //move
                                    mSelector.move((x - mPreviousPoint.x), (y - mPreviousPoint.y));
                                    //add move record
                                    mDoodleAction = Action.MOVE_ACTION;
                                    syncGenerator.updateAction(mDoodleAction);
                                    syncGenerator.onActionMove(mSelector);
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
            syncGenerator.onActionUp();
            if(pushPreviewBoardListener!=null){
                onTouchPreview();
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
                    case GeometryShape.SQUARE:
                        mDoodle = new Square(this, paint);
                        break;
                    case GeometryShape.ARC_LINE:
                        mDoodle = new ArcLine(this, paint);
                        break;
                    case GeometryShape.TRAPEZOID:
                        mDoodle = new Trapezoid(this, paint);
                        break;
                    case GeometryShape.PENTAGON:
                        mDoodle = new Pentagon(this, paint);
                        break;
                    case GeometryShape.HEXAGON:
                        mDoodle = new Hexagon(this, paint);
                        break;
                    case GeometryShape.SINE_CURVE:
                        mDoodle = new SineCurve(this, paint);
                        break;
                    case GeometryShape.ARROW:
                        mDoodle = new Arrow(this, paint);
                        break;
                    case GeometryShape.DASH_LINE:
                        mDoodle = new Dashline(this, paint);
                        break;
                    case GeometryShape.RHOMBUS:
                        mDoodle = new Rhombus(this, paint);
                        break;
                    case GeometryShape.COORDINATE:
                        mDoodle = new Coordinate(this, paint);
                        break;
                    case GeometryShape.RECTANGULAR_COORDINATE:
                        mDoodle = new RectangularCoordinate(this, paint);
                        break;
                    case GeometryShape.XYZ_COORDINATE:
//                        mDoodle = new Arrow(this, paint);
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
            case MODE_SYNC_REMOTE:
                mDoodle=new SyncRemoteLayer(this);
                break;
            case MODE_SYNC_REMOTE_IMG:
                mDoodle=new SyncRemoteImgLayer(this);
                break;
            case MODE_HISTORY_LAYER:
                mDoodle=new HistoryLayer(this,Doodle.STYLE_HISTORY_LAYER);
                break;
            case MODE_HISTORY_IMG_LAYER:
                mDoodle=new HistoryLayer(this,Doodle.STYLE_HISTORY_LAYER);
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

    public void drawAllDoodlesCanvas() {
        eraserAllDoodle();

        if (mAllDoodles != null) {
            for (Doodle d : mAllDoodles) {
                d.setDrawingMatrix(mDrawingMatrix);
                d.setDisplayMatrix(mDisplayMatrix);
                d.drawSelf(mDoodleCanvas);
            }
        }
    }

    private void updateDoodleCanvas() {
        if (mDoodleCanvas != null && mLayer != null) {
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

    private void createDoodleCanvas() {
        if (mDoodleCanvas == null) {
            int w = mBlackboardWidth;
            int h = mBlackboardHeight;


            mDoodleBitmap = mDoodleBitmapPool != null ? mDoodleBitmapPool.getBitmap(w, h) : null;
            if (mDoodleBitmap == null) {
                mDoodleBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
            }
            mDoodleCanvas = new Canvas(mDoodleBitmap);

            mBlackboardRect.set(0, 0, w, h);
            mDrawingMatrix.setRectToRect(new RectF(0, 0, 1, 1), mBlackboardRect, Matrix.ScaleToFit.FILL);
            mDisplayMatrix.setRectToRect(mBlackboardRect, mDoodleBounds, Matrix.ScaleToFit.FILL);
        }else if(mDoodleBitmap.getWidth()!=mBlackboardWidth||mDoodleBitmap.getHeight()!=mBlackboardHeight){
            int w = mBlackboardWidth;
            int h = mBlackboardHeight;


            mDoodleBitmap = mDoodleBitmapPool != null ? mDoodleBitmapPool.getBitmap(w, h) : null;
            if (mDoodleBitmap == null) {
                mDoodleBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_4444);
            }
            mDoodleCanvas = new Canvas(mDoodleBitmap);

            mBlackboardRect.set(0, 0, w, h);
            mDrawingMatrix.setRectToRect(new RectF(0, 0, 1, 1), mBlackboardRect, Matrix.ScaleToFit.FILL);
            mDisplayMatrix.setRectToRect(mBlackboardRect, mDoodleBounds, Matrix.ScaleToFit.FILL);
            mInitLayer=false;
        }

        if(mLayer != null){
            mLayer.setWidth(mBlackboardWidth);
            mLayer.setHeight(mBlackboardHeight);
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
                d.setRole(Doodle.STATE_EDIT);
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

        if(pushPreviewBoardListener!=null){
            onTouchPreview();
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
        if(pushPreviewBoardListener!=null){
            onTouchPreview();
        }
    }

    public void saveWhiteboard() {
        drawAllDoodlesCanvas();
        mDoodle = null;
        invalidate();
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
    }

    public void recycleBackgroundBmp() {
        if (mBackgroundBmp != null && !mBackgroundBmp.isRecycled()) {
            mBackgroundBmp.recycle();
            mBackgroundBmp = null;
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
                    doodle.setBorderRect(prevRecord.rect);
                    doodle.setTransformMatrix(prevRecord.mTransMatrix);
                    doodle.setBorderTransformMatrix(prevRecord.mBorderTransMatrix);
                    doodle.setTotalDegree(prevRecord.degree);
                    doodle.setTotalScaleX(prevRecord.scaleX);
                    doodle.setTotalScaleY(prevRecord.scaleY);
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
                if(pushPreviewBoardListener!=null){
                    onTouchPreview();
                }
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
                doodle.setBorderRect(record.rect);
                doodle.setTransformMatrix(record.mTransMatrix);
                doodle.setBorderTransformMatrix(record.mBorderTransMatrix);
                doodle.setTotalDegree(record.degree);
                doodle.setTotalScaleX(record.scaleX);
                doodle.setTotalScaleY(record.scaleY);
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
                if(pushPreviewBoardListener!=null){
                    onTouchPreview();
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
                /*cmd = Packer.getScaleAndRotateCmd(d, undo ? 1.f / actRd.scale : actRd.scale,
                        undo ? -actRd.degree : actRd.degree, whitTime);*/
                break;
            case Action.SCALE_ACTION:
//                cmd = Packer.getScaleCmd(d, undo ? 1.f / actRd.scale : actRd.scale, whitTime);
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
        if (mBackgroundBmp != null) {
            Bitmap bmp = Bitmap.createBitmap(mDoodleBitmap.getWidth(), mDoodleBitmap.getHeight(), Bitmap.Config.ARGB_4444);
            Canvas c = new Canvas(bmp);
            int offsetX = 0;
            int offsetY = 0;
            switch (mBackgroundScaleType) {
                case BG_SCALE_TYPE_FIT_CENTER:
                    offsetX = (mBlackboardWidth - mDesBackgroundRect.width()) / 2;
                    offsetY = (mBlackboardHeight - mDesBackgroundRect.height()) / 2;
                    break;
                case BG_SCALE_TYPE_FIT_XY:

                    break;
            }
            c.drawColor(BG_COLOR);
            c.save();
            c.translate(offsetX, offsetY);
            c.drawBitmap(mBackgroundBmp, mSrcBackgroundRect, mDesBackgroundRect, null);
            c.restore();
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
    public void onReceive(SyncBoardReceive data) {
        if ( (mLayer != null && mLayer.isCanReceive())&&data != null) {
            if(data.stg== Live.SyncStage.FINISH){
                handleReceiveLayerFinished(data);
            }else if(data.stg== Live.SyncStage.ONGOING){
                handleReceiveLayerGoing(data);
            }else if(data.stg== Live.SyncStage.BEGIN){
                handleReceiveLayerBegin(data);
            }
            postInvalidate();
        }
    }

    public void onInitReceive(ShareboardReceive data) {
        if ( (mLayer != null && mLayer.isCanReceive())&&data != null&&data.board!=null&&data.board.drawing!=null&&data.board.drawing.stylus!=null) {
            syncCreateLayers(data.board.drawing.stylus.layers);
            postInvalidate();
        }
    }

    private synchronized void handleReceiveLayerBegin(SyncBoardReceive data) {
        int event=data.evt;
        switch (event){
            case Live.SyncEvent.PEN:
                Paint paint=null;
                int color=Color.BLACK;
                try {
                    color=Color.parseColor(data.ctx.strokeStyle);
                }catch (Exception e) {
                    Logger.d(e);
                }
                paint = Utils.createPaint(color, data.ctx.lineWidth, Paint.Style.STROKE);
                Doodle d = buildDoodle(data.getId(), MODE_SYNC_REMOTE);
                d.setPaint(paint);
                d.setState(Doodle.STATE_EDIT);
                drawToDoodleCanvas();
                if (d != null) {
                    d.setState(Doodle.STATE_IDLE);
                }
                postInvalidate();
                break;
        }
    }


    private synchronized void handleReceiveLayerGoing(SyncBoardReceive data) {
        int event=data.evt;

        switch (event){
            case Live.SyncEvent.PEN:
                if(ArrayUtil.isEmpty(data.data))return;
                Doodle d=findDoodleById(data.getId());
                List<PointF> ps=d.getPoints();
                if(ps==null){
                    ps=new ArrayList<>();
                    ps.add(data.data.get(0).endPos);
                    ps.add(data.data.get(0).startPos);
                }else if(ps.size()==0){
                    ps.add(data.data.get(0).endPos);
                    ps.add(data.data.get(0).startPos);
                }else if(!ps.get(ps.size()-1).equals(data.data.get(0).startPos)){
                    ps.add(data.data.get(0).endPos);
                    ps.add(data.data.get(0).startPos);
                }else {
                    ps.add(data.data.get(0).startPos);
                }
                d.setControlPoints(ps);
                drawAllDoodlesCanvas();
                postInvalidate();
                break;
        }
    }

    private synchronized void handleReceiveLayerFinished(SyncBoardReceive data) {
        if(ArrayUtil.isEmpty(data.data)){
            return;
        }
        int mode = Whiteboard.MODE_SYNC_REMOTE;
        int event=data.evt;
        switch (event){
            //基本操作
            case Live.SyncEvent.SELECT:
                //包括移动
                syncChangeLayer(data.data.get(0).changedLayers);
                break;
            case Live.SyncEvent.UNDO:
                if(data.data.get(0).command!=null){
                    syncUndo(data.data.get(0).command);
                }
                break;
            case Live.SyncEvent.REDO:
                if(data.data.get(0).command!=null){
                    syncUndo(data.data.get(0).command);
//                    syncRedo(data.data.get(0).command);
                }
                break;
            case Live.SyncEvent.CLEAR:
                syncRemoveLayer(data.data);
                break;
            case Live.SyncEvent.ERASER:
                syncRemoveLayer(data.data);
                break;

            case Live.SyncEvent.CUT:
                //正常剪切和添加图片都是这个事件,返回数据不同
                if(data.data.size()>1||data.data.get(0).layer==null){//大于1，一定是剪切;等于1但layer字段是空，也是剪切
                    syncRemoveLayer(data.data);
                }else{//添加图片
                    mode=Whiteboard.MODE_SYNC_REMOTE_IMG;
                    syncCreateLayer(mode,data.data.get(0).layer);
                }

                break;
            case Live.SyncEvent.PASTE:
                syncCreateLayersForPaste(data.data);
                break;
            //图形同步
            case Live.SyncEvent.PEN:
                if(removeDoodleById(data.from)){
                    drawAllDoodlesCanvas();
                }
                syncCreateLayer(mode,data.data.get(0).layer);
                break;
            case Live.SyncEvent.TEXT:
                break;
            case Live.SyncEvent.IMAGE:
                break;
            case Live.SyncEvent.HANDSCALE:
                break;
            case Live.SyncEvent.GAP:
                break;
            default:
                //目前大于30的为图形
                if(event>=Live.SyncEvent.DASHEDLINE){
                    syncCreateLayer(mode,data.data.get(0).layer);
                }
                break;

        }
    }

    private synchronized void syncCreateLayer(int mode,SyncLayer layer){
        if(layer==null||layer.shape==null)return;
        Paint paint=null;
        paint = Utils.createPaint(ColorUtil.parseColor(layer.lineColor), layer.lineWidth, Paint.Style.STROKE);
        Doodle d = buildDoodle(layer.id, mode);
        d.setPaint(paint);
        d.setState(Doodle.STATE_EDIT);
        d.setControlPoints(layer.shape.data);
        if(mode==MODE_SYNC_REMOTE_IMG){
            mDoodle.setDrawingMatrix(mDrawingMatrix);
            mDoodle.setDisplayMatrix(mDisplayMatrix);
            ((SyncRemoteImgLayer)mDoodle).handleImgSource(layer.info.imgUrl,layer.angle);
        }
        drawToDoodleCanvas();
        if (d != null) {
            d.setState(Doodle.STATE_IDLE);
        }
    }

    private synchronized void syncCreateLayer(int mode,Paint paint,SyncLayer layer){
        if(layer==null||layer.shape==null)return;
        Doodle d = buildDoodle(layer.id, mode);
        d.setPaint(paint);
        d.setState(Doodle.STATE_EDIT);
        d.setControlPoints(layer.shape.data);
        if(mode==MODE_SYNC_REMOTE_IMG){
            mDoodle.setDrawingMatrix(mDrawingMatrix);
            mDoodle.setDisplayMatrix(mDisplayMatrix);
            ((SyncRemoteImgLayer)mDoodle).handleImgSource(layer.info.imgUrl,layer.angle);
        }
        drawToDoodleCanvas();
        if (d != null) {
            d.setState(Doodle.STATE_IDLE);
        }
    }

    private synchronized void syncCreateLayers(List<SyncLayer> layers){
        if(ArrayUtil.isEmpty(layers))return;
        int mode=-1;
        for(SyncLayer layer:layers){
            mode=Live.ShapeType.DRAW_IMAGE.equals(layer.shape.type)?MODE_SYNC_REMOTE_IMG:MODE_SYNC_REMOTE;
            Doodle d = buildDoodle(layer.id, mode);
            if (d != null) {
                int color=Color.BLACK;
                try {
                    color=Color.parseColor(layer.lineColor);
                }catch (Exception e){
                    Logger.d(e);
                }
                d.setPaint(Utils.createPaint(color, layer.lineWidth, Paint.Style.STROKE));
                d.setState(Doodle.STATE_EDIT);
                d.setControlPoints(layer.shape.data);
                if(mode==MODE_SYNC_REMOTE_IMG){
                    mDoodle.setDrawingMatrix(mDrawingMatrix);
                    mDoodle.setDisplayMatrix(mDisplayMatrix);
                    ((SyncRemoteImgLayer)mDoodle).handleImgSource(layer.info.imgUrl,layer.angle);
                }
                drawDoodle(mDoodleCanvas, d);
                d.setState(Doodle.STATE_IDLE);
            }
        }
        drawToDoodleCanvas();
    }

    private synchronized void syncCreateLayersForPaste(List<SyncData> layers){
        if(ArrayUtil.isEmpty(layers))return;
        int mode=-1;
        for(SyncData layer:layers){
            mode=Live.ShapeType.DRAW_IMAGE.equals(layer.paste_shape.type)?MODE_SYNC_REMOTE_IMG:MODE_SYNC_REMOTE;
            Doodle d = buildDoodle(layer.id, mode);
            if (d != null) {
                int color=Color.BLACK;
                try {
                    color=Color.parseColor(layer.paste_lineColor);
                }catch (Exception e){
                    Logger.d(e);
                }
                d.setPaint(Utils.createPaint(color, layer.paste_lineWidth, Paint.Style.STROKE));
                d.setState(Doodle.STATE_EDIT);
                d.setControlPoints(layer.paste_shape.data);
                if(mode==MODE_SYNC_REMOTE_IMG){
                    mDoodle.setDrawingMatrix(mDrawingMatrix);
                    mDoodle.setDisplayMatrix(mDisplayMatrix);
                    ((SyncRemoteImgLayer)mDoodle).handleImgSource(layer.paste_info.imgUrl,layer.paste_angle);
                }
                drawDoodle(mDoodleCanvas, d);
                d.setState(Doodle.STATE_IDLE);
            }
        }
        drawToDoodleCanvas();
    }

    private synchronized void syncRemoveLayer(List<SyncData> layers){
        boolean hasChange=false;
        for(SyncData layer:layers){
            Doodle d = findDoodleById(layer.id);
            if (d != null) {
                hasChange=true;
                d.setVisibility(View.GONE);
                mAllDoodles.remove(d);
            }
        }
        if(hasChange){
            drawAllDoodlesCanvas();
        }
    }

    private synchronized void syncRemoveLayers(List<SyncLayer> layers){
        boolean hasChange=false;
        for(SyncLayer layer:layers){
            Doodle d = findDoodleById(layer.id);
            if (d != null) {
                hasChange=true;
                d.setVisibility(View.GONE);
                mAllDoodles.remove(d);
            }
        }
        if(hasChange){
            drawAllDoodlesCanvas();
        }
    }

    private synchronized void syncChangeLayer(List<SyncLayer> layers){
        if(ArrayUtil.isEmpty(layers))return;
        boolean hasChange=false;
        for(SyncLayer layer:layers){
            if(layer==null)continue;
            Doodle d = findDoodleById(layer.id);
            if (d != null) {
                hasChange=true;
                d.setVisibility(View.GONE);
                mAllDoodles.remove(d);
                if(layer.lineWidth>0){
                    d.getPaint().setStrokeWidth(layer.lineWidth);
                }
                try {
                    int color=Color.parseColor(layer.lineColor);
                    d.getPaint().setColor(color);
                }catch (Exception e){
                    e.printStackTrace();
                }
                syncCreateLayer(d.getStyle()==Doodle.STYLE_SYNC_LAYER_IMG?MODE_SYNC_REMOTE_IMG:MODE_SYNC_REMOTE,d.getPaint(),layer);
            }
        }
        if(hasChange){
            drawAllDoodlesCanvas();
        }
    }

    private synchronized void syncUndo(Command command){
        if(ArrayUtil.isEmpty(command.p)){
            return;
        }
        syncRemoveLayers(command.p);
        if(command.f==0){
            syncCreateLayers(command.p);
        }
    }

    private synchronized void syncRedo(Command command){
        if(ArrayUtil.isEmpty(command.p)){
            return;
        }
        if(command.f==0){
            if("create".equals(command.m)){
                syncCreateLayers(command.p);
            }else {
                syncRemoveLayers(command.p);
            }
        }else {
            syncChangeLayer(command.p);
        }
    }

    /**
     * 将数据解析为历史图层对象
     * @param layers
     */
    private void parseLayers(List<SyncLayer> layers){
        if(ArrayUtil.isEmpty(layers))return;
        int mode=-1;
        for(SyncLayer layer:layers){
            mode=Live.ShapeType.DRAW_IMAGE.equals(layer.shape.type)?MODE_HISTORY_IMG_LAYER:MODE_HISTORY_LAYER;
            Doodle d = buildDoodle(layer.id, mode);
            if (d != null) {
                int color=Color.BLACK;
                try {
                    color=Color.parseColor(layer.lineColor);
                }catch (Exception e){
                    Logger.d(e);
                }
                d.setPaint(Utils.createPaint(color, layer.lineWidth, Paint.Style.STROKE));
                d.setState(Doodle.STATE_EDIT);
                ((HistoryLayer)d).setLayerSrc(layer);
                if(mode==MODE_HISTORY_IMG_LAYER){
                    mDoodle.setDrawingMatrix(mDrawingMatrix);
                    mDoodle.setDisplayMatrix(mDisplayMatrix);
                }
                drawDoodle(mDoodleCanvas, d);
                d.setState(Doodle.STATE_IDLE);
            }
        }
        drawToDoodleCanvas();
    }


    public void setLayers(Drawing drawing){
        if(drawing==null)return;
        if(drawing.stylus==null)return;
        SyncboardHelper.handleLayerData(drawing);
        parseLayers(drawing.stylus.layers);
    }


        @Override
    public void onSend(String cmd) {
        if (!TextUtils.isEmpty(cmd) && (mLayer != null && mLayer.isCanSend())) {
            Log.i("aaa", "************ send=" + cmd);
            //to do something
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

    private boolean removeDoodleById(String id) {
        if (TextUtils.isEmpty(id) || mAllDoodles == null) {
            return false;
        }
        Iterator<Doodle> iterator =mAllDoodles.iterator();
        while (iterator.hasNext()) {
            if (id.equals(iterator.next().getDoodleId())) {
                iterator.remove();
                return true;
            }
        }

        return false;
    }


    public void setSyncDrawingListener(SyncDrawingListener listener){
        syncGenerator.setListener(listener);
    }

    public Viewport getViewport(){
        return viewport;
    }

    public void setViewport(Viewport viewport){
        this.viewport=viewport;
    }

    public String getWhiteBoardId(){
        return whiteBoardId;
    }

    public void setWhiteBoardId(String id){
        whiteBoardId=id;
    }


    public Matrix getDrawingMatrix(){
        return mDrawingMatrix;
    }

    public Bitmap getControllDeleteBm() {
        return bmControllDelete;
    }

    public Bitmap getControllRotateBm() {
        return bmControllRotate;
    }

    public void setNeedReadOnly(boolean readOnly) {
        this.readOnly=readOnly;
    }

    public synchronized Bitmap getPreviewBitmap(){
        setDrawingCacheEnabled(true);
        Bitmap bm=getDrawingCache();
        if(bm!=null){
            bm=Bitmap.createBitmap(bm);
            setDrawingCacheEnabled(false);
            return bm;
        }
        setDrawingCacheEnabled(false);
        return null;
    }


    /**
     * 触发推白板
     */
    public void onTouchPreview(){

        if(previewQueueState==1){
            handler.sendEmptyMessage(1);
        }else if(previewQueueState==2){
            handler.removeMessages(2);
            handler.removeMessages(3);
            handler.sendEmptyMessageDelayed(2,1000);
        }else {
        }
        handler.sendEmptyMessageDelayed(3,1000);
    }

    int previewQueueState=1;
    Handler handler=new Handler(){
        long time;
        @Override
        public void handleMessage(Message msg) {
            if(msg.what!=3){
                Bitmap preview=getPreviewBitmap();
                Logger.d("-----qz-----push preivew="+preview);
                if(preview!=null&&pushPreviewBoardListener!=null){
                    pushPreviewBoardListener.onPush(preview);
                }
                long time2=System.currentTimeMillis();
                Logger.d("-----qz-----push time="+(time2-time));
                time=time2;
            }
            Logger.d("-----qz-----push what="+msg.what);
            if(msg.what==1){
                previewQueueState=2;
            }else if(msg.what==2){
                previewQueueState=1;
            }else if(msg.what==3){
                previewQueueState=1;
            }

        }
    };


    public void setPushPreviewBoardListener(PushPreviewBoardListener pushPreviewBoardListener){
        this.pushPreviewBoardListener=pushPreviewBoardListener;
    }

    public void reset(){
        clearWhiteboard();
        syncGenerator=new SyncGenerator();
        mViewGestureListener.reset();
    }

}

