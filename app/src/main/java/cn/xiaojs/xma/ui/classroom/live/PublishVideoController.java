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
import android.view.View;

import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.ui.classroom.bean.FeedbackStatus;
import cn.xiaojs.xma.ui.classroom.bean.MediaFeedback;
import cn.xiaojs.xma.ui.classroom.bean.OpenMedia;
import cn.xiaojs.xma.ui.classroom.bean.StreamingExpirationNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.StreamingStartedNotify;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.util.XjsUtils;

public class PublishVideoController extends VideoController {

    private int loadingSize = 36;
    private int loadingDesc = 20;

    public PublishVideoController(Context context, View root, OnStreamChangeListener listener) {
        super(context, root, listener);
        listenerSocket();
        loadingSize = context.getResources().getDimensionPixelSize(R.dimen.px36);
        loadingDesc = context.getResources().getDimensionPixelSize(R.dimen.font_20px);
    }

    @Override
    protected void init(View root) {
        /**
         * 用户播放个人推流，用于播放和学生的一对一视频（默认小窗口）
         */
        mPlayView = (PlayerTextureView) root.findViewById(R.id.play_video);
        /**
         * 用于推流视频（直播推流,个人推流,一对一推流）
         */
        mPublishView = (LiveRecordView) root.findViewById(R.id.publish_video);
    }

