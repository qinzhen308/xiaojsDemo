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

    private PlayerTextureView mIndividualPlayView;

    public TeacherVideoController(Context context, View root, OnStreamStateChangeListener listener) {
        super(context, root, listener);
        mUser = Constants.User.TEACHER;
        listenerSocket();
        loadingSize = context.getResources().getDimensionPixelSize(R.dimen.px36);
        loadingDesc = context.getResources().getDimensionPixelSize(R.dimen.font_20px);
    }

    @Override
    protected void init(View root) {
        mPlayView = (PlayerTextureView) root.findViewById(R.id.tea_player_video);

        mPublishView = (LiveRecordView) root.findViewById(R.id.publish_video);
        mPublishView.setVisibility(View.VISIBLE);

        mIndividualPlayView = (PlayerTextureView) root.findViewById(R.id.live_video);
        mIndividualPlayView.setVisibility(View.GONE);
    }

    @Override
    protected void listenerSocket() {
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_FEEDBACK), mReceiveFeedback);
    }

    @Override
    public void confirmPlayStream(boolean confirm) {
        super.confirmPlayStream(confirm);
        mPlayView.setVisibility(View.VISIBLE);
        if (!TextUtils.isEmpty(mPlayStreamUrl)) {
            mPlayView.showLoading(true, loadingSize, loadingDesc);
        }

        if (mStreamListener != null) {
            mStreamListener.onStreamStarted(mUser, OnStreamStateChangeListener.TYPE_STREAM_PLAY_MEDIA_FEEDBACK);
        }
    }

    @Override
    public void takeVideoFrame(FrameCapturedCallback callback) {
        mPublishView.captureOriginalFrame(callback);
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
                                    mStreamListener.onStreamStarted(mUser, OnStreamStateChangeListener.TYPE_STREAM_PUBLISH);
                                }
                            } else {
                                pausePublishStream();
                            }
                        } else {
                            pausePublishStream();
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
                    playStream(response.playUrl);
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
                playStream(startedNotify.RTMPPlayUrl);
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
