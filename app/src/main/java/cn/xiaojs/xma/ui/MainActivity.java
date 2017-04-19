package cn.xiaojs.xma.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;


import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;

import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.UpgradeManager;
import cn.xiaojs.xma.ui.base.BaseConstant;
import cn.xiaojs.xma.ui.base.BaseTabActivity;

import cn.xiaojs.xma.ui.home.HomeFragment;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;
import cn.xiaojs.xma.ui.live.LiveFragment;
import cn.xiaojs.xma.ui.message.ContactActivity;

import cn.xiaojs.xma.ui.message.MessageFragment;
import cn.xiaojs.xma.ui.message.PostDynamicActivity;

import cn.xiaojs.xma.ui.message.im.chatkit.activity.LCIMConversationListFragment;
import cn.xiaojs.xma.ui.message.im.chatkit.utils.LCIMConstants;
import cn.xiaojs.xma.ui.mine.SettingsActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.MessageUitl;
import cn.xiaojs.xma.util.ToastUtil;

import static cn.xiaojs.xma.XiaojsApplication.ACTION_NEW_MESSAGE;

public class MainActivity extends BaseTabActivity {

    public static final String KEY_POSITION = "key_position";

    private long time;

    private UpgradeReceiver upgradeReceiver;
    private boolean hasShow = false;

    private LCIMConversationListFragment conversationListFragment;


    @Override
    protected void initView() {
        //setMiddleTitle(R.string.app_name);
        needHeader(false);
        List<Fragment> fs = new ArrayList<>();
        fs.add(new HomeFragment());
        fs.add(new LiveFragment());
        //fs.add(new MessageFragment());
        conversationListFragment = new LCIMConversationListFragment();

        fs.add(conversationListFragment);
        fs.add(new MineFragment());
        setButtonType(BUTTON_TYPE_CENTER);
        addViews(new int[]{R.string.home_tab_index, R.string.home_tab_live, R.string.home_tab_message, R.string.home_tab_mine},
                new int[]{R.drawable.home_tab_selector, R.drawable.live_tab_selector, R.drawable.message_tab_selector, R.drawable.mine_tab_selector},
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

            if (!DataManager.hasShowGuide(this)) {
                showGuid();
            }

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
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        Fragment fragment = getFragmentByPosition(2);
//        if (fragment != null && fragment instanceof NotificationFragment) {
//            ((NotificationFragment) fragment).notifyConversation();
//        }
    }


    private boolean dealFromNotify(Intent intent) {
        String action = intent.getAction();

        //IM及时通讯
        if (!TextUtils.isEmpty(action) && action.equals(LCIMConstants.CHAT_NOTIFICATION_ACTION)) {
            setTabSelected(2);


            String cid = intent.getStringExtra(LCIMConstants.CONVERSATION_ID);
            String pid = intent.getStringExtra(LCIMConstants.PEER_ID);

            Intent ifarIntent = new Intent();
            ifarIntent.setAction(LCIMConstants.CONVERSATION_ITEM_CLICK_ACTION);
            ifarIntent.putExtra(LCIMConstants.CONVERSATION_ID, cid);
            ifarIntent.putExtra(LCIMConstants.PEER_ID, pid);
            ifarIntent.setPackage(getPackageName());
            ifarIntent.addCategory(Intent.CATEGORY_DEFAULT);

            startActivity(ifarIntent);
            return true;
        }

        //PUSH推送
        if (!TextUtils.isEmpty(action) && action.equals(MessageUitl.ACTION_PUSH_NOTIFY_OPEN)) {

            setTabSelected(2);
            return true;
        }

        return false;
    }

    @Override
    protected void onGooeyMenuClick(int position) {
        switch (position) {
            case 1://开课
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
            case 2:
                //startActivity(new Intent(this,GradeHomeActivity.class));
                startActivity(new Intent(this, ContactActivity.class));
                break;
            case 3:
                startActivityForResult(new Intent(this, PostDynamicActivity.class), BaseConstant.REQUEST_CODE_SEND_MOMENT);
                //if (JMessageClient.getMyInfo() != null){
                //    Intent intent = new Intent(this, ChatActivity.class);
                //    intent.putExtra(ChatActivity.TARGET_ID,"1234567");
                //    startActivity(intent);
                //}
                break;
            case 4:
                //startActivity(new Intent(this,PersonHomeActivity.class));
                break;
            case 5:
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

        upgradeReceiver = new UpgradeReceiver();

        registerReceiver(upgradeReceiver, filter);

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

            } else if (action.equals(MessageUitl.ACTION_NEW_PUSH)) {

                if (XiaojsConfig.DEBUG) {
                    Logger.d("receiver new PUSH...");
                }

                if (!XiaojsConfig.CURRENT_PAGE_IN_MESSAGE) {
                    showMessageTips();
                }

                if (conversationListFragment != null) {
                    conversationListFragment.getMessageOverview();
                }

            }
        }
    }
}
