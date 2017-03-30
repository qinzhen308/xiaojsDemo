package cn.xiaojs.xma.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.event.LoginStateChangeEvent;
import cn.jpush.im.android.api.model.UserInfo;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.im.utils.FileHelper;
import cn.xiaojs.xma.common.im.utils.SharePreferenceManager;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.UpgradeManager;
import cn.xiaojs.xma.ui.base.BaseConstant;
import cn.xiaojs.xma.ui.base.BaseTabActivity;

import cn.xiaojs.xma.ui.home.HomeFragment;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;
import cn.xiaojs.xma.ui.live.LiveFragment;
import cn.xiaojs.xma.ui.message.ContactActivity;

import cn.xiaojs.xma.ui.message.NotificationFragment;
import cn.xiaojs.xma.ui.message.PostDynamicActivity;

import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.APPUtils;
import cn.xiaojs.xma.util.ToastUtil;

public class MainActivity extends BaseTabActivity {

    public static final String KEY_POSITION = "key_position";

    private long time;

    @Override
    protected void initView() {
        //setMiddleTitle(R.string.app_name);
        needHeader(false);
        List<Fragment> fs = new ArrayList<>();
        fs.add(new HomeFragment());
        fs.add(new LiveFragment());
        fs.add(new NotificationFragment());
        //fs.add(new MessageFragment());
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

        UpgradeManager.checkUpgrade(MainActivity.this);

        JMessageClient.registerEventReceiver(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            int position = intent.getIntExtra(KEY_POSITION, -1);
            if (position >= 0) {
                setTabSelected(position);
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

    /**
     * 接收登录状态相关事件:登出事件,修改密码事件及被删除事件
     * @param event 登录状态相关事件
     */
    public void onEventMainThread(LoginStateChangeEvent event) {
        LoginStateChangeEvent.Reason reason = event.getReason();
        UserInfo myInfo = event.getMyInfo();
        if (null != myInfo) {
            String path;
            File avatar = myInfo.getAvatarFile();
            if (avatar != null && avatar.exists()) {
                path = avatar.getAbsolutePath();
            } else {
                path = FileHelper.getUserAvatarPath(myInfo.getUserName());
            }

            SharePreferenceManager.setCachedUsername(myInfo.getUserName());
            SharePreferenceManager.setCachedAvatarPath(path);
            JMessageClient.logout();
        }

        switch (reason) {
            case user_logout:
                //退出登陆，跳转到登陆页面
                APPUtils.exitAndLogin(MainActivity.this,R.string.relogin);
                break;
            case user_deleted:
            case user_password_change:
                break;
        }
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
    protected void onDestroy() {
        JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }
}
