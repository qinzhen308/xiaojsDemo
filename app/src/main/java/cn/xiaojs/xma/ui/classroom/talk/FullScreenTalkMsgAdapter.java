package cn.xiaojs.xma.ui.classroom.talk;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import java.util.Collections;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.XiaojsConfig;
import cn.xiaojs.xma.common.pulltorefresh.AbsChatAdapter;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;

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
 * Date:2017/5/8
 * Desc: 全屏模式下的消息adapter
 *
 * ======================================================================================== */

public class FullScreenTalkMsgAdapter extends AbsChatAdapter<TalkItem, FullScreenTalkMsgAdapter.Holder> implements View.OnClickListener{
    private static int MAX_SIZE = 130; // 260/2
    private String mTicket;
    private TalkComparator mTalkComparator;
    private LiveCriteria mLiveCriteria;
    private OnGetTalkListener mOnGetTalkListener;
    private OnImageClickListener mOnImageClickListener;

    private int mScreenW = 0;
    private int mTalkNameColor = 0;

    public FullScreenTalkMsgAdapter(Context context, String ticket, PullToRefreshListView listView, OnGetTalkListener listener) {
        super(context, listView);
        init(ticket, listener);
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        mOnImageClickListener = listener;
    }

    private void init(String ticket, OnGetTalkListener listener) {
        mTicket = ticket;
        mOnGetTalkListener = listener;
        mTalkComparator = new TalkComparator();
        mLiveCriteria = new LiveCriteria();
        mLiveCriteria.to = String.valueOf(Communications.TalkType.OPEN);

        mTalkNameColor = mContext.getResources().getColor(R.color.classroom_talk_name);
        mScreenW = mContext.getResources().getDisplayMetrics().widthPixels;
        MAX_SIZE = mContext.getResources().getDimensionPixelSize(R.dimen.px130);
    }

    @Override
    public void add(TalkItem talkItem) {
        if (!contains(talkItem)) {
            super.add(talkItem);
        }
    }

    @Override
    protected void setViewContent(final Holder holder, TalkItem bean, int position) {
        boolean isText = false;
        String imgKey = null;
        String txt = null;
        if (bean.body != null) {
            if (!TextUtils.isEmpty(bean.body.text)) {
                txt = bean.body.text;
                if (bean.body.contentType == Communications.ContentType.TEXT) {
                    isText = true;
                }
            } else {
                if (bean.body.drawing != null) {
                    imgKey = bean.body.drawing.name;
                }
            }
        }

        holder.msgImgLayout.setTag(position);
        if (isText) {
            holder.msgImgLayout.setVisibility(View.GONE);
            holder.msgTxt.setVisibility(View.VISIBLE);
            String name = bean.from != null ? bean.from.name : "";
            String msgTxt = name + ":" + (bean.body != null ? bean.body.text : "");
            SpannableString spanStr = new SpannableString(msgTxt);
            ForegroundColorSpan nameSpan = new ForegroundColorSpan(mTalkNameColor);
            ForegroundColorSpan contextSpan = new ForegroundColorSpan(Color.WHITE);
            spanStr.setSpan(nameSpan, 0, name.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            spanStr.setSpan(contextSpan, name.length() + 1, msgTxt.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            holder.msgTxt.setText(spanStr);
        } else if (!TextUtils.isEmpty(txt) || !TextUtils.isEmpty(imgKey)) {
            holder.msgTxt.setVisibility(View.GONE);
            holder.msgName.setText(bean.from.name + ":");
            holder.msgImgLayout.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(txt)) {
                //decode base64 to bitmap
                byte[] imgData = ClassroomBusiness.base64ToByteData(txt);
                Glide.with(mContext)
                        .load(imgData)
                        .into(getImgViewTarget(holder.msgImg));
            } else {
                //load img from qiniu url
                String imgUrl = ClassroomBusiness.getSnapshot(imgKey, MAX_SIZE);
                Glide.with(mContext)
                        .load(imgUrl)
                        .into(getImgViewTarget(holder.msgImg));
            }

        }
    }

    @Override
    protected View createContentView(int position) {
        return LayoutInflater.from(mContext).inflate(R.layout.layout_full_screen_msg_item, null);
    }

    @Override
    protected Holder initHolder(View view) {
        Holder holder = new Holder(view);
        holder.msgTxt = (TextView) view.findViewById(R.id.msg_txt);

        holder.msgImgLayout = view.findViewById(R.id.msg_img_layout);
        holder.msgName = (TextView) view.findViewById(R.id.msg_name);
        holder.msgImg = (ImageView) view.findViewById(R.id.msg_img);
        holder.msgImg.setOnClickListener(this);
        return holder;
    }


    @Override
    protected void doRequest() {
        LiveManager.getTalks(mContext, mTicket, mLiveCriteria, mPagination, new APIServiceCallback<CollectionPage<TalkItem>>() {
            @Override
            public void onSuccess(CollectionPage<TalkItem> object) {
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(mContext, "获取消息成功", Toast.LENGTH_SHORT).show();
                }
                if (object.objectsOfPage != null) {
                    Collections.sort(object.objectsOfPage, mTalkComparator);
                }
                FullScreenTalkMsgAdapter.this.onSuccess(object.objectsOfPage);
                if (mOnGetTalkListener != null) {
                    mOnGetTalkListener.onGetTalkFinished(true);
                }
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                if (XiaojsConfig.DEBUG) {
                    Toast.makeText(mContext, "获取消息失败", Toast.LENGTH_SHORT).show();
                }
                FullScreenTalkMsgAdapter.this.onFailure(errorCode, errorMessage);
                if (mOnGetTalkListener != null) {
                    mOnGetTalkListener.onGetTalkFinished(false);
                }
            }
        });
    }

