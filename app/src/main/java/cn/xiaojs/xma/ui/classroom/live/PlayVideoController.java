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

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.model.socket.room.StreamStartReceive;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom2.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.EventListener;

public class PlayVideoController extends VideoController implements EventListener{

    public PlayVideoController(Context context, View root, OnStreamChangeListener listener) {
        super(context, root, listener);
        listenerSocket();

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

    @Override
    public void receivedEvent(String event, Object object) {

        if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED)
                .equals(event)) {

            if (object == null) {
                return;
            }

            StreamStartReceive receive = (StreamStartReceive) object;

            int type = Live.StreamType.INDIVIDUAL == receive.streamType?
                    CTLConstant.StreamingType.PLAY_INDIVIDUAL : CTLConstant.StreamingType.PLAY_LIVE;
            //FIXME 1对1流的类型是什么？
            playStream(type, receive.RTMPPlayUrl, receive.finishOn);

        } else if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STOPPED).equals(event)) {

            if (object == null) {
                return;
            }
            pausePlayStream(mPlayType);
        } else if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAM_RECLAIMED).equals(event)
                || Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STOP_STREAM_BY_EXPIRATION).equals(event)) {
            if (object == null) {
                return;
            }
            pausePublishStream(mPublishType);
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
    }
}
