package cn.xiaojs.xma.ui.classroom2.live;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.orhanobut.logger.Logger;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.ui.classroom2.base.BaseLiveView;

import static com.pili.pldroid.player.AVOptions.PREFER_FORMAT_M3U8;

/**
 * Created by maxiaobao on 2017/10/19.
 */

public class PlayLiveView extends BaseLiveView {

    private PLVideoTextureView playView;


    private boolean canMove;

    public PlayLiveView(Context context) {
        super(context);
    }

    public PlayLiveView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public PlayLiveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    @Override
    public View createLiveView() {
        playView = new PLVideoTextureView(getContext());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        playView.setLayoutParams(layoutParams);
        configPlayView(playView);
        return playView;
    }

    @Override
    protected void onCloseClick(View view) {
        super.onCloseClick(view);
        stopPlay();

    }

    @Override
    public boolean canMove() {
        return canMove;
    }



    public Bitmap getBitmap() {
        return playView.getTextureView().getBitmap();
    }

    public void startPlay(String path) {
        playView.setVideoPath(path);
        playView.start();
    }

    public void resume() {
        playView.start();
    }

    public void pause() {
        playView.pause();
    }

    public void stopPlay() {
        playView.stopPlayback();
    }

    public void destroy() {
        stopPlay();
    }



    private void configPlayView(PLVideoTextureView videoTextureView) {
        loadingLayout.setVisibility(VISIBLE);
        videoTextureView.setBufferingIndicator(loadingLayout);
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
        int codec = AVOptions.MEDIA_CODEC_AUTO;
        AVOptions options = new AVOptions();
        // the unit of timeout is ms
        options.setInteger(AVOptions.KEY_PREPARE_TIMEOUT, 10 * 1000);
        // 1 -> hw codec enable, 0 -> disable [recommended]
        options.setInteger(AVOptions.KEY_MEDIACODEC, codec);

        // 设置偏好的视频格式，设置后会加快对应格式视频流的加载速度，但播放其他格式会出错
        options.setInteger(AVOptions.KEY_PREFER_FORMAT, PREFER_FORMAT_M3U8);

        videoTextureView.setAVOptions(options);
        videoTextureView.setDebugLoggingEnabled(XiaojsConfig.DEBUG);

        // You can mirror the display
        // mVideoView.setMirror(true);

        // You can also use a custom `MediaController` widget
//        MediaController mediaController = new MediaController(this, !isLiveStreaming, isLiveStreaming);
//        mediaController.setOnClickSpeedAdjustListener(mOnClickSpeedAdjustListener);
//        videoView.setMediaController(mediaController);

        videoTextureView.setOnInfoListener(mOnInfoListener);
        videoTextureView.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
        videoTextureView.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        videoTextureView.setOnCompletionListener(mOnCompletionListener);
        videoTextureView.setOnErrorListener(mOnErrorListener);

        videoTextureView.setLooping(false);

    }

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
                        //Logger.i(videoView.getMetadata().toString());
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

}
