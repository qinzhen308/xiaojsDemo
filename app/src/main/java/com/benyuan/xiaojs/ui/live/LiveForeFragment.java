package com.benyuan.xiaojs.ui.live;
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
 * Date:2016/12/9
 * Desc:
 *
 * ======================================================================================== */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.xf_foundation.Su;
import com.benyuan.xiaojs.data.SecurityManager;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.base.TabFragmentPagerAdapter;
import com.benyuan.xiaojs.ui.widget.LazyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LiveForeFragment extends BaseFragment {

//    @BindView(R.id.home_lesson_tab_teach)
//    BottomLineTextView mTeach;
//    @BindView(R.id.home_lesson_tab_learn)
//    BottomLineTextView mLearn;
    @BindView(R.id.live_fore_pager)
    LazyViewPager mPager;

    //List<BottomLineTextView> mTabs;

    @Override
    protected View getContentView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_fore_live, null);
        return v;
    }

    @Override
    protected void init() {
        boolean isTeacher = SecurityManager.checkPermission(mContext, Su.Permission.COURSE_OPEN_CREATE);
        Fragment f1 = new LiveFragment();
        Bundle b1 = new Bundle();
        b1.putInt(LiveConstant.KEY_USER_TYPE,LiveConstant.USER_TEACHER);
        f1.setArguments(b1);
        Fragment f2 = new LiveFragment();
        Bundle b2 = new Bundle();
        b2.putInt(LiveConstant.KEY_USER_TYPE,LiveConstant.USER_STUDENT);
        f2.setArguments(b2);
        List<Fragment> fs = new ArrayList<>();
        fs.add(f1);
        fs.add(f2);
        PagerAdapter adapter = new TabFragmentPagerAdapter(getChildFragmentManager(), fs);
        mPager.setAdapter(adapter);
        mPager.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switchTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void switchTab(int position){
        mPager.setCurrentItem(position);
    }
}
