package cn.xiaojs.xma.ui.classroom.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.StreamingState;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.model.socket.room.EventReceived;
import cn.xiaojs.xma.model.socket.room.OpenMediaReceive;
import cn.xiaojs.xma.model.socket.room.ShareboardReceive;
import cn.xiaojs.xma.model.socket.room.StreamQualityChangedReceive;
import cn.xiaojs.xma.model.socket.room.SyncClassStateReceive;
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.ui.classroom.bean.StreamingQuality;
import cn.xiaojs.xma.ui.classroom.live.PlayVideoController;
import cn.xiaojs.xma.ui.classroom.live.view.BaseMediaView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.talk.EmbedTalkFragment;
import cn.xiaojs.xma.ui.classroom.talk.OnAttendItemClick;
import cn.xiaojs.xma.ui.classroom.talk.OnGetTalkListener;
import cn.xiaojs.xma.ui.classroom.talk.TalkManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkPresenter;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomType;
import cn.xiaojs.xma.ui.classroom2.core.EventListener;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.ui.widget.SheetFragment;
import cn.xiaojs.xma.util.XjsUtils;
import io.reactivex.functions.Consumer;

import static cn.xiaojs.xma.ui.classroom.live.VideoController.STREAM_MEDIA_CLOSED;

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
 * Date:2017/5/3
 * Desc:
 *
 * ======================================================================================== */

public class PlayFragment extends ClassroomLiveFragment implements OnGetTalkListener {
    public final static int MSG_MODE_INPUT = 1;
    public final static int FULL_SCREEN_MODE_INPUT = 2;

    public final static int REQUEST_PERMISSION = 3;



    @BindView(R.id.tip_view)
    View mTipView;

    @BindView(R.id.play_video)
    PlayerTextureView mPlayVideoView;
    @BindView(R.id.enter_full_screen)
    ImageView mEnterLandBtn;

    //top panel
    @BindView(R.id.top_panel)
    View mTopPanel;
    @BindView(R.id.back_btn)
    ImageView mBackBtn;
    @BindView(R.id.play_pause_btn)
    ImageView mPlayPauseBtn;
    @BindView(R.id.finish_btn)
    ImageView mFinishBtn;
    @BindView(R.id.setting_btn)
    ImageView mSettingBtn;
    @BindView(R.id.lesson_title)
    TextView mLessonTitle;
    @BindView(R.id.lesson_time_info)
    TextView mLessonTimeInfo;

    //time status bar
    @BindView(R.id.time_status_bar)
    View mTimeStatusBar;
    @BindView(R.id.publish_camera_switcher)
    ImageView mPublishCameraSwitcher;

    @BindView(R.id.discussion_list_view)
    PullToRefreshListView mDiscussionListView;

    //layout
    @BindView(R.id.play_layout)
    RelativeLayout mPlayLayout;
    @BindView(R.id.talk_layout)
    FrameLayout mTalkLayout;

    //msg_mode bottom panel
    @BindView(R.id.talk_bottom_panel)
    LinearLayout mPortraitBottomBar;
    @BindView(R.id.open_docs_btn)
    ImageView mOpenDocsBtn;
    @BindView(R.id.talk_open_contact_btn)
    MessageImageView mTalkOpenContactBtn;
    @BindView(R.id.talk_send_txt_btn)
    ImageView mSendTxtBtn;
    @BindView(R.id.talk_send_other_btn)
    ImageView mSendOtherBtn;
    @BindView(R.id.talk_share_btn)
    ImageView mShareBtn;
    @BindView(R.id.class_canlender)
    ImageView mCanlenderBtn;


    //full screen bottom panel
    @BindView(R.id.fc_bottom_panel)
    View mLandBottomPanel;
    @BindView(R.id.fc_land_portrait_btn)
    ImageView mLandPortraitBtn; //land/portrait toggle
    @BindView(R.id.fc_open_contact_btn)
    MessageImageView mFullscreenContactBtn;
    @BindView(R.id.fc_screenshot_portrait_btn)
    ImageView mScreenshotPortraitBtn;
    @BindView(R.id.fc_screenshot_land_btn)
    ImageView mScreenshotLandBtn;
    @BindView(R.id.fc_hide_show_talk_btn)
    MessageImageView mHideShowTalkBtn;
    @BindView(R.id.fc_open_talk_btn)
    ImageView mOpenTalkBtn;


    private EmbedTalkFragment mEmbedTalkFragment;
    private int mVideoWidth;
    private int mVideoHeight;
    private String mBeforePeekAccountId;
    private long mPlayOrPausePressTime = 0;

    private EventListener.ELPlaylive eventListener;

    @Override
    protected void initParams() {
        super.initParams();
        mTipsHelper = new TipsHelper(mContext, mTipView);
        mTimeProgressHelper = new TimeProgressHelper(mContext, mLessonTimeInfo, mTimeStatusBar);
        mVideoController = new PlayVideoController(mContext, mPlayLayout, this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (eventListener != null) {
            eventListener.dispose();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        eventListener = classroomEngine.observerPlaylive(receivedConsumer);
        return view;
    }

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_play, null);
    }


    @Keep
    @PermissionRationale(requestCode = REQUEST_PERMISSION)
    public void requestCameraRationale() {
        PermissionHelper.showRationaleDialog(this,getString(R.string.permission_rationale_camera_audio_tip));
    }


