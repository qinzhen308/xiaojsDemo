package cn.xiaojs.xma.ui.classroom;

import android.Manifest;
import android.animation.Animator;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.FrameCapturedCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.Pagination;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom.bean.OpenMedia;
import cn.xiaojs.xma.ui.classroom.bean.StreamingMode;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.SyncStateResponse;
import cn.xiaojs.xma.ui.classroom.drawer.DrawerLayout;
import cn.xiaojs.xma.ui.classroom.live.OnStreamStateChangeListener;
import cn.xiaojs.xma.ui.classroom.live.StreamType;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.OnImageClickListener;
import cn.xiaojs.xma.ui.classroom.talk.OnTalkMsgListener;
import cn.xiaojs.xma.ui.classroom.whiteboard.Whiteboard;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardAdapter;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardCollection;
import cn.xiaojs.xma.ui.classroom.whiteboard.WhiteboardManager;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import cn.xiaojs.xma.util.BitmapUtils;
import cn.xiaojs.xma.util.DeviceUtil;
import cn.xiaojs.xma.util.MaterialUtil;
import cn.xiaojs.xma.util.TimeUtil;
import cn.xiaojs.xma.util.XjsUtils;
import io.socket.client.Socket;
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

public class ClassroomActivity extends FragmentActivity implements OnStreamStateChangeListener {
    private final static float LIVE_PROGRESS_WIDTH_FACTOR = 0.55F;
    private final static int REQUEST_PERMISSION = 1000;

    private final static int MSG_COUNT_TIME = 1 << 1;
    private final static int MSG_COUNT_DOWN_TIME = 1 << 2;
    private final static int MSG_LIVE_SHOW_COUNT_DOWN_TIME = 1 << 3;
    private final static int MSG_SOCKET_TIME_OUT = 1 << 4;

    //socket time out
    private final static int SOCKET_TIME_OUT = 1500; //1.5s

    private final static int TYPE_LIVE_SCHEDULED = 0;
    private final static int TYPE_LIVE_PENDING = 1;
    private final static int TYPE_LIVE_PLAYING = 2;
    private final static int TYPE_LIVE_RESET = 3;
    private final static int TYPE_LIVE_DELAY = 4;
    private final static int TYPE_LIVE_FINISH = 5;

    private final static int ANIM_SHOW = 1;
    private final static int ANIM_HIDE = 2;

    private final static int PHOTO_DOODLE = 1024;
    private final static int DOCUMENT_PAGE = 2048;
    private final static int VIDEO_PLAY = 4096;
    private final static int PAGE_TOP = 0;

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
    @BindView(R.id.publish_video_panel)
    View mPublishVideoPanel;

    //lesson info
    @BindView(R.id.lesson_title)
    TextView mLessonTitle;
    @BindView(R.id.reset_time)
    TextView mResetTimeTv;
    @BindView(R.id.count_time)
    TextView mCountTimeTv;
    @BindView(R.id.total_time)
    TextView mTotalTimeTv;
    @BindView(R.id.delay_time)
    TextView mDelayTimeTv;
    @BindView(R.id.count_down_time)
    TextView mCountDownTimeTv;
    @BindView(R.id.live_show)
    TextView mLiveShowTv;

    //panel btn
    @BindView(R.id.play_pause_btn)
    ImageView mPlayPauseBtn;
    @BindView(R.id.course_ware_btn)
    ImageView mCourseWareBtn;
    @BindView(R.id.blackboard_switcher_btn)
    ImageView mBoardSwitcherBtn;
    @BindView(R.id.publish_take_pic)
    ImageView mTakePicBtn;
    @BindView(R.id.publish_camera_switcher)
    ImageView mCameraSwitcher;
    @BindView(R.id.finish_btn)
    ImageView mFinishClassBtn;

    @BindView(R.id.tip_view)
    View mTipView;
    @BindView(R.id.tip_title_view)
    TextView mTipTitleTv;
    @BindView(R.id.tip_txt_view)
    TextView mTipDescTv;

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
    private ProgressHUD mProgress;

    private ViewGroup mOpenedDrawer;
    private Panel mOpenedPanel;

    private int mCurrentControllerLevel = InteractiveLevel.MAIN_PANEL;
    private boolean mAnimating = false;
    private PanelAnimListener mPanelAnimListener;
    private String mLiveSessionState = Live.LiveSessionState.PENDING_FOR_JOIN;
    private String mBeforeClamSteamState;

    private ClassroomController mClassroomController;
    private Bundle mExtraData;

    private CommonDialog mExitDialog;
    private CommonDialog mFinishDialog;
    private CommonDialog mMobileNetworkDialog;
    private CommonDialog mKickOutDialog;
    private CommonDialog mContinueConnectClassroomDialog;

    private Socket mSocket;
    private Boolean mSktConnected = false;

    private String mTicket = "";
    private String mLessonID;
    private SyncStateResponse mSyncState;
    private Constants.User mUser = Constants.User.STUDENT;
    private int mAppType = Platform.AppType.UNKNOWN;

    //socket retry count
    private int mSocketRetryCount = 0;

    private long mLessonDuration;
    private long mCountTime = 0;
    private long mCountDownTime = 0;
    private long mLiveShowCountDownTime = 0;
    private long mDelayTime = 0;
    private long mIndividualStreamDuration = 10 * 60; //s 10minute

    private int mPageState = PAGE_TOP;
    private Bitmap mCaptureFrame;
    private String mPlayUrl;
    private String mPublishUrl;
    private NetworkChangedBReceiver mNetworkChangedBReceiver;
    private int mNetworkState = ClassroomBusiness.NETWORK_NONE;
    private List<TalkItem> mReceivedTalkMsg;
    private Map<String, Boolean> mPeerPlaySteamMap;
    private String mTeaPeerPlayAccountId;
    private boolean mNeedInitStream; //init stream after socket connected

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        //init params
        initParams();

        initDrawer();
        initLiveProgress();
        initGestureDetector();

        //init data
        initData(true);

