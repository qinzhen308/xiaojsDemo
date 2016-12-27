package cn.xiaojs.xma.ui.live;

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

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshGridView;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.HorizontalAdaptScrollerView;

public class LiveFragment extends BaseFragment {

    PullToRefreshGridView mGrid;
    HorizontalAdaptScrollerView mHorizontalListView;
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

        mLessonList.setAdapter(new LiveLessonAdapter(mContext));
    }

}
