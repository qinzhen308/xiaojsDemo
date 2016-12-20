package com.benyuan.xiaojs.ui.classroom;

import android.animation.Animator;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.drawer.DrawerLayout;
import com.benyuan.xiaojs.ui.classroom.live.core.Config;
import com.benyuan.xiaojs.ui.classroom.live.view.LiveRecordView;
import com.benyuan.xiaojs.ui.classroom.live.view.MediaContainerView;
import com.benyuan.xiaojs.ui.classroom.whiteboard.Whiteboard;
import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteboardController;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

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
 * Date:2016/11/27
 * Desc:
 *
 * ======================================================================================== */

public class ClassroomActivity extends FragmentActivity {
    public final static int STATE_MAIN_PANEL = 0;
    public final static int STATE_LIVE = 1;
    public final static int STATE_WHITE_BOARD = 2;

    private final static float LIVE_PROGRESS_WIDTH_FACTOR = 0.5F;

    private final static int ANIM_SHOW = 1 << 1;
    private final static int ANIM_HIDE = 1 << 2;

    private final static int STATE_PLAY = 1;
    private final static int STATE_PAUSE = 2;
    private final static int STATE_STOP = 3;

    //drawer
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer_left_layout)
    ViewGroup mDrawerLeftLayout;
    @BindView(R.id.drawer_right_layout)
    ViewGroup mDrawerRightLayout;

    //panel
    @BindView(R.id.drawer_content_layout)
    View mContentRoot;
    @BindView(R.id.main_panel)
    MainPanel mMainPanel;
    @BindView(R.id.top_panel)
    View mTopPanel;
    @BindView(R.id.title_bar)
    View mTitleBar;
    @BindView(R.id.bottom_panel)
    View mBottomPanel;
    @BindView(R.id.white_board_panel)
    View mWhiteBoardPanel;
    @BindView(R.id.live_progress_layout)
    View mLiveProgressLayout;
    @BindView(R.id.live_progress)
    SeekBar mLiveProgress;

    //live,whiteboard
    @BindView(R.id.white_board)
    Whiteboard mWhiteboard;
    @BindView(R.id.teacher_video)
    LiveRecordView mTeacherVideo;
    @BindView(R.id.player_container)
    MediaContainerView mContainer;

    private Unbinder mBinder;

    //all kind of panels
    private CourseWarePanel mCourseWarePanel;
    private MessagePanel mMessagePanel;
    private SettingPanel mSettingPanel;
    private ChatPanel mChatPanel;
    private Dialog mQuestionAnswerPanel;
    private DialogFragment mWhiteBoardManagePanel;

    //gesture
    private ClassroomGestureDetector mMainPanelGestureDetector;
    private ClassroomGestureDetector mWhiteBoardGestureDetector;
    private int mCurrentState = STATE_MAIN_PANEL;
    private boolean mAnimating = false;
    private PanelAnimListener mPanelAnimListener;
    private boolean mNeedOpenWhiteBoardPanel = false;
    private int mPlayState = STATE_PLAY;

    private WhiteboardController mWhiteboardController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        //init params
        initParams();

        initDrawer();
        initLiveProgress();
        initGestureDetector();
        mTeacherVideo.setPath(Config.pathPush);
    }

    private void initParams() {
        mBinder = ButterKnife.bind(this);

        mPanelAnimListener = new PanelAnimListener();
    }

    /**
     * 教室内容分为WhiteBord，(MainPanel:含视频) 2层，底层是WhiteBord 通过重写MainPanel和WhiteBord的OnTouchEvent来控制事件分发
     */
    private void initGestureDetector() {
        mMainPanelGestureDetector = new ClassroomGestureDetector(this, new MainPanelGestureListener());
        mWhiteBoardGestureDetector = new ClassroomGestureDetector(this, new WhiteBoardGestureListener());

        mMainPanel.setGestureDetector(mMainPanelGestureDetector);
        //为了控制面板模式也能缩放，移动画布操作
        mMainPanel.setTransformationWhiteBoard(mWhiteboard);
        mWhiteboard.setGestureDetector(mWhiteBoardGestureDetector);
    }

    private void initDrawer() {
        //默认关闭手势滑动
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
    }

    private void initLiveProgress() {
        int w = getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams params = mLiveProgress.getLayoutParams();
        params.width = (int) (w * LIVE_PROGRESS_WIDTH_FACTOR);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //mTeacherVideo.resume();
        //mContainer.resume();
    }

    private boolean m = false;

    @OnClick({R.id.back_btn, R.id.blackboard_switcher_btn, R.id.course_ware_btn, R.id.setting_btn,
            R.id.notify_msg_btn, R.id.contact_btn, R.id.qa_btn, R.id.chat_btn, R.id.more_btn, R.id.play_pause_btn,
            R.id.select_btn, R.id.handwriting_btn, R.id.shape_btn, R.id.eraser_btn, R.id.text_btn, R.id.color_picker_btn,
            R.id.exit_btn, R.id.main_screen_setting, R.id.save_white_board_btn})
    public void onPanelItemClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.exit_btn:
                finish();
                break;
            case R.id.blackboard_switcher_btn:
                openWhiteBoardManager();
                break;
            case R.id.play_pause_btn:
                if (mPlayState == STATE_PLAY) {
                    mPlayState = STATE_STOP;
                    ((ImageView) v).setImageResource(R.drawable.ic_cr_stop);
                } else {
                    mPlayState = STATE_PLAY;
                    ((ImageView) v).setImageResource(R.drawable.ic_cr_start);
                }

                //live
                if (!m) {
                    mContainer.addPlayer(Config.pathCfu);
                    m = !m;
                    break;
                }
                mContainer.addPlayer(Config.pathHK);
                break;
            case R.id.course_ware_btn:
                openCourseWarePanel();
                break;
            case R.id.setting_btn:
                openSetting();
                break;
            case R.id.notify_msg_btn:
                openAllMessage();
                break;
            case R.id.contact_btn:
                openChat(ChatPanel.MODE_CONTACT);
                break;
            case R.id.qa_btn:
                openQuestionAnswer();
                break;
            case R.id.chat_btn:
                openChat(ChatPanel.MODE_CHAT);
                break;
            case R.id.more_btn:
                switchWhiteBoardPanel();
                break;
            case R.id.main_screen_setting:
                break;
            case R.id.save_white_board_btn:
                break;
            case R.id.select_btn:
            case R.id.handwriting_btn:
            case R.id.shape_btn:
            case R.id.eraser_btn:
            case R.id.text_btn:
            case R.id.color_picker_btn:
                handleWhitePanelClick(v);
                break;
            default:
                break;
        }
    }

    /**
     * 打开白板管理
     */
    private void openWhiteBoardManager() {
        if (mWhiteBoardManagePanel == null) {
            mWhiteBoardManagePanel = new WhiteBoardManagement();
        }
        mWhiteBoardManagePanel.show(getSupportFragmentManager(), "white_board_management");
    }

    /**
     * 打开课件，课件的布局是提前在xml布局添加了，这里打开课件只需要绑定数据
     */
    private void openCourseWarePanel() {
        if (mCourseWarePanel == null) {
            mCourseWarePanel = new CourseWarePanel(this);
        }
        mCourseWarePanel.show(mDrawerLayout, mDrawerLeftLayout);
    }

    /**
     * 打开设置
     */
    private void openSetting() {
        if (mSettingPanel == null) {
            mSettingPanel = new SettingPanel(this);
        }
        mSettingPanel.show(mDrawerLayout, mDrawerRightLayout);
    }

    /**
     * 打开聊天
     */
    private void openChat(int mode) {
        if (mChatPanel == null) {
            mChatPanel = new ChatPanel(this);
        }
        mChatPanel.with(mode).show(mDrawerLayout, mDrawerRightLayout);
    }

    /**
     * 打开通知消息
     */
    private void openAllMessage() {
        if (mMessagePanel == null) {
            mMessagePanel = new MessagePanel(this);
        }
        mMessagePanel.show(mDrawerLayout, mDrawerRightLayout);
    }

    /**
     * 打开问答
     */
    private void openQuestionAnswer() {
        if (mQuestionAnswerPanel == null) {
            mQuestionAnswerPanel = new QuestionAnswer(this);
        }
        mQuestionAnswerPanel.show();
    }

    /**
     * 打开关闭白板操作面板 若正在动画，直接返回 1.若当前在顶部和底部的面板显示，先隐藏顶部底部隐藏动画完成后，再显示白板操作面板 否则，直接隐藏或显示白板操作面板
     */
    private void switchWhiteBoardPanel() {
        if (mAnimating) {
            return;
        }

        if (mCurrentState == STATE_MAIN_PANEL) {
            if (mBottomPanel.getVisibility() == View.VISIBLE) {
                //若当前在顶部和底部的面板显示，先隐藏顶部底部隐藏动画完成后，再显示白板操作面板
                mNeedOpenWhiteBoardPanel = true;
                hideTopBottomPanel();
            } else {
                if (mWhiteBoardPanel.getVisibility() == View.VISIBLE) {
                    hideWhiteBoardPanel();
                } else {
                    showWhiteBoardPanel(true);
                }
            }
        } else {
            if (mWhiteBoardPanel.getVisibility() == View.VISIBLE) {
                hideWhiteBoardPanel();
            } else {
                showWhiteBoardPanel(true);
            }
        }
    }

    private class MainPanelGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("aaa", "========MainPanelGestureListener====onSingleTapConfirmed=============" + mCurrentState);
            if (mCurrentState == STATE_MAIN_PANEL && !mAnimating) {
                if (mBottomPanel.getVisibility() == View.VISIBLE) {
                    mNeedOpenWhiteBoardPanel = false;
                    hideTopBottomPanel();
                } else {
                    showTopBottomPanel();
                }
            }
            return super.onSingleTapConfirmed(e);
        }

    }

    private class WhiteBoardGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("aaa", "=========WhiteBoardGestureListener===onSingleTapConfirmed=============" + mCurrentState);
            return super.onSingleTapConfirmed(e);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mTeacherVideo.pause();
        mContainer.pause();
    }

    @Override
    protected void onDestroy() {
        mTeacherVideo.destroy();
        mContainer.destroy();
        super.onDestroy();

        if (mBinder != null) {
            mBinder.unbind();
        }

        if (mWhiteboardController != null) {
            mWhiteboardController.release();
        }

        cancelAllAnim();
    }

    public int getCurrentState() {
        return mCurrentState;
    }

    /**
     * 隐藏顶部和底部面板
     */
    private void hideTopBottomPanel() {
        if (mAnimating) {
            return;
        }

        hideTopPanel();
        hideBottomPanel();
    }

    /**
     * 显示顶部和底部面板
     */
    private void showTopBottomPanel() {
        if (mAnimating) {
            return;
        }

        showTopPanel();
        showBottomPanel();

    }

    /**
     * 显示底部面板
     */
    private void hideTopPanel() {
        int y = mTitleBar.getBottom();
        mTopPanel.animate()
                .translationY(-y)
                .setListener(mPanelAnimListener.with(mTopPanel).play(ANIM_HIDE))
                .start();
        mLiveProgressLayout.animate().alpha(0).start();
    }

    /**
     * 显示顶部面板
     */
    private void showTopPanel() {
        mTopPanel.animate()
                .translationY(0)
                .setListener(mPanelAnimListener.with(mTopPanel).play(ANIM_SHOW))
                .start();
        mLiveProgressLayout.animate().alpha(1).start();
    }

    /**
     * 隐藏底部面板
     */
    private void hideBottomPanel() {
        int y = mBottomPanel.getTop();
        mBottomPanel.animate()
                .alpha(0.3f)
                .translationY(y)
                .setListener(mPanelAnimListener.with(mBottomPanel).play(ANIM_HIDE))
                .start();
    }

    /**
     * 显示顶部面板
     */
    private void showBottomPanel() {
        int y = 0;
        mBottomPanel.animate()
                .alpha(1)
                .translationY(y)
                .setListener(mPanelAnimListener.with(mBottomPanel).play(ANIM_SHOW))
                .start();
    }

    /**
     * 隐藏白板操作面板
     */
    private void hideWhiteBoardPanel() {
        if (mAnimating) {
            return;
        }

        mWhiteBoardPanel.animate()
                .alpha(0.0f)
                .setListener(mPanelAnimListener.with(mWhiteBoardPanel).play(ANIM_HIDE))
                .start();
    }

    /**
     * 显示白板操作面板
     */
    private void showWhiteBoardPanel(boolean needAnim) {
        if (mAnimating) {
            return;
        }

        if (mWhiteboardController == null) {
            mWhiteboardController = new WhiteboardController(this, mContentRoot);
        }

        if (needAnim) {
            mWhiteBoardPanel.animate()
                    .alpha(1.0f)
                    .setListener(mPanelAnimListener.with(mWhiteBoardPanel).play(ANIM_SHOW))
                    .start();
        } else {
            mWhiteBoardPanel.setAlpha(1.0f);
            mWhiteBoardPanel.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 取消所有动画
     */
    private void cancelAllAnim() {
        if (mTopPanel != null) {
            mTopPanel.animate().cancel();
        }

        if (mBottomPanel != null) {
            mBottomPanel.animate().cancel();
        }

        if (mWhiteBoardPanel != null) {
            mWhiteBoardPanel.animate().cancel();
        }
    }


    /**
     * 动画监听器
     */
    private class PanelAnimListener implements Animator.AnimatorListener {
        private View mV;
        private int mAnimType;

        public PanelAnimListener with(View v) {
            mV = v;
            return this;
        }

        public PanelAnimListener play(int type) {
            mAnimType = type;
            return this;
        }

        @Override
        public void onAnimationStart(Animator animation) {
            mAnimating = true;
            if (mV != null) {
                if ((mAnimType & ANIM_SHOW) != 0) {
                    switch (mV.getId()) {
                        case R.id.bottom_panel:
                            mV.setVisibility(View.VISIBLE);
                            break;
                        case R.id.white_board_panel:
                            mV.setVisibility(View.VISIBLE);
                            break;
                    }
                } else if ((mAnimType & ANIM_HIDE) != 0) {
                    //do nothing
                }
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mAnimating = false;
            if (mV != null) {
                if ((mAnimType & ANIM_SHOW) != 0) {
                    switch (mV.getId()) {
                        case R.id.top_panel:
                            if (mTopPanel != null && mTitleBar != null && mLiveProgressLayout != null) {
                                mTopPanel.setVisibility(View.VISIBLE);
                                mTitleBar.setVisibility(View.VISIBLE);
                                mLiveProgressLayout.setVisibility(View.VISIBLE);
                            }
                            break;
                        case R.id.white_board_panel:
                            if (mWhiteBoardPanel != null) {
                                mCurrentState = STATE_WHITE_BOARD;
                                showWhiteBoardPanel(false);
                            }
                            break;
                    }
                } else if ((mAnimType & ANIM_HIDE) != 0) {
                    switch (mV.getId()) {
                        case R.id.top_panel:
                            if (mTitleBar != null && mLiveProgressLayout != null) {
                                mTitleBar.setVisibility(View.GONE);
                                mLiveProgressLayout.setVisibility(View.GONE);
                            }
                            break;
                        case R.id.bottom_panel:
                            if (mBottomPanel != null && mWhiteBoardPanel != null) {
                                mBottomPanel.setVisibility(View.GONE);
                                if (mNeedOpenWhiteBoardPanel) {
                                    mNeedOpenWhiteBoardPanel = false;
                                    mCurrentState = STATE_WHITE_BOARD;
                                    showWhiteBoardPanel(false);
                                }
                            }
                            break;
                        case R.id.white_board_panel:
                            if (mWhiteBoardPanel != null) {
                                mCurrentState = STATE_MAIN_PANEL;
                                mWhiteBoardPanel.setVisibility(View.GONE);
                            }
                            break;
                    }
                }
            }
            mV = null;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mAnimating = false;
            mV = null;
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

    }

    private void handleWhitePanelClick(View v) {
        if (mWhiteboardController != null) {
            mWhiteboardController.handlePanelItemClick(v);
        }
    }

}