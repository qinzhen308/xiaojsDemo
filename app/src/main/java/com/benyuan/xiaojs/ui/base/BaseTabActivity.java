package com.benyuan.xiaojs.ui.base;

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
 * Date:2016/10/11
 * Desc:底部带TAB的Activity
 *
 * ======================================================================================== */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.LazyViewPager;
import com.benyuan.xiaojs.ui.widget.RedTipTextView;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseTabActivity extends BaseActivity{

    LazyViewPager mViewPager;
    LinearLayout mMenu;
    FragmentPagerAdapter mAdapter;
    private int[] mTitles;
    private int[] mDrawables;
    private List<RedTipTextView> mTabs;
    private Button mCenter;
    private int mCurrentIndex;

    private int mButtonType;
    protected static final int BUTTON_TYPE_CENTER = 1;

    @Override
    protected void addViewContent() {
        View v = addView(R.layout.activity_base_tab);
        mViewPager = (LazyViewPager) v.findViewById(R.id.tab_fragment_content);
        mMenu = (LinearLayout) v.findViewById(R.id.tab_menu);
        initView();
    }

    protected abstract void initView();

    protected void addViews(int[] tabTxt, int[] drawables, List<? extends Fragment> fragments){
        mTitles = tabTxt;
        mDrawables = drawables;
        createTabs();
        mAdapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fragments);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setScrollState(false);
        mViewPager.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    protected void setButtonType(int type){
        this.mButtonType = type;
    }

    private void createTabs(){
        LayoutInflater inflater = getLayoutInflater();
        mTabs = new ArrayList<>();
        for (int i = 0 ;i < mTitles.length ;i++){
            FrameLayout fl = (FrameLayout) inflater.inflate(R.layout.layout_base_tab_item,null);
            RedTipTextView text = (RedTipTextView) fl.findViewById(R.id.base_tab_text);
            if (mButtonType == BUTTON_TYPE_CENTER){
                text.setPadding(0,(int) getResources().getDimension(R.dimen.px30),0,0);
            }
            text.setText(mTitles[i]);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
            if (mDrawables != null && mDrawables.length > i) {
                text.setCompoundDrawablesWithIntrinsicBounds(0,
                        mDrawables[i], 0, 0);
            }
            text.setOnClickListener(new OnTabClick(i));
            mTabs.add(text);
            mMenu.addView(fl,lp);
        }
        if (mButtonType == BUTTON_TYPE_CENTER){
            addCenterButton();
        }
        setTabSelected(0);
    }

    private void addCenterButton(){
        FrameLayout fl = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_base_center_button,null);
        mCenter = (Button) fl.findViewById(R.id.center_button);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,1);
        fl.setLayoutParams(lp);
        mMenu.addView(fl,mTitles.length/2);
        mCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCenterButtonClick();
            }
        });
    }

    protected void onCenterButtonClick(){
        Toast.makeText(this,"OnCenterButtonClick", Toast.LENGTH_SHORT).show();
    }

    protected void setTabSelected(int position){
        for (TextView t : mTabs){
            t.setSelected(false);
        }
        mTabs.get(position).setSelected(true);
        mViewPager.setCurrentItem(position);
        mCurrentIndex = position;
        onTabClick(position);
    }

    protected void onTabClick(int position){

    }

    protected final void setTip(int position ,boolean enable){
        if (position < mTabs.size()){
            mTabs.get(position).setTipEnable(enable);
        }
    }

    public Fragment getCurrentFragment(){
        return mAdapter.getItem(mCurrentIndex);
    }

    public Fragment getFragmentByPosition(int position){
        return mAdapter.getItem(position);
    }

    private class OnTabClick implements View.OnClickListener{

        private int index;
        public OnTabClick(int index){
            this.index = index;
        }
        @Override
        public void onClick(View v) {
            setTabSelected(index);
        }
    }

}
