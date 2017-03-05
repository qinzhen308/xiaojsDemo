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
import android.view.View;
import android.widget.Toast;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.ui.classroom.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.bean.MediaFeedback;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.StreamingStartedNotify;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;

public class TeacherVideoController extends VideoController {
    public TeacherVideoController(Context context, View root) {
        super(context, root);
        listenerSocket();
    }

    @Override
    protected void init(View root) {
        mPlayView = (PlayerTextureView) root.findViewById(R.id.stu_player_video);

        mPublishView = (LiveRecordView) root.findViewById(R.id.publish_video);
        mPublishView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void listenerSocket() {
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_FEEDBACK), mReceiveFeedback);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED), mStreamingStartedListener);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STOPPED), mStreamingStoppedListener);
    }

    @Override
    public void onResume() {
        mPlayView.resume();
        mPublishView.resume();
    }

    @Override
    public void onPause() {
        mPlayView.pause();
        mPublishView.pause();
    }

    @Override
    public void onDestroy() {
        mPlayView.destroy();
        mPublishView.destroy();
    }

    @Override
    public void playStream(String url) {
        mPlayView.setVisibility(View.VISIBLE);
        super.playStream(url);
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
                                Toast.makeText(mContext, "老师推流开始", Toast.LENGTH_LONG).show();
                            } else {
                                onPause();
                            }
                        } else {
                            onPause();
                        }
                    }
                });
                break;
        }
    }

    private SocketManager.EventListener mReceiveFeedback = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            Toast.makeText(mContext, "收到 feedback", Toast.LENGTH_LONG).show();
            if (args != null && args.length > 0) {
                MediaFeedback response = ClassroomBusiness.parseSocketBean(args[0], MediaFeedback.class);
                if (response != null && response.playUrl != null) {
                    playStream(response.playUrl);
                }
            }
        }
    };

    private SocketManager.EventListener mStreamingStartedListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                Toast.makeText(mContext, "流开始", Toast.LENGTH_LONG).show();
                StreamingStartedNotify startedNotify = ClassroomBusiness.parseSocketBean(args[0], StreamingStartedNotify.class);
                if (startedNotify != null) {
                    mPlayStreamUrl = startedNotify.RTMPPlayUrl;
                    playStream(startedNotify.RTMPPlayUrl);
                }
            }
        }
    };

    private SocketManager.EventListener mStreamingStoppedListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                Toast.makeText(mContext, "流停止", Toast.LENGTH_LONG).show();
                onPause();
            }
        }
    };
}