package cn.xiaojs.xma.ui.classroom.main;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
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

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom.bean.OpenMediaNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.SyncStateResponse;
import cn.xiaojs.xma.ui.classroom.live.PlayVideoController;
import cn.xiaojs.xma.ui.classroom.live.StreamType;
import cn.xiaojs.xma.ui.classroom.live.view.BaseMediaView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.talk.EmbedTalkFragment;
import cn.xiaojs.xma.ui.classroom.talk.OnGetTalkListener;
import cn.xiaojs.xma.ui.classroom.talk.OnAttendItemClick;
import cn.xiaojs.xma.ui.classroom.talk.TalkManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkPresenter;
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

public class PlayFragment extends ClassroomLiveFragment implements OnGetTalkListener {
    public final static int MSG_MODE_INPUT = 1;
    public final static int FULL_SCREEN_MODE_INPUT = 2;

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
    MessageImageView mOpenContactBtn;
    @BindView(R.id.talk_send_txt_btn)
    ImageView mSendTxtBtn;
    @BindView(R.id.talk_send_other_btn)
    ImageView mSendOtherBtn;
    @BindView(R.id.talk_share_btn)
    ImageView mShareBtn;

    //full screen bottom panel
    @BindView(R.id.fc_bottom_panel)
    View mLandBottomPanel;
    @BindView(R.id.fc_land_portrait_btn)
    ImageView mLandPortraitBtn; //land/portrait toggle
    @BindView(R.id.fc_open_contact_btn)
    ImageView mContactBtn;
    @BindView(R.id.fc_screenshot_portrait_btn)
    ImageView mScreenshotPortraitBtn;
    @BindView(R.id.fc_screenshot_land_btn)
    ImageView mScreenshotLandBtn;
    @BindView(R.id.fc_hide_show_talk_btn)
    ImageView mHideShowTalkBtn;
    @BindView(R.id.fc_open_talk_btn)
    ImageView mOpenTalkBtn;


    private EmbedTalkFragment mEmbedTalkFragment;
    private int mVideoWidth;
    private int mVideoHeight;

