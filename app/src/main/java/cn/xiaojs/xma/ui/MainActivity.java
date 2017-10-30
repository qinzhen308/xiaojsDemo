package cn.xiaojs.xma.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;

import cn.xiaojs.xma.analytics.AnalyticEvents;
import cn.xiaojs.xma.common.permissiongen.PermissionHelper;
import cn.xiaojs.xma.common.permissiongen.PermissionRationale;
import cn.xiaojs.xma.common.permissiongen.PermissionSuccess;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.UpgradeManager;
import cn.xiaojs.xma.ui.base.BaseConstant;
import cn.xiaojs.xma.ui.base.BaseTabActivity;

import cn.xiaojs.xma.ui.base.IntentFlags;
import cn.xiaojs.xma.ui.base.XiaojsActions;
import cn.xiaojs.xma.ui.classroom.main.ClassroomActivity;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.home.HomeFragment;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.MyCourseListActivity;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;
import cn.xiaojs.xma.ui.lesson.xclass.ClassFragment;
import cn.xiaojs.xma.ui.lesson.xclass.ClassesListActivity;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.live.LiveFragment;
import cn.xiaojs.xma.ui.message.ContactActivity;

import cn.xiaojs.xma.ui.message.MessageFragment;
import cn.xiaojs.xma.ui.message.PostDynamicActivity;

import cn.xiaojs.xma.ui.recordlesson.RLDirListActivity;
import cn.xiaojs.xma.ui.search.SearchActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.ui.widget.SwipeLayout;
import cn.xiaojs.xma.util.MessageUitl;
import cn.xiaojs.xma.util.ToastUtil;

import static cn.xiaojs.xma.XiaojsApplication.ACTION_NEW_MESSAGE;
import static cn.xiaojs.xma.util.MessageUitl.ACTION_NEW_PUSH;

public class MainActivity extends BaseTabActivity implements XiaojsActions , IUpdateMethod{

    public static final String KEY_POSITION = "key_position";

    public static final int PERMISSION_CODE = 0x11;

    private long time;

    private UpgradeReceiver upgradeReceiver;
    private boolean hasShow = false;

    //private MessageFragment conversationFragment;

    SchemeProcessor mSchemeProcessor;


    @Override
    protected void initView() {
        //setMiddleTitle(R.string.app_name);
        needHeader(false);
        List<Fragment> fs = new ArrayList<>();
        fs.add(new ClassFragment());
        fs.add(new HomeFragment());
        fs.add(new MineFragment());

//        conversationFragment = new MessageFragment();
//        fs.add(conversationFragment);


        setButtonType(BUTTON_TYPE_NONE);
        addViews(new int[]{R.string.home_tab_class, R.string.home_tab_circle, R.string.home_tab_mine},
                new int[]{R.drawable.home_tab_selector, R.drawable.live_tab_selector, R.drawable.mine_tab_selector},
                fs);

        Intent intent = getIntent();
        if (intent != null) {
            int position = intent.getIntExtra(KEY_POSITION, -1);
            if (position >= 0) {
                setTabSelected(position);
            }
        }

        showTips();

        hasShow = UpgradeManager.checkUpgrade(MainActivity.this);

        if (!hasShow) {

            //用户帮助引导
//            if (!DataManager.hasShowGuide(this)) {
//                showGuid();
//            }

            requestPermission();
        }

        dealFromNotify(getIntent());

    }

