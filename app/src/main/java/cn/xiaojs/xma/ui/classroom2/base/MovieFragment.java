package cn.xiaojs.xma.ui.classroom2.base;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.socket.EventCallback;
import cn.xiaojs.xma.model.socket.EventResponse;
import cn.xiaojs.xma.model.socket.room.ClaimReponse;
import cn.xiaojs.xma.model.socket.room.StreamStoppedResponse;
import cn.xiaojs.xma.ui.classroom2.ClassDetailFragment;
import cn.xiaojs.xma.ui.classroom2.Classroom2Activity;
import cn.xiaojs.xma.ui.classroom2.SettingFragment;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public abstract class MovieFragment extends BaseRoomFragment {

    @BindView(R.id.control_port)
    public ConstraintLayout controlPort;

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


    public final static int REQUEST_PERMISSION = 3;

    protected ClassroomEngine classroomEngine;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classroomEngine = ClassroomEngine.getEngine();
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

        }
    }


    @OnClick({R.id.center_one2one, R.id.center_board_opera,
            R.id.center_board_mgr, R.id.center_new_board, R.id.center_member,
            R.id.center_database, R.id.center_canlender})
    void onCenterPanelItemClick(View view) {
        switch (view.getId()) {
            case R.id.center_one2one:                                             //一对一音视频
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
    }


    @OnClick({R.id.p_top_back, R.id.p_top_more, R.id.p_bottom_orient})
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
     * 返回
     */
    public void back() {
        getActivity().onBackPressed();
    }


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
     * 开始请求直播
     */
    public void requestLive() {
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

                        changeOrientation();

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

    protected void controlHandleOnRotate(int orientation) {
        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                controlLand.setVisibility(View.VISIBLE);
                controlPort.setVisibility(View.GONE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                controlLand.setVisibility(View.GONE);
                controlPort.setVisibility(View.VISIBLE);
                break;
        }
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

}
