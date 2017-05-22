package cn.xiaojs.xma.ui.personal;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.AbsSwipeAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshSwipeListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.SearchManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.CollectionPageData;
import cn.xiaojs.xma.model.account.OrgTeacher;
import cn.xiaojs.xma.model.search.AccountInfo;
import cn.xiaojs.xma.ui.search.SearchBusiness;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.util.StringUtil;

/**
 * Created by Paul Z on 2017/3/31.
 */

public class PersonHomeOrgTeacherAdapter extends AbsSwipeAdapter<OrgTeacher, PersonHomeOrgTeacherAdapter.Holder> {


    private CircleTransform circleTransform;

    private String accountId;

    public PersonHomeOrgTeacherAdapter(Context context, PullToRefreshSwipeListView listView,String accountId) {
        super(context, listView);
        circleTransform = new CircleTransform(context);
        this.accountId=accountId;
        setDesc(context.getString(R.string.no_teacher_tip));
    }

    @Override
    protected void onDataItemClick(int position, OrgTeacher bean) {
        if (bean != null) {
            Intent intent = new Intent(mContext, PersonHomeActivity.class);
            intent.putExtra(PersonalBusiness.KEY_PERSONAL_ACCOUNT, bean.id);
            mContext.startActivity(intent);
        }
    }

    @Override
    protected void setViewContent(Holder holder, final OrgTeacher bean, int position) {

        holder.tvName.setText(bean.name);
        holder.tvDescription.setText(TextUtils.isEmpty(bean.remarks)?"暂无简介":bean.remarks);
        holder.tvCount.setText(bean.stats.fans + " 课  |  " + bean.stats.followships +" 学生");
        Glide.with(mContext)
                .load(Account.getAvatar(bean.id, 300))
                .bitmapTransform(circleTransform)
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(holder.peopleHead);
    }

    @Override
    protected View createContentView(int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_organization_teacher, null);
        return view;
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        return holder;
    }

    @Override
    protected void doRequest() {
        AccountDataManager.getOrgTeachers(mContext, accountId, false, mPagination, new APIServiceCallback<CollectionPage<OrgTeacher>>() {
            @Override
            public void onSuccess(CollectionPage<OrgTeacher> object) {
                if (object != null && object.objectsOfPage != null) {
                    PersonHomeOrgTeacherAdapter.this.onSuccess(object.objectsOfPage);
                } else {
                    PersonHomeOrgTeacherAdapter.this.onSuccess(null);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                PersonHomeOrgTeacherAdapter.this.onFailure(errorCode, errorMessage);

            }
        });
    }


    class Holder extends BaseHolder {
        @BindView(R.id.people_head)
        ImageView peopleHead;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_count)
        TextView tvCount;
        @BindView(R.id.tv_description)
        TextView tvDescription;

        public Holder(View view) {
            super(view);
        }
    }


}