    @Override
    protected void onNewIntent(Intent intent) {

        if (dealFromNotify(intent)) {

        } else {
            if (intent != null) {
                int position = intent.getIntExtra(KEY_POSITION, -1);
                if (position >= 0) {
                    setTabSelected(position);
                }
                //回到这个页面后，再去其他页面
                doAction(intent.getAction(),intent);
            }
        }
        //集中处理scheme协议
        mSchemeProcessor.handle(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Fragment fragment = getFragmentByPosition(2);
//        if (fragment != null && fragment instanceof NotificationFragment) {
//            ((NotificationFragment) fragment).notifyConversation();
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onTabClick(int position) {
        judgeDoubleClick(position);
        super.onTabClick(position);
        if(position==0){
            AnalyticEvents.onEvent(this,3);
        }else if(position==1){
            AnalyticEvents.onEvent(this,4);
        }else if(position==2){
            AnalyticEvents.onEvent(this,5);
        }
    }

    private final void judgeDoubleClick(int position){
        if(position!=0)return;
        if(System.currentTimeMillis()-clickInvalid<DOUBLE_CLICK_INTERVAL){
            onTabDoubleClick(position);
            clickInvalid=0;
        }else {
            clickInvalid=System.currentTimeMillis();
        }
    }

    private void onTabDoubleClick(int position){
        ClassFragment fragment=(ClassFragment)mAdapter.getFragment(0);
        fragment.updateData();
    }

    private final static int DOUBLE_CLICK_INTERVAL=500;
    private long clickInvalid=0;


    private boolean dealFromNotify(Intent intent) {
        String action = intent.getAction();

        //IM及时通讯
//        if (!TextUtils.isEmpty(action) && action.equals(LCIMConstants.CHAT_NOTIFICATION_ACTION)) {
//            setTabSelected(2);
//
//            Intent ifarIntent = new Intent();
//            ifarIntent.setAction(LCIMConstants.CONVERSATION_ITEM_CLICK_ACTION);
//
//            Bundle extras = intent.getExtras();
//            if (extras != null) {
//
//                boolean group = false;
//                if (extras.containsKey(LCIMConstants.IS_GROUP)) {
//                    group = intent.getBooleanExtra(LCIMConstants.IS_GROUP, false);
//                }
//
//                if (group) {
//                    String cid = intent.getStringExtra(LCIMConstants.CONVERSATION_ID);
//                    ifarIntent.putExtra(LCIMConstants.CONVERSATION_ID, cid);
//
//                } else {
//                    String pid = intent.getStringExtra(LCIMConstants.PEER_ID);
//                    ifarIntent.putExtra(LCIMConstants.PEER_ID, pid);
//                }
//
//                ifarIntent.setPackage(getPackageName());
//                ifarIntent.addCategory(Intent.CATEGORY_DEFAULT);
//
//                startActivity(ifarIntent);
//                return true;
//            }
//        }

        //PUSH推送
        if (!TextUtils.isEmpty(action) && action.equals(MessageUitl.ACTION_PUSH_NOTIFY_OPEN)) {
            setTabSelected(2);
            MessageUitl.lanuchMessageCenter(this);
            return true;
        }

        return false;
    }

    @Override
    protected void onGooeyMenuClick(int position) {
        switch (position) {
            case 1:

                //Toast.makeText(this,"暂未开放此功能",Toast.LENGTH_SHORT).show();
//                if (PermissionUtil.isOverMarshmallow()
//                        && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
//                    requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
//
//                }else {
//                    startActivity(new Intent(this, ScanQrcodeActivity.class));
//                }

                break;
            case 2://开课

                if (AccountDataManager.isTeacher(this)) {
                    //老师可以开课
                    Intent intent = new Intent(this, LessonCreationActivity.class);
                    startActivityForResult(intent, CourseConstant.CODE_CREATE_LESSON);
                } else {
                    //提示申明教学能力
                    final CommonDialog dialog = new CommonDialog(this);
                    dialog.setTitle(R.string.declare_teaching_ability);
                    dialog.setDesc(R.string.declare_teaching_ability_tip);
                    dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, TeachingSubjectActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                break;
            case 3:
                //startActivity(new Intent(this,GradeHomeActivity.class));
                startActivity(new Intent(this, ContactActivity.class));
                break;
            case 4:
                startActivityForResult(new Intent(this, PostDynamicActivity.class), BaseConstant.REQUEST_CODE_SEND_MOMENT);
                //if (JMessageClient.getMyInfo() != null){
                //    Intent intent = new Intent(this, ChatActivity.class);
                //    intent.putExtra(ChatActivity.TARGET_ID,"1234567");
                //    startActivity(intent);
                //}
                break;
            case 5:
                //startActivity(new Intent(this,PersonHomeActivity.class));
                break;
            case 6:
                //startActivityForResult(new Intent(this, PostDynamicActivity.class), BaseConstant.REQUEST_CODE_SEND_MOMENT);
                break;
        }

        autoClose();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getCurrentFragment();
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - time > XiaojsConfig.EXIT_DELAY) {
            ToastUtil.showToast(this, R.string.exit_tips);
            time = System.currentTimeMillis();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(UpgradeManager.ACTION_SHOW_UPGRADE);
        filter.addAction(ACTION_NEW_MESSAGE);
        filter.addAction(ACTION_NEW_PUSH);

        upgradeReceiver = new UpgradeReceiver();

        registerReceiver(upgradeReceiver, filter);
        mSchemeProcessor=new SchemeProcessor(this);
        mSchemeProcessor.handle(getIntent());
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(upgradeReceiver);

        super.onDestroy();
    }


    private void showGuid() {
        GuideDialog dlg = new GuideDialog(this);
        dlg.show();
    }


    private void requestPermission() {

//        if (PermissionUtil.isOverMarshmallow()){
//
//            String[] needPermissions = {
//                    Manifest.permission.VIBRATE
//            };
//
//            PermissionGen.needPermission(this,1,needPermissions);
//        }
    }

    private void showTips() {

        if (!XiaojsConfig.CURRENT_PAGE_IN_MESSAGE && DataManager.hasMessage(this)) {
            showMessageTips();
        }
    }

    @Override
    public void updateData(boolean justNative,Object... others) {
        ClassFragment fragment=(ClassFragment) getFragment(0);
        fragment.updateData();
    }

    @Override
    public void updateItem(int position, Object obj, Object... others) {

    }


//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//
//        switch (requestCode) {
//            case PERMISSION_CODE: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//
//                    startActivity(new Intent(this, ScanQrcodeActivity.class));
//
//                } else {
//
//                    // permission denied, boo! Disable the
//                    // functionality that depends on this permission.
//                }
//                return;
//            }
//        }
//    }

    private class UpgradeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UpgradeManager.ACTION_SHOW_UPGRADE)) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("receiver new upgrade...");
                }

