package cn.xiaojs.xma.ui.classroom.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orhanobut.logger.Logger;
import com.qiniu.pili.droid.streaming.StreamingState;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.ctl.FinishClassResponse;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.socket.room.OpenMediaReceive;
import cn.xiaojs.xma.model.socket.room.SyncClassStateReceive;
import cn.xiaojs.xma.model.socket.room.SyncStateReceive;
import cn.xiaojs.xma.ui.classroom.bean.StreamingQuality;
import cn.xiaojs.xma.ui.classroom.bean.SyncClassStateResponse;
import cn.xiaojs.xma.ui.classroom.bean.SyncStateResponse;
import cn.xiaojs.xma.ui.classroom.live.PublishVideoController;
import cn.xiaojs.xma.ui.classroom.live.VideoController;
import cn.xiaojs.xma.ui.classroom.live.view.BaseMediaView;
import cn.xiaojs.xma.ui.classroom.live.view.LiveMenu;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.talk.OnAttendItemClick;
import cn.xiaojs.xma.ui.classroom.talk.TalkManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkPresenter;
import cn.xiaojs.xma.ui.classroom2.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.ClassroomType;
import cn.xiaojs.xma.ui.classroom2.EventListener;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.ui.widget.SheetFragment;
import okhttp3.ResponseBody;

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

public class PublishFragment extends ClassroomLiveFragment implements LiveRecordView.Listener, EventListener {
    private final static float PLAY_VIDEO_RATION = 16 / 9.0f;
    public static final int MSG_WHAT_EXIT_PUBLISH = 0x4;

    public static final int EXIT_DELAY_MS = 500;//ms

    @BindView(R.id.tip_view)
    View mTipView;

    @BindView(R.id.publish_layout)
    View mPublishLayout;
    @BindView(R.id.publish_video)
    LiveRecordView mPublishVideoView;
    @BindView(R.id.play_video)
    PlayerTextureView mPlayVideoView;
    @BindView(R.id.discussion_list_view)
    PullToRefreshListView mDiscussionListView;

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

    //bottom panel
    @BindView(R.id.fc_land_portrait_btn)
    ImageView mLandPortraitBtn;
    @BindView(R.id.fc_open_contact_btn)
    MessageImageView mContactBtn;
    @BindView(R.id.fc_screenshot_portrait_btn)
    ImageView mScreenshotPortraitBtn;
    @BindView(R.id.fc_hide_show_talk_btn)
    MessageImageView mHideShowTalkBtn;
    @BindView(R.id.fc_open_talk_btn)
    ImageView mOpenTalkBtn;

    //right panel
    @BindView(R.id.fc_screenshot_land_btn)
    ImageView mScreenshotLandBtn;

    @BindView(R.id.close_play_full_screen)
    ImageView mClosePlayFullScreenImg;

    private TalkPresenter mFullScreenTalkPresenter;
    private int mPublishType = CTLConstant.StreamingType.PUBLISH_LIVE;

    private CommonDialog mFinishDialog;
    private boolean mScaled = false;
    private int mNormalVideoWidth;
    private int mNormalVideoHeight;

    private boolean mHandKeyPressing = false;
    private boolean mAlreadyExitFragment = false;
    private long mPlayOrPausePressTime = 0;

