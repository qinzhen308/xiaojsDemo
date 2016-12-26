package cn.xiaojs.xma.ui.course;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:zhanghui
 * Date:2016/11/1
 * Desc:
 *
 * ======================================================================================== */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.Su;
import cn.xiaojs.xma.data.SecurityManager;
import cn.xiaojs.xma.ui.base.BaseTopTabActivity;
import cn.xiaojs.xma.ui.mine.TeachAbilityDemoActivity;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;
import cn.xiaojs.xma.ui.widget.CommonDialog;

public class MyLessonActivity extends BaseTopTabActivity {

    @Override
    protected void initView() {
        setMiddleTitle(R.string.my_course);
        setRightImage(R.drawable.add_selector);
        setLeftImage(R.drawable.back_arrow);
        needHeaderDivider(false);

        List<Fragment> fs = new ArrayList<>();
        Bundle b1 = new Bundle();
        b1.putBoolean(CourseConstant.KEY_IS_TEACHER,false);
        Fragment f1 = new LessonFragment();
        f1.setArguments(b1);
        fs.add(f1);
        List<String> ss = new ArrayList<>();
        ss.add(getString(R.string.course_of_learn));

        if (SecurityManager.checkPermission(this, Su.Permission.COURSE_OPEN_CREATE)){
            Bundle b2 = new Bundle();
            b2.putBoolean(CourseConstant.KEY_IS_TEACHER,true);
            Fragment f2 = new LessonFragment();
            f2.setArguments(b2);
            fs.add(f2);
            ss.add(getString(R.string.course_of_teach));
        }
        addViews(ss,fs);
    }

    @OnClick({R.id.left_image,R.id.right_image})
    public void onLocalClick(View view){
        switch (view.getId()){
            case R.id.left_image:
                finish();
                break;
            case R.id.right_image://右上角menu
                CommonPopupMenu menu = new CommonPopupMenu(this);
                String[] items = getResources().getStringArray(R.array.my_course_list);
                menu.addTextItems(items);
                menu.addImgItems(new Integer[]{R.drawable.open_course_selector,R.drawable.add_private_course_selector});
                menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        handleRightClick(i);
                    }
                });
                int offset = getResources().getDimensionPixelSize(R.dimen.px68);
                menu.show(mBaseHeader,offset);
                break;
            default:
                break;
        }
    }

    private void handleRightClick(int position){
        switch (position){
            case 0://我要开课
                if (SecurityManager.checkPermission(this, Su.Permission.COURSE_OPEN_CREATE)){
                    //老师可以开课
                    Intent intent = new Intent(this,LessonCreationActivity.class);
                    startActivityForResult(intent,CourseConstant.CODE_CREATE_LESSON);
                }else {
                    //提示申明教学能力
                    final CommonDialog dialog = new CommonDialog(this);
                    dialog.setTitle(R.string.declare_teaching_ability);
                    dialog.setDesc(R.string.declare_teaching_ability_tip);
                    dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                        @Override
                        public void onClick() {
                            dialog.dismiss();
                            Intent intent = new Intent(MyLessonActivity.this, TeachAbilityDemoActivity.class);
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
            case 1://加入私密课
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment fragment = getCurrFragment();
        if (fragment != null){
            fragment.onActivityResult(requestCode,resultCode,data);
        }
    }
}