    @Override
    protected void initParams() {
        super.initParams();
        mTipsHelper = new TipsHelper(mContext, mTipView);
        mTimeProgressHelper = new TimeProgressHelper(mContext, mLessonTimeInfo, mTimeStatusBar);
        mVideoController = new PlayVideoController(mContext, mPlayLayout, this);
    }

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_classroom_play, null);
    }

    @OnClick({R.id.back_btn, R.id.play_pause_btn, R.id.setting_btn, R.id.open_docs_btn, R.id.talk_open_contact_btn,
            R.id.talk_send_txt_btn, R.id.talk_send_other_btn, R.id.talk_share_btn, R.id.enter_full_screen, R.id.play_layout,
            R.id.fc_land_portrait_btn, R.id.fc_screenshot_land_btn, R.id.fc_screenshot_portrait_btn, R.id.fc_open_contact_btn,
            R.id.fc_hide_show_talk_btn, R.id.fc_open_talk_btn, R.id.play_video})
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
            case R.id.open_docs_btn:
                mClassroomController.openDocument(this, mCtlSession);
                break;
            case R.id.talk_open_contact_btn:
            case R.id.fc_open_contact_btn:
                if (isPortrait() || v.getId() == R.id.talk_open_contact_btn) {
                    mClassroomController.openContact(this, mCtlSession, SheetFragment.SHEET_GRAVITY_BOTTOM, mSlideViewHeight);
                } else {
                    mClassroomController.openContact(this, mCtlSession, SheetFragment.SHEET_GRAVITY_RIGHT, mSlideViewWidth);
                }
                break;
            case R.id.talk_send_txt_btn:
                mClassroomController.openInputText(this, MSG_MODE_INPUT);
                break;
            case R.id.talk_send_other_btn:
                mClassroomController.selectPic(this);
                break;
            case R.id.talk_share_btn:
                break;
            case R.id.enter_full_screen:
                //enter full screen
                enterFullScreen();
                break;
            case R.id.play_layout:
                startAnim();
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
                mClassroomController.openInputText(this, FULL_SCREEN_MODE_INPUT);
                break;
            case R.id.play_video:
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
        getChildFragmentManager().beginTransaction().add(R.id.talk_layout, mEmbedTalkFragment).commit();
    }

    @Override
    protected void initData() {
        mLessonTitle.setText(!TextUtils.isEmpty(mCtlSession.titleOfPrimary) ? mCtlSession.titleOfPrimary : mCtlSession.ctl.title);
        initCtlLive();

        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        updateViewStyleByLiveState(liveState);

        postHideAnim();
    }

    private void updateViewStyleByLiveState(String liveState) {
        int mode = LiveCtlSessionManager.getInstance().getLiveMode();
        if (Constants.PREVIEW_MODE == mode) {
            //预览模式, 不能有任何操作
            mPlayPauseBtn.setVisibility(View.INVISIBLE);
        } else {
            if (Live.LiveSessionState.SCHEDULED.equals(liveState)
                    || Live.LiveSessionState.FINISHED.equals(liveState)) {
                //课前课后
                mPlayPauseBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
            } else {
                if (mUserMode == Constants.UserMode.TEACHING) {
                    mPlayPauseBtn.setVisibility(View.VISIBLE);
                } else {
                    mPlayPauseBtn.setVisibility(View.INVISIBLE);
                }
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

        if (isPortrait && ClassroomController.getInstance().getPlayFragmentMode() == ClassroomController.MODE_FRAGMENT_PLAY_TALK) {
            updatePortraitPlayViewStyle();
        } else {
            updateFullScreenPlayViewStyle(isPortrait);
        }

        postHideAnim();
    }

    private void enterFullScreen() {
        ClassroomController.getInstance().enterLandFullScreen(isPortrait(), mContext);
        if (mFullScreenTalkPresenter == null) {
            mFullScreenTalkPresenter = new TalkPresenter(mContext, mDiscussionListView, null);
            mDiscussionListView.setVisibility(View.VISIBLE);
            mFullScreenTalkPresenter.switchFullMultiTalk();
        }
    }

    private void handOnBackPressed() {
        if (isPortrait()) {
            if (ClassroomController.getInstance().isFragmentPlayFullScreen()) {
                updatePortraitPlayViewStyle();
                ClassroomController.getInstance().exitFullScreen(mContext, false);
            } else {
                ClassroomController.getInstance().showExitClassroomDialog();
            }
        } else {
            ClassroomController.getInstance().exitFullScreen(mContext, true);
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

        if (ClassroomController.getInstance().isFragmentPlayFullScreen()) {
            if (mTopPanel.getVisibility() == View.VISIBLE) {
                hideAnim(mTopPanel, "mTopPanel");
                hideAnim(mContactBtn, "mContactBtn");
                hideAnim(mOpenTalkBtn, "mOpenTalkBtn");
                hideAnim(mLandPortraitBtn, "mLandPortraitBtn");
            } else if (mTopPanel.getVisibility() == View.INVISIBLE) {
                showAnim(mTopPanel, "mTopPanel");
                showAnim(mContactBtn, "mContactBtn");
                showAnim(mOpenTalkBtn, "mOpenTalkBtn");
                showAnim(mLandPortraitBtn, "mLandPortraitBtn");
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
            mHideShowTalkBtn.setImageResource(R.drawable.ic_cr_show_talk);
            mDiscussionListView.setVisibility(View.GONE);
        } else {
            mHideShowTalkBtn.setImageResource(R.drawable.ic_cr_hide_talk);
            mDiscussionListView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onIndividualPublishCallback(StreamingResponse response) {
        Bundle data = new Bundle();
        data.putInt(Constants.KEY_PUBLISH_TYPE, StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL);
        data.putInt(Constants.KEY_FROM, Constants.FROM_PLAY_FRAGMENT);
        data.putString(Constants.KEY_PUBLISH_URL, response.publishUrl);

        data.putString(Constants.KEY_BEFORE_LIVE_STATE, mOriginSteamState);
        data.putLong(Constants.KEY_INDIVIDUAL_DURATION, response.finishOn);
        data.putString(Constants.KEY_INDIVIDUAL_NAME, "");
        ClassroomController.getInstance().enterPublishFragment(data, true);
    }

    @Override
    protected void onPeerPublishCallback(OpenMediaNotify openMediaNotify) {
        startPublishFragment(openMediaNotify.publishUrl, mPlayUrl, StreamType.TYPE_STREAM_PUBLISH_PEER_TO_PEER);
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
                        if (mPeerTalkAttendee == null || TextUtils.isEmpty(mPeerTalkAttendee.accountId)) {
                            TalkManager.getInstance().sendText(content);
                        } else {
                            TalkManager.getInstance().sendText(mPeerTalkAttendee.accountId, content);
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

                    mPeerTalkAttendee = attendee;
                    switch (action) {
                        case OnAttendItemClick.ACTION_OPEN_TALK:
                            if (ClassroomController.getInstance().isFragmentPlayFullScreen()) {
                                int gravity = data.getIntExtra(Constants.KEY_SHEET_GRAVITY, SheetFragment.SHEET_GRAVITY_BOTTOM);
                                int size = isPortrait() ? mSlideViewHeight : mSlideViewWidth;
                                ClassroomController.getInstance().openSlideTalk(this, attendee, mCtlSession, gravity, size);
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
                    ClassroomController.getInstance().exitDocFragmentWhitOpenMime(doc, this);
                    break;
            }
        }

    }

    @Override
    public void playStream(String url) {
        int type = StreamType.TYPE_STREAM_PLAY;
        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        if (Live.LiveSessionState.LIVE.equals(liveState)) {
            type = StreamType.TYPE_STREAM_PLAY;
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState) ||
                Live.LiveSessionState.SCHEDULED.equals(liveState) ||
                Live.LiveSessionState.FINISHED.equals(liveState)) {
            type = StreamType.TYPE_STREAM_PLAY_INDIVIDUAL;
        }

        mVideoController.playStream(type, url);
    }

    @Override
    public void onStreamStarted(int type, Object extra) {
        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        mCountTime = mTimeProgressHelper.getCountTime();
        switch (type) {
            case StreamType.TYPE_STREAM_PLAY_PEER_TO_PEER:
            case StreamType.TYPE_STREAM_PLAY:
                mTimeProgressHelper.setTimeProgress(mCountTime, liveState);
                break;
            case StreamType.TYPE_STREAM_PLAY_INDIVIDUAL:
                try {
                    mIndividualStreamDuration = (long) extra;
                } catch (Exception e) {
                }
                mTimeProgressHelper.setTimeProgress(mCountTime, mIndividualStreamDuration, liveState, mIndividualName, true);
                break;
        }

        mTipsHelper.hideTips();

        //exit video play
        ClassroomController.getInstance().exitVideoPlayPage();
    }

    @Override
    public void onStreamSizeChanged(BaseMediaView v, int videoW, int videoH) {

    }

    @Override
    public void onStreamStopped(int type, Object extra) {
        //updateViewStyleByLiveState();
        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        mCountTime = mTimeProgressHelper.getCountTime();
        mTimeProgressHelper.setTimeProgress(mCountTime, 0, liveState, mIndividualName, false);

        mTipsHelper.setTipsByStateOnStrop(liveState);
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
    protected void onSyncStateChanged(SyncStateResponse syncState) {
        mCountTime = mTimeProgressHelper.getCountTime();
        boolean autoCountTime = false;

        String liveState = null;
        if (Live.LiveSessionState.SCHEDULED.equals(syncState.from) && Live.LiveSessionState.PENDING_FOR_JOIN.equals(syncState.to)) {
            liveState = Live.LiveSessionState.PENDING_FOR_JOIN;

        } else if ((Live.LiveSessionState.PENDING_FOR_JOIN.equals(syncState.from) && Live.LiveSessionState.LIVE.equals(syncState.to))
                || (Live.LiveSessionState.RESET.equals(syncState.from) && Live.LiveSessionState.LIVE.equals(syncState.to))) {
            liveState = Live.LiveSessionState.LIVE;
            autoCountTime = true;
            mCountTime = syncState.timeline != null ? syncState.timeline.hasTaken : 0;
        } else if (Live.LiveSessionState.LIVE.equals(syncState.from) && Live.LiveSessionState.RESET.equals(syncState.to)) {
            liveState = Live.LiveSessionState.RESET;
            autoCountTime = false;
            mCountTime = syncState.timeline != null ? syncState.timeline.hasTaken : 0;
        } else if (Live.LiveSessionState.LIVE.equals(syncState.from) && Live.LiveSessionState.DELAY.equals(syncState.to)) {
            liveState = Live.LiveSessionState.DELAY;
            autoCountTime = false;
            mCountTime = 0;
        } else if (Live.LiveSessionState.DELAY.equals(syncState.from) && Live.LiveSessionState.FINISHED.equals(syncState.to)) {
            liveState = Live.LiveSessionState.FINISHED;
        }

        setControllerBtnStyle(liveState);
        LiveCtlSessionManager.getInstance().updateCtlSessionState(liveState);
        mTimeProgressHelper.setTimeProgress(mCountTime, liveState, autoCountTime);

        if (mTipsHelper != null) {
            mTipsHelper.hideTips();
        }
    }

    /**
     * 初始化课程直播
     */
    private void initCtlLive() {
        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        mPlayUrl = null;
        if (Live.LiveSessionState.LIVE.equals(liveState)) {
            mPlayUrl = mCtlSession.playUrl;
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState) ||
                Live.LiveSessionState.SCHEDULED.equals(liveState) ||
                Live.LiveSessionState.FINISHED.equals(liveState)) {
            mPlayUrl = mCtlSession.playUrl;
            mIndividualStreamDuration = mCtlSession.finishOn;
        }

        mCountTime = ClassroomBusiness.getCountTimeByCtlSession();
        Bundle data = getArguments();
        if (data != null) {
            int from = data.getInt(Constants.KEY_FROM, Constants.FROM_ACTIVITY);
            if (from == Constants.FROM_PUBLISH_FRAGMENT) {
                mCountTime = data.getLong(Constants.KEY_COUNT_TIME, mCountTime);
            }
        }
        mTimeProgressHelper.setTimeProgress(mCountTime, mIndividualStreamDuration, liveState, mIndividualName, false);

        if (TextUtils.isEmpty(mPlayUrl)) {
            mTipsHelper.setTipsByState(liveState);
        } else {
            playStream(mCtlSession.playUrl);
        }
    }


    @Override
    protected void setControllerBtnStyle(String liveState) {
        if (mPlayPauseBtn == null) {
            return;
        }

        if (Live.LiveSessionState.SCHEDULED.equals(liveState) ||
                Live.LiveSessionState.FINISHED.equals(liveState)) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
            mPlayPauseBtn.setVisibility(View.VISIBLE);
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState) ||
                Live.LiveSessionState.RESET.equals(liveState)) {
            if (mUserMode == Constants.UserMode.TEACHING) {
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_start);
                mPlayPauseBtn.setVisibility(View.VISIBLE);
            } else {
                mPlayPauseBtn.setVisibility(View.INVISIBLE);
            }
        } else if (Live.LiveSessionState.LIVE.equals(liveState)) {
            if (mUserMode == Constants.UserMode.TEACHING) {
                mPlayPauseBtn.setVisibility(View.VISIBLE);
                mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
            } else {
                mPlayPauseBtn.setVisibility(View.INVISIBLE);
            }
        } else if (Live.LiveSessionState.DELAY.equals(liveState)) {
            mPlayPauseBtn.setVisibility(View.INVISIBLE);
        } else if (Live.LiveSessionState.INDIVIDUAL.equals(liveState)) {
            mPlayPauseBtn.setVisibility(View.VISIBLE);
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
        }
    }

    private void playOrPauseLesson() {
        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        if (Live.LiveSessionState.SCHEDULED.equals(liveState) ||
                Live.LiveSessionState.FINISHED.equals(liveState)) {
            individualPublishStream();
        } else if (Live.LiveSessionState.RESET.equals(liveState)) {
            showProgress(true);
            LiveManager.resumeClass(mContext, mTicket, Live.StreamMode.MUTE, new APIServiceCallback<ClassResponse>() {
                @Override
                public void onSuccess(ClassResponse response) {
                    cancelProgress();
                    CtlSession session = LiveCtlSessionManager.getInstance().getCtlSession();
                    LiveCtlSessionManager.getInstance().updateCtlSessionState(Live.LiveSessionState.LIVE);
                    startPublishFragment(response != null ? response.publishUrl : null, null, StreamType.TYPE_STREAM_PUBLISH);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                }
            });
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState)) {
            showProgress(true);
            LiveManager.beginClass(mContext, mTicket, Live.StreamMode.MUTE, new APIServiceCallback<ResponseBody>() {
                @Override
                public void onSuccess(ResponseBody object) {
                    cancelProgress();
                    long countTime = ClassroomBusiness.getCountTimeByCtlSession();
                    ClassResponse response = ApiManager.getClassResponse(object);
                    LiveCtlSessionManager.getInstance().updateCtlSessionState(Live.LiveSessionState.LIVE);
                    startPublishFragment(response != null ? response.publishUrl : null, null, StreamType.TYPE_STREAM_PUBLISH);
                }

                @Override
                public void onFailure(String errorCode, String errorMessage) {
                    cancelProgress();
                }
            });
        } else if (Live.LiveSessionState.INDIVIDUAL.equals(liveState)) {
            cancelProgress();
            LiveCtlSessionManager.getInstance().updateCtlSessionState(mOriginSteamState);
            setControllerBtnStyle(mOriginSteamState);
            mVideoController.pausePublishStream(StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL);
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
        data.putLong(Constants.KEY_COUNT_TIME, mCountTime);
        ClassroomController.getInstance().enterPublishFragment(data, true);
    }

}