    @OnClick({R.id.back_btn, R.id.play_pause_btn, R.id.setting_btn, R.id.open_docs_btn, R.id.talk_open_contact_btn,
            R.id.talk_send_txt_btn, R.id.talk_send_other_btn, R.id.talk_share_btn, R.id.enter_full_screen, R.id.play_layout,
            R.id.fc_land_portrait_btn, R.id.fc_screenshot_land_btn, R.id.fc_screenshot_portrait_btn, R.id.fc_open_contact_btn,
            R.id.fc_hide_show_talk_btn, R.id.fc_open_talk_btn, R.id.play_video, R.id.class_canlender})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                handOnBackPressed();
                break;
            case R.id.play_pause_btn:

                String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

                PermissionGen.needPermission(this, REQUEST_PERMISSION, permissions);

                //playOrPauseLesson();
                break;
            case R.id.setting_btn:
                if (isPortrait()) {
                    mClassroomController.openSetting(this, SheetFragment.SHEET_GRAVITY_BOTTOM, mSlideViewHeight, this);
                } else {
                    mClassroomController.openSetting(this, SheetFragment.SHEET_GRAVITY_RIGHT, mSlideViewWidth, this);
                }
                break;
            case R.id.open_docs_btn:
                AnalyticEvents.onEvent(mContext, 42);
                mClassroomController.openDocument(this,
                        ClassroomEngine.getEngine().getCtlSession());
                break;
            case R.id.talk_open_contact_btn:
            case R.id.fc_open_contact_btn:

                int eventCode = v.getId() == R.id.talk_open_contact_btn ? 43 : 48;
                AnalyticEvents.onEvent(mContext, eventCode);

