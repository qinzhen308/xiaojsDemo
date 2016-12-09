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
 * Date:2016/12/8
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.benyuan.xiaojs.R;
import com.benyuan.xiaojs.common.pulltorefresh.AbsGridViewAdapter;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.benyuan.xiaojs.common.pulltorefresh.core.PullToRefreshGridView;

import java.util.ArrayList;
import java.util.List;

public class LiveAdapter extends AbsGridViewAdapter<LiveBean,LiveAdapter.Holder> {


    public LiveAdapter(Context context, PullToRefreshGridView gridview) {
        super(context, gridview);
    }

    public LiveAdapter(Context context, PullToRefreshGridView gridview, boolean autoLoad) {
        super(context, gridview, autoLoad);
    }

    @Override
    protected void setViewContent(Holder holder, LiveBean bean, int position) {

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
        public Holder(View view) {
            super(view);
        }
    }
}
