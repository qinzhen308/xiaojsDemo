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
import android.view.ViewGroup;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.ui.classroom.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.Constants;
import cn.xiaojs.xma.ui.classroom.bean.FeedbackStatus;
import cn.xiaojs.xma.ui.classroom.bean.OpenMediaNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.StreamingStartedNotify;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.DeviceUtil;

public class StudentVideoController extends VideoController {
    private CommonDialog mAgreeOpenCamera;

    public StudentVideoController(Context context, View root, OnStreamStateChangeListener listener) {
        super(context, root, listener);
        mUser = Constants.User.STUDENT;
        listenerSocket();
    }

    @Override
    protected void init(View root) {
        /**
         * 用于播放直播推流或个人推流
         */
        mPlayView = (PlayerTextureView) root.findViewById(R.id.live_video);
        /**
         * 用于推送一对一流（默认小窗口）
         */
        mPublishView = (LiveRecordView) root.findViewById(R.id.stu_publish_video);
        /**
         * 用于个人推流的
         */
        mIndividualView = (LiveRecordView) root.findViewById(R.id.publish_video);
    }

    @Override
    protected void listenerSocket() {
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA), mReceiveOpenMedia);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED), mReceiveMediaAborted);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA), mReceiveMediaClosed);
    }

    @Override
    public void confirmPublishStream(boolean confirm) {
        if (mPublishType == StreamType.TYPE_STREAM_PEER_TO_PEER) {
            mStreamPublishing = true;
            mPublishView.setPath(mPublishStreamUrl);
            mPublishView.setVisibility(View.VISIBLE);
            mPublishView.resume();
        } else if (mPublishType == StreamType.TYPE_STREAM_INDIVIDUAL) {
            mStreamPublishing = true;
            mIndividualView.setPath(mPublishStreamUrl);
            mIndividualView.setVisibility(View.VISIBLE);
            mIndividualView.resume();
        }
    }

    @Override
    public void confirmPlayStream(boolean confirm) {
        mStreamPlaying = true;
        mPlayView.setPath(mPlayStreamUrl);
        mPlayView.setVisibility(View.VISIBLE);
        mPlayView.resume();
        mPlayView.showLoading(true);
    }

    /**
     * 暂停推流
     */
    @Override
    public void pausePublishStream(int type) {
        if (type == StreamType.TYPE_STREAM_PEER_TO_PEER) {
            if (mPublishView != null) {
                if (mStreamPublishing) {
                    if (ClassroomBusiness.NETWORK_NONE == ClassroomBusiness.getCurrentNetwork(mContext)) {
                        mNeedStreamRePublishing = true;
                        if (mStreamListener != null) {
                            mStreamListener.onStreamStopped(mUser, StreamType.TYPE_STREAM_PEER_TO_PEER);
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
                                                    mStreamListener.onStreamStopped(mUser, StreamType.TYPE_STREAM_PEER_TO_PEER);
                                                }
                                            }
                                        }
                                    }
                                });
                    }

                    mStreamPublishing = false;
                    mPublishView.pause();
                    mPublishView.setVisibility(View.GONE);
                }
            }
        } else if (type == StreamType.TYPE_STREAM_INDIVIDUAL){
            if (mIndividualView != null) {
                if (mStreamPublishing) {
                    if (ClassroomBusiness.NETWORK_NONE == ClassroomBusiness.getCurrentNetwork(mContext)) {
                        mStreamPublishing = false;
                        if (mStreamListener != null) {
                            mStreamListener.onStreamStopped(mUser, StreamType.TYPE_STREAM_INDIVIDUAL);
                        }
                    } else {
                        //send stopped stream
                        SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.STREAMING_STOPPED), new SocketManager.AckListener() {
                            @Override
                            public void call(Object... args) {
                                if (args != null && args.length > 0) {
                                    StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                                    if (response.result) {
                                        if (mStreamListener != null) {
                                            mStreamListener.onStreamStopped(mUser, StreamType.TYPE_STREAM_INDIVIDUAL);
                                        }
                                    }
                                }
                            }
                        });
                    }
                    mStreamPublishing = false;
                    mIndividualView.pause();
                    mIndividualView.setVisibility(View.GONE);
                }
            }

        }
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
                mStreamListener.onStreamStopped(Constants.User.STUDENT, type);
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
        } else if (mIndividualView.getVisibility() == View.VISIBLE) {
            ((LiveRecordView)mIndividualView).captureOriginalFrame(callback);
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
                FeedbackStatus fbStatus = new FeedbackStatus();
                fbStatus.status = Live.MediaStatus.READY;
                int eventType = mPublishType == StreamType.TYPE_STREAM_INDIVIDUAL ? Su.EventType.STREAMING_STARTED : Su.EventType.MEDIA_FEEDBACK;
                SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, eventType), fbStatus, new SocketManager.AckListener() {
                    @Override
                    public void call(Object... args) {
                        if (args != null && args.length > 0) {
                            StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                            if (response != null && response.result) {
                                if (mStreamListener != null) {
                                    mStreamListener.onStreamStarted(mUser, mPublishType);
                                }
                            } else {
                                //暂停推流
                                pausePublishStream(mPublishType);
                            }
                        } else {
                            //暂停推流
                            pausePublishStream(mPublishType);
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
                                publishStream(StreamType.TYPE_STREAM_PEER_TO_PEER, openMediaNotify.publishUrl);
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
            pausePublishStream(StreamType.TYPE_STREAM_PEER_TO_PEER);
        }
    };

    private SocketManager.EventListener mReceiveMediaClosed = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            pausePublishStream(StreamType.TYPE_STREAM_PEER_TO_PEER);
        }
    };

    @Override
    protected void onStreamingStarted(Object... args) {
        if (args != null && args.length > 0) {
            StreamingStartedNotify startedNotify = ClassroomBusiness.parseSocketBean(args[0], StreamingStartedNotify.class);
            if (startedNotify != null) {
                playStream(StreamType.TYPE_STREAM_PLAY, startedNotify.RTMPPlayUrl);
                if (mStreamListener != null) {
                    mStreamListener.onStreamStarted(mUser, StreamType.TYPE_STREAM_PLAY);
                }
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
