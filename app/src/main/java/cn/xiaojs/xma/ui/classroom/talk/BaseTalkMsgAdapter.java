package cn.xiaojs.xma.ui.classroom.talk;
/*  =======================================================================================
 *  Copyright (C) 2016 Xiaojs.cn. All rights reserved.
 *
 *  This computer program source code file is protected by copyright law and international
 *  treaties. Unauthorized distribution of source code files, programs, or portion of the
 *  package, may result in severe civil and criminal penalties, and will be prosecuted to
 *  the maximum extent under the law.
 *
 *  ---------------------------------------------------------------------------------------
 * Author:huangyong
 * Date:2017/6/21
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import cn.xiaojs.xma.common.pulltorefresh.AbsChatAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;

public abstract class BaseTalkMsgAdapter<B, H extends BaseHolder> extends AbsChatAdapter<B, H> implements
        View.OnClickListener, AbsListView.OnScrollListener{
    protected int MAX_SIZE = 280;

    protected Context mContext;
    protected String mTicket;
    protected LiveCriteria mLiveCriteria;
    protected TalkComparator mTalkComparator;

    protected OnImageClickListener mOnImageClickListener; //图片点击监听器
    protected OnTalkImgLoadListener mOnTalkImgLoadListener; //图片加载监听器

    public BaseTalkMsgAdapter (Context context, PullToRefreshListView listView) {
        super(context, listView);
        mContext = context;
        listView.getRefreshableView().setOnScrollListener(this);
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        mOnImageClickListener = listener;
    }

    public void setOnTalkImgLoadListener (OnTalkImgLoadListener listener) {
        mOnTalkImgLoadListener = listener;
    }

    @Override
    protected boolean filterDuplication() {
        return true;
    }

    @Override
    public void add(B talkItem) {
        if (!contains(talkItem)) {
            super.add(talkItem);
        }
    }

    protected void loadImg(String txt, String imgKey, ImageView imageView) {
        if (!TextUtils.isEmpty(txt)) {
            //decode base64 to bitmap
            byte[] imgData = ClassroomBusiness.base64ToByteData(txt);
            int code = imgData !=null ? imgData.hashCode() : 0;
            Glide.with(mContext)
                    .load(imgData)
                    .into(getImgViewTarget(code, imageView));
        } else {
            //load img from qiniu url
            String imgUrl = ClassroomBusiness.getSnapshot(imgKey, MAX_SIZE);
            int code = imgKey !=null ? imgKey.hashCode() : 0;
            Glide.with(mContext)
                    .load(imgUrl)
                    .into(getImgViewTarget(code, imageView));
        }
    }

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

                    if (mOnTalkImgLoadListener != null) {
                        mOnTalkImgLoadListener.onTalkImgLoadFinish(String.valueOf(code), true);
                    }
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);

            }
        };
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState != SCROLL_STATE_IDLE) {
            mOnTalkImgLoadListener = null;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }
}
