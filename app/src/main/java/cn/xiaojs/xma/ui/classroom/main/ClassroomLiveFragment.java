package cn.xiaojs.xma.ui.classroom.main;
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
 * Date:2017/5/11
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Toast;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaResponse;
import cn.xiaojs.xma.model.socket.room.OpenMediaReceive;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.live.OnStreamChangeListener;
import cn.xiaojs.xma.ui.classroom.live.VideoController;
import cn.xiaojs.xma.ui.classroom.page.OnPhotoDoodleShareListener;
import cn.xiaojs.xma.ui.classroom.page.OnSettingChangedListener;
import cn.xiaojs.xma.ui.classroom.page.PhotoDoodleFragment;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.ExitPeerTalkListener;
import cn.xiaojs.xma.ui.classroom.talk.OnTalkItemClickListener;
import cn.xiaojs.xma.ui.classroom.talk.TalkManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkPresenter;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.BitmapUtils;

public abstract class ClassroomLiveFragment extends BaseFragment implements
        OnSettingChangedListener,
        OnStreamChangeListener,
        BackPressListener,
        FrameCapturedCallback,
        OnPhotoDoodleShareListener,
        ExitPeerTalkListener,
        TalkManager.OnTalkMsgReceived,
        SocketManager.OnSocketListener,
        OnTalkItemClickListener {

    protected final static int ANIM_HIDE_TIMEOUT = 3500; //s
    protected final static int BTN_PRESS_INTERVAL = 1000; //ms

    protected String mTicket;

    protected ClassroomController mClassroomController;
    protected VideoController mVideoController;
    protected TalkPresenter mFullScreenTalkPresenter;

    protected TipsHelper mTipsHelper;
    protected TimeProgressHelper mTimeProgressHelper;

    protected long mCountTime;
    protected String mIndividualName;
    protected StreamingResponse mIndividualResponseBody;

    private Map<String, FadeAnimListener> mFadeAnimListeners;
    private List<ViewPropertyAnimator> mViewPropertyAnimators;

    private CommonDialog mAgreeOpenCamera;

    protected int mSlideViewWidth;
    protected int mSlideViewHeight;

    protected Handler mHandler;
    protected ClassroomEngine classroomEngine;
    protected CtlSession mCtlSession;

    private String peerAccountId;

    protected long individualStreamDuration;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        TalkManager.getInstance().registerMsgReceiveListener(this);
        ClassroomController.getInstance(context).setStackFragment(this);
        ClassroomController.getInstance(context).registerBackPressListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        ClassroomController.getInstance(mContext).setStackFragment(null);
        ClassroomController.getInstance(mContext).unregisterBackPressListener(this);
        TalkManager.getInstance().unregisterMsgReceiveListener(this);
    }

    @Override
    protected void init() {
        initParams();
        initView();
        initData();
    }

    protected void initParams() {
        Bundle data = getArguments();
        if (data != null) {
        }

        mHandler = new Handler();
        classroomEngine = ClassroomEngine.getEngine();
        mCtlSession = classroomEngine.getCtlSession();
        mTicket = classroomEngine.getTicket();

        mClassroomController = ClassroomController.getInstance(mContext);
        mFadeAnimListeners = new HashMap<String, FadeAnimListener>();
        mViewPropertyAnimators = new ArrayList<ViewPropertyAnimator>();
    }

    /**
     * 是否是竖屏
     */
    protected boolean isPortrait() {
        if (getActivity() == null)
            return false;

        return getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    /**
     * 切换横竖屏
     */
    protected void toggleLandPortrait() {
        mContext.setRequestedOrientation(isPortrait() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected String getLessonTitle() {
        return ClassroomEngine.getEngine().getRoomTitle();
    }

    protected FadeAnimListener getAnimListener(String name) {
        if (mFadeAnimListeners == null) {
            return null;
        }

        if (mFadeAnimListeners.containsKey(name)) {
            return mFadeAnimListeners.get(name);
        }

        FadeAnimListener animListener = new FadeAnimListener();
        mFadeAnimListeners.put(name, animListener);

        return animListener;
    }

    protected boolean isAnimating() {
        if (mFadeAnimListeners != null) {
            for (Map.Entry<String, FadeAnimListener> entry : mFadeAnimListeners.entrySet()) {
                FadeAnimListener listener = entry.getValue();
                if (listener.isAnimating()) {
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean isAnimViewShow() {
        if (mFadeAnimListeners != null) {
            for (Map.Entry<String, FadeAnimListener> entry : mFadeAnimListeners.entrySet()) {
                FadeAnimListener listener = entry.getValue();
                if (!listener.isShow()) {
                    return false;
                }
            }
        }

        return true;
    }

    protected void postHideAnim() {
        mHandler.removeCallbacks(mHideAnim);
        mHandler.postDelayed(mHideAnim, ANIM_HIDE_TIMEOUT);
    }

    protected void starShowAnim() {
        mHandler.removeCallbacks(mHideAnim);
        startAnim();
    }

    private Runnable mHideAnim = new Runnable() {
        @Override
        public void run() {
            if (!isAnimating() && isAnimViewShow()) {
                startAnim();
            }
        }
    };

    protected void startAnimation(View view, String name, int animMode, int animSets, AnimData data) {
        if (view == null) {
            return;
        }

        FadeAnimListener listener = getAnimListener(name);
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

        if (listener != null) {
            viewPropertyAnimator.setListener(listener.with(view).play(animMode)).start();
        }
    }

    protected void hideAnim(View view, String key) {
        startAnimation(view,
                key,
                FadeAnimListener.MODE_ANIM_HIDE,
                FadeAnimListener.ANIM_ALPHA,
                new AnimData(0));
    }

    protected void showAnim(View view, String key) {
        startAnimation(view,
                key,
                FadeAnimListener.MODE_ANIM_SHOW,
                FadeAnimListener.ANIM_ALPHA,
                new AnimData(1));
    }

    /**
     * 个人推流
     */
    protected void individualPublishStream() {
        showProgress(true);
        ClassroomEngine.getEngine().claimStream(Live.StreamMode.AV,
                new EventCallback<ClaimReponse>() {

            @Override
            public void onSuccess(ClaimReponse claimReponse) {
                cancelProgress();
                onIndividualPublishCallback();
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showOpenMediaDlg(final OpenMediaReceive receive) {
        if (mAgreeOpenCamera == null) {
            mAgreeOpenCamera = new CommonDialog(mContext);
            mAgreeOpenCamera.setTitle(R.string.open_camera_tips);
            mAgreeOpenCamera.setDesc(R.string.agree_open_camera);
            mAgreeOpenCamera.setCancelable(false);

            mAgreeOpenCamera.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mAgreeOpenCamera.dismiss();
                    onPeerPublishCallback(receive);

                }
            });

            mAgreeOpenCamera.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mAgreeOpenCamera.dismiss();
                    //mVideoController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER);
                    //发送拒绝事件
                    sendRefuseMedia();
                }
            });
        }

        mAgreeOpenCamera.show();
    }

    public void showShareBoardDlg(final ShareboardReceive receive) {
        final CommonDialog queryDlg = new CommonDialog(mContext);
        queryDlg.setTitle(R.string.open_camera_tips);
        queryDlg.setDesc(R.string.agree_open_camera);
        queryDlg.setCancelable(false);

        queryDlg.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                queryDlg.dismiss();

                sendShareboardAck(receive.from,true,receive.board.id);
                onAcceptShareBoard(receive);

            }
        });

        queryDlg.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                queryDlg.dismiss();
                //mVideoController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER);
                sendShareboardAck(receive.from,false,receive.board.id);
            }
        });

        queryDlg.show();
    }


    /**
     * 申请打开学生视频
     */
    protected void applyOpenStuVideo(final String accountId) {
        if (XiaojsConfig.DEBUG) {
            Toast.makeText(mContext, "apply open student video: " + accountId, Toast.LENGTH_SHORT).show();
        }

//        if (mUserMode != Constants.UserMode.TEACHING) {
//            return;
//        }

        //FIXME 需要验证状态、权限和身份

        classroomEngine.openMedia(accountId, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                peerAccountId = accountId;

                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(mContext, R.string.seend_one_to_one_ok, Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {

                if (Errors.MEDIA_ALREADY_OPENED.equals(errorCode)) {
                    String tips = mContext.getString(R.string.cr_peer_live_occupy_tips, ClassroomBusiness.getNameByAccountId(accountId));
                    Toast.makeText(mContext, tips, Toast.LENGTH_LONG).show();
                } else if (Errors.PENDING_FOR_OPEN_ACK.equals(errorCode)) {
                    Toast.makeText(mContext, R.string.has_send_one_to_noe_tips, Toast.LENGTH_LONG).show();
                } else if (Errors.NOT_ON_LIVE.equals(errorCode)) {
                    Toast.makeText(mContext, R.string.send_one_to_one_offline_tips, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, R.string.send_one_to_one_failed, Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    protected void sendCloseMedia() {


        classroomEngine.closeMedia(peerAccountId, new EventCallback<CloseMediaResponse>() {
            @Override
            public void onSuccess(CloseMediaResponse closeMediaResponse) {
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(mContext, "close open peer to peer video success", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                Toast.makeText(mContext, "关闭失败", Toast.LENGTH_LONG).show();
            }
        });
    }


    protected void sendRefuseMedia() {
        classroomEngine.mediaFeedback(Live.MediaStatus.FAILED_DUE_TO_DENIED, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(mContext, "你已拒绝", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(mContext, "拒绝失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    protected void sendShareboardAck(String to, final boolean accept, String board) {

        classroomEngine.shareboardAck(to,accept, board, new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {


                if (XiaojsConfig.DEBUG && !accept) {
                    Toast.makeText(mContext, "你已拒绝", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {

            }
        });
    }


    @Override
    public void onPhotoShared(final Attendee attendee, final Bitmap bitmap) {
        if (mContext instanceof FragmentActivity) {
            //exit
            Fragment fragment = ((FragmentActivity) mContext).getSupportFragmentManager().findFragmentById(R.id.photo_doodle_layout);
            if (fragment instanceof PhotoDoodleFragment) {
                ((ClassroomActivity) mContext).getSupportFragmentManager()
                        .beginTransaction()
                        .remove(fragment)
                        .commitAllowingStateLoss();
            }


            //send msg
            if (bitmap != null) {
                new AsyncTask<Integer, Integer, String>() {

                    @Override
                    protected String doInBackground(Integer... params) {
                        //resize max length to 800
                        Bitmap resizeBmp = BitmapUtils.resizeDownBySideLength(bitmap, Constants.SHARE_IMG_SIZE, false);
                        return ClassroomBusiness.bitmapToBase64(resizeBmp);
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            onImgSendShare(attendee, result);
                        } else {
                            cancelProgress();
                        }
                    }
                }.execute(0);
            }
        }
    }

    protected void onImgSendShare(final Attendee attendee, final String bitmap) {
        //only send once
        if (attendee == null || TextUtils.isEmpty(attendee.accountId)) {
            //send to multi talk
            TalkManager.getInstance().sendImg(bitmap);
        } else {
            //send to peer talk
            TalkManager.getInstance().sendImg(attendee.accountId, bitmap);
        }
    }

    @Override
    public void onExitTalk(int type) {

    }

    protected void setControllerBtnStyle(String liveState) {

    }

    @Override
    public void onFrameCaptured(Bitmap bitmap) {
        ClassroomController.getInstance(mContext).enterPhotoDoodleByBitmap(bitmap, this);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mVideoController != null) {
            mVideoController.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mVideoController != null) {
            mVideoController.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mHideAnim);
        }

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelAllAnim();

        if (mHandler != null) {
            mHandler.removeCallbacks(null);
            mHandler = null;
        }

        if (mVideoController != null) {
            mVideoController.onDestroy();
        }
        if (mTimeProgressHelper != null) {
            mTimeProgressHelper.release();
        }

        if (mFullScreenTalkPresenter != null) {
            mFullScreenTalkPresenter.release();
        }

    }

    /**
     * 显示/隐藏talk列表
     */
    protected abstract void showHideTalk();

    /**
     * 开启动画
     */
    protected abstract void startAnim();

    /**
     * 取消所有动画
     */
    private void cancelAllAnim() {
        if (mViewPropertyAnimators != null) {
            for (ViewPropertyAnimator animator : mViewPropertyAnimators) {
                animator.cancel();
            }
            mViewPropertyAnimators.clear();
        }

        if (mFadeAnimListeners != null) {
            mFadeAnimListeners.clear();
        }
    }

    /**
     * 初始化view
     */
    protected abstract void initView();

    protected abstract void initData();


    public void playStream(String url) {

    }

    public void publishStream(String url) {

    }

    protected void onIndividualPublishCallback() {

    }

    protected void onPeerPublishCallback(OpenMediaReceive openMediaNotify) {

    }

    protected void onAcceptShareBoard(ShareboardReceive shareboard) {

    }

}
