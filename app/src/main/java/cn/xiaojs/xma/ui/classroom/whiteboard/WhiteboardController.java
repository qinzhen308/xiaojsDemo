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

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionFail;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.ui.classroom.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.Constants;
import cn.xiaojs.xma.ui.classroom.WhiteboardCollection;
import cn.xiaojs.xma.ui.classroom.socketio.CommendLine;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
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
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class WhiteboardController implements
        EraserPop.EraserChangeListener,
        HandwritingPop.PaintChangeListener,
        GeometryPop.GeometryChangeListener,
        OnColorChangeListener,
        TextPop.TextChangeListener,
        UndoRedoListener,
        WhiteboardAdapter.OnWhiteboardListener {

    private static final int REQUEST_GALLERY_PERMISSION = 1000;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

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

    private Whiteboard mWhiteboard;
    private Whiteboard mOldWhiteboard;
    private View mPanel;

    private Context mContext;
    private int mScreenWidth;

    private int mGeometryId;
    private int mPanelWidth;

    private boolean mSavingWhiteboard = false;
    private AsyncTask mSaveTask;

    private Constants.User mUser = Constants.User.TEACHER;

    private ArrayList<WhiteboardCollection> mWhiteboardCollectionList;
    private WhiteboardCollection mCurrWhiteboardColl;

    private Whiteboard mSyncWhiteboard;
    private Whiteboard mCurrWhiteboard;
    private String mWhiteboardSuffix;

    private Socket mSocket;
    private Handler mHandler;
    private ReceiveRunnable mSyncRunnable;


    public WhiteboardController(Context context, View root, Constants.User client) {
        mContext = context;
        mUser = client;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mWhiteboardSuffix = context.getString(R.string.white_board);

        mWhiteboardSv = (WhiteboardScrollerView) root.findViewById(R.id.white_board_scrollview);
        mSyncWhiteboard = (Whiteboard) root.findViewById(R.id.stu_receive_wb);
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

        mSocket = SocketManager.getSocket();
        mSocket.on(Event.BOARD, mOnBoard);
        mHandler = new Handler();

        initWhiteboardData(context);
    }

    private void initWhiteboardData(Context context) {
        mWhiteboardCollectionList = new ArrayList<WhiteboardCollection>();
        mWhiteboardAdapter = new WhiteboardAdapter(context);

        WhiteboardCollection whiteboardCollection = null;
        if (mUser == Constants.User.STUDENT) {
            whiteboardCollection = new WhiteboardCollection();
            whiteboardCollection.setLive(true);
            WhiteboardLayer layer = new WhiteboardLayer();
            layer.setCanSend(false);
            layer.setCanReceive(true);
            whiteboardCollection.addWhiteboardLayer(layer);
            mSyncWhiteboard.setNeedBitmapPool(false);
            mSyncWhiteboard.setLayer(layer);
        } else {
            whiteboardCollection = new WhiteboardCollection();
            WhiteboardLayer layer = new WhiteboardLayer();
            layer.setCanSend(true);
            layer.setCanReceive(false);
            whiteboardCollection.addWhiteboardLayer(layer);
        }

        mWhiteboardSv.setOffscreenPageLimit(2);
        mWhiteboardSv.setAdapter(mWhiteboardAdapter);
        mWhiteboardAdapter.setOnWhiteboardListener(this);

        onSwitchWhiteboardCollection(whiteboardCollection);
        addToWhiteboardCollectionList(whiteboardCollection);
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
    public void onColorChanged(int color) {
        if (mWhiteboard != null) {
            mColorPicker.setPaintColor(color);
            mWhiteboard.setPaintColor(color);
        }
    }

    @Override
    public void onEraserPaintSize(int size) {

    }

    @Override
    public void onClearDoodles() {
        if (mWhiteboard != null) {
            mWhiteboard.onClearWhiteboard();
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

    /**
     * 释放资源
     */
    public void release() {
        if (mWhiteboard != null) {
            mWhiteboard.release();
        }

        if (mSyncWhiteboard != null) {
            mSyncWhiteboard.release();
        }

        //recycle handler
        if (mHandler != null) {
            mHandler.removeCallbacks(mSyncRunnable);
            mHandler = null;
            mSyncRunnable = null;
        }

        if (mSaveTask != null) {
            mSavingWhiteboard = false;
            mSaveTask.cancel(true);
        }
    }

    /**
     * 设置白板
     */
    private void setWhiteboard(Whiteboard whiteboard) {
        mCurrWhiteboard = whiteboard;
        if (mOldWhiteboard != whiteboard) {
            mWhiteboard = whiteboard;
            if (mWhiteboard != null) {
                mColorPicker.setPaintColor(mWhiteboard.getPaintColor());
                mWhiteboard.setUndoRedoListener(this);
                mWhiteboard.setOnColorChangeListener(this);
                mWhiteboard.onWhiteboardSelected();

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

    /**
     * 退出白板模式
     */
    public void exitWhiteboard() {
        if (mWhiteboard != null) {
            mWhiteboard.exit();
        }
    }

    /**
     * 设置撤销反撤销按钮样式
     */
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

    /**
     * 添加到白板集合
     */
    public int addToWhiteboardCollectionList(WhiteboardCollection collection) {
        if (collection != null) {
            if (TextUtils.isEmpty(collection.getName())) {
                int count = 0;
                for (WhiteboardCollection coll : mWhiteboardCollectionList) {
                    if (coll.isDefaultWhiteboard()) {
                        count++;
                    }
                }
                String name = mWhiteboardSuffix + "_" + (count + 1);
                collection.setName(name);
            }
            mWhiteboardCollectionList.add(collection);
        }

        return mWhiteboardCollectionList != null ? mWhiteboardCollectionList.size() : 0;
    }

    /**
     * 获取所有的白板集
     */
    public ArrayList<WhiteboardCollection> getWhiteboardCollectionList() {
        return mWhiteboardCollectionList;
    }

    /**
     * 切换白板集合
     */
    public void onSwitchWhiteboardCollection(WhiteboardCollection wbColl) {
        if (wbColl != null) {
            mCurrWhiteboardColl = wbColl;
            if (mUser == Constants.User.STUDENT) {
                if (wbColl.isLive()) {
                    mSyncWhiteboard.setVisibility(View.VISIBLE);
                    mWhiteboardSv.setVisibility(View.GONE);
                    mCurrWhiteboard = mSyncWhiteboard;
                } else {
                    mSyncWhiteboard.setVisibility(View.GONE);
                    mWhiteboardSv.setVisibility(View.VISIBLE);
                    mWhiteboardAdapter.setData(wbColl.getWhiteboardLayer());
                    mWhiteboardAdapter.notifyDataSetChanged();
                    mWhiteboardSv.setAdapter(mWhiteboardAdapter);
                }
            } else {
                mWhiteboardAdapter.setData(wbColl.getWhiteboardLayer());
                mWhiteboardAdapter.notifyDataSetChanged();
                mWhiteboardSv.setAdapter(mWhiteboardAdapter);
            }
        }
    }

    /**
     * 设置白板为主屏
     */
    public void setWhiteboardMainScreen() {
        if (mUser == Constants.User.TEACHER) {
            if (!mCurrWhiteboardColl.isLive() && mWhiteboardCollectionList != null) {
                for (WhiteboardCollection coll : mWhiteboardCollectionList) {
                    coll.setLive(false);
                }

                mCurrWhiteboardColl.setLive(true);
            }
        }
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

    @Override
    public void onWhiteboardSelected(Whiteboard whiteboard) {
        setWhiteboard(whiteboard);
        if (mContext instanceof ClassroomActivity) {
            ((ClassroomActivity) mContext).onWhiteboardSelected(whiteboard);
        }
    }

    @Override
    public void onWhiteboardRemove(Whiteboard whiteboard) {
        setWhiteboard(null);
        if (mContext instanceof ClassroomActivity) {
            ((ClassroomActivity) mContext).onWhiteboardRemove(whiteboard);
        }
    }

    private Emitter.Listener mOnBoard = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            List<CommendLine> commendLineList = Parser.unpacking(args);
            mHandler.post(new ReceiveRunnable(commendLineList));
        }
    };

    private class ReceiveRunnable implements Runnable {
        private List<CommendLine> mCommendList;

        public ReceiveRunnable(List<CommendLine> list) {
            mCommendList = list;
        }

        public void setData(List<CommendLine> list) {
            mCommendList = list;
        }

        @Override
        public void run() {
            if (mSyncWhiteboard != null) {
                mSyncWhiteboard.onReceive(mCommendList);
            }
        }
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
}
