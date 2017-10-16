package cn.xiaojs.xma.ui.classroom2;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class PlayFragment extends MovieFragment {

    @BindView(R.id.video_view)
    PLVideoTextureView videoView;
    @BindView(R.id.loading_View)
    LinearLayout loadingView;
    @BindView(R.id.top_back)
    ImageView nameView;

    //port
    @BindView(R.id.top_more)
    ImageView topMoreView;
    @BindView(R.id.top_live)
    TextView topLiveView;
    @BindView(R.id.bottom_bg)
    View bottomBgView;
    @BindView(R.id.bottom_orient)
    ImageView bottomOrientView;
    @BindView(R.id.bottom_class_name)
    TextView bottomNameView;
    @BindView(R.id.bottom_class_state)
    TextView bottomStateView;


    //land
    @BindView(R.id.top_photo)
    ImageView topPhotoView;
    @BindView(R.id.top_roominfo)
    TextView topRoominfoView;
    @BindView(R.id.top_whiteboard)
    ImageView topWhiteboardView;
    @BindView(R.id.top_screenshot)
    ImageView topScreenshotView;
    @BindView(R.id.bottom_chat)
    ImageView bottomChatView;
    @BindView(R.id.bottom_more)
    ImageView bottomMoreView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_classroom2_play, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        configVideoView();
        bindPlayinfo();
        handleRotate(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @OnClick({R.id.bottom_orient, R.id.top_back})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.top_back:                    //返回
                back();
                break;
            case R.id.bottom_orient:               //改变屏幕方向
                changeOrientation();
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        videoView.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        videoView.stopPlayback();
    }

    @Override
    public void onRotate(int orientation) {
//        mVideoView.setDisplayOrientation(mRotation);
        handleRotate(orientation);

    }

    @Override
    public void closeMovie() {

    }

    @Override
    public void onTopbackClick(View view, boolean land) {

    }

    private void bindPlayinfo() {

        bottomNameView.setText("XXX的教室");
        bottomStateView.setText(classroomEngine.getRoomTitle());
        topRoominfoView.setText(classroomEngine.getRoomTitle());
    }

    private void configVideoView() {
        videoView.setBufferingIndicator(loadingView);
        //mVideoView.setCoverView(coverView);

        // If you want to fix display orientation such as landscape, you can use the code show as follow
        //
        // if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
        //     mVideoView.setPreviewOrientation(0);
        // }
        // else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
        //     mVideoView.setPreviewOrientation(270);
        // }

        // 1 -> hw codec enable, 0 -> disable [recommended]
        int codec = AVOptions.MEDIA_CODEC_SW_DECODE;
        AVOptions options = new AVOptions();
        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);

        videoView.setAVOptions(options);
        videoView.setDebugLoggingEnabled(true);

        // You can mirror the display
        // mVideoView.setMirror(true);

        // You can also use a custom `MediaController` widget
//        MediaController mediaController = new MediaController(this, !isLiveStreaming, isLiveStreaming);
//        mediaController.setOnClickSpeedAdjustListener(mOnClickSpeedAdjustListener);
//        videoView.setMediaController(mediaController);

        videoView.setOnInfoListener(mOnInfoListener);
        videoView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        videoView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        videoView.setOnCompletionListener(mOnCompletionListener);
        videoView.setOnErrorListener(mOnErrorListener);

        videoView.setLooping(false);

        videoView.setVideoPath(classroomEngine.getPlayUrl());
        videoView.start();

    }

    private void handleRotate(int orientation) {

        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                //port
                topMoreView.setVisibility(View.GONE);
                topLiveView.setVisibility(View.GONE);
                bottomBgView.setVisibility(View.GONE);
                bottomOrientView.setVisibility(View.GONE);
                bottomNameView.setVisibility(View.GONE);
                bottomStateView.setVisibility(View.GONE);
                //land
                topPhotoView.setVisibility(View.VISIBLE);
                topRoominfoView.setVisibility(View.VISIBLE);
                topWhiteboardView.setVisibility(View.VISIBLE);
                topScreenshotView.setVisibility(View.VISIBLE);
                bottomChatView.setVisibility(View.VISIBLE);
                bottomMoreView.setVisibility(View.VISIBLE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                //port
                topMoreView.setVisibility(View.VISIBLE);
                topLiveView.setVisibility(View.VISIBLE);
                bottomBgView.setVisibility(View.VISIBLE);
                bottomOrientView.setVisibility(View.VISIBLE);
                bottomNameView.setVisibility(View.VISIBLE);
                bottomStateView.setVisibility(View.VISIBLE);
                //land
                topPhotoView.setVisibility(View.GONE);
                topRoominfoView.setVisibility(View.GONE);
                topWhiteboardView.setVisibility(View.GONE);
                topScreenshotView.setVisibility(View.GONE);
                bottomChatView.setVisibility(View.GONE);
                bottomMoreView.setVisibility(View.GONE);
                break;
        }


    }


    private PLMediaPlayer.OnInfoListener mOnInfoListener = new PLMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(PLMediaPlayer plMediaPlayer, int what, int extra) {
            if (XiaojsConfig.DEBUG) {
                Logger.i("OnInfo, what = %d, extra = %d", what, extra);
            }

            switch (what) {
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    break;
                case PLMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    break;
                case PLMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:

                    if (XiaojsConfig.DEBUG) {
                        Logger.i("First video render time: %d ms", extra);
                    }

                    break;
                case PLMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                    if (XiaojsConfig.DEBUG) {
                        Logger.i("First audio render time: %d ms", extra);
                    }
                    break;
                case PLMediaPlayer.MEDIA_INFO_VIDEO_FRAME_RENDERING:
                    if (XiaojsConfig.DEBUG) {
                        Logger.i("video frame rendering, ts = %d", extra);
                    }
                    break;
                case PLMediaPlayer.MEDIA_INFO_AUDIO_FRAME_RENDERING:
                    if (XiaojsConfig.DEBUG) {
                        Logger.i("audio frame rendering, ts = %d", extra);
                    }
                    break;
                case PLMediaPlayer.MEDIA_INFO_VIDEO_GOP_TIME:
                    if (XiaojsConfig.DEBUG) {
                        Logger.i("Gop Time: %d", extra);
                    }
                    break;
                case PLMediaPlayer.MEDIA_INFO_SWITCHING_SW_DECODE:
                    if (XiaojsConfig.DEBUG) {
                        Logger.i("Hardware decoding failure, switching software decoding!");
                    }
                    break;
                case PLMediaPlayer.MEDIA_INFO_METADATA:
                    if (XiaojsConfig.DEBUG) {
                        Logger.i(videoView.getMetadata().toString());
                    }
                    break;
                case PLMediaPlayer.MEDIA_INFO_VIDEO_BITRATE:
                case PLMediaPlayer.MEDIA_INFO_VIDEO_FPS:
                    //updateStatInfo();
                    break;
                case PLMediaPlayer.MEDIA_INFO_CONNECTED:
                    if (XiaojsConfig.DEBUG) {
                        Logger.i("Connected !");
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    };

    private PLMediaPlayer.OnErrorListener mOnErrorListener = new PLMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(PLMediaPlayer mp, int errorCode) {
            if (XiaojsConfig.DEBUG) {
                Logger.e("Error happened, errorCode = %d", errorCode);
            }

            switch (errorCode) {
                case PLMediaPlayer.ERROR_CODE_IO_ERROR:
                    /**
                     * SDK will do reconnecting automatically
                     */
                    if (XiaojsConfig.DEBUG) {
                        Logger.e("IO Error !");
                    }

                    return false;
                case PLMediaPlayer.ERROR_CODE_OPEN_FAILED:
                    if (XiaojsConfig.DEBUG) {
                        Logger.e("failed to open player !");
                    }
                    break;
                case PLMediaPlayer.ERROR_CODE_SEEK_FAILED:
                    if (XiaojsConfig.DEBUG) {
                        Logger.e("failed to seek !");
                    }
                    break;
                default:
                    if (XiaojsConfig.DEBUG) {
                        Logger.e("unknown error !");
                    }
                    break;
            }
            return true;
        }
    };

    private PLMediaPlayer.OnCompletionListener mOnCompletionListener =
            new PLMediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(PLMediaPlayer plMediaPlayer) {

                    if (XiaojsConfig.DEBUG) {
                        Logger.d("Play Completed !");
                    }
                }
            };

    private PLMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener =
            new PLMediaPlayer.OnBufferingUpdateListener() {
                @Override
                public void onBufferingUpdate(PLMediaPlayer plMediaPlayer, int precent) {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("onBufferingUpdate: %d", precent);
                    }
                }
            };

    private PLMediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener =
            new PLMediaPlayer.OnVideoSizeChangedListener() {
                @Override
                public void onVideoSizeChanged(PLMediaPlayer plMediaPlayer, int width, int height) {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("onVideoSizeChanged: width = %d, height = %d", width, height);
                    }
                }
            };

}
