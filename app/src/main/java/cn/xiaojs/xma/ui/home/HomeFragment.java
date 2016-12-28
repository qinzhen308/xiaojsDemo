package cn.xiaojs.xma.ui.home;

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

import android.animation.Animator;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.widget.banner.BannerAdapter;
import cn.xiaojs.xma.ui.widget.banner.BannerBean;
import cn.xiaojs.xma.ui.widget.banner.BannerView;
import cn.xiaojs.xma.util.DeviceUtil;

public class HomeFragment extends BaseFragment {

    @BindView(R.id.home_banner)
    BannerView mBanner;
//    @BindView(R.id.home_my_cls)
//    BlockTabView mClass;
//    @BindView(R.id.home_my_live)
//    BlockTabView mLive;
//    @BindView(R.id.home_teacher)
//    BlockTabView mTeacher;
//    @BindView(R.id.home_person)
//    BlockTabView mPerson;

    @BindView(R.id.title)
    View mTitle;
    @BindView(R.id.home_moment_mark_wrapper)
    RelativeLayout mMark;
    @BindView(R.id.home_moment_mark_right_wrapper)
    LinearLayout mRightMark;
//    @BindView(R.id.right_view)
//    MessageImageView mRightImage;

    PullToRefreshSwipeListView mList;
    private boolean mScrolled;

    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_home, null);
        mList = (PullToRefreshSwipeListView) v.findViewById(R.id.home_list);
        View header = mContext.getLayoutInflater().inflate(R.layout.layout_home_list_header,null);
        mList.getRefreshableView().addHeaderView(header);
        return v;
    }

    @Override
    protected void init() {
        AbsSwipeAdapter ada = new HomeMomentAdapter(mContext,mList);
        mList.setAdapter(ada);

        BannerBean b1 = new BannerBean();
        BannerBean b2 = new BannerBean();
        BannerBean b3 = new BannerBean();
        BannerBean b4 = new BannerBean();
        b1.resId = R.drawable.ic_ad;
        b2.resId = R.drawable.ic_ad;
        b3.resId = R.drawable.ic_ad;
        b4.resId = R.drawable.ic_ad;

        List<BannerBean> beanList = new ArrayList<>();
        beanList.add(b1);
        beanList.add(b2);
        beanList.add(b3);
        beanList.add(b4);
        BannerAdapter adapter = new BannerAdapter(mContext,beanList);
        mBanner.setAdapter(adapter);

        mList.getRefreshableView().setOnScrollListener(new OnScrollYListener(mList.getRefreshableView().getWrappedList()) {
            @Override
            public void onScrollY(int y) {
                handleScrollChanged(y);
                mark();
            }
        });
    }

    private void handleScrollChanged(int offsetY) {
        if (offsetY >= 0) {
            int bannerHeight = mBanner.getMeasuredHeight();
            if (offsetY >= 0 && offsetY <= bannerHeight) {
//                int alpha = (int)((float)(offsetY - bannerHeight) / bannerHeight * 255);
//                Logger.d("alpha = " + alpha);
//                bgColor = Color.argb(alpha,0xf5,0xf5,0xf5);
                mTitle.setBackgroundResource(R.drawable.ic_home_title_bg);
                //mTitle.setBackgroundColor(bgColor);
            }else if (offsetY > bannerHeight){
                int bgColor = Color.argb(255,0xf5,0xf5,0xf5);
                mTitle.setBackgroundColor(bgColor);
            }
        }
    }

    @OnClick({R.id.home_moment_mark_wrapper,R.id.home_moment_mark_right_wrapper})
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_moment_mark_wrapper:
            case R.id.home_moment_mark_right_wrapper:
                Intent intent = new Intent(mContext,MomentUpdateActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.right_view:
//                ToastUtil.showToast(mContext,"message");
                break;
        }
    }

    //动态更新的提示转换动画
    private void mark(){
        if (!mScrolled){
            mMark.animate().translationX(getMarkScrollDistance()).start();
            mMark.animate().setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mMark.setVisibility(View.INVISIBLE);
                    mRightMark.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mScrolled = true;
        }
    }

    private int getMarkScrollDistance(){
        int l[] = new int[2];
        mMark.getLocationOnScreen(l);
        int endX = DeviceUtil.getScreenWidth(mContext) - mMark.getWidth() / 2;
        return endX - l[0];
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
