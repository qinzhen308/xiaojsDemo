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
 * Date:2016/11/8
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

public class LiveAdapter extends BaseAdapter {

    private Context mContext;

    public LiveAdapter(Context context){
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
            view = LayoutInflater.from(mContext).inflate(R.layout.layout_live_block_item,null);
            holder = new Holder(view);
            view.setTag(holder);
        }else {
            holder = (Holder) view.getTag();
        }
        holder.time.setText("12:00");
        holder.image.setImageResource(R.drawable.default_portrait);
        holder.title.setText("title");
        holder.stuNum.setText("111人学过");
        if (i == 0){
            holder.leftBlank.setVisibility(View.GONE);
        }else {
            holder.leftBlank.setVisibility(View.GONE);
        }

        if (i == 9){
            holder.rightBlank.setVisibility(View.GONE);
        }else {
            holder.rightBlank.setVisibility(View.GONE);
        }
        return view;
    }

    class Holder extends BaseHolder{

        @BindView(R.id.live_block_left_blank)
        View leftBlank;
        @BindView(R.id.live_block_right_blank)
        View rightBlank;
        @BindView(R.id.live_block_time)
        TextView time;
        @BindView(R.id.live_block_image)
        ImageView image;
        @BindView(R.id.live_block_title)
        TextView title;
        @BindView(R.id.live_block_stu_num)
        TextView stuNum;

        public Holder(View view) {
            super(view);
        }
    }
}
