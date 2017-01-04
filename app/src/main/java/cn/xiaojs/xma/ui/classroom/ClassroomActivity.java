package cn.xiaojs.xma.ui.classroom;

import android.Manifest;
import android.animation.Animator;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionFail;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.ui.classroom.drawer.DrawerLayout;
import cn.xiaojs.xma.ui.classroom.live.core.Config;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.MediaContainerView;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardAdapter;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardController;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardLayer;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardScrollerView;
import cn.xiaojs.xma.util.CacheUtil;
import cn.xiaojs.xma.util.XjsUtils;

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

public class ClassroomActivity extends FragmentActivity implements WhiteboardAdapter.OnWhiteboardListener {
    private final static float LIVE_PROGRESS_WIDTH_FACTOR = 0.55F;
    private static final int REQUEST_GALLERY_PERMISSION = 1000;
    private static final String[] PERMISSIONS = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private final static int ANIM_SHOW = 1 << 1;
    private final static int ANIM_HIDE = 1 << 2;

    private final static int STATE_PLAY = 1;
    private final static int STATE_PAUSE = 2;
    private final static int STATE_STOP = 3;

    //drawer
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.drawer_left_layout)
    ViewGroup mLeftDrawer;
    @BindView(R.id.drawer_right_layout)
    ViewGroup mRightDrawer;

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
    @BindView(R.id.whiteboard_coll_count)
    TextView mWhiteboardCollCountTv;

    //live, whiteboard list
    @BindView(R.id.white_board_scrollview)
    WhiteboardScrollerView mWhiteboardSv;
    @BindView(R.id.teacher_video)
    LiveRecordView mTeacherVideo;
    @BindView(R.id.player_container)
    MediaContainerView mContainer;

    private Unbinder mBinder;

    //whiteboard adapter
    private WhiteboardAdapter mWhiteboardAdapter;

    //all kind of panels
    private CourseWarePanel mCourseWarePanel;
    private MessagePanel mMessagePanel;
    private InviteFriendPanel mInviteFriendPanel;
    private SettingPanel mSettingPanel;
    private ChatPanel mChatPanel;
    private Dialog mQuestionAnswerPanel;
    private DialogFragment mWhiteBoardManagePanel;

    //gesture
    private ClassroomGestureDetector mMainPanelGestureDetector;
    private ClassroomGestureDetector mWhiteboardGestureDetector;

    private ViewGroup mOpenedDrawer;
    private Panel mOpenedPanel;

    private int mCurrentState = ClassroomState.STATE_MAIN_PANEL;
    private boolean mAnimating = false;
    private PanelAnimListener mPanelAnimListener;
    private boolean mNeedOpenWhiteBoardPanel = false;
    private boolean mSavingWhiteboard = false;
    private int mPlayState = STATE_PLAY;

    private ArrayList<WhiteboardCollection> mWhiteboardCollectionList;
    private WhiteboardController mWhiteboardController;
    private Whiteboard mCurrWhiteboard;
    private String mWhiteboardSuffix;
    private AsyncTask mSaveTask;
    private Bundle mExtraData;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        //init params
        initParams();

        initDrawer();
        initLiveProgress();
        initGestureDetector();
        //init whiteboard
        initWhiteboardData();
        //init nav tips
        showSettingNav();

        mTeacherVideo.setPath(Config.pathPush);
    }

    private void initParams() {
        mBinder = ButterKnife.bind(this);

        mPanelAnimListener = new PanelAnimListener();
        mWhiteboardCollectionList = new ArrayList<WhiteboardCollection>();
        mWhiteboardSuffix = getString(R.string.white_board);
    }

    /**
     * 教室内容分为WhiteBord，(MainPanel:含视频) 2层， 底层是WhiteBord 通过重写MainPanel, WhiteboardScrollView,
     * Whiteboard的OnTouchEvent来控制事件分发
     */
    private void initGestureDetector() {
        mMainPanelGestureDetector = new ClassroomGestureDetector(this, new MainPanelGestureListener());
        mWhiteboardGestureDetector = new ClassroomGestureDetector(this, new WhiteBoardGestureListener());

        mMainPanel.setGestureDetector(mMainPanelGestureDetector);
        mMainPanel.setWhiteboardSv(mWhiteboardSv);
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
                mOpenedDrawer = (ViewGroup) drawerView;
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mOpenedDrawer = null;
                mOpenedPanel = null;
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

    private void initWhiteboardData() {
        if (mWhiteboardAdapter == null) {
            mWhiteboardAdapter = new WhiteboardAdapter(this);
        }

        if (mWhiteboardController == null) {
            mWhiteboardController = new WhiteboardController(this, mContentRoot);
        }

        WhiteboardCollection whiteboardCollection = new WhiteboardCollection();
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        whiteboardCollection.addWhiteboardLayer(new WhiteboardLayer());
        addToWhiteboardCollectionList(whiteboardCollection);

        mWhiteboardAdapter.setData(whiteboardCollection.getWhiteboardLayer());
        mWhiteboardSv.setAdapter(mWhiteboardAdapter);
        mWhiteboardSv.setOffscreenPageLimit(2);
        mWhiteboardAdapter.notifyDataSetChanged();
        mWhiteboardAdapter.setOnWhiteboardListener(this);
    }

    /**
     * 显示设置导航提示
     */
    private void showSettingNav() {
        SharedPreferences sf = XjsUtils.getSharedPreferences();
        boolean flag = sf.getBoolean(Constants.KEY_CLASSROOM_FIRST_USE, true);
        if (flag) {
            SettingDialog setDialog = new SettingDialog(this);
            setDialog.show();

            sf.edit().putBoolean(Constants.KEY_CLASSROOM_FIRST_USE, false).commit();
        }

    }

    @Override
    public void onWhiteboardSelected(Whiteboard whiteboard) {
        mCurrWhiteboard = whiteboard;
        if (mWhiteboardController != null) {
            //为了控制面板模式也能缩放，移动画布操作
            mMainPanel.setTransformationWhiteBoard(whiteboard);
            mWhiteboardController.setWhiteboard(whiteboard);

            whiteboard.setGestureDetector(mWhiteboardGestureDetector);
        }
    }

    @Override
    public void onWhiteboardRemove(Whiteboard whiteboard) {
        if (mWhiteboardController != null) {
            mMainPanel.setTransformationWhiteBoard(null);
            mWhiteboardController.setWhiteboard(null);

            if (whiteboard != null) {
                whiteboard.setGestureDetector(null);
            }
        }
    }

    private void updateWhiteboardData(List<WhiteboardLayer> layers) {
        mWhiteboardAdapter.setData(layers);
        mWhiteboardAdapter.notifyDataSetChanged();
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
            R.id.exit_btn, R.id.main_screen_setting, R.id.save_white_board_btn, R.id.undo, R.id.redo})
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
                    ((ImageView) v).setImageResource(R.drawable.ic_cr_pause);
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
                saveWhiteboard();
                break;
            case R.id.select_btn:
            case R.id.handwriting_btn:
            case R.id.shape_btn:
            case R.id.eraser_btn:
            case R.id.text_btn:
            case R.id.color_picker_btn:
            case R.id.undo:
            case R.id.redo:
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
            mExtraData = new Bundle();
        }
        mWhiteBoardManagePanel.show(getSupportFragmentManager(), "white_board_management");
        mExtraData.putParcelableArrayList(WhiteBoardManagement.WHITE_BOARD_COLL, mWhiteboardCollectionList);
        mWhiteBoardManagePanel.setArguments(mExtraData);
    }

    /**
     * 打开课件，课件的布局是提前在xml布局添加了，这里打开课件只需要绑定数据
     */
    private void openCourseWarePanel() {
        if (mCourseWarePanel == null) {
            mCourseWarePanel = new CourseWarePanel(this);
        }
        mCourseWarePanel.show(mDrawerLayout, mLeftDrawer);
        mOpenedPanel = mCourseWarePanel;
    }

    /**
     * 打开设置
     */
    private void openSetting() {
        if (mSettingPanel == null) {
            mSettingPanel = new SettingPanel(this);
        }
        mSettingPanel.show(mDrawerLayout, mRightDrawer);
        mOpenedPanel = mSettingPanel;
    }

    /**
     * 打开聊天
     */
    private void openChat(int mode) {
        if (mChatPanel == null) {
            mChatPanel = new ChatPanel(this);
            mChatPanel.setPanelCallback(new PanelCallback() {
                @Override
                public void onOpenPanel(int panel) {
                    mChatPanel.close(mDrawerLayout, mRightDrawer, false);
                    openInviteFriend();
                }
            });
        }
        mChatPanel.with(mode).show(mDrawerLayout, mRightDrawer);
        mOpenedPanel = mChatPanel;
    }

    /**
     * 打开通知消息
     */
    private void openAllMessage() {
        if (mMessagePanel == null) {
            mMessagePanel = new MessagePanel(this);
        }
        mMessagePanel.show(mDrawerLayout, mRightDrawer);
        mOpenedPanel = mMessagePanel;
    }

    /**
     * 邀请好友
     */
    private void openInviteFriend() {
        if (mInviteFriendPanel == null) {
            mInviteFriendPanel = new InviteFriendPanel(this);
        }

        mInviteFriendPanel.show(mDrawerLayout, mRightDrawer);
        mOpenedPanel = mInviteFriendPanel;
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

        if (mCurrentState == ClassroomState.STATE_MAIN_PANEL) {
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
            if (mCurrentState == ClassroomState.STATE_MAIN_PANEL && !mAnimating) {
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

        if (mSaveTask != null) {
            mSavingWhiteboard = false;
            mSaveTask.cancel(true);
        }
    }

    public int getCurrentState() {
        return mCurrentState;
    }

    private void saveWhiteboard() {
        if (mSavingWhiteboard) {
            return;
        }

        mSavingWhiteboard = true;
        //save to local
        //PermissionGen.needPermission(ClassroomActivity.this, REQUEST_GALLERY_PERMISSION, PERMISSIONS);

        //save to server
        //TODO
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

        if (mWhiteboardController != null) {
            mWhiteboardController.exitWhiteboard();
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

        if (needAnim) {
            mWhiteBoardPanel.animate()
                    .alpha(1.0f)
                    .setListener(mPanelAnimListener.with(mWhiteBoardPanel).play(ANIM_SHOW))
                    .start();
        } else {
            mWhiteBoardPanel.setAlpha(1.0f);
            mWhiteBoardPanel.setVisibility(View.VISIBLE);
            if (mWhiteboardController != null) {
                mWhiteboardController.setUndoRedoStyle();
            }
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
                                mCurrentState = ClassroomState.STATE_WHITE_BOARD;
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
                                    mCurrentState = ClassroomState.STATE_WHITE_BOARD;
                                    showWhiteBoardPanel(false);
                                }
                            }
                            break;
                        case R.id.white_board_panel:
                            if (mWhiteBoardPanel != null) {
                                mCurrentState = ClassroomState.STATE_MAIN_PANEL;
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

    public int getState() {
        return mCurrentState;
    }

    private void addToWhiteboardCollectionList(WhiteboardCollection collection) {
        if (collection != null) {
            if (TextUtils.isEmpty(collection.getName())) {
                int count = 0;
                for (WhiteboardCollection coll : mWhiteboardCollectionList) {
                    if (coll.isDefaultWhiteboard()) {
                        count++;
                    }
                }
                String name = mWhiteboardSuffix + "_" + (count + 1);
                collection.setName(name);
            }
            mWhiteboardCollectionList.add(collection);
        }
    }

    /**
     * 白板管理界面添加新的白板回调
     */
    public void onAddWhiteboardCollection(WhiteboardCollection collection) {
        addToWhiteboardCollectionList(collection);

        updateWhiteboardCollCountStyle();
    }

    public void updateWhiteboardCollCountStyle() {
        if (mWhiteboardCollectionList != null && mWhiteboardCollectionList.size() > 1) {
            mWhiteboardCollCountTv.setVisibility(View.VISIBLE);
            mWhiteboardCollCountTv.setText(String.valueOf(mWhiteboardCollectionList.size()));
        }
    }

    @PermissionSuccess(requestCode = REQUEST_GALLERY_PERMISSION)
    public void getGallerySuccess() {
        if (mSavingWhiteboard) {
            if (mCurrWhiteboard == null) {
                mSavingWhiteboard = false;
                return;
            }

            final Bitmap bmp = mCurrWhiteboard.getWhiteboardBitmap();
            final WhiteboardLayer layer = mCurrWhiteboard.getLayer();
            if (layer != null) {
                CacheUtil.saveWhiteboard(bmp, layer.getWhiteboardId());
                mSaveTask = new AsyncTask<Integer, Integer, String>() {

                    @Override
                    protected String doInBackground(Integer... params) {
                        return CacheUtil.saveWhiteboard(bmp, layer.getWhiteboardId());
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        mSavingWhiteboard = false;
                        String tips = getString(!TextUtils.isEmpty(result) ? R.string.save_white_board_succ :
                                R.string.save_white_board_fail);
                        Toast.makeText(ClassroomActivity.this, tips, Toast.LENGTH_SHORT).show();
                    }
                }.execute(0);
            } else {
                mSavingWhiteboard = false;
            }
        }
    }

    @PermissionFail(requestCode = REQUEST_GALLERY_PERMISSION)
    public void getGalleryFailure() {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mOpenedDrawer != null && mOpenedPanel != null) {
                mOpenedPanel.close(mDrawerLayout, mOpenedDrawer);
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
