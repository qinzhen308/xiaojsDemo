package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by maxiaobao on 2017/9/28.
 */

public class ReceivedViewHolder extends ChatViewHolder {

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
    @BindView(R.id.content_img)
    ImageView contentImgView;


    public ReceivedViewHolder(Context context, View itemView) {
        super(context, itemView);
        this.context = context;
    }

    @Override
    protected void bindData(TalkItem item) {

        String portraitUrl = Account.getAvatar(item.from != null ? item.from.accountId : null,
                avatorView.getMeasuredWidth());
        Glide.with(context)
                .load(portraitUrl)
                .transform(new CircleTransform(context))
                .placeholder(R.drawable.ic_defaultavatar)
                .error(R.drawable.ic_defaultavatar)
                .into(avatorView);


        if (item.showTime) {
            String timeStr = TimeUtil.getTimeShowString(item.time, false);
            timeLineView.setText(timeStr);
            timeLineView.setVisibility(View.VISIBLE);
        } else {
            timeLineView.setVisibility(View.GONE);
        }


        nameView.setText(item.from.name);

        if (item.body.contentType == Communications.ContentType.TEXT) {

            contentTextView.setText(item.body.text);
            contentTextView.setVisibility(View.VISIBLE);
            contentImgView.setVisibility(View.GONE);

        } else if (item.body.contentType == Communications.ContentType.STYLUS) {


            if (!TextUtils.isEmpty(item.body.text)) {

                byte[] imgData = ClassroomBusiness.base64ToByteData(item.body.text);
                Glide.with(context)
                        .load(imgData)
                        .into(getImgViewTarget(0, contentImgView));

            }else {

                String imgUrl = ClassroomBusiness.getSnapshot(item.body.drawing.name, MAX_SIZE);
                Glide.with(context)
                        .load(imgUrl)
                        .into(getImgViewTarget(0, contentImgView));
            }

            contentTextView.setVisibility(View.GONE);
            contentImgView.setVisibility(View.VISIBLE);

        } else {
            contentTextView.setVisibility(View.GONE);
            contentImgView.setVisibility(View.GONE);
        }


    }
}
