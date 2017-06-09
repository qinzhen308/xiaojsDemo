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
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.crop.CropImagePath;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.ClassResponse;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom.bean.OpenMediaNotify;
import cn.xiaojs.xma.ui.classroom.bean.StreamingResponse;
import cn.xiaojs.xma.ui.classroom.bean.SyncClassStateResponse;
import cn.xiaojs.xma.ui.classroom.bean.SyncStateResponse;
import cn.xiaojs.xma.ui.classroom.live.PlayVideoController;
import cn.xiaojs.xma.ui.classroom.live.StreamType;
import cn.xiaojs.xma.ui.classroom.live.view.BaseMediaView;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.talk.EmbedTalkFragment;
import cn.xiaojs.xma.ui.classroom.talk.OnAttendItemClick;
import cn.xiaojs.xma.ui.classroom.talk.OnGetTalkListener;
import cn.xiaojs.xma.ui.classroom.talk.TalkManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkPresenter;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.ui.widget.SheetFragment;
import cn.xiaojs.xma.util.XjsUtils;
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
            R.id.fc_hide_show_talk_btn, R.id.fc_open_talk_btn, R.id.play_video, R.id.class_canlender})
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
            case R.id.class_canlender://日历课表
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
        getChildFragmentManager().beginTransaction().add(R.id.talk_layout, mEmbedTalkFragment).commit();


        Constants.ClassroomType classroomType = LiveCtlSessionManager.getInstance().getClassroomType();
        //判断教室类型，如果为PrivateClass,需要显示教学日历
        if (classroomType == Constants.ClassroomType.PrivateClass) {
            mCanlenderBtn.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void initData() {
        //TODO 当是classroom为班时，是否title显示要改变？
        mLessonTitle.setText(getLessonTitle());
        initCtlLive();

        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        updateViewStyleByLiveState(liveState);

        mTalkOpenContactBtn.setCount(TalkManager.getInstance().getPeerTalkUnreadMsgCount());
        postHideAnim();
    }


    private void updateTitle() {
        mLessonTitle.setText(getLessonTitle());
    }


    private void updateViewStyleByLiveState(String liveState) {
        int mode = LiveCtlSessionManager.getInstance().getLiveMode();
        if (Constants.PREVIEW_MODE == mode) {
            //预览模式, 不能有任何操作
            mPlayPauseBtn.setVisibility(View.INVISIBLE);
        } else {
            if (ClassroomBusiness.canIndividual(mCtlSession)) {
                //课前课后或者班课的课外时间
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

        ClassroomController.getInstance().enterLandFullScreen(isPortrait(), mContext);
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
            if (ClassroomController.getInstance().isFragmentPlayFullScreen()) {
                updatePortraitPlayViewStyle();
                setUnreadMsgCountOnExitFullscreen();
                ClassroomController.getInstance().exitFullScreen(mContext, false);
            } else {
                ClassroomController.getInstance().showExitClassroomDialog();
            }
        } else {
            setUnreadMsgCountOnExitFullscreen();
            ClassroomController.getInstance().exitFullScreen(mContext, true);
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

        if (ClassroomController.getInstance().isFragmentPlayFullScreen()) {
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
    protected void onIndividualPublishCallback(StreamingResponse response) {
        if (response == null) {
            return;
        }

        Bundle data = new Bundle();

        LiveCtlSessionManager.getInstance().updateCtlSessionState(Live.LiveSessionState.INDIVIDUAL);
        setControllerBtnStyle(Live.LiveSessionState.INDIVIDUAL);
        data.putInt(Constants.KEY_PUBLISH_TYPE, StreamType.TYPE_STREAM_PUBLISH_INDIVIDUAL);
        data.putInt(Constants.KEY_FROM, Constants.FROM_PLAY_FRAGMENT);
        data.putString(Constants.KEY_PUBLISH_URL, response.publishUrl);

        data.putString(Constants.KEY_BEFORE_LIVE_STATE, mOriginSteamState);
        data.putLong(Constants.KEY_INDIVIDUAL_DURATION, response.finishOn);
        data.putString(Constants.KEY_INDIVIDUAL_NAME, "");
        data.putSerializable(Constants.KEY_INDIVIDUAL_RESPONSE, response);
        XjsUtils.hideIMM(mContext, mContent.getWindowToken());
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
                    if (ClassroomController.getInstance().isFragmentPlayFullScreen()) {
                        mFullscreenContactBtn.setCount(peerCount);
                    } else {
                        mTalkOpenContactBtn.setCount(peerCount);
                    }

                    switch (action) {
                        case OnAttendItemClick.ACTION_OPEN_TALK:
                            if (ClassroomController.getInstance().isFragmentPlayFullScreen()) {
                                int gravity = data.getIntExtra(Constants.KEY_SHEET_GRAVITY, SheetFragment.SHEET_GRAVITY_BOTTOM);
                                int size = isPortrait() ? mSlideViewHeight : mSlideViewWidth;
                                if (isPortrait()) {
                                    ClassroomController.getInstance().openSlideTalk(this, attendee, mCtlSession, size);
                                } else {
                                    ClassroomController.getInstance().openSlideTalk(this, attendee, mCtlSession, gravity, size);
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
                    ClassroomController.getInstance().exitDocFragmentWhitOpenMime(doc, this);
                    break;
                case ClassroomController.REQUEST_CLASS_CANLENDER:
                    //TODO 是否回放
                    //ClassroomController.getInstance().enterVideoPlayPage();
                    break;
            }
        }

    }

    @Override
    public void playStream(String url) {
        int type = StreamType.TYPE_STREAM_PLAY;
        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        if (Live.LiveSessionState.LIVE.equals(liveState)
                || Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState)
                || Live.LiveSessionState.PENDING_FOR_LIVE.equals(liveState)) {
            type = StreamType.TYPE_STREAM_PLAY;
        } else if (ClassroomBusiness.canIndividual(mCtlSession)) {
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

                if (ClassroomBusiness.hasTeachingAbility()) {
                    //个人推流时，如果不是同学身份，此按钮要现实，因为老师可以强制尽心推流。
                    mPlayPauseBtn.setVisibility(View.VISIBLE);
                } else {
                    mPlayPauseBtn.setVisibility(View.GONE);
                }


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

        switch (type) {
            case StreamType.TYPE_STREAM_PLAY_INDIVIDUAL:
                mPlayPauseBtn.setVisibility(View.VISIBLE);
                break;
        }


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
        if (Live.LiveSessionState.SCHEDULED.equals(syncState.from) && Live.LiveSessionState.PENDING_FOR_JOIN.equals(syncState.to)) {//TODO 是否要加入班的PEND_FOR_LIVE ？
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

    @Override
    protected void onSyncClassStateChanged(SyncClassStateResponse syncState) {
        //TODO 同步班状态
        //TODO 是否要加入班的PEND_FOR_LIVE ？

        if (syncState == null)
            return;

        if (XiaojsConfig.DEBUG) {
            Logger.d("-----------------------onSyncClassStateChanged---------------------------");
        }


        //班课状态发生变化
        mCtlSession.cls.state = syncState.to;


        if (Live.LiveSessionState.IDLE.equals(syncState.to)) {
            mCtlSession.ctl = null;

            updateTitle();
            mTipsHelper.setTipsByState(syncState.to);
            //FIXME 总是时间，应该显示为0；
            mTimeProgressHelper.setTimeProgress(0, syncState.to, false);
            setControllerBtnStyle(syncState.to);

            //FIXME 应该收到流暂停的消息，先临时放到这个地方处理
            mVideoController.pausePlayStream(StreamType.TYPE_STREAM_PLAY);


        } else if (Live.LiveSessionState.PENDING_FOR_LIVE.equals(syncState.to)) {
            //班中当前课的信息
            if (syncState.current != null) {

                if (mCtlSession.ctl == null || !mCtlSession.ctl.id.equals(syncState.current.id)) {
                    CtlSession.Ctl newCtl = new CtlSession.Ctl();
                    newCtl.title = syncState.current.title;
                    newCtl.id = syncState.current.id;
                    newCtl.subtype = syncState.current.typeName;
                    newCtl.duration = syncState.current.schedule.duration;
                    newCtl.startedOn = syncState.current.schedule.start.toGMTString();

                    mCtlSession.ctl = newCtl;

                    updateTitle();
                    mTipsHelper.setTipsByState(syncState.to);
                    //FIXME
                    mTimeProgressHelper.setTimeProgress(syncState.current.schedule.duration, syncState.to, false);
                    setControllerBtnStyle(syncState.to);
                }
            }
        }
    }

    /**
     * 初始化课程直播
     */
    private void initCtlLive() {
        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        mPlayUrl = mCtlSession.playUrl;

        if (ClassroomBusiness.canIndividual(mCtlSession)) {
            mIndividualStreamDuration = mCtlSession.finishOn;
        }

        mCountTime = ClassroomBusiness.getCountTimeByCtlSession();
        Bundle data = getArguments();
        if (data != null) {
            int from = data.getInt(Constants.KEY_FROM, Constants.FROM_ACTIVITY);
            if (from == Constants.FROM_PUBLISH_FRAGMENT) {
                mCountTime = data.getLong(Constants.KEY_COUNT_TIME, mCountTime);
            }

            mIndividualResponseBody = (StreamingResponse) data.getSerializable(Constants.KEY_INDIVIDUAL_RESPONSE);
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

        if (ClassroomBusiness.canIndividual(mCtlSession)) {
            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_publish_stream);
            mPlayPauseBtn.setVisibility(View.VISIBLE);
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState)
                || Live.LiveSessionState.RESET.equals(liveState)
                || Live.LiveSessionState.PENDING_FOR_LIVE.equals(liveState)) {
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
        if (!ClassroomController.getInstance().isSocketConnected()) {
            Toast.makeText(mContext, R.string.cr_connecting, Toast.LENGTH_SHORT).show();
            return;
        }

        //Prevent frequent clicks
        if (System.currentTimeMillis() - mPlayOrPausePressTime < BTN_PRESS_INTERVAL) {
            return;
        }
        mPlayOrPausePressTime = System.currentTimeMillis();

        String liveState = LiveCtlSessionManager.getInstance().getLiveState();
        if (ClassroomBusiness.canIndividual(mCtlSession)) {
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
        } else if (Live.LiveSessionState.PENDING_FOR_JOIN.equals(liveState) ||
                Live.LiveSessionState.PENDING_FOR_LIVE.equals(liveState)) {//公开课在教室内没有pending_for_live状态，班才有
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
        XjsUtils.hideIMM(mContext, mContent.getWindowToken());
        ClassroomController.getInstance().enterPublishFragment(data, true);
    }

    @Override
    public void onExitTalk(int type) {
        if (type == TalkManager.TYPE_PEER_TALK) {
            if (!ClassroomController.getInstance().isFragmentPlayFullScreen()) {
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

        if (ClassroomController.getInstance().isFragmentPlayFullScreen()) {
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
