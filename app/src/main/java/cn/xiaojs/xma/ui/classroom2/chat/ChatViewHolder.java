package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import butterknife.ButterKnife;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;

/**
 * Created by maxiaobao on 2017/9/28.
 */

public abstract class ChatViewHolder extends RecyclerView.ViewHolder{

    protected Context context;

    public ChatViewHolder(Context context,View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.context = context;
    }


    protected abstract void bindData(TalkItem item);

    protected void loadImg(String txt, String imgKey, ImageView imageView) {
        if (!TextUtils.isEmpty(txt)) {
            //decode base64 to bitmap
            byte[] imgData = ClassroomBusiness.base64ToByteData(txt);
            int code = imgData !=null ? imgData.hashCode() : 0;
            Glide.with(context)
                    .load(imgData)
                    .into(imageView);
        } else {
            //load img from qiniu url
            String imgUrl = ClassroomBusiness.getSnapshot(imgKey, 280);
            int code = imgKey !=null ? imgKey.hashCode() : 0;
            Glide.with(context)
                    .load(imgUrl)
                    .into(imageView);
        }
    }

}