    private GlideDrawableImageViewTarget getImgViewTarget(final ImageView imgView) {
        return new GlideDrawableImageViewTarget(imgView) {
            @Override
            public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                super.onResourceReady(resource, animation);
                if (resource instanceof GlideBitmapDrawable) {
                    Bitmap bmp = ((GlideBitmapDrawable)resource).getBitmap();
                    if (bmp != null) {
                        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) imgView.getLayoutParams();
                        int w = 0;
                        int h = 0;
                        if (bmp.getWidth() / (float)bmp.getHeight() > mScreenW / (float)MAX_SIZE) {
                            //depend on width
                            w = mScreenW;
                            h = (int) ((bmp.getHeight() / (float) bmp.getWidth()) * mScreenW);
                        } else {
                            h = MAX_SIZE;
                            w = (int) ((bmp.getWidth() / (float) bmp.getHeight()) * MAX_SIZE);
                        }
                        params.width = w;
                        params.height = h;
                    }
                    imgView.setImageBitmap(bmp);
                }
            }

            @Override
            public void onLoadFailed(Exception e, Drawable errorDrawable) {
                super.onLoadFailed(e, errorDrawable);

            }
        };
    }

    @Override
    public void onClick(View v) {
        View parent = (View) v.getParent();
        Object obj = parent.getTag();
        try {
            Integer position = (Integer) obj;
            TalkItem talkItem = getItem(position);

            boolean isText = false;
            String drawingKey = null;
            String base64Txt = null;

            if (talkItem.body != null) {
                if (!TextUtils.isEmpty(talkItem.body.text)) {
                    base64Txt = talkItem.body.text;
                    if (talkItem.body.contentType == Communications.ContentType.TEXT) {
                        isText = true;
                    }
                } else {
                    if (talkItem.body.drawing != null) {
                        drawingKey = talkItem.body.drawing.name;
                    }
                }
            }

            if (!isText && (!TextUtils.isEmpty(base64Txt) || !TextUtils.isEmpty(drawingKey))) {
                if (!TextUtils.isEmpty(base64Txt)) {
                    if (mOnImageClickListener != null) {
                        mOnImageClickListener.onImageClick(OnImageClickListener.IMG_FROM_BASE64, base64Txt);
                    }
                } else {
                    if (mOnImageClickListener != null) {
                        mOnImageClickListener.onImageClick(OnImageClickListener.IMG_FROM_QINIU, drawingKey);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    static class Holder extends BaseHolder {
        TextView msgTxt;

        View msgImgLayout;
        TextView msgName;
        ImageView msgImg;

        public Holder(View view) {
            super(view);
        }
    }
}
