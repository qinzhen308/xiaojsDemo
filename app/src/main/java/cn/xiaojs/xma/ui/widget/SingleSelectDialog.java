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
 * Date:2017/2/21
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import cn.xiaojs.xma.R;

public class SingleSelectDialog {
    private String[] items;
    private Context mContext;
    private OnOkClickListener l;
    private int titleResId;
    private String title;
    private int selectPosition;

    public SingleSelectDialog(Context context) {
        mContext = context;
    }

    public void setItems(String[] s) {
        items = s;
    }

    public void setTitle(int title) {
        titleResId = title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSelectPosition(int i) {
        selectPosition = i;
    }

    public void show() {
        if (items != null && items.length > 0) {
            final CommonDialog dialog = new CommonDialog(mContext);
            if (titleResId > 0) {
                dialog.setTitle(titleResId);
            } else {
                dialog.setTitle(title);
            }

            View view = LayoutInflater.from(mContext).inflate(R.layout.layout_dlg_list, null);
            MaxableListView groupListView = (MaxableListView) view;

            if (items.length> 8) {
                groupListView.setListViewHeight(8*80);
            }

            SelectAdapter adapter = new SelectAdapter(mContext, R.layout.layout_single_select_item, R.id.title, items);
            groupListView.setAdapter(adapter);
            groupListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            groupListView.setItemChecked(selectPosition,true);
            dialog.setCustomView(groupListView);
            groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    selectPosition = position;
                }
            });
            dialog.setOnLeftClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    dialog.dismiss();
                }
            });

            dialog.setOnRightClickListener(new CommonDialog.OnClickListener() {
                @Override
                public void onClick() {
                    if (l != null) {
                        l.onOk(selectPosition);
                    }
                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }

    public void setOnOkClick(OnOkClickListener listener) {
        l = listener;
    }

    public interface OnOkClickListener {
        void onOk(int position);
    }

    private class SelectAdapter extends ArrayAdapter<String> {
        public SelectAdapter(Context context, int resource, int tid, String[] objects) {
            super(context, resource, tid, objects);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = super.getView(position, convertView, parent);
            TextView textView = (TextView) v.findViewById(R.id.title);

            textView.setText(getItem(position));
            return v;
        }
    }
}
