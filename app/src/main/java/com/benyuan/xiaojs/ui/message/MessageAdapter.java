package com.benyuan.xiaojs.ui.message;
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
 * Date:2016/11/16
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.View;

import com.benyuan.xiaojs.common.pulltorefresh.AbsSwipeAdapter;
import com.benyuan.xiaojs.common.pulltorefresh.BaseHolder;
import com.handmark.pulltorefresh.AutoPullToRefreshListView;

public class MessageAdapter extends AbsSwipeAdapter<MessageBean,MessageAdapter.Holder> {

    public MessageAdapter(Context context, AutoPullToRefreshListView listView) {
        super(context, listView);

    }

    @Override
    protected void initParam() {
    }

    @Override
    protected void setViewContent(Holder holder, MessageBean bean, int position) {

    }

    @Override
    protected View createContentView(int position) {
        return null;
    }

    @Override
    protected Holder initHolder(View view) {
        return null;
    }

    @Override
    protected void doRequest() {
        onSuccess(null);
    }

    @Override
    protected boolean showEmptyView() {
        return false;
    }

    class Holder extends BaseHolder{

        public Holder(View view) {
            super(view);
        }
    }
}
