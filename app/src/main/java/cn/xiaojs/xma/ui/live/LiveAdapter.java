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
 * Date:2016/12/8
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsGridViewAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshGridView;
import cn.xiaojs.xma.ui.widget.EvaluationStar;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class LiveAdapter extends AbsGridViewAdapter<LiveBean,LiveAdapter.Holder> {

    private final float SCALE = 2f / 3;
    private int margin;


    public LiveAdapter(Context context, PullToRefreshGridView gridview) {
        super(context, gridview);
        init();
    }

    public LiveAdapter(Context context, PullToRefreshGridView gridview, boolean autoLoad) {
        super(context, gridview, autoLoad);
        init();
    }

    private void init(){
        margin = mContext.getResources().getDimensionPixelSize(R.dimen.px30);
    }

    @Override
    protected void setViewContent(Holder holder, LiveBean bean, int position) {
        ViewGroup.LayoutParams lp = holder.imageWrapper.getLayoutParams();
        lp.height = getImageHeight();
        holder.imageWrapper.setLayoutParams(lp);
        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) holder.item.getLayoutParams();
        if (position % 2 == 0){
            mlp.leftMargin = margin;
            mlp.rightMargin = margin / 2;
        }else {
            mlp.leftMargin = margin / 2;
            mlp.rightMargin = margin;
        }

        holder.item.setLayoutParams(mlp);


//        ViewGroup.LayoutParams itemLp = holder.item.getLayoutParams();
//        itemLp.width = getItemWidth();
//        holder.item.setLayoutParams(itemLp);

        holder.portrait.setImageResource(R.drawable.default_portrait);
        holder.teacher.setText("冰冰");

    }

    private int getImageHeight(){
        int height = (int) ( getItemWidth() * SCALE);
        return height;
    }

    private int getItemWidth(){
        int width = (DeviceUtil.getScreenWidth(mContext) - 3 * margin) / 2;
        return width;
    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_home_live_item,null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        return new Holder(view);
    }

    @Override
    protected void doRequest() {
        List<LiveBean> beens = new ArrayList<>();
        beens.add(new LiveBean());
        beens.add(new LiveBean());
        beens.add(new LiveBean());
        beens.add(new LiveBean());
        beens.add(new LiveBean());
        beens.add(new LiveBean());
        beens.add(new LiveBean());
        beens.add(new LiveBean());

        onSuccess(beens);
    }

    class Holder extends BaseHolder {

        @BindView(R.id.home_live_image)
        ImageView image;
        @BindView(R.id.home_live_title)
        TextView title;
        @BindView(R.id.home_live_item_num)
        TextView enroll;
        @BindView(R.id.home_live_item_price)
        TextView price;
        @BindView(R.id.home_live_item_portrait)
        RoundedImageView portrait;
        @BindView(R.id.home_live_item_teacher)
        TextView teacher;
        @BindView(R.id.home_live_item_star)
        EvaluationStar star;
        @BindView(R.id.home_live_item_wrapper)
        RelativeLayout imageWrapper;
        @BindView(R.id.home_live_item_mark)
        ImageView mark;
        @BindView(R.id.home_live_item_level)
        TextView level;
        @BindView(R.id.home_live_item)
        View item;
        public Holder(View view) {
            super(view);
        }
    }
}
