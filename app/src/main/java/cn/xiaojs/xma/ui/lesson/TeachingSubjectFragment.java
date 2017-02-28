package cn.xiaojs.xma.ui.lesson;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.ui.base.BaseFragment;
import cn.xiaojs.xma.ui.widget.EditTextDel;

/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/2/28
 * Desc:
 *
 * ======================================================================================== */

public class TeachingSubjectFragment extends BaseFragment {
    @BindView(R.id.search_subject)
    EditTextDel mSearchSubjectEdt;
    @BindView(R.id.selected_subject)
    TextView mSelectedSubjectTv;
    @BindView(R.id.subject_list)
    PullToRefreshSwipeListView mSubjectListView;

    private TeachingSubjectAdapter mSubjectAdapter;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_teaching_subject, null);
    }

    @Override
    protected void init() {
        loadData();
    }

    @OnClick({R.id.left_image})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_image:
                Activity activity = getActivity();
                if (activity instanceof TeachingSubjectActivity) {
                    ((TeachingSubjectActivity)activity).onBackClicked();
                }
                break;
        }
    }

    private void loadData() {
        mSubjectAdapter = new TeachingSubjectAdapter(mContext, mSubjectListView, null);
        mSubjectListView.setAdapter(mSubjectAdapter);
    }
}
