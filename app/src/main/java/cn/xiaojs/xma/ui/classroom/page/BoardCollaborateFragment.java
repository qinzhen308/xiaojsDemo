package cn.xiaojs.xma.ui.classroom.page;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.live.Board;
import cn.xiaojs.xma.model.live.BoardItem;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.main.AnimData;
import cn.xiaojs.xma.ui.classroom.main.ClassroomController;
import cn.xiaojs.xma.ui.classroom.main.FadeAnimListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardController;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardScrollerView;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.PushPreviewBoardListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.sync.SyncDrawingListener;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.util.StringUtil;
import cn.xiaojs.xma.util.ToastUtil;
import io.reactivex.functions.Consumer;


/**
 * created by Paul Z on 2017/9/4
 */
public class BoardCollaborateFragment extends BaseFragment {
    public final static int TYPE_SINGLE_IMG = 1;
    public final static int TYPE_MULTI_IMG = 2;

    public static final String COLLABORATE_FIRST_DATA="extra_collaborate_first_data";
    public static final String EXTRA_BOARD_ID="extra_board_id";


    @BindView(R.id.white_board_panel)
    View mWhiteBoardPanel;
    @BindView(R.id.white_board_scrollview)
    WhiteboardScrollerView mBoardScrollerView;
    @BindView(R.id.test_preview)
    ImageView testPreview;

    private int boardMode=BOARD_MODE_MINE;
    private final static int BOARD_MODE_MINE=0;//面向发起者
    private final static int BOARD_MODE_YOUR=1;//面向被动者


    private WhiteboardController mBoardController;
    private CTLConstant.UserIdentity mUser = CTLConstant.UserIdentity.LEAD;

    private int mDisplayType = TYPE_SINGLE_IMG;
    private float mDoodleRatio = WhiteboardLayer.DOODLE_CANVAS_RATIO;
    private FadeAnimListener mFadeAnimListener;

    public ShareboardReceive firstData;
    private EventListener.Syncboard eventListener;

