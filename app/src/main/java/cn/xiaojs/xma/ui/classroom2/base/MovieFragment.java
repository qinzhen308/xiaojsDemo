package cn.xiaojs.xma.ui.classroom2.base;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.pili.pldroid.player.AVOptions;
import com.pili.pldroid.player.PLMediaPlayer;
import com.pili.pldroid.player.widget.PLVideoTextureView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.StreamStoppedResponse;
import cn.xiaojs.xma.ui.classroom2.ChatFragment;
import cn.xiaojs.xma.ui.classroom2.ClassDetailFragment;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.classroom2.SettingFragment;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.util.NetworkUtil;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;
import cn.xiaojs.xma.ui.widget.ClosableAdapterSlidingLayout;
import cn.xiaojs.xma.ui.widget.ClosableSlidingLayout;
import cn.xiaojs.xma.ui.widget.CommonDialog;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public abstract class MovieFragment extends BaseRoomFragment
        implements ClosableSlidingLayout.SlideListener{

    @BindView(R.id.control_port)
    public ConstraintLayout controlPort;


    @BindView(R.id.l_top_start_or_stop_living)
    public TextView startOrStopLiveView;
    @BindView(R.id.l_top_photo)
    public ImageView lTopPhotoView;
    @BindView(R.id.l_top_roominfo)
    public TextView lTopRoominfoView;

    @BindView(R.id.l_bottom_session)
    public ImageView lBottomSessionView;


    @BindView(R.id.l_right_screenshot)
    public ImageView lRightScreenshortView;


    @BindView(R.id.center_panel)
    View centerPanelView;
    @BindView(R.id.center_one2one)
    TextView centerOne2oneView;
    @BindView(R.id.center_board_opera)
    TextView centerBoardOperaView;
    @BindView(R.id.center_board_mgr)
    TextView centerBoardMgrView;
    @BindView(R.id.center_new_board)
    TextView centerNewBoardView;
    @BindView(R.id.center_member)
    TextView centerMedmberView;
    @BindView(R.id.center_database)
    TextView centerDatabaseView;
    @BindView(R.id.center_canlender)
    TextView centerCanlenderView;


    @BindView(R.id.control_land)
    public ConstraintLayout controlLand;

    @BindView(R.id.p_bottom_class_name)
    public TextView pBottomClassnameView;


    /////////////
    @BindView(R.id.right_slide_layout)
    public FrameLayout rightSlideLayout;
    @BindView(R.id.slide_layout)
    public ClosableAdapterSlidingLayout slideLayout;


    public final static int REQUEST_PERMISSION = 3;

    protected ClassroomEngine classroomEngine;
    protected Fragment slideFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classroomEngine = ClassroomEngine.getEngine();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        slideLayout.setSlideListener(this);

    }

    @OnClick({R.id.l_top_back, R.id.l_top_start_or_stop_living,
            R.id.l_bottom_chat, R.id.l_bottom_session, R.id.l_bottom_more,
            R.id.l_right_switchcamera, R.id.l_right_screenshot, R.id.l_right_switch_vb})
    void onLandControlItemClick(View view) {
        switch (view.getId()) {
            case R.id.l_top_back:                                             //返回：横屏
                onTopbackClick(view, true);
                break;
            case R.id.l_top_start_or_stop_living:                             //结束／开始直播
                onStartOrStopLiveClick(view);
                break;
            case R.id.l_right_switchcamera:                                   //切换摄像头
                onSwitchCamera(view);
                break;
            case R.id.l_bottom_more:
                showOrHiddenCenterPanel();
                break;
            case R.id.l_bottom_session:
                onTalkVisibilityClick(view);
                break;
            case R.id.l_bottom_chat:
                onInputMessageClick(view);
                break;

        }
    }


    @OnClick({R.id.center_one2one, R.id.center_board_opera,
            R.id.center_board_mgr, R.id.center_new_board, R.id.center_member,
            R.id.center_database, R.id.center_canlender})
    void onCenterPanelItemClick(View view) {
        switch (view.getId()) {
            case R.id.center_one2one:                                             //一对一音视频
                onOne2OneClick(view);
                break;
            case R.id.center_board_opera:                                         //百般协作
                break;
            case R.id.center_board_mgr:                                           //白板管理
                onBoardMgrClick(view);
                break;
            case R.id.center_new_board:                                           //新增白板
                onNewboardClick(view);
                break;
            case R.id.center_member:                                              //教室成员
                break;
            case R.id.center_database:                                            //资料库
                break;
            case R.id.center_canlender:                                           //课表
                break;
        }

        showOrHiddenCenterPanel();
    }


    @OnClick({R.id.p_top_back, R.id.p_top_more, R.id.p_bottom_orient, R.id.p_top_live})
    void onPortControlItemClick(View view) {
        switch (view.getId()) {
            case R.id.p_top_back:                                             //返回：竖屏
                onTopbackClick(view, false);
                break;
            case R.id.p_top_more:                                             //更多
                showMoreMenu(view);
                break;
            case R.id.p_bottom_orient:                                        //切换为横屏
                changeOrientation();
                break;
            case R.id.p_top_live:                                             //开始直播
                onStartLiveClick(view);
                break;
        }
    }


    @OnClick({R.id.right_slide_layout})
    void onRightSlideLayoutClick(View view) {
        switch (view.getId()) {
            case R.id.right_slide_layout:
                exitSlidePanel();
                break;
        }
    }





    /**
     * 点击了返回
     *
     * @param land 是否横屏的返回
     */
    public abstract void onTopbackClick(View view, boolean land);

    /**
     * 关闭当前的fragment
     */
    public abstract void closeMovie();

    /**
     * 响应屏幕横竖屏方向改变
     */
    public abstract void onRotate(int orientation);



    public void enterIdle() {
        ((Classroom2Activity) getActivity()).enterIdle();
    }

    public void enterPlay() {
        ((Classroom2Activity) getActivity()).enterPlay();
    }

    public void enterLiving() {
        ((Classroom2Activity) getActivity()).enterLiving();
    }

    public void enterPlayback() {
        ((Classroom2Activity) getActivity()).enterPlayback();
    }


    public void showMoreMenu(View targetView) {
        CommonPopupMenu menu = new CommonPopupMenu(getContext());
        String[] items = this.getResources().getStringArray(R.array.classroom2_more_item);
        menu.setBg(R.drawable.popup_menu_bg);
        menu.setWidth(this.getResources().getDimensionPixelSize(R.dimen.px280));
        menu.addTextItems(items);
        menu.addImgItems(new Integer[]{R.drawable.ic_setting,
                R.drawable.ic_class_database_share_1,
                R.drawable.ic_add_class1});
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 2:
                        ClassDetailFragment classDetailFragment = new ClassDetailFragment();
                        classDetailFragment.show(getFragmentManager(), "detail");
                        break;
                    case 1:
                        break;
                    case 0:
                        SettingFragment settingFragment = new SettingFragment();
                        settingFragment.show(getFragmentManager(), "setting");
                        break;
                }

            }
        });
        int offset = getResources().getDimensionPixelSize(R.dimen.px68);
        menu.show(targetView, offset);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // control
    //

    /**
     * 切换前后摄像头
     */
    public void onSwitchCamera(View view) {

    }

    /**
     * 竖屏模式下点击了开始直播
     */
    public void onStartLiveClick(View view) {

    }

    /**
     * 横屏模式下点击了开始／结束直播
     */
    public void onStartOrStopLiveClick(View view) {

    }

    /**
     * 点击了新增白板
     */
    public void onNewboardClick(View view) {

    }

    /**
     * 点击了白板管理
     */
    public void onBoardMgrClick(View view) {

    }

    /**
     * 点击了显示或者隐藏聊天列表
     */
    public void onTalkVisibilityClick(View view) {

    }


    public void back() {
        getActivity().onBackPressed();
    }

    /**
     * 点击了输入聊天消息
     * @param view
     */
    public void onInputMessageClick(View view) {

    }

    public void onOne2OneClick(View view) {

    }


    private void showOrHiddenCenterPanel() {

        int visibility = centerPanelView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;

        centerPanelView.setVisibility(visibility);
        centerOne2oneView.setVisibility(visibility);
        centerBoardOperaView.setVisibility(visibility);
        centerBoardMgrView.setVisibility(visibility);
        centerNewBoardView.setVisibility(visibility);
        centerMedmberView.setVisibility(visibility);
        centerDatabaseView.setVisibility(visibility);
        centerCanlenderView.setVisibility(visibility);


    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 右边含有侧滑面板
    //

    public void showSlidePanel(Fragment fragment, String tag) {
        rightSlideLayout.setVisibility(View.VISIBLE);

        slideFragment = fragment;

        getChildFragmentManager()
                .beginTransaction()
                .add(R.id.slide_layout, slideFragment)
                .addToBackStack(tag)
                .commitAllowingStateLoss();
    }

    public void exitSlidePanel() {
        rightSlideLayout.setVisibility(View.GONE);

        if (slideFragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .remove(slideFragment)
                    .commitAllowingStateLoss();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 横竖屏切换
    //


    /**
     * 切换横竖屏
     */
    public void changeOrientation() {
        int changeRequest = getActivity().getRequestedOrientation() ==
                ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ?
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        getActivity().setRequestedOrientation(changeRequest);

    }

    /**
     * 切换横屏
     */
    public void changeOrientationToLand() {

        int changeRequest = getActivity().getRequestedOrientation();

        if (changeRequest != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    protected void controlHandleOnRotate(int orientation) {
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                if (controlLand != null) {
                    controlLand.setVisibility(View.VISIBLE);
                }

                if (controlPort != null) {
                    controlPort.setVisibility(View.GONE);
                }

                break;
            case Configuration.ORIENTATION_PORTRAIT:
                if (controlLand != null) {
                    controlLand.setVisibility(View.GONE);
                }

                if (controlPort != null) {
                    controlPort.setVisibility(View.VISIBLE);
                }
                break;
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 播放
    //

    public void configVideoView(PLVideoTextureView videoTextureView) {
        //videoTextureView.setBufferingIndicator(loadingView);
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

        videoTextureView.setAVOptions(options);
        videoTextureView.setDebugLoggingEnabled(true);

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





    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 直播
    //

    /**
     * 开始请求直播
     */
    public void requestLive() {

        if (NetworkUtil.isWIFI(getContext())) {
            requestLivePermission();
        } else {
            showNetworkTips();
        }
    }

    private void showNetworkTips() {
            final CommonDialog tipsDialog= new CommonDialog(getContext());
        tipsDialog.setDesc("您正在使用非WI-FI网络，直播将产生流量费用");
        tipsDialog.setLefBtnText(R.string.cancel);
        tipsDialog.setRightBtnText(R.string.mobile_network_allow);
        tipsDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    requestLivePermission();
                    tipsDialog.dismiss();

                }
            });
        tipsDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                tipsDialog.dismiss();
            }
        });
        tipsDialog.show();
    }

    private void requestLivePermission(){
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        PermissionGen.needPermission(this, REQUEST_PERMISSION, permissions);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @Keep
    @PermissionRationale(requestCode = REQUEST_PERMISSION)
    public void requestCameraRationale() {
        PermissionHelper.showRationaleDialog(this,
                getString(R.string.permission_rationale_camera_audio_tip));
    }

    @Keep
    @PermissionSuccess(requestCode = REQUEST_PERMISSION)
    public void toLive() {
        //个人推流
        personPublishStream();

    }


    /**
     * 个人推流
     */
    protected void personPublishStream() {
        showProgress(true);
        classroomEngine.claimStream(Live.StreamMode.AV,
                new EventCallback<ClaimReponse>() {

                    @Override
                    public void onSuccess(ClaimReponse claimReponse) {
                        cancelProgress();

                        changeOrientationToLand();

                        enterLiving();
                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {
                        cancelProgress();
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void sendStartStreaming() {

        classroomEngine.startStreaming(new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {

            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {

            }
        });
    }


    public void sendStopStreaming() {

        classroomEngine.stopStreaming(Live.StreamType.INDIVIDUAL,
                classroomEngine.getCsOfCurrent(), new EventCallback<StreamStoppedResponse>() {
                    @Override
                    public void onSuccess(StreamStoppedResponse streamStoppedResponse) {

                    }

                    @Override
                    public void onFailed(String errorCode, String errorMessage) {

                    }
                });
    }


}
