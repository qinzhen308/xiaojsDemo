package cn.xiaojs.xma.ui.classroom.main;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.permissiongen.PermissionGen;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.xf_foundation.Errors;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.common.xf_foundation.schemas.Live;
import cn.xiaojs.xma.common.xf_foundation.schemas.Platform;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.live.CtlSession;
import cn.xiaojs.xma.ui.MainActivity;
import cn.xiaojs.xma.ui.classroom.bean.ModeSwitcher;
import cn.xiaojs.xma.ui.classroom.live.StreamType;
import cn.xiaojs.xma.ui.classroom.page.PhotoDoodleFragment;
import cn.xiaojs.xma.ui.classroom.socketio.Event;
import cn.xiaojs.xma.ui.classroom.socketio.SocketManager;
import cn.xiaojs.xma.ui.classroom.talk.ContactManager;
import cn.xiaojs.xma.ui.classroom.talk.TalkManager;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.ToastUtil;
import io.socket.client.Socket;

import static cn.xiaojs.xma.ui.MainActivity.PERMISSION_CODE;

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

public class ClassroomActivity extends FragmentActivity {
    private final static int REQUEST_PERMISSION = 1000;

    private final static int MSG_SOCKET_TIME_OUT = 0;
    private final static int SOCKET_CONNECT_TIMEOUT = 1;

    //socket time out
    private final static int SOCKET_TIME_OUT = 5 * 1000; //10s
    //socket retry count after time out
    private final static int RETRY_COUNT = 3;

    private ProgressHUD mProgress;
    private NetworkChangedBReceiver mNetworkChangedBReceiver;

    private CommonDialog mKickOutDialog;
    private CommonDialog mContinueConnectDialog;

    private Socket mSocket;
    private Boolean mSktConnected = false;

    private String mTicket = "";
    private CtlSession mCtlSession;
    private int mAppType = Platform.AppType.UNKNOWN;

    //socket retry count
    private int mSocketRetryCount = 0;

