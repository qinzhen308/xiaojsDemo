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

import com.orhanobut.logger.Logger;
import com.pili.pldroid.player.widget.PLVideoTextureView;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom2.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.EventListener;
import io.reactivex.functions.Consumer;

public class PlayVideoController extends VideoController{

    private EventListener.ELPVideoControl eventListener;


    public PlayVideoController(Context context, View root, OnStreamChangeListener listener) {
        super(context, root, listener);
        listenerSocket();

        eventListener = ClassroomEngine.getEngine().observerPVControl(receivedConsumer);
    }

    @Override
    public void onDestroy() {
        eventListener.dispose();
        super.onDestroy();
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
            mStreamChangeListener.onStreamStarted(mPlayType, mPlayStreamUrl, mExtraData);
        }

        if (mPlayView instanceof PlayerTextureView && mNeedStreamRePlaying) {
            mNeedStreamRePlaying = false;
            mPlayView.delayHideLoading();
        }
    }

    /**
     * 暂停推流,
     */
    @Override
    public void pausePublishStream(final int type) {
        //do nothing
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

    private Consumer<EventReceived> receivedConsumer = new Consumer<EventReceived>() {
        @Override
        public void accept(EventReceived eventReceived) throws Exception {

            if (XiaojsConfig.DEBUG) {
                Logger.d("ELPVControl received eventType:%d", eventReceived.eventType);
            }

            switch (eventReceived.eventType) {
                case Su.EventType.STREAMING_STARTED:
                    StreamStartReceive receive = (StreamStartReceive) eventReceived.t;

                    int type = Live.StreamType.INDIVIDUAL == receive.streamType?
                            CTLConstant.StreamingType.PLAY_INDIVIDUAL : CTLConstant.StreamingType.PLAY_LIVE;
                    //FIXME 1对1流的类型是什么？
                    playStream(type, receive.RTMPPlayUrl, receive.finishOn);
                    break;
                case Su.EventType.STREAMING_STOPPED:
                    pausePlayStream(mPlayType);
                    break;
                case Su.EventType.STREAM_RECLAIMED:
                case Su.EventType.STOP_STREAM_BY_EXPIRATION:
                    pausePublishStream(mPublishType);
                    break;
            }
        }
    };

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
    }
}
