package cn.xiaojs.xma.ui;

import android.content.Intent;
import android.support.v4.app.Fragment;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.ui.base.BaseTabActivity;
import cn.xiaojs.xma.ui.course.CourseConstant;
import cn.xiaojs.xma.ui.course.LessonCreationActivity;
import cn.xiaojs.xma.ui.home.HomeFragment;
import cn.xiaojs.xma.ui.home.MomentDetailActivity;
import cn.xiaojs.xma.ui.live.LiveFragment;
import cn.xiaojs.xma.ui.message.NotificationFragment;
import cn.xiaojs.xma.ui.message.PostDynamicActivity;
import cn.xiaojs.xma.ui.mine.PersonHomeActivity;
import cn.xiaojs.xma.ui.mine.TeachAbilityDemoActivity;
import cn.xiaojs.xma.ui.widget.CommonDialog;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class MainActivity extends BaseTabActivity {
    
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
        addViews(new int[]{R.string.home_tab_index,R.string.home_tab_live ,R.string.home_tab_message , R.string.home_tab_mine},
                new int[]{R.drawable.home_tab_selector, R.drawable.live_tab_selector, R.drawable.message_tab_selector, R.drawable.mine_tab_selector},
                fs);
        new OkHttpClient();
    }

    @Override
    protected void onGooeyMenuClick(int position) {
        switch (position){
            case 1://开课
                if (SecurityManager.checkPermission(this, Su.Permission.COURSE_OPEN_CREATE)){
                    //老师可以开课
                    Intent intent = new Intent(this,LessonCreationActivity.class);
                    startActivityForResult(intent, CourseConstant.CODE_CREATE_LESSON);
                }else {
                    //提示申明教学能力
                    final CommonDialog dialog = new CommonDialog(this);
                    dialog.setTitle(R.string.declare_teaching_ability);
                    dialog.setDesc(R.string.declare_teaching_ability_tip);
                    dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                            Intent intent = new Intent(MainActivity.this, TeachAbilityDemoActivity.class);
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
                startActivity(new Intent(this,GlobalSearchActivity.class));
                break;
            case 3:
                startActivity(new Intent(this,MomentDetailActivity.class));
                break;
            case 4:
                startActivity(new Intent(this,PersonHomeActivity.class));
                break;
            case 5:
                startActivity(new Intent(this, PostDynamicActivity.class));
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getCurrentFragment();
        if (fragment != null){
            fragment.onActivityResult(requestCode,resultCode,data);
        }

        super.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        System.exit(1);
    }
}
