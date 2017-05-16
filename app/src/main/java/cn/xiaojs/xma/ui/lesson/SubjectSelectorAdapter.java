package cn.xiaojs.xma.ui.lesson;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.CSubject;
import cn.xiaojs.xma.model.Competency;

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

public class SubjectSelectorAdapter extends BaseAdapter implements View.OnClickListener{
    private List<Competency> mData;
    private Context mContext;
    private int flag;

    public SubjectSelectorAdapter(Context context) {
        mContext = context;
    }

    public SubjectSelectorAdapter(Context context, List<Competency> data) {
        mContext = context;
        mData = data;
    }

    public SubjectSelectorAdapter(Context context, List<Competency> data, Competency selectedCompetency, int flag) {
        mContext = context;
        mData = data;
        this.flag = flag;
        setSelectedCompetency(selectedCompetency);
    }

    public void setData(List<Competency> data) {
        mData = data;
    }

    @Override
    public int getCount() {
        return mData != null ? mData.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return mData != null ? mData.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.teaching_subject_list_item, null);
            if (flag !=1) {
                convertView.setOnClickListener(this);
            }
            holder = new Holder();
            holder.subjectTv = (TextView) convertView.findViewById(R.id.subject_name);
            holder.selectedStatus = (ImageView) convertView.findViewById(R.id.selected_status);
            convertView.setTag(holder);
        } else {
            holder = (Holder)convertView.getTag();
        }

        bindData(position, holder);
        return convertView;
    }

    private void bindData(int position, Holder holder) {
        Competency competency = mData.get(position);
        holder.position = position;
        CSubject subject = null;
        if (competency != null && (subject = competency.getSubject()) != null) {
            holder.subjectTv.setText(subject.getName());
        }

        holder.selectedStatus.setVisibility(competency.isChecked() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        Holder holder = (Holder) v.getTag();
        if (holder != null) {
            for (Competency competency : mData) {
                competency.setChecked(false);
            }
            Competency competency = mData.get(holder.position);
            competency.setChecked(true);

            notifyDataSetChanged();
        }
    }

    public Competency getSelectedCompetency() {
        if (mData != null) {
            for (Competency competency : mData) {
                if (competency.isChecked()) {
                    return competency;
                }
            }
        }

        return null;
    }

    public void setSelectedCompetency(Competency selectedCompetency) {
        if (mData == null | selectedCompetency == null) {
            return;
        }

        for (Competency competency : mData) {
            CSubject selectedSubject = null;
            if (selectedCompetency != null && (selectedSubject = selectedCompetency.getSubject()) != null) {
                CSubject subject = competency.getSubject();
                String id = selectedSubject.getId();
                if (id != null && id.equals(subject.getId())) {
                    competency.setChecked(true);
                    break;
                }
            }
        }
    }

    static class Holder {
        public TextView subjectTv;
        public ImageView selectedStatus;
        public int position;
    }
}
