package cn.xiaojs.xma.ui.classroom.page;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.stetho.common.LogUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.CollaManager;
import cn.xiaojs.xma.data.api.service.QiniuService;
import cn.xiaojs.xma.model.material.UploadReponse;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.model.socket.room.SyncBoardReceive;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.main.AnimData;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.main.FadeAnimListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardAdapter;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardCollection;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardController;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardScrollerView;
import cn.xiaojs.xma.ui.classroom2.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.EventListener;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.CacheUtil;


/**
 * created by Paul Z on 2017/9/4
 */
public class BoardCollaborateFragment extends BaseFragment implements EventListener {
    public final static int TYPE_SINGLE_IMG = 1;
    public final static int TYPE_MULTI_IMG = 2;

    public static final String COLLABORATE_FIRST_DATA="extra_collaborate_first_data";


    @BindView(R.id.white_board_panel)
    View mWhiteBoardPanel;
    @BindView(R.id.white_board_scrollview)
    WhiteboardScrollerView mBoardScrollerView;
    @BindView(R.id.back_btn)
    ImageView mBackBtn;


    private WhiteboardController mBoardController;
    private CTLConstant.UserIdentity mUser = CTLConstant.UserIdentity.LEAD;

    private int mDisplayType = TYPE_SINGLE_IMG;
    private float mDoodleRatio = WhiteboardLayer.DOODLE_CANVAS_RATIO;
    private FadeAnimListener mFadeAnimListener;

    public ShareboardReceive firstData;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_board_collaborate, null);
    }

    @Override
    protected void init() {
        firstData=(ShareboardReceive) getArguments().getSerializable(COLLABORATE_FIRST_DATA);
        LogUtil.d(firstData.board.drawing.stylus);
        mBoardController = new WhiteboardController(mContext, mContent, mUser, 0);
        mFadeAnimListener = new FadeAnimListener();
        mBoardController.showWhiteboardLayout(decodeBg(), mDoodleRatio);
        mBoardController.setCanReceive(true);
        mBoardController.setCanSend(true);
    }

    private Bitmap decodeBg(){
        byte[] bytes=Base64.decode(firstData.board.preview.substring(firstData.board.preview.indexOf(",")+1,firstData.board.preview.length()),Base64.DEFAULT);
        Bitmap bg=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        return bg;
    }

    @OnClick({R.id.back_btn, R.id.select_btn, R.id.handwriting_btn,
            R.id.color_picker_btn,  R.id.eraser_btn, R.id.text_btn, R.id.undo, R.id.redo})
    public void onPanelItemClick(View v) {
        switch (v.getId()) {
            case R.id.select_btn:
            case R.id.handwriting_btn:
            case R.id.eraser_btn:
            case R.id.text_btn:
            case R.id.color_picker_btn:
            case R.id.undo:
            case R.id.redo:
                mBoardController.handlePanelItemClick(v);
                break;
            case R.id.back_btn:
                getFragmentManager().popBackStackImmediate();
                break;

            default:
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ClassroomEngine.getEngine().addEvenListener(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        ClassroomEngine.getEngine().removeEvenListener(this);
        super.onDestroyView();
        mBoardController.hideWhiteboardLayout();
        if (mWhiteBoardPanel != null) {
            mWhiteBoardPanel.animate().cancel();
        }
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


    @Override
    public void receivedEvent(String event, Object object) {
        if(Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_BOARD).equals(event)){
//            LogUtil.d("----qz----"+((SyncBoardReceive)object).data.layer.id);
//            LogUtil.d("data="+((SyncBoardReceive)object).data.layer.shape.data+"----qz---end----");
            mBoardController.onReceive((SyncBoardReceive) object);
        }
    }
}