    private String boardId;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_board_collaborate, null);
    }

    @Override
    protected void init() {
        firstData=(ShareboardReceive) getArguments().getSerializable(COLLABORATE_FIRST_DATA);
//        LogUtil.d(firstData.board.drawing.stylus);
        mBoardController = new WhiteboardController(mContext, mContent, mUser, 0);
        mFadeAnimListener = new FadeAnimListener();
        mBoardController.showWhiteboardLayout(null, mDoodleRatio);
        mBoardController.setCanReceive(true);
        mBoardController.setCanSend(true);
        mBoardController.setCanSend(true);
        boardId=getArguments().getString(EXTRA_BOARD_ID);
        if(TextUtils.isEmpty(boardId)){
            registBoard();
        }else {
            openBoard(boardId);
        }

        if(firstData!=null){
            boardMode=BOARD_MODE_YOUR;
            mBoardController.setWhiteBoardId(firstData.board.id);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBoardController.syncBoardLayerSet(firstData);
                }
            }, 500);
        }else {
            String boardId=getArguments().getString(EXTRA_BOARD_ID);
            if(TextUtils.isEmpty(boardId)){
                mBoardController.setWhiteBoardId(boardId);
            }
        }
        mBoardController.setSyncDrawingListener(syncDrawingListener);
        eventListener = ClassroomEngine.getEngine().observerSyncboard(syncBoardConsumer);
        mBoardController.setPushPreviewBoardListener(new PushPreviewBoardListener() {
            @Override
            public void onPush(Bitmap bitmap) {
                testPreview.setImageBitmap(bitmap);
            }
        });
    }

    private Bitmap decodeBg(){
        byte[] bytes=Base64.decode(firstData.board.preview.substring(firstData.board.preview.indexOf(",")+1,firstData.board.preview.length()),Base64.DEFAULT);
        Bitmap bg=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return bg;
    }


    @OnClick({ R.id.select_btn, R.id.handwriting_btn,R.id.shape_btn,
            R.id.color_picker_btn,  R.id.eraser_btn, R.id.undo, R.id.redo})
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

            default:
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBoardController.hideWhiteboardLayout();
        if (mWhiteBoardPanel != null) {
            mWhiteBoardPanel.animate().cancel();
        }

        eventListener.dispose();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView(){
        /*if(ClassroomController.getInstance(getContext()).isPortrait()){
            mWhiteBoardPanel.setVisibility(View.GONE);
            mBoardController.setWhiteBoardReadOnly(true);
        }*/
    }


    private void hideAnim(View view) {
        startAnimation(view,
                FadeAnimListener.MODE_ANIM_HIDE,
                FadeAnimListener.ANIM_ALPHA,
                new AnimData(0));
    }

    private void showAnim(View view) {
        startAnimation(view,
                FadeAnimListener.MODE_ANIM_SHOW,
                FadeAnimListener.ANIM_ALPHA,
                new AnimData(1));
    }

    protected void startAnimation(View view, int animMode, int animSets, AnimData data) {
        FadeAnimListener listener = mFadeAnimListener;
        ViewPropertyAnimator viewPropertyAnimator = view.animate();

        //alpha anim
        if ((FadeAnimListener.ANIM_ALPHA & animSets) != 0) {
            viewPropertyAnimator.alpha(data.alpha);
        }
        //translate anim
        if ((FadeAnimListener.ANIM_TRANSLATE & animSets) != 0) {
            viewPropertyAnimator.translationX(data.translateX);
            viewPropertyAnimator.translationY(data.translateY);
        }
        //scale anim
        if ((FadeAnimListener.ANIM_SCALE & animSets) != 0) {
            viewPropertyAnimator.scaleX(data.scaleX);
            viewPropertyAnimator.scaleY(data.scaleY);
        }

        viewPropertyAnimator.setListener(listener.with(view).play(animMode)).start();
    }


    public static BoardCollaborateFragment createInstance(ShareboardReceive shareboardReceive) {
        BoardCollaborateFragment fragment = new BoardCollaborateFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(COLLABORATE_FIRST_DATA,shareboardReceive);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static BoardCollaborateFragment createInstance(String boardID) {
        BoardCollaborateFragment fragment = new BoardCollaborateFragment();
        Bundle bundle = new Bundle();
        if(!TextUtils.isEmpty(boardID)){
            bundle.putString(EXTRA_BOARD_ID,boardID);
        }
        fragment.setArguments(bundle);
        return fragment;
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


    private SyncDrawingListener syncDrawingListener=new SyncDrawingListener() {
        @Override
        public void onBegin(String data) {
            ClassroomEngine.getEngine().syncBoard(data, new EventCallback<EventResponse>() {
                @Override
                public void onSuccess(EventResponse eventResponse) {
                    Logger.d("----sync----onBegin---onSuccess");
                }

                @Override
                public void onFailed(String errorCode, String errorMessage) {
                    Logger.d("----sync----onBegin---onFailed:"+errorMessage);
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
                    Logger.d("----sync----onGoing---onFailed:"+errorMessage);
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
                    Logger.d("----sync----onFinished---onFailed:"+errorMessage);
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

    public Bitmap preview(){
        if(mBoardController!=null){
            return mBoardController.getPreviewWhiteBoard();
        }
        return null;
    }

    @Override
    public void onDestroy() {
        mBoardController.setPushPreviewBoardListener(null);
        super.onDestroy();
    }


    public void showWhiteboardManager(){
        /*Fragment fragment=WhiteboardManagerFragment.createInstance("");
        fragment.setTargetFragment(this,200);
        getChildFragmentManager().beginTransaction().
                add(R.id.layout_dialog_container,fragment).
                addToBackStack("dialog_fragment").
                commitAllowingStateLoss();*/

        WhiteboardManagerFragment.createInstance("").show(getChildFragmentManager(),"dialog_fragment");
    }

    public void registBoard(){
        final Board board=new Board();
        board.title="新的白板";
        ClassroomEngine.getEngine().registerBoard(ClassroomEngine.getEngine().getTicket(), board, new APIServiceCallback<BoardItem>() {
            @Override
            public void onSuccess(BoardItem object) {
                openBoard(object.id);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                ToastUtil.showToast(getActivity(),errorMessage+",后续操作无法保存");
            }
        });
    }

    public void openBoard(final String boardId){
        if(!TextUtils.isEmpty(boardId)){
            ClassroomEngine.getEngine().openBoard(ClassroomEngine.getEngine().getTicket(), boardId, new APIServiceCallback<BoardItem>() {
                @Override
                public void onSuccess(BoardItem object) {
                    BoardCollaborateFragment.this.boardId=object.id;
                    mBoardController.setWhiteBoardId(boardId);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    ToastUtil.showToast(getActivity(),errorMessage+",后续操作无法保存");
                }
            });
        }

    }
}
