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
import android.graphics.Bitmap;
import android.view.View;

import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.ui.classroom.bean.FeedbackStatus;
import cn.xiaojs.xma.ui.classroom.bean.StreamingExpirationNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.StreamingStartedNotify;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.main.LiveCtlSessionManager;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;

public class PlayVideoController extends VideoController {

    public PlayVideoController(Context context, View root, OnStreamChangeListener listener) {
        super(context, root, listener);
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
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED), mReceiveMediaAborted);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA), mReceiveMediaClosed);
    }

    @Override
    public void confirmPublishStream(boolean confirm) {
        //do nothing
    }

    @Override
    public void confirmPlayStream(boolean confirm) {
        mStreamPlaying = true;
        mPlayView.setVisibility(View.VISIBLE);
        mPlayView.setPath(mPlayStreamUrl);
        mPlayView.resume();
        mPlayView.showLoading(true);
        if (mStreamChangeListener != null) {
            mStreamChangeListener.onStreamStarted(mPlayType, mExtraData);
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
        if (type == StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER) {
            if (ClassroomBusiness.NETWORK_NONE == ClassroomBusiness.getCurrentNetwork(mContext)) {
                mNeedStreamRePublishing = true;
                if (mStreamChangeListener != null) {
                    mStreamChangeListener.onStreamStopped(type, null);
                }
            } else {
                if (mStreamPublishing) {
                    //send stopped stream
                    SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.STREAMING_STOPPED),
                            new SocketManager.IAckListener() {
                                @Override
                                public void call(Object... args) {
                                    if (args != null && args.length > 0) {
                                        StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                                        if (response.result) {
                                            mNeedStreamRePublishing = true;
                                            if (mStreamChangeListener != null) {
                                                mStreamChangeListener.onStreamStopped(type, null);
                                            }
                                        }
                                    }
                                }
                            });
                } else {
                    if (mStreamChangeListener != null) {
                        mStreamChangeListener.onStreamStopped(type, null);
                    }
                }
            }
        }
        mStreamPublishing = false;
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

            if (mStreamChangeListener != null) {
                mStreamChangeListener.onStreamStopped(type, null);
            }
        }
    }

    @Override
    public void takeVideoFrame(FrameCapturedCallback callback) {
        if (mPlayView.getVisibility() == View.VISIBLE && mStreamPlaying) {
            PLVideoTextureView plVideoTextureView = mPlayView.getPlayer();
            Bitmap bmp = null;
            if (plVideoTextureView != null) {
                bmp = plVideoTextureView.getTextureView().getBitmap();
            }
            if (callback != null) {
                callback.onFrameCaptured(bmp);
            }
        } else {
            if (callback != null) {
                callback.onFrameCaptured(null);
            }
        }
    }

    @Override
    public void onSteamStateChanged(StreamingState streamingState, Object data) {
        switch (streamingState) {
            case STREAMING:
                if (mOnDestroy || mCancelPublish) {
                    return;
                }

                mNeedStreamRePublishing = false;
                FeedbackStatus fbStatus = new FeedbackStatus();
                fbStatus.status = Live.MediaStatus.READY;
                int eventType = mPublishType == StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL ? Su.EventType.STREAMING_STARTED : Su.EventType.MEDIA_FEEDBACK;
                SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, eventType), fbStatus, new SocketManager.IAckListener() {
                    @Override
                    public void call(Object... args) {
                        if (args != null && args.length > 0) {
                            StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                            if (response != null && response.result) {
                                mStreamPublishing = true;
                                if (mStreamChangeListener != null) {
                                    mStreamChangeListener.onStreamStarted(mPublishType, null);
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
                String state = LiveCtlSessionManager.getInstance().getLiveState();
                if (Live.LiveSessionState.LIVE.equals(state)
                        || Live.LiveSessionState.PENDING_FOR_JOIN.equals(state)
                        || Live.LiveSessionState.PENDING_FOR_LIVE.equals(state)) {
                    type = StreamType.TYPE_STREAM_PLAY;
                } else if (ClassroomBusiness.canIndividual(LiveCtlSessionManager.getInstance().getCtlSession())) {
                    type = StreamType.TYPE_STREAM_PLAY_INDIVIDUAL;
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
    }

    @Override
    public void togglePublishResolution() {

    }

    @Override
    protected void offSocketListener() {
        super.offSocketListener();
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED), mReceiveMediaAborted);
        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA), mReceiveMediaClosed);
    }
}
