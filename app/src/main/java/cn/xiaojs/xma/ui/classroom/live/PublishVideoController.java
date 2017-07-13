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
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;
import com.qiniu.pili.droid.streaming.StreamingState;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.CloseMediaResponse;
import cn.xiaojs.xma.model.socket.room.MediaFeedbackReceive;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.model.socket.room.StreamStopReceive;
import cn.xiaojs.xma.model.socket.room.StreamStoppedResponse;
import cn.xiaojs.xma.ui.classroom.bean.StreamingExpirationNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingStartedNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingStoppedNotify;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom2.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.EventListener;
import cn.xiaojs.xma.util.XjsUtils;

public class PublishVideoController extends VideoController implements EventListener {

    private int loadingSize = 36;
    private int loadingDesc = 20;

    public PublishVideoController(Context context, View root, OnStreamChangeListener listener) {
        super(context, root, listener);
        listenerSocket();
        loadingSize = context.getResources().getDimensionPixelSize(R.dimen.px36);
        loadingDesc = context.getResources().getDimensionPixelSize(R.dimen.font_20px);

        ClassroomEngine.getEngine().addEvenListener(this);

    }

    @Override
    public void onDestroy() {
        ClassroomEngine.getEngine().removeEvenListener(this);
        super.onDestroy();
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

                    if (type == CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER) {
                        //TODO 学生关闭一对一流后，需要通知老师端，老师端1对1窗口要关闭
//                        OpenMedia media = new OpenMedia();
//                        media.to = AccountDataManager.getAccountID(mContext);

                        ClassroomEngine.getEngine().closeMedia(null, new EventCallback<CloseMediaResponse>() {
                            @Override
                            public void onSuccess(CloseMediaResponse closeMediaResponse) {
                                mNeedStreamRePublishing = true;
                                if (mStreamChangeListener != null) {
                                    mStreamChangeListener.onStreamStopped(type, null);
                                }
                            }

                            @Override
                            public void onFailed(String errorCode, String errorMessage) {

                                if (XiaojsConfig.DEBUG) {
                                    Logger.d("close media failed");
                                }

                                //失败了也要执行退出操作
                                mNeedStreamRePublishing = true;
                                if (mStreamChangeListener != null) {
                                    mStreamChangeListener.onStreamStopped(type, null);
                                }

                            }
                        });
                    } else {
                        //send stopped stream
                        String csofCurrent = ClassroomEngine.getEngine().getCsOfCurrent();
                        ClassroomEngine.getEngine().stopStreaming(type,csofCurrent,
                                new EventCallback<StreamStoppedResponse>() {
                            @Override
                            public void onSuccess(StreamStoppedResponse streamStoppedResponse) {

                                ClassroomEngine.getEngine().setCsOfCurrent(null);

                                mNeedStreamRePublishing = true;
                                if (mStreamChangeListener != null) {
                                    mStreamChangeListener.onStreamStopped(type, null);
                                }
                            }

                            @Override
                            public void onFailed(String errorCode, String errorMessage) {

                                if (XiaojsConfig.DEBUG) {
                                    Logger.d("stop streaming failed");
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

                if (mPublishType == CTLConstant.StreamingType.PUBLISH_INDIVIDUAL
                        || mPublishType == CTLConstant.StreamingType.PUBLISH_LIVE) {

                    ClassroomEngine.getEngine().startStreaming(new EventCallback<EventResponse>() {
                        @Override
                        public void onSuccess(EventResponse response) {
                            if (mStreamChangeListener != null) {
                                mStreamChangeListener.onStreamStarted(mPublishType, mPublishStreamUrl, null);
                                //FIXME 设置打开后，需要将下面代码打开
                                //muteOrUnmute();
                            }
                            mStreamPublishing = true;
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMessage) {
                            if (!mPausePublishByToggleResolution) {
                                pausePublishStream(mPublishType);
                            }

                            mPausePublishByToggleResolution = false;

                            if (XiaojsConfig.DEBUG) {
                                Logger.d("发送 startStreaming event 失败");
                            }
                        }
                    });
                }else {

                    ClassroomEngine.getEngine().mediaFeedback(Live.MediaStatus.READY, new EventCallback<EventResponse>() {
                        @Override
                        public void onSuccess(EventResponse response) {
                            if (mStreamChangeListener != null) {
                                mStreamChangeListener.onStreamStarted(mPublishType, mPublishStreamUrl, null);
                                //FIXME 设置打开后，需要将下面代码打开
                                //muteOrUnmute();
                            }
                            mStreamPublishing = true;
                        }

                        @Override
                        public void onFailed(String errorCode, String errorMessage) {
                            if (!mPausePublishByToggleResolution) {
                                pausePublishStream(mPublishType);
                            }

                            mPausePublishByToggleResolution = false;

                            if (XiaojsConfig.DEBUG) {
                                Logger.d("发送 mediaFeedback event 失败");
                            }
                        }
                    });
                }
                break;

            case IOERROR:
            case INVALID_STREAMING_URL:
            case AUDIO_RECORDING_FAIL:
                if (mStreamChangeListener != null) {
                    mStreamChangeListener.onStreamException(streamingState, mPublishType, data);
                }
                break;
        }
    }

    @Override
    public void receivedEvent(String event, Object object) {
        if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED)
                .equals(event)) {

            if (object == null) {
                return;
            }

            StreamStartReceive receive = (StreamStartReceive) object;

            mPlayStreamUrl = receive.RTMPPlayUrl;
            mExtraData = receive.finishOn;


            int type = Live.StreamType.INDIVIDUAL == receive.streamType?
                    CTLConstant.StreamingType.PLAY_INDIVIDUAL : CTLConstant.StreamingType.PLAY_LIVE;
            //FIXME 1对1流的类型是什么？
            playStream(type, receive.RTMPPlayUrl, receive.finishOn);

        } else if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STOPPED).equals(event)) {

            Logger.d("STREAMING_STOPPED************************你妈");
            if (object == null) {
                return;
            }

            Logger.d("STREAMING_STOPPED********************************000");

            StreamStopReceive receive = (StreamStopReceive) object;
            if (receive.streamType == Live.StreamType.INDIVIDUAL) {

                Logger.d("STREAMING_STOPPED********************************0001");
                //老师端的推流将学生端的个人推流，强制挤掉。
                if (mStreamChangeListener != null) {
                    mStreamChangeListener.onStreamStopped(CTLConstant.StreamingType.PUBLISH_INDIVIDUAL, null);
                }
            } else {
                Logger.d("STREAMING_STOPPED********************************0002");
                pausePlayStream(mPlayType);
            }

        } else if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAM_RECLAIMED).equals(event)) {
            if (object == null) {
                return;
            }
            pausePublishStream(mPublishType);

        } else if(Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STOP_STREAM_BY_EXPIRATION).equals(event)) {
            if (object == null) {
                return;
            }

            if (mStreamChangeListener != null) {
                mStreamChangeListener.onStreamStopped(mPublishType, STREAM_EXPIRED);
            }
            mStreamPublishing = false;
            mPublishView.pause();
            mPublishView.setVisibility(View.GONE);

            if (mPlayView.getVisibility() == View.VISIBLE) {
                mPlayView.pause();
                mPlayView.setVisibility(View.GONE);
            }
        } else if(Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_FEEDBACK).equals(event)) {

            if (object == null) {
                return;
            }
            MediaFeedbackReceive receive = (MediaFeedbackReceive) object;
            if (Live.MediaStatus.READY == receive.status && !TextUtils.isEmpty(receive.playUrl)) {
                Toast.makeText(mContext, R.string.success_one2one,Toast.LENGTH_SHORT).show();
                mPlayStreamUrl = receive.playUrl;
                playStream(CTLConstant.StreamingType.PLAY_PEER_TO_PEER, receive.playUrl);
            }else if (Live.MediaStatus.FAILED_DUE_TO_DENIED == receive.status) {
                Toast.makeText(mContext, R.string.user_refuse_one_to_one_tips,Toast.LENGTH_SHORT).show();
            }else if (Live.MediaStatus.FAILED_DUE_TO_NETWORK_ISSUES == receive.status) {
                Toast.makeText(mContext, R.string.failed_one2one_network_issue,Toast.LENGTH_SHORT).show();
            }else if (Live.MediaStatus.FAILED_DUE_TO_PRIVACY== receive.status) {
                Toast.makeText(mContext, R.string.failed_one2one_privacy,Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(mContext, R.string.failed_one2one,Toast.LENGTH_SHORT).show();
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
    }
}
