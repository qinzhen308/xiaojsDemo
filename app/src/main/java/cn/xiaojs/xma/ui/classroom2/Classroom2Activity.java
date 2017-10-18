package cn.xiaojs.xma.ui.classroom2;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.ui.classroom.main.*;
import cn.xiaojs.xma.ui.classroom.page.BoardCollaborateFragment;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;
import cn.xiaojs.xma.ui.classroom2.core.BootObservable;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomType;
import cn.xiaojs.xma.ui.classroom2.core.RoomSession;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class Classroom2Activity extends FragmentActivity {

    private final static int REQUEST_PERMISSION = 1000;

    @BindView(R.id.constrant_root)
    ConstraintLayout rootLayout;


    private BootObservable.BootListener bootListener;
    private String initTicket;
    private MovieFragment movieFragment;

    private ProgressHUD mProgress;
    private CommonDialog mKickOutDialog;
    private CommonDialog mContinueConnectDialog;


    private ClassroomEngine classroomEngine;


    private Consumer<BootObservable.BootSession> bootSessionConsumer =
            new Consumer<BootObservable.BootSession>() {
                @Override
                public void accept(BootObservable.BootSession bootSession) throws Exception {

                    switch (bootSession.status) {
                        case BOOT_BEGIN:
                            showProgress(false);
                            break;
                        case BOOT_QUERY_KICKOUT:
                            cancelProgress();
                            queryKickOut();
                            break;
                        case BOOT_SUCCESS:
                            handleBootSuccess(bootSession);
                            break;
                        case SOCKET_CONNECT_BEGIN:
                            showProgress(false);
                            break;
                        case SOCKET_CONNECT_SUCCESS:
                            handleConnectSuccess(bootSession);
                            break;
                        case KICK_OUT_BY_CONSTRAINT:
                            handleKickedOut();
                            break;
                        case SOCKET_DISCONNECTED:
                        case BOOT_FAILED:
                        case SOCKET_CONNECT_FAILED:
                        case SOCKET_CONNECT_TIMEOUT:
                        case RECONNECT_FAILED:
                            handleConnectError();
                            break;


                    }
                }
            };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_classroom2);
        ButterKnife.bind(this);

        //init ticket;
        initTicket = getIntent().getStringExtra(CTLConstant.EXTRA_TICKET);

        onBootlistener(initTicket);
        // TODO: 2017/10/18 需要加上白板id
        collaborateFragment=BoardCollaborateFragment.createInstance("");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {

        if (movieFragment instanceof PlaybackFragment) {
            return super.dispatchKeyEvent(event)
                    || ((PlaybackFragment) movieFragment).dispatchKeyEvent(event);
        }

        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {

        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            changeOrientation();
            return;
        }

        showExitClassroomDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        offBootlistener();
        SocketManager.getSocketManager(this).disConnect();

        if (classroomEngine != null) {
            classroomEngine.destoryEngine();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
//            hideSystemUI();
//        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            hideSystemUI();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation != Configuration.ORIENTATION_UNDEFINED) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the orientation has changed to: %d", newConfig.orientation);
            }

            resetSizeWhenOrientationChanged(newConfig.orientation);

            if (movieFragment != null) {
                movieFragment.onRotate(newConfig.orientation);
            }
        }

    }

    @Keep
    @PermissionRationale(requestCode = REQUEST_PERMISSION)
    public void requestCameraRationale() {
        PermissionHelper.showRationaleDialog(this,
                getString(R.string.permission_rationale_camera_audio_room_tip));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }


    public void showProgress(boolean cancellable) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed()) {
                return;
            }
        }

        if (mProgress == null) {
            mProgress = ProgressHUD.create(this);
        }
        mProgress.setCancellable(cancellable);
        mProgress.show();
    }

    public void cancelProgress() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // Handle methods
    //
    private void handleBootSuccess(BootObservable.BootSession bootSession) {

        //TODO 可更新教室标题

    }

    private void handleConnectSuccess(BootObservable.BootSession bootSession) {
        cancelProgress();
        Toast.makeText(Classroom2Activity.this,
                R.string.socket_connect, Toast.LENGTH_LONG).show();


        //grant permission
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};
        PermissionGen.needPermission(this, REQUEST_PERMISSION, permissions);


        classroomEngine = ClassroomEngine.getEngine();
        classroomEngine.init(this, bootSession.ctlSession.ticket,
                new RoomSession(bootSession.ctlSession));


        String state = classroomEngine.getLiveState();
        if (classroomEngine.getClassroomType() == ClassroomType.StandaloneLesson
                && Live.LiveSessionState.CANCELLED.equals(state)) {
            Toast.makeText(this, R.string.forbidden_enter_class_for_cancel, Toast.LENGTH_SHORT)
                    .show();
            //退出教室
            finish();
        }

        initMovieFragment();

        initChatFragment();
    }

    private void handleConnectError() {
        cancelProgress();
        showConnectClassroom("");
    }

    private void handleKickedOut() {
        Toast.makeText(Classroom2Activity.this,
                R.string.mobile_kick_out_tips, Toast.LENGTH_LONG).show();
        finish();
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // inner use methods
    //

    private void onBootlistener(String ticket) {

        offBootlistener();

        BootObservable bootObservable = new BootObservable(this, ticket);
        bootObservable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bootSessionConsumer);
        bootListener = bootObservable.getBootListener();
    }

    private void offBootlistener() {
        if (bootListener != null && !bootListener.isDisposed()) {
            bootListener.dispose();
        }
    }


    private void initMovieFragment() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed()) {
                return;
            }
        }

        enterIdle();


