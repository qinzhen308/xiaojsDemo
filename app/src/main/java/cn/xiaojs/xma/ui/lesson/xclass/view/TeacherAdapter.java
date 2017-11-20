package cn.xiaojs.xma.ui.lesson.xclass.view;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.account.Account;
import cn.xiaojs.xma.ui.base.AbsListAdapter;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by Paul Z on 2017/11/20.
 */

public class TeacherAdapter extends AbsListAdapter<Account, AbsListAdapter.ViewHolder> {

    private CircleTransform circleTransform;


    public TeacherAdapter(Activity activity) {
        super(activity);
        circleTransform = new CircleTransform(activity);

    }

    @Override
    public void onBindViewHolder(int position, ViewHolder holder) {


        Account bean=(Account) getItem(position);
        Glide.with(mContext)
                .load(cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(bean.getId(), 300))
                .bitmapTransform(circleTransform)
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(((AccountHolder) holder).ivAvatar);
        ((AccountHolder) holder).tvName.setText(bean.getBasic()==null|| TextUtils.isEmpty(bean.getBasic().getName())?bean.name:bean.getBasic().getName());

    }

    @Override
    public AccountHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        AccountHolder holder = new AccountHolder(LayoutInflater.from(mContext).inflate(R.layout.item_teacher_avatar_name, null));
        return holder;
    }


    public static class AccountHolder extends ViewHolder {
        @BindView(R.id.iv_avatar)
        ImageView ivAvatar;
        @BindView(R.id.tv_name)
        TextView tvName;
        public AccountHolder(View v) {
            super(v);
        }
    }
}
