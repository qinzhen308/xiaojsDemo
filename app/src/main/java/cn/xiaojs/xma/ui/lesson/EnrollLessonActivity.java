package cn.xiaojs.xma.ui.lesson;
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
 * Date:2017/1/15
 * Desc:我报的班
 *
 * ======================================================================================== */

import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.model.Criteria;
import cn.xiaojs.xma.ui.base.BaseActivity;

public class EnrollLessonActivity extends BaseActivity {

    @BindView(R.id.enroll_lesson_list)
    PullToRefreshSwipeListView mList;

    @BindView(R.id.my_course_search)
    EditText mSearch;

    @BindView(R.id.filter_line)
    View mFilterLine;
    @BindView(R.id.course_filter)
    TextView mFilter;

    private int timePosition;
    private int statePosition;
    private int sourcePosition;

    private EnrollLessonAdapter mAdapter;

    @Override
    protected void addViewContent() {
        addView(R.layout.activity_enroll_lesson);
        needHeaderDivider(false);
        setMiddleTitle(R.string.course_of_learn);
        mAdapter = new EnrollLessonAdapter(this, mList);
        mList.setAdapter(mAdapter);
        mSearch.setHint("课程名称");
    }

    @OnClick({R.id.left_image, R.id.course_filter})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_image:
                finish();
                break;
            case R.id.course_filter:
                //TODO FILTER
                filter();
                break;
        }
    }

    private void filter() {

        CourseFilterDialog dialog = new CourseFilterDialog(this, false);
        dialog.setTimeSelection(timePosition);
        dialog.setStateSelection(statePosition);
        dialog.showAsDropDown(mFilterLine);
        mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_up, 0);
        dialog.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mFilter.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_filter_down, 0);
            }
        });
        dialog.setOnOkListener(new CourseFilterDialog.OnOkListener() {
            @Override
            public void onOk(int timePosition, int statePosition, int sourcePosition) {
                EnrollLessonActivity.this.timePosition = timePosition;
                EnrollLessonActivity.this.statePosition = statePosition;
                EnrollLessonActivity.this.sourcePosition = sourcePosition;
                Criteria criteria = LessonBusiness.getFilter(timePosition, statePosition, sourcePosition, false);
                if (mAdapter != null) {
                    mAdapter.request(criteria);
                }
            }
        });


    }
}