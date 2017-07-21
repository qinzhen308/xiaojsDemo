package cn.xiaojs.xma.ui.lesson.xclass;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.lesson.CourseConstant;
import cn.xiaojs.xma.ui.lesson.LessonCreationActivity;
import cn.xiaojs.xma.ui.lesson.TeachLessonAdapter;
import cn.xiaojs.xma.ui.lesson.xclass.util.IDialogMethod;
import cn.xiaojs.xma.ui.lesson.xclass.util.IUpdateMethod;
import cn.xiaojs.xma.ui.recordlesson.RecordedLessonFragment;
import cn.xiaojs.xma.ui.view.CommonPopupMenu;
import cn.xiaojs.xma.ui.widget.progress.ProgressHUD;
import cn.xiaojs.xma.util.JudgementUtil;

/**
 * Created by maxiaobao on 2017/5/22.
 */

public class ClassesListActivity extends FragmentActivity implements IUpdateMethod,IDialogMethod{

    @BindView(R.id.lay_tab_group)
    RadioGroup tabGroupLayout;
    @BindView(R.id.tab_viewpager)
    ViewPager tabPager;
    @BindView(R.id.add_btn)
    View addBtn;
    private static final String EXTRA_TAB="extra_tab";
    private int curCheckedTabId;


    private ArrayList<Fragment> fragmentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_list);
        ButterKnife.bind(this);
        initView();
        int tab=getIntent().getIntExtra(EXTRA_TAB,-1);
        if(tab>0){
            checkToTab(tab);
        }
    }

    @OnClick({R.id.left_image, R.id.add_btn,R.id.tab_class,R.id.tab_lesson,R.id.tab_record_lesson})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.add_btn:
                showMenu(addBtn);
                break;
            case R.id.tab_class:
                if(curCheckedTabId!=R.id.tab_class){
                    tabPager.setCurrentItem(0);
                    curCheckedTabId=R.id.tab_class;
                }
                break;
            case R.id.tab_lesson:
                if(curCheckedTabId!=R.id.tab_lesson){
                    tabPager.setCurrentItem(1);
                    curCheckedTabId=R.id.tab_lesson;
                }
                break;
            case R.id.tab_record_lesson:
                if(curCheckedTabId!=R.id.tab_record_lesson){
                    tabPager.setCurrentItem(2);
                    curCheckedTabId=R.id.tab_record_lesson;
                }
                break;
        }
    }

    private void initView() {
        tabGroupLayout.check(R.id.tab_class);
        tabPager.setOffscreenPageLimit(2);
        fragmentList = new ArrayList<>(2);
        fragmentList.add(new MyClassFragment());
        fragmentList.add(new LessonFragment());
        fragmentList.add(new RecordedLessonFragment());

        FrgStatePageAdapter adapter = new FrgStatePageAdapter(getSupportFragmentManager());
        adapter.setList(fragmentList);
        tabPager.setAdapter(adapter);


        tabPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    curCheckedTabId=R.id.tab_class;
                    tabGroupLayout.check(R.id.tab_class);
                }else if(position == 1){
                    curCheckedTabId=R.id.tab_lesson;
                    tabGroupLayout.check(R.id.tab_lesson);
                }else if(position == 2){
                    curCheckedTabId=R.id.tab_record_lesson;
                    tabGroupLayout.check(R.id.tab_record_lesson);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void checkToTab(int tab){
        if(tab==0&&curCheckedTabId!=R.id.tab_class){
            tabPager.setCurrentItem(0);
            tabGroupLayout.check(R.id.tab_class);
            curCheckedTabId=R.id.tab_class;
        }else if(tab==1&&curCheckedTabId!=R.id.tab_lesson){
            tabPager.setCurrentItem(1);
            tabGroupLayout.check(R.id.tab_lesson);
            curCheckedTabId=R.id.tab_lesson;
        }else if(tab==2&&curCheckedTabId!=R.id.tab_record_lesson){
            tabPager.setCurrentItem(2);
            tabGroupLayout.check(R.id.tab_record_lesson);
            curCheckedTabId=R.id.tab_record_lesson;
        }
    }


    private void showMenu(View targetView) {
        CommonPopupMenu menu = new CommonPopupMenu(this);
        String[] items = this.getResources().getStringArray(R.array.add_menu2);
        menu.setWidth(this.getResources().getDimensionPixelSize(R.dimen.px280));
        menu.addTextItems(items);
        menu.addImgItems(new Integer[]{R.drawable.ic_menu_create_lesson,R.drawable.ic_add_class1});
        menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                switch (i) {
                    case 1:
                        if (JudgementUtil.checkTeachingAbility(ClassesListActivity.this)) {
                            ClassesListActivity.this.startActivityForResult(new Intent(ClassesListActivity.this, CreateClassActivity.class),CourseConstant.CODE_CREATE_CLASS);
                        }
                        break;
                    case 0:
                        if (JudgementUtil.checkTeachingAbility(ClassesListActivity.this)) {
                            //老师可以开课
                            Intent intent = new Intent(ClassesListActivity.this, LessonCreationActivity.class);
                            ClassesListActivity.this.startActivityForResult(intent, CourseConstant.CODE_CREATE_LESSON);
                        }
                        break;
                }

            }
        });
        int offset = getResources().getDimensionPixelSize(R.dimen.px68);
        menu.show(targetView, offset);
    }

    @Override
    public void updateData(boolean justNative) {
        ((IUpdateMethod)fragmentList.get(1)).updateData(justNative);
    }

    @Override
    public void updateItem(int position, Object obj,Object... others) {
        ((IUpdateMethod)fragmentList.get(1)).updateItem(position,obj,others);
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

    class FrgStatePageAdapter extends FragmentStatePagerAdapter {

        public FrgStatePageAdapter(FragmentManager fm) {
            super(fm);
        }

        private ArrayList<Fragment> listFrg = new ArrayList<Fragment>();

        public void setList(ArrayList<Fragment> listFrg) {
            this.listFrg = listFrg;
        }

        @Override
        public Fragment getItem(int arg0) {
            return listFrg.get(arg0);
        }

        @Override
        public int getCount() {
            return listFrg.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CourseConstant.CODE_CANCEL_LESSON:
                case CourseConstant.CODE_EDIT_LESSON:
                case CourseConstant.CODE_LESSON_AGAIN:
                case CourseConstant.CODE_CREATE_LESSON:
                    updateData(false);
                    break;
                case CourseConstant.CODE_CREATE_CLASS:
                    ((MyClassFragment)fragmentList.get(0)).refresh();
                    break;
            }
        }
    }


    public static void invoke(Activity context,int tab){
        Intent intent=new Intent(context,ClassesListActivity.class);
        if(tab>=0){
            intent.putExtra(EXTRA_TAB,tab);
        }
        context.startActivity(intent);
    }
}
