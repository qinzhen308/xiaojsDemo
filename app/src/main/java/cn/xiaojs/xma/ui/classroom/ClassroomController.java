package cn.xiaojs.xma.ui.classroom;
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
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionFail;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.ui.classroom.bean.MediaFeedback;
import cn.xiaojs.xma.ui.classroom.bean.OpenMediaNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.socketio.CommendLine;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.Parser;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.whiteboard.ShareDoodlePopWindow;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardAdapter;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardCollection;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardLayout;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardManager;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardScrollerView;
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
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.CacheUtil;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ClassroomController implements
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

    private final static int REQUEST_PERMISSION_CODE = 1024;
    /**
     * 白板注册重试次数
     */
    private static final int RETRY_COUNT = 3;

    private WhiteboardScrollerView mWhiteboardSv;
    //whiteboard adapter
    private WhiteboardAdapter mWhiteboardAdapter;

    //学生端直播view
    private PlayerTextureView mLiveVideo;
    //学生端在推流的时候预览自己的view
    private LiveRecordView mStuPublishVideo;
    //老师端推流的view
    private LiveRecordView mPublishVideo;
    //老师端播放的学生流的view
    private PlayerTextureView mPlayStuView;

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

    private Socket mSocket;
    private Handler mHandler;
    private ReceiveRunnable mSyncRunnable;
    private int mCount;
    private boolean mInitPublishVideo = false;
    private String mPublishUrl;
    private Bitmap mVideoFrameBitmap;
    private ShareDoodlePopWindow mSharePopWindow;
    private CommonDialog mAgreeOpenCamera;

    public ClassroomController(Context context, View root, Constants.User client, int appType) {
        mContext = context;
        mUser = client;
        mAppType = appType;
        mScreenWidth = context.getResources().getDisplayMetrics().widthPixels;
        mSocket = SocketManager.getSocket();

        mLiveVideo = (PlayerTextureView) root.findViewById(R.id.live_video);
        mPublishVideo = (LiveRecordView) root.findViewById(R.id.publish_video);
        mStuPublishVideo = (LiveRecordView) root.findViewById(R.id.stu_preview_video);
        mPlayStuView = (PlayerTextureView) root.findViewById(R.id.stu_player_video);

        mWhiteboardSv = (WhiteboardScrollerView) root.findViewById(R.id.white_board_scrollview);
        mWhiteboardLayout = (WhiteboardLayout) root.findViewById(R.id.white_board_layout);
        mCurrWhiteboard = mWhiteboardLayout.getWhiteboard();
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

        initVideo();
        //initWhiteboardData(context);
        listenerSocket();
    }

    private void initVideo() {
        if (mUser == Constants.User.TEACHER) {
            mPublishVideo.setVisibility(View.VISIBLE);
            mPublishVideo.setOnStreamingStateListener(mStreamingStateChangedListener);
        } else if (mUser == Constants.User.STUDENT) {
            mLiveVideo.setVisibility(View.VISIBLE);
            mStuPublishVideo.setOnStreamingStateListener(mStreamingStateChangedListener);
        }

        onResumeVideo();
    }

    private void listenerSocket() {
        mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED), mReceiveStreamStarted);
        mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA), mReceiveOpenMedia);
        mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_FEEDBACK), mReceiveFeedback);
        mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED), mReceiveMediaAborted);
    }

    /**
     * 初始白板数据(第一期暂时不用)
     */
    private void initWhiteboardData(Context context) {
        mWhiteboardAdapter = new WhiteboardAdapter(context);
        mWhiteboardSv.setOffscreenPageLimit(2);
        mWhiteboardSv.setAdapter(mWhiteboardAdapter);
        mWhiteboardAdapter.setOnWhiteboardListener(this);

        mSocket.on(Event.BOARD, mOnBoard);
        mHandler = new Handler();
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
                            onResumeVideo();

                            boardCollection.setLive(true);
                        }
                    }
                }
            }
        });
    }

    public void showWhiteboard() {
        mCurrWhiteboard.setVisibility(View.VISIBLE);
    }

    public void hideWhiteboard() {
        mCurrWhiteboard.setVisibility(View.GONE);
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

    public void onResumeVideo() {
        if (mUser == Constants.User.TEACHER) {
            mPublishVideo.resume();
            mPlayStuView.destroy();
        } else if (mUser == Constants.User.STUDENT) {
            mStuPublishVideo.resume();
            mLiveVideo.resume();
        }
    }

    public void onPauseVideo() {
        if (mUser == Constants.User.TEACHER) {
            mPublishVideo.pause();
            mPlayStuView.pause();
        } else if (mUser == Constants.User.STUDENT) {
            mStuPublishVideo.pause();
            mLiveVideo.pause();
        }
    }

    public void onDestroyVideo() {
        if (mUser == Constants.User.TEACHER) {
            mPublishVideo.destroy();
            mPlayStuView.destroy();
        } else if (mUser == Constants.User.STUDENT) {
            mStuPublishVideo.destroy();
            mLiveVideo.destroy();
        }
    }

    /**
     * 释放资源
     */
    public void release() {
        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.release();
        }

        if (mCurrWhiteboard != null) {
            mCurrWhiteboard.release();
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
            if (mCurrWhiteboard != null) {
                mColorPicker.setPaintColor(mCurrWhiteboard.getPaintColor());
                mCurrWhiteboard.setUndoRedoListener(this);
                mCurrWhiteboard.setOnColorChangeListener(this);
                mCurrWhiteboard.onWhiteboardSelected();

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
     * 进入视频编辑页面
     */
    public void enterVideoEditing(Bitmap bmp) {
        if (mWhiteboardLayout != null) {
            mVideoFrameBitmap = bmp.copy(Bitmap.Config.ARGB_4444, true);
            mWhiteboardLayout.setVisibility(View.VISIBLE);
            mWhiteboardLayer = new WhiteboardLayer();
            mCurrWhiteboard.setLayer(mWhiteboardLayer);
            mCurrWhiteboard.setSrcBitmap(bmp);
            onWhiteboardSelected(mCurrWhiteboard);
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        if (mUser == Constants.User.TEACHER) {
            mPublishVideo.switchCamera();
        } else if (mUser == Constants.User.STUDENT) {
            mStuPublishVideo.switchCamera();
        }
    }

    /**
     * 退出视频编辑页面
     */
    public void exitVideoEditing() {
        if (mWhiteboardLayout != null) {
            mWhiteboardLayout.setVisibility(View.GONE);
            mCurrWhiteboard.recycleCourseBmp();
        }
    }

    /**
     * 捕获视频帧
     */
    public void takeVideoFrame(FrameCapturedCallback callback) {
        Bitmap bmp = null;
        if (mUser == Constants.User.TEACHER) {
            mPublishVideo.captureOriginalFrame(callback);
        } else if (mUser == Constants.User.STUDENT) {
            bmp = mLiveVideo.getPlayer().getTextureView().getBitmap();
            if (callback != null) {
                callback.onFrameCaptured(bmp);
            }
        }
    }

    /**
     * 选择分享联系人
     */
    public void selectShareContact(View anchor) {
        if (mSharePopWindow == null) {
            mSharePopWindow = new ShareDoodlePopWindow(mContext);
        }

        int offsetX = -mContext.getResources().getDimensionPixelSize(R.dimen.px370);
        int offsetY = -mContext.getResources().getDimensionPixelSize(R.dimen.px58);
        mSharePopWindow.showAsDropDown(anchor, offsetX, offsetY);
    }

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
                        onResumeVideo();
                        mCurrWhiteboard.setVisibility(View.GONE);
                    } else {
                        mCurrWhiteboard.setVisibility(View.VISIBLE);
                    }
                    mWhiteboardSv.setVisibility(View.GONE);
                } else {
                    onPauseVideo();
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
            if (mCurrWhiteboard != null) {
                mCurrWhiteboard.onReceive(mCommendList);
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

    private boolean isWebApp(int app) {
        return app == Platform.AppType.WEB_CLASSROOM || app == Platform.AppType.MOBILE_WEB;
    }

    /**
     * 暂停推流
     */
    public void pauseVideo() {
        if (mUser == Constants.User.TEACHER) {
            mPublishVideo.pause();
        } else if (mUser == Constants.User.STUDENT) {
            mStuPublishVideo.pause();
        }
    }

    /**
     * 开始推流
     */
    public void publishStream(String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }

        if (mUser == Constants.User.TEACHER) {
            mPublishVideo.setVisibility(View.VISIBLE);
            mPublishVideo.setPath(url);
            if (!mInitPublishVideo) {
                mPublishVideo.start();
            } else {
                mPublishVideo.resume();
            }
        } else if (mUser == Constants.User.STUDENT) {
            mStuPublishVideo.setVisibility(View.VISIBLE);
            mStuPublishVideo.setPath(url);
            if (!mInitPublishVideo) {
                mStuPublishVideo.start();
            } else {
                mStuPublishVideo.resume();
            }
        }
    }

    /**
     * 老师端播放学生端视频
     * @param url
     */
    public void playStuVideo(String url) {
        if (mUser == Constants.User.TEACHER) {
            mPlayStuView.setVisibility(View.VISIBLE);
            mPlayStuView.setPath(url);
            mPlayStuView.resume();
        }
    }

    /**
     * 学生端直播老师视频
     */
    public void playTeaVideo(String url) {
        if (mUser == Constants.User.STUDENT) {
            mLiveVideo.setVisibility(View.VISIBLE);
            mLiveVideo.setPath(url);
            mLiveVideo.resume();
        }
    }

    /**
     * 流推送监听
     */
    private StreamingStateChangedListener mStreamingStateChangedListener = new StreamingStateChangedListener() {
        @Override
        public void onStateChanged(StreamingState streamingState, Object o) {
            switch (streamingState) {
                case STREAMING:
                    mInitPublishVideo = true;
                    if (mSocket != null) {
                        int eventType = Su.EventType.STREAMING_STARTED;
                        /*if (mUser == Constants.User.TEACHER) {
                            eventType = Su.EventType.STREAMING_STARTED;
                        } else if (mUser == Constants.User.STUDENT) {
                            eventType = Su.EventType.MEDIA_FEEDBACK;
                        }
                        if (eventType < 0) {
                            return;
                        }*/

                        mSocket.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, eventType), new Ack() {
                            @Override
                            public void call(final Object... args) {
                                if (args != null && args.length > 0) {
                                    StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                                    final String prefix = mUser == Constants.User.TEACHER ? "老师" : "学生";
                                    if (response != null && response.result) {
                                        ((Activity)mContext).runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(mContext, prefix + "推流开始", Toast.LENGTH_LONG).show();
                                            }
                                        });

                                        if (mUser == Constants.User.STUDENT) {
                                            mSocket.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.MEDIA_FEEDBACK), new Ack() {
                                                @Override
                                                public void call(Object... args) {
                                                    if (args != null && args.length > 0) {
                                                        StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                                                        if (response != null && response.result) {
                                                            ((Activity)mContext).runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    Toast.makeText(mContext, prefix + "发送feedback 成功", Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    } else {
                                        onPauseVideo();
                                    }
                                } else {
                                    onPauseVideo();
                                }
                            }
                        });


                    }
                    break;
                case READY:

                    break;
            }

        }
    };

    private Emitter.Listener mReceiveStreamStarted = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "流开始", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener mReceiveMediaAborted = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "流被中断", Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener mReceiveFeedback = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "receive feedback", Toast.LENGTH_LONG).show();
                    if (args != null && args.length > 0) {
                        MediaFeedback response = ClassroomBusiness.parseSocketBean(args[0], MediaFeedback.class);
                        if (response != null && response.playUrl != null) {
                            playStuVideo(response.playUrl);
                        }
                    }
                }
            });
        }
    };

    private Emitter.Listener mReceiveOpenMedia = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            ((Activity)mContext).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(mContext, "收到打开学生视频", Toast.LENGTH_LONG).show();
                    if (mAgreeOpenCamera == null) {
                        mAgreeOpenCamera = new CommonDialog(mContext);
                        mAgreeOpenCamera.setTitle(R.string.open_camera_tips);
                        mAgreeOpenCamera.setDesc(R.string.agree_open_camera);

                        mAgreeOpenCamera.setOnRightClickListener(new CommonDialog.OnClickListener() {
                            @Override
                            public void onClick() {
                                mAgreeOpenCamera.dismiss();
                                if (args != null && args.length > 0) {
                                    OpenMediaNotify openMediaNotify = ClassroomBusiness.parseSocketBean(args[0], OpenMediaNotify.class);
                                    if (openMediaNotify != null) {
                                        publishStream(openMediaNotify.publishUrl);
                                    }
                                }
                            }
                        });
                    }

                    mAgreeOpenCamera.show();
                }
            });
        }
    };
}
