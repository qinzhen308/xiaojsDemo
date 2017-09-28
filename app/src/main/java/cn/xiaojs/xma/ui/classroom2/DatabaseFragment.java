package cn.xiaojs.xma.ui.classroom2;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.RadioGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.lesson.xclass.ClassesListActivity;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class DatabaseFragment extends BottomSheetFragment {

    @BindView(R.id.tab_bar)
    RadioGroup tabGroup;
    @BindView(R.id.tab_viewpager)
    ViewPager viewPager;

    private ArrayList<Fragment> fragmentList;


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_database, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fragmentList = new ArrayList<>(2);
        fragmentList.add(new DatabaseListFragment());
        fragmentList.add(new DatabaseListFragment());

        FrgStatePageAdapter  pageAdapter = new FrgStatePageAdapter(getChildFragmentManager());
        pageAdapter.setList(fragmentList);
        viewPager.setAdapter(pageAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tabGroup.check(R.id.tab_my);
                        break;
                    case 1:
                        tabGroup.check(R.id.tab_class);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @OnClick({R.id.tab_my, R.id.tab_class})
    void onViewClick(View view) {
        switch(view.getId()) {
            case R.id.tab_my:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tab_class:
                viewPager.setCurrentItem(1);
                break;
        }
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

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }
    }
}
