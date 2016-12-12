package com.benyuan.xiaojs.ui.live;
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
 * Date:2016/12/9
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.benyuan.xiaojs.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LiveBrilliantAdapter extends RecyclerView.Adapter<LiveBrilliantAdapter.Holder> {

    private Context mContext;

    public LiveBrilliantAdapter(Context context){
        mContext = context;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_live_block_item, parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        holder.time.setText("12:00");
        holder.image.setImageResource(R.drawable.default_portrait);
        holder.title.setText("titletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitle");
        holder.stuNum.setText("111人学过");
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class Holder extends RecyclerView.ViewHolder {
        @BindView(R.id.live_block_time)
        TextView time;
        @BindView(R.id.live_block_image)
        ImageView image;
        @BindView(R.id.live_block_title)
        TextView title;
        @BindView(R.id.live_block_stu_num)
        TextView stuNum;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
