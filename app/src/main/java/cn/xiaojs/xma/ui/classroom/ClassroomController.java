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
import android.view.View;

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
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
    private VideoEditingFragment mVideoEditFragment;

    public ClassroomController(Context context, View root, Constants.User client, int appType) {
        init(context, root, client, appType);
    }

    private void init(Context context, View root, Constants.User client, int appType) {
        mContext = context;
        mUser = client;

        //init video controller
        if (client == Constants.User.TEACHER) {
            mVideoController = new TeacherVideoController(context, root);
        } else if (client == Constants.User.STUDENT) {
            mVideoController = new StudentVideoController(context, root);
        }

        //init whiteboard controller
        mBoardController = new WhiteboardController(context, root, client, appType);
    }

    public void onPauseVideo() {
        mVideoController.onPause();
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
     * 进入视频编辑页面
     */
    public void enterVideoEditing(Bitmap bmp) {
        if (mContext instanceof ClassroomActivity) {
            mVideoEditFragment = new VideoEditingFragment();
            mVideoEditFragment.setBitmap(bmp);
            ((ClassroomActivity)mContext).getSupportFragmentManager().beginTransaction()
                    .add(R.id.video_edit_layout, mVideoEditFragment).commit();
        }
    }

    /**
     * 退出视频编辑页面
     */
    public void exitVideoEditing() {
        if (mContext instanceof ClassroomActivity && mVideoEditFragment != null) {
            ((ClassroomActivity)mContext).getSupportFragmentManager().beginTransaction()
                    .remove(mVideoEditFragment).commit();
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

    private boolean isWebApp(int app) {
        return app == Platform.AppType.WEB_CLASSROOM || app == Platform.AppType.MOBILE_WEB;
    }

    /**
     * 暂停推流
     */
    public void pauseStream() {
        mVideoController.pausePublishStream();
    }

    /**
     * 开始推流, 直播推流
     */
    public void publishStream(String url) {
        mVideoController.publishStream(url, true);
    }

    /**
     * 开始推流
     * @param live 是否是直播(true:直播推流 false:个人推流)
     */
    public void publishStream(String url, boolean live) {
        mVideoController.publishStream(url, live);
    }

    /**
     * 播放流
     */
    public void playStream(String url) {
        mVideoController.playStream(url);
    }


}
