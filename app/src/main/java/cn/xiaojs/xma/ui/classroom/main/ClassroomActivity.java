package cn.xiaojs.xma.ui.classroom.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import android.view.KeyEvent;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.ApiManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.data.api.socket.SocketManager;
import cn.xiaojs.xma.model.live.CtlSession;

import cn.xiaojs.xma.model.socket.room.ConstraintKickoutReceive;
import cn.xiaojs.xma.ui.classroom.page.PhotoDoodleFragment;
import cn.xiaojs.xma.ui.classroom.talk.ContactManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkManager;
import cn.xiaojs.xma.ui.classroom2.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.ClassroomType;
import cn.xiaojs.xma.ui.classroom2.EventListener;
import cn.xiaojs.xma.ui.classroom2.RoomSession;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

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

public class ClassroomActivity extends FragmentActivity implements EventListener {
    private final static int REQUEST_PERMISSION = 1000;

    private final int MSG_CONNECT_SUCCESS = 1;
    private final int MSG_CONNECT_ERROR = 2;
    private final int MSG_CONNECT_TIMEOUT = 3;
    private final int MSG_NETWORK_CHANGED = 4;

    private ProgressHUD mProgress;
    private CommonDialog mKickOutDialog;
    private CommonDialog mContinueConnectDialog;

    private ClassroomEngine classroomEngine;
    private String ticket;

    private SocketManager socketManager;
    private NetworkChangedReceiver networkChangedReceiver;
    private CtlSession tempSession;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_CONNECT_SUCCESS:     //连接教室成功
                    connectSuccess();
                    break;
                case MSG_CONNECT_ERROR:       //连接教室失败
                case MSG_CONNECT_TIMEOUT:     //连接教室超时

                    connectFailed("-1", "");
                    break;
                case MSG_NETWORK_CHANGED:     //网络状态改变
                    networkStateChanged(msg.arg1);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        ticket = getIntent().getStringExtra(Constants.KEY_TICKET);
        socketManager = SocketManager.getSocketManager(this);
        //init data
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        reset();

        //release
        if (ClassroomController.getInstance() != null) {
            ClassroomController.getInstance().release();
        }
        if (ContactManager.getInstance() != null) {
            ContactManager.getInstance().release();
        }

        if (TalkManager.getInstance() != null) {
            TalkManager.getInstance().release();
        }


        if (classroomEngine != null) {
            classroomEngine.destoryEngine();
        }

