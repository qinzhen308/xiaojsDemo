package com.benyuan.xiaojs.ui.classroom;
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
 * Date:2016/11/29
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.ui.widget.RoundedImageView;

public class ContactBookAdapter extends BaseAdapter {
    private Context mContext;

    public ContactBookAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        //TODO test count
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = createContentView();
        }
        holder = (Holder)convertView.getTag();

        bindData(holder);
        return convertView;
    }

    private View createContentView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_contact_item, null);
        Holder holder = new Holder();
        holder.checkbox = (ImageView) v.findViewById(R.id.checkbox);
        holder.portrait = (RoundedImageView) v.findViewById(R.id.portrait);
        holder.name = (TextView) v.findViewById(R.id.name);
        holder.label = (TextView) v.findViewById(R.id.label);
        holder.msg = (RoundedImageView) v.findViewById(R.id.portrait);
        holder.video = (RoundedImageView) v.findViewById(R.id.portrait);
        v.setTag(holder);
        return v;
    }

    private void bindData(Holder holder) {
        holder.portrait.setImageResource(R.drawable.default_portrait);
    }


    private class Holder {
        ImageView checkbox;
        RoundedImageView portrait;
        TextView name;
        TextView label;
        ImageView msg;
        ImageView video;
    }
}
