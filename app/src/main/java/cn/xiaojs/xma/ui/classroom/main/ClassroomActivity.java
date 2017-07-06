package cn.xiaojs.xma.ui.classroom.main;

import android.Manifest;
import android.content.res.Configuration;

import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import android.view.KeyEvent;
import android.widget.Toast;

import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.model.live.CtlSession;

import cn.xiaojs.xma.ui.classroom.page.PhotoDoodleFragment;
import cn.xiaojs.xma.ui.classroom.talk.ContactManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkManager;
import cn.xiaojs.xma.ui.classroom2.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.ClassroomType;
import cn.xiaojs.xma.ui.classroom2.SessionListener;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;

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
 * Date:2017/4/27
 * Desc:
 *
 * ======================================================================================== */

public class ClassroomActivity extends FragmentActivity implements SessionListener{
    private final static int REQUEST_PERMISSION = 1000;

    private ProgressHUD mProgress;
    private CommonDialog mKickOutDialog;
    private CommonDialog mContinueConnectDialog;

    private ClassroomEngine classroomEngine;
    private String ticket;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        ticket = getIntent().getStringExtra(Constants.KEY_TICKET);
        classroomEngine = ClassroomEngine.getEngine(this,this);

        ClassroomController.init(this);

        //init data
        initData();

        //grant permission
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};
        PermissionGen.needPermission(this, REQUEST_PERMISSION, permissions);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //release
        ClassroomController.getInstance().release();
        LiveCtlSessionManager.getInstance().release();
        ContactManager.getInstance().release();
        TalkManager.getInstance().release();

        classroomEngine.destoryEngine();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            List<Fragment> fragmentList = fragmentManager.getFragments();
            Fragment topFragment = null;
            if (fragmentList != null && fragmentList.size() > 0) {
                topFragment = fragmentList.get(fragmentList.size() - 1);
            }
            if (topFragment instanceof PhotoDoodleFragment) {
                if (((PhotoDoodleFragment) topFragment).isEditMode()) {
                    ((PhotoDoodleFragment) topFragment).exitEdiModeWhitTips();
                    return false;
                }
            }
            int backStackEntryCount = fragmentManager.getBackStackEntryCount();
            if (backStackEntryCount < 1) {
                if (ClassroomController.getInstance().getStackFragment() != null) {
                    ClassroomController.getInstance().onActivityBackPressed(backStackEntryCount);
                } else {
                    ClassroomController.getInstance().showExitClassroomDialog();
                }
                return false;
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    /**
     * 初始化, 启动bootSession
     */
    private void initData() {
//        if (ClassroomBusiness.getCurrentNetwork(this) == ClassroomBusiness.NETWORK_NONE) {
//            return;
//        }

        showProgress(true);
        classroomEngine.init(ticket);
    }



    private void onSucc() {

        String state = classroomEngine.getLiveState();
        if (Live.LiveSessionState.CANCELLED.equals(state)) {
            Toast.makeText(this, R.string.forbidden_enter_class_for_cancel, Toast.LENGTH_SHORT).show();
            //退出教室
            finish();
        }

        CtlSession ctlSession = classroomEngine.getRoomSession().ctlSession;
        int appType = ctlSession.connected != null ? ctlSession.connected.app : Platform.AppType.UNKNOWN;
        //二维码扫描进入教室，需要更新ticket.
        if (!TextUtils.isEmpty(ctlSession.ticket)) {
            //TODO 要更新ticket？
            ticket = ctlSession.ticket;
        }
        //FIXME 先保证不出错，之后要干掉LiveCtlSessionManager
        LiveCtlSessionManager.getInstance().init(ctlSession,ticket);


        ContactManager.getInstance().init();
        TalkManager.getInstance().init(ClassroomActivity.this, ticket);

        //init fragment

        ClassroomLiveFragment liveFragment = ClassroomController.getInstance().getStackFragment();

        if (liveFragment == null) {
            initFragment(ctlSession);
        } else if (liveFragment instanceof PlayFragment) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(liveFragment)
                    .commit();

            ClassroomController.getInstance().setStackFragment(null);

            initFragment(ctlSession);
        }
    }

    private void initFragment(CtlSession ctlSession) {
        //Fragment fragment = null;
        Constants.UserMode mode = ClassroomBusiness.getUserByCtlSession(ctlSession);
        if (mode == Constants.UserMode.TEACHING &&
                (Live.LiveSessionState.DELAY.equals(ctlSession.state) ||
                        Live.LiveSessionState.LIVE.equals(ctlSession.state))) {
            //teacher-->live, delay

            Bundle data = new Bundle();

            ClassroomType cType = classroomEngine.getClassroomType();
            boolean isPrivateClass = cType == ClassroomType.ClassLesson? true : false;
            if (isPrivateClass) {
                data.putBoolean(Constants.KEY_SHOW_CLASS_LESSON_TIPS, true);
                data.putString(Constants.KEY_PUBLISH_URL, ctlSession.publishUrl);
                ClassroomController.getInstance().enterPlayFragment(data, false);

            } else {
                data.putInt(Constants.KEY_FROM, Constants.FROM_ACTIVITY);
                data.putSerializable(Constants.KEY_PUBLISH_TYPE, CTLConstant.StreamingType.PUBLISH_LIVE);
                data.putString(Constants.KEY_PUBLISH_URL, ctlSession.publishUrl);
                ClassroomController.getInstance().enterPublishFragment(data, false);
            }


        } else {
            ClassroomController.getInstance().enterPlayFragment(null, false);
        }
    }