        disConnectSocket();
        socketManager = null;


    }

    private void reset() {
        if (classroomEngine != null) {
            classroomEngine.removeEvenListener(this);
        }

        unRegisteNetwork();
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

            if (backStackEntryCount < 1 && ClassroomController.getInstance() != null) {

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

        reset();

        showProgress(true);
        LiveManager.bootSession(this, ticket, new APIServiceCallback<CtlSession>() {
            @Override
            public void onSuccess(CtlSession session) {

                try {
                    tempSession = session;
                    //连接socket

                    connectSocket(ticket, session.secret, !session.accessible);

                } catch (Exception e) {
                    e.printStackTrace();

                    handler.removeMessages(MSG_CONNECT_TIMEOUT);
                    handler.removeMessages(MSG_CONNECT_ERROR);
                    handler.sendEmptyMessage(MSG_CONNECT_ERROR);
                }

            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                cancelProgress();
                showConnectClassroom(errorMessage);
            }
        });
    }


    private void connectSocket(String ticket, String secret, boolean force) {
        String url = getClassroomUrl(ticket);

        handler.sendEmptyMessageDelayed(MSG_CONNECT_TIMEOUT, 10 * 1000);

        try {
            socketManager.initSocket(url, buildOptions(secret, force));
            socketManager.on(Socket.EVENT_CONNECT, connectListener);
            socketManager.on(Socket.EVENT_DISCONNECT, disConnectListener);
            socketManager.on(Socket.EVENT_CONNECT_ERROR, errorListener);
            socketManager.on(Socket.EVENT_CONNECT_TIMEOUT, timeoutListener);
            socketManager.connect();
        } catch (Exception e) {
            e.printStackTrace();
            handler.removeMessages(MSG_CONNECT_TIMEOUT);
            handler.removeMessages(MSG_CONNECT_ERROR);
            handler.sendEmptyMessage(MSG_CONNECT_ERROR);

        }
    }


    private void onSucc() {

        registeNetwork();

        //grant permission
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};
        PermissionGen.needPermission(this, REQUEST_PERMISSION, permissions);

        //二维码扫描进入教室，需要更新ticket.
        if (!TextUtils.isEmpty(tempSession.ticket)) {
            //TODO 要更新ticket？
            ticket = tempSession.ticket;
        }

        classroomEngine = ClassroomEngine.getEngine();
        classroomEngine.init(this, ticket, new RoomSession(tempSession));
        classroomEngine.addEvenListener(this);

        String state = classroomEngine.getLiveState();
        if (Live.LiveSessionState.CANCELLED.equals(state)) {
            Toast.makeText(this, R.string.forbidden_enter_class_for_cancel, Toast.LENGTH_SHORT).show();
            //退出教室
            finish();
        }

        CtlSession ctlSession = classroomEngine.getCtlSession();

        ClassroomController.init(this);


        ContactManager.getInstance().init();
        TalkManager.getInstance().init(ClassroomActivity.this, ticket);
        ContactManager.getInstance().getAttendees(ClassroomActivity.this, null);

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
        if (classroomEngine.getLiveMode() == Live.ClassroomMode.TEACHING &&
                (Live.LiveSessionState.DELAY.equals(ctlSession.state) ||
                        Live.LiveSessionState.LIVE.equals(ctlSession.state))) {
            //teacher-->live, delay

            Bundle data = new Bundle();

            ClassroomType cType = classroomEngine.getClassroomType();
            boolean isPrivateClass = cType == ClassroomType.ClassLesson ? true : false;
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

    @Override
    public void receivedEvent(String event, Object object) {
        if (Su.getEventSignature(Su.EventCategory.LIVE, Su.EventType.KICK_OUT_BY_CONSTRAINT).equals(event)) {

            if (object == null) {
                return;
            }
            ConstraintKickoutReceive receive = (ConstraintKickoutReceive) object;
            String xaStr = getString(R.string.mobile_kick_out_tips, getNameByXa(receive.xa));
            Toast.makeText(ClassroomActivity.this, xaStr, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private String getNameByXa(int xa) {
        switch (xa) {
            case Platform.AppType.MOBILE_ANDROID:
                return "安卓手机端";
            case Platform.AppType.MOBILE_IOS:
                return "苹果手机端";
            case Platform.AppType.MOBILE_WEB:
                return "WEB浏览器端";
            default:
                return "客户端";

        }
    }

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


    private String getClassroomUrl(String ticket) {
        return new StringBuilder(ApiManager.getXLSUrl(this))
                .append("/")
                .append(ticket)
                .toString();
    }

    private IO.Options buildOptions(String secret, boolean force) {

        String forceStr = force ? "true" : "false";

        IO.Options opts = new IO.Options();
        opts.query = new StringBuilder("secret=")
                .append(secret)
                .append("&avc={\"video\":")
                .append(true)
                .append(",\"audio\":")
                .append(true)
                .append("}&forcibly=")
                .append(forceStr)
                .toString();

        if (XiaojsConfig.DEBUG) {
            Logger.d("IO.Options query: " + opts.query);
        }

        opts.timeout = 20 * 1000; //ms
        opts.transports = new String[]{"websocket"};

        return opts;
    }

    /**
     * 是否继续连接教室
     */
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

                    ClassroomController classroomController = ClassroomController.getInstance();
                    if (classroomController != null) {
                        classroomController.exitWhenReConnect();
                    }


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
                    //onSucc();
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

    private void disConnectSocket() {
        if (socketManager != null) {
            socketManager.disConnect();
        }
    }

    private void registeNetwork() {
        networkChangedReceiver = new NetworkChangedReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkChangedReceiver, filter);
    }

    private void unRegisteNetwork() {
        if (networkChangedReceiver != null) {
            unregisterReceiver(networkChangedReceiver);
            networkChangedReceiver = null;
        }

    }


    /**
     * 网络切换监听
     */
    public class NetworkChangedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();

            boolean mobileNet = mobileInfo == null ? false : mobileInfo.isConnected();
            boolean wifiNet = wifiInfo == null ? false : wifiInfo.isConnected();
            String activeNet = activeInfo == null ? "null" : activeInfo.getTypeName();

            if (activeInfo == null) {
                //没有网络
                Message message = new Message();
                message.what = MSG_NETWORK_CHANGED;
                message.arg1 = CTLConstant.NetworkType.TYPE_NONE;
                handler.sendMessage(message);

            } else if (wifiNet) {
                // WIFI
                Message message = new Message();
                message.what = MSG_NETWORK_CHANGED;
                message.arg1 = CTLConstant.NetworkType.TYPE_WIFI;
                handler.sendMessage(message);
            } else if (mobileNet) {
                // mobile network
                Message message = new Message();
                message.what = MSG_NETWORK_CHANGED;
                message.arg1 = CTLConstant.NetworkType.TYPE_MOBILE;
                handler.sendMessage(message);
            }
        }
    }

    private Emitter.Listener connectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            handler.removeMessages(MSG_CONNECT_TIMEOUT);
            handler.removeMessages(MSG_CONNECT_SUCCESS);
            handler.sendEmptyMessage(MSG_CONNECT_SUCCESS);
        }
    };

    private Emitter.Listener disConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //断开连接
            handler.removeMessages(MSG_CONNECT_TIMEOUT);
            handler.removeMessages(MSG_CONNECT_ERROR);
            handler.sendEmptyMessage(MSG_CONNECT_ERROR);
        }
    };

    private Emitter.Listener errorListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //连接出错
            handler.removeMessages(MSG_CONNECT_TIMEOUT);
            handler.removeMessages(MSG_CONNECT_ERROR);
            handler.sendEmptyMessage(MSG_CONNECT_ERROR);
        }
    };

    private Emitter.Listener timeoutListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //连接超时
            handler.removeMessages(MSG_CONNECT_TIMEOUT);
            handler.sendEmptyMessage(MSG_CONNECT_TIMEOUT);
        }
    };


    public void connectSuccess() {
        cancelProgress();
        if (mContinueConnectDialog != null && mContinueConnectDialog.isShowing()) {
            mContinueConnectDialog.dismiss();
        }
        Toast.makeText(ClassroomActivity.this, R.string.socket_connect, Toast.LENGTH_LONG).show();
        onSucc();
    }

    public void connectFailed(String errorCode, String errorMessage) {
        cancelProgress();

        ClassroomController classroomController = ClassroomController.getInstance();
        if (classroomController != null) {
            classroomController.enterPlayFragment(null,true);
        }

        disConnectSocket();
        showConnectClassroom(null);

        if (XiaojsConfig.DEBUG) {
            Toast.makeText(ClassroomActivity.this, R.string.socket_disconnect, Toast.LENGTH_LONG).show();
        }
    }

    public void networkStateChanged(int state) {
        //TODO
    }


}