    private CommonDialog noPermissionDlg;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_publish, null);
    }

    @Override
    protected void initParams() {
        super.initParams();
        mAlreadyExitFragment = false;
        mTipsHelper = new TipsHelper(mContext, mTipView);
        mTimeProgressHelper = new TimeProgressHelper(mContext, mLessonTimeInfo, mTimeStatusBar);
        mVideoController = new PublishVideoController(mContext, mPublishLayout, this);

        mTipsHelper.hideTips();
    }

    @OnClick({R.id.back_btn, R.id.play_pause_btn, R.id.setting_btn, R.id.fc_land_portrait_btn,
            R.id.fc_screenshot_land_btn, R.id.fc_screenshot_portrait_btn, R.id.fc_open_contact_btn,
            R.id.fc_hide_show_talk_btn, R.id.fc_open_talk_btn, R.id.play_video, R.id.publish_layout,
            R.id.finish_btn, R.id.publish_camera_switcher, R.id.close_play_full_screen})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_btn:
                handOnBackPressed();
                break;
            case R.id.play_pause_btn:
                playOrPauseLesson();
                break;
            case R.id.setting_btn:
                String currentState = classroomEngine.getLiveState();
                if (Live.LiveSessionState.LIVE.equals(currentState)
                        || Live.LiveSessionState.DELAY.equals(currentState)
                        || classroomEngine.liveShow()) {

                    Toast.makeText(mContext, "直播中，设置不可用", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (isPortrait()) {
                    mClassroomController.openSetting(this, SheetFragment.SHEET_GRAVITY_BOTTOM, mSlideViewHeight, this);
                } else {
                    mClassroomController.openSetting(this, SheetFragment.SHEET_GRAVITY_RIGHT, mSlideViewWidth, this);
                }
                break;
            case R.id.fc_open_contact_btn:
                AnalyticEvents.onEvent(mContext, 54);
                if (isPortrait()) {
                    mClassroomController.openContact(this, mCtlSession, SheetFragment.SHEET_GRAVITY_BOTTOM, mSlideViewHeight);
                } else {
                    mClassroomController.openContact(this, mCtlSession, SheetFragment.SHEET_GRAVITY_RIGHT, mSlideViewWidth);
                }
                break;
            case R.id.fc_land_portrait_btn:
                AnalyticEvents.onEvent(mContext, 53);
                toggleLandPortrait();
                break;
            case R.id.fc_screenshot_land_btn:
            case R.id.fc_screenshot_portrait_btn:

                AnalyticEvents.onEvent(mContext, 55);
                //ClassroomController.getInstance().enterPhotoDoodleByBitmap(null);
                mVideoController.takeVideoFrame(this);
                break;
            case R.id.fc_hide_show_talk_btn:
                AnalyticEvents.onEvent(mContext, 56);
                showHideTalk();
                break;
            case R.id.fc_open_talk_btn:
                AnalyticEvents.onEvent(mContext, 57);
                mClassroomController.openInputText(this, 0);
                break;
            case R.id.play_video:
                if (!mScaled) {
                    onPlayVideoViewClick();
                } else {
                    mScaled = !mScaled;
                    setPlayVideoParams(mNormalVideoWidth, mNormalVideoHeight);
                }
                break;
            case R.id.publish_layout:
                startAnim();
                break;
            case R.id.finish_btn:
                showFinishDialog();
                break;
            case R.id.publish_camera_switcher:
                AnalyticEvents.onEvent(mContext, 52);
                mVideoController.switchCamera();
                break;
            case R.id.close_play_full_screen:
                if (mScaled) {
                    mScaled = !mScaled;
                    setPlayVideoParams(mNormalVideoWidth, mNormalVideoHeight);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onViewClickedListener() {
        startAnim();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isPortrait = true;
        if (ActivityInfo.SCREEN_ORIENTATION_USER == newConfig.orientation) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            isPortrait = displayMetrics.widthPixels < displayMetrics.heightPixels;
        } else {
            isPortrait = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT == newConfig.orientation;
        }

        if (isPortrait) {
            updatePortraitViewStyle();
        } else {
            updateLandViewStyle();
        }

        postHideAnim();
    }

    @Override
    protected void toggleLandPortrait() {
        if (mVideoController.hasStreamPublishing()) {
            //TODO to be optimized
            int targetOrientation = isPortrait() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            mVideoController.togglePublishOrientation(targetOrientation, null);

//            if (mPublishType == StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL) {
//                int targetOrientation = isPortrait() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                mVideoController.togglePublishOrientation(targetOrientation, null);
//            } else {
//                mVideoController.pausePublishStream(mPublishType);
//                int targetOrientation = isPortrait() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
//                mVideoController.togglePublishOrientation(targetOrientation, new LiveRecordView.OnStreamOrientationListener() {
//                    @Override
//                    public void onStreamOrientationChanged(int orientation) {
//                        mVideoController.publishStream(mPublishType, mPublishUrl);
//                    }
//                });
//            }
        }

        super.toggleLandPortrait();
    }

    @Override
    protected void initView() {
        //mPublishVideoView.setTouchConsume(false);
        mPublishVideoView.setViewClickListener(this);

        updatePortraitViewStyle();
    }

    @Override
    protected void initData() {
        Bundle data = getArguments();
        mCountTime = ClassroomBusiness.getCountTimeByCtlSession();
        individualStreamDuration = classroomEngine.getIndividualStreamDuration();

        if (data != null) {
            mPublishType = data.getInt(Constants.KEY_PUBLISH_TYPE, CTLConstant.StreamingType.PUBLISH_LIVE);

            //get individual info
            //mIndividualResponseBody = (StreamingResponse) data.getSerializable(Constants.KEY_INDIVIDUAL_RESPONSE);
            //mIndividualStreamDuration = data.getLong(Constants.KEY_INDIVIDUAL_DURATION, 0);
            //mIndividualName = data.getString(Constants.KEY_INDIVIDUAL_NAME, "");
            Logger.d("**********KEY_1111************:" + individualStreamDuration);
            //get count time
            int from = data.getInt(Constants.KEY_FROM, Constants.FROM_ACTIVITY);
            if (from == Constants.FROM_PLAY_FRAGMENT) {
                mCountTime = data.getLong(Constants.KEY_COUNT_TIME, mCountTime);
                individualStreamDuration = data.getLong(Constants.KEY_INDIVIDUAL_DURATION, 0);

                Logger.d("**********KEY_INDIVIDUAL_DURATION************:" + individualStreamDuration);
            }
        }

        //TODO 当是classroom为班时，是否title显示要改变？
        mLessonTitle.setText(getLessonTitle());
        if (mFullScreenTalkPresenter == null) {
            mFullScreenTalkPresenter = new TalkPresenter(mContext, mDiscussionListView, null);
            mFullScreenTalkPresenter.setOnTalkItemClickListener(this);
            mFullScreenTalkPresenter.switchFullMultiTalk();
        }

        String liveState = classroomEngine.getLiveState();
        switch (mPublishType) {
            case CTLConstant.StreamingType.PUBLISH_LIVE:
                if (Live.LiveSessionState.LIVE.equals(liveState)) {
                    mVideoController.publishStream(CTLConstant.StreamingType.PUBLISH_LIVE, mCtlSession.publishUrl);
                } else {
                    playOrPauseLesson();
                }
                break;
            case CTLConstant.StreamingType.PUBLISH_INDIVIDUAL:
                mVideoController.publishStream(CTLConstant.StreamingType.PUBLISH_INDIVIDUAL, mCtlSession.publishUrl);
                break;
            case CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER:
                mVideoController.publishStream(CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER, mCtlSession.publishUrl);
                mVideoController.playStream(CTLConstant.StreamingType.PLAY_PEER_TO_PEER, mCtlSession.playUrl);
                break;
        }


        mTimeProgressHelper.setTimeProgress(mCountTime,
                individualStreamDuration,
                liveState,
                mIndividualName,
                classroomEngine.getLiveState(),
                false);

        setControllerBtnStyle(liveState);

        mContactBtn.setCount(TalkManager.getInstance().getPeerTalkUnreadMsgCount());
        postHideAnim();

        if (mPublishType == CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER
                && Live.LiveSessionState.FINISHED.equals(liveState)) {
            mTimeProgressHelper.mFullScreenTimeInfoTv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void startAnim() {
        if (isAnimating() || mContent == null) {
            return;
        }

        if (mTopPanel != null && mTopPanel.getVisibility() == View.VISIBLE) {
            hideAnim(mTopPanel, "mTopPanel");
            hideAnim(mContactBtn, "mFullscreenContactBtn");
            hideAnim(mOpenTalkBtn, "mOpenTalkBtn");
            hideAnim(mLandPortraitBtn, "mLandPortraitBtn");
            hideAnim(mHideShowTalkBtn, "mHideShowTalkBtn");
        } else if (mTopPanel != null && mTopPanel.getVisibility() == View.INVISIBLE) {
            showAnim(mTopPanel, "mTopPanel");
            showAnim(mContactBtn, "mFullscreenContactBtn");
            showAnim(mOpenTalkBtn, "mOpenTalkBtn");
            showAnim(mLandPortraitBtn, "mLandPortraitBtn");
            showAnim(mHideShowTalkBtn, "mHideShowTalkBtn");
        }

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

        setControllerBtnStyle(Live.LiveSessionState.INDIVIDUAL);
        String publishUrl = ClassroomEngine.getEngine().getPublishUrl();
        mVideoController.publishStream(CTLConstant.StreamingType.PUBLISH_INDIVIDUAL, publishUrl);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case ClassroomController.REQUEST_INPUT:
                    String content = data.getStringExtra(Constants.KEY_MSG_INPUT_TXT);
                    if (mFullScreenTalkPresenter != null && !TextUtils.isEmpty(content)) {
                        if (mHideShowTalkBtn != null) {
                            mHideShowTalkBtn.setImageResource(R.drawable.ic_cr_hide_talk);
                        }

                        if (mDiscussionListView != null) {
                            mDiscussionListView.setVisibility(View.VISIBLE);
                        }

                        String peekAccount = TalkManager.getInstance().getPeekTalkingAccount();
                        if (TextUtils.isEmpty(peekAccount)) {
                            TalkManager.getInstance().sendText(content);
                        } else {
                            TalkManager.getInstance().sendText(peekAccount, content);
                        }
                    }
                    break;
                case ClassroomController.REQUEST_CONTACT:
                    //switch to peer_peer
                    Attendee attendee = (Attendee) data.getSerializableExtra(Constants.KEY_TALK_ATTEND);
                    int action = data.getIntExtra(Constants.KEY_TALK_ACTION, 0);
                    if (attendee == null) {
                        break;
                    }

                    mContactBtn.setCount(TalkManager.getInstance().getPeerTalkUnreadMsgCount());
                    switch (action) {
                        case OnAttendItemClick.ACTION_OPEN_TALK:
                            int gravity = data.getIntExtra(Constants.KEY_SHEET_GRAVITY, SheetFragment.SHEET_GRAVITY_BOTTOM);
                            int size = isPortrait() ? mSlideViewHeight : mSlideViewWidth;
                            if (isPortrait()) {
                                ClassroomController.getInstance(mContext).openSlideTalk(this, attendee, mCtlSession, size);
                            } else {
                                ClassroomController.getInstance(mContext).openSlideTalk(this, attendee, mCtlSession, gravity, size);
                            }
                            break;
                        case OnAttendItemClick.ACTION_OPEN_CAMERA:
                            //open publish: peer to peer
                            applyOpenStuVideo(attendee.accountId);
                            break;
                    }
                    break;
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        classroomEngine.addEvenListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        classroomEngine.removeEvenListener(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        handOnBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();

        //updateSettingBtn();
    }

    @Override
    public void onResolutionChanged(int quality) {
        mVideoController.setPublishStreamByToggleResolution(true);
        mVideoController.togglePublishResolution();
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
    public void receivedEvent(String event, Object object) {
        if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.REMIND_FINALIZATION).equals(event)) {
            Toast.makeText(mContext, R.string.remind_final_tips, Toast.LENGTH_LONG).show();
        } else if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLOSE_MEDIA).equals(event)) {

            onStreamStopped(CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER, STREAM_MEDIA_CLOSED);

        } else if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.MEDIA_ABORTED).equals(event)) {
            onStreamStopped(CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER, STREAM_MEDIA_CLOSED);
        } else if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.OPEN_MEDIA).equals(event)) {
            if (object == null) {
                return;
            }

            OpenMediaReceive receive = (OpenMediaReceive) object;
            showOpenMediaDlg(receive);
        } else if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_STATE).equals(event)) {

            if (object == null) {
                return;
            }

            SyncStateReceive receive = (SyncStateReceive) object;
            handleSyncState(receive);
        } else if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.SYNC_CLASS_STATE).equals(event)) {

            if (object == null)
                return;

            SyncClassStateReceive syncState = (SyncClassStateReceive) object;
            if (Live.LiveSessionState.IDLE.equals(syncState.to)) {
                updateTitle();
                //mTipsHelper.setTipsByState(syncState.to);
                mTimeProgressHelper.reloadLessonDuration();
                mTimeProgressHelper.setTimeProgress(0, syncState.to, false);
                setControllerBtnStyle(syncState.to);
                updateFinishStreamingView();
            } else if (Live.LiveSessionState.PENDING_FOR_LIVE.equals(syncState.to)) {
                //班中当前课的信息

                updateTitle();
                mTimeProgressHelper.reloadLessonDuration();
                updateFinishStreamingView();
                //ClassroomController.getInstance().enterPlayFragment(null, true);
//                    mTipsHelper.setTipsByState(syncState.to);
//                    long sep = (syncState.current.schedule.start.getTime()-System.currentTimeMillis()) / 1000;
//
//                    mTimeProgressHelper.setTimeProgress(sep, syncState.to, false);
//                    setControllerBtnStyle(syncState.to);
            }
        }
    }

    private void handleSyncState(SyncStateReceive syncState) {
        boolean autoCountTime = false;
        if (Live.LiveSessionState.SCHEDULED.equals(syncState.from) && Live.LiveSessionState.PENDING_FOR_JOIN.equals(syncState.to)) {
            autoCountTime = true;
            if (syncState.timeline != null) {
                mCountTime = syncState.timeline.startOn;
            }
        } else if (Live.LiveSessionState.LIVE.equals(syncState.from) && Live.LiveSessionState.RESET.equals(syncState.to)) {
            autoCountTime = false;
            mCountTime = syncState.timeline != null ? syncState.timeline.hasTaken : 0;
        } else if (Live.LiveSessionState.LIVE.equals(syncState.from) && Live.LiveSessionState.DELAY.equals(syncState.to)) {
            autoCountTime = true;
            mCountTime = 0;
        } else if (Live.LiveSessionState.DELAY.equals(syncState.from) && Live.LiveSessionState.FINISHED.equals(syncState.to)) {
            autoCountTime = true;

        } else if (Live.LiveSessionState.NONE.equals(syncState.from) && Live.LiveSessionState.SCHEDULED.equals(syncState.to)) {
            autoCountTime = true;
            mCountTime = syncState.timeline != null ? syncState.timeline.startOn : 0;
        }

        setControllerBtnStyle(syncState.to);

        mTimeProgressHelper.setTimeProgress(mCountTime, syncState.to, autoCountTime);
//        if (mTipsHelper != null) {
//            mTipsHelper.hideTips();
//        }

        //mTipsHelper.setTipsByState(syncState.to);

        if (Live.LiveSessionState.FINISHED.equals(syncState.to)) {
            if (exitHandler != null) {
                exitHandler.removeMessages(MSG_WHAT_EXIT_PUBLISH);
                exitHandler.sendEmptyMessageDelayed(MSG_WHAT_EXIT_PUBLISH, EXIT_DELAY_MS);
            }
        }
    }

    @Override
    public void onStreamStarted(int type, String streamUrl, Object extra) {
        if (mPlayPauseBtn != null) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
        }

        //mTipsHelper.hideTips();
        String liveState = classroomEngine.getLiveState();
        switch (type) {
            case CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER:
                if (classroomEngine.canIndividualByState()) {
                    mTimeProgressHelper.setTimeProgress(mCountTime,
                            individualStreamDuration,
                            liveState,
                            mIndividualName,
                            classroomEngine.getLiveState(),
                            true);
                }else {
                    mTimeProgressHelper.setTimeProgress(mCountTime, liveState);
                }
                break;
            case CTLConstant.StreamingType.PUBLISH_LIVE:
                mTimeProgressHelper.setTimeProgress(mCountTime, liveState);
                break;
            case CTLConstant.StreamingType.PUBLISH_INDIVIDUAL:

                individualStreamDuration = classroomEngine.getIndividualStreamDuration();

                mTimeProgressHelper.setTimeProgress(mCountTime,
                        individualStreamDuration,
                        liveState,
                        mIndividualName,
                        classroomEngine.getLiveState(),
                        true);
                break;
        }

    }

    @Override
    public void onStreamSizeChanged(BaseMediaView v, int videoW, int videoH) {
        if (v != null && v.getId() == mPlayVideoView.getId() && videoW > 0 && videoH > 0) {
            setPlayVideoParams(videoW, videoH);
        }
    }

    @Override
    public void onStreamStopped(int type, Object extra) {

        if (XiaojsConfig.DEBUG) {
            Logger.d("onStreamStopped********************************");
        }

        String liveState = classroomEngine.getLiveState();
        setControllerBtnStyle(liveState);
        //mTipsHelper.setTipsByStateOnStrop(liveState);

        switch (type) {
            case CTLConstant.StreamingType.PUBLISH_LIVE:

                mCountTime = mTimeProgressHelper.getCountTime();
                mTimeProgressHelper.setTimeProgress(mCountTime, liveState, false);

                if (extra != null
                        && extra instanceof String
                        && VideoController.STREAM_EXPIRED.equals((String) extra)) {

                    if (exitHandler != null) {
                        exitHandler.removeMessages(MSG_WHAT_EXIT_PUBLISH);
                        exitHandler.sendEmptyMessageDelayed(MSG_WHAT_EXIT_PUBLISH, EXIT_DELAY_MS);
                    }
                    Toast.makeText(mContext, R.string.cr_ex_live_end, Toast.LENGTH_SHORT).show();
                }

                break;
            case CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER:


                if (Live.LiveSessionState.LIVE.equals(liveState)
                        || Live.LiveSessionState.DELAY.equals(liveState)) {

                    if (classroomEngine.hasTeachingAbility()) {
                        mCountTime = mTimeProgressHelper.getCountTime();
                        mTimeProgressHelper.setTimeProgress(mCountTime, liveState, false);
                        mPlayVideoView.setVisibility(View.GONE);
                    } else {

                        if (extra != null && STREAM_MEDIA_CLOSED.equals((String) extra)) {
                            exitCurrentFragmentPeerToPeer();
                        } else {
                            exitCurrentFragment();
                        }
                    }
                } else {


                    if (Live.LiveSessionState.INDIVIDUAL.equals(liveState)
                            || classroomEngine.liveShow()) {
                        //mCountTime = mTimeProgressHelper.getCountTime();
                        //mTimeProgressHelper.setTimeProgress(mCountTime, liveState, false);
                        mPlayVideoView.setVisibility(View.GONE);
                    } else {

                        if (extra != null && STREAM_MEDIA_CLOSED.equals((String) extra)) {
                            exitCurrentFragmentPeerToPeer();
                        } else {
                            exitCurrentFragment();
                        }
                    }
                }

                break;
            case CTLConstant.StreamingType.PUBLISH_INDIVIDUAL:

                if (XiaojsConfig.DEBUG) {
                    Logger.d("onStreamStopped*********PUBLISH_INDIVIDUAL********ffff***************");
                }

                //mIndividualStreamDuration = mTimeProgressHelper.getIndividualStreamDuration();
                //mTimeProgressHelper.setTimeProgress(mCountTime, mIndividualStreamDuration, liveState, mIndividualName, false);
                if (extra instanceof String && VideoController.STREAM_EXPIRED.equals((String) extra)) {
                    Toast.makeText(mContext, R.string.cr_individual_end, Toast.LENGTH_SHORT).show();
//                    mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
//                    mPlayPauseBtn.setVisibility(View.VISIBLE);
                    exitCurrentFragment();
                } else {
                    exitCurrentFragment();
                }
                break;
            case CTLConstant.StreamingType.PLAY_PEER_TO_PEER:
                if (exitHandler != null) {
                    exitHandler.removeMessages(MSG_WHAT_EXIT_PUBLISH);
                    exitHandler.sendEmptyMessageDelayed(MSG_WHAT_EXIT_PUBLISH, EXIT_DELAY_MS);
                }
                break;
        }

    }

    private void sendExceptionNotify(@StringRes int tips) {
        Intent i = new Intent(CTLConstant.ACTION_STREAMING_EXCEPTION);
        i.putExtra(CTLConstant.EXTRA_EXCEPTION_TIPS, getString(tips));
        mContext.sendBroadcast(i);
    }

    @Override
    public void onStreamException(final StreamingState errorCode, final int type, final Object extra) {


        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        switch (errorCode) {
                            case OPEN_CAMERA_FAIL:
                                showNoCameraPermissDlg(type);
                                break;
                            case DISCONNECTED:
                                if (extra != null && extra instanceof Integer) {
                                    int flag = (int)extra;
                                    if (flag == LiveRecordView.STREAMING_TIMEOUT) {
                                        exitCurrentFragment();
                                        sendExceptionNotify(R.string.streaming_occur_exception);
                                    }
                                }
                                break;
                            default:
                                exitCurrentFragment();
                                sendExceptionNotify(R.string.streaming_occur_exception);

                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


    }

    public void showNoCameraPermissDlg(final int type) {

        if (noPermissionDlg == null) {
            noPermissionDlg =new CommonDialog(mContext);
            noPermissionDlg.setDesc(getString(R.string.permission_rationale_camera_audio_tip));
            noPermissionDlg.setRightBtnText(R.string.go_to_authorization);
            noPermissionDlg.setCancelable(false);
        }

        noPermissionDlg.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                //引导用户至设置页手动授权
                noPermissionDlg.dismiss();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);

                if (type == CTLConstant.StreamingType.PUBLISH_INDIVIDUAL) {
                    pauseIndividual(true);
                }

            }
        });
        noPermissionDlg.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                noPermissionDlg.dismiss();

                Toast.makeText(mContext, "没有允许相机或麦克风权限，您无法直播", Toast.LENGTH_SHORT).show();

                if (type == CTLConstant.StreamingType.PUBLISH_INDIVIDUAL) {
                    pauseIndividual(true);
                }



            }
        });

        if (!noPermissionDlg.isShowing()) {
            noPermissionDlg.show();
        }

    }

    private void updateTitle() {
        mLessonTitle.setText(getLessonTitle());
    }


    @Override
    public void onStreamingQualityChanged(StreamingQuality streamingQuality) {

        //TODO
    }

    private void updateFinishStreamingView() {
        mVideoController.pausePublishStream(CTLConstant.StreamingType.PUBLISH_LIVE);
        mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
        mFinishBtn.setVisibility(View.INVISIBLE);
    }

    private void handOnBackPressed() {
        if (mHandKeyPressing) {
            return;
        }

        mHandKeyPressing = true;
        String liveState = classroomEngine.getLiveState();
        if (Live.LiveSessionState.LIVE.equals(liveState) && mPublishType == CTLConstant.StreamingType.PUBLISH_LIVE) {
            if (classroomEngine.getClassroomType() == ClassroomType.ClassLesson) {
                //exitCurrentFragment();
                showBackDlgForClasslesson();
            } else {

                if (classroomEngine.one2one()) {
                    sendCloseMedia();
                }

                pauseClass(true);
            }
        } else if (classroomEngine.liveShow()) {
            //pause and exit

            if (classroomEngine.one2one()) {
                sendCloseMedia();
            }

            pauseIndividual(true);
        } else if ((Live.LiveSessionState.LIVE.equals(liveState)
                || Live.LiveSessionState.IDLE.equals(liveState)
                || Live.LiveSessionState.FINISHED.equals(liveState)
                || Live.LiveSessionState.SCHEDULED.equals(liveState))
                && mPublishType == CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER) {

            if (classroomEngine.one2one()) {
                sendCloseMedia();
            }

            exitCurrentFragmentPeerToPeer();
        } else {

            if (classroomEngine.one2one()) {
                sendCloseMedia();
            }

            exitCurrentFragment();
        }
    }

    private void showBackDlgForClasslesson() {

        final CommonDialog dialog = new CommonDialog(mContext);
        //dialog.setTitle(R.string.finish_classroom);
        dialog.setDesc(R.string.exit_live_tips);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                mHandKeyPressing = false;
            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();

                if (classroomEngine.one2one()) {
                    sendCloseMedia();
                }

                exitCurrentFragment();
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mHandKeyPressing = false;
            }
        });

        dialog.show();
    }


    private void updateLandViewStyle() {
        mScreenshotLandBtn.setVisibility(View.VISIBLE);
        mScreenshotPortraitBtn.setVisibility(View.GONE);
    }

    private void updatePortraitViewStyle() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        mSlideViewWidth = (int) (0.4F * dm.heightPixels);
        mSlideViewHeight = dm.heightPixels - (int) (dm.widthPixels / Constants.VIDEO_VIEW_RATIO);
        mScreenshotLandBtn.setVisibility(View.GONE);
        mScreenshotPortraitBtn.setVisibility(View.VISIBLE);
    }

    /**
     * 开启或暂停课
     *
     * @see #
     */
    private void playOrPauseLesson() {
        //Prevent frequent clicks
        if (System.currentTimeMillis() - mPlayOrPausePressTime < BTN_PRESS_INTERVAL) {
            return;
        }
        mPlayOrPausePressTime = System.currentTimeMillis();

        //mTipsHelper.hideTips();

        //如果存在1对1 需要发送closemedia给服务器
        if (classroomEngine.one2one()) {
            sendCloseMedia();
        }


        if (classroomEngine.liveShow()) {
            pauseIndividual(true);
            return;
        }

        String liveState = classroomEngine.getLiveState();
        if (Live.LiveSessionState.LIVE.equals(liveState)) {
            if (mPublishType == CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER) {
                //pause and exit
                exitCurrentFragmentPeerToPeer();
            } else {
                AnalyticEvents.onEvent(mContext, 58);
                //pause
                pauseClass(true);
            }
        } else if (mPublishType == CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER
                && (Live.LiveSessionState.LIVE.equals(liveState)
                || Live.LiveSessionState.IDLE.equals(liveState)
                || Live.LiveSessionState.FINISHED.equals(liveState)
                || Live.LiveSessionState.SCHEDULED.equals(liveState))) {

            exitCurrentFragmentPeerToPeer();
        } else if (Live.LiveSessionState.RESET.equals(liveState)) {
            //resume
            resumeClass();
        } else if (Live.LiveSessionState.INDIVIDUAL.equals(liveState)
                || classroomEngine.liveShow()
                || Live.LiveSessionState.IDLE.equals(liveState)) {
            //pause and exit
            mHandKeyPressing = true;
            pauseIndividual(true);
        } else if (ClassroomBusiness.canIndividualByState(liveState)) {
            individualPublishStream();
        }
    }

    @Override
    protected void setControllerBtnStyle(String liveState) {
        if (mPlayPauseBtn == null || mFinishBtn == null) {
            return;
        }

        if (Live.LiveSessionState.INDIVIDUAL.equals(liveState)
                || classroomEngine.liveShow()) {
            mPlayPauseBtn.setVisibility(View.VISIBLE);
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
            return;
        }

        if (classroomEngine.canIndividualByState()) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
            mPlayPauseBtn.setVisibility(View.VISIBLE);
            mFinishBtn.setVisibility(View.INVISIBLE);
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState)
                || Live.LiveSessionState.RESET.equals(liveState)
                || Live.LiveSessionState.PENDING_FOR_LIVE.equals(liveState)) {
            //boolean isPrivateClass = mCtlSession.cls != null;
            //if (ClassroomBusiness.hasTeachingAbility() && !isPrivateClass) {
            if (classroomEngine.hasTeachingAbility()) {
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_start);
                mPlayPauseBtn.setVisibility(View.VISIBLE);
            } else {
                mPlayPauseBtn.setVisibility(View.INVISIBLE);
            }

            if (classroomEngine.hasTeachingAbility()) {
                mFinishBtn.setVisibility(View.VISIBLE);
            }
        } else if (Live.LiveSessionState.LIVE.equals(liveState)) {
            boolean isPrivateClass = mCtlSession.cls != null;
            if (classroomEngine.hasTeachingAbility() && !isPrivateClass) {
                mPlayPauseBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
            } else {
                mPlayPauseBtn.setVisibility(View.INVISIBLE);
            }

            if (classroomEngine.hasTeachingAbility()) {
                mFinishBtn.setVisibility(View.VISIBLE);
            }
        } else if (Live.LiveSessionState.DELAY.equals(liveState)) {
            mPlayPauseBtn.setVisibility(View.INVISIBLE);
            mFinishBtn.setVisibility(View.VISIBLE);
        }