//        String roomState = classroomEngine.getCtlSession().state;
//        if (classroomEngine.getLiveMode() == Live.ClassroomMode.TEACHING &&
//                (Live.LiveSessionState.DELAY.equals(roomState) ||
//                        Live.LiveSessionState.LIVE.equals(roomState))) {
//
//            switch (classroomEngine.getClassroomType()) {
//                case ClassLesson:                        //如果是班课，则进入播放页面
//                    enterPlay();
//                    break;
//                default:                                 //进入直播推流页面
//                    enterLiving();
//                    break;
//            }
//
//            return;
//
//        }
//
//
//        if (Live.LiveSessionState.LIVE.equals(roomState)) {
//            enterPlay();
//        } else {
//            //fragment nothing
//        }
    }

    private void initChatFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        ChatFragment chatFragment = new ChatFragment();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, 0)
                .add(R.id.bottom_lay, chatFragment)
                .commitAllowingStateLoss();
    }

    private void changeOrientation() {
        if (movieFragment != null) {
            movieFragment.changeOrientation();
        }
    }

    private void resetSizeWhenOrientationChanged(int orientation) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(rootLayout);

        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                hideSystemUI();
                constraintSet.setDimensionRatio(R.id.replace_lay, null);
                constraintSet.constrainHeight(R.id.replace_lay, rootLayout.getWidth());
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                showSystemUI();
                constraintSet.setDimensionRatio(R.id.replace_lay, "16:9");
                constraintSet.constrainHeight(R.id.replace_lay, 0);
                break;
        }
        constraintSet.applyTo(rootLayout);
    }


    //Hides the system bars.
    public void hideSystemUI() {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.

        View decorView = getWindow().getDecorView();

        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    // Shows the system bars.
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private void queryKickOut() {
        if (mKickOutDialog == null) {
            mKickOutDialog = new CommonDialog(this);
            mKickOutDialog.setDesc(R.string.mobile_kick_out_desc);
            mKickOutDialog.setLefBtnText(R.string.cancel);
            mKickOutDialog.setRightBtnText(R.string.ok);
            mKickOutDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    //强制登录
                    mKickOutDialog.dismiss();
                    bootListener.continueConnect();
                }
            });

            mKickOutDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mKickOutDialog.dismiss();
                    Toast.makeText(Classroom2Activity.this, R.string.mobile_kick_out_cancel,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        if (!mKickOutDialog.isShowing()) {
            mKickOutDialog.show();
        }

    }

    private void showConnectClassroom(String errorTips) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed()) {
                return;
            }
        }

        if (mContinueConnectDialog == null) {
            mContinueConnectDialog = new CommonDialog(this);
            mContinueConnectDialog.setTitle(R.string.cr_live_connect_fail_title);
            if (TextUtils.isEmpty(errorTips)) {
                mContinueConnectDialog.setDesc(R.string.cr_live_connect_fail_desc);
            } else {
                mContinueConnectDialog.setDesc(errorTips);
            }
            mContinueConnectDialog.setCancelable(false);
            mContinueConnectDialog.setLefBtnText(R.string.cr_live_connect_fail_exit);
            mContinueConnectDialog.setRightBtnText(R.string.cr_live_connect_fail_continue);
            mContinueConnectDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mContinueConnectDialog.dismiss();

                    onBootlistener(initTicket);
                }
            });

            mContinueConnectDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    Classroom2Activity.this.finish();
                }
            });
        }

        mContinueConnectDialog.show();
    }


    public void showExitClassroomDialog() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (isDestroyed()) {
                return;
            }
        }

        final CommonDialog exitDialog = new CommonDialog(this);
        exitDialog.setTitle(R.string.tips);
        exitDialog.setDesc(R.string.exit_classroom);
        exitDialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                finish();
                exitDialog.dismiss();
            }
        });

        exitDialog.show();
    }





    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // movie fragments control
    //



    public void enterIdle() {

        if (movieFragment != null && movieFragment instanceof IdleFragment) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        removeOldFragment(fragmentTransaction);

        movieFragment = new IdleFragment();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, 0)
                .add(R.id.replace_lay, movieFragment)
                .commitAllowingStateLoss();


    }


    public void enterPlay() {

        if (movieFragment != null && movieFragment instanceof PlayFragment) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        removeOldFragment(fragmentTransaction);

        movieFragment = new PlayFragment();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, 0)
                .add(R.id.replace_lay, movieFragment)
                .commitAllowingStateLoss();


    }

    public void enterLiving() {

        if (movieFragment != null && movieFragment instanceof LivingFragment) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        removeOldFragment(fragmentTransaction);

        movieFragment = new LivingFragment();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, 0)
                .add(R.id.replace_lay, movieFragment)
                .commitAllowingStateLoss();


    }

    public void enterPlayback() {

        if (movieFragment != null && movieFragment instanceof PlaybackFragment) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        removeOldFragment(fragmentTransaction);

        movieFragment = new PlaybackFragment();
        String[] urlData = {"https://html5demos.com/assets/dizzy.mp4"};
        Bundle bundle = new Bundle();
        bundle.putBoolean(PlaybackFragment.AUTO_PLAY, false);
        bundle.putStringArray(PlaybackFragment.URI_LIST_EXTRA, urlData);

        movieFragment.setArguments(bundle);

        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, 0)
                .add(R.id.replace_lay, movieFragment)
                .commitAllowingStateLoss();


    }

    private void removeOldFragment(FragmentTransaction transaction) {
        if (movieFragment != null) {
            transaction.remove(movieFragment);
        }
    }

    BoardCollaborateFragment collaborateFragment;
    public BoardCollaborateFragment getCollaBorateFragment(){
        return collaborateFragment;
    }

}
