package cn.xiaojs.xma.ui.classroom.page;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kaola.qrcodescanner.qrcode.utils.ScreenUtils;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.live.Board;
import cn.xiaojs.xma.model.live.BoardCriteria;
import cn.xiaojs.xma.model.live.BoardItem;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.RequestShareboard;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;
import cn.xiaojs.xma.model.socket.room.whiteboard.Drawing;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardController;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardScrollerView;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.PushPreviewBoardListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncDrawingListener;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.util.ArrayUtil;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.MaterialUtil;
import cn.xiaojs.xma.util.ToastUtil;
import io.reactivex.functions.Consumer;


/**
 * created by Paul Z on 2017/9/4
 */
public class BoardCollaborateFragment extends BaseFragment {
    public final static int TYPE_SINGLE_IMG = 1;
    public final static int TYPE_MULTI_IMG = 2;

    public static final String EXTRA_BOARD_ID = "extra_board_id";
    public static final int CODE_OPEN_BOARD = 10;


    @BindView(R.id.white_board_panel)
    View mWhiteBoardPanel;
    @BindView(R.id.white_board_scrollview)
    WhiteboardScrollerView mBoardScrollerView;
        @BindView(R.id.test_preview)
        ImageView testPreview;
    @BindView(R.id.text_pager_points)
    TextView textPagerPoints;

    private int boardMode = BOARD_MODE_MINE;
    private final static int BOARD_MODE_MINE = 0;//面向发起者
    private final static int BOARD_MODE_YOUR = 1;//面向被动者
    private int state = STATE_UNSPECIFIED;
    private final static int STATE_UNSPECIFIED = 0;//未协作状态
    private final static int STATE_COLLABORATE_FREEZE = 1;//白板协作状态---不能操作远程图形
    private final static int STATE_COLLABORATE_NORMAL = 2;//白板协作状态---可以操作远程图形


    private WhiteboardController mBoardController;
    private CTLConstant.UserIdentity mUser = CTLConstant.UserIdentity.LEAD;

    private int mDisplayType = TYPE_SINGLE_IMG;
    private float mDoodleRatio = WhiteboardLayer.DOODLE_CANVAS_RATIO;

    public ShareboardReceive collaborateData;
    private EventListener.Syncboard eventListener;

    private String boardId;

    private LibDoc doc;
    private int boardType = Live.BoardType.WHITE;

    ArrayList<LibDoc.ExportImg> slides = null;

    private int curPage = -1;

    private OnPushPreviewListener onPushPreviewListener;

    private boolean isReadOnly = false;
    private boolean needRegistNewBoard = false;

