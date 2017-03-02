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
import android.widget.Toast;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.ui.classroom.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.bean.OpenMediaNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.FeedbackStatus;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.DeviceUtil;

public class StudentVideoManager extends VideoManager {
    private CommonDialog mAgreeOpenCamera;

    public StudentVideoManager(Context context, View root) {
        super(context, root);
    }

    @Override
    protected void init(View root) {
        mPlayView = (PlayerTextureView) root.findViewById(R.id.live_video);
        mPlayView.setVisibility(View.VISIBLE);

        mPublishView = (LiveRecordView) root.findViewById(R.id.stu_preview_video);
    }

    @Override
    protected void listenerSocket() {
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED), mReceiveStreamStarted);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA), mReceiveOpenMedia);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED), mReceiveMediaAborted);
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
    public void takeVideoFrame(FrameCapturedCallback callback) {
        Bitmap bmp = mPlayView.getPlayer().getTextureView().getBitmap();
        if (callback != null) {
            callback.onFrameCaptured(bmp);
        }
    }

    @Override
    public void onSteamStateChanged(StreamingState streamingState, Object data) {
        FeedbackStatus fbStatus = new FeedbackStatus();
        fbStatus.status = Live.MediaStatus.READY;
        SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.MEDIA_FEEDBACK), fbStatus, new SocketManager.AckListener() {
            @Override
            public void call(Object... args) {
                if (args != null && args.length > 0) {
                    StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                    if (response != null && response.result) {
                        Toast.makeText(mContext, "学生发送feedback 成功", Toast.LENGTH_LONG).show();
                    } else {
                        onPause();
                    }
                } else {
                    onPause();
                }
            }
        });
    }


    private SocketManager.EventListener mReceiveStreamStarted = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            Toast.makeText(mContext, "学生端流开始", Toast.LENGTH_LONG).show();
        }
    };

    private SocketManager.EventListener mReceiveOpenMedia = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            Toast.makeText(mContext, "请求打开学生视频", Toast.LENGTH_LONG).show();
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
            Toast.makeText(mContext, "学生端流被中断", Toast.LENGTH_LONG).show();
        }
    };
}
