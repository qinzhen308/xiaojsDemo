package cn.xiaojs.xma.ui.widget.function;
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
 * Date:2016/10/31
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.ui.widget.banner.PointIndicateView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FunctionArea extends FrameLayout {

    @BindView(R.id.home_function_pager)
    ViewPager mPager;
    @BindView(R.id.home_function_point)
    PointIndicateView mPoint;

    private final int PAGE_SIZE = 8;

    private List<FunctionItemBean> items;

    public FunctionArea(Context context) {
        super(context);
        init();
    }

    public FunctionArea(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FunctionArea(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        LayoutInflater.from(getContext()).inflate(R.layout.layout_home_function,this,true);
        ButterKnife.bind(this);
    }

    public void setItems(List<FunctionItemBean> items){
        this.items = items;
        List<View> views = new ArrayList<>();
        for (int i = 1;i <= getPageNum();i++){
            View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_function_area,null);
            GridView grid = (GridView) v.findViewById(R.id.home_function_grid);
            grid.setAdapter(new FunctionItemAdapter(getContext(),getPage(i)));
            views.add(v);
        }
        PagerAdapter adapter = new FunctionAdapter(views);

        mPager.setAdapter(adapter);
        mPoint.setViewPager(mPager,getPageNum(),0,false,null);
    }

    private int getPageNum(){
        if (items.size() % PAGE_SIZE == 0){
            return items.size() / PAGE_SIZE;
        }
        return items.size() / PAGE_SIZE + 1;
    }

    //获取每页数据，从1开始
    private List<FunctionItemBean> getPage(int page){
        if (this.items == null || page > getPageNum())
            return null;
        if (page == getPageNum())
            return items.subList((page - 1) * PAGE_SIZE,items.size());
        return items.subList((page - 1) * PAGE_SIZE,page * PAGE_SIZE);
    }

}
