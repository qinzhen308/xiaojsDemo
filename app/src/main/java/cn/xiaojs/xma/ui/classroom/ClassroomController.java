package cn.xiaojs.xma.ui.classroom;
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
 * Date:2016/12/2
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom.document.DocumentFragment;
import cn.xiaojs.xma.ui.classroom.live.OnStreamStateChangeListener;
import cn.xiaojs.xma.ui.classroom.live.StudentVideoController;
import cn.xiaojs.xma.ui.classroom.live.TeacherVideoController;
import cn.xiaojs.xma.ui.classroom.live.VideoController;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardCollection;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardController;

public class ClassroomController {

    private Context mContext;

    private VideoController mVideoController;
    private WhiteboardController mBoardController;
    protected Constants.User mUser;
    private PhotoDoodleFragment mPhotoDoodleFragment;
    private DocumentFragment mDocumentFragment;
    private VideoPlayFragment mVideoPlayFragment;

    public ClassroomController(Context context, View root, Constants.User client, int appType, OnStreamStateChangeListener listener) {
        init(context, root, client, appType, listener);
    }

    private void init(Context context, View root, Constants.User client, int appType, OnStreamStateChangeListener listener) {
        mContext = context;
        mUser = client;

        //init video controller
        if (client == Constants.User.TEACHER
                || client == Constants.User.ASSISTANT
                || client == Constants.User.REMOTE_ASSISTANT) {
            mVideoController = new TeacherVideoController(context, root, listener);
        } else if (client == Constants.User.STUDENT) {
            mVideoController = new StudentVideoController(context, root, listener);
        }

        //init whiteboard controller
        mBoardController = new WhiteboardController(context, root, client, appType);
    }

    public void onPauseVideo() {
        mVideoController.onPause();
    }

    public void onResumeVideo() {
        mVideoController.onResume();
    }

    public void onDestroyVideo() {
        mVideoController.onDestroy();
    }

    /**
     * 释放资源
     */
    public void release() {
        mBoardController.release();
        onDestroyVideo();
    }

    /**
     * 进入图片编辑页面
     */
    public void enterPhotoDoodle(Bitmap bmp, OnPhotoDoodleShareListener listener) {
        if (mContext instanceof ClassroomActivity) {
            mPhotoDoodleFragment = new PhotoDoodleFragment();
            Bundle bundle = new Bundle();
            bundle.putInt(Constants.KEY_IMG_DISPLAY_MODE, PhotoDoodleFragment.MODE_SINGLE_IMG);
            mPhotoDoodleFragment.setArguments(bundle);
            mPhotoDoodleFragment.setBitmap(bmp);
            mPhotoDoodleFragment.setPhotoDoodleShareListener(listener);
            ((ClassroomActivity) mContext).getSupportFragmentManager().beginTransaction()
                    .add(R.id.photo_doodle_layout, mPhotoDoodleFragment).commit();
        }
    }

