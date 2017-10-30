package cn.xiaojs.xma.ui.lesson;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RadioGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.ctl.CLesson;
import cn.xiaojs.xma.model.recordedlesson.RLesson;
import cn.xiaojs.xma.ui.base.BaseActivity;
import cn.xiaojs.xma.ui.lesson.xclass.CreateClassActivity;
import cn.xiaojs.xma.ui.lesson.xclass.LessonFragment;
import cn.xiaojs.xma.ui.lesson.xclass.MyClassFragment;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.recordlesson.CreateRecordlessonActivity;
import cn.xiaojs.xma.ui.recordlesson.RecordedLessonActivity;
import cn.xiaojs.xma.ui.recordlesson.RecordedLessonFragment;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import cn.xiaojs.xma.util.JudgementUtil;

/**
 * Created by Paul Z on 2017/10/30.
 */

public class MyCourseListActivity extends BaseActivity implements IUpdateMethod,IDialogMethod{


    RecordedLessonFragment fragment;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_my_course_list);
        setTitle(R.string.my_course_list);
        initView();
    }

    @OnClick({R.id.left_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
        }
    }

    private void initView() {
        fragment=new RecordedLessonFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.layout_container,fragment).commitAllowingStateLoss();
    }


    private void showMenu(View targetView) {
        CommonPopupMenu menu = new CommonPopupMenu(this);
        String[] items = this.getResources().getStringArray(R.array.add_menu2);
        menu.setWidth(this.getResources().getDimensionPixelSize(R.dimen.px280));
        menu.addTextItems(items);
        menu.addImgItems(new Integer[]{R.drawable.ic_menu_create_lesson,
                R.drawable.ic_create_tapedlesson,
                R.drawable.ic_add_class1});
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 2:
                        if (JudgementUtil.checkTeachingAbility(MyCourseListActivity.this)) {
                            MyCourseListActivity.this.startActivityForResult(new Intent(MyCourseListActivity.this, CreateClassActivity.class),CourseConstant.CODE_CREATE_CLASS);
                        }
                        break;
                    case 1:             //开录播课
                        if (JudgementUtil.checkTeachingAbility(MyCourseListActivity.this)) {
//                            startActivityForResult(new Intent(ClassesListActivity.this, CreateRecordlessonActivity.class),CourseConstant.CODE_CREATE_RECORDED_LESSON);
                            startActivity(new Intent(MyCourseListActivity.this, CreateRecordlessonActivity.class));
                        }
                        break;
                    case 0:
                        if (JudgementUtil.checkTeachingAbility(MyCourseListActivity.this)) {
                            //老师可以开课
                            Intent intent = new Intent(MyCourseListActivity.this, LessonCreationActivity.class);
                            MyCourseListActivity.this.startActivityForResult(intent, CourseConstant.CODE_CREATE_LESSON);
                        }
                        break;
                }

            }
        });
        int offset = getResources().getDimensionPixelSize(R.dimen.px68);
        menu.show(targetView, offset);
    }

    @Override
    public void updateData(boolean justNative,Object... others) {
        fragment.updateData(justNative);
    }

    @Override
    public void updateItem(int position, Object obj,Object... others) {
       if(obj instanceof RLesson){
            fragment.updateItem(position,obj,others);
        }
    }

    private ProgressHUD progress;

    @Override
    public void showProgress(boolean cancellable) {
        if (progress == null) {
            progress = ProgressHUD.create(this);
        }
        progress.setCancellable(cancellable);
        progress.show();
    }

    @Override
    public void cancelProgress() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        if (progress != null && progress.isShowing()) {
            progress.dismiss();
            progress = null;
        }
        super.onDestroy();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CourseConstant.CODE_RECORDED_LESSON_AGAIN:
                case RecordedLessonActivity.REQUEST_CODE_MODIFY:
                    updateData(false);
                    break;

            }
        }
    }


    public static void invoke(Activity context){
        Intent intent=new Intent(context,MyCourseListActivity.class);
        context.startActivity(intent);
    }
}
