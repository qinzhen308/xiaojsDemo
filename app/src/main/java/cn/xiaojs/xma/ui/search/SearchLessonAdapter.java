package cn.xiaojs.xma.ui.search;
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
 * Date:2016/12/21
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;

public class SearchLessonAdapter extends CanInScrollviewListView.Adapter {

    private int MAX_COUNT = 0;

    private List<AccountSearch> mBeans;
    private Context mContext;

    public SearchLessonAdapter(Context context, List<AccountSearch> beans,int max) {
        mContext = context;
        mBeans = beans;
        MAX_COUNT = max;
    }

    public SearchLessonAdapter(Context context, List<AccountSearch> beans) {
        mContext = context;
        mBeans = beans;
    }

    @Override
    public int getCount() {
        if (MAX_COUNT > 0){
            int count = 0;
            if (mBeans != null) {
                if (mBeans.size() > MAX_COUNT) {//最多显示3个
                    count = MAX_COUNT;
                } else {
                    count = mBeans.size();
                }
            }
            return count;
        }
        return mBeans == null ? 0 : mBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_search_lesson_item, null);
        Holder holder = new Holder(convertView);
        return convertView;
    }

    public void setData(List<AccountSearch> accounts){
        mBeans = accounts;
    }

    class Holder extends BaseHolder{
        public Holder(View view) {
            super(view);
        }
    }
}