                if (!hasShow) {
                    UpgradeManager.showUpgrade(MainActivity.this);
                    hasShow = true;
                }
            } else if (action.equals(ACTION_NEW_MESSAGE)) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("receiver new MESSAGE...");
                }

                if (!XiaojsConfig.CURRENT_PAGE_IN_MESSAGE) {
                    showMessageTips();
                }

            } else if (action.equals(ACTION_NEW_PUSH)) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("receiver new PUSH...");
                }

                if (!XiaojsConfig.CURRENT_PAGE_IN_MESSAGE) {
                    showMessageTips();
                }

                //FIXME MESSAGE
//                if (conversationFragment != null && conversationFragment.isAdded()) {
//                    conversationFragment.getMessageOverview();
//                }
                Fragment fg=getFragment(2);
                //当前在个人中心才调，否则在切换到个人中心在onResume更新状态
                if(fg instanceof MineFragment && fg.isAdded()){
                    ((MineFragment)fg).showMessageTips(true);
                }

            }
        }
    }


    /**
     * 利用这个activity的singletask模式清理栈，再根据action跳到对应页面
     * @param context
     * @param action
     * @param datas 这种方式传值，虽然通用，但可读性差
     */
    public static void invokeWithAction(Context context,String action,String... datas){
        Intent intent=new Intent(context, MainActivity.class);
        if(!TextUtils.isEmpty(action)){
            intent.setAction(action);
        }
        if(datas!=null){
            for(int i=0;i<datas.length;i++){
                intent.putExtra(IntentFlags.EXTRA_COMMON_KEY+i,datas[i]);
            }
        }
        context.startActivity(intent);
    }



    @Override
    public void doAction(String action, Intent intent) {
        if(TextUtils.isEmpty(action))return;
        switch (action){
            case ACTION_TO_MY_CLASSES:
                ClassesListActivity.invoke(this,0);
                break;
            case ACTION_TO_MY_LESSONS:
                ClassesListActivity.invoke(this,1);
                break;
            case ACTION_TO_MY_RECORDED_LESSONS:
//                ClassesListActivity.invoke(this,2);
                MyCourseListActivity.invoke(this);
                break;
            case ACTION_TO_CLASSROOM:
                String ticket=intent.getStringExtra(IntentFlags.EXTRA_COMMON_KEY+0);
                if (TextUtils.isEmpty(ticket)) {
                    Toast.makeText(this,"进入教室失败",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent();
                i.putExtra(Constants.KEY_TICKET, ticket);
                i.setClass(this, ClassroomActivity.class);
                this.startActivity(i);
                break;
            case ACTION_TO_RECORDED_LESSONS_DIR:
                String id=intent.getStringExtra(IntentFlags.EXTRA_COMMON_KEY+0);
                String isEpiredStr=intent.getStringExtra(IntentFlags.EXTRA_COMMON_KEY+1);
                if (TextUtils.isEmpty(id)) {
                    Toast.makeText(this,"课程id有误",Toast.LENGTH_SHORT).show();
                    return;
                }
                boolean isEpired=false;
                try {
                    isEpired=Boolean.valueOf(isEpiredStr);
                }catch (Exception e){
                    e.printStackTrace();
                }
                RLDirListActivity.invoke(this,id,isEpired);
                break;
        }
    }

    @Keep
    @PermissionSuccess(requestCode = MainActivity.PERMISSION_CODE)
    public void requestCameraSuccess() {
        AnalyticEvents.onEvent(this,2);
        startActivity(new Intent(this, ScanQrcodeActivity.class));
    }

    @Keep
    @PermissionRationale(requestCode = MainActivity.PERMISSION_CODE)
    public void requestCameraRationale() {
        PermissionHelper.showRationaleDialog(this,getResources().getString(R.string.permission_rationale_camera_tip));

    }

}
