package cn.xiaojs.xma.ui.classroom2;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.classroom.main.ClassroomController;
import cn.xiaojs.xma.ui.classroom.main.Constants;
import cn.xiaojs.xma.ui.classroom2.base.BottomSheetFragment;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.material.AddNewFragment;
import cn.xiaojs.xma.ui.classroom2.material.DatabaseListFragment;
import cn.xiaojs.xma.ui.classroom2.material.DownloadListFragment;

/**
 * Created by maxiaobao on 2017/9/26.
 */

public class DatabaseFragment extends BottomSheetFragment
        implements DatabaseListFragment.OnOperatingListener, DialogInterface.OnKeyListener{

    @BindView(R.id.tab_bar)
    RadioGroup tabGroup;
    @BindView(R.id.tab_viewpager)
    ViewPager viewPager;
    @BindView(R.id.download_btn)
    ImageView downloadBtn;

    private ArrayList<Fragment> fragmentList;

    private DatabaseListFragment databaseListFragment;


    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container,
                           @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_classroom2_database, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getDialog().setOnKeyListener(this);

        fragmentList = new ArrayList<>(2);

        databaseListFragment = new DatabaseListFragment();
        databaseListFragment.setOnOperatingListener(this);
        fragmentList.add(databaseListFragment);
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


    @OnClick({R.id.tab_my, R.id.tab_class, R.id.download_btn, R.id.add_btn})
    void onViewClick(View view) {
        switch(view.getId()) {
            case R.id.tab_my:
                viewPager.setCurrentItem(0);
                break;
            case R.id.tab_class:
                viewPager.setCurrentItem(1);
                break;
            case R.id.download_btn:
                downloadOrBack();

                break;
            case R.id.add_btn:
                openAddNew();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && data != null) {

            switch (requestCode) {
                case CTLConstant.REQUEST_MATERIAL_ADD_NEW:
                    databaseListFragment.refreshData();
                    break;
            }
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP){
            handleBackKey();
            return true;
        }

        return false;
    }

    @Override
    public void onEnterFolder() {
        downloadBtn.setImageResource(R.drawable.ic_back);
    }

    @Override
    public void onEnterSearch() {
        downloadBtn.setImageResource(R.drawable.ic_back);
    }

    @Override
    public void onBackSuper() {
        if (databaseListFragment.canBackSuper()) {
            return;
        }

        downloadBtn.setImageResource(R.drawable.ic_class_database_mydownload_1);
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


    private void openAddNew() {
        AddNewFragment addNewFragment = new AddNewFragment();
        Bundle data = new Bundle();
        data.putString(CTLConstant.EXTRA_DIRECTORY_ID, databaseListFragment.getCurrentDirectoryId());
        addNewFragment.setArguments(data);
        addNewFragment.setTargetFragment(this, CTLConstant.REQUEST_MATERIAL_ADD_NEW);
        addNewFragment.show(getFragmentManager(),"addnew");
    }

    private void downloadOrBack() {

        if (databaseListFragment.canBackSuper()) {
            databaseListFragment.backSuper();
        }else {
            DownloadListFragment listFragment = new DownloadListFragment();
            listFragment.show(getFragmentManager(),"downloadlist");
        }
    }

    private void handleBackKey() {

        if (databaseListFragment.canBackSuper()) {
            databaseListFragment.backSuper();
        }else {
            dismiss();
        }
    }
}
