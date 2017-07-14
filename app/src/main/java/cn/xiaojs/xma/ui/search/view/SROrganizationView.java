package cn.xiaojs.xma.ui.search.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.search.SearchResultV2;
import cn.xiaojs.xma.model.social.Relation;
import cn.xiaojs.xma.ui.base.BaseBusiness;
import cn.xiaojs.xma.ui.lesson.xclass.view.IViewModel;
import cn.xiaojs.xma.ui.personal.PersonHomeActivity;
import cn.xiaojs.xma.ui.personal.PersonalBusiness;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.util.StringUtil;

/**
 * Created by Paul Z on 2017/5/23.
 */

public class SROrganizationView extends RelativeLayout implements IViewModel<SearchResultV2> {


    SearchResultV2 mData;
    @BindView(R.id.iv_avatar)
    ImageView ivAvatar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_label)
    TextView tvLabel;
    @BindView(R.id.tv_tel)
    TextView tvTel;
    @BindView(R.id.tv_fans)
    TextView tvFans;
    @BindView(R.id.btn_follow)
    TextView btnFollow;

    private CircleTransform circleTransform;

    @BindColor(R.color.orange_point)
    int heightlightColor;

    @BindColor(R.color.common_text)
    int c_common_text;
    @BindColor(R.color.main_orange)
    int c_main_orange;

    public SROrganizationView(Context context) {
        super(context);
        init();
    }

    public SROrganizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_search_result_organization, this);
        ButterKnife.bind(this);
        circleTransform = new CircleTransform(getContext());
    }


    @Override
    public void bindData(int position, SearchResultV2 data) {
        mData = data;
        setFollowState(btnFollow,mData.isFollowed);
        String tag=((Activity)getContext()).getIntent().getStringExtra("extra_search_keywords");

        Glide.with(getContext())
                .load(Account.getAvatar(mData.id, 300))
                .bitmapTransform(circleTransform)
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(ivAvatar);

        if(mData.basic!=null){
            tvTitle.setText(StringUtil.setHighlightText2(mData.basic.getName(),mData._name,heightlightColor));
        }else {
            tvTitle.setText("");
        }
        String phone=mData.mobile.substring(0,4)+"****"+mData.mobile.substring(mData.mobile.length()-3,mData.mobile.length());
//        tvTel.setText(StringUtil.setHighlightText(phone,tag,heightlightColor));
        tvTel.setText(phone);
        tvFans.setText("粉丝 "+(mData.stats==null?0:mData.stats.fans));

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PersonHomeActivity.class);
                intent.putExtra(PersonalBusiness.KEY_PERSONAL_ACCOUNT, mData.id);
                getContext().startActivity(intent);
            }
        });
    }


    private void setFollowState(TextView v, boolean followed) {
        v.setTextColor(getResources().getColor(R.color.white));
        if (!followed) {
            v.setText("关注");
            v.setTextColor(c_main_orange);
            v.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_follow_plus, 0, 0, 0);
            v.setBackgroundResource(R.drawable.orange_stoke_bg);
            v.setEnabled(true);
        } else {
            v.setText("已关注");
            v.setTextColor(c_common_text);
            v.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            v.setBackgroundResource(R.drawable.gray_stoke_bg);
            v.setEnabled(false);
        }
    }

    @OnClick(R.id.btn_follow)
    public void onViewClicked() {
        chooseGroup();
    }

    private void chooseGroup() {

        BaseBusiness.showFollowDialog(getContext(), new BaseBusiness.OnFollowListener() {
            @Override
            public void onFollow(long group) {
                if (group > 0) {
                    toFollow(group);
                }
            }
        });
    }

    private void toFollow(long group) {

        SocialManager.followContact(getContext(), mData.id,mData.basic.getName(), group, new APIServiceCallback<Relation>() {
            @Override
            public void onSuccess(Relation object) {
                Toast.makeText(getContext(), R.string.followed, Toast.LENGTH_SHORT).show();
                setFollowState(btnFollow,true);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
