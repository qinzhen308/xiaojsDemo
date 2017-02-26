package cn.xiaojs.xma.ui.classroom;

import android.Manifest;
import android.animation.Animator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import com.qiniu.pili.droid.streaming.FrameCapturedCallback;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.ApiManager;
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
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardAdapter;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardCollection;
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

    private final static int MSG_RESET_TIME = 1 << 0;
    private final static int MSG_PLAY_TIME = 1 << 1;
    private final static int MSG_PLAY_BTN_SETTING = 1 << 2;
    private final static int MSG_PLAY_VIDEO = 1 << 3;
    private final static int MSG_DELAY_TO_FINISH = 1 << 4;

    private final static int ANIM_SHOW = 1 << 1;
    private final static int ANIM_HIDE = 1 << 2;

    private final static int PAGE_EDIT_VIDEO = 1024;
    private final static int PAGE_TOP = 2048;

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
    @BindView(R.id.video_doodle_panel)
    View mVideoDoodlePanel;
    @BindView(R.id.publish_video_panel)
    View mPublishVideoPanel;

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
    @BindView(R.id.publish_time)
    TextView mPublishTimeTv;

    //panel btn
    @BindView(R.id.main_screen_setting)
    ImageView mMainScreenSettingBtn;
    @BindView(R.id.wb_toolbar_btn)
    ImageView mWhiteboardToolbarBtn;
    @BindView(R.id.play_pause_btn)
    ImageView mPlayPauseBtn;
    @BindView(R.id.course_ware_btn)
    ImageView mCourseWareBtn;
    @BindView(R.id.save_white_board_btn)
    ImageView mSageWhiteBoardBtn;
    @BindView(R.id.blackboard_switcher_btn)
    ImageView mBoardSwitcherBtn;
    @BindView(R.id.publish_take_pic)
    ImageView mTakePicBtn;
    @BindView(R.id.publish_camera_switcher)
    ImageView mCameraSwitcher;
    @BindView(R.id.finish_btn)
    ImageView mFinishClassBtn;

    //live, whiteboard list
    @BindView(R.id.white_board_scrollview)
    WhiteboardScrollerView mWhiteboardSv;

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

    private ProgressHUD mProgress;

    private ViewGroup mOpenedDrawer;
    private Panel mOpenedPanel;

    private int mCurrentControllerLevel = InteractiveLevel.MAIN_PANEL;
    private boolean mAnimating = false;
    private PanelAnimListener mPanelAnimListener;
    private boolean mNeedOpenWhiteBoardPanel = false;
    private String mLiveSessionState = Live.LiveSessionState.PENDING_FOR_JOIN;

    private ClassroomController mClassroomController;
    private Bundle mExtraData;

    private CommonDialog mExitDialog;
    private CommonDialog mFinishDialog;

    private Socket mSocket;
    private Boolean mSktConnected = false;

    private String mTicket = "";
    private SyncStateResponse mSyncState;
    private Constants.User mUser = Constants.User.STUDENT;
    private int mAppType = Platform.AppType.UNKNOWN;

    private long mLessonDuration;
    private long mResetTotalTime = 30 * 60; //s
    private long mPlayTotalTime = 0;

    private int mPageState = PAGE_TOP;
    private Bitmap mCaptureFrame;
    private String mPlayUrl;
    private NetworkChangedBReceiver mNetworkChangedBReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        //init params
        initParams();

        hidePanel();
        initDrawer();
        initLiveProgress();
        initGestureDetector();
        //init nav tips
        showSettingNav();

        //init data
        initData();

        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.CAPTURE_AUDIO_OUTPUT};
        PermissionGen.needPermission(this, 100, permissions);

        //register network
        registerNetworkReceiver();
    }

    private void initParams() {
        mBinder = ButterKnife.bind(this);
        mUser = Constants.User.STUDENT;

        mTicket = getIntent().getStringExtra(Constants.KEY_TICKET);
        mPanelAnimListener = new PanelAnimListener();
    }

    private void initPanel() {
        switch (mUser) {
            case TEACHER:
            case ASSISTANT:
            case REMOTE_ASSISTANT:
                break;
            case STUDENT:
                mCameraSwitcher.setVisibility(View.GONE);
                mFinishClassBtn.setVisibility(View.GONE);
                break;
            case ADMINISTRATOR:
            case AUDITOR:
            case MANAGER:

                break;
        }
    }

    /**
     * 第一期教室没有课件，没有白板切换，没有白板管理
     */
    private void hidePanel() {
        mLeftPanel.setVisibility(View.GONE);
        mMainScreenSettingBtn.setVisibility(View.GONE);
        mCourseWareBtn.setVisibility(View.GONE);
        mBoardSwitcherBtn.setVisibility(View.GONE);
        mSageWhiteBoardBtn.setVisibility(View.GONE);
    }

    /**
     * 教室内容分为video，WhiteBord，(MainPanel:含视频) 3层， 底层是WhiteBord 通过重写MainPanel, WhiteboardScrollView,
     * Whiteboard的OnTouchEvent来控制事件分发
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
        mLiveProgress.setEnabled(false);
    }

    private void initWhiteboardController() {
        //init whiteboard
        mClassroomController = new ClassroomController(ClassroomActivity.this, mContentRoot, mUser, mAppType);
    }

    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        mNetworkChangedBReceiver = new NetworkChangedBReceiver();
        registerReceiver(mNetworkChangedBReceiver, filter);
    }

    private void unregisterNetworkReceiver() {
        if (mNetworkChangedBReceiver != null) {
            unregisterReceiver(mNetworkChangedBReceiver);
        }
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
                cancelProgress();
                Toast.makeText(ClassroomActivity.this, "BootSession 成功", Toast.LENGTH_SHORT).show();
                if (ctlSession != null) {
                    mUser = ClassroomBusiness.getUser(ctlSession.psType);
                    mLiveSessionState = ctlSession.state;
                    mLessonTitle.setText(!TextUtils.isEmpty(ctlSession.titleOfPrimary) ? ctlSession.titleOfPrimary : ctlSession.ctl.title);
                    mAppType = ctlSession.connected != null ? ctlSession.connected.app : Platform.AppType.UNKNOWN;

                    //init socket
                    initSocketIO(mTicket, ctlSession.secret);
                    listenSocket();

                    //init ui
                    initPanel();
                    setControllerBtnStyle(ctlSession.state);
                    mLessonDuration = ctlSession.ctl != null ? ctlSession.ctl.duration : 0;
                    mTotalTimeTv.setText(TimeUtil.formatMinuteTime(mLessonDuration));

                    //init whiteboard
                    initWhiteboardController();

                    if (Live.LiveSessionState.LIVE.equals(ctlSession.state)) {
                        setPlayTime(ctlSession.ctl.duration * 60 - ctlSession.finishOn, true);
                        if (mUser == Constants.User.TEACHER) {
                            mClassroomController.publishStream(ctlSession.publishUrl);
                        } else if (mUser == Constants.User.STUDENT) {
                            mPlayUrl = ctlSession.playUrl;
                            mClassroomController.playTeaVideo(mPlayUrl);
                        }
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

    private void setControllerBtnStyle(String liveSessionState) {
        if (Live.LiveSessionState.SCHEDULED.equals(liveSessionState) ||
                Live.LiveSessionState.FINISHED.equals(liveSessionState)) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
            mPlayPauseBtn.setVisibility(View.VISIBLE);
            if (Live.LiveSessionState.FINISHED.equals(liveSessionState)) {
                mFinishClassBtn.setVisibility(View.GONE);
            }
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveSessionState) ||
                Live.LiveSessionState.RESET.equals(liveSessionState)) {
            if (mUser == Constants.User.TEACHER) {
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_start);
            } else {
                mPlayPauseBtn.setVisibility(View.GONE);
            }
        } else if (Live.LiveSessionState.LIVE.equals(liveSessionState)) {
            if (mUser == Constants.User.TEACHER) {
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
            } else {
                mPlayPauseBtn.setVisibility(View.GONE);
            }
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
        if (wbColl != null && mClassroomController != null) {
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
            mClassroomController.onSwitchWhiteboardCollection(wbColl);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mClassroomController != null) {
            mClassroomController.onResumeVideo();
        }
    }

    private boolean m = false;

    @OnClick({R.id.back_btn, R.id.blackboard_switcher_btn, R.id.course_ware_btn, R.id.setting_btn,
            R.id.play_pause_btn, R.id.notify_msg_btn, R.id.contact_btn, R.id.qa_btn, R.id.enter_talk_btn,
            R.id.wb_toolbar_btn, R.id.color_picker_btn, R.id.select_btn, R.id.handwriting_btn, R.id.shape_btn,
            R.id.eraser_btn, R.id.text_btn, R.id.finish_btn, R.id.main_screen_setting, R.id.save_white_board_btn,
            R.id.undo, R.id.redo, R.id.publish_camera_switcher, R.id.publish_take_pic, R.id.back_in_doodle,
            R.id.save_doodle, R.id.share_doodle})
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
            case R.id.publish_camera_switcher:
                switchCamera();
                break;
            case R.id.publish_take_pic:
                enterVideoEditing();
                break;
            case R.id.back_in_doodle:
                exitVideoEditing();
                break;
            case R.id.save_doodle:
                break;
            case R.id.share_doodle:
                selectShareContact(v);
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
        if (TextUtils.isEmpty(mTicket)) {
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
                    setControllerBtnStyle(Live.LiveSessionState.RESET);

                    mClassroomController.pauseVideo();
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    Log.i("aaa", "beginClass errorCode=" + errorCode + "   errorMessage=" + errorMessage);
                }
            });
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(mLiveSessionState)) {
            LiveManager.beginClass(this, mTicket, Live.StreamMode.MUTE, new APIServiceCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody object) {
                    cancelProgress();
                    ClassResponse response = ApiManager.getClassResponse(object);

                    mLiveSessionState = Live.LiveSessionState.LIVE;
                    setControllerBtnStyle(Live.LiveSessionState.LIVE);
                    mClassroomController.publishStream(response != null ? response.publishUrl : null);
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
                    mLiveSessionState = Live.LiveSessionState.LIVE;
                    setControllerBtnStyle(Live.LiveSessionState.LIVE);
                    mClassroomController.publishStream(object != null ? object.publishUrl : null);
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
            cancelProgress();
            mSocket.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.CLAIM_STREAMING),
                    Live.StreamMode.MUTE, new Ack() {
                @Override
                public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (args != null) {
                                StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                                if (response.result) {
                                    Toast.makeText(ClassroomActivity.this, "claim streaming succ", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ClassroomActivity.this, "claim streaming fail:" + response.details, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ClassroomActivity.this, "claim streaming fail", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
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
            }).setPanelItemClick(new OnPanelItemClick() {
                @Override
                public void onItemClick(int action, String accountId) {
                    mTalkPanel.close(mDrawerLayout, mRightDrawer, false);
                    applyOpenStuVideo(accountId);
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
        if (mAnimating || mClassroomController == null) {
            return;
        }

        //第一版本暂时去掉该功能
        //if (mUser == Constants.User.STUDENT && mClassroomController.isLiveWhiteboard()) {
        //    Toast.makeText(this, R.string.wb_toolbar_unable_tips, Toast.LENGTH_SHORT).show();
        //    return;
        //}

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

    /**
     * 申请打开学生视频
     */
    private void applyOpenStuVideo(String accountId) {
        Toast.makeText(ClassroomActivity.this, "apply open student video", Toast.LENGTH_SHORT).show();
        if (mUser == Constants.User.TEACHER) {
            mSocket.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.OPEN_MEDIA));
        }
    }

    private void enterVideoEditing() {
        if (mClassroomController != null && mPageState == PAGE_TOP) {
            mClassroomController.takeVideoFrame(mFrameCaptureCallback);
        }
    }

    private FrameCapturedCallback mFrameCaptureCallback = new FrameCapturedCallback() {
        @Override
        public void onFrameCaptured(Bitmap bitmap) {
            mCaptureFrame = bitmap;
            runOnUiThread(mCaptureFrameRunnable);
        }
    };

    private Runnable mCaptureFrameRunnable = new Runnable() {
        @Override
        public void run() {
            if (mCaptureFrame != null) {
                mPageState = PAGE_EDIT_VIDEO;
                mLeftPanel.setVisibility(View.VISIBLE);
                mTakePicBtn.setVisibility(View.GONE);
                mVideoDoodlePanel.setVisibility(View.VISIBLE);
                mPublishVideoPanel.setVisibility(View.GONE);
                mEnterTalkBtn.setVisibility(View.GONE);
                mClassroomController.enterVideoEditing(mCaptureFrame);
                hideTopBottomPanel();
            } else {
                mPageState = PAGE_TOP;
            }
        }
    };

    private void exitVideoEditing() {
        if (mClassroomController != null) {
            mPageState = PAGE_TOP;
            mCurrentControllerLevel = InteractiveLevel.MAIN_PANEL;
            mLeftPanel.setVisibility(View.GONE);
            mTakePicBtn.setVisibility(View.VISIBLE);
            mVideoDoodlePanel.setVisibility(View.GONE);
            mPublishVideoPanel.setVisibility(View.VISIBLE);
            mEnterTalkBtn.setVisibility(View.VISIBLE);
            mClassroomController.exitVideoEditing();
            hideWhiteBoardPanel();
        }
    }

    private void switchCamera() {
        if (mClassroomController != null) {
            mClassroomController.switchCamera();
        }
    }

    private void selectShareContact(View view) {
        if (mClassroomController != null) {
            mClassroomController.selectShareContact(view);
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
                } else if (mPageState != PAGE_EDIT_VIDEO) {
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
                } else if (mPageState != PAGE_EDIT_VIDEO){
                    showTopBottomPanel();
                }
            }
            return super.onSingleTapConfirmed(e);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mClassroomController != null) {
            mClassroomController.onPauseVideo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        WhiteboardManager.getInstance().release();
        if (mBinder != null) {
            mBinder.unbind();
        }

        if (mClassroomController != null) {
            mClassroomController.onDestroyVideo();
        }

        if (mClassroomController != null) {
            mClassroomController.release();
        }

        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
            mProgress = null;
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }

        cancelAllAnim();
        disConnectIO();
        unregisterNetworkReceiver();
    }

    public int getInteractiveLevel() {
        return mCurrentControllerLevel;
    }

    public String getTicket() {
        return mTicket;
    }

    private void saveWhiteboard() {
        if (mClassroomController == null) {
            return;
        }

        mClassroomController.saveWhiteboard();
    }

    private void setWhiteboardMainScreen() {
        if (mClassroomController == null) {
            return;
        }

        mClassroomController.setWhiteboardMainScreen();
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

        if (mClassroomController != null) {
            mClassroomController.exitWhiteboard();
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
        if (mClassroomController != null) {
            mClassroomController.handlePanelItemClick(v);
        }
    }

    public boolean isSyncWhiteboard() {
        if (mClassroomController == null) {
            return false;
        }

        return mClassroomController.isSyncWhiteboard();
    }

    public void updateWhiteboardCollCountStyle(int wbCollSize) {
        if (wbCollSize > 1) {
            mWhiteboardCollCountTv.setVisibility(View.VISIBLE);
            mWhiteboardCollCountTv.setText(String.valueOf(wbCollSize));
        }
    }

    public void updateWhiteboardCollCountStyle() {
        if (mClassroomController == null) {
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
        LiveManager.finishClass(this, mTicket, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                cancelProgress();
                //ClassroomActivity.this.finish();
                mLiveSessionState = Live.LiveSessionState.FINISHED;
                setControllerBtnStyle(mLiveSessionState);
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
                        mHandler.sendEmptyMessage(MSG_PLAY_BTN_SETTING);
                    } else if ((Live.LiveSessionState.PENDING_FOR_JOIN.equals(mSyncState.from) && Live.LiveSessionState.LIVE.equals(mSyncState.to))
                            || (Live.LiveSessionState.RESET.equals(mSyncState.from) && Live.LiveSessionState.LIVE.equals(mSyncState.to))) {
                        if (mUser != Constants.User.TEACHER) {
                            mLiveSessionState = Live.LiveSessionState.LIVE;
                            mHandler.sendEmptyMessage(MSG_PLAY_VIDEO);
                        }

                        setResetTime(false);
                        setPlayTime(mSyncState.timeline != null ? mSyncState.timeline.hasTaken : 0, true);
                    } else if (Live.LiveSessionState.LIVE.equals(mSyncState.from) && Live.LiveSessionState.RESET.equals(mSyncState.to)) {
                        if (mUser != Constants.User.TEACHER) {
                            mLiveSessionState = Live.LiveSessionState.RESET;
                        }

                        setResetTime(true);
                        setPlayTime(mSyncState.timeline != null ? mSyncState.timeline.hasTaken : 0, false);
                    } else if (Live.LiveSessionState.DELAY.equals(mSyncState.from) && Live.LiveSessionState.FINISHED.equals(mSyncState.to)) {
                        mLiveSessionState = Live.LiveSessionState.FINISHED;
                        mHandler.sendEmptyMessage(MSG_DELAY_TO_FINISH);
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
                    mPlayUrl = startedNotify.RTMPPlayUrl;
                    mClassroomController.playTeaVideo(mPlayUrl);
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
                            String t = TimeUtil.formatSecondTime(mPlayTotalTime);
                            mPlayTimeTv.setText(t);
                            mPublishTimeTv.setText(t);
                            mPublishTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_start, 0, 0, 0);
                            m.what = MSG_PLAY_TIME;
                            m.arg1 = 1;
                            mHandler.sendMessageDelayed(m, 1000);
                            if (mLessonDuration > 0) {
                                int progress = Math.round(100 * (mPlayTotalTime / (float) (mLessonDuration * 60)));
                                mLiveProgress.setProgress(progress);
                            }
                        } else if (msg.arg1 == 2) {
                            mPlayTotalTime--;
                            mPlayTimeTv.setText(TimeUtil.formatSecondTime(mPlayTotalTime));
                            m.what = MSG_PLAY_TIME;
                            m.arg1 = 2;
                            mHandler.sendMessageDelayed(m, 1000);
                        } else {
                            String t = TimeUtil.formatSecondTime(mPlayTotalTime);
                            mPlayTimeTv.setText(t);
                            mPublishTimeTv.setText(t);
                            mPublishTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);
                        }
                        break;
                    case MSG_PLAY_BTN_SETTING:
                        setControllerBtnStyle(mLiveSessionState);
                        break;
                    case MSG_PLAY_VIDEO:
                        if (mClassroomController != null) {
                            mClassroomController.playTeaVideo(mPlayUrl);
                        }
                        break;
                    case MSG_DELAY_TO_FINISH:
                        if (mUser == Constants.User.STUDENT) {
                            //学生对课程的评价
                        }
                        setControllerBtnStyle(mLiveSessionState);
                        break;
                }
            }
        }
    };

    public class NetworkChangedBReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();

            boolean mobileNet = mobileInfo == null ? false : mobileInfo.isConnected();
            boolean wifiNet = wifiInfo == null ? false : wifiInfo.isConnected();
            String activeNet = activeInfo == null ? "null" : activeInfo.getTypeName();

            if (activeInfo == null) {
                // have no active network

            } else if (wifiNet) {
                // wifi network

            } else if (mobileNet) {
                // mobile network

            }
        }
    }
}