                if (isPortrait() || v.getId() == R.id.talk_open_contact_btn) {
                    mClassroomController.openContact(this,
                            ClassroomEngine.getEngine().getCtlSession(),
                            SheetFragment.SHEET_GRAVITY_BOTTOM, mSlideViewHeight);
                } else {
                    mClassroomController.openContact(this,
                            ClassroomEngine.getEngine().getCtlSession(),
                            SheetFragment.SHEET_GRAVITY_RIGHT, mSlideViewWidth);
                }
                break;
            case R.id.talk_send_txt_btn:
                AnalyticEvents.onEvent(mContext, 44);
                mClassroomController.openInputText(this, MSG_MODE_INPUT);
                break;
            case R.id.talk_send_other_btn:
                mClassroomController.selectPic(this);
                break;
            case R.id.talk_share_btn:
                break;
            case R.id.enter_full_screen:
                //enter full screen
                AnalyticEvents.onEvent(mContext, 46);
                enterFullScreen();
                break;
            case R.id.play_layout:
                startAnim();
                break;
            case R.id.fc_land_portrait_btn:
                AnalyticEvents.onEvent(mContext, 47);
                toggleLandPortrait();
                break;
            case R.id.fc_screenshot_land_btn:
            case R.id.fc_screenshot_portrait_btn:
                AnalyticEvents.onEvent(mContext, 51);
                //ClassroomController.getInstance().enterPhotoDoodleByBitmap(null);
                mVideoController.takeVideoFrame(this);
                break;
            case R.id.fc_hide_show_talk_btn:
                AnalyticEvents.onEvent(mContext, 49);
                showHideTalk();
                break;
            case R.id.fc_open_talk_btn:
                AnalyticEvents.onEvent(mContext, 50);
                mClassroomController.openInputText(this, FULL_SCREEN_MODE_INPUT);
                break;
            case R.id.play_video:
                break;
            case R.id.class_canlender://日历课表
                AnalyticEvents.onEvent(mContext, 45);
                mClassroomController.enterClassCanlenderFragment(this);
                break;
            default:
                break;
        }
    }

    @Override
    protected void initView() {
        mPlayPauseBtn.setVisibility(View.INVISIBLE);
        mFinishBtn.setVisibility(View.INVISIBLE);

        mShareBtn.setVisibility(View.GONE);
        mDiscussionListView.setVisibility(View.GONE);
        mPublishCameraSwitcher.setVisibility(View.GONE);

        updatePortraitPlayViewStyle();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        ViewGroup.LayoutParams params = mDiscussionListView.getLayoutParams();
        if (params != null) {
            params.width = displayMetrics.widthPixels;
        }

        mPlayVideoView.setTouchConsume(false);
        //add talk
        mEmbedTalkFragment = new EmbedTalkFragment();
        mEmbedTalkFragment.setArguments(getArguments());
        mEmbedTalkFragment.setExitPeerTalkListener(this);
        getFragmentManager().beginTransaction().add(R.id.talk_layout, mEmbedTalkFragment).commit();


        //判断教室类型，如果为PrivateClass,需要显示教学日历
        if (classroomEngine.getClassroomType() == ClassroomType.ClassLesson) {
            mCanlenderBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {

        mLessonTitle.setText(getLessonTitle());
        initCtlLive();

        String liveState = classroomEngine.getLiveState();
        updateViewStyleByLiveState(liveState);

        mTalkOpenContactBtn.setCount(TalkManager.getInstance().getPeerTalkUnreadMsgCount());
        postHideAnim();

    }

    /**
     * 初始化课程直播
     */
    private void initCtlLive() {

        individualStreamDuration = classroomEngine.getIndividualStreamDuration();

        String liveState = classroomEngine.getLiveState();
        if (!TextUtils.isEmpty(mCtlSession.playUrl)
                && classroomEngine.getCtlSession().streamType == Live.StreamType.INDIVIDUAL) {

            individualStreamDuration = classroomEngine.getCtlSession().finishOn;
            if (XiaojsConfig.DEBUG) {
                Logger.d("individualStreamDuration...finishon"+ individualStreamDuration);
            }
        }

        mCountTime = ClassroomBusiness.getCountTimeByCtlSession();
        Bundle data = getArguments();
        if (data != null) {
            int from = data.getInt(Constants.KEY_FROM, Constants.FROM_ACTIVITY);
            if (from == Constants.FROM_PUBLISH_FRAGMENT) {
                mCountTime = data.getLong(Constants.KEY_COUNT_TIME, mCountTime);
                individualStreamDuration = data.getLong(Constants.KEY_INDIVIDUAL_DURATION, 0);
            }

            //mIndividualResponseBody = (StreamingResponse) data.getSerializable(Constants.KEY_INDIVIDUAL_RESPONSE);
        }

        if (classroomEngine.getEngine().getClassroomType() == ClassroomType.ClassLesson) {
            mTimeProgressHelper.setTimeProgress(mCountTime,
                    individualStreamDuration,
                    liveState,
                    mIndividualName,
                    true);
        } else {
            mTimeProgressHelper.setTimeProgress(mCountTime,
                    individualStreamDuration,
                    liveState,
                    mIndividualName,
                    false);
        }


        if (TextUtils.isEmpty(mCtlSession.playUrl)) {
            mTipsHelper.setTipsByState(liveState);

            if (Live.LiveSessionState.LIVE.equals(liveState)
                    && classroomEngine.getClassroomType() == ClassroomType.ClassLesson) {

                if (classroomEngine.hasTeachingAbility()) {
                    mTipsHelper.setTips(R.string.living_back_to_talk_mode_title,
                            R.string.living_back_to_talk_mode_sub);
                }else {
                    mTipsHelper.setTips(R.string.student_living_back_to_talk_mode_title,
                            R.string.student_living_back_to_talk_mode_sub);
                }

            }

        } else {

            //这样做判断是为了
            if (Live.LiveSessionState.LIVE.equals(liveState)
                    && classroomEngine.getClassroomType() == ClassroomType.ClassLesson
                    && !TextUtils.isEmpty(mCtlSession.publishUrl)
                    && classroomEngine.hasTeachingAbility()){

                    mTipsHelper.setTips(R.string.living_back_to_talk_mode_title,
                            R.string.living_back_to_talk_mode_sub);

            }else {
                playStream(mCtlSession.playUrl);
            }
        }

    }


    private void updateViewStyleByLiveState(String liveState) {

        if (classroomEngine.canIndividualByState()) {
            //课前课后或者班课的课外时间
            CtlSession ctlSession = classroomEngine.getCtlSession();
            if ((ctlSession.streamType == Live.StreamType.INDIVIDUAL
                    || ctlSession.streamType == Live.StreamType.LIVE)
                    && !classroomEngine.canForceIndividual()) {

                mPlayPauseBtn.setVisibility(View.INVISIBLE);
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
            } else {

                mPlayPauseBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
            }

        } else {

            if (classroomEngine.getLiveMode() == Live.ClassroomMode.TEACHING) {
                mPlayPauseBtn.setVisibility(View.VISIBLE);
            } else {
                mPlayPauseBtn.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isPortrait;
        if (ActivityInfo.SCREEN_ORIENTATION_USER == newConfig.orientation) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            isPortrait = displayMetrics.widthPixels < displayMetrics.heightPixels;
        } else {
            isPortrait = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == newConfig.orientation;
        }

        if (isPortrait
                && ClassroomController.getInstance(mContext).getPlayFragmentMode() == ClassroomController.MODE_FRAGMENT_PLAY_TALK) {
            updatePortraitPlayViewStyle();
        } else {
            updateFullScreenPlayViewStyle(isPortrait);
        }

        postHideAnim();
    }

    /**
     * 进入全屏
     */
    private void enterFullScreen() {
        mHideShowTalkBtn.setCount(0);
        TalkManager.getInstance().resetMultiTalkUnreadMsgCount();
        mBeforePeekAccountId = TalkManager.getInstance().getPeekTalkingAccount();
        TalkManager.getInstance().setPeekTalkingAccount(null);
        TalkManager.getInstance().setFullscreenMultiTalkVisible(true);
        mFullscreenContactBtn.setCount(TalkManager.getInstance().getPeerTalkUnreadMsgCount());

        ClassroomController.getInstance(mContext).enterLandFullScreen(isPortrait(), mContext);
        if (mFullScreenTalkPresenter == null) {
            mFullScreenTalkPresenter = new TalkPresenter(mContext, mDiscussionListView, null);
            mFullScreenTalkPresenter.setOnTalkItemClickListener(this);
            mDiscussionListView.setVisibility(View.VISIBLE);
            mFullScreenTalkPresenter.switchFullMultiTalk();
        }
    }

    /**
     * 处理back键
     */
    private void handOnBackPressed() {
        if (isPortrait()) {
            if (ClassroomController.getInstance(mContext).isFragmentPlayFullScreen()) {
                updatePortraitPlayViewStyle();
                setUnreadMsgCountOnExitFullscreen();
                ClassroomController.getInstance(mContext).exitFullScreen(mContext, false);
            } else {
                ClassroomController.getInstance(mContext).showExitClassroomDialog();
            }
        } else {
            setUnreadMsgCountOnExitFullscreen();
            ClassroomController.getInstance(mContext).exitFullScreen(mContext, true);
        }
    }

    /**
     * 退出全屏是，设置未读消息
     */
    private void setUnreadMsgCountOnExitFullscreen() {
        if (TextUtils.isEmpty(mBeforePeekAccountId)) {
            //multi talk
            TalkManager.getInstance().setPeekTalkingAccount(null);
            mTalkOpenContactBtn.setCount(TalkManager.getInstance().getPeerTalkUnreadMsgCount());
        } else {
            //peek talk
            TalkManager.getInstance().setPeekTalkingAccount(mBeforePeekAccountId);
            int count = TalkManager.getInstance().getMultiTalkUnreadMsgCount();
            count = count + TalkManager.getInstance().clearPeerTalkUnreadMsgCount(mBeforePeekAccountId);
            mTalkOpenContactBtn.setCount(count);
        }
    }

    private void updateFullScreenPlayViewStyle(boolean isPortrait) {
        mTalkLayout.setVisibility(View.GONE);
        mPortraitBottomBar.setVisibility(View.GONE);
        mEnterLandBtn.setVisibility(View.GONE);
        mScreenshotLandBtn.setVisibility(isPortrait ? View.GONE : View.VISIBLE);
        mScreenshotPortraitBtn.setVisibility(isPortrait ? View.VISIBLE : View.GONE);
        mLandBottomPanel.setVisibility(View.VISIBLE);
        mDiscussionListView.setVisibility(View.VISIBLE);

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mVideoWidth = dm.widthPixels;
        mVideoHeight = dm.heightPixels;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPlayLayout.getLayoutParams();
        if (params != null) {
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        } else {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        }
        mPlayLayout.setLayoutParams(params);
    }

    private void updatePortraitPlayViewStyle() {
        mTalkLayout.setVisibility(View.VISIBLE);
        mPortraitBottomBar.setVisibility(View.VISIBLE);
        mEnterLandBtn.setVisibility(View.VISIBLE);
        mLandBottomPanel.setVisibility(View.GONE);
        mScreenshotLandBtn.setVisibility(View.GONE);
        mDiscussionListView.setVisibility(View.GONE);

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mSlideViewWidth = (int) (0.4F * dm.heightPixels);
        mSlideViewHeight = dm.heightPixels - (int) (dm.widthPixels / Constants.VIDEO_VIEW_RATIO);

        mVideoWidth = dm.widthPixels;
        mVideoHeight = (int) (dm.widthPixels / Constants.VIDEO_VIEW_RATIO);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPlayLayout.getLayoutParams();
        if (params != null) {
            params.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            params.height = mVideoHeight;
        } else {
            params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mVideoHeight);
        }
        mPlayLayout.setLayoutParams(params);
    }

    @Override
    protected void startAnim() {
        if (isAnimating() || mContent == null) {
            return;
        }

        if (ClassroomController.getInstance(mContext).isFragmentPlayFullScreen()) {
            if (mTopPanel.getVisibility() == View.VISIBLE) {
                hideAnim(mTopPanel, "mTopPanel");
                hideAnim(mFullscreenContactBtn, "mFullscreenContactBtn");
                hideAnim(mOpenTalkBtn, "mOpenTalkBtn");
                hideAnim(mLandPortraitBtn, "mLandPortraitBtn");
                hideAnim(mHideShowTalkBtn, "mHideShowTalkBtn");
            } else if (mTopPanel.getVisibility() == View.INVISIBLE) {
                showAnim(mTopPanel, "mTopPanel");
                showAnim(mFullscreenContactBtn, "mFullscreenContactBtn");
                showAnim(mOpenTalkBtn, "mOpenTalkBtn");
                showAnim(mLandPortraitBtn, "mLandPortraitBtn");
                showAnim(mHideShowTalkBtn, "mHideShowTalkBtn");
            }

            //show/hide take pic btn
            if (isPortrait()) {
                if (mScreenshotPortraitBtn.getVisibility() == View.VISIBLE) {
                    hideAnim(mScreenshotPortraitBtn, "mScreenshotPortraitBtn");
                } else {
                    showAnim(mScreenshotPortraitBtn, "mScreenshotPortraitBtn");
                }
            } else {
                if (mScreenshotLandBtn.getVisibility() == View.VISIBLE) {
                    hideAnim(mScreenshotLandBtn, "mScreenshotLandBtn");
                } else {
                    showAnim(mScreenshotLandBtn, "mScreenshotLandBtn");
                }
            }
        } else {
            if (mTopPanel.getVisibility() == View.VISIBLE) {
                hideAnim(mTopPanel, "mTopPanel");
                hideAnim(mEnterLandBtn, "mEnterLandBtn");
            } else if (mTopPanel.getVisibility() == View.INVISIBLE) {
                showAnim(mTopPanel, "mTopPanel");
                showAnim(mEnterLandBtn, "mEnterLandBtn");
            }
        }
    }

    @Override
    protected void showHideTalk() {
        if (mDiscussionListView.getVisibility() == View.VISIBLE) {
            TalkManager.getInstance().setFullscreenMultiTalkVisible(false);
            mHideShowTalkBtn.setImageResource(R.drawable.ic_cr_show_talk);
            mDiscussionListView.setVisibility(View.GONE);
        } else {
            TalkManager.getInstance().setFullscreenMultiTalkVisible(true);
            TalkManager.getInstance().resetMultiTalkUnreadMsgCount();
            mHideShowTalkBtn.setCount(0);
            mHideShowTalkBtn.setImageResource(R.drawable.ic_cr_hide_talk);
            mDiscussionListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onIndividualPublishCallback() {

        Bundle data = new Bundle();
        setControllerBtnStyle(Live.LiveSessionState.INDIVIDUAL);
        data.putInt(Constants.KEY_FROM, Constants.FROM_PLAY_FRAGMENT);
        data.putInt(Constants.KEY_PUBLISH_TYPE, CTLConstant.StreamingType.PUBLISH_INDIVIDUAL);
        mCountTime = mTimeProgressHelper.getCountTime();
        data.putLong(Constants.KEY_COUNT_TIME, mCountTime);
        XjsUtils.hideIMM(mContext, mContent.getWindowToken());

        //FIXME 当老师挤掉学生个人推流并进行直播推流后，需要将老师端学生的播放地址重制，避免老师直播结束后回到小屏模式，还要播放之前学生端推流的问题。

        ClassroomController.getInstance(mContext).enterPublishFragment(data, true);
    }

    @Override
    protected void onPeerPublishCallback(OpenMediaReceive openMediaNotify) {
        startPublishFragment(openMediaNotify.publishUrl, mCtlSession.playUrl, CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER);
    }

    @Override
    protected void onAcceptShareBoard(ShareboardReceive shareboard) {
        //TODO 同意白板协作后的后续操作

//        ClassroomController.getInstance(mContext).enterBoardCollaborateFragment(shareboard);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case ClassroomController.REQUEST_PIC_CODE:
                    String cropImgPath = data.getStringExtra(CropImagePath.CROP_IMAGE_PATH_TAG);
                    if (cropImgPath != null) {
                        int width = data.getIntExtra(CropImagePath.CROP_IMAGE_WIDTH, 0);
                        int height = data.getIntExtra(CropImagePath.CROP_IMAGE_HEIGHT, 0);
                        //send pic
                    }
                    break;
                case ClassroomController.REQUEST_INPUT:
                    String content = data.getStringExtra(Constants.KEY_MSG_INPUT_TXT);
                    if (TextUtils.isEmpty(content)) {
                        break;
                    }

                    int from = data.getIntExtra(Constants.KEY_MSG_INPUT_FROM, MSG_MODE_INPUT);
                    if (from == MSG_MODE_INPUT) {
                        String peekAccount = TalkManager.getInstance().getPeekTalkingAccount();
                        if (TextUtils.isEmpty(peekAccount)) {
                            TalkManager.getInstance().sendText(content);
                        } else {
                            TalkManager.getInstance().sendText(peekAccount, content);
                        }
                    } else if (from == FULL_SCREEN_MODE_INPUT) {
                        TalkManager.getInstance().sendText(content);
                    }
                    break;
                case ClassroomController.REQUEST_CONTACT:
                    //switch to peer_peer
                    Attendee attendee = (Attendee) data.getSerializableExtra(Constants.KEY_TALK_ATTEND);
                    int action = data.getIntExtra(Constants.KEY_TALK_ACTION, 0);
                    if (attendee == null) {
                        break;
                    }

                    int peerCount = TalkManager.getInstance().getPeerTalkUnreadMsgCount();
                    if (ClassroomController.getInstance(mContext).isFragmentPlayFullScreen()) {
                        mFullscreenContactBtn.setCount(peerCount);
                    } else {
                        mTalkOpenContactBtn.setCount(peerCount);
                    }

                    switch (action) {
                        case OnAttendItemClick.ACTION_OPEN_TALK:
                            if (ClassroomController.getInstance(mContext).isFragmentPlayFullScreen()) {
                                int gravity = data.getIntExtra(Constants.KEY_SHEET_GRAVITY, SheetFragment.SHEET_GRAVITY_BOTTOM);
                                int size = isPortrait() ? mSlideViewHeight : mSlideViewWidth;

                                CtlSession ctlSession = classroomEngine.getCtlSession();

                                if (isPortrait()) {
                                    ClassroomController.getInstance(mContext).openSlideTalk(this, attendee, ctlSession, size);
                                } else {
                                    ClassroomController.getInstance(mContext).openSlideTalk(this, attendee, ctlSession, gravity, size);
                                }
                            } else {
                                mEmbedTalkFragment.switchPeerTalk(attendee);
                            }
                            break;
                        case OnAttendItemClick.ACTION_OPEN_CAMERA:
                            //open publish: peer to peer
                            applyOpenStuVideo(attendee.accountId);
                            break;
                    }
                    break;
                case ClassroomController.REQUEST_DOC:
                    LibDoc doc = (LibDoc) data.getSerializableExtra(Constants.KEY_OPEN_DOC_BEAN);
                    ClassroomController.getInstance(mContext).exitDocFragmentWhitOpenMime(doc, this);
                    break;
                case ClassroomController.REQUEST_CLASS_CANLENDER:
                    //TODO 是否回放
                    //ClassroomController.getInstance().enterVideoPlayPage();
                    break;
            }
        }

    }

    private Consumer<EventReceived> receivedConsumer = new Consumer<EventReceived>() {
        @Override
        public void accept(EventReceived eventReceived) throws Exception {

            if (XiaojsConfig.DEBUG) {
                Logger.d("ELPlaylive received eventType:%d", eventReceived.eventType);
            }

            switch (eventReceived.eventType) {
                case Su.EventType.REMIND_FINALIZATION:
                    Toast.makeText(mContext, R.string.remind_final_tips, Toast.LENGTH_LONG).show();
                    break;
                case Su.EventType.CLOSE_MEDIA:
                    onStreamStopped(CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER, STREAM_MEDIA_CLOSED);
                    break;
                case Su.EventType.MEDIA_ABORTED:
                    onStreamStopped(CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER, STREAM_MEDIA_CLOSED);
                    break;
                case Su.EventType.OPEN_MEDIA:
                    OpenMediaReceive receive = (OpenMediaReceive) eventReceived.t;
                    showOpenMediaDlg(receive);
                    break;
                case Su.EventType.REFRESH_STREAMING_QUALITY:
                    StreamQualityChangedReceive qualityChangedReceive =
                            (StreamQualityChangedReceive) eventReceived.t;
                    if (qualityChangedReceive.quality == 1 || qualityChangedReceive.quality == 2) {
                        Toast.makeText(mContext, R.string.liver_bad_net_tips, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Su.EventType.SYNC_STATE:
                    SyncStateReceive syncStateReceive = (SyncStateReceive) eventReceived.t;
                    handleSyncState(syncStateReceive);
                    break;
                case Su.EventType.SYNC_CLASS_STATE:
                    SyncClassStateReceive syncState = (SyncClassStateReceive) eventReceived.t;

                    if (Live.LiveSessionState.IDLE.equals(syncState.to)) {

                        updateTitle();
                        mTipsHelper.setTipsByState(syncState.to);
                        //FIXME 总是时间，应该显示为0；
                        mTimeProgressHelper.reloadLessonDuration();
                        mTimeProgressHelper.setTimeProgress(0, syncState.to, false);
                        setControllerBtnStyle(syncState.to);

                        //FIXME 应该收到流暂停的消息，先临时放到这个地方处理
                        mVideoController.pausePlayStream(CTLConstant.StreamingType.PLAY_LIVE);


                    } else if (Live.LiveSessionState.PENDING_FOR_LIVE.equals(syncState.to)) {
                        //班中当前课的信息

                        //FIXME 应该收到流暂停的消息，先临时放到这个地方处理
                        //mVideoController.pausePlayStream(StreamType.TYPE_STREAM_PLAY);

                        updateTitle();
                        mTimeProgressHelper.reloadLessonDuration();

                    }
                    break;
                case Su.EventType.SHARE_BOARD:
                    showShareBoardDlg((ShareboardReceive) eventReceived.t);
                    break;
                case Su.EventType.STOP_SHARE_BOARD:
                    break;
            }
        }
    };


    private void updateTitle() {
        mLessonTitle.setText(getLessonTitle());
    }

    private void handleSyncState(SyncStateReceive syncState) {
        mCountTime = mTimeProgressHelper.getCountTime();
        boolean autoCountTime = false;

        if (Live.LiveSessionState.SCHEDULED.equals(syncState.from) && Live.LiveSessionState.PENDING_FOR_JOIN.equals(syncState.to)) {
            autoCountTime = true;

            if (syncState.timeline != null) {
                mCountTime = syncState.timeline.startOn;
            }

        } else if ((Live.LiveSessionState.PENDING_FOR_JOIN.equals(syncState.from) && Live.LiveSessionState.LIVE.equals(syncState.to))
                || (Live.LiveSessionState.RESET.equals(syncState.from) && Live.LiveSessionState.LIVE.equals(syncState.to))) {
            autoCountTime = true;
            mCountTime = syncState.timeline != null ? syncState.timeline.hasTaken : 0;
        } else if (Live.LiveSessionState.LIVE.equals(syncState.from) && Live.LiveSessionState.RESET.equals(syncState.to)) {
            autoCountTime = false;
            mCountTime = syncState.timeline != null ? syncState.timeline.hasTaken : 0;
        } else if (Live.LiveSessionState.LIVE.equals(syncState.from) && Live.LiveSessionState.DELAY.equals(syncState.to)) {
            autoCountTime = false;
            mCountTime = 0;
        } else if (Live.LiveSessionState.DELAY.equals(syncState.from) && Live.LiveSessionState.FINISHED.equals(syncState.to)) {

        } else if (Live.LiveSessionState.NONE.equals(syncState.from) && Live.LiveSessionState.SCHEDULED.equals(syncState.to)) {
            autoCountTime = true;
            mCountTime = syncState.timeline != null ? syncState.timeline.startOn : 0;

        } else if (Live.LiveSessionState.LIVE.equals(syncState.from) && Live.LiveSessionState.FINISHED.equals(syncState.to)) {
            autoCountTime = false;
            mCountTime = 0;
        }

        setControllerBtnStyle(syncState.to);
        mTimeProgressHelper.setTimeProgress(mCountTime, syncState.to, autoCountTime);

//        if (mTipsHelper != null) {
//            mTipsHelper.hideTips();
//        }

        mTipsHelper.setTipsByState(syncState.to);
    }

    @Override
    public void playStream(String url) {
        int type = CTLConstant.StreamingType.PLAY_LIVE;
        String liveState = classroomEngine.getLiveState();
        if (Live.LiveSessionState.LIVE.equals(liveState)
                || Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState)
                || Live.LiveSessionState.PENDING_FOR_LIVE.equals(liveState)) {
            type = CTLConstant.StreamingType.PLAY_LIVE;
        } else if (ClassroomBusiness.canIndividualByState(liveState)) {
            type = CTLConstant.StreamingType.PLAY_INDIVIDUAL;
        }

        mVideoController.playStream(type, url);
    }

    @Override
    public void onStreamStarted(int type, String streamUrl, Object extra) {
        String liveState = classroomEngine.getLiveState();
        mCountTime = mTimeProgressHelper.getCountTime();
        switch (type) {
            case CTLConstant.StreamingType.PLAY_PEER_TO_PEER:
                //FIXME 1对1也要更新时间？
                mTimeProgressHelper.setTimeProgress(mCountTime, liveState);
                break;
            case CTLConstant.StreamingType.PLAY_LIVE:
                mTimeProgressHelper.setTimeProgress(mCountTime, liveState);
                break;
            case CTLConstant.StreamingType.PLAY_INDIVIDUAL:

                if (classroomEngine.canForceIndividual()) {
                    //个人推流时，如果不是同学身份，此按钮要现实，因为主讲/班主任可以强制尽心推流。
                    mPlayPauseBtn.setVisibility(View.VISIBLE);
                } else {
                    mPlayPauseBtn.setVisibility(View.INVISIBLE);
                }

                try {
                    individualStreamDuration = (long) extra;

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (individualStreamDuration <= 0) {
                    individualStreamDuration = classroomEngine.getIndividualStreamDuration();
                }

                mTimeProgressHelper.setTimeProgress(mCountTime,
                        individualStreamDuration,
                        liveState,
                        mIndividualName,
                        true);
                break;
        }

        mTipsHelper.hideTips();

        //exit video play
        ClassroomController.getInstance(mContext).exitVideoPlayPage();
    }

    @Override
    public void onStreamSizeChanged(BaseMediaView v, int videoW, int videoH) {

    }

    @Override
    public void onStreamStopped(int type, Object extra) {
        //updateViewStyleByLiveState();
        String liveState = classroomEngine.getLiveState();
        mCountTime = mTimeProgressHelper.getCountTime();
        mTimeProgressHelper.setTimeProgress(mCountTime, 0, liveState, mIndividualName, false);

        mTipsHelper.setTipsByStateOnStrop(liveState);

        switch (type) {
            case CTLConstant.StreamingType.PLAY_LIVE:

                if (classroomEngine.getClassroomType() == ClassroomType.ClassLesson
                        && Live.LiveSessionState.LIVE.equals(liveState)) {
                    mTimeProgressHelper.setTimeProgress(mCountTime, 0, liveState, mIndividualName, true);
                }

                break;
            case CTLConstant.StreamingType.PLAY_INDIVIDUAL:
                mPlayPauseBtn.setVisibility(View.VISIBLE);
                individualStreamDuration = 0;
                break;
        }


    }

    @Override
    public void onStreamException(StreamingState errorCode, int type, Object extra) {

    }

    @Override
    public void onGetTalkFinished(boolean succ) {

    }

    @Override
    public void onResolutionChanged(int quality) {

    }

    @Override
    public void onSwitcherChanged(int switcher, boolean open) {
        switch (switcher) {
            case Constants.SWITCHER_CAMERA:
                mVideoController.openOrCloseCamera();
                break;
            case Constants.SWITCHER_AUDIO:
                mVideoController.muteOrUnmute();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        handOnBackPressed();
    }


    @Override
    public void onStreamingQualityChanged(StreamingQuality streamingQuality) {

        if (streamingQuality != null) {
            if (streamingQuality.quality == 1 || streamingQuality.quality == 2) {
                Toast.makeText(mContext, R.string.liver_bad_net_tips, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void setControllerBtnStyle(String liveState) {
        if (mPlayPauseBtn == null) {
            return;
        }

        if (ClassroomBusiness.canIndividualByState(liveState)) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
            mPlayPauseBtn.setVisibility(View.VISIBLE);
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState)
                || Live.LiveSessionState.RESET.equals(liveState)
                || Live.LiveSessionState.PENDING_FOR_LIVE.equals(liveState)) {
            if (classroomEngine.getLiveMode() == Live.ClassroomMode.TEACHING) {
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_start);
                mPlayPauseBtn.setVisibility(View.VISIBLE);
            } else {
                mPlayPauseBtn.setVisibility(View.INVISIBLE);
            }
        } else if (Live.LiveSessionState.LIVE.equals(liveState)) {
            if (classroomEngine.getLiveMode() == Live.ClassroomMode.TEACHING) {
                mPlayPauseBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
            } else {
                mPlayPauseBtn.setVisibility(View.INVISIBLE);
            }
        } else if (Live.LiveSessionState.DELAY.equals(liveState)) {
            mPlayPauseBtn.setVisibility(View.INVISIBLE);
        } else if (Live.LiveSessionState.INDIVIDUAL.equals(liveState)
                || classroomEngine.liveShow()) {
            mPlayPauseBtn.setVisibility(View.VISIBLE);
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
        }
    }


    private void updateControllerBtnForClassLesson() {
        if (mPlayPauseBtn == null) {
            return;
        }

        String liveState = classroomEngine.getLiveState();
        setControllerBtnStyle(liveState);
    }

    @Keep
    @PermissionSuccess(requestCode = REQUEST_PERMISSION)
    public void playOrPauseLesson() {
        if (System.currentTimeMillis() - mPlayOrPausePressTime < BTN_PRESS_INTERVAL) {
            return;
        }

        mPlayOrPausePressTime = System.currentTimeMillis();

        String liveState = classroomEngine.getLiveState();
        if (ClassroomBusiness.canIndividualByState(liveState)) {
            AnalyticEvents.onEvent(mContext, 41);

            individualPublishStream();
        } else if (Live.LiveSessionState.RESET.equals(liveState)) {
            showProgress(true);


            classroomEngine.resumeClass(mTicket, new APIServiceCallback<ClassResponse>() {
                @Override
                public void onSuccess(ClassResponse object) {
                    cancelProgress();
                    startPublishFragment(object != null ?
                            object.publishUrl : null, null, CTLConstant.StreamingType.PUBLISH_LIVE);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                    Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
                }
            });

        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState) ||
                Live.LiveSessionState.PENDING_FOR_LIVE.equals(liveState)) {//公开课在教室内没有pending_for_live状态，班才有

            showProgress(true);

            classroomEngine.beginClass(mTicket, new APIServiceCallback<ClassResponse>() {
                @Override
                public void onSuccess(ClassResponse response) {
                    cancelProgress();
                    mTimeProgressHelper.resetTime();
                    startPublishFragment(response != null ? response.publishUrl : null,
                            null,
                            CTLConstant.StreamingType.PUBLISH_LIVE);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                }
            });

        } else if (classroomEngine.liveShow()) {
//            AnalyticEvents.onEvent(getActivity(),41);

            cancelProgress();
            setControllerBtnStyle(liveState);
            mVideoController.pausePublishStream(CTLConstant.StreamingType.PUBLISH_INDIVIDUAL);

        } else if (Live.LiveSessionState.LIVE.equals(liveState)
                || Live.LiveSessionState.DELAY.equals(liveState)) {
            cancelProgress();
            //进入播放端
            if (classroomEngine.getLiveMode() == Live.ClassroomMode.TEACHING) {
                //teachers-->live, delay
                Bundle data = new Bundle();
                data.putInt(Constants.KEY_FROM, Constants.FROM_PLAY_FRAGMENT);
                mCountTime = mTimeProgressHelper.getCountTime();
                data.putLong(Constants.KEY_COUNT_TIME, mCountTime);
                data.putSerializable(Constants.KEY_PUBLISH_TYPE, CTLConstant.StreamingType.PUBLISH_LIVE);
                //班课中，由于没有暂停上课，再次进入直播是，需要传之前的播放URL
                //data.putString(Constants.KEY_PUBLISH_URL, mCtlSession.publishUrl);
                ClassroomController.getInstance(mContext).enterPublishFragment(data, true);
            }
        } else {
            cancelProgress();
        }
    }

    private void startPublishFragment(String publishUrl, String playUrl, int streamType) {
        Bundle data = new Bundle();
        data.putSerializable(Constants.KEY_PUBLISH_TYPE, streamType);
        data.putString(Constants.KEY_PUBLISH_URL, publishUrl);
        data.putString(Constants.KEY_PLAY_URL, playUrl);
        data.putInt(Constants.KEY_FROM, Constants.FROM_PLAY_FRAGMENT);

        if (streamType == CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER) {

            data.putLong(Constants.KEY_INDIVIDUAL_DURATION, mTimeProgressHelper.getIndividualStreamDuration());
        }
        mCountTime = mTimeProgressHelper.getCountTime();
        data.putLong(Constants.KEY_COUNT_TIME, mCountTime);
        XjsUtils.hideIMM(mContext, mContent.getWindowToken());
        ClassroomController.getInstance(mContext).enterPublishFragment(data, true);
    }

    @Override
    public void onExitTalk(int type) {
        if (type == TalkManager.TYPE_PEER_TALK) {
            if (!ClassroomController.getInstance(mContext).isFragmentPlayFullScreen()) {
                //exit peer talk and enter multi talk (msg mode)
                TalkManager.getInstance().resetMultiTalkUnreadMsgCount();
                mTalkOpenContactBtn.setCount(TalkManager.getInstance().getPeerTalkUnreadMsgCount());
            }
        }
    }

    @Override
    public void onMsgChanged(boolean receive, int criteria, TalkItem talkItem) {
        if (mTalkOpenContactBtn == null || !receive) {
            return;
        }

        if (ClassroomController.getInstance(mContext).isFragmentPlayFullScreen()) {
            //fullscreen mode
            if (mDiscussionListView.getVisibility() == View.VISIBLE) {
                TalkManager.getInstance().resetMultiTalkUnreadMsgCount();
            } else {
                mHideShowTalkBtn.setCount(TalkManager.getInstance().getMultiTalkUnreadMsgCount());
            }

            mFullscreenContactBtn.setCount(TalkManager.getInstance().getPeerTalkUnreadMsgCount());
            if (mHideShowTalkBtn.getVisibility() != View.VISIBLE) {
                startAnim();
            }
        } else {
            //msg mode
            String peekAccount = TalkManager.getInstance().getPeekTalkingAccount();
            if (peekAccount == null) {
                //multi talk
                mTalkOpenContactBtn.setCount(TalkManager.getInstance().getPeerTalkUnreadMsgCount());
            } else {
                //peer talk
                mTalkOpenContactBtn.setCount(TalkManager.getInstance().getAllUnreadMsgCount());
            }
        }

    }

    @Override
    public void onSocketConnectChanged(boolean connected) {
        if (connected) {
            String peekAccount = TalkManager.getInstance().getPeekTalkingAccount();
            if (TextUtils.isEmpty(peekAccount)) {
                mEmbedTalkFragment.updateMultiTalk();
            } else {
                mEmbedTalkFragment.updatePeerTalk(peekAccount);
            }

            if (mFullScreenTalkPresenter != null) {
                mFullScreenTalkPresenter.switchFullMultiTalk();
            }
        }
    }

    @Override
    public void onTalkItemClick() {
        startAnim();
    }
}
