package cn.xiaojs.xma.ui.lesson;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.data.CategoriesManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CSubject;
import retrofit2.http.PUT;

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

public class TeachingSubjectAdapter extends AbsSwipeAdapter<CSubject, TeachingSubjectAdapter.Holder> implements View.OnClickListener{
    private String mParentId;
    private OnSubjectSelectedListener mSelectedListener;

    public TeachingSubjectAdapter(Context context, PullToRefreshSwipeListView listView, String parentId) {
        super(context, listView);
        mParentId = parentId;
    }

    public void setOnSubjectSelectedListener(OnSubjectSelectedListener listener) {
        mSelectedListener = listener;
    }

    @Override
    public void onClick(View v) {
        Holder holder = (Holder) v.getTag();
        List<CSubject> data = getList();
        if (holder != null) {
            for (CSubject cSubject : data) {
                cSubject.setCheck(false);
            }
            CSubject subject = data.get(holder.position);
            subject.setCheck(true);

            if (mSelectedListener != null) {
                mSelectedListener.onSubjectSelected(subject);
            }

            if (subject.getType() == CSubject.TYPE_NO_CHILD) {
                notifyDataSetChanged();
            }
        }
    }

    @Override
    protected void setViewContent(Holder holder, CSubject bean, int position) {
        holder.position = position;
        if (bean != null) {
            holder.subjectTv.setText(bean.getName());
        }

        holder.selectedStatus.setVisibility(bean.isCheck() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.teaching_subject_list_item, null);
        view.setOnClickListener(this);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        holder.subjectTv = (TextView) view.findViewById(R.id.subject_name);
        holder.selectedStatus = (ImageView) view.findViewById(R.id.selected_status);
        return holder;
    }

    @Override
    protected void doRequest() {
        CategoriesManager.getSubjects(mContext, mParentId, mPagination, new APIServiceCallback<List<CSubject>>() {
            @Override
            public void onSuccess(List<CSubject> object) {
                TeachingSubjectAdapter.this.onSuccess(object);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                TeachingSubjectAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    public CSubject getSelectedSubject() {
        List<CSubject> data = getList();
        if (data != null) {
            for (CSubject subject : data) {
                if (subject.isCheck()) {
                    return subject;
                }
            }
        }

        return null;
    }

    static class Holder extends BaseHolder{
        public TextView subjectTv;
        public ImageView selectedStatus;
        public int position;

        public Holder(View view) {
            super(view);
        }
    }

    public interface OnSubjectSelectedListener {
        public void onSubjectSelected(CSubject subject);
    }

}
