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
 * Date:2016/12/20
 * Desc:
 *
 * ======================================================================================== */

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.orhanobut.logger.Logger;

import java.util.Collections;
import java.util.List;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.common.pulltorefresh.BaseHolder;
import cn.xiaojs.xma.common.pulltorefresh.core.PullToRefreshListView;
import cn.xiaojs.xma.common.xf_foundation.schemas.Account;
import cn.xiaojs.xma.common.xf_foundation.schemas.Communications;
import cn.xiaojs.xma.data.LiveManager;
import cn.xiaojs.xma.data.api.service.APIServiceCallback;
import cn.xiaojs.xma.model.CollectionPage;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCriteria;
import cn.xiaojs.xma.model.live.TalkItem;
import cn.xiaojs.xma.ui.classroom.main.ClassroomBusiness;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.RoundedImageView;
import cn.xiaojs.xma.util.TimeUtil;

public class TalkMsgAdapter extends BaseTalkMsgAdapter<TalkItem, TalkMsgAdapter.Holder> {
    public final static int TYPE_MY_SPEAKER = 0;
    public final static int TYPE_OTHER_SPEAKER = 1;
    private OnPortraitClickListener mPortraitClickListener; //头像点击监听器

    public TalkMsgAdapter(Context context, String ticket, LiveCriteria liveCriteria, PullToRefreshListView listView) {
        super(context, listView);
        mContext = context;
        mTicket = ticket;
        mLiveCriteria = liveCriteria;
        MAX_SIZE = context.getResources().getDimensionPixelSize(R.dimen.px280);
        mTalkComparator = new TalkComparator();
    }

    public void setOnPortraitClickListener(OnPortraitClickListener listener) {
        mPortraitClickListener = listener;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        if (mBeanList.size() > 0) {
            Holder holder = null;
            if (view == null) {
                view = createItem(position);
                holder = initHolder(view);
                view.setTag(holder);
            } else {
                if ((getItemViewType(position) == TYPE_MY_SPEAKER && view.getId() != R.id.my_speaker)
                        || (getItemViewType(position) == TYPE_OTHER_SPEAKER && view.getId() != R.id.other_speaker)) {
                    view = createItem(position);
                    holder = initHolder(view);
                    view.setTag(holder);
                } else {
                    holder = (Holder) view.getTag();
                }
            }
            if (holder == null) {//view可能会传成下方的占位view
                view = createItem(position);
                holder = initHolder(view);
                view.setTag(holder);
            }
            setViewContent(holder, getItem(position), position);
            return view;
        } else {//解决加了header后，header高度超过1屏无法下拉
            View v = new View(mContext);
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
            v.setBackgroundResource(android.R.color.transparent);
            v.setLayoutParams(lp);
            return v;
        }
    }


    @Override
    protected void setViewContent(final Holder holder, TalkItem bean, int position) {
        int size = mContext.getResources().getDimensionPixelSize(R.dimen.px90);
        String portraitUrl = Account.getAvatar(bean.from != null ? bean.from.accountId : null, size);
        Glide.with(mContext)
                .load(portraitUrl)
                .transform(new CircleTransform(mContext))
                .placeholder(R.drawable.default_avatar_grey)
                .error(R.drawable.default_avatar_grey)
                .into(holder.portrait);
        holder.name.setText(bean.from.name);
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

        holder.position = position;
        holder.msgContent.setTag(position);
        if (isText) {
            holder.msgImg.setVisibility(View.GONE);
            holder.msgTxt.setVisibility(View.VISIBLE);
            holder.msgTxt.setText(bean.body != null ? bean.body.text : null);
        } else if (!TextUtils.isEmpty(txt) || !TextUtils.isEmpty(imgKey)) {
            holder.msgTxt.setVisibility(View.GONE);
            holder.msgImg.setVisibility(View.VISIBLE);
            loadImg(txt, imgKey, holder.msgImg);
        }

        holder.time.setText(TimeUtil.format(bean.time, TimeUtil.TIME_HH_MM_SS));
    }

    @Override
    public int getItemViewType(int position) {
        List<TalkItem> talkItems = getList();
        TalkItem item = null;
        if (talkItems == null || (item = talkItems.get(position)) == null) {
            Logger.i("task items is empty");
            return TYPE_MY_SPEAKER;
        }

        if (item == null || item.from == null) {
            Logger.i("item is empty");
            return TYPE_MY_SPEAKER;
        }

        boolean isMyself = ClassroomBusiness.isMyself(mContext, item.from.accountId);
        return isMyself ? TYPE_MY_SPEAKER : TYPE_OTHER_SPEAKER;
    }

    @Override
    protected View createContentView(int position) {
        int type = getItemViewType(position);
        View v = null;
        switch (type) {
            case TYPE_MY_SPEAKER:
                v = LayoutInflater.from(mContext).inflate(R.layout.layout_talk_my_speaker_item, null);
                break;
            case TYPE_OTHER_SPEAKER:
                v = LayoutInflater.from(mContext).inflate(R.layout.layout_talk_other_speaker_item, null);
                break;
        }
        return v;
    }

    @Override
    protected Holder initHolder(View v) {
        Holder holder = new Holder(v);
        holder.portrait = (RoundedImageView) v.findViewById(R.id.portrait);
        holder.name = (TextView) v.findViewById(R.id.name);
        holder.time = (TextView) v.findViewById(R.id.time);
        holder.msgTxt = (TextView) v.findViewById(R.id.msg_txt);
        holder.msgImg = (ImageView) v.findViewById(R.id.msg_img);
        holder.msgContent = v.findViewById(R.id.msg_content);
        holder.msgImg.setOnClickListener(this);
        holder.portrait.setOnClickListener(this);
        return holder;
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
                TalkMsgAdapter.this.onSuccess(object.objectsOfPage);
            }

            @Override
            public void onFailure(String errorCode, String errorMessage) {
                TalkMsgAdapter.this.onFailure(errorCode, errorMessage);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void onClick(View v) {
        View parent = (View) v.getParent();
        Object obj = parent.getTag();
        try {
            TalkItem talkItem = null;
            switch (v.getId()) {
                case R.id.msg_img:
                    boolean isText = false;
                    String drawingKey = null;
                    String base64Txt = null;
                    Integer position = (Integer) obj;
                    talkItem = getItem(position);

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
                    break;
                case R.id.portrait:
                    if (mPortraitClickListener != null && obj instanceof Holder) {
                        talkItem = getItem(((Holder)obj).position);
                        if (!ClassroomBusiness.isMyself(mContext, talkItem.from.accountId)) {
                            Attendee attendee = new Attendee();
                            attendee.accountId = talkItem.from.accountId;
                            attendee.name = ClassroomBusiness.getNameByAccountId(attendee.accountId);
                            mPortraitClickListener.onPortraitClick(attendee);
                        }
                    }
                    break;
            }


        } catch (Exception e) {

        }
    }

    static class Holder extends BaseHolder {
        RoundedImageView portrait;
        TextView name;
        TextView time;
        TextView msgTxt;
        ImageView msgImg;
        View msgContent;
        int position;

        public Holder(View view) {
            super(view);
        }
    }
}