    @Override
    protected void listenerSocket() {
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_FEEDBACK), mReceiveFeedback);
    }

    @Override
    public void confirmPublishStream(boolean confirm) {
        if (mPublishView != null) {
            mPublishView.setVisibility(View.VISIBLE);
            mPublishView.setPath(mPublishStreamUrl);
            mPublishView.resume();
        }
    }

    @Override
    public void confirmPlayStream(boolean confirm) {
        mStreamPlaying = true;
        mPlayView.setVisibility(View.VISIBLE);
        mPlayView.setPath(mPlayStreamUrl);
        mPlayView.resume();
        mPlayView.showLoading(true);


        if (mStreamChangeListener != null) {
            mStreamChangeListener.onStreamStarted(mPlayType, mPlayStreamUrl, mExtraData);
        }

        if (mPlayView instanceof PlayerTextureView && mNeedStreamRePlaying) {
            mNeedStreamRePlaying = false;
            mPlayView.delayHideLoading();
        }

        mPlayView.showLoading(true, loadingSize, loadingDesc);
    }

    /**
     * 暂停推流
     */
    @Override
    public void pausePublishStream(final int type) {
        if (mPublishView != null) {
            if (ClassroomBusiness.NETWORK_NONE == ClassroomBusiness.getCurrentNetwork(mContext)) {
                mNeedStreamRePublishing = true;
                if (mStreamChangeListener != null) {
                    mStreamChangeListener.onStreamStopped(type, null);
                }
            } else {
                if (mStreamPublishing) {

                    if (type == StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER) {
                        //TODO 学生关闭一对一流后，需要通知老师端，老师端1对1窗口要关闭
//                        OpenMedia media = new OpenMedia();
//                        media.to = AccountDataManager.getAccountID(mContext);
//                        SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.CLOSE_MEDIA), media,
//                                new SocketManager.IAckListener() {
//                                    @Override
//                                    public void call(Object... args) {
//                                        if (args != null && args.length > 0) {
//                                            StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
//                                            if (response.result) {
//                                                mNeedStreamRePublishing = true;
//                                                if (mStreamChangeListener != null) {
//                                                    mStreamChangeListener.onStreamStopped(type, null);
//                                                }
//                                            }
//                                        }
//                                    }
//                                });
                    } else {
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
                    }


                } else {
                    if (mStreamChangeListener != null) {
                        mStreamChangeListener.onStreamStopped(type, null);
                    }
                }
            }
            mStreamPublishing = false;
            mPublishView.pause();
            mPublishView.setVisibility(View.GONE);
        }
    }

    /**
     * 暂停播放流
     */
    @Override
    public void pausePlayStream(int type) {
        mStreamPlaying = false;
        mNeedStreamRePlaying = true;

        if (mPlayView != null) {
            mPlayView.pause();
            mPlayView.showLoading(false);
            mPlayView.setVisibility(View.GONE);
        }
        if (mStreamChangeListener != null) {
            mStreamChangeListener.onStreamStopped(type, null);
        }
    }

    @Override
    public void takeVideoFrame(FrameCapturedCallback callback) {
        if (mPublishView.getVisibility() == View.VISIBLE && mStreamPublishing) {
            mPublishView.captureOriginalFrame(callback);
        } else if (mPlayView.getVisibility() == View.VISIBLE && mStreamPlaying) {
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
                mNeedStreamRePublishing = false;
                if (mStreamPublishing || mOnDestroy || mCancelPublish) {
                    break;
                }

                int eventType = mPublishType == StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL || mPublishType == StreamType.TYPE_STREAM_PUBLISH ?
                        Su.EventType.STREAMING_STARTED : Su.EventType.MEDIA_FEEDBACK;
                FeedbackStatus fbStatus = new FeedbackStatus();
                fbStatus.status = Live.MediaStatus.READY;
                SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, eventType), fbStatus, new SocketManager.IAckListener() {
                    @Override
                    public void call(final Object... args) {
                        if (args != null && args.length > 0) {
                            StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                            if (response != null && response.result) {
                                if (mStreamChangeListener != null) {
                                    mStreamChangeListener.onStreamStarted(mPublishType, mPublishStreamUrl, null);
                                    muteOrUnmute();
                                }
                                mStreamPublishing = true;
                            } else {
                                if (!mPausePublishByToggleResolution) {
                                    pausePublishStream(mPublishType);
                                }

                                mPausePublishByToggleResolution = false;
                            }
                        } else {
                            if (!mPausePublishByToggleResolution) {
                                pausePublishStream(mPublishType);
                            }

                            mPausePublishByToggleResolution = false;
                        }
                    }
                });
                break;
        }
    }

    private SocketManager.EventListener mReceiveFeedback = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            if (args != null && args.length > 0) {
                MediaFeedback response = ClassroomBusiness.parseSocketBean(args[0], MediaFeedback.class);
                if (response != null && response.playUrl != null) {
                    mPlayStreamUrl = response.playUrl;
                    playStream(StreamType.TYPE_STREAM_PLAY_PEER_TO_PEER, response.playUrl);
                }
            }
        }
    };

    @Override
    protected void onStreamingStarted(Object... args) {
        if (args != null && args.length > 0) {
            StreamingStartedNotify startedNotify = ClassroomBusiness.parseSocketBean(args[0], StreamingStartedNotify.class);
            if (startedNotify != null) {
                mPlayStreamUrl = startedNotify.RTMPPlayUrl;
                mExtraData = startedNotify.finishOn;
                playStream(StreamType.TYPE_STREAM_PLAY_INDIVIDUAL, startedNotify.RTMPPlayUrl, startedNotify.finishOn);
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
                //pausePublishStream(mPublishType);
                if (mStreamChangeListener != null) {
                    mStreamChangeListener.onStreamStopped(mPublishType, STREAM_EXPIRED);
                }
                mStreamPublishing = false;
                mPublishView.pause();
                mPublishView.setVisibility(View.GONE);
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
        SharedPreferences sf = XjsUtils.getSharedPreferences();
        boolean audioOpen = sf.getBoolean(Constants.KEY_MICROPHONE_OPEN, true);
        if (audioOpen) {
            if (mPublishView.isMute()) {
                mPublishView.mute();
            }
        } else {
            if (!mPublishView.isMute()) {
                mPublishView.mute();
            }
        }
    }

    @Override
    public void togglePublishResolution() {
        mPublishView.togglePublishResolution();
    }


    @Override
    protected void offSocketListener() {
        super.offSocketListener();

        SocketManager.off(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_FEEDBACK), mReceiveFeedback);
    }
}
