package cn.xiaojs.xma.ui.live;
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
import android.widget.LinearLayout;

import cn.xiaojs.xma.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.xiaojs.xma.ui.view.AnimationView;

public class LiveBrilliantAdapter extends RecyclerView.Adapter<LiveBrilliantAdapter.Holder> {

    private Context mContext;
    private final int SPACE;

    public LiveBrilliantAdapter(Context context){
        mContext = context;
        SPACE = mContext.getResources().getDimensionPixelSize(R.dimen.px30);
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_live_brilliant_item, parent,false);
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.wrapper.getLayoutParams();
        if (position == 0) {
            mlp.leftMargin = SPACE;
            mlp.rightMargin = SPACE / 2;
        } else if (position == getItemCount() - 1) {
            mlp.leftMargin = SPACE / 2;
            mlp.rightMargin = SPACE;
        } else {
            mlp.leftMargin = SPACE / 2;
            mlp.rightMargin = SPACE / 2;
        }
        holder.wrapper.setLayoutParams(mlp);
        holder.animation.start();
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    class Holder extends RecyclerView.ViewHolder {

        @BindView(R.id.live_brilliant_wrapper)
        LinearLayout wrapper;
        @BindView(R.id.animation)
        AnimationView animation;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
