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
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;
import cn.xiaojs.xma.model.socket.room.whiteboard.Drawing;
import cn.xiaojs.xma.model.socket.room.whiteboard.Viewport;
import cn.xiaojs.xma.ui.classroom.bean.CommendLine;
import cn.xiaojs.xma.ui.classroom.socketio.Parser;
import cn.xiaojs.xma.ui.classroom.socketio.Receiver;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.whiteboard.core.Doodle;
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
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.PushPreviewBoardListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncDrawingListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.widget.CircleView;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.CacheUtil;

public class WhiteboardController implements EraserPop.EraserChangeListener,
        HandwritingPop.PaintChangeListener,
        GeometryPop.GeometryChangeListener,
        OnColorChangeListener,
        TextPop.TextChangeListener,
        UndoRedoListener,
        WhiteboardAdapter.OnWhiteboardListener,
        Receiver<SyncBoardReceive>{


    private static final int REQUEST_GALLERY_PERMISSION = 1000;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private final static int REQUEST_PERMISSION_CODE = 1024;

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

    private CTLConstant.UserIdentity mUser = CTLConstant.UserIdentity.LEAD;
    private int mAppType = Platform.AppType.UNKNOWN;

    private WhiteboardCollection mCurrWhiteboardColl;
    private WhiteboardCollection mOldWhiteboardColl;
    private WhiteboardLayout mWhiteboardLayout;
    private Whiteboard mCurrWhiteboard;
    private WhiteboardLayer mWhiteboardLayer;

    private int mCount;

    public WhiteboardController(Context context, View root, CTLConstant.UserIdentity client, int appType) {
        mContext = context;
        mUser = client;
        mAppType = appType;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;

        mWhiteboardSv = (WhiteboardScrollerView) root.findViewById(R.id.white_board_scrollview);
        mWhiteboardLayout = (WhiteboardLayout) root.findViewById(R.id.white_board_layout);

        mPanel = root.findViewById(R.id.white_board_panel);
        mPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为了事件不穿透到白板上
            }
        });
        mSelection = (ImageView) root.findViewById(R.id.select_btn);
        mHandWriting = (ImageView) root.findViewById(R.id.handwriting_btn);
        mGeoShape = (ImageView) root.findViewById(R.id.shape_btn);
