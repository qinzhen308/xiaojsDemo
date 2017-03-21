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
    private LiveRecordView mIndividualPublishView;
    private String mIndividualPublishUrl;

    public StudentVideoController(Context context, View root, OnStreamStateChangeListener listener) {
        super(context, root, listener);
        mUser = Constants.User.STUDENT;
        listenerSocket();
    }

    @Override
    protected void init(View root) {
        mPlayView = (PlayerTextureView) root.findViewById(R.id.live_video);
        mPlayView.setVisibility(View.GONE);

        mPublishView = (LiveRecordView) root.findViewById(R.id.stu_publish_video);
        mIndividualPublishView = (LiveRecordView) root.findViewById(R.id.publish_video);

        if (mIndividualPublishView != null) {
            mIndividualPublishView.setOnStreamingStateListener(mStreamingStateChangedListener);
        }
    }

    @Override
    protected void listenerSocket() {
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA), mReceiveOpenMedia);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED), mReceiveMediaAborted);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA), mReceiveMediaClosed);
    }

    public void publishStream(String url) {
        publishStream(url, true);
    }

    @Override
    public void publishStream(String url, boolean live) {
        if (live) {
            //接收老师一对一推流（默认小窗口）
            super.publishStream(url, live);
        } else {
            //个人推流（全屏）
            mIndividualPublishUrl = url;
            if (mStreamListener != null) {
                mLive = live;
                mStreamListener.onStreamPublish(this);
            }
        }
    }

    @Override
    public void confirmPublishStream(boolean confirm) {
        if (mLive) {
            super.confirmPublishStream(confirm);
            mPublishView.setVisibility(View.VISIBLE);
        } else {
            mStreamPublishing = true;
            mIndividualPublishView.setPath(mIndividualPublishUrl);
            mIndividualPublishView.setVisibility(View.VISIBLE);
            if (!mInitIndividualPublishVideo) {
                mIndividualPublishView.start();
            } else {
                mIndividualPublishView.resume();
            }
        }
    }

    /**
     * 暂停推流
     */
    @Override
    public void pausePublishStream() {
        if (mLive) {
            if (mPublishView != null) {
                if (mStreamPublishing) {
                    if (ClassroomBusiness.NETWORK_NONE == ClassroomBusiness.getCurrentNetwork(mContext)) {
                        mNeedStreamRePublishing = true;
                        if (mStreamListener != null) {
                            mStreamListener.onStreamStopped(mUser, OnStreamStateChangeListener.TYPE_STREAM_PUBLISH_MEDIA_FEEDBACK);
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
                                                    mStreamListener.onStreamStopped(mUser, OnStreamStateChangeListener.TYPE_STREAM_PUBLISH_MEDIA_FEEDBACK);
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }
                mStreamPublishing = false;
                mPublishView.pause();
            }
        } else {
            if (mIndividualPublishView != null) {
                if (mStreamPublishing) {
                    if (ClassroomBusiness.NETWORK_NONE == ClassroomBusiness.getCurrentNetwork(mContext)) {
                        mStreamPublishing = false;
                        if (mStreamListener != null) {
                            mStreamListener.onStreamStopped(mUser, OnStreamStateChangeListener.TYPE_STREAM_PUBLISH_INDIVIDUAL);
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
                                            mStreamListener.onStreamStopped(mUser, OnStreamStateChangeListener.TYPE_STREAM_PUBLISH_INDIVIDUAL);
                                        }
                                    }
                                }
                            }
                        });
                    }
                    mIndividualPublishView.pause();
                    mIndividualPublishView.setVisibility(View.GONE);
                }
                mStreamPublishing = false;
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIndividualPublishView != null && !mIndividualPublishView.isResume()) {
            mIndividualPublishView.resume();
        }
    }

    @Override
    public void takeVideoFrame(FrameCapturedCallback callback) {
        Bitmap bmp = mPlayView.getPlayer().getTextureView().getBitmap();
        if (callback != null) {
            callback.onFrameCaptured(bmp);
        }
    }

    @Override
    public void onSteamStateChanged(StreamingState streamingState, Object data) {
        switch (streamingState) {
            case STREAMING:
                FeedbackStatus fbStatus = new FeedbackStatus();
                fbStatus.status = Live.MediaStatus.READY;
                int eventType = mLive ? Su.EventType.MEDIA_FEEDBACK : Su.EventType.STREAMING_STARTED;
                SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, eventType), fbStatus, new SocketManager.AckListener() {
                    @Override
                    public void call(Object... args) {
                        if (args != null && args.length > 0) {
                            StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                            if (response != null && response.result) {
                                if (mStreamListener != null) {
                                    mStreamListener.onStreamStarted(mUser, mLive ? OnStreamStateChangeListener.TYPE_STREAM_PUBLISH_MEDIA_FEEDBACK
                                            : OnStreamStateChangeListener.TYPE_STREAM_PUBLISH_INDIVIDUAL);
                                }
                            } else {
                                //暂停推流
                                pausePublishStream();
                            }
                        } else {
                            //暂停推流
                            pausePublishStream();
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
                                publishStream(openMediaNotify.publishUrl);
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
            pausePublishStream();
        }
    };

    private SocketManager.EventListener mReceiveMediaClosed = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            pausePublishStream();
        }
    };

    @Override
    protected void onStreamingStarted(Object... args) {
        if (args != null && args.length > 0) {
            StreamingStartedNotify startedNotify = ClassroomBusiness.parseSocketBean(args[0], StreamingStartedNotify.class);
            if (startedNotify != null) {
                playStream(startedNotify.RTMPPlayUrl);
                if (mStreamListener != null) {
                    mStreamListener.onStreamStarted(mUser, OnStreamStateChangeListener.TYPE_STREAM_PLAY);
                }
            }
        }
    }

    @Override
    protected void onStringingStopped(Object... args) {
        if (args != null && args.length > 0) {
            pausePlayStream();
        }
    }
}
