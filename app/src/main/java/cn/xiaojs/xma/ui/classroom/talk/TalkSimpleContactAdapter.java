package cn.xiaojs.xma.ui.classroom.talk;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import cn.xiaojs.xma.R;
import cn.xiaojs.xma.model.live.Attendee;
import cn.xiaojs.xma.model.live.LiveCollection;
import cn.xiaojs.xma.ui.widget.MessageImageView;

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

public class TalkSimpleContactAdapter extends BaseAdapter {
    private Context mContext;
    private int mOffset;
    private LiveCollection<Attendee> mLiveCollection;
    private ArrayList<Attendee> mAttendeeList;

    public TalkSimpleContactAdapter(Context context) {
        mContext = context;
        mOffset = context.getResources().getDimensionPixelOffset(R.dimen.px4);
    }

    public void setData(LiveCollection<Attendee> liveCollection) {
        mLiveCollection = liveCollection;
        mAttendeeList = liveCollection != null ? liveCollection.attendees : null;
        notifyDataSetChanged();
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
        holder = (Holder)convertView.getTag();

        bindData(holder);
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
        return v;
    }

    private void bindData(Holder holder) {
        holder.portrait.setImageResource(R.drawable.default_portrait);
        //holder.portrait.setCount(5);
    }


    private class Holder {
        MessageImageView portrait;
    }
}