package cn.xiaojs.xma.ui.classroom2.base;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

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

    public final static int REQUEST_PERMISSION = 3;

    protected ClassroomEngine classroomEngine;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        classroomEngine = ClassroomEngine.getEngine();
    }

    /**
     * 关闭当前的fragment
     */
    public abstract void closeMovie();

    /**
     * 响应屏幕横竖屏方向改变
     */
    public abstract void onRotate(int orientation);

    /**
     * 返回
     */
    public void back() {
        ((Classroom2Activity) getActivity()).onBackPressed();
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

    public void sendStartStreaming(){

        classroomEngine.startStreaming(new EventCallback<EventResponse>() {
            @Override
            public void onSuccess(EventResponse response) {

            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {

            }
        });
    }


    public void sendStopStreaming(){

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
