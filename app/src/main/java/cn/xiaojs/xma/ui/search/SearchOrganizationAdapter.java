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
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.RoundedImageView;

public class SearchOrganizationAdapter extends CanInScrollviewListView.Adapter {

    private int MAX_COUNT = 0;

    private List<AccountSearch> mBeans;
    private Context mContext;

    public SearchOrganizationAdapter(Context context, List<AccountSearch> beans) {
        mContext = context;
        mBeans = beans;
    }

    public SearchOrganizationAdapter(Context context, List<AccountSearch> beans, int max) {
        mContext = context;
        mBeans = beans;
        MAX_COUNT = max;
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
        Holder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_search_organization_item, null);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }

        holder.name.setText(mBeans.get(position)._source.basic.getName());
        Glide.with(mContext)
                .load(Account.getAvatar(mBeans.get(position)._id,300))
                .error(R.drawable.default_avatar)
                .into(holder.head);
        return convertView;
    }

    public void setData(List<AccountSearch> accounts){
        mBeans = accounts;
    }
    class Holder extends BaseHolder {
        @BindView(R.id.search_organization_head)
        RoundedImageView head;
        @BindView(R.id.search_organization_name)
        TextView name;
        @BindView(R.id.search_organization_level)
        TextView level;

        public Holder(View view) {
            super(view);
        }
    }
}
