package cn.xiaojs.xma.ui.classroom.live;
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
 * Date:2017/3/1
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.ui.classroom.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.bean.FeedbackStatus;
import cn.xiaojs.xma.ui.classroom.bean.OpenMediaNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingExpirationNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.StreamingStartedNotify;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.XjsUtils;

public class PlayVideoController extends VideoController {
    private CommonDialog mAgreeOpenCamera;

    public PlayVideoController(Context context, View root, OnStreamStateChangeListener listener) {
        super(context, root, listener);
        mUser = Constants.User.STUDENT;
        listenerSocket();
    }

    @Override
    protected void init(View root) {
        /**
         * 用于播放直播推流或个人推流
         */
        mPlayView = (PlayerTextureView) root.findViewById(R.id.play_video);
    }

    @Override
    protected void listenerSocket() {
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA), mReceiveOpenMedia);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED), mReceiveMediaAborted);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA), mReceiveMediaClosed);
    }

    @Override
    public void confirmPublishStream(boolean confirm) {
        //update param
        /*FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mPublishView.getLayoutParams();
        if (mPublishType == StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER) {
            params.width = mPeerStreamViewWidth;
            params.height = mPeerStreamViewHeight;
            params.gravity = Gravity.LEFT | Gravity.TOP;
            params.topMargin = mPeerStreamViewTopMargin;
            params.leftMargin = mPeerStreamViewMargin;
        } else if (mPublishType == StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL) {
            params.width = FrameLayout.LayoutParams.MATCH_PARENT;
            params.height = FrameLayout.LayoutParams.MATCH_PARENT;
            params.gravity = Gravity.CENTER;
            params.leftMargin = 0;
            params.bottomMargin = 0;
        }

        if (mPublishView != null) {
            mPublishView.setVisibility(View.VISIBLE);
            mPublishView.setPath(mPublishStreamUrl);
            mPublishView.resume();
        }*/
    }

    @Override
    public void confirmPlayStream(boolean confirm) {
        mStreamPlaying = true;
        mPlayView.setVisibility(View.VISIBLE);
        mPlayView.setPath(mPlayStreamUrl);
        mPlayView.resume();
        mPlayView.showLoading(true);
        if (mStreamListener != null) {
            mStreamListener.onStreamStarted(mUser, mPlayType, mExtraData);
        }

        if (mPlayView instanceof PlayerTextureView && mNeedStreamRePlaying) {
            mNeedStreamRePlaying = false;
            mPlayView.delayHideLoading();
        }
    }

    /**
     * 暂停推流
     */
    @Override
    public void pausePublishStream(final int type) {
        /*if (mPublishView != null) {
            if (ClassroomBusiness.NETWORK_NONE == ClassroomBusiness.getCurrentNetwork(mContext)) {
                mNeedStreamRePublishing = true;
                if (mStreamListener != null) {
                    mStreamListener.onStreamStopped(mUser, type, null);
                }
            } else {
                //send stopped stream
                SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.STREAMING_STOPPED),
                        new SocketManager.AckListener() {
                            @Override
                            public void call(Object... args) {
                                if (args != null && args.length > 0) {
                                    StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                                    if (response.result) {
                                        mNeedStreamRePublishing = true;
                                        if (mStreamListener != null) {
                                            mStreamListener.onStreamStopped(mUser, type, null);
                                        }
                                    }
                                }
                            }
                        });
            }

            mStreamPublishing = false;
            mPublishView.pause();
            mPublishView.setVisibility(View.GONE);
        }*/
    }

    /**
     * 暂停播放流
     */
    public void pausePlayStream(int type) {
        if (mPlayView != null) {
            mStreamPlaying = false;
            mNeedStreamRePlaying = true;
            mPlayView.pause();
            mPlayView.showLoading(false);
            mPlayView.setVisibility(View.GONE);

            if (mStreamListener != null) {
                mStreamListener.onStreamStopped(mUser, type, null);
            }
        }
    }

    @Override
    public void takeVideoFrame(FrameCapturedCallback callback) {
        if (mPlayView.getVisibility() == View.VISIBLE) {
            Bitmap bmp = mPlayView.getPlayer().getTextureView().getBitmap();
            if (callback != null) {
                callback.onFrameCaptured(bmp);
            }
        }/* else if (mPublishView.getVisibility() == View.VISIBLE) {
            mPublishView.captureOriginalFrame(callback);
        } */else {
            if (callback != null) {
                callback.onFrameCaptured(null);
            }
        }
    }

    @Override
    public void onSteamStateChanged(StreamingState streamingState, Object data) {
        switch (streamingState) {
            case STREAMING:
                mNeedStreamRePublishing = false;
                FeedbackStatus fbStatus = new FeedbackStatus();
                fbStatus.status = Live.MediaStatus.READY;
                int eventType = mPublishType == StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL ? Su.EventType.STREAMING_STARTED : Su.EventType.MEDIA_FEEDBACK;
                SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, eventType), fbStatus, new SocketManager.AckListener() {
                    @Override
                    public void call(Object... args) {
                        if (args != null && args.length > 0) {
                            StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                            if (response != null && response.result) {
                                mStreamPublishing = true;
                                if (mStreamListener != null) {
                                    mStreamListener.onStreamStarted(mUser, mPublishType, null);
                                    muteOrUnmute();
                                }

                            } else {
                                //暂停推流
                                if (!mPausePublishByToggleResolution && !mStreamPublishing) {
                                    pausePublishStream(mPublishType);
                                }

                                mPausePublishByToggleResolution = false;
                            }
                        } else {
                            //暂停推流
                            if (!mPausePublishByToggleResolution && !mStreamPublishing) {
                                pausePublishStream(mPublishType);
                            }

                            mPausePublishByToggleResolution = false;
                        }
                    }
                });
                break;
        }
    }

    private SocketManager.EventListener mReceiveOpenMedia = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            if (mAgreeOpenCamera == null) {
                mAgreeOpenCamera = new CommonDialog(mContext);
                int width = DeviceUtil.getScreenWidth(mContext) / 2;
                int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                mAgreeOpenCamera.setDialogLayout(width, height);
                mAgreeOpenCamera.setTitle(R.string.open_camera_tips);
                mAgreeOpenCamera.setDesc(R.string.agree_open_camera);

                mAgreeOpenCamera.setOnRightClickListener(new CommonDialog.OnClickListener() {
                    @Override
                    public void onClick() {
                        mAgreeOpenCamera.dismiss();
                        if (args != null && args.length > 0) {
                            OpenMediaNotify openMediaNotify = ClassroomBusiness.parseSocketBean(args[0], OpenMediaNotify.class);
                            if (openMediaNotify != null) {
                                publishStream(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER, openMediaNotify.publishUrl);
                            }
                        }
                    }
                });
            }

            mAgreeOpenCamera.show();
        }
    };

    private SocketManager.EventListener mReceiveMediaAborted = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            pausePublishStream(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER);
        }
    };

    private SocketManager.EventListener mReceiveMediaClosed = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            pausePublishStream(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER);
        }
    };

    @Override
    protected void onStreamingStarted(Object... args) {
        if (args != null && args.length > 0) {
            StreamingStartedNotify startedNotify = ClassroomBusiness.parseSocketBean(args[0], StreamingStartedNotify.class);
            if (startedNotify != null) {
                int type = StreamType.TYPE_STREAM_PLAY;
                if (mContext instanceof ClassroomActivity) {
                    String state = ((ClassroomActivity) mContext).getLiveState();
                    if (Live.LiveSessionState.LIVE.equals(state)) {
                        type = StreamType.TYPE_STREAM_PLAY;
                    } else if (Live.LiveSessionState.SCHEDULED.equals(state)
                            || Live.LiveSessionState.FINISHED.equals(state)) {
                        type = StreamType.TYPE_STREAM_PLAY_INDIVIDUAL;
                    }
                }
                playStream(type, startedNotify.RTMPPlayUrl, startedNotify.finishOn);
            }
        }
    }

    @Override
    protected void onStreamingStopped(Object... args) {
        if (args != null && args.length > 0) {
            StreamingNotify notify = ClassroomBusiness.parseSocketBean(args[0], StreamingNotify.class);
            if (notify != null) {
                pausePlayStream(mPlayType);
            }
        }
    }

    protected void onStreamingStoppedByExpired(Object... args) {
        if (args != null && args.length > 0) {
            StreamingExpirationNotify notify = ClassroomBusiness.parseSocketBean(args[0], StreamingExpirationNotify.class);
            if (notify != null) {
                pausePublishStream(mPublishType);
            }
        }
    }

    protected void onStreamingReclaimed(Object... args) {
        if (args != null && args.length > 0) {
            StreamingNotify notify = ClassroomBusiness.parseSocketBean(args[0], StreamingNotify.class);
            if (notify != null) {
                pausePublishStream(mPublishType);
            }
        }
    }

    @Override
    public void openOrCloseCamera() {

    }

    @Override
    public void muteOrUnmute() {
        /*SharedPreferences sf = XjsUtils.getSharedPreferences();
        boolean audioOpen = sf.getBoolean(Constants.KEY_MICROPHONE_OPEN, true);
        if (audioOpen) {
            if (mPublishView != null && mPublishView.isMute()) {
                mPublishView.mute();
            }
        } else {
            if (mPublishView != null && !mPublishView.isMute()) {
                mPublishView.mute();
            }
        }*/
    }

    @Override
    public void togglePublishResolution() {
        /*if (mPublishView != null) {
            mPublishView.togglePublishResolution();
        }*/
    }
}
