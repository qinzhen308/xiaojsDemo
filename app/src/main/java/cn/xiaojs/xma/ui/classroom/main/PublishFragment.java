package cn.xiaojs.xma.ui.classroom.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.SyncStateResponse;
import cn.xiaojs.xma.ui.classroom.live.PublishVideoController;
import cn.xiaojs.xma.ui.classroom.live.StreamType;
import cn.xiaojs.xma.ui.classroom.live.VideoController;
import cn.xiaojs.xma.ui.classroom.live.view.BaseMediaView;
import cn.xiaojs.xma.ui.classroom.live.view.LiveMenu;
import cn.xiaojs.xma.ui.classroom.live.view.LiveRecordView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.talk.OnAttendItemClick;
import cn.xiaojs.xma.ui.classroom.talk.TalkManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkPresenter;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.ui.widget.SheetFragment;
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
 * Date:2017/5/3
 * Desc:
 *
 * ======================================================================================== */

public class PublishFragment extends ClassroomLiveFragment {
    private final static float PLAY_VIDEO_RATION = 16 / 9.0f;

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
    private int mPublishType = StreamType.TYPE_STREAM_PUBLISH;
    private String mPlayUrl = "";
    private String mPublishUrl = "";

    private CommonDialog mFinishDialog;
    private boolean mScaled = false;
    private int mNormalVideoWidth;
    private int mNormalVideoHeight;

    private boolean mHandKeyPressing = false;
    private boolean mAlreadyExitFragment = false;

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
                if (isPortrait()) {
                    mClassroomController.openSetting(this, SheetFragment.SHEET_GRAVITY_BOTTOM, mSlideViewHeight, this);
                } else {
                    mClassroomController.openSetting(this, SheetFragment.SHEET_GRAVITY_RIGHT, mSlideViewWidth, this);
                }
                break;
            case R.id.fc_open_contact_btn:
                if (isPortrait()) {
                    mClassroomController.openContact(this, mCtlSession, SheetFragment.SHEET_GRAVITY_BOTTOM, mSlideViewHeight);
                } else {
                    mClassroomController.openContact(this, mCtlSession, SheetFragment.SHEET_GRAVITY_RIGHT, mSlideViewWidth);
                }
                break;
            case R.id.fc_land_portrait_btn:
                toggleLandPortrait();
                break;
            case R.id.fc_screenshot_land_btn:
            case R.id.fc_screenshot_portrait_btn:
                //ClassroomController.getInstance().enterPhotoDoodleByBitmap(null);
                mVideoController.takeVideoFrame(this);
                break;
            case R.id.fc_hide_show_talk_btn:
                showHideTalk();
                break;
            case R.id.fc_open_talk_btn:
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
            //int targetOrientation = isPortrait() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
            //mVideoController.togglePublishOrientation(targetOrientation, null);

