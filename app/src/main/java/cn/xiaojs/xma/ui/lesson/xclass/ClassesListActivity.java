package cn.xiaojs.xma.ui.lesson.xclass;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/5/22.
 */

public class ClassesListActivity extends FragmentActivity {

    @BindView(R.id.lay_tab_group)
    RadioGroup tabGroupLayout;
    @BindView(R.id.tab_viewpager)
    ViewPager tabPager;


    private ArrayList<Fragment> fragmentList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_list);
        ButterKnife.bind(this);

        initView();
    }

    @OnClick({R.id.left_image, R.id.add_btn})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.add_btn:
                //TODO 添加
                break;
        }
    }

    private void initView() {
        tabGroupLayout.check(R.id.tab_class);

        fragmentList = new ArrayList<>(2);
        fragmentList.add(new ClassFragment());
        fragmentList.add(new LessonFragment());

        FrgStatePageAdapter adapter = new FrgStatePageAdapter(getSupportFragmentManager());
        adapter.setList(fragmentList);
        tabPager.setAdapter(adapter);

        tabPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
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
}
