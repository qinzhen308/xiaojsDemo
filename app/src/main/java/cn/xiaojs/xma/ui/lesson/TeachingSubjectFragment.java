package cn.xiaojs.xma.ui.lesson;

import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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

public class TeachingSubjectFragment extends BaseFragment implements TeachingSubjectAdapter.OnSubjectSelectedListener {
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
    private String mSelectedSubjectTxt;
    private int mSubjectBlueColor;
    private int mSubjectGrayColor;
    private String mCurrSelectedSubjectTxt;

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
        mSubjectBlueColor = mContext.getResources().getColor(R.color.subject_selected_blue);
        mSubjectGrayColor = mContext.getResources().getColor(R.color.font_dark_gray);
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

        mSelectedSubjectTv.setText(mSelectedSubjectTxt);
        mSelectedSubjectTv.setTextColor(mSubjectBlueColor);
    }

    private void loadData() {
        mSubjectAdapter = new TeachingSubjectAdapter(mContext, mSubjectListView, mParentId);
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

    public void setSelectedSubjectTxt(String txt) {
        mSelectedSubjectTxt = txt;
    }

    public String getParentSubjectTxt() {
        return mParentCSubject.getName();
    }


    @Override
    public void onSubjectSelected(CSubject subject) {
        if (subject != null) {
            if (subject.getType() == CSubject.TYPE_NO_CHILD) {
                mCurrSelectedSubjectTxt = subject.getName();
                mRightView.setVisibility(View.VISIBLE);
                mRightView.setText(getResources().getText(R.string.finish));
                updateSelectedSubjectText(subject.getName());
            } else {
                mRightView.setVisibility(View.GONE);
                Activity activity = getActivity();
                if (activity instanceof TeachingSubjectActivity) {
                    ((TeachingSubjectActivity) activity).onSubjectSelected(subject);
                }
            }
        }
    }

    private void updateSelectedSubjectText(String selectedSubjectTxt) {
        if (TextUtils.isEmpty(mSelectedSubjectTxt)) {
            mSelectedSubjectTv.setText(selectedSubjectTxt);
            mSelectedSubjectTv.setTextColor(mSubjectGrayColor);
        }

        if (TextUtils.isEmpty(selectedSubjectTxt)) {
            return;
        }

        String s = mSelectedSubjectTxt + "/" + selectedSubjectTxt;
        SpannableString spannableString = new SpannableString(s);
        ForegroundColorSpan blueSpan = new ForegroundColorSpan(mSubjectBlueColor);
        ForegroundColorSpan graySpan = new ForegroundColorSpan(mSubjectGrayColor);
        spannableString.setSpan(blueSpan, 0, mSelectedSubjectTxt.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(graySpan, mSelectedSubjectTxt.length() + 1, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        mSelectedSubjectTv.setText(spannableString);
    }
}
