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

import com.orhanobut.logger.Logger;
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
import cn.xiaojs.xma.model.Error;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.bean.MediaFeedback;
import cn.xiaojs.xma.ui.classroom.bean.OpenMedia;
import cn.xiaojs.xma.ui.classroom.bean.OpenMediaNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingMode;
import cn.xiaojs.xma.ui.classroom.bean.StreamingQuality;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.SyncClassStateResponse;
import cn.xiaojs.xma.ui.classroom.bean.SyncStateResponse;
import cn.xiaojs.xma.ui.classroom.live.OnStreamChangeListener;
import cn.xiaojs.xma.ui.classroom.live.StreamType;
import cn.xiaojs.xma.ui.classroom.live.VideoController;
import cn.xiaojs.xma.ui.classroom.page.OnPhotoDoodleShareListener;
import cn.xiaojs.xma.ui.classroom.page.OnSettingChangedListener;
import cn.xiaojs.xma.ui.classroom.page.PhotoDoodleFragment;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.ExitPeerTalkListener;
import cn.xiaojs.xma.ui.classroom.talk.OnTalkItemClickListener;
import cn.xiaojs.xma.ui.classroom.talk.TalkManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkPresenter;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.BitmapUtils;

import static cn.xiaojs.xma.ui.classroom.live.VideoController.STREAM_MEDIA_CLOSED;

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
    protected String mPublishUrl;

    protected int mSlideViewWidth;
    protected int mSlideViewHeight;

    protected Handler mHandler;

    private String peerAccountId;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        TalkManager.getInstance().registerMsgReceiveListener(this);
        ClassroomController.getInstance().setStackFragment(this);
        ClassroomController.getInstance().registerBackPressListener(this);
        ClassroomController.getInstance().registerSocketConnectListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        ClassroomController.getInstance().setStackFragment(null);
        ClassroomController.getInstance().unregisterBackPressListener(this);
        TalkManager.getInstance().unregisterMsgReceiveListener(this);
        ClassroomController.getInstance().unregisterSocketConnectListener(this);
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
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_CLASS_STATE), mSyncClassStateListener);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA), mReceiveOpenMedia);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED), mReceiveMediaAborted);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA), mReceiveMediaClosed);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.REMIND_FINALIZATION), mReceivedRemind);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.REFRESH_STREAMING_QUALITY), mReceivedStreamingQuality);
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
        String title = "";

        if (mCtlSession.cls != null) {

            if (mCtlSession.ctl != null) {
                title = mCtlSession.ctl.title;
            } else {
                title = mCtlSession.cls.title;
            }

        } else {

            if (mCtlSession.ctl != null) {
                title = mCtlSession.ctl.title;
            }
        }


        return title;
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
        StreamingMode streamMode = new StreamingMode();
        streamMode.mode = Live.StreamMode.AV;
        SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.CLAIM_STREAMING), streamMode, new SocketManager.IAckListener() {
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

            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.SYNC_STATE**");
            }

            if (args != null && args.length > 0) {
                SyncStateResponse syncState = ClassroomBusiness.parseSocketBean(args[0], SyncStateResponse.class);
                if (syncState != null) {
                    onSyncStateChanged(syncState);
                }
            }
        }
    };

    private SocketManager.EventListener mSyncClassStateListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.SYNC_CLASS_STATE**");
            }

            if (args != null && args.length > 0) {
                SyncClassStateResponse syncState = ClassroomBusiness.parseSocketBean(args[0], SyncClassStateResponse.class);
                if (syncState != null) {
                    onSyncClassStateChanged(syncState);
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

            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.OPEN_MEDIA**");
            }

            if (mAgreeOpenCamera == null) {
                mAgreeOpenCamera = new CommonDialog(mContext);
                mAgreeOpenCamera.setTitle(R.string.open_camera_tips);
                mAgreeOpenCamera.setDesc(R.string.agree_open_camera);
                mAgreeOpenCamera.setCancelable(false);

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
                        mAgreeOpenCamera.dismiss();
                        //mVideoController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER);
                        //发送拒绝事件
                        sendRefuseMedia();
                    }
                });
            }

            mAgreeOpenCamera.show();
        }
    };


    private SocketManager.EventListener mReceiveMediaAborted = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.MEDIA_ABORTED**");
            }

            onStreamStopped(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER, STREAM_MEDIA_CLOSED);
        }
    };

    private SocketManager.EventListener mReceiveMediaClosed = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.CLOSE_MEDIA**");
            }
            LiveCtlSessionManager.getInstance().setOne2one(false);

            onStreamStopped(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER, STREAM_MEDIA_CLOSED);
        }
    };

    private SocketManager.EventListener mReceivedRemind = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.REMIND_FINALIZATION**");
            }

            onRemindFinalization();
        }
    };

    private SocketManager.EventListener mReceivedStreamingQuality = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.REFRESH_STREAMING_QUALITY**");
            }

            if (args != null && args.length > 0) {
                StreamingQuality streamingQuality = ClassroomBusiness.parseSocketBean(args[0],
                        StreamingQuality.class);

                onStreamingQualityChanged(streamingQuality);
            }

        }
    };

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

        OpenMedia openMedia = new OpenMedia();
        openMedia.to = accountId;
        SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.OPEN_MEDIA), openMedia, new SocketManager.IAckListener() {
            @Override
            public void call(final Object... args) {
                if (args != null && args.length > 0) {
                    StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                    if (response != null) {
                        if (response.result) {

                            peerAccountId = accountId;

                            if (XiaojsConfig.DEBUG) {
                                Toast.makeText(mContext, R.string.seend_one_to_one_ok, Toast.LENGTH_LONG).show();
                            }

                            LiveCtlSessionManager.getInstance().setOne2one(true);

                        } else if (Errors.MEDIA_ALREADY_OPENED.equals(response.ec)) {
                            String tips = mContext.getString(R.string.cr_peer_live_occupy_tips, ClassroomBusiness.getNameByAccountId(accountId));
                            Toast.makeText(mContext, tips, Toast.LENGTH_LONG).show();
                            LiveCtlSessionManager.getInstance().setOne2one(false);
                        } else if (Errors.PENDING_FOR_OPEN_ACK.equals(response.ec)) {
                            Toast.makeText(mContext, R.string.has_send_one_to_noe_tips, Toast.LENGTH_LONG).show();
                        } else if (Errors.NOT_ON_LIVE.equals(response.ec)) {
                            Toast.makeText(mContext, R.string.send_one_to_one_offline_tips, Toast.LENGTH_LONG).show();
                            LiveCtlSessionManager.getInstance().setOne2one(false);
                        } else {
                            Toast.makeText(mContext, R.string.send_one_to_one_failed, Toast.LENGTH_LONG).show();
                            LiveCtlSessionManager.getInstance().setOne2one(false);
                        }
                    }
                }
            }
        });
    }

    protected void sendCloseMedia() {

        LiveCtlSessionManager.getInstance().setOne2one(false);

        OpenMedia media = new OpenMedia();
        media.to = peerAccountId;
        SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.CLOSE_MEDIA),
                media,
                new SocketManager.IAckListener() {

                    @Override
                    public void call(Object... args) {
                        if (args != null && args.length > 0) {
                            StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                            if (response != null) {
                                if (response.result) {
                                    if (XiaojsConfig.DEBUG) {
                                        Toast.makeText(mContext, "close open peer to peer video success", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    //String tips = mContext.getString(R.string.cr_peer_live_occupy_tips, ClassroomBusiness.getNameByAccountId(accountId));
                                    Toast.makeText(mContext, "关闭失败", Toast.LENGTH_LONG).show();
                                }
                            }

                        }
                    }
                });
    }


    protected void sendRefuseMedia() {

        LiveCtlSessionManager.getInstance().setOne2one(false);

        MediaFeedback media = new MediaFeedback();
        media.status = Live.MediaStatus.FAILED_DUE_TO_DENIED;
        SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.MEDIA_FEEDBACK),
                media,
                new SocketManager.IAckListener() {

                    @Override
                    public void call(Object... args) {
                        if (args != null && args.length > 0) {
                            StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                            if (response != null) {
                                if (response.result) {
                                    if (XiaojsConfig.DEBUG) {
                                        Toast.makeText(mContext, "你已拒绝", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    //String tips = mContext.getString(R.string.cr_peer_live_occupy_tips, ClassroomBusiness.getNameByAccountId(accountId));
                                    if (XiaojsConfig.DEBUG) {
                                        Toast.makeText(mContext, "拒绝失败", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }

                        }
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

        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_STATE), mSyncStateListener);
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_CLASS_STATE), mSyncClassStateListener);
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA), mReceiveOpenMedia);
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED), mReceiveMediaAborted);
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA), mReceiveMediaClosed);
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.REMIND_FINALIZATION), mReceivedRemind);
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.REFRESH_STREAMING_QUALITY), mReceivedStreamingQuality);
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

    protected abstract void onSyncClassStateChanged(SyncClassStateResponse syncState);

    public void playStream(String url) {

    }

    public void publishStream(String url) {

    }

    protected void onIndividualPublishCallback(StreamingResponse response) {

    }

    protected void onPeerPublishCallback(OpenMediaNotify openMediaNotify) {

    }

}
