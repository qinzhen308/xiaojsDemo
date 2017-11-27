package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.classroom2.core.CTLConstant;
import cn.xiaojs.xma.ui.classroom2.core.ClassroomEngine;
import cn.xiaojs.xma.ui.classroom2.util.TimeUtil;
import cn.xiaojs.xma.ui.common.ImageViewActivity;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.util.BitmapUtils;

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


    public ReceivedViewHolder(Context context, View itemView, ChatAdapter adapter, boolean flag) {
        super(context, itemView, adapter, flag);
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

        nameView.setText(item.from.name);

        if (group) {

            nameView.setVisibility(View.VISIBLE);

            int colorRes;
            String markStr;

            CTLConstant.UserIdentity identity = ClassroomEngine.getEngine().getChatIdentity(item.from.accountId);
            if (identity == CTLConstant.UserIdentity.ADMINISTRATOR) {
                markStr = "管理者";
                colorRes = context.getResources().getColor(R.color.session_admin);
            } else if (identity == CTLConstant.UserIdentity.LEAD) {
                markStr = "主讲";
                colorRes = context.getResources().getColor(R.color.session_leader);
            } else if (identity == CTLConstant.UserIdentity.ADVISER) {
                markStr = "管理者";
                colorRes = context.getResources().getColor(R.color.session_admin);
            } else if (identity == CTLConstant.UserIdentity.TEACHER2) {
                markStr = "老师";
                colorRes = context.getResources().getColor(R.color.session_teacher);
            } else if (identity == CTLConstant.UserIdentity.ASSISTANT) {
                markStr = "助教";
                colorRes = context.getResources().getColor(R.color.session_assi);
            } else {
                markStr = "";
                colorRes = context.getResources().getColor(android.R.color.transparent);
            }

            tagsView.setText(markStr);
            tagsView.setBackgroundColor(colorRes);
            tagsView.setVisibility(View.VISIBLE);
        }else {
            nameView.setVisibility(View.INVISIBLE);
            tagsView.setVisibility(View.GONE);
        }




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


        contentTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatAdapter.showMenu(v, position, item, false);
            }
        });


        contentImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> imgs = new ArrayList<String>(1);
                if (!TextUtils.isEmpty(item.body.text)) {
                    //base64
                    Bitmap bitmap = BitmapUtils.base64ToBitmapWithPrefix(item.body.text);
                    String previewFile = BitmapUtils.saveSharePreviewToFile(bitmap);
                    imgs.add(previewFile);
                    ImageViewActivity.invoke(context, "", imgs, true);
                } else {

                    imgs.add(item.body.drawing.name);
                    ImageViewActivity.invoke(context, "", imgs, false);
                }


            }
        });


    }
}