    /**
     * 进入图片编辑页面
     */
    public void enterPhotoDoodle(final String url, final OnPhotoDoodleShareListener listener) {
        if (mContext instanceof ClassroomActivity) {
            //load course img
            new AsyncTask<Void, Integer, Bitmap>() {

                @Override
                protected void onPreExecute() {
                    ((ClassroomActivity) mContext).showProgress(true);
                }

                @Override
                protected Bitmap doInBackground(Void... params) {
                    try {
                        return Glide.with(mContext)
                                .load(url)
                                .asBitmap()
                                .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();
                    } catch (Exception e) {
                        Logger.i(e != null ? e.getLocalizedMessage() : "null");
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(Bitmap bitmap) {
                    ((ClassroomActivity) mContext).cancelProgress();
                    if (bitmap != null) {
                        mPhotoDoodleFragment = new PhotoDoodleFragment();
                        Bundle bundle = new Bundle();
                        bundle.putInt(Constants.KEY_IMG_DISPLAY_MODE, PhotoDoodleFragment.MODE_SINGLE_IMG);
                        mPhotoDoodleFragment.setArguments(bundle);
                        mPhotoDoodleFragment.setBitmap(bitmap);
                        mPhotoDoodleFragment.setPhotoDoodleShareListener(listener);
                        ((ClassroomActivity) mContext).getSupportFragmentManager().beginTransaction()
                                .add(R.id.photo_doodle_layout, mPhotoDoodleFragment).commit();
                    } else {
                        Toast.makeText(mContext, R.string.cls_pic_load_fail, Toast.LENGTH_SHORT).show();
                        exitPhotoDoodle();
                    }
                }
            }.execute();
        }
    }

    /**
     * 进入图片编辑，图片可以左右滑动
     */
    public void enterPhotoDoodle(ArrayList<String> imgUrlList, final OnPhotoDoodleShareListener listener) {
        mPhotoDoodleFragment = new PhotoDoodleFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.KEY_IMG_DISPLAY_MODE, PhotoDoodleFragment.MODE_MULTI_IMG);
        bundle.putStringArrayList(Constants.KEY_IMG_LIST, imgUrlList);
        mPhotoDoodleFragment.setArguments(bundle);
        mPhotoDoodleFragment.setPhotoDoodleShareListener(listener);
        ((ClassroomActivity) mContext).getSupportFragmentManager().beginTransaction()
                .add(R.id.photo_doodle_layout, mPhotoDoodleFragment).commit();
    }

    /**
     * 退出图片编辑页面
     */
    public void exitPhotoDoodle() {
        if (mContext instanceof ClassroomActivity && mPhotoDoodleFragment != null) {
            ((ClassroomActivity) mContext).getSupportFragmentManager().beginTransaction()
                    .remove(mPhotoDoodleFragment).commit();
        }
    }

    /**
     * 进入文档页面
     */
    public void enterDocumentFragment() {
        if (mContext instanceof ClassroomActivity) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.KEY_LESSON_ID, ((ClassroomActivity) mContext).getLessonId());
            mDocumentFragment = new DocumentFragment();
            mDocumentFragment.setArguments(bundle);
            ((ClassroomActivity) mContext).getSupportFragmentManager().beginTransaction()
                    .add(R.id.document_layout, mDocumentFragment).commit();
        }
    }

    /**
     * 退出文档页面
     */
    public void exitDocumentFragment() {
        if (mContext instanceof ClassroomActivity && mDocumentFragment != null) {
            ((ClassroomActivity) mContext).getSupportFragmentManager().beginTransaction()
                    .remove(mDocumentFragment).commit();
        }
    }

    /**
     * 进入视频播放
     */
    public void playVideo(LibDoc doc) {
        if (mContext instanceof ClassroomActivity) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.KEY_LIB_DOC, doc);
            mVideoPlayFragment = new VideoPlayFragment();
            mVideoPlayFragment.setArguments(bundle);
            ((ClassroomActivity) mContext).getSupportFragmentManager().beginTransaction()
                    .add(R.id.document_layout, mVideoPlayFragment).commit();
        }
    }

    /**
     * 退出视频播放
     */
    public void exitPlayVideo() {
        if (mContext instanceof ClassroomActivity && mVideoPlayFragment != null) {
            ((ClassroomActivity) mContext).getSupportFragmentManager().beginTransaction()
                    .remove(mVideoPlayFragment).commit();
        }
    }

    /**
     * 切换摄像头
     */
    public void switchCamera() {
        mVideoController.switchCamera();
    }

    /**
     * 捕获视频帧
     */
    public void takeVideoFrame(FrameCapturedCallback callback) {
        mVideoController.takeVideoFrame(callback);
    }

    /**
     * 设置白板为主屏
     */
    public void setWhiteboardMainScreen() {
        mBoardController.setWhiteboardMainScreen();
    }

    /**
     * 是否是同步的白板
     */
    public boolean isSyncWhiteboard() {
        return false;
    }

    /**
     * 是否是直播白板
     */
    public boolean isLiveWhiteboard() {
        return false;
    }

    /**
     * 退出白板模式
     */
    public void exitWhiteboard() {
        mBoardController.exitWhiteboard();
    }

    /**
     * 保存白板
     */
    public void saveWhiteboard() {
        mBoardController.saveWhiteboard();
    }

    public void handleBoardPanelItemClick(View view) {
        mBoardController.handlePanelItemClick(view);
    }

    public void onSwitchWhiteboardCollection(WhiteboardCollection wbColl) {
        mBoardController.onSwitchWhiteboardCollection(wbColl);
    }

    /**
     * 暂停推流
     */
    public void pausePublishStream(int type) {
        mVideoController.pausePublishStream(type);
    }

    /**
     * 暂停推流
     */
    public void pausePublishStream() {
        mVideoController.pausePublishStream();
    }

    /**
     * 开始推流
     */
    public void publishStream(int type, String url) {
        mVideoController.publishStream(type, url);
    }


    public void publishStream() {
        mVideoController.publishStream();
    }

    /**
     * 播放流
     */
    public void playStream(int type, String url) {
        playStream(type, url, null);
    }

    /**
     * 播放流
     */
    public void playStream(int type, String url, Object extra) {
        mVideoController.playStream(type, url, extra);
    }

    public void pausePlayStream() {
        mVideoController.pausePlayStream();
    }

    public void playStream() {
        mVideoController.playStream();
    }

    public boolean needStreamRePublishing() {
        return mVideoController.needStreamRePublishing();
    }

    public boolean needStreamRePlaying() {
        return mVideoController.needStreamRePlaying();
    }

    public boolean hasStreamUsing() {
        return mVideoController.hasStreamUsing();
    }

    public boolean hasStreamPlaying() {
        return mVideoController.hasStreamPlaying();
    }

    public boolean hasStreamPublish() {
        return mVideoController.hasStreamPublishing();
    }

    public void muteOrUnmute() {
        if (hasStreamPublish()) {
            mVideoController.muteOrUnmute();
        }
    }

    public void openOrCloseCamera() {
        if (hasStreamPublish()) {
            mVideoController.openOrCloseCamera();
        }
    }

    public void togglePublishStreamResolution() {
        if (hasStreamPublish()) {
            mVideoController.setPublishStreamByToggleResolution(true);
            mVideoController.togglePublishResolution();
        }
    }

}
