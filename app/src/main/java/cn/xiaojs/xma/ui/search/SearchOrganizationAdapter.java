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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.search.AccountInfo;
import cn.xiaojs.xma.model.search.AccountSearch;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.widget.CanInScrollviewListView;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.StringUtil;

public class SearchOrganizationAdapter extends CanInScrollviewListView.Adapter {

    private int MAX_COUNT = 0;

    private List<AccountInfo> mBeans;
    private Context mContext;
    private CircleTransform circleTransform;

    public SearchOrganizationAdapter(Context context, List<AccountInfo> beans) {
        mContext = context;
        mBeans = beans;
        circleTransform = new CircleTransform(context);
    }

    public SearchOrganizationAdapter(Context context, List<AccountInfo> beans, int max) {
        mContext = context;
        mBeans = beans;
        circleTransform = new CircleTransform(context);
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
        SearchOrganizationAdapter.Holder holder = null;
        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_search_people_item, null);
            holder = new SearchOrganizationAdapter.Holder(convertView);
            convertView.setTag(holder);
        }else {
            holder = (SearchOrganizationAdapter.Holder) convertView.getTag();
        }

        final AccountInfo accountInfo = mBeans.get(position);

        holder.name.setText(accountInfo.basic.getName());
        holder.tag.setText(StringUtil.protectCardNo(accountInfo.phone.subsNum));

        final String aid = accountInfo.id;
        if (!TextUtils.isEmpty(aid) && DataManager.existInContacts(mContext,aid)) {
            holder.relationship.setText("已关注");
            holder.relationship.setTextColor(mContext.getResources().getColor(R.color.font_light_gray));
            holder.relationship.setCompoundDrawablePadding(8);
            holder.relationship.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
            holder.relationship.setBackgroundResource(R.drawable.bg_follow_rect);
            holder.relationship.setEnabled(false);
        }else {
            holder.relationship.setText("关注");
            holder.relationship.setTextColor(mContext.getResources().getColor(R.color.font_orange));
            holder.relationship.setCompoundDrawablePadding(8);
            holder.relationship.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_plus,0,0,0);
            holder.relationship.setBackgroundResource(R.drawable.bg_unfollow_rect);
            holder.relationship.setEnabled(true);
        }

        holder.relationship.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                chooseGroup(accountInfo);
            }
        });


        Glide.with(mContext)
                .load(Account.getAvatar(accountInfo.id,300))
                .bitmapTransform(circleTransform)
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(holder.head);
        return convertView;
    }

    public void setData(List<AccountInfo> accounts){
        mBeans = accounts;
    }

    private void chooseGroup(final AccountInfo bean) {

        BaseBusiness.showFollowDialog(mContext, new BaseBusiness.OnFollowListener() {
            @Override
            public void onFollow(long group) {
                if (group > 0) {
                    toFollow(bean, group);
                }
            }
        });

    }

    private void toFollow(AccountInfo bean, long group) {

        Contact.MetIn metIn = new Contact.MetIn();
        metIn.id = bean.id;
        metIn.subtype = Collaboration.SubType.ORGANIZATION;

        SocialManager.followContact(mContext,
                bean.id,bean.basic.getName(), group, metIn, new APIServiceCallback<Relation>() {
            @Override
            public void onSuccess(Relation object) {
                Toast.makeText(mContext, R.string.followed, Toast.LENGTH_SHORT).show();
                //FIXME 没更新
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(mContext, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    class Holder extends BaseHolder {
        @BindView(R.id.search_people_head)
        ImageView head;
        @BindView(R.id.search_people_item_name)
        TextView name;
        //        @BindView(R.id.search_people_star)
//        EvaluationStar star;
        @BindView(R.id.search_people_tag)
        TextView tag;
        //        @BindView(R.id.search_people_desc)
//        TextView desc;
        @BindView(R.id.search_people_relationship)
        Button relationship;


        public Holder(View view) {
            super(view);
        }
    }
}