    public BoardCollaborateFragment() {
        getLastBoard();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(mContent!=null){
            if(mContent.getParent()!=null){
                ((ViewGroup)mContent.getParent()).removeView(mContent);
            }
            lazyLoadBuz();
            setListener();
            return mContent;
        }
        mContext = getActivity();
        mContent = (ViewGroup) inflater.inflate(R.layout.fragment_base, null);
        View content = getContentView();
        addContainerView(content);
        ButterKnife.bind(this, content);
        init();
        setListener();
        initBoard();
        return mContent;
    }

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_board_collaborate, null);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(needRegistNewBoard){
            registBoard(null);
            needRegistNewBoard=false;
        }
    }

    private void setListener(){
        eventListener = ClassroomEngine.getEngine().observerSyncboard(syncBoardConsumer);
        mBoardController.setPushPreviewBoardListener(new PushPreviewBoardListener() {
            @Override
            public void onPush(Bitmap bitmap) {
                testPreview.setImageBitmap(bitmap);
                if (onPushPreviewListener != null) {
                    onPushPreviewListener.onPushPreview(bitmap);
                }

                Fragment fragment = getTargetFragment();
                if (fragment instanceof IBoardManager && ((IBoardManager) fragment).pushPreviewEnable()) {
                    ((IBoardManager) fragment).onPushPreview(bitmap);
                }

            }
        });
    }

    private void destroyListener(){
        eventListener.dispose();
        mBoardController.setPushPreviewBoardListener(null);
    }

    @Override
    protected void init() {
        mBoardController = new WhiteboardController(mContext, mContent, mUser, 0);
        mBoardController.showWhiteboardLayout(null, mDoodleRatio);
    }


    @OnClick({R.id.select_btn, R.id.handwriting_btn, R.id.shape_btn,
            R.id.color_picker_btn, R.id.eraser_btn, R.id.undo, R.id.redo, R.id.text_pager_points})
    public void onPanelItemClick(View v) {
        switch (v.getId()) {
            case R.id.select_btn:
            case R.id.handwriting_btn:
            case R.id.eraser_btn:
            case R.id.shape_btn:
            case R.id.color_picker_btn:
            case R.id.undo:
            case R.id.redo:
                mBoardController.handlePanelItemClick(v);
                break;
            case R.id.text_pager_points:
                Fragment fragment = getTargetFragment();
                if (fragment instanceof IBoardManager) {
                    ((IBoardManager) fragment).openSlideMenu(doc, slides, curPage);
                }
                break;

            default:
                break;
        }
    }



    @Override
    public void onDestroyView() {
        destroyListener();
        super.onDestroyView();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void lazyLoadBuz(){
        if(mTempDoc!=null){
            doc=mTempDoc;
            mTempDoc=null;
            initBoard();
        }
    }


    private void initBoard() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                boardMode = BOARD_MODE_MINE;
                mBoardController.setCanReceive(false);
                mBoardController.setCanSend(false);
                textPagerPoints.setVisibility(View.GONE);
                //优先初始化最后一次打开的白板数据，如果没有再根据白板类型初始化
                if(mLastBoardItem != null){
                    mBoardController.replaceNewWhiteboardLayout(null, mDoodleRatio);
                    mBoardController.setBoardLayerSet(mLastBoardItem.drawing);
                    mLastBoardItem=null;
                }else if (boardType == Live.BoardType.WHITE) {
                    mBoardController.showWhiteboardLayout(null, mDoodleRatio);
                    if(TextUtils.isEmpty(boardId)){
                        registBoard(null);
                    }
                } else if (boardType == Live.BoardType.SLIDES) {
                    loadNewPage(0);
                    if(TextUtils.isEmpty(boardId)){
                        registBoard(doc.name);
                    }
                } else if (boardType == Live.BoardType.COLLABORATION) {
                    mBoardController.showWhiteboardLayout(null, mDoodleRatio);
                    if (collaborateData == null) {
                        ToastUtil.showToast(getActivity(), "协作失败");
                    } else {
                        //协作初始化  不用注册，打开；但需要打开协作相关的开关，设置同步监听
                        mBoardController.setCanReceive(true);
                        mBoardController.setCanSend(true);
                        boardMode = BOARD_MODE_YOUR;
                        boardId=collaborateData.board.id;
                        mBoardController.syncBoardLayerSet(collaborateData);
                    }
                }
                //根据协作状态初始化对应内容
                if (state == STATE_UNSPECIFIED) {
                    mBoardController.setSyncDrawingListener(null);
                } else if (state == STATE_COLLABORATE_FREEZE) {
                    mBoardController.setSyncDrawingListener(syncDrawingListener);
                } else if (state == STATE_COLLABORATE_NORMAL) {
                    mBoardController.setSyncDrawingListener(syncDrawingListener);
                }
                mBoardController.setWhiteBoardId(boardId);
                mBoardController.setWhiteBoardReadOnly(isReadOnly);
                mBoardController.setPanelShow(!isReadOnly);
            }
        }, 500);

    }

    private Consumer<EventReceived> syncBoardConsumer = new Consumer<EventReceived>() {
        @Override
        public void accept(EventReceived eventReceived) throws Exception {

            if (XiaojsConfig.DEBUG) {
                Logger.d("ELSyncboard received eventType:%d", eventReceived.eventType);
            }

            switch (eventReceived.eventType) {
                case Su.EventType.SYNC_BOARD:
                    mBoardController.onReceive((SyncBoardReceive) eventReceived.t);
                    break;
                case Su.EventType.STOP_SHARE_BOARD:
                    getFragmentManager().popBackStackImmediate();
                    break;

            }
        }
    };


    private SyncDrawingListener syncDrawingListener = new SyncDrawingListener() {
        @Override
        public void onBegin(String data) {
            ClassroomEngine.getEngine().syncBoard(data, new EventCallback<EventResponse>() {
                @Override
                public void onSuccess(EventResponse eventResponse) {
                    Logger.d("----sync----onBegin---onSuccess");
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {
                    Logger.d("----sync----onBegin---onFailed:" + errorMessage);
                }
            });
        }

        @Override
        public void onGoing(String data) {
            ClassroomEngine.getEngine().syncBoard(data, new EventCallback<EventResponse>() {
                @Override
                public void onSuccess(EventResponse eventResponse) {
                    Logger.d("----sync----onGoing---onSuccess");
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {
                    Logger.d("----sync----onGoing---onFailed:" + errorMessage);
                }
            });
        }

        @Override
        public void onFinished(String data) {
            ClassroomEngine.getEngine().syncBoard(data, new EventCallback<EventResponse>() {
                @Override
                public void onSuccess(EventResponse eventResponse) {
                    Logger.d("----sync----onFinished---onSuccess");
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {
                    Logger.d("----sync----onFinished---onFailed:" + errorMessage);
                }
            });
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        /*if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            mBoardController.setWhiteBoardReadOnly(false);
            mWhiteBoardPanel.setVisibility(View.VISIBLE);
        }else {
            mBoardController.setWhiteBoardReadOnly(true);
            mWhiteBoardPanel.setVisibility(View.GONE);
        }*/
    }

    public Bitmap preview() {
        if (mBoardController != null) {
            return mBoardController.getPreviewWhiteBoard();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        setTargetFragment(null, 0);
        isReadOnly = false;
        super.onDestroy();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CODE_OPEN_BOARD) {
                BoardItem boardItem = (BoardItem) data.getSerializableExtra(WhiteboardManagerFragment.EXTRA_SELECTED_BOARD);
                mLastBoardItem=boardItem;
                boardId=boardItem.id;
                boardType=boardItem.type;
                initBoard();
            }
        }
    }

    public void showWhiteboardManager() {
        /*Fragment fragment=WhiteboardManagerFragment.createInstance("");
        fragment.setTargetFragment(this,200);
        getChildFragmentManager().beginTransaction().
                add(R.id.layout_dialog_container,fragment).
                addToBackStack("dialog_fragment").
                commitAllowingStateLoss();*/

        WhiteboardManagerFragment fragment = WhiteboardManagerFragment.createInstance("");
        fragment.setTargetFragment(this, CODE_OPEN_BOARD);
        fragment.show(getChildFragmentManager(), "dialog_fragment");
    }

    public void registBoard(String title) {
        final Board board = new Board();
        board.title = TextUtils.isEmpty(title)?"新的白板":title;
        board.drawing = new Board.DrawDimension();
        board.drawing.width = ScreenUtils.getScreenWidth(getActivity());
        board.drawing.height = board.drawing.width * 9 / 16;
        board.type= boardType;
        if(boardType==Live.BoardType.SLIDES&&!ArrayUtil.isEmpty(slides)){
            board.pages=slides;
        }
        ClassroomEngine.getEngine().registerBoard(ClassroomEngine.getEngine().getTicket(), board, new APIServiceCallback<BoardItem>() {
            @Override
            public void onSuccess(BoardItem object) {
                openBoard(object.id);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(getActivity(), errorMessage + ",后续操作无法保存");
            }
        });
    }

    public void openBoard(final String boardId) {
        if (!TextUtils.isEmpty(boardId)) {
            ClassroomEngine.getEngine().openBoard(ClassroomEngine.getEngine().getTicket(), boardId, new APIServiceCallback<BoardItem>() {
                @Override
                public void onSuccess(BoardItem object) {
                    BoardCollaborateFragment.this.boardId = object.id;
                    mBoardController.setWhiteBoardId(boardId);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    ToastUtil.showToast(getActivity(), errorMessage + ",后续操作无法保存");
                }
            });
        }
    }

    public void createNewBoard(){
        boardType=Live.BoardType.WHITE;
        boardId=null;
        doc=null;
        mTempDoc=null;
        mLastBoardItem=null;
        initBoard();
    }



    public void openDocOutsideBoard(final LibDoc doc) {
        if (doc == null) {
            ToastUtil.showToast(getActivity(), "该文件无效，无法打开");
            return;
        }
        ArrayList<LibDoc.ExportImg> imgs = null;
        if (Collaboration.isImage(doc.mimeType)) {
            imgs = new ArrayList<>(1);
            LibDoc.ExportImg img = new LibDoc.ExportImg();
            img.name = doc.key;
            imgs.add(img);
        } else if (Collaboration.isPPT(doc.mimeType) || Collaboration.isDoc(doc.mimeType) || Collaboration.isPDF(doc.mimeType)) {
            if (doc.exported == null || ArrayUtil.isEmpty(doc.exported.images)) {
                ToastUtil.showToast(getActivity(), "该文件无效，无法打开");
                return;
            }
            imgs = MaterialUtil.getSortImgs(doc.exported.images);
        } else {
            ToastUtil.showToast(getActivity(), "无法打开该类型文件");
            return;
        }
        boardType= Live.BoardType.SLIDES;
        slides = imgs;
        mTempDoc = doc;
    }

    private LibDoc mTempDoc;

    public void openDocInsideBoard(final LibDoc doc) {
        if (doc == null) {
            ToastUtil.showToast(getActivity(), "该文件无效，无法打开");
            return;
        }
        ArrayList<LibDoc.ExportImg> imgs = null;
        if (Collaboration.isImage(doc.mimeType)) {
            imgs = new ArrayList<>(1);
            LibDoc.ExportImg img = new LibDoc.ExportImg();
            img.name = doc.key;
            imgs.add(img);
        } else if (Collaboration.isPPT(doc.mimeType) || Collaboration.isDoc(doc.mimeType) || Collaboration.isPDF(doc.mimeType)) {
            if (doc.exported == null || ArrayUtil.isEmpty(doc.exported.images)) {
                ToastUtil.showToast(getActivity(), "该文件无效，无法打开");
                return;
            }
            imgs = MaterialUtil.getSortImgs(doc.exported.images);
        } else {
            ToastUtil.showToast(getActivity(), "无法打开该类型文件");
            return;
        }
        boardType= Live.BoardType.SLIDES;
        this.doc = doc;
        slides = imgs;
        initBoard();
    }


    public void loadNewPage(int page) {
        String url = null;
       /* if(Collaboration.isImage(doc.mimeType)){
            url=Social.getDrawing(doc.key, false);
        }else if(Collaboration.isPPT(doc.mimeType) || Collaboration.isPDF(doc.mimeType) || Collaboration.isDoc(doc.mimeType)){
        }else {
            return;
        }*/
        curPage = page;
        url = Social.getDrawing(slides.get(page).name, false);
        if (XiaojsConfig.DEBUG) {
            Logger.d("-----qz------loadBoardImg----url=" + url);
        }
        textPagerPoints.setVisibility(View.VISIBLE);
        textPagerPoints.setText((page + 1) + "/" + slides.size());
        Glide.with(this).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                mBoardController.replaceNewWhiteboardLayout(resource, mDoodleRatio);
            }
        });
    }


    @Deprecated
    public void setOnPushPreviewListener(OnPushPreviewListener onPushPreviewListener) {
        this.onPushPreviewListener = onPushPreviewListener;
    }

    @Deprecated
    public interface OnPushPreviewListener {
        public void onPushPreview(Bitmap bitmap);
    }


    /**
     * 发起协作
     */
    public void requestShareBoard() {
        RequestShareboard shareboard = new RequestShareboard();
        shareboard.board = boardId;
        Bitmap preview = preview();
        if (preview != null) {
            shareboard.preview = Base64.encodeToString(BitmapUtils.bmpToByteArray(preview, true), Base64.DEFAULT);
        }
        ClassroomEngine.getEngine().requestShareboard(shareboard, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse eventResponse) {
                ToastUtil.showToast(getActivity(), "发起协作成功");
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                ToastUtil.showToast(getActivity(), "发起协作失败：" + errorMessage);
            }
        });
    }

    BoardItem mLastBoardItem;

    public void getLastBoard() {
        BoardCriteria criteria = new BoardCriteria();
        Pagination pagination = new Pagination();
        pagination.setMaxNumOfObjectsPerPage(1);
        pagination.setPage(1);
        criteria.state = Live.BoardState.OPEN;
        ClassroomEngine.getEngine().getBoards(criteria, pagination, new APIServiceCallback<CollectionPage<BoardItem>>() {
            @Override
            public void onSuccess(CollectionPage<BoardItem> object) {
                if (object == null || ArrayUtil.isEmpty(object.objectsOfPage)) {
                    needRegistNewBoard=true;
                    return;
                }
                BoardItem boardItem = object.objectsOfPage.get(0);
                mLastBoardItem = boardItem;
                boardType=mLastBoardItem.type;
                boardId=mLastBoardItem.id;

                if (isAdded() && !isDetached()) {
                    mBoardController.replaceNewWhiteboardLayout(null, mDoodleRatio);
                    mBoardController.setBoardLayerSet(boardItem.drawing);
                    initBoard();
                }
                if (mOnLastBoardLoadListener != null) {
                    mOnLastBoardLoadListener.onSuccess(BitmapUtils.base64ToBitmapWithPrefix(boardItem.snapshot));
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });

    }

    OnLastBoardLoadListener mOnLastBoardLoadListener;

    public void setLastBoardLoadListener(OnLastBoardLoadListener onLastBoardLoadListener) {
        mOnLastBoardLoadListener = onLastBoardLoadListener;
    }

    public interface OnLastBoardLoadListener {
        void onSuccess(Bitmap preview);
    }


    //定时保存
    private void startSaveTimer() {
        if (executor == null) {
            executor = new ScheduledThreadPoolExecutor(1);
            executor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {

                }
            }, 5, 5, TimeUnit.SECONDS);
        }
    }

    //停止定时保存
    private void stopSaveTimer() {
        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }

    ScheduledThreadPoolExecutor executor;

    public void setReadOnly(boolean isReadOnly) {
        this.isReadOnly = isReadOnly;
        if (isAdded() && !isDetached()) {
            mBoardController.setWhiteBoardReadOnly(isReadOnly);
            mBoardController.setPanelShow(!isReadOnly);
        }
    }

    public static BoardCollaborateFragment createInstance() {
        BoardCollaborateFragment fragment = new BoardCollaborateFragment();
        return fragment;
    }

}
