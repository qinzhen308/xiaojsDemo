package cn.xiaojs.xma.ui.classroom.talk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.widget.CircleTransform;
import cn.xiaojs.xma.ui.widget.MessageImageView;
import cn.xiaojs.xma.util.DeviceUtil;

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
 * Date:2016/11/30
 * Desc:
 *
 * ======================================================================================== */

public class TalkSimpleContactAdapter extends BaseAdapter implements View.OnClickListener {
    private Context mContext;
    private int mOffset;
    private LiveCollection<Attendee> mLiveCollection;
    private ArrayList<Attendee> mAttendeeList;
    private OnPortraitClickListener mListener;

    public TalkSimpleContactAdapter(Context context) {
        mContext = context;
        mOffset = context.getResources().getDimensionPixelOffset(R.dimen.px4);
    }

    public void setData(LiveCollection<Attendee> liveCollection) {
        mLiveCollection = liveCollection;
        mAttendeeList = liveCollection != null ? liveCollection.attendees : null;
        notifyDataSetChanged();
    }

    public void setOnPortraitClickListener(OnPortraitClickListener listener) {
        mListener = listener;
    }

    public LiveCollection<Attendee> getLiveCollection() {
        return mLiveCollection;
    }

    @Override
    public int getCount() {
        return mAttendeeList != null ? mAttendeeList.size() : 0;
    }

    @Override
    public Attendee getItem(int position) {
        return mAttendeeList != null ? mAttendeeList.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = createContentView();
        }
        holder = (Holder) convertView.getTag();

        bindData(holder, position);
        return convertView;
    }

    private View createContentView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_classroom_chat_contact_item, null);
        Holder holder = new Holder();
        holder.portrait = (MessageImageView) v.findViewById(R.id.portrait);
        //set type
        holder.portrait.setType(MessageImageView.TYPE_NUM);
        holder.portrait.setExtraOffsetX(mOffset);
        holder.portrait.setExtraOffsetY(mOffset);

        v.setTag(holder);
        v.setOnClickListener(this);
        return v;
    }

    private void bindData(Holder holder, int position) {
        holder.position = position;
        Attendee attendee = mAttendeeList.get(position);
        int size = mContext.getResources().getDimensionPixelSize(R.dimen.px90);
        String portraitUrl = cn.xiaojs.xma.common.xf_foundation.schemas.Account.getAvatar(attendee.accountId, size);
        Glide.with(mContext)
                .load(portraitUrl)
                .transform(new CircleTransform(mContext))
                .signature(new StringSignature(DeviceUtil.getSignature()))
                .placeholder(R.drawable.default_avatar)
                .error(R.drawable.default_avatar)
                .into(holder.portrait);
        //holder.portrait.setCount(5);
    }

    @Override
    public void onClick(View v) {
        Object obj = v.getTag();
        try {
            if (obj instanceof Holder) {
                Holder holder = (Holder) obj;
                if (mListener != null) {
                    mListener.onPortraitClick(mAttendeeList.get(holder.position));
                }
            }
        } catch (Exception e) {

        }
    }


    private class Holder {
        MessageImageView portrait;
        int position = -1;
    }
}