//    private SocketManager.EventListener mKickoutByUserListener = new SocketManager.EventListener() {
//        @Override
//        public void call(Object... args) {
//
//            if (XiaojsConfig.DEBUG) {
//                Logger.d("Received event: **Su.EventType.KICKOUT_DUE_TO_NEW_CONNECTION**");
//            }
//
//            Toast.makeText(ClassroomActivity.this, R.string.mobile_kick_out_tips, Toast.LENGTH_LONG).show();
//            finish();
//        }
//    };
//
//    private SocketManager.EventListener mModeSwitchListener = new SocketManager.EventListener() {
//        @Override
//        public void call(Object... args) {
//
//
//            if (XiaojsConfig.DEBUG) {
//                Logger.d("Received event: **Su.EventType.CLASS_MODE_SWITCH**");
//            }
//
//
//            //解析并更新session mode
//            if (args != null && args.length > 0) {
//                ModeSwitcher switcher = ClassroomBusiness.parseSocketBean(args[0], ModeSwitcher.class);
//                LiveCtlSessionManager.getInstance().updateCtlSessionMode(switcher.to);
//            }
//
//        }
//    };

    public void showProgress(boolean cancellable) {
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

    /**
     * 是否继续连接教室
     */
    private void showConnectClassroom(String errorTips) {
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
                    initData();
                }
            });

            mContinueConnectDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    ClassroomActivity.this.finish();
                }
            });
        }


        ClassroomController.getInstance().enterPlayFragment(null, true);
        mContinueConnectDialog.show();
    }

    /**
     * 检测是否需要强制踢出
     */
    private void checkForceKickOut(final CtlSession ctlSession, final boolean netWorkChanged) {
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
                    onSucc();
                }
            });

            mKickOutDialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    mKickOutDialog.dismiss();
                    Toast.makeText(ClassroomActivity.this, R.string.mobile_kick_out_cancel, Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        mKickOutDialog.show();
    }

    @Override
    public void connectSuccess() {

        cancelProgress();
        if (mContinueConnectDialog != null && mContinueConnectDialog.isShowing()) {
            mContinueConnectDialog.dismiss();
        }
        Toast.makeText(ClassroomActivity.this, R.string.socket_connect, Toast.LENGTH_LONG).show();
        onSucc();
        //TODO 连接成功后，需要获取联系人数据
        //ContactManager.getInstance().getAttendees(ClassroomActivity.this, null);
    }

    @Override
    public void connectFailed(String errorCode, String errorMessage) {
        cancelProgress();
        showConnectClassroom(null);

        if (XiaojsConfig.DEBUG) {
            Toast.makeText(ClassroomActivity.this, R.string.socket_disconnect, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void networkStateChanged(int state) {
        //TODO
    }
}