            if (mPublishType == StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL) {
                int targetOrientation = isPortrait() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                mVideoController.togglePublishOrientation(targetOrientation, null);
            } else {
                mVideoController.pausePublishStream(mPublishType);
                int targetOrientation = isPortrait() ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                mVideoController.togglePublishOrientation(targetOrientation, new LiveRecordView.OnStreamOrientationListener() {
                    @Override
                    public void onStreamOrientationChanged(int orientation) {
                        mVideoController.publishStream(mPublishType, mPublishUrl);
                    }
                });
            }
        }

        super.toggleLandPortrait();
    }

    @Override
    protected void initView() {
        mPublishVideoView.setTouchConsume(false);

        updatePortraitViewStyle();
    }

    @Override
    protected void initData() {
        Bundle data = getArguments();
        mCountTime = ClassroomBusiness.getCountTimeByCtlSession();
        if (data != null) {
            mPublishType = data.getInt(Constants.KEY_PUBLISH_TYPE, StreamType.TYPE_STREAM_PUBLISH);
            mPublishUrl = data.getString(Constants.KEY_PUBLISH_URL, "");
            mPlayUrl = data.getString(Constants.KEY_PLAY_URL, "");

            //get individual info
            mOriginSteamState = data.getString(Constants.KEY_BEFORE_LIVE_STATE, "");
            mIndividualResponseBody = (StreamingResponse) data.getSerializable(Constants.KEY_INDIVIDUAL_RESPONSE);
            mIndividualStreamDuration = data.getLong(Constants.KEY_INDIVIDUAL_DURATION, 0);
            mIndividualName = data.getString(Constants.KEY_INDIVIDUAL_NAME, "");

            //get count time
            int from = data.getInt(Constants.KEY_FROM, Constants.FROM_ACTIVITY);
            if (from == Constants.FROM_PLAY_FRAGMENT) {
                mCountTime = data.getLong(Constants.KEY_COUNT_TIME, mCountTime);
            }
        }

        mLessonTitle.setText(!TextUtils.isEmpty(mCtlSession.titleOfPrimary) ? mCtlSession.titleOfPrimary : mCtlSession.ctl.title);
        if (mFullScreenTalkPresenter == null) {
            mFullScreenTalkPresenter = new TalkPresenter(mContext, mDiscussionListView, null);
            mFullScreenTalkPresenter.switchFullMultiTalk();
        }

        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        switch (mPublishType) {
            case StreamType.TYPE_STREAM_PUBLISH:
                if (Live.LiveSessionState.LIVE.equals(liveState)) {
                    mVideoController.publishStream(StreamType.TYPE_STREAM_PUBLISH, mPublishUrl);
                } else {
                    playOrPauseLesson();
                }
                break;
            case StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL:
                mVideoController.publishStream(StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL, mPublishUrl);
                break;
            case StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER:
                mVideoController.publishStream(StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER, mPublishUrl);
                mVideoController.playStream(StreamType.TYPE_STREAM_PLAY_PEER_TO_PEER, mPlayUrl);
                break;
        }

        mTimeProgressHelper.setTimeProgress(mCountTime, mIndividualStreamDuration, liveState, mIndividualName, mOriginSteamState, false);
        setControllerBtnStyle(liveState);

        mContactBtn.setCount(TalkManager.getInstance().getPeerTalkUnreadMsgCount());
        postHideAnim();
    }

    @Override
    protected void startAnim() {
        if (isAnimating() || mContent == null) {
            return;
        }

        if (mTopPanel.getVisibility() == View.VISIBLE) {
            hideAnim(mTopPanel, "mTopPanel");
            hideAnim(mContactBtn, "mFullscreenContactBtn");
            hideAnim(mOpenTalkBtn, "mOpenTalkBtn");
            hideAnim(mLandPortraitBtn, "mLandPortraitBtn");
            hideAnim(mHideShowTalkBtn, "mHideShowTalkBtn");
        } else if (mTopPanel.getVisibility() == View.INVISIBLE) {
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
    protected void onIndividualPublishCallback(StreamingResponse response) {
        if (response == null) {
            return;
        }

        LiveCtlSessionManager.getInstance().updateCtlSessionState(Live.LiveSessionState.INDIVIDUAL);
        setControllerBtnStyle(Live.LiveSessionState.INDIVIDUAL);
        mVideoController.publishStream(StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL, response.publishUrl);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {
            switch (requestCode) {
                case ClassroomController.REQUEST_INPUT:
                    String content = data.getStringExtra(Constants.KEY_MSG_INPUT_TXT);
                    if (mFullScreenTalkPresenter != null && !TextUtils.isEmpty(content)) {
                        mHideShowTalkBtn.setImageResource(R.drawable.ic_cr_hide_talk);
                        mDiscussionListView.setVisibility(View.VISIBLE);
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
                                ClassroomController.getInstance().openSlideTalk(this, attendee, mCtlSession, size);
                            } else {
                                ClassroomController.getInstance().openSlideTalk(this, attendee, mCtlSession, gravity, size);
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        handOnBackPressed();
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
    public void onStreamStarted(int type, Object extra) {
        mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
        mTipsHelper.hideTips();
        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        switch (type) {
            case StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER:
            case StreamType.TYPE_STREAM_PUBLISH:
                mTimeProgressHelper.setTimeProgress(mCountTime, liveState);
                break;
            case StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL:
                try {
                    mIndividualStreamDuration = (long) extra;
                } catch (Exception e) {
                }
                mTimeProgressHelper.setTimeProgress(mCountTime, mIndividualStreamDuration, liveState, mIndividualName, mOriginSteamState, true);
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
        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        setControllerBtnStyle(liveState);
        mTipsHelper.setTipsByStateOnStrop(liveState);

        switch (type) {
            case StreamType.TYPE_STREAM_PUBLISH:
            case StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER:
                mCountTime = mTimeProgressHelper.getCountTime();
                mTimeProgressHelper.setTimeProgress(mCountTime, liveState, false);
                if (type == StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER) {
                    mPlayVideoView.setVisibility(View.GONE);
                }
                break;
            case StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL:
                //mIndividualStreamDuration = mTimeProgressHelper.getIndividualStreamDuration();
                //mTimeProgressHelper.setTimeProgress(mCountTime, mIndividualStreamDuration, liveState, mIndividualName, false);
                if (extra instanceof String && VideoController.STREAM_EXPIRED.equals((String) extra)) {
                    Toast.makeText(mContext, R.string.cr_individual_end, Toast.LENGTH_SHORT).show();
                    LiveCtlSessionManager.getInstance().updateCtlSessionState(mOriginSteamState);
                    mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
                    mPlayPauseBtn.setVisibility(View.VISIBLE);
                } else {
                    LiveCtlSessionManager.getInstance().updateCtlSessionState(mOriginSteamState);
                    exitCurrentFragment();
                }

                break;
        }

    }

    @Override
    protected void onSyncStateChanged(SyncStateResponse syncState) {
        boolean autoCountTime = false;
        if (Live.LiveSessionState.SCHEDULED.equals(syncState.from) && Live.LiveSessionState.PENDING_FOR_JOIN.equals(syncState.to)) {
            LiveCtlSessionManager.getInstance().updateCtlSessionState(Live.LiveSessionState.PENDING_FOR_JOIN);
            setControllerBtnStyle(Live.LiveSessionState.PENDING_FOR_JOIN);
            autoCountTime = true;
        } else if (Live.LiveSessionState.LIVE.equals(syncState.from) && Live.LiveSessionState.RESET.equals(syncState.to)) {
            setControllerBtnStyle(Live.LiveSessionState.RESET);
            autoCountTime = false;
            mCountTime = syncState.timeline != null ? syncState.timeline.hasTaken : 0;
        } else if (Live.LiveSessionState.LIVE.equals(syncState.from) && Live.LiveSessionState.DELAY.equals(syncState.to)) {
            LiveCtlSessionManager.getInstance().updateCtlSessionState(Live.LiveSessionState.DELAY);
            setControllerBtnStyle(Live.LiveSessionState.DELAY);
            autoCountTime = true;
            mCountTime = 0;
        } else if (Live.LiveSessionState.DELAY.equals(syncState.from) && Live.LiveSessionState.FINISHED.equals(syncState.to)) {
            LiveCtlSessionManager.getInstance().updateCtlSessionState(Live.LiveSessionState.FINISHED);
            setControllerBtnStyle(Live.LiveSessionState.FINISHED);
            autoCountTime = true;
        }

        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        mTimeProgressHelper.setTimeProgress(mCountTime, liveState, autoCountTime);
        if (mTipsHelper != null) {
            mTipsHelper.hideTips();
        }
    }

    private void handOnBackPressed() {
        if (mHandKeyPressing) {
            return;
        }

        mHandKeyPressing = true;
        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        if (Live.LiveSessionState.LIVE.equals(liveState) && mPublishType == StreamType.TYPE_STREAM_PUBLISH) {
            pauseClass(true);
        } else if (Live.LiveSessionState.INDIVIDUAL.equals(liveState)) {
            //pause and exit
            pauseIndividual(true);
        } else {
            exitCurrentFragment();
        }
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
     * @see #mSyncStateListener
     */
    private void playOrPauseLesson() {
        mTipsHelper.hideTips();
        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        if (Live.LiveSessionState.LIVE.equals(liveState)) {
            if (mPublishType == StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER) {
                //pause and exit

            } else {
                //pause
                pauseClass(false);
            }
        } else if (Live.LiveSessionState.RESET.equals(liveState)) {
            //resume
            resumeClass();
        } else if (Live.LiveSessionState.INDIVIDUAL.equals(liveState)) {
            //pause and exit
            mHandKeyPressing = true;
            pauseIndividual(true);
        } else if (Live.LiveSessionState.FINISHED.equals(liveState)) {
            individualPublishStream();
        }
    }

    @Override
    protected void setControllerBtnStyle(String liveState) {
        if (mPlayPauseBtn == null || mFinishBtn == null) {
            return;
        }

        if (Live.LiveSessionState.SCHEDULED.equals(liveState) ||
                Live.LiveSessionState.FINISHED.equals(liveState)) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
            mPlayPauseBtn.setVisibility(View.VISIBLE);
            mFinishBtn.setVisibility(View.INVISIBLE);
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState) ||
                Live.LiveSessionState.RESET.equals(liveState)) {
            if (mUserMode == Constants.UserMode.TEACHING) {
                mFinishBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_start);
                mPlayPauseBtn.setVisibility(View.VISIBLE);
            } else {
                mPlayPauseBtn.setVisibility(View.INVISIBLE);
            }
        } else if (Live.LiveSessionState.LIVE.equals(liveState)) {
            if (mUserMode == Constants.UserMode.TEACHING) {
                mFinishBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
            } else {
                mPlayPauseBtn.setVisibility(View.INVISIBLE);
            }
        } else if (Live.LiveSessionState.DELAY.equals(liveState)) {
            mPlayPauseBtn.setVisibility(View.INVISIBLE);
            mFinishBtn.setVisibility(View.VISIBLE);
        } else if (Live.LiveSessionState.INDIVIDUAL.equals(liveState)) {
            mPlayPauseBtn.setVisibility(View.VISIBLE);
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
        }
    }

    private void pauseClass(final boolean withExitFragment) {
        showProgress(true);
        LiveManager.pauseClass(mContext, mTicket, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                cancelProgress();
                mHandKeyPressing = false;
                LiveCtlSessionManager.getInstance().updateCtlSessionState(Live.LiveSessionState.RESET);
                setControllerBtnStyle(Live.LiveSessionState.RESET);
                if (withExitFragment) {
                    exitCurrentFragment();
                } else {
                    mVideoController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                mHandKeyPressing = false;
                if (withExitFragment) {
                    exitCurrentFragment();
                } else {
                    mVideoController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH);
                }
            }
        });
    }

    private void resumeClass() {
        showProgress(true);
        LiveManager.resumeClass(mContext, mTicket, Live.StreamMode.MUTE, new APIServiceCallback<ClassResponse>() {
            @Override
            public void onSuccess(ClassResponse object) {
                cancelProgress();
                LiveCtlSessionManager.getInstance().updateCtlSessionState(Live.LiveSessionState.LIVE);
                setControllerBtnStyle(Live.LiveSessionState.LIVE);
                mVideoController.publishStream(StreamType.TYPE_STREAM_PUBLISH, object != null ? object.publishUrl : null);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
            }
        });
    }

    private void finishClass() {
        showProgress(true);
        LiveManager.finishClass(mContext, mTicket, new APIServiceCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody object) {
                cancelProgress();
                LiveCtlSessionManager.getInstance().updateCtlSessionState(Live.LiveSessionState.FINISHED);
                mVideoController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH);
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
                mFinishBtn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
            }
        });
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
                    finishClass();
                }
            });
        }

        mFinishDialog.show();
    }

    private void pauseIndividual(boolean withExitFragment) {
        LiveCtlSessionManager.getInstance().updateCtlSessionState(mOriginSteamState);
        if (withExitFragment) {
            mVideoController.setCancelPublish(true);
            exitCurrentFragment();
        } else {
            mVideoController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL);
        }
        mHandKeyPressing = false;
    }

    private void exitCurrentFragment() {
        if (mAlreadyExitFragment) {
            return;
        }

        mAlreadyExitFragment = true;
        Bundle data = new Bundle();
        data.putInt(Constants.KEY_FROM, Constants.FROM_PUBLISH_FRAGMENT);
        switch (mPublishType) {
            case StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER:
                data.putString(Constants.KEY_PLAY_URL, mPlayUrl);
                break;
            case StreamType.TYPE_STREAM_PUBLISH:
                data.putLong(Constants.KEY_COUNT_TIME, mCountTime);
                break;
            case StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL:
                data.putSerializable(Constants.KEY_INDIVIDUAL_RESPONSE, mIndividualResponseBody);
                break;
            default:
                break;
        }
        ClassroomController.getInstance().enterPlayFragment(data, true);
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
                //close();
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
            //mClosePlayFullScreenImg.setVisibility(View.VISIBLE);
        } else {
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
}
