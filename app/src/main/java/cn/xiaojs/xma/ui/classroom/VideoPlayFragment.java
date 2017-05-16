package cn.xiaojs.xma.ui.classroom;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.classroom.live.view.PlayerTextureView;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.util.TimeUtil;

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
 * Date:2017/4/12
 * Desc:
 *
 * ======================================================================================== */

public class VideoPlayFragment extends BaseFragment {
    private final static float LIVE_PROGRESS_WIDTH_FACTOR = 0.55F;
    private final static int MSG_COUNT_TIME = 0;
    private final static int MSG_HIDE_VIDEO_CONTROLLER = 1;

    private final static int ANIM_SHOW = 1 << 1;
    private final static int ANIM_HIDE = 1 << 2;

    @BindView(R.id.play_pause_btn)
    ImageView mPlayPauseBtn;
    @BindView(R.id.video_player)
    PlayerTextureView mVideoPlayerView;
    @BindView(R.id.count_time)
    TextView mCountTimeTv;
    @BindView(R.id.total_time)
    TextView mTotalTimeTv;
    @BindView(R.id.live_progress_layout)
    View mLiveProgressLayout;
    @BindView(R.id.live_progress)
    SeekBar mLiveProgress;

    private LibDoc mDoc;
    private String mUrl;

    private boolean mStarted = false;
    private boolean mPlaying = false;

    private int mViewHeight;
    private int mViewWidth;

    private PLMediaPlayer mPlMediaPlayer;

    private Handler mHandler;
    private long mCurrPosition;
    private long mDuration;
    private boolean mPause;
    private boolean mAnimating;

