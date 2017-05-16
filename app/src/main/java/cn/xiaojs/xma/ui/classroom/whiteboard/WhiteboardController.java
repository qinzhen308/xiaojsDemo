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
 * Date:2017/3/2
 * Desc:
 *
 * ======================================================================================== */

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionFail;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.bean.CommendLine;
import cn.xiaojs.xma.ui.classroom.socketio.Parser;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.GeometryShape;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.OnColorChangeListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.UndoRedoListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.WhiteboardConfigs;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.ColorPickerPop;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.EraserPop;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.GeometryPop;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.HandwritingPop;
import cn.xiaojs.xma.ui.classroom.whiteboard.setting.TextPop;
import cn.xiaojs.xma.ui.classroom.whiteboard.shape.TextWriting;
import cn.xiaojs.xma.ui.classroom.whiteboard.widget.CircleView;
import cn.xiaojs.xma.util.CacheUtil;

public class WhiteboardController implements EraserPop.EraserChangeListener,
        HandwritingPop.PaintChangeListener,
        GeometryPop.GeometryChangeListener,
        OnColorChangeListener,
        TextPop.TextChangeListener,
        UndoRedoListener,
        WhiteboardAdapter.OnWhiteboardListener {


    private static final int REQUEST_GALLERY_PERMISSION = 1000;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private final static int REQUEST_PERMISSION_CODE = 1024;
    /**
     * 白板注册重试次数
     */
    private static final int RETRY_COUNT = 3;

    private WhiteboardScrollerView mWhiteboardSv;
    //whiteboard adapter
    private WhiteboardAdapter mWhiteboardAdapter;

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

    private Whiteboard mOldWhiteboard;
    private View mPanel;

    private Context mContext;
    private int mScreenWidth;

    private int mGeometryId;
    private int mPanelWidth;

    private boolean mSavingWhiteboard = false;
    private AsyncTask mSaveTask;

    private Constants.User mUser = Constants.User.TEACHER;
    private int mAppType = Platform.AppType.UNKNOWN;

    private WhiteboardCollection mCurrWhiteboardColl;
    private WhiteboardCollection mOldWhiteboardColl;
    private Whiteboard mSyncWhiteboard;
    private WhiteboardLayout mWhiteboardLayout;
    private Whiteboard mCurrWhiteboard;
    private WhiteboardLayer mWhiteboardLayer;

    private int mCount;

    public WhiteboardController(Context context, View root, Constants.User client, int appType) {
        mContext = context;
        mUser = client;
        mAppType = appType;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;

        mWhiteboardSv = (WhiteboardScrollerView) root.findViewById(R.id.white_board_scrollview);
        mWhiteboardLayout = (WhiteboardLayout) root.findViewById(R.id.white_board_layout);

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

    /**
     * 初始白板数据
     */
    public void initWhiteboardData(Context context, WhiteboardCollection boardColl) {
        mWhiteboardAdapter = new WhiteboardAdapter(context);
        mWhiteboardSv.setOffscreenPageLimit(2);
        mWhiteboardSv.setAdapter(mWhiteboardAdapter);
        mWhiteboardAdapter.setOnWhiteboardListener(this);

        mWhiteboardAdapter.setData(boardColl, 0);
        //SocketManager.on(Event.BOARD, mOnBoard);
    }

    /**
     * 注册白板(第一期暂时不用)
     */
    public void registerDefaultBoard(final WhiteboardManager.WhiteboardAddListener listener) {
        WhiteboardManager.getInstance().addDefaultBoard(mContext, mUser, new WhiteboardManager.WhiteboardAddListener() {
            @Override
            public void onWhiteboardAdded(WhiteboardCollection boardCollection) {
                if (boardCollection == null) {
                    if (++mCount > RETRY_COUNT) {
                        //failed
                        if (listener != null) {
                            listener.onWhiteboardAdded(null);
                        }
                        return;
                    } else {
                        //retry
                        registerDefaultBoard(listener);
                    }
                } else {
                    //success
                    if (listener != null) {
                        listener.onWhiteboardAdded(boardCollection);
                    }

                    onSwitchWhiteboardCollection(boardCollection);
                    if (mUser == Constants.User.STUDENT) {
                        if (mAppType == Platform.AppType.MOBILE_ANDROID ||
                                mAppType == Platform.AppType.MOBILE_IOS ||
                                mAppType == Platform.AppType.TABLET_ANDROID ||
                                mAppType == Platform.AppType.TABLET_IOS) {
                            //mobile, tablet
                            boardCollection.setLive(true);
                            mCurrWhiteboard.setNeedBitmapPool(false);
                            mCurrWhiteboard.setLayer(boardCollection.getWhiteboardLayer().get(0));
                        } else {
                            mWhiteboardSv.setVisibility(View.GONE);
                            mCurrWhiteboard.setVisibility(View.GONE);
                            //onResumeVideo();

                            boardCollection.setLive(true);
                        }
                    }
                }
            }
        });
    }

    public void showWhiteboardLayout(Bitmap bmp) {
        if (mWhiteboardSv != null) {
            mWhiteboardSv.setVisibility(View.GONE);
        }

        if (mWhiteboardLayout != null) {
            mCurrWhiteboard = mWhiteboardLayout.getWhiteboard();
            mWhiteboardLayout.setVisibility(View.VISIBLE);
            mWhiteboardLayer = new WhiteboardLayer();
            mCurrWhiteboard.setLayer(mWhiteboardLayer);
            mCurrWhiteboard.setDoodleBackground(bmp);
            mCurrWhiteboard.setUndoRedoListener(this);
            mUndo.setEnabled(false);
            mRedo.setEnabled(false);
            onGeometryChange(GeometryShape.RECTANGLE);
            onColorChanged(WhiteboardConfigs.DEFAULT_PAINT_COLOR);
        }
    }

    public void hideWhiteboardLayout() {
        if (mWhiteboardLayout != null) {
            mWhiteboardLayout.setVisibility(View.GONE);
        }
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.recycleBackgroundBmp();
        }
    }

    public void setWhiteboardScrollerAdapter(WhiteboardAdapter adapter) {
        if (mWhiteboardSv != null) {
            mWhiteboardSv.setVisibility(View.VISIBLE);
            mUndo.setEnabled(false);
            mRedo.setEnabled(false);
            mWhiteboardSv.setAdapter(adapter);
            adapter.setOnWhiteboardListener(this);
        }

        if (mWhiteboardLayout != null) {
            mWhiteboardLayout.setVisibility(View.GONE);
        }
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

    /**
     * 进入文字编辑模式
     */
    private void enterText() {
        if (mCurrWhiteboard == null) {
            return;
        }

        if (mCurrWhiteboard.getMode() == Whiteboard.MODE_TEXT) {
            if (mTextSetting == null) {
                mTextSetting = new TextPop(mContext);
                mTextSetting.setTextChangeListener(this);
            }
            mTextSetting.show(mTextWriting, mPanelWidth);
        }
        enterMode(Whiteboard.MODE_TEXT);
    }

    /**
     * 进入手写涂鸦模式
     */
    private void enterHandWriting() {
        if (mCurrWhiteboard == null) {
            return;
        }

        if (mCurrWhiteboard.getMode() == Whiteboard.MODE_HAND_WRITING) {
            if (mPaintSetting == null) {
                mPaintSetting = new HandwritingPop(mContext);
                mPaintSetting.setOnDoodlePaintParamsListener(this);
            }
            mPaintSetting.show(mHandWriting, mPanelWidth);
        }
        enterMode(Whiteboard.MODE_HAND_WRITING);
    }

    /**
     * 进入橡皮擦模式
     */
    private void enterEraser() {
        if (mCurrWhiteboard == null) {
            return;
        }

        if (mEraserSetting == null) {
            mEraserSetting = new EraserPop(mContext);
            mEraserSetting.setOnEraserParamsListener(this);
        }
        mEraserSetting.show(mEraser, mPanelWidth);
        enterMode(Whiteboard.MODE_ERASER);
    }

    /**
     * 进入图形编辑模式
     */
    private void enterGeometry() {
        if (mCurrWhiteboard == null) {
            return;
        }

        if (mCurrWhiteboard.getMode() == Whiteboard.MODE_GEOMETRY) {
            if (mShapeSetting == null) {
                mShapeSetting = new GeometryPop(mContext);
                mShapeSetting.setOnShapeChangeListener(this);
                mShapeSetting.setShapeParams(mCurrWhiteboard.getPaintColor(), mGeometryId);
            }
            mShapeSetting.updateShapeSelectedState(mGeometryId);
            mShapeSetting.show(mGeoShape, mPanelWidth);
        }
        enterMode(Whiteboard.MODE_GEOMETRY);
    }

    /**
     * 进入颜色选择模式
     */
    private void enterColorPicker() {
        if (mCurrWhiteboard == null) {
            return;
        }

        if (mColorSetting == null) {
            mColorSetting = new ColorPickerPop(mContext);
            mColorSetting.setOnColorChangeListener(this);
        }
        mColorSetting.show(mColorPicker, mPanelWidth);
    }

    /**
     * 更新图形icon样式
     * @param geometryId
     */
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
    public void onColorChanged(int color) {
        if (mCurrWhiteboard != null) {
            mColorPicker.setPaintColor(color);
            mCurrWhiteboard.setPaintColor(color);
        }
    }

    @Override
    public void onEraserPaintSize(int size) {

    }

    @Override
    public void onClearDoodles() {
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.onClearWhiteboard();
        }
    }

    @Override
    public void onGeometryChange(int geometryID) {
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.setGeometryShapeId(geometryID);
            updateGeometryStyle(geometryID);
        }
    }

    @Override
    public void onPaintSize(float size) {
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.setPaintStrokeWidth(size);
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
        if (mCurrWhiteboard != null) {
            switch (orientation) {
                case TextWriting.TEXT_HORIZONTAL:
                    mTextWriting.setImageResource(R.drawable.wb_text_selector);
                    break;

                case TextWriting.TEXT_VERTICAL:
                    mTextWriting.setImageResource(R.drawable.wb_text_vertical_selector);
                    break;
            }
            mCurrWhiteboard.setTextOrientation(orientation);
        }
    }

    private void enterMode(int mode) {
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.switchMode(mode);
        }
    }

    /**
     * 设置白板
     */
    private void setWhiteboard(Whiteboard whiteboard) {
        mCurrWhiteboard = whiteboard;
        if (mOldWhiteboard != whiteboard) {
            if (mCurrWhiteboard != null) {
                mColorPicker.setPaintColor(mCurrWhiteboard.getPaintColor());
                mCurrWhiteboard.setUndoRedoListener(this);
                mCurrWhiteboard.setOnColorChangeListener(this);
                mCurrWhiteboard.onWhiteboardSelected();
                onGeometryChange(GeometryShape.RECTANGLE);
                onColorChanged(WhiteboardConfigs.DEFAULT_PAINT_COLOR);
                reset();
            }
        }

        setUndoRedoStyle();
        mOldWhiteboard = whiteboard;
    }

    /**
     * 重置白板工具栏样式
     */
    private void reset() {
        onGeometryChange(GeometryShape.RECTANGLE);
        onColorChanged(WhiteboardConfigs.DEFAULT_PAINT_COLOR);
        onTextOrientation(TextWriting.TEXT_HORIZONTAL);

        mSelection.setSelected(false);
        mHandWriting.setSelected(false);
        mGeoShape.setSelected(false);
        mTextWriting.setSelected(false);
        mEraser.setSelected(false);
    }

    @Override
    public void onWhiteboardSelected(Whiteboard whiteboard) {
        setWhiteboard(whiteboard);
        whiteboard.setNeedBitmapPool(true);
    }

    @Override
    public void onWhiteboardRemove(Whiteboard whiteboard) {
        setWhiteboard(null);
    }

    private SocketManager.EventListener mOnBoard = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            List<CommendLine> commendLineList = Parser.unpacking(args);
            if (mCurrWhiteboard != null) {
                mCurrWhiteboard.onReceive(commendLineList);
            }
        }
    };


    /**
     * 退出白板模式
     */
    public void exitWhiteboard() {
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.exit();
        }
    }

    /**
     * 设置撤销反撤销按钮样式
     */
    public void setUndoRedoStyle() {
        if (mCurrWhiteboard != null) {
            mUndo.setEnabled(mCurrWhiteboard.isCanUndo());
            mRedo.setEnabled(mCurrWhiteboard.isCanRedo());
        }
    }

    /**
     * 白板撤销
     */
    private void undo() {
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.undo();
        }
    }

    /**
     * 白板反撤销
     */
    private void redo() {
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.redo();
        }
    }

    @Override
    public void onUndoRedoStackChanged() {
        setUndoRedoStyle();
    }

    /**
     * 切换白板集合
     */
    public void onSwitchWhiteboardCollection(WhiteboardCollection wbColl) {
        if (wbColl != null && wbColl != mOldWhiteboardColl) {
            mCurrWhiteboardColl = wbColl;
            if (mUser == Constants.User.STUDENT) {
                if (wbColl.isLive()) {
                    if (isWebApp(mAppType)) {
                        //onResumeVideo();
                        mCurrWhiteboard.setVisibility(View.GONE);
                    } else {
                        mCurrWhiteboard.setVisibility(View.VISIBLE);
                    }
                    mWhiteboardSv.setVisibility(View.GONE);
                } else {
                    //onPauseVideo();
                    mCurrWhiteboard.setVisibility(View.GONE);
                    mWhiteboardSv.setVisibility(View.VISIBLE);
                    mWhiteboardAdapter.setData(wbColl);
                    mWhiteboardAdapter.notifyDataSetChanged();
                    int index = wbColl.getCurrIndex();
                    mWhiteboardSv.setAdapter(mWhiteboardAdapter);
                    mWhiteboardSv.setCurrentItem(index);
                }
            } else {
                mWhiteboardAdapter.setData(wbColl);
                mWhiteboardAdapter.notifyDataSetChanged();
                int index = wbColl.getCurrIndex();
                mWhiteboardSv.setAdapter(mWhiteboardAdapter);
                mWhiteboardSv.setCurrentItem(index);
            }

            mOldWhiteboardColl = wbColl;
        }
    }

    /**
     * 设置白板为主屏
     */
    public void setWhiteboardMainScreen() {
        ArrayList<WhiteboardCollection> collections = WhiteboardManager.getInstance().getWhiteboardCollectionList();
        if (mUser == Constants.User.TEACHER && collections != null) {
            if (!mCurrWhiteboardColl.isLive() && collections != null) {
                for (WhiteboardCollection coll : collections) {
                    coll.setLive(false);
                }

                mCurrWhiteboardColl.setLive(true);
            }
        }
    }

    public Bitmap getWhiteboardBitmap() {
        return mCurrWhiteboard != null ? mCurrWhiteboard.getWhiteboardBitmap() : null;
    }

    /**
     * 是否是同步的白板
     */
    public boolean isSyncWhiteboard() {
        return (mUser == Constants.User.STUDENT)
                && mCurrWhiteboardColl != null && mCurrWhiteboardColl.isLive();
    }

    /**
     * 是否是直播白板
     */
    public boolean isLiveWhiteboard() {
        return mCurrWhiteboardColl.isLive();
    }


    @PermissionSuccess(requestCode = REQUEST_GALLERY_PERMISSION)
    public void getGallerySuccess() {
        if (mSavingWhiteboard) {
            if (mCurrWhiteboard == null) {
                mSavingWhiteboard = false;
                return;
            }

            final Bitmap bmp = mCurrWhiteboard.getWhiteboardBitmap();
            final WhiteboardLayer layer = mCurrWhiteboard.getLayer();
            if (layer != null) {
                CacheUtil.saveWhiteboard(bmp, layer.getWhiteboardId());
                mSaveTask = new AsyncTask<Integer, Integer, String>() {

                    @Override
                    protected String doInBackground(Integer... params) {
                        return CacheUtil.saveWhiteboard(bmp, layer.getWhiteboardId());
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        mSavingWhiteboard = false;
                        String tips = mContext.getString(!TextUtils.isEmpty(result) ? R.string.save_white_board_succ :
                                R.string.save_white_board_fail);
                        Toast.makeText(mContext, tips, Toast.LENGTH_SHORT).show();
                    }
                }.execute(0);
            } else {
                mSavingWhiteboard = false;
            }
        }
    }

    @PermissionFail(requestCode = REQUEST_GALLERY_PERMISSION)
    public void getGalleryFailure() {

    }

    private boolean isWebApp(int app) {
        return app == Platform.AppType.WEB_CLASSROOM || app == Platform.AppType.MOBILE_WEB;
    }


    /**
     * 保存白板
     */
    public void saveWhiteboard() {
        if (mSavingWhiteboard) {
            return;
        }

        mSavingWhiteboard = true;
        //save to server
        //TODO

        /*if (mCurrWhiteboardColl != null) {
            if (mCurrWhiteboardColl.isLive() && mUser == Constants.User.STUDENT) {
                PermissionGen.needPermission(ClassroomActivity.this, REQUEST_GALLERY_PERMISSION, PERMISSIONS);
            } else {

            }
        }*/
    }

    public void release() {
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.release();
        }

        if (mSaveTask != null) {
            mSavingWhiteboard = false;
            mSaveTask.cancel(true);
        }
    }
}
