package com.benyuan.xiaojs.ui.widget;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.benyuan.xiaojs.R;

public class ListBottomDialog extends BottomSheet {

    private ListView mList;
    private TextView mCancel;
    private String[] mItems;
    private OnItemClick listener;

    public ListBottomDialog(Context context) {
        super(context);
    }

    public ListBottomDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ListBottomDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    private void init() {
        setCanceledOnTouchOutside(true);
        Window dialogWindow = getWindow();
        setContentView(R.layout.dialog_base_bottom);
        dialogWindow.setWindowAnimations(R.style.BottomSheetAnim);
        WindowManager.LayoutParams params = dialogWindow.getAttributes();
        params.dimAmount = 0.5f;
        dialogWindow.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWindow.setAttributes(params);
        dialogWindow.setLayout(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogWindow.setGravity(Gravity.BOTTOM);

        mList = (ListView) dialogWindow.findViewById(R.id.base_bottom_dialog_list);
        mCancel = (TextView) dialogWindow.findViewById(R.id.base_bottom_dialog_cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
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
            setTitleVisibility(View.GONE);
            View v = LayoutInflater.from(getContext()).inflate(R.layout.dialog_base_bottom, null);
            mList = (ListView) v.findViewById(R.id.base_bottom_dialog_list);
            mCancel = (TextView) v.findViewById(R.id.base_bottom_dialog_cancel);
            mCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
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
                    if (listener != null){
                        listener.onItemClick(position);
                    }
                    dismiss();
                }
            });
            return item;
        }
    }

    public interface OnItemClick{
        void onItemClick(int position);
    }
}
