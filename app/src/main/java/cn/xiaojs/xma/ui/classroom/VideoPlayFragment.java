package cn.xiaojs.xma.ui.classroom;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    @BindView(R.id.play_pause_btn)
    ImageView mPlayPauseBtn;
    @BindView(R.id.video_player)
    PlayerTextureView mVideoPlayerView;
    @BindView(R.id.count_time)
    TextView mCountTimeTv;
    @BindView(R.id.total_time)
    TextView mTotalTimeTv;
    @BindView(R.id.live_progress)
    SeekBar mLiveProgress;

    private LibDoc mDoc;
    private String mUrl;

    private boolean mStarted = false;
    private boolean mPlaying = false;

    private int mViewHeight;
    private int mViewWidth;

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

        initLiveProgress();

        if (mDoc != null) {
            mUrl = ClassroomBusiness.getImageUrl(mDoc.key);
            if (!TextUtils.isEmpty(mUrl)) {
                PLVideoTextureView player = mVideoPlayerView.getPlayer();
                player.setOnInfoListener(new PLMediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
                        switch (what) {
                            case PLMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                                break;
                            case 10003:
                                //mTotalTimeTv.setText(TimeUtil.formatSecondTime(extra / 100));
                                break;
                        }

                        return false;
                    }
                });

                player.setOnCompletionListener(new PLMediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(PLMediaPlayer plMediaPlayer) {
                        mPlaying = false;
                        if (mPlayPauseBtn != null) {
                            mPlayPauseBtn.setImageResource(R.drawable.ic_cr_start);
                        }
                    }
                });

                player.setOnPreparedListener(new PLMediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(PLMediaPlayer plMediaPlayer) {
                        if (plMediaPlayer != null) {
                            mTotalTimeTv.setText(TimeUtil.formatSecondTime(plMediaPlayer.getDuration() / 1000));
                            int videoW = plMediaPlayer.getVideoWidth();
                            int videoH = plMediaPlayer.getVideoHeight();

                            float ratio = videoW / (float)videoH;
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
                        }
                    }
                });

                //auto play
                play();
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
    public void onDestroy() {
        super.onDestroy();
        if (mVideoPlayerView != null) {
            mVideoPlayerView.destroy();
        }
    }

    private void initLiveProgress() {
        int w = getResources().getDisplayMetrics().widthPixels;
        ViewGroup.LayoutParams params = mLiveProgress.getLayoutParams();
        params.width = (int) (w * LIVE_PROGRESS_WIDTH_FACTOR);
        mLiveProgress.setEnabled(false);
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
            mVideoPlayerView.showLoading(true);
            mVideoPlayerView.resume();
        }
    }

    public void pause() {
        mPlaying = false;
        mPlayPauseBtn.setImageResource(R.drawable.ic_cr_start);
        mVideoPlayerView.pause();
    }

}
