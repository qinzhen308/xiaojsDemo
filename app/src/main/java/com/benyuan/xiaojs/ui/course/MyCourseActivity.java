package com.benyuan.xiaojs.ui.course;
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
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseActivity;
import com.benyuan.xiaojs.ui.base.TabFragmentPagerAdapter;
import com.benyuan.xiaojs.ui.view.CommonPopupMenu;
import com.benyuan.xiaojs.ui.widget.LazyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class MyCourseActivity extends BaseActivity {

    @BindView(R.id.my_course_tab)
    LinearLayout mTab;
    @BindView(R.id.course_teach)
    TextView mLearn;
    @BindView(R.id.course_learn)
    TextView mTeach;
    @BindView(R.id.course_teach_line)
    View mLearnLine;
    @BindView(R.id.course_learn_line)
    View mTeachLine;
    @BindView(R.id.my_course_search)
    TextView mSearch;
    @BindView(R.id.my_course_pager)
    LazyViewPager mPager;
    @BindView(R.id.hover)
    View mHover;
    @BindView(R.id.top_wrapper)
    LinearLayout mTop;
    @BindView(R.id.course_filter)
    TextView mFilter;

    private PopupWindow mDialog;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_my_course);
        setMiddleTitle(R.string.my_course);
        setRightText(R.string.settings);
        setLeftImage(R.drawable.back_arrow);
        mPager.setScrollState(false);
        initTabContent();
        changeTab(0);
    }

    @OnClick({R.id.left_image,R.id.right_view,R.id.course_teach,R.id.course_learn,R.id.course_filter,R.id.my_course_search})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.left_image:
                finish();
                break;
            case R.id.right_view://右上角menu
                CommonPopupMenu menu = new CommonPopupMenu(this);
                String[] items = getResources().getStringArray(R.array.my_course_list);
                menu.addTextItems(items);
                menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        handleRightClick(i);
                    }
                });
                menu.showAsDropDown(mRightText);
                break;
            case R.id.course_teach://我学的课
                changeTab(1);
                break;
            case R.id.course_learn://我教的课
                changeTab(0);
                break;
            case R.id.course_filter://筛选按钮
                if (mDialog == null){
                    mDialog = new CourseFilterDialog(this);
                    mDialog.showAsDropDown(mHover);
                    mDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            mDialog = null;
                        }
                    });
                }else {
                    mDialog.dismiss();
                    mDialog = null;
                }
                break;
            case R.id.my_course_search:
                Intent intent = new Intent(MyCourseActivity.this,MyCourseSearchActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void initTabContent(){
        List<Fragment> fs = new ArrayList<>();
        Bundle b1 = new Bundle();
        b1.putBoolean("key",false);
        Bundle b2 = new Bundle();
        b2.putBoolean("key",true);
        Fragment f1 = new MyCourseFragment();
        f1.setArguments(b1);
        fs.add(f1);
        Fragment f2 = new MyCourseFragment();
        f2.setArguments(b2);
        fs.add(f2);
        TabFragmentPagerAdapter adapter = new TabFragmentPagerAdapter(getSupportFragmentManager(),fs);
        mPager.setAdapter(adapter);
        mPager.setCurrentItem(0);
    }

    private void changeTab(int position){
        int orange = getResources().getColor(R.color.main_orange);
        int common = getResources().getColor(R.color.def);
        if (position == 0){
            mTeach.setTextColor(orange);
            mLearn.setTextColor(common);
            mTeachLine.setVisibility(View.VISIBLE);
            mLearnLine.setVisibility(View.INVISIBLE);
        }else {
            mTeach.setTextColor(common);
            mLearn.setTextColor(orange);
            mTeachLine.setVisibility(View.INVISIBLE);
            mLearnLine.setVisibility(View.VISIBLE);
        }
        mPager.setCurrentItem(position);
    }

    private void handleRightClick(int position){
        switch (position){
            case 0://我要开课
                break;
            case 1://加入私密课
                break;
            default:
                break;
        }
    }

    public void hideTop(){
        mHover.setVisibility(View.GONE);
    }

    public void showTop(){
        mHover.setVisibility(View.VISIBLE);
    }
}
