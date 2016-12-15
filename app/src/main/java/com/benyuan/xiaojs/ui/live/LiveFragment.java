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
 * Date:2016/10/11
 * Desc:
 *
 * ======================================================================================== */

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.core.GridViewWithHeaderAndFooter;
import com.benyuan.xiaojs.common.pulltorefresh.core.PullToRefreshGridView;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.benyuan.xiaojs.ui.widget.CanInScrollviewListView;
import com.benyuan.xiaojs.ui.widget.HorizontalAdaptScrollerView;

public class LiveFragment extends BaseFragment {

    PullToRefreshGridView mGrid;
    HorizontalAdaptScrollerView mHorizontalListView;
//    BottomLineTextView mTeach;
//    BottomLineTextView mLearn;
    CanInScrollviewListView mLessonList;

    View mHeader;

    private int mUserType;
    @Override
    protected View getContentView() {
        View v = mContext.getLayoutInflater().inflate(R.layout.fragment_moment , null);
        mGrid = (PullToRefreshGridView) v.findViewById(R.id.live_grid);

        mHeader = LayoutInflater.from(mContext).inflate(R.layout.layout_live_header,null);
        mHorizontalListView = (HorizontalAdaptScrollerView) mHeader.findViewById(R.id.home_live_brilliant);
        mGrid.getRefreshableView().addHeaderView(mHeader);

        View view = new View(mContext);
        GridViewWithHeaderAndFooter.LayoutParams lp = new GridViewWithHeaderAndFooter.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,mContext.getResources().getDimensionPixelSize(R.dimen.px100));
        view.setLayoutParams(lp);
        mGrid.getRefreshableView().addFooterView(view);

//        mTeach = (BottomLineTextView) mHeader.findViewById(R.id.home_lesson_tab_teach);
//        mLearn = (BottomLineTextView) mHeader.findViewById(R.id.home_lesson_tab_learn);
        mLessonList = (CanInScrollviewListView) mHeader.findViewById(R.id.home_live_list);

        mLessonList.setNeedDivider(true);
        mLessonList.setDividerHeight(mContext.getResources().getDimensionPixelSize(R.dimen.px30));
        mLessonList.setDividerColor(R.color.main_bg);
        return v;
    }

    @Override
    protected void init() {
        Bundle bundle = getArguments();
        if (bundle != null){
            mUserType = bundle.getInt(LiveConstant.KEY_USER_TYPE,LiveConstant.USER_STUDENT);
        }
        RecyclerView.Adapter adapter = new LiveBrilliantAdapter(mContext);
        mHorizontalListView.setItemVisibleCountType(HorizontalAdaptScrollerView.ItemVisibleTypeCount.TYPE_FREE);
        mHorizontalListView.setItemVisibleCount(1.7f);
        mHorizontalListView.setAdapter(adapter);
        ViewGroup.LayoutParams lp = mHorizontalListView.getLayoutParams();
        lp.height = getResources().getDimensionPixelSize(R.dimen.px370);
        mHorizontalListView.setLayoutParams(lp);
        mGrid.setAdapter(new LiveAdapter(mContext,mGrid));

//        if (mUserType == LiveConstant.USER_TEACHER){
//            mTeach.setSelected(true);
//        }else {
//            mLearn.setSelected(true);
//        }

//        mTeach.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switchTab(0);
//            }
//        });
//        mLearn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                switchTab(1);
//            }
//        });

        mLessonList.setAdapter(new LiveLessonAdapter(mContext));
    }

//    private void switchTab(int position){
//        Fragment f = getParentFragment();
//        if (f != null && f instanceof LiveForeFragment){
//            ((LiveForeFragment)f).switchTab(position);
//        }
//    }
}
