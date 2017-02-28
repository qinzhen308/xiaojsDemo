package cn.xiaojs.xma.ui.lesson;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.model.CSubject;
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

public class TeachingSubjectFragment extends BaseFragment implements TeachingSubjectAdapter.OnSubjectSelectedListener{
    @BindView(R.id.search_subject)
    EditTextDel mSearchSubjectEdt;
    @BindView(R.id.selected_subject)
    TextView mSelectedSubjectTv;
    @BindView(R.id.subject_list)
    PullToRefreshSwipeListView mSubjectListView;
    @BindView(R.id.selected_subject_layout)
    View mSelectedSubjectLayout;
    @BindView(R.id.right_view)
    TextView mRightView;
    @BindView(R.id.middle_view)
    TextView mMiddleView;

    private TeachingSubjectAdapter mSubjectAdapter;
    private String mParentId;
    private CSubject mParentCSubject;

    @Override
    protected View getContentView() {
        return LayoutInflater.from(mContext).inflate(R.layout.fragment_teaching_subject, null);
    }

    @Override
    protected void init() {
        initView();
        loadData();
    }

    @OnClick({R.id.left_image, R.id.right_view})
    public void onClick(View v) {
        Activity activity = getActivity();
        switch (v.getId()) {
            case R.id.left_image:
                if (activity instanceof TeachingSubjectActivity) {
                    ((TeachingSubjectActivity) activity).onBackBtnClicked();
                }
                break;
            case R.id.right_view:
                if (activity instanceof TeachingSubjectActivity) {
                    ((TeachingSubjectActivity) activity).onFinishBtnClicked(mSubjectAdapter != null
                            ? mSubjectAdapter.getSelectedSubject() : null);
                }
                break;
        }
    }

    private void initView() {
        mRightView.setTextColor(mContext.getResources().getColor(R.color.font_orange));
        mSearchSubjectEdt.setVisibility(View.VISIBLE);
        if (mParentCSubject != null && mParentCSubject.getType() == CSubject.TYPE_NO_CHILD) {
            mRightView.setVisibility(View.VISIBLE);
        } else {
            mRightView.setVisibility(View.GONE);
        }

        //set title
        if (mParentCSubject == null) {
            mMiddleView.setText(mContext.getString(R.string.teach_ability));
        } else {
            mMiddleView.setText(mParentCSubject.getName());
        }
    }

    private void loadData() {
        mSubjectAdapter = new TeachingSubjectAdapter(mContext, mSubjectListView, mParentId);
        //mSubjectAdapter.setHasChild(mParentCSubject != null ? mParentCSubject.getType() == CSubject.TYPE_NO_CHILD : true);
        mSubjectAdapter.setOnSubjectSelectedListener(this);
        mSubjectListView.setAdapter(mSubjectAdapter);
    }

    public void setParentId(String parentId) {
        mParentId = parentId;
    }

    public void setParentSubject(CSubject subject) {
        mParentCSubject = subject;
        mParentId = subject != null ? subject.getId() : null;
    }


    @Override
    public void onSubjectSelected(CSubject subject) {
        Activity activity = getActivity();
        if (activity instanceof TeachingSubjectActivity) {
            ((TeachingSubjectActivity) activity).onSubjectSelected(subject);
        }
    }
}