//        } else if (Live.LiveSessionState.INDIVIDUAL.equals(liveState)
//                || classroomEngine.liveShow()) {
//            mPlayPauseBtn.setVisibility(View.VISIBLE);
//            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
//        }
    }

    private void pauseClass(final boolean withExitFragment) {
        //私有班课不能课间休息
        if (classroomEngine.getClassroomType() == ClassroomType.ClassLesson) {
            return;
        }

        showProgress(true);
        classroomEngine.pauseClass(mTicket, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                cancelProgress();
                mHandKeyPressing = false;
                setControllerBtnStyle(Live.LiveSessionState.RESET);
                if (withExitFragment) {
                    exitCurrentFragment();
                } else {
                    mVideoController.pausePublishStream(CTLConstant.StreamingType.PUBLISH_LIVE);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                mHandKeyPressing = false;
                if (withExitFragment) {
                    exitCurrentFragment();
                } else {
                    mVideoController.pausePublishStream(CTLConstant.StreamingType.PUBLISH_LIVE);
                }
            }
        });
    }

    private void resumeClass() {
        showProgress(true);

        classroomEngine.resumeClass(mTicket, new APIServiceCallback<ClassResponse>() {
            @Override
            public void onSuccess(ClassResponse object) {
                cancelProgress();
                setControllerBtnStyle(Live.LiveSessionState.LIVE);
                mVideoController.publishStream(CTLConstant.StreamingType.PUBLISH_LIVE, object != null ? object.publishUrl : null);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void finishClass(final boolean withFragment) {
        showProgress(true);

        classroomEngine.finishClass(mTicket, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                cancelProgress();
                if (withFragment) {
                    exitCurrentFragment();
                } else {
                    updateFinishStreamingView();
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private FinishClassResponse getFinishClassResponse(ResponseBody body) {
        FinishClassResponse response = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = body.string();
            response = mapper.readValue(json, FinishClassResponse.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }

    private void showFinishDialog() {
        if (mFinishDialog == null) {
            mFinishDialog = new CommonDialog(mContext);
            mFinishDialog.setTitle(R.string.finish_classroom);
            mFinishDialog.setDesc(R.string.finish_classroom_tips);
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
                    finishClass(true);
                }
            });
        }

        mFinishDialog.show();
    }

    private void pauseIndividual(boolean withExitFragment) {

        if (withExitFragment) {
            mVideoController.setCancelPublish(true);
            exitCurrentFragment();
        } else {
            mVideoController.pausePublishStream(CTLConstant.StreamingType.PUBLISH_INDIVIDUAL);
        }
        mHandKeyPressing = false;
    }


    private void exitCurrentFragmentPeerToPeer() {

        if (XiaojsConfig.DEBUG) {
            Logger.d("exitCurrentFragmentPeerToPeer()......");
        }

        classroomEngine.setOne2one(false);

        if (mAlreadyExitFragment) {
            mHandKeyPressing = true;
            return;
        }

        mAlreadyExitFragment = true;
        Bundle data = new Bundle();
        data.putInt(Constants.KEY_FROM, Constants.FROM_PUBLISH_FRAGMENT);

        mCountTime = mTimeProgressHelper.getCountTime();
        data.putLong(Constants.KEY_COUNT_TIME, mCountTime);

        data.putLong(Constants.KEY_INDIVIDUAL_DURATION, mTimeProgressHelper.getIndividualStreamDuration());

        data.putString(Constants.KEY_PLAY_URL, mCtlSession.playUrl);

        ClassroomController.getInstance(mContext).enterPlayFragment(data, true);

        mHandKeyPressing = true;
    }


    private void exitCurrentFragment() {

        if (XiaojsConfig.DEBUG) {
            Logger.d("exitCurrentFragment()......");
        }

        classroomEngine.setOne2one(false);

        if (mAlreadyExitFragment) {
            mHandKeyPressing = true;
            return;
        }

        mAlreadyExitFragment = true;
        Bundle data = new Bundle();
        data.putInt(Constants.KEY_FROM, Constants.FROM_PUBLISH_FRAGMENT);
        switch (mPublishType) {
            case CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER:
                if (XiaojsConfig.DEBUG) {
                    Logger.d("exitCurrentFragment()....per..");
                }
                data.putLong(Constants.KEY_INDIVIDUAL_DURATION, mTimeProgressHelper.getIndividualStreamDuration());
                break;
            case CTLConstant.StreamingType.PUBLISH_LIVE:

                mCountTime = mTimeProgressHelper.getCountTime();
                data.putLong(Constants.KEY_COUNT_TIME, mCountTime);
                break;
            case CTLConstant.StreamingType.PUBLISH_INDIVIDUAL:

                Logger.d("exitCurrentFragment()....PUBLISH_INDIVIDUAL..");

                mCountTime = mTimeProgressHelper.getCountTime();
                data.putLong(Constants.KEY_COUNT_TIME, mCountTime);

                Logger.d("exitCurrentFragment()....PUBLISH_INDIVIDUAL..:" + mCountTime);
                break;
            default:
                break;
        }
        ClassroomController.getInstance(mContext).enterPlayFragment(data, true);

        mHandKeyPressing = true;
    }

    private void onPlayVideoViewClick() {
        final LiveMenu menu = new LiveMenu(mContext, Gravity.BOTTOM);
        int size[] = getPlayVideoViewSize(mNormalVideoWidth, mNormalVideoHeight);
        menu.show(mPlayVideoView, size[0], size[1]);
        menu.setOnItemClickListener(new LiveMenu.OnItemClickListener() {
            @Override
            public void onScale() {
                //scale();
                mScaled = !mScaled;
                setPlayVideoParams(mNormalVideoWidth, mNormalVideoHeight);
            }

            @Override
            public void onAudio() {
                // mute();
            }

            @Override
            public void onVideoClose() {
                if (classroomEngine.hasTeachingAbility()
                        || classroomEngine.liveShow()
                        || Live.LiveSessionState.INDIVIDUAL.equals(classroomEngine.getLiveState())) {
                    mVideoController.pausePlayStream(CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER);
                    sendCloseMedia();
                } else {
                    mVideoController.pausePublishStream(CTLConstant.StreamingType.PUBLISH_PEER_TO_PEER);
                }

            }
        });
    }

    @TargetApi(17)
    private void setPlayVideoParams(int videoW, int videoH) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mPlayVideoView.getLayoutParams();
        if (mScaled) {
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;
            params.leftMargin = 0;
            params.bottomMargin = 0;
            params.removeRule(RelativeLayout.ABOVE);
            mPublishCameraSwitcher.setVisibility(View.GONE);
            mPlayVideoView.setBackgroundColor(Color.BLACK);
            //mClosePlayFullScreenImg.setVisibility(View.VISIBLE);
        } else {
            mPlayVideoView.setBackgroundColor(Color.TRANSPARENT);

            mPublishCameraSwitcher.setVisibility(View.VISIBLE);
            //mClosePlayFullScreenImg.setVisibility(View.GONE);
            mNormalVideoWidth = videoW;
            mNormalVideoHeight = videoH;
            if (videoW > videoH) {
                int w = mContext.getResources().getDimensionPixelOffset(R.dimen.px200);
                params.width = w;
                params.height = (int) (w / PLAY_VIDEO_RATION);
            } else {
                int h = mContext.getResources().getDimensionPixelOffset(R.dimen.px200);
                params.height = h;
                params.width = (int) (h / PLAY_VIDEO_RATION);
            }
            int size[] = getPlayVideoViewSize(videoW, videoH);
            params.width = size[0];
            params.height = size[1];
            int margin = mContext.getResources().getDimensionPixelOffset(R.dimen.px10);
            params.leftMargin = margin;
            params.bottomMargin = margin;
            params.addRule(RelativeLayout.ABOVE, R.id.discussion_list_view);
        }
    }

    private int[] getPlayVideoViewSize(int videoW, int videoH) {
        int w = 0;
        int h = 0;
        int size[] = new int[2];
        if (videoW > videoH) {
            w = mContext.getResources().getDimensionPixelOffset(R.dimen.px200);
            h = (int) (w / PLAY_VIDEO_RATION);
        } else {
            h = mContext.getResources().getDimensionPixelOffset(R.dimen.px200);
            w = (int) (h / PLAY_VIDEO_RATION);
        }

        size[0] = w;
        size[1] = h;
        return size;
    }

    @Override
    public void onExitTalk(int type) {
        if (type == TalkManager.TYPE_PEER_TALK) {
            TalkManager.getInstance().setPeekTalkingAccount(null);
        }
    }

    @Override
    public void onMsgChanged(boolean receive, int criteria, TalkItem talkItem) {
        //fullscreen mode
        if (mDiscussionListView.getVisibility() == View.VISIBLE) {
            TalkManager.getInstance().resetMultiTalkUnreadMsgCount();
        } else {
            mHideShowTalkBtn.setCount(TalkManager.getInstance().getMultiTalkUnreadMsgCount());
        }

        mContactBtn.setCount(TalkManager.getInstance().getPeerTalkUnreadMsgCount());
        if (mHideShowTalkBtn.getVisibility() != View.VISIBLE) {
            startAnim();
        }
    }

    @Override
    public void onSocketConnectChanged(boolean connected) {
        if (connected) {
            if (mFullScreenTalkPresenter != null) {
                mFullScreenTalkPresenter.switchFullMultiTalk();
            }
        }
    }

    @Override
    public void onTalkItemClick() {
        startAnim();
    }


    private Handler exitHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_WHAT_EXIT_PUBLISH:
                    exitCurrentFragment();
                    break;
                default:
                    break;
            }
        }
    };

}
