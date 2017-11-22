package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.common.xf_foundation.schemas.Social;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.SocialManager;
import cn.xiaojs.xma.data.provider.DataProvider;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by maxiaobao on 2017/9/28.
 */

public class FollowedViewHolder extends ChatViewHolder {

    @BindView(R.id.time_line)
    TextView timeLineView;
    @BindView(R.id.avator)
    ImageView avatorView;
    @BindView(R.id.name)
    TextView nameView;
    @BindView(R.id.tags)
    TextView tagsView;
    @BindView(R.id.content_text)
    TextView contentTextView;
    @BindView(R.id.line)
    View lineView;
    @BindView(R.id.btn_follow)
    Button followBtn;
    @BindView(R.id.btn_see)
    Button seeBtn;


    public FollowedViewHolder(Context context, View itemView, ChatAdapter adapter) {
        super(context, itemView, adapter);
        this.context = context;
    }

    @Override
    protected void bindData(final TalkItem item) {

        String cid = item.from != null ? item.from.accountId : "";

        if (AccountDataManager.isXiaojsAccount(cid)) {
            avatorView.setImageResource(R.drawable.ic_im_xiaojs);
        }else {
            String portraitUrl = Account.getAvatar(cid, avatorView.getMeasuredWidth());
            Glide.with(context)
                    .load(portraitUrl)
                    .transform(new CircleTransform(context))
                    .placeholder(R.drawable.ic_defaultavatar)
                    .error(R.drawable.ic_defaultavatar)
                    .into(avatorView);
        }

        if (item.showTime) {
            String timeStr = TimeUtil.getTimeShowString(item.time, false);
            timeLineView.setText(timeStr);
            timeLineView.setVisibility(View.VISIBLE);
        } else {
            timeLineView.setVisibility(View.GONE);
        }


        nameView.setText(item.from.name);
        contentTextView.setText(item.body.text);

        if (item.extra !=null
                && (item.extra.followType == Social.FllowType.NA
                || item.extra.followType == Social.FllowType.FAN_ONLY)) {
            lineView.setVisibility(View.VISIBLE);
            followBtn.setVisibility(View.VISIBLE);
        }else {
            lineView.setVisibility(View.GONE);
            followBtn.setVisibility(View.GONE);
        }

        followBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatAdapter.toFollow(item);
            }
        });

    }
}