//        mTextWriting = (ImageView) root.findViewById(R.id.text_btn);
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
     * 单张白板
     */
    public void showWhiteboardLayout(Bitmap bmp, float boardRatio) {
        if (mWhiteboardSv != null) {
            mWhiteboardSv.setVisibility(View.GONE);
        }

        if (mWhiteboardLayout != null) {
            mCurrWhiteboard = mWhiteboardLayout.getWhiteboard();
            mWhiteboardLayout.setVisibility(View.VISIBLE);
            mWhiteboardLayer = new WhiteboardLayer();
            mCurrWhiteboard.setLayer(mWhiteboardLayer);
            mCurrWhiteboard.setDoodleBackground(bmp);
            mCurrWhiteboard.setCanvasRatio(boardRatio);
            mCurrWhiteboard.setUndoRedoListener(this);
            mUndo.setEnabled(false);
            mRedo.setEnabled(false);
            onGeometryChange(GeometryShape.RECTANGLE);
            onColorChanged(WhiteboardConfigs.DEFAULT_PAINT_COLOR);
        }
        handlePanelItemClick(mHandWriting);
    }

    public void replaceNewWhiteboardLayout(Bitmap bmp, float boardRatio) {
        if (mWhiteboardSv != null) {
            mWhiteboardSv.setVisibility(View.GONE);
        }

        if (mWhiteboardLayout != null) {
            mCurrWhiteboard = mWhiteboardLayout.getWhiteboard();
            mWhiteboardLayout.setVisibility(View.VISIBLE);
            mWhiteboardLayer = new WhiteboardLayer();
            mCurrWhiteboard.setLayer(mWhiteboardLayer);
            mCurrWhiteboard.setDoodleBackground(bmp);
            mCurrWhiteboard.setCanvasRatio(boardRatio);
            mCurrWhiteboard.setUndoRedoListener(this);
            mUndo.setEnabled(false);
            mRedo.setEnabled(false);
            onGeometryChange(GeometryShape.RECTANGLE);
            onColorChanged(WhiteboardConfigs.DEFAULT_PAINT_COLOR);
            mCurrWhiteboard.reset();
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

    /**
     * 可左右滑动的白板
     */
    public void showWhiteboardLayout(WhiteboardAdapter adapter) {
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

    public void setWhiteboardScrollMode(int mode) {
        if (mWhiteboardSv != null) {
            mWhiteboardSv.setMode(mode);
        }
    }

    public void handlePanelItemClick(View v) {
        if (v.getId() == R.id.color_picker_btn) {
            enterColorPicker();
            return;
        }

        int id=v.getId();
        if(!(id==R.id.undo||id==R.id.redo||id==R.id.eraser_btn)){
            mSelection.setSelected(false);
            mHandWriting.setSelected(false);
            mGeoShape.setSelected(false);
//        mTextWriting.setSelected(false);
            mEraser.setSelected(false);
            //mColorPicker.setSelected(false);
            v.setSelected(true);
        }


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

        final CommonDialog clearDialog=new CommonDialog(mContext);
        clearDialog.setDesc(R.string.board_clear_all_tip);
        clearDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                onClearDoodles();
                clearDialog.dismiss();
            }
        });
        clearDialog.show();
    }
   /* private void enterEraser() {
        if (mCurrWhiteboard == null) {
            return;
        }

        if (mEraserSetting == null) {
            mEraserSetting = new EraserPop(mContext);
            mEraserSetting.setOnEraserParamsListener(this);
        }
        mEraserSetting.show(mEraser, mPanelWidth);
        enterMode(Whiteboard.MODE_ERASER);
    }*/


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
     */
    private void updateGeometryStyle(int geometryId) {
        switch (geometryId) {
            case GeometryShape.ARROW:
                mGeoShape.setImageResource(R.drawable.ic_wb_arrow);
                break;

            case GeometryShape.DOUBLE_ARROW:
                break;

            case GeometryShape.RECTANGLE:
                mGeoShape.setImageResource(R.drawable.ic_wb_rectangle);
                break;

            case GeometryShape.BEELINE:
                mGeoShape.setImageResource(R.drawable.ic_wb_beeline);
                break;

            case GeometryShape.OVAL:
                mGeoShape.setImageResource(R.drawable.ic_wb_oval);
                break;

            case GeometryShape.TRIANGLE:
                mGeoShape.setImageResource(R.drawable.ic_wb_triangle);
                break;
            /*case GeometryShape.SQUARE:
                mGeoShape.setImageResource(R.drawable.ic_wb_arrow);
                break;*/
            case GeometryShape.ARC_LINE:
                mGeoShape.setImageResource(R.drawable.ic_wb_arc);
                break;
            case GeometryShape.TRAPEZOID:
                mGeoShape.setImageResource(R.drawable.ic_wb_trapezoid);
                break;
            case GeometryShape.PENTAGON:
                mGeoShape.setImageResource(R.drawable.ic_wb_pentagon);
                break;
            case GeometryShape.HEXAGON:
                mGeoShape.setImageResource(R.drawable.ic_wb_hexgon);
                break;
            case GeometryShape.SINE_CURVE:
                mGeoShape.setImageResource(R.drawable.ic_wb_sinline);
                break;
            case GeometryShape.DASH_LINE:
                mGeoShape.setImageResource(R.drawable.ic_wb_dashline);
                break;
            case GeometryShape.RHOMBUS:
                mGeoShape.setImageResource(R.drawable.ic_wb_diamond);
                break;
            case GeometryShape.COORDINATE:
                mGeoShape.setImageResource(R.drawable.ic_wb_coordinate);
                break;
            case GeometryShape.RECTANGULAR_COORDINATE:
                mGeoShape.setImageResource(R.drawable.ic_wb_rectangle_coordinates);
                break;
            /*case GeometryShape.XYZ_COORDINATE:
                mGeoShape.setImageResource(R.drawable.ic_scale_pressed);
                break;*/
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
//        onTextOrientation(TextWriting.TEXT_HORIZONTAL);

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
//                mCurrWhiteboard.onReceive(commendLineList);
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

    /**
     * 是否有编辑
     * @return
     */
    public boolean hasEdit() {
        if (mCurrWhiteboard != null) {
            return mCurrWhiteboard.isCanUndo() || mCurrWhiteboard.isCanRedo();
        }

        return false;
    }

    /**
     * 保存编辑
     */
    public void saveEdit() {
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.saveWhiteboard();
        }
    }

    /**
     * 丢弃所有编辑的
     */
    public void abandonEdit() {
        onClearDoodles();
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
            if (mUser == CTLConstant.UserIdentity.STUDENT) {
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

    public Bitmap getWhiteboardBitmap() {
        return mCurrWhiteboard != null ? mCurrWhiteboard.getWhiteboardBitmap() : null;
    }

    /**
     * 是否是同步的白板
     */
    public boolean isSyncWhiteboard() {
        return (mUser == CTLConstant.UserIdentity.STUDENT)
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

    public void release() {
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.release();
        }

        if (mSaveTask != null) {
            mSavingWhiteboard = false;
            mSaveTask.cancel(true);
        }
    }

    public void setCanReceive(boolean needReceive){
        mWhiteboardLayer.setCanReceive(needReceive);
    }
    public void setCanSend(boolean needSend){
        mWhiteboardLayer.setCanSend(needSend);
    }

    public void setWhiteBoardId(String id){
        mCurrWhiteboard.setWhiteBoardId(id);
    }


    @Override
    public void onReceive(SyncBoardReceive receive) {
        mCurrWhiteboard.onReceive(receive);

    }

    public void syncBoardLayerSet(ShareboardReceive layerSet) {
        mCurrWhiteboard.onInitReceive(layerSet);
    }


    public void setSyncDrawingListener(SyncDrawingListener listener) {
        mCurrWhiteboard.setSyncDrawingListener(listener);
    }

    public void setWhiteBoardReadOnly(boolean readOnly){
        mCurrWhiteboard.setNeedReadOnly(readOnly);
        mPanel.setVisibility(View.GONE);
        mPanel.setVisibility(View.VISIBLE);
    }

    public void setPanelShow(boolean isShown){
        if(isShown){
            mPanel.setVisibility(View.VISIBLE);
        }else {
            mPanel.setVisibility(View.GONE);
        }
    }

    public Bitmap getPreviewWhiteBoard(){
        return mCurrWhiteboard.getPreviewBitmap();
    }

    public void setPushPreviewBoardListener(PushPreviewBoardListener pushPreviewBoardListener){
        mCurrWhiteboard.setPushPreviewBoardListener(pushPreviewBoardListener);
    }

    public void setBoardLayerSet(Drawing drawing) {
        mCurrWhiteboard.setLayers(drawing);
    }

    public ArrayList<Doodle> getDoodles(){
        return mCurrWhiteboard.getAllDoodles();
    }

    public void setViewport(Viewport viewport){
        mCurrWhiteboard.setViewport(viewport);
    }
    public void setViewport(int width,int height){
        Viewport viewport=new Viewport();
        viewport.height=height;
        viewport.width=width;
        mCurrWhiteboard.setViewport(viewport);
    }
}
