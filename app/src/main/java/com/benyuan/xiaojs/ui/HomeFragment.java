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
import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.course.MyCourseAdapter;
import com.benyuan.xiaojs.ui.widget.BlockTabView;
import com.benyuan.xiaojs.ui.widget.banner.BannerAdapter;
import com.benyuan.xiaojs.ui.widget.banner.BannerBean;
import com.benyuan.xiaojs.ui.widget.banner.BannerView;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.home_banner)
    BannerView mBanner;
//    @BindView(R.id.home_function_area)
//    FunctionArea mFunction;
    @BindView(R.id.home_my_cls)
    BlockTabView mClass;
    @BindView(R.id.home_my_live)
    BlockTabView mLive;

    AutoPullToRefreshListView mList;

    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_home, null);
        mList = (AutoPullToRefreshListView) v.findViewById(R.id.home_list);
        View header = mContext.getLayoutInflater().inflate(R.layout.layout_home_list_header,null);
        mList.getRefreshableView().addHeaderView(header);
        return v;
    }

    @Override
    protected void init() {
        AbsSwipeAdapter ada = new MyCourseAdapter(mContext,mList,false);
        mList.setAdapter(ada);

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

//        List<BlockFragment> fs = new ArrayList<>();
//        fs.add(new HomeCourseFragment());
//        fs.add(new HomeCourseFragment());
//        mClass.show(mContext.getString(R.string.my_xiaojs),null,fs,getChildFragmentManager(),mContext.getString(R.string.schedule));
//
//        List<BlockFragment> fs1 = new ArrayList<>();
//        fs1.add(new LiveFragment());
//        fs1.add(new LiveFragment());
//        mLive.show("直播进行时",null,fs1,getChildFragmentManager(),"今日直播");
    }

    @OnClick({})
    public void onClick(View v) {

    }

    @Override
    public void onResume() {
        super.onResume();
        mBanner.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBanner.stop();
    }
}
