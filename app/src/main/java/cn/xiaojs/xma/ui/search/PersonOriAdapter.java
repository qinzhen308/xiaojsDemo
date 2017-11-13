package cn.xiaojs.xma.ui.search;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Collaboration;
import cn.xiaojs.xma.data.DataManager;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPageData;
import cn.xiaojs.xma.model.search.AccountInfo;
import cn.xiaojs.xma.model.social.Contact;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.EvaluationStar;
import cn.xiaojs.xma.util.StringUtil;

/**
 * Created by maxiaobao on 2017/3/31.
 */

public class PersonOriAdapter extends SearchAdapter<AccountInfo, PersonOriAdapter.Holder> {

    private CircleTransform circleTransform;

    public PersonOriAdapter(Context context, PullToRefreshSwipeListView listView) {
        super(context, listView);
        circleTransform = new CircleTransform(context);
    }

    @Override
    protected void onDataItemClick(int position, AccountInfo bean) {
        if (bean != null) {
            SearchBusiness.goPersonal(mContext, bean);
        }
    }

    @Override
    protected void setViewContent(Holder holder, final AccountInfo bean, int position) {

        holder.name.setText(bean.basic.getName());
        holder.tag.setText(StringUtil.protectCardNo(bean.phone.subsNum));

        final String aid = bean.id;
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
                chooseGroup(bean);
            }
        });

        Glide.with(mContext)
                .load(Account.getAvatar(bean.id, 300))
                .bitmapTransform(circleTransform)
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(holder.head);
    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_search_people_item, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {

        SearchManager.searchAccounts(mContext, keyWord, mPagination,
                Account.TypeName.PERSION, new APIServiceCallback<CollectionPageData<AccountInfo>>() {
                    @Override
                    public void onSuccess(CollectionPageData<AccountInfo> object) {
                        if (object != null && object.accounts != null) {
                            PersonOriAdapter.this.onSuccess(object.accounts);
                        } else {
                            PersonOriAdapter.this.onSuccess(null);
                        }
                    }

                    @Override
                    public void onFailure(String errorCode, String errorMessage) {
                        PersonOriAdapter.this.onFailure(errorCode, errorMessage);
                    }
                });
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
        metIn.subtype = Collaboration.SubType.PERSON;

        SocialManager.followContact(mContext,
                bean.id,bean.basic.getName(), group, metIn, new APIServiceCallback<Relation>() {
            @Override
            public void onSuccess(Relation object) {
                Toast.makeText(mContext, R.string.followed, Toast.LENGTH_SHORT).show();
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
