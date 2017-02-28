package cn.xiaojs.xma.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.ui.base.BaseConstant;
import cn.xiaojs.xma.ui.base.BaseTabActivity;
import cn.xiaojs.xma.ui.grade.GradeHomeActivity;
import cn.xiaojs.xma.ui.home.HomeFragment;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.TeachingSubjectActivity;
import cn.xiaojs.xma.ui.live.LiveFragment;
import cn.xiaojs.xma.ui.message.NotificationFragment;
import cn.xiaojs.xma.ui.message.PostDynamicActivity;
import cn.xiaojs.xma.ui.mine.MyOrderActivity;
import cn.xiaojs.xma.ui.personal.PersonHomeActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;
import cn.xiaojs.xma.util.ToastUtil;
import okhttp3.OkHttpClient;

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
        fs.add(new MineFragment());
        setButtonType(BUTTON_TYPE_CENTER);
        addViews(new int[]{R.string.home_tab_index, R.string.home_tab_live, R.string.home_tab_message, R.string.home_tab_mine},
                new int[]{R.drawable.home_tab_selector, R.drawable.live_tab_selector, R.drawable.message_tab_selector, R.drawable.mine_tab_selector},
                fs);
        new OkHttpClient();

        //JMessageClient.registerEventReceiver(this);
        Intent intent = getIntent();
        if (intent != null) {
            int position = intent.getIntExtra(KEY_POSITION, -1);
            if (position >= 0) {
                setTabSelected(position);
            }
        }
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
        Fragment fragment = getFragmentByPosition(2);
        if (fragment != null && fragment instanceof NotificationFragment) {
            ((NotificationFragment) fragment).notifyConversation();
        }
    }

    @Override
    protected void onGooeyMenuClick(int position) {
        switch (position) {
            case 1://开课
                if (SecurityManager.checkPermission(this, Su.Permission.COURSE_OPEN_CREATE)) {
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
                startActivity(new Intent(this, GradeHomeActivity.class));
                break;
            case 3:
//                ArrayList<String> urls = new ArrayList<>();
//                urls.add("http://c.hiphotos.baidu.com/image/pic/item/5bafa40f4bfbfbed91fbb0837ef0f736aec31faf.jpg");
//                urls.add("http://h.hiphotos.baidu.com/image/pic/item/4ec2d5628535e5dd2820232370c6a7efce1b623a.jpg");
//                urls.add("http://f.hiphotos.baidu.com/image/h%3D200/sign=368c40c7cbfc1e17e2bf8b317a91f67c/6c224f4a20a446237cd252b39c22720e0df3d7c3.jpg");
//                urls.add("http://h.hiphotos.baidu.com/image/pic/item/203fb80e7bec54e7f14e9ce2bf389b504ec26aa8.jpg");
//                urls.add("http://h.hiphotos.baidu.com/image/pic/item/203fb80e7bec54e7f14e9ce2bf389b504ec26aa8.jpg");
//                urls.add("http://h.hiphotos.baidu.com/image/pic/item/6609c93d70cf3bc798e14b10d700baa1cc112a6c.jpg");
//                startActivity(new Intent(this,ImageViewActivity.class)
//                .putExtra(ImageViewActivity.IMAGE_PATH_KEY,urls));
                //startActivity(new Intent(this, CertificationActivity.class));
                //startActivity(new Intent(this, TestActivity.class));

//                if (JMessageClient.getMyInfo() != null) {
//                    Intent intent = new Intent(this, ChatActivity.class);
//                    intent.putExtra(ChatActivity.TARGET_ID, "1234567");
//                    startActivity(intent);
//                }

                Intent intent = new Intent(this, MyOrderActivity.class);
                startActivity(intent);
                break;
            case 4:
                startActivity(new Intent(this, PersonHomeActivity.class));
                break;
            case 5:
                startActivityForResult(new Intent(this, PostDynamicActivity.class), BaseConstant.REQUEST_CODE_SEND_MOMENT);
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
        finish();
        System.exit(1);
    }

    @Override
    protected void onDestroy() {
        //JMessageClient.unRegisterEventReceiver(this);
        super.onDestroy();
    }
}
