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
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.bean.OpenMedia;
import cn.xiaojs.xma.ui.classroom.bean.OpenMediaNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingMode;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.SyncStateResponse;
import cn.xiaojs.xma.ui.classroom.live.OnStreamChangeListener;
import cn.xiaojs.xma.ui.classroom.live.StreamType;
import cn.xiaojs.xma.ui.classroom.live.VideoController;
import cn.xiaojs.xma.ui.classroom.page.OnPhotoDoodleShareListener;
import cn.xiaojs.xma.ui.classroom.page.OnSettingChangedListener;
import cn.xiaojs.xma.ui.classroom.page.PhotoDoodleFragment;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.ContactManager;
import cn.xiaojs.xma.ui.classroom.talk.ExitPeerTalkListener;
import cn.xiaojs.xma.ui.classroom.talk.TalkManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkPresenter;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.BitmapUtils;

public abstract class ClassroomLiveFragment extends BaseFragment implements
        OnSettingChangedListener,
        OnStreamChangeListener,
        BackPressListener,
        FrameCapturedCallback,
        OnPhotoDoodleShareListener,
        ExitPeerTalkListener,
        TalkManager.OnTalkMsgReceived {

    protected final static int ANIM_HIDE_TIMEOUT = 3500; //s
    protected final static int BTN_PRESS_INTERVAL = 1000; //ms

    protected CtlSession mCtlSession;
    protected String mTicket;
    protected Constants.UserMode mUserMode = Constants.UserMode.PARTICIPANT;

    protected ClassroomController mClassroomController;
    protected VideoController mVideoController;
    protected TalkPresenter mFullScreenTalkPresenter;

    protected TipsHelper mTipsHelper;
    protected TimeProgressHelper mTimeProgressHelper;

    protected long mCountTime;
    protected long mIndividualStreamDuration;
    protected String mOriginSteamState;
    protected String mIndividualName;
    protected StreamingResponse mIndividualResponseBody;

    private Map<String, FadeAnimListener> mFadeAnimListeners;
    private List<ViewPropertyAnimator> mViewPropertyAnimators;

    private CommonDialog mAgreeOpenCamera;

    protected String mPlayUrl;

    protected int mSlideViewWidth;
    protected int mSlideViewHeight;

    protected Handler mHandler;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        TalkManager.getInstance().registerMsgReceiveListener(this);
        ClassroomController.getInstance().setStackFragment(this);
        ClassroomController.getInstance().registerBackPressListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        ClassroomController.getInstance().setStackFragment(null);
        ClassroomController.getInstance().unregisterBackPressListener(this);
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
        mCtlSession = LiveCtlSessionManager.getInstance().getCtlSession();
        mUserMode = ClassroomBusiness.getUserByCtlSession(mCtlSession);
        mTicket = LiveCtlSessionManager.getInstance().getTicket();

        mClassroomController = ClassroomController.getInstance();
        mFadeAnimListeners = new HashMap<String, FadeAnimListener>();
        mViewPropertyAnimators = new ArrayList<ViewPropertyAnimator>();

        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_STATE), mSyncStateListener);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA), mReceiveOpenMedia);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED), mReceiveMediaAborted);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA), mReceiveMediaClosed);
    }

    /**
     * 是否是竖屏
     */
    protected boolean isPortrait() {
        return getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    /**
     * 切换横竖屏
     */
    protected void toggleLandPortrait() {
        mContext.setRequestedOrientation(isPortrait() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected FadeAnimListener getAnimListener(String name) {
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

        viewPropertyAnimator.setListener(listener.with(view).play(animMode)).start();
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
        StreamingMode streamMode = new StreamingMode();
        streamMode.mode = Live.StreamMode.AV;
        SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.CLAIM_STREAMING), streamMode, new SocketManager.AckListener() {
            @Override
            public void call(final Object... args) {
                cancelProgress();
                if (args != null && args.length > 0) {
                    String oldLiveSate = LiveCtlSessionManager.getInstance().getLiveState();
                    StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                    boolean needPublish = false;
                    if (response.result) {
                        mIndividualResponseBody = response;
                        needPublish = true;
                    } else {
                        if (Errors.STREAM_ALREADY_CLAIMED.equals(response.ec)) {
                            needPublish = true;
                        }
                    }

                    if (needPublish) {
                        mOriginSteamState = oldLiveSate;
                        mIndividualStreamDuration = mIndividualResponseBody != null ? mIndividualResponseBody.finishOn : 0;
                        onIndividualPublishCallback(mIndividualResponseBody);
                    }
                } else {
                    if (XiaojsConfig.DEBUG) {
                        Toast.makeText(mContext, "claim streaming fail", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private SocketManager.EventListener mSyncStateListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                SyncStateResponse syncState = ClassroomBusiness.parseSocketBean(args[0], SyncStateResponse.class);
                if (syncState != null) {
                    onSyncStateChanged(syncState);
                }
            }
        }
    };

    /**
     * 一对一
     */
    private SocketManager.EventListener mReceiveOpenMedia = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
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
                                onPeerPublishCallback(openMediaNotify);
                            }
                        }
                    }
                });

                mAgreeOpenCamera.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        mVideoController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER);
                    }
                });
            }

            mAgreeOpenCamera.show();
        }
    };


    private SocketManager.EventListener mReceiveMediaAborted = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            onStreamStopped(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER, null);
        }
    };

    private SocketManager.EventListener mReceiveMediaClosed = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            onStreamStopped(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER, null);
        }
    };

    /**
     * 申请打开学生视频
     */
    protected void applyOpenStuVideo(final String accountId) {
        if (XiaojsConfig.DEBUG) {
            Toast.makeText(mContext, "apply open student video: " + accountId, Toast.LENGTH_SHORT).show();
        }

        if (mUserMode != Constants.UserMode.TEACHING) {
            return;
        }

        OpenMedia openMedia = new OpenMedia();
        openMedia.to = accountId;
        if (!ContactManager.getInstance().hasPeer2PeerStream(accountId)) {
            SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.OPEN_MEDIA), openMedia, new SocketManager.AckListener() {
                @Override
                public void call(final Object... args) {
                    if (args != null && args.length > 0) {
                        StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                        if (response != null && response.result) {
                            if (XiaojsConfig.DEBUG) {
                                Toast.makeText(mContext, "open peer to peer video", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            });
        } else {
            SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.CLOSE_MEDIA), openMedia, new SocketManager.AckListener() {
                @Override
                public void call(final Object... args) {
                    if (args != null && args.length > 0) {
                        StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                        if (response != null && response.result) {
                            ContactManager.getInstance().putPeer2PeerSteamToSet(accountId);
                            if (XiaojsConfig.DEBUG) {
                                Toast.makeText(mContext, "close peer to peer video", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            });
        }
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
                        .commit();
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
        ClassroomController.getInstance().enterPhotoDoodleByBitmap(bitmap, this);
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

        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_STATE));
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA));
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED));
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA));
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

    protected abstract void onSyncStateChanged(SyncStateResponse syncState);

    public void playStream(String url) {

    }

    public void publishStream(String url) {

    }

    protected void onIndividualPublishCallback(StreamingResponse response) {

    }

    protected void onPeerPublishCallback(OpenMediaNotify openMediaNotify) {

    }

}
