package cn.xiaojs.xma.ui.classroom2;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
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
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.material.LibDoc;
import cn.xiaojs.xma.ui.classroom.page.BoardCollaborateFragment;
import cn.xiaojs.xma.ui.classroom.page.MsgInputFragment;
import cn.xiaojs.xma.ui.classroom2.base.MovieFragment;
import cn.xiaojs.xma.ui.classroom2.base.PlayerFragment;
import cn.xiaojs.xma.ui.classroom2.core.BootObservable;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomType;
import cn.xiaojs.xma.ui.classroom2.core.RoomSession;
import cn.xiaojs.xma.ui.classroom2.material.DatabaseFragment;
import cn.xiaojs.xma.ui.classroom2.member.MemberListFragment;
import cn.xiaojs.xma.ui.classroom2.schedule.ScheduleFragment;
import cn.xiaojs.xma.ui.classroom2.util.MaterialUtil;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class Classroom2Activity extends FragmentActivity {

    private final static int REQUEST_PERMISSION = 1000;

    @BindView(R.id.constrant_root)
    ConstraintLayout rootLayout;

    @BindView(R.id.bottom_control_layout)
    LinearLayout bottomControlLayout;

    @BindView(R.id.o2o_root)
    FrameLayout o2oLayout;
    @BindView(R.id.o2o_avator)
    ImageView o2oAvatorView;
    @BindView(R.id.o2o_name)
    TextView o2oNameView;

    private ChatFragment chatFragment;
    private DatabaseFragment databaseFragment;


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


    //进入教室
    public static void invoke(Activity context, String ticket) {
        Intent i = new Intent();
        i.putExtra(CTLConstant.EXTRA_TICKET, ticket);
        i.setClass(context, Classroom2Activity.class);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_classroom2);
        ButterKnife.bind(this);

        //init ticket;
        initTicket = getIntent().getStringExtra(CTLConstant.EXTRA_TICKET);

        onBootlistener(initTicket);
        collaborateFragment = BoardCollaborateFragment.createInstance("");
    }

    @OnClick({R.id.bottom_input, R.id.bottom_members, R.id.bottom_database, R.id.bottom_schedule})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.bottom_input:
                popInput();
                break;
            case R.id.bottom_members:               //教室成员
                popMembers();
                break;
            case R.id.bottom_database:              //资料库
                popDatabase();
                break;
            case R.id.bottom_schedule:              //课表
                popClassSchedule();
                break;
        }
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


        if (movieFragment != null
                && movieFragment.isAdded()
                && movieFragment instanceof LivingFragment) {
            movieFragment.back();
            return;
        }

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

            if (movieFragment != null && movieFragment.isAdded()) {
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
                .subscribeOn(Schedulers.io())
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


        String roomState = classroomEngine.getCtlSession().state;
        if (classroomEngine.getLiveMode() == Live.ClassroomMode.TEACHING &&
                (Live.LiveSessionState.DELAY.equals(roomState) ||
                        Live.LiveSessionState.LIVE.equals(roomState))) {

            showLivingClassDlg();

        } else if (!TextUtils.isEmpty(classroomEngine.getPlayUrl())) {
            enterPlay();
        } else {
            enterIdle();
        }

    }

    private void initChatFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        chatFragment = new ChatFragment();
        Bundle b = new Bundle();
        b.putString(CTLConstant.EXTRA_GROUP_ID, classroomEngine.getCtlSession().cls.id);
        b.putString(CTLConstant.EXTRA_SESSION_NAME, classroomEngine.getClassTitle());
        chatFragment.setArguments(b);
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, 0)
                .add(R.id.bottom_lay, chatFragment)
                .commitAllowingStateLoss();
    }


    protected void showLivingClassDlg() {

        final CommonDialog dialog = new CommonDialog(this);
        dialog.setDesc("您的课还没有结束，是否继续上课？");
        dialog.setRightBtnText(R.string.continue_live);
        dialog.setLefBtnText(R.string.finish_class);
        dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                classroomEngine.finishClass(
                        classroomEngine.getTicket(), new APIServiceCallback<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody object) {
                                enterIdle();
                            }

                            @Override
                            public void onFailure(String errorCode, String errorMessage) {
                                enterIdle();
                            }
                        });

            }
        });

        dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
            @Override
            public void onClick() {
                dialog.dismiss();
                changeOrientation();
                enterLiving();
            }
        });

        dialog.show();
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
                constraintSet.applyTo(rootLayout);
                bottomControlLayout.setVisibility(View.GONE);
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                showSystemUI();
                constraintSet.setDimensionRatio(R.id.replace_lay, "16:9");
                constraintSet.constrainHeight(R.id.replace_lay, 0);
                constraintSet.applyTo(rootLayout);
                bottomControlLayout.setVisibility(View.VISIBLE);
                break;
        }


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

    public void enterPlayback(LibDoc doc) {

        if (movieFragment != null && movieFragment instanceof PlaybackFragment) {
            return;
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        removeOldFragment(fragmentTransaction);

        movieFragment = new PlaybackFragment();
        String[] urlData = {MaterialUtil.getVideoUrl(doc)};
        Bundle bundle = new Bundle();
        bundle.putSerializable(PlayerFragment.EXTRA_OBJECT, doc);
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






    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 底部面板操作
    //
    private void popInput() {

        if (chatFragment !=null && chatFragment.isAdded()) {
            chatFragment.popInput();
        }
    }


    private void popMembers() {
        MemberListFragment memberListfragment = new MemberListFragment();
        memberListfragment.show(getSupportFragmentManager(), "member");
    }

    private void popDatabase() {
        databaseFragment = new DatabaseFragment();
        databaseFragment.show(getSupportFragmentManager(), "database");

    }

    private void popClassSchedule(){
        /*ProfileFragment profileFragment = new ProfileFragment();
        profileFragment.show(getFragmentManager(), "profile");*/
        ScheduleFragment.createInstance("").show(getSupportFragmentManager(),"schedule");
    }


    public void exitDatabaseFragment() {

        if (databaseFragment != null && databaseFragment.isVisible()) {
            databaseFragment.dismiss();
        }

    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    //
    // 一对一的面板操作
    //

    @OnClick({R.id.o2o_argee, R.id.o2o_refused})
    void onO2oClick(View view) {
        switch (view.getId()) {
            case R.id.o2o_argee:
                argeeO2o(true);
                break;
            case R.id.o2o_refused:
                argeeO2o(false);
                break;
        }
    }

    public void showO2oPanel(Attendee attendee) {

        String avatorUrl = Account.getAvatar(attendee.accountId, o2oAvatorView.getMeasuredWidth());
        Glide.with(this)
                .load(avatorUrl)
                .transform(new CircleTransform(this))
                .placeholder(R.drawable.ic_defaultavatar)
                .error(R.drawable.ic_defaultavatar)
                .into(o2oAvatorView);
        o2oNameView.setText(attendee.name);
        o2oLayout.setVisibility(View.VISIBLE);
    }

    private void argeeO2o(boolean argee) {

        o2oLayout.setVisibility(View.GONE);

        if (argee) {
            movieFragment.argeeO2o();
        } else {
            movieFragment.refuseO2o();
        }
    }

    BoardCollaborateFragment collaborateFragment;

    public BoardCollaborateFragment getCollaBorateFragment() {
        return collaborateFragment;
    }

}
