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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.model.Criteria;
import com.benyuan.xiaojs.ui.base.BaseFragment;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

import butterknife.BindView;

public class LessonFragment extends BaseFragment {
    @BindView(R.id.listview)
    AutoPullToRefreshListView mListView;
    TextView mSearch;
    TextView mFilter;
    @BindView(R.id.hover)
    View mHover;
    @BindView(R.id.my_course_search)
    TextView mLocalSearch;
    @BindView(R.id.course_filter)
    TextView mLocalFilter;

    AbsSwipeAdapter adapter;
    private int lastItemPosition;
    private View mHeader;
    private CourseFilterDialog mDialog;

    private int timePosition;
    private int statePosition;
    private int sourcePosition;
    private boolean isTeacher;

    @Override
    protected View getContentView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.fragment_my_course, null);
        return v;
    }

    @Override
    protected void init() {
        Bundle b = getArguments();
        if (b != null) {
            isTeacher = b.getBoolean(CourseConstant.KEY_IS_TEACHER);
        }
        if (isTeacher) {
            adapter = new TeachLessonAdapter(mContext, mListView, this);
        } else {
            adapter = new EnrollLessonAdapter(mContext, mListView, this);
        }

        mListView.setAdapter(adapter);
        mHeader = LayoutInflater.from(mContext).inflate(R.layout.layout_my_course_search, null);
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
                if (firstVisibleItem > lastItemPosition) {
                    //上拉
                    hideTop();
                } else if (firstVisibleItem < lastItemPosition) {
                    //下拉
                    if (firstVisibleItem > 0) {
                        showTop();
                    } else {
                        hideTop();
                    }
                } else if (firstVisibleItem == lastItemPosition) {
                    return;
                }
                lastItemPosition = firstVisibleItem;
            }
        });
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSearch();
            }
        });

        mLocalFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onFilter();
            }
        });

        mLocalSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearch();
            }
        });
    }

    private void onFilter() {
        if (mDialog == null) {
            mDialog = new CourseFilterDialog(mContext, isTeacher);
            mDialog.setTimeSelection(timePosition);
            mDialog.setStateSelection(statePosition);
            if (mHover.getVisibility() == View.VISIBLE) {
                mDialog.showAsDropDown(mHover);
            } else {
                mDialog.showAsDropDown(mHeader);
            }
            mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_up, 0);
            mDialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_down, 0);
                    mDialog = null;
                }
            });
            mDialog.setOnOkListener(new CourseFilterDialog.OnOkListener() {
                @Override
                public void onOk(int timePosition, int statePosition,int sourcePosition) {
                    LessonFragment.this.timePosition = timePosition;
                    LessonFragment.this.statePosition = statePosition;
                    LessonFragment.this.sourcePosition = sourcePosition;
                    Criteria criteria = LessonBusiness.getFilter(timePosition, statePosition,sourcePosition,isTeacher);
                    request(criteria);
                }
            });
        } else {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    private void onSearch() {
        Intent intent = new Intent(mContext, LessonSearchActivity.class);
        intent.putExtra(CourseConstant.KEY_IS_TEACHER, isTeacher);
        mContext.startActivity(intent);
    }

    public void request(Criteria criteria) {
        if (adapter != null) {
            if (adapter instanceof TeachLessonAdapter) {
                ((TeachLessonAdapter) adapter).request(criteria);
            } else if (adapter instanceof EnrollLessonAdapter) {
                ((EnrollLessonAdapter) adapter).request(criteria);
            }

        }
    }

    public void showTop() {
        mHover.setVisibility(View.VISIBLE);
    }

    public void hideTop() {
        mHover.setVisibility(View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CourseConstant.CODE_CANCEL_LESSON:
            case CourseConstant.CODE_EDIT_LESSON:
            case CourseConstant.CODE_LESSON_AGAIN:
                if (resultCode == Activity.RESULT_OK){
                    if (adapter != null && adapter instanceof TeachLessonAdapter){
                        TeachLessonAdapter lessonAdapter = (TeachLessonAdapter)adapter;
                        lessonAdapter.setPageNum(1);
                        lessonAdapter.doRequest();
                    }
                }
                break;
        }
    }
}
