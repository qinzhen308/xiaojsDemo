package com.benyuan.xiaojs.ui.home;

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

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.widget.BlockTabView;
import com.benyuan.xiaojs.ui.widget.MessageImageView;
import com.benyuan.xiaojs.ui.widget.banner.BannerAdapter;
import com.benyuan.xiaojs.ui.widget.banner.BannerBean;
import com.benyuan.xiaojs.ui.widget.banner.BannerView;
import com.benyuan.xiaojs.util.ToastUtil;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.home_banner)
    BannerView mBanner;
    @BindView(R.id.home_my_cls)
    BlockTabView mClass;
    @BindView(R.id.home_my_live)
    BlockTabView mLive;
    @BindView(R.id.home_teacher)
    BlockTabView mTeacher;
    @BindView(R.id.home_person)
    BlockTabView mPerson;

    @BindView(R.id.title)
    View mTitle;
    @BindView(R.id.right_view)
    MessageImageView mRightImage;

    AutoPullToRefreshListView mList;

    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_home, null);
        mList = (AutoPullToRefreshListView) v.findViewById(R.id.home_list);
        View header = mContext.getLayoutInflater().inflate(R.layout.layout_home_list_header,null);
        mList.getRefreshableView().addHeaderView(header);
        View view = new View(mContext);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mContext.getResources().getDimensionPixelSize(R.dimen.px100));
        view.setLayoutParams(lp);
        mList.getRefreshableView().addFooterView(view);
        return v;
    }

    @Override
    protected void init() {
        AbsSwipeAdapter ada = new CourseAdapter(mContext,mList);
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
        List<CourseBlock> cs = new ArrayList<>();
        CourseBlock c1 = new CourseBlock(mContext);
        CourseBlock c2 = new CourseBlock(mContext);
        cs.add(c1);
        cs.add(c2);
        c1.setData(new Date(System.currentTimeMillis() + 1000*60*60*36));
        c2.setData(new Date(System.currentTimeMillis() + 1000*60*60*23));
        mClass.setViews(mContext.getString(R.string.my_xiaojs),mContext.getResources().getStringArray(R.array.course_block_tabs),cs,mContext.getString(R.string.schedule));


        List<LiveBlock> ls = new ArrayList<>();
        LiveBlock l1 = new LiveBlock(mContext);
        LiveBlock l2 = new LiveBlock(mContext);
        ls.add(l1);
        ls.add(l2);
        l1.setData();
        l2.setData();
        mLive.setViews(mContext.getString(R.string.living_time),mContext.getResources().getStringArray(R.array.live_block_tabs),ls,mContext.getString(R.string.live_today));

        List<PersonBlock> pss = new ArrayList<>();
        PersonBlock p = new PersonBlock(mContext);
        pss.add(p);
        p.setData();
        mTeacher.setViews(mContext.getString(R.string.teacher_recommend),null,pss,mContext.getString(R.string.become_teacher));

        List<PersonBlock> ps = new ArrayList<>();
        PersonBlock p1 = new PersonBlock(mContext);
        ps.add(p1);
        p1.setData();
        mPerson.setViews(mContext.getString(R.string.perhaps_interest),null,ps,mContext.getString(R.string.recommend_self));
        mList.getRefreshableView().setOnScrollListener(new OnScrollYListener(mList.getRefreshableView()) {
            @Override
            public void onScrollY(int y) {
                handleScrollChanged(y);
            }
        });
    }

    private void handleScrollChanged(int offsetY) {
        if (offsetY >= 0) {
            int bannerHeight = mBanner.getMeasuredHeight();
            int bgColor = 0;
            if (offsetY >= bannerHeight && offsetY <= 2 * bannerHeight) {
                int alpha = (int)((float)(offsetY - bannerHeight) / bannerHeight * 255);
                Logger.d("alpha = " + alpha);
                bgColor = Color.argb(alpha,0xf5,0xf5,0xf5);
                mTitle.setBackgroundColor(bgColor);
            }else if (offsetY > 2 * bannerHeight){
                bgColor = Color.argb(255,0xf5,0xf5,0xf5);
                mTitle.setBackgroundColor(bgColor);
                mRightImage.setImageResource(R.drawable.home_message_selector2);
            }else if (offsetY < bannerHeight){
                mRightImage.setImageResource(R.drawable.home_message_selector);
            }
        }
    }

    @OnClick({R.id.right_view})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.right_view:
                ToastUtil.showToast(mContext,"message");
                break;
        }
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