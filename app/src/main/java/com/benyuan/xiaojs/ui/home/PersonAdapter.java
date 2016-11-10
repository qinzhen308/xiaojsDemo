package com.benyuan.xiaojs.ui.home;
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
 * Date:2016/11/9
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
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;

import butterknife.BindView;

public class PersonAdapter extends BaseAdapter {

    private Context mContext;
    public PersonAdapter(Context context){
        mContext = context;
    }
    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder = null;
        if (view == null){
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_person_block_item,null);
            holder = new Holder(view);
            view.setTag(holder);
        }else {
            holder = (Holder) view.getTag();
        }
        holder.image.setImageResource(R.drawable.default_portrait);
        holder.name.setText("Davie");
        holder.desc.setText("高级讲师");
        return view;
    }

    class Holder extends BaseHolder{
        @BindView(R.id.person_block_image)
        ImageView image;
        @BindView(R.id.person_block_name)
        TextView name;
        @BindView(R.id.person_block_desc)
        TextView desc;
        public Holder(View view) {
            super(view);
        }
    }

}
