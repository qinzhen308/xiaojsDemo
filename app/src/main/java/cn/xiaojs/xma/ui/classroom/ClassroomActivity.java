package cn.xiaojs.xma.ui.classroom;

import android.Manifest;
import android.animation.Animator;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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

import com.qiniu.pili.droid.streaming.StreamingState;
import com.qiniu.pili.droid.streaming.StreamingStateChangedListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.StreamingStartedNotify;
import cn.xiaojs.xma.ui.classroom.bean.SyncStateResponse;
import cn.xiaojs.xma.ui.classroom.drawer.DrawerLayout;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.MediaContainerView;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardAdapter;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardCollection;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardController;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardManager;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardScrollerView;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;
import io.socket.client.Ack;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.ResponseBody;

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
    private final static int REQUEST_PERMISSION_CODE = 1024;

    private final static int MSG_RESET_TIME = 1024;
    private final static int MSG_PLAY_TIME = 2048;

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
    @BindView(R.id.left_panel)
    View mLeftPanel;
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
    @BindView(R.id.enter_talk_btn)
    MessageImageView mEnterTalkBtn;
    @BindView(R.id.notify_msg_btn)
    MessageImageView mNotifyImgBtn;

    //lesson info
    @BindView(R.id.lesson_title)
    TextView mLessonTitle;
    @BindView(R.id.reset_time)
    TextView mResetTimeTv;
    @BindView(R.id.play_time)
    TextView mPlayTimeTv;
    @BindView(R.id.total_time)
    TextView mTotalTimeTv;
    @BindView(R.id.delay_time)
    TextView mDelayTimeTv;

    //panel btn
    @BindView(R.id.main_screen_setting)
    ImageView mMainScreenSettingBtn;
    @BindView(R.id.wb_toolbar_btn)
    ImageView mWhiteboardToolbarBtn;
    @BindView(R.id.play_pause_btn)
    ImageView mPlayPauseBtn;
    @BindView(R.id.course_ware_btn)
    ImageView mCourseWareBtn;

    //live, whiteboard list
    @BindView(R.id.white_board_scrollview)
    WhiteboardScrollerView mWhiteboardSv;
    @BindView(R.id.my_video)
    LiveRecordView mMyVideo;
    @BindView(R.id.player_container)
    MediaContainerView mContainer;

    private Unbinder mBinder;

    //all kind of panels
    private CourseWarePanel mCourseWarePanel;
    private NotifyMsgPanel mNotifyMsgPanel;
    private InviteFriendPanel mInviteFriendPanel;
    private SettingPanel mSettingPanel;
    private TalkPanel mTalkPanel;
    private Dialog mQuestionAnswerPanel;
    private WhiteboardManageFragment mWhiteBoardManagePanel;

    //gesture
    private GestureDetector mMainPanelGestureDetector;
    private GestureDetector mWhiteboardGestureDetector;
    private GestureDetector mSyncWhitboardGestureDetector;

    ProgressHUD mProgress;

    private ViewGroup mOpenedDrawer;
    private Panel mOpenedPanel;

    private int mCurrentControllerLevel = InteractiveLevel.MAIN_PANEL;
    private boolean mAnimating = false;
    private PanelAnimListener mPanelAnimListener;
    private boolean mNeedOpenWhiteBoardPanel = false;
    private String mLiveSessionState = Live.LiveSessionState.PENDING_FOR_JOIN;

    private WhiteboardController mWhiteboardController;
    private Bundle mExtraData;

    private CommonDialog mExitDialog;
    private CommonDialog mFinishDialog;

    private Socket mSocket;
    private Boolean mSktConnected = false;

    private String mTicket = "";
    private CtlSession mCtlSession;
    private SyncStateResponse mSyncState;
    private Constants.User mUser = Constants.User.STUDENT;
    private int mAppType = Platform.AppType.UNKNOWN;

    private String mPublishUrl;
    private boolean mInitPublishVideo = false;
    private long mResetTotalTime = 30 * 60; //s
    private long mPlayTotalTime = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        //init params
        initParams();

        initPanel();
        initDrawer();
        initLiveProgress();
        initGestureDetector();
        //init nav tips
        showSettingNav();
        //init data
        initData();

    }

    @PermissionSuccess(requestCode = REQUEST_PERMISSION_CODE)
    private void publishStream() {
        mInitPublishVideo = true;
        mMyVideo.setVisibility(View.VISIBLE);
        mMyVideo.setPath(mPublishUrl);
    }

    private void initParams() {
        mBinder = ButterKnife.bind(this);
        mUser = Constants.User.STUDENT;

        mTicket = getIntent().getStringExtra(Constants.KEY_TICKET);
        mPanelAnimListener = new PanelAnimListener();
        mMyVideo.setOnStreamingStateListener(mStreamingStateChangedListener);
    }

    private void initPanel() {
        switch (mUser) {
            case TEACHER:
            case ASSISTANT:
            case REMOTE_ASSISTANT:
                //老师的权限最大，所以都可以使用
                mLeftPanel.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setVisibility(View.VISIBLE);
                mCourseWareBtn.setVisibility(View.VISIBLE);
                mMainScreenSettingBtn.setVisibility(View.VISIBLE);
                break;
            case STUDENT:
                mPlayPauseBtn.setVisibility(View.GONE);
                mCourseWareBtn.setVisibility(View.GONE);
                mMainScreenSettingBtn.setVisibility(View.GONE);
                break;
            case ADMINISTRATOR:
            case AUDITOR:
            case MANAGER:
                mLeftPanel.setVisibility(View.GONE);
                mMainScreenSettingBtn.setVisibility(View.GONE);
                mPlayPauseBtn.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 教室内容分为LiveLayout，WhiteBord，(MainPanel:含视频) 3层， 底层是WhiteBord 通过重写MainPanel,
     * WhiteboardScrollView, Whiteboard的OnTouchEvent来控制事件分发
     */
    private void initGestureDetector() {
        mMainPanelGestureDetector = new GestureDetector(this, new MainPanelGestureListener());
        mWhiteboardGestureDetector = new GestureDetector(this, new WhiteBoardGestureListener());
        mSyncWhitboardGestureDetector = new GestureDetector(this, new SyncWhiteBoardGestureListener());

        mMainPanel.setMainPanelGestureDetector(mMainPanelGestureDetector);
        mMainPanel.setSyncWhiteboardGestureDetector(mSyncWhitboardGestureDetector);
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

    private void initData() {
        /*mEnterTalkBtn.setType(MessageImageView.TYPE_NUM);
        mNotifyImgBtn.setType(MessageImageView.TYPE_MARK);
        int offsetY = getResources().getDimensionPixelOffset(R.dimen.px8);
        mNotifyImgBtn.setExtraOffsetY(offsetY);
        mEnterTalkBtn.setCount(5);
        mNotifyImgBtn.setCount(10);*/

        showProgress(true);
        LiveManager.bootSession(this, mTicket, new APIServiceCallback<CtlSession>() {
            @Override
            public void onSuccess(CtlSession ctlSession) {
                Toast.makeText(ClassroomActivity.this, "BootSession 成功", Toast.LENGTH_SHORT).show();
                if (ctlSession != null) {
                    mCtlSession = ctlSession;
                    mUser = ClassroomBusiness.getUser(ctlSession.psType);
                    mLiveSessionState = ctlSession.state;
                    mLessonTitle.setText(ctlSession.titleOfPrimary);
                    mAppType = ctlSession.connected != null ? ctlSession.connected.app : Platform.AppType.UNKNOWN;

                    //init socket
                    initSocketIO(mTicket, ctlSession.secret);
                    listenSocket();

                    //init ui
                    initPanel();
                    addPlayUrl(ctlSession.playUrl);
                    setPlayPauseBtnStyle(ctlSession.state);
                    mTotalTimeTv.setText(TimeUtil.formatMinuteTime(ctlSession.ctl != null ? ctlSession.ctl.duration : 0));
                    mPublishUrl = ctlSession.publishUrl;
                    if (!mInitPublishVideo) {
                        PermissionGen.needPermission(ClassroomActivity.this, REQUEST_PERMISSION_CODE, Manifest.permission.CAMERA);
                    } else {
                        mMyVideo.setPublishUrl(mPublishUrl);
                        mMyVideo.resume();
                    }

                    //init whiteboard
                    mWhiteboardController = new WhiteboardController(ClassroomActivity.this, mContentRoot, mUser, mAppType);
                    mWhiteboardController.registerDefaultBoard(new WhiteboardManager.WhiteboardAddListener() {
                        @Override
                        public void onWhiteboardAdded(WhiteboardCollection boardCollection) {
                            if (boardCollection != null) {
                                cancelProgress();
                                initNotifyMsgCount();
                            } else {
                                //register failed
                                //ClassroomActivity.this.finish();
                            }
                        }
                    });

                    if (Live.LiveSessionState.LIVE.equals(ctlSession.state)) {
                        setPlayTime(ctlSession.ctl.duration * 60 - ctlSession.finishOn, true);
                    } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(ctlSession.state) ||
                            Live.LiveSessionState.SCHEDULED.equals(ctlSession.state)) {
                        setPendingLivePlayTime(ctlSession.startOn);
                    } else {
                        mPlayTimeTv.setText(TimeUtil.formatSecondTime(ctlSession.hasTaken));
                    }
                } else {
                    Toast.makeText(ClassroomActivity.this, "BootSession 数据返回为null", Toast.LENGTH_SHORT).show();
                    cancelProgress();
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(ClassroomActivity.this, "BootSession 失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                cancelProgress();
            }
        });
    }

    private void addPlayUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            mContainer.addPlayer(url);
        }
    }

    private void setPlayPauseBtnStyle(String liveSessionState) {
        if (Live.LiveSessionState.SCHEDULED.equals(liveSessionState) ||
                Live.LiveSessionState.FINISHED.equals(liveSessionState)) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveSessionState) ||
                Live.LiveSessionState.RESET.equals(liveSessionState)) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_start);
        } else if (Live.LiveSessionState.LIVE.equals(liveSessionState)) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
        }
    }

    /**
     * 未知消息
     */
    private void initNotifyMsgCount() {
        LiveCriteria liveCriteria = new LiveCriteria();
        liveCriteria.to = String.valueOf(Communications.TalkType.SYSTEM);
        Pagination pagination = new Pagination();
        pagination.setPage(1);
        LiveManager.getTalks(this, mTicket, liveCriteria, pagination, new APIServiceCallback<CollectionPage<TalkItem>>() {
            @Override
            public void onSuccess(CollectionPage<TalkItem> collectionPage) {
                if (collectionPage != null) {
                    mNotifyImgBtn.setType(MessageImageView.TYPE_MARK);
                    int offsetY = getResources().getDimensionPixelOffset(R.dimen.px8);
                    mNotifyImgBtn.setExtraOffsetY(offsetY);
                    mNotifyImgBtn.setCount(collectionPage.unread);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {

            }
        });
    }

    @Override
    public void onWhiteboardSelected(Whiteboard whiteboard) {
        //为了控制面板模式也能缩放，移动画布操作
        mMainPanel.setTransformationWhiteBoard(whiteboard);
        whiteboard.setGestureDetector(mWhiteboardGestureDetector);
    }

    @Override
    public void onWhiteboardRemove(Whiteboard whiteboard) {
        mMainPanel.setTransformationWhiteBoard(null);
        whiteboard.setGestureDetector(null);
    }

    public void onSwitchWhiteboardCollection(WhiteboardCollection wbColl) {
        if (wbColl != null && mWhiteboardController != null) {
            if (mUser == Constants.User.STUDENT) {
                if (wbColl.isLive()) {
                    mWhiteBoardPanel.setVisibility(View.GONE);
                    mWhiteboardSv.setVisibility(View.GONE);
                } else {
                    if (mCurrentControllerLevel == InteractiveLevel.WHITE_BOARD &&
                            (mBottomPanel.getVisibility() != View.VISIBLE)) {
                        mWhiteBoardPanel.setVisibility(View.VISIBLE);
                    }
                    mWhiteboardSv.setVisibility(View.VISIBLE);
                }
            }
            mLessonTitle.setText(wbColl.getTitle());
            mWhiteboardController.onSwitchWhiteboardCollection(wbColl);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMyVideo.resume();
        mContainer.resume();

        if (mWhiteboardController != null) {
            mWhiteboardController.onResumeVideo();
        }
    }

    private boolean m = false;

    @OnClick({R.id.back_btn, R.id.blackboard_switcher_btn, R.id.course_ware_btn, R.id.setting_btn,
            R.id.play_pause_btn, R.id.notify_msg_btn, R.id.contact_btn, R.id.qa_btn, R.id.enter_talk_btn,
            R.id.wb_toolbar_btn, R.id.color_picker_btn, R.id.select_btn, R.id.handwriting_btn, R.id.shape_btn,
            R.id.eraser_btn, R.id.text_btn, R.id.finish_btn, R.id.main_screen_setting, R.id.save_white_board_btn,
            R.id.undo, R.id.redo})
    public void onPanelItemClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                showExitDialog();
                break;
            case R.id.finish_btn:
                showFinishDialog();
                break;
            case R.id.blackboard_switcher_btn:
                openWhiteBoardManager();
                break;
            case R.id.play_pause_btn:
                playOrPauseLesson(v);

                //live
                /*if (!m) {
                    mContainer.addPlayer(Config.pathCfu);
                    m = !m;
                    break;
                }
                mContainer.addPlayer(Config.pathHK);*/
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
                openTalk(TalkPanel.MODE_CONTACT);
                break;
            case R.id.qa_btn:
                openQuestionAnswer();
                break;
            case R.id.enter_talk_btn:
                openTalk(TalkPanel.MODE_TALK);
                break;
            case R.id.wb_toolbar_btn:
                switchWhiteBoardToolbar();
                break;
            case R.id.main_screen_setting:
                setWhiteboardMainScreen();
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
     * 开启或暂停课，除老师外的其他参与者的状态变化在socket的回调里面进行处理
     *
     * @see mSyncStateListener
     */
    private void playOrPauseLesson(final View view) {
        if (TextUtils.isEmpty(mTicket) || Live.LiveSessionState.FINISHED.equals(mLiveSessionState)
                || mUser != Constants.User.TEACHER) {
            return;
        }

        showProgress(true);
        if (Live.LiveSessionState.LIVE.equals(mLiveSessionState)) {
            LiveManager.pauseClass(this, mTicket, new APIServiceCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody object) {
                    cancelProgress();

                    //stop stream
                    mLiveSessionState = Live.LiveSessionState.RESET;
                    setPlayPauseBtnStyle(Live.LiveSessionState.RESET);

                    mMyVideo.pause();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    Log.i("aaa", "beginClass errorCode=" + errorCode + "   errorMessage=" + errorMessage);
                }
            });
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(mLiveSessionState)) {
            LiveManager.beginClass(this, mTicket, Live.StreamMode.MUTE, new APIServiceCallback<ClassResponse>() {
                @Override
                public void onSuccess(ClassResponse object) {
                    cancelProgress();
                    mPublishUrl = object != null ? object.publishUrl : null;
                    mLiveSessionState = Live.LiveSessionState.LIVE;
                    setPlayPauseBtnStyle(Live.LiveSessionState.LIVE);
                    if (!mInitPublishVideo) {
                        PermissionGen.needPermission(ClassroomActivity.this, REQUEST_PERMISSION_CODE, Manifest.permission.CAMERA);
                    } else {
                        mMyVideo.setPublishUrl(mPublishUrl);
                        mMyVideo.resume();
                    }
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    Log.i("aaa", "pauseClass errorCode=" + errorCode + "   errorMessage=" + errorMessage);
                }
            });
        } else if (Live.LiveSessionState.RESET.equals(mLiveSessionState)) {
            LiveManager.resumeClass(this, mTicket, Live.StreamMode.MUTE, new APIServiceCallback<ClassResponse>() {
                @Override
                public void onSuccess(ClassResponse object) {
                    cancelProgress();
                    mPublishUrl = object != null ? object.publishUrl : null;
                    mLiveSessionState = Live.LiveSessionState.LIVE;
                    setPlayPauseBtnStyle(Live.LiveSessionState.LIVE);
                    mMyVideo.setPublishUrl(mPublishUrl);
                    mMyVideo.resume();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    Log.i("aaa", "resumeClass errorCode=" + errorCode + "   errorMessage=" + errorMessage);
                }
            });
        } else if (Live.LiveSessionState.SCHEDULED.equals(mLiveSessionState) ||
                Live.LiveSessionState.FINISHED.equals(mLiveSessionState)) {
            //个人推流
        } else {
            cancelProgress();
        }
    }

    /**
     * 打开白板管理
     */
    private void openWhiteBoardManager() {
        if (mWhiteBoardManagePanel == null) {
            mWhiteBoardManagePanel = new WhiteboardManageFragment();
            mExtraData = new Bundle();
        }

        mExtraData.putSerializable(WhiteboardManageFragment.WHITE_BOARD_CLIENT, mUser);
        mWhiteBoardManagePanel.setArguments(mExtraData);
        mWhiteBoardManagePanel.show(getSupportFragmentManager(), "white_board_management");
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
    private void openTalk(int mode) {
        if (mTalkPanel == null) {
            mTalkPanel = new TalkPanel(this, mTicket, mUser);
            mTalkPanel.setPanelCallback(new PanelCallback() {
                @Override
                public void onPanelOpened(int panel) {

                }

                @Override
                public void onPanelClosed(int panel) {

                }

                @Override
                public void switchPanel(int panel) {
                    mTalkPanel.close(mDrawerLayout, mRightDrawer, false);
                    openInviteFriend();
                }
            });
        }
        mTalkPanel.with(mode).show(mDrawerLayout, mRightDrawer);
        mOpenedPanel = mTalkPanel;
    }

    /**
     * 打开通知消息
     */
    private void openAllMessage() {
        if (mNotifyMsgPanel == null) {
            mNotifyMsgPanel = new NotifyMsgPanel(this, mTicket);
        }
        mNotifyMsgPanel.show(mDrawerLayout, mRightDrawer);
        mOpenedPanel = mNotifyMsgPanel;
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
     * 打开关闭白板工具栏 若正在动画，直接返回 1.若当前在顶部和底部的面板显示，先隐藏顶部底部隐藏动画完成后，再显示白板操作面板 否则，直接隐藏或显示白板操作面板
     */
    private void switchWhiteBoardToolbar() {
        if (mAnimating || mWhiteboardController == null) {
            return;
        }

        if (mUser == Constants.User.STUDENT && mWhiteboardController.isLiveWhiteboard()) {
            Toast.makeText(this, R.string.wb_toolbar_unable_tips, Toast.LENGTH_SHORT).show();
            return;
        }

        if (mBottomPanel.getVisibility() == View.VISIBLE && mTopPanel.getVisibility() == View.VISIBLE) {
            //若当前在顶部和底部的面板显示，先隐藏顶部底部隐藏动画完成后，再显示白板操作面板
            mNeedOpenWhiteBoardPanel = true;
            mCurrentControllerLevel = InteractiveLevel.WHITE_BOARD;
            hideTopBottomPanel();
        } else {
            if (mWhiteBoardPanel.getVisibility() == View.VISIBLE) {
                mCurrentControllerLevel = InteractiveLevel.MAIN_PANEL;
                hideWhiteBoardPanel();
            } else {
                mCurrentControllerLevel = InteractiveLevel.WHITE_BOARD;
                showWhiteBoardPanel(true);
            }
        }
    }

    private class MainPanelGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("aaa", "========MainPanelGestureListener====onSingleTapConfirmed=============" + mCurrentControllerLevel);
            if (mCurrentControllerLevel == InteractiveLevel.MAIN_PANEL && !mAnimating) {
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
            Log.i("aaa", "=========WhiteBoardGestureListener===onSingleTapConfirmed=============" + mCurrentControllerLevel);
            return super.onSingleTapConfirmed(e);
        }

    }

    private class SyncWhiteBoardGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("aaa", "=========SyncWhiteBoardGestureListener===onSingleTapConfirmed=============" + mCurrentControllerLevel);
            if (!mAnimating) {
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

    @Override
    protected void onPause() {
        super.onPause();
        mMyVideo.pause();
        mContainer.pause();

        if (mWhiteboardController != null) {
            mWhiteboardController.onPauseVideo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMyVideo.destroy();
        mContainer.destroy();

        WhiteboardManager.getInstance().release();
        if (mBinder != null) {
            mBinder.unbind();
        }

        if (mWhiteboardController != null) {
            mWhiteboardController.onDestroyVideo();
        }

        if (mWhiteboardController != null) {
            mWhiteboardController.release();
        }

        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
            mProgress = null;
        }

        if (mHandler != null) {
            mHandler.removeMessages(MSG_RESET_TIME);
            mHandler.removeMessages(MSG_PLAY_TIME);
        }

        cancelAllAnim();
        disConnectIO();
    }

    public int getInteractiveLevel() {
        return mCurrentControllerLevel;
    }

    public String getTicket() {
        return mTicket;
    }

    private void saveWhiteboard() {
        if (mWhiteboardController == null) {
            return;
        }

        mWhiteboardController.saveWhiteboard();
    }

    private void setWhiteboardMainScreen() {
        if (mWhiteboardController == null) {
            return;
        }

        mWhiteboardController.setWhiteboardMainScreen();
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
                                    showWhiteBoardPanel(false);
                                }
                            }
                            break;
                        case R.id.white_board_panel:
                            if (mWhiteBoardPanel != null) {
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

    public boolean isSyncWhiteboard() {
        if (mWhiteboardController == null) {
            return false;
        }

        return mWhiteboardController.isSyncWhiteboard();
    }

    public void updateWhiteboardCollCountStyle(int wbCollSize) {
        if (wbCollSize > 1) {
            mWhiteboardCollCountTv.setVisibility(View.VISIBLE);
            mWhiteboardCollCountTv.setText(String.valueOf(wbCollSize));
        }
    }

    public void updateWhiteboardCollCountStyle() {
        if (mWhiteboardController == null) {
            return;
        }

        List<WhiteboardCollection> collections = WhiteboardManager.getInstance().getWhiteboardCollectionList();
        int wbCollSize = collections != null ? collections.size() : 0;
        updateWhiteboardCollCountStyle(wbCollSize);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mOpenedDrawer != null && mOpenedPanel != null) {
                mOpenedPanel.close(mDrawerLayout, mOpenedDrawer);
                return false;
            } else {
                showExitDialog();
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    public void showProgress(boolean cancellable) {
        if (mProgress == null) {
            mProgress = ProgressHUD.create(this);
        }
        mProgress.setCancellable(cancellable);
        mProgress.show();
    }

    public void cancelProgress() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }


    private void showExitDialog() {
        if (mExitDialog == null) {
            mExitDialog = new CommonDialog(this);
            mExitDialog.setTitle(R.string.exit_classroom);
            mExitDialog.setDesc(R.string.exit_classroom_tips);
            int width = DeviceUtil.getScreenWidth(this) / 2;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mExitDialog.setDialogLayout(width, height);

            mExitDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mExitDialog.dismiss();
                }
            });

            mExitDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    ClassroomActivity.this.finish();
                    mExitDialog.dismiss();
                }
            });
        }

        mExitDialog.show();
    }


    private void showFinishDialog() {
        if (mFinishDialog == null) {
            mFinishDialog = new CommonDialog(this);
            mFinishDialog.setTitle(R.string.finish_classroom);
            mFinishDialog.setDesc(R.string.finish_classroom_tips);
            int width = DeviceUtil.getScreenWidth(this) / 2;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mFinishDialog.setDialogLayout(width, height);

            mFinishDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mFinishDialog.dismiss();
                }
            });

            mFinishDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mFinishDialog.dismiss();
                    finishClassroom();
                }
            });
        }

        mFinishDialog.show();
    }

    private void finishClassroom() {
        showProgress(true);
        LiveManager.finishClass(this, mTicket, new APIServiceCallback<ClassResponse>() {
            @Override
            public void onSuccess(ClassResponse object) {
                cancelProgress();
                //ClassroomActivity.this.finish();
                mLiveSessionState = Live.LiveSessionState.FINISHED;
                setPlayPauseBtnStyle(mLiveSessionState);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
            }
        });
    }


    private void initSocketIO(String ticket, String secret) {
        SocketManager.init(ticket, secret);
        mSocket = SocketManager.getSocket();
        if (mSocket != null) {
            mSocket.connect();
        }
    }

    private void listenSocket() {
        if (mSocket == null) {
            return;
        }

        mSocket.on(Socket.EVENT_CONNECT, mOnConnect);
        mSocket.on(Socket.EVENT_DISCONNECT, mOnDisconnect);
        mSocket.on(Socket.EVENT_CONNECT_ERROR, mOnConnectError);
        mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, mOnConnectError);
        mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STARTED), mStreamingStartedListener);
        mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.STREAMING_STOPPED), mStreamingStoppedListener);
        mSocket.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_STATE), mSyncStateListener);
    }

    private void disConnectIO() {
        if (mSocket == null) {
            return;
        }

        //mSocket.off(Socket.EVENT_CONNECT, mOnConnect);
        //mSocket.off(Socket.EVENT_DISCONNECT, mOnDisconnect);
        //mSocket.off(Socket.EVENT_CONNECT_ERROR, mOnConnectError);
        //mSocket.off(Socket.EVENT_CONNECT_TIMEOUT, mOnConnectError);
        //mSocket.off(Event.WELCOME, mOnWelcome);

        //off all
        mSocket.off();
        SocketManager.close();
    }

    private Emitter.Listener mOnConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!mSktConnected) {
                        Toast.makeText(ClassroomActivity.this, R.string.socket_connect, Toast.LENGTH_LONG).show();
                        mSktConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener mOnDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mSktConnected = false;
                    Toast.makeText(ClassroomActivity.this, R.string.socket_disconnect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener mOnConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ClassroomActivity.this, R.string.socket_error_connect, Toast.LENGTH_LONG).show();
                }
            });
        }
    };

    private Emitter.Listener mSyncStateListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                mSyncState = ClassroomBusiness.parseSocketBean(args[0], SyncStateResponse.class);
                if (mSyncState != null) {
                    if (Live.LiveSessionState.SCHEDULED.equals(mSyncState.from) && Live.LiveSessionState.PENDING_FOR_JOIN.equals(mSyncState.to)) {
                        mLiveSessionState = Live.LiveSessionState.PENDING_FOR_JOIN;
                        setPlayPauseBtnStyle(mLiveSessionState);
                    } else if ((Live.LiveSessionState.PENDING_FOR_JOIN.equals(mSyncState.from) && Live.LiveSessionState.LIVE.equals(mSyncState.to))
                            || (Live.LiveSessionState.RESET.equals(mSyncState.from) && Live.LiveSessionState.LIVE.equals(mSyncState.to))) {
                        if (mUser != Constants.User.TEACHER) {
                            mLiveSessionState = Live.LiveSessionState.LIVE;
                            //非老师端播放推流地址
                        }

                        setResetTime(false);
                        setPlayTime(mSyncState.timeline != null ? mSyncState.timeline.hasTaken : 0, true);
                    } else if (Live.LiveSessionState.LIVE.equals(mSyncState.from) && Live.LiveSessionState.RESET.equals(mSyncState.to)) {
                        if (mUser != Constants.User.TEACHER) {
                            mLiveSessionState = Live.LiveSessionState.RESET;
                            //非老师端暂停播放
                        }

                        setResetTime(true);
                        setPlayTime(mSyncState.timeline != null ? mSyncState.timeline.hasTaken : 0, false);
                    }
                }
            }
        }
    };

    private Emitter.Listener mStreamingStartedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                StreamingStartedNotify startedNotify = ClassroomBusiness.parseSocketBean(args[0], StreamingStartedNotify.class);
                if (startedNotify != null) {
                    mPublishUrl = startedNotify.RTMPPlayUrl;
                    PermissionGen.needPermission(ClassroomActivity.this, REQUEST_PERMISSION_CODE, Manifest.permission.CAMERA);
                }
            }
        }
    };

    private Emitter.Listener mStreamingStoppedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {

            }
        }
    };

    /**
     * 流推送监听
     */
    private StreamingStateChangedListener mStreamingStateChangedListener = new StreamingStateChangedListener() {
        @Override
        public void onStateChanged(StreamingState streamingState, Object o) {
            switch (streamingState) {
                case STREAMING:
                    if (mSocket != null) {
                        mSocket.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.STREAMING_STARTED), new Ack() {
                            @Override
                            public void call(final Object... args) {
                                if (args != null && args.length > 0) {
                                    StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                                    if (response != null && response.result) {
                                        Toast.makeText(ClassroomActivity.this, "推流开始", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    }
                    break;
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WhiteboardManageFragment.REQUEST_OPEN_DOCS && mWhiteBoardManagePanel != null) {
            mWhiteBoardManagePanel.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setResetTime(boolean show) {
        mHandler.removeMessages(MSG_RESET_TIME);
        mResetTotalTime = 30 * 60;

        Message msg = new Message();
        msg.what = MSG_RESET_TIME;
        msg.arg1 = show ? 1 : 0;
        mHandler.sendMessage(msg);
    }

    private void setPlayTime(long hasTaken, boolean auto) {
        mHandler.removeMessages(MSG_PLAY_TIME);
        mPlayTotalTime = hasTaken;

        Message msg = new Message();
        msg.what = MSG_PLAY_TIME;
        msg.arg1 = auto ? 1 : 0;
        mHandler.sendMessage(msg);
    }

    private void setPendingLivePlayTime(long startOn) {
        mHandler.removeMessages(MSG_PLAY_TIME);
        mPlayTotalTime = startOn;

        Message msg = new Message();
        msg.what = MSG_PLAY_TIME;
        msg.arg1 = 2;
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                Message m = mHandler.obtainMessage();
                switch (msg.what) {
                    case MSG_RESET_TIME:
                        if (msg.arg1 == 1) {
                            mResetTotalTime--;
                            mResetTimeTv.setVisibility(View.VISIBLE);
                            mResetTimeTv.setText(TimeUtil.formatSecondTime(mResetTotalTime));
                            m.what = MSG_RESET_TIME;
                            m.arg1 = 1;
                            mHandler.sendMessageDelayed(m, 1000);
                        } else {
                            mResetTimeTv.setVisibility(View.GONE);
                        }
                        break;
                    case MSG_PLAY_TIME:
                        if (msg.arg1 == 1) {
                            mPlayTotalTime++;
                            mPlayTimeTv.setText(TimeUtil.formatSecondTime(mPlayTotalTime));
                            m.what = MSG_PLAY_TIME;
                            m.arg1 = 1;
                            mHandler.sendMessageDelayed(m, 1000);
                        } else if (msg.arg1 == 2) {
                            mPlayTotalTime--;
                            mPlayTimeTv.setText(TimeUtil.formatSecondTime(mPlayTotalTime));
                            m.what = MSG_PLAY_TIME;
                            m.arg1 = 2;
                            mHandler.sendMessageDelayed(m, 1000);
                        } else {
                            mPlayTimeTv.setText(TimeUtil.formatSecondTime(mPlayTotalTime));
                        }
                        break;
                }
            }
        }
    };
}
