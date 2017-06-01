package cn.xiaojs.xma.ui.lesson.xclass.view;
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
 * Date:2016/11/2
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.base.BaseFullWindow;
import cn.xiaojs.xma.ui.widget.AdapterGirdView;

public class MyClassFilterDialog extends BaseFullWindow {

    @BindView(R.id.course_filter_time_grid)
    AdapterGirdView mTime;
    @BindView(R.id.course_filter_state_grid)
    AdapterGirdView mState;
    @BindView(R.id.course_filter_source_grid)
    AdapterGirdView mSource;
    @BindView(R.id.course_filter_ok)
    Button mOk;

    private boolean isTeacher;

    private int group1Position;
    private int group2Position;
    private int mSourcePosition;
    private OnOkListener mListener;

    public MyClassFilterDialog(Context context, boolean isTeacher) {
        super(context);
        this.isTeacher = isTeacher;
    }

    @Override
    protected View setCustomerContentView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_my_class_filter,null);
        ButterKnife.bind(this,view);
        return view;
    }

    public void setTimeSelection(int position){
        group1Position = position;
    }

    public void setStateSelection(int position){
        group2Position = position;
    }
    public void setSourcePosition(int position){
        mSourcePosition = position;
    }

    public void setOnOkListener(OnOkListener l){
        mListener = l;
    }

    private class CommonAdapter extends BaseAdapter{

        private String[] items;
        private int mSelection;
        public CommonAdapter(String[] items,int index){
            this.items = items;
            mSelection = index;
        }

        @Override
        public int getCount() {
            return items == null ? 0 : items.length;
        }

        @Override
        public Object getItem(int i) {
            return items == null ? null : items[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            TextView t = (TextView) LayoutInflater.from(mContext).inflate(R.layout.layout_course_filter_item,null);
            t.setText(items[i]);
            if (i == mSelection){
                t.setTextColor(mContext.getResources().getColor(R.color.main_orange));
                t.setBackgroundResource(R.drawable.common_light_stroke);
            }
            return t;
        }
    }

    String[] group1;
    public MyClassFilterDialog setGroup1(String[] group1){
        this.group1=group1;
        return this;
    }

    String[] group2;
    public MyClassFilterDialog setGroup2(String[] group1){
        this.group2=group1;
        return this;
    }


    @Override
    public void showAsDropDown(View anchor) {
        int stateId = R.array.course_state_filter_stu;
        if (isTeacher){
            stateId = R.array.course_state_filter_teacher;
            String[] sources = mContext.getResources().getStringArray(R.array.course_source_filter);
            mSource.setAdapter(new CommonAdapter(sources,mSourcePosition));
            mSource.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    changeState(parent, position);
                    mSourcePosition = position;
                }
            });
        }
        mTime.setAdapter(new CommonAdapter(group1, group1Position));
        mState.setAdapter(new CommonAdapter(group2, group2Position));
        mTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                changeState(adapterView,i);
                group1Position = i;
            }
        });
        mState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                changeState(adapterView,i);
                group2Position = i;
            }
        });
        mOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null){
                    mListener.onOk(group1Position, group2Position);
                }
                dismiss();
            }
        });
        super.showAsDropDown(anchor);
    }

    private void changeState(AdapterView<?> adapterView,int position){
        for (int i = 0 ;i < adapterView.getChildCount();i++){
            if (i == position){
                ((TextView)adapterView.getChildAt(i)).setTextColor(mContext.getResources().getColor(R.color.main_orange));
                adapterView.getChildAt(i).setBackgroundResource(R.drawable.common_light_stroke);
                continue;
            }
            ((TextView)adapterView.getChildAt(i)).setTextColor(mContext.getResources().getColor(R.color.common_text));
            adapterView.getChildAt(i).setBackgroundResource(R.drawable.common_grey_stroke);
        }
    }

    public interface OnOkListener{
        void onOk(int group1Position, int group2Position);
    }
}
