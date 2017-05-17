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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Toast;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.OnSettingChangedListener;
import cn.xiaojs.xma.ui.classroom.bean.StreamingMode;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.SyncStateResponse;
import cn.xiaojs.xma.ui.classroom.live.OnStreamStateChangeListener;
import cn.xiaojs.xma.ui.classroom.live.StreamType;
import cn.xiaojs.xma.ui.classroom.live.VideoController;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;

public abstract class ClassroomLiveFragment extends BaseFragment implements OnSettingChangedListener,
        OnStreamStateChangeListener , BackPressListener, FrameCapturedCallback {
    protected CtlSession mCtlSession;
    protected String mTicket;
    protected Constants.User mUser = Constants.User.STUDENT;

    protected ClassroomController mClassroomController;
    protected VideoController mVideoController;
    protected TalkPresenter mFullScreenTalkPresenter;

    protected TipsHelper mTipsHelper;
    protected TimeProgressHelper mTimeProgressHelper;

    protected long mCountTime;
    protected long mIndividualStreamDuration;
    protected String mBeforeClamSteamState;
    protected String mIndividualName;

    private Map<String, FadeAnimListener> mFadeAnimListeners;
    private List<ViewPropertyAnimator> mViewPropertyAnimators;

    protected String mPlayUrl;

    protected int mSlideViewWidth;
    protected int mSlideViewHeight;

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

        mCtlSession = LiveCtlSessionManager.getInstance().getCtlSession();
        mUser = ClassroomBusiness.getUserByCtlSession(mCtlSession);
        mTicket = LiveCtlSessionManager.getInstance().getTicket();

        mClassroomController = ClassroomController.getInstance();
        mFadeAnimListeners = new HashMap<String, FadeAnimListener>();
        mViewPropertyAnimators = new ArrayList<ViewPropertyAnimator>();

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mSlideViewWidth = (int) (0.4F * displayMetrics.heightPixels);
        mSlideViewHeight = displayMetrics.heightPixels - (int) (displayMetrics.widthPixels / Constants.VIDEO_VIEW_RATIO);

        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_STATE), mSyncStateListener);
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

    protected FadeAnimListener getAnimListener(@NonNull Class<?> cls) {
        String type = cls.getSimpleName();
        if (mFadeAnimListeners.containsKey(type)) {
            return mFadeAnimListeners.get(type);
        }

        FadeAnimListener animListener = new FadeAnimListener();
        mFadeAnimListeners.put(type, animListener);

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

    protected void startAnimation(View view, int animMode, int animSets, AnimData data) {
        FadeAnimListener listener = getAnimListener(view.getClass());
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

    protected void hideAnim(View view) {
        startAnimation(view,
                FadeAnimListener.MODE_ANIM_HIDE,
                FadeAnimListener.ANIM_ALPHA,
                new AnimData(0));
    }

    protected void showAnim(View view) {
        startAnimation(view,
                FadeAnimListener.MODE_ANIM_SHOW,
                FadeAnimListener.ANIM_ALPHA,
                new AnimData(1));
    }

    /**
     * 个人推流
     */
    protected void individualPublishStream() {
        StreamingMode streamMode = new StreamingMode();
        streamMode.mode = Live.StreamMode.AV;
        SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.CLAIM_STREAMING), streamMode, new SocketManager.AckListener() {
            @Override
            public void call(final Object... args) {
                //cancelProgress();
                if (args != null && args.length > 0) {
                    String oldLiveSate = LiveCtlSessionManager.getInstance().getLiveState();
                    StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                    if (response.result) {
                        mBeforeClamSteamState = oldLiveSate;
                        mIndividualStreamDuration = response.finishOn;
                        LiveCtlSessionManager.getInstance().updateCtlSessionState(Live.LiveSessionState.INDIVIDUAL);
                        setControllerBtnStyle(Live.LiveSessionState.INDIVIDUAL);
                        if (XiaojsConfig.DEBUG) {
                            Toast.makeText(mContext, "claim streaming succ", Toast.LENGTH_SHORT).show();
                        }
                        onIndividualPublishCallback(response);
                    } else {
                        cancelProgress();
                        if (XiaojsConfig.DEBUG) {
                            Toast.makeText(mContext, "claim streaming fail:" + response.details, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    cancelProgress();
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

    protected String getTxtString(int type) {
        String txt = "";
        switch (type) {
            case StreamType.TYPE_STREAM_PUBLISH:
                txt = "推送流";
                break;
            case StreamType.TYPE_STREAM_PLAY:
                txt = "播放流";
                break;
            case StreamType.TYPE_STREAM_PLAY_PEER_TO_PEER:
                txt = "一对一播流";
                break;
            case StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER:
                txt = "一对一推流";
                break;
            case StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL:
                txt = "个人推送流";
                break;
            case StreamType.TYPE_STREAM_PLAY_INDIVIDUAL:
                txt = "个人播放流";
                break;
        }

        return txt;
    }

    protected void setControllerBtnStyle(String liveState) {

    }


    @Override
    public void onFrameCaptured(Bitmap bitmap) {
        ClassroomController.getInstance().enterPhotoDoodleByBitmap(bitmap);
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
    public void onDestroy() {
        super.onDestroy();

        cancelAllAnim();

        if (mVideoController != null) {
            mVideoController.onDestroy();
        }
        if (mTimeProgressHelper != null) {
            mTimeProgressHelper.release();
        }

        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_STATE));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        ClassroomController.getInstance().setStackFragment(this);
        ClassroomController.getInstance().registerBackPressListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ClassroomController.getInstance().setStackFragment(this);
        ClassroomController.getInstance().unregisterBackPressListener(this);
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

}
