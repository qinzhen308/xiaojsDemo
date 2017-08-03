package cn.xiaojs.xma.ui.grade;
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
 * Date:2017/1/9
 * Desc:我的资料库
 *
 * ======================================================================================== */

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;


import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.data.download.DownloadProvider;
import cn.xiaojs.xma.ui.widget.NoScrollViewPager;


public class MaterialActivity extends FragmentActivity {

    @BindView(R.id.lay_tab_group)
    RadioGroup tabGroupLayout;
    @BindView(R.id.tab_viewpager)
    NoScrollViewPager tabPager;
    @BindView(R.id.left_image)
    ImageView backBtn;
    @BindView(R.id.upload_btn)
    ImageView uploadBtn;
    @BindView(R.id.mode_btn)
    ImageView modeBtn;
    @BindView(R.id.cancel_btn)
    Button cancelBtn;
    @BindView(R.id.choice_btn)
    Button choiceBtn;
    public static final String EXTRA_TAB="extra_tab";
    private int curCheckedTabId;


    private ArrayList<Fragment> fragmentList;
    private MaterialFragment materialFragment;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material_tab);
        ButterKnife.bind(this);
        initView();
        int tab=getIntent().getIntExtra(EXTRA_TAB,-1);
        if(tab > 0){
            initToTab(tab);
        }


    }

    @OnClick({R.id.left_image,R.id.cancel_btn,
            R.id.upload_btn, R.id.mode_btn, R.id.choice_btn, R.id.tab_material,R.id.tab_download})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:             //返回
                finish();
                break;
            case R.id.upload_btn:             //上传文件
                materialFragment.upload();
                break;
            case R.id.mode_btn:               //进入选择模式
                changeChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                break;
            case R.id.cancel_btn:             //取消
                changeChoiceMode(ListView.CHOICE_MODE_NONE);
                break;
            case R.id.choice_btn:             //全选OR取消全选
                changeChoiceStatus();
                break;
            case R.id.tab_material:
                if(curCheckedTabId!=R.id.tab_material){
                    tabPager.setCurrentItem(0);
                    curCheckedTabId=R.id.tab_material;
                }
                break;
            case R.id.tab_download:
                if(curCheckedTabId!=R.id.tab_download){
                    tabPager.setCurrentItem(1);
                    curCheckedTabId=R.id.tab_download;
                }
                break;
        }
    }

    private void initView() {


        DownloadProvider.updateCount(this);


        tabGroupLayout.check(R.id.tab_material);
        uploadBtn.setImageResource(R.drawable.upload_selector);
        modeBtn.setImageResource(R.drawable.ic_datasection_selector);


        fragmentList = new ArrayList<>(2);

        materialFragment = new MaterialFragment();

        fragmentList.add(materialFragment);
        fragmentList.add(new DownloadFragment());

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
                    curCheckedTabId=R.id.tab_material;
                    tabGroupLayout.check(R.id.tab_material);
                    switchOperaBtn(R.id.tab_material);
                }else{
                    curCheckedTabId=R.id.tab_download;
                    tabGroupLayout.check(R.id.tab_download);
                    switchOperaBtn(R.id.tab_download);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabPager.setScrollEnable(false);
    }

    private void initToTab(int tab){
        if(tab==0){
            tabPager.setCurrentItem(0);
            tabGroupLayout.check(R.id.tab_material);
            curCheckedTabId = R.id.tab_material;
        }else if(tab==1){
            tabPager.setCurrentItem(1);
            tabGroupLayout.check(R.id.tab_download);
            curCheckedTabId = R.id.tab_download;
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
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void switchOperaBtn(@IdRes int tabId) {
        if (tabId == R.id.tab_material) {
            modeBtn.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);

        }else {

            changeChoiceMode(ListView.CHOICE_MODE_NONE);

            modeBtn.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.GONE);
        }

        cancelBtn.setVisibility(View.GONE);
        choiceBtn.setVisibility(View.GONE);
    }

    private void changeChoiceStatus() {

        if (materialFragment.cancelChoiceAll()) {
            choiceBtn.setText(R.string.choice_all);
        } else {
            choiceBtn.setText(R.string.cancel_choice_all);
        }

        materialFragment.changeChoiceStatus();

    }

    private void changeChoiceMode(int choiceMode) {
        choiceBtn.setText(R.string.choice_all);
        if (choiceMode == ListView.CHOICE_MODE_MULTIPLE) {

            backBtn.setVisibility(View.GONE);
            uploadBtn.setVisibility(View.GONE);
            modeBtn.setVisibility(View.GONE);

            choiceBtn.setVisibility(View.VISIBLE);
            cancelBtn.setVisibility(View.VISIBLE);
        } else {

            backBtn.setVisibility(View.VISIBLE);
            uploadBtn.setVisibility(View.VISIBLE);
            modeBtn.setVisibility(View.VISIBLE);

            choiceBtn.setVisibility(View.GONE);
            cancelBtn.setVisibility(View.GONE);
        }

        if (materialFragment.isAdded()) {
            materialFragment.changeChoiceMode(choiceMode);
        }
    }


}
