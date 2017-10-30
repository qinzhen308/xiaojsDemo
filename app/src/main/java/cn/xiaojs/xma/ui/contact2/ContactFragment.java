package cn.xiaojs.xma.ui.contact2;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;

/**
 * Created by maxiaobao on 2017/10/29.
 */

public class ContactFragment extends Fragment {

    @BindView(R.id.tab_bar)
    RadioGroup tabGroup;
    @BindView(R.id.tab_viewpager)
    ViewPager viewPager;

    private ArrayList<Fragment> fragmentList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact2, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fragmentList = new ArrayList<>(2);
        fragmentList.add(new FriendsFragment());
        fragmentList.add(new ClassroomsFragment());

        FrgStatePageAdapter pageAdapter = new FrgStatePageAdapter(getChildFragmentManager());
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
