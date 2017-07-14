package cn.xiaojs.xma.ui.widget;
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
 * Date:2016/11/14
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.support.annotation.StringRes;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import cn.xiaojs.xma.R;

public class ListBottomDialog extends BottomSheet {

    private ListView mList;
    private TextView mCancel;
    private TextView mTitle;
    private String[] mItems;
    private OnItemClick listener;

    private String title;

    public ListBottomDialog(Context context) {
        super(context);
        init();
    }

    public ListBottomDialog(Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected ListBottomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        setTitleVisibility(View.GONE);
    }


    public void setItems(String[] its) {
        mItems = its;
    }

    public void setOnItemClick(OnItemClick l){
        listener = l;
    }

    @Override
    public void show() {
        if (mItems != null) {
            View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_base_bottom, null);
            mList = (ListView) v.findViewById(R.id.base_bottom_dialog_list);
            mTitle = (TextView) v.findViewById(R.id.dialog_title);
            if(TextUtils.isEmpty(title)){
                mTitle.setVisibility(View.GONE);
            }else {
                mTitle.setVisibility(View.VISIBLE);
                mTitle.setText(title);
            }
            mCancel = (TextView) v.findViewById(R.id.base_bottom_dialog_cancel);
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if(onCloseListener!=null){
                        onCloseListener.onClick(mCancel);
                    }
                }
            });
            mList.setAdapter(new ItemAdapter());
            setContent(v);
        }
        super.show();
    }

    private class ItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mItems.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            TextView item = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.layout_dialog_base_bottom_item, null);
            item.setText(mItems[position]);
            item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (listener != null){
                        listener.onItemClick(position);
                    }
                }
            });
            return item;
        }
    }

    public void setTopTitle(String title){
        this.title=title;
    }

    public void setTopTitle(@StringRes int textRes){
        this.title=getContext().getResources().getString(textRes);
    }

    public interface OnItemClick{
        void onItemClick(int position);
    }

    View.OnClickListener onCloseListener;
    public void setOnCloseListener(View.OnClickListener onCloseListener){
        this.onCloseListener=onCloseListener;
    }
}
