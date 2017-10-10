package cn.xiaojs.xma.ui.classroom2.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import butterknife.ButterKnife;
import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;

/**
 * Created by maxiaobao on 2017/9/28.
 */

public abstract class ChatViewHolder extends RecyclerView.ViewHolder{


    protected int MAX_SIZE;

    protected Context context;

    public ChatViewHolder(Context context,View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        this.context = context;
        MAX_SIZE = context.getResources().getDimensionPixelSize(R.dimen.px350);
    }


    protected abstract void bindData(TalkItem item);

//    protected void loadImg(String txt, String imgKey, ImageView imageView) {
//        if (!TextUtils.isEmpty(txt)) {
//            //decode base64 to bitmap
//            byte[] imgData = ClassroomBusiness.base64ToByteData(txt);
//            int code = imgData !=null ? imgData.hashCode() : 0;
//            Glide.with(context)
//                    .load(imgData)
//                    .into(getImgViewTarget(code, imageView));
//        } else {
//            //load img from qiniu url
//            String imgUrl = ClassroomBusiness.getSnapshot(imgKey, MAX_SIZE);
//            int code = imgKey !=null ? imgKey.hashCode() : 0;
//            Glide.with(context)
//                    .load(imgUrl)
//                    .into(getImgViewTarget(code, imageView));
//        }
//    }
//
    protected GlideDrawableImageViewTarget getImgViewTarget(final int code, final ImageView imgView) {
        return new GlideDrawableImageViewTarget(imgView) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                if (resource instanceof GlideBitmapDrawable) {
                    Bitmap bmp = ((GlideBitmapDrawable) resource).getBitmap();
                    if (bmp != null) {
                        ViewGroup.LayoutParams params = imgView.getLayoutParams();
                        int w = MAX_SIZE;
                        int h = MAX_SIZE;
                        if (bmp.getWidth() > bmp.getHeight()) {
                            w = MAX_SIZE;
                            h = (int) ((bmp.getHeight() / (float) bmp.getWidth()) * MAX_SIZE);
                        } else {
                            h = MAX_SIZE;
                            w = (int) ((bmp.getWidth() / (float) bmp.getHeight()) * MAX_SIZE);
                        }
                        params.width = w;
                        params.height = h;
                    }
                    imgView.setImageBitmap(bmp);

//                    if (mOnTalkImgLoadListener != null) {
//                        mOnTalkImgLoadListener.onTalkImgLoadFinish(String.valueOf(code), true);
//                    }
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);

            }
        };
    }

}
