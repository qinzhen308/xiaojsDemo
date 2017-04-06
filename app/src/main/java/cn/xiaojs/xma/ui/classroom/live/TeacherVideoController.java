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
import android.text.TextUtils;
import android.view.View;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.ui.classroom.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.Constants;
import cn.xiaojs.xma.ui.classroom.bean.MediaFeedback;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.StreamingStartedNotify;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;

public class TeacherVideoController extends VideoController {
    private int loadingSize = 36;
    private int loadingDesc = 20;

    public TeacherVideoController(Context context, View root, OnStreamStateChangeListener listener) {
        super(context, root, listener);
        mUser = Constants.User.TEACHER;
        listenerSocket();
        loadingSize = context.getResources().getDimensionPixelSize(R.dimen.px36);
        loadingDesc = context.getResources().getDimensionPixelSize(R.dimen.font_20px);
    }

    @Override
    protected void init(View root) {
        /**
         * 用于播放和学生的一对一视频（默认小窗口）
         */
        mPlayView = (PlayerTextureView) root.findViewById(R.id.tea_player_video);
        /**
         * 用于推流视频（直播推流或个人推流）
         */
        mPublishView = (LiveRecordView) root.findViewById(R.id.publish_video);
        /**
         * 用于个人推流的播放
         */
        mIndividualView = (PlayerTextureView) root.findViewById(R.id.live_video);
    }

    @Override
    protected void listenerSocket() {
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_FEEDBACK), mReceiveFeedback);
    }

    @Override
    public void confirmPublishStream(boolean confirm) {
        mStreamPublishing = true;
        mPublishView.setPath(mPublishStreamUrl);
        mPublishView.setVisibility(View.VISIBLE);
        boolean init = mPublishType == StreamType.TYPE_STREAM_PUBLISH ? mInitPublishVideo : mInitIndividualPublishVideo;
        if (!init) {
            mPublishView.start();
        } else {
            mPublishView.resume();
        }
    }

    @Override
    public void confirmPlayStream(boolean confirm) {
        mStreamPlaying = true;
        if (mPlayType == StreamType.TYPE_STREAM_PEER_TO_PEER) {
            mPlayView.setPath(mPlayStreamUrl);
            mPlayView.setVisibility(View.VISIBLE);
            mPlayView.resume();
            mPlayView.showLoading(true);

            if (!TextUtils.isEmpty(mPlayStreamUrl)) {
                mPlayView.setVisibility(View.VISIBLE);
                mPlayView.showLoading(true, loadingSize, loadingDesc);
            }

            if (mStreamListener != null) {
                mStreamListener.onStreamStarted(mUser, StreamType.TYPE_STREAM_PEER_TO_PEER);
            }
        } else if (mPlayType == StreamType.TYPE_STREAM_INDIVIDUAL) {
            mIndividualView.setPath(mPlayStreamUrl);
            mIndividualView.setVisibility(View.VISIBLE);
            mIndividualView.resume();
            mIndividualView.showLoading(true);

            if (!TextUtils.isEmpty(mPlayStreamUrl)) {
                mIndividualView.setVisibility(View.VISIBLE);
                mIndividualView.showLoading(true, loadingSize, loadingDesc);
            }

            if (mStreamListener != null) {
                mStreamListener.onStreamStarted(mUser, StreamType.TYPE_STREAM_INDIVIDUAL);
            }
        }
    }

    /**
     * 暂停推流
     */
    @Override
    public void pausePublishStream(final int type) {
        if (mPublishView != null) {
            if (mStreamPublishing) {
                if (ClassroomBusiness.NETWORK_NONE == ClassroomBusiness.getCurrentNetwork(mContext)) {
                    mNeedStreamRePublishing = true;
                    if (mStreamListener != null) {
                        mStreamListener.onStreamStopped(mUser, type);
                    }
                } else {
                    //send stopped stream
                    SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.STREAMING_STOPPED),
                            new SocketManager.AckListener() {
                                @Override
                                public void call(Object... args) {
                                    if (args != null && args.length > 0) {
                                        mNeedStreamRePublishing = true;
                                        if (mStreamListener != null) {
                                            mStreamListener.onStreamStopped(mUser, type);
                                        }
                                    }
                                }
                            });
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
        if (type == StreamType.TYPE_STREAM_PEER_TO_PEER) {
            mStreamPlaying = false;
            mNeedStreamRePlaying = true;
            mPlayView.pause();
            mPlayView.showLoading(false);
            mPlayView.setVisibility(View.GONE);

            if (mStreamListener != null) {
                mStreamListener.onStreamStopped(Constants.User.STUDENT, type);
            }
        } else if (type == StreamType.TYPE_STREAM_INDIVIDUAL) {
            mStreamPlaying = false;
            mNeedStreamRePlaying = true;
            mIndividualView.pause();
            mIndividualView.showLoading(false);
            mIndividualView.setVisibility(View.GONE);


            if (mStreamListener != null) {
                mStreamListener.onStreamStopped(Constants.User.STUDENT, type);
            }
        }
    }

    @Override
    public void takeVideoFrame(FrameCapturedCallback callback) {
        if (mIndividualView.getVisibility() == View.VISIBLE) {
            Bitmap bmp = ((PlayerTextureView)mIndividualView).getPlayer().getTextureView().getBitmap();
            if (callback != null) {
                callback.onFrameCaptured(bmp);
            }
        } else if (mPublishView.getVisibility() == View.VISIBLE) {
            mPublishView.captureOriginalFrame(callback);
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
                SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.STREAMING_STARTED), new SocketManager.AckListener() {
                    @Override
                    public void call(final Object... args) {
                        if (args != null && args.length > 0) {
                            StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                            if (response != null && response.result) {
                                if (mStreamListener != null) {
                                    mStreamListener.onStreamStarted(mUser, mPublishType);
                                }
                            } else {
                                pausePublishStream(mPublishType);
                            }
                        } else {
                            pausePublishStream(mPublishType);
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
                    playStream(StreamType.TYPE_STREAM_PEER_TO_PEER, response.playUrl);
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
                playStream(StreamType.TYPE_STREAM_INDIVIDUAL, startedNotify.RTMPPlayUrl);
            }
        }
    }

    @Override
    protected void onStringingStopped(Object... args) {
        if (args != null && args.length > 0) {
            pausePlayStream(mPlayType);
        }
    }
}
