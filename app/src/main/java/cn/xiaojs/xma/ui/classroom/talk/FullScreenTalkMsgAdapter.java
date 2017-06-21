package cn.xiaojs.xma.ui.classroom.talk;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;

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

public class FullScreenTalkMsgAdapter extends BaseTalkMsgAdapter<TalkItem, FullScreenTalkMsgAdapter.Holder>{
    private OnTalkItemClickListener mOnTalkItemClickListener; //talk item点击监听器

    private int mScreenW = 0;
    private int mTalkNameColor = 0;

    public FullScreenTalkMsgAdapter(Context context, String ticket, PullToRefreshListView listView) {
        super(context, listView);
        init(ticket);
    }

    public void setOnTalkItemClickListener(OnTalkItemClickListener listener) {
        mOnTalkItemClickListener = listener;
    }


    private void init(String ticket) {
        mTicket = ticket;
        mTalkComparator = new TalkComparator();
        mLiveCriteria = new LiveCriteria();
        mLiveCriteria.to = String.valueOf(Communications.TalkType.OPEN);

        mTalkNameColor = mContext.getResources().getColor(R.color.classroom_talk_name);
        mScreenW = mContext.getResources().getDisplayMetrics().widthPixels;
        MAX_SIZE = mContext.getResources().getDimensionPixelSize(R.dimen.px130);
    }

    @Override
    protected void doRequest() {
        //onSuccess(getTalkList());
        LiveManager.getTalks(mContext, mTicket, mLiveCriteria, mPagination, new APIServiceCallback<CollectionPage<TalkItem>>() {
            @Override
            public void onSuccess(CollectionPage<TalkItem> object) {
                if (object.objectsOfPage != null) {
                    Collections.sort(object.objectsOfPage, mTalkComparator);
                }
                FullScreenTalkMsgAdapter.this.onSuccess(object.objectsOfPage);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                FullScreenTalkMsgAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
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
            loadImg(txt, imgKey, holder.msgImg);
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
        view.setOnClickListener(this);
        return holder;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.talk_msg_info) {
            if (mOnTalkItemClickListener != null) {
                mOnTalkItemClickListener.onTalkItemClick();
            }
        } else {
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