    private PanelAnimListener mPanelAnimListener;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_cls_video_player, null);
    }

    @Override
    protected void init() {
        Bundle data = getArguments();
        if (data != null) {
            mDoc = (LibDoc) data.getSerializable(Constants.KEY_LIB_DOC);
        }

        mViewWidth = getResources().getDisplayMetrics().widthPixels;
        mViewHeight = getResources().getDisplayMetrics().heightPixels;
        mPanelAnimListener = new PanelAnimListener();

        mVideoPlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLiveProgressLayout.getVisibility() == View.VISIBLE) {
                    hideVideoController();
                } else {
                    showVideoController();
                }
            }
        });

        initHandler();
        initLiveProgress();

        mVideoPlayerView.setTouchable(false);
        if (mDoc != null) {
            mUrl = ClassroomBusiness.getMediaUrl(mDoc.key);
            if (!TextUtils.isEmpty(mUrl)) {
                PLVideoTextureView pLVideoTextureView = mVideoPlayerView.getPlayer();

                pLVideoTextureView.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(PLMediaPlayer plMediaPlayer) {
                        mPlaying = false;
                        mCurrPosition = 0;
                        mHandler.removeMessages(MSG_COUNT_TIME);
                        if (mCountTimeTv != null) {
                            mCountTimeTv.setText(TimeUtil.formatSecondTime(0));
                        }
                        if (mPlayPauseBtn != null) {
                            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_start);
                        }
                    }
                });

                pLVideoTextureView.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(PLMediaPlayer plMediaPlayer) {
                        if (plMediaPlayer != null) {
                            mPlMediaPlayer = plMediaPlayer;
                            mDuration = plMediaPlayer.getDuration();
                            mTotalTimeTv.setText(TimeUtil.formatSecondTime(mDuration / 1000));
                            int videoW = plMediaPlayer.getVideoWidth();
                            int videoH = plMediaPlayer.getVideoHeight();

                            float ratio = videoW / (float) videoH;
                            int temp = (int) (ratio * mViewHeight);
                            if (temp > mViewWidth) {
                                // depend width
                                videoW = mViewWidth;
                                videoH = (int) (mViewWidth / ratio);
                            } else {
                                // depend height
                                videoH = mViewHeight;
                                videoW = temp;
                            }

                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mVideoPlayerView.getLayoutParams();
                            layoutParams.width = videoW;
                            layoutParams.height = videoH;
                            mHandler.sendEmptyMessage(MSG_COUNT_TIME);
                            mLiveProgress.setEnabled(true);
                        }
                    }
                });
            }
        }
    }

    @OnClick({R.id.back, R.id.play_pause_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                if (mContext instanceof ClassroomActivity) {
                    ((ClassroomActivity) mContext).exitVideoPlayer();
                }
                break;
            case R.id.play_pause_btn:
                if (mPlaying) {
                    pause();
                } else {
                    play();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        play();
        if (mPause && mVideoPlayerView != null) {
            mPause = false;
            mVideoPlayerView.showLoading(false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        pause();
        mPause = true;
    }

    @Override
    public void onDestroyView() {
        if (mVideoPlayerView != null) {
            mVideoPlayerView.destroy();
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        super.onDestroyView();
    }

    private void initHandler() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                onHandleMessage(msg);
            }
        };
    }

    private void initLiveProgress() {
        int w = getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams params = mLiveProgress.getLayoutParams();
        params.width = (int) (w * LIVE_PROGRESS_WIDTH_FACTOR);
        mLiveProgress.setEnabled(false);

        mLiveProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mHandler.removeMessages(MSG_COUNT_TIME);
                mCurrPosition = (int) ((seekBar.getProgress() / (float) 100) * mDuration);
                seekTo(mCurrPosition);
            }
        });
    }

    private void play() {
        mPlaying = true;
        mVideoPlayerView.setVisibility(View.VISIBLE);
        mPlayPauseBtn.setImageResource(R.drawable.ic_cr_pause);
        if (!mStarted) {
            mStarted = true;
            mVideoPlayerView.setPath(mUrl);
            mVideoPlayerView.showLoading(true);
            mVideoPlayerView.resume();
        } else {
            mVideoPlayerView.showLoading(false);
            mVideoPlayerView.resume();
        }
        mCurrPosition = mPlMediaPlayer != null ? mPlMediaPlayer.getCurrentPosition() : 0;
        mHandler.sendEmptyMessage(MSG_COUNT_TIME);
        mHandler.sendEmptyMessageDelayed(MSG_HIDE_VIDEO_CONTROLLER, 3000);
    }

    public void pause() {
        mPlaying = false;
        mHandler.removeMessages(MSG_COUNT_TIME);
        mHandler.removeMessages(MSG_HIDE_VIDEO_CONTROLLER);
        mPlayPauseBtn.setImageResource(R.drawable.ic_cr_start);
        mVideoPlayerView.pause();
        mCurrPosition = mPlMediaPlayer != null ? mPlMediaPlayer.getCurrentPosition() : 0;
    }

    public void seekTo(long position) {
        if (mPlMediaPlayer != null) {
            pause();
            mPlMediaPlayer.seekTo(position);
            play();
        }
    }

    private void onHandleMessage(Message msg) {
        switch (msg.what) {
            case MSG_COUNT_TIME:
                mCurrPosition = mCurrPosition + 1000;
                if (mCurrPosition > mDuration) {
                    mCurrPosition = mDuration;
                }
                if (mCountTimeTv != null) {
                    mCountTimeTv.setText(TimeUtil.formatSecondTime(mCurrPosition / 1000));
                }

                if (mCurrPosition < mDuration) {
                    mHandler.sendEmptyMessageDelayed(MSG_COUNT_TIME, 1000);
                }

                int progress = (int) ((mCurrPosition / (float) mDuration) * 100);
                mLiveProgress.setProgress(progress);
                break;
            case MSG_HIDE_VIDEO_CONTROLLER:
                hideVideoController();
                break;
        }
    }

    private void showVideoController() {
        if (mAnimating || mLiveProgressLayout == null || mHandler == null) {
            return;
        }
        mHandler.removeMessages(MSG_HIDE_VIDEO_CONTROLLER);
        ViewPropertyAnimator propertyAnimator = mLiveProgressLayout.animate();
        propertyAnimator
                .alpha(1.0f)
                .setListener(mPanelAnimListener.with(mLiveProgressLayout).play(ANIM_SHOW))
                .start();

        mHandler.sendEmptyMessageDelayed(MSG_HIDE_VIDEO_CONTROLLER, propertyAnimator.getDuration() + 3000);
    }

    private void hideVideoController() {
        if (mAnimating || mLiveProgressLayout == null) {
            return;
        }
        mHandler.removeMessages(MSG_HIDE_VIDEO_CONTROLLER);
        mLiveProgressLayout.animate()
                .alpha(0.0f)
                .setListener(mPanelAnimListener.with(mLiveProgressLayout).play(ANIM_HIDE))
                .start();
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
            if (mAnimType == ANIM_SHOW && mV != null) {
                mV.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mAnimating = false;
            if (mAnimType == ANIM_HIDE && mV != null) {
                mV.setVisibility(View.GONE);
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
            mAnimating = false;
            mV = null;
            if (mAnimType == ANIM_SHOW && mV != null) {
                mV.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