        //grant permission
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};
        PermissionGen.needPermission(this, REQUEST_PERMISSION, permissions);

        //register network
        registerNetworkReceiver();
    }

    @OnClick({R.id.back_btn, R.id.blackboard_switcher_btn, R.id.course_ware_btn, R.id.setting_btn,
            R.id.play_pause_btn, R.id.notify_msg_btn, R.id.contact_btn, R.id.qa_btn, R.id.enter_talk_btn,
            R.id.open_docs_btn, R.id.finish_btn, R.id.publish_camera_switcher, R.id.publish_take_pic,
            R.id.title_bar, R.id.live_progress_layout})
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
                playOrPauseLesson();
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
            case R.id.open_docs_btn:
                enterDocument();
                break;
            case R.id.publish_camera_switcher:
                switchCamera();
                break;
            case R.id.publish_take_pic:
                enterPhotoDoodle();
                break;
            case R.id.title_bar:
            case R.id.live_progress_layout:
                //do nothing，set onClick listener to prevent event penetration.
                break;
            default:
                break;
        }
    }

    private void initParams() {
        mBinder = ButterKnife.bind(this);
        mUser = Constants.User.STUDENT;

        mTicket = getIntent().getStringExtra(Constants.KEY_TICKET);
        mPanelAnimListener = new PanelAnimListener();
        mPeerPlaySteamMap = new ConcurrentHashMap<String, Boolean>();
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
     * 重写OnTouchEvent来控制事件分发
     */
    private void initGestureDetector() {
        mMainPanelGestureDetector = new GestureDetector(this, new MainPanelGestureListener());
        mMainPanel.setMainPanelGestureDetector(mMainPanelGestureDetector);
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
        mClassroomController = new ClassroomController(ClassroomActivity.this, mContentRoot, mUser, mAppType, this);
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
     * 初始化, 启动bootSession
     */
    private void initData(boolean showProgress) {
        if (ClassroomBusiness.getCurrentNetwork(this) == ClassroomBusiness.NETWORK_NONE) {
            mTipView.setVisibility(View.VISIBLE);
            return;
        }

        if (showProgress) {
            showProgress(true);
        }
        LiveManager.bootSession(this, mTicket, new APIServiceCallback<CtlSession>() {
            @Override
            public void onSuccess(CtlSession ctlSession) {
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(ClassroomActivity.this, "BootSession 成功", Toast.LENGTH_SHORT).show();
                }

                if (ctlSession != null) {
                    if (ctlSession.accessible) {
                        onBootSessionSucc(false, ctlSession);
                    } else {
                        cancelProgress();
                        checkForceKickOut(ctlSession);
                    }
                } else {
                    cancelProgress();
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(ClassroomActivity.this, "BootSession 失败：" + errorMessage, Toast.LENGTH_SHORT).show();
                }
                cancelProgress();
                //setTips(0, R.string.cls_not_connected);
                showContinueConnectClassroom();
            }
        });
    }

    /**
     * 检测是否需要强制踢出
     */
    private void checkForceKickOut(final CtlSession ctlSession) {
        if (mKickOutDialog == null) {
            mKickOutDialog = new CommonDialog(this);
            int width = DeviceUtil.getScreenWidth(this) / 2;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mKickOutDialog.setDesc(R.string.mobile_kick_out_desc);
            mKickOutDialog.setLefBtnText(R.string.cancel);
            mKickOutDialog.setRightBtnText(R.string.ok);
            mKickOutDialog.setDialogLayout(width, height);
            mKickOutDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    //强制登录
                    mKickOutDialog.dismiss();
                    onBootSessionSucc(true, ctlSession);
                }
            });

            mKickOutDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mKickOutDialog.dismiss();
                    Toast.makeText(ClassroomActivity.this, R.string.mobile_kick_out_cancel, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

            mKickOutDialog.show();
        }
    }

    /**
     * boot session 成功回调后的逻辑处理
     *
     * @param forceConnect 是否强制链接
     * @param ctlSession   课程session
     */
    private void onBootSessionSucc(boolean forceConnect, CtlSession ctlSession) {

        mLiveSessionState = ctlSession.state;
        if (Live.LiveSessionState.CANCELLED.equals(mLiveSessionState)) {
            Toast.makeText(this, R.string.forbidden_enter_class_for_cancel,Toast.LENGTH_SHORT).show();
            //finish();
        }

        mLessonID = ctlSession.ctl != null ? ctlSession.ctl.id : "";

        mAppType = ctlSession.connected != null ? ctlSession.connected.app : Platform.AppType.UNKNOWN;
        //二维码扫描进入教室，需要更新ticket.
        if (!TextUtils.isEmpty(ctlSession.ticket)) {
            mTicket = ctlSession.ticket;
        }

        mUser = ClassroomBusiness.getUser(ctlSession.psType);
        if (mUser == Constants.User.TEACHER
                || mUser == Constants.User.ASSISTANT
                || mUser == Constants.User.REMOTE_ASSISTANT) {
            if (Constants.PARTICIPANT_MODE == ctlSession.mode || Constants.PREVIEW_MODE == ctlSession.mode) {
                mUser = Constants.User.STUDENT;
                //屏蔽聊天和视频截图按钮
                mTakePicBtn.setVisibility(View.GONE);
                mEnterTalkBtn.setVisibility(View.GONE);
            }
        }

        //set title
        mLessonTitle.setText(!TextUtils.isEmpty(ctlSession.titleOfPrimary) ? ctlSession.titleOfPrimary : ctlSession.ctl.title);

        //init socket
        initSocketIO(mTicket, ctlSession.secret, forceConnect);

        //init ui
        initPanel();
        mLessonDuration = ctlSession.ctl != null ? ctlSession.ctl.duration : 0;
        mTotalTimeTv.setText(TimeUtil.formatMinuteTime(mLessonDuration));

        //init whiteboard
        initWhiteboardController();

        //set controller button style
        setControllerBtnStyle(ctlSession.state);
        //set tips
        setTipsByState(ctlSession.state);

        long countTime = 0;
        long countDownTime = 0;
        boolean autoCountTime = false;

        if (Live.LiveSessionState.LIVE.equals(ctlSession.state)) {
            if (mUser == Constants.User.TEACHER) {
                mPublishUrl = ctlSession.publishUrl;
                mNeedInitStream = true;
            } else if (mUser == Constants.User.STUDENT) {
                mPlayUrl = ctlSession.playUrl;
                mNeedInitStream = true;
            }

            autoCountTime = true;
            countTime = ctlSession.ctl.duration * 60 - ctlSession.finishOn;
            countDownTime = ctlSession.finishOn;
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(ctlSession.state) ||
                Live.LiveSessionState.SCHEDULED.equals(ctlSession.state) ||
                Live.LiveSessionState.FINISHED.equals(ctlSession.state)) {
            mPlayUrl = ctlSession.playUrl;
            mNeedInitStream = true;

            countTime = 0;
            countDownTime = ctlSession.startOn;
            if (Live.LiveSessionState.SCHEDULED.equals(ctlSession.state)
                    || Live.LiveSessionState.FINISHED.equals(ctlSession.state)) {
                mIndividualStreamDuration = ctlSession.finishOn;
            }
        } else if (Live.LiveSessionState.RESET.equals(ctlSession.state)) {
            int progress = Math.round(100 * (countTime / (float) (mLessonDuration * 60)));
            mLiveProgress.setProgress(progress);

            autoCountTime = false;
            countTime = ctlSession.hasTaken;
            countDownTime = mLessonDuration * 60 - ctlSession.hasTaken;
        } else if (Live.LiveSessionState.CANCELLED.equals(ctlSession.state)) {
            autoCountTime = false;
            countTime = 0;
            countDownTime = 0;
        }

        //set time display
        int type = getTypeByState(mLiveSessionState);
        setCountTime(countTime, autoCountTime);
        setCountDownTimes(countDownTime, type);

        //init talk
        initTalk();
    }

    private int getTypeByState(String state) {
        if (Live.LiveSessionState.SCHEDULED.equals(state)) {
            return TYPE_LIVE_PENDING;
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(state)) {
            return TYPE_LIVE_PENDING;
        } else if (Live.LiveSessionState.LIVE.equals(state)) {
            return TYPE_LIVE_PLAYING;
        } else if (Live.LiveSessionState.RESET.equals(state)) {
            return TYPE_LIVE_RESET;
        } else if (Live.LiveSessionState.FINISHED.equals(state)) {
            return TYPE_LIVE_FINISH;
        }

        return TYPE_LIVE_PENDING;
    }

    private void setControllerBtnStyle(String liveSessionState) {
        if (Live.LiveSessionState.SCHEDULED.equals(liveSessionState) ||
                Live.LiveSessionState.FINISHED.equals(liveSessionState)) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
            mPlayPauseBtn.setVisibility(View.VISIBLE);
            mFinishClassBtn.setVisibility(View.GONE);
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveSessionState) ||
                Live.LiveSessionState.RESET.equals(liveSessionState)) {
            if (mUser == Constants.User.TEACHER) {
                mFinishClassBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_start);
                mPlayPauseBtn.setVisibility(View.VISIBLE);
            } else {
                mPlayPauseBtn.setVisibility(View.GONE);
            }
        } else if (Live.LiveSessionState.LIVE.equals(liveSessionState)) {
            if (mUser == Constants.User.TEACHER) {
                mFinishClassBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
            } else {
                mPlayPauseBtn.setVisibility(View.GONE);
            }
        } else if (Live.LiveSessionState.INDIVIDUAL.equals(liveSessionState)) {
            mPlayPauseBtn.setVisibility(View.VISIBLE);
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
        }
    }

    private void setTipsByState(String liveSessionState) {
        if (Live.LiveSessionState.SCHEDULED.equals(liveSessionState)) {
            setTips(R.string.cls_not_on_class_title, R.string.cls_not_on_class_desc);
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveSessionState)) {
            setTips(R.string.cls_pending_class_title, R.string.cls_pending_class_desc);
        } else if (Live.LiveSessionState.RESET.equals(liveSessionState)) {
            if (mUser == Constants.User.TEACHER
                    || mUser == Constants.User.ASSISTANT
                    || mUser == Constants.User.REMOTE_ASSISTANT) {
                setTips(R.string.cls_break_title, R.string.cls_break_desc_teacher);
            } else {
                setTips(R.string.cls_break_title, R.string.cls_break_desc);
            }
        } else if (Live.LiveSessionState.LIVE.equals(liveSessionState)) {
            hideTips();
        } else if (Live.LiveSessionState.FINISHED.equals(liveSessionState)) {
            setTips(R.string.cls_finish_title, R.string.cls_not_on_class_desc);
        }
    }

    /**
     * 开启或暂停课，除老师外的其他参与者的状态变化在socket的回调里面进行处理
     *
     * @see #mSyncStateListener
     */
    private void playOrPauseLesson() {
        if (TextUtils.isEmpty(mTicket) || !mSktConnected) {
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
                    mClassroomController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                }
            });
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(mLiveSessionState)) {
            LiveManager.beginClass(this, mTicket, Live.StreamMode.MUTE, new APIServiceCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody object) {
                    //cancelProgress();
                    ClassResponse response = ApiManager.getClassResponse(object);

                    mLiveSessionState = Live.LiveSessionState.LIVE;
                    setControllerBtnStyle(Live.LiveSessionState.LIVE);
                    mClassroomController.publishStream(StreamType.TYPE_STREAM_PUBLISH, response != null ? response.publishUrl : null);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                }
            });
        } else if (Live.LiveSessionState.RESET.equals(mLiveSessionState)) {
            LiveManager.resumeClass(this, mTicket, Live.StreamMode.MUTE, new APIServiceCallback<ClassResponse>() {
                @Override
                public void onSuccess(ClassResponse object) {
                    //cancelProgress();
                    mLiveSessionState = Live.LiveSessionState.LIVE;
                    setControllerBtnStyle(Live.LiveSessionState.LIVE);
                    mClassroomController.publishStream(StreamType.TYPE_STREAM_PUBLISH, object != null ? object.publishUrl : null);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                }
            });
        } else if (Live.LiveSessionState.SCHEDULED.equals(mLiveSessionState) ||
                Live.LiveSessionState.FINISHED.equals(mLiveSessionState)) {
            //个人推流
            individualPublishStream();
        } else if (Live.LiveSessionState.INDIVIDUAL.equals(mLiveSessionState)) {
            cancelProgress();
            mLiveSessionState = mBeforeClamSteamState;
            setControllerBtnStyle(mLiveSessionState);
            if (mClassroomController != null) {
                mClassroomController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL);
            }
        } else {
            cancelProgress();
        }
    }

    /**
     * 个人推流
     */
    private void individualPublishStream() {
        StreamingMode streamMode = new StreamingMode();
        streamMode.mode = Live.StreamMode.AV;
        SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.CLAIM_STREAMING), streamMode, new SocketManager.AckListener() {
            @Override
            public void call(final Object... args) {
                //cancelProgress();
                if (args != null && args.length > 0) {
                    StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                    if (response.result) {
                        mBeforeClamSteamState = mLiveSessionState;
                        mLiveSessionState = Live.LiveSessionState.INDIVIDUAL;
                        mIndividualStreamDuration = response.finishOn;
                        if (mClassroomController != null) {
                            mClassroomController.publishStream(StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL, response.publishUrl);
                        }
                        setControllerBtnStyle(mLiveSessionState);
                        if (XiaojsConfig.DEBUG) {
                            Toast.makeText(ClassroomActivity.this, "claim streaming succ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        cancelProgress();
                        if (XiaojsConfig.DEBUG) {
                            Toast.makeText(ClassroomActivity.this, "claim streaming fail:" + response.details, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    cancelProgress();
                    if (XiaojsConfig.DEBUG) {
                        Toast.makeText(ClassroomActivity.this, "claim streaming fail", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
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
            mSettingPanel.setOnSettingChangedListener(new OnSettingChangedListener() {
                @Override
                public void onResolutionChanged(int quality) {
                    //切换推流视频分辨率
                    if (mClassroomController != null) {
                        mClassroomController.togglePublishStreamResolution();
                    }
                }

                @Override
                public void onSwitcherChanged(int switcher, boolean open) {
                    if (mClassroomController != null) {
                        switch (switcher) {
                            case Constants.SWITCHER_AUDIO:
                                mClassroomController.muteOrUnmute();
                                break;
                            case Constants.SWITCHER_CAMERA:
                                mClassroomController.openOrCloseCamera();
                                break;
                        }
                    }
                }
            });
        }
        mSettingPanel.show(mDrawerLayout, mRightDrawer);
        mOpenedPanel = mSettingPanel;
    }

    /**
     * 打开聊天
     */
    private void openTalk(int mode) {
        initPanel();

        mTalkPanel.with(mode).show(mDrawerLayout, mRightDrawer);
        mOpenedPanel = mTalkPanel;
        mReceivedTalkMsg.clear();
        mEnterTalkBtn.setCount(0);
    }

    /**
     * 初始化talk，监听消息
     */
    private void initTalk() {
        if (mTalkPanel == null) {
            mTalkPanel = new TalkPanel(this, mTicket, mUser);
            mReceivedTalkMsg = new ArrayList<TalkItem>();
            mTalkPanel.setPanelCallback(mPanelCallback)
                    .setPanelItemClick(mOnPanelItemClick)
                    .setTalkMsgListener(mOnTalkMsgListener)
                    .setOnImageClickListener(mOnImageClickListener);

            //only init, without show
            mTalkPanel.initWithoutShow(mDrawerLayout, mRightDrawer);
        }
    }

    /**
     * panel 回调
     */
    private PanelCallback mPanelCallback = new PanelCallback() {
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
    };

    /**
     * panel item回调
     */
    private OnPanelItemClick mOnPanelItemClick = new OnPanelItemClick() {
        @Override
        public void onItemClick(int action, String accountId) {
            mTalkPanel.close(mDrawerLayout, mRightDrawer, false);
            applyOpenStuVideo(accountId);
        }
    };

    /**
     * 收到消息通知, 更新页面
     */
    private OnTalkMsgListener mOnTalkMsgListener = new OnTalkMsgListener() {
        @Override
        public void onTalkMsgReceived(TalkItem talkItem) {
            if (mOpenedPanel == null) {
                if (!mReceivedTalkMsg.contains(talkItem)) {
                    mReceivedTalkMsg.add(talkItem);

                    //update talk msg
                    mEnterTalkBtn.setType(MessageImageView.TYPE_NUM);
                    int offsetY = getResources().getDimensionPixelOffset(R.dimen.px8);
                    mEnterTalkBtn.setExtraOffsetY(offsetY);
                    mEnterTalkBtn.setCount(mReceivedTalkMsg.size());
                }
            }
        }
    };

    /**
     * 消息点击图片回调
     */
    private OnImageClickListener mOnImageClickListener = new OnImageClickListener() {
        @Override
        public void onImageClick(final int type, final String key) {
            //聊天点击大图回调到视频图片编辑页面
            if (!TextUtils.isEmpty(key)) {
                mTalkPanel.close(mDrawerLayout, mRightDrawer, false);
                new AsyncTask<String, Integer, Bitmap>() {
                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Bitmap doInBackground(String... params) {
                        if (params == null || params.length == 0) {
                            return null;
                        }

                        String content = params[0];
                        if (TextUtils.isEmpty(content)) {
                            return null;
                        }

                        if (type == OnImageClickListener.IMG_FROM_BASE64) {
                            return ClassroomBusiness.base64ToBitmap(content);
                        } else if (type == OnImageClickListener.IMG_FROM_QINIU) {
                            try {
                                return Glide.with(ClassroomActivity.this)
                                        .load(ClassroomBusiness.getMediaUrl(key))
                                        .asBitmap()
                                        .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                        .get();
                            } catch (Exception e) {
                                Logger.i(e != null ? e.getLocalizedMessage() : "null");
                            }
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Bitmap bmp) {
                        //enter video edit fragment
                        enterPhotoDoodle(bmp);
                    }
                }.execute(key);
            }
        }
    };

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
     * 申请打开学生视频
     */
    private void applyOpenStuVideo(String accountId) {
        if (XiaojsConfig.DEBUG) {
            Toast.makeText(ClassroomActivity.this, "apply open student video: " + accountId, Toast.LENGTH_SHORT).show();
        }

        mTeaPeerPlayAccountId = accountId;
        if (mUser == Constants.User.TEACHER) {
            if (!mPeerPlaySteamMap.containsKey(accountId) || !mPeerPlaySteamMap.get(accountId)) {
                OpenMedia openMedia = new OpenMedia();
                openMedia.to = accountId;
                SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.OPEN_MEDIA), openMedia, new SocketManager.AckListener() {
                    @Override
                    public void call(final Object... args) {
                        if (args != null && args.length > 0) {
                            StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                            if (response != null && response.result) {
                                if (XiaojsConfig.DEBUG) {
                                    Toast.makeText(ClassroomActivity.this, "申请打开学生视频开始", Toast.LENGTH_LONG).show();
                                }
                            } else {

                            }
                        }
                    }
                });
            } else {
                OpenMedia openMedia = new OpenMedia();
                openMedia.to = accountId;
                SocketManager.emit(Event.getEventSignature(Su.EventCategory.CLASSROOM, Su.EventType.CLOSE_MEDIA), openMedia, new SocketManager.AckListener() {
                    @Override
                    public void call(final Object... args) {
                        if (args != null && args.length > 0) {
                            StreamingResponse response = ClassroomBusiness.parseSocketBean(args[0], StreamingResponse.class);
                            if (response != null && response.result) {
                                mPeerPlaySteamMap.put(mTeaPeerPlayAccountId, false);
                                if (XiaojsConfig.DEBUG) {
                                    Toast.makeText(ClassroomActivity.this, "关闭申请打开学生视频开始", Toast.LENGTH_LONG).show();
                                }
                            } else {

                            }
                        }
                    }
                });
            }
        }
    }

    private void enterPhotoDoodle() {
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
            enterPhotoDoodle(mCaptureFrame);
        }
    };

    private void enterPhotoDoodle(Bitmap bmp) {
        mPageState = PHOTO_DOODLE;
        mClassroomController.enterPhotoDoodle(bmp, mOnPhotoDoodleShareListener);
        mEnterTalkBtn.setVisibility(View.GONE);
    }

    private void enterPhotoDoodle(String url) {
        mPageState = PHOTO_DOODLE;
        mClassroomController.enterPhotoDoodle(url, mOnPhotoDoodleShareListener);
        mEnterTalkBtn.setVisibility(View.GONE);
    }

    private void enterPhotoDoodle(ArrayList<String> imgUrls) {
        mPageState = PHOTO_DOODLE;
        mClassroomController.enterPhotoDoodle(imgUrls, mOnPhotoDoodleShareListener);
        mEnterTalkBtn.setVisibility(View.GONE);
    }

    private OnPhotoDoodleShareListener mOnPhotoDoodleShareListener = new OnPhotoDoodleShareListener() {
        @Override
        public void onVideoShared(final Attendee attendee, final Bitmap bitmap) {
            //send msg
            if (mTalkPanel != null && bitmap != null) {
                new AsyncTask<Integer, Integer, String>() {

                    @Override
                    protected String doInBackground(Integer... params) {
                        //resize max length to 800
                        Bitmap resizeBmp = BitmapUtils.resizeDownBySideLength(bitmap, Constants.SHARE_IMG_SIZE, false);
                        return ClassroomBusiness.bitmapToBase64(resizeBmp);
                    }

                    @Override
                    protected void onPostExecute(String result) {
                        if (!TextUtils.isEmpty(result)) {
                            exitPhotoDoodle();
                            mTalkPanel.sendImg(attendee, result);
                        } else {
                            cancelProgress();
                        }
                    }
                }.execute(0);
            }
        }
    };

    public void exitPhotoDoodle() {
        if (mClassroomController != null) {
            mPageState = PAGE_TOP;
            mCurrentControllerLevel = InteractiveLevel.MAIN_PANEL;
            mClassroomController.exitPhotoDoodle();
            mEnterTalkBtn.setVisibility(View.VISIBLE);
        }
    }

    public void enterDocument() {
        if (mClassroomController != null) {
            mPageState = DOCUMENT_PAGE;
            mEnterTalkBtn.setVisibility(View.GONE);
            mClassroomController.enterDocumentFragment();
        }
    }

    public void exitDocument() {
        if (mClassroomController != null) {
            mPageState = PAGE_TOP;
            mEnterTalkBtn.setVisibility(View.VISIBLE);
            mClassroomController.exitDocumentFragment();
        }
    }

    public void exitDocumentWithAction(LibDoc doc) {
        if (mClassroomController != null) {
            mPageState = PAGE_TOP;
            mEnterTalkBtn.setVisibility(View.VISIBLE);
            mClassroomController.exitDocumentFragment();

            String mimeType = doc.mimeType != null ? doc.mimeType.toLowerCase() : "";
            String url = ClassroomBusiness.getMediaUrl(doc.key);

            if (mimeType.startsWith(Collaboration.PictureMimeTypes.ALL)) {
                enterPhotoDoodle(url);
            } else if (mimeType.startsWith(Collaboration.VideoMimeTypes.ALL)) {
                enterVideoPlayer(doc);
            } else if (mimeType.startsWith(Collaboration.OfficeMimeTypes.PPT)
                    || mimeType.startsWith(Collaboration.OfficeMimeTypes.PPTX)) {
                ArrayList<LibDoc.ExportImg> images = MaterialUtil.getSortImgs(doc.exported != null ? doc.exported.images : null);
                if (images != null) {
                    ArrayList<String> imgUrls = new ArrayList<String>();
                    for (LibDoc.ExportImg img : images) {
                        imgUrls.add(ClassroomBusiness.getMediaUrl(img.name));
                    }
                    enterPhotoDoodle(imgUrls);
                } else {
                    Toast.makeText(this, R.string.cr_ppt_open_fail, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, R.string.cr_not_support_doc, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void enterVideoPlayer(LibDoc doc) {
        if (mClassroomController != null) {
            mPageState = VIDEO_PLAY;
            mClassroomController.playVideo(doc);
        }
    }

    public void exitVideoPlayer() {
        if (mClassroomController != null) {
            mPageState = PAGE_TOP;
            mClassroomController.exitPlayVideo();
        }
    }

    private void switchCamera() {
        if (mClassroomController != null) {
            mClassroomController.switchCamera();
        }
    }

    private class MainPanelGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("aaa", "========MainPanelGestureListener====onSingleTapConfirmed=============" + mCurrentControllerLevel);
            if (mCurrentControllerLevel == InteractiveLevel.MAIN_PANEL && !mAnimating) {
                if (mTopPanel.getVisibility() == View.VISIBLE) {
                    hideTopPanel();
                } else {
                    showTopPanel();
                }
            }
            return super.onSingleTapConfirmed(e);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mClassroomController != null) {
            mClassroomController.onResumeVideo();
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

    public void setCurrentControllerLevel(int level) {
        mCurrentControllerLevel = level;
    }

    public int getInteractiveLevel() {
        return mCurrentControllerLevel;
    }

    public String getLiveState() {
        return mLiveSessionState;
    }

    public String getTicket() {
        return mTicket;
    }

    public String getLessonId() {
        return mLessonID;
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
     * 取消所有动画
     */
    private void cancelAllAnim() {
        if (mTopPanel != null) {
            mTopPanel.animate().cancel();
        }

        if (mBottomPanel != null) {
            mBottomPanel.animate().cancel();
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
                    mV.setVisibility(View.VISIBLE);
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
                    mV.setVisibility(View.VISIBLE);
                } else if ((mAnimType & ANIM_HIDE) != 0) {
                    mV.setVisibility(View.GONE);
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
            } else if (mPageState == PHOTO_DOODLE) {
                exitPhotoDoodle();
                return false;
            } else if (mPageState == DOCUMENT_PAGE) {
                exitDocument();
                return false;
            } else if (mPageState == VIDEO_PLAY) {
                exitVideoPlayer();
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
                mLiveSessionState = Live.LiveSessionState.FINISHED;
                setControllerBtnStyle(mLiveSessionState);
                setTipsByState(mLiveSessionState);
                if (mClassroomController != null) {
                    mClassroomController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
            }
        });
    }


    private void initSocketIO(String ticket, String secret, boolean force) {
        SocketManager.close();
        SocketManager.init(ClassroomActivity.this, ticket, secret, true, true, force);
        mSocket = SocketManager.getSocket();
        mHandler.removeMessages(MSG_SOCKET_TIME_OUT);
        mHandler.sendEmptyMessageDelayed(MSG_SOCKET_TIME_OUT, SOCKET_TIME_OUT);
        listenSocket();
    }

    private void listenSocket() {
        if (mSocket == null) {
            return;
        }

        SocketManager.on(Socket.EVENT_CONNECT, mOnConnect);
        SocketManager.on(Socket.EVENT_DISCONNECT, mOnDisconnect);
        SocketManager.on(Socket.EVENT_CONNECT_ERROR, mOnConnectError);
        SocketManager.on(Socket.EVENT_CONNECT_TIMEOUT, mOnConnectError);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.KICKOUT_DUE_TO_NEW_CONNECTION), mKickoutByUserListener);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_STATE), mSyncStateListener);
        SocketManager.on(Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.LEAVE), mOnLeave);
        SocketManager.connect();
    }

    private void disConnectIO() {
        //off all
        SocketManager.off();
        SocketManager.close();
    }

    private SocketManager.EventListener mOnConnect = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            if (!mSktConnected) {
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(ClassroomActivity.this, R.string.socket_connect, Toast.LENGTH_LONG).show();
                }
            }
            cancelProgress();
            mHandler.removeMessages(MSG_SOCKET_TIME_OUT);
            mSktConnected = true;


            if (mNeedInitStream) {
                mNeedInitStream = false;
                if (Live.LiveSessionState.LIVE.equals(mLiveSessionState)) {
                    if (mUser == Constants.User.TEACHER) {
                        mClassroomController.publishStream(StreamType.TYPE_STREAM_PUBLISH, mPublishUrl);
                    } else if (mUser == Constants.User.STUDENT) {
                        mClassroomController.playStream(StreamType.TYPE_STREAM_PLAY, mPlayUrl);
                    }
                } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(mLiveSessionState)
                        || Live.LiveSessionState.SCHEDULED.equals(mLiveSessionState)
                        || Live.LiveSessionState.FINISHED.equals(mLiveSessionState)) {
                    mClassroomController.playStream(StreamType.TYPE_STREAM_PLAY_INDIVIDUAL, mPlayUrl, mIndividualStreamDuration);
                }
            }
        }
    };

    private SocketManager.EventListener mOnDisconnect = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            mSktConnected = false;
            if (mTalkPanel != null) {
                mTalkPanel.needSocketReListener();
            }
            if (XiaojsConfig.DEBUG) {
                Toast.makeText(ClassroomActivity.this, R.string.socket_disconnect, Toast.LENGTH_LONG).show();
            }
        }
    };

    private SocketManager.EventListener mKickoutByUserListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            Toast.makeText(ClassroomActivity.this, R.string.mobile_kick_out_tips, Toast.LENGTH_LONG).show();
            finish();
        }
    };

    private SocketManager.EventListener mOnConnectError = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            mSktConnected = false;
            if (mTalkPanel != null) {
                mTalkPanel.needSocketReListener();
            }
            if (XiaojsConfig.DEBUG) {
                Toast.makeText(ClassroomActivity.this, R.string.socket_error_connect, Toast.LENGTH_LONG).show();
            }
        }
    };

    private SocketManager.EventListener mSyncStateListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            if (args != null && args.length > 0) {
                mSyncState = ClassroomBusiness.parseSocketBean(args[0], SyncStateResponse.class);

                long countTime = 0;
                boolean autoCountTime = false;
                long countDownTime = mCountDownTime;

                if (mSyncState != null) {
                    if (Live.LiveSessionState.SCHEDULED.equals(mSyncState.from) && Live.LiveSessionState.PENDING_FOR_JOIN.equals(mSyncState.to)) {
                        mLiveSessionState = Live.LiveSessionState.PENDING_FOR_JOIN;
                        setControllerBtnStyle(mLiveSessionState);
                    } else if ((Live.LiveSessionState.PENDING_FOR_JOIN.equals(mSyncState.from) && Live.LiveSessionState.LIVE.equals(mSyncState.to))
                            || (Live.LiveSessionState.RESET.equals(mSyncState.from) && Live.LiveSessionState.LIVE.equals(mSyncState.to))) {
                        if (mClassroomController != null && mUser == Constants.User.STUDENT) {
                            mClassroomController.playStream(StreamType.TYPE_STREAM_PLAY, mPlayUrl);
                        }

                        mLiveSessionState = Live.LiveSessionState.LIVE;
                        autoCountTime = true;
                        countTime = mSyncState.timeline != null ? mSyncState.timeline.hasTaken : 0;
                    } else if (Live.LiveSessionState.LIVE.equals(mSyncState.from) && Live.LiveSessionState.RESET.equals(mSyncState.to)) {
                        mLiveSessionState = Live.LiveSessionState.RESET;
                        autoCountTime = false;
                        countTime = mSyncState.timeline != null ? mSyncState.timeline.hasTaken : 0;
                    } else if (Live.LiveSessionState.LIVE.equals(mSyncState.from) && Live.LiveSessionState.DELAY.equals(mSyncState.to)) {
                        mLiveSessionState = Live.LiveSessionState.DELAY;
                        autoCountTime = false;
                        countTime = mLessonDuration * 60;
                    } else if (Live.LiveSessionState.DELAY.equals(mSyncState.from) && Live.LiveSessionState.FINISHED.equals(mSyncState.to)) {
                        mLiveSessionState = Live.LiveSessionState.FINISHED;
                        setControllerBtnStyle(mLiveSessionState);
                    }

                    int type = getTypeByState(mLiveSessionState);
                    setCountDownTimes(countDownTime, type);
                    setCountTime(countTime, autoCountTime);
                }
            }
        }
    };

    /**
     * 成员退出事件
     */
    private SocketManager.EventListener mOnLeave = new SocketManager.EventListener() {
        @Override
        public void call(final Object... args) {
            if (args == null || args.length == 0) {
                return;
            }

            Attendee attendee = ClassroomBusiness.parseSocketBean(args[0], Attendee.class);
            if (attendee != null) {
                mPeerPlaySteamMap.put(attendee.accountId, false);
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

    private void setCountDownTimes(long time, int type) {
        mHandler.removeMessages(MSG_COUNT_DOWN_TIME);
        mCountDownTime = time;
        Message msg = mHandler.obtainMessage(MSG_COUNT_DOWN_TIME);
        if (msg == null) {
            msg = new Message();
        }
        msg.what = MSG_COUNT_DOWN_TIME;
        msg.arg1 = type;
        mHandler.sendMessage(msg);
    }

    private void setLiveShowCountDownTime(long time) {
        mHandler.removeMessages(MSG_LIVE_SHOW_COUNT_DOWN_TIME);
        mLiveShowCountDownTime = time > 0 ? time : 0; //default 10 minute

        mLiveShowTv.setVisibility(View.VISIBLE);

        Message msg = mHandler.obtainMessage(MSG_LIVE_SHOW_COUNT_DOWN_TIME);
        if (msg == null) {
            msg = new Message();
        }
        msg.what = MSG_LIVE_SHOW_COUNT_DOWN_TIME;
        mHandler.sendMessage(msg);
    }

    private void hideLiveShowCountDownTime() {
        mHandler.removeMessages(MSG_LIVE_SHOW_COUNT_DOWN_TIME);
        mLiveShowTv.setVisibility(View.GONE);
        mCountDownTimeTv.setVisibility(View.VISIBLE);
    }

    private void setCountTime(long time, boolean play) {
        mHandler.removeMessages(MSG_COUNT_TIME);
        mCountTime = time;

        Message msg = new Message();
        msg.what = MSG_COUNT_TIME;
        msg.arg1 = play ? 1 : 0;
        mHandler.sendMessage(msg);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                Message m = mHandler.obtainMessage();
                String time;
                switch (msg.what) {
                    case MSG_COUNT_DOWN_TIME:
                        int type = msg.arg1;
                        mCountDownTimeTv.setVisibility(View.VISIBLE);
                        switch (type) {
                            case TYPE_LIVE_PENDING:
                            case TYPE_LIVE_SCHEDULED:
                                mCountDownTime--;
                                time = TimeUtil.formatSecondTime(mCountDownTime);
                                mCountDownTimeTv.setText(getString(R.string.cls_distance_class, time));
                                mCountDownTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);
                                break;
                            case TYPE_LIVE_PLAYING:
                                mCountDownTime--;
                                time = TimeUtil.formatSecondTime(mCountDownTime);
                                mCountDownTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_start, 0, 0, 0);
                                if (mCountDownTime > 0) {
                                    mCountDownTimeTv.setText(time);
                                } else {
                                    mCountDownTimeTv.setText(R.string.cls_live_arrived);
                                }

                                m.arg1 = type;
                                m.what = MSG_COUNT_DOWN_TIME;
                                mHandler.sendMessageDelayed(m, 1000);
                                break;
                            case TYPE_LIVE_RESET:
                                mCountDownTimeTv.setText(getString(R.string.cls_break_time_desc, TimeUtil.formatSecondTime(mCountDownTime)));
                                mCountDownTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);
                                break;
                            case TYPE_LIVE_DELAY:
                                mDelayTime++;
                                time = getString(R.string.cls_live_delay, TimeUtil.formatSecondTime(mDelayTime));
                                mCountDownTimeTv.setText(getString(R.string.cls_distance_class, time));
                                mCountDownTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);

                                m.arg1 = type;
                                m.what = MSG_COUNT_DOWN_TIME;
                                mHandler.sendMessageDelayed(m, 1000);
                                break;
                            case TYPE_LIVE_FINISH:
                                mCountDownTimeTv.setText(R.string.cls_live_finish);
                                mCountDownTimeTv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cr_publish_video_stop, 0, 0, 0);
                                break;
                        }
                        break;

                    case MSG_COUNT_TIME:
                        if (msg.arg1 == 1) {
                            mCountTime++;
                            if (mCountTime > mLessonDuration * 60) {
                                mCountTime = mLessonDuration * 60;
                            }
                            String t = TimeUtil.formatSecondTime(mCountTime);
                            mCountTimeTv.setText(t);

                            if (mLessonDuration > 0) {
                                int progress = Math.round(100 * (mCountTime / (float) (mLessonDuration * 60)));
                                mLiveProgress.setProgress(progress);
                            }

                            if (mCountDownTime < mLessonDuration * 60) {
                                m.arg1 = 1;
                                m.what = MSG_COUNT_TIME;
                                mHandler.sendMessageDelayed(m, 1000);
                            }
                        } else if (msg.arg1 == 0) {
                            String t = TimeUtil.formatSecondTime(mCountTime);
                            mCountTimeTv.setText(t);
                        }
                        break;

                    case MSG_LIVE_SHOW_COUNT_DOWN_TIME:
                        //live show
                        mLiveShowCountDownTime--;
                        if (mLiveShowCountDownTime > 0) {
                            mLiveShowTv.setVisibility(View.VISIBLE);
                            mCountDownTimeTv.setVisibility(View.GONE);
                            String name = "";
                            time = TimeUtil.formatSecondTime(mLiveShowCountDownTime);
                            mLiveShowTv.setText(getString(R.string.cls_live_show, name, time));

                            m.what = MSG_LIVE_SHOW_COUNT_DOWN_TIME;
                            mHandler.sendMessageDelayed(m, 1000);
                        } else {
                            mCountDownTimeTv.setVisibility(View.VISIBLE);
                            mLiveShowTv.setVisibility(View.GONE);
                        }
                        break;

                    case MSG_SOCKET_TIME_OUT:
                        //socket time out
                        if (!mSktConnected && mSocketRetryCount++ < 3) {
                            //reconnect
                            initData(false);
                        } else {
                            cancelProgress();
                            showContinueConnectClassroom();
                        }
                        break;
                }
            }
        }
    };

    /**
     * 网络切换监听
     */
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
                mSktConnected = false;
                setTips(null, getString(R.string.no_network));
                if (mTalkPanel != null) {
                    mTalkPanel.needSocketReListener();
                }
                if (mClassroomController != null) {
                    mClassroomController.onPauseVideo();
                }
                mNetworkState = ClassroomBusiness.NETWORK_NONE;
            } else if (wifiNet) {
                // wifi network
                hideTips();
                mNetworkState = ClassroomBusiness.NETWORK_WIFI;
                if (mClassroomController != null &&
                        (mClassroomController.needStreamRePublishing() || mClassroomController.needStreamRePlaying())) {
                    if (!mSktConnected) {
                        if (mClassroomController != null) {
                            mClassroomController.onPauseVideo();
                        }
                        initData(false);
                    } else {
                        mClassroomController.onResumeVideo();
                    }
                }
            } else if (mobileNet) {
                // mobile network
                hideTips();
                if (mClassroomController != null &&
                        (mClassroomController.needStreamRePublishing() || mClassroomController.needStreamRePlaying())) {
                    if (!mSktConnected) {
                        if (mClassroomController != null) {
                            mClassroomController.onPauseVideo();
                        }
                        initData(false);
                    } else {
                        //reconnected
                        if (mNetworkState == ClassroomBusiness.NETWORK_WIFI) {
                            if (mClassroomController.needStreamRePlaying()) {
                                handleMobileNetworkLiveDialog(false);
                            }
                            if (mClassroomController.needStreamRePublishing()) {
                                handleMobileNetworkLiveDialog(true);
                            }
                        } else if (mNetworkState == ClassroomBusiness.NETWORK_NONE) {
                            mClassroomController.onResumeVideo();
                        }
                    }
                }
                mNetworkState = ClassroomBusiness.NETWORK_OTHER;
            }
        }
    }

    @Override
    public void onStreamStopped(Constants.User user, int type, Object extra) {
        switch (type) {
            case StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL:
            case StreamType.TYPE_STREAM_PLAY_INDIVIDUAL:
                if (!TextUtils.isEmpty(mBeforeClamSteamState)) {
                    mLiveSessionState = mBeforeClamSteamState;
                    mBeforeClamSteamState = null;
                }
                break;
            case StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER:

                break;
            case StreamType.TYPE_STREAM_PUBLISH:
                if (mClassroomController != null && mUser == Constants.User.TEACHER) {
                    mClassroomController.pausePlayStream();
                }
                break;
            case StreamType.TYPE_STREAM_PLAY:
                if (mClassroomController != null && mUser == Constants.User.STUDENT) {
                    mClassroomController.pausePublishStream();
                }
                break;

            case StreamType.TYPE_STREAM_PLAY_PEER_TO_PEER:

                break;
        }

        cancelProgress();
        setTipsByState(mLiveSessionState);
        setControllerBtnStyle(mLiveSessionState);
        hideLiveShowCountDownTime();
        if (XiaojsConfig.DEBUG) {
            String s = getTxtString(user, type);
            Toast.makeText(this, "=====stop=====" + s, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStreamStarted(Constants.User user, int type, Object extra) {
        switch (type) {
            case StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER:
                if (user == Constants.User.TEACHER && !TextUtils.isEmpty(mTeaPeerPlayAccountId)) {
                    mPeerPlaySteamMap.put(mTeaPeerPlayAccountId, true);
                }
                break;
            case StreamType.TYPE_STREAM_PLAY:
                break;
            case StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL:
                setLiveShowCountDownTime(mIndividualStreamDuration);
                break;
            case StreamType.TYPE_STREAM_PLAY_INDIVIDUAL:
                long time = 0;
                try {
                    time = (long) extra;
                } catch (Exception e) {
                }
                setLiveShowCountDownTime(time);
                break;
            case StreamType.TYPE_STREAM_PUBLISH:
                setCountDownTimes(mCountDownTime, TYPE_LIVE_PLAYING);
                break;
        }

        hideTips();
        cancelProgress();
        if (mClassroomController != null) {
            mClassroomController.exitPlayVideo();
        }

        if (XiaojsConfig.DEBUG) {
            String s = getTxtString(user, type);
            Toast.makeText(this, "=====started=====" + s, Toast.LENGTH_SHORT).show();
        }
    }

    private String getTxtString(Constants.User user, int type) {
        String txt = "";
        switch (type) {
            case StreamType.TYPE_STREAM_PUBLISH:
                txt = "推送流";
                break;
            case StreamType.TYPE_STREAM_PLAY:
                txt = "播放流";
                break;
            case StreamType.TYPE_STREAM_PLAY_PEER_TO_PEER:
                txt = "一对一播流";
                break;
            case StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER:
                txt = "一对一推流";
                break;
            case StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL:
                txt = "个人推送流";
                break;
            case StreamType.TYPE_STREAM_PLAY_INDIVIDUAL:
                txt = "个人播放流";
                break;
        }

        return (user == Constants.User.TEACHER ? "老师" : "学生") + txt;
    }

    /**
     * 处理移动网络播放直播
     *
     * @param publishStream true：推流 false 播放流
     */
    private void handleMobileNetworkLiveDialog(final boolean publishStream) {
        if (ClassroomBusiness.getCurrentNetwork(this) == ClassroomBusiness.NETWORK_OTHER) {
            boolean open = XjsUtils.getSharedPreferences().getBoolean(Constants.KEY_MOBILE_NETWORK_LIVE, false);
            boolean allowMobileLive = XjsUtils.getSharedPreferences().getBoolean(Constants.KEY_MOBILE_NETWORK_LIVE_4_LESSON, false);
            if (!open && !allowMobileLive) {
                if (mMobileNetworkDialog == null) {
                    mMobileNetworkDialog = new CommonDialog(this);
                    int width = DeviceUtil.getScreenWidth(this) / 2;
                    int height = ViewGroup.LayoutParams.WRAP_CONTENT;
                    mMobileNetworkDialog.setTitle(R.string.mobile_network_title);
                    mMobileNetworkDialog.setDesc(R.string.mobile_network_desc);
                    mMobileNetworkDialog.setLefBtnText(R.string.mobile_network_forbid);
                    mMobileNetworkDialog.setRightBtnText(R.string.mobile_network_allow);
                    mMobileNetworkDialog.setDialogLayout(width, height);
                    mMobileNetworkDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                        @Override
                        public void onClick() {
                            mMobileNetworkDialog.dismiss();
                            onCallbackPerformed(publishStream);
                            XjsUtils.getSharedPreferences().edit().putBoolean(Constants.KEY_MOBILE_NETWORK_LIVE_4_LESSON, true).apply();
                        }
                    });

                    mMobileNetworkDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                        @Override
                        public void onClick() {
                        }
                    });
                }

                mMobileNetworkDialog.show();
            } else {
                onCallbackPerformed(publishStream);
            }
        } else {
            onCallbackPerformed(publishStream);
        }
    }

    private void onCallbackPerformed(boolean publishStream) {
        if (mClassroomController != null) {
            if (publishStream) {
                mClassroomController.publishStream();
            } else {
                mClassroomController.playStream();
            }
            setControllerBtnStyle(mLiveSessionState);
        }
    }

    private void setTips(int resTitleId, int resDescId) {
        setTips(getString(resTitleId), getString(resDescId));
    }

    private void setTips(String title, String desc) {
        if (mTipView.getVisibility() != View.VISIBLE) {
            mTipView.setVisibility(View.VISIBLE);
        }

        if (!TextUtils.isEmpty(title)) {
            if (mTipTitleTv.getVisibility() != View.VISIBLE) {
                mTipTitleTv.setVisibility(View.VISIBLE);
            }
            mTipTitleTv.setText(title);
        } else {
            if (mTipTitleTv.getVisibility() != View.GONE) {
                mTipTitleTv.setVisibility(View.GONE);
            }
        }

        if (!TextUtils.isEmpty(desc)) {
            if (mTipDescTv.getVisibility() != View.VISIBLE) {
                mTipDescTv.setVisibility(View.VISIBLE);
            }
            mTipDescTv.setText(desc);
        } else {
            if (mTipDescTv.getVisibility() != View.GONE) {
                mTipDescTv.setVisibility(View.GONE);
            }
        }
    }

    private void hideTips() {
        mTipView.setVisibility(View.GONE);
    }

    private void showContinueConnectClassroom() {
        if (mContinueConnectClassroomDialog == null) {
            mContinueConnectClassroomDialog = new CommonDialog(this);
            int width = DeviceUtil.getScreenWidth(this) / 2;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mContinueConnectClassroomDialog.setTitle(R.string.cr_live_connect_fail_title);
            mContinueConnectClassroomDialog.setDesc(R.string.cr_live_connect_fail_desc);
            mContinueConnectClassroomDialog.setLefBtnText(R.string.cr_live_connect_fail_exit);
            mContinueConnectClassroomDialog.setRightBtnText(R.string.cr_live_connect_fail_continue);
            mContinueConnectClassroomDialog.setDialogLayout(width, height);
            mContinueConnectClassroomDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mContinueConnectClassroomDialog.dismiss();
                    mSocketRetryCount = 0;
                    initData(true);
                }
            });

            mContinueConnectClassroomDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    ClassroomActivity.this.finish();
                }
            });
        }

        mContinueConnectClassroomDialog.show();
    }
}
