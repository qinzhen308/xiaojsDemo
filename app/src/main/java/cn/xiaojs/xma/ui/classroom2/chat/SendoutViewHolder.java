package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.data.XMSManager;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.model.socket.room.Talk;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;
import cn.xiaojs.xma.ui.common.ImageViewActivity;
import cn.xiaojs.xma.ui.view.ChatPopupMenu;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.util.BitmapUtils;

/**
 * Created by maxiaobao on 2017/9/28.
 */

public class SendoutViewHolder extends ChatViewHolder {


    @BindView(R.id.time_line)
    TextView timeLineView;
    @BindView(R.id.avator)
    ImageView avatorView;
    @BindView(R.id.name)
    TextView nameView;
    @BindView(R.id.content_text)
    TextView contentTextView;
    @BindView(R.id.content_img)
    ImageView contentImgView;

    @BindView(R.id.error_layout)
    LinearLayout errorLayout;
    @BindView(R.id.error_tip)
    TextView errorTipsView;
    @BindView(R.id.resend)
    Button resendView;


    public SendoutViewHolder(Context context, View itemView, ChatAdapter adapter) {
        super(context, itemView, adapter);
        this.context = context;
    }

    @Override
    protected void bindData(final int position, final TalkItem item) {

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

        //nameView.setText(item.from.name);

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

            } else {

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


        if (TextUtils.isEmpty(item.error)) {
            errorLayout.setVisibility(View.GONE);
        } else {
            errorLayout.setVisibility(View.VISIBLE);
        }

        contentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatAdapter.showMenu(v, position, item);
            }
        });

        resendView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 重新发送
            }
        });

        contentImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> imgs = new ArrayList<String>(1);
                if (!TextUtils.isEmpty(item.body.text)) {
                    //base64
                    Bitmap bitmap = BitmapUtils.base64ToBitmapWithPrefix(item.body.text);
                    String previewFile=BitmapUtils.saveSharePreviewToFile(bitmap);
                    imgs.add(previewFile);
                    ImageViewActivity.invoke(context, "",imgs, true);
                }else {

                    imgs.add(item.body.drawing.name);
                    ImageViewActivity.invoke(context, "",imgs, false);
                }


            }
        });

    }



}