    private TextView mEmptyTips;
    private int mNetworkState = ClassroomBusiness.NETWORK_NONE;
    //private boolean mNeedInitStream; //init stream after socket connected
    private boolean mNetworkDisconnected = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classroom);

        //init params
        initParams();
        //init data
        initData(true, false);

        //grant permission
        String[] permissions = {Manifest.permission.CAMERA , Manifest.permission.RECORD_AUDIO};
        PermissionGen.needPermission(this, REQUEST_PERMISSION, permissions);

        //register network
        registerNetworkReceiver();

    }

    @Keep
    @PermissionRationale(requestCode = REQUEST_PERMISSION)
    public void requestCameraRationale() {
        PermissionHelper.showRationaleDialog(this,getString(R.string.permission_rationale_camera_audio_room_tip));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this,requestCode,permissions,grantResults);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        disConnectIO();
        unregisterNetworkReceiver();

        //release
        ClassroomController.getInstance().release();
        LiveCtlSessionManager.getInstance().release();
        ContactManager.getInstance().release();
        TalkManager.getInstance().release();

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
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

    private void initParams() {
        mTicket = getIntent().getStringExtra(Constants.KEY_TICKET);
        //init controller;
        ClassroomController.init(this);
    }

    /**
     * 初始化, 启动bootSession
     */
    private void initData(boolean showProgress, final boolean netWorkChanged) {
        if (ClassroomBusiness.getCurrentNetwork(this) == ClassroomBusiness.NETWORK_NONE) {
            //TODO, show tips view
            setNoNetworkTips(true);
            return;
        }

        if (showProgress) {
            showProgress(true);
        }
        LiveManager.bootSession(this, mTicket, new APIServiceCallback<CtlSession>() {
            @Override
            public void onSuccess(CtlSession ctlSession) {
                cancelProgress();
                if (ctlSession != null) {
                    if (XiaojsConfig.DEBUG) {
                        Logger.d("aaa", "session: state=" + ctlSession.state + "   mode=" + ctlSession.mode + "   accessible="
                                + ctlSession.accessible + "   psType=" + ctlSession.psType);
                    }

//                    if (!ctlSession.accessible) {
//                        checkForceKickOut(ctlSession, netWorkChanged);
//                    } else {
                    onBootSessionSucc(false, ctlSession, netWorkChanged);
                    //}
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                if (XiaojsConfig.DEBUG) {
                    Logger.d("boot session error");
                }
                if(Errors.ACCESS_VIOLATION.equals(errorCode)){
                    ToastUtil.showToast(getApplicationContext(),errorMessage);
                    finish();
                    return;
                }
                cancelProgress();
                showContinueConnectClassroom(errorMessage);
            }
        });
    }

    private void setNoNetworkTips(boolean showEmptyTips) {
        if (mEmptyTips == null) {
            mEmptyTips = (TextView) findViewById(R.id.no_net_work_tips);
        }

        mEmptyTips.setVisibility(showEmptyTips ? View.VISIBLE : View.GONE);


        showContinueConnectClassroom(null);
    }

    /**
     * boot session 成功回调后的逻辑处理
     *
     * @param forceConnect 是否强制链接
     * @param ctlSession   课程session
     */
    private void onBootSessionSucc(final boolean forceConnect, final CtlSession ctlSession, final boolean networkChanged) {
        if (Live.LiveSessionState.CANCELLED.equals(ctlSession.state)) {
            Toast.makeText(this, R.string.forbidden_enter_class_for_cancel, Toast.LENGTH_SHORT).show();
            //TODO
            //finish();
        }

        mCtlSession = ctlSession;
        mAppType = ctlSession.connected != null ? ctlSession.connected.app : Platform.AppType.UNKNOWN;
        //二维码扫描进入教室，需要更新ticket.
        if (!TextUtils.isEmpty(ctlSession.ticket)) {
            mTicket = ctlSession.ticket;
        }

        //init socket
        if (!networkChanged) {
            //初始化socket并连接。
            initSocketIO(mTicket, ctlSession.secret, forceConnect);
        } else {
            //重制并重新初始化和连接socket
            SocketManager.close();
            SocketManager.init(ClassroomActivity.this, mTicket, ctlSession.secret, true, true, forceConnect);
            mSocket = SocketManager.getSocket();
            mHandler.removeMessages(MSG_SOCKET_TIME_OUT);
            //mHandler.sendEmptyMessageDelayed(MSG_SOCKET_TIME_OUT, SOCKET_TIME_OUT);
            SocketManager.reListener();
            SocketManager.connect();
        }

        //init global data
        LiveCtlSessionManager.getInstance().init(ctlSession, mTicket);
        ContactManager.getInstance().init();
        TalkManager.getInstance().init(ClassroomActivity.this, mTicket);

        //init fragment

        ClassroomLiveFragment liveFragment = ClassroomController.getInstance().getStackFragment();

        if (liveFragment == null) {
            initFragment(mCtlSession);
        } else if (liveFragment instanceof PlayFragment) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(liveFragment)
                    .commit();

            ClassroomController.getInstance().setStackFragment(null);

            initFragment(mCtlSession);
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

            boolean isPrivateClass = mCtlSession.cls != null;
            if (isPrivateClass) {
                data.putBoolean(Constants.KEY_SHOW_CLASS_LESSON_TIPS, true);
                data.putString(Constants.KEY_PUBLISH_URL, ctlSession.publishUrl);
                ClassroomController.getInstance().enterPlayFragment(data, false);

            } else {
                data.putInt(Constants.KEY_FROM, Constants.FROM_ACTIVITY);
                data.putSerializable(Constants.KEY_PUBLISH_TYPE, StreamType.TYPE_STREAM_PUBLISH);
                data.putString(Constants.KEY_PUBLISH_URL, ctlSession.publishUrl);
                ClassroomController.getInstance().enterPublishFragment(data, false);
            }


        } else {
            ClassroomController.getInstance().enterPlayFragment(null, false);
        }
    }

    private void initSocketIO(String ticket, String secret, boolean force) {
        //off listener
        if (SocketManager.hasEventListeners()) {
            SocketManager.off(false);
        } else {
            SocketManager.off();
        }

        SocketManager.close();
        SocketManager.init(ClassroomActivity.this, ticket, secret, true, true, force);
        mSocket = SocketManager.getSocket();
        mHandler.removeMessages(MSG_SOCKET_TIME_OUT);
        //mHandler.sendEmptyMessageDelayed(MSG_SOCKET_TIME_OUT, SOCKET_TIME_OUT);

        //listener again
        if (SocketManager.hasEventListeners()) {
            SocketManager.reListener();

            if (XiaojsConfig.DEBUG) {
                Logger.d("reListener..********************************..");
            }

        } else {
            listenSocket();
            if (XiaojsConfig.DEBUG) {
                Logger.d("listenSocket..*****************************..");
            }
        }
    }

    private void disConnectIO() {
        //off all
        SocketManager.off();
        SocketManager.close();
    }

    private void listenSocket() {
        if (mSocket == null) {
            return;
        }

        SocketManager.on(Socket.EVENT_CONNECT, mOnConnect);
        SocketManager.on(Socket.EVENT_DISCONNECT, mOnDisconnect);
        SocketManager.on(Socket.EVENT_CONNECT_ERROR, mOnConnectError);
        SocketManager.on(Socket.EVENT_CONNECT_TIMEOUT, mSocketTimeoutListener);
        SocketManager.on(
                Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.KICKOUT_DUE_TO_NEW_CONNECTION),
                mKickoutByUserListener);
        SocketManager.on(
                Event.getEventSignature(Su.EventCategory.LIVE, Su.EventType.CLASS_MODE_SWITCH),
                mModeSwitchListener);

        SocketManager.connect();

        showProgress(false);
        if (mHandler != null) {
            mHandler.removeMessages(SOCKET_CONNECT_TIMEOUT);
            mHandler.sendEmptyMessageDelayed(SOCKET_CONNECT_TIMEOUT, 20 * 1000);
        }

    }

    private SocketManager.EventListener mOnConnect = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            if (!mSktConnected) {
                //if (XiaojsConfig.DEBUG) {
                Toast.makeText(ClassroomActivity.this, R.string.socket_connect, Toast.LENGTH_LONG).show();
                // }
            }
            cancelProgress();

            if (mContinueConnectDialog != null && mContinueConnectDialog.isShowing()) {
                mContinueConnectDialog.dismiss();
            }

            mHandler.removeMessages(MSG_SOCKET_TIME_OUT);
            mHandler.removeMessages(SOCKET_CONNECT_TIMEOUT);
            mSktConnected = true;

            ContactManager.getInstance().getAttendees(ClassroomActivity.this, null);
            ClassroomController.getInstance().notifySocketConnectChanged(true);
        }
    };

    private SocketManager.EventListener mOnDisconnect = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            mSktConnected = false;
            cancelProgress();
            mHandler.removeMessages(SOCKET_CONNECT_TIMEOUT);
            mHandler.removeMessages(MSG_SOCKET_TIME_OUT);
            mHandler.sendEmptyMessage(MSG_SOCKET_TIME_OUT);

            if (XiaojsConfig.DEBUG) {
                Toast.makeText(ClassroomActivity.this, R.string.socket_disconnect, Toast.LENGTH_LONG).show();
            }
        }
    };

    private SocketManager.EventListener mOnConnectError = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {
            mSktConnected = false;
            cancelProgress();
            mHandler.removeMessages(SOCKET_CONNECT_TIMEOUT);
            mHandler.removeMessages(MSG_SOCKET_TIME_OUT);
            mHandler.sendEmptyMessage(MSG_SOCKET_TIME_OUT);

            // if (XiaojsConfig.DEBUG) {
            Toast.makeText(ClassroomActivity.this, R.string.socket_error_connect, Toast.LENGTH_LONG).show();
            // }
        }
    };

    private SocketManager.EventListener mSocketTimeoutListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {

            mSktConnected = false;
            cancelProgress();
            mHandler.removeMessages(SOCKET_CONNECT_TIMEOUT);
            mHandler.removeMessages(MSG_SOCKET_TIME_OUT);
            mHandler.sendEmptyMessage(MSG_SOCKET_TIME_OUT);

            if (XiaojsConfig.DEBUG) {
                Logger.d("socket connect time out...");
            }
        }
    };

    private SocketManager.EventListener mKickoutByUserListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {

            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.KICKOUT_DUE_TO_NEW_CONNECTION**");
            }

            Toast.makeText(ClassroomActivity.this, R.string.mobile_kick_out_tips, Toast.LENGTH_LONG).show();
            finish();
        }
    };

    private SocketManager.EventListener mModeSwitchListener = new SocketManager.EventListener() {
        @Override
        public void call(Object... args) {


            if (XiaojsConfig.DEBUG) {
                Logger.d("Received event: **Su.EventType.CLASS_MODE_SWITCH**");
            }


            //解析并更新session mode
            if (args != null && args.length > 0) {
                ModeSwitcher switcher = ClassroomBusiness.parseSocketBean(args[0], ModeSwitcher.class);
                LiveCtlSessionManager.getInstance().updateCtlSessionMode(switcher.to);
            }

        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg != null) {
                switch (msg.what) {
                    case MSG_SOCKET_TIME_OUT:
                        //socket time out
//                        if (!mSktConnected && mSocketRetryCount++ < RETRY_COUNT) {
//                            //reconnect
//                            if (XiaojsConfig.DEBUG) {
//                                Logger.i("socket reconnecting now!");
//                            }
//                            initData(true, false);
//                        } else {
                        cancelProgress();
                        showContinueConnectClassroom(null);
                        //}
                        break;
                    case SOCKET_CONNECT_TIMEOUT:
                        cancelProgress();
                        if (!mSktConnected) {
                            showContinueConnectClassroom(null);
                        }

                        break;
                }
            }
        }
    };

    private void registerNetworkReceiver() {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        mNetworkChangedBReceiver = new NetworkChangedBReceiver();
        registerReceiver(mNetworkChangedBReceiver, filter);
    }

    private void unregisterNetworkReceiver() {
        if (mNetworkChangedBReceiver != null) {
            unregisterReceiver(mNetworkChangedBReceiver);
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

    /**
     * 是否继续连接教室
     */
    private void showContinueConnectClassroom(String errorTips) {
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
                    mSocketRetryCount = 0;
                    initData(true, true);
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
                    onBootSessionSucc(true, ctlSession, netWorkChanged);
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

    /**
     * 网络切换监听
     */
    public class NetworkChangedBReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();

            boolean mobileNet = mobileInfo == null ? false : mobileInfo.isConnected();
            boolean wifiNet = wifiInfo == null ? false : wifiInfo.isConnected();
            String activeNet = activeInfo == null ? "null" : activeInfo.getTypeName();

            if (activeInfo == null) {
                // have no active network
                mSktConnected = false;
                mNetworkState = ClassroomBusiness.NETWORK_NONE;
                ClassroomController.getInstance().notifySocketConnectChanged(false);

                //disconnect all socket
                mNetworkDisconnected = true;
                SocketManager.off(false);
            } else if (wifiNet) {
                // wifi network
                mNetworkState = ClassroomBusiness.NETWORK_WIFI;
                if (mNetworkDisconnected) {
                    mNetworkDisconnected = false;
//                    setNoNetworkTips(false);
//                    SocketManager.reListener();
//                    SocketManager.connect();
                }
            } else if (mobileNet) {
                // mobile network
                mNetworkState = ClassroomBusiness.NETWORK_OTHER;
                if (mNetworkDisconnected) {
                    mNetworkDisconnected = false;
                    //setNoNetworkTips(false);
                    //SocketManager.reListener();
                    //SocketManager.connect();
                }
            }
        }
    }

}
