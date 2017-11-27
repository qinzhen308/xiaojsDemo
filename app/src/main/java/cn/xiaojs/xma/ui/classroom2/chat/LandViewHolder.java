package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.AccountDataManager;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.widget.CircleTransform;

/**
 * Created by maxiaobao on 2017/9/28.
 */

public class LandViewHolder extends ChatViewHolder {


    @BindView(R.id.name)
    TextView nameView;
    @BindView(R.id.content_img)
    ImageView contentImgView;
    @BindView(R.id.content_text)
    TextView contentTextView;



    public LandViewHolder(Context context, View itemView, ChatAdapter adapter, boolean group) {
        super(context, itemView, adapter, group);
        this.context = context;
    }

    @Override
    protected void bindData(int position, TalkItem item) {


        if (!TextUtils.isEmpty(item.tips)) {

            contentImgView.setVisibility(View.GONE);

            //contentTextView.setText(item.tips);

            contentTextView.setVisibility(View.GONE);
            nameView.setText(item.tips);
            nameView.setVisibility(View.VISIBLE);
        }else {
            String name = (TextUtils.isEmpty(item.from.name)
                    ||AccountDataManager.getAccountID(context).equals(item.from)) ? "我" : item.from.name;

            StringBuilder sb = new StringBuilder(name).append(" ");

            if (item.body.contentType == Communications.ContentType.TEXT) {

                sb.append(item.body.text);

                // contentTextView.setText(item.body.text);
                // contentTextView.setVisibility(View.VISIBLE);
                contentImgView.setVisibility(View.GONE);

            }else if (item.body.contentType == Communications.ContentType.STYLUS) {

                sb.append("");

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

                //contentTextView.setVisibility(View.GONE);
                contentImgView.setVisibility(View.VISIBLE);



            }else {
                //contentTextView.setVisibility(View.GONE);
                contentImgView.setVisibility(View.GONE);
            }


            SpannableString spannableString = new SpannableString(sb.toString());
            spannableString.setSpan(
                    new ForegroundColorSpan(context.getResources().getColor(R.color.chat_name_blue)),
                    0,
                    name.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            nameView.setText(spannableString);
            nameView.setVisibility(View.VISIBLE);
        }
    }
}
