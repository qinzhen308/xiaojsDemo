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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import butterknife.BindView;

public class MyCourseFragment extends BaseFragment {
    @BindView(R.id.listview)
    AutoPullToRefreshListView mListView;
    TextView mSearch;
    TextView mFilter;

    MyCourseAdapter adapter;
    private int lastItemPosition;
    private View mHeader;
    private PopupWindow mDialog;
    @Override
    protected View getContentView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_my_course,null);
        return v;
    }

    @Override
    protected void init() {
        Bundle b = getArguments();
        boolean isTeacher = false;
        if (b != null){
            isTeacher = b.getBoolean("key");
        }
        adapter = new MyCourseAdapter(mContext,mListView,isTeacher);
        mListView.setAdapter(adapter);
        mHeader = LayoutInflater.from(mContext).inflate(R.layout.layout_my_course_search,null);
        mSearch = (TextView) mHeader.findViewById(R.id.my_course_search);
        mFilter = (TextView) mHeader.findViewById(R.id.course_filter);
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFilter();
            }
        });
        mListView.getRefreshableView().addHeaderView(mHeader);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int i1, int i2) {
                if (firstVisibleItem > lastItemPosition){
                    //上拉
                    ((MyCourseActivity)mContext).hideTop();
                }else if (firstVisibleItem < lastItemPosition){
                    //下拉
                    if (firstVisibleItem > 0){
                    ((MyCourseActivity)mContext).showTop();
                }else {
                    ((MyCourseActivity)mContext).hideTop();
                }
                }else if (firstVisibleItem == lastItemPosition){
                    return;
                }
                lastItemPosition = firstVisibleItem;
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,MyCourseSearchActivity.class);
                mContext.startActivity(intent);
            }
        });
    }

    private void onFilter(){
        if (mDialog == null){
            mDialog = new CourseFilterDialog(mContext);
            mDialog.showAsDropDown(mHeader);
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
    }

}
