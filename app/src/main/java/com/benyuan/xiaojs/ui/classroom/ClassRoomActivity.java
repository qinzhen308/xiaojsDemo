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
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.classroom.drawer.DrawerLayout;
import com.benyuan.xiaojs.ui.classroom.live.LiveView;
import com.benyuan.xiaojs.ui.classroom.whiteboard.WhiteBoard;

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

public class ClassRoomActivity extends FragmentActivity {
    public final static int STATE_MAIN_PANEL = 0;
    public final static int STATE_LIVE = 1;
    public final static int STATE_WHITE_BOARD = 2;

    private final static float LIVE_PROGRESS_WIDTH_FACTOR = 0.5F;

    //drawer
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer_left_layout)
    ViewGroup mDrawerLeftLayout;
    @BindView(R.id.drawer_right_layout)
    ViewGroup mDrawerRightLayout;

    //panel
    @BindView(R.id.main_panel)
    MainPanel mMainPanel;
    @BindView(R.id.top_panel)
    View mTopPanel;
    @BindView(R.id.bottom_panel)
    View mBottomPanel;
    @BindView(R.id.live_progress)
    SeekBar mLiveProgress;

    //live,whiteboard
    @BindView(R.id.white_board)
    WhiteBoard mWhiteBoard;
    @BindView(R.id.teacher_video)
    LiveView mTeacherVideo;
    @BindView(R.id.stu_video)
    FrameLayout mStuVideos;

    private Unbinder mBinder;

    //all kind of panels
    private CourseWarePanel mCourseWarePanel;
    private MessagePanel mMessagePanel;
    private SettingPanel mSettingPanel;
    private ContactPanel mContactPanel;
    private Dialog mQuestionAnswerPanel;
    private DialogFragment mWhiteBoardManagePanel;

    //gesture
    private ClassRoomGestureDetector mMainPanelGestureDetector;
    private ClassRoomGestureDetector mWhiteBoardGestureDetector;
    private int mCurrentState = STATE_MAIN_PANEL;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        mBinder = ButterKnife.bind(this);

        initDrawer();
        initLiveProgress();
        initGestureDetector();
    }

    private void initGestureDetector() {
        mMainPanelGestureDetector = new ClassRoomGestureDetector(this, new MainPanelGestureListener());
        mWhiteBoardGestureDetector = new ClassRoomGestureDetector(this, new WhiteBoardGestureListener());

        mMainPanel.setGestureDetector(mMainPanelGestureDetector);
        mWhiteBoard.setGestureDetector(mWhiteBoardGestureDetector);
    }

    private void initDrawer() {
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
        params.width = (int)(w * LIVE_PROGRESS_WIDTH_FACTOR);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @OnClick({R.id.back_btn, R.id.blackboard_switcher_btn, R.id.courese_ware_btn, R.id.setting_btn,
            R.id.notify_msg_btn, R.id.contact_btn, R.id.qa_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.blackboard_switcher_btn:
                openWhiteBoardManager();
                break;
            case R.id.courese_ware_btn:
                openCourseWarePanel();
                break;
            case R.id.setting_btn:
                openSetting();
                break;
            case R.id.notify_msg_btn:
                openAllMessage();
                break;
            case R.id.contact_btn:
                openContacts();
                break;
            case R.id.qa_btn:
                openQuestionAnswer();
                break;
            default:
                break;
        }
    }

    private void openWhiteBoardManager() {
        if (mWhiteBoardManagePanel == null) {
            mWhiteBoardManagePanel = new WhiteBoardManagement();
        }
        mWhiteBoardManagePanel.show(getSupportFragmentManager(), "white_board_management");
    }

    private void openCourseWarePanel() {
        if (mCourseWarePanel == null) {
            mCourseWarePanel = new CourseWarePanel(this);
        }
        mCourseWarePanel.show(mDrawerLayout, mDrawerLeftLayout);
    }

    private void openSetting() {
        if (mSettingPanel == null) {
            mSettingPanel = new SettingPanel(this);
        }
        mSettingPanel.show(mDrawerLayout, mDrawerRightLayout);
    }

    private void openContacts() {
        if (mContactPanel == null) {
            mContactPanel = new ContactPanel(this);
        }
        mContactPanel.show(mDrawerLayout, mDrawerRightLayout);
    }

    private void openAllMessage() {
        if (mMessagePanel == null) {
            mMessagePanel = new MessagePanel(this);
        }
        mMessagePanel.show(mDrawerLayout, mDrawerRightLayout);
    }

    private void openQuestionAnswer() {
        if (mQuestionAnswerPanel == null) {
            mQuestionAnswerPanel = new QuestionAnswer(this);
        }
        mQuestionAnswerPanel.show();
    }

    private class MainPanelGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("aaa", "========MainPanelGestureListener====onSingleTapConfirmed=============");
            switchState();
            return super.onSingleTapConfirmed(e);
        }

    }

    private class WhiteBoardGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("aaa", "=========WhiteBoardGestureListener===onSingleTapConfirmed=============");
            switchState();
            return super.onSingleTapConfirmed(e);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBinder != null) {
            mBinder.unbind();
        }
    }

    public int getCurrentState() {
        return mCurrentState;
    }

    private void switchState() {
        if (mCurrentState == STATE_MAIN_PANEL) {
            mCurrentState = STATE_WHITE_BOARD;
            hideTopPanel();
            hideBottomPanel();
        } else if (mCurrentState == STATE_WHITE_BOARD) {
            mCurrentState = STATE_MAIN_PANEL;
            showTopPanel();
            showBottomPanel();
        } else {
            //default, restore state
            mCurrentState = STATE_MAIN_PANEL;
            showTopPanel();
            showBottomPanel();
        }
    }


    private void hideTopPanel() {
        int y = mTopPanel.getBottom();
        mTopPanel.animate().alpha(0.3f).translationY(-y).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mTopPanel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mTopPanel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void showTopPanel() {
        mTopPanel.animate().alpha(1).translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTopPanel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        }).start();
    }

    private void hideBottomPanel() {
        int y = mBottomPanel.getTop();
        mBottomPanel.animate().alpha(0.3f).translationY(y).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mBottomPanel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mBottomPanel.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

    private void showBottomPanel() {
        int y = 0;
        mBottomPanel.animate().alpha(1).translationY(y).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mBottomPanel.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).start();
    }

}
