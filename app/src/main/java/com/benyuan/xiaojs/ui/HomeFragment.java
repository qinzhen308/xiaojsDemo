package com.benyuan.xiaojs.ui;

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
 * Desc:
 *
 * ======================================================================================== */

import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.widget.banner.BannerAdapter;
import com.benyuan.xiaojs.ui.widget.banner.BannerBean;
import com.benyuan.xiaojs.ui.widget.banner.PointIndicateView;
import com.benyuan.xiaojs.ui.widget.banner.ScrollerViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.home_banner)
    ScrollerViewPager mBanner;
    @BindView(R.id.home_banner_point)
    PointIndicateView mPoint;
    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_home, null);
        return v;
    }

    @Override
    protected void init() {
        BannerBean b1 = new BannerBean();
        BannerBean b2 = new BannerBean();
        BannerBean b3 = new BannerBean();
        BannerBean b4 = new BannerBean();
        b1.resId = R.mipmap.t_one;
        b2.resId = R.mipmap.t_two;
        b3.resId = R.mipmap.t_three;
        b4.resId = R.mipmap.t_four;

        List<BannerBean> beanList = new ArrayList<>();
        beanList.add(b1);
        beanList.add(b2);
        beanList.add(b3);
        beanList.add(b4);
        BannerAdapter adapter = new BannerAdapter(mContext,beanList);
        mBanner.setAdapter(adapter);
        mPoint.setViewPager(mBanner,beanList.size(),0,true,null);
    }

    @OnClick({})
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mBanner.startAutoScroll();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBanner.stopAutoScroll();
    }
}
