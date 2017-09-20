package cn.xiaojs.xma.ui.classroom2;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.ui.classroom2.core.BootObservable;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * Created by maxiaobao on 2017/9/18.
 */

public class Classroom2Activity extends FragmentActivity {

    @BindView(R.id.back_btn)
    ImageView backBtn;
    @BindView(R.id.constrant_root)
    ConstraintLayout rootLayout;
    @BindView(R.id.scale_btn)
    ImageView scaleBtn;
    @BindView(R.id.lesson_title)
    TextView titleView;


    private BootObservable.BootListener bootListener;
    private String initTicket;
    private MovieFragment movieFragment;

    private ProgressHUD mProgress;
    private CommonDialog mKickOutDialog;
    private CommonDialog mContinueConnectDialog;


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
                            handleConnectSuccess();
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
        setContentView(R.layout.activity_classroom2);
        ButterKnife.bind(this);

        //Init defalut fragment
        movieFragment = new PlayFragment();
        //movieFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction()
                .add(R.id.replace_lay, movieFragment)
                .commit();


        //init ticket;
        initTicket = getIntent().getStringExtra(CTLConstant.EXTRA_TICKET);

        onBootlistener(initTicket);
    }

    @Override
    public void onBackPressed() {
        showExitClassroomDialog();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        offBootlistener();
        SocketManager.getSocketManager(this).disConnect();

//        if (classroomEngine != null) {
//            classroomEngine.destoryEngine();
//        }


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation != Configuration.ORIENTATION_UNDEFINED) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("the orientation has changed to: %d", newConfig.orientation);
            }

            resetSizeWhenOrientationChanged(newConfig.orientation);
        }

    }

    @OnClick({R.id.back_btn, R.id.scale_btn})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                onBackPressed();
                break;
            case R.id.scale_btn:
                changeOrientation();
                break;
        }
    }

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

    private void handleBootSuccess(BootObservable.BootSession bootSession) {

        //TODO 可更新教室标题
        titleView.setText("lesson title");


    }

    private void handleConnectSuccess() {
        cancelProgress();
        Toast.makeText(Classroom2Activity.this,
                R.string.socket_connect, Toast.LENGTH_LONG).show();
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

    private void changeOrientation() {
        int changeRequest = getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ?
                ActivityInfo.SCREEN_ORIENTATION_PORTRAIT : ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        setRequestedOrientation(changeRequest);
    }

    private void resetSizeWhenOrientationChanged(int orientation) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(rootLayout);

        switch (orientation) {
            case Configuration.ORIENTATION_LANDSCAPE:
                constraintSet.setDimensionRatio(R.id.replace_lay, null);
                constraintSet.constrainHeight(R.id.replace_lay, rootLayout.getWidth());

                break;
            case Configuration.ORIENTATION_PORTRAIT:
                constraintSet.setDimensionRatio(R.id.replace_lay, "4:3");
                constraintSet.constrainHeight(R.id.replace_lay, 0);
                break;
        }
        constraintSet.applyTo(rootLayout);
    }

    private void showProgress(boolean cancellable) {

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

    private void cancelProgress() {
        if (mProgress != null && mProgress.isShowing()) {
            mProgress.dismiss();
        }
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



}
